package org.eclipsesoundscapes.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.activity.SettingsActivity;
import org.eclipsesoundscapes.activity.MainActivity;
import org.eclipsesoundscapes.service.GPSTracker;
import org.eclipsesoundscapes.service.WakefulReceiver;
import org.eclipsesoundscapes.util.EclipseTimeGenerator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

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
 * Generates a countdown until eclipse and provides a list view of related information by
 * location.
 * @see EclipseTimeGenerator
 */

public class EclipseCenterFragment extends Fragment {

    public static int REQUEST_PERMISSION_SETTING = 48;
    Context mContext;
    EclipseTimeGenerator eclipseTimeGenerator;
    GPSTracker gpsTracker;
    SimpleDateFormat dateFormat;
    DialogFragment permissionDialogFragment;
    WakefulReceiver wakefulReceiver; // notifications
    SharedPreferences preference;

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
    private String latitudeFormat;
    private String longitudeFormat;

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
    private LinearLayout outsideView;
    private LinearLayout totalityDurationLayout;
    private Location closestLocation;

    public EclipseCenterFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.S");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        wakefulReceiver = new WakefulReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(org.eclipsesoundscapes.R.layout.fragment_eclipse_center, container, false);

        coordinatorLayout = (CoordinatorLayout) root.findViewById(org.eclipsesoundscapes.R.id.coordinator_layout);
        countdownView = (RelativeLayout) root.findViewById(org.eclipsesoundscapes.R.id.eclipse_countdown);
        eclipseCenterView = (LinearLayout) root.findViewById(org.eclipsesoundscapes.R.id.rl_eclipse_center);
        permissionView = (LinearLayout) root.findViewById(org.eclipsesoundscapes.R.id.permission_view);
        progressView = (LinearLayout) root.findViewById(org.eclipsesoundscapes.R.id.progressView);
        outsideView = (LinearLayout) root.findViewById(org.eclipsesoundscapes.R.id.outside_view);
        totalityDurationLayout = (LinearLayout) root.findViewById(org.eclipsesoundscapes.R.id.duration_totality_layout);

        eclipseTypeView = (TextView) root.findViewById(org.eclipsesoundscapes.R.id.eclipse_type);
        dateView = (TextView) root.findViewById(org.eclipsesoundscapes.R.id.date);
        latitudeView = (TextView) root.findViewById(org.eclipsesoundscapes.R.id.latitude);
        longitudeView = (TextView) root.findViewById(org.eclipsesoundscapes.R.id.longitude);
        percentEclipseView = (TextView) root.findViewById(org.eclipsesoundscapes.R.id.percent_eclipse);

        contactOneStub = (LinearLayout) root.findViewById(org.eclipsesoundscapes.R.id.stub_contact_one);
        contactTwoStub = (LinearLayout) root.findViewById(org.eclipsesoundscapes.R.id.stub_contact_two);
        contactMidStub = (LinearLayout) root.findViewById(org.eclipsesoundscapes.R.id.stub_contact_mid);
        contactThreeStub = (LinearLayout) root.findViewById(org.eclipsesoundscapes.R.id.stub_contact_three);
        contactFourStub = (LinearLayout) root.findViewById(org.eclipsesoundscapes.R.id.stub_contact_four);

