package com.mygdx.etf;

public class DummyAdsController implements AdsController {

    @Override
    public boolean isWifiConnected() {
        return false;
    }

    @Override
    public void showReviveVideoAd(Runnable then) {
        System.out.println("showReviveVideoAd");
    }

    @Override
    public void showGetMoneyVideoAd(Runnable then) {
        System.out.println("showGetMoneyVideoAd");
    }

    @Override
    public void showLaunchAd(Runnable then) {
        System.out.println("showLaunchAd");
    }

    @Override
    public void showResultScreenAd(Runnable then) {
        System.out.println("showResultScreenAd");
    }

    @Override
    public void showGeneralShopAd(Runnable then) {
        System.out.println("showGeneralShopAd");
    }
}
