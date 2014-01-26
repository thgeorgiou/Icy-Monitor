package com.sakisds.icymonitor.dataobj;

/**
 * Created by Thanasis Georgiou on 30/12/13.
 */
public class NotificationInfo {
    private String mName, mType, mCondition, mValue;
    private Boolean mRingOnce;

    public NotificationInfo(String name, String type, String condition, String value, Boolean ringOnce) {
        this.mName = name;
        this.mType = type;
        this.mCondition = condition;
        this.mValue = value;
        this.mRingOnce = ringOnce;
    }

    public String getNotificationName() {
        return mName;
    }

    public String getNotificationType() {
        return mType;
    }

    public String getCondition() {
        return mCondition;
    }

    public String getNotificationValue() {
        return mValue;
    }

    public Boolean getRingOnce() {
        return mRingOnce;
    }
}