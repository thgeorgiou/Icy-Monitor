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

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.*;
import android.widget.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sakisds.icymonitor.ComputerInfo;
import com.sakisds.icymonitor.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * First activity presented to user.
 * Show a list of servers and let the user pick which one to connect to.
 * Created by sakisds on 23/05/13.
 */
public class ConnectionActivity extends ListActivity {
    public final static String ACCEPTED_SERVER_VERSION = "1.0";

    private SharedPreferences mSettings;
    private ComputerInfo[] mData;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_connection);
        mSettings = getSharedPreferences(MainViewActivity.SHAREDPREFS_FILE, 0);

        refreshListAdapter();

        ListView listView = getListView();

        listView.setLongClickable(true);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new MultipleChoiceListener());

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                String url = "http://" + ((TextView) v.findViewById(R.id.text_address)).getText().toString();
                connect(url);
            }
        });
    }

    /**
     * Connect to URL
     *
     * @param url URL to connect.
     */
    private void connect(final String url) {
        // Create client
        AsyncHttpClient client = new AsyncHttpClient();

        // Create a dialog
        final ProgressDialog progress = ProgressDialog.show(this, "", getResources().getString(R.string.connecting), false);

        // Send request
        client.get(url + "/about", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                progress.dismiss();
                try {
                    String version = response.getString("Version");
                    if (version.equals(ACCEPTED_SERVER_VERSION)) {
                        Intent intent = new Intent(getBaseContext(), MainViewActivity.class);
                        intent.putExtra(MainViewActivity.EXTRA_ADDRESS, url);
                        startActivity(intent);
                    } else {
                        showErrorDialog(R.string.error_outdated_server);
                    }
                } catch (JSONException e) {
                    showErrorDialog(R.string.error_invalid_response);
                }
            }

            @Override
            public void onFailure(Throwable e, String response) {
                progress.dismiss();
                showErrorDialog(R.string.error_could_not_connect);
            }
        });
    }

    /**
     * Refresh the list.
     */
    private void refreshListAdapter() {
        int serverCount = mSettings.getInt("serverCount", 0);
        // Populate data
        mData = new ComputerInfo[serverCount];
        for (int i = 0; i < serverCount; i++) {
            mData[i] = new ComputerInfo(mSettings.getString("server_" + String.valueOf(i) + "_name", "Null"),
                    mSettings.getString("server_" + String.valueOf(i) + "_address", "Null"),
                    mSettings.getInt("server_" + String.valueOf(i) + "_type", 0));
        }
        // Set adapter
        setListAdapter(new ComputersListArrayAdapter(this, mData));

    }

    @Override
    public void onResume() {
        refreshListAdapter();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_connection_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_add) {
            startActivity(new Intent(this, AddServerActivity.class));
            return true;
        } else if (item.getItemId() == R.id.item_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void showErrorDialog(int string) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage(getResources().getString(string));
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private void deleteSelectedItems(final SparseBooleanArray checked) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.sure)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ListView listView = getListView();

                        int cntChoice = listView.getCount();

                        int removedCount = 0;

                        for (int i = 0; i < cntChoice; i++) {
                            if (checked.get(i)) {
                                mData[i].setToBeRemoved();
                                removedCount++;
                            }
                        }

                        List resultData = new LinkedList();
                        for (ComputerInfo comp : mData) {
                            if (!comp.isToBeRemoved()) {
                                //noinspection unchecked
                                resultData.add(comp);
                            }
                        }

                        int newLen = mData.length - removedCount;
                        Log.println(Log.INFO, "newlen", String.valueOf(newLen));

                        SharedPreferences.Editor editor = mSettings.edit();
                        editor.putInt("serverCount", newLen);

                        ComputerInfo[] names = (ComputerInfo[]) resultData.toArray(mData);

                        // Save to settings
                        for (int i = 0; i < newLen; i++) {
                            editor.putString("server_" + String.valueOf(i) + "_name", names[i].getName());
                            editor.putString("server_" + String.valueOf(i) + "_address", names[i].getAddress());
                            editor.putInt("server_" + String.valueOf(i) + "_type", names[i].getType());
                        }
                        editor.commit();

                        refreshListAdapter();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private class ComputersListArrayAdapter extends ArrayAdapter<ComputerInfo> {

        private final Context mContext;

        public ComputersListArrayAdapter(Context context, ComputerInfo[] data) {
            super(context, R.layout.list_item_computer, data);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_item_computer, parent, false);

            ((TextView) rowView.findViewById(R.id.text_name)).setText(mData[position].getName());
            ((TextView) rowView.findViewById(R.id.text_address)).setText(mData[position].getAddress().substring(7));
            ImageView imageViewType = (ImageView) rowView.findViewById(R.id.icon);
            switch (mData[position].getType()) {
                case 0:
                    imageViewType.setImageResource(R.drawable.ic_type_desktop);
                    break;
                case 1:
                    imageViewType.setImageResource(R.drawable.ic_type_laptop);
                    break;
                case 2:
                    imageViewType.setImageResource(R.drawable.ic_type_server);
                    break;
            }

            return rowView;
        }
    }

    private class MultipleChoiceListener implements ListView.MultiChoiceModeListener {
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            final int checkedCount = getListView().getCheckedItemCount();
            switch (checkedCount) {
                case 0:
                    mode.setTitle(null);
                    break;
                case 1:
                    mode.setTitle("One item selected");
                    break;
                default:
                    mode.setTitle("" + checkedCount + " items selected");
                    break;
            }
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // Respond to clicks on the actions in the CAB
            switch (item.getItemId()) {
                case R.id.item_delete:
                    SparseBooleanArray checked = getListView().getCheckedItemPositions().clone();
                    deleteSelectedItems(checked);
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate the menu for the CAB
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_delete, menu);
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