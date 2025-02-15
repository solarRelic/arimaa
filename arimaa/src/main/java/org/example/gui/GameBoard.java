package org.example.gui;

import org.example.Arimaa;
import org.example.BotData;
import org.example.StepHistory;
import org.example.gamedata.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class GameBoard extends JPanel {
    private Arimaa arimaa;
    private Board board;
    private Team currentTeam;
    private boolean placeMode;
    private FiguresType currentPlaceFigure;
    private boolean gameStarted;
    private Cell selectedCell;
    private Cell enemySelectedCell;
    private boolean pullMode;
    private boolean pushMode;
    private boolean botMode;
    private boolean historyMode;
    private ArrayList<Step> stepHistory;
    private StringBuilder surrender = new StringBuilder();
    private StringBuilder botInitialSetting = new StringBuilder();
    private StringBuilder playersInitialSetting = new StringBuilder();


    public GameBoard(Arimaa arimaa) {
        this.arimaa = arimaa;
        this.board = new Board();
        this.addMouseListener(new MouseListenerImpl(this));
        this.currentTeam = Team.GOLD;
        Arimaa.logger.log(this, "Chosen player: " + currentTeam.name() + ".");
        this.gameStarted = false;
        this.placeMode = false;
        arimaa.getStatusPanel().setStatus(currentTeam.getTeamPrefix() + " player places the figures.");
        Arimaa.logger.log(this, "'GameBoard' panel has been created.");
    }

    /**
     * Establishes current figure to be placed on the board
     * during the initiation phase.
     * @param figuresType - figure to be placed.
     */
    public void setCurrentFigureToPlace(FiguresType figuresType) {
        this.currentPlaceFigure = figuresType;
        arimaa.getStatusPanel().setStatus("Place " + figuresType.name() + " on the board.");
        Arimaa.logger.log(this, "Set " + figuresType.name() + " is chosen for the placement on the board.");
    }

    /**
     * Establishes the team on the current move.
     * @param currentTeam - set that team.
     */
    public void setCurrentTeam(Team currentTeam) {
        this.currentTeam = currentTeam;
    }

    /**
     * Gets the team on the current move.
     * @return that team.
     */
    public Team getCurrentTeam() {
        return currentTeam;
    }

    /**
     * Places chosen figure on the board during the initiation phase.
     * @param e - mouse point.
     */
    public void placeFigure(Point e) {
        if (currentPlaceFigure == null) return;
        Cell cell = board.getCellAtMouseCoord(e, this.getHeight(), this.getWidth());
        if (cell == null || cell.getFigure() != null) return;
        int boardY = cell.getY() / Board.CELL_HEIGHT;
        if (currentTeam == Team.GOLD) {
            if (boardY < Board.BOARD_HEIGHT - 2) {
                arimaa.getStatusPanel().setStatus("Figures can only be placed on the two bottom-most rows.");
                return;
            }
        } else {
            if (boardY > 1) {
                arimaa.getStatusPanel().setStatus("Figures can only be placed on the two upper-most rows.");
                return;
            }
        }

        Figure figure = new Figure(currentTeam, currentPlaceFigure);
        cell.setFigure(figure);

        String figureName = StepHistory.getFigurePrefix(figure.getFiguresType(), figure.getFiguresTeam());
        String coord = StepHistory.toNormalCoordinate(cell.getX() / Board.CELL_WIDTH, boardY);
        playersInitialSetting.append(figureName).append(coord).append(" ");

        Arimaa.logger.log(this, currentTeam.name() + " " + currentPlaceFigure.name() + " is placed on the board(" + cell.getY() / Board.CELL_WIDTH + ";" +
                cell.getY() / Board.CELL_HEIGHT + ").");
        this.repaint();

        FiguresSetter fs = arimaa.getFiguresSetter(currentTeam);
        int minusOne = -1;
        if (fs.changeFigureCount(currentPlaceFigure, minusOne)) {     // if cur figure's amount <= 0 ? true : false
            fs.blockFigureButton(currentPlaceFigure);
            currentPlaceFigure = null;
            arimaa.getStatusPanel().setStatus(currentTeam.getTeamPrefix() +
                    " player sets the figures.");
        }
        if (fs.complete()) {     // all figures are set
            if (currentTeam == Team.SILVER) {
                Arimaa.logger.log(this, "All figures have been placed on the board from both teams.");
                placeMode = false;
                arimaa.getControlPanel().setUtilButtons(true);

                board.addToStepHistory(playersInitialSetting);
                arimaa.getControlPanel().setStepHistory(board.getStepHistoryText());

                currentTeam = Team.GOLD;
                arimaa.getStatusPanel().setStatus(currentTeam.getTeamPrefix() +
                        " player makes a move.");
                Arimaa.logger.log(this, "Chosen player: " + currentTeam.name() + ".");
                gameStarted = true;
                arimaa.startGame();
                board.setCurrentTeam(currentTeam);
                board.teamsMovesHistory(currentTeam);
                board.getPlayer(currentTeam).setSteps(4);
                arimaa.getClock().startTimer();
                StepHistory.addStep(new Step(board));
                return;
            }
            board.teamsMovesHistory(currentTeam);
            board.addToStepHistory(playersInitialSetting);
            arimaa.getControlPanel().setStepHistory(board.getStepHistoryText());
            playersInitialSetting = new StringBuilder();     // to refresh the sb
            board.setCurrentTeam(currentTeam);
            currentPlaceFigure = null;
            currentTeam = Team.SILVER;
            board.teamsMovesHistory(currentTeam);
            arimaa.getStatusPanel().setStatus(currentTeam.getTeamPrefix() +
                    " player sets the figures.");
            if (botMode) placeBotFigures();
        }
    }

    /**
     * Figure movement in the given direction.
     * @param button - sets the direction.
     */
    public void moveSelectedFigure(DirControlButton button) {
        if (selectedCell == null) return;

        int steps = 1;
        if (pushMode) {
            steps = 2;
            if (!board.canPlayerMove(currentTeam, steps)) {
                arimaa.getStatusPanel().setStatus("Not enough moves to push the figure");
                return;
            }
            Arimaa.logger.log(this, selectedCell.getFigure().getFiguresTeam() + " " + selectedCell.getFigure().getFiguresType() +
                    " pushes " + enemySelectedCell.getFigure().getFiguresType() + " " + enemySelectedCell.getFigure().getFiguresType() + ".");

            Figure enemyFigure = board.moveFigure(enemySelectedCell, button).getFigure();
            selectedCell = board.moveFigure(selectedCell, enemySelectedCell);

            enemyFigure.setSelected(false);

            enemySelectedCell = null;
            pushMode = false;
            arimaa.getStatusPanel().setStatus(currentTeam.getTeamPrefix() +
                    " player pushes " + selectedCell.getFigure().getFiguresTeam().getTeamPrefix() +
                    " " + selectedCell.getFigure().getFiguresType());
        } else if (pullMode) {
            steps = 2;
            if (!board.canPlayerMove(currentTeam, steps)) {
                arimaa.getStatusPanel().setStatus("Not enough moves to pull the figure");
                return;
            }
            Arimaa.logger.log(this, selectedCell.getFigure().getFiguresTeam() + " " + selectedCell.getFigure().getFiguresType() +
                    " pulls " + enemySelectedCell.getFigure().getFiguresTeam() + " " + enemySelectedCell.getFigure().getFiguresTeam() + ".");

            Cell oldCell = selectedCell;
            selectedCell = board.moveFigure(selectedCell, button);
            Figure enemyFigure = board.moveFigure(enemySelectedCell, oldCell).getFigure();
            enemyFigure.setSelected(false);
            enemySelectedCell = null;
            pullMode = false;
            arimaa.getStatusPanel().setStatus(currentTeam.getTeamPrefix() +
                    " player pulls " + selectedCell.getFigure().getFiguresTeam().getTeamPrefix() +
                    " " + selectedCell.getFigure().getFiguresType());
        } else {
            selectedCell = board.moveFigure(selectedCell, button);
        }

        if (board.reduceStepCount(currentTeam, steps)) {    // <= 0 ? true : false;
            Arimaa.logger.log(this, currentTeam.getTeamPrefix() + " player has no moves left.");
            if (selectedCell != null) selectedCell.getFigure().setSelected(false);
            selectedCell = null;
            arimaa.getControlPanel().blockAllDirButtons();
            arimaa.getStatusPanel().setStatus(currentTeam.getTeamPrefix() + " player's turn.");
        } else {
            arimaa.getControlPanel().blockButton(board.getAvailableDirButtons(selectedCell, false));
        }

        board.clearTrap();
        if (selectedCell != null && selectedCell.getFigure() == null) {
            arimaa.getStatusPanel().setStatus(currentTeam.getTeamPrefix() + " player's turn");
            selectedCell = null;
        }

        String winMessage = board.getWinner(currentTeam);
        if (winMessage.length() != 0) {
            gameStarted = false;
            arimaa.getControlPanel().setEnabledSaveButton(false);
            arimaa.getStatusPanel().setStatus(winMessage);
            arimaa.getControlPanel().blockAllDirButtons();
            arimaa.getControlPanel().setUtilButtons(false);
            arimaa.getClock().stopTimer();
        }
        updatePlayersInfo();
        arimaa.getControlPanel().setStepHistory(board.getStepHistoryText());
        if (historyMode) {
            StepHistory.reduceStepHistory();
            historyMode = false;
        }
        // so as not to save bot's movements (in order to undo only user's movements)
        if (!(botMode && currentTeam == Team.SILVER)) StepHistory.addStep(new Step(board));
        this.repaint();
    }

    /**
     * Updates information about a player on the screen
     * (steps left and time taken).
     */
    public void updatePlayersInfo() {
        if (placeMode) return;
        Arimaa.logger.log(this, "Updating players' information");
        for (Team team : Team.values())
            arimaa.getPlayerInfoPanel(team).updatePlayerInfo(board.getPlayer(team));
    }

    /**
     * Goes back to menu.
     */
    public void exitGame() {
        Arimaa.logger.log(this, "Exiting to menu.");
        if (gameStarted) {
            arimaa.getClock().stopTimer();
            if (board.getPlayer(currentTeam) != null) {
                board.getPlayer(currentTeam).addSeconds(arimaa.getClock().getTimerValue());
            }
        }
        arimaa.showMenuPage();
    }

    /**
     * Ends player's turn.
     */
    public void endTurn() {
        if (!gameStarted) return;

        // if rabbit is on the first row after ending the turn
        StringBuilder winMsg = new StringBuilder();
        Figure figure;
        for (int x = 0; x < Board.BOARD_WIDTH; x++) {
            figure = board.getCell(x, 0).getFigure();
            if (figure != null && figure.getFiguresType() == FiguresType.RABBIT &&
                    figure.getFiguresTeam() == Team.GOLD) {
                winMsg.append(Team.GOLD.getTeamPrefix()).append(" player has won!");
            }
        }
        for (int x = 0; x < Board.BOARD_WIDTH; x++) {
            figure = board.getCell(x, 7).getFigure();
            if (figure != null && figure.getFiguresType() == FiguresType.RABBIT &&
                    figure.getFiguresTeam() == Team.SILVER) {
                winMsg.append(Team.SILVER.getTeamPrefix()).append(" player has won!");
            }
        }
        if (winMsg.length() != 0) {
            gameStarted = false;
            arimaa.getControlPanel().setEnabledSaveButton(false);
            arimaa.getStatusPanel().setStatus(winMsg.toString());
            arimaa.getControlPanel().blockAllDirButtons();
            arimaa.getClock().stopTimer();
        }

        if (board.getPlayer(currentTeam).getSteps() == 4) return;
        Arimaa.logger.log(this, currentTeam.getTeamPrefix() + " player ends their turn.");
        arimaa.getClock().stopTimer();
        board.getPlayer(currentTeam).addSeconds(arimaa.getClock().getTimerValue());
        board.getPlayer(currentTeam).setSteps(0);
        currentTeam = (currentTeam == Team.GOLD) ? Team.SILVER : Team.GOLD;
        pullMode = false;
        pushMode = false;
        if (selectedCell != null) selectedCell.getFigure().setSelected(false);
        if (enemySelectedCell != null) enemySelectedCell.getFigure().setSelected(false);
        selectedCell = null;
        enemySelectedCell = null;
        arimaa.getControlPanel().blockAllDirButtons();
        arimaa.getStatusPanel().setStatus(currentTeam.getTeamPrefix() + " player's turn");
        board.getPlayer(currentTeam).setSteps(4);
        arimaa.getClock().startTimer();
        updatePlayersInfo();
        board.setCurrentTeam(currentTeam);
        board.teamsMovesHistory(currentTeam);
        arimaa.getControlPanel().setStepHistory(board.getStepHistoryText());
        this.repaint();
        if (botMode && currentTeam == Team.SILVER) moveFigureByBot();
    }

    /**
     * Resigning.
     */
    public void surrender() {
        gameStarted = false;
        String prevHistory = board.getStepHistoryText();
        if (prevHistory.length() == 0) prevHistory = "1g";      //if gold resigns on stage of setting the figures
        surrender.append(prevHistory).append(" ").append("resigns");
        arimaa.getControlPanel().setStepHistory(surrender.toString());

        arimaa.getControlPanel().setUtilButtons(false);
        String teamWinPrefix = (currentTeam == Team.GOLD) ? Team.SILVER.getTeamPrefix() : Team.GOLD.getTeamPrefix();
        arimaa.getStatusPanel().setStatus(teamWinPrefix + " player has won!");
        arimaa.getControlPanel().blockAllDirButtons();
        arimaa.getClock().stopTimer();
        updatePlayersInfo();
        Arimaa.logger.log(this, currentTeam.getTeamPrefix() + " has resigend.");
    }

    /**
     * Undoes last actions.
     */
    public void undo() {
        Step prevStep = StepHistory.getPrevStep();
        if (prevStep == null) {
            return;
        }
        historyMode = true;
        board = prevStep.getBoard();

        currentTeam = board.getCurrentTeam();
        if (arimaa.getControlPanel().allDirButtonsBlocked()) {
            arimaa.getControlPanel().unblockAllDirButtons();
        }
        arimaa.getStatusPanel().setStatus(currentTeam.getTeamPrefix() + " player's turn.");

        this.repaint();
        updatePlayersInfo();
        arimaa.getControlPanel().setStepHistory(board.getStepHistoryText());
        selectedCell = null;
        enemySelectedCell = null;
    }

    /**
     * Rollback the last undone actions.
     */
    public void redo() {
        Step nextStep = StepHistory.getNextStep();
        if (nextStep == null) {
            historyMode = false;
            return;
        }
        historyMode = true;
        board = nextStep.getBoard();
        currentTeam = board.getCurrentTeam();
        arimaa.getStatusPanel().setStatus(currentTeam.getTeamPrefix() + " player's turn.");

        this.repaint();
        updatePlayersInfo();
        arimaa.getControlPanel().setStepHistory(board.getStepHistoryText());
        selectedCell = null;
        enemySelectedCell = null;
    }

    /**
     * Bot's figure movement.
     */
    public void moveFigureByBot() {
        Arimaa.logger.log(this, "Bot's turn");
        ArrayList<Cell> availableCells = new ArrayList<>();
        BlockButtons bb;
        for (int y = 0; y < Board.BOARD_HEIGHT; y++) {
            for (int x = 0; x < Board.BOARD_WIDTH; x++) {
                Cell cell = board.getCell(x, y);
                if (cell.getFigure() == null) continue;
                if (cell.getFigure().getFiguresTeam() != currentTeam) continue;
                bb = board.getAvailableDirButtons(cell, false);
                if (bb.allButtonsAreBlocked()) continue;
                availableCells.add(cell);
            }
        }
        if (availableCells.size() == 0) {
            gameStarted = false;
            arimaa.getControlPanel().setEnabledSaveButton(false);
            arimaa.getStatusPanel().setStatus(Team.GOLD.getTeamPrefix() + " player has won!");
            arimaa.getControlPanel().blockAllDirButtons();
            arimaa.getClock().stopTimer();
            return;
        }

        int cellIndex = (int) (Math.random() * availableCells.size());
        selectedCell = availableCells.get(cellIndex);
        Arimaa.logger.log(this, "Bot's chosen figure " + selectedCell.getFigure().getFiguresType() + "(" + selectedCell.getX() / Board.CELL_WIDTH +
                ";" + selectedCell.getY() / Board.CELL_HEIGHT + ").");

        while (board.getPlayer(currentTeam).getSteps() > 0) {
            if (selectedCell == null) break;
            bb = board.getAvailableDirButtons(selectedCell, false);

            Cell enemyFigure = board.getCellWithWeakerEnemyFigureBeside(selectedCell);
            if (enemyFigure != null && board.canPlayerMove(currentTeam, 2)) {
                enemySelectedCell = enemyFigure;
                int chance = (int) (Math.random() * 100);
                if (chance > 50) {
                    pullMode = true;
                    Arimaa.logger.log(this, "Bot pulls figure " + enemySelectedCell.getFigure().getFiguresTeam() + " " + enemySelectedCell.getFigure().getFiguresType() +
                            "(" + enemySelectedCell.getX() / Board.CELL_WIDTH + ";" + enemySelectedCell.getY() / Board.CELL_HEIGHT + ").");
                } else {
                    pushMode = true;
                    Arimaa.logger.log(this, "Bot pushes figure " + enemySelectedCell.getFigure().getFiguresTeam() + " " + enemySelectedCell.getFigure().getFiguresType() +
                            "(" + enemySelectedCell.getX() / Board.CELL_WIDTH + ";" + enemySelectedCell.getY() / Board.CELL_HEIGHT + ").");
                    bb = board.getAvailableDirButtons(enemySelectedCell, true);
                }
            }

            if (bb.allButtonsAreBlocked()) break;
            ArrayList<DirControlButton> dirControlButtons = new ArrayList<>(Arrays.asList(DirControlButton.values()));
            DirControlButton stepButton;
            while (true) {
                int step = (int) (Math.random() * dirControlButtons.size());
                stepButton = dirControlButtons.get(step);
                if (bb.getState(stepButton)) break;
            }
            moveSelectedFigure(stepButton);
        }
        if (currentTeam == Team.SILVER)
            endTurn();
    }

    /**
     * Turn on the initiation phase of the game
     * (setting the figures on the two rows).
     * @param state true to turn on, false to turn off.
     */
    public void setPlaceMode(boolean state) {
        this.placeMode = state;
        Arimaa.logger.log(this, "Placement of the figures mode: " + state + ".");
    }

    /**
     * Set the state of the game.
     * @param started true if the game is on, false otherwise.
     */
    public void setGameStarted(boolean started) {
        arimaa.getControlPanel().setEnabledSaveButton(started);
        this.gameStarted = started;
        Arimaa.logger.log(this, "Game started: " + started + ".");
    }

    /**
     * Set mode against a bot.
     * @param botMode true if a game against a bot, false otherwise.
     */
    public void setBotMode(boolean botMode) {
        this.botMode = botMode;
        Arimaa.logger.log(this, "Against a bot: " + botMode + ".");
    }

    /**
     * Load saved game.
     */
    public void loadGame() {
        try {
            StepHistory.loadStepHistory();
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("game.dat"));
            board = (Board) ois.readObject();
            Arimaa.logger.log(this, "Game is successfully loaded.");
        } catch (IOException | ClassNotFoundException e) {
            Arimaa.logger.log(this, "Error when loading a game from file.");
            throw new RuntimeException(e);
        }
        currentTeam = board.getCurrentTeam();
        Arimaa.logger.log(this, "currentTeam = " + currentTeam + ".");
        arimaa.getStatusPanel().setStatus(currentTeam.getTeamPrefix() + " player's turn");
        setBotMode(board.isBotMode());
        updatePlayersInfo();
        arimaa.getClock().startTimer();
        arimaa.getControlPanel().setStepHistory(board.getStepHistoryText());
        selectedCell = null;
        enemySelectedCell = null;
    }

    /**
     * Selecting the figure for the movement on the current move during the game.
     * @param e - mouse point.
     */
    public void selectFigure(Point e) {
        if (botMode && historyMode && currentTeam == Team.SILVER) return;
        if (board.getPlayer(currentTeam).getSteps() == 0) return;
        if (botMode && currentTeam == Team.SILVER) return;

        Cell newCell = board.getCellAtMouseCoord(e, this.getHeight(), this.getWidth());
        if ((pullMode && newCell == selectedCell || (pushMode && newCell == enemySelectedCell))) {
            enemySelectedCell.getFigure().setSelected(false);
            enemySelectedCell = null;
            arimaa.getStatusPanel().setStatus(currentTeam.getTeamPrefix() + " player moves " + selectedCell.getFigure().getFiguresTeam().getTeamPrefix() +
                    " " + selectedCell.getFigure().getFiguresType());
            if (pullMode) Arimaa.logger.log(this, "PULL mode is turned off.");
            else if (pushMode) Arimaa.logger.log(this, "PUSH mode is turned off.");
            pullMode = false;
            pushMode = false;
            this.repaint();
            return;
        }
        if (pullMode) Arimaa.logger.log(this, "PULL mode is turned off");
        else if (pushMode) Arimaa.logger.log(this, "PUSH mode is turned off");
        pullMode = false;
        pushMode = false;

        if (enemySelectedCell != null && newCell != selectedCell) {
            enemySelectedCell.getFigure().setSelected(false);
            enemySelectedCell = null;
        }

        if (newCell == null) return;

        Figure figureInNewCell = newCell.getFigure();
        if (figureInNewCell == null) return;
        if (figureInNewCell.getFiguresTeam() != currentTeam) {
            if (selectedCell == null) return;
            if (!board.canMoveFigureByFigure(selectedCell.getFigure(), figureInNewCell)) return;
            int dX = (Math.abs(newCell.getX() - selectedCell.getX()) / Board.CELL_WIDTH);
            int dY = (Math.abs(newCell.getY() - selectedCell.getY()) / Board.CELL_HEIGHT);
            if (dX > 1 || dY > 1) return;
            if ((dX == 1) == (dY == 1)) return;   //on diagonals
            enemySelectedCell = newCell;
            enemySelectedCell.getFigure().setSelected(true);
            this.repaint();
            arimaa.getStatusPanel().setStatus(currentTeam.getTeamPrefix() + " player moves " +
                    enemySelectedCell.getFigure().getFiguresTeam().getTeamPrefix() + " " +
                    enemySelectedCell.getFigure().getFiguresType() + "(PUSH)");
            pushMode = true;
            arimaa.getControlPanel().blockButton(board.getAvailableDirButtons(enemySelectedCell, true));
            Arimaa.logger.log(this, "PUSH mode is turned on");
            Arimaa.logger.log(this, enemySelectedCell.getFigure().getFiguresTeam() +
                    " " + enemySelectedCell.getFigure().getFiguresType() + " is chosen (" + enemySelectedCell.getX() / Board.CELL_WIDTH +
                    ";" + enemySelectedCell.getY() / Board.CELL_HEIGHT + ")" + ".");

            board.CheckFrozen(newCell);
            if (figureInNewCell.isFrozen()) {
                arimaa.getControlPanel().blockAllDirButtons();
                Arimaa.logger.log(this, "Full movement blockage due to the freezing");
            }
            return;
        }
        if (selectedCell != null && selectedCell.getFigure() != null) selectedCell.getFigure().setSelected(false);
        selectedCell = newCell;
        figureInNewCell.setSelected(true);
        if (enemySelectedCell != null) pullMode = true;
        arimaa.getStatusPanel().setStatus(currentTeam.getTeamPrefix() + " player moves " +
                selectedCell.getFigure().getFiguresTeam().getTeamPrefix() + " " +
                selectedCell.getFigure().getFiguresType() + (pullMode ? "(PULL)" : ""));
        if (pullMode) Arimaa.logger.log(this, "PULL mode is turned on.");
        Arimaa.logger.log(this, selectedCell.getFigure().getFiguresTeam() + " " +
                selectedCell.getFigure().getFiguresType() + " is chosen (" + selectedCell.getX() / Board.CELL_WIDTH +
                ";" + selectedCell.getY() / Board.CELL_HEIGHT + ")" + ".");
        this.repaint();
        arimaa.getControlPanel().blockButton(board.getAvailableDirButtons(selectedCell, pullMode));

        board.CheckFrozen(newCell);
        if (figureInNewCell.isFrozen()) {
            arimaa.getControlPanel().blockAllDirButtons();
            Arimaa.logger.log(this, "Full movement blockage due to the freezing");
        }

        String winMessage = board.getWinner(currentTeam);
        if (winMessage.length() != 0) {
            gameStarted = false;
            arimaa.getControlPanel().setEnabledSaveButton(false);
            arimaa.getStatusPanel().setStatus(winMessage);
            arimaa.getControlPanel().blockAllDirButtons();
            arimaa.getClock().stopTimer();
        }
    }

    /**
     * Saves the game.
     */
    public void saveGame() {
        if (!gameStarted) return;
        try {
            board.setBotMode(botMode);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("game.dat"));
            oos.writeObject(board);
            oos.flush();
            if (historyMode) StepHistory.reduceStepHistory();
            StepHistory.saveStepHistory();
            Arimaa.logger.log(this, "Game is saved.");
        } catch (IOException e) {
            Arimaa.logger.log(this, "Error when saving the game");
            throw new RuntimeException(e);
        }
    }

    /**
     * Bot figure placement in the initiation phase.
     */
    public void placeBotFigures() {
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < Board.BOARD_WIDTH; x++) {
                Figure figure = new Figure(currentTeam, BotData.getFigures().get(y * Board.BOARD_WIDTH + x));
                board.setFigureAtCell(x, y, figure);

                String figureName = StepHistory.getFigurePrefix(figure.getFiguresType(), figure.getFiguresTeam());
                String coord = StepHistory.toNormalCoordinate(x, y);
                botInitialSetting.append(figureName).append(coord).append(" ");
            }
        }

        board.addToStepHistory(botInitialSetting);
        arimaa.getControlPanel().setStepHistory(board.getStepHistoryText());

        currentTeam = Team.GOLD;
        arimaa.getStatusPanel().setStatus(currentTeam.getTeamPrefix() + " player makes their move.");
        placeMode = false;
        gameStarted = true;
        arimaa.getControlPanel().setUtilButtons(true);
        arimaa.startGame();
        board.setCurrentTeam(currentTeam);
        board.teamsMovesHistory(currentTeam);
        board.getPlayer(currentTeam).setSteps(4);
        arimaa.getClock().startTimer();
        updatePlayersInfo();
        StepHistory.addStep(new Step(board));
    }

    /**
     * Drawing a game board and repaint.
     */
    public void paint(Graphics g) {
        super.paint(g);
        board.drawBoard((Graphics2D) g, this.getHeight(), this.getWidth());
    }

    class MouseListenerImpl implements MouseListener {

        private GameBoard b;    // does the listening on a board

        MouseListenerImpl(GameBoard b) {
            this.b = b;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (placeMode) {        // only true when in initial phase
                b.placeFigure(e.getPoint());
                return;
            }
            if (gameStarted)
                selectFigure(e.getPoint());
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }
}
