package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.entity.componets.PetComponent;
import com.mygdx.game.stages.GameScreenScript;
import com.mygdx.game.utils.EffectUtils;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.systems.action.Actions;

import java.util.Random;

import static com.mygdx.game.entity.componets.PetComponent.State.*;
import static com.mygdx.game.utils.EffectUtils.getTouchCoordinates;
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
        TransformComponent tc = entity.getComponent(TransformComponent.class);
        SpriterComponent sc = entity.getComponent(SpriterComponent.class);

        updateRect(pc, tc, entity.getComponent(DimensionsComponent.class));
        if (!GameScreenScript.isPause && !GameScreenScript.isGameOver) {
            pc.animationCounter--;

            if (pc.state.equals(TAPPED) ) {
                if (tc.x >= 1300) {
                    entity.remove(ActionComponent.class);
                    ActionComponent ac = new ActionComponent();
                    Actions.checkInit();
                    tc.y = PetComponent.getNewPositionY();
                    ac.dataArray.add(Actions.moveTo(X_SPAWN_POSITION, tc.y,0.7f));
                    entity.add(ac);
                    setIdleAnimation(sc);
                }

                if (tc.x == X_SPAWN_POSITION){
                    pc.state = IDLE;
                }
            }
            if (pc.state.equals(BITE)){
                setBiteAnimation(sc);
                if (pc.animationCounter == 0) {
                    if (pc.eatenBugsCounter < pc.amountBugsBeforeCharging) {
                        pc.state = IDLE;
                        setIdleAnimation(sc);
                    } else {
                        canPlayAnimation = true;
                        setDashAnimation(sc);
                        pc.state = DASH;
                    }
                }
            }

            if (pc.state.equals(SPAWNING)) {
                setSpawnAnimation(sc);
                pc.velocity = 0;
                if (pc.animationCounter == 0) {
                    pc.state = IDLE;
                    canPlayAnimation = true;
                    pc.setOutsideStateDuration();
                    setIdleAnimation(sc);
                }
            }

            if (pc.state.equals(DASH)) {
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
                pc.eatenBugsCounter = 0;
                pc.animationCounter = PetComponent.SPAWN_DURATION;
                tc.x = X_SPAWN_POSITION;
                tc.y = PetComponent.getNewPositionY();
                setSpawnAnimation(sc);
            }

            Vector2 v = getTouchCoordinates();
            if (Gdx.input.justTouched() &&
                    pc.boundsRect.contains(v.x, v.y)
                    && !pc.state.equals(TAPPED) && !pc.state.equals(DASH)){
                pc.state = TAPPED;
                setTappedAnimation(sc);
                pc.animationCounter = TAP_DURATION;

                EffectUtils.playYellowStarsParticleEffect(v.x, v.y);

                tc.x ++;
                ActionComponent ac = new ActionComponent();
                Actions.checkInit();
                ac.dataArray.add(Actions.moveTo(1300, tc.y,0.7f));
                entity.add(ac);
            }
        }
    }

    public void updateRect(PetComponent pc, TransformComponent tc, DimensionsComponent dc) {
        pc.boundsRect.width = (int) dc.width * tc.scaleX;
        pc.boundsRect.height = (int) dc.height * tc.scaleY;

        pc.boundsRect.x = (int) tc.x - 348/2;
        pc.boundsRect.y = (int) tc.y - 361/2;
}

    private void setSpawnAnimation(SpriterComponent sc) {
        sc.player.setAnimation(4);
    }

    private void setBiteAnimation(SpriterComponent sc) {
        sc.player.setAnimation(3);
    }

    private void setDashAnimation(SpriterComponent sc) {
        sc.player.setAnimation(1);
    }

    private void setIdleAnimation(SpriterComponent sc) {
        sc.player.setAnimation(0);
    }

    private void setTappedAnimation(SpriterComponent sc) {
        sc.player.setAnimation(2);
    }
}
