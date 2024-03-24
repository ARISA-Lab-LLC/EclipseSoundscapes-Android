package org.eclipsesoundscapes

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import dagger.hilt.android.HiltAndroidApp
import org.eclipsesoundscapes.data.DataManager
import org.eclipsesoundscapes.util.LocaleUtils
import javax.inject.Inject

@HiltAndroidApp
class EclipseSoundscapesApp : Application() {

    @Inject
    lateinit var dataManager: DataManager

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);

        setup()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleUtils.updateLocale(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleUtils.updateLocale(this)
    }

    private fun setup() {
        // allow user to simulate location on every launch
        dataManager.simulated = false

        // reset flag to prompt user for critical app permissions
        dataManager.skippedLocationsPermission = false
        dataManager.skippedNotificationsPermission = false
        dataManager.skippedAlarmsPermission = false
    }
}