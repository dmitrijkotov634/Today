package com.wavecat.today

import android.content.SharedPreferences
import androidx.core.content.edit
import com.wavecat.today.preferences.BooleanPreference
import com.wavecat.today.preferences.IntPreference
import com.wavecat.today.preferences.LongPreference
import com.wavecat.today.preferences.StringPreference
import com.wavecat.today.worker.models.Message
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class DataRepository(private val preferences: SharedPreferences, prefix: String) {
    var apiUrl by StringPreference(preferences, "$prefix$API_URL", Constant.TEST_ENDPOINT)
    var apiKey by StringPreference(preferences, "$prefix$API_KEY", "")
    var model by StringPreference(preferences, "$prefix$MODEL", Constant.DEFAULT_MODEL)
    var contextSize by IntPreference(preferences, "$prefix$CONTEXT_SIZE", 500)
    var prompt by StringPreference(preferences, "$prefix$PROMPT", Constant.BASE_PROMPT)
    var interval by LongPreference(preferences, INTERVAL, 3600000)
    var showErrors by BooleanPreference(preferences, SHOW_ERRORS, false)

    var messageContext: List<Message>
        get() = Json.decodeFromString(preferences.getString(MESSAGE_CONTEXT, "[]")!!)
        set(value) {
            preferences.edit {
                putString(MESSAGE_CONTEXT, Json.encodeToString(value))
                apply()
            }
        }

    companion object {
        const val API_URL = "api_url"
        const val API_KEY = "api_key"
        const val MODEL = "model"
        const val CONTEXT_SIZE = "context_size"
        const val PROMPT = "prompt"
        const val INTERVAL = "interval"
        const val SHOW_ERRORS = "show_errors"
        const val MESSAGE_CONTEXT = "message_context"
    }
}