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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sakisds.icymonitor.R;
import com.sakisds.icymonitor.dataobj.NotificationInfo;
import com.sakisds.icymonitor.dataobj.Sensor;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddNotificationActivity extends Activity implements View.OnClickListener {

    private SharedPreferences mSettings;

    private Spinner mDeviceSpinner, mTypeSpinner, mSensorSpinner, mConditionSpinner;
    private EditText mSensorValue;
    private CheckBox mRingOnceCheckbox;

    private String mURL;

    private AsyncHttpClient mClient = new AsyncHttpClient();

    private Sensor[] mSystemTempSensors, mSystemFanSensors, mSystemVoltSensors;
    private Sensor[] mCPUTempSensors, mCPULoadSensors, mCPUClockSensors, mCPUPowerSensors;
    private Sensor[] mGPUTempSensors, mGPULoadSensors, mGPUFanSensors, mGPUClockSensors;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_notification);
        mSettings = getSharedPreferences(MainViewActivity.SHAREDPREFS_FILE, 0);

        // Prepare buttons
        findViewById(R.id.button_add).setOnClickListener(this);
        findViewById(R.id.button_cancel).setOnClickListener(this);
        findViewById(R.id.button_notification_help).setOnClickListener(this);

        // Prepare spinners
        mDeviceSpinner = (Spinner) findViewById(R.id.spinner_device);
        mTypeSpinner = (Spinner) findViewById(R.id.spinner_type);
        mSensorSpinner = (Spinner) findViewById(R.id.spinner_sensor);
        mConditionSpinner = (Spinner) findViewById(R.id.spinner_condition);

        // Edittext
        mSensorValue = (EditText) findViewById(R.id.editText_value);

        // Checkbox
        mRingOnceCheckbox = (CheckBox) findViewById(R.id.checkbox_notif_ringonce);

        // URL
        mURL = getIntent().getExtras().getString(MainViewActivity.EXTRA_ADDRESS);

        // Client
        mClient.setMaxRetriesAndTimeout(2, 2000);

        // Retrieve sensors
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "listdev");
        params.put("id", String.valueOf(mSettings.getLong("device_id", -2)));

        final Activity context = this;
        final ProgressDialog progress = ProgressDialog.show(this, "", getResources().getString(R.string.retrieving_devices), false);

        mClient.get(mURL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    // System
                    JSONArray systemResponse = response.getJSONArray("System");

                    int len = systemResponse.length();
                    int lenTemp = 0;
                    int lenFan = 0;
                    int lenVolt = 0;

                    for (int i = 0; i < len; i++) {
                        Sensor sensor = new Sensor(systemResponse.getJSONObject(i));

                        if (sensor.getType().equals("Temperature")) {
                            lenTemp++;
                        } else if (sensor.getType().equals("Fan")) {
                            lenFan++;
                        } else if (sensor.getType().equals("Voltage")) {
                            lenVolt++;
                        }
                    }

                    mSystemTempSensors = new Sensor[lenTemp];
                    mSystemFanSensors = new Sensor[lenFan];
                    mSystemVoltSensors = new Sensor[lenVolt];

                    int iTemp = 0;
                    int iFan = 0;
                    int iVolt = 0;

                    for (int i = 0; i < len; i++) {
                        Sensor sensor = new Sensor(systemResponse.getJSONObject(i));

                        if (sensor.getType().equals("Temperature")) {
                            mSystemTempSensors[iTemp] = sensor;
                            iTemp++;
                        } else if (sensor.getType().equals("Fan")) {
                            mSystemFanSensors[iFan] = sensor;
                            iFan++;
                        } else if (sensor.getType().equals("Voltage")) {
                            mSystemVoltSensors[iVolt] = sensor;
                            iVolt++;
                        }
                    }

                    // CPU
                    JSONArray cpuResponse = response.getJSONArray("CPU");

                    len = cpuResponse.length();
                    lenTemp = 0;
                    int lenLoad = 0;
                    int lenClock = 0;
                    int lenPower = 0;

                    for (int i = 0; i < len; i++) {
                        Sensor sensor = new Sensor(cpuResponse.getJSONObject(i));

                        if (sensor.getType().equals("Temperature")) {
                            lenTemp++;
                        } else if (sensor.getType().equals("Load")) {
                            lenLoad++;
                        } else if (sensor.getType().equals("Clock")) {
                            lenClock++;
                        } else if (sensor.getType().equals("Power")) {
                            lenPower++;
                        }
                    }

                    mCPUTempSensors = new Sensor[lenTemp];
                    mCPULoadSensors = new Sensor[lenLoad];
                    mCPUPowerSensors = new Sensor[lenPower];
                    mCPUClockSensors = new Sensor[lenClock];

                    iTemp = 0;
                    int iLoad = 0;
                    int iPower = 0;
                    int iClock = 0;

                    for (int i = 0; i < len; i++) {
                        Sensor sensor = new Sensor(cpuResponse.getJSONObject(i));

                        if (sensor.getType().equals("Temperature")) {
                            mCPUTempSensors[iTemp] = sensor;
                            iTemp++;
                        } else if (sensor.getType().equals("Power")) {
                            mCPUPowerSensors[iPower] = sensor;
                            iPower++;
                        } else if (sensor.getType().equals("Clock")) {
                            mCPUClockSensors[iClock] = sensor;
                            iClock++;
                        } else if (sensor.getType().equals("Load")) {
                            mCPULoadSensors[iLoad] = sensor;
                            iLoad++;
                        }
                    }

                    // GPU
                    JSONArray gpuResponse = response.getJSONArray("GPU");

                    len = gpuResponse.length();
                    lenTemp = 0;
                    lenLoad = 0;
                    lenClock = 0;
                    lenFan = 0;

                    for (int i = 0; i < len; i++) {
                        Sensor sensor = new Sensor(gpuResponse.getJSONObject(i));

                        if (sensor.getType().equals("Temperature")) {
                            lenTemp++;
                        } else if (sensor.getType().equals("Load")) {
                            lenLoad++;
                        } else if (sensor.getType().equals("Clock")) {
                            lenClock++;
                        } else if (sensor.getType().equals("Fan")) {
                            lenFan++;
                        }
                    }

                    mGPUTempSensors = new Sensor[lenTemp];
                    mGPULoadSensors = new Sensor[lenLoad];
                    mGPUFanSensors = new Sensor[lenFan];
                    mGPUClockSensors = new Sensor[lenClock];

                    iTemp = 0;
                    iLoad = 0;
                    iClock = 0;
                    iFan = 0;

                    for (int i = 0; i < len; i++) {
                        Sensor sensor = new Sensor(gpuResponse.getJSONObject(i));

                        if (sensor.getType().equals("Temperature")) {
                            mGPUTempSensors[iTemp] = sensor;
                            iTemp++;
                        } else if (sensor.getType().equals("Fan")) {
                            mGPUFanSensors[iFan] = sensor;
                            iFan++;
                        } else if (sensor.getType().equals("Clock")) {
                            mGPUClockSensors[iClock] = sensor;
                            iClock++;
                        } else if (sensor.getType().equals("Load")) {
                            mGPULoadSensors[iLoad] = sensor;
                            iLoad++;
                        }
                    }

                    // Prepare spinners
                    setAdapter(mSensorSpinner, mSystemTempSensors);

                    mTypeSpinner.setOnItemSelectedListener(new TypeOnItemSelectedListener());
                    mDeviceSpinner.setOnItemSelectedListener(new DeviceOnItemSelectedListener());

                    // Close window
                    progress.hide();
                } catch (JSONException e) {
                    Toast.makeText(context, getString(R.string.error_could_not_reach_host), Toast.LENGTH_LONG).show();
                    progress.hide();
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                Toast.makeText(context, getString(R.string.error_could_not_reach_host) + ":" + error.getMessage(), Toast.LENGTH_LONG).show();
                finish();
                progress.hide();
                Log.e("Notif", error.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_cancel:
                finish();
                break;
            case R.id.button_add:
                addNotification();
                break;
            case R.id.button_notification_help:
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

                dlgAlert.setMessage(getString(R.string.notif_ring_once_help));
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
        }
    }

    private void addNotification() {
        // Error checking
        if (mSensorValue.getText().length() == 0) {
            mSensorValue.setError(getString(R.string.error_empty_value));
            return;
        }

        final ProgressDialog progress = ProgressDialog.show(this, "", getResources().getString(R.string.adding_notification), false);

        NotificationInfo notificationInfo = null;
        String condition = "";
        switch (mConditionSpinner.getSelectedItemPosition()) {
            case 0:
                condition = ">=";
                break;
            case 1:
                condition = "=<";
                break;
        }

        Boolean ringOnce = mRingOnceCheckbox.isChecked();

        switch (mDeviceSpinner.getSelectedItemPosition()) {
            case 0: // System
                switch (mTypeSpinner.getSelectedItemPosition()) {
                    case 0: // Temperature
                        notificationInfo = new NotificationInfo(mSystemTempSensors[mSensorSpinner.getSelectedItemPosition()].getName(),
                                "Temperature", condition, mSensorValue.getText().toString(), ringOnce);
                        break;
                    case 1: // Fan
                        notificationInfo = new NotificationInfo(mSystemFanSensors[mSensorSpinner.getSelectedItemPosition()].getName(),
                                "Temperature", condition, mSensorValue.getText().toString(), ringOnce);
                        break;
                    case 2: // Voltage
                        notificationInfo = new NotificationInfo(mSystemVoltSensors[mSensorSpinner.getSelectedItemPosition()].getName(),
                                "Temperature", condition, mSensorValue.getText().toString(), ringOnce);
                        break;
                }
                break;
            case 1: // CPU
                switch (mTypeSpinner.getSelectedItemPosition()) {
                    case 0: // Temp
                        notificationInfo = new NotificationInfo(mCPUTempSensors[mSensorSpinner.getSelectedItemPosition()].getName(),
                                "Temperature", condition, mSensorValue.getText().toString(), ringOnce);
                        break;
                    case 1: // Power
                        notificationInfo = new NotificationInfo(mCPUPowerSensors[mSensorSpinner.getSelectedItemPosition()].getName(),
                                "Temperature", condition, mSensorValue.getText().toString(), ringOnce);
                        break;
                    case 2: // Load
                        notificationInfo = new NotificationInfo(mCPULoadSensors[mSensorSpinner.getSelectedItemPosition()].getName(),
                                "Temperature", condition, mSensorValue.getText().toString(), ringOnce);
                        break;
                    case 3: // Clock
                        notificationInfo = new NotificationInfo(mCPUClockSensors[mSensorSpinner.getSelectedItemPosition()].getName(),
                                "Temperature", condition, mSensorValue.getText().toString(), ringOnce);
                        break;
                }
                break;
            case 2: // GPU
                switch (mTypeSpinner.getSelectedItemPosition()) {
                    case 0: // Temp
                        notificationInfo = new NotificationInfo(mGPUTempSensors[mSensorSpinner.getSelectedItemPosition()].getName(),
                                "Temperature", condition, mSensorValue.getText().toString(), ringOnce);
                        break;
                    case 1: // Fan
                        notificationInfo = new NotificationInfo(mGPUFanSensors[mSensorSpinner.getSelectedItemPosition()].getName(),
                                "Temperature", condition, mSensorValue.getText().toString(), ringOnce);
                        break;
                    case 2: // Load
                        notificationInfo = new NotificationInfo(mGPULoadSensors[mSensorSpinner.getSelectedItemPosition()].getName(),
                                "Temperature", condition, mSensorValue.getText().toString(), ringOnce);
                        break;
                    case 3: // Clock
                        notificationInfo = new NotificationInfo(mGPUClockSensors[mSensorSpinner.getSelectedItemPosition()].getName(),
                                "Temperature", condition, mSensorValue.getText().toString(), ringOnce);
                        break;
                }
        }

        // Add notification
        RequestParams params = new RequestParams();
        params.put("type", "addnotif");
        params.put("id", String.valueOf(mSettings.getLong("device_id", -2)));

        params.put("name", notificationInfo.getNotificationName());
        params.put("ntype", notificationInfo.getNotificationType());
        params.put("condition", notificationInfo.getCondition());
        params.put("value", notificationInfo.getNotificationValue());
        params.put("ringonce", notificationInfo.getRingOnce().toString());

        final Activity context = this;

        mClient.get(mURL, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                progress.hide();
                Toast.makeText(context, "Notification added", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String responseBody, Throwable error) {
                progress.hide();
                Toast.makeText(context, "Could not add notification: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setAdapter(Spinner spinner, Sensor[] source) {
        String[] items = new String[source.length];
        for (int i = 0; i < items.length; i++) {
            items[i] = source[i].getName();
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
    }

    private void setAdapter(Spinner spinner, String[] source) {
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, source);
        spinner.setAdapter(adapter);
    }

    private class DeviceOnItemSelectedListener implements Spinner.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (mDeviceSpinner.getSelectedItemPosition()) {
                case 0: // System
                    setAdapter(mTypeSpinner, getResources().getStringArray(R.array.type_spinner_system));
                    setAdapter(mSensorSpinner, mSystemTempSensors);
                    break;
                case 1: // CPU
                    setAdapter(mTypeSpinner, getResources().getStringArray(R.array.type_spinner_cpu));
                    setAdapter(mSensorSpinner, mCPUTempSensors);
                    break;
                case 2: // GPU
                    setAdapter(mTypeSpinner, getResources().getStringArray(R.array.type_spinner_gpu));
                    setAdapter(mSensorSpinner, mGPUTempSensors);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private class TypeOnItemSelectedListener implements Spinner.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (mDeviceSpinner.getSelectedItemPosition()) {
                case 0: // System
                    switch (mTypeSpinner.getSelectedItemPosition()) {
                        case 0: // Temperature
                            setAdapter(mSensorSpinner, mSystemTempSensors);
                            break;
                        case 1: // Fan
                            setAdapter(mSensorSpinner, mSystemFanSensors);
                            break;
                        case 2: // Voltage
                            setAdapter(mSensorSpinner, mSystemVoltSensors);
                            break;
                    }
                    break;
                case 1: // CPU
                    switch (mTypeSpinner.getSelectedItemPosition()) {
                        case 0: // Temp
                            setAdapter(mSensorSpinner, mCPUTempSensors);
                            break;
                        case 1: // Power
                            setAdapter(mSensorSpinner, mCPUPowerSensors);
                            break;
                        case 2: // Load
                            setAdapter(mSensorSpinner, mCPULoadSensors);
                            break;
                        case 3: // Clock
                            setAdapter(mSensorSpinner, mCPUClockSensors);
                            break;
                    }
                    break;
                case 2: // GPU
                    switch (mTypeSpinner.getSelectedItemPosition()) {
                        case 0: // Temp
                            setAdapter(mSensorSpinner, mGPUTempSensors);
                            break;
                        case 1: // Fan
                            setAdapter(mSensorSpinner, mGPUFanSensors);
                            break;
                        case 2: // Load
                            setAdapter(mSensorSpinner, mGPULoadSensors);
                            break;
                        case 3: // Clock
                            setAdapter(mSensorSpinner, mGPUClockSensors);
                            break;
                    }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }
}