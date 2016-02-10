package com.mygdx.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.entity.componets.DailyGoal;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.entity.componets.PetComponent;
import com.mygdx.game.entity.componets.VanityComponent;

import java.util.*;

public class SaveMngr {

    public static final String DATA_FILE = "game.sav";
    public static final String VANITIES_FILE = "vanity.params";
    public static final String PETS_FILE = "pets.params";
    //    public static SimpleDateFormat sdf = getDateFormat();

    public static void saveStats(FlowerPublicComponent fc) {
        GameStats gameStats = new GameStats();
        gameStats.totalScore = fc.totalScore;
        gameStats.bestScore = fc.bestScore;
        gameStats.noAds = fc.noAds;
        gameStats.noMusic = fc.noMusic;
        gameStats.noSound = fc.noSound;
//        gameStats.lastGoalsDate = sdf.format(DailyGoalSystem.latestDate.getTime());
        saveVanities(fc);
        saveOtherPets(fc);

        gameStats.currentPet = fc.currentPet != null ? new Pet(fc.currentPet) : null;
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

    private static void saveVanities(FlowerPublicComponent fc) {
        List<VanityStats> vanities = new ArrayList<>();
        for (VanityComponent vc : fc.vanities){
            VanityStats vs = new VanityStats(vc);
            vanities.add(vs);
        }
        Json json2 = new Json();
        writeFile((VANITIES_FILE), json2.toJson(vanities));
    }

    private static void saveOtherPets(FlowerPublicComponent fc) {
        List<Pet> vanities = new ArrayList<>();
        for (PetComponent petComp : fc.pets){
            Pet pet = new Pet(petComp);
            vanities.add(pet);
        }
        Json json2 = new Json();
        writeFile(PETS_FILE, json2.toJson(vanities));
    }

    public static FlowerPublicComponent loadStats(){
        System.err.println(DATA_FILE);
        FlowerPublicComponent fc = new FlowerPublicComponent();
        String saved = readFile(DATA_FILE);
        if (!"".equals(saved)) {
            Json json = new Json();
            GameStats gameStats = json.fromJson(GameStats.class, saved);
            fc.totalScore = gameStats.totalScore;
            fc.bestScore = gameStats.bestScore;
            fc.noAds = gameStats.noAds;
            fc.noMusic = gameStats.noMusic;
            fc.noSound = gameStats.noSound;
//            try {
//                Calendar lastGoalsDate = Calendar.getInstance();
//                lastGoalsDate.setTime(sdf.parse(gameStats.lastGoalsDate));
//                DailyGoalSystem.latestDate = lastGoalsDate;
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }

            for (DailyGoalStats dg : gameStats.goals){
                DailyGoal goal = new DailyGoal();
                goal.achieved = dg.achieved;
                goal.type = DailyGoal.GoalType.valueOf(dg.type);
                goal.n = dg.n;
                goal.description = dg.description;
                fc.goals.add(goal);
            }

            fc.currentPet = gameStats.currentPet != null ? new PetComponent(gameStats.currentPet) : null;
        }
        fc.vanities = getAllVanity();

        fc.pets = getAllPets();

//        fc.currentPet = fc.pets.get(0);
        return fc;
    }

    public static List<VanityComponent> getAllVanity() {
        String saved = readFile(VANITIES_FILE);
        List<VanityComponent> vanComps = new ArrayList<>();

        if (!"".equals(saved)) {
            Json json = new Json();
            List<VanityStats> vinitys = json.fromJson(List.class, saved);

            for (VanityStats vs : vinitys){
                VanityComponent vc = new VanityComponent(vs);
                vanComps.add(vc);
            }
        }

        Collections.sort(vanComps, new Comparator<VanityComponent>() {
            @Override
            public int compare(VanityComponent o1, VanityComponent o2) {
                if (o1.cost > o2.cost) return 1;
                if (o1.cost < o2.cost) return -1;
                return 0;
            }
        });
        return vanComps;
    }

    public static List<PetComponent> getAllPets() {
        String saved = readFile(PETS_FILE);
        List<PetComponent> petComps = new ArrayList<>();

        if (!"".equals(saved)) {
            Json json = new Json();
            List<Pet> pets = json.fromJson(List.class, saved);

            for (Pet p : pets){
                petComps.add(new PetComponent(p));
            }
        }

        Collections.sort(petComps, new Comparator<PetComponent>() {
            @Override
            public int compare(PetComponent o1, PetComponent o2) {
                if (o1.cost > o2.cost) return 1;
                if (o1.cost < o2.cost) return -1;
                return 0;
            }
        });
        return petComps;
    }

