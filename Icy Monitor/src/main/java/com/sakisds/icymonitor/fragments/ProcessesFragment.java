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

package com.sakisds.icymonitor.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sakisds.icymonitor.R;
import com.sakisds.icymonitor.activities.MainViewActivity;
import com.sakisds.icymonitor.dataobj.ProcessInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Processes Fragment
 * Created by Thanasis Georgiou on 11/06/13.
 */
public class ProcessesFragment extends ListFragment implements OnRefreshListener, View.OnClickListener {

    private String mSort = "usage";

    private TextView mTxtUsedMem, mTxtTotalMem, mTxtPercent;
    private ProgressBar mMemUsageProgress;
    private View mHeader, mError, mProgressBar;

    private int mTotalMemory;

    private PullToRefreshLayout mPullToRefreshLayout;

    private SharedPreferences mSettings;

    private boolean mFirstTime = true;

    public ProcessesFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_processes, container, false);

        mSettings = getActivity().getSharedPreferences(MainViewActivity.SHAREDPREFS_FILE, Context.MODE_PRIVATE);

        mMemUsageProgress = (ProgressBar) rootView.findViewById(R.id.progressBar_memory);
        mTxtPercent = (TextView) rootView.findViewById(R.id.textView_percent);
        mTxtTotalMem = (TextView) rootView.findViewById(R.id.textView_total);
        mTxtUsedMem = (TextView) rootView.findViewById(R.id.textView_free);

        mHeader = rootView.findViewById(R.id.process_header);
        mError = rootView.findViewById(R.id.emptyhidden);
        mError.setOnClickListener(this);

        mProgressBar = rootView.findViewById(R.id.progressBar_list);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewGroup viewGroup = (ViewGroup) view;

        mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());

        ActionBarPullToRefresh.from(getActivity())
                .insertLayoutInto(viewGroup)
                .theseChildrenArePullable(android.R.id.list, android.R.id.empty)
                .listener(this)
                .setup(mPullToRefreshLayout);

        // Refresh fragment data
        refreshData();
    }

    private void refreshData() {
        // Request data
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(2, 2000);

        String url = getActivity().getIntent().getStringExtra(MainViewActivity.EXTRA_ADDRESS) + "/data";
        RequestParams params = new RequestParams();
        params.put("type", "processes");
        params.put("sort", mSort);
        params.put("id", String.valueOf(mSettings.getLong("device_id", -2)));

        client.get(url, params, new ProcessesHttpResponseHandler());
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_processes, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_sort_by_name:
                mSort = "name";
                mPullToRefreshLayout.setRefreshing(true);
                refreshData();
                item.setChecked(true);
                return true;
            case R.id.item_sort_by_usage:
                mSort = "usage";
                mPullToRefreshLayout.setRefreshing(true);
                refreshData();
                item.setChecked(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefreshStarted(View view) {
        refreshData();
    }

    @Override
    public void onClick(View view) {
        mPullToRefreshLayout.setRefreshing(true);
        refreshData();
    }

    private class ProcessListArrayAdapter extends ArrayAdapter<ProcessInfo> {

        private final ProcessInfo[] mProcessInfo;
        private final Context mContext;

        public ProcessListArrayAdapter(Context context, ProcessInfo[] processes) {
            super(context, R.layout.list_item_process, processes);
            mProcessInfo = processes;
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_item_process, parent, false);

            // Set data
            ((TextView) rowView.findViewById(R.id.textView_name)).setText(mProcessInfo[position].getName());
            ((TextView) rowView.findViewById(R.id.textView_usage)).setText(mProcessInfo[position].getUsage() + "MB");

            int percent = (int) Math.round(mProcessInfo[position].getUsage() / (mTotalMemory / 100));

            if (percent > 0) {
                ((ProgressBar) rowView.findViewById(R.id.progressBar_process)).setProgress(percent);
                ((TextView) rowView.findViewById(R.id.textView_percent)).setText(percent + "%");
            } else {
                ((ProgressBar) rowView.findViewById(R.id.progressBar_process)).setProgress(1);
                ((TextView) rowView.findViewById(R.id.textView_percent)).setText("<1%");
            }

            // Return view to be displayed
            return rowView;
        }
    }

    private class ProcessesHttpResponseHandler extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(JSONObject response) {
            // Parse data
            int usagePercent;
            ProcessInfo[] processInfo;

            try {
                usagePercent = response.getInt("UsedMem");
                mTotalMemory = response.getInt("TotalMem");

                JSONArray processes = response.getJSONArray("Processes");
                processInfo = new ProcessInfo[processes.length()];

                for (int i = 0; i < processes.length(); i++) {
                    JSONObject process = processes.getJSONObject(i);
                    processInfo[i] = new ProcessInfo(process.getString("Name"), process.getDouble("Mem"));
                }

                int usedMemoryMB = (mTotalMemory / 100) * usagePercent;

                // Set views
                mMemUsageProgress.setProgress(usagePercent);
                mTxtPercent.setText(usagePercent + "%");
                mTxtTotalMem.setText(String.format(getString(R.string.total_memory), mTotalMemory));
                mTxtUsedMem.setText(String.format(getString(R.string.used_memory), usedMemoryMB));
                mHeader.setVisibility(View.VISIBLE);
                mError.setVisibility(View.INVISIBLE);

                // Set list array
                if (!mFirstTime) getListView().setVisibility(View.INVISIBLE);
                setListAdapter(new ProcessListArrayAdapter(getActivity(), processInfo));
                if (mFirstTime) mFirstTime = false;
                else getListView().setVisibility(View.VISIBLE);

                // End refreshing animation
                mPullToRefreshLayout.setRefreshComplete();
            } catch (JSONException e) {
                // Empty list and show error
                showError();
            }
        }

        public void onFailure(Throwable e, JSONObject response) {
            showError();
        }
    }

    private void showError() {
        // Empty list and show error
        mHeader.setVisibility(View.GONE);
        mError.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        setListAdapter(new ProcessListArrayAdapter(getActivity(), new ProcessInfo[0]));

        // End refreshing animation
        mPullToRefreshLayout.setRefreshComplete();
    }
}
