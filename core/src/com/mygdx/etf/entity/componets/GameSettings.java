package com.mygdx.etf.entity.componets;


import com.mygdx.etf.Main;

import java.util.Random;

import static com.mygdx.etf.stages.GameStage.gameScript;
import static com.mygdx.etf.stages.ResultScreenScript.PERCENTS_TO_OFFER_AD;

public class GameSettings {

    Random random = new Random();

    public boolean noAds = false;
    public boolean noSound;
    public boolean noMusic;

    public int totalPlayedGames;
    public int playedGames;

    public int shopAd_max = 4;
    public int resultScreenAd_max = 4;
    public int launchAd_max = 4;
    public int getMoneyAd_max = 4;
    public int reviveAd_max = 10;

    public int shopAd_min = 2;
    public int resultScreenAd_min = 2;
    public int launchAd_min = 2;
    public int getMoneyAd_min = 2;
    public int reviveAd_min = 2;

    public int start_resultScreenAd = 1;
    public int start_shopAd = 1;
    public int start_getMoneyAd = 1;
    public int start_launchAd = 1;
    public int start_reviveAd = 10;

    public int fatigue_resultScreenAd = 1;
    public int fatigue_shopAd = 1;
    public int fatigue_getMoneyAd = 1;
    public int fatigue_launchAd = 1;
    public int fatigue_reviveAd = 1;


    void resetFatigueShopAd() {
        fatigue_shopAd = random.nextInt(shopAd_max - shopAd_min) + shopAd_min;
    }

    void resetFatigueResultScreenAd() {
        fatigue_resultScreenAd = random.nextInt(resultScreenAd_max - resultScreenAd_min) + resultScreenAd_min;
    }

    void resetFatigueLaunchAd() {
        fatigue_launchAd = random.nextInt(launchAd_max - launchAd_min) + launchAd_min;
    }

    void resetFatigueMoneyAd() {
        fatigue_getMoneyAd = random.nextInt(getMoneyAd_max - getMoneyAd_min) + getMoneyAd_min;
    }

    void resetFatigueReviveAd() {
        fatigue_reviveAd = random.nextInt(reviveAd_max - reviveAd_min) + reviveAd_min;
    }

    public boolean shouldShowLaunchAd() {
//        if (!noAds && Main.mainController.isWifiConnected() &&
//                totalPlayedGames >= start_launchAd &&
//                playedGames % fatigue_launchAd == 0) {
//            resetFatigueLaunchAd();
//            return true;
//        } else {
            return false;
//        }
    }

    public boolean shouldShowShopAd() {
        if (!noAds && Main.mainController.isWifiConnected() &&
                totalPlayedGames >= start_shopAd &&
                playedGames % fatigue_shopAd == 0) {
            resetFatigueShopAd();
            return true;
        } else {
            return false;
        }
    }

    public boolean shouldShowResultAd() {
        if (!noAds && Main.mainController.isWifiConnected() &&
                totalPlayedGames >= start_resultScreenAd &&
                playedGames % fatigue_resultScreenAd == 0) {
            resetFatigueResultScreenAd();
            return true;
        } else {
            return false;
        }
    }

    public boolean shouldShowReviveVideoBtnAd() {
        if (!noAds &&
                totalPlayedGames >= start_reviveAd &&
                playedGames % fatigue_reviveAd == 0) {
            resetFatigueReviveAd();
            return true;
        } else {
            return false;
        }
    }

    public boolean shouldShowGetMoneyVideoBtnAd(long need) {
        return gameScript.fpc.settings.totalPlayedGames >= start_getMoneyAd &&
                gameScript.fpc.totalScore < PERCENTS_TO_OFFER_AD &&
                gameScript.fpc.totalScore >= PERCENTS_TO_OFFER_AD * need;
    }

}
