package com.sakisds.icymonitor.dataobj;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Thanasis Georgiou on 30/12/13.
 */
public class Sensor {
    private String mName;
    private String mType;

    public Sensor(String type, String name) {
        this.mType = type;
        this.mName = name;
    }

    public Sensor(JSONObject json) {
        try {
            this.mName = json.getString("Name");
            this.mType = json.getString("Type");
        } catch (JSONException e) {
            this.mName = "Invalid";
            this.mType = "";
        }
    }

    public String getName() {
        return mName;
    }

    public String getType() {
        return mType;
    }
}
