package org.eclipsesoundscapes;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import org.eclipsesoundscapes.data.DataManager;
import org.eclipsesoundscapes.data.SharedPrefsHelper;
import org.eclipsesoundscapes.util.LocaleUtils;

public class EclipseSoundscapesApp extends Application {

    private DataManager dataManager;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.updateLocale(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleUtils.updateLocale(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Fabric.with(this, new Crashlytics());
    }

    public DataManager getDataManager() {
        if (dataManager == null) {
            final SharedPrefsHelper sharedPrefsHelper = new SharedPrefsHelper(getApplicationContext());
            dataManager = new DataManager(sharedPrefsHelper);
        }

        return dataManager;
    }
}
