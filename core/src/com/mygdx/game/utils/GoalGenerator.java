package com.mygdx.game.utils;

import com.mygdx.game.entity.componets.Goal;
import com.mygdx.game.stages.GameStage;

import java.util.HashMap;
import java.util.Random;

import static com.mygdx.game.entity.componets.Goal.GoalType.*;

public class GoalGenerator {

    public static final int MAX_GOALS_AMOUNT = 5;
    public static final int MIN_GOALS_AMOUNT = 3;
    public Random random;
    HashMap<Goal.GoalType, Goal> goals;

    public GoalGenerator() {
       random = new Random();
    }

    public HashMap<Goal.GoalType, Goal> getGoals() {
        goals = new HashMap<>();
        int goalsAmount = random.nextInt(MAX_GOALS_AMOUNT - MIN_GOALS_AMOUNT) + MIN_GOALS_AMOUNT;
        for (int i = 0; i <= goalsAmount; i++) {
            Goal dg = createGoal();
            goals.put(dg.type,dg);
        }
        return goals;
    }

    private Goal createGoal() {
        if (GameStage.gameScript.fpc.level.difficultyLevel < 4) {
            return new Goal(getEasyGoals().get(random.nextInt(getEasyGoals().size() - 1)));
        } else if (GameStage.gameScript.fpc.level.difficultyLevel >= 4 && GameStage.gameScript.fpc.level.difficultyLevel < 8) {
            return new Goal(getMediumGoals().get(random.nextInt(getMediumGoals().size() - 1)));
        }else{
            return new Goal(getHardGoals().get(random.nextInt(getHardGoals().size() - 1)));
        }
    }
}
