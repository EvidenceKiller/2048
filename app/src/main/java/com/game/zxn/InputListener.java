package com.game.zxn;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.game.zxn.settings.SettingsPreference;

/**
 * User : ZXN
 * Date : 2017-02-19
 * Time : 17:50
 */

public class InputListener implements View.OnTouchListener, View.OnKeyListener {

    private static final int SWIPE_MIN_DISTANCE = 0;
    private static int SWIPE_THRESHOLD_VELOCITY = 40;
    private static int MOVE_THRESHOLD = 250;
    private static final int RESET_STARTING = 10;

    public InputListener(View view) {

    }

    public static void loadSensitivity() {
        int sensitivity = SettingsPreference.getInt(SettingsPreference.KEY_SENSITIVITY,
                SettingsPreference.VALUE_SENSITIVITY_NORMAL);
        switch (sensitivity) {
            case SettingsPreference.VALUE_SENSITIVITY_SLOW:
                SWIPE_THRESHOLD_VELOCITY = 20;
                MOVE_THRESHOLD = 200;
                break;
            case SettingsPreference.VALUE_SENSITIVITY_NORMAL:
                SWIPE_THRESHOLD_VELOCITY = 60;
                MOVE_THRESHOLD = 250;
                break;
            case SettingsPreference.VALUE_SENSITIVITY_FAST:
                SWIPE_THRESHOLD_VELOCITY = 100;
                MOVE_THRESHOLD = 300;
                break;
        }
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
