package org.eclipsesoundscapes.activity;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.eclipsesoundscapes.fragments.RumbleMapInstructionsFragment;
import org.eclipsesoundscapes.util.AndroidAudioForJSyn;
import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.EnvelopeAttackDecay;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.SineOscillator;

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
 * Creates an interactive imageview for each eclipse image provided and generates sound / vibration on
 * touch events based on the selected pixel's grayscale value.
 * Allows users to save a marker at any arbitrary location of the view, one per eclipse image.
 * Also includes instructions for interating with the Rumble Map feature.
 *
 * @see org.eclipsesoundscapes.fragments.RumbleMapFragment
 * Also, {@link RumbleMapInstructionsFragment}
 */

public class RumbleMapInteractionActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    // Jsyn
    Synthesizer synthesizer;
    SineOscillator sineOsc1;
    SineOscillator sineOsc2;
    public EnvelopeAttackDecay ampEnv;
    private LineOut lineOut;
    private double modControl = 0.0;
    private double modAmpControl = 0.0;
    private final double modScale = 6.0;

    // views
    private RelativeLayout rumbleMapLayout;
    private ImageView eclipse;

    // media player
    private MediaPlayer tick_sound;
    private MediaPlayer save_sound;

    // rumble map
    private AccessibilityManager am;
    private SharedPreferences preferences;
    private boolean isAccessibilityEnabled;
    private boolean doubleTap = false;
    private boolean isRunning = false; // rumble map on/off
    private int eclipseRes;
    private Rect outRect = new Rect();
    private int[] location = new int[2];

    // marker
    private CountDownTimer countDownTimer;
    private final int LONG_PRESS_TIMEOUT = 2500; // ms
    private final int LONG_PRESS_OFFSET = 25;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.eclipsesoundscapes.R.layout.activity_rumble_map_interaction);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        am = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        isAccessibilityEnabled = am.isEnabled();

        // initialize Jsyn
        synthesizer = JSyn.createSynthesizer(new AndroidAudioForJSyn());
        lineOut = new LineOut();
        sineOsc1 = new SineOscillator();
        sineOsc2 = new SineOscillator();
        ampEnv = new EnvelopeAttackDecay();
        synthesizer.add(lineOut);
        synthesizer.add(sineOsc1);
        synthesizer.add(sineOsc2);

        sineOsc1.amplitude.setup(55.0, 220.0, 1200.0);
        sineOsc1.frequency.set(55);
        sineOsc2.amplitude.setup(0.1, 1.0, 1.0);

        sineOsc1.output.connect( sineOsc2.frequency);
        sineOsc2.output.connect( 0, lineOut.input, 0 );
        sineOsc2.output.connect( 0, lineOut.input, 1 );
        synthesizer.start();

        // fetch eclipse image from intent extra and any saved marker
        eclipseRes = getIntent().getIntExtra("img", 0);
        String savedPoint = preferences.getString(String.valueOf(eclipseRes), "");
        if (!savedPoint.isEmpty()){
            String[] points = savedPoint.split(",");
            savedX = Integer.valueOf(points[0]);
            savedY = Integer.valueOf(points[1]);
        }

        // views
        Button button = (Button) findViewById(org.eclipsesoundscapes.R.id.button_close_rumble_map);
        Button instructionsButton = (Button) findViewById(org.eclipsesoundscapes.R.id.button_instructions);
        eclipse = (ImageView) findViewById(org.eclipsesoundscapes.R.id.eclipse_img);
        eclipse.setImageResource(eclipseRes);
        rumbleMapLayout = (RelativeLayout) findViewById(org.eclipsesoundscapes.R.id.rumble_map_layout);
        rumbleMapLayout.setOnClickListener(this);

        // listeners

        // gesture detector for layout encapsulating the eclipse imageview
        final GestureDetector gd = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDown(MotionEvent e) {
                if (isRunning) {
                    stopSound();
                    int x = (int) e.getRawX();
                    int y = (int) e.getRawY();
                    if (isViewInBounds(eclipse, x, y)) {
                        eclipse.dispatchTouchEvent(e);
                    } else if (isViewInBounds(rumbleMapLayout, x, y)) {
                        // start timing long press
                        mIsLongPress = true;
                        markerX = x;
                        markerY = y;
                        mHandler.postDelayed(longPress, LONG_PRESS_TIMEOUT);

                        checkSavedPoint(x, y);
                        playTick(); // outside eclipse imageview, gray scale value always 0
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
                int action = MotionEventCompat.getActionMasked(motionEvent);
                    if (action == MotionEvent.ACTION_MOVE) {
                        stopSound();
                        int x = (int) motionEvent.getRawX();
                        int y = (int) motionEvent.getRawY();

                        // check if change in touch surpasses long press boundary
                        if ((Math.abs(markerX - x) > LONG_PRESS_OFFSET )|| (Math.abs(markerY - y) > LONG_PRESS_OFFSET)){
                            mIsLongPress = false;
                            mHandler.removeCallbacks(longPress); // cancelled
                        }

                        // within imageview boundaries, dispatch event
                        if (isViewInBounds(eclipse, x, y)) {
                            eclipse.dispatchTouchEvent(motionEvent);
                        } else if (isViewInBounds(rumbleMapLayout, x, y)) {
                            // outside imageview
                            checkSavedPoint(x, y);
                            playTick();
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

        eclipse.setOnTouchListener(this);
        instructionsButton.setOnClickListener(this);
        button.setOnClickListener(this);
    }

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
        String save = String.valueOf(x) +  "," + String.valueOf(y);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(String.valueOf(eclipseRes), save);
        editor.apply();
        savedX = x;
        savedY = y;
        playSaveSound();
    }

    /**
     * Check if current x, y coordinate is within the boundaries of a currently saved marker
     * for this imageview
     * @param x x coordinate of onTouch event
     * @param y y coordinate of onTouch event
     */
    public void checkSavedPoint(int x, int y){
        if (Math.abs(savedX - x) <= 75){
            if (Math.abs(savedY - y) <= 75){
                playSaveSound();
            }
        }
    }

    /**************************************************************************
     * Sounds
     *************************************************************************/

    // play sound when gray scale is 0 or outside of eclipse imageview
    public void playTick() {
        if (tick_sound != null)
            if (tick_sound.isPlaying())
                return;
        stopTick();
        tick_sound = MediaPlayer.create(this, org.eclipsesoundscapes.R.raw.xytick);
        tick_sound.start();
    }

    public void stopTick(){
        if (tick_sound != null) {
            tick_sound.stop();
            tick_sound.release();
            tick_sound = null;
        }
    }

    // play sound when user saves marker or touches a placed marker
    public void playSaveSound(){
        if (save_sound != null)
            if (save_sound.isPlaying())
                return;
        stopSaveSound();
        save_sound = MediaPlayer.create(this, org.eclipsesoundscapes.R.raw.mapmarker);
        save_sound.start();
    }

    public void stopSaveSound(){
        if (save_sound != null) {
            save_sound.stop();
            save_sound.release();
            save_sound = null;
        }
    }

    // generate sound depending on the gray scale value of touched pixel
    public void modulateSound(){
        if (modAmpControl < 1)
            modAmpControl += 1;

        sineOsc1.amplitude.set(modAmpControl * 220.0);
        sineOsc2.amplitude.set(modControl);

        lineOut.start();
    }

    public void stopSound(){
        if (synthesizer != null && synthesizer.isRunning())
            lineOut.stop();
    }

    /**************************************************************************
     * Eclipse imageview onTouch/onClick listeners
     *************************************************************************/
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        if (isRunning) {
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

                    // outside imageview, dispatch
                    if (!isViewInBounds(eclipse, x, y)) {
                        playTick();
                        stopSound();
                        return false;
                    } else if (isViewInBounds(eclipse, x, y)) {
                        // within imageview, get gray scale value
                        float eventX = event.getX();
                        float eventY = event.getY();
                        float[] eventXY = new float[]{eventX, eventY};

                        Matrix invertMatrix = new Matrix();
                        ((ImageView) view).getImageMatrix().invert(invertMatrix);
                        invertMatrix.mapPoints(eventXY);
                        int x2 = Integer.valueOf((int) eventXY[0]);
                        int y2 = Integer.valueOf((int) eventXY[1]);

                        Drawable imgDrawable = ((ImageView) view).getDrawable();
                        Bitmap bitmap = ((BitmapDrawable) imgDrawable).getBitmap();

                        //Limit x, y range within bitmap
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

                        // gray scale value too low
                        if (modControl <= 0.17) {
                            stopSound();
                            playTick();
                        } else {
                            stopTick();
                            modulateSound();
                        }
                        break;
                    }
            }
        } else
            return false;

        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case org.eclipsesoundscapes.R.id.rumble_map_layout:
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
                            countDownTimer = new CountDownTimer(2000, 1000) {
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

                break;
            case org.eclipsesoundscapes.R.id.button_close_rumble_map:
                finish();
                break;
            case org.eclipsesoundscapes.R.id.button_instructions:
                // turn off interaction
                if (isRunning && isAccessibilityEnabled){
                    isRunning = false;
                    rumbleMapLayout.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
                    rumbleMapLayout.setContentDescription(getString(org.eclipsesoundscapes.R.string.rumble_map_inactive));
                }
                // Create instructions fragment and show
                DialogFragment newFragment = new RumbleMapInstructionsFragment();
                newFragment.show(getFragmentManager(), "dialog");
                break;
        }
    }

    /**
     * Turn rumble map interaction on/off
     * @param isEnabled - set status
     */
    public void setRumbleMapStatus(boolean isEnabled){
        if (isEnabled){
            isRunning = true;
            rumbleMapLayout.announceForAccessibility(getString(org.eclipsesoundscapes.R.string.rumble_map_running));
            rumbleMapLayout.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        } else {
            isRunning = false;
            rumbleMapLayout.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
            rumbleMapLayout.announceForAccessibility(getString(org.eclipsesoundscapes.R.string.rumble_map_inactive));
        }
    }

    /**************************************************************************
     * Lifecycle
     *************************************************************************/
    @Override
    protected void onStart() {
        super.onStart();
        // turn on rumble map by default if accessibility talkback is not enabled
        if (!isAccessibilityEnabled)
            isRunning = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (synthesizer != null){
            if (synthesizer.isRunning()) {
                if (lineOut != null)
                    lineOut.stop();
                synthesizer.stop();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (synthesizer != null)
            synthesizer.start();

        if (am != null) {
            isAccessibilityEnabled = am.isEnabled();
            if (!isAccessibilityEnabled)
                isRunning = true;
        }
    }
}
