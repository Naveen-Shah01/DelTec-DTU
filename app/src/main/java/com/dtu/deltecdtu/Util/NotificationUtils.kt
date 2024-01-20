package com.dtu.deltecdtu.Util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.dtu.deltecdtu.R


private val NOTIFICATION_ID = 10
private val channelId = "dtu_notice_channel"
private val channelName ="DTU Notices"


fun NotificationManager.sendNotification(title:String,messageBody: String, applicationContext: Context) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channelId, channelName, NotificationManager.IMPORTANCE_HIGH
        )
            .apply {
                setShowBadge(true)
            }
        notificationChannel.enableVibration(true)
        notificationChannel.description = "DTU Notice Notifications"
        createNotificationChannel(notificationChannel)
    }


    val builder = NotificationCompat.Builder(
        applicationContext,
        channelId
    )
        .setSmallIcon(R.drawable.notification_icon)
        .setContentTitle(title)
        .setContentText(messageBody)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}

