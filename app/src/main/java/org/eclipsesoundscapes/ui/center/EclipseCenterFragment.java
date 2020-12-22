package org.eclipsesoundscapes.ui.center;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.data.DataManager;
import org.eclipsesoundscapes.data.EclipseSimulator;
import org.eclipsesoundscapes.data.EclipseTimeGenerator;
import org.eclipsesoundscapes.model.Event;
import org.eclipsesoundscapes.service.GPSTracker;
import org.eclipsesoundscapes.service.NotificationScheduler;
import org.eclipsesoundscapes.ui.about.SettingsActivity;
import org.eclipsesoundscapes.ui.main.MainActivity;
import org.eclipsesoundscapes.util.EclipseCenterHelper;
import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.eclipsesoundscapes.util.Constants.LOCATION_PERMISSION_REQUEST_CODE;
import static org.eclipsesoundscapes.util.Constants.SETTINGS_RESULT_CODE;


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
    public static final String TAG = "EclipseCenterFragment";

    private EclipseTimeGenerator eclipseTimeGenerator;
    private GPSTracker gpsTracker;
    private DataManager dataManager;
    private EclipseCenterHelper mHelper;
    private CountDownTimer countDownTimer;

    private Location simulatedLocation;

    @BindView(R.id.eclipse_countdown) RelativeLayout countdownView;
    @BindView(R.id.rl_eclipse_center) LinearLayout eclipseCenterView;
    @BindView(R.id.permission_view) LinearLayout permissionView;
    @BindView(R.id.gps_view) LinearLayout gpsView;
    @BindView(R.id.progressView) LinearLayout progressView;
    @BindView(R.id.simulation_view) View simulateView;
    @BindView(R.id.duration_totality_layout) LinearLayout totalityDurationLayout;

    @BindView(R.id.eclipse_type) TextView eclipseTypeView;
    @BindView(R.id.date) TextView dateView;
    @BindView(R.id.latitude) TextView latitudeView;
    @BindView(R.id.longitude) TextView longitudeView;
    @BindView(R.id.percent_eclipse) TextView percentEclipseView;

    @BindView(R.id.stub_contact_one) ConstraintLayout contactOneStub;
    @BindView(R.id.stub_contact_two) ConstraintLayout contactTwoStub;
    @BindView(R.id.stub_contact_mid) ConstraintLayout contactMidStub;
    @BindView(R.id.stub_contact_three) ConstraintLayout contactThreeStub;
    @BindView(R.id.stub_contact_four) ConstraintLayout contactFourStub;

    // count down view
    @BindView(R.id.days_primary) TextView daysPrimary;
    @BindView(R.id.days_secondary) TextView daysSecondary;
    @BindView(R.id.hours_primary) TextView hoursPrimary;
    @BindView(R.id.hours_secondary) TextView hoursSecondary;
    @BindView(R.id.minutes_primary) TextView minPrimary;
    @BindView(R.id.minutes_secondary) TextView minSecondary;
    @BindView(R.id.seconds_primary) TextView secPrimary;
    @BindView(R.id.seconds_secondary) TextView secSecondary;

    @OnClick(R.id.permission_view) void handlePermission(){
        if (getActivity() == null) return;

        if (!dataManager.getLocationAccess()){
            // user disabled access to notifications in app settings
            Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
            settingsIntent.putExtra(SettingsActivity.EXTRA_SETTINGS_MODE, SettingsActivity.MODE_SETTINGS);
            showSettingsDialog(getString(R.string.app_settings_permission), settingsIntent);
            return;
        }

        if (!dataManager.getRequestedLocation()){
            dataManager.setRequestedLocation(true);
            requestLocationPermission();
            return;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestLocationPermission();
        } else {

            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
            intent.setData(uri);
            showSettingsDialog(getString(R.string.device_settings_permission), intent);
        }
    }

    @OnClick(R.id.button_simulate) void onSimulate(){
        dataManager.setSimulated(true);
        simulateView.setVisibility(View.GONE);
        simulateEclipse();
    }

    public static EclipseCenterFragment newInstance() {
        return new EclipseCenterFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_eclipse_center, container, false);
        ButterKnife.bind(this, root);
        setTitles(root);
        return  root;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            dataManager = ((MainActivity)getActivity()).getDataManager();
            mHelper = new EclipseCenterHelper(getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        verifyLocationAccess();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (gpsTracker != null)
            gpsTracker.stopUsingGPS();
    }

    private void setTitles(final View rootView) {
        ((TextView) rootView.findViewById(R.id.eclipse_type_title)).setText(getString(R.string.eclipse_center_title_format,
                getString(R.string.eclipse_type)));

        ((TextView) rootView.findViewById(R.id.date_title)).setText(getString(R.string.eclipse_center_title_format,
                getString(R.string.date)));

        ((TextView) rootView.findViewById(R.id.latitude_title)).setText(getString(R.string.eclipse_center_title_format,
                getString(R.string.location_latitude)));

        ((TextView) rootView.findViewById(R.id.longitude_title)).setText(getString(R.string.eclipse_center_title_format,
                getString(R.string.location_longitude)));

        ((TextView) rootView.findViewById(R.id.percent_eclipse_title)).setText(getString(R.string.eclipse_center_title_format,
                getString(R.string.percent_eclipse)));

        ((TextView) rootView.findViewById(R.id.duration_totality_title)).setText(getString(R.string.eclipse_center_title_format,
                getString(R.string.totality_duration)));
    }

    /**************************************************************************************************
     * Location Access
     *************************************************************************************************/

    private void verifyLocationAccess() {
        if (getActivity() != null) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                showPermissionView(true);
            } else
                startLocationUpdates();
        }
    }

    private void requestLocationPermission(){
        if (getActivity() != null) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    // Re-direct user to device or app settings to enable location access
    private void showSettingsDialog(String message, final Intent intent){
        if (getActivity() == null || getActivity().isDestroyed() || getActivity().isFinishing()) return;

        final Context context = getActivity();
        new AlertDialog.Builder(context).setTitle(context.getString(R.string.permission_denied))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.open_settings), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivityForResult(intent, SETTINGS_RESULT_CODE);
                        getActivity().overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    @OnClick(R.id.gps_view)
    public void showLocationSettingsDialog(){
        if (getActivity() == null || getActivity().isDestroyed() || getActivity().isFinishing()) return;

        final Context context = getActivity();
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.location_disabled))
                .setMessage(context.getString(R.string.location_disabled_message))
                .setPositiveButton(context.getString(R.string.settings), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        getActivity().startActivity(intent);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gpsView.setVisibility(View.VISIBLE);
                        dialog.cancel();
                    }
                })
                .create()
                .show();
    }

    public void showPermissionView(boolean showView){
        if (showView){
            eclipseCenterView.setVisibility(View.GONE);
            permissionView.setVisibility(View.VISIBLE);
        } else {
            eclipseCenterView.setVisibility(View.VISIBLE);
            permissionView.setVisibility(View.GONE);
        }
    }

    /**
     * User has granted us permission, handled by parent activity
     * @see MainActivity
     */
    public void onPermissionGranted(){
        progressView.setVisibility(View.VISIBLE);
        startLocationUpdates();
    }

    /**
     * Start service to fetch users location and generateContact countdown based on that
     * @see GPSTracker
     */
    public void startLocationUpdates(){
        showPermissionView(false);

        if (getContext() == null) {
            return;
        }

        if (gpsTracker == null) {
            gpsTracker = new GPSTracker(getContext());
        }

        // get location
        if (gpsTracker.canGetLocation()) {
            gpsView.setVisibility(View.GONE);
            progressView.setVisibility(View.GONE);

            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();

            // generateContact first contact timing
            Location mLocation = new Location("");
            mLocation.setLatitude(latitude);
            mLocation.setLongitude(longitude);
            eclipseTimeGenerator = new EclipseTimeGenerator(getContext(), latitude, longitude);

            if (simulatedLocation == null){
                EclipseSimulator simulator = new EclipseSimulator(getContext());
                simulatedLocation = simulator.closestPointOnPath(mLocation);
            }

            updateView();
            generateContact();
            setNotificationAndCountDown();

        } else {
            gpsTracker.stopUsingGPS();
            gpsTracker = null;
            if (!gpsView.isShown()) {
                gpsView.setVisibility(View.VISIBLE);
                showLocationSettingsDialog();
            }
        }
    }


    private void setNotificationAndCountDown(){
        // only generateContact countdown and set notifications if eclipse type is at least partial
        if (eclipseTimeGenerator.type != EclipseTimeGenerator.EclipseType.NONE) {
            Event eclipseEvent = eclipseTimeGenerator.contact1();
            String date = eclipseEvent.date.concat(" ").concat(eclipseEvent.time);

            setupNotifications();
            startCountDown(date);
        }
    }

    /**************************************************************************************************
     * Generate data, populate view, start countdown
     **************************************************************************************************/

    public void generateContact(){
        switch (eclipseTimeGenerator.type){
            case NONE:
                boolean simulate = dataManager.getSimulated();
                if (!simulate)
                    simulateView.setVisibility(View.VISIBLE);
                else
                    simulateEclipse();
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
        if (simulatedLocation != null) {
            eclipseTimeGenerator = new EclipseTimeGenerator(getContext(), simulatedLocation.getLatitude(),
                    simulatedLocation.getLongitude());

            updateView();
            generateFullContact();
            setNotificationAndCountDown();
        }
    }

    private void updateView(){
        final Double lat = eclipseTimeGenerator.getLatitude();
        final Double lng = eclipseTimeGenerator.getLongitude();

        final String latStr = getString(R.string.lat_lng_format, lat,
                getString(lat > 0 ? R.string.north : R.string.south));
        final String lngStr = getString(R.string.lat_lng_format, lng,
                getString(lng > 0 ? R.string.east : R.string.west));

        latitudeView.setText(latStr);
        longitudeView.setText(lngStr);

        percentEclipseView.setText(eclipseTimeGenerator.getCoverage());

        // type of eclipse
        if (eclipseTimeGenerator.type == EclipseTimeGenerator.EclipseType.NONE){
            eclipseTypeView.setText(getString(R.string.eclipse_type_none));
        } else {
            dateView.setText(eclipseTimeGenerator.contact1().date);

            if (eclipseTimeGenerator.type == EclipseTimeGenerator.EclipseType.FULL)
                eclipseTypeView.setText(getString(R.string.eclipse_type_full));
            else if (eclipseTimeGenerator.type == EclipseTimeGenerator.EclipseType.PARTIAL)
                eclipseTypeView.setText(getString(
                        R.string.eclipse_type_partial));
        }
    }

    // generateContact view for contact point one - mid - end
    public void generatePartialContact(){
        Event contactOne = eclipseTimeGenerator.contact1();
        Event contactMid = eclipseTimeGenerator.contactMid();
        Event contactFour = eclipseTimeGenerator.contact4();

        // contact point one
        String contactOneLocalTime = mHelper.convertLocalTime(contactOne.time);
        ((TextView) contactOneStub.findViewById(R.id.eclipse_event)).setText(getString(R.string.eclipse_center_title_format, contactOne.name));
        ((TextView) contactOneStub.findViewById(R.id.eclipse_time_local)).setText(contactOneLocalTime);
        ((TextView) contactOneStub.findViewById(
                R.id.eclipse_time_ut)).setText(contactOne.time);
        contactOneStub.setContentDescription(mHelper.generateContentDescription(contactOne.name, contactOneLocalTime, contactOne.time));

        // contact point mid
        String contactMidLocalTime = mHelper.convertLocalTime(contactMid.time);
        ((TextView) contactMidStub.findViewById(R.id.eclipse_event)).setText(getString(R.string.eclipse_center_title_format, contactMid.name));
        ((TextView) contactMidStub.findViewById(
                R.id.eclipse_time_local)).setText(contactMidLocalTime);
        ((TextView) contactMidStub.findViewById(
                R.id.eclipse_time_ut)).setText(contactMid.time);
        contactMidStub.setContentDescription(mHelper.generateContentDescription(contactMid.name, contactMidLocalTime, contactMid.time));

        // contact point end
        String contactFinalLocalTime = mHelper.convertLocalTime(contactFour.time);
        ((TextView) contactFourStub.findViewById(
                R.id.eclipse_event)).setText(getString(R.string.eclipse_center_title_format, contactFour.name));
        ((TextView) contactFourStub.findViewById(
                R.id.eclipse_time_local)).setText(contactFinalLocalTime);
        ((TextView) contactFourStub.findViewById(
                R.id.eclipse_time_ut)).setText(contactFour.time);
        contactFourStub.setContentDescription(mHelper.generateContentDescription(contactFour.name, contactFinalLocalTime, contactFour.time));

        contactOneStub.setVisibility(View.VISIBLE);
        contactMidStub.setVisibility(View.VISIBLE);
        contactFourStub.setVisibility(View.VISIBLE);
    }

    public void generateFullContact(){
        totalityDurationLayout.setVisibility(View.VISIBLE);

        final TextView totalityDuration = totalityDurationLayout.findViewById(R.id.duration_totality);
        totalityDuration.setText(eclipseTimeGenerator.getFormattedDuration());
        totalityDuration.setContentDescription(getTotalityDurationDesc());

        // prevent redundancy, generatePartialContact will generateContact data for c1, mid, c4
        generatePartialContact();

        Event contactTwo = eclipseTimeGenerator.contact2();
        Event contactThree = eclipseTimeGenerator.contact3();

        // contact point two
        String contactTwoLocalTime = mHelper.convertLocalTime(contactTwo.time);
        ((TextView) contactTwoStub.findViewById(
                R.id.eclipse_event)).setText(getString(R.string.eclipse_center_title_format, contactTwo.name));
        ((TextView) contactTwoStub.findViewById(
                R.id.eclipse_time_local)).setText(contactTwoLocalTime);
        ((TextView) contactTwoStub.findViewById(
                R.id.eclipse_time_ut)).setText(contactTwo.time);
        contactTwoStub.setContentDescription(mHelper.generateContentDescription(contactTwo.name, contactTwoLocalTime, contactTwo.time));

        // contact point three
        String contactThreeLocalTime = mHelper.convertLocalTime(contactThree.time);
        ((TextView) contactThreeStub.findViewById(
                R.id.eclipse_event)).setText(getString(R.string.eclipse_center_title_format, contactThree.name));
        ((TextView) contactThreeStub.findViewById(
                R.id.eclipse_time_local)).setText(contactThreeLocalTime);
        ((TextView) contactThreeStub.findViewById(
                R.id.eclipse_time_ut)).setText(contactThree.time);
        contactThreeStub.setContentDescription(mHelper.generateContentDescription(contactThree.name, contactThreeLocalTime, contactThree.time));

        contactTwoStub.setVisibility(View.VISIBLE);
        contactThreeStub.setVisibility(View.VISIBLE);
    }

    private String getTotalityDurationDesc() {
        final DateTime dateTime = eclipseTimeGenerator.getDuration();
        final String seconds = String.format(Locale.getDefault(), "%d.%d", dateTime.getSecondOfMinute(), dateTime.getMillisOfSecond());
        return getString(R.string.duration_min_sec, String.valueOf(dateTime.getMinuteOfHour()), seconds);
    }

    /**
     * Start a count down from provided date and time
     * @param contactDate date generated until first contact from users location
     */
    public void startCountDown(String contactDate){
        Date date;
        DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.S", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            date = formatter.parse(contactDate);
            long start_millis = new Date().getTime();
            long end_millis = date.getTime();
            long total_millis = (end_millis - start_millis);

            long days = TimeUnit.MILLISECONDS.toDays(total_millis);
            if (days > 99) return;

            countdownView.setVisibility(View.VISIBLE);

            // count down by the second
            if (countDownTimer != null) countDownTimer.cancel();
            countDownTimer = new CountDownTimer(total_millis, 1000) {
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

                    if (isAdded()) {
                        final String countDownDescription = getString(R.string.countdown_format,
                                daysPrimary.getText().toString().concat(daysSecondary.getText().toString()),
                                hoursPrimary.getText().toString().concat(hoursSecondary.getText().toString()),
                                minPrimary.getText().toString().concat(minSecondary.getText().toString()),
                                secPrimary.getText().toString().concat(secSecondary.getText().toString()));

                        countdownView.setContentDescription(countDownDescription);
                    }
                }

                @Override
                public void onFinish() { }
            };

            countDownTimer.start();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**************************************************************************************************
     * Helpers
     *************************************************************************************************/

    public void setupNotifications(){

        // user has disabled notifications in the app settings
        if (!dataManager.getNotifications())
            return;

        if (getContext() == null) {
            return;
        }

        Event contactOne;
        Event totality;

        if (eclipseTimeGenerator.type == EclipseTimeGenerator.EclipseType.FULL){
            contactOne = eclipseTimeGenerator.contact1();
            totality = eclipseTimeGenerator.contact2();
        } else {
            // simulate contact point 2
            final EclipseTimeGenerator sGenerator =
                    new EclipseTimeGenerator(getContext(), simulatedLocation.getLatitude(), simulatedLocation.getLongitude());
            contactOne = eclipseTimeGenerator.contact1();
            totality = sGenerator.contact2();
        }

        final String contactTime = contactOne.date + " " + contactOne.time;
        final String totalityTime = totality.date + " " + totality.time;

        dataManager.setFirstContact(contactTime);
        dataManager.setTotality(totalityTime);

        if (getActivity() != null)
            NotificationScheduler.scheduleNotifications(getActivity(), contactTime, totalityTime);
    }
}