package com.mygdx.etf.android;

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
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;
import com.mygdx.etf.AllController;
import com.mygdx.etf.Main;
import com.mygdx.etf.android.util.*;
import com.mygdx.etf.entity.componets.PetComponent;
import com.mygdx.etf.entity.componets.Upgrade;

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
        setupPlayServices();
        RelativeLayout layout = new RelativeLayout(this);
        layout.addView(gameView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(layout);
    }

    public void setupPlayServices() {
        gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        gameHelper.enableDebugLog(false);

        GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener() {
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
//        setupPlayServices();
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
        try {
            iapHelper.iapRemoveAds();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getPhoenix(Upgrade phoenix) {
        try {
            iapHelper.iapGetPhoenix(phoenix);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getBJDouble(Upgrade bj) {
        try {
            iapHelper.iapGetBj(bj);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void getBirdPet(PetComponent petComponent) {
        try {
            iapHelper.iapGetBirdPet(petComponent);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getPhoenixDiscount(Upgrade phoenix) {
        try {
            iapHelper.iapGetPhoenixDiscount(phoenix);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getBJDoubleDiscount(Upgrade bj) {
        try {
            iapHelper.iapGetBjDiscount(bj);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void getBirdPetDiscount(PetComponent petComponent) {
        try {
            iapHelper.iapGetBirdPetDiscount(petComponent);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rateMyApp() {
        Gdx.net.openURI(ANDROID_APP_LINK);
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
        System.err.println(">>>> Sign In");
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

    //TODO: RATE THE GAME!!!!!!
    @Override
    public void rateGame() {
        String str = "Your PlayStore Link";
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
    }

    @Override
    public void submitScore(int highScore) {
        if (isSignedIn()) {
            Games.Leaderboards.submitScore(gameHelper.getApiClient(),
                    getString(R.string.leaderboard_highest), highScore);
        }
    }

    @Override
    public void showScore() {
        if (isSignedIn()) {
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(),
                    getString(R.string.leaderboard_highest)), requestCode);
        } else {
            signIn();
        }
    }

    @Override
    public boolean isSignedIn() {
        return gameHelper.isSignedIn();
    }
}
