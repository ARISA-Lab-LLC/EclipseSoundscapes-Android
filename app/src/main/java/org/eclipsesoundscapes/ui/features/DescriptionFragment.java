package org.eclipsesoundscapes.ui.features;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;


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
    private ArrayList<String> descriptions = new ArrayList<>();

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

        final boolean showAllFeatures = getResources().getBoolean(R.bool.show_all_content);
        if (showAllFeatures) {
            descriptions = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.totality_features_description)));
            return;
        }

        descriptions = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.default_features_description)));

        if (getActivity() != null && getActivity() instanceof MainActivity) {
            if (((MainActivity) getActivity()).isAfterTotality()) {
                descriptions = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.totality_features_description)));
            } else {
                descriptions = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.first_contact_features_description)));
            }
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
