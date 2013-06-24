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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sakisds.icymonitor.R;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Adds a new server.
 * Created by sakisds on 25/05/13.
 */
public class AddServerActivity extends Activity implements Spinner.OnItemSelectedListener, Button.OnClickListener {

    private SharedPreferences mSettings;
    private ImageView mImageView_type;
    private EditText mEditText_name, mEditText_address;
    private Spinner mSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_server);
        mSettings = getSharedPreferences(MainViewActivity.SHAREDPREFS_FILE, 0);

        // Set views
        mImageView_type = (ImageView) findViewById(R.id.imageView_type);
        mImageView_type.setImageResource(R.drawable.ic_type_desktop);
        mSpinner = (Spinner) findViewById(R.id.spinner_type);
        mEditText_address = (EditText) findViewById(R.id.editText_address);
        mEditText_name = (EditText) findViewById(R.id.editText_name);

        // Setup views
        mSpinner.setOnItemSelectedListener(this);
        findViewById(R.id.button_add).setOnClickListener(this);

        // Actionbar
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return (true);
            case R.id.menu_help:
                showHelpDialog();
                return true;
        }

        return (super.onOptionsItemSelected(item));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_server, menu);
        return true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Do nothing
    }

    private void showHelpDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle(getString(R.string.menu_help));
        // set dialog message
        alertDialogBuilder
                .setMessage(getString(R.string.help_text))
                .setCancelable(true);
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
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
        // Exit activity
        finish();
    }

    @Override
    public void onClick(View view) {
        // Check if name is empty
        if (mEditText_name.getText().toString().equals("")) {
            mEditText_name.setError(getResources().getString(R.string.error_empty_name));
            mEditText_name.requestFocus();
        } else { // If it's not
            // Validate URL
            String url = mEditText_address.getText().toString();
            // Add http:// if needed
            if (!url.substring(0, 7).equals("http://")) {
                url = "http://" + url;
            }
            if (!URLUtil.isValidUrl(url)) { // If invalid
                mEditText_address.setError(getResources().getString(R.string.error_invalid_address));
                mEditText_address.requestFocus();
            } else { // If URL is valid
                // Create client
                AsyncHttpClient client = new AsyncHttpClient();
                client.setTimeout(5000);

                // Create a dialog
                final ProgressDialog progress = ProgressDialog.show(this, "", getResources().getString(R.string.connecting), false);

                // Store actual URL
                final String actualURL = url;
                // Send request
                client.get(url + "/about", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        progress.dismiss();
                        try {
                            // Get string
                            String version = response.getString("Version");

                            // Parse response
                            if (version.equals(ConnectionActivity.ACCEPTED_SERVER_VERSION)) {
                                saveServer(actualURL);
                            } else {
                                mEditText_address.setError(getResources().getString(R.string.error_outdated_server));
                                mEditText_address.requestFocus();
                            }
                        } catch (JSONException e) {
                            mEditText_address.setError(getResources().getString(R.string.error_invalid_response));
                            mEditText_address.requestFocus();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e, String response) {
                        progress.dismiss();
                        mEditText_address.setError(getResources().getString(R.string.error_could_not_reach_host));
                        mEditText_address.requestFocus();
                    }
                });
            }
        }
    }
}