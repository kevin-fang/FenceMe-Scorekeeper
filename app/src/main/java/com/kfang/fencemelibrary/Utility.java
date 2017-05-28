package com.kfang.fencemelibrary;

import android.content.Context;
import android.content.SharedPreferences;

import com.kfang.fencemelibrary.main.Fencer;
import com.kfang.fencemelibrary.main.MainContract;

import static com.kfang.fencemelibrary.Constants.CURRENT_GAME_PREFERENCES;
import static com.kfang.fencemelibrary.Constants.CURRENT_GREEN_POINTS;
import static com.kfang.fencemelibrary.Constants.CURRENT_RED_POINTS;
import static com.kfang.fencemelibrary.Constants.CURRENT_TIME;
import static com.kfang.fencemelibrary.Constants.DEFAULT_MINUTES;
import static com.kfang.fencemelibrary.Constants.GREEN_CARDRED;
import static com.kfang.fencemelibrary.Constants.GREEN_CARDYELLOW;
import static com.kfang.fencemelibrary.Constants.GREEN_NAME;
import static com.kfang.fencemelibrary.Constants.RED_CARDRED;
import static com.kfang.fencemelibrary.Constants.RED_CARDYELLOW;
import static com.kfang.fencemelibrary.Constants.RED_NAME;

/**
 * Class to get preferences.
 */

public class Utility {

    // settings string keys
    public static final int TO_ADD = 1;
    public static final int TO_SUBTRACT = 0;

    public static void saveCurrentMatchPreferences(Context context, MainContract.MainPresenter presenter) {
        Fencer redFencer = presenter.getRedFencer();
        Fencer greenFencer = presenter.getGreenFencer();
        SharedPreferences gamePrefs = context.getSharedPreferences(CURRENT_GAME_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor gamePrefsEditor = gamePrefs.edit();
        gamePrefsEditor.putInt(CURRENT_RED_POINTS, redFencer.getPoints())
                .putInt(CURRENT_GREEN_POINTS, greenFencer.getPoints())
                .putInt(CURRENT_TIME, presenter.getCurrentTime())
                .putInt(RED_CARDRED, redFencer.getRedCards())
                .putInt(GREEN_CARDRED, greenFencer.getRedCards())
                .putInt(RED_CARDYELLOW, redFencer.getYellowCards())
                .putInt(GREEN_CARDYELLOW, greenFencer.getYellowCards())
                .putString(GREEN_NAME, greenFencer.getName())
                .putString(RED_NAME, redFencer.getName())
                .apply();
    }

    public static void updateCurrentMatchPreferences(Context context, MainContract.MainPresenter presenter) {
        Fencer redFencer = presenter.getRedFencer();
        Fencer greenFencer = presenter.getGreenFencer();
        SharedPreferences gamePrefs = context.getSharedPreferences(CURRENT_GAME_PREFERENCES, Context.MODE_PRIVATE);
        redFencer.setPoints(gamePrefs.getInt(CURRENT_RED_POINTS, 0));
        greenFencer.setPoints(gamePrefs.getInt(CURRENT_GREEN_POINTS, 0));
        presenter.setTimer(gamePrefs.getInt(CURRENT_TIME, DEFAULT_MINUTES * 60));
        redFencer.setRedCards(gamePrefs.getInt(RED_CARDRED, 0));
        redFencer.setYellowCards(gamePrefs.getInt(RED_CARDYELLOW, 0));
        greenFencer.setRedCards(gamePrefs.getInt(GREEN_CARDRED, 0));
        greenFencer.setYellowCards(gamePrefs.getInt(GREEN_CARDYELLOW, 0));
        redFencer.setName(gamePrefs.getString(RED_NAME, "Red"));
        greenFencer.setName(gamePrefs.getString(GREEN_NAME, "Green"));
    }
}
