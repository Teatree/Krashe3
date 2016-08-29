package com.mygdx.etf.utils;

import com.mygdx.etf.entity.componets.FlowerPublicComponent;
import com.mygdx.etf.entity.componets.Goal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static com.mygdx.etf.entity.componets.Goal.GoalType.*;
import static com.mygdx.etf.stages.GameStage.gameScript;

public class GoalGenerator {

    public Random random;
    List<Goal.GoalType> goalTypes;

    public GoalGenerator() {
        random = new Random();

        goalTypes = new ArrayList<>();
        goalTypes.add(EAT_N_BUGS);
        goalTypes.add(SPEND_MONEYZ);
        goalTypes.add(GET_N_POINTS);
        goalTypes.add(EAT_N_DRUNKS);
        goalTypes.add(EAT_N_CHARGERS);
        goalTypes.add(EAT_N_SIMPLE);
        goalTypes.add(EAT_N_BEES);
        goalTypes.add(DESTROY_N_COCOON);
        goalTypes.add(EAT_N_UMBRELLA);
        goalTypes.add(EAT_N_BUTTERFLIES);
        goalTypes.add(BOUNCE_UMBRELLA_N_TIMES);
        goalTypes.add(TAP);
        goalTypes.add(EAT_N_QUEENS);
        goalTypes.add(SURVIVE_N_ANGERED_MODES);
        goalTypes.add(PET_EAT_N_BUGS);
        goalTypes.add(PET_DASH_N_TIMES);
        goalTypes.add(PET_THE_PET);
        goalTypes.add(PET_DASH_N_TIMES);
    }

    public HashMap<Goal.GoalType, Goal> getGoals(FlowerPublicComponent fpc) {
        HashMap<Goal.GoalType, Goal> goals = new HashMap<>();
        int goalsAmount = random.nextInt(fpc.level.maxGoalsAmount - fpc.level.minGoalsAmount) + fpc.level.minGoalsAmount;
        int medGoalsCounter = fpc.level.mediumGoalsAmount;
        int hardGoalsCounter = fpc.level.hardGoalsAmount;
        for (int i = 0; i <= goalsAmount; i++) {
            Goal dg;
            if (hardGoalsCounter > 0) {
                dg = createGoal(2, fpc);
                hardGoalsCounter--;
            } else if (medGoalsCounter > 0) {
                dg = createGoal(1, fpc);
                medGoalsCounter--;
            } else {
                dg = createGoal(0,fpc);
            }
            goals.put(dg.type, dg);
        }
        return goals;
    }

