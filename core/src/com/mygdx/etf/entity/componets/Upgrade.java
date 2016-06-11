package com.mygdx.etf.entity.componets;

import com.badlogic.gdx.Gdx;
import com.mygdx.etf.Main;
import com.mygdx.etf.utils.SaveMngr;

import java.util.ArrayList;
import java.util.List;

public class Upgrade extends ShopItem{

    public static boolean blowUpAllBugs;
    public UpgradeType upgradeType;
    public int counter;

    public boolean tryPeriod;
    public long tryPeriodDuration;
    public long tryPeriodStart;
    public long tryPeriodTimer;

    public Upgrade() {
    }

    public Upgrade(SaveMngr.UpgradeStats us) {
        this.upgradeType = UpgradeType.valueOf(us.upgradeType);
        this.tryPeriod = us.tryPeriod;
        this.tryPeriodDuration = us.tryPeriodDuration;
        this.tryPeriodTimer = us.tryPeriodTimer;
        this.tryPeriodStart = us.tryPeriodStart;
    }

    public static List<Upgrade> getAllUpgrades() {
        List<Upgrade> all = new ArrayList<>();
        all.add(getPhoenix());
        all.add(getBJDouble());
        return all;
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
        phoenix.transactionId = Main.phoenix_trans_ID;
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
        bjd.transactionId = Main.bj_double_trans_ID;
        return bjd;
    }

    @Override
    public void apply(FlowerPublicComponent fpc) {
        this.enabled = true;
        fpc.upgrades.put(this.upgradeType, this);
    }

    @Override
    public void disable(FlowerPublicComponent fpc) {
        this.enabled = false;
    }

    @Override
    public void buyAndUse(FlowerPublicComponent fpc) {
        this.bought = true;
        apply(fpc);
    }

    public void usePhoenix(FlowerPublicComponent fpc) {
        blowUpAllBugs = true;
        FlowerComponent.state = FlowerComponent.State.PHOENIX;
        counter++;
    }

    public String updateTryPeriodTimer() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        tryPeriodTimer = (tryPeriodStart / 1000 + tryPeriodDuration) - System.currentTimeMillis() / 1000;

        int minutes = ((int) tryPeriodTimer) / 60;
        int seconds = ((int) tryPeriodTimer) % 60;
        return "" + minutes + " : " + seconds;
    }

    public enum UpgradeType {
        PHOENIX, BJ_DOUBLE
    }
}
