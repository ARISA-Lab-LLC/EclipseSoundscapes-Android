package org.eclipsesoundscapes.ui.base;

import android.content.Context;

import org.eclipsesoundscapes.ui.about.AppCompatPreferenceActivity;
import org.eclipsesoundscapes.util.LocaleUtils;

public class BasePreferenceActivity extends AppCompatPreferenceActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleUtils.updateLocale(newBase));
    }
}
