package com.mygdx.game.entity.componets;

import java.util.ArrayList;
import java.util.List;

public class Upgrade extends ShopItem{

    public UpgradeType upgradeType;

    @Override
    public void apply(FlowerPublicComponent fpc) {
        this.enabled = true;
        if (upgradeType.equals(UpgradeType.DOUBLE_JUICE)){
            fpc.doubleJuice = true;
        }
        if (upgradeType.equals(UpgradeType.PHOENIX)){
            fpc.phoenix = true;
        }
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

    public enum UpgradeType {
        PHOENIX, DOUBLE_JUICE
    }

    public static List<Upgrade> getAllUpgrades (){
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
        phoenix.description = "You will not die! ... once...";
        phoenix.enabled = false;
        phoenix.currencyType = CurrencyType.HARD;
        return phoenix;
    }

    public static Upgrade getBJDouble() {
        Upgrade bjd = new Upgrade();
        bjd.upgradeType = UpgradeType.DOUBLE_JUICE;
        bjd.cost = 13;
        bjd.name = "bj_double";
        bjd.bought = false;
        bjd.description = "more juice \\0/";
        bjd.enabled = false;
        bjd.currencyType = CurrencyType.HARD;
        return bjd;
    }
}
