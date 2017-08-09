package org.eclipsesoundscapes.eclipsesoundscapes.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.eclipsesoundscapes.eclipsesoundscapes.R;
import org.eclipsesoundscapes.eclipsesoundscapes.activity.MainActivity;

/**
 * Created by horus on 7/27/17.
 */

public class DescriptionFragment extends Fragment {

    TextView eclipseDescription;
    Context context;

    public DescriptionFragment() {
        // Required empty public constructor
    }

    public static DescriptionFragment newInstance(String param1, String param2) {
        DescriptionFragment fragment = new DescriptionFragment();

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
        View root =  inflater.inflate(R.layout.fragment_description, container, false);

        eclipseDescription = (TextView) root.findViewById(R.id.eclipse_description);
        updateView(((MainActivity)context).getCurrentCP());

        return root;
    }

    public void updateView(int contactPoint){
        switch (contactPoint){
            case 1:
                eclipseDescription.setText(getString(R.string.bailys_beads_description));
                break;
            case 2:
                eclipseDescription.setText(getString(R.string.corona_description));
                break;
            case 3:
                eclipseDescription.setText(getString(R.string.diamond_ring_description));
                break;
            case 4:
                eclipseDescription.setText(getString(R.string.helmet_streamers_description));
                break;
            case 5:
                eclipseDescription.setText(getString(R.string.prominence_description));
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
