package com.fd.etf;

import com.fd.etf.entity.componets.PetComponent;
import com.fd.etf.entity.componets.Upgrade;
import com.fd.etf.stages.GameStage;

public interface AllController extends PlayServices {

    String ANDROID_APP_LINK = "https://playMenu.google.com/store/apps/details?id=com.fd.etf.android";

    boolean isWifiConnected();

    void showReviveVideoAd(Runnable then);

    boolean isAds();

    void showGetMoneyVideoAd(Runnable then);

    void showLaunchAd(Runnable then);

    void showAPAd(Runnable then);

    void showGeneralShopAd(Runnable then);

    void removeAds();

//    void getPhoenix(Upgrade phoenix);
//
//    void getBJDouble(Upgrade bj);
//
//    void getBirdPet(PetComponent pet);
//
//    void getPhoenixDiscount(Upgrade phoenix);
//
//    void getBJDoubleDiscount(Upgrade bj);

    void getPhoenix(GameStage gameStage, Upgrade phoenix);

    void getBJDouble(GameStage gameStage, Upgrade bj);

    void getPet(GameStage gameStage, PetComponent pet);

    void getPhoenixDiscount(GameStage gameStage, Upgrade phoenix);

    void getBJDoubleDiscount(GameStage gameStage, Upgrade bj);

    void getPetDiscount(GameStage gameStage, PetComponent pet);

    void rateMyApp();

    void restorePurchases(GameStage gameStage) throws Exception;

    void openFB();

}
