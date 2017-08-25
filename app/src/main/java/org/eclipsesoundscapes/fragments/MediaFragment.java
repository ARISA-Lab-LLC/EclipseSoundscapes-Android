package org.eclipsesoundscapes.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.activity.MainActivity;
import org.eclipsesoundscapes.adapters.MediaAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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
 * Provides a list view of media/audio content of different eclipse view and launches
 * {@link org.eclipsesoundscapes.activity.MediaPlayerActivity}
 */

public class MediaFragment extends Fragment {

    private ArrayList<String> eventList;
    private ArrayList<Integer> descriptionList;
    private ArrayList<Integer> eventImgList;
    private ArrayList<Integer> eventAudioList;

    private Context mContext;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private TextView moreContentView;
    private MediaAdapter mediaAdapter;
    private Calendar calendar;
    private Date eclipseDate;

    public MediaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
        eventList = new ArrayList<>();
        descriptionList = new ArrayList<>();
        eventImgList = new ArrayList<>();
        eventAudioList = new ArrayList<>();
        eclipseDate = getEclipseDate();

        // add default media content
        addMedia(-1, "Baily's Beads", org.eclipsesoundscapes.R.string.bailys_beads_description, org.eclipsesoundscapes.R.drawable.eclipse_bailys_beads, org.eclipsesoundscapes.R.raw.bailys_beads_full);
        addMedia(-1, "Prominence", org.eclipsesoundscapes.R.string.prominence_description, org.eclipsesoundscapes.R.drawable.eclipse_prominence, org.eclipsesoundscapes.R.raw.prominence_full);
        addMedia(-1, "Corona", org.eclipsesoundscapes.R.string.corona_description, org.eclipsesoundscapes.R.drawable.eclipse_corona, org.eclipsesoundscapes.R.raw.corona_full);
        addMedia(-1, "Helmet Streamers", org.eclipsesoundscapes.R.string.helmet_streamers_description, org.eclipsesoundscapes.R.drawable.helmet_streamers, org.eclipsesoundscapes.R.raw.helmet_streamers_full);
        addMedia(-1, "Diamond Ring", org.eclipsesoundscapes.R.string.diamond_ring_description, org.eclipsesoundscapes.R.drawable.eclipse_diamond_ring, org.eclipsesoundscapes.R.raw.diamond_ring_full);

        if (eclipseDate != null && calendar.getTime().after(eclipseDate)){
            addMedia(0, "First Contact", org.eclipsesoundscapes.R.string.first_contact_description, org.eclipsesoundscapes.R.drawable.eclipse_first_contact, org.eclipsesoundscapes.R.raw.first_contact_short);
            addMedia(-1, "Totality", org.eclipsesoundscapes.R.string.totality_description, org.eclipsesoundscapes.R.drawable.eclipse_totality, org.eclipsesoundscapes.R.raw.totality_short);
            addMedia(-1, "Sun as a Star", org.eclipsesoundscapes.R.string.sun_as_star_description, org.eclipsesoundscapes.R.drawable.sun_as_a_star, org.eclipsesoundscapes.R.raw.sun_as_a_star);
            addMedia(-1, "Eclipse Experience", org.eclipsesoundscapes.R.string.bailys_beads_description, org.eclipsesoundscapes.R.drawable.eclipse_bailys_beads, R.raw.realtime_eclipse_shorts_saas);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(org.eclipsesoundscapes.R.layout.fragment_media, container, false);
        Toolbar toolbar = (Toolbar) root.findViewById(org.eclipsesoundscapes.R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        // views
        moreContentView = (TextView) root.findViewById(org.eclipsesoundscapes.R.id.more_content);
        recyclerView = (RecyclerView) root.findViewById(org.eclipsesoundscapes.R.id.media_recycler);
        layoutManager = new LinearLayoutManager(getActivity());
        mediaAdapter = new MediaAdapter(getActivity(), eventList, descriptionList, eventImgList, eventAudioList);
        dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mediaAdapter);
        recyclerView.addItemDecoration(dividerItemDecoration);

        if (calendar.after(eclipseDate) || eventList.contains("Totality")){
            moreContentView.setVisibility(View.GONE);
        }

        return root;
    }


    @Override
    public void onStart() {
        super.onStart();
        mContext = getActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(mContext, org.eclipsesoundscapes.R.color.colorAccent));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mediaAdapter.notifyDataSetChanged();
        Date firstContact = ((MainActivity)mContext).getFirstContact();
        Date secondContact = ((MainActivity)mContext).getSecondContact();

        // add first contact media content during/after its occurrence
        if (!eventList.contains("First Contact") && firstContact != null){
            if (calendar.getTime().after(firstContact)){
                addMedia(0, "First Contact", org.eclipsesoundscapes.R.string.first_contact_description, org.eclipsesoundscapes.R.drawable.eclipse_first_contact, org.eclipsesoundscapes.R.raw.first_contact_short);
                mediaAdapter.notifyItemInserted(0);
            }
        }

        // add totality media content during/after its occurrence
        if (!eventList.contains("Totality") && secondContact != null){
            if (calendar.getTime().after(secondContact)){
                moreContentView.setVisibility(View.GONE);
                addMedia(6, "Totality", org.eclipsesoundscapes.R.string.totality_description, org.eclipsesoundscapes.R.drawable.eclipse_totality, org.eclipsesoundscapes.R.raw.totality_short);
                addMedia(7, "Sun as a Star", org.eclipsesoundscapes.R.string.sun_as_star_description, org.eclipsesoundscapes.R.drawable.sun_as_a_star, org.eclipsesoundscapes.R.raw.sun_as_a_star);
                addMedia(8, "Eclipse Experience", org.eclipsesoundscapes.R.string.bailys_beads_description, org.eclipsesoundscapes.R.drawable.eclipse_bailys_beads, R.raw.realtime_eclipse_shorts_saas);
                mediaAdapter.notifyItemInserted(6);
                mediaAdapter.notifyItemInserted(7);
                mediaAdapter.notifyItemInserted(8);
            }
        }
    }

    /**
     * Add new media content for list view
     * @param pos position to insert content
     * @param title title for media content
     * @param description description resource id
     * @param img image resource id
     * @param audio audio resource
     */
    public void addMedia(int pos, String title, int description, int img, int audio){
        if (pos == -1){
            eventList.add(title);
            descriptionList.add(description);
            eventImgList.add(img);
            eventAudioList.add(audio);
        } else {
            eventList.add(pos, title);
            descriptionList.add(pos, description);
            eventImgList.add(pos, img);
            eventAudioList.add(pos, audio);
        }
    }

    // get current eclipse date, set to Aug 21st
    public Date getEclipseDate(){
        Calendar eclipseDate = Calendar.getInstance();
        eclipseDate.setTimeZone(TimeZone.getTimeZone("UTC"));
        eclipseDate.set(Calendar.YEAR, 2017);
        eclipseDate.set(Calendar.MONTH, Calendar.AUGUST);
        eclipseDate.set(Calendar.DAY_OF_MONTH, 21);
        eclipseDate.set(Calendar.HOUR_OF_DAY, 20);
        eclipseDate.set(Calendar.MINUTE, 11);
        eclipseDate.set(Calendar.SECOND, 14);
        return eclipseDate.getTime();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
