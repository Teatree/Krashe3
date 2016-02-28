package com.mygdx.game.entity.componets;

import com.mygdx.game.stages.GameScreenScript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * possible goals:
 * 1. eat n butterflies in one game
 * 2. get n points
 * 3. eat n dandelions in one game
 * 4. bounce dandelion n times
 * 5. eat n queens
 * 6. survive n angered bees mode
 * 7. eat n bees
 * 8. eat n chargers
 * 9. eat n drunks
 * 10. eat n simple bugs
 * 11. eat n bugs
 * 12. set a high score
 * 13.
 */
public class DailyGoal {

    public int counter;
    public boolean achieved;
    public String description;
    public GoalType type;
    public PeriodType periodType;
    public HashMap<Integer, PeriodType> periodTypeMap;
    public HashMap<Integer, PetComponent> petType;
    public HashMap<Integer, Float> levelMultipliers;
    public int n;
    public VanityComponent vanityComponent;
    public PetComponent pet;
    private Random random;

    public DailyGoal(FlowerPublicComponent fpc) {
        periodTypeMap = new HashMap<>();
        periodTypeMap.put(0,PeriodType.IN_ONE_LIFE);
        periodTypeMap.put(1,PeriodType.IN_TOTAL);
        periodTypeMap.put(2,PeriodType.IN_A_ROW);
        levelMultipliers = new HashMap<>();
        levelMultipliers.put(0,1.03f);
        levelMultipliers.put(1,1.26f);
        levelMultipliers.put(2,1.8f);
        levelMultipliers.put(3,2.6f);
        levelMultipliers.put(4,3.4f);
        levelMultipliers.put(5,4.2f);
        levelMultipliers.put(6,5f);
        levelMultipliers.put(7,5.8f);
        levelMultipliers.put(8,6.6f);
        levelMultipliers.put(9,7.4f);
        levelMultipliers.put(10,8.2f);
        petType = new HashMap<>();
        int ba = 0;
        for(PetComponent p: fpc.pets){
            petType.put(ba, p);
            ba++;
        }
    }

