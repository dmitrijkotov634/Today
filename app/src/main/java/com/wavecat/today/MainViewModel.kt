package com.wavecat.today

import androidx.lifecycle.ViewModel
import androidx.work.*
import com.wavecat.today.worker.SuggestWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class MainViewModel(
    private val repository: DataRepository,
    private val workManager: WorkManager
) : ViewModel() {
    private val _apiKey = MutableStateFlow(repository.apiKey)
    val apiKey = _apiKey.asStateFlow()

    private val _contextSize = MutableStateFlow(repository.contextSize)
    val contextSize = _contextSize.asStateFlow()

    private val _model = MutableStateFlow(repository.model)
    val model = _model.asStateFlow()

    private val _prompt = MutableStateFlow(repository.prompt)
    val prompt = _prompt.asStateFlow()

    private val _interval = MutableStateFlow(repository.interval.toDuration(DurationUnit.MILLISECONDS))
    val interval = _interval.asStateFlow()

    private val _apiUrl = MutableStateFlow(repository.apiUrl).also {
        if (it.value == Constant.TEST_ENDPOINT)
            setTestRequired()
    }

    val apiUrl = _apiUrl.asStateFlow()

    fun setApiUrl(text: String) {
        if (text == Constant.TEST_ENDPOINT)
            setTestRequired()

        _apiUrl.value = text
        repository.apiUrl = text
    }

    private fun setTestRequired() {
        setContextSize(1000)
        setModel(Constant.GPT_3_5_TURBO)

        if (interval.value < 1.hours)
            setInterval(1.hours)
    }

    fun setApiKey(text: String) {
        _apiKey.value = text
        repository.apiKey = text
    }

    fun setContextSize(value: Int) {
        _contextSize.value = value
        repository.contextSize = value
    }

    fun setModel(text: String) {
        _model.value = text
        repository.model = text
    }

    fun setPrompt(text: String) {
        _prompt.value = text
        repository.prompt = text
    }

    fun setInterval(value: Duration) {
        _interval.value = value
        repository.interval = value.inWholeMilliseconds
    }

    fun onPause() {
        repository.showErrors = false
    }

    fun onResume() {
        repository.showErrors = true
    }

    fun apply() {
        repository.messageContext = emptyList()

        workManager.enqueueUniquePeriodicWork(
            SUGGEST,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            PeriodicWorkRequestBuilder<SuggestWorker>(
                repository.interval, TimeUnit.MILLISECONDS
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                ).build()
        )
    }

    companion object {
        const val SUGGEST = "suggest"
    }
}