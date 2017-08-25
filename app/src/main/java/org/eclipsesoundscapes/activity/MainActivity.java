package org.eclipsesoundscapes.activity;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.eclipsesoundscapes.fragments.AboutFragment;
import org.eclipsesoundscapes.fragments.EclipseCenterFragment;
import org.eclipsesoundscapes.fragments.EclipseFeaturesFragment;
import org.eclipsesoundscapes.fragments.MediaFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
 * Hosts four fragments (EclipseCenter, EclipseFeatures, Mediaplayer,
 * About) through BottomNavigationView. Provides back stack navigation and handles
 * runtime permission
 */

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 47;
    private FragmentManager fragmentManager;
    private Fragment fragment;
    private Class fragmentClass;
    private BottomNavigationView navigation;
    private SharedPreferences preference;
    private SimpleDateFormat dateFormat;

    // Fragment support
    int currentCP = 1; // track current eclipse view in EclipseFeatures fragment
    Date firstContact;
    Date secondContact;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            Class fragmentClass;

            switch (item.getItemId()) {
                case org.eclipsesoundscapes.R.id.navigation_eclipse_features:
                    fragmentClass = EclipseFeaturesFragment.class;
                    break;

                case org.eclipsesoundscapes.R.id.navigation_eclipse_center:
                    fragmentClass = EclipseCenterFragment.class;
                    break;

                case org.eclipsesoundscapes.R.id.navigation_media:
                    fragmentClass = MediaFragment.class;
                    break;

                case org.eclipsesoundscapes.R.id.navigation_about:
                    fragmentClass = AboutFragment.class;
                    break;
                default:
                    fragmentClass = EclipseFeaturesFragment.class;
            }
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                replaceFragment(fragment);
                item.setChecked(true);
                setTitle(item.getTitle());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.eclipsesoundscapes.R.layout.activity_main);

        preference = PreferenceManager.getDefaultSharedPreferences(this);
        dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.S", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        // bottom navigation
        navigation = (BottomNavigationView) findViewById(org.eclipsesoundscapes.R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // display eclipse center by default
        loadEclipseCenter();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void loadEclipseCenter(){
        fragmentManager = getFragmentManager();
        fragmentManager.addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        // Update UI here.
                        updateUI();
                    }
                });
        try {
            Fragment eclipseCenterFragment = EclipseCenterFragment.class.newInstance();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(org.eclipsesoundscapes.R.id.navigation_content, eclipseCenterFragment, eclipseCenterFragment.getClass().getName());
            ft.addToBackStack( eclipseCenterFragment.getClass().getName());
            ft.commit();
            navigation.setSelectedItemId(org.eclipsesoundscapes.R.id.navigation_eclipse_center);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**************************************************************************
     * Back stack navigation and update UI
     *************************************************************************/
    private void updateUI() {
        Fragment fragmentAfterBackPress = getCurrentFragment();
        String fragTag = fragmentAfterBackPress.getTag();
        String[] fullPath = fragTag.split("\\.");
        String currentFrag = fullPath[fullPath.length - 1]; //get fragment name full path

        int id;
        switch (currentFrag) {
            case "EclipseFeaturesFragment":
                id = org.eclipsesoundscapes.R.id.navigation_eclipse_features;
                break;
            case "EclipseCenterFragment":
                id = org.eclipsesoundscapes.R.id.navigation_eclipse_center;
                break;
            case "MediaFragment":
                id = org.eclipsesoundscapes.R.id.navigation_media;
                break;
            case "AboutFragment":
                id = org.eclipsesoundscapes.R.id.navigation_about;
                break;
            default:
                id = org.eclipsesoundscapes.R.id.navigation_eclipse_features;
        }

        MenuItem menuItem = navigation.getMenu().findItem(id);
        if (!menuItem.isChecked()) {
            menuItem.setChecked(true);
            menuItem.setTitle(menuItem.getTitle());
        }
    }

    private Fragment getCurrentFragment() {
        if (fragmentManager.getBackStackEntryCount() != 0) {
            String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
            return fragmentManager.findFragmentByTag(fragmentTag);
        }
        return null;
    }

    /**
     * Replace current fragment
     * @param fragment
     */
    public void replaceFragment (Fragment fragment){
        String backStateName =  fragment.getClass().getName();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate (backStateName, 0);

        // fragment not in back stack - create
        if (!fragmentPopped && fragmentManager.findFragmentByTag(backStateName) == null){
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(org.eclipsesoundscapes.R.id.navigation_content, fragment, backStateName);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() == 1)
            finish();
        else if (fragmentManager.getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    /**************************************************************************
     * Fetch first contact and second contact
     *************************************************************************/

    public void setFirstContact(Date date){
        this.firstContact = date;
    }

    public void setSecondContact(Date date){
        this.secondContact = date;
    }

    public Date getFirstContact() {
        if (firstContact == null){
            String date = preference.getString("first_contact", "");
            if (!date.isEmpty()){
                Date contactDate;
                try {
                    contactDate = dateFormat.parse(date);
                    return contactDate;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return firstContact;
    }

    public Date getSecondContact() {
        if (secondContact == null){
            String date = preference.getString("second_contact", "");
            if (!date.isEmpty()){
                Date contactDate;
                try {
                    contactDate = dateFormat.parse(date);
                    return contactDate;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return secondContact;
    }

    public void setCurrentCP(int currentCP){
        this.currentCP = currentCP;
    }

    public int getCurrentCP(){
        return currentCP;
    }


    /**************************************************************************
     * Handle location permission
     *************************************************************************/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String backStateName =  EclipseCenterFragment.class.getName();
                EclipseCenterFragment fragment = (EclipseCenterFragment) getFragmentManager().findFragmentByTag(backStateName);
                if (fragment != null)
                    fragment.onPermissionResult();
            }
        } if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            String backStateName =  EclipseCenterFragment.class.getName();
            EclipseCenterFragment fragment = (EclipseCenterFragment) getFragmentManager().findFragmentByTag(backStateName);
            if (fragment != null) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    fragment.onPermissionDenied();
                } else
                    fragment.onPermissionNeverAsk();
            }
        }
    }
}
