package Tanks;

import processing.core.PShape;

public class Tank {
    private Column col;
    private static final float PI = 3.1415927410125732f;
    private float turretAngle = 0;
    private Player player;
    private int[] color;
    private static float BASE_WIDTH = 25;
    private static float BASE_HEIGHT = 5;
    private static float TURRET_WIDTH = 5;
    private static float TURRET_HEIGHT = 20;
    private static float BORDER_RADIUS = 10;

    public Tank(Column col, Player player) {
        this.player = player;
        this.color = player.getColor();
        this.col = col;
    }

    public int getX() {
        return this.col.getX();
    }

    public int getY() {
        return this.col.getY();
    }

    public Column getCol() {
        return this.col;
    }

    public void changeCol(Column col) {
        this.col.setTank(null);
        this.col = col;
    }

    public void changeDeg(int deg) {
        this.turretAngle += deg;
    }

    public void draw(App app) {
        int x = col.getX();
        int y = 642 - col.getY();
        app.rectMode(3);

        // Turret
        app.fill(0, 0, 0);
        app.pushMatrix();
        app.translate(x, y);
        app.rotate(app.radians(this.turretAngle));
        app.rect(0, -TURRET_HEIGHT / 2 - 3, TURRET_WIDTH, TURRET_HEIGHT, BORDER_RADIUS);
        app.popMatrix();

        // PShape square = app.createShape(30, 0, 0, TURRET_WIDTH, TURRET_HEIGHT,
        // BORDER_RADIUS);
        // square.rotate(app.radians(this.turretAngle));
        // square.translate(-TURRET_WIDTH / 2, -TURRET_HEIGHT / 2);
        // app.shape(square, x + TURRET_WIDTH / 2, y - (float) (TURRET_HEIGHT * 0.5) +
        // TURRET_HEIGHT / 2);

        // app.fill(0, 0, 0);
        // app.rect(x, y - (float) (TURRET_HEIGHT * 0.5), TURRET_WIDTH, TURRET_HEIGHT,
        // BORDER_RADIUS);

        // Base
        app.rectMode(3);
        app.fill(color[0], color[1], color[2]);
        app.rect(x, y - (float) (BASE_HEIGHT * 0.5), BASE_WIDTH, BASE_HEIGHT,
                BORDER_RADIUS);
        app.rect(x, y - (float) (BASE_HEIGHT * 1.5) + 1, (float) (BASE_WIDTH * 0.7),
                BASE_HEIGHT,
                BORDER_RADIUS);

        app.rectMode(0);
    }
}
