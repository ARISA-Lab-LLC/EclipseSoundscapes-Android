package org.eclipsesoundscapes.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.adapters.AboutArrayAdapter;

/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see [http://www.gnu.org/licenses/].
  * */


/**
 * @author Joel Goncalves
 *
 * Provides a brief description of the Eclipse Soundscapes Project and creates a list view
 * other navigation options
 * @see org.eclipsesoundscapes.activity.OurTeamActivity
 * @see org.eclipsesoundscapes.activity.OurPartnersActivity
 * @see org.eclipsesoundscapes.activity.FutureEclipsesActivity
 * @see org.eclipsesoundscapes.activity.WalkthroughActivity
 * @see org.eclipsesoundscapes.activity.SettingsActivity
 */

public class AboutFragment extends Fragment {

    private Context mContext;
    private String[] listOptions;
    private Integer[] listIcons = {R.drawable.ic_nav_elcipse_features, R.drawable.ic_team, R.drawable.ic_partners,
                                     R.drawable.ic_nav_eclipse_center, R.drawable.ic_instructions, R.drawable.ic_settings, R.drawable.ic_legal};

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        mContext = getActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // populate option list
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

        RecyclerView listView = (RecyclerView) root.findViewById(R.id.about_list_view);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // adapters
        AboutArrayAdapter aboutAdapter = new AboutArrayAdapter(getActivity(), listOptions, listIcons );
        listView.setAdapter(aboutAdapter);

        return root;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
