package org.eclipsesoundscapes.ui.about;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.eclipsesoundscapes.BuildConfig;
import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.model.Eclipse;
import org.eclipsesoundscapes.model.PhotoCredit;
import org.eclipsesoundscapes.ui.base.BaseActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

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
 * <p>
 * Display legal document depending on intent from SettingsActivity
 * E.g Application license, Photo Credits, open source libraries
 * See {@link SettingsActivity}
 */

public class LegalActivity extends BaseActivity {

    public static final String EXTRA_LEGAL = "legal";
    public static final String EXTRA_LICENSE = "license";
    public static final String EXTRA_LIBS = "libraries";
    public static final String EXTRA_PHOTO_CREDS = "photo_credits";
    public static final String EXTRA_PRIVACY_POLICY = "privacy_policy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String legalMode = getIntent().getStringExtra(EXTRA_LEGAL);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switch (legalMode) {
            case EXTRA_LICENSE:
                String versionName = BuildConfig.VERSION_NAME;
                String title = getString(R.string.app_name_version, getString(R.string.app_name),
                        versionName);
                getSupportActionBar().setTitle(title);
                setTitle(title);
                setContentView(R.layout.activity_legal_license);
                showWebView(R.id.license_details, "gpl_3.0.html");
                break;
            case EXTRA_LIBS:
                title = getString(R.string.open_src_libs);
                getSupportActionBar().setTitle(title);
                setTitle(title);
                setContentView(R.layout.activity_legal_libraries);
                showWebView(R.id.jsyn_license, "apache_license_2.0.html");
                break;
            case EXTRA_PRIVACY_POLICY:
                title = getString(R.string.privacy_policy);
                getSupportActionBar().setTitle(title);
                setTitle(title);
                setContentView(R.layout.activity_legal_webview);
                showPrivacyPolicy();
                break;
            case EXTRA_PHOTO_CREDS:
                title = getString(R.string.photo_credits);
                getSupportActionBar().setTitle(title);
                setTitle(title);
                setContentView(R.layout.legal_photo_credits);

                RecyclerView recyclerView = findViewById(R.id.photo_credits_recycler);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(new PhotoCreditAdapter(createPhotoCredits()));
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }

    private void showPrivacyPolicy() {
        final WebView webView = findViewById(R.id.webview);
        final ProgressBar progressBar = findViewById(R.id.webview_progress);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });

        final String url = getString(R.string.privacy_policy_url);
        webView.loadUrl(url);
    }

    private void showWebView(final int webViewId, final String fileName) {
        final WebView webView = findViewById(webViewId);
        final String data = readAsset(fileName);
        if (data != null && !data.isEmpty()) {
            final String mimeType = "text/html";
            final String encoding = "UTF-8";
            webView.loadData(data, mimeType, encoding);
        }
    }

    private String readAsset(final String fileName) {
        AssetManager assetManager = getAssets();
        try {
            final InputStream input = assetManager.open(fileName);
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            return new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Creates a list of entities credited for {@link Eclipse} images
     * @return a list of {@link PhotoCredit}
     */
    private ArrayList<PhotoCredit> createPhotoCredits() {
        final ArrayList<PhotoCredit> credits = new ArrayList<>();

        for (Eclipse eclipse : Eclipse.Companion.photoCreditEclipses()) {
            PhotoCredit photoCredit = null;

            switch (eclipse) {
                case FIRST_CONTACT:
                    photoCredit = new PhotoCredit(eclipse, getString(R.string.credits_first_contact),
                            getString(R.string.credits_link_first_contact));
                    break;
                case BAILYS_BEADS:
                case BAILYS_BEADS_CLOSEUP:
                    photoCredit = new PhotoCredit(eclipse, getString(R.string.credits_bailys_beads),
                            getString(R.string.credits_link_bailys_beads));
                    break;
                case CORONA:
                    photoCredit = new PhotoCredit(eclipse, getString(R.string.credits_corona),
                            getString(R.string.credits_link_corona));
                    break;
                case DIAMOND_RING:
                    photoCredit = new PhotoCredit(eclipse, getString(R.string.credits_diamond_ring),
                            getString(R.string.credits_link_diamond_ring));
                    break;
                case HELMET_STREAMER:
                case HELMET_STREAMER_CLOSEUP:
                    photoCredit = new PhotoCredit(eclipse, getString(R.string.credits_helmet_streamers),
                            getString(R.string.credits_link_helmet_streamers));
                    break;
                case PROMINENCE:
                case PROMINENCE_CLOSEUP:
                    photoCredit = new PhotoCredit(eclipse, getString(R.string.credits_prominence),
                            getString(R.string.credits_link_prominence));
                    break;
                case TOTALITY:
                    photoCredit = new PhotoCredit(eclipse, getString(R.string.credits_totality),
                            getString(R.string.credits_link_totality));
                    break;
                case ANNULAR_START:
                    photoCredit = new PhotoCredit(eclipse, getString(R.string.credits_annular_start),
                            getString(R.string.credits_link_annular_start));
                    break;
                case ANNULAR_PHASE_START:
                    photoCredit = new PhotoCredit(eclipse, getString(R.string.credits_annular_phase_start),
                            getString(R.string.credits_link_annular_phase_start));
                    break;
                case ANNULARITY:
                    photoCredit = new PhotoCredit(eclipse, getString(R.string.credits_annularity),
                            getString(R.string.credits_link_annularity));
                    break;
                case ANNULAR_PHASE_END:
                    photoCredit = new PhotoCredit(eclipse, getString(R.string.credits_annular_phase_end),
                            getString(R.string.credits_link_annular_phase_end));
                    break;
                case ANNULAR_END:
                    photoCredit = new PhotoCredit(eclipse, getString(R.string.credits_annular_end),
                            getString(R.string.credits_link_annular_end));
                    break;
            }

            credits.add(photoCredit);
        }

        return credits;
    }
}
