package com.mygdx.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.entity.componets.DailyGoal;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.entity.componets.VanityComponent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by AnastasiiaRudyk on 12/14/2015.
 */
public class SaveMngr {

    public static final String DATA_FILE = "game.sav";
    public static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    public static void saveStats(FlowerPublicComponent fc) {
        GameStats gameStats = new GameStats();
        gameStats.totalScore = fc.totalScore;
        gameStats.bestScore = fc.bestScore;
        gameStats.lastGoalsDate = sdf.format(DailyGoalSystem.latestDate.getTime());
        for (VanityComponent vc : fc.vanities){
            VanityStats vs = new VanityStats(vc);
            gameStats.vanities.add(vs);
        }
        for (DailyGoal goal : fc.goals){
            DailyGoalStats dgs = new DailyGoalStats();
            dgs.achieved = goal.achieved;
            dgs.description = goal.description;
            dgs.n = goal.n;
            dgs.type = goal.type.toString();
            gameStats.goals.add(dgs);
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
            fc.bestScore = gameStats.bestScore;
            try {
                Calendar lastGoalsDate = Calendar.getInstance();
                lastGoalsDate.setTime(sdf.parse(gameStats.lastGoalsDate));
                DailyGoalSystem.latestDate = lastGoalsDate;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for (VanityStats vs : gameStats.vanities){
                VanityComponent vc = new VanityComponent();
                vc.assetsToChange = vs.assetsToChange;
                fc.vanities.add(vc);
            }
            for (DailyGoalStats dg : gameStats.goals){
                DailyGoal goal = new DailyGoal();
                goal.achieved = dg.achieved;
                goal.type = DailyGoal.GoalType.valueOf(dg.type);
                goal.n = dg.n;
                goal.description = dg.description;
                fc.goals.add(goal);
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

    private static class GameStats {
        public int bestScore;
        public int totalScore;
        public String lastGoalsDate;
        public List<VanityStats> vanities = new ArrayList<>();
        public List<DailyGoalStats> goals = new ArrayList<>();
    }

    private static class DailyGoalStats{
        public int n;
        public String type;
        public String description;
        public boolean achieved;
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
//        vanity1.icon = "btn_shop_item_1";
        vanity1.cost = 150;
        vanity1.assetsToChange.put("head_top", "head_top_christmas");
        vanity1.assetsToChange.put("head_mid", "head_mid_christmas");
        vanity1.assetsToChange.put("head_bottom", "head_bottom_christmas");

//        vanity2.icon = "btn_shop_item_2";
        vanity2.cost = 250;
        vanity2.assetsToChange.put("head_top", "head_top_deer");
        vanity2.assetsToChange.put("head_mid", "head_mid_default");
        vanity2.assetsToChange.put("head_bottom", "head_bottom_default");

//        vanity3.icon = "btn_shop_item_3";
        vanity3.cost = 90;
        vanity3.assetsToChange.put("leaf_left", "leaf_left_christmas");
        vanity3.assetsToChange.put("leaf_right", "leaf_right_christmas");

//        vanity4.icon = "btn_shop_item_4";
        vanity4.cost = 650;
        vanity4.assetsToChange.put("peducle_bottom", "peducle_bottom_christmas");
        vanity4.assetsToChange.put("peducle_middle", "peducle_middle_christmas");
        vanity4.assetsToChange.put("peducle_middle_aboveLeaf", "peducle_middle_aboveLeaf_chirstmas");
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


}
