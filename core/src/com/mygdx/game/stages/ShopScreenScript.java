package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entity.componets.BugComponent;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.entity.componets.VanityComponent;
import com.mygdx.game.utils.SaveMngr;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import javax.xml.transform.TransformerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Teatree on 7/25/2015.
 */
public class ShopScreenScript implements IScript {

    private GameStage stage;
    private ItemWrapper shopItem;
    public Entity scoreLbl;
    public Entity touchZone;
    public LabelComponent lc;

    public Vector2 tempGdx = new Vector2();
    public boolean isGdxWritten;
    public List<Entity> bags = new ArrayList<>();
    public List<Entity> itemIcons = new ArrayList<>();
    public ButtonComponent touchZoneBtn;
    private boolean isHighlighted;
    float stopVelocity;

    // FIELD!!!! MUHAHAHAHHAHAHAHAHHA!!!
    private Entity preview_e;
    private Entity lbl_score_lbl;
    private Entity lbl_desc;
    private Entity lbl_title;
    private Entity lbl_price;
    private Entity btn_back_shop;
    private Entity btn_left;
    private Entity btn_right;
    private Entity btn_buy;
    private Entity preview_icon;
    private Entity bg;
    private boolean isPreviewOn;
    private TransformComponent tc1;
    private TintComponent tci1;


//    private int spawnCounter = 0;

    public ShopScreenScript(GameStage stage) {
        this.stage = stage;
    }

    @Override
    public void init(Entity item) {
        shopItem = new ItemWrapper(item);

        stage.sceneLoader.addComponentsByTagName("button", ButtonComponent.class);


        addBackButtonPlease();

        scoreLbl = shopItem.getChild("score_lbl").getEntity();
        lc = scoreLbl.getComponent(LabelComponent.class);

        touchZone = shopItem.getChild("touchZone_scroll").getEntity();

        touchZoneBtn = touchZone.getComponent(ButtonComponent.class);

        lbl_score_lbl = shopItem.getChild("preview").getChild("preview_score_lbl").getEntity();
        lbl_desc = shopItem.getChild("preview").getChild("lbl_desc").getEntity();
        lbl_title = shopItem.getChild("preview").getChild("lbl_item_name").getEntity();
        lbl_price = shopItem.getChild("preview").getChild("lbl_price").getEntity();
        btn_back_shop = shopItem.getChild("preview").getChild("btn_back_shop").getEntity();
        btn_left = shopItem.getChild("preview").getChild("btn_left").getEntity();
        btn_right = shopItem.getChild("preview").getChild("btn_right").getEntity();
        btn_buy = shopItem.getChild("preview").getChild("btn_buy").getEntity();
        preview_icon = shopItem.getChild("preview").getChild("btn_back_shop").getEntity();
        bg = shopItem.getChild("preview").getChild("img_bg_show_case").getEntity();

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
        getAllAllVanities();
    }

