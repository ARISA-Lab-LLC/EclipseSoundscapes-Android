package org.eclipsesoundscapes.ui.base

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import org.eclipsesoundscapes.util.LocaleUtils

open class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleUtils.updateLocale(newBase))
    }
}