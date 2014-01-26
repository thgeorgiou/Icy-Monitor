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

package com.sakisds.icymonitor.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.sakisds.icymonitor.R;
import com.sakisds.icymonitor.fragments.about.AboutAppFragment;
import com.sakisds.icymonitor.fragments.about.AboutLibrariesFragment;
import com.sakisds.icymonitor.views.PagerSlidingTabStrip;

import java.util.Locale;

/**
 * Created by Thanasis Georgiou on 22/06/13.
 */
public class AboutActivity extends FragmentActivity {

    private Fragment[] mFragments, mFragmentsTablet;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_graphcontainer);

        // Actionbar
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // Tablet
        boolean doublePane = getResources().getBoolean(R.bool.double_graphs);

        // Setup fragments
        if (doublePane) {
            mFragments = new Fragment[1];
            mFragmentsTablet = new Fragment[1];

            if (savedInstanceState == null) {
                mFragments[0] = new AboutAppFragment();
                mFragmentsTablet[0] = new AboutLibrariesFragment();
            } else {
                mFragments[0] = getSupportFragmentManager().getFragment(savedInstanceState, getFragmentName(0));
                mFragmentsTablet[0] = getSupportFragmentManager().getFragment(savedInstanceState, getFragmentName(1));
            }
        } else {
            if (savedInstanceState == null) {
                mFragments = new Fragment[2];
                mFragments[0] = new AboutAppFragment();
                mFragments[1] = new AboutLibrariesFragment();
            } else {
                mFragments = new Fragment[getFragmentCount()];
                for (int i = 0; i < mFragments.length; i++) {
                    mFragments[i] = getSupportFragmentManager().getFragment(savedInstanceState, getFragmentName(i));
                }
            }
        }

        // Setup view
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), false);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(5);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(mViewPager);

        if (doublePane) {
            SectionsPagerAdapter mSectionsPagerAdapter2 = new SectionsPagerAdapter(getSupportFragmentManager(), true);

            ViewPager mViewPager2 = (ViewPager) findViewById(R.id.pager_tablet);
            mViewPager2.setAdapter(mSectionsPagerAdapter2);
            mViewPager2.setOffscreenPageLimit(5);

            PagerSlidingTabStrip tabs2 = (PagerSlidingTabStrip) findViewById(R.id.tabs_tablet);
            tabs2.setViewPager(mViewPager2);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return (true);
            case R.id.item_github:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://sakisds.github.io/Icy-Monitor/"));
                startActivity(browserIntent);
                return true;
            case R.id.item_contact:
                Uri uri = Uri.parse("mailto:sakisds.s@gmail.com");
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                        "Icy Monitor Feedback");
                startActivity(emailIntent);
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        for (int i = 0; i < mFragments.length; i++) {
            getSupportFragmentManager().putFragment(outState, getFragmentName(i), mFragments[i]);
        }
    }

    protected int getFragmentCount() {
        return 2;
    }

    protected String getFragmentName(int position) {
        String[] names = {"APPLICATION", "LIBRARIES"};
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
