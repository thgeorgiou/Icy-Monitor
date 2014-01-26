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

package com.sakisds.icymonitor;

/**
 * Handles GCM notifications.
 * Created by Thanasis Georgiou on 01/07/13.
 */

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.sakisds.icymonitor.activities.ConnectionActivity;
import com.sakisds.icymonitor.activities.MainViewActivity;

import java.util.Random;

public class GcmBroadcastReceiver extends BroadcastReceiver {
    private Context mContext;
    private SharedPreferences mSettings;
    private Resources mRes;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        Bundle extras = intent.getExtras();
        mSettings = context.getSharedPreferences(MainViewActivity.SHAREDPREFS_FILE, 0);
        mRes = context.getResources();

        try {
            String sensorName = extras.getString("SensorName");
            String compName = extras.getString("CompName");

            int id;
            if (!sensorName.equals("") && !compName.equals("")) {
                id = sensorName.charAt(0) + compName.charAt(0) + (128 * compName.charAt(0));
            } else {
                id = 128 * new Random().nextInt();
            }

            sendNotification(sensorName, compName, id, extras.getString("Value"));
            setResultCode(Activity.RESULT_OK);
        } catch (Exception e) {
            Log.w("IcyNotifReceiver", "Invalid notification received. " + e.getMessage());
        }
    }

    // Put the GCM message into a notification and post it.
    private void sendNotification(String name, String computerName, int id, String value) {
        NotificationManager mNotificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                new Intent(mContext, ConnectionActivity.class), 0);

        String text;
        if (name.equals("IcyTestNotification")) {
            text = "Test notification received.";
            name = "Test";
        } else {
            text = "Sensor is currently at " + value;
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(computerName + ": " + name)
                        .setContentText(text)
                        .setSound(Uri.parse(mSettings.getString(mRes.getString(R.string.key_notification), Settings.System.DEFAULT_NOTIFICATION_URI.toString())));

        if (mSettings.getBoolean(mRes.getString(R.string.key_vibrate), true)) {
            mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        }

        if(mSettings.getBoolean(mRes.getString(R.string.key_lights), true)) {
            mBuilder.setLights(Color.RED, 1000, 1000);
        }

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(id, mBuilder.build());
    }
}