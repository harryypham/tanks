package Tanks;

public class Bullet extends GameComponent {
    /**
     * Class variable.
     */
    private static float GRAVITY = 3.6f;
    private static Column[] columns;

    // The tank that shot this bullet.
    private Tank tank;

    // Position of the bullet.
    private float x, y;

    // The index of this bullet in the array of bullets that the tank has shot.
    private int bulletIdx;

    // Color of the bullet (same as the color of the player).
    private int[] color;

    // Velocity
    private float xChange;
    private float yChange = 1;

    // Wind's value
    private int wind;

    // Whether bullet is still flying
    private boolean moving = true;

    // Whether bullet has hit terrain and explode
    private boolean explode = false;

    private ExplodeAnimation explodeAnimation;
    private int dummy = 12;

    /**
     * Set the terrain of the game.
     * 
     * @param cols Arrays of columns representing the terrain of the game.
     */
    public static void setCol(Column[] cols) {
        columns = cols;
    }

    /**
     * Constructor for a bullet.
     * 
     * @param tank      The tank that shot this bullet.
     * @param bulletIdx An integer representing the index of this bullet in the
     *                  array of bullets that the tank has shot.
     * @param x         A float representing the X-coordinate of the bullet.
     * @param y         A float representing the Y-coordinate of the bullet.
     * @param deg       A float representing the degree of the tank's turret.
     * @param power     A float representing the power of the tank.
     * @param wind      An integer representing the value of the wind at the time
     *                  the bullet is shot.
     * @param color     An integer array containing the color of the bullet.
     */
    public Bullet(Tank tank, int bulletIdx, float x, float y, float deg, float power, int wind,
            int[] color) {
        this.tank = tank;
        this.x = (float) (x + 10 * Math.tan(deg));
        this.y = y - 10;
        this.bulletIdx = bulletIdx;
        this.color = color;
        this.wind = wind;
        this.yChange = power;
        this.xChange = (float) (power * Math.tan(deg));
    }

    /**
     * Calculate the Euclidean distance between 2 points.
     * 
     * @param x1 An integer representing the x-coordinate of point 1.
     * @param x2 An integer representing the x-coordinate of point 2.
     * @param y1 An integer representing the y-coordinate of point 1.
     * @param y2 An integer representing the y-coordinate of point 2.
     * @return An integer representing the distance.
     */
    public static int calculateDistance(int x1, int x2, int y1, int y2) {
        return (int) Math.floor(Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)));
    }

    /**
     * Calculate how many pixels (terrain) are in the explosion given that a column
     * is diff_x from the explosion's center.
     * 
     * @param diff An integer representing the distance from the explosion's
     *             center.
     * @return An integer representing how many pixels loss.
     */
    public static int calculateHeightDiff(int diff) {
        return (int) Math.floor(Math.sqrt(Math.pow(30, 2) - Math.pow(diff, 2)));
    }

    /**
     * Change the height of a column and reduce tank's health if hit.
     * 
     * @param col           A column within the proximity of the explosion.
     * @param height_origin An integer representing the y-coordinate of the
     *                      explosion's center.
     * @param height_diff   An integer representing how many pixels that the column
     *                      suppose to lose.
     * @param t             The tank that shot the bullet.
     */
    public static void changeHeight(Column col, int height_origin, int height_diff, Tank t) {
        int val = 0;
        if (col.getY() >= height_origin + height_diff) {
            val = (int) Math.round(2 * height_diff);
        } else if (col.getY() >= height_origin) {
            val = (int) (height_diff + (col.getY() - height_origin));
        } else if (col.getY() > height_origin - height_diff) {
            val = (int) (height_diff - (height_origin - col.getY()));
        }

        col.decreseY(val);
        if (col.getTank() != null) {
            Tank t1 = col.getTank();
            if (t1.getParachutes() > 0) {
                t1.useParachutes();
            } else if (t1 != t) {
                t1.changeHealth(val, t);
            }
        }

    }

    /**
     * Calculate all the explosion damage.
     * 
     * @param x_origin An integer representing the x-coordinate of the explosion's
     *                 center.
     * @param y_origin An integer representing the y-coordinate of the explosion's
     *                 center.
     */
    public void explode(int x_origin, int y_origin) {
        for (int i = x_origin - 30; i <= x_origin + 30; i++) {
            if (i < 0 || i >= 864) {
                continue;
            }
            if (columns[i].getTank() != null) {
                int tankDistFromExplosion = calculateDistance(i, x_origin, columns[i].getY(), y_origin);
                if (tankDistFromExplosion < 30) {
                    if (columns[i].getTank() != this.tank) {
                        columns[i].getTank().changeHealth(60 * (30 - tankDistFromExplosion) / 30, this.tank);
                    } else {
                        columns[i].getTank().changeHealth(60 * (30 - tankDistFromExplosion) / 30, null);
                    }

                }
            }
            changeHeight(columns[i], y_origin, calculateHeightDiff(i - x_origin), this.tank);
        }

    }

    /**
     * Display the bullet on screen.
     * 
     * @param app The main application to draw on.
     */
    public void draw(App app) {
        if (this.moving) {
            x += xChange;
            y -= yChange;
            if (this.dummy > 0) {
                this.dummy -= 1;
            } else {
                // Change this to x when testing
                xChange += wind * 0.03 / 30;
                yChange -= GRAVITY / 30;
            }
        }
        if (this.x <= 0 || Math.round(this.x) >= 864 || this.y <= 0 || this.y >= 650) {
            this.moving = false;
        }
        if (this.moving) {
            app.fill(color[0], color[1], color[2]);
            app.ellipse(x, y, 7, 7);
        }

        if (this.explode) {
            boolean finish = explodeAnimation.draw(app);
            if (finish) {
                tank.deleteBullet(bulletIdx);
            }
        }

        if (this.moving && this.y >= 640 - columns[Math.round(this.x)].getY()) {
            this.moving = false;
            this.explode = true;
            this.explodeAnimation = new ExplodeAnimation(this.x, this.y);
            explode(Math.round(this.x), columns[Math.round(this.x)].getY());
        }
    }
}
