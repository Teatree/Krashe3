package com.mygdx.game.entity.componets;

import com.mygdx.game.stages.GameScreenScript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static com.mygdx.game.entity.componets.GoalConstants.*;

/**
 * goals:
 * 1. eat n butterflies +
 * 2. get n points
 * 3. eat n dandelions
 * 4. bounce umbrella
 * 5. eat n queens
 * 6. survive n angered bees mode
 * 7. eat n bees +`
 * 8. eat n charge +
 * 9. eat n drunks +
 * 10. eat n simple bugs +
 * 11. eat n bugs +
 * 12. TAP
 * 13. EAT_N_BUG_UPPER
 * 14. EAT_N_BUG_LOWER
 * 15. PET_THE_PET
 * 16. PET_EAT_N_BUGS
 * 17. PET_CHARGE_N_TIMES
 */

public class Goal {

    public static HashMap<Integer, PeriodType> periodTypeMap;
    public static HashMap<Integer, PetComponent> petType;
    public static HashMap<Integer, Float> levelMultipliers;
    public int counter;
    public boolean achieved;
    public String description;
    public GoalType type;
    public PeriodType periodType;
    public int n;
    public PetComponent pet;
    private Random random = new Random();

    public Goal() {
    }

    public Goal(GoalType goalType) {
        this.type = goalType;
        n = random.nextInt(goalType.max - goalType.min) + goalType.min;
        description = goalType.desc;
        periodType = periodTypeMap.get(random.nextInt(goalType.periodTypeMax));
        n /= periodType.adjustByTypeDivider;
        n = changeByLevel(GameScreenScript.fpc.level, n);
    }

    public static void init(FlowerPublicComponent fpc) {
        periodTypeMap = new HashMap<>();
        periodTypeMap.put(0, PeriodType.IN_ONE_LIFE);
        periodTypeMap.put(1, PeriodType.IN_TOTAL);
        periodTypeMap.put(2, PeriodType.IN_A_ROW);

        levelMultipliers = new HashMap<>();
        levelMultipliers.put(0, 1.03f);
        levelMultipliers.put(1, 1.26f);
        levelMultipliers.put(2, 1.8f);
        levelMultipliers.put(3, 2.6f);
        levelMultipliers.put(4, 3.4f);
        levelMultipliers.put(5, 4.2f);
        levelMultipliers.put(6, 5f);
        levelMultipliers.put(7, 5.8f);
        levelMultipliers.put(8, 6.6f);
        levelMultipliers.put(9, 7.4f);
        levelMultipliers.put(10, 8.2f);

        petType = new HashMap<>();
        int ba = 0;
        for (PetComponent p : fpc.pets) {
            petType.put(ba++, p);
        }
    }

    public void update() {
        counter++;
        if (counter == n) {
            achieved = true;
        }
    }

    public void updateInARowGoals(BugComponent bc) {
        if (periodType.equals(PeriodType.IN_A_ROW)) {
            if (type.equals(GoalType.EAT_N_DRUNKS) && !bc.type.equals(BugType.DRUNK)) {
                counter = 0;
            }
            if (type.equals(GoalType.EAT_N_BEES) && !bc.type.equals(BugType.BEE)) {
                counter = 0;
            }
            if (type.equals(GoalType.EAT_N_CHARGERS) && !bc.type.equals(BugType.CHARGER)) {
                counter = 0;
            }
            if (type.equals(GoalType.EAT_N_SIMPLE) && !bc.type.equals(BugType.SIMPLE)) {
                counter = 0;
            }
        }
    }

    public int changeByLevel(int level, int n) {
        return Math.round(n * levelMultipliers.get(level));
    }

    public enum PeriodType {
        IN_A_ROW(10),
        IN_ONE_LIFE(5),
        IN_TOTAL(1);

        public int adjustByTypeDivider;

        PeriodType(int divider) {
            this.adjustByTypeDivider = divider;
        }
    }

