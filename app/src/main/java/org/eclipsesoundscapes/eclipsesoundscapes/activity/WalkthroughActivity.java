package org.eclipsesoundscapes.eclipsesoundscapes.activity;

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
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.eclipsesoundscapes.eclipsesoundscapes.R;
import org.eclipsesoundscapes.eclipsesoundscapes.fragments.WalkthroughFragment;
import org.eclipsesoundscapes.eclipsesoundscapes.util.Constants;


/*
 * Displays the walk through by loading each layout corresponding to each feature
 * If activity is passed with extra intent mode_menu, then it will display walk through
 * without last layout (permission)
 */

public class WalkthroughActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private SharedPreferences sharedPreferences;
    private String mode;

    // views
    private ViewPager mViewPager;
    private Button skipCloseButton; // skips or closes walk through depending on mode
    private ImageButton nextButton;
    private ImageButton prevButton;

    private static final int[] LAYOUT_RES_IDS = {
            R.layout.layout_walkthrough_one, R.layout.layout_walkthrough_two, R.layout.layout_walkthrough_three,
            R.layout.layout_walkthrough_four, R.layout.layout_walkthrough_five};

    private static final int[] LAYOUT_RES_MENU = {
            R.layout.layout_walkthrough_one, R.layout.layout_walkthrough_two, R.layout.layout_walkthrough_three,
            R.layout.layout_walkthrough_four};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkthrough);
        context = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mode = getIntent().getStringExtra("mode");

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        skipCloseButton = (Button) findViewById(R.id.skip_close_button);
        if (isMenuMode()) {
            skipCloseButton.setText(getString(R.string.closeButton));
            skipCloseButton.setContentDescription(getString(R.string.closeButton));
        }
        nextButton = (ImageButton) findViewById(R.id.next_button);
        prevButton = (ImageButton) findViewById(R.id.prev_button);
        nextButton.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent));
        prevButton.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent));

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
                        prevButton.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent));
                        if (!prevButton.isShown())
                            prevButton.setVisibility(View.VISIBLE);
                        if (!nextButton.isShown())
                            nextButton.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    public void updateNavigationButtons(int position, int size){
        if (position == size - 1) { // hide next button on last layout
            nextButton.setVisibility(View.INVISIBLE);
        } else if (position == 0) {
            prevButton.setVisibility(View.GONE); // hide prev button on first layout
        } else {
            prevButton.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent));
            if (!prevButton.isShown())
                prevButton.setVisibility(View.VISIBLE);
            if (!nextButton.isShown())
                nextButton.setVisibility(View.VISIBLE);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        WalkthroughPagerAdapter adapter = new WalkthroughPagerAdapter(getFragmentManager());
        if (isMenuMode())
            viewPager.setOffscreenPageLimit(LAYOUT_RES_MENU.length);
        else
            viewPager.setOffscreenPageLimit(LAYOUT_RES_IDS.length); // Helps to keep fragment alive, otherwise I will have to load again images
        viewPager.setAdapter(adapter);
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
                    finish();
                else {
                    mViewPager.setCurrentItem(LAYOUT_RES_IDS[LAYOUT_RES_IDS.length - 1]); // skip to last page
                }
                break;
        }
    }

    public void onCompleteWalkthrough(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.PREFERENCE_WALKTHROUGH, true);
        editor.apply();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

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

    public boolean isMenuMode(){
        return mode != null && !mode.isEmpty();
    }

    public class WalkthroughPagerAdapter extends FragmentStatePagerAdapter {

        public WalkthroughPagerAdapter(FragmentManager fm) {
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


