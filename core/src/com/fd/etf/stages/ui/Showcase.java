package com.fd.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.fd.etf.entity.componets.listeners.ImageButtonListener;
import com.fd.etf.stages.GameStage;
import com.fd.etf.stages.ResultScreenScript;
import com.fd.etf.utils.EffectUtils;
import com.fd.etf.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.fd.etf.stages.GameStage.sceneLoader;
import static com.fd.etf.stages.ResultScreenScript.show;
import static com.fd.etf.stages.ResultScreenScript.showCaseVanity;
import static com.fd.etf.utils.EffectUtils.fadeChildren;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;

public class Showcase {

    public static final String PATH_PREFIX = "orig\\spriter_animations\\showcase_present_ani\\";
    public static final String TYPE_SUFFIX = ".png";
    public static final String ITEM_UNKNOWN_DEFAULT = "item_unknown";
    public static final String INTRO = "intro";
    public static final String SHOWCASE = "showcase_lib";
    public static final String LBL_ITEM_NAME = "lbl_item_name";
    public static final String LBL_ITEM_DESC = "lbl_item_desc2";
    public static final String SPOTLIGHT = "spotLight";
    public static final String LBL_ITEM_COLLECTION = "lbl_item_collection";
    public static final String LBL_ITEM_PRICE = "lbl_item_price";
    public static final String SHOWCASE_ANI = "showcase_ani";
    public static final String BTN_NO = "btn_no";
    public static final String BTN_BUY = "btn_buy";
    public static final String COIN = "coin";
    public TransformComponent tcShowCase;
    private ItemWrapper screenItem;
    private ResultScreenScript resultScreen;
    public Entity showcaseE;
    private TransformComponent tcItem;
    private int counter = 0;
    private int celebratingCounter = 0;

    private Entity itemIcon;
    public Entity spotLightE;
    public Entity backBtn;
    public Entity buyBtn;
    public Entity lbl_nameE;
    public Entity lbl_descE;
    public Entity lbl_collE;
    public Entity lbl_priceE;
    public Entity coin;

    public boolean dropItem = false;
    public boolean isActing = false;
    public boolean isCelebrating = false;

    public Showcase(ItemWrapper resultScreenItem, ResultScreenScript resultScreen) {
        this.screenItem = resultScreenItem;
        this.resultScreen = resultScreen;

        loadShowcaseFromLib();
        initShowCaseBackButton();
        initShowCaseBuyButton();

        tcShowCase = showcaseE.getComponent(TransformComponent.class);
    }

    public void act(float delta) {
        if (isActing) {
            //TODO: NPE when item is bought

//            System.out.println("ACT lbl_descE.getComponent(TintComponent.class).color.a: " + lbl_descE.getComponent(TintComponent.class).color.a);
//            System.out.println("ACT lbl_collE.getComponent(TintComponent.class).color.a: " + lbl_collE.getComponent(TintComponent.class).color.a);
//            System.out.println("SHOWCASE ACTING!" + " counter: " + counter);
            counter += 1;
            if (counter >= 70 && spotLightE != null && itemIcon != null) {
                itemIcon.getComponent(ZIndexComponent.class).setZIndex(1000);
                if (spotLightE.getComponent(TintComponent.class).color.a < 1) {
                    spotLightE.getComponent(TintComponent.class).color.a += 0.05f;
                }
                if (counter == 150) {
                    ActionComponent ac = new ActionComponent();
                    Actions.checkInit();
                    ac.dataArray.add(Actions.moveTo(570, 347, 0.8f));
                    itemIcon.add(ac);
                    dropItem = false;
                }
                if (itemIcon.getComponent(TransformComponent.class).y < 349 && backBtn.getComponent(TintComponent.class).color.a < 1) {
                    backBtn.getComponent(TintComponent.class).color.a += 0.05f;
                    buyBtn.getComponent(TintComponent.class).color.a += 0.05f;
                    lbl_nameE.getComponent(TintComponent.class).color.a += 0.05f;
                    lbl_descE.getComponent(TintComponent.class).color.a += 0.05f;
                    lbl_collE.getComponent(TintComponent.class).color.a += 0.05f;
                    lbl_priceE.getComponent(TintComponent.class).color.a += 0.05f;
                    coin.getComponent(TintComponent.class).color.a += 0.05f;
                }
            }
        }

        if (isCelebrating) {
//            System.out.println("celebrating");
            celebratingCounter++;
//            System.out.println("celebratiion counter: " + celebratingCounter);
            if (celebratingCounter == 31) {
                EffectUtils.playYellowStarsParticleEffect(300, 300);
            }
            if (celebratingCounter > 100) {
                celebratingCounter = 0;
                isCelebrating = false;
                isActing = false;
                ResultScreenScript.isWasShowcase = true;
                resultScreen.initResultScreen();
                showCaseVanity = null;
                counter = 0;

                if (lbl_priceE != null) {
                    backBtn.getComponent(TintComponent.class).color.a = 0;
                    buyBtn.getComponent(TintComponent.class).color.a = 0;
                    lbl_priceE.getComponent(TintComponent.class).color.a = 0;
                    lbl_collE.getComponent(TintComponent.class).color.a = 0;
                    coin.getComponent(TintComponent.class).color.a = 0;
                    lbl_descE.getComponent(TintComponent.class).color.a = 0;
                    spotLightE.getComponent(TintComponent.class).color.a = 0;
                    lbl_nameE.getComponent(TintComponent.class).color.a = 0;
                }
            }
        }
    }

