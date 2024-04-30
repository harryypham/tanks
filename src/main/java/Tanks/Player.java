package Tanks;

public class Player implements Comparable<Player> {
    private char player;
    private int[] color;
    private int score;
    private Tank tank;

    public Player(char player, int[] color) {
        this.color = color;
        this.player = player;
        this.score = 0;
    }

    public char getChar() {
        return this.player;
    }

    public int[] getColor() {
        return this.color;
    }

    public int getScore() {
        return this.score;
    }

    public void changeScore(int val) {
        this.score += val;
    }

    public void setTank(Tank tank) {
        this.tank = tank;
    }

    public Tank getTank() {
        return this.tank;
    }

    @Override
    public int compareTo(Player p) {
        return Integer.compare(p.getScore(), this.score);
    }
}
