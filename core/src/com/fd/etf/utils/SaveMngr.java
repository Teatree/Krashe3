package com.fd.etf.utils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.fd.etf.entity.componets.*;
import com.fd.etf.stages.GameScreenScript;
import com.fd.etf.system.AchievementSystem;
import com.fd.etf.system.BugSpawnSystem;
import com.fd.etf.system.CocoonSystem;

import java.util.*;

import static com.fd.etf.entity.componets.Upgrade.UpgradeType;
import static com.fd.etf.entity.componets.VanityComponent.vanityCollections;
import static com.fd.etf.entity.componets.VanityComponent.vanityComponentsByChangedAssets;

public class SaveMngr {

    private static final String DATA_FILE = "game.sav";
    private static final String VANITIES_FILE = "vanity.params";
    private static final String PETS_FILE = "pets.params";
    private static final String LEVELS_JSON = "levels.json";
    private static final String MULTIPLIERS_JSON = "BugMultipliersByDuration.json";
    private static final String COCOON_MULTIPLIERS_JSON = "CocoonSpawnMultipliersByDuration.json";
    private static final String DANDELION_MULTIPLIERS_JSON = "DandelionSpawnMultipliersByDuration.json";

    public static void saveStats(FlowerPublicComponent fc) {
        GameStats gameStats = new GameStats();
        gameStats.totalScore = fc.totalScore;
        gameStats.bestScore = fc.bestScore;
        gameStats.noAds = fc.settings.noAds;
        gameStats.noMusic = fc.settings.noMusic;
        gameStats.noSound = fc.settings.noSound;
        gameStats.goalStatusChanged = Level.goalStatusChanged;
        gameStats.totalPlayedGames = fc.settings.totalPlayedGames;

        //achievements
        gameStats.bugAchCounter = AchievementSystem.bugAchCounter;
        gameStats.queenAchCounter = AchievementSystem.queenAchCounter;
        gameStats.butterflyAchCounter = AchievementSystem.butterflyAchCounter;
        gameStats.queenAchGoal = AchievementSystem.queenAchGoal;
        gameStats.bugAchGoal = AchievementSystem.bugAchGoal;
        gameStats.butterflyAchGoal = AchievementSystem.butterflyAchGoal;

        gameStats.reviveAd_max = fc.reviveAdsMaxNastya;
        gameStats.gameOverReviveTimesLimit = GameScreenScript.gameOverReviveTimesLimit;
        gameStats.curDay = fc.curDay;

        gameStats.upgrades = new ArrayList<>();
        for (Upgrade u : fc.upgrades.values()) {
            gameStats.upgrades.add(new UpgradeStats(u));
        }

        for (VanityComponent vc : fc.vanities) {
            if (vc.bought || vc.enabled) {
                gameStats.boughtVanities.add(new VanityJson(vc));
            }
        }
//        saveVanities(fc);
        saveOtherPets(fc);

        gameStats.currentPet = FlowerPublicComponent.currentPet != null ? new PetJson(FlowerPublicComponent.currentPet) : null;

        for (Goal goal : fc.level.goals.values()) {
            DailyGoalStats dgs = new DailyGoalStats();
            dgs.achieved = goal.achieved;
            dgs.description = goal.description;
            dgs.n = goal.n;
            dgs.type = goal.type.toString();
            dgs.justAchieved = goal.justAchieved;
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

//    private static void saveVanities(FlowerPublicComponent fc) {
//        List<VanityJson> vanities = new ArrayList<VanityJson>();
//        for (VanityComponent vc : fc.vanities) {
//            VanityJson vs = new VanityJson(vc);
//            vanities.add(vs);
//        }
//        Json json2 = new Json();
//        writeFile((VANITIES_FILE), json2.toJson(vanities));
//    }

    private static void saveOtherPets(FlowerPublicComponent fc) {
        List<PetJson> pets = new ArrayList<PetJson>();
        if (!fc.pets.isEmpty()) {
            for (PetComponent petComp : fc.pets) {
                PetJson pet = new PetJson(petComp);
                pets.add(pet);
            }
            Json json2 = new Json();
            writeFile(PETS_FILE, json2.toJson(pets));
        }
    }

    public static FlowerPublicComponent loadStats() {
        initAllLevels();
        initAllMultipliers();
        initCocoonMultipliers(0);
        initDandelionMultipliers(0);

        FlowerPublicComponent fc = new FlowerPublicComponent();
        fc.upgrades = new HashMap<>();
        Goal.init(fc);
        String saved = readFile(DATA_FILE);
        Level.goalStatusChanged = true;

        GameStats gameStats = null;
        if (!"".equals(saved)) {
            Json json = new Json();
            gameStats = json.fromJson(GameStats.class, saved);
            fc.totalScore = gameStats.totalScore;
            fc.bestScore = gameStats.bestScore;
            fc.curDay = gameStats.curDay;

            GameStats stats = json.fromJson(GameStats.class, saved);
            fc.settings.noAds = stats.noAds;
            fc.settings.noMusic = stats.noMusic;
            fc.settings.noSound = stats.noSound;
            fc.settings.totalPlayedGames = stats.totalPlayedGames;
            fc.settings.playedGames = 0;
            Level.goalStatusChanged = stats.goalStatusChanged;

            //achievements
            AchievementSystem.bugAchCounter = stats.bugAchCounter;
            AchievementSystem.queenAchCounter = stats.queenAchCounter;
            AchievementSystem.butterflyAchCounter = stats.butterflyAchCounter;
            AchievementSystem.queenAchGoal = stats.queenAchGoal;
            AchievementSystem.bugAchGoal = stats.bugAchGoal;
            AchievementSystem.bugAchGoal = stats.bugAchGoal;

            fc.reviveAdsMaxNastya = stats.reviveAd_max;
            GameScreenScript.gameOverReviveTimesLimit =  stats.gameOverReviveTimesLimit;

            if (gameStats.upgrades != null) {
                for (UpgradeStats e : gameStats.upgrades) {
                    Upgrade u = new Upgrade(e);
                    fc.upgrades.put(UpgradeType.valueOf(e.upgradeType), u);
                }
            }

            PetComponent petComponent = gameStats.currentPet != null ? new PetComponent(gameStats.currentPet) : null;
            petComponent = checkPetsTryPeriod(petComponent);
            FlowerPublicComponent.currentPet = petComponent;
//            dummyPet(fc);
            Goal.init(fc);
            addGoals(fc, gameStats);
        }

        fc.vanities = getAllVanity(gameStats);

        fc.pets = getAllPets();
        return fc;
    }

    private static PetComponent checkPetsTryPeriod(PetComponent petComponent) {
        if (petComponent != null && petComponent.tryPeriod) {
            long now = System.currentTimeMillis();
            if (now - petComponent.tryPeriodStart >= petComponent.tryPeriodDuration * 1000) {
                petComponent = null;
            }
        }
        return petComponent;
    }

    private static void dummyPet(FlowerPublicComponent fc) {
//      fc.currentPet.tryPeriodStart = System.currentTimeMillis();
        PetComponent u = fc.pets.get(0);
        u.tryPeriod = true;
//        fc.currentPet.tryPeriod = true;
//        fc.currentPet.enabled = true;
//        fc.currentPet.tryPeriodDuration = 30;
//        f
        u.tryPeriodDuration = 30;
        u.tryPeriodStart = System.currentTimeMillis();
        u.bought = true;
        u.enabled = true;
        FlowerPublicComponent.currentPet = u;
    }

    private static void dummyUpgrade(FlowerPublicComponent fc) {
        Upgrade u = Upgrade.getBJDouble(null);
        u.tryPeriod = true;
//        fc.currentPet.tryPeriod = true;
//        fc.currentPet.enabled = true;
//        fc.currentPet.tryPeriodDuration = 30;
//        f
        u.tryPeriodDuration = 2 * 60;
        u.tryPeriodStart = System.currentTimeMillis();
        u.bought = true;
        u.enabled = true;
        fc.upgrades.put(Upgrade.UpgradeType.BJ_DOUBLE, u);
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
            fc.level.difficultyLevel = dg.difficultyLevel - 1;
            fc.level.name = Level.levelsInfo.get(dg.difficultyLevel - 1).name;
            fc.level.resetNewInfo();

        }
        fc.level.updateLevel(fc);

//        Goal goal = new Goal();
//        goal.achieved = false;
//        goal.justAchieved = false;
//        goal.periodType = Goal.PeriodType.IN_A_ROW;
//        goal.type = Goal.GoalType.EAT_N_BUGS;
//        goal.n = 3;
//        goal.description = GoalConstants.EAT_N_BUGS_DESC;
//        fc.level.goals.put(goal.type, goal);

    }

    public static List<VanityComponent> getAllVanity(GameStats gameStats) {
        String saved = readFile(VANITIES_FILE);
        List<VanityComponent> vanComps = new ArrayList<VanityComponent>();
        vanityCollections = new HashMap<>();
        vanityComponentsByChangedAssets = new HashMap<>();

        if (!"".equals(saved)) {
            Json json = new Json();
            List<VanityJson> vinitys = json.fromJson(List.class, saved);
            for (VanityJson vs : vinitys) {
                VanityComponent vc = new VanityComponent(vs);
                vanComps.add(vc);
                groupVanitiesByChangedAssets(vc);
                groupVanitiesByCollection(vc);

                //if it was bought or enabled
                if (gameStats != null && gameStats.boughtVanities != null && !gameStats.boughtVanities.isEmpty())
                    for (VanityJson savedVanity : gameStats.boughtVanities) {
                        if (vc.name.equals(savedVanity.name)) {
                            vc.bought = savedVanity.bought;
                            if (savedVanity.enabled) {
                                vc.applyOnLoad();
                            }
                        }
                    }
            }
        }
        sortVanities(vanComps);
        return vanComps;
    }

    private static void groupVanitiesByChangedAssets(VanityComponent vc) {
        for (String fileName : vc.assetsToChange.keySet()) {
            if (vanityComponentsByChangedAssets.get(fileName) != null) {
                vanityComponentsByChangedAssets.get(fileName).add(vc);
            } else {
                List<VanityComponent> list = new ArrayList<>();
                list.add(vc);
                vanityComponentsByChangedAssets.put(fileName, list);
            }
        }
    }

    private static void sortVanities(List<VanityComponent> vanComps) {
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
    }

    private static void groupVanitiesByCollection(VanityComponent vc) {
        if (vc.collection != null && !vc.collection.equals("")) {
            if (vanityCollections.get(vc.collection) == null) {
                vanityCollections.put(vc.collection, new VanityCollection(vc));
            } else {
                vanityCollections.get(vc.collection).total++;
                vanityCollections.get(vc.collection).unlocked += vc.bought ? 1 : 0;
            }
        }
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
        FileHandle file;
        if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
            file = Gdx.files.local(fileName);
            if (file == null || !file.exists()) {
                file = Gdx.files.internal(fileName);
            }
        } else {
            file = Gdx.files.local(fileName);
        }
        if (file != null && file.exists()) {
            String s = file.readString();
            if (!s.isEmpty()) {
                return s;
            }
        }
        return "";
    }

