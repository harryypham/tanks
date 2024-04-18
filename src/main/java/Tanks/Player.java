package Tanks;

public class Player {
    char player;
    char next;
    int[] color;
    int score;
    Tank tank;

    public Player(char player, int[] color) {
        this.color = color;
        this.player = player;
        this.score = 0;
    }

    public char getNextPlayer() {
        return this.next;
    }

    public void setNextPlayer(char next) {
        this.next = next;
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

    public void addTank(Tank tank) {
        this.tank = tank;
    }

    public Tank getTank() {
        return this.tank;
    }
}
