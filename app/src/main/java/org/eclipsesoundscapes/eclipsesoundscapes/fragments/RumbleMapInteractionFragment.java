package org.eclipsesoundscapes.eclipsesoundscapes.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.SineOscillatorPhaseModulated;

import org.eclipsesoundscapes.eclipsesoundscapes.R;
import org.eclipsesoundscapes.eclipsesoundscapes.activity.MainActivity;
import org.eclipsesoundscapes.eclipsesoundscapes.util.AndroidAudioForJSyn;

import static java.lang.Thread.sleep;

/**
 * Created by horus on 8/2/17.
 */

public class RumbleMapInteractionFragment extends DialogFragment implements View.OnTouchListener {


    public EnvelopeAttackDecay ampEnv;
    public SineOscillatorPhaseModulated carrierOsc;
    public EnvelopeAttackDecay modEnv;
    public UnitInputPort index;
    public UnitInputPort modRange;
    public UnitInputPort frequency;

    // views
    private RelativeLayout rumbleMapLayout;
    private ImageView eclipse;
    private Bitmap rumbleBitmap;
    private ScaleAnimation mAnimation;

    private Context mContext;
    int eclipseRes; // eclipse resource img
    private boolean isRunning = false; // rumble map is running

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eclipseRes = getArguments().getInt("img");

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        /*
        switch ((mNum-1)%6) {
            case 1: style = DialogFragment.STYLE_NO_TITLE; break;
            case 2: style = DialogFragment.STYLE_NO_FRAME; break;
            case 3: style = DialogFragment.STYLE_NO_INPUT; break;
            case 4: style = DialogFragment.STYLE_NORMAL; break;
            case 5: style = DialogFragment.STYLE_NORMAL; break;
            case 6: style = DialogFragment.STYLE_NO_TITLE; break;
            case 7: style = DialogFragment.STYLE_NO_FRAME; break;
            case 8: style = DialogFragment.STYLE_NORMAL; break;
        }
        switch ((mNum-1)%6) {
            case 4: theme = android.R.style.Theme_Holo; break;
            case 5: theme = android.R.style.Theme_Holo_Light_Dialog; break;
            case 6: theme = android.R.style.Theme_Holo_Light; break;
            case 7: theme = android.R.style.Theme_Holo_Light_Panel; break;
            case 8: theme = android.R.style.Theme_Holo_Light; break;
        }
        */
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rumble_map_interaction, container, false);

        eclipse = (ImageView) v.findViewById(R.id.eclipse_img);
        eclipse.setImageResource(eclipseRes);
        rumbleMapLayout = (RelativeLayout) v.findViewById(R.id.rumble_map_layout);
        rumbleMapLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRunning) {
                    isRunning = true;
                    rumbleMapLayout.setContentDescription(getString(R.string.rumble_map_running));
                }
                else {
                    isRunning = false;
                    rumbleMapLayout.setContentDescription(getString(R.string.rumble_map_inactive));
                }
            }
        });

        eclipse.setOnTouchListener(this);


        final Button button = (Button)v.findViewById(R.id.button_close_rumble_map);
        final Button instructionsButton = (Button) v.findViewById(R.id.button_instructions);
        instructionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create the fragment and show it as a dialog.
                DialogFragment newFragment = new RumbleMapInstructionsFragment();
                newFragment.show(((MainActivity)mContext).getFragmentManager(), "dialog");
            }
        });

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (getDialog() != null)
                    getDialog().dismiss();
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.MyAnimation_Window;
    }

    @Override
    public void onStart() {
        super.onStart();
        eclipse.setOnTouchListener(this);


    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap( v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getWidth(), v.getHeight());
        v.draw(c);
        return b;
    }


    /*
    public static void playSound() {
        Synthesizer synth = JSyn.createSynthesizer( new AndroidAudioForJSyn() );
        try {
            synth.start();
            Add mixer = new Add();
            EnvelopeAttackDecay ampEv = new EnvelopeAttackDecay();
            ampEv.attack.setup(0.001, 0.6875, 20.0);
            SineOscillator sineOsc1 = new SineOscillator();
            SineOscillator sineOsc2 = new SineOscillator();
            LineOut lineOut  = new LineOut();
            synth.add(lineOut);
            synth.add(mixer);
            synth.add(sineOsc1);
            synth.add(sineOsc2);

            sineOsc1.output.connect( mixer.inputA );
            sineOsc2.output.connect( mixer.inputB );

            mixer.output.connect( 0, lineOut.input, 0 );
            mixer.output.connect( 0, lineOut.input, 1 );

            sineOsc1.amplitude.set(1.0);
            sineOsc1.frequency.set(55.0);

            sineOsc2.amplitude.set( 0.4 );
            sineOsc1.frequency.set(330.0);


            lineOut.start();
            mixer.start();
            sineOsc1.start();
            sineOsc2.start();



        } finally {
            synth.stop();
        }
    }
    */

    public void playSound(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        switch(action){
            case MotionEvent.ACTION_DOWN:

                float eventX = event.getX();
                float eventY = event.getY();
                float[] eventXY = new float[] {eventX, eventY};

                Matrix invertMatrix = new Matrix();
                ((ImageView)view).getImageMatrix().invert(invertMatrix);

                invertMatrix.mapPoints(eventXY);
                int x = Integer.valueOf((int)eventXY[0]);
                int y = Integer.valueOf((int)eventXY[1]);

                Log.d(
                        "touched position: ",
                                String.valueOf(eventX) + " / "
                                + String.valueOf(eventY));
                Log.d(
                        "touched position: ",
                                 String.valueOf(x) + " / "
                                + String.valueOf(y));



                Drawable imgDrawable = ((ImageView)view).getDrawable();
                //Bitmap bitmap = ((BitmapDrawable)imgDrawable).getBitmap();
                Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                imgDrawable.draw(canvas);

                Log.d(
                        "drawable size: "
                                , String.valueOf(bitmap.getWidth()) + " / "
                                + String.valueOf(bitmap.getHeight()));

                //Limit x, y range within bitmap
                if(x < 0){
                    x = 0;
                }else if(x > bitmap.getWidth()-1){
                    x = bitmap.getWidth()-1;
                }

                if(y < 0){
                    y = 0;
                }else if(y > bitmap.getHeight()-1){
                    y = bitmap.getHeight() - 1;
                }

                int touchedRGB = bitmap.getPixel(x, y);
                int red = Color.red(touchedRGB);
                int blue = Color.blue(touchedRGB);
                int green = Color.green(touchedRGB);
                double grayscale = (red + blue + green) / 3 ;
                Log.d("grayscale", String.valueOf(grayscale));
                Log.d("touched color: " , "#" + Integer.toHexString(touchedRGB));

                return true;
            default:
                return true;
        }
    }
}