    public static void generatePetsJson() {
        PetJson dummyPet = new PetJson();

        dummyPet.name = "pet";
        dummyPet.petCannonName = "pet_cannon";
        dummyPet.logoName = "star_lib";
        dummyPet.cost = 42;
        dummyPet.amountBugsBeforeCharging = 3;
        dummyPet.totalEatenBugs = 0;

        ArrayList<PetJson> allPets = new ArrayList<PetJson>();
        allPets.add(dummyPet);

        Json jsonPetsObj = new Json();

        writeFile(PETS_FILE, jsonPetsObj.toJson(allPets));
    }

    public static void generateLevelsJson() {

        List<LevelInfo> levels = new ArrayList<>();
        LevelInfo l = new LevelInfo();
        l.difficultyLevel = 1;
        l.name = "Novice";

        LevelInfo l1 = new LevelInfo();
        l1.difficultyLevel = 2;
        l1.name = "Pro";

        LevelInfo l2 = new LevelInfo();
        l2.difficultyLevel = 3;
        l2.name = "Prorer Pro";

        LevelInfo l3 = new LevelInfo();
        l3.difficultyLevel = 4;
        l3.name = "Prorest Pro";

        levels.add(l);
        levels.add(l1);
        levels.add(l2);
        levels.add(l3);

        writeFile(LEVELS_JSON, new Json().toJson(levels));
    }

