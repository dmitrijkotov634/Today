package com.wavecat.today.preferences

import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class LongPreference(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: Long
) : ReadWriteProperty<Any, Long> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>) =
        preferences.getLong(name, defaultValue)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) {
        preferences.edit {
            putLong(name, value)
            apply()
        }
    }
}