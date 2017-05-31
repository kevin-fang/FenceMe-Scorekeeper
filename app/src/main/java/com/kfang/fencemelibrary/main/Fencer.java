package com.kfang.fencemelibrary.main;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.kfang.fencemelibrary.BR;

import java.io.Serializable;


/**
 * Class to contain fencers
 */

public class Fencer extends BaseObservable implements Serializable {
    public int points;
    public String name;
    private int redCards;
    private int yellowCards;
    private boolean hasPriority;

    // is decrement card functionality really needed?

    public Fencer(String name) {
        this.points = 0;
        this.name = name;
        this.redCards = 0;
        this.yellowCards = 0;
    }

    public String getDefaultName() {
        return name;
    }

    public void assignPriority() {
        this.hasPriority = true;
    }

    public boolean hasPriority() {
        return this.hasPriority;
    }

    @Bindable
    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
        notifyPropertyChanged(BR.points);
    }

    public void incrementNumPoints() {
        this.points++;
        notifyPropertyChanged(BR.points);
    }

    public void decrementNumPoints() {
        this.points--;
        notifyPropertyChanged(BR.points);
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public int getRedCards() {
        return redCards;
    }

    public void setRedCards(int cards) {
        this.redCards = cards;
    }

    public void incrementRedCards() {
        this.redCards++;
    }

    @Bindable
    public int getYellowCards() {
        return yellowCards;
    }

    public void setYellowCards(int cards) {
        this.yellowCards = cards;
    }

    public void incrementYellowCards() {
        this.yellowCards++;
    }

    public void resetCards() {
        this.redCards = 0;
        this.yellowCards = 0;
    }
}
