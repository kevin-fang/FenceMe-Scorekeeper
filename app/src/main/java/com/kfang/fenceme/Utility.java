package com.kfang.fenceme;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Class to get preferences.
 */

public class Utility {

    public static final String ALARM_AT_END = "enable_alarm";
    public static final String MAX_BRIGHTNESS = "maximum_brightness";
    static final String BOUT_LENGTH_POINTS = "bout_length_points";
    static final String BOUT_LENGTH_MINUTES = "bout_length_time";
    static final String RED_PLAYER = "player.red";
    static final String GREEN_PLAYER = "player.green";
    static final int DEFAULT_POINTS = 5;
    static final int DEFAULT_MINUTES = 3;
    static int redScore = 0;
    static int greenScore = 0;
    static String redName;
    static String greenName;
    private static SharedPreferences prefs;

    static int getPointsPreference(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(BOUT_LENGTH_POINTS, DEFAULT_POINTS);
    }

    static boolean getBoolPreference(Context context, String value, String defaultValue) {
        return prefs.getBoolean(value, true);
    }

    static int updateCurrentTime(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(BOUT_LENGTH_MINUTES, DEFAULT_MINUTES);
    }
}
