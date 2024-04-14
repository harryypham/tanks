package Tanks;

public class Column {
    private int x, y;
    private Tree tree;

    public Column(int x, int y) {
        this.x = x;
        this.y = y; // height
    }

    public void addTree(Tree tree) {
        this.tree = tree;
    }

    public void draw(App app) {
        app.rect(x, 640 - y, 1, y);
    }
}
