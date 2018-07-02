package com.fd.etf.android.util;

import com.badlogic.gdx.Gdx;
import com.fd.etf.android.AndroidLauncher;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by ARudyk on 9/12/2016.
 */
public class EtfAdsHelper {

    AndroidLauncher app;
    private static final String INTERSTITIAL_VIDEO_UNIT_ID = "ca-app-pub-4809397092315700/1974891273";
    private static final String INTERSTITIAL_LAUNCH_UNIT_ID = "ca-app-pub-4809397092315700/1061404471";
    private static final String INTERSTITIAL_BETWEEN_SCREENS_ID = "ca-app-pub-4809397092315700/8386757762";

    public InterstitialAd interstitialVideoAd;
    public volatile boolean isAdLoaded = false;
    private InterstitialAd interstitialGeneralAd;
    private InterstitialAd interstitialBetweenScreensAd;

    public EtfAdsHelper(AndroidLauncher app) {
        this.app = app;
    }

    public void setupAds() {

        interstitialVideoAd = new InterstitialAd(app);
        interstitialVideoAd.setAdUnitId(INTERSTITIAL_VIDEO_UNIT_ID);
        AdRequest.Builder builder = new AdRequest.Builder();
        AdRequest ad = builder.build();
        interstitialVideoAd.loadAd(ad);
        isAdLoaded = true;

        interstitialGeneralAd = new InterstitialAd(app);
        interstitialGeneralAd.setAdUnitId(INTERSTITIAL_LAUNCH_UNIT_ID);
        AdRequest.Builder builderGen = new AdRequest.Builder();
        AdRequest adGen = builderGen.build();
        interstitialGeneralAd.loadAd(adGen);

        interstitialBetweenScreensAd = new InterstitialAd(app);
        interstitialBetweenScreensAd.setAdUnitId(INTERSTITIAL_BETWEEN_SCREENS_ID);
        AdRequest.Builder builderGen2 = new AdRequest.Builder();
        AdRequest adGen2 = builderGen2.build();
        interstitialBetweenScreensAd.loadAd(adGen2);
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

//                            isAdLoaded = interstitialVideoAd.isLoaded();
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();

                            isAdLoaded = true;
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
                    interstitialGeneralAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            Gdx.app.postRunnable(then);
                            AdRequest.Builder builder = new AdRequest.Builder();
                            AdRequest ad = builder.build();
                            interstitialGeneralAd.loadAd(ad);
                        }
                    });

                    interstitialGeneralAd.show();
                }
            });
    }

    public void showResultScreenAd(final Runnable then) {
        app.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (then != null) {
                    interstitialBetweenScreensAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            Gdx.app.postRunnable(then);
                            AdRequest.Builder builder = new AdRequest.Builder();
                            AdRequest ad = builder.build();
                            interstitialBetweenScreensAd.loadAd(ad);
                        }
                    });
                }
                interstitialBetweenScreensAd.show();
            }
        });
    }

    public void showGeneralShopAd(final Runnable then) {
        app.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (then != null) {
                    interstitialBetweenScreensAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            Gdx.app.postRunnable(then);
                            AdRequest.Builder builder = new AdRequest.Builder();
                            AdRequest ad = builder.build();
                            interstitialBetweenScreensAd.loadAd(ad);
                        }
                    });
                }
                interstitialBetweenScreensAd.show();
            }
        });
    }
}
