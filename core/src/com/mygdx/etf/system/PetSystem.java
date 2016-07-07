package com.mygdx.etf.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.etf.entity.componets.FlowerPublicComponent;
import com.mygdx.etf.entity.componets.PetComponent;
import com.mygdx.etf.stages.GameStage;
import com.mygdx.etf.utils.EffectUtils;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.systems.action.Actions;

import java.util.Random;

import static com.mygdx.etf.entity.componets.Goal.GoalType.PET_DASH_N_TIMES;
import static com.mygdx.etf.entity.componets.Goal.GoalType.PET_THE_PET;
import static com.mygdx.etf.entity.componets.PetComponent.State.*;
import static com.mygdx.etf.entity.componets.PetComponent.X_SPAWN_POSITION;
import static com.mygdx.etf.stages.GameScreenScript.isGameOver;
import static com.mygdx.etf.stages.GameScreenScript.isPause;
import static com.mygdx.etf.utils.EffectUtils.getTouchCoordinates;
import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.mygdx.etf.utils.GlobalConstants.FPS;

public class PetSystem extends IteratingSystem {

    public static final int TAPPED_X = 1300;
    public static final float DURATION_TAP = 1.7f;
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
        DimensionsComponent dc = entity.getComponent(DimensionsComponent.class);

        DimensionsComponent cannondc = entity.getComponent(DimensionsComponent.class);
        TransformComponent cannontc = pc.petCannon.getComponent(TransformComponent.class);
        SpriterComponent cannonsc = pc.petCannon.getComponent(SpriterComponent.class);

        dc.width = 56;
        dc.height = 100;
        updateRect(pc, tc, dc, cannontc, cannondc);


        if (!isPause && !isGameOver) {
            sc.player.speed = FPS;
            cannonsc.player.speed = FPS;
//            pc.animationCounter--;

            if (pc.state.equals(TAPPED)) {
                if (tc.x >= TAPPED_X) {
                    entity.remove(ActionComponent.class);
                    ActionComponent ac = new ActionComponent();
                    Actions.checkInit();
                    tc.y = PetComponent.getNewPositionY();
                    cannontc.y = tc.y;
                    ac.dataArray.add(Actions.moveTo(X_SPAWN_POSITION, tc.y, DURATION_TAP));
                    entity.add(ac);
                    pc.petCannon.add(ac);
                    setIdleAnimation(sc);
                    setIdleAnimation(cannonsc);
                }

                if (tc.x == X_SPAWN_POSITION) {
                    pc.state = IDLE;
                }
            }
            if (pc.state.equals(BITE)) {
                setBiteAnimation(sc);
                setBiteAnimation(cannonsc);
                if (isAnimationFinished(sc)) {
                    if (pc.eatenBugsCounter < pc.amountBugsBeforeCharging) {
                        pc.state = IDLE;
                        setIdleAnimation(sc);
                        setIdleAnimation(cannonsc);
                    } else {
                        canPlayAnimation = true;
                        setDashAnimation(cannonsc);
                        setDashAnimation(sc);
                        pc.state = DASH;
                        checkPetDashGoal();
                    }
                }
            }

            if (pc.state.equals(SPAWNING)) {
                tc.x = PetComponent.X_SPAWN_POSITION;
                cannontc.x = PetComponent.X_SPAWN_POSITION;
                setSpawnAnimation(sc);
                setSpawnAnimation(cannonsc);
                pc.velocity = 0;
                if (isAnimationFinished(sc)) {
                    pc.state = IDLE;
                    canPlayAnimation = true;
                    pc.setOutsideStateDuration();
                    setIdleAnimation(sc);
                    setIdleAnimation(cannonsc);
                }
            }

            if (pc.state.equals(DASH)) {
                pc.velocity += deltaTime * 3.4;
                tc.x -= pc.velocity;
                if (isAnimationFinished(cannonsc)) {
                    cannonsc.player.speed = 0;
                    cannontc.x = FAR_FAR_AWAY_X;
                }
            }

            if (tc.x < -100) {
                pc.state = OUTSIDE;
                pc.velocity = 0;
                tc.x = FAR_FAR_AWAY_X;
                pc.setOutsideStateDuration();
            }

            if (pc.state.equals(OUTSIDE)) {
                pc.state = SPAWNING;
                pc.eatenBugsCounter = 0;
//                pc.animationCounter = PetComponent.SPAWN_DURATION;
                tc.x = X_SPAWN_POSITION;
                tc.y = PetComponent.getNewPositionY();

                cannontc.x = tc.x;
                cannontc.y = tc.y;
                setSpawnAnimation(sc);
                setSpawnAnimation(cannonsc);
            }

            Vector2 v = getTouchCoordinates();
            if (Gdx.input.justTouched() &&
                    pc.boundsRect.contains(v.x, v.y)
                    && !pc.state.equals(TAPPED) && !pc.state.equals(DASH)) {
                pc.state = TAPPED;
                setTappedAnimation(sc);
                setTappedAnimation(cannonsc);

                EffectUtils.playYellowStarsParticleEffect(v.x, v.y);

                tc.x++;
                ActionComponent ac = new ActionComponent();
                Actions.checkInit();
                ac.dataArray.add(Actions.moveTo(TAPPED_X, tc.y, DURATION_TAP));
                entity.add(ac);
                pc.petCannon.add(ac);
                checkPetThePetGoal();
            }
        } else {
            sc.player.speed = 0;
            cannonsc.player.speed = 0;
            if (!pc.state.equals(DASH)) {
                pc.state = IDLE;
                setIdleAnimation(sc);
                pc.petCannon.getComponent(TransformComponent.class).x = X_SPAWN_POSITION;
                tc.x = X_SPAWN_POSITION;
            }
        }
//        GameStage.sceneLoader.renderer.drawDebugRect(pc.boundsRect.x,pc.boundsRect.y,pc.boundsRect.width,pc.boundsRect.height,entity.toString());
    }

    private boolean isAnimationFinished(SpriterComponent sc) {
        return sc.player.getTime() >= sc.player.getAnimation().length - 20;
    }

    public void updateRect(PetComponent pc, TransformComponent tc, DimensionsComponent dc,
                           TransformComponent cannontc, DimensionsComponent cannondc) {
        pc.boundsRect.width = (int) dc.width * tc.scaleX + cannondc.width * cannontc.scaleX;
        pc.boundsRect.height = (int) cannondc.height * cannontc.scaleY;

        pc.boundsRect.x = (int) tc.x - dc.width;
        pc.boundsRect.y = (int) cannontc.y - cannondc.height / 2;
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

    private void checkPetThePetGoal() {
        if (GameStage.gameScript.fpc.level.getGoalByType(PET_THE_PET) != null) {
            GameStage.gameScript.fpc.level.getGoalByType(PET_THE_PET).update();
        }
    }

    private void checkPetDashGoal() {
        if (GameStage.gameScript.fpc.level.getGoalByType(PET_DASH_N_TIMES) != null) {
            GameStage.gameScript.fpc.level.getGoalByType(PET_DASH_N_TIMES).update();
        }
    }
}
