package org.eclipsesoundscapes.eclipsesoundscapes.activity;

import android.app.DialogFragment;
import android.content.Context;
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

import org.eclipsesoundscapes.eclipsesoundscapes.R;
import org.eclipsesoundscapes.eclipsesoundscapes.adapters.MediaAdapter;
import org.eclipsesoundscapes.eclipsesoundscapes.fragments.MediaPlayerFragment;

/**
 * Class is used to simply display legal document depending on intent from SettingsActivity
 */

public class LegalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String legalMode = getIntent().getStringExtra("legal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (legalMode.equals("license")) {
            getSupportActionBar().setTitle("License");
            setTitle("License"); // accessibility title read
            setContentView(R.layout.activity_legal_license);
        }
        else if (legalMode.equals("libraries")) {
            getSupportActionBar().setTitle("Eclipse Soundscapes v1.0");
            setTitle("Eclipse Soundscapes v1.0"); // accessibility title read
            setContentView(R.layout.activity_legal_libraries);
        } else {
            getSupportActionBar().setTitle("Photo Credits");
            setTitle("Photo Credits"); // accessibility title read
            setContentView(R.layout.legal_photo_credits);

            Integer[] imgs = {R.drawable.eclipse_diamond_ring, R.drawable.helmet_streamers,
                    R.drawable.eclipse_prominence, R.drawable.sun_as_a_star, R.drawable.eclipse_first_contact,
                    R.drawable.eclipse_totality, R.drawable.eclipse_bailys_beads, R.drawable.eclipse_corona};
            String[] titles = getResources().getStringArray(R.array.eclipse_imgs_title);
            String[] credits = getResources().getStringArray(R.array.eclipse_img_credits);
            String[] links = getResources().getStringArray(R.array.eclipse_img_credit_links);

            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.photo_credits_recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(new CreditsAdapter(imgs, titles, credits, links));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }


    public class CreditsAdapter extends RecyclerView.Adapter<CreditsAdapter.ViewHolder> {

        private final Integer[] photos;
        private final String[] titles;
        private final String[] credits;
        private final String[] links;

        public CreditsAdapter(Integer[] photos, String[] titles, String[] credits, String[] links ) {
            this.photos = photos;
            this.titles = titles;
            this.credits = credits;
            this.links = links;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_photo_credit, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.photo.setImageResource(photos[position]);
            holder.title.setText(titles[position]);
            holder.author.setText(credits[position]);
            holder.link.setText(links[position]);
            Linkify.addLinks(holder.link, Linkify.ALL);
        }

        @Override
        public int getItemCount() {
            return photos.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView photo;
            TextView title;
            TextView author;
            TextView link;

            ViewHolder(View view) {
                super(view);
                photo = (ImageView) view.findViewById(R.id.photo);
                title = (TextView) view.findViewById(R.id.title);
                author = (TextView) view.findViewById(R.id.credit);
                link = (TextView) view.findViewById(R.id.link);
            }
        }

    }

}