    private void getAllAllVanities(){
//        List<VanityComponent> vanityComponentList = ;
        int x = 173;
        int y = 289;

        for (final VanityComponent vc : GameScreenScript.fpc.vanities) {
            CompositeItemVO tempC = GameStage.sceneLoader.loadVoFromLibrary("btn_shop_icon_lib").clone();
            final Entity bagEntity = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempC);
            GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), bagEntity, tempC.composite);
            GameStage.sceneLoader.getEngine().addEntity(bagEntity);

            Entity itemIcon;
            if(!vc.bought) {
                CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary("item_unknown_n").clone();
                itemIcon = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
                GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), itemIcon, tempItemC.composite);
                GameStage.sceneLoader.getEngine().addEntity(itemIcon);
            }else{
                CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary(vc.shopIcon).clone();
                itemIcon = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
                GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), itemIcon, tempItemC.composite);
                GameStage.sceneLoader.getEngine().addEntity(itemIcon);
            }

            TransformComponent tc = bagEntity.getComponent(TransformComponent.class);
            tc.x = x;
            tc.y = y;

            itemIcon.add(new ButtonComponent());
            shopItem.getChild("btn_shop_icon_lib").addChild(itemIcon);
            TransformComponent tcb = itemIcon.getComponent(TransformComponent.class);
            tcb.x = tc.x;
            tcb.y = tc.y;

            bags.add(bagEntity);
            itemIcons.add(itemIcon);


            bagEntity.add(new ButtonComponent());



            bagEntity.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
                @Override
                public void touchUp() {

                }

                @Override
                public void touchDown() {
                }

                @Override
                public void clicked() {
//                    System.out.println(vc.icon);
                    if(!isPreviewOn) {
                        lbl_score_lbl.getComponent(LabelComponent.class).text.replace(0, lbl_score_lbl.getComponent(LabelComponent.class).text.length, String.valueOf(GameScreenScript.fpc.totalScore));
                        if (vc.description != null) {
                            lbl_desc.getComponent(LabelComponent.class).text.replace(0, lbl_desc.getComponent(LabelComponent.class).text.length, vc.description);
                        }
                        lbl_title.getComponent(LabelComponent.class).text.replace(0, lbl_title.getComponent(LabelComponent.class).text.length, vc.name);
                        lbl_price.getComponent(LabelComponent.class).text.replace(0, lbl_price.getComponent(LabelComponent.class).text.length, String.valueOf(vc.cost));
                        btn_buy.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener(){
                            @Override
                            public void touchUp() {
                            }

                            @Override
                            public void touchDown() {
                            }

                            @Override
                            public void clicked() {
                                vc.buyAndUse(GameScreenScript.fpc);

//                                shopItem.getChild("preview").addChild("")
                            }
                        });


                        preview_e = shopItem.getChild("preview").getEntity();
                        TransformComponent preview_tc = preview_e.getComponent(TransformComponent.class);
                        preview_tc.x = -30;
                        preview_tc.y = -30;

                        Entity iconE;
                        if(!vc.bought) {
                            CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary("item_unknown_n").clone();
                            iconE = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
                            GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), iconE, tempItemC.composite);
                            GameStage.sceneLoader.getEngine().addEntity(iconE);
                        }else{
                            CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary(vc.shopIcon).clone();
                            iconE = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
                            GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), iconE, tempItemC.composite);
                            GameStage.sceneLoader.getEngine().addEntity(iconE);
                        }
                        iconE.getComponent(ZIndexComponent.class).setZIndex(100);
                        shopItem.getChild("btn_shop_icon_lib").addChild(iconE);
                        tc1 = iconE.getComponent(TransformComponent.class);
                        tc1.x = 484;
                        tc1.y = 407;
                        tci1 = iconE.getComponent(TintComponent.class);

                        isPreviewOn = true;
