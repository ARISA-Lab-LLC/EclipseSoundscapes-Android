package org.eclipsesoundscapes.ui.about;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import org.eclipsesoundscapes.R;


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

    private TypedArray teamPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_our_team);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getString(R.string.our_team));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView partnersRecyclerView = findViewById(R.id.teams_recyclerview);
        String[] teamList = getResources().getStringArray(R.array.team);
        String[] teamDescription = getResources().getStringArray(R.array.team_bio);
        String[] teamTitle = getResources().getStringArray(R.array.team_title);

        final Resources res = getResources();
        teamPhotos = res.obtainTypedArray(R.array.team_photos);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        TeamAdapter adapter = new TeamAdapter(teamList, teamTitle, teamDescription, teamPhotos);
        partnersRecyclerView.setLayoutManager(layoutManager);
        partnersRecyclerView.setAdapter(adapter);
        partnersRecyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (teamPhotos != null) {
            teamPhotos.recycle();
        }
    }
}
