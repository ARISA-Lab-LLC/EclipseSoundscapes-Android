package org.eclipsesoundscapes.fragments;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import org.eclipsesoundscapes.activity.MainActivity;
import org.eclipsesoundscapes.util.CustomViewPager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
 * Creates a tablayout with {@link RumbleMapFragment} and {@link DescriptionFragment}
 * and also provides nagivation to switch between content
 */

public class EclipseFeaturesFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private Toolbar toolbar;
    private TextView toolbarTitle;

    private TabLayout tabLayout;
    private CustomViewPager mViewPager;
    private int currentView;
    private HashMap<Integer, String> currentImgs;
    private Calendar calendar;

    public EclipseFeaturesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        calendar = Calendar.getInstance();

        // maps current view to title
        currentImgs = new HashMap<>();
        currentImgs.put(1, getString(org.eclipsesoundscapes.R.string.bailys_beads_title));
        currentImgs.put(2, getString(org.eclipsesoundscapes.R.string.bailys_beads_closeup_title));
        currentImgs.put(3, getString(org.eclipsesoundscapes.R.string.corona_title));
        currentImgs.put(4, getString(org.eclipsesoundscapes.R.string.diamond_ring_title));
        currentImgs.put(5, getString(org.eclipsesoundscapes.R.string.helmet_streamers_title));
        currentImgs.put(6, getString(org.eclipsesoundscapes.R.string.helmet_streamers_closeup_title));
        currentImgs.put(7, getString(org.eclipsesoundscapes.R.string.prominence_title));
        currentImgs.put(8, getString(org.eclipsesoundscapes.R.string.prominence_closeup_title));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(org.eclipsesoundscapes.R.layout.fragment_eclipse_features, container, false);

        // views
        toolbar = (Toolbar) root.findViewById(org.eclipsesoundscapes.R.id.toolbar);
        toolbarTitle = (TextView) toolbar.findViewById(org.eclipsesoundscapes.R.id.toolbar_title);
        setTitleFromView(currentView);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // custom view pager, similar to default but disables swipe to change pages
        mViewPager = (CustomViewPager) root.findViewById(org.eclipsesoundscapes.R.id.viewpager);
        mViewPager.setPagingEnabled(false);
        setupViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                // replace the content of the description or rumble map fragment
                Fragment page = getChildFragmentManager().findFragmentByTag("android:switcher:" + org.eclipsesoundscapes.R.id.viewpager + ":" + mViewPager.getCurrentItem());
                if ( page != null) {
                    if (position == 0) {
                        ((DescriptionFragment)page).updateView(currentView);
                    }
                    else if (position == 1)
                        ((RumbleMapFragment) page).updateView(currentView);
                }
                mViewPager.announceForAccessibility("");
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        tabLayout = (TabLayout) root.findViewById(org.eclipsesoundscapes.R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        ImageButton prevButton = (ImageButton) root.findViewById(org.eclipsesoundscapes.R.id.previousButton);
        ImageButton nextButton = (ImageButton) root.findViewById(org.eclipsesoundscapes.R.id.nextButton);
        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        Date eclipseDate = getEclipseDate();
        Date date = ((MainActivity)mContext).getFirstContact();
        Date dateTwo = ((MainActivity)mContext).getSecondContact();
        if (calendar.getTime().after(eclipseDate) && currentImgs.size() < 10){
            currentImgs.clear();
            currentImgs.put(1, getString(org.eclipsesoundscapes.R.string.first_contact_title));
            currentImgs.put(2, getString(org.eclipsesoundscapes.R.string.bailys_beads_title));
            currentImgs.put(3, getString(org.eclipsesoundscapes.R.string.bailys_beads_closeup_title));
            currentImgs.put(4, getString(org.eclipsesoundscapes.R.string.corona_title));
            currentImgs.put(5, getString(org.eclipsesoundscapes.R.string.diamond_ring_title));
            currentImgs.put(6, getString(org.eclipsesoundscapes.R.string.helmet_streamers_title));
            currentImgs.put(7, getString(org.eclipsesoundscapes.R.string.helmet_streamers_closeup_title));
            currentImgs.put(8, getString(org.eclipsesoundscapes.R.string.prominence_title));
            currentImgs.put(9, getString(org.eclipsesoundscapes.R.string.prominence_closeup_title));
            currentImgs.put(10, getString(org.eclipsesoundscapes.R.string.totality_title));
        }else if (date != null && dateTwo != null){
            if (calendar.getTime().after(date) && calendar.getTime().before(dateTwo) && currentImgs.size() < 9){
                currentImgs.clear();
                currentImgs.put(1, getString(org.eclipsesoundscapes.R.string.first_contact_title));
                currentImgs.put(2, getString(org.eclipsesoundscapes.R.string.bailys_beads_title));
                currentImgs.put(3, getString(org.eclipsesoundscapes.R.string.bailys_beads_closeup_title));
                currentImgs.put(4, getString(org.eclipsesoundscapes.R.string.corona_title));
                currentImgs.put(5, getString(org.eclipsesoundscapes.R.string.diamond_ring_title));
                currentImgs.put(6, getString(org.eclipsesoundscapes.R.string.helmet_streamers_title));
                currentImgs.put(7, getString(org.eclipsesoundscapes.R.string.helmet_streamers_closeup_title));
                currentImgs.put(8, getString(org.eclipsesoundscapes.R.string.prominence_title));
                currentImgs.put(9, getString(org.eclipsesoundscapes.R.string.prominence_closeup_title));
            } else if (calendar.getTime().after(dateTwo)&& currentImgs.size() < 10){
                currentImgs.clear();
                currentImgs.put(1, getString(org.eclipsesoundscapes.R.string.first_contact_title));
                currentImgs.put(2, getString(org.eclipsesoundscapes.R.string.bailys_beads_title));
                currentImgs.put(3, getString(org.eclipsesoundscapes.R.string.bailys_beads_closeup_title));
                currentImgs.put(4, getString(org.eclipsesoundscapes.R.string.corona_title));
                currentImgs.put(5, getString(org.eclipsesoundscapes.R.string.diamond_ring_title));
                currentImgs.put(6, getString(org.eclipsesoundscapes.R.string.helmet_streamers_title));
                currentImgs.put(7, getString(org.eclipsesoundscapes.R.string.helmet_streamers_closeup_title));
                currentImgs.put(8, getString(org.eclipsesoundscapes.R.string.prominence_title));
                currentImgs.put(9, getString(org.eclipsesoundscapes.R.string.prominence_closeup_title));
                currentImgs.put(10, getString(org.eclipsesoundscapes.R.string.totality_title));
            }
        }
        setTitleFromView(currentView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        mContext = getActivity();
        // get last navigated position if any
        currentView = ((MainActivity)getActivity()).getCurrentCP();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(mContext, org.eclipsesoundscapes.R.color.colorPrimaryDark));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // save current position in view when leaving fragment
        ((MainActivity) mContext).setCurrentCP(currentView);
    }

    @Override
    public void onClick(View view) {
        Fragment page = getChildFragmentManager().findFragmentByTag("android:switcher:" + org.eclipsesoundscapes.R.id.viewpager + ":" + mViewPager.getCurrentItem());
        switch (view.getId()){
            // show next content
            case org.eclipsesoundscapes.R.id.nextButton:
                if (currentView < currentImgs.size()) {
                    currentView++;
                } else {
                    currentView = 1;
                    setTitleFromView(currentView);
                }
                setTitleFromView(currentView);

                if (mViewPager.getCurrentItem() == 0 && page != null) {
                    ((DescriptionFragment) page).updateView(currentView);
                } else if (mViewPager.getCurrentItem() == 1 && page != null) {
                    ((RumbleMapFragment)page).updateView(currentView);
                }
                break;
            // show previous content
            case org.eclipsesoundscapes.R.id.previousButton:
                if (currentView > 1) {
                    currentView--;
                } else {
                    // last img
                    currentView = currentImgs.size();
                }
                setTitleFromView(currentView);
                if (mViewPager.getCurrentItem() == 0 && page != null) {
                    ((DescriptionFragment) page).updateView(currentView);
                } else if (mViewPager.getCurrentItem() == 1 && page != null) {
                    ((RumbleMapFragment)page).updateView(currentView);
                }
                break;
        }
    }

    /**
     * Set the title of current eclipse item in navigation
     * @param pos current navigated position mapped to a title
     */
    public void setTitleFromView(int pos){
        toolbarTitle.setText(currentImgs.get(pos));
    }

    // get date for current eclipse, set to Aug 21
    public Date getEclipseDate(){
        Calendar eclipseDate = Calendar.getInstance();
        eclipseDate.setTimeZone(TimeZone.getTimeZone("UTC"));
        eclipseDate.set(Calendar.YEAR, 2017);
        eclipseDate.set(Calendar.MONTH, Calendar.AUGUST);
        eclipseDate.set(Calendar.DAY_OF_MONTH, 21);
        eclipseDate.set(Calendar.HOUR_OF_DAY, 20);
        eclipseDate.set(Calendar.MINUTE, 11);
        eclipseDate.set(Calendar.SECOND, 14);
        return eclipseDate.getTime();
    }

    /**
     * Setup the view pager with two tabs
     * @see DescriptionFragment
     * @see RumbleMapFragment
     * @param viewPager viewpager for setup
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new DescriptionFragment(), "Description");
        adapter.addFragment(new RumbleMapFragment(), "Rumble Map");
        viewPager.setAdapter(adapter);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
