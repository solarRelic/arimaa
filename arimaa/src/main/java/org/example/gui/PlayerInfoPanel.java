package org.example.gui;

import org.example.Arimaa;
import org.example.gamedata.Player;
import org.example.gamedata.Team;

import javax.swing.*;
import java.awt.*;
import java.security.PublicKey;

import static org.example.gui.GuiUtil.createGBC;

public class PlayerInfoPanel extends JPanel{
    private final Arimaa arimaa;
    private JLabel time;
    private JLabel stepCount;
    private JLabel movesLeft;
    private JLabel timeSpent;


    public PlayerInfoPanel(Arimaa arimaa, Team team) {
        this.arimaa = arimaa;
        this.setLayout(new GridBagLayout());
        this.setBackground(team.getTeamColor());

        Font font = new Font("Helvetica", Font.PLAIN, 19);

        movesLeft = new JLabel("Moves left");
        movesLeft.setFont(font);
        movesLeft.setHorizontalAlignment(SwingConstants.HORIZONTAL);

        stepCount = new JLabel("0");
        stepCount.setFont(font);
        stepCount.setHorizontalAlignment(SwingConstants.HORIZONTAL);

        timeSpent = new JLabel("      Time spent      ");
        timeSpent.setFont(font);
        timeSpent.setHorizontalAlignment(SwingConstants.HORIZONTAL);

        time = new JLabel("0");
        time.setFont(font);
        time.setHorizontalAlignment(SwingConstants.HORIZONTAL);

        this.add(movesLeft, createGBC(0, 0, 2, 1, 0, 0, 1));
        this.add(stepCount, createGBC(0, 1, 2, 1, 0, 0, 1));
        this.add(timeSpent, createGBC(0, 2, 2, 1, 0, 0, 1));
        this.add(time,      createGBC(0, 3, 2, 1, 0, 0, 1));
        Arimaa.logger.log(this, "'PlayerInfoPanel' panel has been created.");
    }

    /**
     * sidesGui updater. Updates the moves left and time taken.
     * @param player left side for the gold player, right side for the silver.
     */
    public void updatePlayerInfo(Player player) {
        stepCount.setText(player.getSteps() + "");      // + "" makes it a string, .setText() needs a string
        time.setText(player.getSeconds() / 60 + ":" + player.getSeconds() % 60);
    }
}
