package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.entity.componets.ShopItem;
import com.mygdx.game.entity.componets.VanityComponent;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.game.entity.componets.ShopItem.CurrencyType.HARD;
import static com.mygdx.game.entity.componets.ShopItem.CurrencyType.SOFT;
import static com.mygdx.game.stages.GameStage.sceneLoader;
import static com.mygdx.game.stages.ShopScreenScript.*;
import static com.mygdx.game.utils.EffectUtils.playYellowStarsParticleEffect;
import static com.mygdx.game.utils.GlobalConstants.FAR_FAR_AWAY_X;

public class Preview {

    public static final String PREVIEW = "previewTag";
    public static final String ITEM_UNKNOWN = "item_unknown_n";
    public static final String BTN_RIGHT = "tag_right_btn";
    public static final String BTN_LEFT = "tag_left_btn";
    public static final String LBL_NOT_ENOUGH = "tag_lbl_not_enough";
    public static final String IMG_BG_SHOW_CASE = "tag_img_bg_show_case";
    public static final String BTN_BUY = "tag_btn_buy";
    public static final String BTN_ENABLE = "tag_btn_enable";
    public static final String BTN_DISABLE = "tag_btn_disable";
    public static final String LBL_PRICE = "tag_lbl_price";
    public static final String LBL_ITEM_NAME = "tag_lbl_item_name";
    public static final String LBL_DESC = "tag_lbl_desc";
    public static final int ICON_X = 465;
    public static final int ICON_X_RELATIVE = 220;
    public static final int ICON_Y_RELATIVE = 280;
    public static final int PREVIEW_X = 260;
    public static final String NOT_NUFF = "tag_notNuff";
    public static final int PREVIEW_Y = 30;
    private static boolean shouldDeleteIconE = true;
    public Entity previewE;
    public Entity lbl_desc;
    public Entity lbl_title;
    public Entity lbl_price;
    public Entity lbl_not_enough;
    public Entity bg;
    private ItemWrapper shopItem;
    private Entity iconE;
    private Entity btnLeft;
    private Entity btnNext;
    private Rectangle tagBoundingBox;
    private ShopItem vc;
    public Entity shadowE;

    public Preview(ItemWrapper shopItem) {
        this.shopItem = shopItem;
        previewE = shopItem.getChild(PREVIEW).getEntity();
        lbl_desc = shopItem.getChild(PREVIEW).getChild(LBL_DESC).getEntity();
        lbl_title = shopItem.getChild(PREVIEW).getChild(LBL_ITEM_NAME).getEntity();
        lbl_price = shopItem.getChild(PREVIEW).getChild(LBL_PRICE).getEntity();
        bg = shopItem.getChild(PREVIEW).getChild(IMG_BG_SHOW_CASE).getEntity();
        lbl_not_enough = shopItem.getChild(PREVIEW).getChild(LBL_NOT_ENOUGH).getEntity();
        btnLeft = shopItem.getChild(PREVIEW).getChild(BTN_LEFT).getEntity();

        btnNext = shopItem.getChild(PREVIEW).getChild(BTN_RIGHT).getEntity();
        btnNext.getComponent(TransformComponent.class).rotation = -180;
        btnNext.getComponent(TransformComponent.class).x += btnNext.getComponent(DimensionsComponent.class).width;
        btnNext.getComponent(TransformComponent.class).y += btnNext.getComponent(DimensionsComponent.class).height;

        previewE.getComponent(ZIndexComponent.class).setZIndex(51);
    }

