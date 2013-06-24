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
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sakisds.icymonitor.ProcessInfo;
import com.sakisds.icymonitor.R;
import com.sakisds.icymonitor.activities.MainViewActivity;
import com.sakisds.icymonitor.views.HoloCircularProgressBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Processes Fragment
 * Created by Thanasis Georgiou on 11/06/13.
 */
public class ProcessesFragment extends ListFragment {

    private int mBarMax;
    private String mSort = "usage";

    private TextView mTxtUsedMem, mTxtTotalMem, mTxtPercent;
    private HoloCircularProgressBar mMemUsageProgress;

    public ProcessesFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_processes, container, false);

        mMemUsageProgress = (HoloCircularProgressBar) rootView.findViewById(R.id.progress_total_mem);
        mTxtPercent = (TextView) rootView.findViewById(R.id.txt_used_mem_per);
        mTxtTotalMem = (TextView) rootView.findViewById(R.id.txt_avail_mem);
        mTxtUsedMem = (TextView) rootView.findViewById(R.id.txt_used_mem);

        setHasOptionsMenu(true);
        refreshData();

        return rootView;
    }

    private void refreshData() {
        // Set loading
        setListAdapter(null);
        mTxtPercent.setText("--");
        mTxtTotalMem.setText("--");
        mTxtUsedMem.setText("--");
        mMemUsageProgress.setProgress(0.0f);

        // Request data
        AsyncHttpClient client = new AsyncHttpClient();

        String url = getActivity().getIntent().getStringExtra(MainViewActivity.EXTRA_ADDRESS) + "/data";
        RequestParams params = new RequestParams();
        params.put("type", "processes");
        params.put("sort", mSort);

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
                refreshData();
                item.setChecked(true);
                return true;
            case R.id.item_sort_by_usage:
                mSort = "usage";
                refreshData();
                item.setChecked(true);
                return true;
            case R.id.item_refresh:
                refreshData();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
            ((TextView) rowView.findViewById(R.id.text_process)).setText(mProcessInfo[position].getName());
            ((TextView) rowView.findViewById(R.id.text_usage_mb)).setText(mProcessInfo[position].getUsage() + "MB");

            ProgressBar progressBar = (ProgressBar) rowView.findViewById(R.id.progress_usage);
            progressBar.setMax(mBarMax);
            progressBar.setProgress((int) Math.round(mProcessInfo[position].getUsage()));

            // Return view to be displayed
            return rowView;
        }
    }

    private class ProcessesHttpResponseHandler extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(JSONObject response) {
            // Parse data
            int usagePercent = 0, totalMem = 0;
            ProcessInfo[] processInfo = null;

            try {
                usagePercent = response.getInt("UsedMem");
                totalMem = response.getInt("TotalMem");

                JSONArray processes = response.getJSONArray("Processes");
                processInfo = new ProcessInfo[processes.length()];

                for (int i = 0; i < processes.length(); i++) {
                    JSONObject process = processes.getJSONObject(i);
                    processInfo[i] = new ProcessInfo(process.getString("Name"), process.getDouble("Mem"));
                }
            } catch (JSONException e) {
                // Empty list and set error
                ProcessInfo[] dummyInfo = new ProcessInfo[1];
                dummyInfo[0] = new ProcessInfo("Invalid Response", 0);
                setListAdapter(new ProcessListArrayAdapter(getActivity(), dummyInfo));
            }

            int usedMemoryMB = (totalMem / 100) * usagePercent;

            // Store bar max
            mBarMax = totalMem;

            // Set views
            float progress = (float) usagePercent / 100;
            mMemUsageProgress.setProgress(progress);
            mTxtPercent.setText(usagePercent + "%");
            mTxtTotalMem.setText(totalMem + "MB");
            mTxtUsedMem.setText(usedMemoryMB + "MB");
            // Set list array
            setListAdapter(new ProcessListArrayAdapter(getActivity(), processInfo));
        }

        public void onFailure(Throwable e, String response) {
            // Empty list and set error
            ProcessInfo[] dummyInfo = new ProcessInfo[1];
            dummyInfo[0] = new ProcessInfo("Connection Error", 0);
            setListAdapter(new ProcessListArrayAdapter(getActivity(), dummyInfo));
        }
    }
}