    public static void initAllLevels() {
        String saved = readFile(LEVELS_JSON);
        Level.levelsInfo = new ArrayList<>();
        List levels = new Json().fromJson(List.class, saved);
        for (Object l : levels) {
            Level.levelsInfo.add((LevelInfo) l);
        }
    }

    public static void initAllMultipliers() {
        String file = readFile(MULTIPLIERS_JSON);
        List<BugSpawnSystem.Multiplier> multipliers = new Json().fromJson(List.class, file);
        BugSpawnSystem.mulipliers = new ArrayList<>();
        for (BugSpawnSystem.Multiplier m : multipliers) {
            BugSpawnSystem.mulipliers.add(m);
        }
    }

    public static class LevelInfo {

        public static final String MONEY_50 = "MONEY_50";
        public static final String MONEY_100 = "MONEY_100";
        public static final String MONEY_150 = "MONEY_150";
        public static final String MONEY_200 = "MONEY_200";
        public static final String MONEY_250 = "MONEY_250";
        public static final String MONEY_300 = "MONEY_300";

        public static final String PET = "RAVEN";
        public static final String PET_2 = "CAT";
        public static final String PET_3 = "DRAGON";

        public static final String PHOENIX = "PHOENIX";
        public static final String BJ_DOUBLE = "BJ_DOUBLE";

