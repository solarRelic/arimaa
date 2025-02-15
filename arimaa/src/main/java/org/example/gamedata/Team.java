package org.example.gamedata;

import java.awt.*;
import java.io.Serializable;

public enum Team implements Serializable {
    GOLD("g_", "Gold", new Color(255,215,0)),
    SILVER("s_", "Silver", new Color(192,192,192));
    private final String imagePrefix;
    private final String teamPrefix;
    private final Color teamColor;

    Team(String imagePrefix, String teamPrefix, Color teamColor) {
        this.imagePrefix = imagePrefix;
        this.teamPrefix = teamPrefix;
        this.teamColor = teamColor;
    }

    public String getImagePrefix() {
        return imagePrefix;
    }

    public String getTeamPrefix() {
        return teamPrefix;
    }
    public Color getTeamColor() {
        return teamColor;
    }
}
