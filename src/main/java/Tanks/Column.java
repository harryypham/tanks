package Tanks;

public class Column extends GameComponent {
    /**
     * X-coordinate and height of the column.
     */
    private int x, y;
    /**
     * Tank located on top of this column.
     */
    private Tank tank;

    /**
     * Constructor for a column.
     * 
     * @param x X-coordinate of the column.
     * @param y Height of the column.
     */
    public Column(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get the x-coordinate of the column.
     * 
     * @return An integer representing the x-coordinate of the column.
     */
    public int getX() {
        return this.x;
    }

    /**
     * Get the height of the column.
     * 
     * @return An integer representing the height of the column.
     */
    public int getY() {
        return this.y;
    }

    /**
     * Decrease the height of the column.
     * 
     * @param val An integer representing the number of pixels loss.
     */
    public void decreseY(int val) {
        this.y -= val;
    }

    /**
     * Get the tank of the column.
     * 
     * @return The tank located on top of the column.
     */
    public Tank getTank() {
        return this.tank;
    }

    /**
     * Set the tank for the column.
     * 
     * @param tank A tank located on this column.
     */
    public void setTank(Tank tank) {
        if (tank != null && this.tank != null) {
            return;
        }
        this.tank = tank;
    }

    /**
     * Display the column on screen.
     * 
     * @param app The main application to draw on.
     */
    public void draw(App app) {
        app.rect(x, 640 - y, 1, y);
    }
}
