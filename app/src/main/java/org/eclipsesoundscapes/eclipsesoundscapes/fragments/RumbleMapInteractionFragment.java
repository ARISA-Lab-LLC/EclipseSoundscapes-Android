package org.eclipsesoundscapes.eclipsesoundscapes.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.unitgen.Add;
import com.jsyn.unitgen.EnvelopeAttackDecay;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.LinearRamp;
import com.jsyn.unitgen.SawtoothOscillatorBL;
import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.SineOscillatorPhaseModulated;
import com.jsyn.unitgen.UnitOscillator;

import org.eclipsesoundscapes.eclipsesoundscapes.R;
import org.eclipsesoundscapes.eclipsesoundscapes.activity.MainActivity;
import org.eclipsesoundscapes.eclipsesoundscapes.util.AndroidAudioForJSyn;


/**
 * Created by horus on 8/2/17.
 */

public class RumbleMapInteractionFragment extends DialogFragment implements View.OnTouchListener, View.OnClickListener {


    // jsyn
    Synthesizer synthesizer;
    SineOscillator sineOsc1;
    SineOscillator sineOsc2;
    public EnvelopeAttackDecay ampEnv;
    public SineOscillatorPhaseModulated carrierOsc;
    public EnvelopeAttackDecay modEnv;
    public UnitInputPort index;
    public UnitInputPort modRange;
    public UnitInputPort frequency;
    private UnitOscillator osc;
    private LinearRamp lag;
    private LineOut lineOut;

    // rumble map interaction
    private MediaPlayer tick_sound;
    private double modControl = 0.0;
    private double modAmpControl = 0.0;
    private final double modScale = 6.0;


    // views
    private RelativeLayout rumbleMapLayout;
    private ImageView eclipse;
    private Bitmap eclipseBitmap;
    private ScaleAnimation mAnimation;

    private Context mContext;
    int eclipseRes; // eclipse resource img
    private boolean isRunning = false; // rumble map is running
    Rect outRect = new Rect();
    int[] location = new int[2];

    /**
     * Create a new instance of RumbleMapInteractionFragment, providing "eclipse"
     * as an argument.
     */
    static RumbleMapInteractionFragment newInstance(int eclipseRes) {
        RumbleMapInteractionFragment f = new RumbleMapInteractionFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("img", eclipseRes);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        synthesizer = JSyn.createSynthesizer(new AndroidAudioForJSyn());
        lineOut = new LineOut();
        sineOsc1 = new SineOscillator();
        sineOsc2 = new SineOscillator();
        ampEnv = new EnvelopeAttackDecay();
        synthesizer.add(lineOut);
        synthesizer.add(sineOsc1);
        synthesizer.add(sineOsc2);
        //synthesizer.add(ampEnv);

        //sineOsc1.amplitude.set(440.0);
        //sineOsc1.frequency.set(60);
        //sineOsc2.amplitude.set(1);
        sineOsc1.amplitude.setup(55.0, 220.0, 1200.0);
        sineOsc1.frequency.set(55);
        sineOsc2.amplitude.setup(0.1, 1.0, 1.0);

        // env
        //ampEnv.amplitude.setup(0.001, 1.0, 8.0);
        //ampEnv.decay.setup(0.001, 0.1, 8.0);
        //sineOsc2.output.connect(ampEnv.amplitude);

        //sineOsc2.frequency.set(sineOsc1.output.getValue());
        sineOsc1.output.connect( sineOsc2.frequency);
        sineOsc2.output.connect( 0, lineOut.input, 0 );
        sineOsc2.output.connect( 0, lineOut.input, 1 );

        synthesizer.start();



        // Add a tone generator.
        //synthesizer.add( osc = new SawtoothOscillatorBL() );
        // Add a lag to smooth out amplitude changes and avoid pops.
        //synthesizer.add( lag = new LinearRamp() );
        // Add an output mixer.
        //synthesizer.add( lineOut = new LineOut() );
        // Connect the oscillator to the output.
        //osc.output.connect( 0, lineOut.input, 0 );

        // Set the minimum, current and maximum values for the port.
        //lag.output.connect( osc.amplitude );
        //lag.input.setup( 0.0, 0.5, 1.0 );
        //lag.time.set(  0.2 );
        //osc.frequency.setup( 50.0, 300.0, 10000.0 );

        eclipseRes = getArguments().getInt("img");
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rumble_map_interaction, container, false);
        Button button = (Button)v.findViewById(R.id.button_close_rumble_map);
        Button instructionsButton = (Button) v.findViewById(R.id.button_instructions);
        eclipse = (ImageView) v.findViewById(R.id.eclipse_img);
        eclipse.setImageResource(eclipseRes);
        eclipseBitmap = ((BitmapDrawable)eclipse.getDrawable()).getBitmap();
        rumbleMapLayout = (RelativeLayout) v.findViewById(R.id.rumble_map_layout);

        rumbleMapLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = MotionEventCompat.getActionMasked(motionEvent);
                switch(action) {
                    case MotionEvent.ACTION_DOWN:case MotionEvent.ACTION_MOVE:
                        stopSound();
                        int x = (int)motionEvent.getRawX();
                        int y = (int)motionEvent.getRawY();
                            if(isViewInBounds(eclipse, x, y))
                                eclipse.dispatchTouchEvent(motionEvent);
                            else if(isViewInBounds(rumbleMapLayout, x, y)){
                                Log.d("touchListener", "onTouch layout");
                                // execute on onTouch layout
                                playTick();
                            }
                        }
                        // Further touch is not handled
                        return false;
                }
            });

        eclipse.setOnTouchListener(this);
        instructionsButton.setOnClickListener(this);
        button.setOnClickListener(this);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.MyAnimation_Window;
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
        tick_sound = MediaPlayer.create(mContext, R.raw.xytick);
        tick_sound.start();
    }

    public void stopTick(){
        if (tick_sound != null) {
            tick_sound.stop();
            tick_sound.release();
            tick_sound = null;
        }
    }

    public void modulateSound(){

        //sineOsc2.frequency.set(modAmpControl * 220.0);


        if (modAmpControl < 1)
            modAmpControl += 1;

        sineOsc1.amplitude.set(modAmpControl * 220.0);
        sineOsc2.amplitude.set(modControl);

        //ampEnv.input.trigger();
        lineOut.start();
    }

    public void stopSound(){
        if (synthesizer != null && synthesizer.isRunning())
            synthesizer.stop();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch(action){

            case MotionEvent.ACTION_UP:
                stopSound();
                break;
            case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                int x = (int)event.getRawX();
                int y = (int)event.getRawY();

                if(!isViewInBounds(eclipse, x, y)) {
                    rumbleMapLayout.dispatchTouchEvent(event);
                }
                else if(isViewInBounds(eclipse, x, y)) {
                    Log.d("touchListener", "onTouch eclipse");

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
                    double grayscale = (red + green + blue) / 3; // 0 - 255
                    double grayScaleZero = ((red / 255) + (green / 255) + (blue / 255)) / 3;
                    modAmpControl = grayScaleZero * modScale;
                    modControl = grayScaleZero;
                    modulateSound();
                    break;
                }
        }
        // Further touch is not handled
                return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rumble_map_layout:
                if (!isRunning) {
                    isRunning = true;
                    rumbleMapLayout.setContentDescription(getString(R.string.rumble_map_running));
                }
                else {
                    isRunning = false;
                    rumbleMapLayout.setContentDescription(getString(R.string.rumble_map_inactive));
                }
                break;
            case R.id.button_close_rumble_map:
                if (getDialog() != null)
                    getDialog().dismiss();
                break;
            case R.id.button_instructions:
                // Create the fragment and show it as a dialog.
                DialogFragment newFragment = new RumbleMapInstructionsFragment();
                newFragment.show(((MainActivity)mContext).getFragmentManager(), "dialog");
                break;
        }
    }
}
