package org.eclipsesoundscapes.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import org.eclipsesoundscapes.R

/*
 * Creates a notification channel that all notifications will be assigned to
 * starting Android 8.0 (API level 26)+
 *
 */
object NotificationUtils {
    const val NOTIFICATION_CHANNEL_ID = "org.eclipsesoundscapes.ECLIPSE_NOTIFICATION_ID"
    private const val CHANNEL_NAME = "Eclipse Notifications"

    @JvmStatic
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun createNotificationChannel(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
        )
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        // Configure the notification channel.
        notificationChannel.description = context.getString(R.string.notification_channel_desc)
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationChannel.lightColor = Color.parseColor("#E35E05")
        notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
        notificationChannel.setSound(sound, attributes)
        notificationManager.createNotificationChannel(notificationChannel)
    }
}