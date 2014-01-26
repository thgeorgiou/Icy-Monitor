package com.sakisds.icymonitor;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import com.sakisds.icymonitor.activities.MainViewActivity;

/**
 * Created by stratisg on 20/8/2013.
 */
public class IcyBackupAgent extends BackupAgentHelper {
    // A key to uniquely identify the set of backup data
    static final String PREFS_BACKUP_KEY = "prefs";

    // Allocate a helper and add it to the backup agent
    @Override
    public void onCreate() {
        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, MainViewActivity.SHAREDPREFS_FILE);
        addHelper(PREFS_BACKUP_KEY, helper);
    }
}
