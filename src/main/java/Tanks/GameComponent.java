package Tanks;

public abstract class GameComponent {
    /**
     * Position of item
     */
    int x, y;

    /**
     * Display item on screen.
     * 
     * @param app The main application to draw on.
     */
    abstract void draw(App app);
}
