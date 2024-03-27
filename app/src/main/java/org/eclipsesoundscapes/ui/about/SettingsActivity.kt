package org.eclipsesoundscapes.ui.about

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import org.eclipsesoundscapes.EclipseSoundscapesApp
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.data.DataManager
import org.eclipsesoundscapes.databinding.ActivitySettingsBinding
import org.eclipsesoundscapes.ui.base.BaseActivity
import org.eclipsesoundscapes.ui.main.MainActivity

/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see [http://www.gnu.org/licenses/].
  * */
/**
 * @author Joel Goncalves
 *
 * Track user permissions for notifications and location access
 * Also display legal documents, copyrights, license and acknowledgements
 * for the Eclipse Soundscapes Project.
 * See [LegalActivity]
 */
class SettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingsBinding.inflate(layoutInflater).apply {
            setSupportActionBar(appBar.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        setContentView(binding.root)

        intent.getStringExtra(EXTRA_SETTINGS_MODE)?.let {
            if (it.isNotEmpty()) {
                val fragment: Fragment = if (it == MODE_SETTINGS) {
                    SettingsPreferenceFragment()
                } else {
                    LegalPreferenceFragment()
                }

                setTitle(
                    if (it == MODE_SETTINGS) {
                        R.string.permissions
                    } else {
                        R.string.legal
                    }
                )

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings_container, fragment)
                    .commit()
            }
        }

        onBackPressedDispatcher.addCallback {
            if (isTaskRoot) {
                startActivity(Intent(this@SettingsActivity, MainActivity::class.java).apply {
                    putExtra(MainActivity.EXTRA_FRAGMENT_TAG, AboutFragment::class.java.simpleName)
                })
            }

            finish()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
            } else {
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
            }

            return@addCallback
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    class SettingsPreferenceFragment : PreferenceFragmentCompat() {
        private var locationPref: SwitchPreferenceCompat? = null
        private var notificationPref: SwitchPreferenceCompat? = null

        private var dataManager: DataManager? = null
            get() {
                if (field == null) {
                    field = (context?.applicationContext as? EclipseSoundscapesApp)?.dataManager
                }

                return field
            }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.pref_settings, rootKey)
        }

        override fun onResume() {
            super.onResume()
            setupMenuOptions()
        }

        private fun setupMenuOptions() {
            locationPref = findPreference("settings_location")
            notificationPref = findPreference("notifications_enabled")

            notificationPref?.let {
                it.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
                        (newValue as? Boolean)?.let { value -> dataManager?.setNotification(value) }
                        true
                    }
            }

            locationPref?.let {
                it.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
                        (newValue as? Boolean)?.let { value -> dataManager?.locationAccess = value }
                        true
                    }
            }
        }
    }

    class LegalPreferenceFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.pref_legal, rootKey)
            setupPreferences()
        }

        private fun setupPreferences() {
            findPreference<Preference>("license_display")?.let {
                it.onPreferenceClickListener =
                    Preference.OnPreferenceClickListener {
                        showLegality(LegalActivity.EXTRA_LICENSE)
                        true
                    }
            }

            findPreference<Preference>("libraries_display")?.let {
                it.onPreferenceClickListener =
                    Preference.OnPreferenceClickListener {
                        showLegality(LegalActivity.EXTRA_LIBS)
                        true
                    }
            }

            findPreference<Preference>("tos")?.let {
                it.onPreferenceClickListener =
                    Preference.OnPreferenceClickListener {
                        showLegality(LegalActivity.EXTRA_TOS)
                        true
                    }
            }

            findPreference<Preference>("privacy_policy")?.let {
                it.onPreferenceClickListener =
                    Preference.OnPreferenceClickListener {
                        showLegality(LegalActivity.EXTRA_PRIVACY_POLICY)
                        true
                    }
            }
        }

        private fun showLegality(extra: String) {
            activity?.startActivity(Intent(activity, LegalActivity::class.java).apply {
                putExtra(LegalActivity.EXTRA_LEGAL, extra)
            })

            activity?.overridePendingTransition(
                R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_left
            )
        }
    }

    companion object {
        const val EXTRA_SETTINGS_MODE = "SETTINGS_MODE"
        const val MODE_SETTINGS = "settings"
        const val MODE_LEGAL = "legal"
    }
}