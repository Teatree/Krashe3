package com.mygdx.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.Main;
import com.mygdx.game.entity.componets.*;

import java.util.*;

public class SaveMngr {

    public static final String DATA_FILE = "game.sav";
    public static final String VANITIES_FILE = "vanity.params";
    public static final String PETS_FILE = "pets.params";
    public static final String UPGRADES_FILE = "upgrades.params";
    public static final String LEVELS_JSON = "levels.json";

    public static void saveStats(FlowerPublicComponent fc) {
        GameStats gameStats = new GameStats();
        gameStats.totalScore = fc.totalScore;
        gameStats.bestScore = fc.bestScore;
        gameStats.noAds = fc.settings.noAds;
        gameStats.noMusic = fc.settings.noMusic;
        gameStats.noSound = fc.settings.noSound;
        for (Upgrade u : fc.upgrades.values()) {
            gameStats.upgrades.put(u.upgradeType.toString(), new UpgradeStats(u));
        }
        saveVanities(fc);
        saveOtherPets(fc);

        gameStats.currentPet = fc.currentPet != null ? new PetJson(fc.currentPet) : null;

        for (Goal goal : fc.level.goals.values()) {
            DailyGoalStats dgs = new DailyGoalStats();
            dgs.achieved = goal.achieved;
            dgs.description = goal.description;
            dgs.n = goal.n;
            dgs.type = goal.type.toString();
            dgs.periodType = goal.periodType.toString();
            dgs.difficultyLevel = fc.level.difficultyLevel;
            if (goal.periodType.equals(Goal.PeriodType.TOTAL)) {
                dgs.counter = goal.counter;
            }
            gameStats.goals.add(dgs);
        }
        Json json = new Json();
        writeFile(DATA_FILE, json.toJson(gameStats));
    }

    private static void saveVanities(FlowerPublicComponent fc) {
        List<VanityJson> vanities = new ArrayList<VanityJson>();
        for (VanityComponent vc : fc.vanities) {
            VanityJson vs = new VanityJson(vc);
            vanities.add(vs);
        }
        Json json2 = new Json();
        writeFile((VANITIES_FILE), json2.toJson(vanities));
    }

    private static void saveOtherPets(FlowerPublicComponent fc) {
        List<PetJson> vanities = new ArrayList<PetJson>();
        for (PetComponent petComp : fc.pets) {
            PetJson pet = new PetJson(petComp);
            vanities.add(pet);
        }
        Json json2 = new Json();
        writeFile(PETS_FILE, json2.toJson(vanities));
    }

    public static FlowerPublicComponent loadStats() {
        initAllLevels();
        FlowerPublicComponent fc = new FlowerPublicComponent();
        Goal.init(fc);
        String saved = readFile(DATA_FILE);
        if (!"".equals(saved)) {
            Json json = new Json();
            GameStats gameStats = json.fromJson(GameStats.class, saved);
            fc.totalScore = gameStats.totalScore;
            fc.bestScore = gameStats.bestScore;
            fc.settings.noAds = gameStats.noAds;
            fc.settings.noMusic = gameStats.noMusic;
            fc.settings.noSound = gameStats.noSound;

            for (Map.Entry<String, UpgradeStats> e : gameStats.upgrades.entrySet()) {
                fc.upgrades.put(Upgrade.UpgradeType.valueOf(e.getKey()), new Upgrade(e.getValue()));
            }
            fc.pets = getAllPets();
            PetComponent petComponent = gameStats.currentPet != null ? new PetComponent(gameStats.currentPet) : null;
            if (petComponent != null && petComponent.tryPeriod) {
                long now = System.currentTimeMillis();
                if (now - petComponent.tryPeriodStart >= petComponent.tryPeriodDuration * 1000) {
                    petComponent = null;
                }
            }
            fc.currentPet = petComponent;
//            dummyUpgrade(fc);
            dummyPet(fc);
            Goal.init(fc);
            addGoals(fc, gameStats);
        }
        fc.vanities = getAllVanity();
        return fc;
    }

    private static void dummyPet(FlowerPublicComponent fc) {
        fc.currentPet = fc.pets.get(0);
        fc.currentPet.tryPeriod = true;
        fc.currentPet.enabled = true;
        fc.currentPet.tryPeriodDuration = 2 * 60;
        fc.currentPet.tryPeriodStart = System.currentTimeMillis();
    }

    private static void dummyUpgrade(FlowerPublicComponent fc) {
        Upgrade u = Upgrade.getBJDouble();
        u.tryPeriod = true;
        u.tryPeriodDuration = 2 * 60;
        u.tryPeriodStart = System.currentTimeMillis();
        u.bought = true;
        u.enabled = true;
        fc.upgrades.put(Upgrade.UpgradeType.BJ_DOUBLE,u);
    }

    private static void addGoals(FlowerPublicComponent fc, GameStats gameStats) {
        for (DailyGoalStats dg : gameStats.goals) {
            Goal goal = new Goal();
            goal.achieved = dg.achieved;
            goal.counter = dg.counter;
            goal.justAchieved = dg.justAchieved;
            goal.type = Goal.GoalType.valueOf(dg.type);
            goal.periodType = Goal.PeriodType.valueOf(dg.periodType);
            goal.n = dg.n;
            goal.description = dg.description;
            fc.level.goals.put(goal.type, goal);
            fc.level.difficultyLevel = dg.difficultyLevel;
            fc.level.name = Level.levelsInfo.get(dg.difficultyLevel - 1).name;
        }
//        Goal goal = new Goal();
//        goal.achieved = false;
//        goal.justAchieved = false;
//        goal.periodType = Goal.PeriodType.IN_A_ROW;
//        goal.type = Goal.GoalType.EAT_N_BUGS;
//        goal.n = 3;
//        goal.description = GoalConstants.EAT_N_BUGS_DESC;
//        fc.level.goals.put(goal.type, goal);

    }

