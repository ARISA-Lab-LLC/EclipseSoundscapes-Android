package org.eclipsesoundscapes

import android.app.Application
import android.content.Context
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import org.eclipsesoundscapes.data.DataManager
import org.eclipsesoundscapes.util.LocaleUtils
import javax.inject.Inject

@HiltAndroidApp
class EclipseSoundscapesApp : Application(), Configuration.Provider {

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) android.util.Log.DEBUG else android.util.Log.ERROR)
            .build()

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);

        setup()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleUtils.updateLocale(base))
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleUtils.updateLocale(this)
    }

    private fun setup() {
        if (BuildConfig.DEBUG) {
            StrictMode.enableDefaults()
        }

        // allow user to simulate location on every launch
        dataManager.simulated = false

        // reset flag to prompt user for critical app permissions
        dataManager.skippedLocationsPermission = false
        dataManager.skippedNotificationsPermission = false
        dataManager.skippedAlarmsPermission = false
    }
}