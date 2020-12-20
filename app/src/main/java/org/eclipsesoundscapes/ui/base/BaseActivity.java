package org.eclipsesoundscapes.ui.base;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

import org.eclipsesoundscapes.util.LocaleUtils;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleUtils.updateLocale(newBase));
    }
}
