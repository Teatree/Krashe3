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
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.game.entity.componets.DandelionComponent.State.*;
import static com.mygdx.game.stages.GameStage.sceneLoader;
import static com.mygdx.game.utils.GlobalConstants.*;
import static com.badlogic.gdx.graphics.g2d.Animation.*;
import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.*;


/**
 * Created by Teatree on 9/3/2015.
 */
public class DandelionSystem extends IteratingSystem {

    public static final String SPAWN_ANI_NAME = "Spawn";
    public static final String IDLE_ANI_NAME = "Idle";
    public static final String DIE_ANI_NAME = "Die";
    public static final int HIDE_POSITION = -200;


    private ComponentMapper<DandelionComponent> mapper = ComponentMapper.getFor(DandelionComponent.class);
    private FlowerPublicComponent fcc;

    private int idleCounter;

    //counts time from start of animation.
    //Use to check if Animation finished in NORMAL mode
    //Should be set to 0 when current animation finished
    private float stateTime;

    private boolean canPlayAnimation = true;

    public DandelionSystem(FlowerPublicComponent fcc) {
        super(Family.all(DandelionComponent.class).get());
        this.fcc = fcc;
    }

    private void spawnUmbrella(float x, float y){

        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        Entity umbrellaEntity = root.getChild("umbrellaAni").getEntity();

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

        DandelionComponent dc = mapper.get(entity);
        SpriteAnimationComponent saComponent = ComponentRetriever.get(entity, SpriteAnimationComponent.class);
        SpriteAnimationStateComponent animStateComp = ComponentRetriever.get(entity, SpriteAnimationStateComponent.class);

        if (!GameScreenScript.isPause && !GameScreenScript.isGameOver) {

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
                        tc.x = HIDE_POSITION;
                        tc.y = HIDE_POSITION;
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
}

