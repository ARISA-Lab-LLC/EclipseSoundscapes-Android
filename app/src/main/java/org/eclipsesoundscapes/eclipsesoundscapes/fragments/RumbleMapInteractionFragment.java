package org.eclipsesoundscapes.eclipsesoundscapes.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.eclipsesoundscapes.eclipsesoundscapes.R;

/**
 * Created by horus on 8/2/17.
 */

public class RumbleMapInteractionFragment extends DialogFragment {

    int eclipseRes; // eclipse resource img
    private RelativeLayout rumbleMapLayout;
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

        ImageView eclipse = (ImageView) v.findViewById(R.id.eclipse_img);
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

        Button button = (Button)v.findViewById(R.id.button_close_rumble_map);
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

        //rumbleMapLayout.announceForAccessibility(getString(R.string.rumble_map_inactive));

    }
}
