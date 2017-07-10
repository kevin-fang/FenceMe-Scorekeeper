package com.kfang.fencemelibrary.model


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
    private var totalMilliSeconds: Long = 0


    private fun formatTime(milliseconds: Long): String {
        val hundredthSeconds = milliseconds / 10
        if (hundredthSeconds / 100 < 10) {
            val currentSeconds = hundredthSeconds / 100
            val currentHundredths = hundredthSeconds % 100
            //Log.d(LOG_TAG, "minutes: " + currentMinutes + ", seconds: " + currentSeconds + ", total: " + seconds);
            return String.format(Locale.getDefault(), "%1d.%02d", currentSeconds, currentHundredths)
        } else {
            val currentSeconds = hundredthSeconds / 100 % 60
            val currentMinutes = hundredthSeconds / 100 / 60
            return String.format(Locale.getDefault(), "%01d:%02d", currentMinutes, currentSeconds)
        }
    }

    override var seconds: Int
        get() =
        if (currentTime != -1L) {
            currentTime.toInt()
        } else {
            initialMinutes * 60000
        }
        set(value) {}

    override fun startTimer() {
        totalMilliSeconds = seconds.toLong()
        timerView.updateTime(formatTime(totalMilliSeconds))
        disposable.add(Observable.interval(1, TimeUnit.MILLISECONDS)
                .take(totalMilliSeconds.toInt().toLong())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map { aLong ->
                    currentTime = totalMilliSeconds - aLong.toInt().toLong() - 1
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
                        totalMilliSeconds = (initialMinutes * 60).toLong()
                    }
                }))
    }

    override fun stopTimer() {
        disposable.clear()
    }

    override fun setTimer(milliseconds: Int) {
        disposable.clear()
        totalMilliSeconds = milliseconds.toLong()
        currentTime = totalMilliSeconds
        timerView.updateTime(formatTime(totalMilliSeconds))
    }
}
