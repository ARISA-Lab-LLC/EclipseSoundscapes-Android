package org.eclipsesoundscapes.ui.rumblemap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.eclipsesoundscapes.EclipseSoundscapesApp;
import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.data.DataManager;
import org.eclipsesoundscapes.ui.features.EclipseFragment;
import org.eclipsesoundscapes.util.AndroidAudioForJSyn;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.LinearRamp;
import com.jsyn.unitgen.SineOscillator;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
 * Creates an interactive imageview for each eclipseImageView image provided and generates sound / vibration on
 * touch events based on the selected pixel's grayscale value.
 * Allows users to save a marker at any arbitrary location of the view, one per eclipseImageView image.
 * Also includes instructions for interating with the Rumble Map feature.
 *
 * @see EclipseFragment
 * Also, {@link RumbleMapInstructionsActivity}
 */

public class RumbleMapInteractionActivity extends AppCompatActivity implements View.OnTouchListener {

    public static final String EXTRA_IMG = "img";
    private DataManager dataManager;

    // media player and state management
    private MediaPlayer mediaPlayer;
    private boolean PLAYING_TICK = false;
    private boolean STATE_READY = false;
    private boolean STATE_PREPARING = false;

    // Jsyn
    private Synthesizer mSynth;
    private LineOut mLineOut;

    private SineOscillator sineOsc1;
    private SineOscillator sineOsc2;

    private double modControl = 1.0;
    private double modAmpControl = 0.0;
    private final double modScale = 6.0;

    // rumble map
    private AccessibilityManager am;
    private boolean isAccessibilityEnabled;
    private boolean doubleTap = false;
    private boolean isRunning = false; // rumble map on/off
    private int eclipseRes;
    private Rect outRect = new Rect();
    private int[] location = new int[2];

    // marker
    private final int LONG_PRESS_TIMEOUT = 2500; // ms
    private final int CHECKPOINT_OFFSET = 25;
    private int savedX = 0;
    private int savedY = 0;
    private int markerX  = 0;
    private int markerY = 0;
    private boolean mIsLongPress = false;
    private Handler mHandler = new Handler();
    private Runnable longPress = new Runnable() {
        @Override
        public void run() {
            if (mIsLongPress) {
                mIsLongPress = false;
                saveMarker(markerX, markerY);
            }
        }
    };

    // views
    @BindView(R.id.rumble_map_layout) RelativeLayout rumbleMapLayout;
    @BindView(R.id.eclipse_img) ImageView eclipseImageView;

    @OnClick(R.id.button_instructions) void showInstructions(){
        // turn off interaction
        if (isRunning && isAccessibilityEnabled){
            isRunning = false;
            rumbleMapLayout.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
            rumbleMapLayout.setContentDescription(getString(R.string.rumble_map_inactive));
        }

        // Create instructions fragment and show
        startActivity(new Intent(this, RumbleMapInstructionsActivity.class));
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rumble_map_interaction);
        ButterKnife.bind(this);

        // fetch eclipseImageView image from intent extra and any saved marker
        if (getIntent() == null || !getIntent().hasExtra(EXTRA_IMG))
            finish();

