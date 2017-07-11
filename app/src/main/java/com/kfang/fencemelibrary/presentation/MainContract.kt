package com.kfang.fencemelibrary.presentation

import com.kfang.fencemelibrary.model.Fencer

/**
 * Main Contract class
 */

class MainContract {
    interface MainPresenter {
        val redFencer: Fencer

        val greenFencer: Fencer

        fun handleCarding(cardingPlayer: String, cardToGive: String)

        fun toggleTimer()

        fun startTimer()

        fun stopTimer()

        fun resetBout()

        fun resetScores()

        fun setTimer(seconds: Int)

        fun resetTimer()

        fun equalPoints(): Boolean

        fun checkForVictories(fencer: Fencer): Boolean

        fun checkForVictories(): Fencer?

        fun resetCards()

        fun randomFencer(): Fencer

        fun higherPoints(): Fencer?

        fun incrementBothPoints()

        var timerRunning: Boolean

        var tiebreaker: Boolean

        var currentSeconds: Int

        var boutLengthMinutes: Int

        fun vibrateOnTimerFinish(): Boolean

        fun stayAwakeDuringTimer(): Boolean

        var pointsToWin: Int

        fun popupOnScoreChange(): Boolean

        fun restoreOnAppReset(): Boolean

        fun enableDoubleTouch(): Boolean

        fun vibrateOnTimerToggle(): Boolean

        fun pauseOnScoreChange(): Boolean
    }

    interface MainView {
        fun updateTime(time: String)

        fun setScoreChangeability(allowChange: Boolean)

        fun timerUp()

        fun displayWinnerDialog(winner: Fencer)

        fun vibrateTimeUp()

        fun setTimerColor(color: String)
    }

    interface FenceTimer {
        fun startTimer()

        fun stopTimer()

        fun setTimer(milliseconds: Int)

        var seconds: Int
    }

}