    private void loadShowcaseFromLib() {
        CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary(SHOWCASE).clone();
        showcaseE = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
        GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), showcaseE, tempItemC.composite);
        GameStage.sceneLoader.getEngine().addEntity(showcaseE);
    }

    public void showFading() {
        NodeComponent nc = showcaseE.getComponent(NodeComponent.class);
        TintComponent tcp = showcaseE.getComponent(TintComponent.class);

        boolean appear = (tcp.color.a < 1 && show) || (tcp.color.a > 0 && !show);

        int fadeCoefficient = show ? 1 : -1;

        if (appear) {
            tcp.color.a += fadeCoefficient * GlobalConstants.TENTH;
            fadeChildren(nc, fadeCoefficient);
            if (itemIcon != null && fadeCoefficient < 0)
                fadeChildren(itemIcon.getComponent(NodeComponent.class), fadeCoefficient);
            if (lbl_descE != null) {
                lbl_priceE.getComponent(TintComponent.class).color.a = 0;
                lbl_collE.getComponent(TintComponent.class).color.a = 0;
                coin.getComponent(TintComponent.class).color.a = 0;
                lbl_descE.getComponent(TintComponent.class).color.a = 0;
                spotLightE.getComponent(TintComponent.class).color.a = 0;
                lbl_nameE.getComponent(TintComponent.class).color.a = 0;
            }
        }
        hideWindow(tcp);

    }

    private void hideWindow(TintComponent ticParent) {
        if (!show && ticParent.color.a <= 0 && showcaseE != null) {
            if (itemIcon != null) {
                tcItem.x = FAR_FAR_AWAY_X;
                sceneLoader.getEngine().removeEntity(itemIcon);
                itemIcon = null;
                tcItem = null;
            }
            tcShowCase.x = FAR_FAR_AWAY_X;
        }
    }

    public void initShowCase() {
        show = true;
        coin = showcaseE.getComponent(NodeComponent.class).getChild(COIN);

        lbl_nameE = showcaseE.getComponent(NodeComponent.class).getChild(LBL_ITEM_NAME);
        showcaseE.getComponent(ZIndexComponent.class).setZIndex(200);
        LabelComponent lc = lbl_nameE.getComponent(LabelComponent.class);

        lc.text.replace(0, lc.text.capacity(), showCaseVanity.name);

        spotLightE = showcaseE.getComponent(NodeComponent.class).getChild(SPOTLIGHT);

        lbl_descE = showcaseE.getComponent(NodeComponent.class).getChild(LBL_ITEM_DESC);
        LabelComponent lc2 = lbl_descE.getComponent(LabelComponent.class);
        if (showCaseVanity.description != null) {
            lc2.text.replace(0, lc2.text.capacity(), showCaseVanity.description);
        } else {
            lc2.text.replace(0, lc2.text.capacity(), "");
        }

        lbl_collE = showcaseE.getComponent(NodeComponent.class).getChild(LBL_ITEM_COLLECTION);
        LabelComponent lcColl = lbl_collE.getComponent(LabelComponent.class);
        if (showCaseVanity.collection != null && !"".equals(showCaseVanity.collection)) {
            lcColl.text.replace(0, lcColl.text.capacity(), "In " + showCaseVanity.collection + " collection");
        } else {
            lcColl.text.replace(0, lcColl.text.capacity(), "");
        }

        lbl_priceE = showcaseE.getComponent(NodeComponent.class).getChild(LBL_ITEM_PRICE);
        LabelComponent lc3 = lbl_priceE.getComponent(LabelComponent.class);
        lc3.text.replace(0, lc3.text.capacity(), Long.toString(showCaseVanity.cost));

//        Entity aniE = showcaseE.getComponent(NodeComponent.class).getChild(SHOWCASE_ANI);
//        SpriterComponent sc = ComponentRetriever.get(aniE, SpriterComponent.class);
//        sc.animationName = INTRO;
//        sc.player.speed = GlobalConstants.FPS / 4;

        initShowCaseItem();

//        lbl_priceE.getComponent(TintComponent.class).color.a = 0;
//        lbl_collE.getComponent(TintComponent.class).color.a = 0;
//        coin.getComponent(TintComponent.class).color.a = 0;
//        lbl_descE.getComponent(TintComponent.class).color.a = 0;
//        spotLightE.getComponent(TintComponent.class).color.a = 0;
//        lbl_nameE.getComponent(TintComponent.class).color.a = 0;
//        System.out.println("lbl_descE.getComponent(TintComponent.class).color.a: " + lbl_descE.getComponent(TintComponent.class).color.a);
//        System.out.println("lbl_collE.getComponent(TintComponent.class).color.a: " + lbl_collE.getComponent(TintComponent.class).color.a);

        tcShowCase.x = -25;
        tcShowCase.y = -35;

        isActing = true;
    }

    private void initShowCaseItem() {
        CompositeItemVO tempItemC = sceneLoader.loadVoFromLibrary(showCaseVanity.shopIcon).clone();
        itemIcon = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempItemC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), itemIcon, tempItemC.composite);
        sceneLoader.getEngine().addEntity(itemIcon);
        itemIcon.getComponent(ZIndexComponent.class).setZIndex(showcaseE.getComponent(ZIndexComponent.class).getZIndex() + 1);

        tcItem = itemIcon.getComponent(TransformComponent.class);
        tcItem.x = 570;
        tcItem.y = 900;
