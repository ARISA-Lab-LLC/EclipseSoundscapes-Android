package org.eclipsesoundscapes.eclipsesoundscapes.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eclipsesoundscapes.eclipsesoundscapes.R;
import org.eclipsesoundscapes.eclipsesoundscapes.adapters.AboutArrayAdapter;


public class AboutFragment extends Fragment {

    private Context mContext;
    private RecyclerView listView;
    private String[] listOptions;
    private Integer[] listIcons = {R.drawable.ic_nav_elcipse_features, R.drawable.ic_team, R.drawable.ic_partners,
                                     R.drawable.ic_nav_eclipse_center, R.drawable.ic_instructions, R.drawable.ic_settings, R.drawable.ic_legal};

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // about us list and info list
        listOptions = getResources().getStringArray(R.array.listOptions);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_about, container, false);

        // views
        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        listView = (RecyclerView) root.findViewById(R.id.about_list_view);
        listView.setLayoutManager(new LinearLayoutManager(mContext));

        // adapters
        AboutArrayAdapter aboutAdapter = new AboutArrayAdapter(mContext, listOptions, listIcons );
        listView.setAdapter(aboutAdapter);

        return root;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
