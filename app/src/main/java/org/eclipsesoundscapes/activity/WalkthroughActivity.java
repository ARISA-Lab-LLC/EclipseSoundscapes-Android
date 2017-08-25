package org.eclipsesoundscapes.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import org.eclipsesoundscapes.fragments.WalkthroughFragment;
import org.eclipsesoundscapes.util.Constants;

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
 * Create a walk through with view pager
 * Displayed during applications first launch or launched from {@link org.eclipsesoundscapes.fragments.AboutFragment}
 * @see WalkthroughFragment
 */

public class WalkthroughActivity extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private SharedPreferences sharedPreferences;
    private String mode; // either initial launch or from AboutFragment

    // views
    private ViewPager mViewPager;
    private Button skipCloseButton; // skips or closes walk through depending on mode
    private ImageButton nextButton;
    private ImageButton prevButton;

    // walk through fragment layouts for first time launch
    private static final int[] LAYOUT_RES_IDS = {
            org.eclipsesoundscapes.R.layout.layout_walkthrough_one, org.eclipsesoundscapes.R.layout.layout_walkthrough_two, org.eclipsesoundscapes.R.layout.layout_walkthrough_three,
            org.eclipsesoundscapes.R.layout.layout_walkthrough_four, org.eclipsesoundscapes.R.layout.layout_walkthrough_five};

    // walk through fragment layouts launched from AboutFragment, omits permission
    private static final int[] LAYOUT_RES_MENU = {
            org.eclipsesoundscapes.R.layout.layout_walkthrough_one, org.eclipsesoundscapes.R.layout.layout_walkthrough_two, org.eclipsesoundscapes.R.layout.layout_walkthrough_three,
            org.eclipsesoundscapes.R.layout.layout_walkthrough_four};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.eclipsesoundscapes.R.layout.activity_walkthrough);
        context = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mode = getIntent().getStringExtra("mode");

        // views
        mViewPager = (ViewPager) findViewById(org.eclipsesoundscapes.R.id.viewpager);
        skipCloseButton = (Button) findViewById(org.eclipsesoundscapes.R.id.skip_close_button);
        if (isMenuMode()) {
            skipCloseButton.setText(getString(org.eclipsesoundscapes.R.string.closeButton));
            skipCloseButton.setContentDescription(getString(org.eclipsesoundscapes.R.string.closeButton));
        }
        nextButton = (ImageButton) findViewById(org.eclipsesoundscapes.R.id.next_button);
        prevButton = (ImageButton) findViewById(org.eclipsesoundscapes.R.id.prev_button);
        nextButton.setColorFilter(ContextCompat.getColor(context, org.eclipsesoundscapes.R.color.colorAccent));
        prevButton.setColorFilter(ContextCompat.getColor(context, org.eclipsesoundscapes.R.color.colorAccent));

        // listeners
        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        skipCloseButton.setOnClickListener(this);

        setupViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (isMenuMode())
                    updateNavigationButtons(position, LAYOUT_RES_MENU.length);
                else {
                    if (position == LAYOUT_RES_IDS.length - 1) { // hide next button on last layout
                        prevButton.setColorFilter(ContextCompat.getColor(context, android.R.color.black));
                        nextButton.setVisibility(View.INVISIBLE);
                    } else if (position == 0) {
                        prevButton.setVisibility(View.GONE); // hide prev button on first layout
                    } else {
                        prevButton.setColorFilter(ContextCompat.getColor(context, org.eclipsesoundscapes.R.color.colorAccent));
                        if (!prevButton.isShown())
                            prevButton.setVisibility(View.VISIBLE);
                        if (!nextButton.isShown())
                            nextButton.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onPageSelected(int position) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    // User has selected to view walk through from AboutFragment
    public boolean isMenuMode(){
        return mode != null && !mode.isEmpty();
    }

    /**
     * Updates next and previous button visibility and color
     * when walk through is accessed through AboutFragment
     * @param position current position in view pager
     * @param size number of pages in view pager
     */
    public void updateNavigationButtons(int position, int size){
        if (position == size - 1) { // hide next button on last layout
            nextButton.setVisibility(View.INVISIBLE);
        } else if (position == 0) {
            prevButton.setVisibility(View.GONE); // hide prev button on first layout
        } else {
            prevButton.setColorFilter(ContextCompat.getColor(context, org.eclipsesoundscapes.R.color.colorAccent));
            if (!prevButton.isShown())
                prevButton.setVisibility(View.VISIBLE);
            if (!nextButton.isShown())
                nextButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case org.eclipsesoundscapes.R.id.next_button:
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                break;
            case org.eclipsesoundscapes.R.id.prev_button:
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                break;
            case org.eclipsesoundscapes.R.id.skip_close_button:
                if (isMenuMode())
                    finish();
                else {
                    mViewPager.setCurrentItem(LAYOUT_RES_IDS[LAYOUT_RES_IDS.length - 1]); // skip to last page
                }
                break;
        }
    }

    // update preference, user has completed / skipped walk-through
    public void onCompleteWalkthrough(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.PREFERENCE_WALKTHROUGH, true);
        editor.apply();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // handle location permission during initial launch
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("settings_location", true);
            editor.putBoolean(Constants.PREFERENCE_WALKTHROUGH, true);
            editor.apply();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.PREFERENCE_WALKTHROUGH, true);
            editor.apply();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    /**
     * Setup viewpager layout based on mode
     * @param viewPager uses same fragment to display several different layout
     */
    private void setupViewPager(ViewPager viewPager) {
        WalkthroughPagerAdapter adapter = new WalkthroughPagerAdapter(getFragmentManager());
        if (isMenuMode())
            viewPager.setOffscreenPageLimit(LAYOUT_RES_MENU.length);
        else
            viewPager.setOffscreenPageLimit(LAYOUT_RES_IDS.length); // Helps to keep fragment alive, otherwise I will have to load again images
        viewPager.setAdapter(adapter);
    }

    private class WalkthroughPagerAdapter extends FragmentStatePagerAdapter {

        WalkthroughPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (isMenuMode())
                return WalkthroughFragment.newInstance(LAYOUT_RES_MENU[position], position, LAYOUT_RES_MENU.length);
            else
                return WalkthroughFragment.newInstance(LAYOUT_RES_IDS[position], position, LAYOUT_RES_IDS.length);
        }

        @Override
        public int getCount() {
            if (isMenuMode())
                return LAYOUT_RES_MENU.length;
            else
                return LAYOUT_RES_IDS.length;
        }
    }
}
