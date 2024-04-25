package Tanks;

import java.util.ArrayList;

import processing.core.PImage;
import processing.core.PShape;

public class Tank {
    private Column col;
    private static final float PI = 3.1415927410125732f;
    private static PImage fuelImg;
    private float turretAngle = 0;
    private int turretPower = 1;
    private int health;
    private int fuel;
    private int power;
    private Bullet[] bullets = new Bullet[100];
    private int numBullets = 0;
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
        this.health = 100;
        this.fuel = 250;
        this.power = 50;
    }

    public static void setFuelImg(PImage img) {
        fuelImg = img;
    }

    public int getX() {
        return this.col.getX();
    }

    public int getY() {
        return this.col.getY();
    }

    public void deleteBullet(int bulletIdx) {
        bullets[bulletIdx] = null;
    }

    public Column getCol() {
        return this.col;
    }

    public int getFuel() {
        return this.fuel;
    }

    public int getHealth() {
        return this.health;
    }

    public int getPower() {
        return this.power;
    }

    public void changeCol(Column col) {
        this.fuel -= 2;
        this.col.setTank(null);
        this.col = col;
    }

    public void changeDeg(float deg) {
        this.turretAngle += deg;
    }

    public void changePower(float pow) {
        this.power += pow;
        if (this.power > 100) {
            this.power = 100;
        }
        if (this.power < 0) {
            this.power = 0;
        }
        if (this.power > this.health) {
            this.power = this.health;
        }
    }

    public void changeHealth(int health) {
        this.health -= health;
        if (this.health < 0) {
            this.health = 0;
        }
        if (this.power > this.health) {
            this.power = this.health;
        }
    }

    public void fire(int wind) {
        Bullet bullet = new Bullet(this, numBullets, col.getX(), 642 - col.getY(), (float) turretAngle,
                (float) (power / 12.5 + 1),
                wind);
        bullets[numBullets] = bullet;
        numBullets += 1;
    }

    public void draw(App app) {
        for (Bullet bullet : bullets) {
            if (bullet != null) {
                bullet.draw(app);
            }
        }
        int x = col.getX();
        int y = 642 - col.getY();
        app.rectMode(3);

        // Fuel
        // app.image(fuelImg, 150, 10, 25, 25);
        // app.fill(0, 0, 0);
        // app.textSize(16);
        // app.textAlign(39);
        // app.text(this.fuel, 150 + 60, 30);

        // Turret
        app.fill(0, 0, 0);
        app.pushMatrix();
        app.translate(x, y);
        app.rotate(this.turretAngle);
        app.rect(0, -TURRET_HEIGHT / 2 - 5, TURRET_WIDTH, TURRET_HEIGHT - 3, BORDER_RADIUS);
        app.popMatrix();

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
