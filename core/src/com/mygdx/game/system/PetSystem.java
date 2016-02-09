package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.entity.componets.PetComponent;
import com.mygdx.game.stages.GameScreenScript;
import com.mygdx.game.utils.EffectUtils;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import com.uwsoft.editor.renderer.systems.action.Actions;
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
        TransformComponent tc = entity.getComponent(TransformComponent.class);

        updateRect(pc, tc, entity.getComponent(DimensionsComponent.class));
        if (!GameScreenScript.isPause && !GameScreenScript.isGameOver) {
            pc.animationCounter--;

            if (pc.state.equals(MOVE) ) {
                if (tc.x >= 1150) {
                    entity.remove(ActionComponent.class);
                    ActionComponent ac = new ActionComponent();
                    Actions.checkInit();
                    tc.y = PetComponent.getNewPositionY();
                    ac.dataArray.add(Actions.moveTo(X_SPAWN_POSITION, tc.y,0.7f));
                    entity.add(ac);
                }

                if (tc.x == X_SPAWN_POSITION){
                    pc.state = IDLE;
                }
            }
            if (pc.state.equals(EATING)) {
                setAnimation(EAt_ANI, Animation.PlayMode.LOOP, sasc, sac);
                if (pc.animationCounter >= 0) {
                    if (pc.eatenBugsCounter < pc.amountBugsBeforeCharging) {
                        pc.state = IDLE;
                        setAnimation(IDLE_ANI, Animation.PlayMode.LOOP, sasc, sac);
                    } else {
                        canPlayAnimation = true;
                        setAnimation(CHARGING_ANI, Animation.PlayMode.LOOP, sasc, sac);
                        pc.state = CHARGING;
                    }
                }
            }
            if (pc.state.equals(SPAWNING)) {
                setAnimation(SPAWN_ANI, Animation.PlayMode.LOOP, sasc, sac);
                pc.velocity = 0;
                if (pc.animationCounter == 0) {
                    pc.state = IDLE;
                    canPlayAnimation = true;
                    pc.setOutsideStateDuration();
                    setAnimation(IDLE_ANI, Animation.PlayMode.LOOP, sasc, sac);
                }
            }

            if (pc.state.equals(CHARGING)) {
                pc.velocity += deltaTime * 3.4;
                tc.x -= pc.velocity;
            }

            if (tc.x < -100) {
                pc.state = OUTSIDE;
                pc.velocity = 0;
                tc.x = FAR_FAR_AWAY_X;
                pc.setOutsideStateDuration();
            }

            if (pc.state.equals(OUTSIDE) && pc.animationCounter <= 0) {
                pc.state = SPAWNING;
                pc.animationCounter = PetComponent.SPAWN_DURATION;
                tc.x = X_SPAWN_POSITION;
                tc.y = PetComponent.getNewPositionY();
            }

            if (Gdx.input.justTouched() &&
                    pc.boundsRect.contains(Gdx.input.getX(), 786 - Gdx.input.getY())
                    && !pc.state.equals(MOVE)){
                pc.state = MOVE;
                setAnimation(TACK_ANI, Animation.PlayMode.LOOP, sasc, sac);
                pc.animationCounter = TACK_DURATION;

                EffectUtils.playYellowStarsParticleEffect(Gdx.input.getX(), 786 - Gdx.input.getY());

                tc.x ++;
                ActionComponent ac = new ActionComponent();
                Actions.checkInit();
                ac.dataArray.add(Actions.moveTo(1150, tc.y,0.7f));
                entity.add(ac);
            }
        }
    }

    public void updateRect(PetComponent pc, TransformComponent tc, DimensionsComponent dc) {
        pc.boundsRect.x = (int) tc.x;
        pc.boundsRect.y = (int) tc.y;
        pc.boundsRect.width = (int) dc.width * tc.scaleX;
        pc.boundsRect.height = (int) dc.height * tc.scaleY;
    }

    public void setAnimation(String animationName, Animation.PlayMode mode, SpriteAnimationStateComponent sasComponent, SpriteAnimationComponent saComponent) {
        if (canPlayAnimation) {
            sasComponent.set(saComponent.frameRangeMap.get(animationName), FPS, mode);
            canPlayAnimation = false;
        }
    }
}
