package com.mygdx.game.utils;

import com.mygdx.game.entity.componets.Goal;

import java.util.HashMap;
import java.util.Random;

import static com.mygdx.game.entity.componets.Goal.GoalType.*;
import static com.mygdx.game.stages.GameScreenScript.fpc;

public class DailyGoalSystem {
    public static final int MAX_GOALS_AMOUNT = 3;
    public Random random;
    HashMap<Goal.GoalType, Goal> goals;

    public DailyGoalSystem() {
    }

    public HashMap<Goal.GoalType, Goal> getGoals() {
        goals = new HashMap<>();
        for (int i = 0; i < MAX_GOALS_AMOUNT; i++) {
            Goal dg = createGoal();
            goals.put(dg.type,dg);
        }
        return goals;
    }

    private Goal createGoal() {
        Random r = new Random();
        if (fpc.level.difficultyLevel < 4) {
            return new Goal(getEasyGoals().get(r.nextInt(getEasyGoals().size() - 1)));
        } else if (fpc.level.difficultyLevel >= 4 && fpc.level.difficultyLevel < 8) {
            return new Goal(getMediumGoals().get(r.nextInt(getMediumGoals().size() - 1)));
        }else{
            return new Goal(getHardGoals().get(r.nextInt(getHardGoals().size() - 1)));
        }
    }
}
