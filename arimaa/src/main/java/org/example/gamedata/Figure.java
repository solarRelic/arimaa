package org.example.gamedata;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public class Figure implements Serializable {
    private static final String folderName = "img/figures/";
    private final String imageName;
    private final ImageIcon image;
    private final FiguresType figuresType;
    private final Team figuresTeam;
    transient private boolean selected;     //transient for not being recorded to the history (figure selection)
    private boolean isFrozen;

    public Figure(Team figuresTeam, FiguresType figuresType) {
        this.figuresTeam = figuresTeam;
        this.figuresType = figuresType;
        this.imageName = folderName + figuresTeam.getImagePrefix() + figuresType.getImageName();
        this.image = new ImageIcon(this.imageName);
    }

    public ImageIcon getImage() {
        return image;
    }

    /**
     * Checks if the current figure is selected (for highlighting).
     * @return true if it is, false otherwise
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets the figure to be selected (for highlighting).
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Type of figure.
     * @return type of the current figure (elephant, camel, ... ).
     */
    public FiguresType getFiguresType() {
        return figuresType;
    }

    /**
     * Belonging of the figure to the team.
     * @return team which the figure belongs to.
     */
    public Team getFiguresTeam() {
        return figuresTeam;
    }

    /**
     * Gets the 'freezing' state of the figure.
     * @return true if frozen, false otherwise.
     */
    public boolean isFrozen() {
        return isFrozen;
    }

    /**
     * Sets the 'freezing' state of the figure.
     * @param frozen true to set to frozen, false otherwise
     */
    public void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }
}
