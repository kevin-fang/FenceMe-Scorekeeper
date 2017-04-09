package com.kfang.fencemelibrary;

/**
 * Class to contain fencers
 */

class Fencer {
    private int points;
    private String name;
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

    int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    void incrementNumPoints() {
        this.points++;
    }

    void decrementNumPoints() {
        this.points--;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    int getRedCards() {
        return redCards;
    }

    void setRedCards(int cards) {
        this.redCards = cards;
    }

    void incrementRedCards() {
        this.redCards++;
    }

    /* void decrementRedCards() {
        this.redCards--;
    } */

    int getYellowCards() {
        return yellowCards;
    }

    void setYellowCards(int cards) {
        this.yellowCards = cards;
    }

    void incrementYellowCards() {
        this.yellowCards++;
    }

    /*
    void decrementYellowCards() {
        this.yellowCards--;
    }
    */

    void resetCards() {
        this.redCards = 0;
        this.yellowCards = 0;
    }
}
