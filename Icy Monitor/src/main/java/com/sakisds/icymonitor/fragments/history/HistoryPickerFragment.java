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

package com.sakisds.icymonitor.fragments.history;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sakisds.icymonitor.R;
import com.sakisds.icymonitor.activities.MainViewActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * History Picker fragment.
 * Pick a history file to receive from server.
 * Created by Thanasis Georgiou on 15/12/13.
 */
public class HistoryPickerFragment extends ListFragment implements OnRefreshListener, View.OnClickListener {
    private PullToRefreshLayout mPullToRefreshLayout;
    private View mEmptyView, mProgressBar;

    private SharedPreferences mSettings;

    private AsyncHttpClient mClient = new AsyncHttpClient();

    private DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd_kk-mm", Locale.getDefault());
    private DateFormat mFriendlyDateFormat = new SimpleDateFormat("yyyy/MM/dd kk:mm", Locale.getDefault());

    private String mSort = "true";

    private Boolean mFirstTime = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_list, container, false);
        mSettings = getActivity().getSharedPreferences(MainViewActivity.SHAREDPREFS_FILE, Context.MODE_PRIVATE);
        setHasOptionsMenu(true);
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

        mClient.setMaxRetriesAndTimeout(2, 2000);

        // Refresh fragment data
        refreshData();
    }

    /**
     * Refresh the list.
     */
    private void refreshData() {
        String url = getActivity().getIntent().getStringExtra(MainViewActivity.EXTRA_ADDRESS) + "/data";
        RequestParams params = new RequestParams();
        params.put("type", "historylist");
        params.put("sort", mSort);
        params.put("id", String.valueOf(mSettings.getLong("device_id", -2)));

        mClient.get(url, params, new HistoryPickerHttpResponseHandler());
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_historypicker, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_sort_newestFirst:
                mSort = "true";
                mPullToRefreshLayout.setRefreshing(true);
                refreshData();
                item.setChecked(true);
                return true;
            case R.id.item_sort_oldestFirst:
                mSort = "false";
                mPullToRefreshLayout.setRefreshing(true);
                refreshData();
                item.setChecked(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Opens a HistoryGraphFragment when a listitem is pressed.
     *
     * @param listView
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        String file = ((HistoryListArrayAdapter) getListAdapter()).mHistoryFiles[position][2];

        ((MainViewActivity) getActivity()).openHistoryFile(file);
    }

    private class HistoryListArrayAdapter extends ArrayAdapter<String[]> {

        private final String[][] mHistoryFiles;
        private final Context mContext;

        public HistoryListArrayAdapter(Context context, String[][] historyFiles) {
            super(context, R.layout.list_item_historyfile, historyFiles);
            mHistoryFiles = historyFiles;
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_item_historyfile, parent, false);

            // Set data
            TextView textView = (TextView) rowView.findViewById(R.id.textView_start);
            textView.setText(String.format(getString(R.string.startAt), mHistoryFiles[position][0], mHistoryFiles[position][1]));
            textView.setTag(mHistoryFiles[position][2]);

            // Return view to be displayed
            return rowView;
        }
    }

    private class HistoryPickerHttpResponseHandler extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(JSONArray response) {
            try {
                int length = response.length();
                String[][] historyFiles = new String[length][3];

                for (int i = 0; i < length; i++) {
                    JSONArray file = response.getJSONArray(i);
                    historyFiles[i][0] = mFriendlyDateFormat.format(mDateFormat.parse(file.getString(0)));
                    historyFiles[i][1] = file.getString(1);
                    historyFiles[i][2] = file.getString(0);
                }

                // Set list array
                if (length == 0) {
                    mProgressBar.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                    mEmptyView.findViewById(R.id.imageView_error).setVisibility(View.GONE);
                    ((TextView) mEmptyView.findViewById(R.id.textView_error)).setText("No history files found");
                }

                if (!mFirstTime) getListView().setVisibility(View.INVISIBLE);
                setListAdapter(new HistoryListArrayAdapter(getActivity(), historyFiles));
                if (mFirstTime) mFirstTime = false;
                else getListView().setVisibility(View.VISIBLE);

                mPullToRefreshLayout.setRefreshComplete();
            } catch (JSONException e) {
                // Empty list and set error
                showError(getString(R.string.error_invalid_response));
            } catch (ParseException e) {
                showError(getString(R.string.error_invalid_response));
            }
        }

        public void onFailure(Throwable e, JSONObject response) {
            showError(getString(R.string.error_no_refresh));
        }
    }

    private void showError(String text) {
        setListAdapter(new HistoryListArrayAdapter(getActivity(), new String[0][0]));

        mProgressBar.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
        ((TextView) mEmptyView.findViewById(R.id.textView_error)).setText(text);

        mPullToRefreshLayout.setRefreshComplete();
    }

}