//                    vc.apply(GameScreenScript.fpc);
                    }
                }
            });
            x += 250;

        }
    }

    private void addBackButtonPlease(){
        Entity btnBack = shopItem.getChild("btn_back").getEntity();
        final LayerMapComponent lc = ComponentRetriever.get(btnBack, LayerMapComponent.class);
        btnBack.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
                lc.getLayer("normal").isVisible = true;
                lc.getLayer("Default").isVisible = false;
                lc.getLayer("pressed").isVisible = false;
            }

            @Override
            public void touchDown() {
                lc.getLayer("normal").isVisible = false;
                lc.getLayer("Default").isVisible = false;
                lc.getLayer("pressed").isVisible = true;
            }

            @Override
            public void clicked() {
                if(!isPreviewOn) {
                    stage.initMenu();
                }
            }
        });
    }

    @Override
    public void dispose() {

    }

    @Override
    public void act(float delta) {
        if(!isPreviewOn) {
            if (touchZoneBtn.isTouched) {
                if (!isGdxWritten) {
                    tempGdx.x = Gdx.input.getX();
                    isGdxWritten = true;
                }
                if (tempGdx.x > Gdx.input.getX()) {
                    int i = 0;
                    while (i < bags.size()) {
                        bags.get(i).getComponent(TransformComponent.class).x -= (tempGdx.x - Gdx.input.getX()) / 15;
                        itemIcons.get(i).getComponent(TransformComponent.class).x = bags.get(i).getComponent(TransformComponent.class).x;
                        i++;
                    }
                    stopVelocity = (Gdx.input.getX() - tempGdx.x) / 15;
                    tempGdx.x -= (tempGdx.x - Gdx.input.getX()) / 15;
                    isHighlighted = true;
                }
                if (tempGdx.x < Gdx.input.getX()) {
                    int i = 0;
                    while (i < bags.size()) {
                        bags.get(i).getComponent(TransformComponent.class).x += (Gdx.input.getX() - tempGdx.x) / 15;
                        itemIcons.get(i).getComponent(TransformComponent.class).x = bags.get(i).getComponent(TransformComponent.class).x;
                        i++;
                    }
                    stopVelocity = (Gdx.input.getX() - tempGdx.x) / 15;
                    tempGdx.x += (Gdx.input.getX() - tempGdx.x) / 15;
                    isHighlighted = true;
                }
            } else {
                isGdxWritten = false;
                if (stopVelocity != 0) {
                    int i = 0;
                    while (i < bags.size()) {
                        bags.get(i).getComponent(TransformComponent.class).x += stopVelocity;
                        itemIcons.get(i).getComponent(TransformComponent.class).x = bags.get(i).getComponent(TransformComponent.class).x;
                        i++;
                    }
                    stopVelocity -= stopVelocity / 20;
                } else {
                    isHighlighted = false;
                }
            }
//        stage.sceneLoader.getEngine().update(Gdx.graphics.getDeltaTime());

        }
        lc.text.replace(0, lc.text.length(), String.valueOf(GameScreenScript.fpc.totalScore));
        fadePreview();
    }

    private void fadePreview() {
        TintComponent ticLblScore = lbl_score_lbl.getComponent(TintComponent.class);
        TintComponent ticLblDesc = lbl_desc.getComponent(TintComponent.class);
        TintComponent ticTitle = lbl_title.getComponent(TintComponent.class);
        TintComponent ticLblPrice = lbl_price.getComponent(TintComponent.class);
        TintComponent ticBackShop = btn_back_shop.getComponent(TintComponent.class);
        TintComponent ticLeftBtn = btn_left.getComponent(TintComponent.class);
        TintComponent ticRightBtn = btn_right.getComponent(TintComponent.class);
        TintComponent ticBuyBtn = btn_buy.getComponent(TintComponent.class);
        TintComponent ticPreviewIcon = preview_icon.getComponent(TintComponent.class);
        TintComponent ticBg = bg.getComponent(TintComponent.class);

        boolean appear = (ticBackShop.color.a < 1 && isPreviewOn) ||
                (ticBackShop.color.a > 0 && !isPreviewOn);

        int fadeCoefficient = isPreviewOn ? 1 : -1;

        if (appear) {
            ticLblScore.color.a += fadeCoefficient * 0.1f;
            ticLblDesc.color.a += fadeCoefficient * 0.1f;
            ticTitle.color.a += fadeCoefficient * 0.1f;
            ticLblPrice.color.a += fadeCoefficient * 0.1f;
            ticBackShop.color.a += fadeCoefficient * 0.1f;
            ticLeftBtn.color.a += fadeCoefficient * 0.1f;
            ticRightBtn.color.a += fadeCoefficient * 0.1f;
            ticBuyBtn.color.a += fadeCoefficient * 0.1f;
            ticPreviewIcon.color.a += fadeCoefficient * 0.1f;
            ticBg.color.a += fadeCoefficient * 0.1f;
            if(tci1!=null) {
                tci1.color.a += fadeCoefficient * 0.1f;
            }
        }

        if (!isPreviewOn && ticBackShop.color.a <= 0 && preview_e!=null) {
            TransformComponent previewTc = preview_e.getComponent(TransformComponent.class);
            previewTc.x = -1500;
            tc1.x = -1500;
        }
    }

}
