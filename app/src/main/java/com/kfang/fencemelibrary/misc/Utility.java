package com.kfang.fencemelibrary.misc;

import android.content.Context;
import android.content.SharedPreferences;

import com.kfang.fencemelibrary.main.Fencer;
import com.kfang.fencemelibrary.main.MainContract;

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
        SharedPreferences gamePrefs = context.getSharedPreferences(Constants.CURRENT_GAME_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor gamePrefsEditor = gamePrefs.edit();
        gamePrefsEditor.putInt(Constants.CURRENT_RED_POINTS, redFencer.getPoints())
                .putInt(Constants.CURRENT_GREEN_POINTS, greenFencer.getPoints())
                .putInt(Constants.CURRENT_TIME, presenter.getCurrentTime())
                .putInt(Constants.RED_CARDRED, redFencer.getRedCards())
                .putInt(Constants.GREEN_CARDRED, greenFencer.getRedCards())
                .putInt(Constants.RED_CARDYELLOW, redFencer.getYellowCards())
                .putInt(Constants.GREEN_CARDYELLOW, greenFencer.getYellowCards())
                .putString(Constants.GREEN_NAME, greenFencer.getName())
                .putString(Constants.RED_NAME, redFencer.getName())
                .apply();
    }

    public static void updateCurrentMatchPreferences(Context context, MainContract.MainPresenter presenter) {
        Fencer redFencer = presenter.getRedFencer();
        Fencer greenFencer = presenter.getGreenFencer();
        SharedPreferences gamePrefs = context.getSharedPreferences(Constants.CURRENT_GAME_PREFERENCES, Context.MODE_PRIVATE);
        redFencer.setPoints(gamePrefs.getInt(Constants.CURRENT_RED_POINTS, 0));
        greenFencer.setPoints(gamePrefs.getInt(Constants.CURRENT_GREEN_POINTS, 0));
        try {
            presenter.setTimer(gamePrefs.getInt(Constants.CURRENT_TIME, Constants.DEFAULT_MINUTES * 60));
        } catch (Exception e) {
            e.printStackTrace();
        }
        redFencer.setRedCards(gamePrefs.getInt(Constants.RED_CARDRED, 0));
        redFencer.setYellowCards(gamePrefs.getInt(Constants.RED_CARDYELLOW, 0));
        greenFencer.setRedCards(gamePrefs.getInt(Constants.GREEN_CARDRED, 0));
        greenFencer.setYellowCards(gamePrefs.getInt(Constants.GREEN_CARDYELLOW, 0));
        redFencer.setName(gamePrefs.getString(Constants.RED_NAME, "Red"));
        greenFencer.setName(gamePrefs.getString(Constants.GREEN_NAME, "Green"));
    }
}
