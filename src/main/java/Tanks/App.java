package Tanks;

import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

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

        if (event.getKeyCode() == LEFT) {
            if (t.getFuel() > 0) {
                Column new_col = columns[t.getX() - 2];
                t.changeCol(new_col);
            }
        }

        if (event.getKeyCode() == RIGHT) {
            if (t.getFuel() > 0) {
                Column new_col = columns[t.getX() + 2];
                t.changeCol(new_col);
            }
        }

        if (event.getKeyCode() == UP) {
            t.changeDeg((float) 0.1);
        }

        if (event.getKeyCode() == DOWN) {
            t.changeDeg((float) -0.1);
        }

        if (event.getKeyCode() == W) {
            t.changePower((float) 1.2);
        }

        if (event.getKeyCode() == S) {
            t.changePower((float) -1.2);
        }

        if (event.getKeyCode() == BACKSPACE) {
            t.fire(wind.getWind());
            nextPlayer();
            wind.changeWind();
        }

        if (!endgame && event.getKeyCode() == R) {
            if (p.getScore() >= 20) {
                t.repair();
                p.changeScore(-20);
            }
        }

        if (event.getKeyCode() == F) {
            if (p.getScore() >= 10) {
                t.addFuel();
                p.changeScore(-10);
            }
        }

        if (event.getKeyCode() == P) {
            if (p.getScore() >= 15) {
                t.addParachute();
                p.changeScore(-15);
            }
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
        // currentPlayer = players.get(currentPlayer).getNextPlayer();
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
    public void nextPlayer() {
        if (playersLeft == 0) {
            return;
        }
        currentPlayerIdx = (currentPlayerIdx == playersTurn.length - 1) ? 0 : currentPlayerIdx + 1;
        currentPlayer = playersTurn[currentPlayerIdx];
        if (currentPlayer == '#' || tanks.get(currentPlayer) == null) {
            nextPlayer();
        }
    }

    private int findPlayerIdx(char key) {
        List<Character> list = Arrays.asList(playersTurn);
        return list.indexOf(key);
    }

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
                columns[i].addTree(trees[i]);
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

    private void drawTanks() {
        for (Map.Entry<Character, Tank> entry : tanks.entrySet()) {
            Character key = entry.getKey();
            Tank tank = entry.getValue();
            if (tank == null) {
                continue;
            }
            if (tank.getHealth() > 0) {
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

    private void drawWind() {
        wind.draw(this);
    }

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
