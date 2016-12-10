package com.fd.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Interpolation;
import com.fd.etf.entity.componets.Gift;
import com.fd.etf.stages.GameStage;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.Random;

import static com.fd.etf.stages.GameScreenScript.isGameOver;
import static com.fd.etf.stages.GameStage.gameScript;
import static com.fd.etf.stages.GameStage.sceneLoader;
import static com.fd.etf.utils.EffectUtils.fade;
import static com.fd.etf.utils.GlobalConstants.*;

public class GiftScreen extends AbstractDialog {
    public final String GIFT_SCREEN = "lib_gift_screen";
    public final String ITEM_MONEY_GIFT = "itemMoneyGift";
    public final String SHADE = "shade";
    public final String BTN_PINATA = "btn_gift";
    public final String BOX_ANI = "box_ani";
    public final int GIFT_SCREEN_X = -20;
    public final int GIFT_SCREEN_Y = -20;

    public final String LBL_GIFT_SCREEN = "lbl_gift_screen";
    public boolean isGiftScreenOpen = false;
    public boolean playGiftAni = false;

    private Entity giftScreen;
    private Entity boxAniE;
    private Entity giftE;
    private Entity lbl;
    private SpriteAnimationComponent saBox;
    private SpriteAnimationStateComponent sasBox;
    private Gift gift;

    private int helpTimer = 0;
    private float idleCounter = 0;
    private boolean openedGift;
    private boolean canPlayAnimation;
    private boolean canOpenGift;

    public GiftScreen() {
    }