    public enum GoalType {
        EAT_N_BUGS(EAT_N_BUGS_MIN, EAT_N_BUGS_MAX, EAT_N_BUGS_DESC, 3),
        GET_N_POINTS(GET_N_POINTS_MIN, GET_N_POINTS_MAX, GET_N_POINTS_DESC, 2),
        EAT_N_DRUNKS(EAT_N_DRUNKS_MIN, EAT_N_DRUNKS_MAX, EAT_N_DRUNKS_DESC, 2),
        EAT_N_CHARGERS(EAT_N_CHARGERS_MIN, EAT_N_CHARGERS_MAX, EAT_N_CHARGERS_DESC, 2),
        EAT_N_SIMPLE(EAT_N_SIMPLE_MIN, EAT_N_SIMPLE_MAX, EAT_N_SIMPLE_DESC, 2),
        EAT_N_BEES(EAT_N_BEES_MIN, EAT_N_BEES_MAX, EAT_N_BEES_DESC, 2),
        DESTROY_N_COCOON(DESTROY_N_COCOON_MIN, DESTROY_N_COCOON_MAX, DESTROY_N_COCOON_DESC, 2),
        EAT_N_UMBRELLA(EAT_N_UMBRELLA_MIN, EAT_N_UMBRELLA_MAX, EAT_N_UMBRELLA_DESC, 2),
        EAT_N_BUTTERFLIES(EAT_N_BUTTERFLIES_MIN, EAT_N_BUTTERFLIES_MAX, EAT_N_BUTTERFLIES_DESC, 2),
        BOUNCE_UMBRELLA_N_TIMES(BOUNCE_UMBRELLA_N_TIMES_MIN, BOUNCE_UMBRELLA_N_TIMES_MAX, BOUNCE_UMBRELLA_N_TIMES_DESC, 1),
        TAP(TAP_MIN, TAP_MAX, TAP_DESC, 2),
        EAT_N_BUG_UPPER(EAT_N_BUG_UPPER_MIN, EAT_N_BUG_UPPER_MAX, EAT_N_BUG_UPPER_DESC, 2),
        EAT_N_BUG_LOWER(EAT_N_BUG_LOWER_MIN, EAT_N_BUG_LOWER_MAX, EAT_N_BUG_LOWER_DESC, 2),
        EAT_N_QUEENS(EAT_N_QUEENS_MIN, EAT_N_QUEENS_MAX, EAT_N_QUEENS_DESC, 2),
        SURVIVE_N_ANGERED_MODES(SURVIVE_N_ANGERED_MODES_MIN, SURVIVE_N_ANGERED_MODES_MAX, SURVIVE_N_ANGERED_MODES_DESC, 2),
        PET_THE_PET(PET_THE_PET_MIN, PET_THE_PET_MAX, PET_THE_PET_DESC, 2),
        PET_EAT_N_BUGS(PET_EAT_N_BUGS_MIN, PET_EAT_N_BUGS_MAX, PET_EAT_N_BUGS_DESC, 3),
        PET_CHARGE_N_TIMES(PET_CHARGE_N_TIMES_MIN, PET_CHARGE_N_TIMES_MAX, PET_CHARGE_N_TIMES_DESC, 2);

        public int min;
        public int max;
        public String desc;
        public int periodTypeMax;


        GoalType(int min, int max, String desc, int periodTypeMax) {
            this.min = min;
            this.max = max;
            this.desc = desc;
            this.periodTypeMax = periodTypeMax;
        }

        public static List<GoalType> getEasyGoals() {
            List<GoalType> result = new ArrayList<>();
            result.add(EAT_N_BUGS);
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

            return result;
        }

        public static List<GoalType> getMediumGoals() {
            List<GoalType> result = new ArrayList<>();
            result.add(EAT_N_BUGS);
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
            result.add(EAT_N_BUG_UPPER);
            result.add(EAT_N_BUG_LOWER);

            return result;
        }

        public static List<GoalType> getHardGoals() {
            List<GoalType> result = new ArrayList<>();
            result.add(EAT_N_BUGS);
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
            result.add(PET_CHARGE_N_TIMES);
            result.add(PET_THE_PET);

            return result;
        }
    }
}
