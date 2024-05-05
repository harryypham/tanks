package Tanks;

public class Player implements Comparable<Player> {
    /**
     * Name of the player. Ex: A, B, etc
     */
    private char player;

    /**
     * Player's color
     */
    private int[] color;

    /**
     * Player's score
     */
    private int score;

    /**
     * Player's tank
     */
    private Tank tank;

    /**
     * Constructor for a player.
     * 
     * @param player A character representing the name of the player (letter).
     * @param color  An integer array of length 3 (RGB) specifying the color of the
     *               player.
     */
    public Player(char player, int[] color) {
        this.color = color;
        this.player = player;
        this.score = 0;
    }

    /**
     * Get the name of the player.
     * 
     * @return A character representing the name of the player.
     */
    public char getChar() {
        return this.player;
    }

    /**
     * Get the color of the player.
     * 
     * @return An integer array of length 3 containing the color of the player.
     */
    public int[] getColor() {
        return this.color;
    }

    /**
     * Get the score of the player.
     * 
     * @return An integer representing the score of the player.
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Modify the score of the player.
     * 
     * @param val An integer representing how many score the player gains/loses.
     */
    public void changeScore(int val) {
        this.score += val;
    }

    /**
     * Set tank of the player.
     * 
     * @param tank The tank of the player.
     */
    public void setTank(Tank tank) {
        this.tank = tank;
    }

    /**
     * Get the tank of the player
     * 
     * @return The tank of the player
     */
    public Tank getTank() {
        return this.tank;
    }

    /**
     * Compares this player with the specified player for order.
     * Returns a negative integer, zero, or a positive integer as this player's
     * score is less than, equal to, or greater than the specified player's score.
     */
    @Override
    public int compareTo(Player p) {
        return Integer.compare(p.getScore(), this.score);
    }
}
