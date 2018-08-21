package org.eclipsesoundscapes;

import android.app.Application;

import org.eclipsesoundscapes.data.DataManager;
import org.eclipsesoundscapes.data.SharedPrefsHelper;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class EclipseSoundscapesApp extends Application {
    DataManager dataManager;

    @Override
    public void onCreate() {
        super.onCreate();
//        Fabric.with(this, new Crashlytics());

        SharedPrefsHelper sharedPrefsHelper = new SharedPrefsHelper(getApplicationContext());
        dataManager = new DataManager(sharedPrefsHelper);
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}
