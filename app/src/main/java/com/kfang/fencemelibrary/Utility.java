package com.kfang.fencemelibrary;

import android.content.Context;
import android.content.SharedPreferences;

import com.kfang.fencemelibrary.main.MainActivity;

import static com.kfang.fencemelibrary.Constants.CURRENT_GAME_PREFERENCES;
import static com.kfang.fencemelibrary.Constants.CURRENT_GREEN_POINTS;
import static com.kfang.fencemelibrary.Constants.CURRENT_RED_POINTS;
import static com.kfang.fencemelibrary.Constants.DEFAULT_MINUTES;
import static com.kfang.fencemelibrary.Constants.GREEN_CARDRED;
import static com.kfang.fencemelibrary.Constants.GREEN_CARDYELLOW;
import static com.kfang.fencemelibrary.Constants.GREEN_NAME;
import static com.kfang.fencemelibrary.Constants.RED_CARDRED;
import static com.kfang.fencemelibrary.Constants.RED_CARDYELLOW;
import static com.kfang.fencemelibrary.Constants.RED_NAME;
import static com.kfang.fencemelibrary.main.MainActivity.mGreenFencer;
import static com.kfang.fencemelibrary.main.MainActivity.mRedFencer;
import static com.kfang.fencemelibrary.main.TimerService.CURRENT_TIME;

/**
 * Class to get preferences.
 */

public class Utility {

    // settings string keys
    public static final int TO_ADD = 1;
    public static final int TO_SUBTRACT = 0;

    public static void saveCurrentMatchPreferences(Context context) {
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

    public static void updateCurrentMatchPreferences(Context context) {
        SharedPreferences gamePrefs = context.getSharedPreferences(CURRENT_GAME_PREFERENCES, Context.MODE_PRIVATE);
        mRedFencer.setPoints(gamePrefs.getInt(CURRENT_RED_POINTS, 0));
        mGreenFencer.setPoints(gamePrefs.getInt(CURRENT_GREEN_POINTS, 0));
        MainActivity.mCurrentTime = gamePrefs.getLong(CURRENT_TIME, DEFAULT_MINUTES * 60000);
        mRedFencer.setRedCards(gamePrefs.getInt(RED_CARDRED, 0));
        mRedFencer.setYellowCards(gamePrefs.getInt(RED_CARDYELLOW, 0));
        mGreenFencer.setRedCards(gamePrefs.getInt(GREEN_CARDRED, 0));
        mGreenFencer.setYellowCards(gamePrefs.getInt(GREEN_CARDYELLOW, 0));
        mRedFencer.setName(gamePrefs.getString(RED_NAME, "Red"));
        mGreenFencer.setName(gamePrefs.getString(GREEN_NAME, "Green"));
    }
}
