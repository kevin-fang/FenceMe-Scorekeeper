package com.kfang.fenceme;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Class to get preferences.
 */

class Utility {

    static final String ALARM_AT_END = "enable_alarm";
    static final String MAX_BRIGHTNESS = "maximum_brightness";
    static final String BOUT_LENGTH_POINTS = "bout_length_points";
    static final String BOUT_LENGTH_MINUTES = "bout_length_time";
    static final String RESET_BOUT_PREFERENCES = "reset_bout_length";
    static final String RED_PLAYER = "player.red";
    static final String GREEN_PLAYER = "player.green";
    static final String CURRENT_GAME_PREFERENCES = "current_game_preferences";
    static final int DEFAULT_POINTS = 5;
    static final int DEFAULT_MINUTES = 3;
    static final String CURRENT_RED_POINTS = "current_red_points";
    static final String CURRENT_GREEN_POINTS = "current_green_points";
    static final String CURRENT_TIME = "current_time";

    static int redScore = 0;
    static int greenScore = 0;
    static String redName = "Red";
    static String greenName = "Green";
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

    static void saveCurrentMatchPreferences(Context context) {
        SharedPreferences gamePrefs = context.getSharedPreferences(CURRENT_GAME_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor gamePrefsEditor = gamePrefs.edit();
        gamePrefsEditor.putInt(CURRENT_RED_POINTS, ((MainActivity) context).getRedScore())
                .putInt(CURRENT_GREEN_POINTS, ((MainActivity) context).getGreenScore())
                .putLong(CURRENT_TIME, MainActivity.mCurrentTime)
                .apply();
    }

    static void updateCurrentMatchPreferences(Context context) {
        SharedPreferences gamePrefs = context.getSharedPreferences(CURRENT_GAME_PREFERENCES, Context.MODE_PRIVATE);
        MainActivity activity = ((MainActivity) context);
        activity.setRedScore(gamePrefs.getInt(CURRENT_RED_POINTS, 0));
        activity.setGreenScore(gamePrefs.getInt(CURRENT_GREEN_POINTS, 0));
        MainActivity.mCurrentTime = gamePrefs.getLong(CURRENT_TIME, DEFAULT_MINUTES * 180000);
    }
}