//        tcItem.scaleX = 0.05f;
//        tcItem.scaleY = 0.05f;
//        itemIcon.getComponent(TintComponent.class).color.a = 0;


//        ActionComponent ac = new ActionComponent();
//        Actions.checkInit();
//        ac.dataArray.add(Actions.parallel(
//                Actions.scaleTo(1.5f, 1.5f, 0.7f, Interpolation.exp5Out),
//                Actions.fadeIn(0.8f, Interpolation.exp10Out)));
//        itemIcon.add(ac);
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
                    lbl_collE.getComponent(TintComponent.class).color.a = 0;
                    coin.getComponent(TintComponent.class).color.a = 0;
                    lbl_descE.getComponent(TintComponent.class).color.a = 0;
                    spotLightE.getComponent(TintComponent.class).color.a = 0;
                    lbl_nameE.getComponent(TintComponent.class).color.a = 0;
                }
            }
        });
    }

    private void initShowCaseBuyButton() {
        buyBtn = showcaseE.getComponent(NodeComponent.class).getChild(BTN_BUY);
        if (buyBtn.getComponent(ButtonComponent.class) == null) {
            buyBtn.add(new ButtonComponent());
        }
        buyBtn.getComponent(TintComponent.class).color.a = 0;
        buyBtn.getComponent(ButtonComponent.class).clearListeners();
        buyBtn.getComponent(ButtonComponent.class).addListener(new ImageButtonListener(buyBtn) {
            @Override
            public void clicked() {
                if (btn.getComponent(TintComponent.class).color.a > 0) {
                    showCaseVanity.buyAndUse();
                    GameStage.changedFlower2 = true;
                    ResultScreenScript.isWasShowcase = true;
                    resultScreen.initResultScreen();
                    if (GameStage.shopScript != null) {
                        GameStage.shopScript.preview.changeBagIcon(showCaseVanity);
                    }
//                    showCaseVanity = null;
                    isCelebrating = true;
                }
            }
        });
    }
}
