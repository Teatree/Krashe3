package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.mygdx.game.entity.componets.DandelionComponent;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.entity.componets.UmbrellaComponent;
import com.mygdx.game.stages.GameScreenScript;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.GlobalConstants;
import com.sun.org.apache.xpath.internal.SourceTree;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import static com.mygdx.game.entity.componets.DandelionComponent.State.*;
import static com.mygdx.game.utils.GlobalConstants.*;


/**
 * Created by Teatree on 9/3/2015.
 */
public class DandelionSystem extends IteratingSystem {


    private ComponentMapper<DandelionComponent> mapper = ComponentMapper.getFor(DandelionComponent.class);
    private FlowerPublicComponent fcc;

    private int counter;
    private CompositeItemVO umbrellaComposite;
    float stateTime;

    private boolean canPlay = true;

    public DandelionSystem(FlowerPublicComponent fcc) {
        super(Family.all(DandelionComponent.class).get());
        this.fcc = fcc;

        umbrellaComposite = GameStage.sceneLoader.loadVoFromLibrary("umbrellaLib");
    }

    private void spawnUmbrella(float x, float y){
        Entity umbrellaEntity = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), umbrellaComposite);
        GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), umbrellaEntity, umbrellaComposite.composite);
        GameStage.sceneLoader.getEngine().addEntity(umbrellaEntity);

        TransformComponent transformComponent = new TransformComponent();
        transformComponent.x = x;
        transformComponent.y = y;
        umbrellaEntity.add(transformComponent);

        UmbrellaComponent umbrellaComponent  = new UmbrellaComponent();
        umbrellaEntity.add(umbrellaComponent);

        umbrellaEntity.add(fcc);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (!GameScreenScript.isPause && !GameScreenScript.isGameOver) {


            stateTime += Gdx.graphics.getDeltaTime();
            DandelionComponent dc = mapper.get(entity);
            SpriteAnimationComponent saComponent = ComponentRetriever.get(entity, SpriteAnimationComponent.class);
            SpriteAnimationStateComponent sasComponent = ComponentRetriever.get(entity, SpriteAnimationStateComponent.class);

            System.out.println("current ani keyframe is: " + sasComponent.currentAnimation.getFrameDuration());

            if ("GAME".equals(CUR_SCREEN)) {
//
                if (dc.state == GROWING) {
//                    if (counter >= GlobalConstants.DANDELION_GROWING_DURATION) {
//                        dc.state = IDLE;
//                        counter = 0;
//                    }
                    setAnimSpawn(sasComponent, saComponent);
                    if (sasComponent.get().isAnimationFinished(stateTime)){
                        canPlay = true;
                        System.out.println("curAni is: " + sasComponent.currentAnimation.getKeyFrame(54) + "SPAWN!");
                        dc.state = IDLE;
//                        sasComponent.set(saComponent.frameRangeMap.get("Idle"), 24, Animation.PlayMode.LOOP);
                    }
                }
                if (dc.state == IDLE) {
                    System.out.println(" I'M IDLE!!! ");
                    counter++;
                    setAnimIdle(sasComponent, saComponent);
                    if (counter >= GlobalConstants.DANDELION_IDLE_DURATION) {
                        canPlay = true;
                        System.out.println("curAni is: " + sasComponent.currentAnimation.getKeyFrame(54) + "IDLE!");
                        dc.state = DYING;
                        counter = 0;
                    }
                }
                if (dc.state == DYING) {
                    setAnimDie(sasComponent, saComponent);
                    if (sasComponent.get().isAnimationFinished(0.83f)) {
                        System.out.println("curAni is: " + sasComponent.currentAnimation.getKeyFrame(54) + "DYING!");
                        TransformComponent tc = ComponentRetriever.get(entity, TransformComponent.class);
                        spawnUmbrella(tc.x, tc.y);
                        dc.state = DEAD;
                        canPlay = true;
                        GameStage.sceneLoader.getEngine().removeEntity(entity);
                    }
                }
            }
        }
    }

    public void setAnimSpawn(SpriteAnimationStateComponent sasComponent, SpriteAnimationComponent saComponent){
        if (canPlay) {
            sasComponent.set(saComponent.frameRangeMap.get("Spawn"), 24, Animation.PlayMode.NORMAL);
            canPlay = false;
        }
    }
    public void setAnimIdle(SpriteAnimationStateComponent sasComponent, SpriteAnimationComponent saComponent){
        if (canPlay) {
            sasComponent.set(saComponent.frameRangeMap.get("Idle"), 24, Animation.PlayMode.LOOP);
            canPlay = false;
        }
    }
    public void setAnimDie(SpriteAnimationStateComponent sasComponent, SpriteAnimationComponent saComponent){
        if (canPlay) {
            sasComponent.set(saComponent.frameRangeMap.get("Die"), 24, Animation.PlayMode.NORMAL);
            canPlay = false;
        }
    }
}

