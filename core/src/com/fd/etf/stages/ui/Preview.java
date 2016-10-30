package com.fd.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.fd.etf.entity.componets.ShopItem;
import com.fd.etf.entity.componets.VanityComponent;
import com.fd.etf.entity.componets.listeners.ImageButtonListener;
import com.fd.etf.stages.GameStage;
import com.fd.etf.stages.ShopScreenScript;
import com.fd.etf.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;

import static com.fd.etf.entity.componets.ShopItem.HARD;
import static com.fd.etf.entity.componets.ShopItem.SOFT;
import static com.fd.etf.stages.GameStage.sceneLoader;
import static com.fd.etf.stages.ShopScreenScript.*;
import static com.fd.etf.utils.EffectUtils.getTouchCoordinates;
import static com.fd.etf.utils.EffectUtils.playYellowStarsParticleEffect;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;

public class Preview extends AbstractDialog {

    public static final String PREVIEW = "tag_lib";
    public static final String ITEM_UNKNOWN = "item_unknown_n";
    public static final String BTN_RIGHT = "tag_right_btn";
    public static final String BTN_LEFT = "tag_left_btn";
    public static final String IMG_BG_SHOW_CASE = "tag_img_bg_show_case";
    public static final String BTN_BUY = "tag_btn_buy";
    public static final String BTN_ENABLE = "tag_btn_enable";
    public static final String BTN_DISABLE = "tag_btn_disable";
    public static final String LBL_PRICE = "tag_lbl_price";
    public static final String LBL_ITEM_NAME = "tag_lbl_item_name";
    public static final String LBL_DESC = "tag_lbl_desc";
    public static final String TAG_NOT_NUFF = "tag_lbl_not_enough";

    public static final int ICON_X = 550;
    public static final int ICON_X_RELATIVE = 270;
    public static final int ICON_Y_RELATIVE = 370;
    public static final int PREVIEW_X = 260;
    public static final int PREVIEW_Y = 30;
    public static final float PREVIEW_SCALE = 0.9f;
    private static final int UNKNOWN_ICON_Y = 450;
    private static final int UNKNOWN_ICON_Y_ON_JUMP = 900;

    private static boolean shouldDeleteIconE = true;
    public Entity previewE;
    public Entity lbl_desc;
    public Entity lbl_title;
    public Entity lbl_price;
    public Entity lbl_not_enough;
    public Entity bg;
    private Entity iconE;
    private Entity btnLeft;
    private Entity btnNext;

    private Rectangle previewBoundingBox;
    private ShopItem vc;

