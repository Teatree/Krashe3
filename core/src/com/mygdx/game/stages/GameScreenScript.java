package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.mygdx.game.entity.componets.DandelionComponent;
import com.mygdx.game.entity.componets.FlowerCollisionComponent;
import com.mygdx.game.entity.componets.FlowerComponent;
import com.mygdx.game.system.*;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.data.CompositeVO;
import com.uwsoft.editor.renderer.data.LayerItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.Random;

import static com.mygdx.game.utils.GlobalConstants.*;
import static com.mygdx.game.stages.GameStage.*;

/**
 * Created by Teatree on 7/25/2015.
 */
public class GameScreenScript implements IScript {

    public static boolean GAME_OVER = false;
    public static boolean GAME_PAUSED = false;

    private GameStage stage;
    private ItemWrapper gameItem;
    public Random random = new Random();
//    CompositeVO
//    private int spawnCounter = 0;

    public int dandelionSpawnCounter;

    public GameScreenScript(GameStage stage) {
        this.stage = stage;
    }

    @Override
    public void init(Entity item) {
        gameItem = new ItemWrapper(item);

        dandelionSpawnCounter = random.nextInt(DANDELION_SPAWN_CHANCE_MAX - DANDELION_SPAWN_CHANCE_MIN) + DANDELION_SPAWN_CHANCE_MIN;

        stage.sceneLoader.addComponentsByTagName("button", ButtonComponent.class);
        Entity shopBtn = gameItem.getChild("btn_shop").getEntity();

        BugSystem bugSystem = new BugSystem();

        stage.sceneLoader.getEngine().addSystem(bugSystem);
        stage.sceneLoader.getEngine().addSystem(new DandelionSystem(sceneLoader));
        stage.sceneLoader.getEngine().addSystem(new UmbrelaSystem());
        stage.sceneLoader.getEngine().addSystem(new FlowerSystem());

        //init Flower
        final CompositeItemVO tempC = stage.sceneLoader.loadVoFromLibrary("flowerLibV3");
//        LayerItemVO tempL = tempC.
        Entity flowerEntity = stage.sceneLoader.entityFactory.createEntity(stage.sceneLoader.getRoot(), tempC);
        stage.sceneLoader.entityFactory.initAllChildren(stage.sceneLoader.getEngine(), flowerEntity, tempC.composite);
        stage.sceneLoader.getEngine().addEntity(flowerEntity);
//        flowerEntity.getComponent().composite.layers.get(1).isVisible = false;


        TransformComponent tc = new TransformComponent();
        tc.x = 970;
        tc.y = -774;
        tc.scaleX = 0.6f;
        tc.scaleY = 0.6f;
        flowerEntity.add(tc);

        FlowerComponent fc = new FlowerComponent();
        flowerEntity.add(fc);

        FlowerCollisionComponent fcc = new FlowerCollisionComponent();
        flowerEntity.add(fcc);

        LayerMapComponent lc = ComponentRetriever.get(flowerEntity, LayerMapComponent.class);
        lc.setLayers(tempC.composite.layers);
        flowerEntity.add(lc);
        stage.sceneLoader.getEngine().addSystem(new BugSpawnSystem(stage.sceneLoader,fcc));

//        LayerItemVO tempL = lc.getLayer("Layer1");
//                tempC.composite.layers.get(0).isVisible = true;

        SpriteAnimationComponent spriteAnimationComponent = new SpriteAnimationComponent();
//        SpriteAnimationStateComponent spriteAnimationStateComponent = new SpriteAnimationStateComponent();
//        stage.sceneLoader.getEngine().addSystem(new LayerSystem());

//        tempL.isVisible = true;
//        System.out.println(tempL.isVisible + " IS THE NAME");

        // Adding a Click listener to playButton so we can start game when clicked
        shopBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
                tempC.composite.layers.get(0).isVisible = true;
                System.out.println("isVisibleNow? =" + tempC.composite.layers.get(0).isVisible);
            }

            @Override
            public void touchDown() {
                tempC.composite.layers.get(0).isVisible = false;
                System.out.println("isVisible? =" + tempC.composite.layers.get(0).isVisible);
            }

            @Override
            public void clicked() {
//                tempC.composite.layers.get(0).isVisible = true;
            }
        });

    }

    @Override
    public void dispose() {

    }

    @Override
    public void act(float delta) {
        dandelionSpawnCounter--;
        //Spawn dandelion
        if (dandelionSpawnCounter <= 0) {
            dandelionSpawnCounter =
                    random.nextInt(DANDELION_SPAWN_CHANCE_MAX - DANDELION_SPAWN_CHANCE_MIN) + DANDELION_SPAWN_CHANCE_MIN;

            CompositeItemVO dandelionComposite = sceneLoader.loadVoFromLibrary("simpleLib");
            Entity dandelionEntity = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), dandelionComposite);
            sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), dandelionEntity, dandelionComposite.composite);

            TransformComponent transformComponent = new TransformComponent();
            transformComponent.x = 200;
            transformComponent.y = 110;
            dandelionEntity.add(transformComponent);

            DandelionComponent dc = new DandelionComponent();
            dc.state = DandelionComponent.State.GROWING;
            dandelionEntity.add(dc);

            sceneLoader.getEngine().addEntity(dandelionEntity);
        }
    }

    public static boolean isGameAlive() {
        return !GAME_PAUSED && !GAME_OVER;
    }
}
