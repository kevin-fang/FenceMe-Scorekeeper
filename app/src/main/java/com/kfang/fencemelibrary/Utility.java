package com.kfang.fencemelibrary;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;

import static com.kfang.fencemelibrary.MainActivity.mGreenFencer;
import static com.kfang.fencemelibrary.MainActivity.mRedFencer;

/**
 * Class to get preferences.
 */

class Utility {

    static final String TO_CARD_PLAYER = "card_player";
    static final String CHANGE_TIMER = "TOGGLE";
    static final String TIMER_RUNNING = "timer_running";
    static final String COLOR_GREEN = "green";
    static final String COLOR_RED = "red";
    // settings string keys
    static final String VIBRATE_AT_END = "vibrate_on_finish";
    static final String PAUSE_ON_SCORE_CHANGE = "pause_on_score_change";
    static final String KEEP_DEVICE_AWAKE = "keep_awake";
    static final String BOUT_LENGTH_POINTS = "bout_length_points";
    static final String BOUT_LENGTH_MINUTES = "bout_length_time";
    static final String MAX_BRIGHTNESS = "maximum_brightness";
    static final String RESET_BOUT_PREFERENCES = "reset_bout_prefs";
    static final String POPUP_ON_SCORE = "popup_on_score_increment";
    static final String VIBRATE_TIMER = "vibrate_on_timer_change";
    static final String TOGGLE_DOUBLE_TOUCH = "toggle_double_touch";
    static final int TO_ADD = 1;
    static final int TO_SUBTRACT = 0;
    static final int DEFAULT_POINTS = 5;
    static final int DEFAULT_MINUTES = 3;
    static final String LAST_VERSION_NUMBER = "last_version_number";
    static final String RESTORE_ON_EXIT = "restore_status";
    private static final String RED_NAME = "red_name";
    private static final String GREEN_NAME = "green_name";
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

    static boolean getPopupPreference(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(POPUP_ON_SCORE, false);
    }

    static boolean getRestoreStatus(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(RESTORE_ON_EXIT, true);
    }

    static boolean getVibrateStatus(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(VIBRATE_AT_END, true);
    }

    static boolean getDoubleTouchStatus(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(TOGGLE_DOUBLE_TOUCH, true);
    }

    // returns whether to vibrate phone
    static boolean getVibrateTimerStatus(Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(VIBRATE_TIMER, true) && v.hasVibrator();
    }

    static boolean getPauseStatus(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(PAUSE_ON_SCORE_CHANGE, true);
    }

    static boolean equalPoints() {
        return mRedFencer.getPoints() == mGreenFencer.getPoints();
    }

    static int updateCurrentTime(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(BOUT_LENGTH_MINUTES, DEFAULT_MINUTES);
    }

    static void saveCurrentMatchPreferences(Context context) {
        SharedPreferences gamePrefs = context.getSharedPreferences(CURRENT_GAME_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor gamePrefsEditor = gamePrefs.edit();
        gamePrefsEditor.putInt(CURRENT_RED_POINTS, mRedFencer.getPoints())
                .putInt(CURRENT_GREEN_POINTS, mGreenFencer.getPoints())
                .putLong(CURRENT_TIME, MainActivity.mCurrentTime)
                .putInt(RED_CARDRED, mRedFencer.getRedCards())
                .putInt(GREEN_CARDRED, mGreenFencer.getRedCards())
                .putInt(RED_CARDYELLOW, mRedFencer.getYellowCards())
                .putInt(GREEN_CARDYELLOW, mGreenFencer.getYellowCards())
                .putString(GREEN_NAME, mGreenFencer.getName())
                .putString(RED_NAME, mRedFencer.getName())
                .apply();
    }

    static void updateCurrentMatchPreferences(Context context) {
        SharedPreferences gamePrefs = context.getSharedPreferences(CURRENT_GAME_PREFERENCES, Context.MODE_PRIVATE);
        MainActivity activity = ((MainActivity) context);
        mRedFencer.setPoints(gamePrefs.getInt(CURRENT_RED_POINTS, 0));
        mGreenFencer.setPoints(gamePrefs.getInt(CURRENT_GREEN_POINTS, 0));
        activity.updateScores();
        MainActivity.mCurrentTime = gamePrefs.getLong(CURRENT_TIME, DEFAULT_MINUTES * 60000);
        mRedFencer.setRedCards(gamePrefs.getInt(RED_CARDRED, 0));
        mRedFencer.setYellowCards(gamePrefs.getInt(RED_CARDYELLOW, 0));
        mGreenFencer.setRedCards(gamePrefs.getInt(GREEN_CARDRED, 0));
        mGreenFencer.setYellowCards(gamePrefs.getInt(GREEN_CARDYELLOW, 0));
        mRedFencer.setName(gamePrefs.getString(RED_NAME, "Red"));
        mGreenFencer.setName(gamePrefs.getString(GREEN_NAME, "Green"));
    }
}
