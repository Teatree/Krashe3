package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.mygdx.game.entity.componets.VanityComponent;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.game.stages.GameStage.*;
import static com.mygdx.game.stages.ShopScreenScript.*;

public class Preview {

    private ItemWrapper shopItem;

    public Entity preview_e;
    public Entity lbl_score_lbl;
    public Entity lbl_desc;
    public Entity lbl_title;
    public Entity lbl_price;
    public Entity btn_back_shop;
    public Entity btn_left;
    public Entity btn_right;
    public Entity btn_buy;
    public Entity lbl_not_enough;
    public Entity preview_icon;
    public Entity preview_n;
    public Entity bg;
    Entity iconE;

    public TransformComponent tcPreviewIcon;
    public TransformComponent tcPreviewWindow;

    public Preview(ItemWrapper shopItem) {
        this.shopItem = shopItem;
        preview_n = shopItem.getChild("preview").getEntity();
        lbl_score_lbl = shopItem.getChild("preview").getChild("preview_score_lbl").getEntity();
        lbl_desc = shopItem.getChild("preview").getChild("lbl_desc").getEntity();
        lbl_title = shopItem.getChild("preview").getChild("lbl_item_name").getEntity();
        lbl_price = shopItem.getChild("preview").getChild("lbl_price").getEntity();
        btn_back_shop = shopItem.getChild("preview").getChild("btn_back_shop").getEntity();
        btn_left = shopItem.getChild("preview").getChild("btn_left").getEntity();
        btn_right = shopItem.getChild("preview").getChild("btn_right").getEntity();
        btn_buy = shopItem.getChild("preview").getChild("btn_buy").getEntity();
        preview_icon = shopItem.getChild("preview").getChild("preview_shop_icon").getEntity();
        bg = shopItem.getChild("preview").getChild("img_bg_show_case").getEntity();
        lbl_not_enough = shopItem.getChild("preview").getChild("lbl_not_enough").getEntity();

        preview_n.getComponent(ZIndexComponent.class).setZIndex(51);
        btn_back_shop.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener(){
            @Override
            public void touchUp() {
            }

            @Override
            public void touchDown() {
            }

            @Override
            public void clicked() {
                isPreviewOn = false;
            }
        });
    }

    public void fadePreview() {
        NodeComponent nc = preview_n.getComponent(NodeComponent.class);
        TintComponent tcp = preview_n.getComponent(TintComponent.class);

        boolean appear = (tcp.color.a < 1 && isPreviewOn) ||
                (tcp.color.a > 0 && !isPreviewOn);

        int fadeCoefficient = isPreviewOn ? 1 : -1;

        if (appear) {
            tcp.color.a += fadeCoefficient * 0.1f;
            fadeChildren(nc, fadeCoefficient);
        }

        hidePreview(tcp);
    }

    private void fadeChildren(NodeComponent nc, int fadeCoefficient) {
        if (nc != null && nc.children != null && nc.children.size != 0) {
            for (Entity e : nc.children) {
                TintComponent tc = e.getComponent(TintComponent.class);
                tc.color.a += fadeCoefficient * 0.1f;
                fadeChildren(e.getComponent(NodeComponent.class), fadeCoefficient);
            }
        }
    }

    private void hidePreview(TintComponent ticBackShop) {
        if (!isPreviewOn && ticBackShop.color.a <= 0 && preview_e != null) {
            TransformComponent previewTc = preview_e.getComponent(TransformComponent.class);
            previewTc.x = -1500;
            if (tcPreviewIcon != null) {
                tcPreviewIcon.x = -1500;
            }
            if (tcPreviewWindow != null) {
                tcPreviewWindow.x = -1500;
            }
        }
    }

    public void initBoughtPreviewIcon(VanityComponent vc) {
        CompositeItemVO tempItemC = sceneLoader.loadVoFromLibrary(vc.shopIcon).clone();
        Entity iconE = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempItemC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), iconE, tempItemC.composite);
        sceneLoader.getEngine().addEntity(iconE);
        iconE.getComponent(ZIndexComponent.class).setZIndex(100);
        shopItem.getChild("preview").addChild(iconE);
        tcPreviewIcon = iconE.getComponent(TransformComponent.class);
        tcPreviewIcon.x = 484;
        tcPreviewIcon.y = 407;
        tcPreviewWindow.x = -1500;
    }

    public void initPreviewWindow() {
        preview_e = shopItem.getChild("preview").getEntity();
        TransformComponent preview_tc = preview_e.getComponent(TransformComponent.class);
        preview_tc.x = -30;
        preview_tc.y = -30;
    }

    public void setLabelsValues(VanityComponent vc) {
        lbl_score_lbl.getComponent(LabelComponent.class).text.replace(0, lbl_score_lbl.getComponent(LabelComponent.class).text.length, String.valueOf(GameScreenScript.fpc.totalScore));
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
        Entity iconE;
        CompositeItemVO tempItemC = sceneLoader.loadVoFromLibrary("item_unknown_n").clone();
        iconE = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempItemC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), iconE, tempItemC.composite);
        sceneLoader.getEngine().addEntity(iconE);
        lbl_not_enough.getComponent(ZIndexComponent.class).setZIndex(52);
        return iconE;
    }

    public void showPreview(VanityComponent vc) {
        canBuyCheck(vc);

        if (!ShopScreenScript.isPreviewOn) {
            setLabelsValues(vc);
            initBuyButton(vc);
            initPreviewWindow();

            tcPreviewWindow = shopItem.getChild("preview").getChild("preview_shop_icon").getEntity().
                    getComponent(TransformComponent.class);

            if (vc.bought) {
                initBoughtPreviewIcon(vc);
            } else {
                tcPreviewWindow.x = 484;
            }
            ShopScreenScript.isPreviewOn = true;
        }
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
                        CompositeItemVO tempItemC = sceneLoader.loadVoFromLibrary(vc.shopIcon).clone();
                        iconE = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempItemC);
                        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), iconE, tempItemC.composite);
                        sceneLoader.getEngine().addEntity(iconE);

                        changeBagIcon(tempItemC);

                        lbl_score_lbl.getComponent(LabelComponent.class).text.replace(0, lbl_score_lbl.getComponent(LabelComponent.class).text.length, String.valueOf(GameScreenScript.fpc.totalScore));
                    }

                    iconE.getComponent(ZIndexComponent.class).setZIndex(100);
                    shopItem.getChild("preview").addChild(iconE);

                    tcPreviewIcon = iconE.getComponent(TransformComponent.class);
                    tcPreviewIcon.x = 484;
                    tcPreviewIcon.y = 407;
                    tcPreviewWindow.x = -1500;
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
                itemIcons.put(vc.shopIcon,iconBagClone);
            }
        });
    }
}
