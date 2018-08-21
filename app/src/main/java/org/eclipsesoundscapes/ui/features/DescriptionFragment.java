package org.eclipsesoundscapes.ui.features;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.ui.main.MainActivity;


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
 * Description tab of the EclipseFragment tablayout, provides a text based description
 * of each eclipse image
 * @see EclipseFragment
 */

public class DescriptionFragment extends Fragment {

    private TextView eclipseDescription;
    private SparseArray<String> descriptions;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_description, container, false);
        eclipseDescription = root.findViewById(R.id.eclipse_description);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        descriptions = new SparseArray<>();

        if (getActivity() != null && getActivity() instanceof MainActivity) {
            if (!((MainActivity) getActivity()).isAfterFirstContact()) {
                descriptions.put(1, getString(R.string.bailys_beads_description));
                descriptions.put(2, getString(R.string.bailys_beads_description));
                descriptions.put(3, getString(R.string.corona_description));
                descriptions.put(4, getString(R.string.diamond_ring_description));
                descriptions.put(5, getString(R.string.helmet_streamers_description));
                descriptions.put(6, getString(R.string.helmet_streamers_description));
                descriptions.put(7, getString(R.string.prominence_description));
                descriptions.put(8, getString(R.string.prominence_description));
                return;
            }

            descriptions.put(1, getString(R.string.first_contact_description));
            descriptions.put(2, getString(R.string.bailys_beads_description));
            descriptions.put(3, getString(R.string.bailys_beads_description));
            descriptions.put(4, getString(R.string.corona_description));
            descriptions.put(5, getString(R.string.diamond_ring_description));
            descriptions.put(6, getString(R.string.helmet_streamers_description));
            descriptions.put(7, getString(R.string.helmet_streamers_description));
            descriptions.put(8, getString(R.string.prominence_description));
            descriptions.put(9, getString(R.string.prominence_description));

            if (((MainActivity) getActivity()).isAfterTotality())
                descriptions.put(10, getString(R.string.totality_description));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null){
            int currentPoint = ((MainActivity) getActivity()).getCurrentView();
            updateView(currentPoint);
        }
    }

    /**
     * Replace the current description of eclipse
     * @param contactPoint Corresponds to a particular eclipse description
     */
    public void updateView(int contactPoint){
        eclipseDescription.setText(descriptions.get(contactPoint));
    }
}
