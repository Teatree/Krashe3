package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.entity.componets.ShopItem;
import com.mygdx.game.entity.componets.Upgrade;
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
import static com.mygdx.game.stages.GameStage.sceneLoader;
import static com.mygdx.game.entity.componets.ShopItem.CurrencyType.*;

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

    public int bagPosId;

    public Preview preview;
    public static List<ShopItem> allShopItems = new ArrayList<>();

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
        TransformComponent previousTc = null;
        allShopItems.addAll(Upgrade.getAllUpgrades());
        allShopItems.addAll(fpc.pets);
        allShopItems.addAll(fpc.vanities);

        Collections.sort(allShopItems, new Comparator<ShopItem>() {
            @Override
            public int compare(ShopItem o1, ShopItem o2) {
                if (o1.currencyType.equals(o2.currencyType)){
                    return compareByCost(o1, o2);
                } else {
                    return o1.currencyType.equals(HARD) ? 1 : -1;
                }
            }

            public int compareByCost(ShopItem o1, ShopItem o2) {
                if (o1.cost == o2.cost){
                    return 0;
                } else {
                    return o1.cost > o2.cost ? 1 : -1;
                }
            }
        });
        Collections.reverse(allShopItems);

        for (final ShopItem vc : allShopItems) {
            CompositeItemVO tempC = GameStage.sceneLoader.loadVoFromLibrary("btn_shop_icon_lib").clone();
            final Entity bagEntity = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempC);
            GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), bagEntity, tempC.composite);
            GameStage.sceneLoader.getEngine().addEntity(bagEntity);

            Entity itemIcon;
            if (vc.currencyType.equals(SOFT)) {
                itemIcon = initSoftCurrencyShopItem(vc);
            } else {
                itemIcon = new ItemWrapper(sceneLoader.getRoot()).getChild(vc.name).getEntity();
            }

            itemIcon.getComponent(ZIndexComponent.class).setZIndex(25);

            final TransformComponent tc = getNextBagPos(previousTc, bagEntity.getComponent(DimensionsComponent.class));
            bagEntity.add(tc);

            addDot(bagEntity, bagEntity.getComponent(TransformComponent.class), bagEntity.getComponent(DimensionsComponent.class));
            previousTc = tc;

            itemIcon.add(new ButtonComponent());
            shopItem.getChild("btn_shop_icon_lib").addChild(itemIcon);
            TransformComponent tcb = itemIcon.getComponent(TransformComponent.class);
            tcb.x = tc.x;
            tcb.y = tc.y;

            bags.add(bagEntity);
            if (vc.currencyType.equals(SOFT)) {
                itemIcons.put(vc.shopIcon, itemIcon);
            } else {
                itemIcons.put(vc.name, itemIcon);
            }

            bagEntity.add(new ButtonComponent());

            final LayerMapComponent lc = ComponentRetriever.get(bagEntity, LayerMapComponent.class);
            bagEntity.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
                @Override
                public void touchUp() {
                    skipLayersOverride(lc);
                }

                @Override
                public void touchDown() {
                    skipLayersOverride(lc);
                }

                @Override
                public void clicked() {
                    if (!isPreviewOn && canOpenPreview) {
                        preview.showPreview(vc, true, false);
                    }
                    skipLayersOverride(lc);
                }
            });
        }
    }

    public void skipLayersOverride(LayerMapComponent lc) {
        if (isPreviewOn || !canOpenPreview){
            lc.getLayer("normal").isVisible = true;
            lc.getLayer("pressed").isVisible = false;
        }
    }

    private Entity initSoftCurrencyShopItem(ShopItem vc) {
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
            itemIcon.getComponent(ZIndexComponent.class).setZIndex(120);
            GameStage.sceneLoader.getEngine().addEntity(itemIcon);
        }
        return itemIcon;
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
                    ButtonComponent.skipDefaultLayersChange = false;
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

                    ButtonComponent.skipDefaultLayersChange = true;
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
                    ButtonComponent.skipDefaultLayersChange = true;
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

    private void addDot (Entity bag, TransformComponent bagTc, DimensionsComponent bagDc) {
        TransformComponent tc = new TransformComponent();
        switch (bagPosId) {
            case 0: {
                tc.x = bagDc.width/2;
                tc.y = -5;
//                tc.x = bagTc.x + bagDc.width/2;
//                tc.y = bagTc.y - 5;
                break;
            }
            case 1: {
                tc.x = bagDc.width + 5;
                tc.y = bagDc.height/2;
//                tc.x = bagTc.x + bagDc.width + 5;
//                tc.y = bagTc.y + bagDc.height/2;
                break;
            }
            case 2: {
                tc.x = bagDc.width/2;
                tc.y = bagDc.height + 5;
//                 tc.x = bagTc.x + bagDc.width/2;
//                tc.y = bagTc.y + bagDc.height + 5;
                break;

            }
            case 3: {
                tc.x = bagDc.width + 5;
                tc.y = bagDc.height/2;
//                tc.x = bagTc.x + bagDc.width + 5;
//                tc.y = bagTc.y + bagDc.height/2;
                break;
            }
        }
        tc.scaleX = 0.2f;
        tc.scaleY = 0.2f;

        CompositeItemVO tempC = GameStage.sceneLoader.loadVoFromLibrary("dot_lib").clone();
        final Entity dotE = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempC);
        GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), dotE, tempC.composite);
        dotE.getComponent(ZIndexComponent.class).setZIndex(0);
        dotE.add(tc);
        bag.getComponent(NodeComponent.class).addChild(dotE);
        shopItem.getChild("background").getEntity().getComponent(ZIndexComponent.class).setZIndex(1);
        for(Entity e : bag.getComponent(NodeComponent.class).children)
            e.getComponent(ZIndexComponent.class).setZIndex(25);
//        GameStage.sceneLoader.getEngine().removeEntity(dotE);
    }

    public TransformComponent getNextBagPos(TransformComponent previous, DimensionsComponent previousDc){
        TransformComponent tc = new TransformComponent();
        int step = 20;

        if (previous == null) {
            tc.x = 173;
            tc.y = 359;
            return tc;
        }
        switch(bagPosId) {
            case 0: {
                tc.x = previous.x;
                tc.y = previous.y - previousDc.height - step;

                bagPosId++;
                break;
            }
            case 1: {
                tc.x = previous.x + previousDc.width + step;
                tc.y = previous.y;

                bagPosId++;
                break;
            }
            case 2: {
                tc.x = previous.x;
                tc.y = previous.y + previousDc.height + step;

                bagPosId++;
                break;
            }
            case 3: {
                tc.x = previous.x + previousDc.width + step;
                tc.y = previous.y;

                bagPosId = 0;
                break;
            }
        }
        return tc;
    }

    public static void reloadScoreLabel(FlowerPublicComponent fcc) {
        scoreLbl.getComponent(LabelComponent.class).text.replace(0, scoreLbl.getComponent(LabelComponent.class).text.capacity(),
                String.valueOf(fcc.totalScore));
    }

    @Override
    public void dispose() {
    }

}
