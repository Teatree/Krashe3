package com.mygdx.game.utils;

import com.mygdx.game.entity.componets.DailyGoal;
import com.mygdx.game.stages.GameScreenScript;

import java.util.*;

public class DailyGoalSystem {
    public static final int MAX_GOALS_AMOUNT = 3;
    public Random random;
    public static Calendar latestDate;
    HashMap<DailyGoal.GoalType, DailyGoal> goals;

    public DailyGoalSystem() {
    }

    public HashMap<DailyGoal.GoalType, DailyGoal> getGoals(){
        goals = new HashMap<>();
        for (int i = 0; i < MAX_GOALS_AMOUNT; i++) {
            DailyGoal dg = createGoal();
            goals.put(dg.type,dg);
        }
        return goals;
    }

    private DailyGoal createGoal(){
        Random r = new Random();
        if(GameScreenScript.fpc.level < 4){
            return new DailyGoal(DailyGoal.GoalType.getEasyGoals().get(r.nextInt(DailyGoal.GoalType.getEasyGoals().size() - 1)));
        }else if (GameScreenScript.fpc.level >= 4 && GameScreenScript.fpc.level < 8) {
            return new DailyGoal(DailyGoal.GoalType.getMediumGoals().get(r.nextInt(DailyGoal.GoalType.getMediumGoals().size() - 1)));
        }else{
            return new DailyGoal(DailyGoal.GoalType.getHardGoals().get(r.nextInt(DailyGoal.GoalType.getHardGoals().size() - 1)));
        }
    }
}
