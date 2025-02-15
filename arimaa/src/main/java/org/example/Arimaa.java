package org.example;


import org.example.gamedata.Team;
import org.example.gui.*;
import org.example.gui.Menu;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class Arimaa extends JFrame {
    public static int WIDTH = 1200;
    public static int HEIGHT = 950;
    public static String TITLE = "Arimaa game";
    private StatusPanel statusPanel;
    private ControlPanel controlPanel;
    private GameBoard gameBoard;
    private HashMap<Team, FiguresSetter> figuresSetterHashMap = new HashMap<>();
    private HashMap<Team, PlayerInfoPanel> playerInfoPanelHashMap = new HashMap<>();
    private Menu menu;
    private Clock clock;
    public static final Logger logger = new Logger(true);


    public Arimaa()  {
        startTimer();
        showMenuPage();

        this.setTitle(TITLE);
        this.setSize(WIDTH, HEIGHT);
        this.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - WIDTH / 2,
                Toolkit.getDefaultToolkit().getScreenSize().height / 2 - HEIGHT / 2);
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    /**
     * Opens the menu.
     */
    public void showMenuPage() {
        logger.log(this, "Menu opened.");
        this.getContentPane().removeAll();
        menu = new Menu(this);
        this.getContentPane().add(menu, BorderLayout.CENTER);
        this.revalidate();
    }

    /**
     * Start of the new game.
     */
    public void newGame() {
        logger.log(this, "Start of the new game.");
        this.getContentPane().removeAll();

        controlPanel = new ControlPanel(this);
        figuresSetterHashMap.put(Team.GOLD, new FiguresSetter(this, Team.GOLD));
        figuresSetterHashMap.put(Team.SILVER, new FiguresSetter(this, Team.SILVER));
        statusPanel = new StatusPanel(this);
        gameBoard = new GameBoard(this);
        gameBoard.setPlaceMode(true);

        this.getContentPane().add(gameBoard, BorderLayout.CENTER);
        this.getContentPane().add(controlPanel, BorderLayout.SOUTH);
        this.getContentPane().add(figuresSetterHashMap.get(Team.GOLD), BorderLayout.WEST);
        this.getContentPane().add(figuresSetterHashMap.get(Team.SILVER), BorderLayout.EAST);
        this.getContentPane().add(statusPanel, BorderLayout.NORTH);
        this.revalidate();

        getControlPanel().setUtilButtons(false);
        getControlPanel().setEnabledResignButton(true);
        getControlPanel().blockAllDirButtons();
    }

    /**
     * Starting of the game after setting the figures.
     */
    public void startGame() {
        logger.log(this, "Starting of the game after setting the figures.");
        playerInfoPanelHashMap.put(Team.GOLD, new PlayerInfoPanel(this, Team.GOLD));
        playerInfoPanelHashMap.put(Team.SILVER, new PlayerInfoPanel(this, Team.SILVER));

        this.getContentPane().remove(figuresSetterHashMap.get(Team.GOLD));
        this.getContentPane().remove(figuresSetterHashMap.get(Team.SILVER));

        this.getContentPane().add(playerInfoPanelHashMap.get(Team.GOLD), BorderLayout.WEST);
        this.getContentPane().add(playerInfoPanelHashMap.get(Team.SILVER), BorderLayout.EAST);
        this.revalidate();
    }

    /**
     * Game start after loading the last saved game state.
     */
    public void loadGame() {
        logger.log(this, "Game start after loading.");
        this.getContentPane().removeAll();

        controlPanel = new ControlPanel(this);
        playerInfoPanelHashMap.put(Team.GOLD, new PlayerInfoPanel(this, Team.GOLD));
        playerInfoPanelHashMap.put(Team.SILVER, new PlayerInfoPanel(this, Team.SILVER));
        statusPanel = new StatusPanel(this);
        gameBoard = new GameBoard(this);
        gameBoard.setGameStarted(true);
        gameBoard.loadGame();

        this.getContentPane().add(gameBoard, BorderLayout.CENTER);
        this.getContentPane().add(controlPanel, BorderLayout.SOUTH);
        this.getContentPane().add(statusPanel, BorderLayout.NORTH);
        this.getContentPane().add(playerInfoPanelHashMap.get(Team.GOLD), BorderLayout.WEST);
        this.getContentPane().add(playerInfoPanelHashMap.get(Team.SILVER), BorderLayout.EAST);
        this.revalidate();
    }

    /**
     * Timer starter. Works in the background as a clock.
     */
    private void startTimer() {
        logger.log(this, "Game clock start.");
        clock = new Clock(this);
        Thread thread = new Thread(clock);
        thread.start();
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public StatusPanel getStatusPanel() {
        return statusPanel;
    }

    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    public FiguresSetter getFiguresSetter(Team team) {
        return figuresSetterHashMap.get(team);
    }

    public PlayerInfoPanel getPlayerInfoPanel(Team team) {
        return playerInfoPanelHashMap.get(team);
    }

    public Clock getClock() {
        return clock;
    }
}
