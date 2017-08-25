package org.eclipsesoundscapes.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
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
 * Reads json data in assets folder (future_eclipses.json) file to present a list of future
 * eclipses supported by this application
 */

public class FutureEclipsesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.eclipsesoundscapes.R.layout.activity_future_eclipses);
        getSupportActionBar().setTitle("Eclipses We Support");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get parse json data and create a FutureEclipse object for each entry
        ArrayList<FutureEclipse> futureEclipses = new ArrayList<>();
        JSONArray jsonArray = null;
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
        RecyclerView recyclerView = (RecyclerView) findViewById(org.eclipsesoundscapes.R.id.future_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FutureEclipseAdapter(futureEclipses));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    /**
     * Parses json into a String
     * @return
     */
    public String loadJSONFromAsset() {
        String json = null;
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

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(org.eclipsesoundscapes.R.layout.item_future_eclipse, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.date.setText("Date: ".concat(futureEclipses.get(position).getDate()));
            holder.time.setText("Time: ".concat(futureEclipses.get(position).getTime()));
            holder.type.setText("Type: ".concat(futureEclipses.get(position).getType()));
            holder.features.setText("Features: ".concat(futureEclipses.get(position).getFeatures()));
        }

        @Override
        public int getItemCount() {
            return futureEclipses.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView date;
            TextView time;
            TextView type;
            TextView features;

            ViewHolder(View view) {
                super(view);
                date = (TextView) view.findViewById(org.eclipsesoundscapes.R.id.date);
                time = (TextView) view.findViewById(org.eclipsesoundscapes.R.id.time);
                type = (TextView) view.findViewById(org.eclipsesoundscapes.R.id.type);
                features = (TextView) view.findViewById(org.eclipsesoundscapes.R.id.features);
            }
        }

    }
}
