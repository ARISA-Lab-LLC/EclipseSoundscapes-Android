package org.eclipsesoundscapes.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import org.eclipsesoundscapes.EclipseSoundscapesApp;
import org.eclipsesoundscapes.data.DataManager;
import org.eclipsesoundscapes.data.SharedPrefsHelper;

import java.util.Locale;

public class LocaleUtils {

    public static Context updateLocale(final Context context) {
        final String language = getLanguage(context);
        if (language == null || language.isEmpty()) {
            return context;
        }

        final Locale locale = new Locale(language);
        Locale.setDefault(locale);

        final Resources res = context.getResources();
        final Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        return context.createConfigurationContext(config);
    }

    private static String getLanguage(final Context context) {
        DataManager dataManager;
        final EclipseSoundscapesApp app = ((EclipseSoundscapesApp) context.getApplicationContext());

        if (app == null) {
            final SharedPrefsHelper sharedPrefsHelper = new SharedPrefsHelper(context);
            dataManager = new DataManager(sharedPrefsHelper);
        } else {
            dataManager = app.getDataManager();
        }

        return dataManager.getLanguage();
    }
}