        public String name;
        public int difficultyLevel;
        public String type;

        //WHY???
        public float spawnInterval = 1;
        public float breakFreqMin = 1;
        public float breakFreqMax = 1;
        public float breakLengthMin = 1;
        public float breakLengthMax = 1;
        public float simpleBugSpawnChance = 1;
        public float drunkBugSpawnChance = 1;
        public float chargerBugSpawnChance = 1;
        public float queenBeeSpawnChance = 1;
        public float beeSpawnChance = 1;

        public float simpleBugMoveDuration = 1;
        public float simpleBugAmplitude = 1;
        public float drunkBugMoveDuration = 1;
        public float drunkBugAmplitude = 1;
        public float beeMoveDuration = 1;
        public float beeAmplitude = 1;
        public float queenBeeMoveDuration = 1;
        public float queenBeeAmplitude = 1;
        public float chargerBugMove = 1;

        public int maxGoalsAmount = 5;
        public int minGoalsAmount = 3;
        public int easyGoalsAmount = 1;
        public int mediumGoalsAmount = 1;
        public int hardGoalsAmount = 1;
        public float goalMultiplier = 1.05f;

        public float prob_eat_n_bugs;
        public float prob_eat_n_drunks;
        public float prob_eat_n_chargers;
        public float prob_eat_n_simple;
        public float prob_eat_n_bees;
        public float prob_eat_n_queens;
        public float prob_eat_n_umrellas;
        public float prob_eat_n_butterflies;
        public float prob_destroy_n_cocoon;
        public float prob_bounce_umbrella_n_times;
        public float prob_tap;
        public float prob_survive_n_angered_modes;
        public float prob_spend_n_moneyz;
        public float prob_get_n_moneyz;
        public float prob_pet_the_pet_n_times;
        public float prob_pet_eat_n_bugs;
        public float prob_pet_dash_n_times;

