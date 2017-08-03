package org.eclipsesoundscapes.eclipsesoundscapes.activity;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.eclipsesoundscapes.eclipsesoundscapes.R;
import org.eclipsesoundscapes.eclipsesoundscapes.fragments.AboutFragment;
import org.eclipsesoundscapes.eclipsesoundscapes.fragments.EclipseCenterFragment;
import org.eclipsesoundscapes.eclipsesoundscapes.fragments.EclipseFeaturesFragment;
import org.eclipsesoundscapes.eclipsesoundscapes.util.BottomNavigationViewHelper;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 47;
    private FragmentManager fragmentManager;
    Fragment fragment;
    Class fragmentClass;
    BottomNavigationView navigation;

    // fragment support
    int currentCP; // current contact point

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            Class fragmentClass;

            switch (item.getItemId()) {
                case R.id.navigation_eclipse_center:
                    fragmentClass = EclipseCenterFragment.class;
                    break;
                case R.id.navigation_eclipse_features:
                    fragmentClass = EclipseFeaturesFragment.class;
                    break;
                /*
                case R.id.navigation_media:
                    fragmentClass = MediaFragment.class;
                    break;
                    */
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.addShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        currentCP = 1;

        // display rumble map by default
        loadEclipseCenter();
    }

    public void replaceFragment (Fragment fragment){
        String backStateName =  fragment.getClass().getName();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate (backStateName, 0);

        // fragment not in back stack - create
        if (!fragmentPopped && fragmentManager.findFragmentByTag(backStateName) == null){
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.navigation_content, fragment, backStateName);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void loadEclipseCenter(){
        fragmentManager = getFragmentManager();
        fragmentManager.addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        // Update your UI here.
                        updateUI();
                    }
                });
        try {
            Fragment eclipseCenterFragment = EclipseCenterFragment.class.newInstance();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.navigation_content, eclipseCenterFragment, eclipseCenterFragment.getClass().getName());
            ft.addToBackStack( eclipseCenterFragment.getClass().getName());
            ft.commit();
            navigation.setSelectedItemId(R.id.navigation_eclipse_center);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /*
     * Methods below provide back stack navigation and update UI accordingly
     */

    private void updateUI() {
        Fragment fragmentAfterBackPress = getCurrentFragment();
        String fragTag = fragmentAfterBackPress.getTag();
        String[] fullPath = fragTag.split("\\.");
        String currentFrag = fullPath[fullPath.length - 1]; //get fragment name full path

        int id;
        switch (currentFrag) {
            case "EclipseFeaturesFragment":
                id = R.id.navigation_eclipse_features;
                break;
            case "EclipseCenterFragment":
                id = R.id.navigation_eclipse_center;
                break;
            //case "MediaFragment":
             //   id = R.id.navigation_media;
              //  break;
            case "AboutFragment":
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

    public void setCurrentCP(int currentCP){
        this.currentCP = currentCP;
    }

    public int getCurrentCP(){
        return currentCP;
    }

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
            if (fragment != null)
                fragment.onPermissionDenied();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //currentCP = 1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
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

}
