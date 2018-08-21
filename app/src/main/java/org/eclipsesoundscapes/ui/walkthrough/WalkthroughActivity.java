package org.eclipsesoundscapes.ui.walkthrough;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import org.eclipsesoundscapes.EclipseSoundscapesApp;
import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.data.DataManager;
import org.eclipsesoundscapes.ui.main.MainActivity;
import org.eclipsesoundscapes.ui.about.AboutFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

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
 * Displayed during applications first launch or launched from {@link AboutFragment}
 * @see WalkthroughFragment
 */

public class WalkthroughActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_MODE = "MODE";
    public static final String MODE_MENU = "MODE_MENU";

    private String mode; // either initial launch or from AboutFragment
    private DataManager dataManager;

    @BindView(R.id.viewpager) ViewPager mViewPager;
    @BindView(R.id.skip_close_button) Button skipCloseButton;

    @BindView(R.id.next_button) ImageButton nextButton;
    @BindView(R.id.prev_button) ImageButton prevButton;

    // walk through fragment layouts for first time launch
    private static final int[] LAYOUT_RES_IDS = {
            R.layout.layout_walkthrough_one, R.layout.layout_walkthrough_two, R.layout.layout_walkthrough_three,
            R.layout.layout_walkthrough_four, R.layout.layout_walkthrough_five};

    // walk through fragment layouts launched from AboutFragment, omits permission
    private static final int[] LAYOUT_RES_MENU = {
            R.layout.layout_walkthrough_one, R.layout.layout_walkthrough_two, R.layout.layout_walkthrough_three,
            R.layout.layout_walkthrough_four};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkthrough);
        ButterKnife.bind(this);

        dataManager = ((EclipseSoundscapesApp) getApplication()).getDataManager();
        mode = getIntent().getStringExtra(EXTRA_MODE);

        if (isMenuMode()) {
            skipCloseButton.setText(getString(R.string.closeButton));
            skipCloseButton.setContentDescription(getString(R.string.closeButton));
        }

        // listeners
        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        skipCloseButton.setOnClickListener(this);

        setupViewPager(mViewPager);
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
            prevButton.setColorFilter(ContextCompat.getColor(this, org.eclipsesoundscapes.R.color.colorAccent));
            if (!prevButton.isShown())
                prevButton.setVisibility(View.VISIBLE);
            if (!nextButton.isShown())
                nextButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.next_button:
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                break;
            case R.id.prev_button:
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                break;
            case R.id.skip_close_button:
                if (isMenuMode())
                    onBackPressed();
                else {
                    mViewPager.setCurrentItem(LAYOUT_RES_IDS[LAYOUT_RES_IDS.length - 1]); // skip to last page
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }

    // handle location permission during initial launch
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        onCompleteWalkthrough();
    }

    // User has selected to view walk through from AboutFragment
    public boolean isMenuMode(){
        return mode != null && !mode.isEmpty();
    }

    // update preference, user has completed / skipped walk-through
    public void onCompleteWalkthrough(){
        dataManager.setWalkthroughComplete(true);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    /**
     * Setup viewpager layout based on mode
     * @param viewPager uses same fragment to display several different layout
     */
    private void setupViewPager(ViewPager viewPager) {
        WalkthroughPagerAdapter adapter = new WalkthroughPagerAdapter(getSupportFragmentManager());
        if (isMenuMode())
            viewPager.setOffscreenPageLimit(LAYOUT_RES_MENU.length);
        else
            viewPager.setOffscreenPageLimit(LAYOUT_RES_IDS.length); // Helps to keep fragment alive, otherwise I will have to load again images

        viewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (isMenuMode())
                    updateNavigationButtons(position, LAYOUT_RES_MENU.length);
                else {
                    if (position == LAYOUT_RES_IDS.length - 1) { // hide next button on last layout
                        nextButton.setVisibility(View.INVISIBLE);
                        skipCloseButton.setVisibility(View.INVISIBLE);
                    } else if (position == 0) {
                        prevButton.setVisibility(View.GONE); // hide prev button on first layout
                        skipCloseButton.setVisibility(View.VISIBLE);
                    } else {
                        if (!prevButton.isShown())
                            prevButton.setVisibility(View.VISIBLE);
                        if (!nextButton.isShown())
                            nextButton.setVisibility(View.VISIBLE);
                        skipCloseButton.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onPageSelected(int position) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
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
