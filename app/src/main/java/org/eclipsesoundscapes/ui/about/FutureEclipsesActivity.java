package org.eclipsesoundscapes.ui.about;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.ui.base.BaseActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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
 * Reads json data in assets folder (future_eclipses.json) file to present a list of future
 * eclipses supported by this application
 */

public class FutureEclipsesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.eclipsesoundscapes.R.layout.activity_future_eclipses);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getString(R.string.supported_eclipse));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get parse json data and create a FutureEclipse object for each entry
        ArrayList<FutureEclipse> futureEclipses = new ArrayList<>();
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(loadJSONFromAsset());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject eclipse = jsonArray.getJSONObject(i);
                futureEclipses.add(new FutureEclipse(eclipse.getString("Date"), eclipse.getString("Time"),
                        eclipse.getString("Type"), eclipse.getString("Features")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // present in list view
        RecyclerView recyclerView = findViewById(R.id.future_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FutureEclipseAdapter(futureEclipses));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
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

    /**
     * Parses json into a String
     * @return
     */
    public String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = null;
            try {
                is = getAssets().open("future_eclipses.json");
            } catch (IOException e) {
                e.printStackTrace();
            }
            int size = 0;
            if (is != null) {
                size = is.available();
            }
            byte[] buffer = new byte[size];
            if (is != null) {
                is.read(buffer);
                is.close();
            }
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    /**
     * Class represents a future eclipse object, includes date, time, type and
     * what interactive features will be available through Eclipse Soundscapes application
     */
    private class FutureEclipse {

        String date;
        String time;
        String type;
        String features;

        FutureEclipse(String date, String time, String type, String features){
            this.date  = date;
            this.time = time;
            this.type = type;
            this.features = features;
        }

        String getDate() {
            return date;
        }

        String getTime() {
            return time;
        }

        String getType() {
            return type;
        }

         String getFeatures() {
            return features;
        }

    }

    /**
     * Simple adapter for future eclipse objects in a list view
     */
    class FutureEclipseAdapter extends RecyclerView.Adapter<FutureEclipseAdapter.ViewHolder> {

        ArrayList<FutureEclipse> futureEclipses;

        FutureEclipseAdapter(ArrayList<FutureEclipse> futureEclipses) {
            this.futureEclipses = futureEclipses;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(org.eclipsesoundscapes.R.layout.item_future_eclipse, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            final Context context = holder.itemView.getContext();
            if (context == null) {
                return;
            }

            holder.date.setText(context.getString(R.string.future_eclipse_label_format,
                    context.getString(R.string.date), futureEclipses.get(position).getDate()));

            holder.time.setText(context.getString(R.string.future_eclipse_label_format,
                    context.getString(R.string.time), futureEclipses.get(position).getTime()));

            holder.type.setText(context.getString(R.string.future_eclipse_label_format,
                    context.getString(R.string.type), futureEclipses.get(position).getType()));

            holder.features.setText(context.getString(R.string.future_eclipse_label_format,
                    context.getString(R.string.features), context.getString(R.string.future_eclipse_features)));
        }

        @Override
        public int getItemCount() {
            return futureEclipses.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.date) TextView date;
            @BindView(R.id.time) TextView time;
            @BindView(R.id.type) TextView type;
            @BindView(R.id.features) TextView features;


            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
