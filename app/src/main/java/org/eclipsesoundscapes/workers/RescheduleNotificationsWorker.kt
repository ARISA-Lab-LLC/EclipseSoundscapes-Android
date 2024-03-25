package org.eclipsesoundscapes.workers

import org.eclipsesoundscapes.EclipseSoundscapesApp
import org.eclipsesoundscapes.data.EclipseExplorer
import org.eclipsesoundscapes.service.NotificationScheduler
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipsesoundscapes.data.EclipseConfigurationRepository

@HiltWorker
class RescheduleNotificationsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private  val eclipseConfigurationRepository: EclipseConfigurationRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        (applicationContext as? EclipseSoundscapesApp)?.let {
            it.dataManager.currentEclipseDate?.let { date ->
                it.dataManager.lastLocation?.let { location ->
                    eclipseConfigurationRepository.eclipseConfiguration(date).collect { config ->
                        val eclipseExplorer = EclipseExplorer(applicationContext, config, location.latitude, location.longitude)
                        NotificationScheduler.scheduleNotifications(applicationContext, eclipseExplorer)

                        Result.success()
                    }
                }
            }
        }

        Result.failure()
    }

    companion object {
        const val NOTIFICATIONS_WORK_NAME = "RescheduleNotificationsWorker"
    }
}