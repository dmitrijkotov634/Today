package com.wavecat.today

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.wavecat.today.preferences.BooleanPreference
import com.wavecat.today.preferences.IntPreference
import com.wavecat.today.preferences.StringPreference
import com.wavecat.today.worker.models.Message
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class DataRepository(context: Context) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    var apiUrl by StringPreference(preferences, API_URL, Constant.TEST_ENDPOINT)
    var apiKey by StringPreference(preferences, API_KEY, "")
    var model by StringPreference(preferences, MODEL, Constant.GPT_3_5_TURBO)
    var contextSize by IntPreference(preferences, CONTEXT_SIZE, 1000)
    var prompt by StringPreference(preferences, PROMPT, Constant.BASE_PROMPT)
    var notificationHours by IntPreference(preferences, NOTIFICATION_HOURS, 1)
    var suggestion by StringPreference(preferences, SUGGESTION, "")
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
        const val NOTIFICATION_HOURS = "notification_hours"
        const val SUGGESTION = "suggestion"
        const val SHOW_ERRORS = "show_errors"
        const val MESSAGE_CONTEXT = "message_context"
    }
}