package com.fd.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.fd.etf.entity.componets.listeners.ImageButtonListener;
import com.fd.etf.stages.GameStage;
import com.fd.etf.stages.ResultScreenScript;
import com.fd.etf.stages.ShopScreenScript;
import com.fd.etf.utils.EffectUtils;
import com.fd.etf.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;

import static com.fd.etf.stages.ResultScreenScript.show;
import static com.fd.etf.stages.ResultScreenScript.showCaseVanity;
import static com.fd.etf.utils.EffectUtils.fade;
import static com.fd.etf.utils.EffectUtils.fadeChildren;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;

public class Showcase {

    private static final String SHOWCASE = "showcase_lib";
    private static final String SHOWCASE_BACKGROUND = "showcase_background";
    private static final String NEXT_ICON = "next_item";
    private static final String NEXT_ICON_SHINE = "next_item_shine";
    private static final String LBL_ITEM_NAME = "lbl_item_name";
//    private static final String SPOTLIGHT = "spotLight";
    private static final String LBL_ITEM_COLLECTION = "lbl_item_collection";
    private static final String LBL_ITEM_PRICE = "lbl_item_price";
    private static final String LBL_ITEM_PRICE_S = "lbl_item_price_s";
    private static final String BTN_NO = "btn_no";
    private static final String BTN_BUY = "btn_buy";
    private static final String COIN = "coin";
    public static final String NEW_LINE_SIGN = "~";
    private final GameStage gameStage;
    public TransformComponent tcShowCase;
    private ResultScreenScript resultScreen;
    public Entity showcaseE;
    private TransformComponent tcItem;
    private int counter = 0;
    private int celebratingCounter = 0;

    private Entity itemIcon;
    private Entity nextIcon;
    private Entity nextIconShine;
//    public Entity spotLightE;
    public Entity backBtn;
    public Entity buyBtn;
    public Entity lbl_nameE;
    public Entity lbl_collE;
    public Entity lbl_priceE;
    public Entity lbl_priceEs;
    public Entity showcase_background;
    public Entity coin;

    public boolean dropItem = false;
    public boolean isActing = false;
    public boolean isCelebrating = false;

    public Showcase(GameStage gameStage, ResultScreenScript resultScreen) {
        this.gameStage = gameStage;
        this.resultScreen = resultScreen;

        loadShowcaseFromLib();
        initShowCaseBackButton();
        initShowCaseBuyButton();

        tcShowCase = showcaseE.getComponent(TransformComponent.class);
    }

    public void act(float delta) {
        if (isActing) {
            counter += 1;
            if (counter == 30 && itemIcon != null) {
                EffectUtils.playShineParticleEffect(gameStage, 1010, 290);
                ActionComponent ac3 = gameStage.sceneLoader.engine.createComponent(ActionComponent.class);
                ActionComponent ac2 = gameStage.sceneLoader.engine.createComponent(ActionComponent.class);
                ActionComponent ac = gameStage.sceneLoader.engine.createComponent(ActionComponent.class);
                Actions.checkInit();

                ac2.dataArray.add(Actions.fadeOut(0.3f));
                ac.dataArray.add(Actions.sequence(Actions.fadeIn(0.3f), Actions.delay(1),
                        Actions.parallel(Actions.moveTo(510, 327, 0.8f, Interpolation.exp5), Actions.scaleTo(1.5f, 1.5f, 0.8f, Interpolation.exp5))));
                ac3.dataArray.add(Actions.sequence(Actions.delay(1), Actions.fadeIn(0.3f),
                        Actions.parallel(Actions.moveTo(510, 327, 0.8f, Interpolation.exp5), Actions.scaleTo(2, 2, 0.8f, Interpolation.exp5), Actions.rotateBy(20000, 400)),Actions.rotateBy(20000, 400)));

                nextIcon.add(ac2);
                itemIcon.add(ac);
                nextIconShine.add(ac3);
                nextIconShine.getComponent(ZIndexComponent.class).setZIndex(itemIcon.getComponent(ZIndexComponent.class).getZIndex()-1);

                dropItem = false;
            }
            if (counter >= 120 && itemIcon != null) {
                itemIcon.getComponent(ZIndexComponent.class).setZIndex(1000);
                nextIconShine.getComponent(ZIndexComponent.class).setZIndex(itemIcon.getComponent(ZIndexComponent.class).getZIndex()-1);
                if (showcase_background.getComponent(TintComponent.class).color.a < 0.9f) {
                    showcase_background.getComponent(TintComponent.class).color.a += 0.05f;
                }
                if (itemIcon.getComponent(TransformComponent.class).y < 349
                        && backBtn.getComponent(TintComponent.class).color.a < 1
                        && !isCelebrating) {
                    backBtn.getComponent(TintComponent.class).color.a += 0.05f;
                    buyBtn.getComponent(TintComponent.class).color.a += 0.05f;
                    lbl_nameE.getComponent(TintComponent.class).color.a += 0.05f;
                    lbl_nameE.getComponent(TintComponent.class).color.a += 0.05f;
                    lbl_collE.getComponent(TintComponent.class).color.a += 0.05f;
                    lbl_priceE.getComponent(TintComponent.class).color.a += 0.05f;
                    lbl_priceEs.getComponent(TintComponent.class).color.a += 0.05f;
                    coin.getComponent(TintComponent.class).color.a += 0.05f;
                }
            }
        }

        if (isCelebrating) {
            celebratingCounter++;
            if (celebratingCounter == 31) {
                EffectUtils.playShineParticleEffect(gameStage, 603, 441);
            }
            if (celebratingCounter > 50) {
                fade(showcaseE, false);
                if (nextIconShine.getComponent(TintComponent.class).color.a > 0) {
//                    fade(showcaseE, false);
//                    backBtn.getComponent(TintComponent.class).color.a -= 0.1f;
//                    buyBtn.getComponent(TintComponent.class).color.a -= 0.1f;
//                    lbl_priceE.getComponent(TintComponent.class).color.a -= 0.1f;
//                    lbl_collE.getComponent(TintComponent.class).color.a -= 0.1f;
//                    coin.getComponent(TintComponent.class).color.a -= 0.1f;
//                    lbl_nameE.getComponent(TintComponent.class).color.a -= 0.1f;
                    nextIconShine.getComponent(TintComponent.class).color.a -= 0.1f;

                }else{
                    celebratingCounter = 0;
                    isCelebrating = false;
                    isActing = false;
                    ResultScreenScript.isWasShowcase = true;
                    showCaseVanity = null;
                    counter = 0;
                    resultScreen.initResultScreen();
                }
            }
        }
    }

