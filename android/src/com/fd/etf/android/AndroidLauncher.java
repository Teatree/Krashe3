package com.fd.etf.android;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.fd.etf.AllController;
import com.fd.etf.Main;
import com.fd.etf.android.util.EtfAdsHelper;
import com.fd.etf.android.util.EtfIAPhelper;
import com.fd.etf.android.util.EtfPlayServicesHelper;
import com.fd.etf.entity.componets.PetComponent;
import com.fd.etf.entity.componets.Upgrade;

public class AndroidLauncher extends AndroidApplication implements AllController {

    EtfIAPhelper iapHelper;
    EtfAdsHelper adsHelper;
    EtfPlayServicesHelper psHelper;
    Main game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupIAP();

        game = new Main(this);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        View gameView = initializeForView(game, config);
        setupAds();
        psHelper = new EtfPlayServicesHelper(this);
        psHelper.setupPlayServices();

        RelativeLayout layout = new RelativeLayout(this);
        layout.addView(gameView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(layout);
    }

    public void setupPlayServices() {
        psHelper.setupPlayServices();
    }

    private void setupIAP() {
        iapHelper = new EtfIAPhelper(this);
        iapHelper.setupIAP();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        psHelper.onStart();
    }

    public void setupAds() {
        adsHelper = new EtfAdsHelper(this);
        adsHelper.setupAds();
    }

    @Override
    public boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = cm.getActiveNetworkInfo();
        if (i == null) {
            return false;
        }
        if (!i.isConnected()) {
            return false;
        }
        if (!i.isAvailable()) {
            return false;
        }
        return true;
//        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        NetworkInfo mobileInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//
//        return (wifiInfo != null && wifiInfo.isConnected());
    }

    @Override
    public void showReviveVideoAd(final Runnable then) {
        adsHelper.showReviveVideoAd(then);
    }

    @Override
    public void showGetMoneyVideoAd(final Runnable then) {
        adsHelper.showGetMoneyVideoAd(then);
    }

    @Override
    public void showLaunchAd(final Runnable then) {
        adsHelper.showLaunchAd(then);
    }

    @Override
    public void showResultScreenAd(final Runnable then) {
        adsHelper.showResultScreenAd(then);
    }

    @Override
    public void showGeneralShopAd(final Runnable then) {
        adsHelper.showGeneralShopAd(then);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iapHelper.onDestroy();
    }

    public void removeAds() {
        iapHelper.iapRemoveAds();
    }

    @Override
    public void getPhoenix(Upgrade phoenix) {
        iapHelper.iapGetPhoenix(phoenix);
    }

    @Override
    public void getBJDouble(Upgrade bj) {
        iapHelper.iapGetBj(bj);
    }


    @Override
    public void getBirdPet(PetComponent petComponent) {
        iapHelper.iapGetBirdPet(petComponent);
    }

    @Override
    public void getPhoenixDiscount(Upgrade phoenix) {
        iapHelper.iapGetPhoenixDiscount(phoenix);

    }

    @Override
    public void getBJDoubleDiscount(Upgrade bj) {
        iapHelper.iapGetBjDiscount(bj);
    }


    @Override
    public void getBirdPetDiscount(PetComponent petComponent) {
        iapHelper.iapGetBirdPetDiscount(petComponent);
    }

    @Override
    public void rateMyApp() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ANDROID_APP_LINK)));
    }

    @Override
    public void restorePurchases() throws Exception {
        iapHelper.restorePurchases();
    }

    //--------------------------PLAY SERVICES -----------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        psHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void signIn() {
        psHelper.signIn();
    }

    @Override
    public void signOut() {
        psHelper.signOut();
    }

    @Override
    public void submitScore(int highScore) {
        psHelper.submitScore(highScore);
    }

    @Override
    public void showScore() {
        psHelper.showScore();
    }

    @Override
    public boolean isSignedIn() {
        return psHelper.isSignedIn();
    }

    @Override
    public void unlockAchievement(String achievementId) {
        psHelper.unlockAchievement(achievementId);
    }

    @Override
    public void revealAchievement(String achievementId) {

    }

    @Override
    public void incrementAchievement(String achievementId, int steps) {

    }

    @Override
    public void getLeaderboard() {
        psHelper.getLeaderboard();
    }

    @Override
    public void getAchievements() {
        psHelper.getAchievements();
    }

    //TODO: ADD open in FB app
    @Override
    public void openFB() {
        Gdx.net.openURI("https://facebook.com/Teatree1992");
    }
}
