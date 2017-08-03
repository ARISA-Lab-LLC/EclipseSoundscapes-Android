package org.eclipsesoundscapes.eclipsesoundscapes.activity;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.eclipsesoundscapes.eclipsesoundscapes.R;
import org.eclipsesoundscapes.eclipsesoundscapes.fragments.PermissionDialogFragment;

/**
 *  SettingsActivity used to track user permissions for notifications and location
 *  Also used to display legal documents, copyrights, license and acknowledgements
 *  for the Eclipse Soundscapes Project.
 */

public class SettingsActivity extends AppCompatPreferenceActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 47;
    private static final int ACTIVITY_RESULT_CODE = 12;
    String settingsMode;
    Context mContext;
    SharedPreferences preference;

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

        preference = PreferenceManager.getDefaultSharedPreferences(this);

        settingsMode = getIntent().getStringExtra("settings");
        mContext = this;

        if (settingsMode != null && !settingsMode.isEmpty()) {
            if (settingsMode.equals("settings")) {
                setTitle(getString(R.string.title_activity_settings));
                getFragmentManager().beginTransaction().replace(android.R.id.content,
                        new SettingsPreferenceFragment())
                        .commit();
            }
            else {
                setTitle(getString(R.string.settings_legal));
                getFragmentManager().beginTransaction().replace(android.R.id.content,
                        new LegalPreferenceFragment())
                        .commit();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            PermissionDialogFragment fragment = (PermissionDialogFragment) getFragmentManager().findFragmentByTag("dialog");
            fragment.permissionEnabled();
            SharedPreferences.Editor editor = preference.edit();
            editor.putBoolean("settings_location", true);
            editor.apply();
            return;
        }

        // permission denied
        SharedPreferences.Editor editor = preference.edit();
        editor.putBoolean("settings_location", false);
        editor.apply();

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, ACTIVITY_RESULT_CODE);
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Disabled!")
                .setMessage(R.string.location_permission_denied)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_RESULT_CODE) {
            int rc = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
            if (rc == PackageManager.PERMISSION_GRANTED) {
                SharedPreferences.Editor editor = preference.edit();
                editor.putBoolean("settings_location", true);
                editor.apply();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }


    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SettingsPreferenceFragment extends PreferenceFragment {

        Context mContext;
        SwitchPreference locationPref;
        SharedPreferences sharedPreferences;
        DialogFragment newFragment;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);
            setHasOptionsMenu(true);

            locationPref = (SwitchPreference) findPreference("settings_location");
            locationPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    boolean isEnabled = (Boolean) o;
                    if (!locationPref.isChecked()) {
                        int rc = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
                        if (rc != PackageManager.PERMISSION_GRANTED) {
                            showLocationDialog();
                            return false;
                        }
                    }

                    return true;
                }
            });
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            mContext = context;
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }


        @Override
        public void onResume() {
            super.onResume();

            int rc = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
            if (rc != PackageManager.PERMISSION_GRANTED) {
              locationPref.setChecked(false);
            } else {
                if (sharedPreferences.getBoolean("settings_location", false))
                    locationPref.setChecked(true);
            }
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        public void showLocationDialog(){
            newFragment = PermissionDialogFragment.newInstance();
            newFragment.show(getFragmentManager(), "dialog");
        }


    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class LegalPreferenceFragment extends PreferenceFragment {

        Context mContext;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_legal);
            setHasOptionsMenu(true);

            Preference license = findPreference("license_display");
            Preference libraries = findPreference("libraries_display");

            license.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent legalIntent = new Intent(mContext, LegalActivity.class);
                    legalIntent.putExtra("legal", "license");
                    mContext.startActivity(legalIntent);
                    return true;
                }
            });

            libraries.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent legalIntent = new Intent(mContext, LegalActivity.class);
                    legalIntent.putExtra("legal", "libraries");
                    mContext.startActivity(legalIntent);
                    return true;
                }
            });

        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            mContext = context;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            return super.onOptionsItemSelected(item);
        }
    }
}
