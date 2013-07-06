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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import com.larswerkman.colorpicker.ColorPicker;
import com.larswerkman.colorpicker.SaturationBar;
import com.larswerkman.colorpicker.ValueBar;
import com.sakisds.icymonitor.R;
import com.sakisds.icymonitor.RandomColor;
import com.sakisds.icymonitor.fragments.GraphFragment;

import java.util.Random;

public class EditActivity extends Activity implements AdapterView.OnItemSelectedListener, ColorPicker.OnColorChangedListener {

    private static final int POSITION_CUSTOM = 0;
    private static final int POSITION_BLUE = 1;
    private static final int POSITION_GREEN = 2;
    private static final int POSITION_RED = 3;
    private static final int POSITION_PURPLE = 4;
    private static final int POSITION_ORANGE = 5;

    private ColorPicker mPicker;
    private EditText mEditText;
    private String mColorToChange;
    private SharedPreferences mSettings;
    private int mFirstChange = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picker);

        // Load settings
        mSettings = getSharedPreferences(MainViewActivity.SHAREDPREFS_FILE, 0);
        mColorToChange = getIntent().getStringExtra(GraphFragment.EXTRAS_COLOR);

        // Set edittext
        mEditText = (EditText) findViewById(R.id.editText_name);
        mEditText.setText(mSettings.getString("name_" + mColorToChange, ""));

        // Create colour picker
        mPicker = (ColorPicker) findViewById(R.id.picker);
        SaturationBar saturationBar = (SaturationBar) findViewById(R.id.saturationbar);
        ValueBar valueBar = (ValueBar) findViewById(R.id.valuebar);

        mPicker.addSaturationBar(saturationBar);
        mPicker.addValueBar(valueBar);

        mPicker.setColor(mSettings.getInt(mColorToChange, RandomColor.getColor(new Random(), getResources())));

        // Set current color
        mPicker.setOldCenterColor(mPicker.getColor());

        // Set listeners
        mPicker.setOnColorChangedListener(this);
        ((Spinner) findViewById(R.id.spinner_default_colors)).setOnItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_color_picker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.item_confirm:
                setSettings();
                finish();
                return true;
            case R.id.item_cancel:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSettings() {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(mColorToChange, mPicker.getColor());
        if(!mEditText.getText().toString().equals("")) {
            editor.putString("name_" + mColorToChange, mEditText.getText().toString());
        } else {
            editor.remove("name_" + mColorToChange);
        }
        editor.commit();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        mFirstChange = 0;
        switch (pos) {
            case POSITION_CUSTOM:
                break;
            case POSITION_BLUE:
                mPicker.setColor(getResources().getColor(R.color.color_holo_blue));
                break;
            case POSITION_GREEN:
                mPicker.setColor(getResources().getColor(R.color.color_holo_green));
                break;
            case POSITION_RED:
                mPicker.setColor(getResources().getColor(R.color.color_holo_red));
                break;
            case POSITION_PURPLE:
                mPicker.setColor(getResources().getColor(R.color.color_holo_purple));
                break;
            case POSITION_ORANGE:
                mPicker.setColor(getResources().getColor(R.color.color_holo_orange));
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Do nothing
    }

    @Override
    public void onColorChanged(int color) {
        // Reset spinner to "Custom"
        if (mFirstChange == 5) {
            ((Spinner) findViewById(R.id.spinner_default_colors)).setSelection(0);
        }
        mFirstChange++;
    }
}
