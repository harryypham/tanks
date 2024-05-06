package Tanks;

import processing.core.PImage;

public class Tank extends GameComponent {
    // Class variables
    private static PImage parachuteImg;
    private static float BASE_WIDTH = 25;
    private static float BASE_HEIGHT = 5;
    private static float TURRET_WIDTH = 5;
    private static float TURRET_HEIGHT = 20;
    private static float BORDER_RADIUS = 10;

    // Column this tank belongs to.
    private Column col;

    // Tank's attributes
    private float turretAngle = 0;
    private int health;
    private int fuel;
    private int power;
    private int parachutes;
    private int[] color;

    // Whether the tank is descending with a parachute.
    private boolean useParachute;

    // Whether the tank is protected with a shield.
    private boolean shield;

    // An array storing the bullets the tank has fired
    private Bullet[] bullets = new Bullet[100];
    private int numBullets = 0;

    // Player this tank belongs to
    private Player player;

    // Whether tank has been destroyed.
    private ExplodeAnimation explodeAnimation;
    private boolean display;
    private boolean deleted;

    /**
     * Constructor for a tank.
     * 
     * @param col    Column the tank belongs to.
     * @param player Player the tank belongs to.
     */
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
        this.shield = false;
        this.deleted = false;
    }

    /**
     * Set the parachute's image.
     * 
     * @param img A Pimage.
     */
    public static void setParachuteImg(PImage img) {
        parachuteImg = img;
    }

    /**
     * Get the x-coordinate of the tank.
     * 
     * @return An integer representing the x-coordinate of the tank.
     */
    public int getX() {
        return this.col.getX();
    }

    /**
     * Get the y-coordinate of the tank.
     * 
     * @return An integer representing the y-coordinate of the tank.
     */
    public int getY() {
        return this.col.getY();
    }

    /**
     * Get the column that the tank belongs to.
     * 
     * @return A column.
     */
    public Column getCol() {
        return this.col;
    }

    /**
     * Get the player that the tank belongs to.
     * 
     * @return A player.
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Get how many fuel tank has left.
     *
     * @return An integer representing the amount of fuel left.
     */
    public int getFuel() {
        return this.fuel;
    }

    /**
     * Get the tank's health.
     * 
     * @return An integer representing the tank's health.
     */
    public int getHealth() {
        return this.health;
    }

    /**
     * Get the tank's power.
     * 
     * @return An integer representing the tank's power.
     */
    public int getPower() {
        return this.power;
    }

    /**
     * Get the number of parachutes the tank has left.
     * 
     * @return An integer representing how many parachutes the tank has left.
     */
    public int getParachutes() {
        return this.parachutes;
    }

    /**
     * Whether the tank get displayed.
     * 
     * @return A boolean indicating whether the tank is displayed.
     */
    public boolean getDisplay() {
        return this.display;
    }

    /**
     * Check if the tank has been deleted.
     * 
     * @return A boolean indicating whether the tank has been deleted.
     */
    public boolean checkDeleted() {
        return this.deleted;
    }

    /**
     * Use the parachute.
     */
    public void useParachutes() {
        this.parachutes -= 1;
        this.useParachute = true;
    }

    /**
     * Use the shield.
     */
    public void useShield() {
        this.shield = true;
    }

    /**
     * Change the column the tank belongs to.
     * 
     * @param c The updated column.
     */
    public void changeCol(Column c) {
        this.fuel -= 2;
        this.col.setTank(null);
        this.col = c;
        c.setTank(this);
        this.x = c.getX();
        this.y = 642 - c.getY();
    }

    /**
     * Change the turret degree.
     * 
     * @param deg A float representing the angle the turret should turn (clockwise).
     */
    public void changeDeg(float deg) {
        this.turretAngle += deg;
    }

    /**
     * Change the tank's power.
     * 
     * @param pow A float representing how many power to add.
     */
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

    /**
     * Change the tank's health.
     * 
     * @param health An integer representing how many health the tank lose.
     * @param t      The tank that cause the damage.
     */
    public void changeHealth(int health, Tank t) {
        if (this.shield) {
            this.shield = false;
            return;
        }
        int val = health;
        if (this.health < health) {
            val = this.health;
        }
        this.health -= val;
        if (t != null) {
            t.getPlayer().changeScore(val);
        }
        if (this.power > this.health) {
            this.power = this.health;
        }
    }

    /**
     * Repair the tank.
     */
    public void repair() {
        this.health += 20;
        if (this.health > 100) {
            this.health = 100;
        }
    }

    /**
     * Add fuel.
     */
    public void addFuel() {
        this.fuel += 200;
    }

    /**
     * Add parachute.
     */
    public void addParachute() {
        this.parachutes += 1;
    }

    /**
     * Fire a bullet.
     * 
     * @param wind An integer representing the value of wind.
     */
    public void fire(int wind) {
        Bullet bullet = new Bullet(this, numBullets, col.getX(), 642 - col.getY(), (float) turretAngle,
                (float) (power / 12.5 + 1),
                wind, color);
        bullets[numBullets] = bullet;
        numBullets += 1;
    }

    /**
     * Delete bullet that has hit terrain or fly outside of screen.
     * 
     * @param idx An integer representing the index of the bullet in the
     *            array.
     */
    public void deleteBullet(int idx) {
        bullets[idx] = null;
    }

    /**
     * Delete the tank from the game.
     */
    public void deleteTank() {
        this.col.setTank(null);
        this.player.setTank(null);
        this.explodeAnimation = new ExplodeAnimation(this.x, this.y);
        this.display = true;
        this.deleted = true;
    }

    /**
     * Display the tank on scren.
     * 
     * @param app The main application to draw on.
     */
    public void draw(App app) {
        if (this.y <= 2) {
            deleteTank();
        }
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
        app.fill(color[0], color[1], color[2]);
        app.rect(x, y - (float) (BASE_HEIGHT * 0.5), BASE_WIDTH, BASE_HEIGHT,
                BORDER_RADIUS);
        app.rect(x, y - (float) (BASE_HEIGHT * 1.5) + 1, (float) (BASE_WIDTH * 0.7),
                BASE_HEIGHT,
                BORDER_RADIUS);

        if (this.shield) {
            app.fill(0, 0);
            app.stroke(color[0], color[1], color[2]);
            app.ellipse(x, y - (float) (BASE_HEIGHT * 1.8), 35, 35);
        }

        app.noStroke();
        app.rectMode(0);
    }
}
