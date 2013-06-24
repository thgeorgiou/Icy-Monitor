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
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.*;
import com.sakisds.icymonitor.R;
import com.sakisds.icymonitor.fragments.*;


public class MainViewActivity extends FragmentActivity implements ListView.OnItemClickListener {
    public static final String SHAREDPREFS_FILE = "com.sakisds.icymonitor.settings";
    public static final String EXTRA_ADDRESS = "com.sakisds.icymonitor.extra";

    private final String[] mFragmentList = {"System", "CPU", "GPU", "Processes", "Disks"};
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean mTabletLandscape;

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
                    R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                    R.string.open_drawer,  /* "open drawer" description */
                    R.string.close_drawer  /* "close drawer" description */
            );


            mDrawerLayout.setDrawerListener(mDrawerToggle);
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }

        // Set default view
        if (savedInstanceState == null) {
            switchContent(new SystemFragment());
            setTitle(mFragmentList[0]);
        }
    }


    /**
     * Switches content view to a new fragment.
     *
     * @param fragment
     */
    private void switchContent(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
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
        switch (pos) {
            case 0:
                switchContent(new SystemFragment());
                break;
            case 1:
                switchContent(new CPUFragment());
                break;
            case 2:
                switchContent(new GPUFragment());
                break;
            case 3:
                switchContent(new ProcessesFragment());
                break;
            case 4:
                switchContent(new DiskFragment());
                break;
        }
        setTitle(mFragmentList[pos]);
        if (!mTabletLandscape) {
            mDrawerLayout.closeDrawer(mDrawerList);
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
                imageView.setImageResource(R.drawable.ic_action_drawer_list_cpu);
            } else if (s.equals("System")) {
                imageView.setImageResource(R.drawable.ic_action_drawer_list_info);
            } else if (s.equals("Disks")) {
                imageView.setImageResource(R.drawable.ic_action_drawer_list_disks);
                //} else if (s.equals("Battery")) {
                //    imageView.setImageResource(R.drawable.ic_action_drawer_list_battery);
            } else if (s.equals("Processes")) {
                imageView.setImageResource(R.drawable.ic_action_drawer_list_processes);
            } else if (s.equals("GPU")) {
                imageView.setImageResource(R.drawable.ic_action_drawer_list_memory);
            }
            imageView.setContentDescription(s + " icon");

            return rowView;
        }
    }
}
