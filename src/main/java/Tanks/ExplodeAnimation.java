package Tanks;

public class ExplodeAnimation {
    /**
     * Center of the explosion
     */
    private float x, y;

    /**
     * Radius of the explosion
     */
    private int r1, r2, r3;

    /**
     * Constructor for an explosion
     * 
     * @param x A float representing the x-coordinate of the center of the
     *          explosion.
     * @param y A float representing the y-coordinate of the center of the
     *          explosion.
     */
    public ExplodeAnimation(float x, float y) {
        this.x = x;
        this.y = y;
        this.r1 = 0;
        this.r2 = 0;
        this.r3 = 0;
    }

    /**
     * Display the explosion on screen. Return true if the explosion has finished,
     * otherwise false.
     * 
     * @param app The main application to draw on.
     * @return A boolean specifying whether the explosion animation has finished.
     */
    public boolean draw(App app) {
        r1 += 5;
        r2 += 2.5;
        r3 += 1;
        if (r1 > 30) {
            return true;
        }
        app.fill(255, 0, 0);
        app.ellipse(this.x, this.y, r1 * 2, r1 * 2);
        app.fill(255, 127, 80);
        app.ellipse(this.x, this.y, r2 * 2, r2 * 2);
        app.fill(255, 215, 0);
        app.ellipse(this.x, this.y, r3 * 2, r3 * 2);
        return false;
    }
}
