package org.eclipsesoundscapes.ui.rumblemap

import android.os.Bundle
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.databinding.ActivityRumbleMapInstructionsBinding
import org.eclipsesoundscapes.ui.base.BaseActivity

class RumbleMapInstructionsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRumbleMapInstructionsBinding.inflate(layoutInflater).apply {
            exitButton.setOnClickListener { onBackPressed() }
        }
        val view = binding.root
        setContentView(view)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
    }
}