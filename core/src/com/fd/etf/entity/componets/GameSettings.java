package com.fd.etf.entity.componets;


import com.fd.etf.Main;

import java.util.Date;

public class GameSettings {

    public boolean noAds = false;
    public boolean noSound;
    public boolean noMusic;

    public int totalPlayedGames;
    public int playedGames;

    public int reviveAd_max;

    public boolean shouldShowLaunchAd() {
        if(Main.mainController.isWifiConnected()) {
            return true;
        }
        return false;
    }



}
