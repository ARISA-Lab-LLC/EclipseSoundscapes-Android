package org.eclipsesoundscapes.ui.about;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.model.Partner;
import org.eclipsesoundscapes.ui.base.BaseActivity;

import java.util.ArrayList;

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

public class OurPartnersActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_our_partners);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.our_partners));
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final RecyclerView partnersRecyclerView = findViewById(R.id.partners_recyclerview);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        final ArrayList<Partner> currentPartners = getPartners(true);
        final ArrayList<Partner> pastPartners = getPartners(false);
        final PartnersAdapter adapter = new PartnersAdapter(this, currentPartners, pastPartners);

        partnersRecyclerView.setLayoutManager(layoutManager);
        partnersRecyclerView.setNestedScrollingEnabled(false);
        partnersRecyclerView.setAdapter(adapter);
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

    /**
     * Retrieves a list of partners
     * @param isCurrent true if partners are current supporters, otherwise past
     * @return a {@link java.util.List} of {@link Partner partners}
     */
    private ArrayList<Partner> getPartners(final boolean isCurrent) {
        final ArrayList<Partner> partners = new ArrayList<>();

        final Resources res = getResources();
        final TypedArray logos = res.obtainTypedArray(isCurrent ? R.array.current_partners_logo
                : R.array.past_partners_logo);

        final String[] titles = getResources().getStringArray(isCurrent ? R.array.current_partners_title
                : R.array.past_partners_title);

        final String[] descriptions = getResources().getStringArray(isCurrent ? R.array.current_partners_desc
                : R.array.past_partners_desc);

        final String[] links = getResources().getStringArray(isCurrent ? R.array.current_partners_link
                : R.array.past_partners_link);


        // assumes all partner data is in order and of equal length
        for (int i = 0; i < titles.length; i++) {
            final Partner partner = new Partner(titles[i], descriptions[i], links[i], logos.getDrawable(i));
            partners.add(partner);
        }

        logos.recycle();
        return partners;
    }
}