    public DailyGoal (GoalType type){
        this.type = type;
        random = new Random();
        switch (type){
            case EAT_N_BUTTERFLIES: {
                n = random.nextInt(EAT_N_BUTTERFLIES_MAX - EAT_N_BUTTERFLIES_MIN) + EAT_N_BUTTERFLIES_MIN;
                description = EAT_N_BUTTERFLIES_DESK;
                periodType = periodTypeMap.get(random.nextInt(2));
                n /= periodType.divider;
                n = changeByLevel(GameScreenScript.fpc.level, n);
                break;
            }
            case GET_N_POINTS: {
                n = random.nextInt(EAT_N_BUTTERFLIES_MAX - EAT_N_BUTTERFLIES_MIN) + EAT_N_BUTTERFLIES_MIN;
                description = EAT_N_GET_N_POINTS_DESK;
                periodType = periodTypeMap.get(random.nextInt(2));
                n /= periodType.divider;
                n = changeByLevel(GameScreenScript.fpc.level, n);
                break;
            }
            case EAT_N_UMBRELLA: {
                n = random.nextInt(EAT_N_BUTTERFLIES_MAX - EAT_N_BUTTERFLIES_MIN) + EAT_N_BUTTERFLIES_MIN;
                description = EAT_N_DANDELIONS_DESK;
                periodType = periodTypeMap.get(random.nextInt(2));
                n /= periodType.divider;
                n = changeByLevel(GameScreenScript.fpc.level, n);
                break;
            }
            case BOUNCE_DANDELION_N_TIMES: {
                n = random.nextInt(EAT_N_BUTTERFLIES_MAX - EAT_N_BUTTERFLIES_MIN) + EAT_N_BUTTERFLIES_MIN;
                description = BOUNCE_DANDELION_N_TIMES_DESK;
                periodType = periodTypeMap.get(random.nextInt(1));
                n /= periodType.divider;
                n = changeByLevel(GameScreenScript.fpc.level, n);
                break;
            }
            case EAT_N_QUEENS: {
                n = random.nextInt(EAT_N_BUTTERFLIES_MAX - EAT_N_BUTTERFLIES_MIN) + EAT_N_BUTTERFLIES_MIN;
                description = EAT_N_QUEENS_DESK;
                periodType = periodTypeMap.get(random.nextInt(2));
                n /= periodType.divider;
                n = changeByLevel(GameScreenScript.fpc.level, n);
                break;
            }
            case SURVIVE_N_ANGERED_MODES:{
                n = random.nextInt(EAT_N_BUTTERFLIES_MAX - EAT_N_BUTTERFLIES_MIN) + EAT_N_BUTTERFLIES_MIN;
                description = SURVIVE_N_ANGERED_MODES_DESK;
                periodType = periodTypeMap.get(random.nextInt(2));
                n /= periodType.divider;
                n = changeByLevel(GameScreenScript.fpc.level, n);
                break;
            }
            case EAT_N_BEES: {
                n = random.nextInt(EAT_N_BUTTERFLIES_MAX - EAT_N_BUTTERFLIES_MIN) + EAT_N_BUTTERFLIES_MIN;
                description = EAT_N_BEES_DESK;
                periodType = periodTypeMap.get(random.nextInt(2));
                n /= periodType.divider;
                n = changeByLevel(GameScreenScript.fpc.level, n);
                break;
            }
            case EAT_N_CHARGERS: {
                n = random.nextInt(EAT_N_BUTTERFLIES_MAX - EAT_N_BUTTERFLIES_MIN) + EAT_N_BUTTERFLIES_MIN;
                description = EAT_N_CHARGERS_DESK;
                periodType = periodTypeMap.get(random.nextInt(2));
                n /= periodType.divider;
                n = changeByLevel(GameScreenScript.fpc.level, n);
                break;
            }
            case EAT_N_DRUNKS: {
                n = random.nextInt(EAT_N_BUTTERFLIES_MAX - EAT_N_BUTTERFLIES_MIN) + EAT_N_BUTTERFLIES_MIN;
                description = EAT_N_DRUNKS_DESK;
                periodType = periodTypeMap.get(random.nextInt(2));
                n /= periodType.divider;
                n = changeByLevel(GameScreenScript.fpc.level, n);
                break;
            }
            case EAT_N_SIMPLE: {
                n = random.nextInt(EAT_N_BUTTERFLIES_MAX - EAT_N_BUTTERFLIES_MIN) + EAT_N_BUTTERFLIES_MIN;
                description = EAT_N_SIMPLE_DESK;
                periodType = periodTypeMap.get(random.nextInt(2));
                n /= periodType.divider;
                n = changeByLevel(GameScreenScript.fpc.level, n);
                break;
            }
            case EAT_N_BUGS: {
                n = random.nextInt(EAT_N_BUTTERFLIES_MAX - EAT_N_BUTTERFLIES_MIN) + EAT_N_BUTTERFLIES_MIN;
                description = EAT_N_BUGS_DESK;
                periodType = periodTypeMap.get(random.nextInt(3));
                n /= periodType.divider;
                n = changeByLevel(GameScreenScript.fpc.level, n);
                break;
            }
            case DESTROY_N_CACOON: {
                n = random.nextInt(DESTROY_N_CACOON_MAX - DESTROY_N_CACOON_MIN) + DESTROY_N_CACOON_MIN;
                description = DESTROY_N_CACOON_DESC;
                periodType = periodTypeMap.get(random.nextInt(2));
                n /= periodType.divider;
                n = changeByLevel(GameScreenScript.fpc.level, n);
                break;
            }
            case PET_EAT_N_BUGS: {
                n = random.nextInt(PET_EAT_N_BUGS_MAX - PET_EAT_N_BUGS_MIN) + PET_EAT_N_BUGS_MIN;
                description = PET_EAT_N_BUGS_DESC;
                periodType = periodTypeMap.get(random.nextInt(3));
                n /= periodType.divider;
                n = changeByLevel(GameScreenScript.fpc.level, n);
                pet = petType.get(random.nextInt(petType.size()));
                break;
            }
            case PET_CHARGE_N_TIMES: {
                n = random.nextInt(PET_CHARGE_N_TIMES_MAX - PET_CHARGE_N_TIMES_MIN) + PET_CHARGE_N_TIMES_MIN;
                description = PET_CHARGE_N_TIMES_DESC;
                periodType = periodTypeMap.get(random.nextInt(2));
                n /= periodType.divider;
                n = changeByLevel(GameScreenScript.fpc.level, n);
                pet = petType.get(random.nextInt(petType.size()));
                break;
            }
            case PET_THE_PET: {
                n = random.nextInt(PET_THE_PET_MAX - PET_THE_PET_MIN) + PET_THE_PET_MIN;
                description = PET_THE_PET_DESK;
                periodType = periodTypeMap.get(random.nextInt(2));
                n /= periodType.divider;
                n = changeByLevel(GameScreenScript.fpc.level, n);
                pet = petType.get(random.nextInt(petType.size()));
                break;
            }
            case EAT_N_BUG_UPPER: {
                n = random.nextInt(EAT_N_BUG_UPPER_MAX - EAT_N_BUG_UPPER_MIN) + EAT_N_BUG_UPPER_MIN;
                description = EAT_N_BUG_UPPER_DESK;
                periodType = periodTypeMap.get(random.nextInt(2));
                n /= periodType.divider;
                n = changeByLevel(GameScreenScript.fpc.level, n);
                pet = petType.get(random.nextInt(petType.size()));
                break;
            }
            case EAT_N_BUG_LOWER: {
                n = random.nextInt(EAT_N_BUG_LOWER_MAX - EAT_N_BUG_LOWER_MIN) + EAT_N_BUG_LOWER_MIN;
                description = EAT_N_BUG_LOWER_DESK;
                periodType = periodTypeMap.get(random.nextInt(2));
                n /= periodType.divider;
                n = changeByLevel(GameScreenScript.fpc.level, n);
                pet = petType.get(random.nextInt(petType.size()));
                break;
            }
            case TAP: {
                n = random.nextInt(TAP_MAX - TAP_MIN) + TAP_MIN;
                description = TAP_DESK;
                periodType = periodTypeMap.get(random.nextInt(2));
                n /= periodType.divider;
                n = changeByLevel(GameScreenScript.fpc.level, n);
                pet = petType.get(random.nextInt(petType.size()));
                break;
            }
        }
    }

