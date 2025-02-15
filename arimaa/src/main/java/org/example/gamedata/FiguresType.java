package org.example.gamedata;

import java.io.Serializable;

public enum FiguresType implements Serializable {
    ELEPHANT("elephant.png", 6),
    CAMEL("camel.png", 5),
    HORSE("horse.png", 4),
    DOG("dog.png", 3),
    CAT("cat.png", 2),
    RABBIT("rabbit.png", 1);

    private final String imageName;
    private final int weight;

    FiguresType(String imageName, int weight) {
        this.imageName = imageName;
        this.weight = weight;
    }

    public String getImageName() {
        return imageName;
    }

    /**
     * To get the power of a figure.
     * @return power of a figure.
     */
    public int getWeight() {
        return weight;
    }
}
