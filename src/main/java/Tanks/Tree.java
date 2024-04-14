package Tanks;

import processing.core.PImage;

public class Tree {
    private static PImage treeImg;
    private Column col;

    public Tree(Column col) {
        this.col = col;
    }

    public static void setImg(PImage defaultImg) {
        treeImg = defaultImg;
    }

    public void draw(App app) {
        app.image(treeImg, col.getX() - 16, 640 - col.getY() - 30, 32, 32);
    }

}
