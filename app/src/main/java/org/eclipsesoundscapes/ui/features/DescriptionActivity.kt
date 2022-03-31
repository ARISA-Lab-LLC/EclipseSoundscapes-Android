package org.eclipsesoundscapes.ui.features

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import org.eclipsesoundscapes.databinding.ActivityDescriptionBinding
import org.eclipsesoundscapes.model.Eclipse

class DescriptionActivity : AppCompatActivity() {

    private lateinit var eclipse: Eclipse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!intent.hasExtra(EXTRA_ECLIPSE)) {
            finish()
            return
        }

        eclipse = intent.extras?.getSerializable(EXTRA_ECLIPSE) as Eclipse

        val binding = ActivityDescriptionBinding.inflate(layoutInflater).apply {
            setSupportActionBar(appBar.toolbar)
            supportActionBar?.title = getString(eclipse.title())
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            eclipseDescription.setText(eclipse.description())
        }

        val view = binding.root
        setContentView(view)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_ECLIPSE = "eclipse"
    }

}