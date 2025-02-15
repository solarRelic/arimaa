package org.example;

import org.example.gamedata.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameBoardTest {

    @Test
    void cellFigurePlacement_rabbitPlacement_retsRabbit() {
        BlockButtons blockButtonsMocked = Mockito.mock(BlockButtons.class);
    }
//    @Test
//    void cellFigurePlacement_rabbitPlacement_retsRabbit() {
//        Figure expected = new Figure(Team.GOLD, FiguresType.RABBIT);
//        Cell cellMock = Mockito.mock(Cell.class);
////        Mockito.when(cellMock.getFigure()).thenReturn(expected);
////        Figure actual = new Figure(Team.GOLD, FiguresType.RABBIT);
////        assertEquals(actual, cellMock.getFigure());
//
//    }

//    @Test
//    void figureMovement_rabbitMovesUp_movedSuccessfully() {
//        Figure figure = new Figure(Team.GOLD, FiguresType.RABBIT);
//        Figure figure = Mockito.mock(Figure.class);
//        Board board = new Board();
//
//    }


    @ParameterizedTest
    @CsvSource({"0,7", "7,0", "7,7", "0,0"})
    void cellTest_validCell_returnsCell(String r, String c) {
        Board board = new Board();
        int x = Integer.valueOf(r);
        int y = Integer.valueOf(c);

        Cell computed = board.getCell(x, y);

        assertTrue(computed instanceof Cell);
    }

    @ParameterizedTest
    @CsvSource({"-1,0", "0,-1", "8,0", "0,8"})
    void cellTest_invalidCell_returnsNull(String r, String c) {
        Board board = new Board();
        int x = Integer.valueOf(r);
        int y = Integer.valueOf(c);

        Cell computed = board.getCell(x, y);

        assertTrue(computed == null);

    }


}
