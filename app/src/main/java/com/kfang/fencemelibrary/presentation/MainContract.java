package com.kfang.fencemelibrary.presentation;

import com.kfang.fencemelibrary.model.Fencer;

/**
 * Main Contract class
 */

public class MainContract {
    public interface MainPresenter {
        Fencer getRedFencer();

        Fencer getGreenFencer();

        void setTieBreaker(boolean status);

        void handleCarding(String cardingPlayer, String cardToGive);

        void toggleTimer();

        void startTimer();

        void stopTimer();

        void resetBout();

        void resetScores();

        void setTimer(int time);

        void resetTimer();

        boolean equalPoints();

        boolean checkForVictories(Fencer fencer);

        Fencer checkForVictories();

        void resetCards();

        Fencer randomFencer();

        Fencer higherPoints();

        void incrementBothPoints();

        boolean timerRunning();

        boolean getTiebreaker();

        int getCurrentSeconds();

        int getBoutLengthMinutes();

        boolean vibrateOnTimerFinish();

        boolean stayAwakeDuringTimer();

        int getPointsToWin();

        boolean popupOnScoreChange();

        boolean restoreOnAppReset();

        boolean enableDoubleTouch();

        boolean vibrateOnTimerToggle();

        boolean pauseOnScoreChange();
    }

    public interface MainView {
        void updateTime(String time);

        void enableChangingScore();

        void enableTimerButton();

        void disableTimerButton();

        void disableChangingScore();

        void timerUp();

        void updateToggle(String colorTo, int text);

        void displayWinnerDialog(Fencer winner);

        void vibrateTimer();
    }

    public interface FenceTimer {
        void startTimer();

        void stopTimer();

        void setTimer(int time);

        int getTime();
    }

}
