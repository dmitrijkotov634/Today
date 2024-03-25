package com.wavecat.today.services

import android.service.notification.NotificationListenerService

class NotificationService : NotificationListenerService() {
    override fun onListenerConnected() {
        instance = this
    }

    override fun onListenerDisconnected() {
        instance = null
    }

    companion object {
        var instance: NotificationService? = null
    }
}