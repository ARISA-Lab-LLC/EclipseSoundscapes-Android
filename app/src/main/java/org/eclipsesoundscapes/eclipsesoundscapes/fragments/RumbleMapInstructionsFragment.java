package org.eclipsesoundscapes.eclipsesoundscapes.fragments;

/**
 * Created by horus on 8/4/17.
 */

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import android.widget.TextView;

import org.eclipsesoundscapes.eclipsesoundscapes.R;

/**
 * Created by horus on 8/2/17.
 */

public class RumbleMapInstructionsFragment extends DialogFragment {


    Context mContext;
    /**
     * Create a new instance of RumbleMapInteractionFragment, providing "eclipse"
     * as an argument.
     */
    static RumbleMapInstructionsFragment newInstance() {
        return new RumbleMapInstructionsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_walkthrough_two, container, false);

        v.findViewById(R.id.current_page).setVisibility(View.GONE);
        TextView title = (TextView) v.findViewById(R.id.bottom_view_title);
        title.setTextColor(ContextCompat.getColor(mContext, android.R.color.black));
        TextView body = (TextView) v.findViewById(R.id.bottom_view_more);
        body.setTextColor(ContextCompat.getColor(mContext, android.R.color.black));
        body.setText(getString(R.string.rumble_map_instructions));

        ImageButton closeButton = (ImageButton) v.findViewById(R.id.exit_button);
        closeButton.setVisibility(View.VISIBLE);
        closeButton.setColorFilter(ContextCompat.getColor(mContext, android.R.color.black));
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
    }

    @Override
    public void onStart() {
        super.onStart();

        //rumbleMapLayout.announceForAccessibility(getString(R.string.rumble_map_inactive));

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

}
