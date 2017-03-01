package com.kfang.fenceme;

/**
 * Used for eventBus
 */

public class TimerServiceEvent {
    private final String action;

    public TimerServiceEvent(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}
