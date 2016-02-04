package com.mygdx.game;

/**
 * Created by AnastasiiaRudyk on 2/4/2016.
 */
public interface AdsController {

    void showBannerAd();
    void hideBannerAd();
    boolean isWifiConnected();
    void showInterstitialAd (Runnable then);
}