    public static List<VanityComponent> getAllVanity() {
        String saved = readFile(VANITIES_FILE);
        List<VanityComponent> vanComps = new ArrayList<VanityComponent>();

        if (!"".equals(saved)) {
            Json json = new Json();
            List<VanityJson> vinitys = json.fromJson(List.class, saved);

            for (VanityJson vs : vinitys) {
                VanityComponent vc = new VanityComponent(vs);
                vanComps.add(vc);
            }
        }

        Comparator vanitiesComparator = new Comparator<VanityComponent>() {
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
        };
        Collections.sort(vanComps, vanitiesComparator);
        return vanComps;
    }

    public static List<PetComponent> getAllPets() {
        String saved = readFile(PETS_FILE);
        List<PetComponent> petComps = new ArrayList<PetComponent>();

        if (!"".equals(saved)) {
            Json json = new Json();
            List<PetJson> pets = json.fromJson(List.class, saved);

            for (PetJson p : pets) {
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

        List<VanityJson> vanityStatses = new ArrayList<VanityJson>();

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

        dummyPet.name = "pet";
        dummyPet.cost = 42;
        dummyPet.amountBugsBeforeCharging = 3;
        dummyPet.totalEatenBugs = 0;
        dummyPet.transactionId = Main.pet_bird_trans_ID;

        ArrayList<PetJson> allPets = new ArrayList<PetJson>();
        allPets.add(dummyPet);

        Json jsonPetsObj = new Json();

        writeFile(PETS_FILE, jsonPetsObj.toJson(allPets));
    }

    public static void generateLevelsJson() {
        List<LevelInfo> levels = new ArrayList<>();
        LevelInfo l = new LevelInfo();
        l.difficultyLevel = 1;
        l.difficultyMultiplier = 0.5f;
        l.name = "Novice";

        LevelInfo l1 = new LevelInfo();
        l1.difficultyLevel = 2;
        l1.difficultyMultiplier = 0.8f;
        l1.name = "Pro";

        LevelInfo l2 = new LevelInfo();
        l2.difficultyLevel = 3;
        l2.difficultyMultiplier = 0.8f;
        l2.name = "Prorer Pro";

        LevelInfo l3 = new LevelInfo();
        l3.difficultyLevel = 4;
        l3.difficultyMultiplier = 0.8f;
        l3.name = "Prorest Pro";

        levels.add(l);
        levels.add(l1);
        levels.add(l2);
        levels.add(l3);

        writeFile(LEVELS_JSON, new Json().toJson(levels));
    }

    public static List<LevelInfo> initAllLevels() {
        String saved = readFile(LEVELS_JSON);
        List<LevelInfo> levels = new Json().fromJson(List.class, saved);
        Level.levelsInfo = levels;
        return levels;
    }

    public static class LevelInfo {
        public String name;
        public int difficultyLevel;
        public float difficultyMultiplier;
        public String type;

        public LevelInfo() {}
    }

    private static class GameStats {
        public boolean noAds;
        public boolean noSound;
        public boolean noMusic;
        public long bestScore;
        public long totalScore;
        public List<DailyGoalStats> goals = new ArrayList<DailyGoalStats>();
        public PetJson currentPet;
        public Map<String, UpgradeStats> upgrades = new HashMap<>();
    }

    private static class DailyGoalStats {
        public int n;
        public String type;
        public String periodType;
        public String description;
        public boolean achieved;
        public boolean justAchieved;
        public int difficultyLevel;
        public int counter;
    }

    public static class UpgradeStats {
        public String upgradeType;
        public boolean tryPeriod;
        public long tryPeriodDuration;
        public long tryPeriodStart;
        public long tryPeriodTimer;

        public UpgradeStats() {
        }

        public UpgradeStats(Upgrade us) {
            this.upgradeType = us.upgradeType.toString();
            this.tryPeriod = us.tryPeriod;
            this.tryPeriodDuration = us.tryPeriodDuration;
            this.tryPeriodTimer = us.tryPeriodTimer;
            this.tryPeriodStart = us.tryPeriodStart;
        }
    }

    public static class VanityJson {
        public Map<String, String> assetsToChange = new HashMap<String, String>();

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
        public long tryPeriodDuration;
        public int amountBugsBeforeCharging;
        public int totalEatenBugs;
        public String shopIcon;
        public long tryPeriodTimer;
        public long tryPeriodStart;
        public String transactionId;

        public PetJson() {
        }

        public PetJson(PetComponent petComponent) {
            this.name = petComponent.name;
            this.activated = petComponent.enabled;
            this.bought = petComponent.bought;
            this.cost = petComponent.cost;
            this.amountBugsBeforeCharging = petComponent.amountBugsBeforeCharging;
            this.totalEatenBugs = petComponent.totalEatenBugs;
            this.shopIcon = petComponent.shopIcon;

            this.tryPeriod = petComponent.tryPeriod;
            this.tryPeriodDuration = petComponent.tryPeriodDuration;
            this.tryPeriodTimer = petComponent.tryPeriodTimer;
            this.tryPeriodStart = petComponent.tryPeriodStart;
            this.transactionId = petComponent.transactionId;
        }
    }
}
