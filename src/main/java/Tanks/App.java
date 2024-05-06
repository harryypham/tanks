package Tanks;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
import processing.event.KeyEvent;

import java.util.*;

public class App extends PApplet {

    // App settings
    public static int WIDTH = 864;
    public static int HEIGHT = 640;
    public static final int FPS = 30;

    // Keycode
    public static final int BACKSPACE = 32;
    public static final int UP = 38;
    public static final int LEFT = 37;
    public static final int DOWN = 40;
    public static final int RIGHT = 39;
    public static final int W = 87;
    public static final int S = 83;
    public static final int F = 70;
    public static final int R = 82;
    public static final int P = 80;

    // Path of config file
    public String configPath;

    // Additional attributes
    private int totalLevels;
    private int level = 0;
    private Config config;
    private PImage backgroundImg;
    private PImage fuelImg;
    private PImage parachuteImg;
    private int[] foregroundColor;
    private Wind wind;

    private Column[] columns = new Column[WIDTH];
    private Tree[] trees = new Tree[WIDTH];
    private Map<Character, Tank> tanks = new HashMap<Character, Tank>();
    private Map<Character, Player> players = new HashMap<Character, Player>();

    private int currentPlayerIdx;
    private char currentPlayer;
    private Character[] playersTurn;
    private int playersLeft;

    private boolean endgame = false;

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
     * Load all resources (terrain, trees, tanks, ...)
     */
    @Override
    public void setup() {
        frameRate(FPS);
        JSONObject json = loadJSONObject(configPath);
        config = new Config(json, this, level);

        totalLevels = config.getNumLevels();
        backgroundImg = config.getBackground();
        foregroundColor = config.getForegroundColor();

        PImage treeImg = config.getTreeImage();
        Tree.setImg(treeImg);

        fuelImg = config.getFuelImage();

        parachuteImg = config.getParachuteImage();
        Tank.setParachuteImg(parachuteImg);

        PImage posWindImg = config.getPosWindImg();
        PImage negWindImg = config.getNegWindImg();
        Wind.setWindImg(posWindImg, negWindImg);

        // Process layout
        initializeEnv();

        // Process players
        initializePlayers();

        Bullet.setCol(columns);
    }

    /**
     * Receive key pressed signal from the keyboard.
     * 
     * @param event A KeyEvent specifying which key the user just pressed.
     */
    @Override
    public void keyPressed(KeyEvent event) {
        Player p = players.get(currentPlayer);
        Tank t = tanks.get(currentPlayer);
        if (endgame && event.getKeyCode() == R) {
            endgame = false;
            this.level = 0;
            this.setup();
        }

        if (!endgame && event.getKeyCode() == LEFT) {
            if (t.getFuel() > 0) {
                Column new_col = columns[t.getX() - 2];
                t.changeCol(new_col);
            }
        }

        if (!endgame && event.getKeyCode() == RIGHT) {
            if (t.getFuel() > 0) {
                Column new_col = columns[t.getX() + 2];
                t.changeCol(new_col);
            }
        }

        if (!endgame && event.getKeyCode() == UP) {
            t.changeDeg((float) 0.1);
        }

        if (!endgame && event.getKeyCode() == DOWN) {
            t.changeDeg((float) -0.1);
        }

        if (!endgame && event.getKeyCode() == W) {
            t.changePower((float) 1.2);
        }

        if (!endgame && event.getKeyCode() == S) {
            t.changePower((float) -1.2);
        }

        if (!endgame && event.getKeyCode() == BACKSPACE) {
            t.fire(wind.getWind());
            nextPlayer();
            wind.changeWind();
        }

        if (!endgame && event.getKeyCode() == R) {
            if (p.getScore() >= 20 && t.getHealth() < 100) {
                t.repair();
                p.changeScore(-20);
            }
        }

        if (!endgame && event.getKeyCode() == F) {
            if (p.getScore() >= 10) {
                t.addFuel();
                p.changeScore(-10);
            }
        }

        if (!endgame && event.getKeyCode() == P) {
            if (p.getScore() >= 15) {
                t.addParachute();
                p.changeScore(-15);
            }
        }
    }

