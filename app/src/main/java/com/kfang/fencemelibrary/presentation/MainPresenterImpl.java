package com.kfang.fencemelibrary.presentation;

import android.content.SharedPreferences;
import android.os.Vibrator;

import com.kfang.fencemelibrary.R;
import com.kfang.fencemelibrary.model.Fencer;
import com.kfang.fencemelibrary.model.RxTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.kfang.fencemelibrary.main.CardPlayerActivity.RED_CARD;
import static com.kfang.fencemelibrary.misc.Constants.COLOR_GREEN;
import static com.kfang.fencemelibrary.misc.Constants.COLOR_RED;

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
        fenceTimer = new RxTimer(getBoutLengthMinutes(), mainView);

        redFencer = new Fencer("Red");
        greenFencer = new Fencer("Green");
        fencers = new ArrayList<>();
        fencers.add(redFencer);
        fencers.add(greenFencer);
    }

    @Override
    public void handleCarding(String cardingPlayer, String cardToGive) {
        if (cardingPlayer.equals(redFencer.getName())) {
            if (cardToGive.equals(RED_CARD)) {
                redFencer.incrementRedCards();
                greenFencer.incrementNumPoints();
            } else {
                redFencer.incrementYellowCards();
            }
        } else if (cardingPlayer.equals(greenFencer.getName())) {
            if (cardToGive.equals(RED_CARD)) {
                greenFencer.incrementRedCards();
                redFencer.incrementNumPoints();
            } else {
                greenFencer.incrementYellowCards();
            }
        }
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
    public boolean getTiebreaker() {
        return tieBreakerStatus;
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

    private void setStopButton() {
        mainView.updateToggle(COLOR_RED, R.string.button_stop_timer);
    }

    private void setStartButton() {
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
        fenceTimer.setTimer(getBoutLengthMinutes() * 60 * 1000);
        setStartButton();
        timerRunning = false;
    }

    @Override
    public void stopTimer() {
        if (timerRunning()) {
            fenceTimer.stopTimer();
            setStartButton();
            timerRunning = false;
        }
    }

    @Override
    public void setTimer(int seconds) {
        fenceTimer.setTimer(seconds);
    }

    @Override
    public Fencer randomFencer() {
        Random r = new Random();
        Fencer chosenFencer = fencers.get(r.nextInt(fencers.size()));
        chosenFencer.assignPriority();
        return chosenFencer;
    }

    @Override
    public int getCurrentSeconds() {
        return fenceTimer.getTime();
    }

    @Override
    public void resetCards() {
        redFencer.resetCards();
        greenFencer.resetCards();
    }

    @Override
    public int getBoutLengthMinutes() {
        return sharedPreferences.getBoutLengthMinutes();
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
        if (redFencer.getPoints() >= getPointsToWin() || greenFencer.getPoints() >= getPointsToWin() || tieBreakerStatus) {
            if (checkForVictories(redFencer)) {
                return redFencer;
            } else if (checkForVictories(greenFencer)) {
                return greenFencer;
            }
        }
        return null;
    }

    @Override
    public void setTieBreaker(boolean status) {
        tieBreakerStatus = status;
    }

    @Override
    public boolean checkForVictories(Fencer fencer) {
        // check if the points are not equal and there is a fencer with enough points to win or there is a tiebreaker and the points aren't equal
        if (!equalPoints() && fencer.getPoints() >= getPointsToWin() || (tieBreakerStatus && !equalPoints())) {
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
