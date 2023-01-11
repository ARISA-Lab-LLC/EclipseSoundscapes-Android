package org.eclipsesoundscapes.ui.media

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.data.EclipseExplorer
import org.eclipsesoundscapes.databinding.ActivityMediaPlayerBinding
import org.eclipsesoundscapes.model.MediaItem
import org.eclipsesoundscapes.ui.base.BaseActivity
import org.eclipsesoundscapes.util.EclipseUtils
import org.eclipsesoundscapes.util.MediaUtils
import org.joda.time.DateTime
import java.util.concurrent.TimeUnit

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
@AndroidEntryPoint
class MediaPlayerActivity : BaseActivity(), OnSeekBarChangeListener {
    private lateinit var binding: ActivityMediaPlayerBinding
    private lateinit var mediaHelper: MediaUtils
    private lateinit var handler: Handler

    private val viewModel: MediaPlayerViewModel by viewModels()
    private var mediaPlayer: MediaPlayer? = null
    private var mediaItem: MediaItem? = null
    private var eclipseExplorer: EclipseExplorer? = null
    private var liveCountdownTimer: CountDownTimer? = null

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
            viewModel.eclipseConfiguration.observe(this, {
                it?.let { config ->
                    viewModel.dataManager.lastLocation?.let { location ->
                        eclipseExplorer = EclipseExplorer(
                            this@MediaPlayerActivity,
                            config,
                            location.latitude,
                            location.longitude
                        )
                    }
                }
            })
        }

        mediaHelper = MediaUtils()
        handler = Handler(Looper.getMainLooper())

        showMedia()
    }

    private fun showMedia() {
        mediaItem?.let {
            showCurrentMediaView(true)
            updateMediaDetails(it.titleResId, it.descriptionResId, it.imageResId)

            mediaPlayer = MediaPlayer.create(this, it.audioResId)

            if (mediaPlayer == null) {
                // media player creation failed
                finish()
                return
            }

            mediaPlayer?.let { mp ->
                mp.setOnCompletionListener {
                    if (isLive) {
                        showNextLiveEvent()
                    }

                    updateMediaState(false)
                }
            }

            if (isLive) {
                setupLiveAudioUI()
            }

            val accessibilityEnabled = (getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager).isEnabled
            handler.postDelayed({
                playAudio()
            }, if (accessibilityEnabled) {
                // allow time for accessibility to read labels before playing audio
                5000L
            } else {
                0L
            })
        }
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
        super.onBackPressed()
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
        finish()
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

    private fun showNextLiveEvent() {
        EclipseUtils.getNextEvent(this@MediaPlayerActivity, eclipseExplorer)?.let {
            showCurrentMediaView(false)
            val event = it.first

            binding.nextMediaView.eclipseImg.setImageResource(event.imageResource())
            binding.nextMediaView.eventLabel.setText(event.title())
            mediaItem = MediaItem(
                event.imageResource(),
                event.title(),
                event.shortAudioDescription(),
                event.shortAudio()
            )

            val millis = it.second.millis - DateTime.now().millis
            liveCountdownTimer = object : CountDownTimer(millis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                    val s: Long = seconds % 60
                    val m: Long = seconds / 60 % 60
                    val h: Long = seconds / (60 * 60) % 24

                    val time: String = if (h <= 0) {
                        if (m > 0) {
                            String.format("%02d:%02d", m, s)
                        } else {
                            String.format("00:%02d", s)
                        }
                    } else {
                        String.format("%02d:%02d:%02d", h, m, s)
                    }

                    binding.nextMediaView.countdownLabel.text =
                        getString(R.string.next_live_event_countdown, time)
                }

                override fun onFinish() {
                    showMedia()
                }
            }.start()
        }
    }

    private fun showCurrentMediaView(current: Boolean) {
        if (current) {
            binding.eclipseImg.visibility = View.VISIBLE
            binding.playButton.visibility = View.VISIBLE
            binding.nextMediaView.root.visibility = View.GONE
        } else {
            binding.eclipseImg.visibility = View.GONE
            binding.playButton.visibility = View.GONE
            binding.nextMediaView.root.visibility = View.VISIBLE
        }
    }

    companion object {
        const val EXTRA_MEDIA = "media"
        const val EXTRA_LIVE = "is_live"
    }
}