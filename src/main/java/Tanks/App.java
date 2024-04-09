package Tanks;

import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;

public class App extends PApplet {

    public static final int CELLSIZE = 32; // 8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 0;
    public static int WIDTH = 864; // CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; // BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH / CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;

    public String configPath;

    public static Random random = new Random();

    // Additional attributes
    private JSONObject[] levels;
    private int currentLevelIdx = 0;
    private PImage backgroundImg;
    private PImage treeImg;
    private int[] pixels = new int[896];
    private int[] trees = new int[896];
    private Wind wind;

    private String[][] terrain = new String[BOARD_HEIGHT][BOARD_WIDTH];

    // Feel free to add any additional methods or attributes you want. Please put
    // classes in different files.

    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player
     * and map elements.
     */
    @Override
    public void setup() {
        frameRate(FPS);
        // See PApplet javadoc:
        JSONObject json = loadJSONObject(configPath);

        JSONArray tempArr = json.getJSONArray("levels");

        // Save levels into Object[] levels
        this.levels = new JSONObject[tempArr.size()];
        for (int i = 0; i < tempArr.size(); i++) {
            this.levels[i] = (JSONObject) tempArr.get(i);
        }

        // Background
        String backgroundImgPath = "build/resources/main/Tanks/"
                + this.levels[this.currentLevelIdx].getString("background");
        backgroundImg = loadImage(backgroundImgPath);

        // Tree
        String treeImgPath;
        if (this.levels[this.currentLevelIdx].getString("trees") == null) {
            treeImgPath = "tree1.png";
        } else {
            treeImgPath = this.levels[this.currentLevelIdx].getString("trees");
        }
        treeImgPath = "build/resources/main/Tanks/"
                + treeImgPath;
        treeImg = loadImage(treeImgPath);

        // Wind
        wind = new Wind();

        // Get level and draw terrain
        String filename = this.levels[this.currentLevelIdx].getString("layout");
        File file = new File(filename);

        int lineIdx = 0;
        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                lineIdx += 1;
                if (line.length() == 0) {
                    continue;
                }
                for (int c = 0; c < line.length(); c++) {
                    if (line.charAt(c) == 'X') {
                        for (int i = 0; i < 32; i++) {
                            System.out.println(HEIGHT - (lineIdx - 1) * 32);
                            pixels[c * 32 + i] = HEIGHT - (lineIdx - 1) * 32;
                        }
                        System.out.printf("Line: %d, Col: %d \n", lineIdx, c);
                    }
                    if (line.charAt(c) == 'T') {
                        trees[c * 32] = 1;
                    }
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
        for (int k = 0; k < 2; k++) {
            int currSum = 0;
            for (int i = 1; i < 33; i++) {
                currSum += pixels[i];
            }
            for (int i = 0; i < 864; i++) {
                pixels[i] = currSum / 32;
                if (i < 863) {
                    currSum -= pixels[i + 1];
                    currSum += pixels[i + 33];
                }
            }
        }

        // System.out.println(this.getClass().getResource());
        // System.out.println(this.getClass().getResource(filename).getPath().toLowerCase(Locale.ROOT).replace("%20",
        // " "));
        // loadImage(this.getClass().getResource(filename).getPath().toLowerCase(Locale.ROOT).replace("%20",
        // " "));
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
    @Override
    public void keyPressed(KeyEvent event) {

    }

    /**
     * Receive key released signal from the keyboard.
     */
    @Override
    public void keyReleased() {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO - powerups, like repair and extra fuel and teleport

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Draw all elements in the game by current frame.
     */
    @Override
    public void draw() {

        background(backgroundImg);
        wind.draw(this);
        this.noStroke();
        String temp = this.levels[this.currentLevelIdx].getString("foreground-colour");
        String[] colors = temp.split(",", -1);
        this.fill(Integer.parseInt(colors[0]), Integer.parseInt(colors[1]), Integer.parseInt(colors[2]));
        for (int c = 0; c < 864; c++) {
            this.rect(c, 640 - pixels[c], 1, pixels[c]);
        }
        for (int c = 0; c < 864; c++) {
            if (trees[c] == 1) {
                image(treeImg, c - 16, 640 - pixels[c] - 30, 32, 32);
            }
        }

        // ----------------------------------
        // display HUD:
        // ----------------------------------
        // TODO

        // ----------------------------------
        // display scoreboard:
        // ----------------------------------
        // TODO

        // ----------------------------------
        // ----------------------------------

        // TODO: Check user action
    }

    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }

}
