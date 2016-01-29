package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;
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
import static com.mygdx.game.utils.Utils.*;

public class Preview {

//    public static final String PREVIEW = "preview";
//    public static final String PREVIEW_SHOP_ICON = "preview_shop_icon";
//    public static final String ITEM_UNKNOWN = "item_unknown_n";
//    public static final String BTN_RIGHT = "btn_right";
//    public static final String BTN_LEFT = "btn_left";
//    public static final String LBL_NOT_ENOUGH = "lbl_not_enough";
//    public static final String IMG_BG_SHOW_CASE = "img_bg_show_case";
//    public static final String BTN_BUY = "btn_buy";
//    public static final String LBL_PRICE = "lbl_price";
//    public static final String LBL_ITEM_NAME = "lbl_item_name";
//    public static final String LBL_DESC = "lbl_desc";

    public static final String PREVIEW = "previewTag";
    public static final String PREVIEW_SHOP_ICON = "tag_shop_icon";
    public static final String ITEM_UNKNOWN = "item_unknown_n";
    public static final String BTN_RIGHT = "tag_right_btn";
    public static final String BTN_LEFT = "tag_left_btn";
    public static final String LBL_NOT_ENOUGH = "tag_lbl_not_enough";
    public static final String IMG_BG_SHOW_CASE = "tag_img_bg_show_case";
    public static final String BTN_BUY = "tag_btn_buy";
    public static final String LBL_PRICE = "tag_lbl_price";
    public static final String LBL_ITEM_NAME = "tag_lbl_item_name";
    public static final String LBL_DESC = "tag_lbl_desc";

    private ItemWrapper shopItem;

    public Entity previewE;
//    public Entity lbl_score_lbl;
    public Entity lbl_desc;
    public Entity lbl_title;
    public Entity lbl_price;
//    public Entity btn_back_shop;
//    public Entity btn_right;
    public Entity btn_buy;
    public Entity lbl_not_enough;
//    public Entity preview_icon;
//    public Entity preview_n;
    public Entity bg;
    private Entity iconE;
    private Entity btnLeft;
    private Entity btnNext;

//    public TransformComponent tcPreviewIcon;
    public TransformComponent tcPreviewUnknowIcon;
    private Rectangle boundingBox;

    ParticleEffectPool bombEffectPool;
    Array<ParticleEffectPool.PooledEffect> effects = new Array();

    public Preview(ItemWrapper shopItem) {
        this.shopItem = shopItem;
        previewE = shopItem.getChild(PREVIEW).getEntity();
//        lbl_score_lbl = shopItem.getChild(PREVIEW).getChild("preview_score_lbl").getEntity();
        lbl_desc = shopItem.getChild(PREVIEW).getChild(LBL_DESC).getEntity();
        lbl_title = shopItem.getChild(PREVIEW).getChild(LBL_ITEM_NAME).getEntity();
        lbl_price = shopItem.getChild(PREVIEW).getChild(LBL_PRICE).getEntity();
//        btn_back_shop = shopItem.getChild(PREVIEW).getChild("btn_back_shop").getEntity();
//        btn_right = shopItem.getChild(PREVIEW).getChild(BTN_RIGHT).getEntity();
        btn_buy = shopItem.getChild(PREVIEW).getChild(BTN_BUY).getEntity();
//        preview_icon = shopItem.getChild(PREVIEW).getChild(PREVIEW_SHOP_ICON).getEntity();
        bg = shopItem.getChild(PREVIEW).getChild(IMG_BG_SHOW_CASE).getEntity();
        lbl_not_enough = shopItem.getChild(PREVIEW).getChild(LBL_NOT_ENOUGH).getEntity();
        btnLeft = shopItem.getChild(PREVIEW).getChild(BTN_LEFT).getEntity();

        btnNext = shopItem.getChild(PREVIEW).getChild(BTN_RIGHT).getEntity();
        btnNext.getComponent(TransformComponent.class).rotation = -180;
        btnNext.getComponent(TransformComponent.class).x += btnNext.getComponent(DimensionsComponent.class).width;
        btnNext.getComponent(TransformComponent.class).y += btnNext.getComponent(DimensionsComponent.class).height;

        previewE.getComponent(ZIndexComponent.class).setZIndex(51);
//        btn_back_shop.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
//            @Override
//            public void touchUp() {
//            }
//
//            @Override
//            public void touchDown() {
//            }
//
//            @Override
//            public void clicked() {
//                isPreviewOn = false;
//            }
//        });

//        TextureAtlas particleAtlas; //<-load some atlas with your particle assets in
//
//        ParticleEffect bombEffect = new ParticleEffect();
//        bombEffect.load(Gdx.files.internal("particles/bomb.p"));
//
//        bombEffectPool = new ParticleEffectPool(bombEffect, 1, 2);
//
//        ParticleEffectPool.PooledEffect effect = bombEffectPool.obtain();
//        effect.setPosition(x, y);
//        effects.add(effect);


    }

