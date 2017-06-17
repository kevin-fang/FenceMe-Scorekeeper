package com.kfang.fencemelibrary.model;


import com.kfang.fencemelibrary.presentation.MainContract;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * ReactiveX Async Timer
 */

public class RxTimer implements MainContract.FenceTimer {
    private final CompositeDisposable disposable = new CompositeDisposable();
    private int initialMinutes;
    private MainContract.MainView timerView;
    private long currentTime = -1;
    private long totalMilliSeconds;

    public RxTimer(int initialMinutes, MainContract.MainView mainView) {
        this.initialMinutes = initialMinutes;
        this.timerView = mainView;
    }

    private static String formatTime(long milliseconds) {
        long hundredthseconds = milliseconds / 10;
        if (hundredthseconds / 100 < 10) {
            long currentSeconds = hundredthseconds / 100;
            long currentHundredths = hundredthseconds % 100;
            //Log.d(LOG_TAG, "minutes: " + currentMinutes + ", seconds: " + currentSeconds + ", total: " + seconds);
            return String.format(Locale.getDefault(), "%1d.%02d", currentSeconds, currentHundredths);
        } else {
            long currentSeconds = hundredthseconds / 100 % 60;
            long currentMinutes = hundredthseconds / 100 / 60;
            return String.format(Locale.getDefault(), "%01d:%02d", currentMinutes, currentSeconds);
        }
    }

    @Override // returns in seconds the current time
    public int getTime() {
        if (currentTime != -1) {
            return (int) currentTime;
        } else {
            return initialMinutes * 60000;
        }
    }

    @Override
    public void startTimer() {
        totalMilliSeconds = getTime();
        timerView.updateTime(formatTime(totalMilliSeconds));
        disposable.add(Observable.interval(1, TimeUnit.MILLISECONDS)
                .take((int) totalMilliSeconds)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map(aLong -> {
                    currentTime = totalMilliSeconds - aLong.intValue() - 1;
                    return formatTime(currentTime);
                })
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(@NonNull String s) {
                        timerView.updateTime(s);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        timerView.timerUp();
                        totalMilliSeconds = initialMinutes * 60;
                    }
                }));
    }

    @Override
    public void stopTimer() {
        disposable.clear();
    }

    @Override
    public void setTimer(int milliseconds) {
        disposable.clear();
        totalMilliSeconds = milliseconds;
        currentTime = totalMilliSeconds;
        timerView.updateTime(formatTime(totalMilliSeconds));
    }
}
