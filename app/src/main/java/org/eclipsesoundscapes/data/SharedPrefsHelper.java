package org.eclipsesoundscapes.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import static android.content.Context.MODE_PRIVATE;

public class SharedPrefsHelper {

    private static final String ECLIPSE_PREFS = "ECLIPSE_PREFS";
    private final static String PREF_KEY_FIRST_CONTACT = "PREF_KEY_FIRST_CONTACT";
    private final static String PREF_KEY_TOTALITY = "PREF_KEY_TOTALITY";
    private final static String PREF_KEY_SIMULATED = "PREF_KEY_SIMULATED";

    private final static String PREF_KEY_LOCATION = "PREF_KEY_LOCATION";
    private final static String PREF_KEY_REQUESTED_LOCATION = "PREF_KEY_REQ_LOCATION";
    private final static String PREF_KEY_SKIPPED_LOCATION = "PREF_KEY_SKIPPED_LOCATION";

    private final static String PREF_KEY_REQUESTED_NOTIFICATIONS = "PREF_KEY_REQ_NOTIFICATIONS";
    private final static String PREF_KEY_SKIPPED_NOTIFICATIONS = "PREF_KEY_SKIPPED_NOTIFICATIONS";

    private final static String PREF_KEY_SKIPPED_ALARM = "PREF_KEY_SKIPPED_ALARM";

    private final static String PREF_KEY_NOTIFICATION = "PREF_KEY_NOTIFICATION";

    private final static String PREF_KEY_WALKTHROUGH = "PREF_KEY_WALKTHROUGH";
    private final static String PREF_KEY_LANGUAGE = "PREF_KEY_LANGUAGE";

    private final static String PREF_KEY_CURRENT_ECLIPSE = "PREF_KEY_CURRENT_ECLIPSE";

    private final static String PREF_KEY_LAST_LOCATION_LATITUDE = "PREF_KEY_LAST_LOCATION_LATITUDE";
    private final static String PREF_KEY_LAST_LOCATION_LONGITUDE = "PREF_KEY_LAST_LOCATION_LONGITUDE";

    private SharedPreferences mSharedPreferences;

    public SharedPrefsHelper(Context context) {
        mSharedPreferences = context.getSharedPreferences(ECLIPSE_PREFS, MODE_PRIVATE);
    }

    public void clear() {
        mSharedPreferences.edit().clear().apply();
    }

    public void saveFirstContact(String date){
        mSharedPreferences.edit().putString(PREF_KEY_FIRST_CONTACT, date).apply();
    }

    public void saveTotality(String date){
        mSharedPreferences.edit().putString(PREF_KEY_TOTALITY, date).apply();
    }

    public void setCheckpoint(String eclipseId, String coordinates){
        mSharedPreferences.edit().putString(eclipseId, coordinates).apply();
    }

    public void saveSimulated(boolean simulated){
        mSharedPreferences.edit().putBoolean(PREF_KEY_SIMULATED, simulated).apply();
    }

    public void saveLocationAccess(boolean access){
        mSharedPreferences.edit().putBoolean(PREF_KEY_LOCATION, access).apply();
    }

    public void saveRequestedLocation(boolean requested){
        mSharedPreferences.edit().putBoolean(PREF_KEY_REQUESTED_LOCATION, requested).apply();
    }

    public void saveSkippedLocationPermission(boolean requested){
        mSharedPreferences.edit().putBoolean(PREF_KEY_SKIPPED_LOCATION, requested).apply();
    }

    public void saveRequestedNotifications(boolean requested){
        mSharedPreferences.edit().putBoolean(PREF_KEY_REQUESTED_NOTIFICATIONS, requested).apply();
    }

    public void saveSkippedNotificationsPermission(boolean requested){
        mSharedPreferences.edit().putBoolean(PREF_KEY_SKIPPED_NOTIFICATIONS, requested).apply();
    }

    public void saveNotification(boolean canSendNotifications){
        mSharedPreferences.edit().putBoolean(PREF_KEY_NOTIFICATION, canSendNotifications).apply();
    }

    public void saveSkippedAlarmPermission(boolean requested){
        mSharedPreferences.edit().putBoolean(PREF_KEY_SKIPPED_ALARM, requested).apply();
    }

    public void saveWalkthroughComplete(boolean completed){
        mSharedPreferences.edit().putBoolean(PREF_KEY_WALKTHROUGH, completed).apply();
    }

    public void setPreferredLanguage(final String language) {
        mSharedPreferences.edit().putString(PREF_KEY_LANGUAGE, language).apply();
    }

    public void setEclipseDate(final String eclipseDate) {
        mSharedPreferences.edit().putString(PREF_KEY_CURRENT_ECLIPSE, eclipseDate).apply();
    }

    public void setLastLocation(final Location location) {
        if (location != null) {
            mSharedPreferences.edit().putFloat(PREF_KEY_LAST_LOCATION_LATITUDE, (float) location.getLatitude()).apply();
            mSharedPreferences.edit().putFloat(PREF_KEY_LAST_LOCATION_LONGITUDE, (float) location.getLongitude()).apply();
        }
    }

    public Location getLastLocation() {
        final float latitude = mSharedPreferences.getFloat(PREF_KEY_LAST_LOCATION_LATITUDE, -1.0f);
        final float longitude = mSharedPreferences.getFloat(PREF_KEY_LAST_LOCATION_LATITUDE, -1.0f);
        if (latitude == -1.0f || longitude == -1.0f) {
            return null;
        }

        final Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        return location;
    }

    public String getPreferredLanguage() {
        return mSharedPreferences.getString(PREF_KEY_LANGUAGE, "");
    }

    public String getFirstContact(){
        return mSharedPreferences.getString(PREF_KEY_FIRST_CONTACT, "");
    }

    public String getTotality(){
        return mSharedPreferences.getString(PREF_KEY_TOTALITY, "");
    }

    public String getCheckpoint(String eclipseId){
        return mSharedPreferences.getString(eclipseId, "");
    }

    public boolean getSimulated(){
        return mSharedPreferences.getBoolean(PREF_KEY_SIMULATED, false);
    }

    public boolean getRequestedLocation(){
        return mSharedPreferences.getBoolean(PREF_KEY_REQUESTED_LOCATION, false);
    }

    public boolean getSkippedLocationPermission(){
        return mSharedPreferences.getBoolean(PREF_KEY_SKIPPED_LOCATION, false);
    }

    public boolean getRequestedNotifications(){
        return mSharedPreferences.getBoolean(PREF_KEY_REQUESTED_NOTIFICATIONS, false);
    }

    public boolean getSkippedNotificationsPermission(){
        return mSharedPreferences.getBoolean(PREF_KEY_SKIPPED_NOTIFICATIONS, false);
    }

    public boolean getSkippedAlarmPermission(){
        return mSharedPreferences.getBoolean(PREF_KEY_SKIPPED_ALARM, false);
    }

    public boolean getLocationAccess(){
        return mSharedPreferences.getBoolean(PREF_KEY_LOCATION, true);
    }

    public boolean getNotifications(){
        return mSharedPreferences.getBoolean(PREF_KEY_NOTIFICATION, true);
    }

    public boolean getWalkthroughComplete(){
        return mSharedPreferences.getBoolean(PREF_KEY_WALKTHROUGH, false);
    }

    public String getEclipseDate(){
        return mSharedPreferences.getString(PREF_KEY_CURRENT_ECLIPSE, "");
    }
}