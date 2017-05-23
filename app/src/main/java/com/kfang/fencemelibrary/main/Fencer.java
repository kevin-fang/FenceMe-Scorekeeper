package com.kfang.fencemelibrary.main;

/**
 * Class to contain fencers
 */

public class Fencer {
    public int points;
    public String name;
    private int redCards;
    private int yellowCards;
    private boolean hasPriority;

    // is decrement card functionality really needed?

    Fencer(String name) {
        this.points = 0;
        this.name = name;
        this.redCards = 0;
        this.yellowCards = 0;
    }

    void assignPriority() {
        this.hasPriority = true;
    }

    boolean hasPriority() {
        return this.hasPriority;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void incrementNumPoints() {
        this.points++;
    }

    public void decrementNumPoints() {
        this.points--;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public int getRedCards() {
        return redCards;
    }

    public void setRedCards(int cards) {
        this.redCards = cards;
    }

    public void incrementRedCards() {
        this.redCards++;
    }

    public int getYellowCards() {
        return yellowCards;
    }

    public void setYellowCards(int cards) {
        this.yellowCards = cards;
    }

    public void incrementYellowCards() {
        this.yellowCards++;
    }

    void resetCards() {
        this.redCards = 0;
        this.yellowCards = 0;
    }
}
