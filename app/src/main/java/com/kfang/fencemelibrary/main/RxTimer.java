package com.kfang.fencemelibrary.main;


import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * ReactiveX Async Timer
 */

class RxTimer implements MainContract.FenceTimer {
    private final CompositeDisposable disposable = new CompositeDisposable();
    private int initialMinutes;
    private MainContract.MainView timerView;
    private long currentTime = -1;
    private long totalSeconds;

    RxTimer(int initialMinutes, MainContract.MainView mainView) {
        this.initialMinutes = initialMinutes;
        this.timerView = mainView;
    }

    private static String formatTime(long seconds) {
        long currentSeconds = seconds % 60;
        long currentMinutes = seconds / 60;
        //Log.d(LOG_TAG, "minutes: " + currentMinutes + ", seconds: " + currentSeconds + ", total: " + seconds);
        return String.format(Locale.getDefault(), "%01d:%02d", currentMinutes, currentSeconds);
    }

    @Override // returns in seconds the current time
    public int getTime() {
        if (currentTime != -1) {
            return (int) currentTime;
        } else {
            return initialMinutes * 60;
        }
    }

    @Override
    public void startTimer() {
        totalSeconds = getTime();
        timerView.updateTime(formatTime(totalSeconds));
        disposable.add(Observable.interval(1, TimeUnit.SECONDS)
                .take((int) totalSeconds)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map(aLong -> {
                    currentTime = totalSeconds - aLong.intValue() - 1;
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
                        totalSeconds = initialMinutes * 60;
                    }
                }));
    }

    @Override
    public void stopTimer() {
        disposable.clear();
    }

    @Override
    public void setTimer(int time) {
        disposable.clear();
        totalSeconds = time;
        currentTime = totalSeconds;
        timerView.updateTime(formatTime(totalSeconds));
    }
}
