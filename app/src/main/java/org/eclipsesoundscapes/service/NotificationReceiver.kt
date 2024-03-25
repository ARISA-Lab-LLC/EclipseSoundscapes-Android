package org.eclipsesoundscapes.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import org.eclipsesoundscapes.model.Eclipse
import org.eclipsesoundscapes.model.EclipseType
import org.eclipsesoundscapes.service.NotificationScheduler.NOTIFICATION_ECLIPSE_EVENT
import org.eclipsesoundscapes.service.NotificationScheduler.NOTIFICATION_ECLIPSE_TYPE
import org.eclipsesoundscapes.service.NotificationScheduler.NOTIFICATION_LAUNCH_MEDIA
import org.eclipsesoundscapes.workers.RescheduleNotificationsWorker

/*
 * Displays any scheduled notification or re-schedules them after system reboot
 */
@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != null && intent.action.equals(Intent.ACTION_BOOT_COMPLETED, ignoreCase = true)) {
            rescheduleNotifications(context)
        } else {
            showNotification(context, intent)
        }
    }

    private fun showNotification(context: Context, intent: Intent) {
        val eclipseName = intent.getStringExtra(NOTIFICATION_ECLIPSE_EVENT)
        val typeName = intent.getStringExtra(NOTIFICATION_ECLIPSE_TYPE)
        if (eclipseName.isNullOrEmpty() || typeName.isNullOrEmpty()) {
            return
        }

        val eclipse = enumValueOf<Eclipse>(eclipseName)
        val type = enumValueOf<EclipseType>(typeName)
        val showMedia = intent.getBooleanExtra(NOTIFICATION_LAUNCH_MEDIA, false)

        NotificationScheduler.showNotification(context, eclipse, type, showMedia)
    }

    private fun rescheduleNotifications(context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<RescheduleNotificationsWorker>()
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            RescheduleNotificationsWorker.NOTIFICATIONS_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest)
    }
}