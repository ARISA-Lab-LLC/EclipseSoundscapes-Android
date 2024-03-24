package org.eclipsesoundscapes.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.data.EclipseExplorer
import org.eclipsesoundscapes.model.Eclipse
import org.eclipsesoundscapes.model.EclipseType
import org.eclipsesoundscapes.model.EclipseVisibility
import org.eclipsesoundscapes.model.MediaItem
import org.eclipsesoundscapes.ui.main.MainActivity
import org.eclipsesoundscapes.ui.media.MediaPlayerActivity
import org.eclipsesoundscapes.util.DateTimeUtils
import org.eclipsesoundscapes.util.MediaUtils
import org.eclipsesoundscapes.util.NotificationUtils.NOTIFICATION_CHANNEL_ID
import org.eclipsesoundscapes.util.NotificationUtils.createNotificationChannel
import org.eclipsesoundscapes.util.PermissionUtils
import org.joda.time.DateTime

/*
 * Schedules notification reminder for Eclipse events
 */
object NotificationScheduler {
    private const val NOTIFICATION_ID = 100

    private const val MEDIA_REQUEST_CODE_OFFSET = 1
    private const val FIRST_CONTACT_REQUEST_CODE = 2
    private const val SECOND_CONTACT_REQUEST_CODE = 3
    private const val MID_CONTACT_REQUEST_CODE = 4

    const val NOTIFICATION_ECLIPSE_EVENT = "eclipse_event"
    const val NOTIFICATION_ECLIPSE_TYPE = "eclipse_type"
    const val NOTIFICATION_LAUNCH_MEDIA = "launch_media"

    fun scheduleNotifications(context: Context, eclipseExplorer: EclipseExplorer) {
        if (eclipseExplorer.eclipseConfiguration.type == EclipseType.ANNULAR) {
            scheduleAnnularNotifications(context, eclipseExplorer)
        } else if (eclipseExplorer.eclipseConfiguration.type == EclipseType.TOTAL) {
            scheduleTotalNotifications(context, eclipseExplorer)
        }
    }

    private fun scheduleAnnularNotifications(context: Context, eclipseExplorer: EclipseExplorer) {
        // first contact
        DateTimeUtils.eventLocalTime(eclipseExplorer.contact1())?.let {
            scheduleNotification(context, Eclipse.ANNULAR_START, eclipseExplorer.eclipseType, it.minusMinutes(2))
            scheduleNotification(context, Eclipse.ANNULAR_START, eclipseExplorer.eclipseType, it.minusSeconds(10), true)
        }

        // annularity
        DateTimeUtils.eventLocalTime(eclipseExplorer.contactMid())?.let {
            scheduleNotification(context, Eclipse.ANNULARITY, eclipseExplorer.eclipseType, it.minusSeconds(30), true)
        }

        if (eclipseExplorer.eclipseVisibility == EclipseVisibility.FULL) {
            // annular phase start
            DateTimeUtils.eventLocalTime(eclipseExplorer.contact2())?.let {
                // scheduled 30 seconds + length of audio file before event
                val mediaDuration = MediaUtils.getPhaseStartOffset(context)
                scheduleNotification(context, Eclipse.ANNULAR_PHASE_START, eclipseExplorer.eclipseType, it.minusSeconds(
                    mediaDuration
                ), true)
            }
        }
    }

