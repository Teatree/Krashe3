package com.fd.etf.entity.componets;

import com.fd.etf.stages.GameStage;
import com.fd.etf.stages.ui.PromoWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.fd.etf.stages.GameStage.gameScript;
import static com.fd.etf.utils.SaveMngr.LevelInfo.*;

public class Gift {

    public static final String MONEY = "MONEY";

    public static final int ONE_HOUR = 3600000;

    private static Random random = new Random();

    static List<Integer> moneySums;
    static {
        moneySums = new ArrayList<>();
        moneySums.add(50);
        moneySums.add(100);
        moneySums.add(150);
        moneySums.add(200);
        moneySums.add(250);
        moneySums.add(300);
    }
    public PetComponent pet;
    public int money;
    public String type;
    public Upgrade upgrade;

    public static Gift getRandomGift() {
        Gift gift = new Gift();
//            gift = getRandomMoneyGift();
        int i = random.nextInt(100);
        if (i > 0 && i <= gameScript.fpc.level.rewardChanceGroups.get(PET)) {
            gift = getPetGift();
        } else if (i > gameScript.fpc.level.rewardChanceGroups.get(PET) &&
                i <= gameScript.fpc.level.rewardChanceGroups.get(PET_2)) {
            gift = getPet2Gift();
        } else if (i > gameScript.fpc.level.rewardChanceGroups.get(PET_2) &&
                i <= gameScript.fpc.level.rewardChanceGroups.get(PET_3)) {
            gift = getPet3Gift();
        } else if (i > gameScript.fpc.level.rewardChanceGroups.get(PET_3) &&
                i <= gameScript.fpc.level.rewardChanceGroups.get(PHOENIX)) {
            gift = getPhoenixGift();
        } else if (i > gameScript.fpc.level.rewardChanceGroups.get(PHOENIX) &&
                i <= gameScript.fpc.level.rewardChanceGroups.get(BJ_DOUBLE)) {
            gift = getDoubleJuiceGift();
        } else if (i > gameScript.fpc.level.rewardChanceGroups.get(BJ_DOUBLE) &&
                i <= gameScript.fpc.level.rewardChanceGroups.get(MONEY_50)) {
            gift = getRandomMoneyGift();
        } else if (i > gameScript.fpc.level.rewardChanceGroups.get(MONEY_50) &&
                i <= gameScript.fpc.level.rewardChanceGroups.get(MONEY_100)) {
            gift = getRandomMoneyGift();
        } else if (i > gameScript.fpc.level.rewardChanceGroups.get(MONEY_100) &&
                i <= gameScript.fpc.level.rewardChanceGroups.get(MONEY_150)) {
            gift = getRandomMoneyGift();
        } else if (i > gameScript.fpc.level.rewardChanceGroups.get(MONEY_150) &&
                i <= gameScript.fpc.level.rewardChanceGroups.get(MONEY_200)) {
            gift = getRandomMoneyGift();
        } else if (i > gameScript.fpc.level.rewardChanceGroups.get(MONEY_200) &&
                i <= gameScript.fpc.level.rewardChanceGroups.get(MONEY_250)) {
            gift = getRandomMoneyGift();
        } else if (i > gameScript.fpc.level.rewardChanceGroups.get(MONEY_250)) {
            gift = getRandomMoneyGift();
        }
        return gift;
    }

    public static Gift getRandomMoneyGift() {
        Gift gift = new Gift();
        int moneyIndex = random.nextInt(moneySums.size());
        gift.money = moneySums.get(moneyIndex);
        gift.type = MONEY;
        return gift;
    }

    public static Gift getPetGift() {
        Gift gift = new Gift();
        PetComponent pet = GameStage.gameScript.fpc.pets.get(0);
        if (!pet.bought) {
            gift.pet = pet;
            gift.pet.tryPeriod = true;
            gift.pet.tryPeriodDuration = ONE_HOUR;
            gift.type = PET;
            return gift;
        } else {
            return getRandomMoneyGift();
        }
    }

    public static Gift getPet2Gift() {
        Gift gift = new Gift();
        PetComponent pet = GameStage.gameScript.fpc.pets.get(1);
        if (!pet.bought) {
            gift.pet = pet;
            gift.pet.tryPeriod = true;
            gift.pet.tryPeriodDuration = ONE_HOUR;
            gift.type = PET;
            return gift;
        } else {
            return getRandomMoneyGift();
        }
    }

    public static Gift getPet3Gift() {
        Gift gift = new Gift();
        PetComponent pet = GameStage.gameScript.fpc.pets.get(2);
        if (!pet.bought) {
            gift.pet = pet;
            gift.pet.tryPeriod = true;
            gift.pet.tryPeriodDuration = ONE_HOUR;
            gift.type = PET;
            return gift;
        } else {
            return getRandomMoneyGift();
        }
    }

    public static Gift getPhoenixGift() {
        if (!Upgrade.getPhoenix().enabled && !Upgrade.getPhoenix().bought) {
            Gift gift = new Gift();
            gift.upgrade = Upgrade.getPhoenix();
            gift.upgrade.tryPeriod = true;
            gift.upgrade.tryPeriodDuration = ONE_HOUR;
            gift.type = PHOENIX;
            return gift;
        } else {
            return getRandomMoneyGift();
        }
    }

    public static Gift getDoubleJuiceGift() {
        if (!Upgrade.getBJDouble().enabled && !Upgrade.getBJDouble().bought) {
            Gift gift = new Gift();
            gift.upgrade = Upgrade.getBJDouble();
            gift.upgrade.tryPeriod = true;
            gift.upgrade.tryPeriodDuration = ONE_HOUR;
            gift.type = BJ_DOUBLE;
            return gift;
        } else {
            return getRandomMoneyGift();
        }
    }

    public void takeGift(FlowerPublicComponent fpc) {
        PromoWindow.offerPromo = false;
        switch (type) {
            case (MONEY): {
                fpc.totalScore += money;
                break;
            }
            case (PET): {
                fpc.currentPet = pet;
                fpc.currentPet.tryPeriod = true;
                fpc.currentPet.tryPeriodDuration = 1 * 60;
                fpc.currentPet.tryPeriodStart = System.currentTimeMillis();
                fpc.currentPet.bought = true;
                fpc.currentPet.enabled = true;
                break;
            }
            case (PHOENIX): {
                upgrade.tryPeriod = true;
                upgrade.tryPeriodDuration = 1 * 60;
                upgrade.tryPeriodStart = System.currentTimeMillis();
                upgrade.bought = true;
                upgrade.enabled = true;
                fpc.upgrades.put(Upgrade.UpgradeType.PHOENIX, upgrade);
                break;
            }
            case (BJ_DOUBLE): {
                upgrade.tryPeriod = true;
                upgrade.tryPeriodDuration = 1 * 60;
                upgrade.tryPeriodStart = System.currentTimeMillis();
                upgrade.bought = true;
                upgrade.enabled = true;
                fpc.upgrades.put(Upgrade.UpgradeType.BJ_DOUBLE, upgrade);
                break;
            }
        }
        fpc.level.updateLevel(GameStage.gameScript.fpc);
    }
}