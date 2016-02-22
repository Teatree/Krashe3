package com.mygdx.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.entity.componets.*;

import java.util.*;

public class SaveMngr {

    public static final String DATA_FILE = "game.sav";
    public static final String VANITIES_FILE = "vanity.params";
    public static final String PETS_FILE = "pets.params";
    public static final String UPGRADES_FILE = "upgrades.params";
    //    public static SimpleDateFormat sdf = getDateFormat();

    public static void saveStats(FlowerPublicComponent fc) {
        GameStats gameStats = new GameStats();
        gameStats.totalScore = fc.totalScore;
        gameStats.bestScore = fc.bestScore;
        gameStats.noAds = fc.settings.noAds;
        gameStats.noMusic = fc.settings.noMusic;
        gameStats.noSound = fc.settings.noSound;
        gameStats.doubleJuice = fc.haveBugJuiceDouble();
        gameStats.phoenix = fc.havePhoenix();
//        gameStats.lastGoalsDate = sdf.format(DailyGoalSystem.latestDate.getTime());
        saveVanities(fc);
        saveOtherPets(fc);

        gameStats.currentPet = fc.currentPet != null ? new PetJson(fc.currentPet) : null;
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
        List<VanityJson> vanities = new ArrayList<>();
        for (VanityComponent vc : fc.vanities){
            VanityJson vs = new VanityJson(vc);
            vanities.add(vs);
        }
        Json json2 = new Json();
        writeFile((VANITIES_FILE), json2.toJson(vanities));
    }

    private static void saveOtherPets(FlowerPublicComponent fc) {
        List<PetJson> vanities = new ArrayList<>();
        for (PetComponent petComp : fc.pets){
            PetJson pet = new PetJson(petComp);
            vanities.add(pet);
        }
        Json json2 = new Json();
        writeFile(PETS_FILE, json2.toJson(vanities));
    }

    public static FlowerPublicComponent loadStats(){
        FlowerPublicComponent fc = new FlowerPublicComponent();
        String saved = readFile(DATA_FILE);
        if (!"".equals(saved)) {
            Json json = new Json();
            GameStats gameStats = json.fromJson(GameStats.class, saved);
            fc.totalScore = gameStats.totalScore;
            fc.bestScore = gameStats.bestScore;
            fc.settings.noAds = gameStats.noAds;
            fc.settings.noMusic = gameStats.noMusic;
            fc.settings.noSound = gameStats.noSound;

            if (gameStats.doubleJuice) {
                fc.upgrades.put(Upgrade.UpgradeType.DOUBLE_JUICE, Upgrade.getBJDouble());
            }
            if (gameStats.phoenix) {
                fc.upgrades.put(Upgrade.UpgradeType.PHOENIX, Upgrade.getPhoenix());
            }
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

        fc.currentPet = fc.pets.get(0);
        return fc;
    }

    public static List<VanityComponent> getAllVanity() {
        String saved = readFile(VANITIES_FILE);
        List<VanityComponent> vanComps = new ArrayList<>();

        if (!"".equals(saved)) {
            Json json = new Json();
            List<VanityJson> vinitys = json.fromJson(List.class, saved);

            for (VanityJson vs : vinitys){
                VanityComponent vc = new VanityComponent(vs);
                vanComps.add(vc);
            }
        }

        vanComps.sort(new Comparator<VanityComponent>() {
            @Override
            public int compare(VanityComponent o1, VanityComponent o2) {
                return compareByCost(o1, o2);
            }

            public int compareByCost(ShopItem o1, ShopItem o2) {
                if (o1.cost == o2.cost) {
                    return 0;
                } else {
                    return o1.cost < o2.cost ? -1 : 1;
                }
            }
        });
        return vanComps;
    }

    public static List<PetComponent> getAllPets() {
        String saved = readFile(PETS_FILE);
        List<PetComponent> petComps = new ArrayList<>();

        if (!"".equals(saved)) {
            Json json = new Json();
            List<PetJson> pets = json.fromJson(List.class, saved);

            for (PetJson p : pets){
                petComps.add(new PetComponent(p));
            }
        }
        return petComps;
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
        VanityJson vanity1 = new VanityJson();
        VanityJson vanity2 = new VanityJson();
        VanityJson vanity3 = new VanityJson();
        VanityJson vanity4 = new VanityJson();
        VanityJson vanity5 = new VanityJson();
        VanityJson vanity6 = new VanityJson();
        VanityJson vanity7 = new VanityJson();
        VanityJson vanity8 = new VanityJson();

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

        List<VanityJson> vanityStatses = new ArrayList<>();

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

    public static void generatePetsJson() {
        PetJson dummyPet = new PetJson();

        dummyPet.activated = true;
        dummyPet.bought = true;

        dummyPet.name = "pet";
        dummyPet.cost = 42;
        dummyPet.amountBugsBeforeCharging = 3;
        dummyPet.totalEatenBugs = 0;
        dummyPet.shopIcon = "btn_back_GUI_lib";

        ArrayList<PetJson> allPets = new ArrayList<>();
        allPets.add(dummyPet);

        Json jsonPetsObj = new Json();

        writeFile(PETS_FILE, jsonPetsObj.toJson(allPets));
    }

    private static class GameStats {
        public boolean noAds;
        public boolean noSound;
        public boolean noMusic;
        public long bestScore;
        public long totalScore;
        public String lastGoalsDate;
        public List<DailyGoalStats> goals = new ArrayList<>();
        public PetJson currentPet;
        public boolean doubleJuice;
        public boolean phoenix;
    }

    private static class DailyGoalStats{
        public int n;
        public String type;
        public String description;
        public boolean achieved;
    }

    public static class VanityJson {
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
        public PetJson pet;

        public VanityJson() {
        }

        public VanityJson(VanityComponent vc) {
            this.name = vc.name;
            this.cost = vc.cost;
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
            this.pet = vc.pet != null ? new PetJson(vc.pet) : null;
        }
    }

    public static class PetJson {
        public String name;
        public boolean bought;
        public boolean activated;
        public long cost;
        public boolean tryPeriod;
        public int tryPeriodDuration;
        public int amountBugsBeforeCharging;
        public int totalEatenBugs;
        public String shopIcon;

        public PetJson() {
        }

        public PetJson(PetComponent petComponent) {
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
}