    private static class GameStats {
        public boolean noAds;
        public boolean noSound;
        public boolean noMusic;
        public long bestScore;
        public long totalScore;
        public String lastGoalsDate;
        public List<DailyGoalStats> goals = new ArrayList<>();
        public Pet currentPet;
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
        public String shopIcon;
        public String name;
        public long cost;
        public String description;
        public boolean bought;
        public boolean advertised;
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
//            this.icon = vc.icon;
            this.shopIcon = vc.shopIcon;
            this.assetsToChange = vc.assetsToChange;
            this.description = vc.description;
            this.bought = vc.bought;
            this.advertised = vc.advertised;
            this.enabled = vc.enabled;
            this.floatingText = vc.floatingText;
            this.bugsSpawnAmount = vc.bugsSpawnAmount;
            this.attackSpeed = vc.attackSpeed;
            this.cocoonChance = vc.cocoonChance;
            this.dandelionChance = vc.dandelionChance;
            this.angeredBeesDuration = vc.angeredBeesDuration;
        }
    }

    public static class Pet {
        public String name;
        public boolean bought;
        public boolean activated;
        public long cost;
        public boolean tryPeriod;
        public int tryPeriodDuration;
        public int amountBugsBeforeCharging;
        public int totalEatenBugs;
        public String shopIcon;

        public Pet(){}
        public Pet(PetComponent petComponent) {
            this.name = petComponent.name;
            this.activated = petComponent.enabled;
            this.bought = petComponent.bought;
            this.cost = petComponent.cost;
            this.tryPeriod = petComponent.tryPeriod;
            this.tryPeriodDuration = petComponent.tryPeriodDuration;
            this.amountBugsBeforeCharging = petComponent.amountBugsBeforeCharging;
            this.totalEatenBugs = petComponent.totalEatenBugs;
            this.shopIcon = petComponent.shopIcon;
        }
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

    public static void generateVanityJson() {
        VanityStats vanity1 = new VanityStats();
        VanityStats vanity2 = new VanityStats();
        VanityStats vanity3 = new VanityStats();
        VanityStats vanity4 = new VanityStats();
        VanityStats vanity5 = new VanityStats();
        VanityStats vanity6 = new VanityStats();
        VanityStats vanity7 = new VanityStats();
        VanityStats vanity8 = new VanityStats();

        vanity3.cost = 90;
        vanity3.name = "Christmas leaves";
        vanity3.icon = "leaf_christmas";
        vanity3.shopIcon = "bug_juice_bubble_lib";
        vanity3.assetsToChange.put("leaf_left", "leaf_left_christmas");
        vanity3.assetsToChange.put("leaf_right", "leaf_right_christmas");

        vanity1.attackSpeed = 5;
        vanity1.cost = 150;
        vanity1.name = "majestic beard";
        vanity1.icon = "santabeard";
        vanity1.shopIcon = "item_white_beard_n";
        vanity1.assetsToChange.put("head_top", "head_top_christmas");
        vanity1.assetsToChange.put("head_mid", "head_mid_christmas");
        vanity1.assetsToChange.put("head_bottom", "head_bottom_christmas");

        vanity2.cost = 250;
        vanity2.name = "Deer horns";
        vanity2.icon = "deer";
        vanity2.shopIcon = "item_deer_horns_n";
        vanity2.assetsToChange.put("head_top", "head_top_deer");
        vanity2.assetsToChange.put("head_mid", "head_mid_default");
        vanity2.assetsToChange.put("head_bottom", "head_bottom_default");

        vanity4.cost = 650;
        vanity4.name = "Santa outfit";
        vanity4.icon = "christmas";
        vanity4.shopIcon = "item_santa_hat_n";
        vanity4.assetsToChange.put("peducle_bottom", "peducle_bottom_christmas");
        vanity4.assetsToChange.put("peducle_middle", "peducle_middle_christmas");
        vanity4.assetsToChange.put("peducle_middle_aboveLeaf", "peducle_middle_aboveLeaf_chirstmas");
        vanity4.assetsToChange.put("peducle_top", "peducle_top_christmas");
        vanity4.assetsToChange.put("peducle_top_under", "peducle_top_under_christmas");

        vanity5.cost = 750;
        vanity5.name = "Bike helmet";
        vanity5.icon = "leaf_christmas";
        vanity5.shopIcon = "item_biker_helm_n";

        vanity6.cost = 900;
        vanity6.name = "Cool Sun glasses";
        vanity6.icon = "santabeard";
        vanity6.shopIcon = "item_sun_glasses_n";

        vanity7.cost = 1200;
        vanity7.name = "Pilot's scarf";
        vanity7.icon = "deer";
        vanity7.shopIcon = "item_pilot_scarf_n";

        vanity8.cost = 1550;
        vanity8.name = "Tea cup";
        vanity8.icon = "christmas";
        vanity8.shopIcon = "item_tea_cup_n";

        List<VanityStats> vanityStatses = new ArrayList<>();

        vanityStatses.add(vanity1);
        vanityStatses.add(vanity2);
        vanityStatses.add(vanity3);
        vanityStatses.add(vanity4);
        vanityStatses.add(vanity5);
        vanityStatses.add(vanity6);
        vanityStatses.add(vanity7);
        vanityStatses.add(vanity8);

        Json jsonVanityObj = new Json();

        writeFile(VANITIES_FILE, jsonVanityObj.toJson(vanityStatses));
    }

    public static void generatePetsJson(){
        Pet dummyPet = new Pet();
//        dummyPet.activated = true;
//        dummyPet.bought = true;
        dummyPet.name = "pet";
        dummyPet.cost = 42;
        dummyPet.amountBugsBeforeCharging = 2;
        dummyPet.totalEatenBugs = 0;
        dummyPet.shopIcon = "btn_back_GUI_lib";

        ArrayList<Pet> allPets = new ArrayList<>();
        allPets.add(dummyPet);

        Json jsonPetsObj = new Json();

        writeFile(PETS_FILE, jsonPetsObj.toJson(allPets));
    }
}
