package org.example.gui;

import org.example.Arimaa;

import javax.swing.*;
import java.awt.*;

public class StatusPanel extends JPanel {
    private Arimaa arimaa;
    private JLabel statusLabel;

    public StatusPanel(Arimaa arimaa) {
        this.arimaa = arimaa;
        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Helvetica", Font.BOLD, 25));
        statusLabel.setText("Place the figures");
        this.add(statusLabel);
        Arimaa.logger.log(this, "'Status Panel' panel has been created.");
    }

    /**
     * Upmost Gui info status setter.
     * @param status - status to be set.
     */
    public void setStatus(String status) {
        statusLabel.setText(status);
    }
}
