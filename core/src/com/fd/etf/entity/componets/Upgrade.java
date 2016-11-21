package com.fd.etf.entity.componets;

import com.fd.etf.Main;
import com.fd.etf.stages.GameStage;
import com.fd.etf.system.BugSystem;
import com.fd.etf.utils.SaveMngr;

import java.util.ArrayList;
import java.util.List;

public class Upgrade extends ShopItem{

    public UpgradeType upgradeType;
    public int counter;

//    public boolean tryPeriod;
//    public long tryPeriodDuration;
//    public long tryPeriodStart;
//    public long tryPeriodTimer;

    public Upgrade() {
    }

    public Upgrade(SaveMngr.UpgradeStats us) {
        this.upgradeType = UpgradeType.valueOf(us.upgradeType);
        this.tryPeriod = us.tryPeriod;
        this.tryPeriodDuration = us.tryPeriodDuration;
        this.tryPeriodTimer = us.tryPeriodTimer;
        this.tryPeriodStart = us.tryPeriodStart;
        this.bought = us.bought;
        this.enabled = us.enabled;
    }

    public static List<Upgrade> getAllUpgrades() {
        List<Upgrade> all = new ArrayList<>();
        all.add(getPhoenix());
        all.add(getBJDouble());
        return all;
    }

    public static Upgrade getUpgrade (UpgradeType type){
        return type.equals(UpgradeType.PHOENIX) ? getPhoenix() : getBJDouble();
    }

    public static Upgrade getPhoenix() {
        Upgrade phoenix = new Upgrade();
        phoenix.upgradeType = UpgradeType.PHOENIX;
        phoenix.cost = 13;
        phoenix.name = "phoenix";
        phoenix.bought = false;
        phoenix.description = "You will not die! ... ";
        phoenix.enabled = false;
        phoenix.currencyType = HARD;
//        phoenix.logoName = "itemfedora";
        phoenix.shopIcon = "itemfedora";
//        phoenix.transactionId = Main.phoenix_trans_ID;
        return phoenix;
    }

    public static Upgrade getBJDouble() {
        Upgrade bjd = new Upgrade();
        bjd.upgradeType = UpgradeType.BJ_DOUBLE;
        bjd.cost = 13;
        bjd.name = "bj_double";
        bjd.bought = false;
        bjd.description = "more juice \\0/";
        bjd.enabled = false;
        bjd.currencyType = HARD;
//        bjd.logoName = "itemwig";
        bjd.shopIcon = "itemwig";
//        bjd.transactionId = Main.bj_double_trans_ID;
        return bjd;
    }

    @Override
    public void apply() {
        this.enabled = true;
        GameStage.gameScript.fpc.upgrades.put(this.upgradeType, this);
    }

    @Override
    public void disable() {
        this.enabled = false;
    }

    @Override
    public void buyAndUse() {
        this.bought = true;
        apply();
    }

    @Override
    public void buyHard() {
        this.bought = true;
        this.enabled = true;

        if (upgradeType.equals(UpgradeType.PHOENIX)){
            Main.mainController.getPhoenix(this);
        }

        if (upgradeType.equals(UpgradeType.BJ_DOUBLE)){
            Main.mainController.getBJDouble(this);
        }
    }

    @Override
    public void buyHardDiscount() {
        if (upgradeType.equals(UpgradeType.PHOENIX)){
            Main.mainController.getPhoenixDiscount(this);
        }

        if (upgradeType.equals(UpgradeType.BJ_DOUBLE)){
            Main.mainController.getBJDoubleDiscount(this);
        }
    }

    public void usePhoenix() {
        BugSystem.blowUpAllBugs = true;
        FlowerComponent.state = FlowerComponent.State.PHOENIX;
        counter++;
    }
//
//    public String updateTryPeriodTimer() {
//        float deltaTime = Gdx.graphics.getDeltaTime();
//
//        tryPeriodTimer = (tryPeriodStart / 1000 + tryPeriodDuration) - System.currentTimeMillis() / 1000;
//
//        int minutes = ((int) tryPeriodTimer) / 60;
//        int seconds = ((int) tryPeriodTimer) % 60;
//        return "" + minutes + " : " + seconds;
//    }

    public enum UpgradeType {
        PHOENIX, BJ_DOUBLE
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Upgrade) {
            Upgrade newObj = (Upgrade) obj;
            return newObj.upgradeType.equals(this.upgradeType);
        } else {
            return false;
        }
    }
}
