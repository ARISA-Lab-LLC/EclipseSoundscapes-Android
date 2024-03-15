package org.eclipsesoundscapes.data

import android.location.Location
import androidx.core.util.Pair

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

    var skippedLocationsPermission: Boolean
        get() = sharedPrefsHelper.skippedLocationPermission
        set(requested) {
            sharedPrefsHelper.saveSkippedLocationPermission(requested)
        }

    var requestedNotification: Boolean
        get() = sharedPrefsHelper.requestedNotifications
        set(requested) {
            sharedPrefsHelper.saveRequestedNotifications(requested)
        }

    var skippedNotificationsPermission: Boolean
        get() = sharedPrefsHelper.skippedNotificationsPermission
        set(requested) {
            sharedPrefsHelper.saveSkippedNotificationsPermission(requested)
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
}