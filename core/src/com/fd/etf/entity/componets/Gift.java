package com.fd.etf.entity.componets;

import com.fd.etf.stages.GameStage;
import com.fd.etf.stages.ui.PromoWindow;
import com.fd.etf.stages.ui.TrialTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.fd.etf.utils.SaveMngr.LevelInfo.*;

public class Gift {

    private static final String MONEY = "MONEY";

    private static final int ONE_HOUR = 60;

    private static Random random = new Random();

    private static List<Integer> moneySums;
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

    public static Gift getRandomGift(GameStage gameStage) {
        Gift gift = new Gift();
//        gift = getPhoenixGift(gameStage);
        int i = random.nextInt(100);
        System.out.println("radnom i is: " + i);
        if (i > 0 && i <= gameStage.gameScript.fpc.level.rewardChanceGroups.get(PET)) {
            gift = getPetGift(gameStage);
        } else if (i > gameStage.gameScript.fpc.level.rewardChanceGroups.get(PET) &&
                i <= gameStage.gameScript.fpc.level.rewardChanceGroups.get(PET_2)) {
            gift = getPet2Gift(gameStage);
        } else if (i > gameStage.gameScript.fpc.level.rewardChanceGroups.get(PET_2) &&
                i <= gameStage.gameScript.fpc.level.rewardChanceGroups.get(PET_3)) {
            gift = getPet3Gift(gameStage);
        } else if (i > gameStage.gameScript.fpc.level.rewardChanceGroups.get(PET_3) &&
                i <= gameStage.gameScript.fpc.level.rewardChanceGroups.get(PHOENIX)) {
            gift = getPhoenixGift(gameStage);
        } else if (i > gameStage.gameScript.fpc.level.rewardChanceGroups.get(PHOENIX) &&
                i <= gameStage.gameScript.fpc.level.rewardChanceGroups.get(BJ_DOUBLE)) {
            gift = getDoubleJuiceGift(gameStage);
        } else if (i > gameStage.gameScript.fpc.level.rewardChanceGroups.get(BJ_DOUBLE) &&
                i <= gameStage.gameScript.fpc.level.rewardChanceGroups.get(MONEY_50)) {
            gift = getRandomMoneyGift();
        } else if (i > gameStage.gameScript.fpc.level.rewardChanceGroups.get(MONEY_50) &&
                i <= gameStage.gameScript.fpc.level.rewardChanceGroups.get(MONEY_100)) {
            gift = getRandomMoneyGift();
        } else if (i > gameStage.gameScript.fpc.level.rewardChanceGroups.get(MONEY_100) &&
                i <= gameStage.gameScript.fpc.level.rewardChanceGroups.get(MONEY_150)) {
            gift = getRandomMoneyGift();
        } else if (i > gameStage.gameScript.fpc.level.rewardChanceGroups.get(MONEY_150) &&
                i <= gameStage.gameScript.fpc.level.rewardChanceGroups.get(MONEY_200)) {
            gift = getRandomMoneyGift();
        } else if (i > gameStage.gameScript.fpc.level.rewardChanceGroups.get(MONEY_200) &&
                i <= gameStage.gameScript.fpc.level.rewardChanceGroups.get(MONEY_250)) {
            gift = getRandomMoneyGift();
        } else if (i > gameStage.gameScript.fpc.level.rewardChanceGroups.get(MONEY_250)) {
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

    public static Gift getPetGift(GameStage gameStage) {
        Gift gift = new Gift();
        PetComponent pet = gameStage.gameScript.fpc.pets.get(0);
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

    public static Gift getPet2Gift(GameStage gameStage) {
        Gift gift = new Gift();
        PetComponent pet = gameStage.gameScript.fpc.pets.get(1);
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

    public static Gift getPet3Gift(GameStage gameStage) {
        Gift gift = new Gift();
        PetComponent pet = gameStage.gameScript.fpc.pets.get(2);
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

    public static Gift getPhoenixGift(GameStage gameStage) {
        if (!Upgrade.getPhoenix(gameStage).enabled && !Upgrade.getPhoenix(gameStage).bought) {
            Gift gift = new Gift();
            gift.upgrade = Upgrade.getPhoenix(gameStage);
            gift.upgrade.tryPeriod = true;
            gift.upgrade.tryPeriodDuration = ONE_HOUR;
            gift.type = PHOENIX;
            return gift;
        } else {
            return getRandomMoneyGift();
        }
    }

    public static Gift getDoubleJuiceGift(GameStage gameStage) {
        if (!Upgrade.getBJDouble(gameStage).enabled && !Upgrade.getBJDouble(gameStage).bought) {
            Gift gift = new Gift();
            gift.upgrade = Upgrade.getBJDouble(gameStage);
            gift.upgrade.tryPeriod = true;
            gift.upgrade.tryPeriodDuration = ONE_HOUR;
            gift.type = BJ_DOUBLE;
            return gift;
        } else {
            return getRandomMoneyGift();
        }
    }

    public void takeGift(GameStage gameStage, FlowerPublicComponent fpc) {
        PromoWindow.offerPromo = false;
        switch (type) {
            case (MONEY): {
                fpc.totalScore += money;
                System.out.println("FECK fpc.totalScore" + fpc.totalScore);
                break;
            }
            case (PET): {
                FlowerPublicComponent.currentPet = pet;
                gameStage.gameScript.changePet = true;
                FlowerPublicComponent.currentPet.tryPeriod = true;
//                fpc.currentPet.tryPeriodDuration = 1 * 60;
                FlowerPublicComponent.currentPet.tryPeriodStart = System.currentTimeMillis();
                FlowerPublicComponent.currentPet.bought = true;
                FlowerPublicComponent.currentPet.enabled = true;

                TrialTimer.trialTimerLogoName = FlowerPublicComponent.currentPet.shopIcon;
                break;
            }
            case (PHOENIX): {
                upgrade.tryPeriod = true;
//                upgrade.tryPeriodDuration = 1 * 60;
                upgrade.tryPeriodStart = System.currentTimeMillis();
                upgrade.bought = true;
                upgrade.enabled = true;
                fpc.upgrades.put(Upgrade.UpgradeType.PHOENIX, upgrade);
                TrialTimer.trialTimerLogoName = upgrade.shopIcon;
                break;
            }
            case (BJ_DOUBLE): {
                upgrade.tryPeriod = true;
//                upgrade.tryPeriodDuration = 1 * 60;
                upgrade.tryPeriodStart = System.currentTimeMillis();
                upgrade.bought = true;
                upgrade.enabled = true;
                fpc.upgrades.put(Upgrade.UpgradeType.BJ_DOUBLE, upgrade);
                TrialTimer.trialTimerLogoName = upgrade.shopIcon;
                break;
            }
        }
        fpc.level.updateLevel(gameStage.gameScript.fpc);
    }
}