        public int chanceMONEY_50 = 5;
        public int chanceMONEY_100 = 3;
        public int chanceMONEY_150 = 2;
        public int chanceMONEY_200 = 0;
        public int chanceMONEY_250 = 0;
        public int chancePHOENIX = 0;
        public int chancePET1 = 60;
        public int chancePET2 = 20;
        public int chancePET3 = 10;
        public int chanceBJ_DOUBLE = 0;

        public LevelInfo() {
        }

        public Map<String, Integer> getRewardChanceGroups() {
            Map<String, Integer> rewardChanceGroups = new HashMap<>();
            rewardChanceGroups.put(MONEY_50, chanceMONEY_50);
            rewardChanceGroups.put(MONEY_100, chanceMONEY_100 + chanceMONEY_50);
            rewardChanceGroups.put(MONEY_150, chanceMONEY_150 + chanceMONEY_100 + chanceMONEY_50);
            rewardChanceGroups.put(MONEY_200, chanceMONEY_200 + chanceMONEY_150 + chanceMONEY_100 + chanceMONEY_50);
            rewardChanceGroups.put(MONEY_250, chanceMONEY_250 + chanceMONEY_200 + chanceMONEY_150 + chanceMONEY_100 + chanceMONEY_50);
            rewardChanceGroups.put(PHOENIX, chancePHOENIX + chanceMONEY_250 + chanceMONEY_200 + chanceMONEY_150 + chanceMONEY_100 + chanceMONEY_50);
            rewardChanceGroups.put(PET, chancePET1 + chancePHOENIX + chanceMONEY_250 + chanceMONEY_200 + chanceMONEY_150 + chanceMONEY_100 + chanceMONEY_50);
            rewardChanceGroups.put(PET_2, chancePET2 + chancePET1 + chancePHOENIX + chanceMONEY_250 + chanceMONEY_200 + chanceMONEY_150 + chanceMONEY_100 + chanceMONEY_50);
            rewardChanceGroups.put(PET_3, chancePET3 + chancePET2 + chancePET1 + chancePHOENIX + chanceMONEY_250 + chanceMONEY_200 + chanceMONEY_150 + chanceMONEY_100 + chanceMONEY_50);
            rewardChanceGroups.put(BJ_DOUBLE, chanceBJ_DOUBLE + chancePET3 + chancePET2 + chancePET1 + chancePHOENIX + chanceMONEY_250 + chanceMONEY_200 + chanceMONEY_150 + chanceMONEY_100 + chanceMONEY_50);
            return rewardChanceGroups;
        }
    }

    private static class GameStats {
        public boolean goalStatusChanged;
        public boolean noAds;
        public boolean noSound;
        public boolean noMusic;
        public long bestScore;
        public long totalScore;
        public long curDay;
        public List<DailyGoalStats> goals = new ArrayList<DailyGoalStats>();
        public PetJson currentPet;
        public List<UpgradeStats> upgrades;
        public int totalPlayedGames;

        public int reviveAd_max;
        public int gameOverReviveTimesLimit;

        //achievements
        public int queenAchGoal;
        public int bugAchGoal;
        public int butterflyAchGoal;

        public int queenAchCounter;
        public int bugAchCounter;
        public int butterflyAchCounter;

        public List<VanityJson> boughtVanities = new ArrayList<>();
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
        public String sku;
        public String sku_discount;
        public String upgradeType;
        public String name;
        public long cost;
        public long disc;
        public long costDisc;
        public String description;
        public String collection;
        public String currencyType;
        public boolean tryPeriod;
        public long tryPeriodDuration;
        public long tryPeriodStart;
        public long tryPeriodTimer;
        public boolean bought;
        public boolean enabled;
        public String shopIcon;

        public UpgradeStats() {
        }

