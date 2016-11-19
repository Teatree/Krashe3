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
import com.fd.etf.entity.componets.PetComponent;
import com.fd.etf.entity.componets.Upgrade;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;

public class AndroidLauncher extends AndroidApplication implements AllController {

    EtfIAPhelper iapHelper;
    EtfAdsHelper adsHelper;

    Main game;
    //game play service
    private GameHelper gameHelper;
    private final static int requestCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupIAP();

        game = new Main(this);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        View gameView = initializeForView(game, config);
        setupAds();
        if (isWifiConnected())
            setupPlayServices();

        RelativeLayout layout = new RelativeLayout(this);
        layout.addView(gameView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(layout);
    }

    public void setupPlayServices() {
        gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        gameHelper.enableDebugLog(true);

        final GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener() {
            @Override
            public void onSignInFailed() {
            }

            @Override
            public void onSignInSucceeded() {
            }
        };

        gameHelper.setup(gameHelperListener);
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
        if (isWifiConnected())
            gameHelper.onStart(this);
    }

    public void setupAds() {
        adsHelper = new EtfAdsHelper(this);
        adsHelper.setupAds();
    }

    @Override
    public boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return (ni != null && ni.isConnected());
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
        gameHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void signIn() {
        if(isWifiConnected()){
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gameHelper.beginUserInitiatedSignIn();
                    }
                });
            } catch (Exception e) {
                Gdx.app.log("MainActivity", "Log in failed: " + e.getMessage() + ".");
            }
        }
    }

    @Override
    public void signOut() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameHelper.signOut();
                }
            });
        } catch (Exception e) {
            Gdx.app.log("MainActivity", "Log out failed: " + e.getMessage() + ".");
        }
    }

    @Override
    public void submitScore(int highScore) {
        if (isSignedIn()) {
            Games.Leaderboards.submitScore(gameHelper.getApiClient(),
                    getString(R.string.leaderboard_leaderboard), highScore);
        }
    }

    @Override
    public void showScore() {
        if (isSignedIn()) {
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(),
                    getString(R.string.leaderboard_leaderboard)), requestCode);
        } else {
            signIn();
        }
    }

    @Override
    public boolean isSignedIn() {
        return gameHelper.isSignedIn();
    }

    @Override
    public void unlockAchievement(String achievementId) {
        Games.Achievements.unlock(gameHelper.getApiClient(),
                getString(R.string.achievement_queens_slayer));
    }

    @Override
    public void revealAchievement(String achievementId) {

    }

    @Override
    public void incrementAchievement(String achievementId, int steps) {

    }

    @Override
    public void getLeaderboard() {
        if (isSignedIn()) {
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(),
                    getString(R.string.leaderboard_leaderboard)), requestCode);
        } else {
            signIn();
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(),
                    getString(R.string.leaderboard_leaderboard)), requestCode);
        }
    }

    @Override
    public void getAchievements() {
        if (isSignedIn()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), requestCode);
        } else {
            signIn();
        }
    }

    //TODO: ADD open in FB app
    @Override
    public void openFB() {
        Gdx.net.openURI("https://facebook.com/Teatree1992");
    }
}
