package org.example;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private boolean enabled;
    public Logger(boolean enable) {
        this.enabled = enable;
    }

    public void log(Object from, String message) {
        if (enabled)
            // \u001B for orange colour.
            System.out.println("\u001B[33m [" + new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis())) + "]" +
                    "[" + from.getClass().getName() + "]: " + message + "\u001B[0m");
    }
}
