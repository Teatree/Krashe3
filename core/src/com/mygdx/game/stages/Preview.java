package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.entity.componets.VanityComponent;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.game.stages.GameStage.*;
import static com.mygdx.game.stages.ShopScreenScript.*;

public class Preview {

    public static final String PREVIEW = "previewTag";
    public static final String PREVIEW_SHOP_ICON = "tag_shop_icon";
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
    public static final int ICON_X_RELATIONAL = 260;
    public static final int PREVIEW_X = 260;
    public static final int FAR_FAR_AWAY_X = 1500;
    public static final int FAR_FAR_AWAY_Y = 1500;

    private ItemWrapper shopItem;

    public Entity previewE;
    public Entity lbl_desc;
    public Entity lbl_title;
    public Entity lbl_price;
    public Entity lbl_not_enough;
    public Entity bg;
    private Entity iconE;
    private Entity btnLeft;
    private Entity btnNext;

    private Rectangle tagBoundingBox;

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

    public void initBoughtPreviewIcon(VanityComponent vc, boolean playAni) {
        if (iconE != null) {
            sceneLoader.getEngine().removeEntity(iconE);
        }
        CompositeItemVO tempItemC = sceneLoader.loadVoFromLibrary(vc.shopIcon);
        iconE = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempItemC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), iconE, tempItemC.composite);
        sceneLoader.getEngine().addEntity(iconE);

        if (playAni) {
            iconE.getComponent(TransformComponent.class).scaleX = 0.1f;
            iconE.getComponent(TransformComponent.class).scaleY = 0.1f;
            iconE.getComponent(TransformComponent.class).x = 575;
            iconE.getComponent(TransformComponent.class).y = 379;

            ActionComponent ac = new ActionComponent();
            Actions.checkInit();
            ac.dataArray.add(Actions.parallel(
                    Actions.scaleTo(1, 1, 1f, Interpolation.exp5Out),
                    Actions.moveTo(ICON_X, 309, 1f, Interpolation.exp5Out)));
            iconE.add(ac);
            iconE.getComponent(ZIndexComponent.class).setZIndex(101);
        }else{
            iconE.getComponent(TransformComponent.class).x = ICON_X;
            iconE.getComponent(TransformComponent.class).y = 309;
            iconE.getComponent(ZIndexComponent.class).setZIndex(101);
        }

        shopItem.getChild(PREVIEW).getChild(PREVIEW_SHOP_ICON).getEntity().
                getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
//        sceneLoader.getEngine().removeEntity(shopItem.getChild(PREVIEW).getChild(PREVIEW_SHOP_ICON).getEntity());
    }

    public void initPreviewWindow() {
        TransformComponent preview_tc = previewE.getComponent(TransformComponent.class);
        preview_tc.x = PREVIEW_X;
        preview_tc.y = -10;
        preview_tc.scaleX = 0.9f;
        preview_tc.scaleY = 0.9f;

    }

    public void setLabelsValues(VanityComponent vc) {
        if (vc.description != null) {
            lbl_desc.getComponent(LabelComponent.class).text.replace(0, lbl_desc.getComponent(LabelComponent.class).text.length, vc.description);
        }
        lbl_title.getComponent(LabelComponent.class).text.replace(0, lbl_title.getComponent(LabelComponent.class).text.length, vc.name);
        lbl_price.getComponent(LabelComponent.class).text.replace(0, lbl_price.getComponent(LabelComponent.class).text.length, String.valueOf(vc.cost));
    }

    public void canBuyCheck(VanityComponent vc, Entity btn_buy) {
        btn_buy.getComponent(ZIndexComponent.class).setZIndex(0);
        lbl_not_enough.getComponent(ZIndexComponent.class).setZIndex(0);
        if (vc.isAffordable()) {
            btn_buy.getComponent(ZIndexComponent.class).setZIndex(100);
            lbl_not_enough.getComponent(ZIndexComponent.class).setZIndex(0);
        } else {
            btn_buy.getComponent(ZIndexComponent.class).setZIndex(0);
            lbl_not_enough.getComponent(ZIndexComponent.class).setZIndex(100);
        }
    }

    public Entity initUnknownPreviewIcon() {
        if (iconE != null) {
            sceneLoader.getEngine().removeEntity(iconE);
        }
        CompositeItemVO tempItemC = sceneLoader.loadVoFromLibrary(ITEM_UNKNOWN);
        iconE = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempItemC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), iconE, tempItemC.composite);
        sceneLoader.getEngine().addEntity(iconE);
        lbl_not_enough.getComponent(ZIndexComponent.class).setZIndex(52);
