package com.dtu.deltecdtu.service

import android.app.NotificationManager
import androidx.core.content.ContextCompat
import com.dtu.deltecdtu.util.sendNotification
import com.example.deltecdtu.Util.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber


class MyFirebaseService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.tag(Constants.FIREBASE_NOTIFICATION_TAG).e("From: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            Timber.tag(Constants.FIREBASE_NOTIFICATION_TAG).e("Message data payload: ${remoteMessage.data}")
        }

        remoteMessage.notification?.let {
            val title = remoteMessage.notification?.title
            val body = it.body
            Timber.tag(Constants.FIREBASE_NOTIFICATION_TAG).e("Message Title: $title")
            Timber.tag(Constants.FIREBASE_NOTIFICATION_TAG).e("Message Notification Body: $body")
            sendNotification(title!!, body!!)
        }
    }

    private fun sendNotification(title:String,message: String) {
        val notificationManager = ContextCompat.getSystemService(
            applicationContext, NotificationManager::class.java
        ) as NotificationManager
        notificationManager.sendNotification(title,message, applicationContext)
    }
}