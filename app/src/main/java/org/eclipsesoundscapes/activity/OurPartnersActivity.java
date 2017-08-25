package org.eclipsesoundscapes.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import org.eclipsesoundscapes.R;
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
 * Create a list view of Eclipse Soundscapes partners
 */

public class OurPartnersActivity extends AppCompatActivity {

    private Integer[] partner_logo = {R.drawable.nasa, R.drawable.national_parkers_logo, R.drawable.smithsonian_logo,
                                        R.drawable.ncam_logo, R.drawable.eclipsemob_textlogo_large_reverse,
                                        R.drawable.logo_scifri, R.drawable.ngcp_theme_logo, R.drawable.nso_logo_200_a, R.drawable.byu};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_our_partners);

        getSupportActionBar().setTitle(getString(R.string.our_partners));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView partnersRecyclerView = (RecyclerView) findViewById(R.id.partners_recyclerview);
        String[] partnerList = getResources().getStringArray(R.array.partners);
        String[] partnerDescription = getResources().getStringArray(R.array.partner_description);
        String[] partnerLink = getResources().getStringArray(R.array.partner_links);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        PartnerTeamAdapter adapter = new PartnerTeamAdapter(partnerList, partnerLink, partnerDescription, partner_logo, false);
        partnersRecyclerView.setLayoutManager(layoutManager);
        partnersRecyclerView.setNestedScrollingEnabled(false);
        partnersRecyclerView.setAdapter(adapter);
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
