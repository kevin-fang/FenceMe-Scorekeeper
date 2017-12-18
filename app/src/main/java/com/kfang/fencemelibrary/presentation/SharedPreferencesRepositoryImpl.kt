package com.kfang.fencemelibrary.presentation

import android.content.SharedPreferences
import android.os.Vibrator
import com.kfang.fencemelibrary.misc.Constants

/**
 * SharedPreferences repository implementation
 */

internal class SharedPreferencesRepositoryImpl(private val prefs: SharedPreferences, private val v: Vibrator) : SharedPreferencesRepository {

    override fun stayAwakeDuringTimer(): Boolean {
        return prefs.getBoolean(Constants.KEEP_DEVICE_AWAKE, true)
    }

    override fun pointsToWin(): Int {
        return prefs.getInt(Constants.BOUT_LENGTH_POINTS, Constants.DEFAULT_POINTS)
    }

    override fun popupOnScoreChange(): Boolean {
        return prefs.getBoolean(Constants.POPUP_ON_SCORE, false)
    }

    override fun restoreOnAppReset(): Boolean {
        return prefs.getBoolean(Constants.RESTORE_ON_EXIT, true)
    }

    override fun vibrateOnTimerFinish(): Boolean {
        return prefs.getBoolean(Constants.VIBRATE_AT_END, true)
    }

    override fun volumeButtonTimerToggle(): Boolean {
        return prefs.getBoolean(Constants.VOLUME_BUTTON_TIMER_TOGGLE, true)
    }

    override fun enableDoubleTouch(): Boolean {
        return prefs.getBoolean(Constants.TOGGLE_DOUBLE_TOUCH, true)
    }

    override fun vibrateOnTimerToggle(): Boolean {
        return prefs.getBoolean(Constants.VIBRATE_TIMER, true) && v.hasVibrator()
    }

    override fun pauseOnScoreChange(): Boolean {
        return prefs.getBoolean(Constants.PAUSE_ON_SCORE_CHANGE, true)
    }

    override fun getBoutLengthMinutes(): Int {
        return prefs.getInt(Constants.BOUT_LENGTH_MINUTES, Constants.DEFAULT_MINUTES)
    }
}
