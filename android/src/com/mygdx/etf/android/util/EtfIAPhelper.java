package com.mygdx.etf.android.util;

import android.util.Log;
import com.mygdx.etf.android.AndroidLauncher;
import com.mygdx.etf.entity.componets.PetComponent;
import com.mygdx.etf.entity.componets.Upgrade;
import com.mygdx.etf.stages.GameStage;

import java.util.List;

import static com.mygdx.etf.stages.GameStage.gameScript;

/**
 * Created by ARudyk on 9/12/2016.
 */
public class EtfIAPhelper {

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;
    static final String SKU_NO_ADS = "no_ads";
    static final String SKU_BJ = "bj";
    static final String SKU_PHOENIX = "phoenix";
    static final String SKU_PET = "Revive";

    static final String SKU_PROMO_BJ = "bj_promo";
    static final String SKU_PROMO_PHOENIX = "phoenix_promo";
    static final String SKU_PROMO_PET = "Revive_promo";


    IabHelper mHelper;
    AndroidLauncher app;

    public EtfIAPhelper(AndroidLauncher app) {
        this.app = app;
    }

    public void setupIAP() {
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkG5yyQ0/X40mDtDgG/YnL5qZnrdsWfezQgPPUklXRyOZcsXzapMIxuQjkKefvmk+XOP3hUvco9lWZ2G07dW3S8qHepvdEFZFXpscdFICUP9dz3lPMvw4LDsuEoBgB6ioZB0XYlU751qEsBygvDWPjXLSgtY4vmfOJc55rYrbVaHxhnBW3dxAfOLO1Eh/3OPIHiVQXoRcvrSSfJ/Ct7znTHknHZtGtuuWmIOBC69WqXygyof8BSCvPP+D3KT4+lRwKyzJqSQCsCTGVAMQsRtO4CcTC18G13GsLXWaVoiv8Bv0Vzh0tGsyk93GqqHlnhZCads196ePX+QFyfuz4dKDOwIDAQAB";

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

    public void restorePurchases() throws Exception {
        List<String> skus = mHelper.getPurchases();
        if (skus.isEmpty()) {
            for (String sku : skus) {
                if (sku.equals(SKU_BJ) || sku.equals(SKU_PROMO_BJ)) {
                    Upgrade.getBJDouble().buyAndUse();
                }

                if (sku.equals(SKU_PHOENIX) || sku.equals(SKU_PROMO_PHOENIX)) {
                    Upgrade.getPhoenix().buyAndUse();
                }

                if (sku.equals(SKU_PET) || sku.equals(SKU_PROMO_PET)) {
                    GameStage.gameScript.fpc.pets.get(0).buyAndUse();
                }
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

                    if (purchase.getSku().equals(SKU_NO_ADS)) {
                        gameScript.fpc.settings.noAds = true;
                    }
                }
            };
            mHelper.launchPurchaseFlow(app, SKU_NO_ADS, RC_REQUEST,
                    mPurchaseFinishedListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    public void iapGetBirdPet(final PetComponent petComponent) {
        try {
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
            mHelper.launchPurchaseFlow(app, SKU_NO_ADS, RC_REQUEST,
                    mPurchaseFinishedListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    public void iapGetBj(final Upgrade bj) {
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
                        bj.buyAndUse();
                    }
                }
            };
            mHelper.launchPurchaseFlow(app, SKU_NO_ADS, RC_REQUEST,
                    mPurchaseFinishedListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    public void iapGetPhoenix(final Upgrade phoenix) {
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
                        phoenix.buyAndUse();
                    }
                }
            };
            mHelper.launchPurchaseFlow(app, SKU_PHOENIX, RC_REQUEST,
                    mPurchaseFinishedListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    public void iapGetBirdPetDiscount(final PetComponent petComponent) {
        try {
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
            mHelper.launchPurchaseFlow(app, SKU_NO_ADS, RC_REQUEST,
                    mPurchaseFinishedListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    public void iapGetBjDiscount(final Upgrade bj) {
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
                        bj.buyAndUse();
                    }
                }
            };
            mHelper.launchPurchaseFlow(app, SKU_NO_ADS, RC_REQUEST,
                    mPurchaseFinishedListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    public void iapGetPhoenixDiscount(final Upgrade phoenix) {
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
                        phoenix.buyAndUse();
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
