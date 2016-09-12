package com.mygdx.etf.android.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.badlogic.gdx.Gdx;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.mygdx.etf.android.AndroidLauncher;

/**
 * Created by ARudyk on 9/12/2016.
 */
public class EtfAdsHelper {

    AndroidLauncher app;
    private static final String INTERSTITIAL_VIDEO_UNIT_ID = "ca-app-pub-4809397092315700/1974891273";
    private static final String INTERSTITIAL_GENERAL_UNIT_ID = "ca-app-pub-4809397092315700/1061404471";

    InterstitialAd interstitialVideoAd;
    InterstitialAd interstitialGeneralAd;

    public EtfAdsHelper(AndroidLauncher app) {
        this.app = app;
    }

    public void setupAds() {

        interstitialVideoAd = new InterstitialAd(app);
        interstitialVideoAd.setAdUnitId(INTERSTITIAL_VIDEO_UNIT_ID);
        AdRequest.Builder builder = new AdRequest.Builder();
        AdRequest ad = builder.build();
        interstitialVideoAd.loadAd(ad);

        interstitialGeneralAd = new InterstitialAd(app);
        interstitialGeneralAd.setAdUnitId(INTERSTITIAL_GENERAL_UNIT_ID);
        AdRequest.Builder builderGen = new AdRequest.Builder();
        AdRequest adGen = builderGen.build();
        interstitialGeneralAd.loadAd(adGen);
    }

    public void showReviveVideoAd(final Runnable then) {
        app.runOnUiThread(new Runnable() {
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

    public void showGetMoneyVideoAd(final Runnable then) {
        app.runOnUiThread(new Runnable() {
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

    public void showLaunchAd(final Runnable then) {

        app.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                interstitialVideoAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        Gdx.app.postRunnable(then);
                        AdRequest.Builder builder = new AdRequest.Builder();
                        AdRequest ad = builder.build();
                        interstitialVideoAd.loadAd(ad);
                    }
                });

                interstitialVideoAd.show();
            }
        });
    }

    public void showResultScreenAd(final Runnable then) {
        app.runOnUiThread(new Runnable() {
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

    public void showGeneralShopAd(final Runnable then) {
        app.runOnUiThread(new Runnable() {
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
}
