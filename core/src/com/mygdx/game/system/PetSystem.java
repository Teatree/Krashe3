package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.entity.componets.PetComponent;
import com.mygdx.game.stages.GameScreenScript;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import java.util.Random;

import static com.mygdx.game.entity.componets.PetComponent.State.*;
import static com.mygdx.game.utils.GlobalConstants.*;
import static com.mygdx.game.entity.componets.PetComponent.*;

public class PetSystem extends IteratingSystem {

    public Random random = new Random();
    boolean canPlayAnimation = true;

    private ComponentMapper<PetComponent> mapper = ComponentMapper.getFor(PetComponent.class);
    private ComponentMapper<FlowerPublicComponent> fccMapper = ComponentMapper.getFor(FlowerPublicComponent.class);

    public PetSystem() {
        super(Family.all(PetComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PetComponent pc = mapper.get(entity);
        SpriteAnimationStateComponent sasc = ComponentRetriever.get(entity, SpriteAnimationStateComponent.class);
        SpriteAnimationComponent sac = ComponentRetriever.get(entity, SpriteAnimationComponent.class);

        updateRect(pc, entity.getComponent(TransformComponent.class), entity.getComponent(DimensionsComponent.class));
        if (!GameScreenScript.isPause && !GameScreenScript.isGameOver) {
            pc.counter--;
            if (pc.state.equals(SPAWNING)){
                setAnimation(IDLE_ANI, Animation.PlayMode.LOOP, sasc, sac);
                pc.velocity = 0;
                if (pc.counter == 0) {
                    pc.state = IDLE;
                    canPlayAnimation = true;
                    pc.setIdleStateDuration();
                    setAnimation(IDLE_ANI, Animation.PlayMode.LOOP, sasc, sac);
                }
            }

            if (pc.state.equals(IDLE)) {
                if (pc.counter == 0) {
                    canPlayAnimation = true;
                    setAnimation(CHARGING_ANI, Animation.PlayMode.LOOP, sasc, sac);
                    pc.state = CHARGING;
                }
            }
            if (pc.state.equals(CHARGING)) {
                pc.velocity += deltaTime * 3.4;
                entity.getComponent(TransformComponent.class).x -= pc.velocity;
            }

            if (entity.getComponent(TransformComponent.class).x < -100){
                pc.state = OUTSIDE;
                pc.velocity = 0;
            }

            if (pc.state.equals(OUTSIDE)){
                entity.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            }
        }
    }

    public void updateRect(PetComponent pc, TransformComponent tc, DimensionsComponent dc) {
        pc.boundsRect.x = (int)tc.x;
        pc.boundsRect.y = (int)tc.y;
        pc.boundsRect.width = (int)dc.width*tc.scaleX;
        pc.boundsRect.height = (int)dc.height*tc.scaleY;
    }

    public void setAnimation(String animationName, Animation.PlayMode mode, SpriteAnimationStateComponent sasComponent, SpriteAnimationComponent saComponent){
        if (canPlayAnimation) {
            sasComponent.set(saComponent.frameRangeMap.get(animationName), FPS, mode);
            canPlayAnimation = false;
        }
    }
}
