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
        // PC only
        Upgrade.getPhoenix().buyAndUse();
    }

    @Override
    public void getBJDouble(Upgrade bj) {
        System.out.println("pay to get bj");
        // PC only
        Upgrade.getBJDouble().buyAndUse();
    }

    @Override
    public void getBirdPet(PetComponent pet) {
        pet.buyAndUse();
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
    public void openFB() {
        System.out.println("I am at Facebook");
    }

    @Override
    public void restorePurchases() {
        System.out.println("restore purchases");
    }

    @Override
    public void signIn() {
        System.out.println("signIn");
    }

    @Override
    public void signOut() {
        System.out.println("signOut");
    }

    @Override
    public void submitScore(int highScore) {
        System.out.println("submitScore");
    }

    @Override
    public void showScore() {
        System.out.println("showScore");
    }

    @Override
    public boolean isSignedIn() {
        return false;
    }

    @Override
    public void setupPlayServices() {
        System.out.println("Set up services");
    }

    @Override
    public void unlockAchievement(String achievementId) {

    }

    @Override
    public void revealAchievement(String achievementId) {

    }

    @Override
    public void incrementAchievement(String achievementId, int steps) {

    }

    @Override
    public void getLeaderboard() {

    }

    @Override
    public void getAchievements() {

    }
}
