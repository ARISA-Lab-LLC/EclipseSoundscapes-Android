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
import android.view.animation.ScaleAnimation;
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

public class RumbleMapInteractionActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    // jsyn
    Synthesizer synthesizer;
    SineOscillator sineOsc1;
    SineOscillator sineOsc2;
    public EnvelopeAttackDecay ampEnv;
    private LineOut lineOut;
    private double modControl = 0.0;
    private double modAmpControl = 0.0;
    private final double modScale = 6.0;

    // rumble map interaction
    private final int LONG_PRESS_TIMEOUT = 2500; // ms
    private MediaPlayer tick_sound;
    private MediaPlayer save_sound;
    private int savedX = 0;
    private int savedY = 0;
    boolean isAccessibilityEnabled;

    // views
    private RelativeLayout rumbleMapLayout;
    private ImageView eclipse;
    private Bitmap eclipseBitmap;
    private ScaleAnimation mAnimation;

    // objs
    private AccessibilityManager am;
    private SharedPreferences preferences;
    private CountDownTimer countDownTimer;
    private boolean doubleTap = false;
    private int eclipseRes; // eclipse resource img
    private boolean isRunning = false; // rumble map is running
    Rect outRect = new Rect();
    int[] location = new int[2];

    private int markerX  = 0;
    private int markerY = 0;
    private Handler mHandler = new Handler();
    private boolean mIsLongPress = false;
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


        eclipseRes = getIntent().getIntExtra("img", 0);
        String savedPoint = preferences.getString(String.valueOf(eclipseRes), "");

        if (!savedPoint.isEmpty()){
            String[] points = savedPoint.split(",");
            savedX = Integer.valueOf(points[0]);
            savedY = Integer.valueOf(points[1]);
        }


        Button button = (Button) findViewById(org.eclipsesoundscapes.R.id.button_close_rumble_map);
        Button instructionsButton = (Button) findViewById(org.eclipsesoundscapes.R.id.button_instructions);
        eclipse = (ImageView) findViewById(org.eclipsesoundscapes.R.id.eclipse_img);
        eclipse.setImageResource(eclipseRes);
        rumbleMapLayout = (RelativeLayout) findViewById(org.eclipsesoundscapes.R.id.rumble_map_layout);
        rumbleMapLayout.setOnClickListener(this);

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
                        // execute on onTouch layout
                        mIsLongPress = true;
                        markerX = x;
                        markerY = y;
                        mHandler.postDelayed(longPress, LONG_PRESS_TIMEOUT);
                        checkSavedPoint(x, y);
                        playTick();
                    }
                }
                return false;
            }
        });

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
                        if ((Math.abs(markerX - x) > 25 )|| (Math.abs(markerY - y) > 25)){
                            mIsLongPress = false;
                            mHandler.removeCallbacks(longPress);
                        }
                        if (isViewInBounds(eclipse, x, y)) {
                            eclipse.dispatchTouchEvent(motionEvent);

                        } else if (isViewInBounds(rumbleMapLayout, x, y)) {
                            // execute on onTouch layout
                            checkSavedPoint(x, y);
                            playTick();
                        }
                        return false;
                    } else if (action == MotionEvent.ACTION_UP) {
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

    @Override
    protected void onStart() {
        super.onStart();
        if (!isAccessibilityEnabled)
            isRunning = true;
    }

    public void saveMarker(int x, int y){
        String save = String.valueOf(x) +  "," + String.valueOf(y);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(String.valueOf(eclipseRes), save);
        editor.apply();
        savedX = x;
        savedY = y;
        playSaveSound();
    }

    public void checkSavedPoint(int x, int y){
        if (Math.abs(savedX - x) <= 75){
            if (Math.abs(savedY - y) <= 75){
                playSaveSound();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (synthesizer != null)
            if (synthesizer.isRunning()) {
                if (lineOut != null)
                    lineOut.stop();
                synthesizer.stop();
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

    private boolean isViewInBounds(View view, int x, int y){
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }

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

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        if (isRunning) {
            int x = (int) event.getRawX();
            int y = (int) event.getRawY();
            switch (action) {
                case MotionEvent.ACTION_UP:
                    mIsLongPress = false;
                    mHandler.removeCallbacks(longPress);
                    stopSound();
                    break;
                case MotionEvent.ACTION_DOWN:
                    mIsLongPress = true;
                    markerX = x;
                    markerY = y;
                    mHandler.postDelayed(longPress, LONG_PRESS_TIMEOUT);
                    checkSavedPoint(x, y);

                case MotionEvent.ACTION_MOVE:
                    // saved point
                    checkSavedPoint(x, y);
                    if ((Math.abs(markerX - x) > 25 )|| (Math.abs(markerY - y) > 25)){
                        mIsLongPress = false;
                        mHandler.removeCallbacks(longPress);
                    }

                    if (!isViewInBounds(eclipse, x, y)) {
                        playTick();
                        stopSound();
                        return false;
                    }
                    else if (isViewInBounds(eclipse, x, y)) {
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

                        int pixel = bitmap.getPixel(x2, y2);
                        int red = Color.red(pixel);
                        int blue = Color.blue(pixel);
                        int green = Color.green(pixel);
                        double grayScaleZero = ((red / 255.0) + (green / 255.0) + (blue / 255.0)) / 3;
                        modAmpControl = grayScaleZero * modScale;
                        modControl = grayScaleZero;

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

    public void setRumbleMapStatus(boolean isEnable){
        if (isEnable){
            isRunning = isEnable;
            rumbleMapLayout.announceForAccessibility(getString(org.eclipsesoundscapes.R.string.rumble_map_running));
            rumbleMapLayout.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        } else {
            isRunning = isEnable;
            rumbleMapLayout.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
            rumbleMapLayout.announceForAccessibility(getString(org.eclipsesoundscapes.R.string.rumble_map_inactive));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case org.eclipsesoundscapes.R.id.rumble_map_layout:
                if (isAccessibilityEnabled) {
                    if (!isRunning) {
                        setRumbleMapStatus(true);
                    } else {
                        if (doubleTap) {
                            setRumbleMapStatus(false);
                        } else {
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
                // Create the fragment and show it as a dialog.
                if (isRunning && isAccessibilityEnabled){
                    isRunning = false;
                    rumbleMapLayout.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
                    rumbleMapLayout.setContentDescription(getString(org.eclipsesoundscapes.R.string.rumble_map_inactive));
                }
                DialogFragment newFragment = new RumbleMapInstructionsFragment();
                newFragment.show(getFragmentManager(), "dialog");
                break;
        }
    }
}
