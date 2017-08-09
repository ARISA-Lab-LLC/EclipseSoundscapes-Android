package org.eclipsesoundscapes.eclipsesoundscapes.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.eclipsesoundscapes.eclipsesoundscapes.R;
import org.eclipsesoundscapes.eclipsesoundscapes.activity.MainActivity;


public class RumbleMapFragment extends Fragment {

    private Context mContext;
    private ImageView imageView;
    private RelativeLayout relativeLayout;
    private int currentEclipseID; // current eclipse img displaying

    public RumbleMapFragment() {
        // Required empty public constructor
    }


    public static RumbleMapFragment newInstance(String param1, String param2) {
        RumbleMapFragment fragment = new RumbleMapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_rumble_map, container, false);
        relativeLayout = (RelativeLayout) root.findViewById(R.id.rl_rumble_map);
        imageView = (ImageView) root.findViewById(R.id.contact_point_img);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchRumbleMap();
            }
        });

        updateView(((MainActivity)mContext).getCurrentCP());
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
        }
    }

    public void updateView(int contactPoint){
        switch (contactPoint){
            case 1:
                imageView.setImageResource(R.drawable.eclipse_bailys_beads);
                currentEclipseID = R.drawable.eclipse_bailys_beads;
                break;
            case 2:
                imageView.setImageResource(R.drawable.eclipse_corona);
                currentEclipseID = R.drawable.eclipse_corona;
                break;
            case 3:
                imageView.setImageResource(R.drawable.eclipse_diamond_ring);
                currentEclipseID = R.drawable.eclipse_diamond_ring;
                break;
            case 4:
                imageView.setImageResource(R.drawable.helmet_streamers);
                currentEclipseID = R.drawable.helmet_streamers;
                break;
            case 5:
                imageView.setImageResource(R.drawable.eclipse_prominence);
                currentEclipseID = R.drawable.eclipse_prominence;
                break;
        }
    }

    void launchRumbleMap() {
        // Create the fragment and show it as a dialog.
        DialogFragment newFragment = RumbleMapInteractionFragment.newInstance(currentEclipseID);
        newFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
