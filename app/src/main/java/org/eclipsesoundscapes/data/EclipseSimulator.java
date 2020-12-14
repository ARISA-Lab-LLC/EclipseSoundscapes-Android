package org.eclipsesoundscapes.data;

import android.content.Context;
import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class EclipseSimulator {
    private Context context;

    public EclipseSimulator(Context context){
        this.context = context;
    }

    /**
     * Parse json into a String
     */
    private String loadJSONFromAsset() {
        String json = "";
        InputStream is;

        try {
            is = context.getAssets().open("maineclipsepolyline.json");
            if (is == null) return "";

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");
        } catch (IOException e){
            e.printStackTrace();
        }

        return json;
    }

    /**
     * Create a map of points in the path of totality from String parsed Json including lat, lng
     * Used to simulate eclipse event
     */
    private HashMap<Double, Double> parseJson(){
        HashMap<Double, Double> locations = new HashMap<>();
        JSONArray jsonArray;

        try {
            jsonArray = new JSONArray(loadJSONFromAsset());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject location = jsonArray.getJSONObject(i);
                locations.put(location.getDouble("lat"), location.getDouble("lon"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return locations;
    }

    /**
     * Find the closest point in the path of totality from this location
     * @param location location not in path of totality
     */

    public Location closestPointOnPath(Location location){

        Location shortestLocation = null;
        HashMap<Double, Double> locations = parseJson();
        Float shortestDistance = Float.POSITIVE_INFINITY;

        for (Double lat : locations.keySet()){
            Double lng = locations.get(lat);
            Location newLoc = new Location("");
            newLoc.setLatitude(lat);
            newLoc.setLongitude(lng);

            Float distance = location.distanceTo(newLoc);
            if (distance < shortestDistance) {
                shortestDistance = distance;
                shortestLocation = newLoc;
            }
        }

        return shortestLocation;
    }
}
