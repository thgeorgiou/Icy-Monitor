package com.sakisds.icymonitor.dataobj;

/**
 * Created by Thanasis Georgiou on 15/11/13.
 */

public class MulticastComputer {
    private String mName;
    private String mAddress;

    public MulticastComputer(String name, String address) {
        mName = name;
        mAddress = address;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getName() {
        return mName;
    }
}
