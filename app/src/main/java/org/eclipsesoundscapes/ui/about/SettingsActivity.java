package org.eclipsesoundscapes.ui.about;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import org.eclipsesoundscapes.EclipseSoundscapesApp;
import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.data.DataManager;


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

public class SettingsActivity extends AppCompatPreferenceActivity {

    public static final String EXTRA_SETTINGS_MODE = "SETTINGS_MODE";
    public static final String MODE_SETTINGS = "settings";
    public static final String MODE_LEGAL = "legal";

    private DataManager dataManager;

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        dataManager = ((EclipseSoundscapesApp)getApplication()).getDataManager();
        String settingsMode = getIntent().getStringExtra(EXTRA_SETTINGS_MODE);

        if (settingsMode != null && !settingsMode.isEmpty()) {
            if (settingsMode.equals(MODE_SETTINGS)) {
                // show notification and location view
                setTitle(getString(R.string.settings));
                getFragmentManager().beginTransaction().replace(android.R.id.content,
                        new SettingsPreferenceFragment())
                        .commit();
            }
            else {
                // show legal preference fragment
                setTitle(getString(org.eclipsesoundscapes.R.string.legal));
                getFragmentManager().beginTransaction().replace(android.R.id.content,
                        new LegalPreferenceFragment())
                        .commit();
            }
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class SettingsPreferenceFragment extends PreferenceFragment {
        SwitchPreference locationPref;
        SwitchPreference notificationPref;
        DataManager dataManager;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);
            setHasOptionsMenu(true);

            locationPref = (SwitchPreference) findPreference("settings_location");
            notificationPref = (SwitchPreference) findPreference("notifications_enabled");

            notificationPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    boolean isEnabled = (boolean) o;
                    dataManager.setNotification(isEnabled);
                    return true;
                }
            });

            locationPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    boolean isAccessible = (boolean) o;
                    dataManager.setLocationAccess(isAccessible);
                    return true;
                }
            });
        }

        @Override
        public void onStart() {
            super.onStart();
            if (getActivity() != null){
                if (getActivity() instanceof SettingsActivity){
                    dataManager = ((SettingsActivity) getActivity()).getDataManager();
                }
            }
        }
    }

    /******************************************************************************
     * Preference fragment to display legal documents
     *****************************************************************************/
    public static class LegalPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_legal);
            setHasOptionsMenu(true);

            Preference license = findPreference("license_display");
            Preference libraries = findPreference("libraries_display");
            Preference credits = findPreference("credits_display");

            license.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent legalIntent = new Intent(getActivity(), LegalActivity.class);
                    legalIntent.putExtra("legal", "license");
                    showLegality(legalIntent);
                    return true;
                }
            });

            libraries.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent legalIntent = new Intent(getActivity(), LegalActivity.class);
                    legalIntent.putExtra("legal", "libraries");
                    showLegality(legalIntent);
                    return true;
                }
            });

            credits.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent legalIntent = new Intent(getActivity(), LegalActivity.class);
                    legalIntent.putExtra("legal", "credits");
                    showLegality(legalIntent);
                    return true;
                }
            });

        }

        private void showLegality(Intent intent){
            if (getActivity() != null) {
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        }
    }
}
