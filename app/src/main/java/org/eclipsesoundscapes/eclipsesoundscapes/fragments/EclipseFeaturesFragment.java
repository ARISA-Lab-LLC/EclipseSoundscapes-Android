package org.eclipsesoundscapes.eclipsesoundscapes.fragments;

import android.app.DialogFragment;
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
import android.widget.ViewFlipper;

import org.eclipsesoundscapes.eclipsesoundscapes.R;
import org.eclipsesoundscapes.eclipsesoundscapes.activity.MainActivity;
import org.eclipsesoundscapes.eclipsesoundscapes.util.CustomViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class EclipseFeaturesFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private Toolbar toolbar;
    private TextView toolbarTitle;

    private TabLayout tabLayout;
    private CustomViewPager mViewPager;
    private ViewFlipper viewFlipper;
    private int currentView;
    private HashMap<Integer, String> viewToText;

    public EclipseFeaturesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_eclipse_features, container, false);

        viewToText = new HashMap<>();
        viewToText.put(1, "Bailey's Beads");
        viewToText.put(2, "Corona");
        viewToText.put(3, "Diamond Ring");
        viewToText.put(4, "Helmet Streamers");
        viewToText.put(5, "Prominence");

        // views
        toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setTitleFromView(currentView);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mViewPager = (CustomViewPager) root.findViewById(R.id.viewpager);
        mViewPager.setPagingEnabled(false);
        setupViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Fragment page = getChildFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + mViewPager.getCurrentItem());
                if ( page != null) {
                    if (position == 0) {

                        ((RumbleMapFragment) page).updateView(currentView);
                    }
                    else if (position == 1)
                        ((DescriptionFragment)page).updateView(currentView);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout = (TabLayout) root.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        ImageButton prevButton = (ImageButton) root.findViewById(R.id.previousButton);
        ImageButton nextButton = (ImageButton) root.findViewById(R.id.nextButton);
        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        return root;
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
            window.setStatusBarColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) mContext).setCurrentCP(currentView);
    }

    @Override
    public void onClick(View view) {

        Fragment page = getChildFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + mViewPager.getCurrentItem());

        switch (view.getId()){
            case R.id.nextButton:
                if (currentView < 5) {
                    currentView++;
                } else {
                    currentView = 1;
                    setTitleFromView(currentView);
                }
                setTitleFromView(currentView);

                if (mViewPager.getCurrentItem() == 0 && page != null) {
                    ((RumbleMapFragment)page).updateView(currentView);
                    toolbarTitle.announceForAccessibility("Now viewing, " + viewToText.get(currentView));
                } else if (mViewPager.getCurrentItem() == 1 && page != null) {
                    ((DescriptionFragment) page).updateView(currentView);
                    toolbarTitle.announceForAccessibility("Now showing description about " + viewToText.get(currentView));
                }
                break;
            case R.id.previousButton:
                if (currentView > 1) {
                    currentView--;
                } else {
                    currentView = 5;
                }
                setTitleFromView(currentView);
                if (mViewPager.getCurrentItem() == 0 && page != null) {
                    ((RumbleMapFragment)page).updateView(currentView);
                    toolbarTitle.announceForAccessibility("Now viewing, " + viewToText.get(currentView));
                } else if (mViewPager.getCurrentItem() == 1 && page != null) {
                    ((DescriptionFragment) page).updateView(currentView);
                    toolbarTitle.announceForAccessibility("Now showing description about " + viewToText.get(currentView));
                }
                break;
        }
    }

    public void setTitleFromView(int view){
        switch (view){
            case 1: // baily's beads
                toolbarTitle.setText(getString(R.string.bailys_beads_title));
                break;
            case 2: // corona
                toolbarTitle.setText(getString(R.string.corona_title));
                break;
            case 3: // diamond ring
                toolbarTitle.setText(getString(R.string.diamond_ring_title));
                break;
            case 4: // helmet streamers
                toolbarTitle.setText(getString(R.string.helmet_streamers_title));
                break;
            case 5: // prominence
                toolbarTitle.setText(getString(R.string.prominence_title));
                break;
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new RumbleMapFragment(), "Rumble Map");
        adapter.addFragment(new DescriptionFragment(), "Description");
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
