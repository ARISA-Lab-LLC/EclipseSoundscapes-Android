package org.eclipsesoundscapes.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import org.eclipsesoundscapes.util.Constants;

/**
 * Simple splash screen that display's on initial launch and then launches the MainActivity
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getBoolean(Constants.PREFERENCE_WALKTHROUGH, false)) {
            // user has completed walk through
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, WalkthroughActivity.class);
            startActivity(intent);
            finish();
        }
    }

}