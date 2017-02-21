package com.kfang.fenceme;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Class to get preferences.
 */

class Utility {

    static final String VIBRATE_AT_END = "vibrate_on_finish";
    static final int TO_ADD = 1;
    static final int TO_SUBTRACT = 0;
    static final String TO_CARD_PLAYER = "card_player";
    static final String RED_CARDRED = "red_cardred";
    static final String RED_CARDYELLOW = "red_cardyellow";
    static final String GREEN_CARDRED = "green_cardred";
    static final String GREEN_CARDYELLOW = "green_cardyellow";
    static final String MAX_BRIGHTNESS = "maximum_brightness";
    static final String BOUT_LENGTH_POINTS = "bout_length_points";
    static final String BOUT_LENGTH_MINUTES = "bout_length_time";
    static final String RESET_BOUT_PREFERENCES = "reset_bout_length";
    static final String RED_PLAYER = "player.red";
    static final String GREEN_PLAYER = "player.green";
    static final String CHANGE_TIMER = "TOGGLE";
    static final int DEFAULT_POINTS = 5;
    static final int DEFAULT_MINUTES = 3;
    static final String RESTORE_ON_EXIT = "restore_status";
    private static final String CURRENT_GAME_PREFERENCES = "current_game_preferences";
    private static final String CURRENT_RED_POINTS = "current_red_points";
    private static final String CURRENT_GREEN_POINTS = "current_green_points";
    private static final String CURRENT_TIME = "current_time";
    static String redName = "Red";
    static String greenName = "Green";
    static int redScore = 0;
    static int greenScore = 0;
    private static SharedPreferences prefs;
    private static SharedPreferences gamePrefs;

    static int getPointsPreference(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(BOUT_LENGTH_POINTS, DEFAULT_POINTS);
    }

    static boolean getRestoreStatus(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(RESTORE_ON_EXIT, true);
    }

    static boolean getVibrateStatus(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(VIBRATE_AT_END, true);
    }

    static int updateCurrentTime(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(BOUT_LENGTH_MINUTES, DEFAULT_MINUTES);
    }

    static void saveCurrentMatchPreferences(Context context) {
        gamePrefs = context.getSharedPreferences(CURRENT_GAME_PREFERENCES, Context.MODE_PRIVATE);
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
        MainActivity.mCurrentTime = gamePrefs.getLong(CURRENT_TIME, DEFAULT_MINUTES * 60000);
    }
}
