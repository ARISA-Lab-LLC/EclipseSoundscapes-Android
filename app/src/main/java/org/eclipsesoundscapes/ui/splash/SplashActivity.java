package org.eclipsesoundscapes.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;

import org.eclipsesoundscapes.EclipseSoundscapesApp;
import org.eclipsesoundscapes.data.DataManager;
import org.eclipsesoundscapes.ui.main.MainActivity;
import org.eclipsesoundscapes.ui.walkthrough.WalkthroughActivity;

import io.fabric.sdk.android.Fabric;

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
 * Splash screen, directs to {@link MainActivity} or {@link WalkthroughActivity}
 */

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        DataManager dataManager = ((EclipseSoundscapesApp)getApplication()).getDataManager();
        if (dataManager.getWalkthroughComplete()) {
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