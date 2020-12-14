package org.eclipsesoundscapes.data;

public class DataManager {

    private SharedPrefsHelper mSharedPrefsHelper;

    public DataManager(SharedPrefsHelper sharedPrefsHelper) {
        mSharedPrefsHelper = sharedPrefsHelper;
    }

    public void setFirstContact(String date){
        mSharedPrefsHelper.saveFirstContact(date);
    }

    public void setTotality(String date){
        mSharedPrefsHelper.saveTotality(date);
    }

    public void setSimulated(boolean simulated){
        mSharedPrefsHelper.saveSimulated(simulated);
    }

    public void setLocationAccess(boolean access){
        mSharedPrefsHelper.saveLocationAccess(access);
    }

    public void setRequestedLocation(boolean requested){
        mSharedPrefsHelper.saveRequestedLocation(requested);
    }

    public void setNotification(boolean canSendNotifications){
        mSharedPrefsHelper.saveNotification(canSendNotifications);
    }

    public void setWalkthroughComplete(boolean completed){
        mSharedPrefsHelper.saveWalkthroughComplete(completed);
    }


    public void setRumblingCheckpoint(String eclipseId, String coordinates){
        mSharedPrefsHelper.setCheckpoint(eclipseId, coordinates);
    }

    public String getFirstContact(){
        return mSharedPrefsHelper.getFirstContact();
    }

    public String getTotality(){
        return mSharedPrefsHelper.getTotality();
    }

    public String getRumblingCheckpoint(String eclipseId){
        return mSharedPrefsHelper.getCheckpoint(eclipseId);
    }

    public boolean getSimulated(){
        return mSharedPrefsHelper.getSimulated();
    }

    public boolean getWalkthroughComplete(){
        return mSharedPrefsHelper.getWalkthroughComplete();
    }

    public boolean getLocationAccess(){
        return mSharedPrefsHelper.getLocationAccess();
    }

    public boolean getRequestedLocation(){
        return mSharedPrefsHelper.getRequestedLocation();
    }

    public boolean getNotifications(){
        return mSharedPrefsHelper.getNotifications();
    }
}