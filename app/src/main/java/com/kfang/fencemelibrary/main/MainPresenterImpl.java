package com.kfang.fencemelibrary.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;

import static com.kfang.fencemelibrary.Constants.CHANGE_TIMER;

/**
 * Main Presenter Implementation
 */

public class MainPresenterImpl implements IMainPresenter {

    private SharedPreferencesRepository sharedPreferences;
    private IMainView mainView;

    public MainPresenterImpl(IMainView mainView, SharedPreferences sp, Vibrator v) {
        sharedPreferences = new SharedPreferencesRepositoryImpl(sp, v);
        this.mainView = mainView;

    }

    @Override
    public boolean keepScreenAwake() {
        return sharedPreferences.stayAwakeDuringTimer();
    }

    @Override
    public void toggleTimer(Context context) {
        // create toggle timer intent and fire
        Intent startTimer = new Intent(context, TimerService.class);
        startTimer.putExtra(CHANGE_TIMER, TimerService.TOGGLE_TIMER);
        context.startService(startTimer);
    }

    @Override
    public void resetTimer(Context context) {
        Intent stopTimer = new Intent(context.getApplicationContext(), TimerService.class);
        stopTimer.putExtra(CHANGE_TIMER, TimerService.RESET_TIMER);
        context.startService(stopTimer);
    }

    @Override
    public boolean getRestoreStatus() {
        return sharedPreferences.restoreOnAppReset();
    }

    @Override
    public boolean getDoubleTouch() {
        return sharedPreferences.enableDoubleTouch();
    }

    @Override
    public int getBoutLength() {
        return sharedPreferences.getBoutLength();
    }

    @Override
    public void resetBout() {

    }

    @Override
    public void resetScores() {

    }

    @Override
    public void setTimer() {

    }

    @Override
    public boolean checkForVictories() {
        return false;
    }

    @Override
    public void changeName() {

    }

    @Override
    public boolean vibrateOnTimerFinish() {
        return sharedPreferences.vibrateOnTimerFinish();
    }

    @Override
    public boolean stayAwakeDuringTimer() {
        return sharedPreferences.stayAwakeDuringTimer();
    }

    @Override
    public int getPointsToWin() {
        return sharedPreferences.pointsToWin();
    }

    @Override
    public boolean popupOnScoreChange() {
        return sharedPreferences.popupOnScoreChange();
    }

    @Override
    public boolean restoreOnAppReset() {
        return sharedPreferences.restoreOnAppReset();
    }

    @Override
    public boolean vibrateAtEnd() {
        return sharedPreferences.vibrateOnTimerFinish();
    }

    @Override
    public boolean enableDoubleTouch() {
        return sharedPreferences.enableDoubleTouch();
    }

    @Override
    public boolean vibrateOnTimerToggle() {
        return sharedPreferences.vibrateOnTimerToggle();
    }

    @Override
    public boolean pauseOnScoreChange() {
        return sharedPreferences.pauseOnScoreChange();
    }
}
