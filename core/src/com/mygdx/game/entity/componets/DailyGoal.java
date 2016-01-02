package com.mygdx.game.entity.componets;

import java.util.Calendar;
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

    public boolean achieved;
    public String description;
    public GoalType type;
    public int n;
    private Random random;

    public DailyGoal() {
    }

    public DailyGoal (GoalType type){
        this.type = type;
        random = new Random();
        switch (type){
            case EAT_N_BUTTERFLIES: {
                n = random.nextInt(EAT_N_BUTTERFLIES_MAX - EAT_N_BUTTERFLIES_MIN) + EAT_N_BUTTERFLIES_MIN;
                description = EAT_N_BUTTERFLIES_DESK;
                break;
            }
            case GET_N_POINTS: {
                n = random.nextInt(EAT_N_BUTTERFLIES_MAX - EAT_N_BUTTERFLIES_MIN) + EAT_N_BUTTERFLIES_MIN;
                description = EAT_N_GET_N_POINTS_DESK;
                break;
            }
            case EAT_N_DANDELIONS: {
                n = random.nextInt(EAT_N_BUTTERFLIES_MAX - EAT_N_BUTTERFLIES_MIN) + EAT_N_BUTTERFLIES_MIN;
                description = EAT_N_DANDELIONS_DESK;
                break;
            }
            case BOUNCE_DANDELION_N_TIMES: {
                n = random.nextInt(EAT_N_BUTTERFLIES_MAX - EAT_N_BUTTERFLIES_MIN) + EAT_N_BUTTERFLIES_MIN;
                description = BOUNCE_DANDELION_N_TIMES_DESK;
                break;
            }
            case EAT_N_QUEENS: {
                n = random.nextInt(EAT_N_BUTTERFLIES_MAX - EAT_N_BUTTERFLIES_MIN) + EAT_N_BUTTERFLIES_MIN;
                description = EAT_N_QUEENS_DESK;
                break;
            }
            case SURVIVE_N_ANGERED_MODES:{
                n = random.nextInt(EAT_N_BUTTERFLIES_MAX - EAT_N_BUTTERFLIES_MIN) + EAT_N_BUTTERFLIES_MIN;
                description = SURVIVE_N_ANGERED_MODES_DESK;
                break;
            }
            case EAT_N_BEES: {
                n = random.nextInt(EAT_N_BUTTERFLIES_MAX - EAT_N_BUTTERFLIES_MIN) + EAT_N_BUTTERFLIES_MIN;
                description = EAT_N_BEES_DESK;
                break;
            }
            case EAT_N_CHARGERS: {
                n = random.nextInt(EAT_N_BUTTERFLIES_MAX - EAT_N_BUTTERFLIES_MIN) + EAT_N_BUTTERFLIES_MIN;
                description = EAT_N_CHARGERS_DESK;
                break;
            }
            case EAT_N_DRUNKS: {
                n = random.nextInt(EAT_N_BUTTERFLIES_MAX - EAT_N_BUTTERFLIES_MIN) + EAT_N_BUTTERFLIES_MIN;
                description = EAT_N_DRUNKS_DESK;
                break;
            }
            case EAT_N_SIMPLE: {
                n = random.nextInt(EAT_N_BUTTERFLIES_MAX - EAT_N_BUTTERFLIES_MIN) + EAT_N_BUTTERFLIES_MIN;
                description = EAT_N_SIMPLE_DESK;
                break;
            }
            case EAT_N_BUGS: {
                n = random.nextInt(EAT_N_BUTTERFLIES_MAX - EAT_N_BUTTERFLIES_MIN) + EAT_N_BUTTERFLIES_MIN;
                description = EAT_N_BUGS_DESK;
                break;
            }
            case SET_HIGH_SCORE: {
                n = random.nextInt(EAT_N_BUTTERFLIES_MAX - EAT_N_BUTTERFLIES_MIN) + EAT_N_BUTTERFLIES_MIN;
                description = SET_HIGH_SCORE;
                break;
            }
        }
    }

    public boolean checkIfAchieved(int nResultsToCheck){
       return this.n <= nResultsToCheck;
    }

    public enum GoalType {
        EAT_N_BUTTERFLIES,
        GET_N_POINTS,
        EAT_N_DANDELIONS,
        BOUNCE_DANDELION_N_TIMES,
        EAT_N_QUEENS,
        SURVIVE_N_ANGERED_MODES,
        EAT_N_BEES,
        EAT_N_CHARGERS,
        EAT_N_DRUNKS,
        EAT_N_SIMPLE,
        EAT_N_BUGS,
        SET_HIGH_SCORE;
    }

    public static final int EAT_N_BUTTERFLIES_MIN = 2;
    public static final int EAT_N_BUTTERFLIES_MAX = 5;
    public static final String EAT_N_BUTTERFLIES_DESK = "EAT_N_BUTTERFLIES";

    public static final int GET_N_POINTS_MIN = 200;
    public static final int EAT_N_GET_N_POINTS_MAX = 400;
    public static final String EAT_N_GET_N_POINTS_DESK = "EAT_N_GET_N_POINTS";

    public static final int EAT_N_DANDELIONS_MIN = 1;
    public static final int EAT_N_DANDELIONS_MAX = 4;
    public static final String EAT_N_DANDELIONS_DESK = "EAT_N_DANDELIONS";

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

    public static final String SET_HIGH_SCORE = "SET_HIGH_SCORE";
}
