package org.eclipsesoundscapes.eclipsesoundscapes.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import org.eclipsesoundscapes.eclipsesoundscapes.R;

/**
 * Class is used to simply display legal document depending on intent from SettingsActivity
 */

public class LegalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String legalMode = getIntent().getStringExtra("legal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (legalMode.equals("license")) {
            getSupportActionBar().setTitle("License");
            setTitle("License"); // accessibility title read
            setContentView(R.layout.activity_legal_license);
        }
        else {
            getSupportActionBar().setTitle("Eclipse Soundscapes v1.0");
            setTitle("Eclipse Soundscapes v1.0"); // accessibility title read
            setContentView(R.layout.activity_legal_libraries);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        } else

            return super.onOptionsItemSelected(item);
    }
}
