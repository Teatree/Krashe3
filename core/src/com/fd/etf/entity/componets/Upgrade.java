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
    public GameStage gameStage;

//    public boolean tryPeriod;
//    public long tryPeriodDuration;
//    public long tryPeriodStart;
//    public long tryPeriodTimer;

    public Upgrade(GameStage gameStage) {
        this.gameStage = gameStage;
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

    public static List<Upgrade> getAllUpgrades(GameStage gameStage) {
        List<Upgrade> all = new ArrayList<>();
        all.add(getPhoenix(gameStage));
        all.add(getBJDouble(gameStage));
        return all;
    }

    public static Upgrade getUpgrade (GameStage gameStage, UpgradeType type){
        return type.equals(UpgradeType.PHOENIX) ? getPhoenix(gameStage) : getBJDouble(gameStage);
    }

    public static Upgrade getPhoenix(GameStage gameStage) {
        Upgrade phoenix = new Upgrade(gameStage);
        phoenix.upgradeType = UpgradeType.PHOENIX;
        phoenix.cost = 13;
        phoenix.name = "PHOENIX";
        phoenix.bought = false;
        phoenix.description = "You will not die! ... ";
        phoenix.enabled = false;
        phoenix.currencyType = HARD;
//        phoenix.logoName = "itemfedora";
        phoenix.shopIcon = "itemfedora";
//        phoenix.transactionId = Main.phoenix_trans_ID;
        return phoenix;
    }

    public static Upgrade getBJDouble(GameStage gameStage) {
        Upgrade bjd = new Upgrade(gameStage);
        bjd.upgradeType = UpgradeType.BJ_DOUBLE;
        bjd.cost = 13;
        bjd.name = "DOUBLE~BUG JUICE";
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
    public void apply(GameStage gameStage) {
        this.enabled = true;
        gameStage.gameScript.fpc.upgrades.put(this.upgradeType, this);
    }

    @Override
    public void disable(GameStage gameStage) {
        this.enabled = false;
    }

    @Override
    public void buyAndUse(GameStage gameStage) {
        this.bought = true;
        apply(gameStage);
    }

    @Override
    public void buyHard(GameStage gameStage) {
        this.bought = true;
        this.enabled = true;

        if (upgradeType.equals(UpgradeType.PHOENIX)){
            Main.mainController.getPhoenix(gameStage, this);
        }

        if (upgradeType.equals(UpgradeType.BJ_DOUBLE)){
            Main.mainController.getBJDouble(gameStage, this);
        }
    }

    @Override
    public void buyHardDiscount(GameStage gameStage) {
        if (upgradeType.equals(UpgradeType.PHOENIX)){
            Main.mainController.getPhoenixDiscount(gameStage, this);
        }

        if (upgradeType.equals(UpgradeType.BJ_DOUBLE)){
            Main.mainController.getBJDoubleDiscount(gameStage, this);
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
