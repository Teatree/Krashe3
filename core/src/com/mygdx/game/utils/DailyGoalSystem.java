package com.mygdx.game.utils;

import com.mygdx.game.entity.componets.Goal;
import com.mygdx.game.stages.GameScreenScript;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class DailyGoalSystem {
    public static final int MAX_GOALS_AMOUNT = 3;
    public static Calendar latestDate;
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
        if(GameScreenScript.fpc.level < 4){
            return new Goal(Goal.GoalType.getEasyGoals().get(r.nextInt(Goal.GoalType.getEasyGoals().size() - 1)));
        }else if (GameScreenScript.fpc.level >= 4 && GameScreenScript.fpc.level < 8) {
            return new Goal(Goal.GoalType.getMediumGoals().get(r.nextInt(Goal.GoalType.getMediumGoals().size() - 1)));
        }else{
            return new Goal(Goal.GoalType.getHardGoals().get(r.nextInt(Goal.GoalType.getHardGoals().size() - 1)));
        }
    }
}
