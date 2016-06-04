package com.mygdx.game;


public class DummyAdsController implements AdsController {

    @Override
    public boolean isWifiConnected() {
        return false;
    }

    @Override
    public void showInterstitialReviveVideoAd(Runnable then) {

    }

    @Override
    public void showLaunchAd(Runnable then) {

    }

    @Override
    public void showResultScreenAd(Runnable then) {

    }

    @Override
    public void showGeneralShopAd(Runnable then) {

    }
}
