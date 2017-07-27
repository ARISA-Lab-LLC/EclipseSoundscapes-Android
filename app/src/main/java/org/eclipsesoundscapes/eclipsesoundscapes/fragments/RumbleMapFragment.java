package org.eclipsesoundscapes.eclipsesoundscapes.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.eclipsesoundscapes.eclipsesoundscapes.R;


public class RumbleMapFragment extends Fragment {

    private Context mContext;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ViewFlipper viewFlipper;
    private int currentView = 1;

    public RumbleMapFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_rumble_map, container, false);

        // views
        toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_white);

        viewFlipper = (ViewFlipper) root.findViewById(R.id.rumble_map_flipper);
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
                android.R.anim.fade_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,
                android.R.anim.fade_out));
        return root;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.rumble_map_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_next:
                viewFlipper.showNext();
                if (currentView < 4) {
                    currentView++;
                    toolbarTitle.setText("Contact Point ".concat(String.valueOf(currentView)));
                } else {
                    currentView = 1;
                    toolbarTitle.setText("Contact Point ".concat(String.valueOf(currentView)));
                }
                return true;
            case android.R.id.home:
                viewFlipper.showPrevious();
                if (currentView > 1) {
                    currentView--;
                    toolbarTitle.setText("Contact Point ".concat(String.valueOf(currentView)));
                } else {
                    currentView = 4;
                    toolbarTitle.setText("Contact Point ".concat(String.valueOf(currentView)));
                }
            default:
                return super.onOptionsItemSelected(item);

        }
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