    public void update() {
        counter++;
        if (counter == n) {
            achieved = true;
        }
        System.out.println("num");
    }

    public int changeByLevel(int level, int n){
        return Math.round(n * levelMultipliers.get(level));
    }

    public boolean checkIfAchieved(int nResultsToCheck){
       return this.n <= nResultsToCheck;
    }

    public enum PeriodType{
        IN_A_ROW(10),
        IN_ONE_LIFE(5),
        IN_TOTAL(1);

        public int divider;
        PeriodType(int divider){
            this.divider = divider;
        }
    }

    public enum GoalType {
        EAT_N_BUGS,
        GET_N_POINTS,
        EAT_N_DRUNKS,
        EAT_N_CHARGERS,
        EAT_N_SIMPLE,
        EAT_N_BEES,
        DESTROY_N_CACOON,
        EAT_N_UMBRELLA,
        EAT_N_BUTTERFLIES,
        BOUNCE_DANDELION_N_TIMES,
        TAP,
        EAT_N_BUG_UPPER,
        EAT_N_BUG_LOWER,
        EAT_N_QUEENS,
        SURVIVE_N_ANGERED_MODES,
        PET_THE_PET,
        PET_EAT_N_BUGS,
        PET_CHARGE_N_TIMES;
        public static List<GoalType> getEasyGoals(){
            List<GoalType> result = new ArrayList<>();
            result.add(EAT_N_BUGS);
            result.add(GET_N_POINTS);
            result.add(EAT_N_DRUNKS);
            result.add(EAT_N_CHARGERS);
            result.add(EAT_N_SIMPLE);
            result.add(EAT_N_BEES);
            result.add(DESTROY_N_CACOON);
            result.add(EAT_N_UMBRELLA);
            result.add(EAT_N_BUTTERFLIES);
            result.add(BOUNCE_DANDELION_N_TIMES);
            result.add(TAP);

            return result;
        }
        public static List<GoalType> getMediumGoals(){
            List<GoalType> result = new ArrayList<>();
            result.add(EAT_N_BUGS);
            result.add(GET_N_POINTS);
            result.add(EAT_N_DRUNKS);
            result.add(EAT_N_CHARGERS);
            result.add(EAT_N_SIMPLE);
            result.add(EAT_N_BEES);
            result.add(DESTROY_N_CACOON);
            result.add(EAT_N_UMBRELLA);
            result.add(EAT_N_BUTTERFLIES);
            result.add(BOUNCE_DANDELION_N_TIMES);
            result.add(TAP);
            result.add(EAT_N_QUEENS);
            result.add(SURVIVE_N_ANGERED_MODES);
            result.add(EAT_N_BUG_UPPER);
            result.add(EAT_N_BUG_LOWER);

            return result;
        }
        public static List<GoalType> getHardGoals(){
            List<GoalType> result = new ArrayList<>();
            result.add(EAT_N_BUGS);
            result.add(GET_N_POINTS);
            result.add(EAT_N_DRUNKS);
            result.add(EAT_N_CHARGERS);
            result.add(EAT_N_SIMPLE);
            result.add(EAT_N_BEES);
            result.add(DESTROY_N_CACOON);
            result.add(EAT_N_UMBRELLA);
            result.add(EAT_N_BUTTERFLIES);
            result.add(BOUNCE_DANDELION_N_TIMES);
            result.add(TAP);
            result.add(EAT_N_QUEENS);
            result.add(SURVIVE_N_ANGERED_MODES);
            result.add(PET_EAT_N_BUGS);
            result.add(PET_CHARGE_N_TIMES);
            result.add(PET_THE_PET);

            return result;
        }

    }

