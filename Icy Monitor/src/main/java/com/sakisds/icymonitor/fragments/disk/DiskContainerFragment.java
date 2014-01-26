/*
 * Copyright 2013 Thanasis Georgiou
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.sakisds.icymonitor.fragments.disk;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sakisds.icymonitor.R;
import com.sakisds.icymonitor.views.PagerSlidingTabStrip;

import java.util.Locale;

public class DiskContainerFragment extends Fragment {

    // Fragments
    protected Fragment[] mFragments, mFragmentsTablet;

    public DiskContainerFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        boolean doublePane = getResources().getBoolean(R.bool.double_graphs);

        View rootView = inflater.inflate(R.layout.fragment_graphcontainer, container, false);
        setHasOptionsMenu(true);

        // Setup fragments
        if (doublePane) {
            mFragments = new Fragment[1];
            mFragmentsTablet = new Fragment[1];

            if (savedInstanceState == null) {
                mFragments[0] = new DiskFSFragment();
                mFragmentsTablet[0] = new DiskTempFragment();
            } else {
                mFragments[0] = getChildFragmentManager().getFragment(savedInstanceState, getFragmentName(0));
                mFragmentsTablet[0] = getChildFragmentManager().getFragment(savedInstanceState, getFragmentName(1));
            }
        } else {
            if (savedInstanceState == null) {
                mFragments = new Fragment[2];
                mFragments[0] = new DiskFSFragment();
                mFragments[1] = new DiskTempFragment();
            } else {
                mFragments = new Fragment[getFragmentCount()];
                for (int i = 0; i < mFragments.length; i++) {
                    mFragments[i] = getChildFragmentManager().getFragment(savedInstanceState, getFragmentName(i));
                }
            }
        }


        // Setup view
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), false);

        ViewPager mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(5);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
        tabs.setViewPager(mViewPager);

        if (doublePane) {
            SectionsPagerAdapter mSectionsPagerAdapter2 = new SectionsPagerAdapter(getChildFragmentManager(), true);

            ViewPager mViewPager2 = (ViewPager) rootView.findViewById(R.id.pager_tablet);
            mViewPager2.setAdapter(mSectionsPagerAdapter2);
            mViewPager2.setOffscreenPageLimit(5);

            PagerSlidingTabStrip tabs2 = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs_tablet);
            tabs2.setViewPager(mViewPager2);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        for (int i = 0; i < mFragments.length; i++) {
            getChildFragmentManager().putFragment(outState, getFragmentName(i), mFragments[i]);
        }
    }

    protected int getFragmentCount() {
        return 2;
    }

    protected String getFragmentName(int position) {
        String[] names = {"FILESYSTEMS", "TEMP"};
        return names[position];
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private boolean mIsTablet;

        public SectionsPagerAdapter(FragmentManager fm, boolean isTablet) {
            super(fm);
            mIsTablet = isTablet;
        }

        @Override
        public Fragment getItem(int position) {
            return mIsTablet ? mFragmentsTablet[position] : mFragments[position];
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (mIsTablet) {
                return getFragmentName(1);
            } else {
                Locale l = Locale.getDefault();
                return getFragmentName(position).toUpperCase(l);
            }
        }
    }
}