    private Goal createGoal(int difficulty, FlowerPublicComponent fpc) {
        int probabilityValueRandom = random.nextInt(100)+1;
        int probabilityValueCheck = 0;
        Goal.GoalType goalType = EAT_N_BUGS;

        if (probabilityValueRandom > probabilityValueCheck
                && probabilityValueRandom <= probabilityValueCheck + fpc.level.prob_eat_n_bugs) {
            goalType = EAT_N_BUGS;
            return new Goal(goalType, difficulty, fpc.level.goalMultiplier);
        } else {
            probabilityValueCheck += fpc.level.prob_eat_n_bugs;
        }

        if (probabilityValueRandom > probabilityValueCheck
                && probabilityValueRandom <= probabilityValueCheck + fpc.level.prob_eat_n_drunks) {
            goalType = EAT_N_DRUNKS;
            return new Goal(goalType, difficulty, fpc.level.goalMultiplier);
        } else {
            probabilityValueCheck += fpc.level.prob_eat_n_drunks;
        }

        if (probabilityValueRandom > probabilityValueCheck
                && probabilityValueRandom <= probabilityValueCheck + fpc.level.prob_eat_n_chargers) {
            goalType = EAT_N_CHARGERS;
            return new Goal(goalType, difficulty, fpc.level.goalMultiplier);
        } else {
            probabilityValueCheck += fpc.level.prob_eat_n_chargers;
        }

        if (probabilityValueRandom > probabilityValueCheck
                && probabilityValueRandom <= probabilityValueCheck + fpc.level.prob_eat_n_simple) {
            goalType = EAT_N_SIMPLE;
            return new Goal(goalType, difficulty, fpc.level.goalMultiplier);
        } else {
            probabilityValueCheck += fpc.level.prob_eat_n_simple;
        }

        if (probabilityValueRandom > probabilityValueCheck
                && probabilityValueRandom <= probabilityValueCheck + fpc.level.prob_eat_n_bees) {
            goalType = EAT_N_BEES;
            return new Goal(goalType, difficulty, fpc.level.goalMultiplier);
        } else {
            probabilityValueCheck += fpc.level.prob_eat_n_bees;
        }

        if (probabilityValueRandom > probabilityValueCheck
                && probabilityValueRandom <= probabilityValueCheck + fpc.level.prob_eat_n_queens) {
            goalType = EAT_N_QUEENS;
            return new Goal(goalType, difficulty, fpc.level.goalMultiplier);
        } else {
            probabilityValueCheck += fpc.level.prob_eat_n_queens;
        }

        if (probabilityValueRandom > probabilityValueCheck
                && probabilityValueRandom <= probabilityValueCheck + fpc.level.prob_eat_n_umrellas) {
            goalType = EAT_N_UMBRELLA;
            return new Goal(goalType, difficulty, fpc.level.goalMultiplier);
        } else {
            probabilityValueCheck += fpc.level.prob_eat_n_umrellas;
        }

        if (probabilityValueRandom > probabilityValueCheck
                && probabilityValueRandom <= probabilityValueCheck + fpc.level.prob_eat_n_butterflies) {
            goalType = EAT_N_BUTTERFLIES;
            return new Goal(goalType, difficulty, fpc.level.goalMultiplier);
        } else {
            probabilityValueCheck += fpc.level.prob_eat_n_butterflies;
        }

        if (probabilityValueRandom > probabilityValueCheck
                && probabilityValueRandom <= probabilityValueCheck + fpc.level.prob_destroy_n_cocoon) {
            goalType = DESTROY_N_COCOON;
            return new Goal(goalType, difficulty, fpc.level.goalMultiplier);
        } else {
            probabilityValueCheck += fpc.level.prob_destroy_n_cocoon;
        }

        if (probabilityValueRandom > probabilityValueCheck
                && probabilityValueRandom <= probabilityValueCheck + fpc.level.prob_bounce_umbrella_n_times) {
            goalType = BOUNCE_UMBRELLA_N_TIMES;
            return new Goal(goalType, difficulty, fpc.level.goalMultiplier);
        } else {
            probabilityValueCheck += fpc.level.prob_bounce_umbrella_n_times;
        }

        if (probabilityValueRandom > probabilityValueCheck
                && probabilityValueRandom <= probabilityValueCheck + fpc.level.prob_tap) {
            goalType = TAP;
            return new Goal(goalType, difficulty, fpc.level.goalMultiplier);
        } else {
            probabilityValueCheck += fpc.level.prob_tap;
        }

        if (probabilityValueRandom > probabilityValueCheck
                && probabilityValueRandom <= probabilityValueCheck + fpc.level.prob_survive_n_angered_modes) {
            goalType = SURVIVE_N_ANGERED_MODES;
            return new Goal(goalType, difficulty, fpc.level.goalMultiplier);
        } else {
            probabilityValueCheck += fpc.level.prob_survive_n_angered_modes;
        }

        if (probabilityValueRandom > probabilityValueCheck
                && probabilityValueRandom <= probabilityValueCheck + fpc.level.prob_pet_the_pet_n_times) {
            goalType = PET_THE_PET;
            return new Goal(goalType, difficulty, fpc.level.goalMultiplier);
        } else {
            probabilityValueCheck += fpc.level.prob_pet_the_pet_n_times;
        }

        if (probabilityValueRandom > probabilityValueCheck
                && probabilityValueRandom <= probabilityValueCheck + fpc.level.prob_pet_eat_n_bugs) {
            goalType = PET_EAT_N_BUGS;
            return new Goal(goalType, difficulty, fpc.level.goalMultiplier);
        } else {
            probabilityValueCheck += fpc.level.prob_pet_eat_n_bugs;
        }

        if (probabilityValueRandom > probabilityValueCheck
                && probabilityValueRandom <= probabilityValueCheck + fpc.level.prob_pet_dash_n_times) {
            goalType = PET_DASH_N_TIMES;
            return new Goal(goalType, difficulty, fpc.level.goalMultiplier);
        }
        
        return new Goal(goalType, difficulty, fpc.level.goalMultiplier);
    }
}
