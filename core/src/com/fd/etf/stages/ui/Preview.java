package com.fd.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.brashmonkey.spriter.Dimension;
import com.fd.etf.entity.componets.ShopItem;
import com.fd.etf.entity.componets.VanityComponent;
import com.fd.etf.entity.componets.listeners.ImageButtonListener;
import com.fd.etf.stages.GameStage;
import com.fd.etf.stages.ShopScreenScript;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;

import java.util.List;

import static com.fd.etf.entity.componets.ShopItem.HARD;
import static com.fd.etf.entity.componets.ShopItem.SOFT;
import static com.fd.etf.stages.GameStage.sceneLoader;
import static com.fd.etf.stages.GameStage.shopScript;
import static com.fd.etf.stages.ShopScreenScript.*;
import static com.fd.etf.utils.EffectUtils.DEFAULT_LAYER;
import static com.fd.etf.utils.EffectUtils.playYellowStarsParticleEffect;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_Y;

public class Preview extends AbstractDialog {

    public static final String PREVIEW = "tag_lib";
    public static final String ITEM_UNKNOWN = "item_unknown_n";
    public static final String BTN_RIGHT = "tag_right_btn";
    public static final String BTN_LEFT = "tag_left_btn";
    public static final String BTN_BUY = "tag_btn_buy";
    public static final String BTN_DISABLE = "tag_btn_disable";
    public static final String BTN_ENABLE = "tag_btn_enable";
    public static final String TAG_INFO_LIB = "tag_info_lib";

    public static final String BTN_CLOSE = "tag_btn_close";
    public static final String LBL_ITEM_NAME = "tag_lbl_item_name";
    public static final String LBL_PRICE_SH = "tag_lbl_price_sh";
    public static final String LBL_PAPER_PIECE = "paper_piece_img";
    public static final String COINZ_ICON = "coinz_icon";
    public static final String LBL_PRICE = "tag_lbl_price";

    private static final int HIDE_INFO_TAG_RIGHT = 2000;
    private static final int HIDE_INFO_TAG_LEFT = -1000;

    public static final String LBL_DESC = "tag_lbl_desc";
    public static final String IMG_SEC_BUBBLE = "img_sec_bubble";
    private static final float HIDE_INFO_TAG_DURATION = 0.3f;
    private static final String BTN_INACTIVE = "Gray";
    private static final String TAG_BUTTONZ_LIB = "tag_buttonz_lib";
    public Entity lbl_desc;
    public Entity lblPrice;
    public Entity lblPriceSh;
    public Entity lblTitle;

    public static final String TAG_NOT_NUFF = "tag_lbl_not_enough";
    public static final String TAG_NOT_NUFF_SH = "tag_lbl_not_enough_sh";
    public Entity lblNotEnough;
    public Entity lblNotEnoughSh;

    public static final int ICON_X = 550;
    public static final int ICON_X_RELATIVE = 130;
    public static final int ICON_Y_RELATIVE = 150;

    public static final int INFO_TAG_X = 350;
    public static final int INFO_TAG_Y = 240;

    public static final int BTNZ_X = 308;
    public static final int BTNZ_Y = 33;

    private static final int UNKNOWN_ICON_Y = 350;
    private static final int INFO_TAG_HIDE_Y = 900;
    private static final int UNKNOWN_ICON_Y_ON_JUMP = INFO_TAG_HIDE_Y;

    private static boolean shouldDeleteIconE = true;
    private Entity iconE;
    private Entity btnPrev;
    private Entity btnNext;
    private Entity btnClose;
    private Entity infoTag;
    private Entity buttonz;

    private ShopItem vc;
    private int movedTo;
    public boolean canPlayDescAni;

