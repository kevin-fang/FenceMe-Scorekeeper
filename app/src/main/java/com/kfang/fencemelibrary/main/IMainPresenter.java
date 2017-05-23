package com.kfang.fencemelibrary.main;


import android.content.Context;

/**
 * IMainPresenter interface
 */

public interface IMainPresenter {

    void toggleTimer(Context context);

    boolean keepScreenAwake();

    void resetTimer(Context context);

    void resetBout();

    void resetScores();

    void setTimer();

    boolean checkForVictories();

    void changeName();

    boolean getDoubleTouch();

    boolean getRestoreStatus();

    int getBoutLength();

    boolean vibrateOnTimerFinish();

    boolean stayAwakeDuringTimer();

    int getPointsToWin();

    boolean popupOnScoreChange();

    boolean restoreOnAppReset();

    boolean vibrateAtEnd();

    boolean enableDoubleTouch();

    boolean vibrateOnTimerToggle();

    boolean pauseOnScoreChange();
}
