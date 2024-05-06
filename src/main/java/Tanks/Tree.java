package Tanks;

import processing.core.PImage;

public class Tree extends GameComponent {
    /**
     * Tree image
     */
    private static PImage treeImg;

    /**
     * Column the tree belongs to.
     */
    private Column col;

    /**
     * Constructor for a tree.
     * 
     * @param col Column that the tree belongs to.
     */
    public Tree(Column col) {
        this.col = col;
        this.x = col.getX();
        this.y = 640 - col.getY();
    }

    /**
     * Set tree image when initialize the game.
     * 
     * @param defaultImg An image representing the tree.
     */
    public static void setImg(PImage defaultImg) {
        treeImg = defaultImg;
    }

    /**
     * Display the tree on screen.
     * 
     * @param app The main application to draw on.
     */
    public void draw(App app) {
        this.y = 640 - col.getY();
        app.image(treeImg, x - 16, y - 30, 32, 32);
    }

}
