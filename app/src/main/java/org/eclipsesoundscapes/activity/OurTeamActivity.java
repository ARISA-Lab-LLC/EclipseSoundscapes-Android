package org.eclipsesoundscapes.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import org.eclipsesoundscapes.adapters.PartnerTeamAdapter;

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
 * Create a list view of the Eclipse Soundscapes team
 */

public class OurTeamActivity extends AppCompatActivity {

    private Integer[] teamImgs = {org.eclipsesoundscapes.R.drawable.henry_winter, org.eclipsesoundscapes.R.drawable.marykay_severino, org.eclipsesoundscapes.R.drawable.arlindo,
            org.eclipsesoundscapes.R.drawable.miles_gordon, org.eclipsesoundscapes.R.drawable.christina_migliore, org.eclipsesoundscapes.R.drawable.kristin_divona, org.eclipsesoundscapes.R.drawable.kelsey_perrett,
            org.eclipsesoundscapes.R.drawable.ic_default};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.eclipsesoundscapes.R.layout.activity_our_team);

        getSupportActionBar().setTitle(getString(org.eclipsesoundscapes.R.string.our_team));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView partnersRecyclerView = (RecyclerView) findViewById(org.eclipsesoundscapes.R.id.teams_recyclerview);
        String[] teamList = getResources().getStringArray(org.eclipsesoundscapes.R.array.team);
        String[] teamDescription = getResources().getStringArray(org.eclipsesoundscapes.R.array.team_bio);
        String[] teamTitle = getResources().getStringArray(org.eclipsesoundscapes.R.array.team_title);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        PartnerTeamAdapter adapter = new PartnerTeamAdapter(teamList, teamTitle, teamDescription, teamImgs, true);
        partnersRecyclerView.setLayoutManager(layoutManager);
        partnersRecyclerView.setAdapter(adapter);
        partnersRecyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
