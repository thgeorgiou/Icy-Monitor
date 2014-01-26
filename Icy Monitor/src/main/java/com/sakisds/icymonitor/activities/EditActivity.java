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

package com.sakisds.icymonitor.activities;

import android.app.Activity;
import android.app.backup.BackupManager;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.sakisds.icymonitor.R;
import com.sakisds.icymonitor.fragments.graph.GraphFragment;
import com.sakisds.icymonitor.views.colorpicker.ColorPickerPalette;
import com.sakisds.icymonitor.views.colorpicker.ColorPickerSwatch;

public class EditActivity extends Activity implements Button.OnClickListener, ColorPickerSwatch.OnColorSelectedListener {
    private String mColorToChange;
    private SharedPreferences mSettings;

    private ColorPickerPalette mColorPalette;
    private EditText mEditTextName;

    private int[] mPalette;

    private int mSelectedColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picker);

        // Set buttons
        findViewById(R.id.button_confirm).setOnClickListener(this);
        findViewById(R.id.button_cancel).setOnClickListener(this);

        // Get intent
        mSettings = getSharedPreferences(MainViewActivity.SHAREDPREFS_FILE, 0);
        mColorToChange = getIntent().getStringExtra(GraphFragment.EXTRAS_COLOR);

        // Find edit text view
        mEditTextName = (EditText) findViewById(R.id.editText_name);
        mEditTextName.setText(mSettings.getString("name_" + mColorToChange, ""));

        // Create array of colors
        Resources res = getResources();
        mPalette = new int[25];

        mPalette[0] = res.getColor(R.color.palette_blue_1);
        mPalette[1] = res.getColor(R.color.palette_blue_2);
        mPalette[2] = res.getColor(R.color.palette_blue_3);
        mPalette[3] = res.getColor(R.color.palette_blue_4);
        mPalette[4] = res.getColor(R.color.palette_blue_5);
        mPalette[5] = res.getColor(R.color.palette_purple_5);
        mPalette[6] = res.getColor(R.color.palette_purple_4);
        mPalette[7] = res.getColor(R.color.palette_purple_3);
        mPalette[8] = res.getColor(R.color.palette_purple_2);
        mPalette[9] = res.getColor(R.color.palette_purple_1);
        mPalette[10] = res.getColor(R.color.palette_green_1);
        mPalette[11] = res.getColor(R.color.palette_green_2);
        mPalette[12] = res.getColor(R.color.palette_green_3);
        mPalette[13] = res.getColor(R.color.palette_green_4);
        mPalette[14] = res.getColor(R.color.palette_green_5);
        mPalette[15] = res.getColor(R.color.palette_orange_5);
        mPalette[16] = res.getColor(R.color.palette_orange_4);
        mPalette[17] = res.getColor(R.color.palette_orange_3);
        mPalette[18] = res.getColor(R.color.palette_orange_2);
        mPalette[19] = res.getColor(R.color.palette_orange_1);
        mPalette[20] = res.getColor(R.color.palette_red_1);
        mPalette[21] = res.getColor(R.color.palette_red_2);
        mPalette[22] = res.getColor(R.color.palette_red_3);
        mPalette[23] = res.getColor(R.color.palette_red_4);
        mPalette[24] = res.getColor(R.color.palette_red_5);

        mSelectedColor = mSettings.getInt(mColorToChange, res.getColor(R.color.palette_blue_1));

        // Find and populate color palette
        mColorPalette = (ColorPickerPalette) findViewById(R.id.colorpalette);
        mColorPalette.init(ColorPickerPalette.SIZE_SMALL, 5, this);
        mColorPalette.drawPalette(mPalette, mSelectedColor);
    }

    private void setSettings() {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(mColorToChange, mSelectedColor);

        String name = mEditTextName.getText().toString();
        if (name.equals("")) {
            editor.remove("name_" + mColorToChange);
        } else {
            editor.putString("name_" + mColorToChange, name);
        }
        editor.commit();

        BackupManager.dataChanged("com.sakisds.icymonitor");
    }

    /**
     * Handle dialog-style buttons
     *
     * @param view The button pressed
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_confirm:
                setSettings();
                finish();
                break;
            case R.id.button_cancel:
                finish();
                break;
        }
    }

    @Override
    public void onColorSelected(int color) {
        mColorPalette.drawPalette(mPalette, color);
        mSelectedColor = color;
    }
}
