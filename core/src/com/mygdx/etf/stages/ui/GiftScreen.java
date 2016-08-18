package com.mygdx.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.mygdx.etf.entity.componets.FlowerPublicComponent;
import com.mygdx.etf.entity.componets.PetComponent;
import com.mygdx.etf.entity.componets.Upgrade;
import com.mygdx.etf.stages.GameStage;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.mygdx.etf.stages.GameScreenScript.isGameOver;
import static com.mygdx.etf.stages.GameStage.GAME;
import static com.mygdx.etf.stages.GameStage.sceneLoader;
import static com.mygdx.etf.stages.GameStage.gameScript;
import static com.mygdx.etf.utils.EffectUtils.fade;
import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_Y;

public class GiftScreen {
    public final String GIFT_SCREEN = "lib_gift_screen";
    public final String BTN_PINATA = "btn_pinata";
    public final int GIFT_SCREEN_X = -20;
    public final int GIFT_SCREEN_Y = -20;

    public final String LBL_GIFT_SCREEN = "lbl_gift_screen";
    public final int PINATA_X = 363;
    public final int PINATA_Y = 275;
    public final int HIT_COUNTER_MAX = 10;
    public final int HIT_COUNTER_MIN = 5;
    public boolean isGiftScreenOpen = false;

    private Entity giftScreen;
    private Entity pinataBtn;
    private Gift gift;

    private int openGiftCooldown = 30;
    private boolean showNewLevelAnim;
    private boolean openedGift;

    static List<Integer> moneySums;

    public GiftScreen() {
        /*this.gameItem = gameItem;*/
        moneySums = new ArrayList<>();
        moneySums.add(50);
        moneySums.add(100);
        moneySums.add(150);
        moneySums.add(200);
        moneySums.add(250);
        moneySums.add(300);
    }

    public void init() {
//        giftScreen = gameItem.getChild(GIFT_SCREEN).getEntity();
        openedGift = false;
        openGiftCooldown = 30;

        final CompositeItemVO tempC = sceneLoader.loadVoFromLibrary(GIFT_SCREEN);
        giftScreen = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), giftScreen, tempC.composite);
        sceneLoader.getEngine().addEntity(giftScreen);

        giftScreen.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        giftScreen.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;

        pinataBtn = new ItemWrapper(giftScreen).getChild(BTN_PINATA).getEntity();
        pinataBtn.getComponent(TransformComponent.class).scaleX = 1.4f;
        pinataBtn.getComponent(TransformComponent.class).scaleY = 1.4f;
        pinataBtn.add(new ButtonComponent());
        pinataBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            float previousX;
            float previousY;

            @Override
            public void touchUp() {
                ActionComponent ac = new ActionComponent();
                Actions.checkInit();
                ac.dataArray.add(Actions.moveTo(previousX, previousY, 0.05f, Interpolation.bounce));
                pinataBtn.add(ac);
            }

            @Override
            public void touchDown() {
                previousX = pinataBtn.getComponent(TransformComponent.class).x;
                previousY = pinataBtn.getComponent(TransformComponent.class).y;

                ActionComponent ac = new ActionComponent();
                Actions.checkInit();
                ac.dataArray.add(Actions.moveTo(previousX + 10, previousY + 20, 0.05f, Interpolation.bounce));
                pinataBtn.add(ac);
            }

            @Override
            public void clicked() {
                openedGift = true;
                openGiftCooldown = 30;
            }
        });
    }

    public void show() {
        gift = Gift.getRandomGift();

        final TransformComponent screenTc = giftScreen.getComponent(TransformComponent.class);
        screenTc.x = GIFT_SCREEN_X;
        screenTc.y = GIFT_SCREEN_Y;

        final TransformComponent pinataTc = pinataBtn.getComponent(TransformComponent.class);
        pinataTc.x = PINATA_X;
        pinataTc.y = PINATA_Y;

        isGiftScreenOpen = true;
        openedGift = false;
    }

    public void hide() {
        giftScreen.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        giftScreen.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
    }

    public void update() {
        if (Gdx.input.justTouched() && isGameOver && showNewLevelAnim) {
            showNewLevelScreen();
            showNewLevelAnim = false;
        }
        if (openedGift && openGiftCooldown == 0) {
            showGift();
            openedGift = false;
            showNewLevelAnim = true;
        } else if (openGiftCooldown > 0) {
            openGiftCooldown--;
        }

        fade(giftScreen, true);
    }

    private void showNewLevelScreen() {
        gift.takeGift(GameStage.gameScript.fpc);
        giftScreen.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        GameStage.gameScript.goalFeedbackScreen.init(true);
        GameStage.gameScript.goalFeedbackScreen.showNewLevel();
    }

    private void showGift() {
        pinataBtn.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        Entity lbl = new ItemWrapper(giftScreen).getChild(LBL_GIFT_SCREEN).getEntity();

        lbl.getComponent(LabelComponent.class).text.replace(0,
                lbl.getComponent(LabelComponent.class).text.capacity(),
                "YOU GOT " + gift.money + " " + gift.type + " !!!");
    }

    public static class Gift {

        public static final String MONEY = "MONEY";
        public static final String MONEY_50 = "MONEY_50";
        public static final String MONEY_100 = "MONEY_100";
        public static final String MONEY_150 = "MONEY_150";
        public static final String MONEY_200 = "MONEY_200";
        public static final String MONEY_250 = "MONEY_250";
        public static final String MONEY_300 = "MONEY_300";

        public static final String PET = "PET";
        public static final String PET_2 = "PET2";
        public static final String PET_3 = "PET3";

        public static final String PHOENIX = "PHOENIX";
        public static final String BJ_DOUBLE = "BJ_DOUBLE";

        public static final int ONE_HOUR = 3600000;

        private static Random random = new Random();

        public PetComponent pet;
        public int money;
        private String type;
        private Upgrade upgrade;

        public static Gift getRandomGift() {
            Gift gift = new Gift();
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
            }else if (i > gameScript.fpc.level.rewardChanceGroups.get(MONEY_50) &&
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
            }else if (i > gameScript.fpc.level.rewardChanceGroups.get(MONEY_250)) {
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

        public static Gift getPet3Gift() {
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
}
