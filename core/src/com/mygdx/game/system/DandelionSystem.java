package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.mygdx.game.entity.componets.DandelionComponent;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.entity.componets.UmbrellaComponent;
import com.mygdx.game.stages.GameScreenScript;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.Random;

import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP;
import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.NORMAL;
import static com.mygdx.game.entity.componets.CocoonComponent.SPAWN_INTERVAL_BASE;
import static com.mygdx.game.entity.componets.CocoonComponent.cocoonMultipliers;
import static com.mygdx.game.entity.componets.CocoonComponent.currentCocoonMultiplier;
import static com.mygdx.game.entity.componets.DandelionComponent.*;
import static com.mygdx.game.entity.componets.DandelionComponent.State.*;
import static com.mygdx.game.stages.GameScreenScript.*;
import static com.mygdx.game.utils.GlobalConstants.*;

public class DandelionSystem extends IteratingSystem {

    public static final String SPAWN_ANI_NAME = "Spawn";
    public static final String IDLE_ANI_NAME = "Idle";
    public static final String DIE_ANI_NAME = "Die";
    public static final String UMBRELLA_ANI = "umbrellaAni";

    private ComponentMapper<DandelionComponent> mapper = ComponentMapper.getFor(DandelionComponent.class);
    private FlowerPublicComponent fcc;

//    public float SPAWN_INTERVAL_BASE;
    private int idleCounter;

    //counts time from start of animation.
    //Use to check if Animation finished in BTN_NORMAL mode
    //Should be set to 0 when current animation finished
    private float stateTime;
    private ItemWrapper gameItem;

    private boolean canPlayAnimation = true;

    public DandelionSystem(GameScreenScript gameScript) {
        super(Family.all(DandelionComponent.class).get());
        this.gameItem = gameScript.gameItem;
    }

    private void spawnUmbrella(float x, float y){

        Entity umbrellaEntity = gameItem.getChild(UMBRELLA_ANI).getEntity();
        TransformComponent transformComponent = new TransformComponent();
        transformComponent.x = x;
        transformComponent.y = y;
        umbrellaEntity.add(transformComponent);

        UmbrellaComponent umbrellaComponent  = new UmbrellaComponent();
        umbrellaEntity.add(umbrellaComponent);

        umbrellaEntity.add(GameStage.gameScript.fpc);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        DandelionComponent dc = mapper.get(entity);
        SpriteAnimationComponent saComponent = ComponentRetriever.get(entity, SpriteAnimationComponent.class);
        SpriteAnimationStateComponent animStateComp = ComponentRetriever.get(entity, SpriteAnimationStateComponent.class);

        if (!isStarted) {
            entity.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            entity.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
        }

        if (!isPause && !isGameOver && isStarted) {

            animStateComp.paused = false;
            stateTime += Gdx.graphics.getDeltaTime();

            if ("GAME".equals(CUR_SCREEN)) {
                if (dc.state == GROWING) {
                    setAnimation(SPAWN_ANI_NAME, NORMAL, animStateComp, saComponent);
                    if (animStateComp.get().isAnimationFinished(stateTime)){
                        canPlayAnimation = true;
                        dc.state = IDLE;
                    }
                }
                if (dc.state == IDLE) {
                    idleCounter++;
                    setAnimation(IDLE_ANI_NAME, LOOP, animStateComp, saComponent);
                    if (idleCounter >= GlobalConstants.DANDELION_IDLE_DURATION) {
                        canPlayAnimation = true;
                        dc.state = DYING;
                        idleCounter = 0;
                    }
                }
                if (dc.state == DYING) {
                    setAnimation(DIE_ANI_NAME, NORMAL, animStateComp, saComponent);
                    if (animStateComp.get().isAnimationFinished(stateTime)) {
                        TransformComponent tc = ComponentRetriever.get(entity, TransformComponent.class);
                        spawnUmbrella(tc.x, tc.y);
                        dc.state = DEAD;
                        canPlayAnimation = true;
                        tc.x = FAR_FAR_AWAY_X;
                        tc.y = FAR_FAR_AWAY_Y;
                    }
                }
            }
        } else {
            animStateComp.paused = true;
        }
    }

    public void setAnimation(String animationName, PlayMode mode, SpriteAnimationStateComponent sasComponent, SpriteAnimationComponent saComponent){
        if (canPlayAnimation) {
            stateTime = 0;
            sasComponent.set(saComponent.frameRangeMap.get(animationName), FPS, mode);
            canPlayAnimation = false;
        }
    }

    public static float getNextSpawnInterval(){
        Random r = new Random();
        float randCoefficient = currentDandelionMultiplier.minSpawnCoefficient +
                r.nextFloat()*(currentDandelionMultiplier.maxSpawnCoefficient-currentDandelionMultiplier.minSpawnCoefficient);
        return SPAWN_INTERVAL_BASE*randCoefficient;
    }

    public static void resetSpawnCoefficients(){
        currentDandelionMultiplier = dandelionMultipliers.get(0);
    }
}
