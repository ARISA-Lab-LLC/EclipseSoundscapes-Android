package org.eclipsesoundscapes.eclipsesoundscapes.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipsesoundscapes.eclipsesoundscapes.R;
import org.eclipsesoundscapes.eclipsesoundscapes.activity.MainActivity;
import org.eclipsesoundscapes.eclipsesoundscapes.activity.SettingsActivity;
import org.eclipsesoundscapes.eclipsesoundscapes.service.GPSTracker;
import org.eclipsesoundscapes.eclipsesoundscapes.util.EclipseTimeGenerator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

public class EclipseCenterFragment extends Fragment {

    public static int REQUEST_PERMISSION_SETTING = 48;

    Context mContext;
    EclipseTimeGenerator eclipseTimeGenerator;
    GPSTracker gpsTracker;
    SimpleDateFormat dateFormat;
    SharedPreferences sharedPreferences;
    DialogFragment permissionDialogFragment;

    // views
    // countdown
    private TextView daysPrimary;
    private TextView daysSecondary;
    private TextView hoursPrimary;
    private TextView hoursSecondary;
    private TextView minPrimary;
    private TextView minSecondary;
    private TextView secPrimary;
    private TextView secSecondary;

    // Eclipse details
    private TextView eclipseTypeView;
    private TextView dateView;
    private TextView latitudeView;
    private TextView longitudeView;
    private TextView percentEclipseView;

    // eclipse events
    LinearLayout contactOneStub;
    LinearLayout contactTwoStub;
    LinearLayout contactMidStub;
    LinearLayout contactThreeStub;
    LinearLayout contactFourStub;

    private CoordinatorLayout coordinatorLayout;
    private RelativeLayout countdownView;
    private LinearLayout eclipseCenterView;
    private LinearLayout permissionView;
    private LinearLayout progressView;

    public EclipseCenterFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.S");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_eclipse_center, container, false);

        coordinatorLayout = (CoordinatorLayout) root.findViewById(R.id.coordinator_layout);
        countdownView = (RelativeLayout) root.findViewById(R.id.eclipse_countdown);
        eclipseCenterView = (LinearLayout) root.findViewById(R.id.rl_eclipse_center);
        permissionView = (LinearLayout) root.findViewById(R.id.permission_view);
        progressView = (LinearLayout) root.findViewById(R.id.progressView);

        eclipseTypeView = (TextView) root.findViewById(R.id.eclipse_type);
        dateView = (TextView) root.findViewById(R.id.date);
        latitudeView = (TextView) root.findViewById(R.id.latitude);
        longitudeView = (TextView) root.findViewById(R.id.longitude);
        percentEclipseView = (TextView) root.findViewById(R.id.percent_eclipse);

        contactOneStub = (LinearLayout) root.findViewById(R.id.stub_contact_one);
        contactTwoStub = (LinearLayout) root.findViewById(R.id.stub_contact_two);
        contactMidStub = (LinearLayout) root.findViewById(R.id.stub_contact_mid);
        contactThreeStub = (LinearLayout) root.findViewById(R.id.stub_contact_three);
        contactFourStub = (LinearLayout) root.findViewById(R.id.stub_contact_four);

        daysPrimary = (TextView)root.findViewById(R.id.days_primary);
        daysSecondary = (TextView)root.findViewById(R.id.days_secondary);
        hoursPrimary = (TextView)root.findViewById(R.id.hours_primary);
        hoursSecondary = (TextView)root.findViewById(R.id.hours_secondary);
        minPrimary = (TextView)root.findViewById(R.id.minutes_primary);
        minSecondary = (TextView)root.findViewById(R.id.minutes_secondary);
        secPrimary = (TextView)root.findViewById(R.id.seconds_primary);
        secSecondary = (TextView)root.findViewById(R.id.seconds_secondary);
        return  root;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void onStart() {
        super.onStart();

        //checkLocationPermission();
        Log.d("locationRes", String.valueOf(sharedPreferences.getBoolean("settings_location", false)));

    }

    @Override
    public void onResume() {
        super.onResume();
        checkLocationPermission();
    }

