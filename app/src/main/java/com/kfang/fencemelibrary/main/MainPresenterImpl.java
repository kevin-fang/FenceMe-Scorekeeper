package com.kfang.fencemelibrary.main;

import android.content.SharedPreferences;
import android.os.Vibrator;

import com.kfang.fencemelibrary.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.kfang.fencemelibrary.Constants.COLOR_GREEN;
import static com.kfang.fencemelibrary.Constants.COLOR_RED;

/**
 * Main Presenter Implementation
 */

public class MainPresenterImpl implements MainContract.MainPresenter {

    private SharedPreferencesRepository sharedPreferences;
    private MainContract.MainView mainView;
    private MainContract.FenceTimer fenceTimer;
    private boolean timerRunning = false;
    private boolean tieBreakerStatus;

    private Fencer redFencer;
    private Fencer greenFencer;

    private List<Fencer> fencers;

    public MainPresenterImpl(MainContract.MainView mainView, SharedPreferences sp, Vibrator v) {
        sharedPreferences = new SharedPreferencesRepositoryImpl(sp, v);
        this.mainView = mainView;
        fenceTimer = new RxTimer(getBoutLength(), mainView, this);

        redFencer = new Fencer("Red");
        greenFencer = new Fencer("Green");
        fencers = new ArrayList<>();
        fencers.add(redFencer);
        fencers.add(greenFencer);
    }


    @Override
    public Fencer higherPoints() {
        if (redFencer.getPoints() > greenFencer.getPoints()) {
            return redFencer;
        } else if (greenFencer.getPoints() > redFencer.getPoints()) {
            return greenFencer;
        } else {
            return null;
        }
    }

    @Override
    public void incrementBothPoints() {
        redFencer.incrementNumPoints();
        greenFencer.incrementNumPoints();
    }

    @Override
    public Fencer getRedFencer() {
        return redFencer;
    }

    @Override
    public Fencer getGreenFencer() {
        return greenFencer;
    }

    @Override
    public void toggleTimer() {
        mainView.vibrateTimer();
        if (timerRunning) {
            stopTimer();
            timerRunning = false;
        } else {
            startTimer();
            timerRunning = true;
        }

    }

    void setStopButton() {
        mainView.updateToggle(COLOR_RED, R.string.button_stop_timer);
    }

    void setStartButton() {
        mainView.updateToggle(COLOR_GREEN, R.string.button_start_timer);
    }

    @Override
    public boolean timerRunning() {
        return timerRunning;
    }

    @Override
    public void startTimer() {
        fenceTimer.startTimer();
        setStopButton();
        timerRunning = true;
    }

    @Override
    public void resetTimer() {
        fenceTimer.setTimer(getBoutLength() * 60);
        setStartButton();
        timerRunning = false;
    }

    @Override
    public void stopTimer() {
        fenceTimer.stopTimer();
        setStartButton();
        timerRunning = false;
    }

    @Override
    public void setTimer(int time) {
        fenceTimer.setTimer(time);
    }

    @Override
    public Fencer randomFencer() {
        Random r = new Random();
        Fencer chosenFencer = fencers.get(r.nextInt(fencers.size()));
        chosenFencer.assignPriority();
        return chosenFencer;
    }

    @Override
    public int getCurrentTime() {
        return fenceTimer.getTime();
    }

    @Override
    public void resetCards() {
        redFencer.resetCards();
        greenFencer.resetCards();
    }

    @Override
    public int getBoutLength() {
        return sharedPreferences.getBoutLength();
    }

    @Override
    public void resetBout() {
        resetCards();
        resetScores();
        resetTimer();
        mainView.enableTimerButton();

    }

    @Override
    public void resetScores() {
        mainView.enableChangingScore();
        tieBreakerStatus = false;
        redFencer.setPoints(0);
        greenFencer.setPoints(0);
    }

    @Override
    public boolean equalPoints() {
        return redFencer.getPoints() == greenFencer.getPoints();
    }

    @Override
    public Fencer checkForVictories() {
        if (redFencer.getPoints() >= getBoutLength()) {
            if (checkForVictories(redFencer)) {
                return redFencer;
            } else if (checkForVictories(greenFencer)) {
                return greenFencer;
            }
        }
        return null;
    }

    @Override
    public boolean checkForVictories(Fencer fencer) {
        // check if the points are not equal and there is a fencer with enough points to win or there is a tiebreaker and the points aren't equal
        if (!equalPoints() && fencer.getPoints() >= getPointsToWin() || (tieBreakerStatus && equalPoints())) {
            if (timerRunning) {
                fenceTimer.stopTimer();
            }
            mainView.disableChangingScore();
            mainView.displayWinnerDialog(fencer);
            return true;
        }
        return false;
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
