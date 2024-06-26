package Tanks;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
import processing.event.KeyEvent;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// IMPORTANT !!!: Change how the wind affect the bullets in Bullet.java so the game ends faster. Line 172.
public class MainTest {

    /**
     * Test ExplodeAnimation class.
     */
    @Test
    public void testExplodeAnimation() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        ExplodeAnimation e = new ExplodeAnimation(432, 320);
        app.setup();
        for (int i = 0; i < App.FPS * 1; i++) {
            e.draw(app);
        }
    }

    /**
     * Test Player class.
     */
    @Test
    public void testPlayer() {
        int[] color = { 0, 0, 0 };
        Player a = new Player('A', color);
        assertEquals(a.getChar(), 'A');
        a.changeScore(20);
        assertEquals(a.getScore(), 20);
        assertEquals(a.getTank(), null);

        Player b = new Player('B', color);
        b.changeScore(30);
        assertEquals(a.compareTo(b), 1);
    }

    /**
     * Test Column class.
     */
    @Test
    public void testColumn() {
        Column col = new Column(200, 300);
        assertEquals(col.getX(), 200);
        assertEquals(col.getY(), 300);
        col.decreseY(50);
        assertEquals(col.getY(), 250);

        int[] color = { 0, 0, 0 };
        Player a = new Player('A', color);
        Tank t = new Tank(col, a);
        col.setTank(t);
        assertEquals(col.getTank(), t);

        Tank t1 = new Tank(col, a);
        col.setTank(t1);
        assertEquals(col.getTank(), t);
    }

    /**
     * Test Tree class.
     */
    @Test
    public void testTree() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        JSONObject json = app.loadJSONObject(app.configPath);
        Config conf = new Config(json, app, 0);
        PImage img = conf.getTreeImage();
        Column c = new Column(400, 200);
        Tree t = new Tree(c);
        Tree.setImg(img);
        t.draw(app);
        // No testable method.
    }

    /**
     * Test Wind class.
     */
    @Test
    public void testWind() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        JSONObject json = app.loadJSONObject(app.configPath);
        Config c = new Config(json, app, 0);
        PImage img1 = c.getPosWindImg();
        PImage img2 = c.getNegWindImg();

        Wind w = new Wind();
        Wind.setWindImg(img1, img2);
        w.changeWind();
        assertTrue(w.getWind() <= 35);
        assertTrue(w.getWind() >= -35);
        w.draw(app);
    }

    /**
     * Test Tank class.
     */
    @Test
    public void testTank() {
        Column col = new Column(200, 300);
        Column col1 = new Column(222, 333);

        int[] color = { 0, 0, 0 };
        Player a = new Player('A', color);
        Player b = new Player('B', color);
        Tank t = new Tank(col, a);
        Tank t1 = new Tank(col1, b);

        // Test getters
        assertEquals(t.getCol(), col);
        assertEquals(t.getX(), 200);
        assertEquals(t.getY(), 300);
        assertEquals(t.getPlayer(), a);
        assertEquals(t.getFuel(), 250);

        // Test parachute-related methods.
        assertEquals(t.getParachutes(), 3);
        t.useParachutes();
        assertEquals(t.getParachutes(), 2);
        t.addParachute();
        assertEquals(t.getParachutes(), 3);

        // Test power-related methods.
        assertEquals(t.getPower(), 50);
        t.changePower(15);
        assertEquals(t.getPower(), 65);
        t.changePower(200);
        assertEquals(t.getPower(), 100);
        t.changePower(-200);
        assertEquals(t.getPower(), 0);

        // Test health-related methods.
        assertEquals(t.getHealth(), 100);
        t.changeHealth(30, t1);
        assertEquals(t.getHealth(), 70);
        assertEquals(t1.getPlayer().getScore(), 30);
        t.changePower(75);
        assertEquals(t.getPower(), 70);
        t.changeHealth(120, t1);
        assertEquals(t.getHealth(), 0);

        // Test repair.
        t1.repair();
        assertEquals(t1.getHealth(), 100);

        // Test adding fuel.
        t1.addFuel();
        assertEquals(t1.getFuel(), 450);

        // Test using shield.
        t1.useShield();

        // Test tank's display/existence.
        assertEquals(t.checkDeleted(), false);
        t.deleteTank();
        assertEquals(t.checkDeleted(), true);
        assertEquals(t.getDisplay(), true);

        // Test firing method.
        t.fire(20);
        t.deleteBullet(0);
    }

    /**
     * Test Bullet class.
     */
    @Test
    public void testBullet() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        Column col = new Column(200, 300);

        int[] color = { 0, 0, 0 };
        Player a = new Player('A', color);
        Tank t = new Tank(col, a);

        // Test class methods.
        int d = Bullet.calculateDistance(5, 2, 4, 0);
        assertEquals(d, 5);

        d = Bullet.calculateHeightDiff(28);
        assertEquals(d, 10);

        Bullet.changeHeight(col, 200, 180, t);

        // Random bullet.
        Bullet b = new Bullet(t, 0, -10, 30, 1, 50, 20, color);

        // Test instance methods.
        // Explode out of screen.
        b.explode(10, 50);
        b.explode(900, 50);

        // Draw bullet.
        b.draw(app);
    }

    /**
     * E2E Testing. Shooting hundreds of bullets to test changing levels and end
     * game conditions.
     */
    @Test
    public void testEndToEnd() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000);
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 37));
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 38));
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 39));
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 40));
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 87));
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 83));
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 70));
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 82));
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 80));
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 72));

        app.noLoop();
        for (int i = 0; i < App.FPS * 60; i++) {
            if (i % App.FPS == 0) {
                app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));
            }
            app.draw();
        }

        // Test restart the game.
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 82));

    }
}
