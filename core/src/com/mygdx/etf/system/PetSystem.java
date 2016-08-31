package com.mygdx.etf.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
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
    public static final float DURATION_TAP = 1.2f;
    public Random random = new Random();
    boolean canPlayAnimation = true;

    private ComponentMapper<PetComponent> mapper = ComponentMapper.getFor(PetComponent.class);

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
            tapped(entity, pc, tc, sc, cannontc, cannonsc);
            bite(pc, sc, cannonsc);
            spawn(pc, tc, sc, cannontc, cannonsc);
            dash(deltaTime, pc, tc, cannontc, cannonsc);
            outside(pc, tc, sc, cannontc, cannonsc);
            tap(entity, cannonsc);
        } else {
            pausedState(pc, tc, sc, cannonsc);
        }
        if (!pc.enabled) {
            tc.x = FAR_FAR_AWAY_X;
            pc.petCannon.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        }
//        GameStage.sceneLoader.renderer.drawDebugRect(pc.boundsRect.x,pc.boundsRect.y,pc.boundsRect.width,pc.boundsRect.height,entity.toString());
    }

    private void tapped(Entity entity, PetComponent pc, TransformComponent tc,
                        SpriterComponent sc, TransformComponent cannontc, SpriterComponent cannonsc) {

        if (tc.x <= X_SPAWN_POSITION + 2 && pc.tappedback) {
            pc.state = IDLE;
            pc.tappedback = false;
        }
        if (pc.state.equals(TAPPED)) {
            if (tc.x >= TAPPED_X - 1) {
                pc.state = TAPPED_BACK;
                entity.remove(ActionComponent.class);
                pc.petCannon.remove(ActionComponent.class);

                ActionComponent ac = new ActionComponent();
                ActionComponent acc = new ActionComponent();
                Actions.checkInit();
                tc.y = PetComponent.getNewPositionY();
                cannontc.y = tc.y;
                ac.dataArray.add(Actions.moveTo(X_SPAWN_POSITION, tc.y, DURATION_TAP));
                acc.dataArray.add(Actions.moveTo(X_SPAWN_POSITION+62, tc.y+11, DURATION_TAP));

                entity.add(ac);
                pc.petCannon.add(acc);

                setIdleAnimationStage1(sc);

                if(pc.stageCounter == 0) {
                    setIdleAnimationStage1(cannonsc);
                }else if(pc.stageCounter == 1) {
                    setIdleAnimationStage2(cannonsc);
                }else if(pc.stageCounter == 2){
                    setIdleAnimationStage3(cannonsc);
                }
                pc.tappedback = true;
            }
        }
    }

    private void bite(PetComponent pc, SpriterComponent sc, SpriterComponent cannonsc) {
        if (pc.state.equals(BITE)) {
            setBiteAnimationStage1(sc);
            if(pc.stageCounter == 1) {
                setBiteAnimationStage1(cannonsc);
            }else{
                setBiteAnimationStage2(cannonsc);
            }

            if (isAnimationFinished(sc)) {
                pc.eatenBugsCounter++;
                pc.totalEatenBugs++;
                pc.duringGameEatenBugs++;
                pc.stageCounter++;
                System.out.println("increasing stageCounter: " + pc.stageCounter);

                if (pc.eatenBugsCounter < pc.amountBugsBeforeCharging) {
                    pc.state = IDLE;
                    setIdleAnimationStage1(sc);
                    if(pc.stageCounter == 0) {
                        setIdleAnimationStage1(cannonsc);
                    }else if(pc.stageCounter == 1) {
                        setIdleAnimationStage2(cannonsc);
                    }else if(pc.stageCounter == 2){
                        setIdleAnimationStage3(cannonsc);
                    }
                } else {
                    canPlayAnimation = true;
                    setDashAnimation(cannonsc);
                    setDashAnimation(sc);
                    pc.state = DASH;
                    checkPetDashGoal();
                }
            }
        }
    }

    private void spawn(PetComponent pc, TransformComponent tc, SpriterComponent sc, TransformComponent cannontc, SpriterComponent cannonsc) {
        if (pc.state.equals(SPAWNING)) {
            tc.x = PetComponent.X_SPAWN_POSITION;
            cannontc.x = PetComponent.X_SPAWN_POSITION + 62;
            cannontc.y = tc.y + 11;
            setSpawnAnimation(sc);
            setSpawnAnimation(cannonsc);
            pc.velocity = 0;
            if (isAnimationFinished(sc)) {
                pc.state = IDLE;
                canPlayAnimation = true;
                pc.setOutsideStateDuration();
                setIdleAnimationStage1(sc);
                if(pc.stageCounter == 0) {
                    setIdleAnimationStage1(cannonsc);
                }else if(pc.stageCounter == 1) {
                    setIdleAnimationStage2(cannonsc);
                }else if(pc.stageCounter == 2){
                    setIdleAnimationStage3(cannonsc);
                }
            }
        }
    }

    private void pausedState(PetComponent pc, TransformComponent tc, SpriterComponent sc, SpriterComponent cannonsc) {
        if (!pc.state.equals(DASH)) {
            pc.state = IDLE;
            setIdleAnimationStage1(sc);
            if(pc.stageCounter == 0) {
                setIdleAnimationStage1(cannonsc);
            }else if(pc.stageCounter == 1) {
                setIdleAnimationStage2(cannonsc);
            }else if(pc.stageCounter == 2){
                setIdleAnimationStage3(cannonsc);
            }
            pc.petCannon.getComponent(TransformComponent.class).x = PetComponent.X_SPAWN_POSITION + 62;
            pc.petCannon.getComponent(TransformComponent.class).y = tc.y + 11;
            tc.x = X_SPAWN_POSITION;
        }
        sc.player.speed = 0;
        cannonsc.player.speed = 0;
    }

    private void outside(PetComponent pc, TransformComponent tc, SpriterComponent sc, TransformComponent cannontc, SpriterComponent cannonsc) {
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
    }

    private void dash(float deltaTime, PetComponent pc, TransformComponent tc, TransformComponent cannontc, SpriterComponent cannonsc) {
        if (pc.state.equals(DASH)) {
            pc.stageCounter = 0;
            pc.velocity += deltaTime * 3.4;
            tc.x -= pc.velocity;
            if (isAnimationFinished(cannonsc)) {
                cannonsc.player.speed = 0;
                cannontc.x = FAR_FAR_AWAY_X;
            }
        }
    }

    private void tap(Entity entity, SpriterComponent cannonsc) {
        PetComponent pc = entity.getComponent(PetComponent.class);
        Vector2 v = getTouchCoordinates();
        if (Gdx.input.justTouched() &&
                pc.boundsRect.contains(v.x, v.y)
                && !pc.state.equals(TAPPED) && !pc.state.equals(DASH)) {
            pc.state = TAPPED;
            setTappedAnimation(entity.getComponent(SpriterComponent.class));
            setTappedAnimation(cannonsc);
            pc.tappedback = false;

            EffectUtils.playYellowStarsParticleEffect(v.x, v.y);

//            entity.getComponent(TransformComponent.class).x++;
            entity.remove(ActionComponent.class);
            pc.petCannon.remove(ActionComponent.class);

            ActionComponent ac = new ActionComponent();
            ActionComponent acc = new ActionComponent();
            Actions.checkInit();
            ac.dataArray.add(Actions.moveTo(TAPPED_X, entity.getComponent(TransformComponent.class).y, DURATION_TAP));
            acc.dataArray.add(Actions.moveTo(TAPPED_X+62, entity.getComponent(TransformComponent.class).y+11, DURATION_TAP));
            entity.add(ac);
            pc.petCannon.add(acc);

            checkPetThePetGoal();
        }
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

    private void setBiteAnimationStage1(SpriterComponent sc) {
        sc.player.setAnimation(3);
    }
    private void setBiteAnimationStage2(SpriterComponent sc) {
        sc.player.setAnimation(6);
    }

    private void setDashAnimation(SpriterComponent sc) {
        sc.player.setAnimation(1);
    }

    private void setIdleAnimationStage1(SpriterComponent sc) {
        sc.player.setAnimation(0);
    }
    private void setIdleAnimationStage2(SpriterComponent sc) {
        sc.player.setAnimation(5);
    }
    private void setIdleAnimationStage3(SpriterComponent sc) {
        sc.player.setAnimation(7);
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
