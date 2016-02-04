package com.mygdx.game;

/**
 * Created by AnastasiiaRudyk on 2/4/2016.
 */
public class DummyAdsController implements AdsController {
    @Override
    public void showBannerAd() {

    }

    @Override
    public void hideBannerAd() {

    }

    @Override
    public boolean isWifiConnected() {
        return false;
    }

    @Override
    public void showInterstitialAd(Runnable then) {

    }
}
