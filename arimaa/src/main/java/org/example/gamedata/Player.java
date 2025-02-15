package org.example.gamedata;

import java.io.Serializable;

public class Player implements Serializable {
    private int seconds;
    private int steps;

    /**
     * Add an amount of seconds.
     * @param value - amount of seconds to be added.
     */
    public void addSeconds(int value) {
        this.seconds += value;
    }

    /**
     * Get the amount of seconds that have passed.
     * @return seconds amount.
     */
    public int getSeconds() {
        return seconds;
    }

    /**
     * Set the amount of steps.
     * @param steps - amount of steps to be set.
     */
    public void setSteps(int steps) {
        this.steps = steps;
    }

    /**
     * Reduce the amount of steps.
     * @param value - amount of steps to be reduced by.
     * @return true if there are 0 or fewer steps on a move left, false otherwise.
     */
    public boolean reduceSteps(int value) {
        this.steps -= value;
        return this.steps <= 0;
    }

    /**
     * Get the amount of steps on a move.
     * @return amount of steps on a move.
     */
    public int getSteps() {
        return steps;
    }
}
