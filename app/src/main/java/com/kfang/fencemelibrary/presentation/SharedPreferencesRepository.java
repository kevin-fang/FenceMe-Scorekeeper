package com.kfang.fencemelibrary.presentation;

/**
 * Interface used for Shared Preferences
 **/

interface SharedPreferencesRepository {
    boolean stayAwakeDuringTimer();

    int pointsToWin();

    boolean popupOnScoreChange();

    boolean restoreOnAppReset();

    boolean vibrateOnTimerFinish();

    boolean enableDoubleTouch();

    boolean vibrateOnTimerToggle();

    boolean pauseOnScoreChange();

    int getBoutLengthMinutes();

}
