package com.wavecat.today.workers

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.service.notification.StatusBarNotification
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.ModelType
import com.wavecat.today.DataRepository
import com.wavecat.today.R
import com.wavecat.today.workers.models.CompletionsInput
import com.wavecat.today.workers.models.CompletionsResult
import com.wavecat.today.workers.models.Message
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*


class SuggestWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    private val registry = Encodings.newLazyEncodingRegistry()
    val encoding by lazy { registry.getEncodingForModel(ModelType.GPT_3_5_TURBO)!! }

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                coerceInputValues = true
                ignoreUnknownKeys = true
            })
        }

        defaultRequest {
            contentType(ContentType.Application.Json)
        }
    }

    override suspend fun doWork(): Result {
        val repository = DataRepository(applicationContext)

        var prompt = repository.prompt

        if (prompt.contains(NOTIFICATIONS_VAR))
            prompt = prompt.replace(NOTIFICATIONS_VAR, getNotifications())

        if (prompt.contains(DATE_AND_TIME_VAR)) {
            val dateFormat = SimpleDateFormat(
                "yyyy-MM-dd HH:mm", Locale.ENGLISH
            )

            prompt = prompt.replace(DATE_AND_TIME_VAR, dateFormat.format(Date()))
        }

        val inputMessages = trimMessages(
            buildList {
                addAll(repository.messageContext)
                add(Message(Message.USER, prompt))
            },
            repository.contextSize
        )

        runCatching {
            client.post {
                url(repository.apiUrl)
                bearerAuth(repository.apiKey)
                setBody(
                    CompletionsInput(
                        repository.model,
                        inputMessages
                    )
                )
            }.body<CompletionsResult>()
        }.onFailure {
            it.printStackTrace()
            notify(it.message.toString())
        }.onSuccess { response ->
            val message = response.choices?.get(0)?.message

            if (message == null) {
                notify(response.error.toString())
                return@onSuccess
            }

            repository.messageContext = buildList {
                addAll(inputMessages)
                add(message)
            }

            notify(message.content.toString())
        }

        return Result.success()
    }

    private fun getNotifications(): String = buildString {
        val notificationService: NotificationService = NotificationService.get() ?: return@buildString
        val notifications: Array<StatusBarNotification> = notificationService.getActiveNotifications()

        notifications.forEach {
            if (it.packageName == applicationContext.packageName) return@forEach

            runCatching {
                val packageManager: PackageManager = applicationContext.packageManager
                val info = packageManager.getApplicationInfo(it.packageName, PackageManager.GET_META_DATA)
                packageManager.getApplicationLabel(info) as String
            }.onSuccess { appName ->
                append("$appName: ")
            }

            it.notification.extras.getCharSequence(Notification.EXTRA_TITLE)?.run {
                append(this)
                append(": ")
            }

            if (it.notification.extras.getCharSequence(Notification.EXTRA_BIG_TEXT) != null) {
                append(it.notification.extras.getCharSequence(Notification.EXTRA_BIG_TEXT))
            } else if (it.notification.extras.getCharSequence(Notification.EXTRA_TEXT) != null) {
                append(it.notification.extras.getCharSequence(Notification.EXTRA_TEXT))
            }

            append("\n")
        }
    }

    private fun notify(content: String) {
        val notificationManager = NotificationManagerCompat.from(applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = applicationContext.getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle())
            .setOngoing(true)
            .setSmallIcon(R.drawable.baseline_auto_awesome_24)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            )
                return
        }

        notificationManager.notify(id.hashCode(), notification)
    }

    private fun trimMessages(
        inputMessages: List<Message>,
        tokenLimit: Int
    ): List<Message> = buildList {
        var numTokens = 0

        for (message in inputMessages.reversed()) {
            numTokens += 6
            numTokens += encoding.encode(message.role).size
            numTokens += encoding.encode(message.content).size

            if (numTokens > tokenLimit) {
                if (isEmpty())
                    add(
                        message.copy(
                            content = encoding.decode(
                                encoding.encode(message.content)
                                    .take(tokenLimit)
                            )
                        )
                    )

                break
            }

            add(message)
        }
    }.reversed()

    companion object {
        private const val CHANNEL_ID = "analysis"

        private const val NOTIFICATIONS_VAR = "%NOTIFICATIONS%"
        private const val DATE_AND_TIME_VAR = "%DATETIME%"
    }
}