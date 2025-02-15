package org.example.gui;

import java.awt.*;

/**
 * For the placement of the buttons on the screen.
 */
public class GuiUtil {
    public static GridBagConstraints createGBC(int gridx, int gridy,
                                               int gridWidth, int gridHeight,
                                               int ipadx, int ipady, int fill) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridWidth;
        gbc.gridheight = gridHeight;
        gbc.ipadx = ipadx;
        gbc.ipady = ipady;
        gbc.fill = fill;
        return gbc;
    }
    private GuiUtil() {
    }
}
