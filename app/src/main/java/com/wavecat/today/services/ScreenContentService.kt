package com.wavecat.today.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.pm.PackageManager
import android.view.accessibility.AccessibilityEvent


class ScreenContentService : AccessibilityService() {
    @Override
    override fun onServiceConnected() {
        instance = this

        super.onServiceConnected()

        val info = AccessibilityServiceInfo()

        info.flags = AccessibilityServiceInfo.DEFAULT

        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK

        serviceInfo = info
    }

    override fun onInterrupt() {
        instance = null
    }

    val screenContentText: String
        get() = buildString {
            if (rootInActiveWindow == null) return@buildString

            runCatching {
                val info =
                    packageManager.getApplicationInfo(
                        rootInActiveWindow.packageName.toString(),
                        PackageManager.GET_META_DATA
                    )


                packageManager.getApplicationLabel(info) as String
            }.onSuccess { appName ->
                append("$appName:\n")
            }

            rootInActiveWindow.toText(this)
        }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    companion object {
        @Suppress("StaticFieldLeak")
        var instance: ScreenContentService? = null
    }
}