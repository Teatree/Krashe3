package com.mygdx.etf.entity.componets;


import com.mygdx.etf.stages.GameStage;
import com.mygdx.etf.stages.ui.GoalFeedbackScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static com.mygdx.etf.entity.componets.GoalConstants.*;

/**
 * goals:
 * 1. eat n butterflies +
 * 2. get n points +
 * 3. eat n umbrella +
 * 4. bounce umbrella +
 * 5. eat n queens +
 * 6. survive n angered bees mode +
 * 7. eat n bees +`
 * 8. eat n charge +
 * 9. eat n drunks +
 * 10. eat n simple bugs +
 * 11. eat n bugs +
 * 12. TAP +
 * 13. EAT_N_BUG_UPPER +
 * 14. EAT_N_BUG_LOWER +
 * 15. PET_THE_PET +
 * 16. PET_EAT_N_BUGS
 * 17. PET_DASH_N_TIMES +
 * 18. DESTROY_N_COCOON +
 */

public class Goal {

    public static HashMap<Integer, PeriodType> periodTypeMap;
    public static HashMap<Integer, PetComponent> petType;

    public int counter;
    public boolean achieved;
    public boolean justAchieved;
    public String description;
    public GoalType type;
    public PeriodType periodType;
    public int n;
    public PetComponent pet;
    private Random random = new Random();

    public Goal() {
    }

    public Goal(GoalType goalType, int difficulty) {
        this.type = goalType;
        description = goalType.desc;
        periodType = periodTypeMap.get(random.nextInt(goalType.periodTypeMax));
        if(periodType == PeriodType.TOTAL) {
            if (getbParameters().get(goalType).get(difficulty*2 + 1) != getbParameters().get(goalType).get(difficulty*2)) {
                n = random.nextInt(getbParameters().get(goalType).get(difficulty * 2 + 1) - getbParameters().get(goalType).get(difficulty * 2)) + getbParameters().get(goalType).get(difficulty * 2);
            }else{
                n = getbParameters().get(goalType).get(difficulty*2);
            }
        }else{
            if (getbParameters().get(goalType).get(difficulty*2 + 7) != getbParameters().get(goalType).get(difficulty*2+6)) {
                n = random.nextInt(getbParameters().get(goalType).get(difficulty * 2 + 7) - getbParameters().get(goalType).get(difficulty * 2 + 6)) + getbParameters().get(goalType).get(difficulty * 2 + 6);
            }else{
                n = getbParameters().get(goalType).get(difficulty * 2 + 6);
            }
        }
        n *= GameStage.gameScript.fpc.level.goalMultiplier;
    }

    public static void init(FlowerPublicComponent fpc) {
        periodTypeMap = new HashMap<>();
        periodTypeMap.put(0, PeriodType.IN_ONE_LIFE);
        periodTypeMap.put(1, PeriodType.TOTAL);

        petType = new HashMap<>();
        int ba = 0;
        for (PetComponent p : fpc.pets) {
            petType.put(ba++, p);
        }
    }

    public void update() {
        counter++;
        if (counter >= n && !achieved) {
            Level.goalStatusChanged = true;
            achieved = true;
            justAchieved = true;
            if (!GoalFeedbackScreen.shouldShow) {
                GoalFeedbackScreen.shouldShow = true;
            }
        }
    }

    public String getDescription() {
        return description.replace("#", " " + n + " ") + " " + periodType;
    }

    public int getN() {
        return n;
    }

    public int getCounter() {
        return counter;
    }

    public enum PeriodType {
        IN_ONE_LIFE(5),
        TOTAL(1);

        public int adjustByTypeDivider;

        PeriodType(int divider) {
            this.adjustByTypeDivider = divider;
        }
    }

