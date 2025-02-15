package org.example.gamedata;

import java.io.*;

/**
 * Saving the state of the game.
 * taken from: https://www.baeldung.com/java-write-to-file#write-with-fileoutputstream,
 * https://www.youtube.com/watch?v=S8ALWHZWylk&ab_channel=RyiSnow
 */

public class Step implements Serializable {
    private byte[] byteArray;

    /**
     * Saving the current state of the board.
     * @param board - current state of the board.
     */
    public Step(Board board) {
        try {
            ByteArrayOutputStream baOS = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baOS);
            oos.writeObject(board);
            oos.flush();
            byteArray = baOS.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Getting the last saved state of the board.
     * @return last saved state of the board.
     */
    public Board getBoard() {
        try {
            ByteArrayInputStream baIS = new ByteArrayInputStream(byteArray);
            ObjectInputStream ois = new ObjectInputStream(baIS);
            return (Board) ois.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }
}
