package org.eclipsesoundscapes.ui.rumblemap

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.os.*
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.accessibility.AccessibilityManager
import com.jsyn.JSyn
import com.jsyn.Synthesizer
import com.jsyn.unitgen.LineOut
import com.jsyn.unitgen.LinearRamp
import com.jsyn.unitgen.SineOscillator
import org.eclipsesoundscapes.EclipseSoundscapesApp
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.data.DataManager
import org.eclipsesoundscapes.databinding.ActivityRumbleMapInteractionBinding
import org.eclipsesoundscapes.ui.base.BaseActivity
import org.eclipsesoundscapes.util.AndroidAudioForJSyn
import java.io.IOException
import kotlin.math.abs

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
 * Creates an interactive imageview for each eclipse image provided and generates sound / vibration
 * on touch events based on the selected pixel's grayscale value.
 * Allows users to save a marker at any arbitrary location in the view, one per eclipse image.
 */
class RumbleMapInteractionActivity : BaseActivity(), OnTouchListener {

    private lateinit var binding: ActivityRumbleMapInteractionBinding

    private var dataManager: DataManager? = null
        get() {
            if (field == null) {
                field = (application as? EclipseSoundscapesApp)?.dataManager
            }

            return field
        }

    private val isAccessibilityEnabled
        get() = (getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager).isEnabled

    // Jsyn
    private lateinit var sineOsc1: SineOscillator
    private lateinit var sineOsc2: SineOscillator
    private var synthesizer: Synthesizer? = null
    private var lineOut: LineOut? = null

    private var modControl = 1.0
    private var modAmpControl = 0.0
    private val modScale = 6.0

    // media
    private var mediaPlayer: MediaPlayer? = null
    private var currentAudio: Int? = null

    // rumble map
    private var doubleTap = false
    private var isRunning = false
    private var eclipseRes = 0
    private val outRect = Rect()
    private val location = IntArray(2)

    // marker
    private lateinit var handler: Handler
    private var savedX = 0
    private var savedY = 0
    private var markerX = 0
    private var markerY = 0
    private var mIsLongPress = false
    private val longPress = Runnable {
        if (mIsLongPress) {
            mIsLongPress = false
            saveMarker(markerX, markerY)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent == null || !intent.hasExtra(EXTRA_IMG)) {
            finish()
            return
        }

        eclipseRes = intent.getIntExtra(EXTRA_IMG, 0)

        binding = ActivityRumbleMapInteractionBinding.inflate(layoutInflater).apply {

            eclipseImg.setImageResource(eclipseRes)

            buttonInstructions.setOnClickListener {
                if (isRunning && isAccessibilityEnabled) {
                    isRunning = false
                    rumbleMapLayout.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
                    rumbleMapLayout.contentDescription = getString(R.string.rumble_map_inactive)
                }

                startActivity(Intent(this@RumbleMapInteractionActivity, RumbleMapInstructionsActivity::class.java))
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left)
            }

            rumbleMapLayout.setOnClickListener {
                enableInteraction(!isRunning)
            }

            buttonCloseRumbleMap.setOnClickListener { onBackPressed() }
        }

        setContentView(binding.root)