        init();
        setupListeners();
        setupSynthesizer();
        createMediaPlayer();
    }

    private void init(){
        dataManager = ((EclipseSoundscapesApp)getApplication()).getDataManager();

        am = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        if (am != null) isAccessibilityEnabled = am.isEnabled();

        eclipseRes = getIntent().getIntExtra(EXTRA_IMG, 0);
        eclipseImageView.setImageResource(eclipseRes);

        final String savedPoint = dataManager.getRumblingCheckpoint(String.valueOf(eclipseRes));
        if (!savedPoint.isEmpty()){
            String[] points = savedPoint.split(",");
            savedX = Integer.valueOf(points[0]);
            savedY = Integer.valueOf(points[1]);
        }
    }

    protected void createMediaPlayer(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
//                mediaPlayer.seekTo(0);
            }
        });
    }

    /**************************************************************************
     * JSyn / SoundPool
     *************************************************************************/

    private void setupSynthesizer(){
        AndroidAudioForJSyn mAudioManager = new AndroidAudioForJSyn();
        mSynth = JSyn.createSynthesizer(mAudioManager);

//        // Add an output mixer.
        mSynth.add(sineOsc1 = new SineOscillator());
        mSynth.add(sineOsc2 = new SineOscillator());

        mSynth.add(mLineOut = new LineOut());

        // Add a lag to smooth out amplitude changes and avoid pops.
        LinearRamp lag;
        mSynth.add(lag = new LinearRamp());

        sineOsc1.output.connect( sineOsc2.frequency);
        sineOsc2.output.connect( 0, mLineOut.input, 0 );
        sineOsc2.output.connect( 0, mLineOut.input, 1 );

        sineOsc1.amplitude.setup(110.0, 220.0, 1200.0);
        sineOsc1.frequency.set(55.0);

        sineOsc2.amplitude.setup(0.1, 1.0, 1.0);

        lag.output.connect(sineOsc2.amplitude);
        lag.input.setup(0.01, 0.5, 1.0);
        lag.time.set(0.5);



        mSynth.start();
    }

    private void grayScale(View v, float[] eventXY){

        final Matrix invertMatrix = new Matrix();
        ((ImageView) v).getImageMatrix().invert(invertMatrix);
        invertMatrix.mapPoints(eventXY);

        int x2 = (int) eventXY[0];
        int y2 = (int) eventXY[1];

        Drawable imgDrawable = ((ImageView) v).getDrawable();
        Bitmap bitmap = ((BitmapDrawable) imgDrawable).getBitmap();

        // limit x, y range within bitmap
        if (x2 < 0)
            x2 = 0;
        else if (x2 > bitmap.getWidth() - 1)
            x2 = bitmap.getWidth() - 1;

        if (y2 < 0)
            y2 = 0;
        else if (y2 > bitmap.getHeight() - 1)
            y2 = bitmap.getHeight() - 1;

        // selected pixel
        int pixel = bitmap.getPixel(x2, y2);
        int red = Color.red(pixel);
        int blue = Color.blue(pixel);
        int green = Color.green(pixel);
        double grayScaleZero = ((red / 255.0) + (green / 255.0) + (blue / 255.0)) / 3;

        modAmpControl = grayScaleZero * modScale;
        modControl = grayScaleZero;

        if (grayScaleZero <= 0.17) {
            stopSound();
            playMediaPlayer(true);
        } else {
            modulateSound();
        }
    }

    // generate sound depending on the gray scale value of pixel
    private void modulateSound(){
        if (modAmpControl < 1)
            modAmpControl += 1;

        final double freq = modAmpControl * 55.0;
        final double amp = modAmpControl * 220.0;

        if (freq > 220.0)
            sineOsc1.frequency.set(220.0);
        else
            sineOsc1.frequency.set(freq);


        if (amp > 440.0)
            sineOsc1.amplitude.set(440.0);
        else
            sineOsc1.amplitude.set(amp);

        if (mLineOut.isStartRequired())
            mLineOut.start();
    }

    private void stopSound(){
        if (mSynth != null && mSynth.isRunning())
            mLineOut.stop();
    }

    // plays tick when rumbling in low gray scale area
    // and plays different sound when setting a marker or crossing one
    private void playMediaPlayer(final boolean isTick){

        if (STATE_READY){
            if (isTick && PLAYING_TICK){
                mediaPlayer.start();
                return;
            } else if (!isTick && !PLAYING_TICK){
                mediaPlayer.start();
                return;
            } else {
                stopMediaPlayer();
            }
        }

        if (!STATE_READY && !STATE_PREPARING) {

            final String res = (isTick) ? "xytick" : "mapmarker";
            Uri audioUri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + res);

            try {
                mediaPlayer.setDataSource(this, audioUri);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer player) {
                        PLAYING_TICK = isTick;
                        STATE_PREPARING = false;
                        STATE_READY = true;
                        player.start();
                    }
                });

                STATE_PREPARING = true;
                mediaPlayer.prepareAsync();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopMediaPlayer(){
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();

            STATE_READY = false;
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();

                    STATE_READY = false;
                }
            });
        }
    }


    /**************************************************************************
     * OnTouch / Accessibility Interactions
     *************************************************************************/

    @SuppressLint("ClickableViewAccessibility")
    private void setupListeners(){
        final GestureDetector gd = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDown(MotionEvent e) {
                if (isRunning) {
                    stopSound();

                    int x = (int) e.getRawX();
                    int y = (int) e.getRawY();

                    if (isViewInBounds(eclipseImageView, x, y)) {
                        eclipseImageView.dispatchTouchEvent(e);
                    } else if (isViewInBounds(rumbleMapLayout, x, y)) {

                        // start timing long press
                        mIsLongPress = true;
                        markerX = x;
                        markerY = y;
                        mHandler.postDelayed(longPress, LONG_PRESS_TIMEOUT);

                        checkSavedPoint(x, y);
                        playMediaPlayer(true); // outside imageView, gray scale value always 0
                    }
                }
                return false;
            }
        });

        /* Handle user interaction even when accessibility service is intercepting
         * onTouch/onClick events
         */
        rumbleMapLayout.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View view, MotionEvent event) {
                final int action = event.getAction();
                if (isRunning) {
                    switch (action) {
                        case MotionEvent.ACTION_HOVER_ENTER: {
                            event.setAction(MotionEvent.ACTION_DOWN);
                        }
                        break;
                        case MotionEvent.ACTION_HOVER_MOVE: {
                            event.setAction(MotionEvent.ACTION_MOVE);
                        }
                        break;
                        case MotionEvent.ACTION_HOVER_EXIT: {
                            event.setAction(MotionEvent.ACTION_UP);
                        }
                        break;
                    }
                    return rumbleMapLayout.dispatchTouchEvent(event);
                }
                return false;
            }
        });

        rumbleMapLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getActionMasked();

                if (action == MotionEvent.ACTION_MOVE) {
                    stopSound();

                    int x = (int) motionEvent.getRawX();
                    int y = (int) motionEvent.getRawY();

                    // check if change in touch surpasses long press boundary
                    if ((Math.abs(markerX - x) > CHECKPOINT_OFFSET)|| (Math.abs(markerY - y) > CHECKPOINT_OFFSET)){
                        mIsLongPress = false;
                        mHandler.removeCallbacks(longPress);
                    }

                    // dispatch event to imageView if within its bounds
                    if (isViewInBounds(eclipseImageView, x, y)) {
                        eclipseImageView.dispatchTouchEvent(motionEvent);
                    } else if (isViewInBounds(rumbleMapLayout, x, y)) {
                        checkSavedPoint(x, y);
                        playMediaPlayer(true); // outside imageView
                    }

                    return false;

                } else if (action == MotionEvent.ACTION_UP) {
                    // cancel long press
                    mIsLongPress = false;
                    mHandler.removeCallbacks(longPress);
                    stopSound();
                    return false;
                } else
                    return gd.onTouchEvent(motionEvent);
            }
        });

        // add imageView listener
        eclipseImageView.setOnTouchListener(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int action = event.getActionMasked();

        if (!isRunning)
            return false;

        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (action) {
            case MotionEvent.ACTION_UP:
                // cancel long press
                mIsLongPress = false;
                mHandler.removeCallbacks(longPress);
                stopSound();
                break;

            case MotionEvent.ACTION_DOWN:
                // start timing for long press
                mIsLongPress = true;
                markerX = x;
                markerY = y;
                mHandler.postDelayed(longPress, LONG_PRESS_TIMEOUT);
                checkSavedPoint(x, y);

            case MotionEvent.ACTION_MOVE:
                checkSavedPoint(x, y);

                // check if change in touch surpasses long press boundary
                if ((Math.abs(markerX - x) > 25 )|| (Math.abs(markerY - y) > 25)){
                    mIsLongPress = false;
                    mHandler.removeCallbacks(longPress);
                }

                if (!isViewInBounds(eclipseImageView, x, y)) {
                    playMediaPlayer(true);
                    stopSound();
                    return false;
                } else if (isViewInBounds(eclipseImageView, x, y)) {
                    // within imageView bounds
                    grayScale(view, new float[]{event.getX(), event.getY()});
                    break;
                }
        }

        return true;
    }


    /**************************************************************************
     * Helpers
     *************************************************************************/

    /**
     * Check if x, y coordinate is within the boundaries of this view
     * @param view imageview or it's parent layout
     * @param x x coordinate of onTouch event
     * @param y y coordinate of onTouch event
     * @return true if within, else false
     */
    private boolean isViewInBounds(View view, int x, int y){
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }


    /**
     * Saves the x, y screen coordinate in shared preferences
     * Sets a marker and replaces previous one if present
     * @param x x coordinate of onTouch event
     * @param y y coordinate of onTouch event
     */
    public void saveMarker(int x, int y){
        String position = String.valueOf(x) +  "," + String.valueOf(y);
        dataManager.setRumblingCheckpoint(String.valueOf(eclipseRes), position);

        savedX = x;
        savedY = y;

        playMediaPlayer(false);
    }

    /**
     * Check if current x, y coordinate is within the boundaries of a currently saved marker
     * for this imageview
     * @param x x coordinate of onTouch event
     * @param y y coordinate of onTouch event
     */
    public void checkSavedPoint(int x, int y){
        if (Math.abs(savedX - x) <= CHECKPOINT_OFFSET){
            if (Math.abs(savedY - y) <= CHECKPOINT_OFFSET){
                playMediaPlayer(false);
            }
        }
    }

    /**
     * Turn rumble map interaction on/off
     * @param isEnabled - set status
     */
    public void setRumbleMapStatus(boolean isEnabled){
        if (isEnabled){
            isRunning = true;
            rumbleMapLayout.announceForAccessibility(getString(R.string.rumble_map_running));
            rumbleMapLayout.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        } else {
            isRunning = false;
            rumbleMapLayout.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
            rumbleMapLayout.announceForAccessibility(getString(R.string.rumble_map_inactive));
        }
    }


    @OnClick(R.id.rumble_map_layout)
    public void activateRumbleMap(){
        if (isAccessibilityEnabled) {
            if (!isRunning) {
                // turn interaction on
                setRumbleMapStatus(true);
            } else {
                if (doubleTap) {
                    // turn interaction off
                    setRumbleMapStatus(false);
                } else {
                    // set timing for double click to turn off interaction
                    new CountDownTimer(2000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            doubleTap = true;
                        }
                        public void onFinish() {
                            doubleTap = false;
                        }
                    }.start();
                }
            }
        }
    }


    /**************************************************************************
     * Lifecycle
     *************************************************************************/
    @Override
    protected void onStart() {
        super.onStart();
        // turn on rumble map by default if accessibility talk-back is not enabled
        if (!isAccessibilityEnabled)
            isRunning = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSynth != null){
            if (mSynth.isRunning()) {
                if (mLineOut != null)
                    mLineOut.stop();
                mSynth.stop();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSynth != null)
            mSynth.start();

        if (am != null) {
            isAccessibilityEnabled = am.isEnabled();
            if (!isAccessibilityEnabled)
                isRunning = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLineOut = null;

        if (mSynth != null)
            mSynth.stop();

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    @OnClick(R.id.button_close_rumble_map)
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