    private void loadPreviewFromLib() {
        CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary(PREVIEW).clone();
        previewE = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
        GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), previewE, tempItemC.composite);
        GameStage.sceneLoader.getEngine().addEntity(previewE);
    }

    public Preview() {
    }

    public void init() {
        loadPreviewFromLib();
        lbl_desc = previewE.getComponent(NodeComponent.class).getChild(LBL_DESC);
        lbl_title = previewE.getComponent(NodeComponent.class).getChild(LBL_ITEM_NAME);
        lbl_price = previewE.getComponent(NodeComponent.class).getChild(LBL_PRICE);
        bg = previewE.getComponent(NodeComponent.class).getChild(IMG_BG_SHOW_CASE);
        lbl_not_enough = previewE.getComponent(NodeComponent.class).getChild(TAG_NOT_NUFF);
        btnLeft = previewE.getComponent(NodeComponent.class).getChild(BTN_LEFT);
        btnNext = previewE.getComponent(NodeComponent.class).getChild(BTN_RIGHT);

        previewE.getComponent(ZIndexComponent.class).setZIndex(51);
        initShadow();
    }

    public void initBoughtPreviewIcon(boolean playAni) {

        removeIconE();
        CompositeItemVO tempItemC = sceneLoader.loadVoFromLibrary(vc.shopIcon);
        iconE = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempItemC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), iconE, tempItemC.composite);
        sceneLoader.getEngine().addEntity(iconE);

        if (playAni) {
            if (vc.currencyType.equals(SOFT)) {
                iconE.getComponent(TransformComponent.class).scaleX = GlobalConstants.TENTH;
                iconE.getComponent(TransformComponent.class).scaleY = GlobalConstants.TENTH;
                iconE.getComponent(TransformComponent.class).x = 575;
                iconE.getComponent(TransformComponent.class).y = 409;

                ActionComponent ac = new ActionComponent();
                Actions.checkInit();
                ac.dataArray.add(Actions.parallel(
                        Actions.scaleTo(1, 1, 0.7f, Interpolation.exp5Out),
                        Actions.moveTo(PREVIEW_X + ICON_X_RELATIVE, PREVIEW_Y + ICON_Y_RELATIVE, 0.7f, Interpolation.exp5Out)));
                iconE.add(ac);
                iconE.getComponent(ZIndexComponent.class).setZIndex(101);

            }
            playYellowStarsParticleEffect(544, 467);
        } else {
            if (vc.currencyType.equals(SOFT)) {
                iconE.getComponent(TransformComponent.class).x = PREVIEW_X + ICON_X_RELATIVE;
                iconE.getComponent(TransformComponent.class).y = PREVIEW_Y + ICON_Y_RELATIVE;
            }
            iconE.getComponent(ZIndexComponent.class).setZIndex(101);
        }
    }

    public void setupPreviewWindow() {
        TransformComponent preview_tc = previewE.getComponent(TransformComponent.class);
        preview_tc.x = PREVIEW_X;
        preview_tc.y = PREVIEW_Y;
        preview_tc.scaleX = PREVIEW_SCALE;
        preview_tc.scaleY = PREVIEW_SCALE;
    }

    public void setLabelsValues() {
        if (vc.description != null) {
            lbl_desc.getComponent(LabelComponent.class).text.replace(0, lbl_desc.getComponent(LabelComponent.class).text.length, vc.description);
        }
        lbl_title.getComponent(LabelComponent.class).text.replace(0, lbl_title.getComponent(LabelComponent.class).text.length, vc.name);
        lbl_price.getComponent(LabelComponent.class).text.replace(0, lbl_price.getComponent(LabelComponent.class).text.length, String.valueOf(vc.cost));
    }

    public boolean canBuyCheck(VanityComponent vc, Entity btn_buy) {
        btn_buy.getComponent(ZIndexComponent.class).setZIndex(0);
        lbl_not_enough.getComponent(ZIndexComponent.class).setZIndex(0);
        if (vc.canBuy()) {
            btn_buy.getComponent(ZIndexComponent.class).setZIndex(100);
            lbl_not_enough.getComponent(TintComponent.class).color.a = 0;
            return true;
        } else {
            btn_buy.getComponent(ZIndexComponent.class).setZIndex(0);
            btn_buy.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            lbl_not_enough.getComponent(TintComponent.class).color.a = 1;
            lbl_not_enough.getComponent(ZIndexComponent.class).setZIndex(100);
            return false;
        }
    }

    public Entity initUnknownPreviewIcon(boolean jump) {
        removeIconE();
        CompositeItemVO tempItemC = sceneLoader.loadVoFromLibrary(ITEM_UNKNOWN);
        iconE = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempItemC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), iconE, tempItemC.composite);
        sceneLoader.getEngine().addEntity(iconE);

        lbl_not_enough.getComponent(ZIndexComponent.class).setZIndex(52);
        iconE.getComponent(TransformComponent.class).x = ICON_X;
        if (!jump) {
            iconE.getComponent(TransformComponent.class).y = UNKNOWN_ICON_Y;
        } else {
            iconE.getComponent(TransformComponent.class).y = UNKNOWN_ICON_Y_ON_JUMP;
        }
        iconE.getComponent(ZIndexComponent.class).setZIndex(101);
        return iconE;
    }

    public void showPreview(ShopItem vc, boolean jump, boolean justBoughtAni) {
        if (previewE == null) {
            init();
        }

        this.vc = vc;
        isPreviewOn = true;
        setLabelsValues();

        if (!vc.bought) {
            initBuyButton(vc);
        } else {
            initEnableButton(vc);
            initDisableButton(vc);
        }
        initPrevButton(vc);
        initNextButton(vc);
        setupPreviewWindow();

        if (vc.currencyType.equals(SOFT)) {
            initSoftIcon(vc, jump, justBoughtAni);
        } else {
            initUnknownPreviewIcon(jump);
            if (iconE != null) {
                iconE.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            }
//            iconE = new ItemWrapper(sceneLoader.getRoot()).getChild(PREVIEW).getChild(vc.name);
//            iconE.getComponent(TransformComponent.class).x = ICON_X_RELATIVE;
//            iconE.getComponent(TransformComponent.class).y = ICON_Y_RELATIVE;
        }
        iconE.getComponent(ZIndexComponent.class).setZIndex(101);

        if (jump) {
            addShadow();
            previewE.getComponent(TransformComponent.class).x = PREVIEW_X;
            previewE.getComponent(TransformComponent.class).y = 460;

            ActionComponent ac = new ActionComponent();
            Actions.checkInit();
            ac.dataArray.add(Actions.moveTo(PREVIEW_X, PREVIEW_Y, 1.5f, Interpolation.exp10Out));
            previewE.add(ac);
        } else {
            previewE.getComponent(TransformComponent.class).x = PREVIEW_X;
            previewE.getComponent(TransformComponent.class).y = PREVIEW_Y;
        }

        previewBoundingBox = new Rectangle(previewE.getComponent(TransformComponent.class).x - 120,
                30,
                448,
                previewE.getComponent(DimensionsComponent.class).height);
        previewE.getComponent(ZIndexComponent.class).setZIndex(shadowE.getComponent(ZIndexComponent.class).getZIndex() + 10);
    }

    private void initSoftIcon(ShopItem vc, boolean jump, boolean justBoughtAni) {
        if (vc.bought) {
            initBoughtPreviewIcon(justBoughtAni);
        } else {
            initUnknownPreviewIcon(jump);
        }
    }

    public void initBuyButton(final ShopItem vc) {
        final Entity btnBuy = previewE.getComponent(NodeComponent.class).getChild(BTN_BUY);
        previewE.getComponent(NodeComponent.class).getChild(BTN_DISABLE).getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        previewE.getComponent(NodeComponent.class).getChild(BTN_ENABLE).getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;

        if (!vc.bought && (vc.currencyType.equals(HARD) || canBuyCheck((VanityComponent) vc, btnBuy))) {
            btnBuy.getComponent(ZIndexComponent.class).setZIndex(101);
            btnBuy.getComponent(TransformComponent.class).x =
                    previewE.getComponent(NodeComponent.class).getChild(TAG_NOT_NUFF).getComponent(TransformComponent.class).x;
            btnBuy.getComponent(TransformComponent.class).y =
                    previewE.getComponent(NodeComponent.class).getChild(TAG_NOT_NUFF).getComponent(TransformComponent.class).y;
            if (btnBuy.getComponent(ButtonComponent.class) == null) {
                btnBuy.add(new ButtonComponent());
            }
            btnBuy.getComponent(ButtonComponent.class).clearListeners();
            btnBuy.getComponent(ButtonComponent.class).addListener(new ImageButtonListener(btnBuy) {
                @Override
                public void clicked() {
                    if (btnBuy.getComponent(ZIndexComponent.class).getZIndex() > 2 && animFinished()) {
                        if (vc.currencyType.equals(HARD)) {
                            vc.buyHard(); // TODO: http://cdn.collider.com/wp-content/uploads/die-hard-with-a-vengeance-bruce-willis.jpg
                        } else {
                            vc.buyAndUse();

                            putInPlaceNewIconPosition();
                        }
                        showPreview(vc, false, true);
//                        if (vc.currencyType.equals(SOFT)) {
//                            changeBagIcon(vc);
//                            sceneLoader.getEngine().addEntity(itemIcons.get(vc.shopIcon));
//                            itemIcons.get(vc.shopIcon).getComponent(TransformComponent.class).x = itemIcons.get(vc.shopIcon).getComponent(TransformComponent.class).x;
//                            itemIcons.get(vc.shopIcon).getComponent(TransformComponent.class).y = itemIcons.get(vc.shopIcon).getComponent(TransformComponent.class).y;
//                            itemIcons.get(vc.shopIcon).getComponent(ZIndexComponent.class).setZIndex(36);
//                        }
                        ShopScreenScript.reloadScoreLabel(GameStage.gameScript.fpc);
                    }
                }

                private void putInPlaceNewIconPosition() {
                    TransformComponent tc = changeBagIcon(vc);
                    itemIcons.get(vc.shopIcon).add(tc);
                    itemIcons.get(vc.shopIcon).getComponent(ZIndexComponent.class).setZIndex(
                            ShopScreenScript.bagsZindex + 1
                    );
                    sceneLoader.getEngine().addEntity(itemIcons.get(vc.shopIcon));
//                    itemIcons.get(vc.shopIcon).getComponent(ZIndexComponent.class).setZIndex(36);
                }
            });
        }
    }

    public void initEnableButton(final ShopItem vc) {
        if (vc.bought && !vc.enabled) {
            previewE.getComponent(NodeComponent.class).getChild(BTN_DISABLE).getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            previewE.getComponent(NodeComponent.class).getChild(BTN_BUY).getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            Entity enableBtn = previewE.getComponent(NodeComponent.class).getChild(BTN_ENABLE);
            enableBtn.getComponent(ZIndexComponent.class).setZIndex(101);
            enableBtn.getComponent(TransformComponent.class).x =
                    previewE.getComponent(NodeComponent.class).getChild(TAG_NOT_NUFF).getComponent(TransformComponent.class).x;
            enableBtn.getComponent(TransformComponent.class).y =
                    previewE.getComponent(NodeComponent.class).getChild(TAG_NOT_NUFF).getComponent(TransformComponent.class).y;

            if (enableBtn.getComponent(ButtonComponent.class) == null) {
                enableBtn.add(new ButtonComponent());
            }
            enableBtn.getComponent(ButtonComponent.class).clearListeners();
            enableBtn.getComponent(ButtonComponent.class).addListener(new ImageButtonListener(enableBtn) {
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
            previewE.getComponent(NodeComponent.class).getChild(BTN_ENABLE).getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            previewE.getComponent(NodeComponent.class).getChild(BTN_BUY).getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            Entity disableBtn = previewE.getComponent(NodeComponent.class).getChild(BTN_DISABLE);
            disableBtn.getComponent(ZIndexComponent.class).setZIndex(101);
            disableBtn.getComponent(TransformComponent.class).x =
                    previewE.getComponent(NodeComponent.class).getChild(TAG_NOT_NUFF).getComponent(TransformComponent.class).x;
            disableBtn.getComponent(TransformComponent.class).y =
                    previewE.getComponent(NodeComponent.class).getChild(TAG_NOT_NUFF).getComponent(TransformComponent.class).y;

            disableBtn.add(new ButtonComponent());
            if (disableBtn.getComponent(ButtonComponent.class) == null) {
                disableBtn.getComponent(ButtonComponent.class).clearListeners();
            }
            disableBtn.getComponent(ButtonComponent.class).addListener(new ImageButtonListener(disableBtn) {
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
        if (btnLeft.getComponent(ButtonComponent.class) == null) {
            btnLeft.add(new ButtonComponent());
        }
        btnLeft.getComponent(ButtonComponent.class).clearListeners();
        btnLeft.getComponent(ButtonComponent.class).addListener(new ImageButtonListener(btnLeft) {

            @Override
            public void clicked() {
                if (animFinished()) {
                    if (vc.currencyType.equals(SOFT)) {
                        prevSoftItem();
                    } else {
                        prevHardItem();
                    }
                }
            }

            private void prevSoftItem() {
                int previousIndex = allShopItems.indexOf(vc) - 1;
                if (previousIndex >= 0) {
                    setShouldDeleteIconE();
                    showPreview(allShopItems.get(previousIndex), false, false);
                }
            }

            private void prevHardItem() {
                int previousIndex = allHCItems.indexOf(vc) - 1;
                if (previousIndex >= 0) {
                    setShouldDeleteIconE();
                    showPreview(allHCItems.get(previousIndex), false, false);
                }
            }
        });
    }

    private void initNextButton(final ShopItem vc) {
        if (btnNext.getComponent(ButtonComponent.class) == null) {
            btnNext.add(new ButtonComponent());
        }
        btnNext.getComponent(ButtonComponent.class).clearListeners();
        btnNext.getComponent(ButtonComponent.class).addListener(new ImageButtonListener(btnNext) {
            @Override
            public void clicked() {
                if (animFinished()) {
                    if (vc.currencyType.equals(SOFT)) {
                        nextSoftItem();
                    } else {
                        nextHardItem();
                    }
                }
            }

            private void nextSoftItem() {
                int nextIndex = allShopItems.indexOf(vc) + 1;
                if (nextIndex < allShopItems.size()) {
                    setShouldDeleteIconE();
                    showPreview(allShopItems.get(nextIndex), false, false);
                }
            }

            private void nextHardItem() {
                int nextIndex = allHCItems.indexOf(vc) + 1;
                if (nextIndex < allHCItems.size()) {
                    setShouldDeleteIconE();
                    showPreview(allHCItems.get(nextIndex), false, false);
                }
            }
        });
    }

    public void checkAndClose() {

        if (previewBoundingBox != null) {
            GameStage.sceneLoader.renderer.drawDebugRect(previewBoundingBox.x,
                    previewBoundingBox.y,
                    previewBoundingBox.width,
                    previewBoundingBox.height,
                    previewBoundingBox.toString());
        }
        if (previewE != null) {
            updateTagIcon();
            Vector2 v = getTouchCoordinates();
            boolean isOutside = previewBoundingBox == null ||
                    !previewBoundingBox.contains(v.x, v.y);
            float currentYpos = previewE.getComponent(TransformComponent.class).y;
            if (Gdx.input.isTouched() && currentYpos <= PREVIEW_Y || currentYpos >= 800) {
                if (isPreviewOn && isOutside) {
                    ActionComponent ac = new ActionComponent();
                    Actions.checkInit();
                    ac.dataArray.add(Actions.moveTo(PREVIEW_X, 900, 1, Interpolation.exp10));
                    previewE.add(ac);

                    ActionComponent ac2 = new ActionComponent();
                    ac2.dataArray.add(Actions.fadeOut(0.5f, Interpolation.exp5));
                    shadowE.add(ac2);
                }
            }

            if (previewE.getComponent(TransformComponent.class).y >= 790) {
                isPreviewOn = false;
                setShouldDeleteIconE();
                removeIconE();
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

    private void updateTagIcon() {
        if (iconE != null && vc.currencyType.equals(SOFT)) {
            iconE.getComponent(TransformComponent.class).x = previewE.getComponent(TransformComponent.class).x + ICON_X_RELATIVE;
            iconE.getComponent(TransformComponent.class).y = previewE.getComponent(TransformComponent.class).y + ICON_Y_RELATIVE;
        }
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

    private boolean animFinished() {
        return previewE.getComponent(TransformComponent.class).y <= PREVIEW_Y + 30;
    }
}