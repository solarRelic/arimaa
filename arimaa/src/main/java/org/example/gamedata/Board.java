package org.example.gamedata;

import org.example.Arimaa;
import org.example.StepHistory;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Board implements Serializable {
    public static final int BOARD_WIDTH = 8;
    public static final int BOARD_HEIGHT = 8;
    public static final int CELL_WIDTH = 100;
    public static final int CELL_HEIGHT = 100;
    private final Cell[][] board;
    private final ImageIcon imageBoard;
    private final String imageBoardPath = "img/Board.png";
    private static final ArrayList<Point> trapCoordinates = new ArrayList<>();
    private Team currentTeam;
    private HashMap<Team, Player> playerHashMap;
    private boolean botMode;
    private StringBuilder stepHistory = new StringBuilder();
    private int moves;

    static {
        trapCoordinates.add(new Point(2, 2));
        trapCoordinates.add(new Point(5, 2));
        trapCoordinates.add(new Point(5, 5));
        trapCoordinates.add(new Point(2, 5));
        Arimaa.logger.log(Board.class, "Traps' coordinates have been initialised.");
    }

    /**
     * Creation of the board of 8x8 cells.
     * One cell is 100x100.
     */
    public Board() {
        moves = 0;
        imageBoard = new ImageIcon(imageBoardPath);
        board = new Cell[BOARD_WIDTH][BOARD_HEIGHT];
        playerHashMap = new HashMap<>();

        playerHashMap.put(Team.GOLD, new Player());
        playerHashMap.put(Team.SILVER, new Player());

        for (int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_HEIGHT; y++) {
                board[x][y] = new Cell(x * CELL_HEIGHT, y * CELL_WIDTH);
            }
        }
        Arimaa.logger.log(this, "Empty board has been created.");
    }

    /**
     * To get board's cell.
     * @param x board's coordinate.
     * @param y board's coordinate.
     * @return board's cell at (x,y).
     */
    public Cell getCell(int x, int y) {
        if (x >= BOARD_WIDTH  || x < 0 ||
            y >= BOARD_HEIGHT || y < 0) return null;
        else return board[x][y];
    }

    /**
     * To place a figure on the board's cell.
     * @param x cell's coordinate on the board.
     * @param y cell's coordinate on the board.
     * @param figure to be placed in the cell.
     */
    public void setFigureAtCell(int x, int y, Figure figure) {
        board[x][y].setFigure(figure);

        Arimaa.logger.log(this, "Figure " + figure.getFiguresTeam() + " "
                + figure.getFiguresType() + " has been placed " + "(" + x + ";" + y + ")" + ".");
    }

    /**
     * Figure movement to the adjacent cell (used when pushing).
     * @param oldCell starting cell.
     * @param newCell adjacent destination cell.
     * @return destination cell.
     */
    public Cell moveFigure(Cell oldCell, Cell newCell) {
        Figure figure = oldCell.getFigure();
        oldCell.clearFigure();
        newCell.setFigure(figure);

        Arimaa.logger.log(this,newCell.getFigure().getFiguresTeam() + " " + newCell.getFigure().getFiguresType() + " moved. " +
                "(" + oldCell.getX()/CELL_WIDTH + ";" + oldCell.getY()/CELL_HEIGHT + ") -> (" + newCell.getX()/CELL_WIDTH + ";" + newCell.getY()/CELL_HEIGHT + ")");

        String step = StepHistory.getFigurePrefix(newCell.getFigure().getFiguresType(), newCell.getFigure().getFiguresTeam()) +
                StepHistory.toNormalCoordinate(newCell.getX() / CELL_WIDTH, newCell.getY() / CELL_HEIGHT) + "P";
        if (stepHistory == null) stepHistory = new StringBuilder();
        stepHistory.append(step).append(" ");
        return newCell;
    }

    /**
     * Figure movement to the adjacent cell in one of the four directions.
     * @param currentCell starting cell.
     * @param direction west, east, north or south.
     * @return destination cell.
     */
    public Cell moveFigure(Cell currentCell, DirControlButton direction) {
        int boardX = currentCell.getX() / CELL_WIDTH;
        int boardY = currentCell.getY() / CELL_HEIGHT;
        Cell newCell = board[boardX + direction.getX()][boardY + direction.getY()];
        movementHistory(currentCell, direction);
        newCell.setFigure(currentCell.getFigure());
        currentCell.clearFigure();
        Arimaa.logger.log(this, newCell.getFigure().getFiguresTeam() + " " + newCell.getFigure().getFiguresType() + " moved. " +
                "(" + currentCell.getX()/CELL_WIDTH + ";" + currentCell.getY()/CELL_HEIGHT + ") -> (" + newCell.getX()/CELL_WIDTH + ";" + newCell.getY()/CELL_HEIGHT + ")");
        return newCell;
    }
    /**
     * To convert step history according to the official Arimaa notation.
     */
    public void movementHistory(Cell cell, DirControlButton direction) {
        FiguresType figuresType = cell.getFigure().getFiguresType();
        Team figuresTeam = cell.getFigure().getFiguresTeam();
        char stepDirection;
        switch (direction) {
            case UP -> stepDirection = 'n';
            case DOWN -> stepDirection = 's';
            case RIGHT -> stepDirection = 'e';
            case LEFT -> stepDirection = 'w';
            default -> stepDirection = ' ';
        }

        String stepFigureName = StepHistory.getFigurePrefix(figuresType, figuresTeam);
        String stepFromCoordinate = StepHistory.toNormalCoordinate(cell.getX() / CELL_WIDTH, cell.getY() / CELL_HEIGHT);
        char stepDir = stepDirection;

        if (stepHistory == null) stepHistory = new StringBuilder();
        stepHistory.append(stepFigureName).append(stepFromCoordinate).append(stepDirection).append(" ");
    }

    /**
     * Get a cell based on mouse's point.
     * @param point - mouse's point position.
     * @param height = JFRAME_height - statusPanel(BorderLayout.NORTH)_height - controlPanel(BorderLayout.SOUTH)_height
     * @param width = JFRAME_width - leftSideGui(BorderLayout.WEST)_width - rightSideGui(BorderLayout.EAST)_width
     * @return board's cell if mouse's point is in bounds of the board, null otherwise
     */
    public Cell getCellAtMouseCoord(Point point, int height, int width) {
        int startX = (width - imageBoard.getIconWidth()) / 2;       //margin between leftSideGui and the beginning of the board image
        int startY = (height - imageBoard.getIconHeight()) / 2;     //margin between upperSideGui and the beginning of the board image (it's 0)
        int x = (point.x - startX) / CELL_WIDTH;
        int y = (point.y - startY) / CELL_HEIGHT;
        try {
            return board[x][y];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Deletes the figure from the board if it's in the trap
     * and there are no allied figures nearby.
     */
    public void clearTrap() {
        for (Point point : trapCoordinates) {
            Figure figure = board[point.x][point.y].getFigure();
            if (figure != null) {
                Figure besideFigureEAST = board[point.x + 1][point.y].getFigure();
                Figure besideFigureWEST = board[point.x - 1][point.y].getFigure();
                Figure besideFigureSOUTH = board[point.x][point.y + 1].getFigure();
                Figure besideFigureNORTH = board[point.x][point.y - 1].getFigure();

                boolean f = false;
                if ((besideFigureEAST != null))
                    f = f | besideFigureEAST.getFiguresTeam() == figure.getFiguresTeam();
                if ((besideFigureWEST != null))
                    f = f | besideFigureWEST.getFiguresTeam() == figure.getFiguresTeam();
                if ((besideFigureSOUTH != null))
                    f = f | besideFigureSOUTH.getFiguresTeam() == figure.getFiguresTeam();
                if ((besideFigureNORTH != null))
                    f = f | besideFigureNORTH.getFiguresTeam() == figure.getFiguresTeam();

                if (!f) {
                    if (stepHistory == null) stepHistory = new StringBuilder();
                    String stepFigureName = StepHistory.getFigurePrefix(figure.getFiguresType(), figure.getFiguresTeam());
                    String trapCoord = StepHistory.toNormalCoordinate(point.x, point.y);
                    stepHistory.append(stepFigureName).append(trapCoord).append("x").append(" ");

                    Arimaa.logger.log(this, board[point.x][point.y].getFigure().getFiguresTeam() + " " +
                            board[point.x][point.y].getFigure().getFiguresType() + " deleted from the trap(" + point.x + ";" + point.y + ").");

                    board[point.x][point.y].clearFigure();
                }
            }
        }
    }

    /**
     * Check if there's a winner.
     * @param currentTeam - team on the current turn.
     * @return winner's team as a string if there is a winner, empty string "" otherwise.
     */
    public String getWinner(Team currentTeam) {
        //if rabbit reaches the end of the board
        Figure figure;
        for (int x = 0; x < BOARD_WIDTH; x++) {
            figure = board[x][0].getFigure();
            if (figure != null && figure.getFiguresType() == FiguresType.RABBIT &&
                    figure.getFiguresTeam() == Team.GOLD && getPlayer(Team.GOLD).getSteps() == 0) {
                return Team.GOLD.getTeamPrefix() + " player has won!";
            }
        }
        for (int x = 0; x < BOARD_WIDTH; x++) {
            figure = board[x][7].getFigure();
            if (figure != null && figure.getFiguresType() == FiguresType.RABBIT &&
                    figure.getFiguresTeam() == Team.SILVER && getPlayer(Team.SILVER).getSteps() == 0) {
                return Team.SILVER.getTeamPrefix() + " player has won!";
            }
        }

        // if player has no rabbits left
        if (!playerHasRabbit(currentTeam)) {
            Arimaa.logger.log(this, currentTeam.getTeamPrefix() + " lost all rabits.");
            Team winTeam = (currentTeam == Team.GOLD) ? Team.SILVER : Team.GOLD;
            return winTeam.getTeamPrefix() + " player has won!";
        }

        // if player has no available moves to make
        if (!playerCanMove(currentTeam)) {
            Arimaa.logger.log(this, currentTeam.getTeamPrefix() + " no more moves available.");
            Team winTeam = (currentTeam == Team.GOLD) ? Team.SILVER : Team.GOLD;
            return winTeam.getTeamPrefix() + " player has won!";
        }
        return "";
    }

    /**
     * Check for the available moves left.
     * @param team - team to be checked.
     * @return true if a move is possible, false otherwise
     */
    public boolean playerCanMove(Team team) {
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                Figure playerFigure = board[x][y].getFigure();
                if (playerFigure == null || playerFigure.getFiguresTeam() != team) continue;
                if (!getAvailableDirButtons(board[x][y], false).allButtonsAreBlocked())
                    return true;
            }
        }
        return false;
    }

    /**
     * Check for the amount rabbits.
     * @param team - team to be checked.
     * @return true if there is at least one rabbit, false otherwise
     */
    public boolean playerHasRabbit(Team team) {
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                Figure playerFigure;
                playerFigure = board[x][y].getFigure();
                if (playerFigure == null || playerFigure.getFiguresTeam() != team) continue;
                if (playerFigure.getFiguresType() == FiguresType.RABBIT) return true;
            }
        }
        return false;
    }

    /**
     * Get a player.
     * @param team - player of this team.
     * @return player of Team.GOLD or Team.SILVER
     */
    public Player getPlayer(Team team) {
        return playerHashMap.get(team);
    }

    /**
     * Reduces the amount of player's steps.
     * @param team - player of this team.
     * @param value - will be reduced by this amount.
     * @return true if the amount of steps is <= 0, false otherwise
     */
    public boolean reduceStepCount(Team team, int value) {
        return playerHashMap.get(team).reduceSteps(value);
    }

    /**
     * Checks for the possibility of a player of the given team to make a move.
     * @param team - player of this team.
     * @param stepsNeeded - needed amount of steps for the move.
     * @return true if possible, false otherwise
     */
    public boolean canPlayerMove(Team team, int stepsNeeded) {
        return playerHashMap.get(team).getSteps() >= stepsNeeded;
    }

    /**
     * To get allowed direction button(s) for movement and block all dirs in case of being frozen.
     * @param cell - from this cell the evaluation will be made.
     * @param mode is set to true during PUSH mode when we need to evaluate available directions
     *             from the cell which contains the figure that we want to push
     *             (effectively disabling freezing evaluation for the pushed figure),
     *             false in all other cases.
     * @return available direction buttons.
     */
    public BlockButtons getAvailableDirButtons(Cell cell, boolean mode) {
        BlockButtons blockButtons = new BlockButtons();
        Figure selectedFigure = cell.getFigure();
        boolean hasFriendFigure = false;
        boolean hasEnemyStrongerFigure = false;

        int boardX = cell.getX() / CELL_WIDTH;
        int boardY = cell.getY() / CELL_HEIGHT;

        if (boardY + 1 >= BOARD_HEIGHT ) blockButtons.blockButton(DirControlButton.DOWN);
        else if (board[boardX][boardY + 1].getFigure() != null) {
            blockButtons.blockButton(DirControlButton.DOWN);
            if (board[boardX][boardY + 1].getFigure().getFiguresTeam() == selectedFigure.getFiguresTeam())
                hasFriendFigure = true;
            if (!mode) hasEnemyStrongerFigure = canMoveFigureByFigure(board[boardX][boardY + 1].getFigure(), selectedFigure);
        }

        if (boardY - 1 < 0 ) blockButtons.blockButton(DirControlButton.UP);
        else if (board[boardX][boardY - 1].getFigure() != null) {
            blockButtons.blockButton(DirControlButton.UP);
            if (board[boardX][boardY - 1].getFigure().getFiguresTeam() == selectedFigure.getFiguresTeam())
                hasFriendFigure = true;
            if (!mode) hasEnemyStrongerFigure = canMoveFigureByFigure(board[boardX][boardY - 1].getFigure(), selectedFigure);
        }

        if (boardX + 1 >= BOARD_WIDTH ) blockButtons.blockButton(DirControlButton.RIGHT);
        else if (board[boardX + 1][boardY].getFigure() != null) {
            blockButtons.blockButton(DirControlButton.RIGHT);
            if (board[boardX + 1][boardY].getFigure().getFiguresTeam() == selectedFigure.getFiguresTeam())
                hasFriendFigure = true;
            if (!mode) hasEnemyStrongerFigure = canMoveFigureByFigure(board[boardX + 1][boardY].getFigure(), selectedFigure);
        }

        if (boardX - 1 < 0 ) blockButtons.blockButton(DirControlButton.LEFT);
        else if (board[boardX - 1][boardY].getFigure() != null) {
            blockButtons.blockButton(DirControlButton.LEFT);
            if (board[boardX - 1][boardY].getFigure().getFiguresTeam() == selectedFigure.getFiguresTeam())
                hasFriendFigure = true;
            if (!mode) hasEnemyStrongerFigure = canMoveFigureByFigure(board[boardX -1][boardY].getFigure(), selectedFigure);

        }

        if (!mode && selectedFigure.getFiguresType() == FiguresType.RABBIT) {
            switch (selectedFigure.getFiguresTeam()) {
                case GOLD -> blockButtons.blockButton(DirControlButton.DOWN);
                case SILVER -> blockButtons.blockButton(DirControlButton.UP);
            }
        }
        if (!hasFriendFigure && hasEnemyStrongerFigure){
            blockButtons.blockAllDirButtons();
        }
        return blockButtons;
    }

    public void CheckFrozen(Cell cell) {
        Figure selectedFigure = cell.getFigure();
        selectedFigure.setFrozen(false);

        boolean hasFriendFigure = false;
        boolean hasEnemyStrongerFigure = false;

        int boardX = cell.getX() / CELL_WIDTH;
        int boardY = cell.getY() / CELL_HEIGHT;

        if (boardY + 1 < BOARD_HEIGHT && board[boardX][boardY + 1].getFigure() != null) {
            if (board[boardX][boardY + 1].getFigure().getFiguresTeam() == selectedFigure.getFiguresTeam())
                hasFriendFigure = true;
            if (!hasFriendFigure && canMoveFigureByFigure(board[boardX][boardY + 1].getFigure(), selectedFigure))
                selectedFigure.setFrozen(true);
        }

        if (boardY - 1 >= 0 && board[boardX][boardY - 1].getFigure() != null) {
            if (board[boardX][boardY - 1].getFigure().getFiguresTeam() == selectedFigure.getFiguresTeam())
                hasFriendFigure = true;
            if (!hasFriendFigure && canMoveFigureByFigure(board[boardX][boardY - 1].getFigure(), selectedFigure))
                selectedFigure.setFrozen(true);
        }

        if (boardX + 1 < BOARD_WIDTH && board[boardX + 1][boardY].getFigure() != null) {
            if (board[boardX + 1][boardY].getFigure().getFiguresTeam() == selectedFigure.getFiguresTeam())
                hasFriendFigure = true;
            if (!hasFriendFigure && canMoveFigureByFigure(board[boardX + 1][boardY].getFigure(), selectedFigure))
                selectedFigure.setFrozen(true);
        }

        if (boardX - 1 >= 0 && board[boardX - 1][boardY].getFigure() != null) {
            if (board[boardX - 1][boardY].getFigure().getFiguresTeam() == selectedFigure.getFiguresTeam())
                hasFriendFigure = true;
            if (!hasFriendFigure && canMoveFigureByFigure(board[boardX - 1][boardY].getFigure(), selectedFigure))
                selectedFigure.setFrozen(true);
        }
    }

    /**
     * Look for an adjacent cell with a weaker enemy figure in it.
     * @param cell - from this cell the evaluation will be made.
     * @return cell with an enemy figure in it, if there is one, null otherwise.
     */
    public Cell getCellWithWeakerEnemyFigureBeside(Cell cell) {
        Figure figure;
        int boardX = cell.getX() / CELL_WIDTH;
        int boardY = cell.getY() / CELL_HEIGHT;
        if (boardX + 1 < BOARD_WIDTH && (figure = board[boardX + 1][boardY].getFigure()) != null)
            if (canMoveFigureByFigure(cell.getFigure(), figure))
                return board[boardX + 1][boardY];

        if (boardX - 1 >= 0 && (figure = board[boardX - 1 ][boardY].getFigure()) != null)
            if (canMoveFigureByFigure(cell.getFigure(), figure))
                return board[boardX - 1][boardY];

        if (boardY + 1 < BOARD_HEIGHT && (figure = board[boardX][boardY + 1].getFigure()) != null)
            if (canMoveFigureByFigure(cell.getFigure(), figure))
                return board[boardX][boardY + 1];

        if (boardY - 1 >= 0 && (figure = board[boardX][boardY - 1].getFigure()) != null)
            if (canMoveFigureByFigure(cell.getFigure(), figure))
                return board[boardX][boardX - 1];
        return null;
    }

    /**
     * Check if one figure can move the other.
     * @param figure - the one that moves.
     * @param target - the one that is being moved.
     * @return true if possible, false otherwise.
     */
    public boolean canMoveFigureByFigure(Figure figure, Figure target) {
        int figureWeight = figure.getFiguresType().getWeight();
        int targetWeight = target.getFiguresType().getWeight();
        return figureWeight > targetWeight && figure.getFiguresTeam() != target.getFiguresTeam();
    }

    /**
     * Drawing a board and highlighting the cell with the selected figure.
     * @param g2d
     * @param height = JFRAME_height - statusPanel(BorderLayout.NORTH)_height - controlPanel(BorderLayout.SOUTH)_height
     * @param width = JFRAME_width - leftSideGui(BorderLayout.WEST)_width - rightSideGui(BorderLayout.EAST)_width
     */
    public void drawBoard(Graphics2D g2d, int height, int width) {
        int paddingX = (width - imageBoard.getIconWidth()) / 2;
        int paddingY = (height - imageBoard.getIconHeight()) / 2;
        g2d.drawImage(imageBoard.getImage(), paddingX, paddingY, null);
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board.length; y++) {
                Figure figure = board[x][y].getFigure();
                if (figure != null) {
                    g2d.drawImage(figure.getImage().getImage(), paddingX + board[x][y].getX(), paddingY + board[x][y].getY(), null);
                    if (figure.isSelected()) {
                        g2d.setColor(Color.BLUE);
                        g2d.setStroke(new BasicStroke(2));
                        g2d.drawRect(paddingX + board[x][y].getX(), paddingY + board[x][y].getY(), 100, 100);
                    }
                }
            }
        }
    }

    public String getStepHistoryText() {
        return stepHistory.toString();
    }

    public void setCurrentTeam(Team currentTeam) {
        this.currentTeam = currentTeam;
    }

    /**
     * Keeps track of the process of teams' moves and portrays it
     * in the history. ("1g ... ;
     *                   1s ... ")
     * @param currentTeam - current team's turn
     */
    public void teamsMovesHistory(Team currentTeam) {
        if (stepHistory.length() != 0) stepHistory.append("\n");
        char teamPrefix = Character.toLowerCase(currentTeam.name().charAt(0));
        stepHistory.append(moves/2 + 1).append(teamPrefix).append(" ");
        moves++;
    }

    /**
     * Adds to the current history of steps.
     * @param histToAdd - history to be added.
     */
    public void addToStepHistory(StringBuilder histToAdd) {
        stepHistory.append(histToAdd);
    }

    public Team getCurrentTeam() {
        return currentTeam;
    }

    /**
     * Turns on the mode against a bot.
     * @param botMode true if a game against a bot, false otherwise.
     */
    public void setBotMode(boolean botMode) {
        this.botMode = botMode;
    }

    /**
     * Checks if the current game is against a bot.
     * @return true if it is, false otherwise.
     */
    public boolean isBotMode() {
        return botMode;
    }
}
