package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mygdx.game.entity.componets.*;
import com.mygdx.game.system.*;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
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

    private ItemWrapper gameItem;
    public Random random = new Random();
//    CompositeVO
//    private int spawnCounter = 0;

    public int dandelionSpawnCounter;
    public int cocoonSpawnCounter;

    //One flower collision component will be used in all systems
    public FlowerPublicComponent fcc;
    public PlayerComponent pc;
    public static LabelComponent scoreLabelComponent;
    public static LabelComponent startLabelComponent;
    public static boolean isPause;
    public static boolean isGameOver;
    public static boolean isStarted;

    @Override
    public void init(Entity item) {
        gameItem = new ItemWrapper(item);

        dandelionSpawnCounter = random.nextInt(DANDELION_SPAWN_CHANCE_MAX - DANDELION_SPAWN_CHANCE_MIN) + DANDELION_SPAWN_CHANCE_MIN;
        cocoonSpawnCounter = random.nextInt(COCOON_SPAWN_MAX - COCOON_SPAWN_MIN) + COCOON_SPAWN_MIN;

        GameStage.sceneLoader.addComponentsByTagName("button", ButtonComponent.class);
        Entity shopBtn = gameItem.getChild("btn_shop").getEntity();

        Entity scoreLabel = gameItem.getChild("lbl_score").getEntity();
        scoreLabelComponent = scoreLabel.getComponent(LabelComponent.class);

        scoreLabelComponent.text.replace(0, scoreLabelComponent.text.capacity(), "why string builder?");

        Entity startLabel = gameItem.getChild("lbl_tap2start").getEntity();

        startLabelComponent = startLabel.getComponent(LabelComponent.class);

        startLabelComponent.text.replace(0, startLabelComponent.text.capacity(), "TAP TO START");

        fcc = new FlowerPublicComponent();
        pc = new PlayerComponent();

        GameStage.sceneLoader.getEngine().addSystem(new BugSystem());
        GameStage.sceneLoader.getEngine().addSystem(new DandelionSystem(fcc));

        GameStage.sceneLoader.getEngine().addSystem(new UmbrellaSystem());
        GameStage.sceneLoader.getEngine().addSystem(new FlowerSystem());
        GameStage.sceneLoader.getEngine().addSystem(new CocoonSystem(sceneLoader));
        GameStage.sceneLoader.getEngine().addSystem(new BugSpawnSystem(fcc));
        GameStage.sceneLoader.getEngine().addSystem(new ButterflySystem());

        //init Flower
        final CompositeItemVO tempC = GameStage.sceneLoader.loadVoFromLibrary("flowerLibV3");
        Entity flowerEntity = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempC);
        GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), flowerEntity, tempC.composite);
        GameStage.sceneLoader.getEngine().addEntity(flowerEntity);

        TransformComponent tc = new TransformComponent();
        tc.x = 970;
        tc.y = -774;
        tc.scaleX = BUG_SCALE;
        tc.scaleY = BUG_SCALE;
        flowerEntity.add(tc);

        FlowerComponent fc = new FlowerComponent();
        flowerEntity.add(fc);

        flowerEntity.add(fcc);
        flowerEntity.add(pc);

        LayerMapComponent lc = ComponentRetriever.get(flowerEntity, LayerMapComponent.class);
        lc.setLayers(tempC.composite.layers);
        flowerEntity.add(lc);


        // Adding a Click listener to playButton so we can start game when clicked
        shopBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
                tempC.composite.layers.get(0).isVisible = true;
            }

            @Override
            public void touchDown() {
                tempC.composite.layers.get(0).isVisible = false;
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
        if (Gdx.input.isKeyPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            isPause = !isPause;
        }

        if (!isStarted && Gdx.input.justTouched()){
            startLabelComponent.text.replace(0, startLabelComponent.text.capacity(), "");
            isStarted = true;
        }

        if (!isPause && !isGameOver && isStarted) {
            if (canDandelionSpawn()) {
                dandelionSpawnCounter--;
            }
            if (canCocoonSpawn()) {
                cocoonSpawnCounter--;
            }
            //Spawn dandelion
            if (dandelionSpawnCounter <= 0) {
                spawnDandelion();
            }
            //spawn Cocoon
            if (cocoonSpawnCounter <= 0) {
                spawnCocoon();
            }
            for(Entity e : GameStage.sceneLoader.getEngine().getEntitiesFor(Family.all(SpriteAnimationStateComponent.class).get())){
                SpriteAnimationStateComponent sacs = ComponentRetriever.get(e, SpriteAnimationStateComponent.class);
                sacs.paused = false;
            }
        }else{
            for(Entity e : GameStage.sceneLoader.getEngine().getEntitiesFor(Family.all(SpriteAnimationStateComponent.class).get())){
                SpriteAnimationStateComponent sacs = ComponentRetriever.get(e, SpriteAnimationStateComponent.class);
                sacs.paused = true;
            }
        }
    }

    private void spawnDandelion() {

        if(canDandelionSpawn()){

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

    private void spawnCocoon() {
        if(canCocoonSpawn()) {
            cocoonSpawnCounter = random.nextInt(COCOON_SPAWN_MAX - COCOON_SPAWN_MIN) + COCOON_SPAWN_MIN;

            CompositeItemVO cocoonComposite = sceneLoader.loadVoFromLibrary("drunkbugLib");
            Entity cocoonEntity = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), cocoonComposite);
            sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), cocoonEntity, cocoonComposite.composite);

            TransformComponent transformComponent = new TransformComponent();
            transformComponent.x = 850;
            transformComponent.y = 710;
            cocoonEntity.add(transformComponent);

            cocoonEntity.add(fcc);
            CocoonComponent cc = new CocoonComponent();
            cocoonEntity.add(cc);

//        DimensionsComponent dc = new DimensionsComponent();
//        cocoonEntity.add(dc);
            sceneLoader.getEngine().addEntity(cocoonEntity);
        }
    }

    private boolean canCocoonSpawn() {
        return sceneLoader.getEngine().getEntitiesFor(Family.all(CocoonComponent.class).get()) == null ||
                sceneLoader.getEngine().getEntitiesFor(Family.all(CocoonComponent.class).get()).size() == 0;
    }

    private boolean canDandelionSpawn() {
        return (sceneLoader.getEngine().getEntitiesFor(Family.all(DandelionComponent.class).get()) == null ||
                sceneLoader.getEngine().getEntitiesFor(Family.all(DandelionComponent.class).get()).size() == 0) &&
                (sceneLoader.getEngine().getEntitiesFor(Family.all(UmbrellaComponent.class).get()) == null ||
                        sceneLoader.getEngine().getEntitiesFor(Family.all(UmbrellaComponent.class).get()).size() == 0);
    }
}
