package com.mygdx.etf.entity.componets;

import com.mygdx.etf.stages.GameStage;
import com.mygdx.etf.utils.GoalGenerator;
import com.mygdx.etf.utils.SaveMngr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Level {

    public static  boolean goalStatusChanged;

    public static List<SaveMngr.LevelInfo> levelsInfo;
    public int difficultyLevel;
    public String name;
    public HashMap<Goal.GoalType, Goal> goals = new HashMap<>();
    public GoalGenerator goalGenerator = new GoalGenerator();

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

    public int maxGoalsAmount = 3;
    public int minGoalsAmount = 5;
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

    public Map<String, Integer> rewardChanceGroups;

    public Level() {
        name = levelsInfo.get(difficultyLevel).name;
        spawnInterval = levelsInfo.get(difficultyLevel).spawnInterval;
        breakFreqMin = levelsInfo.get(difficultyLevel).breakFreqMin;
        breakFreqMax = levelsInfo.get(difficultyLevel).breakFreqMax;
        breakLengthMin = levelsInfo.get(difficultyLevel).breakLengthMin;
        breakLengthMax = levelsInfo.get(difficultyLevel).breakLengthMax;
        simpleBugSpawnChance = levelsInfo.get(difficultyLevel).simpleBugSpawnChance;
        drunkBugSpawnChance = levelsInfo.get(difficultyLevel).drunkBugSpawnChance;
        chargerBugSpawnChance = levelsInfo.get(difficultyLevel).chargerBugSpawnChance;
        queenBeeSpawnChance = levelsInfo.get(difficultyLevel).queenBeeSpawnChance;
        beeSpawnChance = levelsInfo.get(difficultyLevel).beeSpawnChance;

        simpleBugMoveDuration = levelsInfo.get(difficultyLevel).simpleBugMoveDuration;
        simpleBugAmplitude = levelsInfo.get(difficultyLevel).simpleBugAmplitude;
        drunkBugMoveDuration = levelsInfo.get(difficultyLevel).drunkBugMoveDuration;
        drunkBugAmplitude = levelsInfo.get(difficultyLevel).drunkBugAmplitude;
        beeMoveDuration = levelsInfo.get(difficultyLevel).beeMoveDuration;
        beeAmplitude = levelsInfo.get(difficultyLevel).beeAmplitude;
        queenBeeMoveDuration = levelsInfo.get(difficultyLevel).queenBeeMoveDuration;
        queenBeeAmplitude = levelsInfo.get(difficultyLevel).queenBeeAmplitude;
        chargerBugMove = levelsInfo.get(difficultyLevel).chargerBugMove;

        maxGoalsAmount = levelsInfo.get(difficultyLevel).maxGoalsAmount;
        minGoalsAmount = levelsInfo.get(difficultyLevel).minGoalsAmount;
        easyGoalsAmount = levelsInfo.get(difficultyLevel).easyGoalsAmount;
        mediumGoalsAmount = levelsInfo.get(difficultyLevel).mediumGoalsAmount;
        hardGoalsAmount = levelsInfo.get(difficultyLevel).hardGoalsAmount;

        prob_eat_n_bugs = levelsInfo.get(difficultyLevel).prob_eat_n_bugs;
        prob_eat_n_drunks = levelsInfo.get(difficultyLevel).prob_eat_n_drunks;
        prob_eat_n_chargers = levelsInfo.get(difficultyLevel).prob_eat_n_chargers;
        prob_eat_n_simple = levelsInfo.get(difficultyLevel).prob_eat_n_simple;
        prob_eat_n_bees = levelsInfo.get(difficultyLevel).prob_eat_n_bees;
        prob_eat_n_queens = levelsInfo.get(difficultyLevel).prob_eat_n_queens;
        prob_eat_n_umrellas = levelsInfo.get(difficultyLevel).prob_eat_n_umrellas;
        prob_eat_n_butterflies = levelsInfo.get(difficultyLevel).prob_eat_n_butterflies;
        prob_destroy_n_cocoon = levelsInfo.get(difficultyLevel).prob_destroy_n_cocoon;
        prob_bounce_umbrella_n_times = levelsInfo.get(difficultyLevel).prob_bounce_umbrella_n_times;
        prob_tap = levelsInfo.get(difficultyLevel).prob_tap;
        prob_survive_n_angered_modes = levelsInfo.get(difficultyLevel).prob_survive_n_angered_modes;
        prob_spend_n_moneyz = levelsInfo.get(difficultyLevel).prob_spend_n_moneyz;
        prob_get_n_moneyz = levelsInfo.get(difficultyLevel).prob_get_n_moneyz;
        prob_pet_the_pet_n_times = levelsInfo.get(difficultyLevel).prob_pet_the_pet_n_times;
        prob_pet_eat_n_bugs = levelsInfo.get(difficultyLevel).prob_pet_eat_n_bugs;
        prob_pet_dash_n_times = levelsInfo.get(difficultyLevel).prob_pet_dash_n_times;
        rewardChanceGroups = levelsInfo.get(difficultyLevel).rewardChanceGroups;
    }

    public Goal getGoalByType(Goal.GoalType type) {
        return goals.get(type);
    }

    public boolean checkAllGoals() {
        // Fucking weird ass method
        // If you put the 1st goal to achieved: true
        // AllAchieved will be true and new goals will be generated
        boolean allAchieved = true;
        for (Goal goal : goals.values()) {
            allAchieved = allAchieved && goal.achieved;
        }
        return allAchieved;
    }

    public List<Goal> getGoals() {
        return new ArrayList<>(goals.values());
    }

    public void updateLevel(FlowerPublicComponent fpc) {
        if (checkAllGoals()) {
            resetNewInfo();
            goals = goalGenerator.getGoals(fpc);
        }
    }

    public void resetNewInfo() {
        if (difficultyLevel < levelsInfo.size()) {
            difficultyLevel++;
            SaveMngr.LevelInfo info = levelsInfo.get(difficultyLevel - 1);
            name = info.name;
            spawnInterval = info.spawnInterval;
            breakFreqMin = info.breakFreqMin;
            breakFreqMax = info.breakFreqMax;
            breakLengthMin = info.breakLengthMin;
            breakLengthMax = info.breakLengthMax;
            simpleBugSpawnChance = info.simpleBugSpawnChance;
            drunkBugSpawnChance = info.drunkBugSpawnChance;
            chargerBugSpawnChance = info.chargerBugSpawnChance;
            queenBeeSpawnChance = info.queenBeeSpawnChance;
            beeSpawnChance = info.beeSpawnChance;

            simpleBugMoveDuration = info.simpleBugMoveDuration;
            simpleBugAmplitude = info.simpleBugAmplitude;
            drunkBugMoveDuration = info.drunkBugMoveDuration;
            drunkBugAmplitude = info.drunkBugAmplitude;
            beeMoveDuration = info.beeMoveDuration;
            beeAmplitude = info.beeAmplitude;
            queenBeeMoveDuration = info.queenBeeMoveDuration;
            queenBeeAmplitude = info.queenBeeAmplitude;
            chargerBugMove = info.chargerBugMove;

            maxGoalsAmount = info.maxGoalsAmount;
            minGoalsAmount = info.minGoalsAmount;
            easyGoalsAmount = info.easyGoalsAmount;
            mediumGoalsAmount = info.mediumGoalsAmount;
            hardGoalsAmount = info.hardGoalsAmount;
            rewardChanceGroups = info.rewardChanceGroups;
        }
    }

    public String getRemainingGoals() {
        int remainingCounter = 0;
        for (Goal g : goals.values()){
            if (!g.achieved){
                remainingCounter++;
            }
        }
        return String.valueOf(remainingCounter);
    }
}
