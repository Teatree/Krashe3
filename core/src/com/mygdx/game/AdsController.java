package com.mygdx.game;

/**
 * Created by AnastasiiaRudyk on 2/4/2016.
 */
public interface AdsController {

    boolean isWifiConnected();

    void showInterstitialVideoAd(Runnable then);

    void showInterstitialGeneralAd(Runnable then);
}
