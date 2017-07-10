package com.kfang.fencemelibrary.presentation

import android.content.SharedPreferences
import android.os.Vibrator
import com.kfang.fencemelibrary.R
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
    private var timerRunning = false
    private var tieBreakerStatus: Boolean = false

    override val redFencer: Fencer
    override val greenFencer: Fencer

    override var tiebreaker: Boolean = false
        get() = tieBreakerStatus
        set(value) {
            field = value
        }

    override var currentSeconds: Int
        get() = fenceTimer.seconds
        set(value) {}

    override var boutLengthMinutes: Int
        get() = sharedPreferences.getBoutLengthMinutes()
        set(value) {}

    override var pointsToWin: Int
        get() = sharedPreferences.pointsToWin()
        set(value) {}

    private val fencers: MutableList<Fencer>

    init {
        sharedPreferences = SharedPreferencesRepositoryImpl(sp, v)
        fenceTimer = RxTimer(boutLengthMinutes * 1000 * 60, mainView)

        redFencer = Fencer("Red")
        greenFencer = Fencer("Green")
        fencers = ArrayList<Fencer>()
        fencers.add(redFencer)
        fencers.add(greenFencer)
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
        if (redFencer.getPoints() > greenFencer.getPoints()) {
            return redFencer
        } else if (greenFencer.getPoints() > redFencer.getPoints()) {
            return greenFencer
        } else {
            return null
        }
    }

    override fun incrementBothPoints() {
        redFencer.incrementNumPoints()
        greenFencer.incrementNumPoints()
    }

    override fun toggleTimer() {
        mainView.vibrateTimer()
        if (timerRunning) {
            stopTimer()
            timerRunning = false
        } else {
            startTimer()
            timerRunning = true
        }

    }

    private fun setStopButton() {
        mainView.updateToggle(Constants.COLOR_RED, R.string.button_stop_timer)
    }

    private fun setStartButton() {
        mainView.updateToggle(Constants.COLOR_GREEN, R.string.button_start_timer)
    }

    override fun timerRunning(): Boolean {
        return timerRunning
    }

    override fun startTimer() {
        fenceTimer.startTimer()
        setStopButton()
        timerRunning = true
    }

    override fun resetTimer() {
        fenceTimer.setTimer(boutLengthMinutes * 60 * 1000)
        setStartButton()
        timerRunning = false
    }

    override fun stopTimer() {
        if (timerRunning()) {
            fenceTimer.stopTimer()
            setStartButton()
            timerRunning = false
        }
    }

    override fun setTimer(seconds: Int) {
        fenceTimer.setTimer(seconds)
    }

    override fun randomFencer(): Fencer {
        val r = Random()
        val chosenFencer = fencers[r.nextInt(fencers.size)]
        chosenFencer.assignPriority()
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
        mainView.enableTimerButton()
    }

    override fun resetScores() {
        mainView.enableChangingScore()
        tieBreakerStatus = false
        redFencer.setPoints(0)
        greenFencer.setPoints(0)
    }

    override fun equalPoints(): Boolean {
        return redFencer.getPoints() == greenFencer.getPoints()
    }

    override fun checkForVictories(): Fencer? {
        if (redFencer.getPoints() >= pointsToWin || greenFencer.getPoints() >= pointsToWin || tieBreakerStatus) {
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
        if (!equalPoints() && fencer.getPoints() >= pointsToWin || tieBreakerStatus && !equalPoints()) {
            if (timerRunning) {
                fenceTimer.stopTimer()
            }
            mainView.disableChangingScore()
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
