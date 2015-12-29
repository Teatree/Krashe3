package com.mygdx.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.entity.componets.VanityComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AnastasiiaRudyk on 12/14/2015.
 */
public class SaveMngr {

    public static final String DATA_FILE = "game.sav";

    public static void saveStats(FlowerPublicComponent fc) {
        GameStats gameStats = new GameStats();
        gameStats.totalScore = fc.totalScore;

        for (VanityComponent vc : fc.vanities){
            VanityStats vs = new VanityStats(vc);
            gameStats.vanities.add(vs);
        }
        Json json = new Json();
        writeFile(DATA_FILE, json.toJson(gameStats));
    }

    public static FlowerPublicComponent loadStats(){
        FlowerPublicComponent fc = new FlowerPublicComponent();
        String saved = readFile(DATA_FILE);
        if (!"".equals(saved)) {
            Json json = new Json();
            GameStats gameStats = json.fromJson(GameStats.class, saved);
            fc.totalScore = gameStats.totalScore;

            for (VanityStats vs : gameStats.vanities){
                VanityComponent vc = new VanityComponent();
                vc.assetsToChange = vs.assetsToChange;
                fc.vanities.add(vc);
            }
        }
        return fc;
    }

    private static void writeFile(String fileName, String s) {
        FileHandle file = Gdx.files.local(fileName);
//        file.writeString(com.badlogic.gdx.utils.Base64Coder.encodeString(s), false);
        file.writeString(s, false);
    }

    private static String readFile(String fileName) {
        FileHandle file = Gdx.files.local(fileName);
        if (file != null && file.exists()) {
            String s = file.readString();
            if (!s.isEmpty()) {
//                return com.badlogic.gdx.utils.Base64Coder.decodeString(s);
                return s;
            }
        }
        return "";
    }

    private static class GameStats {
        public int totalScore;
        public List<VanityStats> vanities = new ArrayList<>();
    }


    public static class VanityStats {
        public Map<String, String> assetsToChange = new HashMap<>();

        public String icon;
        public String name;
        public int cost;
        public String description;
        public boolean bought = true;
        public boolean available = true;
        public boolean enabled;
        public boolean floatingText;
        public int bugsSpawnAmount;
        public int attackSpeed;
        public int cocoonChance;
        public int dandelionChance;
        public int angeredBeesDuration;

        public VanityStats() {
        }

        public VanityStats(VanityComponent vc) {
            this.name = vc.name;
            this.cost = vc.cost;
            this.description = vc.description;
            this.bought = vc.bought;
            this.available = vc.available;
            this.enabled = vc.enabled;
            this.floatingText = vc.floatingText;
            this.bugsSpawnAmount = vc.bugsSpawnAmount;
            this.attackSpeed = vc.attackSpeed;
            this.cocoonChance = vc.cocoonChance;
            this.dandelionChance = vc.dandelionChance;
            this.angeredBeesDuration = vc.angeredBeesDuration;
        }
    }
    public static void generateVanityJSON() {
        VanityStats vanity1 = new VanityStats();
        VanityStats vanity2 = new VanityStats();
        VanityStats vanity3 = new VanityStats();
        VanityStats vanity4 = new VanityStats();

        vanity1.attackSpeed = 5;
        vanity1.icon = "btn_shop_item_1";
        vanity1.assetsToChange.put("head_top", "head_top_christmas");
        vanity1.assetsToChange.put("head_mid", "head_mid_christmas");
        vanity1.assetsToChange.put("head_bottom", "head_bottom_christmas");

        vanity2.icon = "btn_shop_item_2";
        vanity2.assetsToChange.put("head_top", "head_top_deer");
        vanity2.assetsToChange.put("head_mid", "head_mid_default");
        vanity2.assetsToChange.put("head_bottom", "head_bottom_default");

        vanity3.icon = "btn_shop_item_3";
        vanity3.assetsToChange.put("leaf_left", "leaf_left_christmas");
        vanity3.assetsToChange.put("leaf_right", "leaf_right_christmas");

        vanity4.icon = "btn_shop_item_4";
        vanity4.assetsToChange.put("peducle_bottom", "peducle_bottom_christmas");
        vanity4.assetsToChange.put("peducle_middle", "peducle_middle_christmas");
        vanity4.assetsToChange.put("peducle_middle_aboveLeaf", "peducle_middle_aboveLeaf_christmas");
        vanity4.assetsToChange.put("peducle_top", "peducle_top_christmas");
        vanity4.assetsToChange.put("peducle_top_under", "peducle_top_under_christmas");

        List<VanityStats> vanityStatses = new ArrayList<>();

        vanityStatses.add(vanity1);
        vanityStatses.add(vanity2);
        vanityStatses.add(vanity3);
        vanityStatses.add(vanity4);

        Json jsonVanityObj = new Json();

        writeFile("vanity.params", jsonVanityObj.toJson(vanityStatses));
    }

    public static List<VanityComponent> getAllVanity() {
        String saved = readFile("vanity.params");
        List<VanityComponent> vanComps = new ArrayList<>();

        if (!"".equals(saved)) {
            Json json = new Json();
            List<VanityStats> vinitys = json.fromJson(List.class, saved);

            for (VanityStats vs : vinitys){
                VanityComponent vc = new VanityComponent(vs);
                vanComps.add(vc);
            }
        }
        return vanComps;
    }
}