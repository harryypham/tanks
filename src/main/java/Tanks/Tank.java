package Tanks;

import java.util.*;

public class Tank {
    private int x, y;
    private int turretAngle;
    private char player;
    private int[] color;
    private static float BASE_WIDTH = 25;
    private static float BASE_HEIGHT = 5;
    private static float TURRET_WIDTH = 5;
    private static float TURRET_HEIGHT = 25;
    private static float BORDER_RADIUS = 10;

    private Random rand = new Random();

    public Tank(char player, int[] colors) {
        this.player = player;
        this.color = colors.clone();
        x = rand.nextInt(864);
        y = rand.nextInt(640);
    }

    public void draw(App app) {
        app.rectMode(3);

        // Turret
        app.fill(0, 0, 0);
        app.rect(this.x, this.y - (float) (TURRET_HEIGHT * 0.5), TURRET_WIDTH, TURRET_HEIGHT, BORDER_RADIUS);

        // Base
        app.fill(color[0], color[1], color[2]);
        app.rect(this.x, this.y - (float) (BASE_HEIGHT * 0.5), BASE_WIDTH, BASE_HEIGHT, BORDER_RADIUS);
        app.rect(this.x, this.y - (float) (BASE_HEIGHT * 1.5) + 1, (float) (BASE_WIDTH * 0.7), BASE_HEIGHT,
                BORDER_RADIUS);

        app.rectMode(0);
    }
}
