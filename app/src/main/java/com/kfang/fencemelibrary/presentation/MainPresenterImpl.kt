package com.kfang.fencemelibrary.presentation

import android.content.SharedPreferences
import android.os.Vibrator
import com.kfang.fencemelibrary.main.CardPlayerActivity
import com.kfang.fencemelibrary.misc.Constants
import com.kfang.fencemelibrary.model.Fencer
import com.kfang.fencemelibrary.model.RxTimer
import java.util.*


/**
 * Main Presenter Implementation
 */

class MainPresenterImpl(private val mainView: MainContract.MainView, sp: SharedPreferences, v: Vibrator) : MainContract.MainPresenter {

    private val sharedPreferences: SharedPreferencesRepository
    private val fenceTimer: MainContract.FenceTimer
    override var timerRunning = false
    override var tiebreaker: Boolean = false
    override val redFencer: Fencer
    override val greenFencer: Fencer

    override var currentSeconds: Int = -1
        get() = fenceTimer.seconds

    override var boutLengthMinutes: Int = -1
        get() = sharedPreferences.getBoutLengthMinutes()

    override var pointsToWin: Int = -1
        get() = sharedPreferences.pointsToWin()

    private val fencers: MutableList<Fencer>

    init {
        sharedPreferences = SharedPreferencesRepositoryImpl(sp, v)
        fenceTimer = RxTimer(boutLengthMinutes * 1000 * 60, mainView)

        redFencer = Fencer("Red")
        greenFencer = Fencer("Green")
        fencers = ArrayList()
        fencers.add(redFencer)
        fencers.add(greenFencer)
    }

    override var sabreMode: Boolean = false
        set(value) {
            if (value) {
                stopTimer()
                mainView.hideTimer()
            } else {
                mainView.showTimer()
            }
            field = value
        }

    override fun toggleSabreMode() {
        if (sabreMode) {
            sabreMode = false
            mainView.showTimer()
        } else {
            sabreMode = true
            mainView.hideTimer()
        }
    }

    override fun handleCarding(cardingPlayer: String, cardToGive: String) {
        if (cardingPlayer == redFencer.name) {
            if (cardToGive == CardPlayerActivity.RED_CARD) {
                redFencer.incrementRedCards()
                greenFencer.incrementNumPoints()
            } else {
                redFencer.incrementYellowCards()
            }
        } else if (cardingPlayer == greenFencer.name) {
            if (cardToGive == CardPlayerActivity.RED_CARD) {
                greenFencer.incrementRedCards()
                redFencer.incrementNumPoints()
            } else {
                greenFencer.incrementYellowCards()
            }
        }
    }

    override fun higherPoints(): Fencer? {
        return when {
            redFencer.getPoints() > greenFencer.getPoints() -> redFencer
            greenFencer.getPoints() > redFencer.getPoints() -> greenFencer
            else -> null
        }
    }

    override fun incrementBothPoints() {
        redFencer.incrementNumPoints()
        greenFencer.incrementNumPoints()
    }

    override fun toggleTimer() {
        if (timerRunning && !sabreMode) {
            stopTimer()
            timerRunning = false
        } else if (!sabreMode) {
            startTimer()
            timerRunning = true
        }
    }
    private fun setStopButton() {
        mainView.setTimerColor(Constants.COLOR_RED)
    }

    private fun setStartButton() {
        mainView.setTimerColor(Constants.COLOR_GREEN)
    }

    override fun startTimer() {
        if (!sabreMode) {
            fenceTimer.startTimer()
            setStopButton()
            timerRunning = true
            mainView.vibrateStart()
        }
    }

    override fun resetTimer() {
        fenceTimer.setTimer(boutLengthMinutes * 60 * 1000)
        setStartButton()
        timerRunning = false
    }

    override fun stopTimer() {
        if (!sabreMode && timerRunning) {
            fenceTimer.stopTimer()
            setStartButton()
            timerRunning = false
            mainView.vibrateStop()
        }
    }

    override fun setTimer(seconds: Int) {
        fenceTimer.setTimer(seconds)
    }

    override fun randomFencer(): Fencer {
        val r = Random()
        val chosenFencer = fencers[r.nextInt(fencers.size)]
        chosenFencer.priority = true
        return chosenFencer
    }

    override fun resetCards() {
        redFencer.resetCards()
        greenFencer.resetCards()
    }

    override fun resetBout() {
        resetCards()
        resetScores()
        resetTimer()
    }

    override fun resetScores() {
        mainView.setScoreChangeability(true)
        tiebreaker = false
        redFencer.setPoints(0)
        greenFencer.setPoints(0)
    }

    override fun equalPoints(): Boolean {
        return redFencer.getPoints() == greenFencer.getPoints()
    }

    override fun checkForVictories(): Fencer? {
        if (redFencer.getPoints() >= pointsToWin || greenFencer.getPoints() >= pointsToWin || tiebreaker) {
            if (checkForVictories(redFencer)) {
                return redFencer
            } else if (checkForVictories(greenFencer)) {
                return greenFencer
            }
        }
        return null
    }

    override fun checkForVictories(fencer: Fencer): Boolean {
        // check if the points are not equal and there is a fencer with enough points to win or there is a tiebreaker and the points aren't equal
        if (!equalPoints() && fencer.getPoints() >= pointsToWin || tiebreaker && !equalPoints()) {
            stopTimer()
            mainView.setScoreChangeability(false)
            mainView.displayWinnerDialog(fencer)
            return true
        }
        return false
    }

    override fun vibrateOnTimerFinish(): Boolean {
        return sharedPreferences.vibrateOnTimerFinish()
    }

    override fun stayAwakeDuringTimer(): Boolean {
        return sharedPreferences.stayAwakeDuringTimer()
    }

    override fun popupOnScoreChange(): Boolean {
        return sharedPreferences.popupOnScoreChange()
    }

    override fun restoreOnAppReset(): Boolean {
        return sharedPreferences.restoreOnAppReset()
    }

    override fun enableDoubleTouch(): Boolean {
        return sharedPreferences.enableDoubleTouch()
    }

    override fun vibrateOnTimerToggle(): Boolean {
        return sharedPreferences.vibrateOnTimerToggle()
    }

    override fun pauseOnScoreChange(): Boolean {
        return sharedPreferences.pauseOnScoreChange()
    }
}
