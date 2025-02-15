package org.example.gui;

import org.example.Arimaa;
import org.example.gamedata.FiguresType;
import org.example.gamedata.Team;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;

import static org.example.gui.GuiUtil.createGBC;

public class FiguresSetter extends JPanel {
    private final Arimaa arimaa;
    private HashMap<FiguresType, JButton> buttons = new HashMap<>();
    private HashMap<FiguresType, Integer> figuresCount = new HashMap<>();
    private final Team team;

    public FiguresSetter(Arimaa arimaa, Team team) {
        figuresCount.put(FiguresType.ELEPHANT, 1);
        figuresCount.put(FiguresType.CAMEL, 1);
        figuresCount.put(FiguresType.HORSE, 2);
        figuresCount.put(FiguresType.DOG, 2);
        figuresCount.put(FiguresType.CAT, 2);
        figuresCount.put(FiguresType.RABBIT, 8);

        this.arimaa = arimaa;
        this.team = team;
        this.setLayout(new GridBagLayout());
        this.setBackground(team.getTeamColor());
        int i = 0;
        for (FiguresType type : FiguresType.values()) {
            JButton button = new JButton(type.name());
            buttons.put(type, button);
            buttons.get(type).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (arimaa.getGameBoard().getCurrentTeam() == team )
                        arimaa.getGameBoard().setCurrentFigureToPlace(type);
                }
            });
            this.add(buttons.get(type), createGBC(0,i,2,1,75,10,1));
            i++;
        }
        Arimaa.logger.log(this, "'FiguresSetter' panel is created.");
    }

    /**
     * Changes the amount of figures needed for placement.
     * @param figuresType type of figure to reduce the amount of
     * @param count - will be reduced by this amount (it is always -1)
     * @return true if there are no more figures of the given type, false otherwise
     */
    public boolean changeFigureCount(FiguresType figuresType, int count) {
        int amount = figuresCount.get(figuresType);
        amount += count;
        figuresCount.replace(figuresType, amount);
        return amount <= 0;
    }


    /**
     * Disables the button of the figure when all the figures of this type
     * have been placed on the board during the initiation phase.
     * @param figuresType - given figure (ELEPHANT, CAMEL, ...).
     */
    public void blockFigureButton(FiguresType figuresType) {
        buttons.get(figuresType).setEnabled(false);
        Arimaa.logger.log(this, team.name() + figuresType.name() + " button has been blocked.");
    }

    /**
     * Checks for completion of setting the figures on the board during the initiation phase.
     * @return true if no more figures to place, false otherwise.
     */
    public boolean complete() {
        for (int amount : figuresCount.values())
            if (amount > 0) return false;
        Arimaa.logger.log(this, "All " + team.getTeamPrefix() + " player's figures have been set.");
        return true;
    }

}
