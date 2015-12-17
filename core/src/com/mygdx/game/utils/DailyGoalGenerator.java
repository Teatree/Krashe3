package com.mygdx.game.utils;

import com.mygdx.game.entity.componets.DailyGoal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by AnastasiiaRudyk on 12/16/2015.
 */
public class DailyGoalGenerator {
    public static final int GOALS_AMOUNT_FOR_ONE_DAY = 3;
    public Random random;
    public Calendar latestDate;
    List<DailyGoal> goals;

    public DailyGoalGenerator() {
        latestDate = Calendar.getInstance();
        latestDate.set(2012, 12, 15);
    }

    public List<DailyGoal> getGoalsForToday(){

        Calendar cal1 = Calendar.getInstance();
        boolean sameDay = cal1.get(Calendar.YEAR) == latestDate.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == latestDate.get(Calendar.DAY_OF_YEAR);

        if (!sameDay){
            goals = new ArrayList<>();
            for (int i = 0; i < GOALS_AMOUNT_FOR_ONE_DAY; i++){
                goals.add(createGoal());
            }
        }
        return goals;
    }

    private DailyGoal createGoal(){
        DailyGoal goal = new DailyGoal();
        goal.achieved = false;
        goal.description = "We are the same :(";
        return goal;
    }
}
