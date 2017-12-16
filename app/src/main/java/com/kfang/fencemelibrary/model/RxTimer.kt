package com.kfang.fencemelibrary.model


import android.util.Log
import com.kfang.fencemelibrary.presentation.MainContract
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * ReactiveX Async Timer
 */

class RxTimer(private val initialMinutes: Int, private val timerView: MainContract.MainView) : MainContract.FenceTimer {
    private val disposable = CompositeDisposable()
    private var currentTime: Long = -1
    // decisecond = 1/100th of a second
    private var totalDeciseconds: Long = 0

    private fun formatTime(deciSeconds: Long): String {
        return if (deciSeconds / 100 < 10) {
            val currentSeconds = deciSeconds / 100
            val currentHundredths = deciSeconds % 100
            //Log.d(LOG_TAG, "minutes: " + currentMinutes + ", deciSeconds: " + currentDeciSeconds + ", total: " + deciSeconds);
            String.format(Locale.getDefault(), "%1d.%02d", currentSeconds, currentHundredths)
        } else {
            val currentMinutes: Long = deciSeconds / 100 / 60
            val currentSeconds = deciSeconds / 100 % 60
            String.format(Locale.getDefault(), "%01d:%02d", currentMinutes, currentSeconds)
        }
    }

    override var deciSeconds: Int = 0
            // handle if the time was never set, return the initial minutes
        get() = if (currentTime != -1L) {
            currentTime.toInt()
        } else {
            initialMinutes * 60
        }

    override fun startTimer() {
        totalDeciseconds = deciSeconds.toLong()
        timerView.updateTime(formatTime(totalDeciseconds))
        disposable.add(Observable.interval(10, TimeUnit.MILLISECONDS)
                .take(totalDeciseconds)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map { aLong ->
                    currentTime = totalDeciseconds - aLong.toInt().toLong() - 1
                    formatTime(currentTime)
                }
                .subscribeWith(object : DisposableObserver<String>() {
                    override fun onNext(@NonNull s: String) {
                        timerView.updateTime(s)
                    }

                    override fun onError(@NonNull e: Throwable) {

                    }

                    override fun onComplete() {
                        timerView.timerUp()
                        totalDeciseconds = (initialMinutes * 60).toLong()
                    }
                }))
    }

    override fun stopTimer() {
        disposable.clear()
    }

    override fun setTimerSeconds(seconds: Int) {
        val deciSec = seconds * 100
        Log.d("RXTIMER", "Time set: " + seconds)
        disposable.clear()
        totalDeciseconds = (deciSec).toLong()
        currentTime = totalDeciseconds
        timerView.updateTime(formatTime(totalDeciseconds))
    }
}
