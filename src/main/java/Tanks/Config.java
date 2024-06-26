package Tanks;

import java.io.*;
import java.util.*;

import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;

public class Config {
    /**
     * JSONObject extracted from the config file.
     */
    private JSONObject json;

    /**
     * JSONObject containing the settings for levels.
     */
    private JSONObject[] levels;

    /**
     * JSONObject containing the settings of the player.s
     */
    private JSONObject playerObject;

    private int currentLevel;
    private App app;

    /**
     * Constructor.
     * 
     * @param json  JSONObject extracted from the config file.
     * @param app   The main application.
     * @param level An integer representing the current level.
     */
    public Config(JSONObject json, App app, int level) {
        this.json = json;
        this.currentLevel = level;
        this.app = app;
        initLevels();
        initPlayerColors();
    }

    /**
     * Initialize the levels. Load the levels into an array of JSONObject.
     */
    private void initLevels() {
        JSONArray tempArr = json.getJSONArray("levels");
        this.levels = new JSONObject[tempArr.size()];
        for (int i = 0; i < tempArr.size(); i++) {
            this.levels[i] = (JSONObject) tempArr.get(i);
        }
    }

    /**
     * Initialize the players.
     */
    private void initPlayerColors() {
        this.playerObject = json.getJSONObject("player_colours");
    }

    /**
     * Retrieve the number of levels in the game.
     * 
     * @return An integer representing how many levels the game has.
     */
    public int getNumLevels() {
        return this.levels.length;
    }

    /**
     * Get the layout file for the current level.
     * 
     * @return A File containing the terrain, trees and tanks of the current level.
     */
    public File getLayoutFile() {
        String filename = this.levels[this.currentLevel].getString("layout");
        File file = new File(filename);
        return file;
    }

    /**
     * Get the background image.
     * 
     * @return A PImage.
     */
    public PImage getBackground() {
        String backgroundImgPath = "build/resources/main/Tanks/"
                + this.levels[this.currentLevel].getString("background");
        PImage backgroundImg = app.loadImage(backgroundImgPath);
        return backgroundImg;
    }

    /**
     * Get the foreground color.
     * 
     * @return An integer array of length 3 representing the foreground color.
     */
    public int[] getForegroundColor() {
        String temp = this.levels[this.currentLevel].getString("foreground-colour");
        String[] tempColors = temp.split(",", -1);
        int[] colors = { Integer.parseInt(tempColors[0]), Integer.parseInt(tempColors[1]),
                Integer.parseInt(tempColors[2]) };
        return colors;
    }

    /**
     * Get the parachute's image.
     * 
     * @return A PImage.
     */
    public PImage getParachuteImage() {
        String imgPath = "build/resources/main/Tanks/parachute.png";
        PImage parachuteImg = app.loadImage(imgPath);
        return parachuteImg;
    }

    /**
     * Get the fuel's image.
     * 
     * @return A PImage.
     */
    public PImage getFuelImage() {
        String fuelImgPath = "build/resources/main/Tanks/fuel.png";
        PImage fuelImg = app.loadImage(fuelImgPath);
        return fuelImg;
    }

    /**
     * Get the trees' image.
     * 
     * @return A PImage.
     */
    public PImage getTreeImage() {
        String treeImgPath;
        if (this.levels[this.currentLevel].getString("trees") == null) {
            treeImgPath = "tree1.png";
        } else {
            treeImgPath = this.levels[this.currentLevel].getString("trees");
        }
        treeImgPath = "build/resources/main/Tanks/" + treeImgPath;
        PImage treeImg = app.loadImage(treeImgPath);
        return treeImg;
    }

    /**
     * Get the positive wind's image.
     * 
     * @return A PImage.
     */
    public PImage getPosWindImg() {
        String windImgPath = "build/resources/main/Tanks/wind.png";
        PImage windImg = app.loadImage(windImgPath);
        return windImg;
    }

    /**
     * Get the negative wind's image.
     * 
     * @return A PImage.
     */
    public PImage getNegWindImg() {
        String windImgPath = "build/resources/main/Tanks/wind-1.png";
        PImage windImg = app.loadImage(windImgPath);
        return windImg;
    }

    /**
     * Initialize the players' color.
     * 
     * @return A map mapping the player's name to the player's color.
     */
    public Map<Character, int[]> getPlayerColors() {
        Map<Character, int[]> playerColor = new HashMap<Character, int[]>();
        Iterator<?> iter = playerObject.keys().iterator();
        String currKey;
        int[] colors = new int[3];
        Random rand = new Random();
        while (iter.hasNext()) {
            currKey = (String) iter.next();

            String[] temp = playerObject.getString(currKey).split(",", -1);
            if (temp[0].equals("random")) {
                for (int i = 0; i < temp.length; i++) {
                    colors[i] = rand.nextInt(256);
                }
            } else {
                for (int i = 0; i < temp.length; i++) {
                    colors[i] = Integer.parseInt(temp[i]);
                }
            }
            playerColor.put(currKey.charAt(0), colors.clone());
        }
        return playerColor;
    }

    /**
     * Load layout (terrain, tanks, trees) from the layout file.
     * 
     * @return An object containing the layout of the columns, trees and tanks.
     */
    public Object[] loadLayout() {
        File file = getLayoutFile();
        Object[] re = new Object[3];
        int[] pixels = new int[896];
        int[] trees = new int[896];
        char[] tanks = new char[896];

        int lineIdx = 0;
        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                lineIdx += 1;
                if (line.length() == 0) {
                    continue;
                }
                for (int c = 0; c < line.length(); c++) {
                    if (line.charAt(c) == 'X') {
                        for (int i = 0; i < 32; i++) {
                            pixels[c * 32 + i] = 640 - (lineIdx - 1) * 32;
                        }
                        continue;
                    }
                    if (line.charAt(c) == 'T') {
                        trees[c * 32] = 1;
                        continue;
                    }
                    if (Character.isLetter(line.charAt(c))) {
                        tanks[c * 32] = line.charAt(c);
                    }
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }

        // moving average twice (sliding window)
        for (int k = 0; k < 2; k++) {
            int currSum = 0;
            for (int i = 1; i < 33; i++) {
                currSum += pixels[i];
            }
            for (int i = 0; i < 864; i++) {
                pixels[i] = currSum / 32;
                if (i < 863) {
                    currSum -= pixels[i + 1];
                    currSum += pixels[i + 33];
                }
            }
        }
        re[0] = pixels;
        re[1] = trees;
        re[2] = tanks;
        return re;
    }
}
