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
//    public GameStage gameStage;

//    public Upgrade(){}
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
        this.shopIcon = us.shopIcon;
        this.name = us.name;
        this.cost = us.cost;
        this.disc = us.disc;
        this.costDisc = us.costDisc;
        this.description = us.description;
        this.collection = us.collection;
        this.currencyType = us.currencyType;
    }

    public static List<Upgrade> getAllUpgrades(GameStage gameStage) {
        List<Upgrade> all = new ArrayList<>();
        all.add(getPhoenix());
        all.add(getBJDouble());
        return all;
    }

    public static Upgrade getPhoenix() {
        Upgrade phoenix = new Upgrade();
        phoenix.upgradeType = UpgradeType.PHOENIX;
        phoenix.cost = 199;
        phoenix.costDisc = 100;
        phoenix.disc = 0;
        phoenix.name = "EXTRA~LIFE";
        phoenix.bought = false;
        phoenix.description = "Get 1 extra life~every new game";
        phoenix.enabled = false;
        phoenix.currencyType = HARD;
        phoenix.sku_discount = "phoenix_discount";
        phoenix.sku = "phoenix_____2";
        phoenix.shopIcon = "itemphoenixUPGRADE";
        return phoenix;
    }

    public static Upgrade getBJDouble() {
        Upgrade bjd = new Upgrade();
        bjd.upgradeType = UpgradeType.BJ_DOUBLE;
        bjd.cost = 349;
        bjd.costDisc = 175;
        bjd.disc = 0;
        bjd.name = "DOUBLE~COINS";
        bjd.bought = false;
        bjd.description = "Twice the coins~from eating bugs";
        bjd.enabled = false;
        bjd.currencyType = HARD;
        bjd.sku = "bj_upgrade";
        bjd.sku_discount = "bj_upgrade_discount";
        bjd.shopIcon = "itemdoubleUPGRDE";
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
        if(Main.mainController.isWifiConnected()) {
            if (upgradeType.equals(UpgradeType.PHOENIX)) {
                Main.mainController.getPhoenix(gameStage, this);
            }

            if (upgradeType.equals(UpgradeType.BJ_DOUBLE)) {
                Main.mainController.getBJDouble(gameStage, this);
            }
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

    public boolean hasPet() {
        return false;
    }

    public void usePhoenix() {
//        BugSystem.blowUpAllBugs = true;
        BugSystem.blowUpAllBugs();
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
