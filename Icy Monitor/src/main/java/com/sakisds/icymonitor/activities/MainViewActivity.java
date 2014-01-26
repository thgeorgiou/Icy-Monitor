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

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.*;
import com.sakisds.icymonitor.R;
import com.sakisds.icymonitor.fragments.NotificationsFragment;
import com.sakisds.icymonitor.fragments.OverviewFragment;
import com.sakisds.icymonitor.fragments.ProcessesFragment;
import com.sakisds.icymonitor.fragments.disk.DiskContainerFragment;
import com.sakisds.icymonitor.fragments.graph.CPUFragment;
import com.sakisds.icymonitor.fragments.graph.GPUFragment;
import com.sakisds.icymonitor.fragments.graph.SystemFragment;
import com.sakisds.icymonitor.fragments.history.HistoryGraphFragment;
import com.sakisds.icymonitor.fragments.history.HistoryPickerFragment;


public class MainViewActivity extends FragmentActivity implements ListView.OnItemClickListener {
    public static final String SHAREDPREFS_FILE = "com.sakisds.icymonitor.settings";
    public static final String EXTRA_ADDRESS = "com.sakisds.icymonitor.extra";

    private final String[] mFragmentList = {"Overview", "System", "CPU", "GPU", "Processes", "Disks", "History", "Notifications"};
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean mTabletLandscape;
    private int mCurrentFragmentIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create layout and drawer
        setContentView(R.layout.activity_main);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set listeners
        mDrawerList.setAdapter(new DrawerListArrayAdapter(this, mFragmentList));
        mDrawerList.setOnItemClickListener(this);

        // Drawer layout
        mDrawerLayout = ((DrawerLayout) findViewById(R.id.drawer_layout));

        mTabletLandscape = mDrawerLayout == null;

        if (!mTabletLandscape) {
            // Change actionbar toggle icon
            mDrawerToggle = new ActionBarDrawerToggle(
                    this,                  /* host Activity */
                    mDrawerLayout,         /* DrawerLayout object */
                    R.drawable.ic_navigation_drawer,  /* nav drawer icon to replace 'Up' caret */
                    R.string.open_drawer,  /* "open drawer" description */
                    R.string.close_drawer  /* "close drawer" description */
            );


            mDrawerLayout.setDrawerListener(mDrawerToggle);
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }

        // Set default view
        if (savedInstanceState == null) {
            switchContent(new OverviewFragment(), false);
            setTitle(mFragmentList[0]);
        }
    }

    /**
     * Switches content view to a new fragment.
     *
     * @param fragment  Fragment to display.
     * @param backstack True if this transaction has to be added to the backstack.
     */
    private void switchContent(Fragment fragment, Boolean backstack) {
        if (backstack) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.content_frame, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Check toggle
        if (!mTabletLandscape) {
            if (mDrawerToggle.onOptionsItemSelected(item)) {
                return true;
            }
        }

        // Check menus
        switch (item.getItemId()) {
            case R.id.item_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.item_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
        if (mCurrentFragmentIndex == pos) {
            if (!mTabletLandscape) mDrawerLayout.closeDrawer(mDrawerList);
            return;
        }
        mCurrentFragmentIndex = pos;

        switch (pos) {
            case 0:
                switchContent(new OverviewFragment(), false);
                break;
            case 1:
                switchContent(new SystemFragment(), false);
                break;
            case 2:
                switchContent(new CPUFragment(), false);
                break;
            case 3:
                switchContent(new GPUFragment(), false);
                break;
            case 4:
                switchContent(new ProcessesFragment(), false);
                break;
            case 5:
                switchContent(new DiskContainerFragment(), false);
                break;
            case 6:
                switchContent(new HistoryPickerFragment(), false);
                break;
            case 7:
                switchContent(new NotificationsFragment(), false);
        }
        setTitle(mFragmentList[pos]);

        if (!mTabletLandscape) {
            // Delay fragment change to avoid lag when closing the navigation drawer
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
            }, 150);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (!mTabletLandscape) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!mTabletLandscape) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    /**
     * Replaces content with a HistoryGraphFragment and adds it to the backstack.
     *
     * @param file File to request from server
     */
    public void openHistoryFile(String file) {
        Bundle bundle = new Bundle();
        bundle.putString(HistoryGraphFragment.KEY_HISTORY_FILE, file);

        Fragment fragment = new HistoryGraphFragment();
        fragment.setArguments(bundle);

        switchContent(fragment, false);
    }

    class DrawerListArrayAdapter extends ArrayAdapter<String> {

        private final Context mContext;
        private final String[] mValues;

        public DrawerListArrayAdapter(Context context, String[] values) {
            super(context, R.layout.list_item_icon, values);
            mContext = context;
            mValues = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_item_icon, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.label);

            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
            textView.setText(mValues[position]);

            // Set icons
            String s = mValues[position];
            if (s.equals("CPU")) {
                imageView.setImageResource(R.drawable.ic_drawer_cpu);
            } else if (s.equals("System")) {
                imageView.setImageResource(R.drawable.ic_drawer_system);
            } else if (s.equals("Disks")) {
                imageView.setImageResource(R.drawable.ic_drawer_disks);
            } else if (s.equals("Processes")) {
                imageView.setImageResource(R.drawable.ic_drawer_processes);
            } else if (s.equals("GPU")) {
                imageView.setImageResource(R.drawable.ic_drawer_gpu);
            } else if (s.equals("Overview")) {
                imageView.setImageResource(R.drawable.ic_drawer_overview);
            } else if (s.equals("History")) {
                imageView.setImageResource(R.drawable.ic_drawer_history);
            } else if (s.equals("Notifications")) {
                imageView.setImageResource(R.drawable.ic_drawer_notifications);
            }
            imageView.setContentDescription(s + " icon");

            return rowView;
        }
    }
}