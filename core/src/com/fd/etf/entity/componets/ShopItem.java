package com.fd.etf.entity.componets;

import com.fd.etf.stages.GameStage;
import com.fd.etf.stages.ui.TrialTimer;

public abstract class ShopItem {

    public static final  String SOFT = "SOFT";
    public static final String HARD = "HARD";

    public String sku;
    public String sku_discount;

    public String shopIcon;
    public String name;
    public long cost;
    public long disc;
    public long costDisc;

    public String description;
    public String collection;
    public String currencyType;

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

    public abstract void buyHard(GameStage gameStage);

    public abstract void buyHardDiscount(GameStage gameStage);

    public abstract boolean hasPet();

    protected void disablePetItems(GameStage gameStage) {
        //Disable pet items
        if (this.hasPet() && gameStage.shopScript != null) {
            for (ShopItem si : gameStage.shopScript.allHCItems) {
                if (si.bought && si.hasPet() && si != this) {
                    si.disable(gameStage);
                }
            }

            for (ShopItem si : gameStage.shopScript.allSoftItems) {
                if (si.bought && si.hasPet() && si != this) {
                    si.disable(gameStage);
                }
            }
        }
    }

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

    public String getTimerTimeTime() {
        int minutes = ((int) tryPeriodDuration) / 60;
        int seconds = ((int) tryPeriodDuration) % 60;
        String result = minutes == 0 ? "" : "" + minutes + " MINUTE ";
        result += seconds == 0 ? " " : seconds + " SECONDS ";
        return result;
    }
}
