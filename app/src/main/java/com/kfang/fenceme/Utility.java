package com.kfang.fenceme;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import static com.kfang.fenceme.MainActivity.mGreenFencer;
import static com.kfang.fenceme.MainActivity.mRedFencer;

/**
 * Class to get preferences.
 */

class Utility {

    // settings string keys
    static final String VIBRATE_AT_END = "vibrate_on_finish";
    static final String PAUSE_ON_SCORE_CHANGE = "pause_on_score_change";
    static final String KEEP_DEVICE_AWAKE = "keep_awake";
    static final String BOUT_LENGTH_POINTS = "bout_length_points";
    static final String BOUT_LENGTH_MINUTES = "bout_length_time";
    static final String RESET_BOUT_PREFERENCES = "reset_bout_length";

    static final int TO_ADD = 1;
    static final int TO_SUBTRACT = 0;
    static final String TO_CARD_PLAYER = "card_player";
    static final String MAX_BRIGHTNESS = "maximum_brightness";
    static final String CHANGE_TIMER = "TOGGLE";
    static final int DEFAULT_POINTS = 5;
    static final int DEFAULT_MINUTES = 3;
    static final String RESTORE_ON_EXIT = "restore_status";
    private static final String RED_CARDRED = "red_cardred";
    private static final String RED_CARDYELLOW = "red_cardyellow";
    private static final String GREEN_CARDRED = "green_cardred";
    private static final String GREEN_CARDYELLOW = "green_cardyellow";
    private static final String CURRENT_GAME_PREFERENCES = "current_game_preferences";
    private static final String CURRENT_RED_POINTS = "current_red_points";
    private static final String CURRENT_GREEN_POINTS = "current_green_points";
    private static final String CURRENT_TIME = "current_time";
    private static SharedPreferences prefs;

    static boolean getAwakeStatus(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(KEEP_DEVICE_AWAKE, true);
    }
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

    static boolean getPauseStatus(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(PAUSE_ON_SCORE_CHANGE, true);
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
                .putInt(RED_CARDRED, mRedFencer.getRedCards())
                .putInt(GREEN_CARDRED, mGreenFencer.getRedCards())
                .putInt(RED_CARDYELLOW, mRedFencer.getYellowCards())
                .putInt(GREEN_CARDYELLOW, mGreenFencer.getYellowCards())
                .apply();
    }

    static void updateCurrentMatchPreferences(Context context) {
        SharedPreferences gamePrefs = context.getSharedPreferences(CURRENT_GAME_PREFERENCES, Context.MODE_PRIVATE);
        MainActivity activity = ((MainActivity) context);
        mRedFencer.setPoints(gamePrefs.getInt(CURRENT_RED_POINTS, 0));
        activity.setRedScore(gamePrefs.getInt(CURRENT_RED_POINTS, 0));
        mGreenFencer.setPoints(gamePrefs.getInt(CURRENT_GREEN_POINTS, 0));
        activity.setGreenScore(gamePrefs.getInt(CURRENT_GREEN_POINTS, 0));
        MainActivity.mCurrentTime = gamePrefs.getLong(CURRENT_TIME, DEFAULT_MINUTES * 60000);
        mRedFencer.setRedCards(gamePrefs.getInt(RED_CARDRED, 0));
        mRedFencer.setYellowCards(gamePrefs.getInt(RED_CARDYELLOW, 0));
        mGreenFencer.setRedCards(gamePrefs.getInt(GREEN_CARDRED, 0));
        mGreenFencer.setYellowCards(gamePrefs.getInt(GREEN_CARDYELLOW, 0));
    }
}
