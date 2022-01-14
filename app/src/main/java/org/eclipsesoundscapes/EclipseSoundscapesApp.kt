package org.eclipsesoundscapes

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import org.eclipsesoundscapes.data.DataManager
import org.eclipsesoundscapes.data.SharedPrefsHelper
import org.eclipsesoundscapes.util.LocaleUtils

class EclipseSoundscapesApp : Application() {

    lateinit var dataManager: DataManager
        private set

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
        val sharedPrefsHelper = SharedPrefsHelper(applicationContext)
        dataManager = DataManager(sharedPrefsHelper)

        // allow user to simulate location on every launch
        dataManager.simulated = false
    }
}