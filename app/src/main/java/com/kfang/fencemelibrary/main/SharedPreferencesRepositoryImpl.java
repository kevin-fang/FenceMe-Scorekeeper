package com.kfang.fencemelibrary.main;

import android.content.SharedPreferences;
import android.os.Vibrator;

import static com.kfang.fencemelibrary.Constants.BOUT_LENGTH_MINUTES;
import static com.kfang.fencemelibrary.Constants.BOUT_LENGTH_POINTS;
import static com.kfang.fencemelibrary.Constants.DEFAULT_MINUTES;
import static com.kfang.fencemelibrary.Constants.DEFAULT_POINTS;
import static com.kfang.fencemelibrary.Constants.KEEP_DEVICE_AWAKE;
import static com.kfang.fencemelibrary.Constants.PAUSE_ON_SCORE_CHANGE;
import static com.kfang.fencemelibrary.Constants.POPUP_ON_SCORE;
import static com.kfang.fencemelibrary.Constants.RESTORE_ON_EXIT;
import static com.kfang.fencemelibrary.Constants.TOGGLE_DOUBLE_TOUCH;
import static com.kfang.fencemelibrary.Constants.VIBRATE_AT_END;
import static com.kfang.fencemelibrary.Constants.VIBRATE_TIMER;

/**
 * SharedPreferences repository implementation
 */

public class SharedPreferencesRepositoryImpl implements SharedPreferencesRepository {

    private SharedPreferences prefs;
    private Vibrator v;

    public SharedPreferencesRepositoryImpl(SharedPreferences sp, Vibrator v) {

        prefs = sp;
        this.v = v;
    }

    @Override
    public boolean stayAwakeDuringTimer() {
        return prefs.getBoolean(KEEP_DEVICE_AWAKE, true);
    }

    @Override
    public int pointsToWin() {
        return prefs.getInt(BOUT_LENGTH_POINTS, DEFAULT_POINTS);
    }

    @Override
    public boolean popupOnScoreChange() {
        return prefs.getBoolean(POPUP_ON_SCORE, false);
    }

    @Override
    public boolean restoreOnAppReset() {
        return prefs.getBoolean(RESTORE_ON_EXIT, true);
    }

    @Override
    public boolean vibrateOnTimerFinish() {
        return prefs.getBoolean(VIBRATE_AT_END, true);
    }

    @Override
    public boolean enableDoubleTouch() {
        return prefs.getBoolean(TOGGLE_DOUBLE_TOUCH, true);
    }

    @Override
    public boolean vibrateOnTimerToggle() {
        return prefs.getBoolean(VIBRATE_TIMER, true) && v.hasVibrator();
    }

    @Override
    public boolean pauseOnScoreChange() {
        return prefs.getBoolean(PAUSE_ON_SCORE_CHANGE, true);
    }

    @Override
    public int getBoutLengthMinutes() {
        return prefs.getInt(BOUT_LENGTH_MINUTES, DEFAULT_MINUTES);
    }
}
