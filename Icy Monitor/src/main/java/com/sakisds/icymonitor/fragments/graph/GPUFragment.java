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

public class GPUFragment extends GraphContainerFragment {

    private GraphFragment mTempFragment, mFanFragment, mLoadFragment, mClockFragment;
    private GraphFragment mTempFragmentTablet, mFanFragmentTablet, mLoadFragmentTablet, mClockFragmentTablet;

    public GPUFragment() {
    }

    @Override
    protected void assignFragments() {
        mTempFragment = mFragments[0];
        mFanFragment = mFragments[1];
        mLoadFragment = mFragments[2];
        mClockFragment = mFragments[3];

        if (mFragmentsTablet != null) {
            mTempFragmentTablet = mFragmentsTablet[0];
            mFanFragmentTablet = mFragmentsTablet[1];
            mLoadFragmentTablet = mFragmentsTablet[2];
            mClockFragmentTablet = mFragmentsTablet[3];
        }
    }

    @Override
    protected int getFragmentCount() {
        return 4;
    }

    @Override
    protected RequestParams getParametersWithoutId() {
        return new RequestParams("type", "gpu");
    }

    @Override
    protected String getFragmentName(int position) {
        switch (position) {
            case 0:
                return getString(R.string.fragment_temperatures);
            case 1:
                return getString(R.string.fragment_fans);
            case 2:
                return getString(R.string.fragment_load);
            case 3:
                return getString(R.string.fragment_clock);
        }
        return null;
    }

    @Override
    protected void initializeFragments(JSONObject response) {
        try {
            int tempCount = response.getJSONArray("Temp").length();
            int loadCount = response.getJSONArray("Load").length();
            int clockCount = response.getJSONArray("Clock").length();
            int fanCount = response.getJSONArray("Fans").length();

            // Init fragments
            String tempNames[] = new String[tempCount];
            String loadNames[] = new String[loadCount];
            String clockNames[] = new String[clockCount];
            String fanNames[] = new String[fanCount];

            for (int i = 0; i < tempCount; i++) {
                tempNames[i] = response.getJSONArray("Temp").getJSONObject(i).getString("Name");
            }
            for (int i = 0; i < loadCount; i++) {
                loadNames[i] = response.getJSONArray("Load").getJSONObject(i).getString("Name");
            }
            for (int i = 0; i < clockCount; i++) {
                clockNames[i] = response.getJSONArray("Clock").getJSONObject(i).getString("Name");
            }
            for (int i = 0; i < fanCount; i++) {
                fanNames[i] = response.getJSONArray("Fans").getJSONObject(i).getString("Name");
            }

            // TODO replace with string resources
            mTempFragment.init(tempNames, "C", 100);
            mLoadFragment.init(loadNames, "%", 100);
            mClockFragment.init(clockNames, "MHz", 10000);
            mFanFragment.init(fanNames, "RPM", 5000);

            if (mFragmentsTablet != null) {
                mTempFragmentTablet.init(tempNames, "C", 100);
                mLoadFragmentTablet.init(loadNames, "%", 100);
                mClockFragmentTablet.init(clockNames, "MHz", 10000);
                mFanFragmentTablet.init(fanNames, "RPM", 5000);
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
            float[] loadData = new float[response.getJSONArray("Load").length()];
            float[] clockData = new float[response.getJSONArray("Clock").length()];
            float[] fanData = new float[response.getJSONArray("Fans").length()];

            for (int i = 0; i < tempData.length; i++) {
                tempData[i] = (float) response.getJSONArray("Temp").getJSONObject(i).getDouble("Value");
            }
            for (int i = 0; i < loadData.length; i++) {
                loadData[i] = response.getJSONArray("Load").getJSONObject(i).getInt("Value");
            }
            for (int i = 0; i < clockData.length; i++) {
                clockData[i] = (float) response.getJSONArray("Clock").getJSONObject(i).getDouble("Value");
            }
            for (int i = 0; i < fanData.length; i++) {
                fanData[i] = (float) response.getJSONArray("Fans").getJSONObject(i).getDouble("Value");
            }

            mTempFragment.addData(tempData);
            mLoadFragment.addData(loadData);
            mClockFragment.addData(clockData);
            mFanFragment.addData(fanData);

            if (mFragmentsTablet != null) {
                mTempFragmentTablet.addData(tempData);
                mLoadFragmentTablet.addData(loadData);
                mClockFragmentTablet.addData(clockData);
                mFanFragmentTablet.addData(fanData);
            }
        } catch (JSONException e) {
            toggleBackgroundWork();
            showErrorDialog(R.string.error_invalid_response);
        }
    }
}
