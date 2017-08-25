package org.eclipsesoundscapes.fragments;

import android.Manifest;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.activity.MainActivity;
import org.eclipsesoundscapes.activity.WalkthroughActivity;

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
 * Reycled in the walk-through viewpager to display layouts
 * @see WalkthroughActivity
 *
 */

public class WalkthroughFragment extends Fragment implements View.OnClickListener {

    private static String LAYOUT_ID = "layout_id";
    private static String LAYOUT_POS = "layout_pos";
    private static String LAYOUT_TOTAL = "layout_total";


    /* Each fragment has got an R reference to the image it will display
     * an R reference to the title it will display, and an R reference to the
     * string content.
     */
    private int layoutId;
    private int position;
    private int total; // total pages for walkthrough

    public static WalkthroughFragment newInstance(int layoutId, int position, int total) {
        final WalkthroughFragment f = new WalkthroughFragment();
        final Bundle args = new Bundle();
        args.putInt(LAYOUT_ID, layoutId);
        args.putInt(LAYOUT_POS, position);
        args.putInt(LAYOUT_TOTAL, total);

        f.setArguments(args);
        return f;
    }

    // Empty constructor, required as per Fragment docs
    public WalkthroughFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.layoutId = arguments.getInt(LAYOUT_ID);
            this.position = arguments.getInt(LAYOUT_POS);
            this.total = arguments.getInt(LAYOUT_TOTAL);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Identify and set fields!
        ViewGroup rootView = (ViewGroup) inflater.inflate(layoutId, container, false);
        TextView currentPage = (TextView) rootView.findViewById(R.id.current_page);
        position = position + 1; // offset from 0
        currentPage.setText("Page ".concat(String.valueOf(position)).concat(" of ").concat(String.valueOf(total)));
        currentPage.setContentDescription("Page ".concat(String.valueOf(position)).concat(" of ").concat(String.valueOf(total)));
        if (layoutId == R.layout.layout_walkthrough_five){
            // handle permissions before main content
            Button askLaterButton = (Button) rootView.findViewById(R.id.button_ask_later);
            Button locationButton = (Button) rootView.findViewById(R.id.button_location);
            askLaterButton.setOnClickListener(this);
            locationButton.setOnClickListener(this);
        }
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_ask_later:
                ((WalkthroughActivity)getActivity()).onCompleteWalkthrough();
                break;
            case R.id.button_location:
                permissionLocation();
                break;
        }
    }

    // ask user for location permission during walk through
    public void permissionLocation(){
        final String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, MainActivity.LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        ActivityCompat.requestPermissions(getActivity(), permissions, MainActivity.LOCATION_PERMISSION_REQUEST_CODE);
    }
}