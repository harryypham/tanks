package Tanks;

import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

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

    public static final int UP = 38;
    public static final int LEFT = 37;
    public static final int DOWN = 40;
    public static final int RIGHT = 39;
    public static final int W = 87;
    public static final int S = 83;

    public String configPath;

    public static Random random = new Random();

    // Additional attributes
    private Config config;
    private PImage backgroundImg;
    private PImage treeImg;
    private PImage fuelImg;
    private int[] foregroundColor;
    private Column[] columns = new Column[WIDTH];
    private Tree[] trees = new Tree[WIDTH];
    private Wind wind;
    private char currentPlayer;
    private Map<Character, Tank> tanks = new HashMap<Character, Tank>();
    private Map<Character, Player> players = new HashMap<Character, Player>();
    // private ArrayList<Tank> tanks = new ArrayList<Tank>();

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
        Tree.setImg(treeImg);

        // Fuel
        fuelImg = config.getFuelImage();

        // Players' color
        Map<Character, int[]> playersColor = config.getPlayerColors();

        // Wind
        PImage posWindImg = config.getPosWindImg();
        PImage negWindImg = config.getNegWindImg();
        wind = new Wind(posWindImg, negWindImg);

        // Get layout file and initialize game
        File file = config.getLayoutFile();
        int[][] temp = getLayout(file);
        int[] tempColumn = temp[0];
        int[] tempTree = temp[1];
        char[] tempTank = getTanks(file);

        for (int i = 0; i < WIDTH; i++) {
            columns[i] = new Column(i, tempColumn[i]);
            if (tempTree[i] == 1) {
                trees[i] = new Tree(columns[i]);
                columns[i].addTree(trees[i]);
            }
            if (Character.isLetter(tempTank[i])) {
                Player player = new Player(tempTank[i], playersColor.get(tempTank[i]));
                Tank tank = new Tank(columns[i], player);
                tanks.put(tempTank[i], tank);
                player.addTank(tank);
                players.put(tempTank[i], player);
                columns[i].setTank(tank);
            }

        }

        Object[] tempArr = players.keySet().toArray();
        for (int c = 0; c < tempArr.length; c++) {
            if (c == tempArr.length - 1) {
                players.get(tempArr[c]).setNextPlayer((char) tempArr[0]);
                continue;
            }
            players.get(tempArr[c]).setNextPlayer((char) tempArr[c + 1]);
        }

        currentPlayer = (char) tempArr[0];

        // System.out.println(this.getClass().getResource());
        // System.out.println(this.getClass().getResource(filename).getPath().toLowerCase(Locale.ROOT).replace("%20",
        // " "));
        // loadImage(this.getClass().getResource(filename).getPath().toLowerCase(Locale.ROOT).replace("%20",
        // " "));
    }

    public Column[] getColumns() {
        return this.columns;
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getKeyCode() == LEFT) {
            if (tanks.get(currentPlayer).getFuel() > 0) {
                Column new_col = columns[tanks.get(currentPlayer).getX() - 2];
                tanks.get(currentPlayer).changeCol(new_col);
            }
        }
        if (event.getKeyCode() == RIGHT) {
            if (tanks.get(currentPlayer).getFuel() > 0) {
                Column new_col = columns[tanks.get(currentPlayer).getX() + 2];
                tanks.get(currentPlayer).changeCol(new_col);
            }
        }

        if (event.getKeyCode() == UP) {
            tanks.get(currentPlayer).changeDeg((float) 0.1);
        }

        if (event.getKeyCode() == DOWN) {
            tanks.get(currentPlayer).changeDeg((float) -0.1);
        }

        if (event.getKeyCode() == W) {
            tanks.get(currentPlayer).changePower((float) 1.2);
        }

        if (event.getKeyCode() == S) {
            tanks.get(currentPlayer).changePower((float) -1.2);
        }

        if (event.getKeyCode() == 32) {
            tanks.get(currentPlayer).fire(wind.getWind());
            currentPlayer = players.get(currentPlayer).getNextPlayer();
            wind.changeWind();
        }
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
        currentPlayer = players.get(currentPlayer).getNextPlayer();
        wind.changeWind();
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
        for (Tank tank : tanks.values()) {
            tank.draw(this);
        }

        // draw wind
        wind.draw(this);

        // draw current player
        fill(0, 0, 0);
        textSize(16);
        text(String.format("Player %c's turn", currentPlayer), 130, 30);

        // draw fuel
        image(fuelImg, 150, 10, 25, 25);
        fill(0, 0, 0);
        textSize(16);
        textAlign(39);
        text(players.get(currentPlayer).getTank().getFuel(), 150 + 60, 30);

        // draw health
        fill(0, 0, 0);
        textSize(16);
        textAlign(39);
        text("Health:", 400, 30);
        stroke(0, 0, 0);
        strokeWeight(2);
        fill(players.get(currentPlayer).getColor()[0], players.get(currentPlayer).getColor()[1],
                players.get(currentPlayer).getColor()[2]);
        rect(400 + 10, 10, 200, 30);
        fill(0, 0, 0);
        text(tanks.get(currentPlayer).getHealth(), 650, 30);
        text(String.format("Power: %d", tanks.get(currentPlayer).getPower()), 400 + 22, 70);

        // draw scoreboard
        fill(0, 1);
        stroke(0);
        strokeWeight(4);
        rect(710, 50, 140, 120);
        fill(0, 0, 0);
        textSize(16);
        text("Scores", 770, 70);
        line(710, 80, 850, 80);

        int currY = 100;
        for (Player player : players.values()) {
            int[] color = player.getColor();
            fill(color[0], color[1], color[2]);
            text(String.format("Player %c", player.getChar()), 780, currY);
            fill(0, 0, 0);
            text(player.getScore(), 840, currY);
            currY += 20;
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

    /*
     * Get and smooth terrain, get tree
     */
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
                        continue;
                    }
                    if (line.charAt(c) == 'T') {
                        trees[c * 32] = 1;
                        continue;
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

    /*
     * Get tank
     */
    private char[] getTanks(File file) {
        char[] tanks = new char[896];

        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.length() == 0) {
                    continue;
                }
                for (int c = 0; c < line.length(); c++) {
                    if (Character.isLetter(line.charAt(c)) & line.charAt(c) != 'X' & line.charAt(c) != 'T') {
                        tanks[c * 32] = line.charAt(c);
                    }
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
        return tanks;
    }
}
