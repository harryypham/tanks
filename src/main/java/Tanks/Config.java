package Tanks;

import java.io.*;
import java.util.*;

import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;

public class Config {
    private JSONObject json;
    private JSONObject[] levels;
    private JSONObject playerObject;
    private int currentLevel;
    private App app;

    public Config(JSONObject json, App app) {
        this.json = json;
        this.currentLevel = 0;
        this.app = app;
        initLevels();
        initPlayerColors();
    }

    private void initLevels() {
        JSONArray tempArr = json.getJSONArray("levels");
        this.levels = new JSONObject[tempArr.size()];
        for (int i = 0; i < tempArr.size(); i++) {
            this.levels[i] = (JSONObject) tempArr.get(i);
        }
    }

    private void initPlayerColors() {
        this.playerObject = json.getJSONObject("player_colours");
    }

    public void setLevel(int level) {
        this.currentLevel = level;
    }

    public File getLayoutFile() {
        String filename = this.levels[this.currentLevel].getString("layout");
        File file = new File(filename);
        return file;
    }

    public PImage getBackground() {
        String backgroundImgPath = "build/resources/main/Tanks/"
                + this.levels[this.currentLevel].getString("background");
        PImage backgroundImg = app.loadImage(backgroundImgPath);
        return backgroundImg;
    }

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

    public int[] getForegroundColor() {
        String temp = this.levels[this.currentLevel].getString("foreground-colour");
        String[] tempColors = temp.split(",", -1);
        int[] colors = { Integer.parseInt(tempColors[0]), Integer.parseInt(tempColors[1]),
                Integer.parseInt(tempColors[2]) };
        return colors;
    }

    public Map<Character, int[]> getPlayerColors() {
        Map<Character, int[]> playerColor = new HashMap<Character, int[]>();
        Iterator<?> iter = playerObject.keys().iterator();
        String currKey;
        int[] colors = new int[3];
        while (iter.hasNext()) {
            currKey = (String) iter.next();

            String[] temp = playerObject.getString(currKey).split(",", -1);
            if (temp[0].equals("random")) {
                continue;
            }

            for (int i = 0; i < temp.length; i++) {
                colors[i] = Integer.parseInt(temp[i]);
            }
            playerColor.put(currKey.charAt(0), colors);
        }
        return playerColor;
    }
}
