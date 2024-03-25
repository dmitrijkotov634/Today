package com.wavecat.today.preferences

import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class StringPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: String = String()
) : ReadWriteProperty<Any, String?> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>) =
        preferences.getString(name, defaultValue) ?: defaultValue

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        preferences.edit {
            putString(name, value)
            apply()
        }
    }
}