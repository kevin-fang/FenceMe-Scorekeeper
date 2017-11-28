package com.kfang.fencemelibrary.misc

import android.content.Context
import com.kfang.fencemelibrary.presentation.MainContract

/**
 * Class to get preferences.
 */

object Utility {

    // settings string keys
    val TO_ADD = 1
    val TO_SUBTRACT = 0

    fun saveCurrentMatchPreferences(context: Context, presenter: MainContract.MainPresenter) {
        val redFencer = presenter.redFencer
        val greenFencer = presenter.greenFencer
        val gamePrefs = context.getSharedPreferences(Constants.CURRENT_GAME_PREFERENCES, Context.MODE_PRIVATE)
        val gamePrefsEditor = gamePrefs.edit()
        gamePrefsEditor.putInt(Constants.CURRENT_RED_POINTS, redFencer.getPoints())
                .putInt(Constants.CURRENT_GREEN_POINTS, greenFencer.getPoints())
                .putInt(Constants.CURRENT_TIME, presenter.currentSeconds)
                .putInt(Constants.RED_CARDRED, redFencer.redCards)
                .putInt(Constants.GREEN_CARDRED, greenFencer.redCards)
                .putInt(Constants.RED_CARDYELLOW, redFencer.yellowCards)
                .putInt(Constants.GREEN_CARDYELLOW, greenFencer.yellowCards)
                .putString(Constants.GREEN_NAME, greenFencer.name)
                .putString(Constants.RED_NAME, redFencer.name)
                .apply()
    }

    fun updateCurrentMatchPreferences(context: Context, presenter: MainContract.MainPresenter) {
        val redFencer = presenter.redFencer
        val greenFencer = presenter.greenFencer
        val gamePrefs = context.getSharedPreferences(Constants.CURRENT_GAME_PREFERENCES, Context.MODE_PRIVATE)
        redFencer.setPoints(gamePrefs.getInt(Constants.CURRENT_RED_POINTS, 0))
        greenFencer.setPoints(gamePrefs.getInt(Constants.CURRENT_GREEN_POINTS, 0))
        try {
            presenter.setTimerSeconds(gamePrefs.getInt(Constants.CURRENT_TIME, Constants.DEFAULT_MINUTES * 60))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        redFencer.redCards = gamePrefs.getInt(Constants.RED_CARDRED, 0)
        redFencer.yellowCards = gamePrefs.getInt(Constants.RED_CARDYELLOW, 0)
        greenFencer.redCards = gamePrefs.getInt(Constants.GREEN_CARDRED, 0)
        greenFencer.yellowCards = gamePrefs.getInt(Constants.GREEN_CARDYELLOW, 0)
        redFencer.name = gamePrefs.getString(Constants.RED_NAME, "Red")
        greenFencer.name = gamePrefs.getString(Constants.GREEN_NAME, "Green")
    }
}
