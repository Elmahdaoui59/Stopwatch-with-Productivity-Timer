package org.hyperskill.stopwatch

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

const val CHANNEL_ID = "org.hyperskill"
const val NOTIFICATION_ID = 393939

class NotificationManagement(
    private val context: Context
): Application() {
    init {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Stopwatch State"
            val descriptionText = "your stopwatch status"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)


        }
    }
    val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_notification_overlay)
        .setContentTitle("Notification")
        .setContentText("Time exceeded")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
}