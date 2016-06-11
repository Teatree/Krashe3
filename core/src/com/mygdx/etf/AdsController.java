package com.mygdx.etf;

public interface AdsController {

    boolean isWifiConnected();

    void showReviveVideoAd(Runnable then);

    void showGetMoneyVideoAd(Runnable then);

    void showLaunchAd(Runnable then);

    void showResultScreenAd(Runnable then);

    void showGeneralShopAd(Runnable then);

//    boolean shouldShowGetMoneyVideoBtnAd(long need);

//    boolean shouldShowLaunchAd();
//
//    boolean shouldShowShopAd();
//
//    boolean shouldShowResultAd();
//
//    boolean shouldShowReviveVideoBtnAd();

}
