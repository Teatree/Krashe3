package com.mygdx.game.utils;

import com.mygdx.game.entity.componets.DailyGoal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by AnastasiiaRudyk on 12/16/2015.
 */
public class DailyGoalSystem {
    public static final int GOALS_AMOUNT_FOR_ONE_DAY = 3;
    public Random random;
    public static Calendar latestDate;
    List<DailyGoal> goals;

    public DailyGoalSystem() {
//        if (latestDate == null)
//        latestDate = Calendar.getInstance();
    }

    public List<DailyGoal> getGoalsForToday(){
        Calendar today = Calendar.getInstance();
        boolean sameDay = false;
        if (latestDate !=null) {
            sameDay = today.get(Calendar.YEAR) == latestDate.get(Calendar.YEAR) &&
                    today.get(Calendar.DAY_OF_YEAR) == latestDate.get(Calendar.DAY_OF_YEAR);
        }
        if (!sameDay){
            latestDate = today;
            goals = new ArrayList<>();
            for (int i = 0; i < GOALS_AMOUNT_FOR_ONE_DAY; i++){
                goals.add(createGoal());
            }
        }
        return goals;
    }

    private DailyGoal createGoal(){
        Random r = new Random();
        DailyGoal goal = new DailyGoal(DailyGoal.GoalType.values()[r.nextInt(DailyGoal.GoalType.values().length-1)]);
        return goal;
    }
}
