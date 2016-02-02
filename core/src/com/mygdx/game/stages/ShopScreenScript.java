package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.entity.componets.VanityComponent;
import com.mygdx.game.system.ParticleLifespanSystem;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.*;

import static com.mygdx.game.stages.GameScreenScript.fpc;

public class ShopScreenScript implements IScript {

    private GameStage stage;
    private ItemWrapper shopItem;
    public static Entity scoreLbl;
    public Entity touchZone;
    public LabelComponent lc;

    public Vector2 tempGdx = new Vector2();
    public boolean isGdxWritten;
    public List<Entity> bags = new ArrayList<>();
    public static final Map<String, Entity> itemIcons = new LinkedHashMap<>();
    public ButtonComponent touchZoneBtn;
    float stopVelocity;
    public static boolean isPreviewOn;
    public static boolean canOpenPreview = true;

    public Preview preview;

    public ShopScreenScript(GameStage stage) {
        this.stage = stage;
    }

    @Override
    public void init(Entity item) {
        GameStage.sceneLoader.addComponentsByTagName("button", ButtonComponent.class);
        shopItem = new ItemWrapper(item);
        preview = new Preview(shopItem);
        GameStage.sceneLoader.getEngine().addSystem(new ParticleLifespanSystem());

        addBackButtonPlease();
        scoreLbl = shopItem.getChild("score_lbl").getEntity();
        lc = scoreLbl.getComponent(LabelComponent.class);
        touchZone = shopItem.getChild("touchZone_scroll").getEntity();
        touchZoneBtn = touchZone.getComponent(ButtonComponent.class);

        getAllAllVanities();
    }

    private void getAllAllVanities() {
        int x = 173;
        int y = 289;

        for (final VanityComponent vc : GameScreenScript.fpc.vanities) {
            CompositeItemVO tempC = GameStage.sceneLoader.loadVoFromLibrary("btn_shop_icon_lib").clone();
            final Entity bagEntity = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempC);
            GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), bagEntity, tempC.composite);
            GameStage.sceneLoader.getEngine().addEntity(bagEntity);

            Entity itemIcon;
            if (!vc.bought) {
                CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary("item_unknown_n").clone();
                itemIcon = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
                GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), itemIcon, tempItemC.composite);
                GameStage.sceneLoader.getEngine().addEntity(itemIcon);
            } else {
                CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary(vc.shopIcon).clone();
                itemIcon = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
                GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), itemIcon, tempItemC.composite);
                GameStage.sceneLoader.getEngine().addEntity(itemIcon);
            }

            final TransformComponent tc = bagEntity.getComponent(TransformComponent.class);
            tc.x = x;
            tc.y = y;

            itemIcon.add(new ButtonComponent());
            shopItem.getChild("btn_shop_icon_lib").addChild(itemIcon);
            TransformComponent tcb = itemIcon.getComponent(TransformComponent.class);
            tcb.x = tc.x;
            tcb.y = tc.y;

            bags.add(bagEntity);
            itemIcons.put(vc.shopIcon, itemIcon);

            bagEntity.add(new ButtonComponent());

            final LayerMapComponent lc = ComponentRetriever.get(bagEntity, LayerMapComponent.class);
            bagEntity.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
                @Override
                public void touchUp() {
                    if (isPreviewOn){
                        bagEntity.getComponent(ButtonComponent.class).isTouched = false;
                        lc.getLayer("normal").isVisible = true;
                        lc.getLayer("pressed").isVisible = false;
                    }
                }

                @Override
                public void touchDown() {
                    if (isPreviewOn){
                        bagEntity.getComponent(ButtonComponent.class).isTouched = false;
                        lc.getLayer("normal").isVisible = true;
                        lc.getLayer("pressed").isVisible = false;
                    }
                }

                @Override
                public void clicked() {
                    if (!isPreviewOn && canOpenPreview) {
                        preview.showPreview(vc, true, false);
                    } else {
                        lc.getLayer("normal").isVisible = true;
                        lc.getLayer("pressed").isVisible = false;
                    }
                }
            });
            x += 250;
        }
    }

    private void addBackButtonPlease() {
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
                if (!isPreviewOn) {
                    stage.initMenu();
                }
            }
        });
    }

    @Override
    public void act(float delta) {
        if (!isPreviewOn) {
            List<Entity> itemIcons2 = new ArrayList<>(itemIcons.values());
            if (touchZoneBtn.isTouched) {
                if (!isGdxWritten) {
                    tempGdx.x = Gdx.input.getX();
                    isGdxWritten = true;
                    canOpenPreview = true;
                }
                if (tempGdx.x > Gdx.input.getX()) {
                    int i = 0;
                    while (i < bags.size()) {
                        bags.get(i).getComponent(TransformComponent.class).x -= (tempGdx.x - Gdx.input.getX()) / 15;
                        itemIcons2.get(i).getComponent(TransformComponent.class).x = bags.get(i).getComponent(TransformComponent.class).x;
                        i++;
                    }
                    stopVelocity = (Gdx.input.getX() - tempGdx.x) / 15;
                    tempGdx.x -= (tempGdx.x - Gdx.input.getX()) / 15;

                    canOpenPreview = false;
                }
                if (tempGdx.x < Gdx.input.getX()) {
                    int i = 0;
                    while (i < bags.size()) {
                        bags.get(i).getComponent(TransformComponent.class).x += (Gdx.input.getX() - tempGdx.x) / 15;
                        itemIcons2.get(i).getComponent(TransformComponent.class).x = bags.get(i).getComponent(TransformComponent.class).x;
                        i++;
                    }
                    stopVelocity = (Gdx.input.getX() - tempGdx.x) / 15;
                    tempGdx.x += (Gdx.input.getX() - tempGdx.x) / 15;
                    canOpenPreview = false;
                }
            } else {
                isGdxWritten = false;
                if (stopVelocity != 0) {
                    int i = 0;
                    while (i < bags.size()) {
                        bags.get(i).getComponent(TransformComponent.class).x += stopVelocity;
                        itemIcons2.get(i).getComponent(TransformComponent.class).x = bags.get(i)
                                .getComponent(TransformComponent.class).x;
                        i++;
                    }
                    stopVelocity -= stopVelocity / 20;
                }
            }
        }
        preview.checkAndClose();
        lc.text.replace(0, lc.text.length(), String.valueOf(fpc.totalScore));
    }

    public static void reloadScoreLabel(FlowerPublicComponent fcc) {
        scoreLbl.getComponent(LabelComponent.class).text.replace(0, scoreLbl.getComponent(LabelComponent.class).text.capacity(),
                String.valueOf(fcc.totalScore));
    }

    @Override
    public void dispose() {
    }

}
