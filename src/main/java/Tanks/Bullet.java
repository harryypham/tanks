package Tanks;

public class Bullet {
    private static float GRAVITY = 3.6f;
    private Tank tank;
    private float x, y, deg;
    private float velocity = 6;
    private int wind;
    private final double xChange;
    private boolean moving = true;
    private boolean display = false;
    private int dummy = 5;

    private float r1, r2, r3;

    public Bullet(Tank tank, float x, float y, float deg, int wind) {
        this.tank = tank;
        this.x = (float) (x + 10 * Math.tan(deg));
        this.y = y - 10;
        this.deg = deg;
        this.wind = wind;
        this.xChange = this.velocity * Math.tan(deg);
        System.out.println(wind);
    }

    public int calculateHeightDiff(int diff_x) {
        return (int) Math.floor(Math.sqrt(Math.pow(30, 2) - Math.pow(diff_x, 2)));
    }

    public void calculateHeightLoss(Column col, int height_origin, int height_diff) {
        if (col.getY() >= height_origin + height_diff) {
            col.decreseY((int) Math.round(2 * height_diff));
        } else if (col.getY() >= height_origin) {
            col.decreseY((int) (height_diff + (col.getY() - height_origin)));
        } else if (col.getY() > height_origin - height_diff) {
            col.decreseY((int) (height_diff - (height_origin - col.getY())));
        }
    }

    public void explode(Column[] columns, int x_origin, int y_origin) {
        for (int i = x_origin - 30; i <= x_origin + 30; i++) {
            if (i < 0 || i >= 864) {
                continue;
            }
            calculateHeightLoss(columns[i], y_origin, calculateHeightDiff(i - x_origin));
        }

    }

    public void draw(App app) {
        if (this.moving) {
            y -= velocity;
            x += xChange;
            if (this.dummy > 0) {
                this.dummy -= 1;
            } else {
                x += wind * 0.03;
            }
        }
        if (this.x < 0 || this.x >= 864 || this.y < 0 || this.y >= 640) {
            this.moving = false;
            this.display = false;
        }
        if (this.moving) {
            app.fill(0, 0, 0);
            app.ellipse(x, y, 7, 7);
        }

        if (this.display) {
            r1 += 5;
            r2 += 2.5;
            r3 += 1;
            if (r1 > 30) {
                tank.deleteBullet();
            }
            app.fill(255, 0, 0);
            app.ellipse(this.x, this.y, r1 * 2, r1 * 2);
            app.fill(255, 127, 80);
            app.ellipse(this.x, this.y, r2 * 2, r2 * 2);
            app.fill(255, 215, 0);
            app.ellipse(this.x, this.y, r3 * 2, r3 * 2);
        }

        velocity -= 0.12;
        if (this.moving && this.y >= 640 - app.getColumns()[Math.round(this.x)].getY()) {
            System.out.println(Math.round(this.x));
            System.out.println(app.getColumns()[Math.round(this.x)].getY());
            this.moving = false;
            this.display = true;
            r1 = 0;
            r2 = 0;
            r3 = 0;
            explode(app.getColumns(), Math.round(this.x), app.getColumns()[Math.round(this.x)].getY());
        }
    }
}
