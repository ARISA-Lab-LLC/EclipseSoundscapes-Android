package org.eclipsesoundscapes.ui.about

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import org.eclipsesoundscapes.BuildConfig
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.databinding.ActivityLegalLibrariesBinding
import org.eclipsesoundscapes.databinding.ActivityLegalLicenseBinding
import org.eclipsesoundscapes.databinding.ActivityLegalWebviewBinding
import org.eclipsesoundscapes.databinding.LegalPhotoCreditsBinding
import org.eclipsesoundscapes.model.Eclipse
import org.eclipsesoundscapes.model.Eclipse.Companion.mediaEclipses
import org.eclipsesoundscapes.model.PhotoCredit
import org.eclipsesoundscapes.ui.base.BaseActivity
import java.io.IOException
import java.util.*

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
 *
 * Display legal document depending on intent from SettingsActivity
 * E.g Application license, Photo Credits, open source libraries
 * See [SettingsActivity]
 */
class LegalActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val legalMode = intent.getStringExtra(EXTRA_LEGAL)

        when (legalMode) {
            EXTRA_LICENSE -> {
                val binding = ActivityLegalLicenseBinding.inflate(layoutInflater).apply {
                    val title = getString(
                        R.string.app_name_version, getString(R.string.app_name),
                        BuildConfig.VERSION_NAME
                    )

                    setToolbar(appBar.toolbar, title)
                }

                setContentView(binding.root)
                showWebView(R.id.license_details, "gpl_3.0.html")
            }
            EXTRA_LIBS -> {
                val binding = ActivityLegalLibrariesBinding.inflate(layoutInflater).apply {
                    val title = getString(R.string.open_src_libs)
                    setToolbar(appBar.toolbar, title)
                }

                setContentView(binding.root)
                showWebView(R.id.jsyn_license, "apache_license_2.0.html")
            }
            EXTRA_PRIVACY_POLICY, EXTRA_TOS -> {
                val binding = ActivityLegalWebviewBinding.inflate(layoutInflater).apply {
                    val title = if (legalMode == EXTRA_PRIVACY_POLICY) {
                        getString(R.string.privacy_policy)
                    } else {
                        getString(R.string.tos)
                    }

                    setToolbar(appBar.toolbar, title)
                }

                setContentView(binding.root)
                showLegalLink(legalMode == EXTRA_PRIVACY_POLICY)
            }
            EXTRA_PHOTO_CREDS -> {
                val binding = LegalPhotoCreditsBinding.inflate(layoutInflater).apply {
                    val title = getString(R.string.photo_credits)
                    setToolbar(appBar.toolbar, title)

                    photoCreditsRecycler.layoutManager = LinearLayoutManager(this@LegalActivity)
                    photoCreditsRecycler.setHasFixedSize(true)
                    photoCreditsRecycler.adapter = PhotoCreditAdapter(createPhotoCredits(), PhotoCreditAdapter.CreditsClickListener {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.link)))
                    })
                }

                setContentView(binding.root)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
    }

    private fun showLegalLink(privacyPolicy: Boolean) {
        val webView = findViewById<WebView>(R.id.webview)
        val progressBar = findViewById<ProgressBar>(R.id.webview_progress)

        webView?.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar?.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar?.visibility = View.GONE
            }
        }

        val url = if (privacyPolicy) {
            getString(R.string.privacy_policy_url)
        } else {
            getString(R.string.tos_url)
        }

        webView?.loadUrl(url)
    }

    private fun showWebView(webViewId: Int, fileName: String) {
        val webView = findViewById<WebView>(webViewId)
        val data = readAsset(fileName)
        if (data.isNotEmpty()) {
            val mimeType = "text/html"
            val encoding = "UTF-8"
            webView?.loadData(data, mimeType, encoding)
        }
    }

    private fun readAsset(fileName: String): String {
        val assetManager = assets
        try {
            val input = assetManager.open(fileName)
            val size = input.available()
            val buffer = ByteArray(size)
            input.read(buffer)
            input.close()
            return String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * Creates a list of entities credited for [Eclipse] images
     * @return a list of [PhotoCredit]
     */
    private fun createPhotoCredits(): ArrayList<PhotoCredit> {
        val credits = ArrayList<PhotoCredit>()
        for (eclipse in mediaEclipses()) {
            val photoCredit: PhotoCredit = when (eclipse) {
                Eclipse.FIRST_CONTACT -> PhotoCredit(
                    eclipse, getString(R.string.credits_first_contact),
                    getString(R.string.credits_link_first_contact)
                )
                Eclipse.BAILYS_BEADS, Eclipse.BAILYS_BEADS_CLOSEUP -> PhotoCredit(
                    eclipse, getString(R.string.credits_bailys_beads),
                    getString(R.string.credits_link_bailys_beads)
                )
                Eclipse.CORONA -> PhotoCredit(
                    eclipse, getString(R.string.credits_corona),
                    getString(R.string.credits_link_corona)
                )
                Eclipse.DIAMOND_RING -> PhotoCredit(
                    eclipse, getString(R.string.credits_diamond_ring),
                    getString(R.string.credits_link_diamond_ring)
                )
                Eclipse.HELMET_STREAMER, Eclipse.HELMET_STREAMER_CLOSEUP -> PhotoCredit(
                    eclipse, getString(R.string.credits_helmet_streamers),
                    getString(R.string.credits_link_helmet_streamers)
                )
                Eclipse.PROMINENCE, Eclipse.PROMINENCE_CLOSEUP -> PhotoCredit(
                    eclipse, getString(R.string.credits_prominence),
                    getString(R.string.credits_link_prominence)
                )
                Eclipse.TOTALITY -> PhotoCredit(
                    eclipse, getString(R.string.credits_totality),
                    getString(R.string.credits_link_totality)
                )
                Eclipse.SUN_AS_STAR -> PhotoCredit(
                    eclipse, getString(R.string.credits_sun_as_star),
                    getString(R.string.credits_link_sun_as_star)
                )
                Eclipse.ANNULAR_START -> PhotoCredit(
                    eclipse, getString(R.string.credits_annular_start),
                    getString(R.string.credits_link_annular_start)
                )
                Eclipse.ANNULAR_PHASE_START -> PhotoCredit(
                    eclipse, getString(R.string.credits_annular_phase_start),
                    getString(R.string.credits_link_annular_phase_start)
                )
                Eclipse.ANNULARITY -> PhotoCredit(
                    eclipse, getString(R.string.credits_annularity),
                    getString(R.string.credits_link_annularity)
                )
                Eclipse.ANNULAR_PHASE_END -> PhotoCredit(
                    eclipse, getString(R.string.credits_annular_phase_end),
                    getString(R.string.credits_link_annular_phase_end)
                )
                Eclipse.ANNULAR_END -> PhotoCredit(
                    eclipse, getString(R.string.credits_annular_end),
                    getString(R.string.credits_link_annular_end)
                )
            }
            credits.add(photoCredit)
        }
        return credits
    }

    private fun setToolbar(toolbar: Toolbar, title: String) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = title
    }

    companion object {
        const val EXTRA_LEGAL = "legal"
        const val EXTRA_LICENSE = "license"
        const val EXTRA_LIBS = "libraries"
        const val EXTRA_PHOTO_CREDS = "photo_credits"
        const val EXTRA_PRIVACY_POLICY = "privacy_policy"
        const val EXTRA_TOS = "tos"
    }
}