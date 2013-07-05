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
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.sakisds.icymonitor.activities.ConnectionActivity;

/**
 * Handling of GCM messages.
 */
public class GcmBroadcastReceiver extends BroadcastReceiver {
    private NotificationManager mNotificationManager;
    Context mContext;
    Uri mSound;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        Bundle extras = intent.getExtras();
        sendNotification(extras.getString("Title"), Integer.valueOf(extras.getString("ID")), extras.getString("AValue"),
                extras.getString("CValue"));
        setResultCode(Activity.RESULT_OK);
    }

    // Put the GCM message into a notification and post it.
    private void sendNotification(String title, int id, String avalue, String cvalue) {
        mNotificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                new Intent(mContext, ConnectionActivity.class), 0);

        String text = "Allowed value: " + avalue + ", Sensor value: " + cvalue;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_stat_error)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(text))
                        .setSound(mSound)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentText(text);

        mBuilder.setContentIntent(contentIntent);
        Log.println(Log.INFO, "id", String.valueOf(id));
        mNotificationManager.notify(id, mBuilder.build());
    }
}