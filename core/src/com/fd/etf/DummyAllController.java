package com.fd.etf;

import com.badlogic.gdx.Gdx;
import com.fd.etf.entity.componets.PetComponent;
import com.fd.etf.entity.componets.Upgrade;
import com.fd.etf.stages.GameStage;

public class DummyAllController implements AllController {

    @Override
    public boolean isWifiConnected() {
        return false;
    }

    @Override
    public void showReviveVideoAd(Runnable then) {
        //System.out.println("showReviveVideoAd");
    }

    @Override
    public boolean isAds() {
        return false;
    }

    @Override
    public void showGetMoneyVideoAd(Runnable then) {
        //System.out.println("showGetMoneyVideoAd");
    }

    @Override
    public void showLaunchAd(Runnable then) {
        //System.out.println("showLaunchAd");
    }

    @Override
    public void showResultScreenAd(Runnable then) {
        //System.out.println("showResultScreenAd");
    }

    @Override
    public void showGeneralShopAd(Runnable then) {
        //System.out.println("showGeneralShopAd");
    }

    @Override
    public void removeAds() {
        //System.out.println("pay to remove ads");
    }

    @Override
    public void getPhoenix(GameStage gameStage, Upgrade phoenix) {
        //System.out.println("pay to get phoenix");
        // PC only
        Upgrade.getPhoenix(gameStage).buyAndUse(gameStage);
    }

    @Override
    public void getBJDouble(GameStage gameStage, Upgrade bj) {
        //System.out.println("pay to get bj");
        // PC only
        Upgrade.getBJDouble(gameStage).buyAndUse(gameStage);
    }

    @Override
    public void getPet(GameStage gameStage, PetComponent pet) {
        pet.buyAndUse(gameStage);
        //System.out.println("pay to get pet");
    }

    @Override
    public void getPhoenixDiscount(GameStage gameStage, Upgrade phoenix) {
        //System.out.println("pay to get phoenix cheaper");
        Upgrade.getPhoenix(gameStage).buyAndUse(gameStage);
    }

    @Override
    public void getBJDoubleDiscount(GameStage gameStage, Upgrade bj) {
        //System.out.println("pay to get bj cheaper");
        Upgrade.getBJDouble(gameStage).buyAndUse(gameStage);
    }

    @Override
    public void getPetDiscount(GameStage gameStage, PetComponent pet) {
//        GameStage.gameScript.fpc.pets.get(0).buyAndUse();
        //System.out.println("pay to get pet cheaper");
    }

    @Override
    public void rateMyApp() {
        Gdx.net.openURI(ANDROID_APP_LINK);
    }

    @Override
    public void openFB() {
        //System.out.println("I am at Facebook");
    }

    @Override
    public void restorePurchases(GameStage gameStage) {
        //System.out.println("restore purchases");
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
