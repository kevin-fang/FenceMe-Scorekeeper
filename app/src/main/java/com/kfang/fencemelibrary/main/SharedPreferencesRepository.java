package com.kfang.fencemelibrary.main;

/**
 * Interface used for Shared Preferences
 **/

public interface SharedPreferencesRepository {
    boolean stayAwakeDuringTimer();

    int pointsToWin();

    boolean popupOnScoreChange();

    boolean restoreOnAppReset();

    boolean vibrateOnTimerFinish();

    boolean enableDoubleTouch();

    boolean vibrateOnTimerToggle();

    boolean pauseOnScoreChange();

    int getBoutLength();

}