        init()
        setupGestureDetector()
        setupSynthesizer()
    }

    private fun init() {
        handler = Handler(Looper.getMainLooper())

        dataManager?.getRumblingCheckpoint(eclipseRes.toString())?.let {
            if (it.isNotEmpty()) {
                val points = it.split(",".toRegex()).toTypedArray()
                if (points.size > 1) {
                    savedX = Integer.valueOf(points[0])
                    savedY = Integer.valueOf(points[1])
                }
            }
        }
    }

    private fun setupSynthesizer() {
        synthesizer = JSyn.createSynthesizer(AndroidAudioForJSyn())

        // Add an output mixer
        synthesizer?.add(SineOscillator().also { sineOsc1 = it })
        synthesizer?.add(SineOscillator().also { sineOsc2 = it })
        synthesizer?.add(LineOut().also { lineOut = it })

        // Add a lag to smooth out amplitude changes and avoid pops
        val lag = LinearRamp()
        synthesizer?.add(lag)

        sineOsc1.output.connect(sineOsc2.frequency)
        sineOsc2.output.connect(0, lineOut?.input, 0)
        sineOsc2.output.connect(0, lineOut?.input, 1)

        sineOsc1.amplitude.setup(110.0, 220.0, 1200.0)
        sineOsc1.frequency.set(55.0)
        sineOsc2.amplitude.setup(0.1, 1.0, 1.0)

        lag.output.connect(sineOsc2.amplitude)
        lag.input.setup(0.01, 0.5, 1.0)
        lag.time.set(0.5)

        synthesizer?.start()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupGestureDetector() {
        val gestureDetector = GestureDetector(this, object : SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean {
                handleTouchEventDown(e)
                return false
            }
        })

        binding.rumbleMapLayout.setOnTouchListener { _, motionEvent ->
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_MOVE -> {
                    handleTouchEvent(motionEvent)
                    false
                }
                MotionEvent.ACTION_UP -> {
                    handleTouchEventUp()
                    false
                }
                else -> gestureDetector.onTouchEvent(motionEvent)
            }
        }

        // handles user interaction even when accessibility service is intercepting
        // onTouch events
        binding.rumbleMapLayout.setOnHoverListener(View.OnHoverListener { _, event ->
            val action = event.action
            if (isRunning) {
                when (action) {
                    MotionEvent.ACTION_HOVER_ENTER -> {
                        event.action = MotionEvent.ACTION_DOWN
                    }
                    MotionEvent.ACTION_HOVER_MOVE -> {
                        event.action = MotionEvent.ACTION_MOVE
                    }
                    MotionEvent.ACTION_HOVER_EXIT -> {
                        event.action = MotionEvent.ACTION_UP
                    }
                }
                return@OnHoverListener binding.rumbleMapLayout.dispatchTouchEvent(event)
            }
            false
        })

        binding.eclipseImg.setOnTouchListener(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        if (!isRunning) {
            return false
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_UP -> {
                handleTouchEventUp()
            }
            MotionEvent.ACTION_DOWN -> {
                handleTouchEventDown(event)
            }
            MotionEvent.ACTION_MOVE -> {
                handleTouchEvent(event)
            }
        }
        return true
    }

    private fun handleTouchEventUp() {
        // cancel long press
        mIsLongPress = false
        handler.removeCallbacks(longPress)

        stopSynthesizer()
        stopMediaPlayer()
        performHapticFeedback()
    }

    private fun handleTouchEventDown(event: MotionEvent) {
        if (!isRunning) {
            return
        }

        val x = event.rawX.toInt()
        val y = event.rawY.toInt()

        // start timing for long press
        mIsLongPress = true
        markerX = x
        markerY = y
        handler.postDelayed(longPress, LONG_PRESS_TIMEOUT.toLong())

        handleTouchEvent(event)
    }

    private fun handleTouchEvent(event: MotionEvent) {
        performHapticFeedback()

        val x = event.rawX.toInt()
        val y = event.rawY.toInt()

        checkSavedPoint(x, y)

        // check if change in touch surpasses long press boundary
        if (abs(markerX - x) > CHECKPOINT_OFFSET || abs(markerY - y) > CHECKPOINT_OFFSET) {
            mIsLongPress = false
            handler.removeCallbacks(longPress)
        }

        if (isViewInBounds(binding.eclipseImg, x, y)) {
            grayScale(floatArrayOf(event.x, event.y))
        } else if (isViewInBounds(binding.rumbleMapLayout, x, y)) {
            // outside image view, gray scale value is always 0
            startMediaPlayer(true)
        }
    }

    private fun performHapticFeedback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            binding.eclipseImg.performHapticFeedback(HapticFeedbackConstants.TEXT_HANDLE_MOVE)
        } else {
            binding.eclipseImg.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        }
    }

    private fun grayScale(points: FloatArray) {
        val imageView = binding.eclipseImg
        val invertMatrix = Matrix()
        imageView.imageMatrix.invert(invertMatrix)
        invertMatrix.mapPoints(points)

        var x2 = points[0].toInt()
        var y2 = points[1].toInt()

        val drawable = imageView.drawable
        (drawable as? BitmapDrawable)?.bitmap?.let { bitmap ->
            // limit x, y range within bitmap
            if (x2 < 0) x2 = 0 else if (x2 > bitmap.width - 1) x2 = bitmap.width - 1
            if (y2 < 0) y2 = 0 else if (y2 > bitmap.height - 1) y2 = bitmap.height - 1

            // selected pixel
            val pixel = bitmap.getPixel(x2, y2)
            val red = Color.red(pixel)
            val blue = Color.blue(pixel)
            val green = Color.green(pixel)

            val grayScaleZero = (red / 255.0 + green / 255.0 + blue / 255.0) / 3
            modAmpControl = grayScaleZero * modScale
            modControl = grayScaleZero
            if (grayScaleZero <= GRAY_SCALE_THRESHOLD) {
                startMediaPlayer(true)
            } else {
                startSynthesizer()
            }
        }
    }

    /**
     * Generates a sound depending on the gray scale value of last selected pixel
     */
    private fun startSynthesizer() {
        if (modAmpControl < 1) {
            modAmpControl += 1.0
        }

        val frequency = modAmpControl * 55.0
        if (frequency > 220.0) {
            sineOsc1.frequency.set(220.0)
        } else {
            sineOsc1.frequency.set(frequency)
        }

        val amp = modAmpControl * 220.0
        if (amp > 440.0) {
            sineOsc1.amplitude.set(440.0)
        } else {
            sineOsc1.amplitude.set(amp)
        }

        lineOut?.let {
            if (it.isStartRequired) {
                stopMediaPlayer()
                it.start()
            }
        }
    }

    private fun stopSynthesizer() {
        if (synthesizer?.isRunning == true) {
            lineOut?.stop()
        }
    }

    /**
     * Plays one of two audio files:
     * - xytick if the currently selected pixel grayscale value doesn't meet the threshold
     * - mapmarker if the currently selected pixel is marked as a save point
     */
    private fun startMediaPlayer(isTick: Boolean) {
        try {
            stopSynthesizer()

            val audioFile = if (isTick) {
                R.raw.xytick
            } else {
                R.raw.mapmarker
            }

            if (mediaPlayer?.isPlaying == true && currentAudio == audioFile) {
                return
            }

            if (mediaPlayer?.isPlaying == true && currentAudio == R.raw.mapmarker && audioFile != R.raw.mapmarker) {
                // prioritize map marker sound over tick
                return
            }

            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer?.release()
            mediaPlayer = null

            mediaPlayer = MediaPlayer.create(this, audioFile)
            currentAudio = audioFile

            mediaPlayer?.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopMediaPlayer() {
        mediaPlayer?.stop()
    }

    /**
     * Check if x, y coordinate is within the boundaries of this view
     * @param view imageview or it's parent layout
     * @param x x coordinate of onTouch event
     * @param y y coordinate of onTouch event
     * @return true if within, else false
     */
    private fun isViewInBounds(view: View?, x: Int, y: Int): Boolean {
        view?.getDrawingRect(outRect)
        view?.getLocationOnScreen(location)
        outRect.offset(location[0], location[1])
        return outRect.contains(x, y)
    }

    /**
     * Saves the x, y screen coordinate in shared preferences
     * Sets a marker and replaces previous one if present
     * @param x x coordinate of onTouch event
     * @param y y coordinate of onTouch event
     */
    private fun saveMarker(x: Int, y: Int) {
        val position = "$x,$y"
        dataManager?.setRumblingCheckpoint(eclipseRes.toString(), position)
        savedX = x
        savedY = y
        startMediaPlayer(false)
    }

    /**
     * Check if current x, y coordinate is within the boundaries of a currently saved marker
     * for this imageview
     * @param x x coordinate of onTouch event
     * @param y y coordinate of onTouch event
     */
    private fun checkSavedPoint(x: Int, y: Int) {
        if (abs(savedX - x) <= CHECKPOINT_OFFSET) {
            if (abs(savedY - y) <= CHECKPOINT_OFFSET) {
                startMediaPlayer(false)
            }
        }
    }

    /**
     * Turn rumble map interaction on/off
     * @param enable - set status
     */
    private fun enableInteraction(enable: Boolean) {
        if (enable) {
            isRunning = true
            binding.rumbleMapLayout.announceForAccessibility(getString(R.string.rumble_map_running))
            binding.rumbleMapLayout.contentDescription = getString(R.string.rumble_map_running)
        } else {
            isRunning = false
            binding.rumbleMapLayout.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
            binding.rumbleMapLayout.contentDescription = getString(R.string.rumble_map_inactive)
        }
    }

    override fun onStart() {
        super.onStart()
        // turn on rumble map by default if accessibility talk-back is not enabled
        if (!isAccessibilityEnabled) {
            isRunning = true
        }
    }

    public override fun onResume() {
        super.onResume()
        synthesizer?.start()

        if (!isAccessibilityEnabled) {
            isRunning = true
        }
    }

    public override fun onPause() {
        super.onPause()
        synthesizer?.let {
            if (it.isRunning) {
                lineOut?.stop()
                it.stop()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lineOut = null
        synthesizer?.stop()

        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {
        const val EXTRA_IMG = "img"

        private const val LONG_PRESS_TIMEOUT = 2500
        private const val CHECKPOINT_OFFSET = 25
        private const val GRAY_SCALE_THRESHOLD = 0.17
    }
}