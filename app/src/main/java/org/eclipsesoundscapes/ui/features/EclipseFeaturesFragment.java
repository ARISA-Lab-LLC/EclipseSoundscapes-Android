package org.eclipsesoundscapes.ui.features;

import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.ui.main.MainActivity;
import org.eclipsesoundscapes.ui.custom.CustomViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

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
 * Creates a tablayout with {@link EclipseFragment} and {@link DescriptionFragment}
 * and also provides nagivation to switch between content
 */

public class EclipseFeaturesFragment extends Fragment {

    private static final int ITEM_DESCRIPTION = 0;
    private static final int ITEM_ECLIPSE = 1;

    private int currentView;
    private SparseArray<String> titles;

    @BindView(R.id.toolbar_title) TextView toolbarTitle;
    @BindView(R.id.tabs) TabLayout tabLayout;
    @BindView(R.id.viewpager) CustomViewPager mViewPager;

    @OnClick(R.id.nextButton) void viewNext(){
        Fragment page = getChildFragmentManager().findFragmentByTag("android:switcher:" +
                R.id.viewpager + ":" + mViewPager.getCurrentItem());

        currentView = (currentView < titles.size()) ? currentView + 1 : 1;
        setTitleFromView(currentView);

        if (mViewPager.getCurrentItem() == 0 && page != null)
            ((DescriptionFragment) page).updateView(currentView);
        else if (mViewPager.getCurrentItem() == 1 && page != null)
            ((EclipseFragment)page).updateView(currentView);
    }

    @OnClick(R.id.previousButton) void viewPrevious(){
        Fragment page = getChildFragmentManager().findFragmentByTag("android:switcher:" +
                R.id.viewpager + ":" + mViewPager.getCurrentItem());

        currentView = (currentView > 1) ? currentView - 1 : titles.size();
        setTitleFromView(currentView);

        if (mViewPager.getCurrentItem() == 0 && page != null)
            ((DescriptionFragment) page).updateView(currentView);
        else if (mViewPager.getCurrentItem() == 1 && page != null)
            ((EclipseFragment)page).updateView(currentView);
    }

    @OnTextChanged(R.id.toolbar_title)
    protected void onTextChanged(CharSequence text) {
        String announce;
        if (mViewPager.getCurrentItem() == ITEM_DESCRIPTION){
            announce = getString(R.string.viewing_desc_format, text.toString());
        } else {
            announce = getString(R.string.viewing_image_format, text.toString());
        }

        toolbarTitle.announceForAccessibility(announce);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_eclipse_features, container, false);
        ButterKnife.bind(this, root);

        // toolbar
        Toolbar toolbar = root.findViewById(R.id.toolbar);
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
        }

        // custom view pager, similar to default but disables swipe to change pages
        mViewPager.setPagingEnabled(false);
        setupViewPager(mViewPager);
        tabLayout.setupWithViewPager(mViewPager);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        titles = new SparseArray<>();

        if (getActivity() != null && getActivity() instanceof MainActivity) {

            if (!((MainActivity) getActivity()).isAfterFirstContact()) {
                titles.put(1, getString(org.eclipsesoundscapes.R.string.bailys_beads_title));
                titles.put(2, getString(org.eclipsesoundscapes.R.string.bailys_beads_closeup_title));
                titles.put(3, getString(org.eclipsesoundscapes.R.string.corona_title));
                titles.put(4, getString(org.eclipsesoundscapes.R.string.diamond_ring_title));
                titles.put(5, getString(org.eclipsesoundscapes.R.string.helmet_streamers_title));
                titles.put(6, getString(org.eclipsesoundscapes.R.string.helmet_streamers_closeup_title));
                titles.put(7, getString(org.eclipsesoundscapes.R.string.prominence_title));
                titles.put(8, getString(org.eclipsesoundscapes.R.string.prominence_closeup_title));
                return;
            }

                titles.put(1, getString(org.eclipsesoundscapes.R.string.first_contact_title));
                titles.put(2, getString(org.eclipsesoundscapes.R.string.bailys_beads_title));
                titles.put(3, getString(org.eclipsesoundscapes.R.string.bailys_beads_closeup_title));
                titles.put(4, getString(org.eclipsesoundscapes.R.string.corona_title));
                titles.put(5, getString(org.eclipsesoundscapes.R.string.diamond_ring_title));
                titles.put(6, getString(org.eclipsesoundscapes.R.string.helmet_streamers_title));
                titles.put(7, getString(org.eclipsesoundscapes.R.string.helmet_streamers_closeup_title));
                titles.put(8, getString(org.eclipsesoundscapes.R.string.prominence_title));
                titles.put(9, getString(org.eclipsesoundscapes.R.string.prominence_closeup_title));

                if (((MainActivity) getActivity()).isAfterTotality())
                    titles.put(10, getString(org.eclipsesoundscapes.R.string.totality_title));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() != null)
            currentView = ((MainActivity)getActivity()).getCurrentView();
        setTitleFromView(currentView);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null)
            ((MainActivity) getActivity()).setCurrentView(currentView);
    }

    /**
     * Set the title of current eclipse item in navigation
     * @param pos current navigated position mapped to a title
     */
    public void setTitleFromView(int pos){
        toolbarTitle.setText(titles.get(pos));
    }

    /**
     * Setup the view pager with two tabs
     * @see DescriptionFragment
     * @see EclipseFragment
     * @param viewPager viewpager for setup
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new DescriptionFragment(), getString(R.string.description));
        adapter.addFragment(new EclipseFragment(), getString(R.string.title_rumble_map));
        viewPager.setAdapter(adapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                // replace the content of the description or rumble map fragment
                Fragment page = getChildFragmentManager().findFragmentByTag("android:switcher:" +
                        R.id.viewpager + ":" + mViewPager.getCurrentItem());

                if (page != null) {
                    if (position == 0) {
                        ((DescriptionFragment)page).updateView(currentView);
                    } else if (position == 1)
                        ((EclipseFragment) page).updateView(currentView);
                }

                mViewPager.announceForAccessibility("");
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
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