    private fun scheduleTotalNotifications(context: Context, eclipseExplorer: EclipseExplorer) {
        // first contact
        DateTimeUtils.eventLocalTime(eclipseExplorer.contact1())?.let {
            scheduleNotification(context, Eclipse.FIRST_CONTACT, eclipseExplorer.eclipseType, it.minusMinutes(2))
            scheduleNotification(context, Eclipse.FIRST_CONTACT, eclipseExplorer.eclipseType, it.minusSeconds(10), true)
        }

        if (eclipseExplorer.eclipseVisibility == EclipseVisibility.FULL) {
            // totality
            DateTimeUtils.eventLocalTime(eclipseExplorer.contactMid())?.let {
                scheduleNotification(context, Eclipse.TOTALITY, eclipseExplorer.eclipseType,
                    it.minusSeconds(30), true)
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification(context: Context, eclipse: Eclipse, eclipseType: EclipseType,
                                     dateTime: DateTime, showMedia: Boolean = false) {
        if (dateTime.isBeforeNow) {
            return
        }

        val pendingIntent = createPendingIntent(context, eclipse, eclipseType, showMedia)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (PermissionUtils.hasAlarmPermission(context)) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dateTime.millis, pendingIntent)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dateTime.millis, pendingIntent)
        }
    }

    private fun createPendingIntent(context: Context, eclipse: Eclipse, eclipseType: EclipseType,
                                    showMedia: Boolean = false) : PendingIntent {

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra(NOTIFICATION_ECLIPSE_EVENT, eclipse.name)
            putExtra(NOTIFICATION_ECLIPSE_TYPE, eclipseType.name)
            putExtra(NOTIFICATION_LAUNCH_MEDIA, showMedia)
        }

        val flags = if (SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        return PendingIntent.getBroadcast(context,
            requestCode(eclipse, showMedia),
            intent,
            flags)
    }

    private fun requestCode(eclipse: Eclipse, showMedia: Boolean) : Int {
        var requestCode: Int = when (eclipse) {
            Eclipse.FIRST_CONTACT, Eclipse.ANNULAR_START -> {
                FIRST_CONTACT_REQUEST_CODE
            }
            Eclipse.TOTALITY, Eclipse.ANNULAR_PHASE_START -> {
                SECOND_CONTACT_REQUEST_CODE
            }
            Eclipse.ANNULARITY -> {
                MID_CONTACT_REQUEST_CODE
            }
            else -> {
                0
            }
        }

        if (showMedia) {
            requestCode += MEDIA_REQUEST_CODE_OFFSET
        }

        return requestCode
    }

    fun showNotification(context: Context, event: Eclipse, eclipseType: EclipseType,
                         launchMedia: Boolean) {
        val resultIntent: Intent
        val title: String
        val description: String

        if (launchMedia) {
            resultIntent = Intent(context, MediaPlayerActivity::class.java)
            val mediaItem = MediaItem(event.imageResource(), event.title(),
                event.shortAudioDescription(), event.shortAudio())
            resultIntent.putExtra(MediaPlayerActivity.EXTRA_MEDIA, mediaItem)
            resultIntent.putExtra(MediaPlayerActivity.EXTRA_LIVE, true)

            title = when (event) {
                Eclipse.FIRST_CONTACT -> context.getString(R.string.notification_first_contact)
                Eclipse.TOTALITY -> context.getString(R.string.notification_totality)
                Eclipse.ANNULAR_START -> context.getString(R.string.notification_annular_start)
                Eclipse.ANNULAR_PHASE_START -> context.getString(R.string.notification_annular_phase_start)
                Eclipse.ANNULARITY -> context.getString(R.string.notification_annularity)
                else -> ""
            }
            description = context.getString(R.string.tap_listen)
        } else {
            resultIntent = Intent(context, MainActivity::class.java)
            title = context.getString(R.string.app_name)
            description = when (eclipseType) {
                EclipseType.ANNULAR -> context.getString(R.string.annular_eclipse_reminder)
                EclipseType.TOTAL -> context.getString(R.string.total_eclipse_reminder)
                EclipseType.PARTIAL, EclipseType.NONE -> context.getString(R.string.default_eclipse_reminder)
            }
        }

        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(resultIntent)

            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        if (SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context)
        }

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setSmallIcon(R.drawable.ic_stat_ic_waves)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(resultPendingIntent)
            .setContentTitle(title)
            .setContentText(description)
            .setTicker(description)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}