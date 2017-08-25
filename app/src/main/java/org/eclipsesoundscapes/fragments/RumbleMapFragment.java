package org.eclipsesoundscapes.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.eclipsesoundscapes.activity.MainActivity;
import org.eclipsesoundscapes.activity.RumbleMapInteractionActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;


public class RumbleMapFragment extends Fragment {

    private Context mContext;
    private ImageView imageView;
    private int currentEclipseID; // current eclipse img displaying
    private int currentPoint = 1; // default
    private HashMap<Integer, Integer> currentImgs;
    private Calendar calendar;

    public RumbleMapFragment() {
        // Required empty public constructor
    }

    public static RumbleMapFragment newInstance(String param1, String param2) {
        return new RumbleMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();

        // maps contact point to a eclipse image
        currentImgs = new HashMap<>();
        currentImgs.put(1, org.eclipsesoundscapes.R.drawable.eclipse_bailys_beads);
        currentImgs.put(2, org.eclipsesoundscapes.R.drawable.bailys_beads_close_up);
        currentImgs.put(3, org.eclipsesoundscapes.R.drawable.eclipse_corona);
        currentImgs.put(4, org.eclipsesoundscapes.R.drawable.eclipse_diamond_ring);
        currentImgs.put(5, org.eclipsesoundscapes.R.drawable.helmet_streamers);
        currentImgs.put(6, org.eclipsesoundscapes.R.drawable.helmet_streamer_closeup);
        currentImgs.put(7, org.eclipsesoundscapes.R.drawable.eclipse_prominence);
        currentImgs.put(8, org.eclipsesoundscapes.R.drawable.prominence_closeup);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(org.eclipsesoundscapes.R.layout.fragment_rumble_map, container, false);
        imageView = (ImageView) root.findViewById(org.eclipsesoundscapes.R.id.contact_point_img);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchRumbleMap();
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        Date eclipseDate = getEclipseDate();
        Date date = ((MainActivity)mContext).getFirstContact();
        Date dateTwo = ((MainActivity)mContext).getSecondContact();
        if (calendar.getTime().after(eclipseDate) && currentImgs.size() < 10){
            currentImgs.clear();
            currentImgs.put(1, org.eclipsesoundscapes.R.drawable.eclipse_first_contact);
            currentImgs.put(2, org.eclipsesoundscapes.R.drawable.eclipse_bailys_beads);
            currentImgs.put(3, org.eclipsesoundscapes.R.drawable.bailys_beads_close_up);
            currentImgs.put(4, org.eclipsesoundscapes.R.drawable.eclipse_corona);
            currentImgs.put(5, org.eclipsesoundscapes.R.drawable.eclipse_diamond_ring);
            currentImgs.put(6, org.eclipsesoundscapes.R.drawable.helmet_streamers);
            currentImgs.put(7, org.eclipsesoundscapes.R.drawable.helmet_streamer_closeup);
            currentImgs.put(8, org.eclipsesoundscapes.R.drawable.eclipse_prominence);
            currentImgs.put(9, org.eclipsesoundscapes.R.drawable.prominence_closeup);
            currentImgs.put(10, org.eclipsesoundscapes.R.drawable.eclipse_totality);
        } else if (date != null && dateTwo != null){
            if (calendar.getTime().after(date) && calendar.getTime().before(dateTwo)){
                currentImgs.clear();
                currentImgs.put(1, org.eclipsesoundscapes.R.drawable.eclipse_first_contact);
                currentImgs.put(2, org.eclipsesoundscapes.R.drawable.eclipse_bailys_beads);
                currentImgs.put(3, org.eclipsesoundscapes.R.drawable.bailys_beads_close_up);
                currentImgs.put(4, org.eclipsesoundscapes.R.drawable.eclipse_corona);
                currentImgs.put(5, org.eclipsesoundscapes.R.drawable.eclipse_diamond_ring);
                currentImgs.put(6, org.eclipsesoundscapes.R.drawable.helmet_streamers);
                currentImgs.put(7, org.eclipsesoundscapes.R.drawable.helmet_streamer_closeup);
                currentImgs.put(8, org.eclipsesoundscapes.R.drawable.eclipse_prominence);
                currentImgs.put(9, org.eclipsesoundscapes.R.drawable.prominence_closeup);

            } else if (calendar.getTime().after(dateTwo)){
                currentImgs.clear();
                currentImgs.put(1, org.eclipsesoundscapes.R.drawable.eclipse_first_contact);
                currentImgs.put(2, org.eclipsesoundscapes.R.drawable.eclipse_bailys_beads);
                currentImgs.put(3, org.eclipsesoundscapes.R.drawable.bailys_beads_close_up);
                currentImgs.put(4, org.eclipsesoundscapes.R.drawable.eclipse_corona);
                currentImgs.put(5, org.eclipsesoundscapes.R.drawable.eclipse_diamond_ring);
                currentImgs.put(6, org.eclipsesoundscapes.R.drawable.helmet_streamers);
                currentImgs.put(7, org.eclipsesoundscapes.R.drawable.helmet_streamer_closeup);
                currentImgs.put(8, org.eclipsesoundscapes.R.drawable.eclipse_prominence);
                currentImgs.put(9, org.eclipsesoundscapes.R.drawable.prominence_closeup);
                currentImgs.put(10, org.eclipsesoundscapes.R.drawable.eclipse_totality);
            }
        }

        if (!getActivity().isDestroyed())
            currentPoint = ((MainActivity)getActivity()).getCurrentCP();
        updateView(currentPoint);
    }

    @Override
    public void onStart() {
        super.onStart();
        mContext = getActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(mContext, org.eclipsesoundscapes.R.color.colorPrimaryDark));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        currentPoint = ((MainActivity)getActivity()).getCurrentCP();
    }

    /**
     * Update current image view
     * @param contactPoint Corresponds to a particular eclipse image
     */
    public void updateView(int contactPoint){
        imageView.setImageResource(currentImgs.get(contactPoint));
        currentEclipseID = currentImgs.get(contactPoint);
    }

    // launch RumbleMapInteractionActivity
    void launchRumbleMap() {
        Intent rumbleIntent = new Intent(mContext, RumbleMapInteractionActivity.class);
        rumbleIntent.putExtra("img", currentEclipseID);
        mContext.startActivity(rumbleIntent);
    }

    // get current eclipse date, set to Aug 21st
    public Date getEclipseDate(){
        Calendar eclipseDate = Calendar.getInstance();
        eclipseDate.setTimeZone(TimeZone.getTimeZone("UTC"));
        eclipseDate.set(Calendar.YEAR, 2017);
        eclipseDate.set(Calendar.MONTH, Calendar.AUGUST);
        eclipseDate.set(Calendar.DAY_OF_MONTH, 21);
        eclipseDate.set(Calendar.HOUR_OF_DAY, 20);
        eclipseDate.set(Calendar.MINUTE, 11);
        eclipseDate.set(Calendar.SECOND, 14);
        return eclipseDate.getTime();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
