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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.SparseBooleanArray;
import android.view.*;
import android.widget.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sakisds.icymonitor.R;
import com.sakisds.icymonitor.activities.AddNotificationActivity;
import com.sakisds.icymonitor.activities.MainViewActivity;
import com.sakisds.icymonitor.dataobj.NotificationInfo;
import org.apache.http.Header;
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
public class NotificationsFragment extends ListFragment implements OnRefreshListener, View.OnClickListener {

    private View mError, mProgressBar;

    private PullToRefreshLayout mPullToRefreshLayout;

    private SharedPreferences mSettings;

    private boolean mFirstTime = true;

    public NotificationsFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        mSettings = getActivity().getSharedPreferences(MainViewActivity.SHAREDPREFS_FILE, Context.MODE_PRIVATE);

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

        // List view properties
        getListView().setLongClickable(true);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setMultiChoiceModeListener(new MultipleChoiceListener());
    }

    @Override
    public void onResume() {
        super.onResume();
        mPullToRefreshLayout.setRefreshing(true);
        refreshData();
    }

    private void refreshData() {
        // Request data
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(2, 2000);

        String url = getActivity().getIntent().getStringExtra(MainViewActivity.EXTRA_ADDRESS) + "/data";
        RequestParams params = new RequestParams();
        params.put("type", "listnotif");
        params.put("id", String.valueOf(mSettings.getLong("device_id", -2)));

        client.get(url, params, new NotificationsHttpResponseHandler());
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_notifications, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_test_notification:
                AsyncHttpClient client = new AsyncHttpClient();

                String url = getActivity().getIntent().getStringExtra(MainViewActivity.EXTRA_ADDRESS) + "/data";
                RequestParams params = new RequestParams();
                params.put("type", "testnotif");
                params.put("id", String.valueOf(mSettings.getLong("device_id", -2)));

                client.get(url, params, new TextHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                        Toast.makeText(getActivity(), "Test notification sent", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String responseBody, Throwable error) {
                        Toast.makeText(getActivity(), "Test notification could not be sent: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                return true;
            case R.id.item_add:
                Intent intent = new Intent(getActivity(), AddNotificationActivity.class);
                intent.putExtra(MainViewActivity.EXTRA_ADDRESS,
                        getActivity().getIntent().getStringExtra(MainViewActivity.EXTRA_ADDRESS) + "/data");

                startActivity(intent);
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

    private class NotificationsListArrayAdapter extends ArrayAdapter<NotificationInfo> {

        private final NotificationInfo[] mNotificationInfo;
        private final Context mContext;

        public NotificationsListArrayAdapter(Context context, NotificationInfo[] notifications) {
            super(context, R.layout.list_item_process, notifications);
            mNotificationInfo = notifications;
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_item_notification, parent, false);

            // Set data
            String sensor = String.format(mNotificationInfo[position].getNotificationName() + " (" + mNotificationInfo[position].getNotificationType() + ")");
            String condition = String.format(getString(R.string.notif_condition),
                    mNotificationInfo[position].getCondition(), mNotificationInfo[position].getNotificationValue());

            ((TextView) rowView.findViewById(R.id.textView_sensor)).setText(sensor);
            ((TextView) rowView.findViewById(R.id.textView_condition)).setText(condition);

            // Return view to be displayed
            return rowView;
        }
    }

    private class NotificationsHttpResponseHandler extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(JSONArray response) {
            // Parse data
            NotificationInfo[] notificationInfo;

            try {
                notificationInfo = new NotificationInfo[response.length()];

                if (response.length() == 0) {
                    ((ImageView) mError.findViewById(R.id.imageView_error)).setImageResource(R.drawable.ic_empty_list_notifications);
                    ((TextView) mError.findViewById(R.id.textView_error)).setText(getString(R.string.empty_list_notifications));
                    mError.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject notification = response.getJSONObject(i);

                        notificationInfo[i] = new NotificationInfo(notification.getString("Name"), notification.getString("Type"),
                                notification.getString("Condition"), notification.getString("Value"), notification.getBoolean("RingOnce"));
                    }

                    // Set list array
                    if (!mFirstTime) getListView().setVisibility(View.INVISIBLE);
                    if (mFirstTime) mFirstTime = false;
                    else getListView().setVisibility(View.VISIBLE);
                }

                // Set list adapter
                setListAdapter(new NotificationsListArrayAdapter(getActivity(), notificationInfo));
                // End refreshing animation
                mPullToRefreshLayout.setRefreshComplete();
            } catch (JSONException e) {
                // Empty list and show error
                showError();
            }
        }

        @Override
        public void onFailure(Throwable e, JSONObject response) {
            showError();
        }
    }

    private void showError() {
        // Empty list and show error
        ((ImageView) mError.findViewById(R.id.imageView_error)).setImageResource(R.drawable.ic_empty_list_error);
        ((TextView) mError.findViewById(R.id.textView_error)).setText(getString(R.string.disks_empty_list));
        mError.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        setListAdapter(new NotificationsListArrayAdapter(getActivity(), new NotificationInfo[0]));

        // End refreshing animation
        mPullToRefreshLayout.setRefreshComplete();
    }

    private class MultipleChoiceListener implements ListView.MultiChoiceModeListener {
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            final int checkedCount = getListView().getCheckedItemCount();

            final String title = getResources().getQuantityString(R.plurals.notificationSelected, checkedCount, checkedCount);
            mode.setTitle(title);
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // Respond to clicks on the actions in the CAB
            if (item.getItemId() == R.id.item_delete) {
                SparseBooleanArray checked = getListView().getCheckedItemPositions().clone();

                int size = getListView().getCount();
                boolean first = true;
                String index = "";
                for (int i = 0; i < size; i++) {
                    if (checked.get(i)) {
                        if (first) {
                            first = false;
                            index = String.valueOf(i);
                        } else {
                            index += ";" + i;
                        }
                    }
                }

                // Request data
                AsyncHttpClient client = new AsyncHttpClient();

                String url = getActivity().getIntent().getStringExtra(MainViewActivity.EXTRA_ADDRESS) + "/data";
                RequestParams params = new RequestParams();
                params.put("type", "remnotif");
                params.put("index", index);
                params.put("id", String.valueOf(mSettings.getLong("device_id", -2)));

                client.get(url, params, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        refreshData();
                    }

                    @Override
                    public void onFailure(Throwable e, String response) {
                        Toast.makeText(getActivity(), "Could not perform action. Please try again", Toast.LENGTH_LONG).show();
                        refreshData();
                    }
                });

                mode.finish(); // Action picked, so close the CAB
                return true;
            }
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate the menu for the CAB
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_computers, menu);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }
    }
}
