package Tanks;

public class Column {
    private int x, y;
    private Tank tank;

    public Column(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void decreseY(int val) {
        this.y -= val;
    }

    public Tank getTank() {
        return this.tank;
    }

    public void setTank(Tank tank) {
        if (tank != null && this.tank != null) {
            return;
        }
        this.tank = tank;
    }

    public void draw(App app) {
        app.rect(x, 640 - y, 1, y);
    }
}
