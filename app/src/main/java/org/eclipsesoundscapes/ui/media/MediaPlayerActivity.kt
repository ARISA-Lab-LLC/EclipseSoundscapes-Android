package org.eclipsesoundscapes.ui.media

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.databinding.ActivityMediaPlayerBinding
import org.eclipsesoundscapes.model.MediaItem
import org.eclipsesoundscapes.ui.base.BaseActivity
import org.eclipsesoundscapes.util.MediaHelper

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
 * Audio player class for verbal and text description of an eclipseImageView
 * Also launched during first and second contact of eclipseImageView from notifications for live audio
 * See [MediaFragment]
 */
class MediaPlayerActivity : BaseActivity(), OnSeekBarChangeListener {

    private lateinit var binding: ActivityMediaPlayerBinding
    private lateinit var mediaItem: MediaItem

    private lateinit var mediaHelper: MediaHelper
    private lateinit var handler: Handler
    private var mediaPlayer: MediaPlayer? = null

    var isLive = false
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (!intent.hasExtra(EXTRA_MEDIA)) {
            finish()
            return
        }

        mediaItem = intent.extras?.getParcelable<MediaItem>(EXTRA_MEDIA) as MediaItem
        isLive = intent.getBooleanExtra(EXTRA_LIVE, false)

        updateMediaDetails(mediaItem.titleResId, mediaItem.descriptionResId, mediaItem.imageResId)
        binding.apply {
            playButton.setOnClickListener {
                onPlayButtonClicked()
            }

            backButton.setOnClickListener {
                onBackPressed()
            }

            audioProgress.setOnSeekBarChangeListener(this@MediaPlayerActivity)
        }

        // setup live audio UI
        if (isLive) {
            setupLiveAudioUI()
        }

        mediaHelper = MediaHelper()
        handler = Handler(Looper.getMainLooper())

        mediaPlayer = MediaPlayer.create(this, mediaItem.audioResId)
        if (mediaPlayer == null) {
            // media player creation failed
            finish()
            return
        }

        mediaPlayer?.let {
            it.setOnCompletionListener {
                if (isLive) {
                    onLiveExperienceEnd()
                }

                updateMediaState(false)
            }
        }

