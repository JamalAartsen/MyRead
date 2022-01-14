package com.jamal.myread.utils

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.util.Pair
import com.jamal.myread.R

object NotificationUtils {
    private const val NOTIFICATION_ID = 1337
    private const val NOTIFICATION_CHANNEL_ID = "com.jamal.myread.app"
    private const val NOTIFICATION_CHANNEL_NAME = "com.jamal.myread.app"

    fun getNotification(context: Context): Pair<Int, Notification>? {
        createNotificationChannel(context)
        val notification = createNotification(context)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
        return Pair(NOTIFICATION_ID, notification)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(context: Context): Notification {
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        builder.apply {
            setSmallIcon(R.drawable.ic_read_person_white)
            setContentTitle(context.getString(R.string.app_name))
            setOngoing(true)
            setCategory(Notification.CATEGORY_SERVICE)
            priority = setPriority()
            setShowWhen(true)
        }
        return builder.build()
    }

    fun setPriority(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManager.IMPORTANCE_LOW
        } else Notification.PRIORITY_LOW
    }
}