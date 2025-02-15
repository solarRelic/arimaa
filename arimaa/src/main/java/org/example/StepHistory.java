package org.example;


import org.example.gamedata.FiguresType;
import org.example.gamedata.Step;
import org.example.gamedata.Team;

import java.io.*;
import java.util.ArrayList;

public class StepHistory {
    private static int stepVision;
    private static ArrayList<Step> stepsHistory = new ArrayList<>();

    /**
     * Add step (board state) to the history.
     * @param step has a board state.
     */
    public static void addStep(Step step) {
        stepsHistory.add(step);
        stepVision = stepsHistory.size() - 1;
    }

    /**
     * Refresh when new game.
     */
    public static void newGame() {
        stepsHistory = new ArrayList<>();
        stepVision = 0;
    }

    /**
     * Get previous step (saved board).
     * @return
     */
    public static Step getPrevStep() {
        if (stepVision == 0) return null;
        stepVision -= 1;
        return stepsHistory.get(stepVision);
    }

    /**
     * Get next step (saved board).
     * @return
     */
    public static Step getNextStep() {
        if (stepVision >= stepsHistory.size() - 1) return null;
        stepVision++;
        return stepsHistory.get(stepVision);
    }

    /**
     * Limit step history
     * (practically deleting N-th state of the game when pressing 'undo' and coming to N-1 saved state).
     */
    public static void reduceStepHistory() {
        stepsHistory = new ArrayList<>(stepsHistory.stream().limit(stepVision + 1).toList());
    }

    /**
     * Save history of steps (saved boards) to an output file for later loading.
     * taken from: https://www.baeldung.com/java-write-to-file#write-with-fileoutputstream,
     * https://www.youtube.com/watch?v=S8ALWHZWylk&ab_channel=RyiSnow
     */
    public static void saveStepHistory() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("step.dat"));
            oos.writeObject(stepsHistory);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load history of steps (saved boards) from a file.
     * taken from: https://www.baeldung.com/java-write-to-file#write-with-fileoutputstream,
     * https://www.youtube.com/watch?v=S8ALWHZWylk&ab_channel=RyiSnow
     */
    public static void loadStepHistory() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("step.dat"));
            stepsHistory = (ArrayList<Step>) ois.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Converting according to the official Arimaa notation.
     */
    public static String getFigurePrefix(FiguresType figuresType, Team team) {
        return switch (figuresType) {
            case ELEPHANT -> (team == Team.GOLD ? "E" : "e");
            case CAMEL -> (team == Team.GOLD ? "M" : "m");
            case HORSE -> (team == Team.GOLD ? "H" : "h");
            case DOG -> (team == Team.GOLD ? "D" : "d");
            case CAT -> (team == Team.GOLD ? "C" : "c");
            case RABBIT -> (team == Team.GOLD ? "R" : "r");
        };
    }

    /**
     * Converting according to the official Arimaa notation.
     */
    public static String toNormalCoordinate(int x, int y) {
        int newY = 8 - y;
        char c = (char) (97 + x);
        return c + "" + newY;
    }
}

//test history,