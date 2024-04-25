package Tanks;

public class ExplodeAnimation {
    private float x, y;
    private int r1, r2, r3;

    public ExplodeAnimation(float x, float y) {
        this.x = x;
        this.y = y;
        this.r1 = 0;
        this.r2 = 0;
        this.r3 = 0;
    }

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
