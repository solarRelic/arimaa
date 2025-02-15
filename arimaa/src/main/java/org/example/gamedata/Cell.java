package org.example.gamedata;

import java.io.Serializable;

public class Cell implements Serializable {
    private final int x;
    private final int y;
    private Figure figure;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setFigure(Figure figure) {
        this.figure = figure;
    }
    public Figure getFigure() {
        return figure;
    }
    public void clearFigure() {
        this.figure = null;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
}
