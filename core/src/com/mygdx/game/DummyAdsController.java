package com.mygdx.game;


public class DummyAdsController implements AdsController {

    @Override
    public boolean isWifiConnected() {
        return false;
    }

    @Override
    public void showInterstitialVideoAd(Runnable then) {

    }

    @Override
    public void showInterstitialGeneralAd(Runnable then) {

    }
}
