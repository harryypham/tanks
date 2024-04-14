package Tanks;

public class Tank {
    private Column col;
    private int turretAngle;
    private Player player;
    private int[] color;
    private static float BASE_WIDTH = 25;
    private static float BASE_HEIGHT = 5;
    private static float TURRET_WIDTH = 5;
    private static float TURRET_HEIGHT = 25;
    private static float BORDER_RADIUS = 10;

    public Tank(Column col, Player player) {
        this.player = player;
        this.color = player.getColor();
        this.col = col;
    }

    public void draw(App app) {
        int x = col.getX();
        int y = 642 - col.getY();
        app.rectMode(3);

        // Turret
        app.fill(0, 0, 0);
        app.rect(x, y - (float) (TURRET_HEIGHT * 0.5), TURRET_WIDTH, TURRET_HEIGHT, BORDER_RADIUS);

        // Base
        app.fill(color[0], color[1], color[2]);
        app.rect(x, y - (float) (BASE_HEIGHT * 0.5), BASE_WIDTH, BASE_HEIGHT, BORDER_RADIUS);
        app.rect(x, y - (float) (BASE_HEIGHT * 1.5) + 1, (float) (BASE_WIDTH * 0.7), BASE_HEIGHT,
                BORDER_RADIUS);

        app.rectMode(0);
    }
}
