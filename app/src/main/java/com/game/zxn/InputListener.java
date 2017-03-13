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

    private float x;
    private float y;
    private float lastDx;
    private float lastDy;
    private float previousX;
    private float previousY;
    private float startingX;
    private float startingY;
    private int previousDirection = 1;
    private int veryLastDirection = 1;
    private boolean moved = false;

    private MainView mView;

    public InputListener(MainView view) {
        super();
        this.mView = view;
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
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                startingX = x;
                startingY = y;
                previousX = x;
                previousY = y;
                lastDx = 0;
                lastDy = 0;
                moved = false;
                return true;
            case MotionEvent.ACTION_MOVE:
                if (MainView.inverseMode) {
                    return true;
                }
        }
        return false;
    }
}
