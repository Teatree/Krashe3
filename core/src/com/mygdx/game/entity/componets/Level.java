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

    public Level() {
        name = levelsInfo.get(difficultyLevel).name;
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
            difficultyMultiplier = info.difficultyMultiplier;
        }
    }
}