    private void loadShowcaseFromLib() {
        CompositeItemVO tempItemC = gameStage.sceneLoader.loadVoFromLibrary(SHOWCASE).clone();
        showcaseE = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), tempItemC);
        gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), showcaseE, tempItemC.composite);
        gameStage.sceneLoader.getEngine().addEntity(showcaseE);
    }

    public void showFading() {

        TintComponent tcp = showcaseE.getComponent(TintComponent.class);

        boolean appear = (tcp.color.a < 1 && show) || (tcp.color.a > 0 && !show);

        int fadeCoefficient = show ? 1 : -1;

        if (appear) {
            tcp.color.a += fadeCoefficient * GlobalConstants.TENTH;
//            fadeChildren(nc, fadeCoefficient);

            if (itemIcon != null && fadeCoefficient < 0) {
                fadeChildren(itemIcon.getComponent(NodeComponent.class), fadeCoefficient);
            }

            lbl_priceE.getComponent(TintComponent.class).color.a = 0;
            lbl_priceEs.getComponent(TintComponent.class).color.a = 0;
            lbl_collE.getComponent(TintComponent.class).color.a = 0;
            coin.getComponent(TintComponent.class).color.a = 0;
            lbl_nameE.getComponent(TintComponent.class).color.a = 0;
        }
        hideWindow(tcp);
    }

    private void hideWindow(TintComponent ticParent) {
        if (!show && ticParent.color.a <= 0 && showcaseE != null) {
            if (itemIcon != null) {
                tcItem.x = FAR_FAR_AWAY_X;
                gameStage.sceneLoader.getEngine().removeEntity(itemIcon);
                itemIcon = null;
                tcItem = null;
            }
            tcShowCase.x = FAR_FAR_AWAY_X;
            nextIconShine.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        }
    }

    public void initShowCase() {
        show = true;
        coin = showcaseE.getComponent(NodeComponent.class).getChild(BTN_BUY).getComponent(NodeComponent.class).getChild(COIN);

        showcase_background = showcaseE.getComponent(NodeComponent.class).getChild(SHOWCASE_BACKGROUND);
        showcase_background.getComponent(TintComponent.class).color.a = 0;
        lbl_nameE = showcaseE.getComponent(NodeComponent.class).getChild(LBL_ITEM_NAME);
        nextIcon = resultScreen.resultScreenItem.getChild(NEXT_ICON).getEntity();
        nextIconShine = resultScreen.resultScreenItem.getChild(NEXT_ICON_SHINE).getEntity();
        nextIconShine.getComponent(TransformComponent.class).scaleX = 1.5f;
        nextIconShine.getComponent(TransformComponent.class).scaleY = 1.5f;
        showcaseE.getComponent(ZIndexComponent.class).setZIndex(200);
        LabelComponent lc = lbl_nameE.getComponent(LabelComponent.class);
        lbl_nameE.getComponent(TintComponent.class).color.a = 0;
        nextIcon.getComponent(TintComponent.class).color.a = 1;

        if(showCaseVanity.name.contains(NEW_LINE_SIGN)) {
            String name = showCaseVanity.name.replace(NEW_LINE_SIGN, " ");
            lc.text.replace(0, lc.text.capacity(), name);
        }else{
            lc.text.replace(0, lc.text.capacity(), showCaseVanity.name);
        }

//        spotLightE = showcaseE.getComponent(NodeComponent.class).getChild(SPOTLIGHT);

        lbl_collE = showcaseE.getComponent(NodeComponent.class).getChild(LBL_ITEM_COLLECTION);
        LabelComponent lcColl = lbl_collE.getComponent(LabelComponent.class);
        if (showCaseVanity.collection != null && !"".equals(showCaseVanity.collection)) {
            lcColl.text.replace(0, lcColl.text.capacity(), "In " + showCaseVanity.collection + " collection");
        } else {
            lcColl.text.replace(0, lcColl.text.capacity(), "");
        }

        lbl_priceE = showcaseE.getComponent(NodeComponent.class).getChild(BTN_BUY).getComponent(NodeComponent.class).getChild(LBL_ITEM_PRICE);
        lbl_priceEs = showcaseE.getComponent(NodeComponent.class).getChild(BTN_BUY).getComponent(NodeComponent.class).getChild(LBL_ITEM_PRICE_S);
        LabelComponent lc3 = lbl_priceE.getComponent(LabelComponent.class);
        lc3.text.replace(0, lc3.text.capacity(), Long.toString(showCaseVanity.cost));
        LabelComponent lc3s = lbl_priceEs.getComponent(LabelComponent.class);
        lc3s.text.replace(0, lc3s.text.capacity(), Long.toString(showCaseVanity.cost));
        lbl_priceE.getComponent(TintComponent.class).color.a = 0;
        lbl_priceEs.getComponent(TintComponent.class).color.a = 0;

        coin.getComponent(TintComponent.class).color.a = 0;
//        spotLightE.getComponent(TintComponent.class).color.a = 0;

        initShowCaseItem();

        tcShowCase.x = -25;
        tcShowCase.y = -35;

        isActing = true;
    }

    private void initShowCaseItem() {
        CompositeItemVO tempItemC;
        if (showCaseVanity.name == null || "".equals(showCaseVanity.name)) {
            tempItemC = gameStage.sceneLoader.loadVoFromLibrary(ShopScreenScript.ITEM_UNKNOWN_N);
        }else{
            tempItemC = gameStage.sceneLoader.loadVoFromLibrary(showCaseVanity.shopIcon);
        }
        itemIcon = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), tempItemC);
        gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), itemIcon, tempItemC.composite);
        gameStage.sceneLoader.getEngine().addEntity(itemIcon);
        itemIcon.getComponent(ZIndexComponent.class).setZIndex(showcaseE.getComponent(ZIndexComponent.class).getZIndex() + 1);
        itemIcon.getComponent(TintComponent.class).color.a = 0;

        tcItem = itemIcon.getComponent(TransformComponent.class);
        tcItem.x = 962;
        tcItem.y = 252;
        tcItem.scaleX = 0.8f;
        tcItem.scaleY = 0.8f;
    }

    private void initShowCaseBackButton() {
        backBtn = showcaseE.getComponent(NodeComponent.class).getChild(BTN_NO);
        if (backBtn.getComponent(ButtonComponent.class) == null) {
            backBtn.add(new ButtonComponent());
        }
        backBtn.getComponent(TintComponent.class).color.a = 0;
        backBtn.getComponent(ButtonComponent.class).clearListeners();
        backBtn.getComponent(ButtonComponent.class).addListener(new ImageButtonListener(backBtn) {
            @Override
            public void clicked() {
                if (btn.getComponent(TintComponent.class).color.a >= 1) {
                    isActing = false;
                    ResultScreenScript.isWasShowcase = true;
                    resultScreen.initResultScreen();
                    showCaseVanity = null;
                    counter = 0;

                    backBtn.getComponent(TintComponent.class).color.a = 0;
                    buyBtn.getComponent(TintComponent.class).color.a = 0;
                    lbl_priceE.getComponent(TintComponent.class).color.a = 0;
                    lbl_priceEs.getComponent(TintComponent.class).color.a = 0;
                    lbl_collE.getComponent(TintComponent.class).color.a = 0;
                    coin.getComponent(TintComponent.class).color.a = 0;
                    lbl_nameE.getComponent(TintComponent.class).color.a = 0;
                }
            }
        });
    }

    private void initShowCaseBuyButton() {
        buyBtn = showcaseE.getComponent(NodeComponent.class).getChild(BTN_BUY);
        buyBtn.getComponent(TintComponent.class).color.a = 0;
        if (buyBtn.getComponent(ButtonComponent.class) == null) {
            buyBtn.add(new ButtonComponent());
        } else {
            buyBtn.getComponent(ButtonComponent.class).clearListeners();
        }
        buyBtn.getComponent(ButtonComponent.class).addListener(new ImageButtonListener(buyBtn) {
            @Override
            public void clicked() {
                if (btn.getComponent(TintComponent.class).color.a > 0) {
                    showCaseVanity.buyAndUse(gameStage);
                    gameStage.changedFlowerEntity2 = true;
                    ResultScreenScript.isWasShowcase = true;
                    if (gameStage.shopScript != null) {
                        gameStage.shopScript.preview.changeBagIcon(showCaseVanity);
                    }
                    isCelebrating = true;
                }
            }
        });
    }
}