    public void checkLocationPermission(){
        int rc = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        if (rc != PackageManager.PERMISSION_GRANTED) {
            eclipseCenterView.setVisibility(View.GONE);
            permissionView.setVisibility(View.VISIBLE);
            permissionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    permissionDialogFragment = PermissionDialogFragment.newInstance();
                    permissionDialogFragment.show(getFragmentManager(), "dialog");
                }
            });
        } else if(!sharedPreferences.getBoolean("settings_location", false)) {
            permissionView.setVisibility(View.VISIBLE);
            eclipseCenterView.setVisibility(View.GONE);
            TextView msg = (TextView) permissionView.findViewById(R.id.permission_msg);
            msg.setText(getString(R.string.app_settings_permission));
            final Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, getString(R.string.settings_permission_off), Snackbar.LENGTH_INDEFINITE)
                    .setAction("ENABLE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent settingsIntent = new Intent(mContext, SettingsActivity.class);
                            settingsIntent.putExtra("settings", "settings");
                            startActivity(settingsIntent);
                        }
                    });

            permissionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent settingsIntent = new Intent(mContext, SettingsActivity.class);
                    settingsIntent.putExtra("settings", "settings");
                    if (snackbar.isShown())
                        snackbar.dismiss();
                    startActivity(settingsIntent);
                }
            });

            snackbar.show();
        } else {
            permissionView.setVisibility(View.GONE);
            eclipseCenterView.setVisibility(View.VISIBLE);

            showSearchingProgress();
            startLocationUpdates();
        }
    }

    // called by parent activity (MainActivity)
    public void onPermissionResult(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("settings_location", true);
        editor.apply();

        if (permissionView.isShown()) {
            permissionView.setVisibility(View.GONE);
            eclipseCenterView.setVisibility(View.VISIBLE);
        }

        if (permissionDialogFragment != null && permissionDialogFragment.getDialog() != null
                && permissionDialogFragment.getDialog().isShowing()) {
            permissionDialogFragment.dismiss();
        }

        showSearchingProgress();
        startLocationUpdates();
    }

    public void onPermissionDenied(){
        if (permissionDialogFragment != null && permissionDialogFragment.getDialog() != null
                && permissionDialogFragment.getDialog().isShowing()) {
            permissionDialogFragment.dismiss();
        }
    }

    public void startLocationUpdates(){
        gpsTracker = new GPSTracker(getActivity());

        // get location
        if (gpsTracker.canGetLocation()) {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            hideSearchingProgress();
            Log.d("GPSResult", String.valueOf(latitude));
            Log.d("GPSResult", String.valueOf(longitude));
            String latitudeFormat = String.format(Locale.getDefault(), "%.3f", Math.abs(latitude));
            String longitudeFormat = String.format(Locale.getDefault(), "%.3f", Math.abs(longitude));

            // generate first contact timing
            eclipseTimeGenerator = new EclipseTimeGenerator(latitude, longitude);
            EclipseTimeGenerator.EclipseEvent eclipseEvent = eclipseTimeGenerator.contact1();

            if (latitude > 0 )
                latitudeView.setText(String.valueOf(latitudeFormat).concat(" " .concat((char) 0x00B0 + " North")));
            else
                latitudeView.setText(String.valueOf(latitudeFormat).concat(" ".concat((char) 0x00B0 + " South")));

            if (longitude > 0)
                longitudeView.setText(String.valueOf(longitudeFormat).concat(" ".concat((char) 0x00B0 + " East")));
            else
                longitudeView.setText(String.valueOf(longitudeFormat).concat(" ".concat((char) 0x00B0 + " West")));

            dateView.setText(eclipseEvent.date);
            percentEclipseView.setText(eclipseTimeGenerator.getcoverage());

            // partial or full?
            if (eclipseTimeGenerator.type == EclipseTimeGenerator.EclipseType.FULL)
                eclipseTypeView.setText(getString(R.string.eclipse_type_full));
            else if (eclipseTimeGenerator.type == EclipseTimeGenerator.EclipseType.PARTIAL)
                eclipseTypeView.setText(getString(R.string.eclipse_type_partial));
            else
                eclipseTypeView.setText(getString(R.string.eclipse_type_none));

            populateEvents();

            // countdown until first contact
            String date = eclipseEvent.date.concat(" ").concat(eclipseEvent.time);
            startCountDown(date);

        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    public void populateEvents(){
        switch (eclipseTimeGenerator.type){
            case NONE:
                TextView type = (TextView) contactOneStub.findViewById(R.id.eclipse_event);
                type.setText(getString(R.string.eclipse_type_none));
                contactOneStub.setContentDescription(getString(R.string.eclipse_none_description));
                contactOneStub.setVisibility(View.VISIBLE);
                break;
            case PARTIAL:
                generatePartialContact();
                break;
            case FULL:
                generateFullContact();
                break;
        }
    }

    // generate view for contact point one - mid - end
    public void generatePartialContact(){
        EclipseTimeGenerator.EclipseEvent contactOne = eclipseTimeGenerator.contact1();
        EclipseTimeGenerator.EclipseEvent contactMid = eclipseTimeGenerator.contactMid();
        EclipseTimeGenerator.EclipseEvent contactFour = eclipseTimeGenerator.contact4();

        // contact point one
        String contactOneLocalTime = convertLocalTime(contactOne.time);
        ((TextView) contactOneStub.findViewById(R.id.eclipse_event)).setText(contactOne.name);
        ((TextView) contactOneStub.findViewById(R.id.eclipse_time_local)).setText(contactOneLocalTime);
        ((TextView) contactOneStub.findViewById(R.id.eclipse_time_ut)).setText(contactOne.time);
        contactOneStub.setContentDescription(generateContentDescription(contactOne.name, contactOneLocalTime, contactOne.time));

        // contact point mid
        String contactMidLocalTime = convertLocalTime(contactMid.time);
        ((TextView) contactMidStub.findViewById(R.id.eclipse_event)).setText(contactMid.name);
        ((TextView) contactMidStub.findViewById(R.id.eclipse_time_local)).setText(contactMidLocalTime);
        ((TextView) contactMidStub.findViewById(R.id.eclipse_time_ut)).setText(contactMid.time);
        contactMidStub.setContentDescription(generateContentDescription(contactMid.name, contactMidLocalTime, contactMid.time));

        // contact point end
        String contactFinalLocalTime = convertLocalTime(contactFour.time);
        ((TextView) contactFourStub.findViewById(R.id.eclipse_event)).setText(contactFour.name);
        ((TextView) contactFourStub.findViewById(R.id.eclipse_time_local)).setText(contactFinalLocalTime);
        ((TextView) contactFourStub.findViewById(R.id.eclipse_time_ut)).setText(contactFour.time);
        contactFourStub.setContentDescription(generateContentDescription(contactFour.name, contactFinalLocalTime, contactFour.time));

        contactOneStub.setVisibility(View.VISIBLE);
        contactMidStub.setVisibility(View.VISIBLE);
        contactFourStub.setVisibility(View.VISIBLE);
    }

    public void generateFullContact(){
        EclipseTimeGenerator.EclipseEvent contactOne = eclipseTimeGenerator.contact1();
        EclipseTimeGenerator.EclipseEvent contactTwo = eclipseTimeGenerator.contact2();
        EclipseTimeGenerator.EclipseEvent contactMid = eclipseTimeGenerator.contactMid();
        EclipseTimeGenerator.EclipseEvent contactThree = eclipseTimeGenerator.contact3();
        EclipseTimeGenerator.EclipseEvent contactFour = eclipseTimeGenerator.contact4();

        // contact point one
        String contactOneLocalTime = convertLocalTime(contactOne.time);
        ((TextView) contactOneStub.findViewById(R.id.eclipse_event)).setText(contactOne.name);
        ((TextView) contactOneStub.findViewById(R.id.eclipse_time_local)).setText(contactOneLocalTime);
        ((TextView) contactOneStub.findViewById(R.id.eclipse_time_ut)).setText(contactOne.time);
        contactOneStub.setContentDescription(generateContentDescription(contactOne.name, contactOneLocalTime, contactOne.time));

        // contact point two
        String contactTwoLocalTime = convertLocalTime(contactTwo.time);
        ((TextView) contactTwoStub.findViewById(R.id.eclipse_event)).setText(contactTwo.name);
        ((TextView) contactTwoStub.findViewById(R.id.eclipse_time_local)).setText(contactTwoLocalTime);
        ((TextView) contactTwoStub.findViewById(R.id.eclipse_time_ut)).setText(contactTwo.time);
        contactTwoStub.setContentDescription(generateContentDescription(contactTwo.name, contactTwoLocalTime, contactTwo.time));

        // contact point mid
        String contactMidLocalTime = convertLocalTime(contactMid.time);
        ((TextView) contactMidStub.findViewById(R.id.eclipse_event)).setText(contactMid.name);
        ((TextView) contactMidStub.findViewById(R.id.eclipse_time_local)).setText(contactMidLocalTime);
        ((TextView) contactMidStub.findViewById(R.id.eclipse_time_ut)).setText(contactMid.time);
        contactMidStub.setContentDescription(generateContentDescription(contactMid.name, contactMidLocalTime, contactMid.time));

        // contact point three
        String contactThreeLocalTime = convertLocalTime(contactThree.time);
        ((TextView) contactThreeStub.findViewById(R.id.eclipse_event)).setText(contactThree.name);
        ((TextView) contactThreeStub.findViewById(R.id.eclipse_time_local)).setText(contactThreeLocalTime);
        ((TextView) contactThreeStub.findViewById(R.id.eclipse_time_ut)).setText(contactThree.time);
        contactThreeStub.setContentDescription(generateContentDescription(contactThree.name, contactThreeLocalTime, contactThree.time));

        // contact point end
        String contactFinalLocalTime = convertLocalTime(contactFour.time);
        ((TextView) contactFourStub.findViewById(R.id.eclipse_event)).setText(contactFour.name);
        ((TextView) contactFourStub.findViewById(R.id.eclipse_time_local)).setText(contactFinalLocalTime);
        ((TextView) contactFourStub.findViewById(R.id.eclipse_time_ut)).setText(contactFour.time);
        contactFourStub.setContentDescription(generateContentDescription(contactFour.name, contactFinalLocalTime, contactFour.time));

        contactOneStub.setVisibility(View.VISIBLE);
        contactTwoStub.setVisibility(View.VISIBLE);
        contactMidStub.setVisibility(View.VISIBLE);
        contactThreeStub.setVisibility(View.VISIBLE);
        contactFourStub.setVisibility(View.VISIBLE);
    }

    public String generateContentDescription(String event, String localTime, String uniTime){
        return getString(R.string.event_prefix).concat(",").concat(event)
                .concat(",").concat(getString(R.string.local_time_prefix)).concat(",").concat(localTime)
                .concat(",").concat(getString(R.string.ut_time_prefix)).concat(",").concat(uniTime);
    }

    public void startCountDown(String contactDate){

        Date date = null;
        try {
            date = dateFormat.parse(contactDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar start_calendar = Calendar.getInstance();
        long start_millis = start_calendar.getTimeInMillis(); //get the start time in milliseconds
        long end_millis = date.getTime(); //get the end time in milliseconds
        long total_millis = (end_millis - start_millis); //total time in milliseconds

        //1000 = 1 second interval
        CountDownTimer cdt = new CountDownTimer(total_millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                millisUntilFinished -= TimeUnit.DAYS.toMillis(days);
                if (days > 9) {
                    String[] daysArr = String.valueOf(days).split("");
                    daysPrimary.setText(String.valueOf(daysArr[1]));
                    daysSecondary.setText(String.valueOf(daysArr[2]));
                } else {
                    daysPrimary.setText(String.valueOf(0));
                    daysSecondary.setText(String.valueOf(days));
                }

                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);
                if (hours > 9) {
                    String[] hrsArr = String.valueOf(hours).split("");
                    hoursPrimary.setText(String.valueOf(hrsArr[1]));
                    hoursSecondary.setText(String.valueOf(hrsArr[2]));
                } else {
                    hoursPrimary.setText(String.valueOf(0));
                    hoursSecondary.setText(String.valueOf(hours));
                }

                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);
                if (minutes > 9) {
                    String[] minArr = String.valueOf(minutes).split("");
                    minPrimary.setText(String.valueOf(minArr[1]));
                    minSecondary.setText(String.valueOf(minArr[2]));
                } else {
                    minPrimary.setText(String.valueOf(0));
                    minSecondary.setText(String.valueOf(minutes));
                }

                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                if (seconds > 9) {
                    String[] secArr = String.valueOf(seconds).split("");
                    secPrimary.setText(String.valueOf(secArr[1]));
                    secSecondary.setText(String.valueOf(secArr[2]));
                } else {
                    secPrimary.setText(String.valueOf(0));
                    secSecondary.setText(String.valueOf(seconds));
                }
                String countdown_prefix = "";
                if(isAdded())
                    countdown_prefix = getString(R.string.countdown_prefix);

                String time = countdown_prefix.concat(", ").concat(daysPrimary.getText().toString().concat(daysSecondary.getText().toString())
                        .concat(" days, ").concat(hoursPrimary.getText().toString().concat(hoursSecondary.getText().toString()))
                        .concat(" hours, ").concat(minPrimary.getText().toString().concat(minSecondary.getText().toString()))
                        .concat(" minutes and ").concat(secPrimary.getText().toString().concat(secSecondary.getText().toString()))
                        .concat(" seconds "));
                countdownView.setContentDescription(time);
            }

            @Override
            public void onFinish() {

            }
        };
        cdt.start();
    }

    public String convertLocalTime(String time) {

        SimpleDateFormat dtf = new SimpleDateFormat("HH:mm:ss.S");
        dtf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = dtf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" h:mm:ss a", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        String myDate = null;
        myDate = simpleDateFormat.format(date);
        return  myDate;
    }


    public void showSearchingProgress(){
        progressView.setVisibility(View.VISIBLE);
    }

    public void hideSearchingProgress(){
        progressView.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        hideSearchingProgress();
        contactOneStub.setVisibility(View.GONE);
        contactTwoStub.setVisibility(View.GONE);
        contactMidStub.setVisibility(View.GONE);
        contactThreeStub.setVisibility(View.GONE);
        contactFourStub.setVisibility(View.GONE);
        if (gpsTracker != null)
            gpsTracker.stopUsingGPS();
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

}