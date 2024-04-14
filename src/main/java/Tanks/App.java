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
    private Config config;
    private JSONObject[] levels;
    private PImage backgroundImg;
    private PImage treeImg;
    private int[] foregroundColor;
    private Column[] columns = new Column[WIDTH];
    private Tree[] trees = new Tree[WIDTH];
    private Wind wind;
    private ArrayList<Tank> tanks = new ArrayList<Tank>();

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

        config = new Config(json, this);

        // Background
        backgroundImg = config.getBackground();

        // Foreground
        foregroundColor = config.getForegroundColor();

        // Tree
        treeImg = config.getTreeImage();

        // Players' color
        Map<Character, int[]> playersColor = config.getPlayerColors();

        for (Map.Entry<Character, int[]> playerColor : playersColor.entrySet()) {
            // Printing keys
            System.out.print(playerColor.getKey() + ":");
            System.out.println(playerColor.getValue());
        }

        // Wind
        wind = new Wind();

        // Get layout file and initialize game
        File file = config.getLayoutFile();
        int[][] temp = getLayout(file);
        int[] tempColumn = temp[0];
        int[] tempTree = temp[1];

        for (int i = 0; i < WIDTH; i++) {
            columns[i] = new Column(i, tempColumn[i]);
            if (tempTree[i] == 1) {
                trees[i] = new Tree(i, tempColumn[i], treeImg);
                columns[i].addTree(trees[i]);
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

        this.noStroke();
        this.fill(foregroundColor[0], foregroundColor[1], foregroundColor[2]);

        // draw columns
        for (int c = 0; c < 864; c++) {
            columns[c].draw(this);
        }

        // draw trees
        for (int c = 0; c < 864; c++) {
            if (trees[c] != null) {
                trees[c].draw(this);
            }
        }
        // draw tanks
        for (Tank tank : tanks) {
            tank.draw(this);
        }

        // draw wind
        wind.draw(this);

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

    private int[][] getLayout(File file) {
        int[] pixels = new int[896];
        int[] trees = new int[896];

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
                            pixels[c * 32 + i] = HEIGHT - (lineIdx - 1) * 32;
                        }
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

        // moving average twice (sliding window)
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
        int[][] re = { pixels, trees };
        return re;
    }
}
