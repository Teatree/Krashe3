package com.mygdx.game.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.entity.componets.PetComponent;
import com.mygdx.game.entity.componets.Upgrade;
import com.mygdx.game.stages.GameScreenScript;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.Random;

import static com.mygdx.game.stages.GameScreenScript.*;
import static com.mygdx.game.utils.EffectUtils.fade;
import static com.mygdx.game.utils.EffectUtils.playYellowStarsParticleEffect;
import static com.mygdx.game.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.mygdx.game.utils.GlobalConstants.FAR_FAR_AWAY_Y;

public class GiftScreen {
    public static final String GIFT_SCREEN = "gift_screen";
    public static final String BTN_PINATA = "btn_pinata";
    public static final int GIFT_SCREEN_X = -20;
    public static final int GIFT_SCREEN_Y = -20;
    public static final int ONE_HOUR = 3600000;
    public static final String LBL_GIFT_SCREEN = "lbl_gift_screen";
    public static final int PINATA_X = 363;
    public static final int PINATA_Y = 275;
    public static final int HIT_COUNTER_MAX = 10;
    public static final int HIT_COUNTER_MIN = 5;

    private ItemWrapper gameItem;
    private Entity giftScreen;
    private Entity pinataBtn;

    private int pinataHitCounter;
    private Gift gift;

    public GiftScreen(ItemWrapper gameItem) {
        this.gameItem = gameItem;
    }

    public void init() {
        giftScreen = gameItem.getChild(GIFT_SCREEN).getEntity();

        giftScreen.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        giftScreen.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;

        pinataBtn = gameItem.getChild(GIFT_SCREEN).getChild(BTN_PINATA).getEntity();
        pinataBtn.getComponent(TransformComponent.class).scaleX = 1.4f;
        pinataBtn.getComponent(TransformComponent.class).scaleY = 1.4f;
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
                pinataHitCounter--;
                playYellowStarsParticleEffect(Gdx.input.getX(), Gdx.input.getY());
            }
        });

        pinataHitCounter = new Random().nextInt(HIT_COUNTER_MAX) + HIT_COUNTER_MIN;
    }

    public void show() {

        gift = Gift.getRandomMoneyGift();

        final TransformComponent screenTc = giftScreen.getComponent(TransformComponent.class);
        screenTc.x = GIFT_SCREEN_X;
        screenTc.y = GIFT_SCREEN_Y;

        final TransformComponent pinataTc = pinataBtn.getComponent(TransformComponent.class);
        pinataTc.x = PINATA_X;
        pinataTc.y = PINATA_Y;
    }

    public void update() {
        if (pinataHitCounter <= 0) {
            pinataBtn.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            gift.takeGift(fpc);
            Entity lbl = gameItem.getChild(GIFT_SCREEN).getChild(LBL_GIFT_SCREEN).getEntity();
            lbl.getComponent(LabelComponent.class).text.replace(0,
                    lbl.getComponent(LabelComponent.class).text.capacity(),
                    "YOU GOT " + gift.money + " " + gift.type + " !!!");
            if (Gdx.input.justTouched() && isGameOver) {
                isGameOver = false;
                game.initResult();
            }
        }
        fade(giftScreen, true);
    }

    public static class Gift {

        public static final String MONEY = "MONEY";
        public static final String PET = "PET";
        public static final String PHOENIX = "PHOENIX";
        public static final String BJ_DOUBLE = "BJ_DOUBLE";
        private static Random random = new Random();
        public PetComponent pet;
        public int money;
        private String type;
        private Upgrade upgrade;

        public static Gift getRandomMoneyGift() {
            Gift gift = new Gift();
            gift.money = random.nextInt(50) + 100;
            gift.type = MONEY;
            return gift;
        }

        public static Gift getPetGift() {
            Gift gift = new Gift();
            gift.pet = GameScreenScript.fpc.pets.get(0);
            gift.pet.tryPeriod = true;
            gift.pet.tryPeriodDuration = ONE_HOUR;
            gift.type = PET;
            return gift;
        }

        public static Gift getPhoenixGift() {
            Gift gift = new Gift();
            gift.pet = GameScreenScript.fpc.pets.get(0);
            gift.pet.tryPeriod = true;
            gift.pet.tryPeriodDuration = ONE_HOUR;
            gift.type = PHOENIX;
            gift.upgrade = Upgrade.getPhoenix();
            return gift;
        }

        public static Gift getDoubleJuiceGift() {
            Gift gift = new Gift();
            gift.pet = GameScreenScript.fpc.pets.get(0);
            gift.pet.tryPeriod = true;
            gift.pet.tryPeriodDuration = ONE_HOUR;
            gift.type = BJ_DOUBLE;
            gift.upgrade = Upgrade.getBJDouble();
            return gift;
        }

        public void takeGift(FlowerPublicComponent fpc) {
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
        }


    }
}
