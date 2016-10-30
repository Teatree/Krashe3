package com.fd.etf;

import com.fd.etf.entity.componets.PetComponent;
import com.fd.etf.entity.componets.Upgrade;

public interface AllController extends PlayServices {

    String ANDROID_APP_LINK = "https://play.google.com/store/apps/details?id=com.fd.etf.android";

    boolean isWifiConnected();

    void showReviveVideoAd(Runnable then);

    void showGetMoneyVideoAd(Runnable then);

    void showLaunchAd(Runnable then);

    void showResultScreenAd(Runnable then);

    void showGeneralShopAd(Runnable then);

    void removeAds();

    void getPhoenix(Upgrade phoenix);

    void getBJDouble(Upgrade bj);

    void getBirdPet(PetComponent pet);

    void getPhoenixDiscount(Upgrade phoenix);

    void getBJDoubleDiscount(Upgrade bj);

    void getBirdPetDiscount(PetComponent pet);

    void rateMyApp();

    void openFB();

    void restorePurchases() throws  Exception;
}
