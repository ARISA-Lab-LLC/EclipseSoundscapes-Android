package org.eclipsesoundscapes.ui.rumblemap

import android.os.Build
import android.os.Bundle
import androidx.activity.addCallback
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

        onBackPressedDispatcher.addCallback {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
            } else {
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
            }

            finish()
            return@addCallback
        }
    }
}