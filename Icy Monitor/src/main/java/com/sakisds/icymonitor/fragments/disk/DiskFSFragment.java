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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sakisds.icymonitor.R;
import com.sakisds.icymonitor.activities.MainViewActivity;
import com.sakisds.icymonitor.dataobj.DiskInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Disk fragment.
 * Created by sakisds on 18/06/13.
 */
public class DiskFSFragment extends ListFragment implements OnRefreshListener, View.OnClickListener {

    private PullToRefreshLayout mPullToRefreshLayout;

    private SharedPreferences mSettings;

    private View mEmptyView, mProgressBar;

    private boolean mFirstTime = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_list, container, false);
        mSettings = getActivity().getSharedPreferences(MainViewActivity.SHAREDPREFS_FILE, Context.MODE_PRIVATE);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewGroup viewGroup = (ViewGroup) view;

        mEmptyView = view.findViewById(R.id.emptyhidden);
        mEmptyView.setOnClickListener(this);

        mProgressBar = view.findViewById(R.id.progressBar_list);

        mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());

        ActionBarPullToRefresh.from(getActivity())
                .insertLayoutInto(viewGroup)
                .theseChildrenArePullable(android.R.id.list, android.R.id.empty)
                .listener(this)
                .setup(mPullToRefreshLayout);

        // Refresh fragment data
        refreshData();
    }

    /**
     * Refresh the list.
     */
    private void refreshData() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(2, 2000);

        String url = getActivity().getIntent().getStringExtra(MainViewActivity.EXTRA_ADDRESS) + "/data";
        RequestParams params = new RequestParams();
        params.put("type", "fs");
        params.put("id", String.valueOf(mSettings.getLong("device_id", -2)));

        client.get(url, params, new DisksHttpResponseHandler());
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

    private class DisksListArrayAdapter extends ArrayAdapter<DiskInfo> {

        private final DiskInfo[] mDisksInfo;
        private final Context mContext;

        public DisksListArrayAdapter(Context context, DiskInfo[] disks) {
            super(context, R.layout.list_item_disk, disks);
            mDisksInfo = disks;
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_item_disk, parent, false);

            // Set data
            DiskInfo disk = mDisksInfo[position];

            float used = 1 - (disk.getFree() / disk.getSize());
            int usedPercent = Math.round(used * 100);

            if (disk.getLabel().equals("N/A")) {
                ((TextView) rowView.findViewById(R.id.textView_name)).setText(disk.getName());
            } else {
                ((TextView) rowView.findViewById(R.id.textView_name)).setText(disk.getName() + " - " + disk.getLabel());
            }

            Resources res = getResources();
            ((TextView) rowView.findViewById(R.id.textView_size)).setText(String.format(res.getString(R.string.size), disk.getSize()));
            ((TextView) rowView.findViewById(R.id.textView_free)).setText(String.format(res.getString(R.string.free), disk.getFree()));
            ((TextView) rowView.findViewById(R.id.textView_format)).setText(String.format(res.getString(R.string.format), disk.getFormat()));

            ProgressBar progressBar = (ProgressBar) rowView.findViewById(R.id.progressBar_disk);
            if (usedPercent > 0) {
                ((TextView) rowView.findViewById(R.id.textView_percent)).setText(usedPercent + "%");
                progressBar.setProgress(usedPercent);
            } else {
                ((TextView) rowView.findViewById(R.id.textView_percent)).setText("<" + usedPercent + "%");
                progressBar.setProgress(1);
            }

            if (usedPercent > 90) {
                progressBar.setBackgroundResource(R.drawable.progress_background_red);
            }

            // Return view to be displayed
            return rowView;
        }
    }

    private class DisksHttpResponseHandler extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(JSONArray response) {
            // Load temperature info
            mProgressBar.setVisibility(View.GONE);

            DiskInfo[] diskInfo = null;

            try {
                int length = response.length();
                diskInfo = new DiskInfo[length];

                for (int i = 0; i < length; i++) {
                    JSONObject data = response.getJSONObject(i);

                    String name = String.format(getResources().getString(R.string.drive), data.getString("Name"));
                    DiskInfo disk = new DiskInfo(name, data.getString("Label"),
                            data.getString("Format"), (float) data.getDouble("Size"), (float) data.getDouble("Free"));
                    diskInfo[i] = disk;
                }

                // Set list array
                if (!mFirstTime) getListView().setVisibility(View.INVISIBLE);
                setListAdapter(new DisksListArrayAdapter(getActivity(), diskInfo));
                if (mFirstTime) mFirstTime = false;
                else getListView().setVisibility(View.VISIBLE);

                mPullToRefreshLayout.setRefreshComplete();
            } catch (JSONException e) {
                // Empty list and set error
                mProgressBar.setVisibility(View.GONE);
                setListAdapter(new DisksListArrayAdapter(getActivity(), new DiskInfo[0]));
                mEmptyView.setVisibility(View.VISIBLE);
                mPullToRefreshLayout.setRefreshComplete();
            }
        }

        public void onFailure(Throwable e, JSONObject response) {
            // Empty list and set error
            mProgressBar.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
            setListAdapter(new DisksListArrayAdapter(getActivity(), new DiskInfo[0]));
            mPullToRefreshLayout.setRefreshComplete();
        }
    }
}
