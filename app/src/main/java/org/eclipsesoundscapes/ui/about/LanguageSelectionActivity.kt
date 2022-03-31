package org.eclipsesoundscapes.ui.about

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.eclipsesoundscapes.EclipseSoundscapesApp
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.data.DataManager
import org.eclipsesoundscapes.databinding.ActivityLanguageSelectionBinding
import org.eclipsesoundscapes.ui.base.BaseActivity
import org.eclipsesoundscapes.util.LocaleUtils
import java.util.*

class LanguageSelectionActivity : BaseActivity() {

    private var dataManager: DataManager? = null
        get() {
            if (field == null) {
                field = (application as? EclipseSoundscapesApp)?.dataManager
            }

            return field
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLanguageSelectionBinding.inflate(layoutInflater).apply {
            setSupportActionBar(appBar.toolbar)
            supportActionBar?.title = getString(R.string.language)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            languageRecycler.setHasFixedSize(true)
            languageRecycler.layoutManager = LinearLayoutManager(this@LanguageSelectionActivity)
            languageRecycler.adapter = LanguageAdapter(supportedLanguages(),
                LanguageViewHolder.LanguageClickListener {
                    if (it.language != LocaleUtils.getCurrentLanguage(this@LanguageSelectionActivity).language) {
                        setLanguage(it)
                    }
                })
        }

        setContentView(binding.root)
    }

    private fun supportedLanguages(): ArrayList<Locale> {
        val languages = ArrayList<Locale>()
        val locales = resources.getStringArray(R.array.supported_languages_locale)
        locales.forEach { locale -> languages.add(Locale(locale)) }
        return languages
    }


    private fun setLanguage(locale: Locale) {
        dataManager?.language = locale.language
        LocaleUtils.updateLocale(this)
        reload()
    }

    private fun reload() {
        finish()
        startActivity(Intent(this, javaClass).apply {
            // clear current stack so when we navigate back previous activities are re-created
            // and resources are reloaded
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    internal inner class LanguageAdapter internal constructor(
        private val languages: ArrayList<Locale>,
        val clickListener: LanguageViewHolder.LanguageClickListener
    ) :
        RecyclerView.Adapter<LanguageViewHolder>() {

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): LanguageViewHolder {
            return LanguageViewHolder.from(viewGroup)
        }

        override fun onBindViewHolder(viewHolder: LanguageViewHolder, i: Int) {
            viewHolder.bind(languages[i], clickListener)
        }

        override fun getItemCount(): Int {
            return languages.size
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (!isTaskRoot) {
            super.onBackPressed()
            finish()
        } else {
            // locale has been updated and task cleared
            finish()
            startActivity(Intent(this, SettingsActivity::class.java).apply {
                putExtra(SettingsActivity.EXTRA_SETTINGS_MODE, SettingsActivity.MODE_SETTINGS)
            })
        }
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
    }
}