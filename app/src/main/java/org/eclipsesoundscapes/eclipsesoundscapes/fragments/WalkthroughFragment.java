package org.eclipsesoundscapes.eclipsesoundscapes.fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.eclipsesoundscapes.eclipsesoundscapes.R;
import org.eclipsesoundscapes.eclipsesoundscapes.activity.MainActivity;
import org.eclipsesoundscapes.eclipsesoundscapes.activity.WalkthroughActivity;

/**
 * Created by joel on 8/3/17.
 */

public class WalkthroughFragment extends Fragment implements View.OnClickListener {

    private static String LAYOUT_ID = "layout_id";
    private static String LAYOUT_POS = "layout_pos";
    private static String LAYOUT_TOTAL = "layout_total";

    private Context mContext;

    /* Each fragment has got an R reference to the image it will display
     * an R reference to the title it will display, and an R reference to the
     * string content.
     */
    private int layoutId;
    private int position;
    private int total; // total pages for walkt hrough

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
         mContext = context;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_ask_later:
                ((WalkthroughActivity)mContext).onCompleteWalkthrough();
                break;
            case R.id.button_location:
                permissionLocation();
                break;
        }
    }

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