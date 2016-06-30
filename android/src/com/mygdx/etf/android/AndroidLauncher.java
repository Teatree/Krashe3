package com.mygdx.etf.android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.mygdx.etf.AllController;
import com.mygdx.etf.Main;
import com.mygdx.etf.android.util.IabHelper;
import com.mygdx.etf.android.util.IabResult;
import com.mygdx.etf.android.util.Purchase;
import com.mygdx.etf.entity.componets.PetComponent;
import com.mygdx.etf.entity.componets.Upgrade;

import static com.mygdx.etf.stages.GameStage.gameScript;

public class AndroidLauncher extends AndroidApplication implements AllController {

    private static final String INTERSTITIAL_VIDEO_UNIT_ID = "ca-app-pub-4809397092315700/1974891273";
    private static final String INTERSTITIAL_GENERAL_UNIT_ID = "ca-app-pub-4809397092315700/1061404471";

    InterstitialAd interstitialVideoAd;
    InterstitialAd interstitialGeneralAd;

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;
    static final String SKU_NO_ADS = "no_ads";
    static final String SKU_BJ = "bj";
    static final String SKU_PHOENIX = "phoenix";
    static final String SKU_PET = "Revive";

    private Main game;

    IabHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        game = new Main(this);
        setupIAP();

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        View gameView = initializeForView(game, config);
        setupAds();

        RelativeLayout layout = new RelativeLayout(this);
        layout.addView(gameView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(layout);
    }

    private void setupIAP() {
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkG5yyQ0/X40mDtDgG/YnL5qZnrdsWfezQgPPUklXRyOZcsXzapMIxuQjkKefvmk+XOP3hUvco9lWZ2G07dW3S8qHepvdEFZFXpscdFICUP9dz3lPMvw4LDsuEoBgB6ioZB0XYlU751qEsBygvDWPjXLSgtY4vmfOJc55rYrbVaHxhnBW3dxAfOLO1Eh/3OPIHiVQXoRcvrSSfJ/Ct7znTHknHZtGtuuWmIOBC69WqXygyof8BSCvPP+D3KT4+lRwKyzJqSQCsCTGVAMQsRtO4CcTC18G13GsLXWaVoiv8Bv0Vzh0tGsyk93GqqHlnhZCads196ePX+QFyfuz4dKDOwIDAQAB";

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d("IAB", "Problem setting up In-app Billing: " + result);
                }
                Log.d("IAB", "Billing Success: " + result);
            }
        });
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
    public void showReviveVideoAd(final Runnable then) {
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
    public void showGetMoneyVideoAd(final Runnable then) {
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
    public void showLaunchAd(final Runnable then) {

        runOnUiThread(new Runnable() {
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

    @Override
    public void showResultScreenAd(final Runnable then) {
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
    public void showGeneralShopAd(final Runnable then) {
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
    protected void onDestroy() {
        super.onDestroy();
        if (mHelper != null) try {
            mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
        mHelper = null;
    }

    public void removeAds() {
        try {
            iapRemoveAds();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getPhoenix(Upgrade phoenix) {
        try {
            iapGetPhoenix(phoenix);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getBJDouble(Upgrade bj) {
        try {
            iapGetBj(bj);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void getBirdPet(PetComponent petComponent) {
        try {
            iapGetBirdPet(petComponent);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rateMyApp() {
        Gdx.net.openURI(ANDROID_APP_LINK);
    }

    @Override
    public void restorePurchases() {

    }

    public void iapRemoveAds() throws IabHelper.IabAsyncInProgressException {
        // Callback for when a purchase is finished
        IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                if (purchase == null) return;
                Log.d("IAB", "Purchase finished: " + result + ", purchase: " + purchase);

                // if we were disposed of in the meantime, quit.
                if (mHelper == null) return;

                if (result.isFailure()) {
                    //complain("Error purchasing: " + result);
                    //setWaitScreen(false);
                    return;
                }
//            if (!verifyDeveloperPayload(purchase)) {
//                //complain("Error purchasing. Authenticity verification failed.");
//                //setWaitScreen(false);
//                return;
//            }

                Log.d("IAB", "Purchase successful.");

                if (purchase.getSku().equals(SKU_NO_ADS)) {
                    gameScript.fpc.settings.noAds = true;
                }
            }
        };
        mHelper.launchPurchaseFlow(this, SKU_NO_ADS, RC_REQUEST,
                mPurchaseFinishedListener);
    }

    public void iapGetBirdPet(final PetComponent petComponent) throws IabHelper.IabAsyncInProgressException {
        IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                if (purchase == null) return;
                Log.d("IAB", "Purchase finished: " + result + ", purchase: " + purchase);

                if (mHelper == null) return;

                if (result.isFailure()) {
                    return;
                }

                if (purchase.getSku().equals(SKU_PET)) {
                    petComponent.buyAndUse();
                }
            }
        };
        mHelper.launchPurchaseFlow(this, SKU_NO_ADS, RC_REQUEST,
                mPurchaseFinishedListener);
    }

    public void iapGetBj(final Upgrade bj) throws IabHelper.IabAsyncInProgressException {
        IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                if (purchase == null) return;
                Log.d("IAB", "Purchase finished: " + result + ", purchase: " + purchase);

                if (mHelper == null) return;
                if (result.isFailure()) {
                    return;
                }

                Log.d("IAB", "Purchase successful.");
                if (purchase.getSku().equals(SKU_BJ)) {
                    bj.buyAndUse();
                }
            }
        };
        mHelper.launchPurchaseFlow(this, SKU_NO_ADS, RC_REQUEST,
                mPurchaseFinishedListener);
    }

    public void iapGetPhoenix(final Upgrade phoenix) throws IabHelper.IabAsyncInProgressException {
        IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                if (purchase == null) return;
                Log.d("IAB", "Purchase finished: " + result + ", purchase: " + purchase);

                if (mHelper == null) return;
                if (result.isFailure()) {
                    return;
                }

                Log.d("IAB", "Purchase successful.");
                if (purchase.getSku().equals(SKU_PHOENIX)) {
                    phoenix.buyAndUse();
                }
            }
        };
        mHelper.launchPurchaseFlow(this, SKU_PHOENIX, RC_REQUEST,
                mPurchaseFinishedListener);
    }
}