    public void init() {
        openedGift = false;
        initShadow();

        idleCounter = 0;

        final CompositeItemVO tempC = sceneLoader.loadVoFromLibrary(GIFT_SCREEN);
        giftScreen = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), giftScreen, tempC.composite);
        sceneLoader.getEngine().addEntity(giftScreen);

        lbl = new ItemWrapper(giftScreen).getChild(LBL_GIFT_SCREEN).getEntity();
        lbl.getComponent(TintComponent.class).color.a = 0;

        giftScreen.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        giftScreen.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;

        boxAniE = new ItemWrapper(giftScreen).getChild(BOX_ANI).getEntity();
        boxAniE.getComponent(ZIndexComponent.class).setZIndex(220);
        saBox = boxAniE.getComponent(SpriteAnimationComponent.class);
        sasBox = boxAniE.getComponent(SpriteAnimationStateComponent.class);
    }

    private void openGift() {
        if (!openedGift) {
            ActionComponent ac = new ActionComponent();
            Actions.checkInit();
            ac.dataArray.add(Actions.sequence(
                    Actions.delay(3),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            canPlayAnimation = true;
                            showGift();
                            openedGift = false;
                            idleCounter = -2;
                            setAnimation("open", Animation.PlayMode.NORMAL, sasBox, saBox);
                        }
                    })
            ));
            boxAniE.add(ac);
        }
    }

    public void show() {
        gift = Gift.getRandomGift();

        final TransformComponent screenTc = giftScreen.getComponent(TransformComponent.class);
        screenTc.x = FAR_FAR_AWAY_X;
        screenTc.y = FAR_FAR_AWAY_X;
        canOpenGift = false;
        boxAniE.getComponent(TintComponent.class).color.a = 0;

        ActionComponent ac = new ActionComponent();
        Actions.checkInit();
        ac.dataArray.add(Actions.sequence(
                Actions.delay(8f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        canPlayAnimation = true;
                        screenTc.x = GIFT_SCREEN_X;
                        screenTc.y = GIFT_SCREEN_Y;
                        setAnimation("idle", Animation.PlayMode.NORMAL, sasBox, saBox);
                    }
                }),
                Actions.fadeIn(0.3f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        canOpenGift = true;
                    }
                })
        ));
        boxAniE.add(ac);

        isGiftScreenOpen = true;
        openedGift = false;

        giftScreen.getComponent(ZIndexComponent.class).setZIndex(320);
    }

    public void hide() {
        giftScreen.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        giftScreen.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
    }

    public void update() {
        if (!openedGift && idleCounter < 0 && idleCounter > -1) {
            canPlayAnimation = true;
            setAnimation("idle", Animation.PlayMode.NORMAL, sasBox, saBox);
            idleCounter = 6 + new Random().nextInt(12);
        } else {
            idleCounter -= Gdx.graphics.getDeltaTime();
        }

        if (Gdx.input.isTouched() && isGameOver.get() && canOpenGift) {
            openGift();
        }

        helpTimer++;
        if (helpTimer > 200) {
            if (lbl.getComponent(TintComponent.class).color.a < 1) {
                lbl.getComponent(TintComponent.class).color.a += Gdx.graphics.getDeltaTime();
            }
        }

        if (playGiftAni && giftE != null) {
            ActionComponent ac = new ActionComponent();
            Actions.checkInit();
            ac.dataArray.add(Actions.moveTo(535, 439, 2f, Interpolation.exp5));
            giftE.add(ac);
            if (giftScreen.getComponent(NodeComponent.class).getChild(SHADE).getComponent(TintComponent.class).color.a < 0.7) {
                giftScreen.getComponent(NodeComponent.class).getChild(SHADE).getComponent(TintComponent.class).color.a += 0.1f;
            }
            if (giftE.getComponent(TransformComponent.class).x < 530) {
                playGiftAni = false;
            }
        }
        if (!playGiftAni && giftE != null && Gdx.input.justTouched()) {
            close(giftScreen);
            hideShadow();
            hide();
            isGiftScreenOpen = false;
            isGiftScreenOpen = false;
            giftE.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            GoalFeedbackScreen.hideGoalFeedback();
            gameScript.stage.initResultWithAds();
        }

        fade(giftScreen, true);
    }

    private void showGift() {
        if (!openedGift) {
            addShadow();
            openedGift = true;
            playGiftAni = true;
            lbl.getComponent(TintComponent.class).color.a = 1;

            if (gift.pet != null || gift.upgrade != null) {
                lbl.getComponent(LabelComponent.class).text.replace(0,
                        lbl.getComponent(LabelComponent.class).text.capacity(),
                        "YOU GOT A " + gift.type + " !!!");
            } else {
                lbl.getComponent(LabelComponent.class).text.replace(0,
                        lbl.getComponent(LabelComponent.class).text.capacity(),
                        "YOU GOT " + gift.money + " " + gift.type + " !!!");
            }
            showGiftIcon();
        }
    }

    public void setAnimation(String animationName, Animation.PlayMode mode, SpriteAnimationStateComponent sasComponent, SpriteAnimationComponent saComponent) {
        if (canPlayAnimation) {
            sasComponent.set(saComponent.frameRangeMap.get(animationName), FPS, mode);
            canPlayAnimation = false;
        }
    }

    private void showGiftIcon() {
        if (gift.pet != null) {
            CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary(gift.pet.shopIcon);
            giftE = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
            GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), giftE, tempItemC.composite);
            GameStage.sceneLoader.getEngine().addEntity(giftE);
            giftE.getComponent(ZIndexComponent.class).setZIndex(200);
            giftE.getComponent(TransformComponent.class).x = 200;
            giftE.getComponent(TransformComponent.class).y = 329;
        } else if (gift.upgrade != null) {
            CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary(gift.upgrade.shopIcon);
            giftE = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
            GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), giftE, tempItemC.composite);
            GameStage.sceneLoader.getEngine().addEntity(giftE);
            giftE.getComponent(ZIndexComponent.class).setZIndex(100);
            giftE.getComponent(TransformComponent.class).x = 100;
            giftE.getComponent(TransformComponent.class).y = 329;
        } else {
            CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary(ITEM_MONEY_GIFT);
            giftE = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
            GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), giftE, tempItemC.composite);
            GameStage.sceneLoader.getEngine().addEntity(giftE);
            giftE.getComponent(ZIndexComponent.class).setZIndex(200);
            giftE.getComponent(TransformComponent.class).x = 100;
            giftE.getComponent(TransformComponent.class).y = 329;
        }

        playGiftAni = true;
    }

}
