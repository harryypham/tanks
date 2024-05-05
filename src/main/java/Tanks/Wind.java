package Tanks;

import processing.core.PImage;

import java.util.*;

public class Wind {
    /**
     * The value of the wind
     */
    private int value;

    /**
     * Image of wind when value is positve
     */
    private static PImage img;

    /**
     * Image of wind when value is negative
     */
    private static PImage rev_img;
    private Random rand = new Random();

    /**
     * Constructor for the wind.
     */
    public Wind() {
        this.value = rand.nextInt(71) - 35; // [-35, 35]
    }

    /**
     * Set wind images when initialize the game.
     * 
     * @param image1 An image representing the wind when value is positve.
     * @param image2 An image representing the wind when value is negative.
     */
    public static void setWindImg(PImage image1, PImage image2) {
        img = image1;
        rev_img = image2;
    }

    /**
     * Change wind after every player's turn.
     */
    public void changeWind() {
        int randomValue = rand.nextInt(11) - 5; // [-5, 5]
        this.value += randomValue;
        this.value = Math.max(Math.min(35, this.value), -35);
    }

    /**
     * Get the value of the wind.
     * 
     * @return An integer representing the current value of the wind.
     */
    public int getWind() {
        return this.value;
    }

    /**
     * Display the wind on screen.
     * 
     * @param app The main application to draw on.
     */
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