//        iconE.getComponent(TransformComponent.class).x = ICON_X;
//        iconE.getComponent(TransformComponent.class).y = 309;
//        iconE.getComponent(ZIndexComponent.class).setZIndex(101);
        return iconE;
    }

    public void showPreview(VanityComponent vc, boolean jump, boolean justBoughtAni) {
        setLabelsValues(vc);
        if (!vc.bought) {
            initBuyButton(vc);
        } else {
            initEnableButton(vc);
            initDisableButton(vc);
        }
        initPrevButton(vc);
        initNextButton(vc);
        initPreviewWindow();

        if (vc.bought) {
            initBoughtPreviewIcon(vc, justBoughtAni);
        } else {
            shopItem.getChild(PREVIEW).getChild(PREVIEW_SHOP_ICON).getEntity().
                    getComponent(TransformComponent.class).x = ICON_X_RELATIONAL;
            initUnknownPreviewIcon();

        }
        if (jump) {
            previewE.getComponent(TransformComponent.class).x = PREVIEW_X;
            previewE.getComponent(TransformComponent.class).y = 460;

            ActionComponent ac = new ActionComponent();
            Actions.checkInit();
            ac.dataArray.add(Actions.moveTo(PREVIEW_X, 30, 2, Interpolation.exp10Out));
            previewE.add(ac);

            ActionComponent c = new ActionComponent();
            Actions.checkInit();
            c.dataArray.add(Actions.moveTo(ICON_X, 309, 2, Interpolation.exp10Out));
            iconE.add(ac);

        }else{
            previewE.getComponent(TransformComponent.class).x = PREVIEW_X;
            previewE.getComponent(TransformComponent.class).y = 30;
        }

        tagBoundingBox = new Rectangle(previewE.getComponent(TransformComponent.class).x + 40,
                30,
                648,
                previewE.getComponent(DimensionsComponent.class).height);

        ShopScreenScript.isPreviewOn = true;
    }

    public void initBuyButton(final VanityComponent vc) {
        final Entity btnBuy = shopItem.getChild(PREVIEW).getChild(BTN_BUY).getEntity();
        canBuyCheck(vc, btnBuy);
        if (!vc.bought) {
            shopItem.getChild(PREVIEW).getChild(BTN_DISABLE).getEntity().getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            shopItem.getChild(PREVIEW).getChild(BTN_ENABLE).getEntity().getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            btnBuy.getComponent(ZIndexComponent.class).setZIndex(101);
            btnBuy.getComponent(TransformComponent.class).x =
                    shopItem.getChild(PREVIEW).getChild("tag_notNuff").getComponent(TransformComponent.class).x;
            btnBuy.getComponent(TransformComponent.class).y =
                    shopItem.getChild(PREVIEW).getChild("tag_notNuff").getComponent(TransformComponent.class).y;
            btnBuy.getComponent(ButtonComponent.class).clearListeners();
            btnBuy.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
                @Override
                public void touchUp() {}
                @Override
                public void touchDown() {}

                @Override
                public void clicked() {
                    if (btnBuy.getComponent(ZIndexComponent.class).getZIndex() > 2) {
                        vc.buyAndUse(GameScreenScript.fpc);
                        showPreview(vc, false, true);
                        changeBagIcon(sceneLoader.loadVoFromLibrary(vc.shopIcon).clone(), vc);
                        ShopScreenScript.reloadScoreLabel(GameScreenScript.fpc);
                    }
                }
            });
        }
    }

    public void initEnableButton(final VanityComponent vc) {
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
                public void touchUp() {}

                @Override
                public void touchDown() {}

                @Override
                public void clicked() {
                    vc.apply(GameScreenScript.fpc);
                    showPreview(vc, false, false);
                }
            });
        }
    }

    public void initDisableButton(final VanityComponent vc) {
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
                    vc.disable(GameScreenScript.fpc);
                    showPreview(vc, false, false);
                }
            });
        }
    }

