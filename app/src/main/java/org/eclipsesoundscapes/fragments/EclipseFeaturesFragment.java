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

        mViewPager = (CustomViewPager) root.findViewById(org.eclipsesoundscapes.R.id.viewpager);
        mViewPager.setPagingEnabled(false);
        setupViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
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
            public void onPageScrollStateChanged(int state) {

            }
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


        Date eclipseDate = ((MainActivity)mContext).getEclipseDate();
        Date date = ((MainActivity)mContext).getFirstContact();
        Date dateTwo = ((MainActivity)mContext).getSecondContact();
        if (date != null && dateTwo != null){
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

            } else if (calendar.getTime().after(eclipseDate) && currentImgs.size() < 10){
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
        currentView = ((MainActivity)context).getCurrentCP();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(mContext, org.eclipsesoundscapes.R.color.colorPrimaryDark));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) mContext).setCurrentCP(currentView);
    }

    @Override
    public void onClick(View view) {

        Fragment page = getChildFragmentManager().findFragmentByTag("android:switcher:" + org.eclipsesoundscapes.R.id.viewpager + ":" + mViewPager.getCurrentItem());

        switch (view.getId()){
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

    public void setTitleFromView(int view){
        toolbarTitle.setText(currentImgs.get(view));
    }

    public void setCurrentImgs(HashMap<Integer, String> newImgs){
        currentImgs = newImgs;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new DescriptionFragment(), "Description");
        adapter.addFragment(new RumbleMapFragment(), "Rumble Map");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
