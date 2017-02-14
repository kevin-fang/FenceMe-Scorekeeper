package com.kfang.fenceme;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by kfang on 2/13/2017.
 */

public class Preferences {

    public static final String BOUT_LENGTH_POINTS = "bout_length_points";
    public static final String BOUT_LENGTH_MINUTES = "bout_length_time";
    public static final String ALARM_AT_END = "enable_alarm";
    public static final String MAX_BRIGHTNESS = "maximum_brightness";
    public static final String DEFAULT_POINTS = "5";
    public static final String DEFAULT_MINUTES = "3";
    static SharedPreferences prefs;

    public static int getIntPreference(Context context, String value, String defaultValue) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(prefs.getString(value, defaultValue));
    }

    public static boolean getBoolPreference(Context context, String value, String defaultValue) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(value, true);
    }

    public static int updateCurrentTime(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(prefs.getString(BOUT_LENGTH_MINUTES, DEFAULT_MINUTES));
    }
}
