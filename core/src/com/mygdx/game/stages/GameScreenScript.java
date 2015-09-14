package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.mygdx.game.system.BugSpawnSystem;
import com.mygdx.game.system.BugSystem;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.data.LayerItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.systems.LayerSystem;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

/**
 * Created by Teatree on 7/25/2015.
 */
public class GameScreenScript implements IScript {

    private GameStage stage;
    private ItemWrapper gameItem;
//    private int spawnCounter = 0;


    public GameScreenScript(GameStage stage) {
        this.stage = stage;
    }

    @Override
    public void init(Entity item) {
        gameItem = new ItemWrapper(item);

        stage.sceneLoader.addComponentsByTagName("button", ButtonComponent.class);
        Entity shopBtn = gameItem.getChild("btn_shop").getEntity();

        BugSystem bugSystem = new BugSystem();

        stage.sceneLoader.getEngine().addSystem(bugSystem);
        stage.sceneLoader.getEngine().addSystem(new BugSpawnSystem(stage.sceneLoader));


        //init Flower
        CompositeItemVO tempC = stage.sceneLoader.loadVoFromLibrary("flowerLib");
//        LayerItemVO tempL = tempC.
        Entity flowerEntity = stage.sceneLoader.entityFactory.createEntity(stage.sceneLoader.getRoot(), tempC);
        stage.sceneLoader.entityFactory.initAllChildren(stage.sceneLoader.getEngine(), flowerEntity, tempC.composite);
        stage.sceneLoader.getEngine().addEntity(flowerEntity);

        TransformComponent tc = new TransformComponent();
        tc.x = 300;
        tc.y = -400;
        tc.scaleX = 0.6f;
        tc.scaleY = 0.6f;
        flowerEntity.add(tc);

//        LayerMapComponent lc = ComponentRetriever.get(flowerEntity, LayerMapComponent.class);
//        lc.setLayers(tempC.composite.layers);
//        flowerEntity.add(lc);

//        LayerItemVO tempL = lc.getLayer("Layer1");

//                tempC.composite.layers.get(0).isVisible = false;

        SpriteAnimationComponent spriteAnimationComponent = new SpriteAnimationComponent();
//        SpriteAnimationStateComponent spriteAnimationStateComponent = new SpriteAnimationStateComponent();
//        stage.sceneLoader.getEngine().addSystem(new LayerSystem());

//        tempL.isVisible = true;
//        System.out.println(tempL.isVisible + " IS THE NAME");

        // Adding a Click listener to playButton so we can start game when clicked
        shopBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
//                spriteAnimationComponent.playMode
//                System.out.println(spriteAnimationComponent.playMode);
            }

            @Override
            public void touchDown() {

                stage.initMenu();
            }

            @Override
            public void clicked() {

            }
        });

    }

    @Override
    public void dispose() {

    }

    @Override
    public void act(float delta) {
//        stage.sceneLoader.getEngine().update(Gdx.graphics.getDeltaTime());
    }
}
