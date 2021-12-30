package org.eclipsesoundscapes.data;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;

public class DataManager {

    private SharedPrefsHelper sharedPrefsHelper;

    private Pair<Integer, Integer> featuresPosition;

    public DataManager(SharedPrefsHelper sharedPrefsHelper) {
        this.sharedPrefsHelper = sharedPrefsHelper;
    }

    public void setFirstContact(String date){
        sharedPrefsHelper.saveFirstContact(date);
    }

    public void setTotality(String date){
        sharedPrefsHelper.saveTotality(date);
    }

    public void setSimulated(boolean simulated){
        sharedPrefsHelper.saveSimulated(simulated);
    }

    public void setLocationAccess(boolean access){
        sharedPrefsHelper.saveLocationAccess(access);
    }

    public void setRequestedLocation(boolean requested){
        sharedPrefsHelper.saveRequestedLocation(requested);
    }

    public void setNotification(boolean canSendNotifications){
        sharedPrefsHelper.saveNotification(canSendNotifications);
    }

    public void setWalkthroughComplete(boolean completed){
        sharedPrefsHelper.saveWalkthroughComplete(completed);
    }

    public void setLanguage(final String language) {
        sharedPrefsHelper.setPreferredLanguage(language);
    }

    public String getLanguage() {
        return sharedPrefsHelper.getPreferredLanguage();
    }

    public void setRumblingCheckpoint(String eclipseId, String coordinates){
        sharedPrefsHelper.setCheckpoint(eclipseId, coordinates);
    }

    public String getFirstContact(){
        return sharedPrefsHelper.getFirstContact();
    }

    public String getTotality(){
        return sharedPrefsHelper.getTotality();
    }

    public String getRumblingCheckpoint(String eclipseId){
        return sharedPrefsHelper.getCheckpoint(eclipseId);
    }

    public boolean getSimulated(){
        return sharedPrefsHelper.getSimulated();
    }

    public boolean getWalkthroughComplete(){
        return sharedPrefsHelper.getWalkthroughComplete();
    }

    public boolean getLocationAccess(){
        return sharedPrefsHelper.getLocationAccess();
    }

    public boolean getRequestedLocation(){
        return sharedPrefsHelper.getRequestedLocation();
    }

    public boolean getNotifications(){
        return sharedPrefsHelper.getNotifications();
    }

    public void saveFeaturesPosition(int page, int tab) {
        featuresPosition = new Pair(page, tab);
    }

    @Nullable
    public Pair<Integer, Integer> getFeaturesPosition() {
        return featuresPosition;
    }
}