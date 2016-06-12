package com.mygdx.etf;

import com.mygdx.etf.entity.componets.PetComponent;
import com.mygdx.etf.entity.componets.Upgrade;

public interface AllController {

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
}
