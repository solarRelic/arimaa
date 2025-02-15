package org.example.gui;

import org.example.Arimaa;
import org.example.StepHistory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static org.example.gui.GuiUtil.createGBC;

public class Menu extends JPanel {
    private final Arimaa arimaa;
    private JLabel title;
    private JButton startGame;
    private JButton startGameWithBot;
    private JButton loadGame;

    public Menu(Arimaa arimaa) {
        this.arimaa = arimaa;
        this.setLayout(new GridBagLayout());
        title = new JLabel("ARIMAA");
        title.setFont(new Font("Helvetica", Font.BOLD, 60));

        startGame = new JButton("Launch game");
        startGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StepHistory.newGame();
                arimaa.newGame();
            }
        });

        startGameWithBot = new JButton("Launch game vs. pc");
        startGameWithBot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                StepHistory.newGame();
                arimaa.newGame();
                arimaa.getGameBoard().setBotMode(true);
            }
        });

        loadGame = new JButton("Load game");
        loadGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                arimaa.loadGame();
            }
        });
        if (!(new File("game.dat").exists()) || !(new File("step.dat").exists()))
            loadGame.setEnabled(false);

        this.add(title, createGBC(0, 0, 4, 1, 0, 0, 1));
        this.add(startGame, createGBC(0, 1, 4, 1, 0, 0, 1));
        this.add(startGameWithBot, createGBC(0, 2, 4, 1, 0, 0, 1));
        this.add(loadGame, createGBC(0, 3, 4, 1, 0, 0, 1));
        Arimaa.logger.log(this, "'Menu' panel has been created.");
    }
}