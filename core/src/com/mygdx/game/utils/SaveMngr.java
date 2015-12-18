package com.mygdx.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.entity.componets.FlowerPublicComponent;

/**
 * Created by AnastasiiaRudyk on 12/14/2015.
 */
public class SaveMngr {

    public static final String DATA_FILE = "game.sav";

    public static void saveStats(FlowerPublicComponent fc) {
        GameStats gameStats = new GameStats();
        gameStats.totalScore = fc.totalScore;
        Json json = new Json();
        writeFile(DATA_FILE, json.toJson(gameStats));
    }

    public static FlowerPublicComponent loadStats(){
        FlowerPublicComponent fc = new FlowerPublicComponent();
        String saved = readFile(DATA_FILE);
        if (saved != null && !"".equals(saved)) {
            Json json = new Json();
            GameStats gameStats = json.fromJson(GameStats.class, saved);
            fc.totalScore = gameStats.totalScore;
        }
        return fc;
    }

    private static void writeFile(String fileName, String s) {
        FileHandle file = Gdx.files.local(fileName);
        file.writeString(com.badlogic.gdx.utils.Base64Coder.encodeString(s), false);
    }

    private static String readFile(String fileName) {
        FileHandle file = Gdx.files.local(fileName);
        if (file != null && file.exists()) {
            String s = file.readString();
            if (!s.isEmpty()) {
                return com.badlogic.gdx.utils.Base64Coder.decodeString(s);
            }
        }
        return "";
    }

    private static class GameStats {
        public int totalScore;
    }
}
