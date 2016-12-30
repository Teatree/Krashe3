package com.fd.etf.entity.componets;

import com.fd.etf.stages.GameStage;
import com.fd.etf.stages.ui.TrialTimer;

public abstract class ShopItem {

    public static final  String SOFT = "SOFT";
    public static final String HARD = "HARD";

    public String shopIcon;
    public String name;
    public long cost;
    public String description;
    public String collection;
    public String currencyType;

    public String transactionId;
    public String discountTransactionId;
//    public String logoName;

    public boolean tryPeriod;
    public long tryPeriodDuration;
    public long tryPeriodStart;
    public long tryPeriodTimer;

    //true when was bought (could be not applied)
    public boolean bought;
    //true when is applied now
    public boolean enabled ;

    public abstract void apply (GameStage gameStage);

    public abstract void disable(GameStage gameStage);

    public abstract void buyAndUse(GameStage gameStage);

    public abstract void buyHard();

    public abstract void buyHardDiscount();


    public String updateTryPeriodTimer() {
        tryPeriodTimer = (tryPeriodStart / 1000 + tryPeriodDuration) - System.currentTimeMillis() / 1000;

        int minutes = ((int) tryPeriodTimer) / 60;
        int seconds = ((int) tryPeriodTimer) % 60;
        String result =  "" + minutes + " : " + seconds;
        if (tryPeriodTimer < 0 ){
            result = TrialTimer.TIMER_LBL_TIME_UP;
        }
        return result;
    }
}
