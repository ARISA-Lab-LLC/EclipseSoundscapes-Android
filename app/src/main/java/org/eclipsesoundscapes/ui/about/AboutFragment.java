package org.eclipsesoundscapes.ui.about;

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

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.ui.walkthrough.WalkthroughActivity;

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
 * @see OurTeamActivity
 * @see OurPartnersActivity
 * @see FutureEclipsesActivity
 * @see WalkthroughActivity
 * @see SettingsActivity
 */

public class AboutFragment extends Fragment {
    private String[] listOptions;
    private Integer[] listIcons = {R.drawable.ic_nav_elcipse_features, R.drawable.ic_team, R.drawable.ic_partners,
                                R.drawable.ic_nav_eclipse_center, R.drawable.ic_instructions,
                                R.drawable.ic_feedback, R.drawable.ic_settings, R.drawable.ic_legal};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listOptions = getResources().getStringArray(R.array.listOptions);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_about, container, false);

        // views
        Toolbar toolbar = root.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.about_us));
        if (getActivity() != null)
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        RecyclerView recyclerView = root.findViewById(R.id.about_list_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.hasFixedSize();

        // adapter
        AboutArrayAdapter aboutAdapter = new AboutArrayAdapter(getActivity(), listOptions, listIcons );
        recyclerView.setAdapter(aboutAdapter);

        return root;
    }
}
