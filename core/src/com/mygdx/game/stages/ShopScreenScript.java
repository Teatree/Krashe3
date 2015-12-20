package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.mygdx.game.entity.componets.VanityComponent;
import com.mygdx.game.utils.SaveMngr;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.List;

/**
 * Created by Teatree on 7/25/2015.
 */
public class ShopScreenScript implements IScript {

    private GameStage stage;
    private ItemWrapper shopItem;
//    private int spawnCounter = 0;


    public ShopScreenScript(GameStage stage) {
        this.stage = stage;
    }

    @Override
    public void init(Entity item) {
        shopItem = new ItemWrapper(item);

        stage.sceneLoader.addComponentsByTagName("button", ButtonComponent.class);
        Entity menuBtn = shopItem.getChild("btn_shop").getEntity();


        stage.sceneLoader.addComponentsByTagName("button", ButtonComponent.class);
        final Entity playBtn = shopItem.getChild("btn_play").getEntity();


        stage.sceneLoader.addComponentsByTagName("button", ButtonComponent.class);
//        BugSystem bugSystem = new BugSystem();

//        stage.sceneLoader.getEngine().addSystem(bugSystem);
//        stage.sceneLoader.getEngine().addSystem(new BugSpawnSystem(stage.sceneLoader));

//        CompositeItemVO bugData =  stage.sceneLoader.loadVoFromLibrary("chargerAni");
//        while(spawnCounter < 20){
//            spawnCounter++;
//            CompositeItemVO tempC = bugData.clone();
//            tempC.x = MathUtils.random(0, Gdx.graphics.getWidth()-100);
//            tempC.y = MathUtils.random(0, Gdx.graphics.getHeight()-100);
//            Entity tempEnty = stage.sceneLoader.entityFactory.createEntity(stage.sceneLoader.getRoot(), tempC);
//            stage.sceneLoader.entityFactory.initAllChildren(stage.sceneLoader.getEngine(), tempEnty, tempC.composite);
//            stage.sceneLoader.getEngine().addEntity(tempEnty);
//            System.out.println("Successfully spawned dude: " + spawnCounter);
//            BugComponent bc = new BugComponent();
//            bc.type = BugType.CHARGER;
////            bc.type = BugType.SIMPLE;
//
//            tempEnty.add(bc);
//        }


//        final Entity btnSettings = menuItem.getCompositeById("btn_settings");
//        final Entity btnNoAds = menuItem.getCompositeById("btn_noAds");
//        final Entity btnShop = menuItem.getChild("btn_shop").getEntity();

        // Adding a Click listener to playButton so we can start game when clicked


        getAllVanities2();

        menuBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {

            }

            @Override
            public void touchDown() {

                stage.initMenu();
                List<VanityComponent> vanityComponentList = SaveMngr.getAllVanity();
                System.out.println(vanityComponentList.get(1).icon);
                vanityComponentList.get(1).apply(GameScreenScript.fpc);
            }

            @Override
            public void clicked() {

            }
        });

    }

    private void getAllVanities2() {
        List<VanityComponent> vanityComponentList = SaveMngr.getAllVanity();

        for (final VanityComponent vc : vanityComponentList) {
            shopItem.getChild(vc.icon).getEntity().getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
                @Override
                public void touchUp() {

                }

                @Override
                public void touchDown() {

                    stage.initMenu();
                    System.out.println(vc.icon);
                    vc.apply(GameScreenScript.fpc);
                }

                @Override
                public void clicked() {

                }
            });
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    public void act(float delta) {
//        stage.sceneLoader.getEngine().update(Gdx.graphics.getDeltaTime());
    }

}
