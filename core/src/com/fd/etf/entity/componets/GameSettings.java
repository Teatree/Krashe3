package com.fd.etf.entity.componets;


import java.util.Date;

public class GameSettings {

    public boolean noAds = false;
    public boolean noSound;
    public boolean noMusic;

    public int totalPlayedGames;
    public int playedGames;

    public int reviveAd_max;

    public boolean shouldShowLaunchAd() {
        return false;
    }



}