    public enum GoalType {
        EAT_N_BUGS(EAT_N_BUGS_DESC, 2),
        GET_N_POINTS(GET_N_POINTS_DESC, 2),
        SPEND_MONEYZ(SPEND_MONEYZ_DESC, 1),
        EAT_N_DRUNKS(EAT_N_DRUNKS_DESC, 2),
        EAT_N_CHARGERS(EAT_N_CHARGERS_DESC, 2),
        EAT_N_SIMPLE(EAT_N_SIMPLE_DESC, 2),
        EAT_N_BEES(EAT_N_BEES_DESC, 2),
        DESTROY_N_COCOON(DESTROY_N_COCOON_DESC, 2),
        EAT_N_UMBRELLA(EAT_N_UMBRELLA_DESC, 2),
        EAT_N_BUTTERFLIES(EAT_N_BUTTERFLIES_DESC, 2),
        BOUNCE_UMBRELLA_N_TIMES(BOUNCE_UMBRELLA_N_TIMES_DESC, 1),
        TAP(TAP_DESC, 2),
        EAT_N_QUEENS(EAT_N_QUEENS_DESC, 2),
        SURVIVE_N_ANGERED_MODES(SURVIVE_N_ANGERED_MODES_DESC, 2),
        PET_THE_PET(PET_THE_PET_DESC, 2),
        PET_EAT_N_BUGS(PET_EAT_N_BUGS_DESC, 2),
        PET_DASH_N_TIMES(PET_CHARGE_N_TIMES_DESC, 2);

        public String desc;
        public int periodTypeMax;


        GoalType( String desc, int periodTypeMax) {

            this.desc = desc;
            this.periodTypeMax = periodTypeMax;
        }

        public static List<GoalType> getEasyGoals() {
            List<GoalType> result = new ArrayList<>();
            result.add(EAT_N_BUGS);
            result.add(SPEND_MONEYZ);
            result.add(GET_N_POINTS);
            result.add(EAT_N_DRUNKS);
            result.add(EAT_N_CHARGERS);
            result.add(EAT_N_SIMPLE);
            result.add(EAT_N_BEES);
            result.add(DESTROY_N_COCOON);
            result.add(EAT_N_UMBRELLA);
            result.add(EAT_N_BUTTERFLIES);
            result.add(BOUNCE_UMBRELLA_N_TIMES);
            result.add(TAP);
            result.add(PET_DASH_N_TIMES);

            return result;
        }

        public static List<GoalType> getMediumGoals() {
            List<GoalType> result = new ArrayList<>();
            result.add(EAT_N_BUGS);
            result.add(SPEND_MONEYZ);
            result.add(GET_N_POINTS);
            result.add(EAT_N_DRUNKS);
            result.add(EAT_N_CHARGERS);
            result.add(EAT_N_SIMPLE);
            result.add(EAT_N_BEES);
            result.add(DESTROY_N_COCOON);
            result.add(EAT_N_UMBRELLA);
            result.add(EAT_N_BUTTERFLIES);
            result.add(BOUNCE_UMBRELLA_N_TIMES);
            result.add(TAP);
            result.add(EAT_N_QUEENS);
            result.add(SURVIVE_N_ANGERED_MODES);
            result.add(PET_DASH_N_TIMES);

            return result;
        }

        public static List<GoalType> getHardGoals() {
            List<GoalType> result = new ArrayList<>();
            result.add(EAT_N_BUGS);
            result.add(SPEND_MONEYZ);
            result.add(GET_N_POINTS);
            result.add(EAT_N_DRUNKS);
            result.add(EAT_N_CHARGERS);
            result.add(EAT_N_SIMPLE);
            result.add(EAT_N_BEES);
            result.add(DESTROY_N_COCOON);
            result.add(EAT_N_UMBRELLA);
            result.add(EAT_N_BUTTERFLIES);
            result.add(BOUNCE_UMBRELLA_N_TIMES);
            result.add(TAP);
            result.add(EAT_N_QUEENS);
            result.add(SURVIVE_N_ANGERED_MODES);
            result.add(PET_EAT_N_BUGS);
            result.add(PET_DASH_N_TIMES);
            result.add(PET_THE_PET);
            result.add(PET_DASH_N_TIMES);

            return result;
        }
    }
}
