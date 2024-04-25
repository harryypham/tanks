package Tanks;

public class Bullet {
    private static float GRAVITY = 3.6f;
    private Tank tank;
    private int bulletIdx;
    private ExplodeAnimation explodeAnimation;
    private float x, y, deg;
    private float velocity = 1;
    private int wind;
    private final double xChange;
    private boolean moving = true;
    private boolean display = false;
    private int dummy = 12;

    public Bullet(Tank tank, int bulletIdx, float x, float y, float deg, float power, int wind) {
        this.tank = tank;
        this.bulletIdx = bulletIdx;
        this.x = (float) (x + 10 * Math.tan(deg));
        this.y = y - 10;
        this.deg = deg;
        this.wind = wind;
        this.velocity = power;
        this.xChange = this.velocity * Math.tan(deg);
    }

    public int calculateDistance(int x1, int x2, int y1, int y2) {
        return (int) Math.floor(Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)));
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
            if (columns[i].getTank() != null) {
                int tankDistFromExplosion = calculateDistance(i, x_origin, columns[i].getY(), y_origin);
                System.out.println(tankDistFromExplosion);
                columns[i].getTank().changeHealth(60 * tankDistFromExplosion / 30);
            }
        }

    }

    public void draw(App app) {
        if (this.moving) {
            y -= velocity;
            x += xChange;
            if (this.dummy > 0) {
                this.dummy -= 1;
            } else {
                x += wind * 0.03 / 30;
                velocity -= GRAVITY / 30;
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
            boolean finish = explodeAnimation.draw(app);
            if (finish) {
                tank.deleteBullet(bulletIdx);
            }
        }

        if (this.moving && this.y >= 640 - app.getColumns()[Math.round(this.x)].getY()) {
            this.moving = false;
            this.display = true;
            this.explodeAnimation = new ExplodeAnimation(this.x, this.y);
            explode(app.getColumns(), Math.round(this.x), app.getColumns()[Math.round(this.x)].getY());
        }
    }
}
