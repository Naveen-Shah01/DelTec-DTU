package com.dtu.deltecdtu.service

import android.app.NotificationManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.dtu.deltecdtu.Util.sendNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseService : FirebaseMessagingService() {
    val TAG="Firebase Notification"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        Log.d(TAG, "From: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
//            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

        }
        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            val title = remoteMessage.notification?.title
            val body = it.body
//            Log.d(TAG, "Message Title: $title")
//            Log.d(TAG, "Message Notification Body: $body")
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