package com.game.zxn.settings;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * User : ZXN
 * Date : 2017-02-16
 * Time : 20:59
 */

public class SettingsPreference {

    public static final String KEY_PREFERENCE = "preference";

    public static final String KEY_SENSITIVITY = "settings_sensitivity";
    public static final String KEY_VARIETY = "settings_variety";
    public static final String KEY_INVERSE_MODE = "settings_inverse_mode";

    public static final int VALUE_SENSITIVITY_SLOW = 0;
    public static final int VALUE_SENSITIVITY_NORMAL = 1;
    public static final int VALUE_SENSITIVITY_FAST = 2;

    public static SharedPreferences preferences;

    public static void initPreferences(Context context) {
        if (null == preferences) {
            preferences = context.getSharedPreferences(KEY_PREFERENCE, Context.MODE_PRIVATE);
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    public static void putBoolean(String key, boolean value) {
        preferences.edit().putBoolean(key, value).commit();
    }

    public static int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    public static void putInt(String key, int value) {
        preferences.edit().putInt(key, value).commit();
    }

    public static String getString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    public static void putString(String key, String value) {
        preferences.edit().putString(key, value).commit();
    }

    public static void remove(String key) {
        preferences.edit().remove(key).commit();
    }

}
