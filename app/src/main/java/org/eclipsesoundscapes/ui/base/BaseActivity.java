package org.eclipsesoundscapes.ui.base;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import org.eclipsesoundscapes.util.LocaleUtils;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleUtils.updateLocale(newBase));
    }
}
