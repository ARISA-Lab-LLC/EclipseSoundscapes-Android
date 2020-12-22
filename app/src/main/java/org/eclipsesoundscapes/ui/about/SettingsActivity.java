package org.eclipsesoundscapes.ui.about;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import org.eclipsesoundscapes.EclipseSoundscapesApp;
import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.data.DataManager;
import org.eclipsesoundscapes.ui.base.BaseActivity;
import org.eclipsesoundscapes.ui.main.MainActivity;

import java.util.Locale;

import static org.eclipsesoundscapes.ui.about.LegalActivity.EXTRA_LEGAL;
import static org.eclipsesoundscapes.ui.about.LegalActivity.EXTRA_LIBS;
import static org.eclipsesoundscapes.ui.about.LegalActivity.EXTRA_LICENSE;
import static org.eclipsesoundscapes.ui.about.LegalActivity.EXTRA_PHOTO_CREDS;
import static org.eclipsesoundscapes.ui.about.LegalActivity.EXTRA_PRIVACY_POLICY;
import static org.eclipsesoundscapes.ui.main.MainActivity.EXTRA_FRAGMENT_TAG;


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
 * See {@link LegalActivity}
 */

public class SettingsActivity extends BaseActivity {

    public static final String EXTRA_SETTINGS_MODE = "SETTINGS_MODE";
    public static final String MODE_SETTINGS = "settings";
    public static final String MODE_LEGAL = "legal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupActionBar();

        final String settingsMode = getIntent().getStringExtra(EXTRA_SETTINGS_MODE);
        if (settingsMode != null && !settingsMode.isEmpty()) {
            final boolean isSettings = settingsMode.equals(MODE_SETTINGS);
            final String title =  getString(isSettings ? R.string.settings : R.string.legal);
            final Fragment fragment = isSettings ? new SettingsPreferenceFragment() : new LegalPreferenceFragment();

            setTitle(title);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_container, fragment)
                    .commit();
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!isTaskRoot()) {
            super.onBackPressed();
            finish();
        } else {
            final Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(EXTRA_FRAGMENT_TAG, AboutFragment.class.getSimpleName());
            startActivity(intent);
        }

        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class SettingsPreferenceFragment extends PreferenceFragmentCompat {
        SwitchPreferenceCompat locationPref;
        SwitchPreferenceCompat notificationPref;
        DataManager dataManager;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.pref_settings, rootKey);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            if (getActivity() != null && getActivity().getApplication() != null) {
                dataManager = ((EclipseSoundscapesApp) getActivity().getApplication()).getDataManager();
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            setupMenuOptions();
        }

        private void setupMenuOptions() {
            locationPref = findPreference("settings_location");
            notificationPref = findPreference("notifications_enabled");

            if (notificationPref != null) {
                notificationPref.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean isEnabled = (boolean) newValue;
                    if (dataManager != null) {
                        dataManager.setNotification(isEnabled);
                    }

                    return true;
                });
            }

            if (locationPref != null) {
                locationPref.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean isAccessible = (boolean) newValue;
                    if (dataManager != null) {
                        dataManager.setLocationAccess(isAccessible);
                    }

                    return true;
                });
            }

            final Preference languagePref = findPreference("language");
            if (languagePref != null) {
                final String language = dataManager.getLanguage();
                if (!language.isEmpty()) {
                    final Locale locale = new Locale(language);
                    languagePref.setSummary(locale.getDisplayName());
                }

                languagePref.setOnPreferenceClickListener(preference -> {
                    if (getActivity() != null) {
                        startActivity(new Intent(getActivity(), LanguageSelectionActivity.class));
                        getActivity().overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                    }

                    return false;
                });
            }
        }
    }

    /******************************************************************************
     * Preference fragment to display legal documents
     *****************************************************************************/
    public static class LegalPreferenceFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.pref_legal, rootKey);
            setupPreferences();
        }

        private void setupPreferences() {
            Preference license = findPreference("license_display");
            Preference libraries = findPreference("libraries_display");
            Preference credits = findPreference("credits_display");
            Preference privacyPolicy = findPreference("privacy_policy");

            if (license != null) {
                license.setOnPreferenceClickListener(preference -> {
                    showLegality(EXTRA_LICENSE);
                    return true;
                });
            }

            if (libraries != null) {
                libraries.setOnPreferenceClickListener(preference -> {
                    showLegality(EXTRA_LIBS);
                    return true;
                });
            }

            if (credits != null) {
                credits.setOnPreferenceClickListener(preference -> {
                    showLegality(EXTRA_PHOTO_CREDS);
                    return true;
                });
            }

            if (privacyPolicy != null) {
                privacyPolicy.setOnPreferenceClickListener(preference -> {
                    showLegality(EXTRA_PRIVACY_POLICY);
                    return true;
                });
            }
        }

        private void showLegality(final String extra){
            final Intent legalIntent = new Intent(getActivity(), LegalActivity.class);
            legalIntent.putExtra(EXTRA_LEGAL, extra);

            if (getActivity() != null) {
                startActivity(legalIntent);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        }
    }
}
