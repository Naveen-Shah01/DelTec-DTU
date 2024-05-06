package com.dtu.deltecdtu.service

import android.app.NotificationManager
import androidx.core.content.ContextCompat
import com.dtu.deltecdtu.util.sendNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let {
            val title = remoteMessage.notification?.title
            val body = it.body
            sendNotification(title!!, body!!)
        }
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager = ContextCompat.getSystemService(
            applicationContext, NotificationManager::class.java
        ) as NotificationManager
        notificationManager.sendNotification(title, message, applicationContext)
    }
}