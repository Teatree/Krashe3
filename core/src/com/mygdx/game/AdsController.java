package com.mygdx.game;

public interface AdsController {

    boolean isWifiConnected();

    void showInterstitialReviveVideoAd(Runnable then);

    void showLaunchAd(Runnable then);
    void showResultScreenAd(Runnable then);
    void showGeneralShopAd(Runnable then);
}