    public void fadePreview() {
        NodeComponent nc = previewE.getComponent(NodeComponent.class);
        TintComponent tcp = previewE.getComponent(TintComponent.class);

        boolean appear = (tcp.color.a < 1 && isPreviewOn) ||
                (tcp.color.a > 0 && !isPreviewOn);

        int fadeCoefficient = isPreviewOn ? 1 : -1;

        if (appear) {
            tcp.color.a += fadeCoefficient * 0.1f;
            fadeChildren(nc, fadeCoefficient);
        }

//        hidePreview(tcp);
    }

    private void hidePreview(TintComponent ticBackShop) {
        if (!isPreviewOn && ticBackShop.color.a <= 0 && previewE != null) {
            TransformComponent previewTc = previewE.getComponent(TransformComponent.class);
            previewTc.x = -1500;
            if (iconE.getComponent(TransformComponent.class) != null) {
                iconE.getComponent(TransformComponent.class).x = -1500;
            }
            if (tcPreviewUnknowIcon != null) {
                tcPreviewUnknowIcon.x = -1500;
            }
        }
    }

    public void initBoughtPreviewIcon(VanityComponent vc) {
        CompositeItemVO tempItemC = sceneLoader.loadVoFromLibrary(vc.shopIcon).clone();
        if (iconE != null) {
            sceneLoader.getEngine().removeEntity(iconE);
        }
        iconE = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempItemC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), iconE, tempItemC.composite);
        sceneLoader.getEngine().addEntity(iconE);
        iconE.getComponent(ZIndexComponent.class).setZIndex(100);
        shopItem.getChild(PREVIEW).addChild(iconE);
        if (iconE.getComponent(ActionComponent.class) == null) {
            iconE.getComponent(TransformComponent.class).x = 290;
            iconE.getComponent(TransformComponent.class).y = 407;
        }
        tcPreviewUnknowIcon.x = -1500;
    }

    public void initPreviewWindow() {
//        previewE = shopItem.getChild(PREVIEW).getEntity();
        TransformComponent preview_tc = previewE.getComponent(TransformComponent.class);
        preview_tc.x = 260;
        preview_tc.y = -10;
        preview_tc.scaleX = 0.9f;
        preview_tc.scaleY = 0.9f;

    }

    public void setLabelsValues(VanityComponent vc) {
//        lbl_score_lbl.getComponent(LabelComponent.class).text.replace(0, lbl_score_lbl.getComponent(LabelComponent.class).text.length, String.valueOf(GameScreenScript.fpc.totalScore));
        if (vc.description != null) {
            lbl_desc.getComponent(LabelComponent.class).text.replace(0, lbl_desc.getComponent(LabelComponent.class).text.length, vc.description);
        }
        lbl_title.getComponent(LabelComponent.class).text.replace(0, lbl_title.getComponent(LabelComponent.class).text.length, vc.name);
        lbl_price.getComponent(LabelComponent.class).text.replace(0, lbl_price.getComponent(LabelComponent.class).text.length, String.valueOf(vc.cost));
    }

    public void canBuyCheck(VanityComponent vc) {
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
        CompositeItemVO tempItemC = sceneLoader.loadVoFromLibrary(ITEM_UNKNOWN).clone();
        iconE = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempItemC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), iconE, tempItemC.composite);
        sceneLoader.getEngine().addEntity(iconE);
        lbl_not_enough.getComponent(ZIndexComponent.class).setZIndex(52);
        return iconE;
    }

    public void showPreview(VanityComponent vc, boolean jump) {
        canBuyCheck(vc);

        setLabelsValues(vc);
        initBuyButton(vc);
        initPrevButton(vc);
        initNextButton(vc);
        initPreviewWindow();

        if (jump) {
            previewE.getComponent(TransformComponent.class).x = 260;
            previewE.getComponent(TransformComponent.class).y = 460;

            ActionComponent ac = new ActionComponent();
            Actions.checkInit();
            ac.dataArray.add(Actions.moveTo(260, -10, 2, Interpolation.bounceOut));
            previewE.add(ac);
        }

        tcPreviewUnknowIcon = shopItem.getChild(PREVIEW).getChild(PREVIEW_SHOP_ICON).getEntity().
                getComponent(TransformComponent.class);
        if (vc.bought) {
            initBoughtPreviewIcon(vc);
        } else {
            shopItem.getChild(PREVIEW).getChild(PREVIEW_SHOP_ICON).getEntity().
                    getComponent(TransformComponent.class).x = 290;
            initUnknownPreviewIcon();
        }

        boundingBox = new Rectangle(previewE.getComponent(TransformComponent.class).x,
                30,
                previewE.getComponent(DimensionsComponent.class).width, //+ 2 * btnNext.getComponent(DimensionsComponent.class).width,
                previewE.getComponent(DimensionsComponent.class).height);

        ShopScreenScript.isPreviewOn = true;
    }

    public void initBuyButton(final VanityComponent vc) {
        btn_buy.getComponent(ButtonComponent.class).clearListeners();
        btn_buy.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
            }

            @Override
            public void touchDown() {
            }

            @Override
            public void clicked() {
                if (btn_buy.getComponent(ZIndexComponent.class).getZIndex() > 2) {
                    vc.buyAndUse(GameScreenScript.fpc);

                    initPreviewWindow();

                    if (!vc.bought) {
                        iconE = initUnknownPreviewIcon();
                    } else {
                        initBoughtPreviewIcon(vc);
                        changeBagIcon(sceneLoader.loadVoFromLibrary(vc.shopIcon).clone());
//                        playParticleEffect();

//                        lbl_score_lbl.getComponent(LabelComponent.class).text.replace(0,
//                                lbl_score_lbl.getComponent(LabelComponent.class).text.length,
//                                String.valueOf(GameScreenScript.fpc.totalScore));
                    }

                    iconE.getComponent(ZIndexComponent.class).setZIndex(100);
                    shopItem.getChild(PREVIEW).addChild(iconE);

//                    tcPreviewIcon = iconE.getComponent(TransformComponent.class);
                    iconE.getComponent(TransformComponent.class).x = 290;
                    iconE.getComponent(TransformComponent.class).y = 407;
                    tcPreviewUnknowIcon.x = -1500;
                }
            }

            private void changeBagIcon(CompositeItemVO tempItemC) {
                Entity iconBagClone = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempItemC.clone());
                sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), iconBagClone, tempItemC.composite);
                sceneLoader.getEngine().addEntity(iconBagClone);
                iconBagClone.getComponent(ZIndexComponent.class).setZIndex(20);
                iconBagClone.getComponent(TransformComponent.class).x = itemIcons.get(vc.shopIcon).getComponent(TransformComponent.class).x;
                iconBagClone.getComponent(TransformComponent.class).y = itemIcons.get(vc.shopIcon).getComponent(TransformComponent.class).y;
                sceneLoader.getEngine().removeEntity(itemIcons.get(vc.shopIcon));
                itemIcons.put(vc.shopIcon, iconBagClone);
            }
        });
    }

    private void playParticleEffect() {

        iconE.getComponent(TransformComponent.class).scaleY = 0.1f;
        iconE.getComponent(TransformComponent.class).scaleX = 0.1f;
        iconE.getComponent(TintComponent.class).color.a = 0;

        iconE.getComponent(TransformComponent.class).x = 534;
        iconE.getComponent(TransformComponent.class).y = 700;

        ActionComponent ac = new ActionComponent();
        Actions.checkInit();
        ac.dataArray.add(Actions.parallel(Actions.fadeIn(3),
                Actions.scaleTo(1.2f, 1.2f, 3),
                Actions.moveTo(484, 407, 3)));
        iconE.add(ac);

        CompositeItemVO starBurstParticleC = sceneLoader.loadVoFromLibrary("star_burst_particle_lib");
        Entity starBurstParticleE = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), starBurstParticleC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), starBurstParticleE, starBurstParticleC.composite);
        sceneLoader.getEngine().addEntity(starBurstParticleE);

