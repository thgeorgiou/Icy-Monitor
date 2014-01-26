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
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sakisds.icymonitor.R;
import com.sakisds.icymonitor.activities.MainViewActivity;
import com.sakisds.icymonitor.dataobj.DiskTempInfo;
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
public class DiskTempFragment extends ListFragment implements OnRefreshListener, View.OnClickListener {

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
        params.put("type", "disks");
        params.put("id", String.valueOf(mSettings.getLong("device_id", -2)));

        client.get(url, params, new DisksTempHttpResponseHandler());
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

    private class DisksTempListArrayAdapter extends ArrayAdapter<DiskTempInfo> {

        private final DiskTempInfo[] mDisksInfo;
        private final Context mContext;

        public DisksTempListArrayAdapter(Context context, DiskTempInfo[] disks) {
            super(context, R.layout.list_item_disktemp, disks);
            mDisksInfo = disks;
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_item_disktemp, parent, false);

            // Set data
            DiskTempInfo disk = mDisksInfo[position];

            ((TextView) rowView.findViewById(R.id.textView_name)).setText(disk.getName());
            ((TextView) rowView.findViewById(R.id.textView_temp)).setText(disk.getTemp() + " °C");

            // Return view to be displayed
            return rowView;
        }
    }

    private class DisksTempHttpResponseHandler extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(JSONArray response) {
            mProgressBar.setVisibility(View.GONE);

            DiskTempInfo[] diskInfo;

            try {
                int length = response.length();
                diskInfo = new DiskTempInfo[length];

                for (int i = 0; i < length; i++) {
                    JSONObject data = response.getJSONObject(i);
                    DiskTempInfo disk = new DiskTempInfo(data.getString("Name"), (float) data.getDouble("Temp"));
                    diskInfo[i] = disk;
                }

                // Set list array
                if (!mFirstTime) getListView().setVisibility(View.INVISIBLE);
                setListAdapter(new DisksTempListArrayAdapter(getActivity(), diskInfo));
                if (mFirstTime) mFirstTime = false;
                else getListView().setVisibility(View.VISIBLE);

                mPullToRefreshLayout.setRefreshComplete();
            } catch (JSONException e) {
                showError();
            }
        }

        public void onFailure(Throwable e, JSONObject response) {
            showError();
        }

        private void showError() {
            // Empty list and set error
            mProgressBar.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
            setListAdapter(new DisksTempListArrayAdapter(getActivity(), new DiskTempInfo[0]));
            mPullToRefreshLayout.setRefreshComplete();
        }
    }

}