        public UpgradeStats(Upgrade us) {
            this.upgradeType = us.upgradeType.toString();
            this.tryPeriod = us.tryPeriod;
            this.tryPeriodDuration = us.tryPeriodDuration;
            this.tryPeriodTimer = us.tryPeriodTimer;
            this.tryPeriodStart = us.tryPeriodStart;
            this.bought = us.bought;
            this.enabled = us.enabled;
            this.shopIcon = us.shopIcon;
            this.name = us.name;
            this.cost = us.cost;
            this.disc = us.disc;
            this.costDisc = us.costDisc;
            this.description = us.description;
            this.collection = us.collection;
            this.currencyType = us.currencyType;
            this.sku = us.sku;
            this.sku_discount = us.sku_discount;
        }
    }

    public static class VanityJson {
        public Map<String, String> assetsToChange = new HashMap<String, String>();

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
        public boolean leaves;
        //        public boolean changeFlower;
        public String collection;
        public String title;

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
            this.leaves = vc.changeLeaves;
            this.floatingText = vc.floatingText;
            this.bugsSpawnAmount = vc.bugsSpawnAmount;
            this.attackSpeed = vc.attackSpeed;
            this.cocoonChance = vc.cocoonChance;
            this.dandelionChance = vc.dandelionChance;
            this.angeredBeesDuration = vc.angeredBeesDuration;
            this.pet = vc.pet != null ? new PetJson(vc.pet) : null;
            this.collection = vc.collection;
        }
    }

    public static class PetJson {
        public String sku;
        public String sku_discount;
        public String name;
        public String description;
        public boolean bought;
        public boolean activated;
        public long cost;
        public long costDisc;
        public long disc;
        public boolean tryPeriod;
        public long tryPeriodDuration;
        public int amountBugsBeforeCharging;
        public int totalEatenBugs;
        public String shopIcon;
        public long tryPeriodTimer;
        public long tryPeriodStart;
        public String petCannonName;
        public String petHeadName;
        public String logoName;
        public int projectileSpawnIntervalFrames;
        public boolean isHardCurr;

        public PetJson() {
        }

        public PetJson(PetComponent petComponent) {
            this.name = petComponent.name;
            this.description = petComponent.description;
            this.activated = petComponent.enabled;
            this.bought = petComponent.bought;
            this.cost = petComponent.cost;
            this.costDisc = petComponent.costDisc;
            this.disc = petComponent.disc;
            this.projectileSpawnIntervalFrames = petComponent.projectileSpawnIntervalFrames;
            this.amountBugsBeforeCharging = petComponent.amountBugsBeforeCharging;
            this.totalEatenBugs = petComponent.totalEatenBugs;
            this.shopIcon = petComponent.shopIcon;
            this.isHardCurr = petComponent.isHardCurr;

            this.tryPeriod = petComponent.tryPeriod;
//            this.logoName = petComponent.logoName;

            this.tryPeriodDuration = petComponent.tryPeriodDuration;
            this.tryPeriodTimer = petComponent.tryPeriodTimer;
            this.tryPeriodStart = petComponent.tryPeriodStart;
            this.sku = petComponent.sku;
            this.sku_discount = petComponent.sku_discount;

            this.petCannonName = petComponent.petCannonName;
//            this.petHeadName = petComponent.petHeadName;
        }
    }

    public static void initCocoonMultipliers(int index) {
        String file = readFile(COCOON_MULTIPLIERS_JSON);
        List<CocoonComponent.CocoonMultiplier> multipliers = new Json().fromJson(List.class, file);
        CocoonSystem.cocoonMultipliers = multipliers;
        CocoonSystem.currentCocoonMultiplier = multipliers.get(index);
    }

    public static void initDandelionMultipliers(int index) {
        String file = readFile(DANDELION_MULTIPLIERS_JSON);
        List<UmbrellaComponent.DandelionMultiplier> multipliers = new Json().fromJson(List.class, file);
        UmbrellaComponent.multipliers = multipliers;
        //UmbrellaComponent.currentMultiplier = multipliers.get(index); // the value has to change for fuck sake

        if(index >= 0 && index < multipliers.size()) {
            UmbrellaComponent.currentMultiplier = multipliers.get(index); // the value has to change for fuck sake
        }else{
            UmbrellaComponent.currentMultiplier = multipliers.get(multipliers.size()-1);
        }

    }
}