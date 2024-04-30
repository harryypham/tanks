package Tanks;

import processing.core.PImage;

import java.util.*;

public class Wind {
    private int value;
    private static PImage img;
    private static PImage rev_img;
    private Random rand = new Random();

    public Wind() {
        this.value = rand.nextInt(71) - 35; // [-35, 35]
    }

    public static void setWindImg(PImage image1, PImage image2) {
        img = image1;
        rev_img = image2;
    }

    public void changeWind() {
        int randomValue = rand.nextInt(11) - 5; // [-5, 5]
        this.value += randomValue;
        this.value = Math.max(Math.min(35, this.value), -35);
    }

    public int getWind() {
        return this.value;
    }

    public void draw(App app) {
        PImage image;
        if (value >= 0) {
            image = img;
        } else {
            image = rev_img;
        }
        app.image(image, 760, 0, 40, 40);
        app.fill(0, 0, 0);
        app.textSize(16);
        app.textAlign(39);
        app.text(value, 760 + 76, 25);
    }
}