//    private void playParticleEffect() {
//        iconE.getComponent(TransformComponent.class).scaleY = 0.1f;
//        iconE.getComponent(TransformComponent.class).scaleX = 0.1f;
//        iconE.getComponent(TintComponent.class).color.a = 0;
//
//        iconE.getComponent(TransformComponent.class).x = 524;
//        iconE.getComponent(TransformComponent.class).y = 700;
//
//        ActionComponent ac = new ActionComponent();
//        Actions.checkInit();
//        ac.dataArray.add(Actions.parallel(Actions.fadeIn(3),
//                Actions.scaleTo(1.2f, 1.2f, 3),
//                Actions.moveTo(484, 407, 3)));
//        iconE.add(ac);
//
//        CompositeItemVO starBurstParticleC = sceneLoader.loadVoFromLibrary("star_burst_particle_lib");
//        Entity starBurstParticleE = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), starBurstParticleC);
//        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), starBurstParticleE, starBurstParticleC.composite);
//        sceneLoader.getEngine().addEntity(starBurstParticleE);
//
////        ParticleComponent pc = new ParticleComponent();
////        pc.particleEffect = starBurstParticleC.composite.;
////        pc.particleEffect.setDuration(101);
////        starBurstParticleE.add(pc);
//
//        TransformComponent tcParticles = starBurstParticleE.getComponent(TransformComponent.class);
//        tcParticles.x = 534;
//        tcParticles.y = 477;
//    }

    private void initPrevButton(final VanityComponent vc) {
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
                int previousIndex = GameScreenScript.fpc.vanities.indexOf(vc) - 1;
                if (previousIndex >= 0) {
                    showPreview(GameScreenScript.fpc.vanities.get(previousIndex), false, false);
                }
            }
        });
    }

    private void initNextButton(final VanityComponent vc) {
        btnNext.getComponent(ButtonComponent.class).clearListeners();

        btnNext.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {}

            @Override
            public void touchDown() {
            }

            @Override
            public void clicked() {
                int nextIndex = GameScreenScript.fpc.vanities.indexOf(vc) + 1;
                if (nextIndex < GameScreenScript.fpc.vanities.size()) {
                    showPreview(GameScreenScript.fpc.vanities.get(nextIndex), false, false);
                }
            }
        });
    }

    public void checkAndClose() {
        boolean isOutside = tagBoundingBox == null || !tagBoundingBox.contains(Gdx.input.getX(), Gdx.input.getY());
        if (Gdx.input.justTouched() && isPreviewOn && isOutside) {

            ActionComponent ac = new ActionComponent();
            Actions.checkInit();
            ac.dataArray.add(Actions.moveTo(PREVIEW_X, 900, 1, Interpolation.exp10));
            previewE.add(ac);

            if (iconE.getComponent(TransformComponent.class) != null) {
//                ActionComponent c = new ActionComponent();
//                Actions.checkInit();
//                c.dataArray.add(Actions.moveTo(ICON_X, 900, 1, Interpolation.exp10));
//                iconE.add(c);
//                iconE.getComponent(TransformComponent.class).x = -1500;
//                sceneLoader.getEngine().removeEntity(iconE);
            }
        }
        if (previewE.getComponent(TransformComponent.class).y >= 890) {
            isPreviewOn = false;
//            iconE.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
//            iconE.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
            sceneLoader.getEngine().removeEntity(iconE);
        }
//        if (previewE.getComponent(TransformComponent.class).y >= 590) {
//            iconE.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
//            iconE.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
//            sceneLoader.getEngine().removeEntity(iconE);
//        }
    }

    private void changeBagIcon(CompositeItemVO tempItemC, VanityComponent vc) {
        Entity iconBagClone = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempItemC.clone());
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), iconBagClone, tempItemC.composite);
        sceneLoader.getEngine().addEntity(iconBagClone);
        iconBagClone.getComponent(ZIndexComponent.class).setZIndex(20);
        iconBagClone.getComponent(TransformComponent.class).x = itemIcons.get(vc.shopIcon).getComponent(TransformComponent.class).x;
        iconBagClone.getComponent(TransformComponent.class).y = itemIcons.get(vc.shopIcon).getComponent(TransformComponent.class).y;
        sceneLoader.getEngine().removeEntity(itemIcons.get(vc.shopIcon));
        itemIcons.put(vc.shopIcon, iconBagClone);
    }
}