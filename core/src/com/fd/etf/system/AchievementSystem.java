package com.fd.etf.system;

import com.fd.etf.Main;
import com.fd.etf.PlayServices;

public class AchievementSystem {

    public static int queenAchGoal = 15;
    public static int bugAchGoal = 1000;
    public static int butterflyAchGoal = 20;

    public static int queenAchCounter;
    public static int bugAchCounter;
    public static int butterflyAchCounter;

    public static void checkQueenSlayerAchievement() {
        queenAchCounter++;
        if (queenAchCounter == queenAchGoal) {
            Main.mainController.unlockAchievement(PlayServices.ACH_QUEEN_SLAYER);
        }
    }

    public static void checkBugsAchievement() {
        bugAchCounter++;
        if (bugAchCounter == bugAchGoal) {
            Main.mainController.unlockAchievement(PlayServices.ACH_HUNGER_STRIKE);
        }
    }

    public static void checkButterfliesAchievement() {
        butterflyAchCounter++;
        if (butterflyAchCounter == butterflyAchGoal) {
            Main.mainController.unlockAchievement(PlayServices.ACH_BUTTEFLY_EATER);
        }
    }

    public static void checkVanityAchCollectGoal(boolean achievementComplete) {
        if (achievementComplete == true) {
            Main.mainController.unlockAchievement(PlayServices.ACH_COLLECTOR);
        }
    }

    public static void checkDogBuyAchGoal(boolean achievementComplete) {
        if (achievementComplete == true) {
            Main.mainController.unlockAchievement(PlayServices.ACH_DOG_PERSONE);
        }
    }
}
