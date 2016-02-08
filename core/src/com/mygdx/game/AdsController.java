package com.mygdx.game;

public interface AdsController {

    boolean isWifiConnected();

    void showInterstitialVideoAd(Runnable then);

    void showInterstitialGeneralAd(Runnable then);
}