        handler.postDelayed({
            // allow time for accessibility to read label before playing audio
            playAudio()
        }, 1000)
    }

    private fun onPlayButtonClicked() {
        if (mediaPlayer?.isPlaying == true) {
            // pause audio
            mediaPlayer?.pause()
            updateMediaState(false)
        } else {
            // play audio
            mediaPlayer?.start()
            updateMediaState(true)
        }
    }

    private fun updateMediaState(playing: Boolean) {
        if (playing) {
            binding.playButton.setImageResource(R.drawable.ic_pause)
            binding.playButton.contentDescription = getString(R.string.pause)
        } else {
            binding.playButton.setImageResource(R.drawable.ic_play)
            binding.playButton.contentDescription = getString(R.string.play)
        }
    }

    override fun onBackPressed() {
        if (!isLive) {
            super.onBackPressed()
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
            finish()
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    // disable seek bar, play/pause control and accessibility during live audio
    private fun setupLiveAudioUI() {
        binding.audioProgress.setOnTouchListener { _, _ -> true }
        binding.playButton.visibility = View.GONE
        binding.eclipseTitle.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        binding.eclipseDescription.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
    }

    /**
     * Update title, description and image based on audio timing
     * @param titleId String resource id for title
     * @param descriptionId String resource id for description
     * @param imageId Drawable resource id for image
     */
    private fun updateMediaDetails(titleId: Int, descriptionId: Int, imageId: Int) {
        binding.apply {
            eclipseTitle.setText(titleId)
            eclipseDescription.setText(descriptionId)
            eclipseImg.setImageResource(imageId)
        }
    }

    /**
     * Create media player and start audio from provided resource
     */
    private fun playAudio() {
        try {
            mediaPlayer?.start()
            updateMediaState(true)

            binding.apply {
                audioProgress.progress = 0
                audioProgress.max = 100
            }

            handler.postDelayed(progressRunnable, 100)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private val progressRunnable: Runnable by lazy {
        object : Runnable {
            override fun run() {
                mediaPlayer?.let {
                    val totalDuration = it.duration.toLong()
                    val currentDuration = it.currentPosition.toLong()

                    // update total time label
                    val totalTimeFormatted = mediaHelper.milliSecondsToTimer(totalDuration)
                    val totalTimeDescription = getTimeDescription(totalTimeFormatted)

                    binding.timeTotal.text = totalTimeFormatted
                    binding.timeTotal.contentDescription = totalTimeDescription

                    // update time lapsed label
                    val timeLapsedFormatted = mediaHelper.milliSecondsToTimer(currentDuration)
                    val currentTimeDescription = getTimeDescription(timeLapsedFormatted)

                    binding.timeLapsed.text = timeLapsedFormatted
                    binding.timeLapsed.contentDescription =
                        getString(
                            R.string.duration_current_progress,
                            currentTimeDescription,
                            totalTimeDescription
                        )

                    val progress = mediaHelper.getProgressPercentage(currentDuration, totalDuration)
                    binding.audioProgress.progress = progress
                    binding.audioProgress.contentDescription =
                        getString(
                            R.string.duration_current_progress,
                            currentTimeDescription,
                            totalTimeDescription
                        )

                    if (mediaItem.audioResId == R.raw.realtime_eclipse_shorts_saas) {
                        updateFullExperience()
                    }

                    handler.postDelayed(this, 100)
                }
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        handler.removeCallbacks(progressRunnable)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        handler.removeCallbacks(progressRunnable)
        mediaPlayer?.let {
            val totalDuration = it.duration
            val currentPosition = mediaHelper.progressToTimer(seekBar.progress, totalDuration)

            it.seekTo(currentPosition)
            handler.postDelayed(progressRunnable, 100)
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
        mediaPlayer?.let {
            if (it.isPlaying && binding.eclipseDescription.isAccessibilityFocused) {
                binding.playButton.callOnClick()
            }
        }
    }

    private fun getTimeDescription(time: String): String {
        val lapse = time.split(":".toRegex()).toTypedArray()
        return when {
            Integer.valueOf(lapse[0]) == 0 -> {
                getString(R.string.duration_desc_seconds, lapse[1])
            }
            Integer.valueOf(lapse[0]) == 1 -> {
                getString(R.string.duration_desc_minute, lapse[1])
            }
            else -> {
                getString(R.string.duration_desc_minutes, lapse[0], lapse[1])
            }
        }
    }

    /**
     * Updates UI based on current audio description of eclipseImageView event
     * Currently setup for the eclipseImageView August 21st
     */
    fun updateFullExperience() {
        mediaPlayer?.let {
            val currentTitle = binding.eclipseTitle.text.toString()
            if (it.currentPosition < 120000 && currentTitle != getString(R.string.bailys_beads)) {
                // baily's beads < 2:01
                updateMediaDetails(
                    R.string.bailys_beads,
                    R.string.bailys_beads_short,
                    R.drawable.eclipse_bailys_beads
                )
            } else if (it.currentPosition in 120000..199999 && currentTitle != getString(R.string.totality)) {
                // totality >= 2:01 < 5:21
                updateMediaDetails(
                    R.string.totality,
                    R.string.totality_short,
                    R.drawable.eclipse_totality
                )
            } else if (it.currentPosition in 200500..319999 && currentTitle != getString(R.string.diamond_ring)) {
                // diamond ring >= 3:21
                updateMediaDetails(
                    R.string.diamond_ring,
                    R.string.diamond_ring_short,
                    R.drawable.eclipse_diamond_ring
                )
            } else if (it.currentPosition >= 320500 && currentTitle != getString(R.string.sun_as_star)) {
                // sun as a star 5:21
                updateMediaDetails(
                    R.string.sun_as_star,
                    R.string.sun_as_star_description,
                    R.drawable.sun_as_a_star
                )
            }
        }
    }

    // resume user control over media player
    private fun onLiveExperienceEnd() {
        binding.apply {
            audioProgress.setOnTouchListener(null)
            playButton.visibility = View.VISIBLE
            backButton.visibility = View.VISIBLE
            eclipseTitle.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
            eclipseDescription.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
            isLive = false
        }
    }

    companion object {
        const val EXTRA_MEDIA = "media"
        const val EXTRA_LIVE = "is_live"
    }
}