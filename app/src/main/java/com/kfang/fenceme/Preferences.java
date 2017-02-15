package com.kfang.fenceme;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Class to get preferences.
 */

public class Preferences {

    public static final String ALARM_AT_END = "enable_alarm";
    public static final String MAX_BRIGHTNESS = "maximum_brightness";
    static final String BOUT_LENGTH_POINTS = "bout_length_points";
    static final String BOUT_LENGTH_MINUTES = "bout_length_time";
    static final String DEFAULT_POINTS = "5";
    static final String DEFAULT_MINUTES = "3";
    static String redName;
    static String greenName;
    private static SharedPreferences prefs;

    static int getIntPreference(Context context, String value, String defaultValue) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(prefs.getString(value, defaultValue));
    }

    static boolean getBoolPreference(Context context, String value, String defaultValue) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(value, true);
    }

    static int updateCurrentTime(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(prefs.getString(BOUT_LENGTH_MINUTES, DEFAULT_MINUTES));
    }
}