    private void loadPreviewFromLib() {
        CompositeItemVO infoTempItemC = GameStage.sceneLoader.loadVoFromLibrary(TAG_INFO_LIB).clone();
        infoTag = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), infoTempItemC);
        GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), infoTag, infoTempItemC.composite);
        GameStage.sceneLoader.getEngine().addEntity(infoTag);

        CompositeItemVO buttonzTempItemC = GameStage.sceneLoader.loadVoFromLibrary(TAG_BUTTONZ_LIB).clone();
        buttonz = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), buttonzTempItemC);
        GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), buttonz, buttonzTempItemC.composite);
        GameStage.sceneLoader.getEngine().addEntity(buttonz);
    }

    public Preview() {
    }

    public void init() {
        loadPreviewFromLib();
        lbl_desc = infoTag.getComponent(NodeComponent.class).getChild(LBL_DESC);
        lblTitle = infoTag.getComponent(NodeComponent.class).getChild(LBL_ITEM_NAME);
        lblPrice = buttonz.getComponent(NodeComponent.class).getChild(LBL_PRICE_SH);
        lblPriceSh = buttonz.getComponent(NodeComponent.class).getChild(LBL_PRICE);
        lblNotEnough = buttonz.getComponent(NodeComponent.class).getChild(TAG_NOT_NUFF);
        lblNotEnoughSh = buttonz.getComponent(NodeComponent.class).getChild(TAG_NOT_NUFF_SH);
        btnPrev = buttonz.getComponent(NodeComponent.class).getChild(BTN_LEFT);
        btnNext = buttonz.getComponent(NodeComponent.class).getChild(BTN_RIGHT);

        initCloseBtn();
        infoTag.getComponent(ZIndexComponent.class).setZIndex(51);
        buttonz.getComponent(ZIndexComponent.class).setZIndex(51);
        buttonz.getComponent(TransformComponent.class).x = BTNZ_X;
        buttonz.getComponent(TransformComponent.class).y = -500;
        infoTag.getComponent(TransformComponent.class).x = INFO_TAG_X;
        infoTag.getComponent(TransformComponent.class).y = INFO_TAG_HIDE_Y;

        infoTag.getComponent(NodeComponent.class).
                getChild(LBL_PAPER_PIECE).getComponent(ZIndexComponent.class).setZIndex(20);
        ZIndexComponent z1 = infoTag.getComponent(NodeComponent.class).
                getChild(LBL_PAPER_PIECE).getComponent(ZIndexComponent.class);
        infoTag.getComponent(NodeComponent.class).
                getChild(BTN_CLOSE).getComponent(ZIndexComponent.class).setZIndex(z1.getZIndex()+1);
        infoTag.getComponent(NodeComponent.class).
                getChild(LBL_ITEM_NAME).getComponent(ZIndexComponent.class).setZIndex(z1.getZIndex()+1);

        initShadow();
    }

    private void initCloseBtn() {
        btnClose = infoTag.getComponent(NodeComponent.class).getChild(BTN_CLOSE);

        if (btnClose.getComponent(ButtonComponent.class) == null) {
            btnClose.add(new ButtonComponent());
        }
        btnClose.getComponent(ButtonComponent.class).clearListeners();
        btnClose.getComponent(ButtonComponent.class).
                addListener(new ImageButtonListener(btnClose) {
                    @Override
                    public void clicked() {
                        checkAndClose();
                    }
                });
    }

    public void initBoughtPreviewIcon(boolean playAni) {

        removeIconE();
        CompositeItemVO tempItemC = sceneLoader.loadVoFromLibrary(vc.shopIcon);
        iconE = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempItemC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), iconE, tempItemC.composite);
        sceneLoader.getEngine().addEntity(iconE);

        if (playAni) {
            iconE.getComponent(TransformComponent.class).x = infoTag.getComponent(TransformComponent.class).x + 20;
            iconE.getComponent(TransformComponent.class).y = UNKNOWN_ICON_Y;
            iconE.getComponent(ZIndexComponent.class).setZIndex(infoTag.getComponent(ZIndexComponent.class).getZIndex() + 10);
            playYellowStarsParticleEffect(544, 467);
        } else {
            iconE.getComponent(TransformComponent.class).x = INFO_TAG_X + ICON_X_RELATIVE;
            iconE.getComponent(TransformComponent.class).y = INFO_TAG_Y + ICON_Y_RELATIVE;
            iconE.getComponent(ZIndexComponent.class).setZIndex(infoTag.getComponent(ZIndexComponent.class).getZIndex() + 200);
        }
    }

    public void setLabelsValues() {
        if (vc.description != null) {
            lbl_desc.getComponent(LabelComponent.class).text.replace(0, lbl_desc.getComponent(LabelComponent.class).text.length, vc.description);
        }
        if (vc.collection != null) {
            lbl_desc.getComponent(LabelComponent.class).text.replace(0, lbl_desc.getComponent(LabelComponent.class).text.length, vc.collection);
        }
        lblTitle.getComponent(LabelComponent.class).text.replace(0, lblTitle.getComponent(LabelComponent.class).text.length, vc.name);
        lblPrice.getComponent(LabelComponent.class).text.replace(0, lblPrice.getComponent(LabelComponent.class).text.length,
                String.valueOf(vc.cost));
        lblPriceSh.getComponent(LabelComponent.class).text.replace(0, lblPriceSh.getComponent(LabelComponent.class).text.length,
                String.valueOf(vc.cost));
    }

    public boolean canBuyCheck(VanityComponent vc, Entity btn_buy) {
        if (vc.canBuy()) {
            btn_buy.getComponent(ZIndexComponent.class).setZIndex(100);
            lblNotEnough.getComponent(TintComponent.class).color.a = 0;
            lblNotEnoughSh.getComponent(TintComponent.class).color.a = 0;
            return true;
        } else {
            btn_buy.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
            lblNotEnough.getComponent(TransformComponent.class).y = 36;
            lblNotEnoughSh.getComponent(TransformComponent.class).y = 36;
            lblNotEnough.getComponent(TintComponent.class).color.a = 1;
            lblNotEnoughSh.getComponent(TintComponent.class).color.a = 1;
            lblNotEnough.getComponent(ZIndexComponent.class).setZIndex(99);
            lblNotEnoughSh.getComponent(ZIndexComponent.class).setZIndex(100);
            lblPriceSh.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            lblPrice.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            buttonz.getComponent(NodeComponent.class).getChild(COINZ_ICON).getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            return false;
        }
    }

    public Entity initUnknownPreviewIcon(boolean jump) {
        removeIconE();

        CompositeItemVO tempItemC = sceneLoader.loadVoFromLibrary(ITEM_UNKNOWN);
        iconE = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempItemC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), iconE, tempItemC.composite);
        sceneLoader.getEngine().addEntity(iconE);

        lblNotEnough.getComponent(ZIndexComponent.class).setZIndex(52);
        lblNotEnoughSh.getComponent(ZIndexComponent.class).setZIndex(53);

        if (!jump) {
            iconE.getComponent(TransformComponent.class).x =
                    infoTag.getComponent(TransformComponent.class).x + 20;
            iconE.getComponent(TransformComponent.class).y = UNKNOWN_ICON_Y;
        } else {
            iconE.getComponent(TransformComponent.class).x = ICON_X;
            iconE.getComponent(TransformComponent.class).y = UNKNOWN_ICON_Y_ON_JUMP;
        }
        iconE.getComponent(ZIndexComponent.class).setZIndex(infoTag.getComponent(ZIndexComponent.class).getZIndex() + 100);
        return iconE;
    }

    public void showPreview(ShopItem vc, boolean jump, boolean justBoughtAni) {
        if (infoTag == null) {
            init();
        }
        this.vc = vc;
        isPreviewOn.set(true);
        setLabelsValues();

        if (!vc.bought) {
            initBuyButton(vc);
        } else {
            initEnableButton(vc);
            initDisableButton(vc);
        }
        initPrevButton(vc);
        initNextButton(vc);

        initIcon(vc, jump, justBoughtAni);
        iconE.getComponent(ZIndexComponent.class).setZIndex(101);

        if (jump) {
            addShadow();
            infoTag.getComponent(TransformComponent.class).y = INFO_TAG_HIDE_Y - 10;
            ActionComponent ac = new ActionComponent();
            Actions.checkInit();
            ac.dataArray.add(Actions.moveTo(INFO_TAG_X, INFO_TAG_Y, 1f, Interpolation.exp10Out));
            infoTag.add(ac);

            ActionComponent acButtonz = new ActionComponent();
            Actions.checkInit();
            acButtonz.dataArray.add(Actions.moveTo(BTNZ_X, BTNZ_Y, 1f, Interpolation.exp10Out));
            buttonz.add(acButtonz);

        } else {
            ActionComponent ac = new ActionComponent();
            Actions.checkInit();
            ac.dataArray.add(Actions.moveTo(INFO_TAG_X, infoTag.getComponent(TransformComponent.class).y, HIDE_INFO_TAG_DURATION));
            infoTag.add(ac);
        }

        setDescription(vc, jump, justBoughtAni);

        for (Entity e: ShopScreenScript.itemIcons.values()){
            e.getComponent(ZIndexComponent.class).
                    setZIndex(shadowE.getComponent(ZIndexComponent.class).getZIndex()-2);
        }
        infoTag.getComponent(ZIndexComponent.class).setZIndex(shadowE.getComponent(ZIndexComponent.class).getZIndex() + 10);
        buttonz.getComponent(ZIndexComponent.class).setZIndex(shadowE.getComponent(ZIndexComponent.class).getZIndex() + 10);
    }

    private void setDescription(ShopItem vc, boolean jump, boolean justBoughtAni) {
        if (vc.description == null) {
            infoTag.getComponent(NodeComponent.class).
                    getChild(LBL_DESC).getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
            infoTag.getComponent(NodeComponent.class).
                    getChild(IMG_SEC_BUBBLE).getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
        } else if (vc.description != null && canPlayDescAni){
            infoTag.getComponent(NodeComponent.class).
                    getChild(IMG_SEC_BUBBLE).getComponent(TransformComponent.class).x = 100;
            infoTag.getComponent(NodeComponent.class).
                    getChild(IMG_SEC_BUBBLE).getComponent(TransformComponent.class).y = 51;
            infoTag.getComponent(NodeComponent.class).
                    getChild(IMG_SEC_BUBBLE).getComponent(TransformComponent.class).scaleX = 0.5f;
            infoTag.getComponent(NodeComponent.class).
                    getChild(IMG_SEC_BUBBLE).getComponent(TransformComponent.class).scaleY = 0.5f;
            infoTag.getComponent(NodeComponent.class).
                    getChild(IMG_SEC_BUBBLE).getComponent(TintComponent.class).color.a = 0;
            infoTag.getComponent(NodeComponent.class).
                    getChild(IMG_SEC_BUBBLE).getComponent(ZIndexComponent.class).setZIndex(infoTag.getComponent(NodeComponent.class).
                    getChild(LBL_PAPER_PIECE).getComponent(ZIndexComponent.class).getZIndex()-2);

            infoTag.getComponent(NodeComponent.class).
                    getChild(LBL_DESC).getComponent(TransformComponent.class).x = 100;
            infoTag.getComponent(NodeComponent.class).
                    getChild(LBL_DESC).getComponent(TransformComponent.class).y = 51;
            infoTag.getComponent(NodeComponent.class).
                    getChild(LBL_DESC).getComponent(TintComponent.class).color.a = 0;
            infoTag.getComponent(NodeComponent.class).
                    getChild(LBL_DESC).getComponent(ZIndexComponent.class).setZIndex(infoTag.getComponent(NodeComponent.class).
                    getChild(IMG_SEC_BUBBLE).getComponent(ZIndexComponent.class).getZIndex()+1);

            ActionComponent ac = new ActionComponent();
            Actions.checkInit();
            ac.dataArray.add(Actions.sequence(Actions.delay(0.5f),
                    Actions.parallel(Actions.fadeIn(1.5f, Interpolation.exp5), Actions.moveTo(434, 51, 1, Interpolation.exp5),
                            Actions.scaleTo(1, 1, 1f, Interpolation.exp5))));
            infoTag.getComponent(NodeComponent.class).
                    getChild(IMG_SEC_BUBBLE).add(ac);
            infoTag.getComponent(NodeComponent.class).
                    getChild(LBL_DESC).add(ac);
            canPlayDescAni = false;
        }
    }

    private void initIcon(ShopItem vc, boolean jump, boolean justBoughtAni) {
        if (vc.bought || vc.currencyType.equals(HARD)) {
            initBoughtPreviewIcon(justBoughtAni);
        } else {
            initUnknownPreviewIcon(jump);
        }
    }

    public void initBuyButton(final ShopItem vc) {
        final Entity btnBuy = buttonz.getComponent(NodeComponent.class).getChild(BTN_BUY);
        final Entity coinzE = buttonz.getComponent(NodeComponent.class).getChild(COINZ_ICON);
        buttonz.getComponent(NodeComponent.class).getChild(BTN_DISABLE).getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
        buttonz.getComponent(NodeComponent.class).getChild(BTN_ENABLE).getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
        buttonz.getComponent(NodeComponent.class).getChild(TAG_NOT_NUFF).getComponent(TintComponent.class).color.a = 0;
        buttonz.getComponent(NodeComponent.class).getChild(TAG_NOT_NUFF_SH).getComponent(TintComponent.class).color.a = 0;

        if (!vc.bought && (vc.currencyType.equals(HARD) || canBuyCheck((VanityComponent) vc, btnBuy))) {
            btnBuy.getComponent(ZIndexComponent.class).setZIndex(61);
            btnBuy.getComponent(TransformComponent.class).y = 5;
            if (vc.currencyType.equals(HARD)) {
                coinzE.getComponent(TintComponent.class).color.a = 0;
                lblPrice.getComponent(LabelComponent.class).text.append("$");
                lblPriceSh.getComponent(LabelComponent.class).text.append("$");
            } else {
                coinzE.getComponent(TintComponent.class).color.a = 1;
                coinzE.getComponent(TransformComponent.class).y = 27;
                coinzE.getComponent(ZIndexComponent.class).setZIndex(btnBuy.getComponent(ZIndexComponent.class).getZIndex() + 10);
            }
            // Finding the middle of the button to display price and coin icon
            // only works if width of text label is 1
            int wordCount = lblPrice.getComponent(LabelComponent.class).getText().length;
            int base = 282; // x pos
            if (vc.currencyType.equals(SOFT)) {
                lblPriceSh.getComponent(TransformComponent.class).x = base + 15 * wordCount;
                lblPrice.getComponent(TransformComponent.class).x = base + 15 * wordCount;
                coinzE.getComponent(TransformComponent.class).x = base + 15 * wordCount;
            } else {
                lblPriceSh.getComponent(TransformComponent.class).x = base + 20 * wordCount;
                lblPrice.getComponent(TransformComponent.class).x = base + 20 * wordCount;
            }
            lblPrice.getComponent(ZIndexComponent.class).setZIndex(btnBuy.getComponent(ZIndexComponent.class).getZIndex() + 10);
            lblPriceSh.getComponent(ZIndexComponent.class).setZIndex(101);

            if (btnBuy.getComponent(ButtonComponent.class) == null) {
                btnBuy.add(new ButtonComponent());
            }
            btnBuy.getComponent(ButtonComponent.class).clearListeners();
            btnBuy.getComponent(ButtonComponent.class).addListener(new ImageButtonListener(btnBuy) {
                @Override
                public void clicked() {
                    if (btnBuy.getComponent(ZIndexComponent.class).getZIndex() > 2 && animFinished()) {
                        if (vc.currencyType.equals(HARD)) {
                            vc.buyHard();
                        } else {
                            vc.buyAndUse();
                            putInPlaceNewIconPosition();
                        }
                        showPreview(vc, false, true);
                        ShopScreenScript.reloadScoreLabel(GameStage.gameScript.fpc);
                    }
                }
            });
        }
    }

    private void putInPlaceNewIconPosition() {
        TransformComponent tc = changeBagIcon(vc);
        itemIcons.get(vc.shopIcon).add(tc);
        itemIcons.get(vc.shopIcon).getComponent(ZIndexComponent.class).setZIndex(
                ShopScreenScript.bagsZindex + 10);
        sceneLoader.getEngine().addEntity(itemIcons.get(vc.shopIcon));
        itemIcons.get(vc.shopIcon).getComponent(ZIndexComponent.class).setZIndex(
                shadowE.getComponent(ZIndexComponent.class).getZIndex() - 1);
    }

    public TransformComponent changeBagIcon(ShopItem vc) {
        if (vc.currencyType.equals(SOFT)) {
            CompositeItemVO tempItemC = sceneLoader.loadVoFromLibrary(vc.shopIcon);
            Entity iconBagClone = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempItemC);
            sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), iconBagClone, tempItemC.composite);
            TransformComponent oldTc = itemIcons.get(vc.shopIcon).getComponent(TransformComponent.class);
            sceneLoader.getEngine().removeEntity(itemIcons.get(vc.shopIcon));
            itemIcons.put(vc.shopIcon, iconBagClone);
            return oldTc;
        }
        return null;
    }

    public void initEnableButton(final ShopItem vc) {
        if (vc.bought && !vc.enabled) {
            buttonz.getComponent(NodeComponent.class).getChild(BTN_DISABLE).getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
            buttonz.getComponent(NodeComponent.class).getChild(BTN_BUY).getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
            buttonz.getComponent(NodeComponent.class).getChild(TAG_NOT_NUFF).getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
            buttonz.getComponent(NodeComponent.class).getChild(TAG_NOT_NUFF_SH).getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
            buttonz.getComponent(NodeComponent.class).getChild(COINZ_ICON).getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
            lblPriceSh.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            lblPrice.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;

            Entity enableBtn = buttonz.getComponent(NodeComponent.class).getChild(BTN_ENABLE);
            enableBtn.getComponent(ZIndexComponent.class).setZIndex(101);
//            enableBtn.getComponent(TransformComponent.class).x = 282;
            enableBtn.getComponent(TransformComponent.class).y = 0;

            if (enableBtn.getComponent(ButtonComponent.class) == null) {
                enableBtn.add(new ButtonComponent());
            }
            enableBtn.getComponent(ButtonComponent.class).clearListeners();
            enableBtn.getComponent(ButtonComponent.class)
                    .addListener(new ImageButtonListener(enableBtn) {
                        @Override
                        public void clicked() {
                            if (animFinished()) {
                                vc.apply();
                                showPreview(vc, false, false);
                            }
                        }
                    });
        }
    }

    public void initDisableButton(final ShopItem vc) {
        if (vc.bought && vc.enabled) {
            buttonz.getComponent(NodeComponent.class).getChild(BTN_ENABLE).getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
            buttonz.getComponent(NodeComponent.class).getChild(BTN_BUY).getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
            buttonz.getComponent(NodeComponent.class).getChild(TAG_NOT_NUFF).getComponent(TransformComponent.class).y = FAR_FAR_AWAY_X;
            buttonz.getComponent(NodeComponent.class).getChild(TAG_NOT_NUFF_SH).getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
            buttonz.getComponent(NodeComponent.class).getChild(COINZ_ICON).getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
            lblPriceSh.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            lblPrice.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;

            Entity disableBtn = buttonz.getComponent(NodeComponent.class).getChild(BTN_DISABLE);
            disableBtn.getComponent(ZIndexComponent.class).setZIndex(101);
//            disableBtn.getComponent(TransformComponent.class).x = 282;
            disableBtn.getComponent(TransformComponent.class).y = 0;

            disableBtn.add(new ButtonComponent());
            if (disableBtn.getComponent(ButtonComponent.class) == null) {
                disableBtn.getComponent(ButtonComponent.class).clearListeners();
            }
            disableBtn.getComponent(ButtonComponent.class)
                    .addListener(new ImageButtonListener(disableBtn) {
                        @Override
                        public void clicked() {
                            if (animFinished()) {
                                vc.disable();
                                showPreview(vc, false, false);
                            }
                        }
                    });
        }
    }

    private void initPrevButton(final ShopItem vc) {
        if (btnPrev.getComponent(ButtonComponent.class) == null) {
            btnPrev.add(new ButtonComponent());
        }
        btnPrev.getComponent(ButtonComponent.class).clearListeners();

        LayerMapComponent lc = btnPrev.getComponent(LayerMapComponent.class);
        if (isPrevBtnActive(vc)) {
            lc.getLayer(BTN_INACTIVE).isVisible = false;
            lc.getLayer(DEFAULT_LAYER).isVisible = true;
        } else {
            lc.getLayer(DEFAULT_LAYER).isVisible = false;
            lc.getLayer(BTN_INACTIVE).isVisible = true;
        }

        btnPrev.getComponent(ButtonComponent.class)
                .addListener(new ImageButtonListener(btnPrev) {
                    @Override
                    public void touchUp() {
                        if (isPrevBtnActive(vc))
                            super.touchUp();
                    }

                    @Override
                    public void touchDown() {
                        if (isPrevBtnActive(vc))
                            super.touchDown();
                    }

                    @Override
                    public void clicked() {
                        if (animFinished() && isPrevBtnActive(vc)) {
                            ActionComponent ac = new ActionComponent();
                            Actions.checkInit();
                            ac.dataArray.add(Actions.moveTo(HIDE_INFO_TAG_RIGHT, infoTag.getComponent(TransformComponent.class).y, HIDE_INFO_TAG_DURATION));
                            infoTag.add(ac);
                            movedTo = HIDE_INFO_TAG_RIGHT;
                            if (vc.currencyType.equals(SOFT) && (allSoftItems.indexOf(vc)) % 8 == 0) {
                                shopScript.scrollBagsOnePageLeft();
                            }
                        }
                    }
                });
    }

    private boolean isPrevBtnActive(ShopItem vc) {
        return (vc.currencyType.equals(HARD) && allHCItems.indexOf(vc) > 0) ||
                (vc.currencyType.equals(SOFT) && allSoftItems.indexOf(vc) > 0);
    }

    private void showPreviewAfterAniPrev() {
        if (vc.currencyType.equals(SOFT)) {
            prevItem(allSoftItems);
        } else {
            prevItem(allHCItems);
        }
    }

    private void prevItem(List<ShopItem> list) {
        int previousIndex = list.indexOf(vc) - 1;
        if (previousIndex >= 0) {
            setShouldDeleteIconE();
            canPlayDescAni = true;
            showPreview(list.get(previousIndex), false, false);
        }
    }

    private void initNextButton(final ShopItem vc) {
        if (btnNext.getComponent(ButtonComponent.class) == null) {
            btnNext.add(new ButtonComponent());
        }

        LayerMapComponent lc = btnNext.getComponent(LayerMapComponent.class);
        if (isNextBtnActive(vc)) {
            lc.getLayer(BTN_INACTIVE).isVisible = false;
            lc.getLayer(DEFAULT_LAYER).isVisible = true;
        } else {
            lc.getLayer(DEFAULT_LAYER).isVisible = false;
            lc.getLayer(BTN_INACTIVE).isVisible = true;
        }

        btnNext.getComponent(ButtonComponent.class).clearListeners();
        btnNext.getComponent(ButtonComponent.class)
                .addListener(new ImageButtonListener(btnNext) {
                    @Override
                    public void touchUp() {
                        if (isNextBtnActive(vc))
                            super.touchUp();
                    }

                    @Override
                    public void touchDown() {
                        if (isNextBtnActive(vc))
                            super.touchDown();
                    }

                    @Override
                    public void clicked() {
                        if (isNextBtnActive(vc) && animFinished()) {
                            ActionComponent ac = new ActionComponent();
                            Actions.checkInit();
                            ac.dataArray.add(Actions.moveTo(HIDE_INFO_TAG_LEFT, infoTag.getComponent(TransformComponent.class).y, HIDE_INFO_TAG_DURATION));
                            infoTag.add(ac);
                            movedTo = HIDE_INFO_TAG_LEFT;

                            if (vc.currencyType.equals(SOFT) && (allSoftItems.indexOf(vc) + 1) % 8 == 0) {
                                shopScript.scrollBagsOnePageRight();
                            }
                        }
                    }
                });
    }

    private boolean isNextBtnActive(ShopItem vc) {
        return (allHCItems.indexOf(vc) < allHCItems.size() - 1 && vc.currencyType.equals(HARD))
                || (vc.currencyType.equals(SOFT) && allSoftItems.indexOf(vc) < allSoftItems.size() - 1);
    }

    private void showPreviewAfterAniNext() {
        if (vc.currencyType.equals(SOFT)) {
            nextItem(allSoftItems);
        } else {
            nextItem(allHCItems);
        }
    }

    private void nextItem(List<ShopItem> list) {
        int nextIndex = list.indexOf(vc) + 1;
        if (nextIndex < list.size()) {
            setShouldDeleteIconE();
            canPlayDescAni = true;
            showPreview(list.get(nextIndex), false, false);
        }
    }

    public void checkAndClose() {
        if (infoTag != null) {
            float currentYpos = infoTag.getComponent(TransformComponent.class).y;
            if (currentYpos <= INFO_TAG_Y + 30 || currentYpos >= 800) {
                if (isPreviewOn.get()) {
                    ActionComponent ac = new ActionComponent();
                    Actions.checkInit();
                    ac.dataArray.add(Actions.moveTo(INFO_TAG_X, INFO_TAG_HIDE_Y, 0.8f, Interpolation.exp10));
                    infoTag.add(ac);

                    ActionComponent acButtonz = new ActionComponent();
                    Actions.checkInit();
                    acButtonz.dataArray.add(Actions.moveTo(BTNZ_X, -300, 1f, Interpolation.exp10Out));
                    buttonz.add(acButtonz);

                    ActionComponent ac2 = new ActionComponent();
                    ac2.dataArray.add(Actions.fadeOut(0.8f, Interpolation.exp5));
                    shadowE.add(ac2);
                }
            }
        }
    }

    public void removeIconE() {
        if (iconE != null) {
            if (shouldDeleteIconE) {
                sceneLoader.getEngine().removeEntity(iconE);
            } else {
                iconE.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            }
        }
    }

    public void setShouldDeleteIconE() {
        shouldDeleteIconE = vc.currencyType.equals(SOFT);
    }

    public void updatePreview() {
        if (movedTo != 0) {
            if (infoTag.getComponent(TransformComponent.class).x == movedTo) {
                if (movedTo == HIDE_INFO_TAG_RIGHT) {
                    infoTag.getComponent(TransformComponent.class).x = -500;
                    showPreviewAfterAniPrev();
                } else {
                    infoTag.getComponent(TransformComponent.class).x = 1300;
                    showPreviewAfterAniNext();
                }
                movedTo = 0;
            }
        }

        if (iconE != null) {
            iconE.getComponent(TransformComponent.class).x = infoTag.getComponent(TransformComponent.class).x + ICON_X_RELATIVE;
            iconE.getComponent(TransformComponent.class).y = infoTag.getComponent(TransformComponent.class).y + ICON_Y_RELATIVE;
            iconE.getComponent(TransformComponent.class).scaleX = 1.5f;
            iconE.getComponent(TransformComponent.class).scaleY = 1.5f;
        }
        if (isPreviewOn.get() && infoTag != null && infoTag.getComponent(TransformComponent.class).y >= INFO_TAG_HIDE_Y) {
            isPreviewOn.set(false);
            setShouldDeleteIconE();
            removeIconE();
        }
    }

    private boolean animFinished() {
        return infoTag.getComponent(TransformComponent.class).y <= INFO_TAG_Y + 30
                && infoTag.getComponent(TransformComponent.class).x == INFO_TAG_X;
    }
}