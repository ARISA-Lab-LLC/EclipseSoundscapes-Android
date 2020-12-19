package org.eclipsesoundscapes.ui.main;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import org.eclipsesoundscapes.EclipseSoundscapesApp;
import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.data.DataManager;
import org.eclipsesoundscapes.ui.about.AboutFragment;
import org.eclipsesoundscapes.ui.base.BaseActivity;
import org.eclipsesoundscapes.ui.center.EclipseCenterFragment;
import org.eclipsesoundscapes.ui.features.EclipseFeaturesFragment;
import org.eclipsesoundscapes.ui.media.MediaFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.eclipsesoundscapes.util.Constants.LOCATION_PERMISSION_REQUEST_CODE;

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

public class MainActivity extends BaseActivity {
    public static final String EXTRA_FRAGMENT_TAG = "extra_fragment_tag";

    private FragmentManager fragmentManager;
    private BottomNavigationView navigation;
    private DataManager dataManager;

    // track current eclipseImageView view in EclipseFeatures fragment
    int currentView = 1;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            Class fragmentClass;

            switch (item.getItemId()) {
                case R.id.navigation_eclipse_features:
                    fragmentClass = EclipseFeaturesFragment.class;
                    break;

                case R.id.navigation_eclipse_center:
                    fragmentClass = EclipseCenterFragment.class;
                    break;

                case R.id.navigation_media:
                    fragmentClass = MediaFragment.class;
                    break;

                case R.id.navigation_about:
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataManager = ((EclipseSoundscapesApp) getApplication()).getDataManager();

        // bottom navigation
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                updateUI();
            }
        });

        if (getIntent() != null && getIntent().hasExtra(EXTRA_FRAGMENT_TAG)) {
            final String fragmentTag = getIntent().getStringExtra(EXTRA_FRAGMENT_TAG);
            showFragment(fragmentTag);
        } else {
            showFragment(EclipseCenterFragment.TAG);
        }
    }

    private void showFragment(final String tag) {
        final Fragment fragment = getFragment(tag);
        final Integer navigationItemId = getNavigationItemId(tag);

        if (fragment != null && navigationItemId != null) {
            final FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.navigation_content, fragment, tag);
            ft.addToBackStack(tag);
            ft.commit();
            navigation.setSelectedItemId(navigationItemId);
        }
    }

    @Nullable
    private Fragment getFragment(final String tag) {
        switch (tag) {
            case EclipseCenterFragment.TAG:
                return EclipseCenterFragment.newInstance();
            case EclipseFeaturesFragment.TAG:
                return EclipseFeaturesFragment.newInstance();
            case MediaFragment.TAG:
                return MediaFragment.newInstance();
            case AboutFragment.TAG:
                return AboutFragment.newInstance();
        }

        return null;
    }

    public Integer getNavigationItemId(final String tag) {
        switch (tag) {
            case EclipseCenterFragment.TAG:
                return R.id.navigation_eclipse_center;
            case EclipseFeaturesFragment.TAG:
                return R.id.navigation_eclipse_features;
            case MediaFragment.TAG:
                return R.id.navigation_media;
            case AboutFragment.TAG:
                return R.id.navigation_about;
        }

        return null;
    }

    /**************************************************************************
     * Back stack navigation and update UI
     *************************************************************************/
    private void updateUI() {
        Fragment fragmentAfterBackPress = getCurrentFragment();
        if (fragmentAfterBackPress == null) return;

        String fragTag = fragmentAfterBackPress.getTag();
        if (fragTag == null) return;

        String[] fullPath = fragTag.split("\\.");
        String currentFrag = fullPath[fullPath.length - 1];

        int id;
        switch (currentFrag) {
            case EclipseFeaturesFragment.TAG:
                id = R.id.navigation_eclipse_features;
                break;
            case EclipseCenterFragment.TAG:
                id = R.id.navigation_eclipse_center;
                break;
            case MediaFragment.TAG:
                id = R.id.navigation_media;
                break;
            case AboutFragment.TAG:
                id = R.id.navigation_about;
                break;
            default:
                id = R.id.navigation_eclipse_features;
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
     * @param fragment next fragment to be shown and added to the back stack
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

    public boolean isAfterFirstContact(){
        final String date = dataManager.getFirstContact();
        if (date.isEmpty()) return false;

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.S", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            final Date contact = dateFormat.parse(date);
            final Date current = new Date();
            return current.equals(contact) || current.after(contact);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isAfterTotality(){
        final String date = dataManager.getTotality();
        if (date.isEmpty()) return false;

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.S", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            final Date totality = dateFormat.parse(date);
            final Date current = new Date();
            return current.equals(totality) || current.after(totality);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     *
     * @param currentView set current view in eclipseImageView features fragment
     */
    public void setCurrentView(int currentView){
        this.currentView = currentView;
    }

    public int getCurrentView(){
        return currentView;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    /**************************************************************************
     * Handle location permission for Eclipse Center
     *************************************************************************/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE){
            String backStateName =  EclipseCenterFragment.class.getName();
            EclipseCenterFragment fragment = (EclipseCenterFragment) getSupportFragmentManager().findFragmentByTag(backStateName);

            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (fragment != null)
                    fragment.onPermissionGranted();
            } else {
                if (fragment != null) {
                    fragment.showPermissionView(true);
                }
            }
        }
    }
}
