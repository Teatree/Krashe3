package com.fd.etf.entity.componets;


import com.fd.etf.Main;

import java.util.Date;
import java.util.Random;

public class GameSettings {

    public boolean noAds = false;
    public boolean noSound;
    public boolean noMusic;
    Random random;

    public int totalPlayedGames;
    public int playedGames;

    public int reviveAd_max;

    public boolean shouldShowLaunchAd() {
        if(Main.mainController.isWifiConnected() && random.nextInt(100) > 50) {
            return true;
        }
        return false;
    }

    public boolean shouldShowShopAd() {
        if(Main.mainController.isWifiConnected() && random.nextInt(100) > 50) {
            return true;
        }
        return false;
    }

    public boolean shouldShowGameAd() {
        if(Main.mainController.isWifiConnected() && random.nextInt(100) > 75) {
            return true;
        }
        return false;
    }
}
