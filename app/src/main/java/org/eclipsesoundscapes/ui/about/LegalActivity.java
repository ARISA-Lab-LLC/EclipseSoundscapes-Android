package org.eclipsesoundscapes.ui.about;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import org.eclipsesoundscapes.R;

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
 * Display legal document depending on intent from SettingsActivity
 * E.g Application license, Photo Credits, open source libraries
 * See {@link SettingsActivity}
 */

public class LegalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String legalMode = getIntent().getStringExtra("legal");
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switch (legalMode) {
            case "license":
                getSupportActionBar().setTitle("License");
                setTitle("License");
                setContentView(R.layout.activity_legal_license);
                break;
            case "libraries":
                getSupportActionBar().setTitle("Eclipse Soundscapes v1.0");
                setTitle("Eclipse Soundscapes v1.0");
                setContentView(R.layout.activity_legal_libraries);
                break;
            default:
                getSupportActionBar().setTitle("Photo Credits");
                setTitle("Photo Credits"); // accessibility title read
                setContentView(R.layout.legal_photo_credits);

                Integer[] imgs = {R.drawable.eclipse_diamond_ring, R.drawable.helmet_streamers,
                        R.drawable.eclipse_prominence, R.drawable.sun_as_a_star, R.drawable.eclipse_first_contact,
                        R.drawable.eclipse_totality, R.drawable.eclipse_bailys_beads, R.drawable.eclipse_corona};
                String[] titles = getResources().getStringArray(R.array.eclipse_imgs_title);
                String[] credits = getResources().getStringArray(R.array.eclipse_img_credits);
                String[] links = getResources().getStringArray(R.array.eclipse_img_credit_links);

                RecyclerView recyclerView = findViewById(R.id.photo_credits_recycler);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(new CreditsAdapter(imgs, titles, credits, links));
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }

    class CreditsAdapter extends RecyclerView.Adapter<CreditsAdapter.ViewHolder> {

        private final Integer[] photos;
        private final String[] titles;
        private final String[] credits;
        private final String[] links;

        CreditsAdapter(Integer[] photos, String[] titles, String[] credits, String[] links) {
            this.photos = photos;
            this.titles = titles;
            this.credits = credits;
            this.links = links;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_photo_credit, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            holder.title.setText(titles[position]);
            holder.author.setText(credits[position]);
            holder.link.setText(links[position]);

            Glide.with(holder.photo.getContext())
                    .load(photos[position])
                    .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(8)))
                    .into(holder.photo);

            Linkify.addLinks(holder.link, Linkify.ALL);
        }

        @Override
        public int getItemCount() {
            return photos.length;
        }

        //View holder for list view items in photo credits
        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.photo) ImageView photo;
            @BindView(R.id.title) TextView title;
            @BindView(R.id.credit) TextView author;
            @BindView(R.id.link) TextView link;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