        daysPrimary = (TextView)root.findViewById(org.eclipsesoundscapes.R.id.days_primary);
        daysSecondary = (TextView)root.findViewById(org.eclipsesoundscapes.R.id.days_secondary);
        hoursPrimary = (TextView)root.findViewById(org.eclipsesoundscapes.R.id.hours_primary);
        hoursSecondary = (TextView)root.findViewById(org.eclipsesoundscapes.R.id.hours_secondary);
        minPrimary = (TextView)root.findViewById(org.eclipsesoundscapes.R.id.minutes_primary);
        minSecondary = (TextView)root.findViewById(org.eclipsesoundscapes.R.id.minutes_secondary);
        secPrimary = (TextView)root.findViewById(org.eclipsesoundscapes.R.id.seconds_primary);
        secSecondary = (TextView)root.findViewById(org.eclipsesoundscapes.R.id.seconds_secondary);
        return  root;
    }

    @Override
    public void onStart() {
        super.onStart();
        mContext = getActivity();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        }
        preference = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        checkLocationPermission();
    }

    /**
     * Check if user has granted permission to access device's location. If not than ask for it
     */
    public void checkLocationPermission(){
        int rc = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
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
        } else if(!preference.getBoolean("settings_location", false)) {
            permissionView.setVisibility(View.VISIBLE);
            eclipseCenterView.setVisibility(View.GONE);
            TextView msg = (TextView) permissionView.findViewById(org.eclipsesoundscapes.R.id.permission_msg);
            msg.setText(getString(org.eclipsesoundscapes.R.string.app_settings_permission));
            final Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, getString(org.eclipsesoundscapes.R.string.settings_permission_off), Snackbar.LENGTH_INDEFINITE)
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
                    Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
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

    /**
     * User has granted us permission, handled by parent activity
     * @see MainActivity
     */
    public void onPermissionResult(){
        SharedPreferences.Editor editor = preference.edit();
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

    /**
     * User has denied us permission
     */
    public void onPermissionDenied(){
        if (permissionDialogFragment != null && permissionDialogFragment.getDialog() != null
                && permissionDialogFragment.getDialog().isShowing()) {
            permissionDialogFragment.dismiss();
        }
    }

    /**
     * User has denied us permission and checked "Never show again"
     */
    public void onPermissionNeverAsk(){
        if (permissionDialogFragment != null && permissionDialogFragment.getDialog() != null
                && permissionDialogFragment.getDialog().isShowing()) {
            permissionDialogFragment.dismiss();
        }

        // direct user to device's app settings to enable it
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Important")
                .setMessage("Permission denied. Please, go to settings and allow permission to access location")
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        permissionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
            }
        });

        builder.show();
    }

    /**
     * Start service to fetch users location and generate countdown based on that
     * @see GPSTracker
     */
    public void startLocationUpdates(){
        gpsTracker = new GPSTracker(getActivity());

        // get location
        if (gpsTracker.canGetLocation()) {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            hideSearchingProgress();
            latitudeFormat = String.format(Locale.getDefault(), "%.3f", Math.abs(latitude));
            longitudeFormat = String.format(Locale.getDefault(), "%.3f", Math.abs(longitude));

            // generate first contact timing
            Location location = new Location("");
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            closestLocation  = closestPointOnPath(location);
            eclipseTimeGenerator = new EclipseTimeGenerator(latitude, longitude);

            if (latitude > 0 )
                latitudeView.setText(String.valueOf(latitudeFormat).concat(" " .concat((char) 0x00B0 + " North")));
            else
                latitudeView.setText(String.valueOf(latitudeFormat).concat(" ".concat((char) 0x00B0 + " South")));

            if (longitude > 0)
                longitudeView.setText(String.valueOf(longitudeFormat).concat(" ".concat((char) 0x00B0 + " East")));
            else
                longitudeView.setText(String.valueOf(longitudeFormat).concat(" ".concat((char) 0x00B0 + " West")));

            percentEclipseView.setText(eclipseTimeGenerator.getcoverage());

            // partial or full?
            if (eclipseTimeGenerator.type == EclipseTimeGenerator.EclipseType.FULL)
                eclipseTypeView.setText(getString(org.eclipsesoundscapes.R.string.eclipse_type_full));
            else if (eclipseTimeGenerator.type == EclipseTimeGenerator.EclipseType.PARTIAL)
                eclipseTypeView.setText(getString(org.eclipsesoundscapes.R.string.eclipse_type_partial));
            else
                eclipseTypeView.setText(getString(org.eclipsesoundscapes.R.string.eclipse_type_none));

            populateEvents();

            // only generate countdown and set notifications if eclipse type is at least partial
            if (eclipseTimeGenerator.type != EclipseTimeGenerator.EclipseType.NONE) {
                EclipseTimeGenerator.EclipseEvent eclipseEvent = eclipseTimeGenerator.contact1();
                dateView.setText(eclipseEvent.date);

                // countdown until first contact
                String date = eclipseEvent.date.concat(" ").concat(eclipseEvent.time);
                setupNotifications();
                startCountDown(date);
            }
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    /**
     * populate view with information about eclipse events based on users location
     * such as eclipse type, date, time etc..
     */
    public void populateEvents(){
        switch (eclipseTimeGenerator.type){
            case NONE:
                boolean simulate = preference.getBoolean("simulateEclipse", false);
                if (!simulate){
                    outsideView.setVisibility(View.VISIBLE);
                    Button simulateButton = (Button) outsideView.findViewById(org.eclipsesoundscapes.R.id.button_simulate);
                    simulateButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SharedPreferences.Editor editor = preference.edit();
                            editor.putBoolean("simulateEclipse", true);
                            editor.apply();
                            simulateEclipse();
                            generateFullContact();
                            outsideView.setVisibility(View.GONE);
                        }
                    });
                } else {
                    simulateEclipse();
                    generateFullContact();
                }
                break;
            case PARTIAL:
                generatePartialContact();
                break;
            case FULL:
                generateFullContact();
                break;
        }
    }

    /**
     * User is not in the path of eclipse, simulate count down and details from nearest point
     * in the path of totality
     */
    public void simulateEclipse(){
        Double lat = closestLocation.getLatitude();
        Double lng = closestLocation.getLongitude();
        latitudeFormat = String.format(Locale.getDefault(), "%.3f", Math.abs(lat));
        longitudeFormat = String.format(Locale.getDefault(), "%.3f", Math.abs(lng));
        eclipseTimeGenerator = new EclipseTimeGenerator(lat, lng);


        if (lat > 0 )
            latitudeView.setText(String.valueOf(latitudeFormat).concat(" " .concat((char) 0x00B0 + " North")));
        else
            latitudeView.setText(String.valueOf(latitudeFormat).concat(" ".concat((char) 0x00B0 + " South")));

        if (lng > 0)
            longitudeView.setText(String.valueOf(longitudeFormat).concat(" ".concat((char) 0x00B0 + " East")));
        else
            longitudeView.setText(String.valueOf(longitudeFormat).concat(" ".concat((char) 0x00B0 + " West")));

        percentEclipseView.setText(eclipseTimeGenerator.getcoverage());

        // partial or full?
        if (eclipseTimeGenerator.type == EclipseTimeGenerator.EclipseType.FULL)
            eclipseTypeView.setText(getString(org.eclipsesoundscapes.R.string.eclipse_type_full));
        else if (eclipseTimeGenerator.type == EclipseTimeGenerator.EclipseType.PARTIAL)
            eclipseTypeView.setText(getString(org.eclipsesoundscapes.R.string.eclipse_type_partial));
        else
            eclipseTypeView.setText(getString(org.eclipsesoundscapes.R.string.eclipse_type_none));


    }

    // generate view for contact point one - mid - end
    public void generatePartialContact(){
        EclipseTimeGenerator.EclipseEvent contactOne = eclipseTimeGenerator.contact1();
        EclipseTimeGenerator.EclipseEvent contactMid = eclipseTimeGenerator.contactMid();
        EclipseTimeGenerator.EclipseEvent contactFour = eclipseTimeGenerator.contact4();

        // contact point one
        String contactOneLocalTime = convertLocalTime(contactOne.time);
        ((TextView) contactOneStub.findViewById(org.eclipsesoundscapes.R.id.eclipse_event)).setText(contactOne.name);
        ((TextView) contactOneStub.findViewById(org.eclipsesoundscapes.R.id.eclipse_time_local)).setText(contactOneLocalTime);
        ((TextView) contactOneStub.findViewById(org.eclipsesoundscapes.R.id.eclipse_time_ut)).setText(contactOne.time);
        contactOneStub.setContentDescription(generateContentDescription(contactOne.name, contactOneLocalTime, contactOne.time));

        // contact point mid
        String contactMidLocalTime = convertLocalTime(contactMid.time);
        ((TextView) contactMidStub.findViewById(org.eclipsesoundscapes.R.id.eclipse_event)).setText(contactMid.name);
        ((TextView) contactMidStub.findViewById(org.eclipsesoundscapes.R.id.eclipse_time_local)).setText(contactMidLocalTime);
        ((TextView) contactMidStub.findViewById(org.eclipsesoundscapes.R.id.eclipse_time_ut)).setText(contactMid.time);
        contactMidStub.setContentDescription(generateContentDescription(contactMid.name, contactMidLocalTime, contactMid.time));

        // contact point end
        String contactFinalLocalTime = convertLocalTime(contactFour.time);
        ((TextView) contactFourStub.findViewById(org.eclipsesoundscapes.R.id.eclipse_event)).setText(contactFour.name);
        ((TextView) contactFourStub.findViewById(org.eclipsesoundscapes.R.id.eclipse_time_local)).setText(contactFinalLocalTime);
        ((TextView) contactFourStub.findViewById(org.eclipsesoundscapes.R.id.eclipse_time_ut)).setText(contactFour.time);
        contactFourStub.setContentDescription(generateContentDescription(contactFour.name, contactFinalLocalTime, contactFour.time));

        contactOneStub.setVisibility(View.VISIBLE);
        contactMidStub.setVisibility(View.VISIBLE);
        contactFourStub.setVisibility(View.VISIBLE);
    }

    public void generateFullContact(){

        totalityDurationLayout.setVisibility(View.VISIBLE);
        TextView durationTitle = (TextView) totalityDurationLayout.findViewById(org.eclipsesoundscapes.R.id.duration_totality_title);
        TextView duration = (TextView) totalityDurationLayout.findViewById(org.eclipsesoundscapes.R.id.duration_totality);
        durationTitle.setText(getString(org.eclipsesoundscapes.R.string.totality_duration));
        duration.setText(eclipseTimeGenerator.getduration());

        EclipseTimeGenerator.EclipseEvent contactOne = eclipseTimeGenerator.contact1();
        EclipseTimeGenerator.EclipseEvent contactTwo = eclipseTimeGenerator.contact2();
        EclipseTimeGenerator.EclipseEvent contactMid = eclipseTimeGenerator.contactMid();
        EclipseTimeGenerator.EclipseEvent contactThree = eclipseTimeGenerator.contact3();
        EclipseTimeGenerator.EclipseEvent contactFour = eclipseTimeGenerator.contact4();

        // contact point one
        String contactOneLocalTime = convertLocalTime(contactOne.time);
        ((TextView) contactOneStub.findViewById(org.eclipsesoundscapes.R.id.eclipse_event)).setText(contactOne.name);
        ((TextView) contactOneStub.findViewById(org.eclipsesoundscapes.R.id.eclipse_time_local)).setText(contactOneLocalTime);
        ((TextView) contactOneStub.findViewById(org.eclipsesoundscapes.R.id.eclipse_time_ut)).setText(contactOne.time);
        contactOneStub.setContentDescription(generateContentDescription(contactOne.name, contactOneLocalTime, contactOne.time));

        // contact point two
        String contactTwoLocalTime = convertLocalTime(contactTwo.time);
        ((TextView) contactTwoStub.findViewById(org.eclipsesoundscapes.R.id.eclipse_event)).setText(contactTwo.name);
        ((TextView) contactTwoStub.findViewById(org.eclipsesoundscapes.R.id.eclipse_time_local)).setText(contactTwoLocalTime);
        ((TextView) contactTwoStub.findViewById(org.eclipsesoundscapes.R.id.eclipse_time_ut)).setText(contactTwo.time);
        contactTwoStub.setContentDescription(generateContentDescription(contactTwo.name, contactTwoLocalTime, contactTwo.time));

        // contact point mid
        String contactMidLocalTime = convertLocalTime(contactMid.time);
        ((TextView) contactMidStub.findViewById(org.eclipsesoundscapes.R.id.eclipse_event)).setText(contactMid.name);
        ((TextView) contactMidStub.findViewById(org.eclipsesoundscapes.R.id.eclipse_time_local)).setText(contactMidLocalTime);
        ((TextView) contactMidStub.findViewById(org.eclipsesoundscapes.R.id.eclipse_time_ut)).setText(contactMid.time);
        contactMidStub.setContentDescription(generateContentDescription(contactMid.name, contactMidLocalTime, contactMid.time));

        // contact point three
        String contactThreeLocalTime = convertLocalTime(contactThree.time);
        ((TextView) contactThreeStub.findViewById(org.eclipsesoundscapes.R.id.eclipse_event)).setText(contactThree.name);
        ((TextView) contactThreeStub.findViewById(org.eclipsesoundscapes.R.id.eclipse_time_local)).setText(contactThreeLocalTime);
        ((TextView) contactThreeStub.findViewById(org.eclipsesoundscapes.R.id.eclipse_time_ut)).setText(contactThree.time);
        contactThreeStub.setContentDescription(generateContentDescription(contactThree.name, contactThreeLocalTime, contactThree.time));

        // contact point end
        String contactFinalLocalTime = convertLocalTime(contactFour.time);
        ((TextView) contactFourStub.findViewById(org.eclipsesoundscapes.R.id.eclipse_event)).setText(contactFour.name);
        ((TextView) contactFourStub.findViewById(org.eclipsesoundscapes.R.id.eclipse_time_local)).setText(contactFinalLocalTime);
        ((TextView) contactFourStub.findViewById(org.eclipsesoundscapes.R.id.eclipse_time_ut)).setText(contactFour.time);
        contactFourStub.setContentDescription(generateContentDescription(contactFour.name, contactFinalLocalTime, contactFour.time));

        contactOneStub.setVisibility(View.VISIBLE);
        contactTwoStub.setVisibility(View.VISIBLE);
        contactMidStub.setVisibility(View.VISIBLE);
        contactThreeStub.setVisibility(View.VISIBLE);
        contactFourStub.setVisibility(View.VISIBLE);
    }

    /**
     *
     * @param event type of visible eclipse (none, partial or full)
     * @param localTime local time of first contact
     * @param uniTime universal time of first contact
     */
    public String generateContentDescription(String event, String localTime, String uniTime){
        return getString(org.eclipsesoundscapes.R.string.event_prefix).concat(",").concat(event)
                .concat(",").concat(getString(org.eclipsesoundscapes.R.string.local_time_prefix)).concat(",").concat(localTime)
                .concat(",").concat(getString(org.eclipsesoundscapes.R.string.ut_time_prefix)).concat(",").concat(uniTime);
    }

    /**
     * Start a count down from provided date and time
     * @param contactDate date generated until first contact from users location
     */
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
                    countdown_prefix = getString(org.eclipsesoundscapes.R.string.countdown_prefix);

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

    /**
     * Convert universal time to local time, takes into account daylight sacings
     * @param time date/time for conversion
     */
    public String convertLocalTime(String time) {
        float ONE_HOUR_MILLIS = 60 * 60 * 1000;

        SimpleDateFormat dtf = new SimpleDateFormat("HH:mm:ss.S");
        dtf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = dtf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TimeZone timeZone = TimeZone.getDefault();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" h:mm:ss a", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        String myDate = null;
        myDate = simpleDateFormat.format(date);

        // Daylight Saving time
        if (timeZone.useDaylightTime()) {
            float dstOffset = timeZone.getDSTSavings() / ONE_HOUR_MILLIS;
            if (timeZone.inDaylightTime(new Date())) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.HOUR, (int) dstOffset);
                return simpleDateFormat.format(calendar.getTime());
            } else
                return myDate;
        } else
            return  myDate;
    }

    /**
     * Set up notification for first and second contact
     * @see WakefulReceiver
     * @see org.eclipsesoundscapes.service.BootReceiver
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setupNotifications(){

        // cancel previous notification
        wakefulReceiver.cancelFirstContactAlarm(mContext);
        wakefulReceiver.cancelTotalityAlarm(mContext);

        if (eclipseTimeGenerator.type == EclipseTimeGenerator.EclipseType.PARTIAL || eclipseTimeGenerator.type == EclipseTimeGenerator.EclipseType.NONE){
            EclipseTimeGenerator eclipseTimeGenerator2 = new EclipseTimeGenerator(closestLocation.getLatitude(), closestLocation.getLongitude());
            createNotification(eclipseTimeGenerator2.contact1(), eclipseTimeGenerator2.contact2());
        } else
            createNotification(eclipseTimeGenerator.contact1(), eclipseTimeGenerator.contact2());
    }

    /**
     * Create notification based on date of first and second contact relative to users location
     * @param contactOne First contact event
     * @param contactTwo Second contact event
     */
    public void createNotification(EclipseTimeGenerator.EclipseEvent contactOne, EclipseTimeGenerator.EclipseEvent contactTwo){

        String dateTime = contactOne.date + " " + contactOne.time;
        String dateTimeTwo = contactTwo.date + " " + contactTwo.time;
        SharedPreferences.Editor editor = preference.edit();

        editor.putString("first_contact", dateTime);
        editor.putString("second_contact", dateTimeTwo);
        editor.apply();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.S", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = null;
        Date date2 = null;
        try {
            date = dateFormat.parse(dateTime);
            date2 = dateFormat.parse(dateTimeTwo);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
            calendar.setTime(date);
            ((MainActivity)mContext).setFirstContact(date);
            wakefulReceiver.setFirstContactAlarm(mContext, calendar);
        }

        if (date2 != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
            calendar.setTime(date2);
            ((MainActivity)mContext).setSecondContact(date2);
            wakefulReceiver.setTotalityAlarm(mContext, calendar);
        }
    }

    public void showSearchingProgress(){
        progressView.setVisibility(View.VISIBLE);
    }

    public void hideSearchingProgress(){
        progressView.setVisibility(View.GONE);
    }

    /**
     * Parse json into a String
     */
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = null;
            try {
                is = getActivity().getAssets().open("maineclipsepolyline.json");
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
     * Create a map of points in the path of totality from String parsed Json including lat, lng
     * Used to simulate eclipse event
     */
    public HashMap<Double, Double> parseJson(){
        HashMap<Double, Double> locations = new HashMap<>();
        JSONArray jsonArray = null;
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
        HashMap<Double, Double> locations = parseJson();

        Location shortestLocation = null;
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