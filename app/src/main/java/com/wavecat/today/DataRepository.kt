package com.wavecat.today

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.wavecat.today.workers.models.Message
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DataRepository(context: Context) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    var apiUrl: String
        get() = preferences.getString(API_URL, Constant.TEST_ENDPOINT)!!
        set(value) {
            preferences.edit {
                putString(API_URL, value)
                apply()
            }
        }

    var apiKey: String
        get() = preferences.getString(API_KEY, "")!!
        set(value) {
            preferences.edit {
                putString(API_KEY, value)
                apply()
            }
        }

    var prompt: String
        get() = preferences.getString(PROMPT, Constant.BASE_PROMPT)!!
        set(value) {
            preferences.edit {
                putString(PROMPT, value)
                apply()
            }
        }

    var model: String
        get() = preferences.getString(MODEL, Constant.GPT_3_5_TURBO)!!
        set(value) {
            preferences.edit {
                putString(MODEL, value)
                apply()
            }
        }

    var contextSize: Int
        get() = preferences.getInt(CONTEXT_SIZE, 1000)
        set(value) {
            preferences.edit {
                putInt(CONTEXT_SIZE, value)
                apply()
            }
        }

    var notificationHours: Int
        get() = preferences.getInt(NOTIFICATION_HOURS, 1)
        set(value) {
            preferences.edit {
                putInt(NOTIFICATION_HOURS, value)
                apply()
            }
        }

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
        const val PROMPT = "prompt"
        const val CONTEXT_SIZE = "context_size"
        const val NOTIFICATION_HOURS = "notification_hours"
        const val MESSAGE_CONTEXT = "message_context"
    }
}