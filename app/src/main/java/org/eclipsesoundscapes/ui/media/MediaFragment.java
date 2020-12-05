package org.eclipsesoundscapes.ui.media;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.ui.main.MainActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


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
 * {@link MediaPlayerActivity}
 */

public class MediaFragment extends Fragment {

    private ArrayList<String> eventList;
    private ArrayList<Integer> descriptionList;
    private ArrayList<Integer> eventImgList;
    private ArrayList<Integer> eventAudioList;
    private MediaAdapter mediaAdapter;

    @BindView(R.id.media_recycler) RecyclerView mRecycler;
    @BindView(R.id.more_content) TextView moreView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventList = new ArrayList<>();
        descriptionList = new ArrayList<>();
        eventImgList = new ArrayList<>();
        eventAudioList = new ArrayList<>();

        // add default media content
        addMedia(-1, getString(R.string.bailys_beads), R.string.bailys_beads_description, R.drawable.eclipse_bailys_beads, R.raw.bailys_beads_full);
        addMedia(-1, getString(R.string.prominence), R.string.prominence_description, R.drawable.eclipse_prominence, R.raw.prominence_full);
        addMedia(-1, getString(R.string.corona), R.string.corona_description, R.drawable.eclipse_corona, R.raw.corona_full);
        addMedia(-1, getString(R.string.helmet_streamers), R.string.helmet_streamers_description, R.drawable.helmet_streamers, R.raw.helmet_streamers_full);
        addMedia(-1, getString(R.string.diamond_ring), R.string.diamond_ring_description, R.drawable.eclipse_diamond_ring, R.raw.diamond_ring_full);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_media, container, false);
        ButterKnife.bind(this, root);

        Toolbar toolbar = root.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.media));

        if (getActivity() != null)
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mediaAdapter = new MediaAdapter(getActivity(), eventList, descriptionList, eventImgList, eventAudioList);
        mRecycler.setAdapter(mediaAdapter);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mediaAdapter != null)
            mediaAdapter.notifyDataSetChanged();

        if (getActivity() != null){
            if (getActivity() instanceof MainActivity){

                // add first contact & totality media content during/after its occurrence
                if (!eventList.contains(getString(R.string.first_contact))
                        && ((MainActivity) getActivity()).isAfterFirstContact()){
                    addFirstContact();
                }

                if (!eventList.contains(getString(R.string.totality))
                        && ((MainActivity) getActivity()).isAfterTotality()){
                    addTotality();
                }
            }

            if (eventList.contains(getString(R.string.totality)))
                moreView.setVisibility(View.GONE);
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
    private void addMedia(int pos, String title, int description, int img, int audio){
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

    private void addFirstContact(){
        addMedia(0, getString(R.string.first_contact),
                R.string.first_contact_description, R.drawable.eclipse_first_contact, R.raw.first_contact_short);
        mediaAdapter.notifyItemInserted(0);
    }

    private void addTotality(){
        addMedia(eventList.size(), getString(R.string.totality),
                R.string.totality_description, R.drawable.eclipse_totality, R.raw.totality_short);
        addMedia(eventList.size(), getString(R.string.sun_as_star),
                R.string.sun_as_star_description, R.drawable.sun_as_a_star, R.raw.sun_as_a_star);
        addMedia(eventList.size(), getString(R.string.eclipse_experience),
                R.string.bailys_beads_description, R.drawable.eclipse_bailys_beads, R.raw.realtime_eclipse_shorts_saas);

        mediaAdapter.notifyItemInserted(eventList.size() - 1);
        mediaAdapter.notifyItemInserted(eventList.size() - 1);
        mediaAdapter.notifyItemInserted(eventList.size() - 1);
    }
}
