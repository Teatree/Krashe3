package com.fd.etf.android.util;

import android.util.Log;
import com.fd.etf.android.AndroidLauncher;
import com.fd.etf.entity.componets.PetComponent;
import com.fd.etf.entity.componets.Upgrade;
import com.fd.etf.stages.GameStage;

import java.util.List;

/**
 * Created by ARudyk on 9/12/2016.
 */
public class EtfIAPhelper {

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;
    static final String SKU_NO_ADS = "no_ads";
    static final String SKU_BJ = "bj";
    static final String SKU_PHOENIX = "phoenix";
//    static final String SKU_PET = "Revive";

    static final String SKU_DISCOUNT_BJ = "bj_promo";
    static final String SKU_DISCOUNT_PHOENIX = "phoenix_promo";


    IabHelper mHelper;
    AndroidLauncher app;

    public EtfIAPhelper(AndroidLauncher app) {
        this.app = app;
    }

    public void setupIAP() {
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgTFXb9o0MvDZ4/JWX1+r/9G9oSxd3tvATOZlkbG1WNerm1orK8OIWTmSQAXdonweM3nMgorvaP+vleRNDQUpDH7tee/OInuAiYBWJCi9f3NPkmGd1FKnH/9tT/xleIiay+fNZ8I2f6vXwkKlFFshhhXWfaL3dJWTG+cuvTU0Y2fGJSy44rvhSACQ/fyFlpOO+TsDGdH5jpAxME415sgAa5HihimUCh0cg6LrsnLk18LMFuPh/f4Oas+2XgJoYmJLFng28gIqix5/Df0fEZrX+mYgNifbswONRilRq3bg8B2A77exMecQ1eN/3RGWYcNMVp+YxU2txLILIeglTJprZwIDAQAB";

        mHelper = new IabHelper(app, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d("IAB", "Problem setting up In-app Billing: " + result);
                }
                Log.d("IAB", "Billing Success: " + result);
            }
        });
    }

    public void onDestroy() {
        if (mHelper != null) try {
            mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
        mHelper = null;
    }

    public void restorePurchases(GameStage gameStage) throws Exception {
        List<String> skus = mHelper.getPurchases();
        if (skus.isEmpty()) {
            for (String sku : skus) {
                if (sku.equals(SKU_BJ) || sku.equals(SKU_DISCOUNT_BJ)) {
                    Upgrade.getBJDouble(gameStage).buyAndUse(gameStage);
                }

                if (sku.equals(SKU_PHOENIX) || sku.equals(SKU_DISCOUNT_PHOENIX)) {
                    Upgrade.getPhoenix(gameStage).buyAndUse(gameStage);
                }

//                if (sku.equals(SKU_PET) || sku.equals(SKU_PROMO_PET)) {
//                    gameStage.gameScript.fpc.pets.get(0).buyAndUse(gameStage);
//                }
            }
        }
    }

    public void iapRemoveAds() {
        // Callback for when a purchase is finished
        try {
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

//                    if (purchase.getSku().equals(SKU_NO_ADS)) {
//                        gameScript.fpc.settings.noAds = true;
//                    }
                }
            };
            mHelper.launchPurchaseFlow(app, SKU_NO_ADS, RC_REQUEST,
                    mPurchaseFinishedListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    public void iapGetPet(final GameStage gameStage, final PetComponent petComponent) {
        try {
            IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
                public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                    if (purchase == null) return;
                    Log.d("IAB", "Purchase finished: " + result + ", purchase: " + purchase);

                    if (mHelper == null) return;

                    if (result.isFailure()) {
                        return;
                    }

                    if (purchase.getSku().equals(petComponent.sku)) {
                        petComponent.buyAndUse(gameStage);
                    }
                }
            };
            mHelper.launchPurchaseFlow(app, SKU_NO_ADS, RC_REQUEST,
                    mPurchaseFinishedListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    public void iapGetBj(final GameStage gameStage, final Upgrade bj) {
        try {
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
                        bj.buyAndUse(gameStage);
                    }
                }
            };
            mHelper.launchPurchaseFlow(app, SKU_NO_ADS, RC_REQUEST,
                    mPurchaseFinishedListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    public void iapGetPhoenix(final GameStage gameStage, final Upgrade phoenix) {
        try {
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
                        phoenix.buyAndUse(gameStage);
                    }
                }
            };
            mHelper.launchPurchaseFlow(app, SKU_PHOENIX, RC_REQUEST,
                    mPurchaseFinishedListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    public void iapGetPetDiscount(final GameStage gameStage, final PetComponent petComponent) {
        try {
            IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
                public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                    if (purchase == null) return;
                    Log.d("IAB", "Purchase finished: " + result + ", purchase: " + purchase);

                    if (mHelper == null) return;

                    if (result.isFailure()) {
                        return;
                    }

                    if (purchase.getSku().equals(petComponent.sku_discount)) {
                        petComponent.buyAndUse(gameStage);
                    }
                }
            };
            mHelper.launchPurchaseFlow(app, SKU_NO_ADS, RC_REQUEST,
                    mPurchaseFinishedListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    public void iapGetBjDiscount(final GameStage gameStage, final Upgrade bj) {
        try {
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
                        bj.buyAndUse(gameStage);
                    }
                }
            };
            mHelper.launchPurchaseFlow(app, SKU_NO_ADS, RC_REQUEST,
                    mPurchaseFinishedListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    public void iapGetPhoenixDiscount(final GameStage gameStage, final Upgrade phoenix) {
        try {
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
                        phoenix.buyAndUse(gameStage);
                    }
                }
            };
            mHelper.launchPurchaseFlow(app, SKU_PHOENIX, RC_REQUEST,
                    mPurchaseFinishedListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

}
