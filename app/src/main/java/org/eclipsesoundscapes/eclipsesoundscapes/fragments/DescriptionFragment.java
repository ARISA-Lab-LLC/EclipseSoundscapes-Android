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

import java.util.HashMap;

/**
 * Created by horus on 7/27/17.
 */

public class DescriptionFragment extends Fragment {

    TextView eclipseDescription;
    Context context;
    private HashMap<Integer, String> currentDescriptions;

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

        currentDescriptions = new HashMap<>();
        currentDescriptions.put(1, getString(R.string.bailys_beads_description));
        currentDescriptions.put(2, getString(R.string.corona_description));
        currentDescriptions.put(3, getString(R.string.diamond_ring_description));
        currentDescriptions.put(4, getString(R.string.helmet_streamers_description));
        currentDescriptions.put(5, getString(R.string.prominence_description));
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
        eclipseDescription.setText(currentDescriptions.get(contactPoint));
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
