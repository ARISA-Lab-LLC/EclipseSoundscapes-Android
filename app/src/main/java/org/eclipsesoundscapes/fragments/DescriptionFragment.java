package org.eclipsesoundscapes.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.activity.MainActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
 * Description tab of the RumbleMapFragment tablayout, provides a text based description
 * of each eclipse image
 * @see RumbleMapFragment
 */

public class DescriptionFragment extends Fragment {

    private TextView eclipseDescription;
    private Context context;
    private int currentPoint = 1; // keep track of current page, default
    private HashMap<Integer, String> currentDescriptions;
    private Calendar calendar;

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
        calendar = Calendar.getInstance();
        currentDescriptions = new HashMap<>();
        currentDescriptions.put(1, getString(R.string.bailys_beads_description));
        currentDescriptions.put(2, getString(R.string.bailys_beads_description));
        currentDescriptions.put(3, getString(R.string.corona_description));
        currentDescriptions.put(4, getString(R.string.diamond_ring_description));
        currentDescriptions.put(5, getString(R.string.helmet_streamers_description));
        currentDescriptions.put(6, getString(R.string.helmet_streamers_description));
        currentDescriptions.put(7, getString(R.string.prominence_description));
        currentDescriptions.put(8, getString(R.string.prominence_description));
    }

    @Override
    public void onStart() {
        super.onStart();
        context = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        Date eclipseDate = getEclipseDate();
        Date date = ((MainActivity)context).getFirstContact();
        Date dateTwo = ((MainActivity)context).getSecondContact();
        if (calendar.getTime().after(eclipseDate) && currentDescriptions.size() < 10){
            currentDescriptions.clear();
            currentDescriptions.put(1, getString(R.string.first_contact_description));
            currentDescriptions.put(2, getString(R.string.bailys_beads_description));
            currentDescriptions.put(3, getString(R.string.bailys_beads_description));
            currentDescriptions.put(4, getString(R.string.corona_description));
            currentDescriptions.put(5, getString(R.string.diamond_ring_description));
            currentDescriptions.put(6, getString(R.string.helmet_streamers_description));
            currentDescriptions.put(7, getString(R.string.helmet_streamers_description));
            currentDescriptions.put(8, getString(R.string.prominence_description));
            currentDescriptions.put(9, getString(R.string.prominence_description));
            currentDescriptions.put(10, getString(R.string.totality_description));
        } else if (date != null && dateTwo != null){
            if (calendar.getTime().after(date) && calendar.getTime().before(dateTwo) && currentDescriptions.size() < 9){
                currentDescriptions.clear();
                currentDescriptions.put(1, getString(R.string.first_contact_description));
                currentDescriptions.put(2, getString(R.string.bailys_beads_description));
                currentDescriptions.put(3, getString(R.string.bailys_beads_description));
                currentDescriptions.put(4, getString(R.string.corona_description));
                currentDescriptions.put(5, getString(R.string.diamond_ring_description));
                currentDescriptions.put(6, getString(R.string.helmet_streamers_description));
                currentDescriptions.put(7, getString(R.string.helmet_streamers_description));
                currentDescriptions.put(8, getString(R.string.prominence_description));
                currentDescriptions.put(9, getString(R.string.prominence_description));
            } else if (calendar.getTime().after(dateTwo) && currentDescriptions.size() < 10){
                currentDescriptions.clear();
                currentDescriptions.put(1, getString(R.string.first_contact_description));
                currentDescriptions.put(2, getString(R.string.bailys_beads_description));
                currentDescriptions.put(3, getString(R.string.bailys_beads_description));
                currentDescriptions.put(4, getString(R.string.corona_description));
                currentDescriptions.put(5, getString(R.string.diamond_ring_description));
                currentDescriptions.put(6, getString(R.string.helmet_streamers_description));
                currentDescriptions.put(7, getString(R.string.helmet_streamers_description));
                currentDescriptions.put(8, getString(R.string.prominence_description));
                currentDescriptions.put(9, getString(R.string.prominence_description));
                currentDescriptions.put(10, getString(R.string.totality_description));

            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_description, container, false);
        eclipseDescription = (TextView) root.findViewById(R.id.eclipse_description);
        currentPoint = ((MainActivity)getActivity()).getCurrentCP();
        updateView(currentPoint);

        return root;
    }

    /**
     * Replace the current description of eclipse
     * @param contactPoint Corresponds to a particular eclipse description
     */
    public void updateView(int contactPoint){
        eclipseDescription.setText(currentDescriptions.get(contactPoint));
    }

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
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