    public void initBoughtPreviewIcon(boolean playAni) {

        removeIconE();
        CompositeItemVO tempItemC = sceneLoader.loadVoFromLibrary(vc.shopIcon);
        iconE = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempItemC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), iconE, tempItemC.composite);
        sceneLoader.getEngine().addEntity(iconE);

        if (playAni) {
            if (vc.currencyType.equals(SOFT)) {
                iconE.getComponent(TransformComponent.class).scaleX = 0.1f;
                iconE.getComponent(TransformComponent.class).scaleY = 0.1f;
                iconE.getComponent(TransformComponent.class).x = 575;
                iconE.getComponent(TransformComponent.class).y = 409;

                ActionComponent ac = new ActionComponent();
                Actions.checkInit();
                ac.dataArray.add(Actions.parallel(
                        Actions.scaleTo(1, 1, 1f, Interpolation.exp5Out),
                        Actions.moveTo(PREVIEW_X + ICON_X_RELATIVE, PREVIEW_Y + ICON_Y_RELATIVE, 1f, Interpolation.exp5Out)));
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

//
//        shopItem.getChild(PREVIEW).getChild(PREVIEW_SHOP_ICON).getEntity().
//                getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
//        shopItem.getChild(PREVIEW).getChild(PREVIEW_SHOP_ICON).getEntity().
//                getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
//        sceneLoader.getEngine().removeEntity(shopItem.getChild(PREVIEW).getChild(PREVIEW_SHOP_ICON).getEntity());
    }

    public void initPreviewWindow() {
        TransformComponent preview_tc = previewE.getComponent(TransformComponent.class);
        preview_tc.x = PREVIEW_X;
        preview_tc.y = PREVIEW_Y;
        preview_tc.scaleX = 0.9f;
        preview_tc.scaleY = 0.9f;

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
            lbl_not_enough.getComponent(ZIndexComponent.class).setZIndex(0);
            return true;
        } else {
            btn_buy.getComponent(ZIndexComponent.class).setZIndex(0);
            btn_buy.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
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
            iconE.getComponent(TransformComponent.class).y = 309;
        } else {
            iconE.getComponent(TransformComponent.class).y = 900;
        }
        iconE.getComponent(ZIndexComponent.class).setZIndex(101);
        return iconE;
    }

    public void showPreview(ShopItem vc, boolean jump, boolean justBoughtAni) {
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
        initPreviewWindow();

        if (vc.currencyType.equals(SOFT)) {
            if (vc.bought) {
                initBoughtPreviewIcon(justBoughtAni);
            } else {
                initUnknownPreviewIcon(jump);
            }
        } else {
            if (iconE != null) {
                iconE.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            }
            iconE = new ItemWrapper(sceneLoader.getRoot()).getChild(PREVIEW).getChild(vc.name).getEntity();
            iconE.getComponent(TransformComponent.class).x = ICON_X_RELATIVE;
            iconE.getComponent(TransformComponent.class).y = ICON_Y_RELATIVE;
        }
        iconE.getComponent(ZIndexComponent.class).setZIndex(101);

        if (jump) {
            previewE.getComponent(TransformComponent.class).x = PREVIEW_X;
            previewE.getComponent(TransformComponent.class).y = 460;

            ActionComponent ac = new ActionComponent();
            Actions.checkInit();
            ac.dataArray.add(Actions.moveTo(PREVIEW_X, PREVIEW_Y, 2, Interpolation.exp10Out));
            previewE.add(ac);
        } else {
            previewE.getComponent(TransformComponent.class).x = PREVIEW_X;
            previewE.getComponent(TransformComponent.class).y = PREVIEW_Y;
        }

        tagBoundingBox = new Rectangle(previewE.getComponent(TransformComponent.class).x + 40,
                30,
                648,
                previewE.getComponent(DimensionsComponent.class).height);

        ShopScreenScript.isPreviewOn = true;
    }

    public void initBuyButton(final ShopItem vc) {
        final Entity btnBuy = shopItem.getChild(PREVIEW).getChild(BTN_BUY).getEntity();
        shopItem.getChild(PREVIEW).getChild(BTN_DISABLE).getEntity().getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        shopItem.getChild(PREVIEW).getChild(BTN_ENABLE).getEntity().getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;

        if (!vc.bought && (vc.currencyType.equals(HARD) || canBuyCheck((VanityComponent) vc, btnBuy))) {
            btnBuy.getComponent(ZIndexComponent.class).setZIndex(101);
            btnBuy.getComponent(TransformComponent.class).x =
                    shopItem.getChild(PREVIEW).getChild(NOT_NUFF).getComponent(TransformComponent.class).x;
            btnBuy.getComponent(TransformComponent.class).y =
                    shopItem.getChild(PREVIEW).getChild(NOT_NUFF).getComponent(TransformComponent.class).y;
            btnBuy.getComponent(ButtonComponent.class).clearListeners();
            btnBuy.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
                @Override
                public void touchUp() {
                }

                @Override
                public void touchDown() {
                }

                @Override
                public void clicked() {
                    if (btnBuy.getComponent(ZIndexComponent.class).getZIndex() > 2 && animFinished()) {
                        vc.buyAndUse(GameScreenScript.fpc);
                        showPreview(vc, false, true);
                        if (vc.currencyType.equals(SOFT)) {
                            changeBagIcon(sceneLoader.loadVoFromLibrary(vc.shopIcon));
                        }
                        ShopScreenScript.reloadScoreLabel(GameScreenScript.fpc);
                    }
                }
            });
        }
    }

    public void initEnableButton(final ShopItem vc) {
        if (vc.bought && !vc.enabled) {
            shopItem.getChild(PREVIEW).getChild(BTN_DISABLE).getEntity().getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            shopItem.getChild(PREVIEW).getChild(BTN_BUY).getEntity().getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            Entity enableBtn = shopItem.getChild(PREVIEW).getChild(BTN_ENABLE).getEntity();
            enableBtn.getComponent(ZIndexComponent.class).setZIndex(101);
            enableBtn.getComponent(TransformComponent.class).x =
                    shopItem.getChild(PREVIEW).getChild("tag_notNuff").getComponent(TransformComponent.class).x;
            enableBtn.getComponent(TransformComponent.class).y =
                    shopItem.getChild(PREVIEW).getChild("tag_notNuff").getComponent(TransformComponent.class).y;

            enableBtn.getComponent(ButtonComponent.class).clearListeners();
            enableBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
                @Override
                public void touchUp() {
                }

                @Override
                public void touchDown() {
                }

                @Override
                public void clicked() {
                    if(animFinished()) {
                        vc.apply(GameScreenScript.fpc);
                        showPreview(vc, false, false);
                    }
                }
            });
        }
    }

    public void initDisableButton(final ShopItem vc) {
        if (vc.bought && vc.enabled) {
            shopItem.getChild(PREVIEW).getChild(BTN_ENABLE).getEntity().getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            shopItem.getChild(PREVIEW).getChild(BTN_BUY).getEntity().getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            Entity disableBtn = shopItem.getChild(PREVIEW).getChild(BTN_DISABLE).getEntity();
            disableBtn.getComponent(ZIndexComponent.class).setZIndex(101);
            disableBtn.getComponent(TransformComponent.class).x =
                    shopItem.getChild(PREVIEW).getChild("tag_notNuff").getComponent(TransformComponent.class).x;
            disableBtn.getComponent(TransformComponent.class).y =
                    shopItem.getChild(PREVIEW).getChild("tag_notNuff").getComponent(TransformComponent.class).y;


            disableBtn.getComponent(ButtonComponent.class).clearListeners();
            disableBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
                @Override
                public void touchUp() {
                }

                @Override
                public void touchDown() {
                }

                @Override
                public void clicked() {
                    if(animFinished()) {
                        vc.disable(GameScreenScript.fpc);
                        showPreview(vc, false, false);
                    }
                }
            });
        }
    }

    private void initPrevButton(final ShopItem vc) {
        btnLeft.getComponent(ButtonComponent.class).clearListeners();
        btnLeft.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
            }

            @Override
            public void touchDown() {
            }

            @Override
            public void clicked() {
                if(animFinished()) {
                    int previousIndex = allShopItems.indexOf(vc) - 1;
                    if (previousIndex >= 0) {
                        setShouldDeleteIconE();
                        showPreview(allShopItems.get(previousIndex), false, false);
                    }
                }
            }
        });
    }

    private void initNextButton(final ShopItem vc) {
        btnNext.getComponent(ButtonComponent.class).clearListeners();

        btnNext.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
            }

            @Override
            public void touchDown() {
            }

            @Override
            public void clicked() {
                if(animFinished()) {
                    int nextIndex = allShopItems.indexOf(vc) + 1;
                    if (nextIndex < allShopItems.size()) {
                        setShouldDeleteIconE();
                        showPreview(allShopItems.get(nextIndex), false, false);
                    }
                }
            }
        });
    }

    public void checkAndClose() {

        updateTagIcon();

        boolean isOutside = tagBoundingBox == null || !tagBoundingBox.contains(Gdx.input.getX(), Gdx.input.getY());
        float currentYpos = previewE.getComponent(TransformComponent.class).y;
        if (Gdx.input.isTouched() && currentYpos <= 50 || currentYpos >= 800) {
            if (isPreviewOn && isOutside) {
                ActionComponent ac = new ActionComponent();
                Actions.checkInit();
                ac.dataArray.add(Actions.moveTo(PREVIEW_X, 900, 1, Interpolation.exp10));
                previewE.add(ac);

                ActionComponent ac2 = new ActionComponent();
                ac2.dataArray.add(Actions.fadeOut(0.5f, Interpolation.sineOut));
                shadowE.add(ac2);
            }
        }

        if (previewE.getComponent(TransformComponent.class).y >= 790) {
            isPreviewOn = false;
            setShouldDeleteIconE();
            removeIconE();
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

    private void changeBagIcon(CompositeItemVO tempItemC) {
        if (vc.currencyType.equals(SOFT)) {
            Entity iconBagClone = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempItemC);
            sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), iconBagClone, tempItemC.composite);
            sceneLoader.getEngine().addEntity(iconBagClone);
            iconBagClone.getComponent(TransformComponent.class).x = itemIcons.get(vc.shopIcon).getComponent(TransformComponent.class).x;
            iconBagClone.getComponent(TransformComponent.class).y = itemIcons.get(vc.shopIcon).getComponent(TransformComponent.class).y;
            sceneLoader.getEngine().removeEntity(itemIcons.get(vc.shopIcon));
            itemIcons.put(vc.shopIcon, iconBagClone);
            iconBagClone.getComponent(ZIndexComponent.class).setZIndex(36);
        }
    }

    private boolean animFinished(){
        return previewE.getComponent(TransformComponent.class).y == PREVIEW_Y;
    }
}