package Tanks;

import processing.core.PImage;

public class Tree {
    private PImage treeImg;
    private int x, y;

    public Tree(int x, int y, PImage treeImg) {
        this.x = x;
        this.y = y;
        this.treeImg = treeImg;
    }

    public void draw(App app) {
        app.image(treeImg, x - 16, 640 - y - 30, 32, 32);
    }

}
