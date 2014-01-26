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

package com.sakisds.icymonitor.fragments.graph;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sakisds.icymonitor.R;
import com.sakisds.icymonitor.activities.MainViewActivity;
import com.sakisds.icymonitor.views.PagerSlidingTabStrip;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

public class GraphContainerFragment extends Fragment {

    // Fragments
    protected GraphFragment[] mFragments, mFragmentsTablet;

    // Keep screen on menuitem
    private MenuItem mKeepOnItem;
    private MenuItem mPauseItem;

    // Network stuff
    private AsyncHttpClient mClient;
    private RequestParams mParams;
    private String mUrl;

    // Background thread
    private ChartTask mTask;
    private boolean mWorking = true;
    private boolean mPaused = false;
    private boolean mInitialized = false;
    private int mRefreshRate = 1000;
    private boolean mLastUpdateFinished = true;

    public GraphContainerFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_graphcontainer, container, false);
        setHasOptionsMenu(true);

        boolean doubleGraphs = getResources().getBoolean(R.bool.double_graphs);

        // Setup fragments
        if (savedInstanceState == null) {
            mFragments = new GraphFragment[getFragmentCount()];
            if (doubleGraphs) mFragmentsTablet = new GraphFragment[getFragmentCount()];
            for (int i = 0; i < getFragmentCount(); i++) {
                mFragments[i] = new GraphFragment();
                if (doubleGraphs) mFragmentsTablet[i] = new GraphFragment();
            }
            assignFragments();
        } else {
            mFragments = new GraphFragment[getFragmentCount()];
            if (doubleGraphs) mFragmentsTablet = new GraphFragment[getFragmentCount()];
            for (int i = 0; i < mFragments.length; i++) {
                mFragments[i] = (GraphFragment) getChildFragmentManager().getFragment(savedInstanceState, getFragmentName(i));
                if (doubleGraphs)
                    mFragmentsTablet[i] = (GraphFragment) getChildFragmentManager().getFragment(savedInstanceState, getFragmentName(i) + "tablet");
            }
            assignFragments();
        }

        // First graph
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), false);

        ViewPager mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(5);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
        tabs.setViewPager(mViewPager);

        if (doubleGraphs) {
            SectionsPagerAdapter mSectionsPagerAdapter2 = new SectionsPagerAdapter(getChildFragmentManager(), true);

            ViewPager mViewPager2 = (ViewPager) rootView.findViewById(R.id.pager_tablet);
            mViewPager2.setAdapter(mSectionsPagerAdapter2);
            mViewPager2.setOffscreenPageLimit(5);

            PagerSlidingTabStrip tabs2 = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs_tablet);
            tabs2.setViewPager(mViewPager2);

            mViewPager2.setCurrentItem(1);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Create async client
        mClient = new AsyncHttpClient();
        mClient.setTimeout(1000);
        mClient.setMaxRetriesAndTimeout(2, 1000);

        mParams = getParameters();
        mParams.put("name", "a"); // Retrieve names for the first time
        mUrl = getActivity().getIntent().getStringExtra(MainViewActivity.EXTRA_ADDRESS) + "/data";

        // Request first batch of data
        mClient.get(mUrl, mParams, new FirstGraphsHttpResponseHandler());

        mParams.remove("name");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        for (int i = 0; i < mFragments.length; i++) {
            getChildFragmentManager().putFragment(outState, getFragmentName(i), mFragments[i]);
            if (getResources().getBoolean(R.bool.double_graphs))
                getChildFragmentManager().putFragment(outState, getFragmentName(i) + "tablet", mFragments[i]);
        }
    }

    protected void assignFragments() {
        // Use this to assign fragments to objects after they are retrieved from the fragment manager.
    }

    protected RequestParams getParameters() {
        RequestParams params = getParametersWithoutId();
        SharedPreferences prefs = getActivity().getSharedPreferences(MainViewActivity.SHAREDPREFS_FILE, 0);

        params.put("id", String.valueOf(prefs.getLong("device_id", -2)));

        return params;
    }

    protected RequestParams getParametersWithoutId() {
        // Override this
        return new RequestParams();
    }

    protected int getFragmentCount() {
        // Override and return how many fragments you have
        return 0;
    }

    protected String getFragmentName(int position) {
        // Override to return fragment names
        return null;
    }

    protected void initializeFragments(JSONObject response) {
        // Override to initialize fragments (first batch of info arrived)
    }

    protected void updateFragments(JSONObject response) {
        // Override to push new data to fragments.
    }

    protected void initializeFragments(JSONArray response) {
        // Override to initialize fragments (first batch of info arrived)
    }

    protected void updateFragments(JSONArray response) {
        // Override to push new data to fragments.
    }

    private void refreshSettings() {
        SharedPreferences settings = getActivity().getSharedPreferences(MainViewActivity.SHAREDPREFS_FILE, 0);
        // Is 'keep screen on' enabled
        boolean isKeepScreenOn = settings.getBoolean("keep_on", false);
        if (isKeepScreenOn) {
            toggleKeepScreenOn();
        }

        // Refresh rate
        mRefreshRate = Integer.valueOf(settings.getString(getString(R.string.key_refresh_rate), "1000"));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_graphs, menu);
        mKeepOnItem = menu.findItem(R.id.item_keep_screen_on);
        mPauseItem = menu.findItem(R.id.item_resume_pause);
        refreshSettings();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_clear: // Clear graphs
                clearGraphs();
                return true;
            case R.id.item_resume_pause: // Toggle graph updates
                toggleBackgroundWork();
                return true;
            case R.id.item_keep_screen_on:
                toggleKeepScreenOn();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleKeepScreenOn() {
        if (mKeepOnItem.isChecked()) { // Disable
            mKeepOnItem.setChecked(false);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else { // Enable
            mKeepOnItem.setChecked(true);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void clearGraphs() {
        for (GraphFragment fragment : mFragments) {
            fragment.clearGraphs();
        }
    }

    /**
     * Shows an error dialog
     *
     * @param error
     */
    protected void showErrorDialog(int error) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(getActivity());
        dlgAlert.setMessage(getResources().getString(error));
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    /**
     * Toggle updates
     */
    protected void toggleBackgroundWork() {
        if (mPaused) {
            mPauseItem.setIcon(R.drawable.ic_action_pause);
            mPaused = false;
            mWorking = true;
            mLastUpdateFinished = true;
            mTask = new ChartTask();
            mTask.execute();
        } else {
            mPauseItem.setIcon(R.drawable.ic_action_play);
            mPaused = true;
            mWorking = false;
            mTask.cancel(true);
        }
    }

    /**
     * Stop updates when pausing
     */
    @Override
    public void onPause() {
        if (mTask != null) mTask.cancel(true);
        mClient.cancelRequests(getActivity(), true);
        mWorking = false;

        // Store keep screen on setting
        if (mKeepOnItem != null) {
            SharedPreferences settings = getActivity().getSharedPreferences(MainViewActivity.SHAREDPREFS_FILE, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("keep_on", mKeepOnItem.isChecked());
            editor.commit();
        }

        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onPause();
    }

    /**
     * Restart updates if needed and refresh settings.
     */
    @Override
    public void onResume() {
        super.onResume();

        if (!mPaused && mInitialized) {
            mWorking = true;
            mTask = new ChartTask();
            mTask.execute();
        }
    }


    /**
     * Handles first batch of data and creates the graphs. Also starts the AsyncTask that handles
     * all updates.
     */
    private class FirstGraphsHttpResponseHandler extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(JSONObject response) {
            initializeFragments(response);
            mInitialized = true;
            mTask = new ChartTask();
            mTask.execute();
        }

        @Override
        public void onSuccess(JSONArray response) {
            initializeFragments(response);
            mTask = new ChartTask();
            mTask.execute();
        }

        @Override
        public void onFailure(Throwable e, JSONObject response) {
            showErrorDialog(R.string.error_could_not_connect);
        }
    }

    private class UpdateGraphsHttpResponseHandler extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(JSONObject response) {
            mLastUpdateFinished = true;
            updateFragments(response);
        }

        @Override
        public void onSuccess(JSONArray response) {
            updateFragments(response);
        }

        @Override
        public void onFailure(Throwable e, JSONObject response) {
            toggleBackgroundWork();
            showErrorDialog(R.string.error_could_not_connect);
        }
    }

    /**
     * Updates the graphs.
     */
    private class ChartTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            while (mWorking) {
                try {
                    Thread.sleep(mRefreshRate);
                    publishProgress();
                } catch (InterruptedException e) {
                    return null;
                }
            }
            return null;
        }

        // Plotting generated data in the graph
        @Override
        protected void onProgressUpdate(Void... values) {
            if (mLastUpdateFinished) {
                mClient.get(mUrl, mParams, new UpdateGraphsHttpResponseHandler());
                mLastUpdateFinished = false;
            }
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final boolean mDoubleGraph;

        public SectionsPagerAdapter(FragmentManager fm, boolean doubleGraph) {
            super(fm);
            mDoubleGraph = doubleGraph;
        }

        @Override
        public Fragment getItem(int position) {
            return mDoubleGraph ? mFragmentsTablet[position] : mFragments[position];
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            return getFragmentName(position).toUpperCase(l);
        }
    }
}