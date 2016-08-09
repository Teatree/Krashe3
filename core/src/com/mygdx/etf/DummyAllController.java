package com.mygdx.etf;

import com.badlogic.gdx.Gdx;
import com.mygdx.etf.entity.componets.PetComponent;
import com.mygdx.etf.entity.componets.Upgrade;
import com.mygdx.etf.stages.GameStage;

public class DummyAllController implements AllController {

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

    @Override
    public void removeAds() {
        System.out.println("pay to remove ads");
    }

    @Override
    public void getPhoenix(Upgrade phoenix) {
        System.out.println("pay to get phoenix");
    }

    @Override
    public void getBJDouble(Upgrade bj) {
        System.out.println("pay to get bj");
    }

    @Override
    public void getBirdPet(PetComponent pet) {
        GameStage.gameScript.fpc.pets.get(0).buyAndUse();
        System.out.println("pay to get pet");
    }

    @Override
    public void getPhoenixDiscount(Upgrade phoenix) {
        System.out.println("pay to get phoenix cheaper");
    }

    @Override
    public void getBJDoubleDiscount(Upgrade bj) {
        System.out.println("pay to get bj cheaper");
    }

    @Override
    public void getBirdPetDiscount(PetComponent pet) {
        GameStage.gameScript.fpc.pets.get(0).buyAndUse();
        System.out.println("pay to get pet cheaper");
    }

    @Override
    public void rateMyApp() {
        Gdx.net.openURI(ANDROID_APP_LINK);
    }

    @Override
    public void restorePurchases() {

    }

    @Override
    public long getObjectSize(Object o) {
        return Main.inst.getObjectSize(o);
    }
}
