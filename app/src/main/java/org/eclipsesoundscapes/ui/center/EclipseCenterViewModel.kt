package org.eclipsesoundscapes.ui.center

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.data.DataManager
import org.eclipsesoundscapes.data.EclipseConfigurationRepository
import org.eclipsesoundscapes.data.EclipseExplorer
import org.eclipsesoundscapes.model.EclipseConfiguration
import org.eclipsesoundscapes.model.EclipseType
import org.eclipsesoundscapes.util.DateTimeUtils
import org.eclipsesoundscapes.util.DateTimeUtils.dateToEclipseDateFormat
import org.eclipsesoundscapes.util.DateTimeUtils.eclipseDateFormatToDate
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class EclipseCenterViewModel @Inject internal constructor(
    application: Application,
    private val eclipseConfigurationRepository: EclipseConfigurationRepository,
    val dataManager: DataManager,
) : AndroidViewModel(application) {

    private val currentEclipseDate: MutableStateFlow<String?> = MutableStateFlow(dataManager.currentEclipseDate)

    val eclipseConfiguration: LiveData<EclipseConfiguration> = currentEclipseDate.flatMapLatest { date ->
        if (date.isNullOrEmpty()) {
            eclipseConfigurationRepository.nextEclipseConfiguration(dateToEclipseDateFormat())
        } else {
            val eclipseDate = eclipseDateFormatToDate(date)
            if (eclipseDate == null || showNextEclipse(eclipseDate)) {
                eclipseConfigurationRepository.nextEclipseConfiguration(dateToEclipseDateFormat())
            } else {
                eclipseConfigurationRepository.eclipseConfiguration(date)
            }
        }
    }.asLiveData()

    private fun showNextEclipse(eclipseDate: Date) : Boolean {
        val daysBetweenEclipse = getApplication<Application>()
            .applicationContext.resources
            .getInteger(R.integer.days_between_eclipse)

        return TimeUnit.MILLISECONDS.toDays(Date().time - eclipseDate.time) >= daysBetweenEclipse
    }

    fun saveEclipseDate(eclipseConfiguration: EclipseConfiguration) {
        dataManager.currentEclipseDate = eclipseConfiguration.date
    }

    fun saveEclipseEventDates(eclipseExplorer: EclipseExplorer) {
        if (eclipseExplorer.type == EclipseType.NONE) {
            return
        }

        val firstContactDate = DateTimeUtils.formatEclipseDate(eclipseExplorer.contact1())
        dataManager.firstContact = firstContactDate

        if (eclipseExplorer.isFullOrAnnular) {
            val secondContact = DateTimeUtils.formatEclipseDate(eclipseExplorer.contact2())
            dataManager.totality = secondContact
        }
    }

    fun firstContactDate() : Date? = dataManager.firstContactDate()

    fun totalityDate() : Date? = dataManager.totalityDate()

    fun afterTotality() : Boolean {
        return dataManager.isAfterTotality()
    }

    /**
     * Find the closest point in the path of totality from this location
     * @param location location not in path of totality
     */
    fun closestPointOnPath(location: Location) : Location? {
        return eclipseConfiguration.value?.let {
            var closestLocation: Location? = null
            var closestDistance: Double = Double.POSITIVE_INFINITY

            for (centralLine in it.centralLines) {
                for (coordinate in centralLine) {
                    val centralLocation = Location("")
                    centralLocation.latitude = coordinate.latitude
                    centralLocation.longitude = coordinate.longitude

                    val distance = location.distanceTo(centralLocation)
                    if (distance < closestDistance) {
                        closestDistance = distance.toDouble()
                        closestLocation = centralLocation
                    }
                }
            }

            return closestLocation
        }
    }
}