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

package com.sakisds.icymonitor.fragments.graph;

import com.loopj.android.http.RequestParams;
import com.sakisds.icymonitor.R;
import org.json.JSONException;
import org.json.JSONObject;

public class SystemFragment extends GraphContainerFragment {

    private GraphFragment mVoltagesFragment, mTempsFragment, mFansFragment;
    private GraphFragment mVoltagesFragmentTablet, mTempsFragmentTablet, mFansFragmentTablet;

    public SystemFragment() {
    }

    @Override
    protected void assignFragments() {
        mTempsFragment = mFragments[0];
        mFansFragment = mFragments[1];
        mVoltagesFragment = mFragments[2];

        if (mFragmentsTablet != null) {
            mTempsFragmentTablet = mFragmentsTablet[0];
            mFansFragmentTablet = mFragmentsTablet[1];
            mVoltagesFragmentTablet = mFragmentsTablet[2];
        }
    }

    @Override
    protected int getFragmentCount() {
        return 3;
    }

    @Override
    protected RequestParams getParametersWithoutId() {
        return new RequestParams("type", "system");
    }

    @Override
    protected String getFragmentName(int position) {
        switch (position) {
            case 0:
                return getString(R.string.fragment_temperatures);
            case 1:
                return getString(R.string.fragment_fans);
            case 2:
                return getString(R.string.fragment_voltages);
        }
        return null;
    }

    @Override
    protected void initializeFragments(JSONObject response) {
        try {
            int tempCount = response.getJSONArray("Temp").length();
            int fanCount = response.getJSONArray("Fans").length();
            int voltageCount = response.getJSONArray("Voltages").length();

            // Init fragments
            String tempNames[] = new String[tempCount];
            String fanNames[] = new String[fanCount];
            String voltageNames[] = new String[voltageCount];

            for (int i = 0; i < tempCount; i++) {
                tempNames[i] = response.getJSONArray("Temp").getJSONObject(i).getString("Name");
            }
            for (int i = 0; i < fanCount; i++) {
                fanNames[i] = response.getJSONArray("Fans").getJSONObject(i).getString("Name");
            }
            for (int i = 0; i < voltageCount; i++) {
                voltageNames[i] = response.getJSONArray("Voltages").getJSONObject(i).getString("Name");
            }

            // TODO replace with string resources
            mTempsFragment.init(tempNames, "C", 100);
            mFansFragment.init(fanNames, "RPM", 5000);
            mVoltagesFragment.init(voltageNames, "V", 5);

            if (mFragmentsTablet != null) {
                mTempsFragmentTablet.init(tempNames, "C", 100);
                mFansFragmentTablet.init(fanNames, "RPM", 5000);
                mVoltagesFragmentTablet.init(voltageNames, "V", 5);
            }
        } catch (JSONException e) {
            toggleBackgroundWork();
            showErrorDialog(R.string.error_invalid_response);
        }
    }

    @Override
    protected void updateFragments(JSONObject response) {
        try {
            float[] tempData = new float[response.getJSONArray("Temp").length()];
            float[] fanData = new float[response.getJSONArray("Fans").length()];
            float[] voltageData = new float[response.getJSONArray("Voltages").length()];

            for (int i = 0; i < tempData.length; i++) {
                tempData[i] = (float) response.getJSONArray("Temp").getJSONObject(i).getDouble("Value");
            }
            for (int i = 0; i < fanData.length; i++) {
                fanData[i] = response.getJSONArray("Fans").getJSONObject(i).getInt("Value");
            }
            for (int i = 0; i < voltageData.length; i++) {
                voltageData[i] = (float) response.getJSONArray("Voltages").getJSONObject(i).getDouble("Value");
            }

            mTempsFragment.addData(tempData);
            mFansFragment.addData(fanData);
            mVoltagesFragment.addData(voltageData);

            if (mFragmentsTablet != null) {
                mTempsFragmentTablet.addData(tempData);
                mFansFragmentTablet.addData(fanData);
                mVoltagesFragmentTablet.addData(voltageData);
            }
        } catch (JSONException e) {
            toggleBackgroundWork();
            showErrorDialog(R.string.error_invalid_response);
        }
    }
}
