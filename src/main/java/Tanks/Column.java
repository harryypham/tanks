package Tanks;

public class Column {
    private int x, y;
    private Tree tree;
    private Tank tank;

    public Column(int x, int y) {
        this.x = x;
        this.y = y; // height
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void addTree(Tree tree) {
        this.tree = tree;
    }

    public void setTank(Tank tank) {
        this.tank = tank;
    }

    public void draw(App app) {
        app.rect(x, 640 - y, 1, y);
    }
}
