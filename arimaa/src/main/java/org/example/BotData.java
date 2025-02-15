package org.example;


import org.example.gamedata.FiguresType;

import java.util.ArrayList;

public class BotData {
    private static final ArrayList<FiguresType> figures = new ArrayList<>();
    static {
        figures.add(FiguresType.RABBIT);
        figures.add(FiguresType.RABBIT);
        figures.add(FiguresType.RABBIT);
        figures.add(FiguresType.RABBIT);
        figures.add(FiguresType.RABBIT);
        figures.add(FiguresType.RABBIT);
        figures.add(FiguresType.RABBIT);
        figures.add(FiguresType.RABBIT);
        figures.add(FiguresType.ELEPHANT);
        figures.add(FiguresType.CAMEL);
        figures.add(FiguresType.HORSE);
        figures.add(FiguresType.HORSE);
        figures.add(FiguresType.DOG);
        figures.add(FiguresType.DOG);
        figures.add(FiguresType.CAT);
        figures.add(FiguresType.CAT);
    }

    public static ArrayList<FiguresType> getFigures() {
        return BotData.figures;
    }
}
