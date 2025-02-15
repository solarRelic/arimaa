package org.example;

import org.example.gamedata.FiguresType;
import org.example.gamedata.Team;

import static org.junit.jupiter.api.Assertions.*;

class StepHistoryTest {

    @org.junit.jupiter.api.Test
    void getFigurePrefixTest_GoldElephant() {
        String prefixTest = StepHistory.getFigurePrefix(FiguresType.ELEPHANT, Team.GOLD);
        String prefixExpected = "E";
        assertEquals(prefixExpected, prefixTest);
    }
    @org.junit.jupiter.api.Test
    void getFigurePrefixTest_SilverCat() {
        String prefixTest = StepHistory.getFigurePrefix(FiguresType.CAT, Team.SILVER);
        String prefixExpected = "c  ";
        assertEquals(prefixExpected, prefixTest);
    }

    @org.junit.jupiter.api.Test
    void toNormalCoordinateTest_0x0() {
        String coordTest = StepHistory.toNormalCoordinate(0, 0);
        String coordExpected = "a8";
        assertEquals(coordExpected, coordTest);
    }

    @org.junit.jupiter.api.Test
    void toNormalCoordinateTest7x7() {
        String coordTest = StepHistory.toNormalCoordinate(7, 7);
        String coordExpected = "h1";
        assertEquals(coordExpected, coordTest);
    }
}