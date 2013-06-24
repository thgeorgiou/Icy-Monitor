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
import android.widget.TextView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sakisds.icymonitor.DiskInfo;
import com.sakisds.icymonitor.R;
import com.sakisds.icymonitor.activities.MainViewActivity;
import com.sakisds.icymonitor.views.HoloCircularProgressBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Disk fragment.
 * Created by sakisds on 18/06/13.
 */
public class DiskFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_disks, container, false);

        setHasOptionsMenu(true);

        refreshData();

        return mRootView;
    }

    /**
     * Refresh the list.
     */
    private void refreshData() {
        setListAdapter(null);

        AsyncHttpClient client = new AsyncHttpClient();

        String url = getActivity().getIntent().getStringExtra(MainViewActivity.EXTRA_ADDRESS) + "/data";
        RequestParams params = new RequestParams();
        params.put("type", "disks");

        client.get(url, params, new DisksHttpResponseHandler());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_disks, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_refresh) {
            refreshData();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            long usedPercent = Math.round(used * 100);

            ((TextView) rowView.findViewById(R.id.textView_drive_name)).setText(disk.getName());
            ((TextView) rowView.findViewById(R.id.textView_label)).setText(disk.getLabel());
            ((TextView) rowView.findViewById(R.id.textView_size)).setText(disk.getSize() + "GB");
            ((TextView) rowView.findViewById(R.id.textView_free)).setText(disk.getFree() + "GB");
            ((TextView) rowView.findViewById(R.id.textView_disk_percent)).setText(usedPercent + "%");

            ((HoloCircularProgressBar) rowView.findViewById(R.id.diskProgress)).setProgress(used);

            // Return view to be displayed
            return rowView;
        }
    }

    private class DisksHttpResponseHandler extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(JSONArray response) {
            DiskInfo[] diskInfo = null;

            try {
                int length = response.length();
                diskInfo = new DiskInfo[length];

                for (int i = 0; i < length; i++) {
                    JSONObject data = response.getJSONObject(i);
                    DiskInfo disk = new DiskInfo("Drive " + data.getString("Name"), data.getString("Label"),
                            data.getString("Format"), (float) data.getDouble("Size"), (float) data.getDouble("Free"));
                    diskInfo[i] = disk;
                }
            } catch (JSONException e) {
                // Empty list and set error
                DiskInfo[] dummyInfo = new DiskInfo[1];
                dummyInfo[0] = new DiskInfo("Invalid Response", "--", "--", 0, 0);
                setListAdapter(new DisksListArrayAdapter(getActivity(), dummyInfo));
            }

            // Set list array
            setListAdapter(new DisksListArrayAdapter(getActivity(), diskInfo));
        }

        public void onFailure(Throwable e, String response) {
            // Empty list and set error
            DiskInfo[] dummyInfo = new DiskInfo[1];
            dummyInfo[0] = new DiskInfo("Connection Error", "--", "--", 0, 0);
            setListAdapter(new DisksListArrayAdapter(getActivity(), dummyInfo));
        }
    }

}