    /**
     * Draw all elements in the game by current frame.
     */
    @Override
    public void draw() {
        background(backgroundImg);
        noStroke();
        fill(foregroundColor[0], foregroundColor[1], foregroundColor[2]);

        drawTerrain();
        drawTanks();
        drawWind();
        drawCurrentPlayerStats();
        drawScoreboard();

        if (endgame) {
            endGame();
        } else {
            checkEndgame();
        }
    }

    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }

    /*
     * Below are helper functions.
     */

    /**
     * End the current turn. Move to the next player.
     */
    private void nextPlayer() {
        if (playersLeft == 0) {
            return;
        }
        currentPlayerIdx = (currentPlayerIdx == playersTurn.length - 1) ? 0 : currentPlayerIdx + 1;
        currentPlayer = playersTurn[currentPlayerIdx];
        if (currentPlayer == '#' || tanks.get(currentPlayer) == null) {
            nextPlayer();
        }
    }

    /**
     * Get player's index in the array given the name of the player.
     * 
     * @param key A character representing the name of the player.
     * @return The player's index
     */
    private int findPlayerIdx(char key) {
        List<Character> list = Arrays.asList(playersTurn);
        return list.indexOf(key);
    }

    /**
     * Set up the terrain, trees, tanks.
     */
    private void initializeEnv() {
        Object[] temp = config.loadLayout();
        Map<Character, int[]> playersColor = config.getPlayerColors();
        trees = new Tree[WIDTH];
        wind = new Wind();
        int[] tempColumn = (int[]) temp[0];
        int[] tempTree = (int[]) temp[1];
        char[] tempTank = (char[]) temp[2];

        for (int i = 0; i < WIDTH; i++) {
            columns[i] = new Column(i, tempColumn[i]);
            if (tempTree[i] == 1) {
                trees[i] = new Tree(columns[i]);
            }
            if (Character.isLetter(tempTank[i])) {
                Player player;
                if (this.level == 0) {
                    player = new Player(tempTank[i], playersColor.get(tempTank[i]));
                } else {
                    player = players.get(tempTank[i]);
                }
                Tank tank = new Tank(columns[i], player);
                tanks.put(tempTank[i], tank);
                player.setTank(tank);
                if (this.level == 0) {
                    players.put(tempTank[i], player);
                }
                columns[i].setTank(tank);
            }
        }
    }

    /**
     * Set up the players.
     */
    private void initializePlayers() {
        Object[] tempArr = players.keySet().toArray();
        playersTurn = new Character[tempArr.length];
        for (int c = 0; c < tempArr.length; c++) {
            playersTurn[c] = (char) tempArr[c];
        }
        currentPlayerIdx = 0;
        currentPlayer = playersTurn[0];
        playersLeft = playersTurn.length;
    }

    /**
     * Display the terrain on screen.
     */
    private void drawTerrain() {
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
    }

    /**
     * Display the tanks on screen.
     */
    private void drawTanks() {
        for (Map.Entry<Character, Tank> entry : tanks.entrySet()) {
            Character key = entry.getKey();
            Tank tank = entry.getValue();
            if (tank == null) {
                continue;
            }
            if (tank.getHealth() > 0 && tank.getY() > 0) {
                tank.draw(this);
            } else {
                if (!tank.checkDeleted()) {
                    tank.deleteTankFromGame();
                    playersTurn[findPlayerIdx(key)] = '#';
                    playersLeft -= 1;
                }
                if (!tank.getDisplay()) {
                    tanks.replace(key, null);
                } else {
                    tank.draw(this);
                }
            }
        }
    }

    /**
     * Display the wind on screen.
     */
    private void drawWind() {
        wind.draw(this);
    }

    /**
     * Display current player's information (tank's health, power, parachutes) on
     * screen.
     */
    private void drawCurrentPlayerStats() {
        Tank t = tanks.get(currentPlayer);
        if (t == null) {
            nextPlayer();
        }
        t = tanks.get(currentPlayer);
        Player p = players.get(currentPlayer);

        // draw fuel image
        image(fuelImg, 150, 10, 25, 25);

        // draw parachute image
        image(parachuteImg, 150, 40, 25, 25);

        if (playersLeft > 0) {
            // display current player
            fill(0, 0, 0);
            textSize(16);
            text(String.format("Player %c's turn", currentPlayer), 130, 30);

            // display current player's tank stats (fuel and num of parachutes)
            textAlign(39);
            text(t.getFuel(), 150 + 60, 30);
            text(t.getParachutes(), 150 + 40, 60);

            fill(0, 0, 0);
            textSize(16);
            textAlign(39);
            text("Health:", 400, 30);

            // draw health and power bar
            stroke(0, 0, 0);
            strokeWeight(2);
            fill(255, 255, 255);
            rect(400 + 10, 10, 200, 30);

            fill(p.getColor()[0], p.getColor()[1],
                    p.getColor()[2]);
            rect(400 + 10, 10, t.getHealth() * 2, 30);

            stroke(211, 211, 211);
            strokeWeight(3);
            rect(400 + 10, 10, t.getPower() * 2, 30);

            // display health and power text
            fill(0, 0, 0);
            text(t.getHealth(), 650, 30);
            text(String.format("Power: %d", t.getPower()), 400 + 22, 70);
        }
    }

    /**
     * Display the scoreboard on screen.
     */
    private void drawScoreboard() {
        fill(0, 1);
        stroke(0);
        strokeWeight(4);
        rect(710, 50, 140, 120 + (playersTurn.length - 4) * 20);
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
    }

    /**
     * Check if game has ended.
     */
    private void checkEndgame() {
        if (playersLeft <= 1) {
            if (this.level == totalLevels - 1) {
                endgame = true;
                return;
            }
            this.level += 1;
            this.setup();
        }
    }

    /**
     * End the game.
     */
    private void endGame() {
        List<Player> sortedPlayers = new ArrayList<>(players.values());
        Collections.sort(sortedPlayers);

        Player winner = sortedPlayers.get(0);
        int[] color = winner.getColor();
        fill(color[0], color[1], color[2]);
        textSize(24);
        text(String.format("Player %c wins!", winner.getChar()), 390, 180);

        fill(0, 1);
        stroke(0);
        strokeWeight(4);
        rect(215, 200, 400, 170 + (playersTurn.length - 4) * 20);
        fill(0, 0, 0);
        text("Final Scores", 360, 230);
        line(215, 240, 615, 240);

        int y = 270;
        for (Player p : sortedPlayers) {
            color = p.getColor();
            fill(color[0], color[1], color[2]);
            text(String.format("Player %c", p.getChar()), 315, y);
            fill(0, 0, 0);
            text(p.getScore(), 600, y);
            y += 30;
        }
    }
}
