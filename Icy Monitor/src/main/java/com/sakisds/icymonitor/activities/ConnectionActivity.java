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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.*;
import android.widget.*;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sakisds.icymonitor.R;
import com.sakisds.icymonitor.dataobj.ComputerInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

/**
 * First activity presented to user.
 * Show a list of servers and let the user pick which one to connect to.
 * Created by sakisds on 23/05/13.
 */
public class ConnectionActivity extends ListActivity {
    public final static String ACCEPTED_SERVER_VERSION = "2.0.1";
    public final static String UPDATE_SERVER_VERSION = "2.0";

    private SharedPreferences mSettings;
    private ComputerInfo[] mData;

    private AsyncHttpClient mClient = new AsyncHttpClient();

    // GCM stuff
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_ON_SERVER_EXPIRATION_TIME = "onServerExpirationTimeMs";
    public static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 14; // 14 days
    String SENDER_ID = "703448116465";

    GoogleCloudMessaging gcm;
    Context context;
    String regid;

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

        // Http Client
        mClient.setMaxRetriesAndTimeout(2, 2000);

        // Setup GCM
        context = getApplicationContext();
        regid = getRegistrationId(context);

        if (regid.length() == 0) {
            registerBackground();
        }
        gcm = GoogleCloudMessaging.getInstance(this);
    }

    /**
     * Gets the current registration id for application on GCM service.
     * <p/>
     * If result is empty, the registration has failed.
     *
     * @return registration id, or empty string if the registration is not
     * complete.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.length() == 0) {
            Log.v("gcm", "Registration not found.");
            return "";
        }
        // check if app was updated; if so, it must clear registration id to
        // avoid a race condition if GCM sends a message
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion || isRegistrationExpired()) {
            Log.v("gcm", "App version changed or registration expired.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences(ConnectionActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Checks if the registration has expired.
     * <p/>
     * <p>To avoid the scenario where the device sends the registration to the
     * server but the server loses it, the app developer may choose to re-register
     * after REGISTRATION_EXPIRY_TIME_MS.
     *
     * @return true if the registration has expired.
     */
    private boolean isRegistrationExpired() {
        final SharedPreferences prefs = getGCMPreferences(context);
        // checks if the information is not stale
        long expirationTime =
                prefs.getLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, -1);
        return System.currentTimeMillis() > expirationTime;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration id, app versionCode, and expiration time in the
     * application's shared preferences.
     */
    private void registerBackground() {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration id=" + regid;
                    setRegistrationId(context, regid);
                } catch (IOException ex) {
                }
                return null;
            }
        }.execute(null, null, null);
    }

    /**
     * Stores the registration id, app versionCode, and expiration time in the
     * application's {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration id
     */
    private void setRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.v("gcm", "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        long expirationTime = System.currentTimeMillis() + REGISTRATION_EXPIRY_TIME_MS;

        Log.v("gcm", "Setting registration expiry time to " + new Timestamp(expirationTime));
        editor.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
        editor.commit();
    }

    /**
     * Connect to URL
     *
     * @param url URL to connect.
     */
    private void connect(final String url) {
        // Create client
        RequestParams params = new RequestParams("id", mSettings.getLong("device_id", -1));

        // Create a dialog
        final ProgressDialog progress = ProgressDialog.show(this, "", getResources().getString(R.string.connecting), false);

        final Context context = this;
        // Send request
        mClient.get(url + "/auth", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                progress.dismiss();
                try {
                    String version = response.getString("Version");
                    if (version.equals(ACCEPTED_SERVER_VERSION)) {
                        String auth = response.getString("Auth");
                        if (auth.equals("OK")) {
                            Intent intent = new Intent(getBaseContext(), MainViewActivity.class);
                            intent.putExtra(MainViewActivity.EXTRA_ADDRESS, url);
                            startActivity(intent);
                        } else {
                            showErrorDialog(R.string.error_could_not_connect_title, R.string.error_authentication);
                        }
                    } else if (version.equals(UPDATE_SERVER_VERSION)) {
                        showErrorDialog(R.string.error_could_not_connect_title, R.string.error_outdated_server_update);
                    } else {
                        showErrorDialog(R.string.error_outdated_server, R.string.error_outdated_server_long);
                    }
                } catch (JSONException e) {
                    showErrorDialog(R.string.error_could_not_connect_title, R.string.error_invalid_response);
                }
            }

            @Override
            public void onFailure(Throwable e, JSONObject response) {
                progress.dismiss();
                showErrorDialog(R.string.error_could_not_connect_title, R.string.error_could_not_connect);
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
        switch (item.getItemId()) {
            case R.id.item_add:
                startActivity(new Intent(this, DetectServerActivity.class));
                return true;
            case R.id.item_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.item_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void showErrorDialog(int title, int message) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setTitle(getResources().getString(title));
        dlgAlert.setMessage(getResources().getString(message));
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private void deleteSelectedItems(final SparseBooleanArray checked) {
        String message = getResources().getQuantityString(R.plurals.deleteComputers, checked.size());

        new AlertDialog.Builder(this)
                .setMessage(message)
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

            final String title = getResources().getQuantityString(R.plurals.computerSelected, checkedCount, checkedCount);
            mode.setTitle(title);
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