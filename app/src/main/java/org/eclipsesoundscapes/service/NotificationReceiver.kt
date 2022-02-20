package org.eclipsesoundscapes.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import org.eclipsesoundscapes.EclipseSoundscapesApp
import org.eclipsesoundscapes.data.EclipseConfigurationRepository
import org.eclipsesoundscapes.data.EclipseExplorer
import org.eclipsesoundscapes.extensions.goAsync
import org.eclipsesoundscapes.model.Eclipse
import org.eclipsesoundscapes.model.EclipseType
import org.eclipsesoundscapes.service.NotificationScheduler.NOTIFICATION_ECLIPSE_EVENT
import org.eclipsesoundscapes.service.NotificationScheduler.NOTIFICATION_ECLIPSE_TYPE
import org.eclipsesoundscapes.service.NotificationScheduler.NOTIFICATION_LAUNCH_MEDIA
import javax.inject.Inject

/*
 * Displays any scheduled notification or re-schedules them after system reboot
 */
@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var eclipseConfigurationRepository: EclipseConfigurationRepository

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

        NotificationScheduler.showNotification(context, eclipse, type, showMedia, intent)
    }

    private fun rescheduleNotifications(context: Context) {
        goAsync {
            (context.applicationContext as? EclipseSoundscapesApp)?.let {
                it.dataManager.currentEclipseDate?.let { date ->
                    it.dataManager.lastLocation?.let { location ->
                        eclipseConfigurationRepository.eclipseConfiguration(date).collect { config ->
                            if (config != null) {
                                val eclipseExplorer = EclipseExplorer(context, config, location.latitude, location.longitude)
                                NotificationScheduler.scheduleNotifications(context, eclipseExplorer)
                            }
                        }
                    }
                }
            }
        }
    }
}