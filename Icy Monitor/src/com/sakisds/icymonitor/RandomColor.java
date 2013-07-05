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

import android.content.res.Resources;

import java.util.Random;

/**
 * Generates a randomcolor
 * Created by Thanasis Georgiou on 20/06/13.
 */
public class RandomColor {

    /**
     * Get random color
     *
     * @param res getResources()
     * @return A random color
     */
    public static int getColor(Random random, Resources res) {
        int r = random.nextInt(5);

        switch (r) {
            case 0:
                return res.getColor(R.color.color_holo_blue);
            case 1:
                return res.getColor(R.color.color_holo_green);
            case 2:
                return res.getColor(R.color.color_holo_red);
            case 3:
                return res.getColor(R.color.color_holo_orange);
            case 4:
                return res.getColor(R.color.color_holo_purple);
            default:
                return res.getColor(R.color.color_holo_blue);
        }
    }
}
