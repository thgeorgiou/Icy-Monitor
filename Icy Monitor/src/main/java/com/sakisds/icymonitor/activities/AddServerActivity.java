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

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sakisds.icymonitor.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Adds a new server.
 * Created by sakisds on 25/05/13.
 */
public class AddServerActivity extends Activity implements Spinner.OnItemSelectedListener, Button.OnClickListener {

    public static final String EXTRA_NAME = "addserver.extra.name";
    public static final String EXTRA_ADDRESS = "addserver.extra.address";

    private SharedPreferences mSettings;
    private ImageView mImageView_type;
    private EditText mEditText_name, mEditText_address, mEditText_port;
    private Spinner mSpinner;

    private String mGCMID;

    private Boolean mDrop = false;
    private Boolean mResponseReady = false;

    private final AsyncHttpClient mClient = new AsyncHttpClient();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_server);
        mSettings = getSharedPreferences(MainViewActivity.SHAREDPREFS_FILE, 0);

        // Create unique ID if needed
        if (mSettings.getLong("device_id", -1) == -1) {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putLong("device_id", UUID.randomUUID().getMostSignificantBits());
            editor.commit();
        }

        // Get data from intent if it's there
        String name = getIntent().getStringExtra(EXTRA_NAME);
        String address = getIntent().getStringExtra(EXTRA_ADDRESS);

        // Set views
        mImageView_type = (ImageView) findViewById(R.id.imageView_type);
        mImageView_type.setImageResource(R.drawable.ic_type_desktop);
        mSpinner = (Spinner) findViewById(R.id.spinner_type);
        mEditText_address = (EditText) findViewById(R.id.editText_address);
        mEditText_name = (EditText) findViewById(R.id.editText_name);
        mEditText_port = (EditText) findViewById(R.id.editText_port);

        if (name != null) {
            mEditText_name.setText(name);
            mEditText_address.setText(address);
        }

        mClient.setMaxRetriesAndTimeout(2, 2000);

        // Setup views
        mSpinner.setOnItemSelectedListener(this);
        findViewById(R.id.button_add).setOnClickListener(this);

        // Get GCM id
        SharedPreferences gcmprefs = getSharedPreferences(ConnectionActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        mGCMID = gcmprefs.getString(ConnectionActivity.PROPERTY_REG_ID, "null");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        switch (position) {
            case 0:
                mImageView_type.setImageResource(R.drawable.ic_type_desktop);
                break;
            case 1:
                mImageView_type.setImageResource(R.drawable.ic_type_laptop);
                break;
            case 2:
                mImageView_type.setImageResource(R.drawable.ic_type_server);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Do nothing
    }

    private void saveServer(String url) {
        // Save data
        int currentServer = mSettings.getInt("serverCount", 0);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString("server_" + String.valueOf(currentServer) + "_name", mEditText_name.getText().toString());
        editor.putString("server_" + String.valueOf(currentServer) + "_address", url);
        editor.putInt("server_" + String.valueOf(currentServer) + "_type", mSpinner.getSelectedItemPosition());
        editor.putInt("serverCount", currentServer + 1);
        editor.commit();

        BackupManager.dataChanged("com.sakisds.icymonitor");

        // Exit activity
        finish();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onClick(View view) {
        // Check if name is empty
        if (mEditText_name.getText().toString().equals("")) {
            mEditText_name.setError(getResources().getString(R.string.error_empty_name));
            mEditText_name.requestFocus();
        } else if (mEditText_port.getText().toString().equals("")) {
            mEditText_port.setError(getString(R.string.error_empty_port));
            mEditText_port.requestFocus();
        } else if (mEditText_address.getText().toString().equals("")) {
            mEditText_address.setError(getString(R.string.error_empty_address));
            mEditText_address.requestFocus();
        } else { // If it's not
            // Validate URL
            String url = mEditText_address.getText().toString();
            // Add http:// if needed
            if (!url.substring(0, 7).equals("http://")) {
                url = "http://" + url;
            }
            url += ":" + mEditText_port.getText().toString();
            url = url.replace(" ", "");
            if (!URLUtil.isValidUrl(url)) { // If invalid
                mEditText_address.setError(getResources().getString(R.string.error_invalid_address));
                mEditText_address.requestFocus();
            } else { // If URL is valid
                // Create a dialog
                final ProgressDialog progress = ProgressDialog.show(this, "", getResources().getString(R.string.connecting), false);

                // Store actual URL
                final String actualURL = url;

                // Check if auth is enabled
                mClient.get(url + "/authEnabled", null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            if (response.getBoolean("AuthEnabled")) {
                                progress.setMessage(getString(R.string.check_computer));
                            }
                        } catch (JSONException ignored) { } // If there are issues they will be dealt with later
                    }
                });

                // Register
                RequestParams params = new RequestParams();
                params.put("name", android.os.Build.MODEL);
                params.put("id", String.valueOf(mSettings.getLong("device_id", -2)));
                params.put("gcm", mGCMID);
                params.put("ask", "true");

                JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            // Get string
                            String version = response.getString("Version");

                            // Parse response
                            if (version.equals(ConnectionActivity.ACCEPTED_SERVER_VERSION)) {
                                String regStatus = response.getString("Status");

                                if (regStatus.equals("ALLOWED")) {
                                    saveServer(actualURL);
                                    progress.dismiss();
                                    mResponseReady = true;
                                } else if (regStatus.equals("DENIED")) {
                                    mEditText_address.setError(getResources().getString(R.string.error_rejected));
                                    mEditText_address.requestFocus();
                                    progress.dismiss();
                                    mResponseReady = true;
                                }
                            } else {
                                mEditText_address.setError(getResources().getString(R.string.error_outdated_server));
                                mEditText_address.requestFocus();
                                progress.dismiss();
                                mResponseReady = true;
                            }
                        } catch (JSONException e) {
                            mEditText_address.setError(getResources().getString(R.string.error_invalid_response));
                            mEditText_address.requestFocus();
                            progress.dismiss();
                            mResponseReady = true;
                        }
                    }

                    @Override
                    public void onFailure(Throwable e, JSONObject errorResponse) {
                        progress.dismiss();
                        mEditText_address.setError(getResources().getString(R.string.error_could_not_reach_host));
                        mEditText_address.requestFocus();
                        mResponseReady = true;
                    }
                };

                mClient.get(url + "/register", params, handler);
                params.remove("ask");

                WaitingTask task = new WaitingTask();

                task.mUrl = url + "/register";
                task.mParams = params;
                task.mHandler = handler;

                task.execute();
            }
        }
    }

    private class WaitingTask extends AsyncTask<Void, Void, Void> {
        private String mUrl;
        private RequestParams mParams;
        private JsonHttpResponseHandler mHandler;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                while (!mResponseReady) {
                    this.publishProgress();
                    Thread.sleep(500);
                }
            } catch (InterruptedException ignored) { }
            return null;
        }

        // Plotting generated data in the graph
        @Override
        protected void onProgressUpdate(Void... values) {
            mClient.get(mUrl, mParams, mHandler);
        }
    }
}