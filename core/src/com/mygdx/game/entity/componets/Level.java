package com.mygdx.game.entity.componets;

import com.mygdx.game.utils.GoalGenerator;
import com.mygdx.game.utils.SaveMngr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Level {

    public static List<SaveMngr.LevelInfo> levelsInfo;
    public int difficultyLevel;
    public float difficultyMultiplier;
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
    }

    public Goal getGoalByType(Goal.GoalType type) {
        return goals.get(type);
    }

    public boolean checkAllGoals() {
        boolean allAchieved = true;
        for (Goal goal : goals.values()) {
            allAchieved = allAchieved && goal.achieved;
        }
        return allAchieved;
    }

    public List<Goal> getGoals() {
        return new ArrayList<>(goals.values());
    }

    public void updateLevel() {
        if (checkAllGoals()) {
            resetNewInfo();
            goals = goalGenerator.getGoals();
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
        }
    }
}
