package Tanks;

import processing.core.PApplet;
import processing.core.PImage;

import java.io.*;
import java.util.*;

public class Wind {
    private int value;
    private PImage img;

    public Wind() {
        Random rand = new Random();
        this.value = rand.nextInt(71) - 35;
    }

    public int getWind() {
        return value;
    }

    public void draw(App app) {
        String imgPath = "build/resources/main/Tanks/";
        if (this.value > 0) {
            imgPath += "wind.png";
        } else {
            imgPath += "wind-1.png";
        }
        img = app.loadImage(imgPath);
        app.image(img, 760, 0, 40, 40);
        app.fill(0, 0, 0);
        app.textSize(20);
        app.textAlign(39);
        app.text(value, 760 + 76, 25);
    }
}
