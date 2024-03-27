package com.wavecat.today.worker

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Build
import android.service.notification.StatusBarNotification
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.ModelType
import com.wavecat.today.DataRepository
import com.wavecat.today.R
import com.wavecat.today.services.NotificationService
import com.wavecat.today.services.ScreenContentService
import com.wavecat.today.ui.widget.TodayWidget
import com.wavecat.today.worker.models.CompletionsInput
import com.wavecat.today.worker.models.CompletionsResult
import com.wavecat.today.worker.models.Message
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

    private val replaceable = mapOf(
        NOTIFICATIONS_VAR to ::getNotifications,
        BATTERY_LEVEL_VAR to ::getBatteryLevel,
        SCREEN_CONTENT_VAR to ::getScreenContent,
        DATE_AND_TIME_VAR to {
            val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm", Locale.ENGLISH)
            dateFormat.format(Date())
        }
    )

    private val repository = DataRepository(applicationContext)

    override suspend fun doWork(): Result {
        var prompt = repository.prompt

        for ((k, v) in replaceable) {
            if (prompt.contains(k))
                prompt = prompt.replace(k, v())
        }

        val inputMessages = trimMessages(
            encoding,
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
            notifyError(it.message.toString())
            it.printStackTrace()
        }.onSuccess { response ->
            val message = response.choices?.get(0)?.message

            if (message == null) {
                notifyError(response.error.toString())
                return@onSuccess
            }

            repository.messageContext = buildList {
                addAll(inputMessages)
                add(message)
            }

            message.content?.let {
                GlanceAppWidgetManager(applicationContext).getGlanceIds(TodayWidget::class.java)
                    .forEach { glanceId ->
                        updateAppWidgetState(applicationContext, glanceId) { mutablePreferences ->
                            mutablePreferences[TodayWidget.suggestion] = it
                        }

                        TodayWidget.update(applicationContext, glanceId)
                    }

                notify(it)
            }
        }

        return Result.success()
    }

    private fun getBatteryLevel(): String {
        val bm: BatteryManager = applicationContext.getSystemService(BATTERY_SERVICE) as BatteryManager
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).toString()
    }

    private fun getScreenContent(): String =
        ScreenContentService.instance?.screenContentText ?: ""

    private fun getNotifications(): String = buildString {
        val notificationService: NotificationService = NotificationService.instance ?: return@buildString
        val notifications: Array<StatusBarNotification> = notificationService.getActiveNotifications()

        notifications.forEachIndexed { index, notification ->
            if (notification.packageName == applicationContext.packageName) return@forEachIndexed

            append("${index + 1}. ")

            runCatching {
                val packageManager: PackageManager = applicationContext.packageManager
                val info = packageManager.getApplicationInfo(notification.packageName, PackageManager.GET_META_DATA)
                packageManager.getApplicationLabel(info) as String
            }.onSuccess { appName ->
                append("$appName: ")
            }

            notification.notification.extras.getCharSequence(Notification.EXTRA_TITLE)?.run {
                append(this)
                append(": ")
            }

            if (notification.notification.extras.getCharSequence(Notification.EXTRA_BIG_TEXT) != null) {
                append(notification.notification.extras.getCharSequence(Notification.EXTRA_BIG_TEXT))
            } else if (notification.notification.extras.getCharSequence(Notification.EXTRA_TEXT) != null) {
                append(notification.notification.extras.getCharSequence(Notification.EXTRA_TEXT))
            }

            append("\n")
        }
    }

    private fun notifyError(content: String) {
        if (repository.showErrors)
            notify(content)
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

    companion object {
        private const val CHANNEL_ID = "suggest"

        const val NOTIFICATIONS_VAR = "%NOTIFICATIONS%"
        const val SCREEN_CONTENT_VAR = "%SCREENCONTENT%"
        const val DATE_AND_TIME_VAR = "%DATETIME%"
        const val BATTERY_LEVEL_VAR = "%BATTERYLEVEL%"
    }
}