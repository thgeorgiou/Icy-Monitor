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
 * Store info about each disk.
 * Created by sakisds on 18/06/13.
 */
public class DiskInfo {
    private final String mName, mLabel, mFormat;
    private final float mSize, mFree;

    public DiskInfo(String name, String label, String format, float size, float free) {
        mName = name;
        if (label.equals("")) {
            mLabel = "N/A";
        } else {
            mLabel = label;
        }
        mFormat = format;
        mSize = size;
        mFree = free;
    }

    /**
     * Get the name of this disk.
     *
     * @return name of this disk.
     */
    public String getName() {
        return mName;
    }

    /**
     * Get the label of this disk. N/A is returned if there is no label
     *
     * @return label of this disk.
     */
    public String getLabel() {
        return mLabel;
    }

    /**
     * Get the filesystem format of the current disk.
     *
     * @return filesystem format
     */
    public String getFormat() {
        return mFormat;
    }

    /**
     * Get the total size of this disk in GB
     *
     * @return total size
     */
    public float getSize() {
        return mSize;
    }

    /**
     * Get the free space of this disk in GB
     *
     * @return free space
     */
    public float getFree() {
        return mFree;
    }
}
