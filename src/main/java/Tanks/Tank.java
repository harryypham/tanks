package Tanks;

import java.util.ArrayList;

import processing.core.PImage;
import processing.core.PShape;

public class Tank {
    private Column col;
    private int x, y;
    private static final float PI = 3.1415927410125732f;
    private float turretAngle = 0;
    private int turretPower = 1;
    private int health;
    private int fuel;
    private int power;
    private int parachutes;
    private boolean useParachute;
    private Bullet[] bullets = new Bullet[100];
    private int numBullets = 0;
    private Player player;
    private int[] color;
    private ExplodeAnimation explodeAnimation;
    private boolean display;
    private boolean deleted = false;
    private static PImage parachuteImg;
    private static float BASE_WIDTH = 25;
    private static float BASE_HEIGHT = 5;
    private static float TURRET_WIDTH = 5;
    private static float TURRET_HEIGHT = 20;
    private static float BORDER_RADIUS = 10;

    public Tank(Column col, Player player) {
        this.player = player;
        this.color = player.getColor();
        this.col = col;
        this.x = col.getX();
        this.y = 642 - col.getY();
        this.health = 100;
        this.fuel = 250;
        this.power = 50;
        this.parachutes = 3;
    }

    public static void setParachuteImg(PImage img) {
        parachuteImg = img;
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

    public Player getPlayer() {
        return this.player;
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

    public int getParachutes() {
        return this.parachutes;
    }

    public boolean getDisplay() {
        return this.display;
    }

    public boolean checkDeleted() {
        return this.deleted;
    }

    public void useParachutes() {
        this.parachutes -= 1;
        this.useParachute = true;
    }

    public void changeCol(Column c) {
        this.fuel -= 2;
        this.col.setTank(null);
        this.col = c;
        c.setTank(this);
        this.x = c.getX();
        this.y = 642 - c.getY();
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

    public void changeHealth(int health, Tank t) {
        int val = health;
        if (this.health < health) {
            val = this.health;
        }
        this.health -= val;
        if (t != null) {
            t.getPlayer().score += val;
        }
        if (this.power > this.health) {
            this.power = this.health;
        }
    }

    public void fire(int wind) {
        Bullet bullet = new Bullet(this, numBullets, col.getX(), 642 - col.getY(), (float) turretAngle,
                (float) (power / 12.5 + 1),
                wind, color);
        bullets[numBullets] = bullet;
        numBullets += 1;
    }

    public void deleteTankFromGame() {
        this.col.setTank(null);
        this.player.setTank(null);
        this.explodeAnimation = new ExplodeAnimation(this.x, this.y);
        this.display = true;
        this.deleted = true;
    }

    public void draw(App app) {
        for (Bullet bullet : bullets) {
            if (bullet != null) {
                bullet.draw(app);
            }
        }
        if (this.useParachute) {
            if (this.y < 642 - col.getY()) {
                this.y += 2;
            } else {
                useParachute = false;
            }
        } else {
            this.y = 642 - col.getY();
        }

        if (this.display) {
            boolean finish = explodeAnimation.draw(app);
            if (finish) {
                this.display = false;
            }
        }

        app.rectMode(3);

        // Parachute (if use)
        if (useParachute) {
            app.image(parachuteImg, x - 32, y - 64, 64, 64);
        }

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
