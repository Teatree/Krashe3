package com.mygdx.game.android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.*;
import com.mygdx.game.Main;
import com.mygdx.game.AdsController;
import com.mygdx.game.stages.GameScreenScript;

public class AndroidLauncher extends AndroidApplication implements AdsController {

    private static final String INTERSTITIAL_VIDEO_UNIT_ID = "ca-app-pub-4809397092315700/1974891273";
    private static final String INTERSTITIAL_GENERAL_UNIT_ID = "ca-app-pub-4809397092315700/1061404471";

    InterstitialAd interstitialVideoAd;
    InterstitialAd interstitialGeneralAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        View gameView = initializeForView(new Main(this), config);
        setupAds();

        RelativeLayout layout = new RelativeLayout(this);
        layout.addView(gameView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(layout);
    }

    public void setupAds() {

        interstitialVideoAd = new InterstitialAd(this);
        interstitialVideoAd.setAdUnitId(INTERSTITIAL_VIDEO_UNIT_ID);
        AdRequest.Builder builder = new AdRequest.Builder();
        AdRequest ad = builder.build();
        interstitialVideoAd.loadAd(ad);

        interstitialGeneralAd = new InterstitialAd(this);
        interstitialGeneralAd.setAdUnitId(INTERSTITIAL_GENERAL_UNIT_ID);
        AdRequest.Builder builderGen = new AdRequest.Builder();
        AdRequest adGen = builderGen.build();
        interstitialGeneralAd.loadAd(adGen);
    }

    @Override
    public boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return (ni != null && ni.isConnected());
    }

    @Override
    public void showInterstitialVideoAd(final Runnable then) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (then != null) {
                    interstitialVideoAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            Gdx.app.postRunnable(then);
                            AdRequest.Builder builder = new AdRequest.Builder();
                            AdRequest ad = builder.build();
                            interstitialVideoAd.loadAd(ad);
                        }
                    });
                }
                interstitialVideoAd.show();
            }
        });
    }

    @Override
    public void showInterstitialGeneralAd(final Runnable then) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (then != null && !GameScreenScript.fpc.noAds) {
                    interstitialVideoAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            Gdx.app.postRunnable(then);
                            AdRequest.Builder builder = new AdRequest.Builder();
                            AdRequest ad = builder.build();
                            interstitialVideoAd.loadAd(ad);
                        }
                    });
                }
                interstitialVideoAd.show();
            }
        });
    }
}
