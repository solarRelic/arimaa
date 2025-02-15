package org.example.gui;

import org.example.Arimaa;
import org.example.gamedata.BlockButtons;
import org.example.gamedata.DirControlButton;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

import static org.example.gui.GuiUtil.createGBC;

public class ControlPanel extends JPanel {
    private final Arimaa arimaa;
    private final HashMap<DirControlButton, JButton> buttons = new HashMap<>();
    private JLabel currentTime;
    private JButton endTurn;
    private JButton saveGame;
    private JButton menu;
    private JButton undo;
    private JButton redo;
    private JButton surrender;
    private JTextArea stepHistory;

    public ControlPanel(Arimaa arimaa) {
        this.arimaa = arimaa;
        this.setLayout(new GridBagLayout());
        currentTime = new JLabel("00:00:00");
        currentTime.setFont(new Font("Font", Font.PLAIN, 24));

        buttons.put(DirControlButton.UP, new JButton("Up"));
        buttons.put(DirControlButton.DOWN, new JButton("Down"));
        buttons.put(DirControlButton.LEFT, new JButton("Left"));
        buttons.put(DirControlButton.RIGHT, new JButton("Right"));

        stepHistory = new JTextArea(2, 25);
        stepHistory.setEditable(false);

        saveGame = new JButton("Save");
        saveGame.setEnabled(false);
        saveGame.addActionListener(e -> arimaa.getGameBoard().saveGame());

        menu = new JButton("Menu");
        menu.addActionListener(e -> arimaa.getGameBoard().exitGame());

        endTurn = new JButton("End turn");
        endTurn.addActionListener(e -> arimaa.getGameBoard().endTurn());

        surrender = new JButton("Resign");
        surrender.addActionListener(e -> arimaa.getGameBoard().surrender());

        undo = new JButton("Undo");
        undo.addActionListener(e -> arimaa.getGameBoard().undo());

        redo = new JButton("Redo");
        redo.addActionListener(actionEvent -> arimaa.getGameBoard().redo());

        for (DirControlButton button : DirControlButton.values()) {
            buttons.get(button).addActionListener(e -> arimaa.getGameBoard().moveSelectedFigure(button));
        }

        JScrollPane scrollPane = new JScrollPane(stepHistory);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(currentTime, createGBC(0, 0, 1, 1, 30, 0, 1));
        this.add(buttons.get(DirControlButton.LEFT), createGBC(1, 0, 1, 2, 20, 0, 1));
        this.add(buttons.get(DirControlButton.UP), createGBC(2, 0, 1, 1, 20,0, 1));
        this.add(buttons.get(DirControlButton.DOWN), createGBC(2, 1, 1, 1, 20, 0, 1));
        this.add(buttons.get(DirControlButton.RIGHT), createGBC(3, 0, 1, 2, 20, 0, 1));
        this.add(menu, createGBC(4, 0, 1, 1, 20, 0, 1));
        this.add(endTurn, createGBC(4, 1, 1, 1, 20, 0, 1));
        this.add(saveGame, createGBC(5, 0, 1, 1, 20, 0, 1));
        this.add(surrender, createGBC(5, 1, 1, 1, 20, 0, 1));
        this.add(undo, createGBC(6, 0, 1, 1, 20, 0, 1));
        this.add(redo, createGBC(6, 1, 1, 1, 20, 0, 1));
        this.add(scrollPane, createGBC(7, 0, 1, 2, 300, 10, 1));
        Arimaa.logger.log(this, "'ControlPanel' panel is created.");
    }

    public void setCurrentTime(String time) {
        currentTime.setText(time);
    }

    /**
     * Disabling of the direction buttons.
     */
    public void blockAllDirButtons() {
        Arimaa.logger.log(this, "Blocking all the control buttons.");
        for (DirControlButton button : DirControlButton.values())
            buttons.get(button).setEnabled(false);
    }

    /**
     * Enabling of the direction buttons.
     */
    public void unblockAllDirButtons() {
        Arimaa.logger.log(this, "Blocking all the control buttons.");
        for (DirControlButton button : DirControlButton.values())
            buttons.get(button).setEnabled(true);
    }

    /**
     * Check if all the direction buttons are disabled.
     * @return true if so, false otherwise.
     */
    public boolean allDirButtonsBlocked() {
        for (DirControlButton button : DirControlButton.values())
            if (!buttons.get(button).isEnabled()) return true;
        return false;
    }

    /**
     * Disables/enables a direction button based on whether it is blocked or not.
     * @param blockButtons if false, this button will be disabled; if true - enabled.
     */
    public void blockButton(BlockButtons blockButtons) {
        StringBuilder sb = new StringBuilder();
        for (DirControlButton button : DirControlButton.values()) {
            buttons.get(button).setEnabled(blockButtons.getState(button));
            if (!blockButtons.getState(button))
                sb.append("(").append(button.name()).append(")");
        }
        Arimaa.logger.log(this, "Blocking some of the control buttons " + sb + ".");
    }

    /**
     * Disables/enables the 'Save' button on the Control panel part of the screen.
     * @param enable true to enable, false to disable.
     */
    public void setEnabledSaveButton(boolean enable) {
        saveGame.setEnabled(enable);
    }

    /**
     * Disables/enables the 'Resign' button on the Control panel part of the screen.
     * @param enable true to enable, false to disable.
     */
    public void setEnabledResignButton(boolean enable) {
        surrender.setEnabled(enable);
    }

    /**
     * Portrays the history of moves and its steps in the History window.
     * @param text - to be shown in the window.
     */
    public void setStepHistory(String text) {
        stepHistory.setText(text);
        stepHistory.setCaretPosition(stepHistory.getDocument().getLength());
    }

    /**
     * Disables/enables the 'Util' buttons on the screen.
     * @param state true to enable, false to disable.
     */
    public void setUtilButtons(boolean state) {
        undo.setEnabled(state);
        redo.setEnabled(state);
        saveGame.setEnabled(state);
        surrender.setEnabled(state);
        endTurn.setEnabled(state);
    }
}
