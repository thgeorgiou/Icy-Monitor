package com.sakisds.icymonitor.dataobj;

public class DiskTempInfo {
    private String mName;
    private float mTemp;

    public DiskTempInfo(String name, float temp) {
        mName = name;
        mTemp = temp;
    }

    public String getName() {
        return mName;
    }

    public float getTemp() {
        return mTemp;
    }
}
