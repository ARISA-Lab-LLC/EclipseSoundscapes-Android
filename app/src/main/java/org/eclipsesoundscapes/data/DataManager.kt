package org.eclipsesoundscapes.data

import android.location.Location
import androidx.core.util.Pair
import org.eclipsesoundscapes.util.DateTimeUtils
import org.joda.time.DateTime
import java.util.*

class DataManager(private val sharedPrefsHelper: SharedPrefsHelper) {
    var featuresPosition: Pair<Int, Int>? = null
        private set

    fun saveFeaturesPosition(page: Int, tab: Int) {
        featuresPosition = Pair<Int, Int>(page, tab)
    }

    var language: String?
        get() = sharedPrefsHelper.preferredLanguage
        set(language) {
            sharedPrefsHelper.preferredLanguage = language
        }

    var firstContact: String?
        get() = sharedPrefsHelper.firstContact
        set(date) {
            sharedPrefsHelper.saveFirstContact(date)
        }

    var totality: String?
        get() = sharedPrefsHelper.totality
        set(date) {
            sharedPrefsHelper.saveTotality(date)
        }

    var simulated: Boolean
        get() = sharedPrefsHelper.simulated
        set(simulated) {
            sharedPrefsHelper.saveSimulated(simulated)
        }

    var walkthroughComplete: Boolean
        get() = sharedPrefsHelper.walkthroughComplete
        set(completed) {
            sharedPrefsHelper.saveWalkthroughComplete(completed)
        }

    var locationAccess: Boolean
        get() = sharedPrefsHelper.locationAccess
        set(access) {
            sharedPrefsHelper.saveLocationAccess(access)
        }

    var requestedLocation: Boolean
        get() = sharedPrefsHelper.requestedLocation
        set(requested) {
            sharedPrefsHelper.saveRequestedLocation(requested)
        }

    val notifications: Boolean
        get() = sharedPrefsHelper.notifications

    var currentEclipseDate: String?
        get() = sharedPrefsHelper.eclipseDate
        set(eclipseDate) {
            sharedPrefsHelper.eclipseDate = eclipseDate
        }

    var lastLocation: Location?
        get() = sharedPrefsHelper.lastLocation
        set(location) {
            sharedPrefsHelper.lastLocation = location
        }

    fun setRumblingCheckpoint(eclipseId: String?, coordinates: String?) {
        sharedPrefsHelper.setCheckpoint(eclipseId, coordinates)
    }

    fun getRumblingCheckpoint(eclipseId: String?): String {
        return sharedPrefsHelper.getCheckpoint(eclipseId)
    }

    fun setNotification(canSendNotifications: Boolean) {
        sharedPrefsHelper.saveNotification(canSendNotifications)
    }

    fun isAfterFirstContact() : Boolean {
        return firstContact?.let {
            afterCurrentDate(it)
        } ?: false
    }

    fun isAfterTotality() : Boolean {
        return totality?.let {
            afterCurrentDate(it)
        } ?: false
    }

    private fun afterCurrentDate(dateString: String) : Boolean {
        val date = DateTimeUtils.eclipseEventDate(dateString)
        return date?.let {
            val current = DateTime.now()
            current.isEqual(date) || current.isAfter(date)
        } ?: false
    }

    fun firstContactDate() : Date? {
        return firstContact?.let {
            DateTimeUtils.eclipseDateFormatToDate(it)
        }
    }

    fun totalityDate() : Date? {
        return totality?.let {
            DateTimeUtils.eclipseDateFormatToDate(it)
        }
    }
}