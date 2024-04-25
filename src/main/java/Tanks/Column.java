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

    public void decreseY(int val) {
        this.y -= val;
        if (this.tank != null) {
            if (this.tank.getParachutes() > 0) {
                this.tank.useParachutes();
            } else {
                this.tank.changeHealth(val);
            }
        }
    }

    public int getY() {
        return this.y;
    }

    public void addTree(Tree tree) {
        this.tree = tree;
    }

    public Tank getTank() {
        return this.tank;
    }

    public void setTank(Tank tank) {
        this.tank = tank;
    }

    public void draw(App app) {
        app.rect(x, 640 - y, 1, y);
    }
}