    public static final int PET_EAT_N_BUGS_MIN = 2;
    public static final int PET_EAT_N_BUGS_MAX = 5;
    public static final String PET_EAT_N_BUGS_DESC = "PET_EAT_N_BUGS";

    public static final int PET_CHARGE_N_TIMES_MIN = 2;
    public static final int PET_CHARGE_N_TIMES_MAX = 5;
    public static final String PET_CHARGE_N_TIMES_DESC = "PET_CHARGE_N_TIMES";

    public static final int DESTROY_N_CACOON_MIN = 2;
    public static final int DESTROY_N_CACOON_MAX = 5;
    public static final String DESTROY_N_CACOON_DESC = "DESTROY_N_CACOON";

    public static final int EAT_N_BUTTERFLIES_MIN = 2;
    public static final int EAT_N_BUTTERFLIES_MAX = 5;
    public static final String EAT_N_BUTTERFLIES_DESK = "EAT_N_BUTTERFLIES";

    public static final int GET_N_POINTS_MIN = 200;
    public static final int EAT_N_GET_N_POINTS_MAX = 400;
    public static final String EAT_N_GET_N_POINTS_DESK = "EAT_N_GET_N_POINTS";

    public static final int EAT_N_DANDELIONS_MIN = 1;
    public static final int EAT_N_DANDELIONS_MAX = 4;
    public static final String EAT_N_DANDELIONS_DESK = "EAT_N_UMBRELLA";

    public static final int BOUNCE_DANDELION_N_TIMES_MIN = 2;
    public static final int BOUNCE_DANDELION_N_TIMES_MAX = 4;
    public static final String BOUNCE_DANDELION_N_TIMES_DESK = "BOUNCE_DANDELION_N_TIMES";

    public static final int EAT_N_QUEENS_MIN = 1;
    public static final int EAT_N_QUEENS_MAX = 4;
    public static final String EAT_N_QUEENS_DESK = "EAT_N_QUEENS";

    public static final int SURVIVE_N_ANGERED_MODES_MIN = 2;
    public static final int SURVIVE_N_ANGERED_MODES_MAX = 4;
    public static final String SURVIVE_N_ANGERED_MODES_DESK = "SURVIVE_N_ANGERED_MODES";

    public static final int EAT_N_BEES_MIN = 20;
    public static final int EAT_N_BEES_MAX = 40;
    public static final String EAT_N_BEES_DESK = "EAT_N_BEES";

    public static final int EAT_N_CHARGERS_MIN = 20;
    public static final int EAT_N_CHARGERS_MAX = 40;
    public static final String EAT_N_CHARGERS_DESK = "EAT_N_CHARGERS";

    public static final int EAT_N_DRUNKS_MIN = 20;
    public static final int EAT_N_DRUNKS_MAX = 40;
    public static final String EAT_N_DRUNKS_DESK = "EAT_N_DRUNKS";

    public static final int EAT_N_SIMPLE_MIN = 20;
    public static final int EAT_N_SIMPLE_MAX = 40;
    public static final String EAT_N_SIMPLE_DESK = "EAT_N_SIMPLE";

    public static final int EAT_N_BUGS_MIN = 20;
    public static final int EAT_N_BUGS_MAX = 40;
    public static final String EAT_N_BUGS_DESK = "EAT_N_BUGS";

    public static final int EAT_N_BUG_LOWER_MIN = 20;
    public static final int EAT_N_BUG_LOWER_MAX = 40;
    public static final String EAT_N_BUG_LOWER_DESK = "EAT_N_BUG_LOWER";

    public static final int EAT_N_BUG_UPPER_MIN = 20;
    public static final int EAT_N_BUG_UPPER_MAX = 40;
    public static final String EAT_N_BUG_UPPER_DESK = "EAT_N_BUG_UPPER";

    public static final int PET_THE_PET_MIN = 20;
    public static final int PET_THE_PET_MAX = 40;
    public static final String PET_THE_PET_DESK = "PET_THE_PET";

    public static final int TAP_MIN = 20;
    public static final int TAP_MAX = 40;
    public static final String TAP_DESK = "TAP";
}