//        ParticleComponent pc = new ParticleComponent();
//        pc.particleEffect = starBurstParticleC.composite.;
//        pc.particleEffect.setDuration(101);
//        starBurstParticleE.add(pc);

        TransformComponent tcParticles = starBurstParticleE.getComponent(TransformComponent.class);
        tcParticles.x = 534;
        tcParticles.y = 477;
    }

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
                    showPreview(GameScreenScript.fpc.vanities.get(previousIndex), false);
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
            public void touchDown() {}

            @Override
            public void clicked() {
                int nextIndex = GameScreenScript.fpc.vanities.indexOf(vc) + 1;
                if (nextIndex < GameScreenScript.fpc.vanities.size()) {
                    showPreview(GameScreenScript.fpc.vanities.get(nextIndex), false);
                }
            }
        });
    }

    public void checkAndClose(){
        boolean isOutside = boundingBox != null ? !boundingBox.contains(Gdx.input.getX(), Gdx.input.getY()) : true;
        if (Gdx.input.justTouched() && isPreviewOn && isOutside){
            TransformComponent previewTc = previewE.getComponent(TransformComponent.class);
            previewTc.x = -1500;
            if (iconE.getComponent(TransformComponent.class) != null) {
                iconE.getComponent(TransformComponent.class).x = -1500;
            }
            isPreviewOn = false;
        }
    }
}