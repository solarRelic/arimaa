package org.example;


import org.example.gui.ControlPanel;

import java.text.SimpleDateFormat;

public class Clock implements Runnable {
    private final Arimaa arimaa;
    private int timerValue;
    private boolean enableTimer;

    public Clock(Arimaa arimaa) {
        this.arimaa = arimaa;
    }

    /**
     * Starting the timer for showing how much
     * time has been spent
     */
    public void startTimer() {
        timerValue = 0;
        enableTimer = true;
    }

    /**
     * Disable the timer.
     */
    public void stopTimer() {
        enableTimer = false;
    }

    /**
     * Get current timer value.
     * @return timer value.
     */
    public int getTimerValue() {
        return timerValue;
    }

    public void run() {
        while (true) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            ControlPanel cp = arimaa.getControlPanel();
            if (cp != null)
                cp.setCurrentTime(sdf.format(System.currentTimeMillis()));
            if (enableTimer)
                timerValue++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
