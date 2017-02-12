package com.fd.etf.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.fd.etf.entity.componets.PetComponent;
import com.fd.etf.stages.GameStage;
import com.fd.etf.utils.EffectUtils;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;

import java.util.ArrayList;
import java.util.List;

import static com.fd.etf.entity.componets.Goal.GoalType.PET_DASH_N_TIMES;
import static com.fd.etf.entity.componets.Goal.GoalType.PET_THE_PET;
import static com.fd.etf.entity.componets.PetComponent.State.*;
import static com.fd.etf.entity.componets.PetComponent.X_SPAWN_POSITION;
import static com.fd.etf.stages.GameScreenScript.isGameOver;
import static com.fd.etf.stages.GameScreenScript.isPause;
import static com.fd.etf.utils.EffectUtils.getTouchCoordinates;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.fd.etf.utils.GlobalConstants.FPS;

public class PetSystem extends IteratingSystem {

    private static final int TAPPED_X = 1300;
    private static final float DURATION_TAP = 1.2f;
    private static final int PET_CANNON_SHIFT_Y = 10;
    private static final int PET_CANNON_SHIFT_X = 62;
    private final GameStage gameStage;
    private int counter = 0;
//    private boolean canPlayAnimation = true;

    private ComponentMapper<PetComponent> mapper = ComponentMapper.getFor(PetComponent.class);

    public PetSystem(GameStage gameStage) {
        super(Family.all(PetComponent.class).get());
        this.gameStage = gameStage;
    }

    @Override
    protected void processEntity(Entity e, float deltaTime) {
        counter++;
        PetComponent pc = mapper.get(e);

        DimensionsComponent cannondc = e.getComponent(DimensionsComponent.class);
        TransformComponent cannontc = pc.petCannon.getComponent(TransformComponent.class);
        SpriterComponent cannonsc = pc.petCannon.getComponent(SpriterComponent.class);

        pc.petHead.getComponent(TransformComponent.class).x = e.getComponent(TransformComponent.class).x;
        pc.petHead.getComponent(TransformComponent.class).y = e.getComponent(TransformComponent.class).y;
        SpriterComponent scPetHead = pc.petHead.getComponent(SpriterComponent.class);

        e.getComponent(DimensionsComponent.class).width = 56;
        e.getComponent(DimensionsComponent.class).height = 100;
        updateRect(pc, e.getComponent(TransformComponent.class), e.getComponent(DimensionsComponent.class), cannontc, cannondc);
        moveCannonWithPet(e, pc);

        if (!isPause.get() && !isGameOver.get()) {

            e.getComponent(SpriterComponent.class).player.speed = FPS;
            scPetHead.player.speed = FPS;
            cannonsc.player.speed = FPS;

            tapped(e,
                    cannonsc,
                    scPetHead,
                    pc.petHead.getComponent(TransformComponent.class));

            bite(pc,
                    e.getComponent(SpriterComponent.class),
                    cannonsc,
                    scPetHead);

            spawn(pc,
                    e.getComponent(TransformComponent.class),
                    e.getComponent(SpriterComponent.class),
                    cannonsc,
                    scPetHead,
                    pc.petHead.getComponent(TransformComponent.class));

            dash(deltaTime,
                    pc,
                    cannonsc,
                    scPetHead,
                    e);

            outside(pc,
                    cannonsc,
                    scPetHead,
                    pc.petHead.getComponent(TransformComponent.class),
                    e);

            tap(e, cannonsc);

            if (pc.eatenBugsCounter >= pc.amountBugsBeforeCharging) {
//                canPlayAnimation = true;
                setDashAnimation(cannonsc);
                setDashAnimation(e.getComponent(SpriterComponent.class));
                setDashAnimation(pc.petHead.getComponent(SpriterComponent.class));
                pc.state = DASH;
                checkPetDashGoal();
            }
        } else {
            pausedState(pc, e.getComponent(TransformComponent.class), e.getComponent(SpriterComponent.class), cannonsc, scPetHead, pc.petHead.getComponent(TransformComponent.class));
        }
        if (!pc.enabled) {
            e.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            pc.petHead.getComponent(TransformComponent.class).x = e.getComponent(TransformComponent.class).x;
        }
    }

    private void tapped(Entity e, SpriterComponent cannonsc, SpriterComponent scPetHead, TransformComponent tcPetHead) {

        if (e.getComponent(TransformComponent.class).x <= X_SPAWN_POSITION + 2 && e.getComponent(PetComponent.class).state.equals(TAPPED_BACK)) {
            e.getComponent(PetComponent.class).state = IDLE;
        }

        if (e.getComponent(PetComponent.class).state.equals(TAPPED)) {
            if (e.getComponent(TransformComponent.class).x == e.getComponent(PetComponent.class).previousX) {
                e.getComponent(PetComponent.class).state = TAPPED_BACK;
                e.remove(ActionComponent.class);
                e.getComponent(PetComponent.class).petCannon.remove(ActionComponent.class);

                if (e.getComponent(ActionComponent.class) == null) {
                    ActionComponent ac = gameStage.sceneLoader.engine.createComponent(ActionComponent.class);
                    e.add(ac);
                }
                Actions.checkInit();
                e.getComponent(TransformComponent.class).y = PetComponent.getNewPositionY();
                tcPetHead.y = e.getComponent(TransformComponent.class).y;
                e.getComponent(ActionComponent.class).dataArray.add(
                        Actions.moveTo(X_SPAWN_POSITION, e.getComponent(TransformComponent.class).y, DURATION_TAP));

                e.getComponent(PetComponent.class).petHead.add(e.getComponent(ActionComponent.class));

                setIdleAnimationStage1(e.getComponent(SpriterComponent.class));
                setIdleAnimationStage1(scPetHead);

                if (e.getComponent(PetComponent.class).stageCounter == 0) {
                    setIdleAnimationStage1(cannonsc);
                } else if (e.getComponent(PetComponent.class).stageCounter == 1) {
                    setIdleAnimationStage2(cannonsc);
                } else if (e.getComponent(PetComponent.class).stageCounter == 2) {
                    setIdleAnimationStage3(cannonsc);
                }
            } else {
                e.getComponent(PetComponent.class).previousX = e.getComponent(TransformComponent.class).x;
            }
        }
    }

    private void moveCannonWithPet(Entity entity, PetComponent pc) {
        if (pc.state == DASH) {
            pc.petCannon.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        } else {
            pc.petCannon.getComponent(TransformComponent.class).x = entity.getComponent(TransformComponent.class).x + PET_CANNON_SHIFT_X;
            pc.petCannon.getComponent(TransformComponent.class).y = entity.getComponent(TransformComponent.class).y - PET_CANNON_SHIFT_Y;
        }
    }

    private void bite(PetComponent pc, SpriterComponent scPetBody, SpriterComponent cannonsc, SpriterComponent scPetHead) {
        if (pc.state.equals(BITE)) {
            setBiteAnimationStage1(scPetBody);
            setBiteAnimationStage1(scPetHead);
            if (pc.stageCounter == 1) {
                setBiteAnimationStage1(cannonsc);
            } else {
                setBiteAnimationStage2(cannonsc);
            }

            if (isAnimationFinished(scPetBody) && isAnimationFinished(scPetHead)) {
                pc.eatenBugsCounter++;
                pc.totalEatenBugs++;
                pc.duringGameEatenBugs++;
                pc.stageCounter++;

                pc.state = IDLE;
                setIdleAnimationStage1(scPetBody);
                setIdleAnimationStage1(scPetHead);
                if (pc.stageCounter == 0) {
                    setIdleAnimationStage1(cannonsc);
                } else if (pc.stageCounter == 1) {
                    setIdleAnimationStage2(cannonsc);
                } else if (pc.stageCounter == 2) {
                    setIdleAnimationStage2(cannonsc);
                } else if (pc.stageCounter == 3) {
                    setIdleAnimationStage3(cannonsc);
                }
            }
        }
    }

    private void spawn(PetComponent pc,
                       TransformComponent tcPetBody,
                       SpriterComponent scPetBody,
                       SpriterComponent cannonsc,
                       SpriterComponent scPetHead,
                       TransformComponent tcPetHead) {

        if (pc.state.equals(SPAWNING)) {
            tcPetBody.x = PetComponent.X_SPAWN_POSITION;
            tcPetHead.x = tcPetBody.x;
            tcPetHead.y = tcPetBody.y;
            setSpawnAnimation(scPetBody);
            setSpawnAnimation(scPetHead);
            setSpawnAnimation(cannonsc);
            pc.velocity = 0;
            if (isAnimationFinished(scPetBody) && isAnimationFinished(scPetHead)) {
                pc.state = IDLE;
//                canPlayAnimation = true;
                pc.setOutsideStateDuration();
                setIdleAnimationStage1(scPetBody);
                setIdleAnimationStage1(scPetHead);
                if (pc.stageCounter == 0) {
                    setIdleAnimationStage1(cannonsc);
                } else if (pc.stageCounter == 1) {
                    setIdleAnimationStage2(cannonsc);
                } else if (pc.stageCounter == 2) {
                    setIdleAnimationStage3(cannonsc);
                }
            }
        }
    }

    private void pausedState(PetComponent pc,
                             TransformComponent tcPetBody,
                             SpriterComponent scPetBody,
                             SpriterComponent cannonsc,
                             SpriterComponent scPetHead,
                             TransformComponent tcPetHead) {

        if (!pc.state.equals(DASH)) {
            pc.state = IDLE;
            setIdleAnimationStage1(scPetBody);
            setIdleAnimationStage1(scPetHead);
            if (pc.stageCounter == 0) {
                setIdleAnimationStage1(cannonsc);
            } else if (pc.stageCounter == 1) {
                setIdleAnimationStage2(cannonsc);
            } else if (pc.stageCounter == 2) {
                setIdleAnimationStage3(cannonsc);
            }
            pc.petCannon.getComponent(TransformComponent.class).x = PetComponent.X_SPAWN_POSITION + PET_CANNON_SHIFT_X;
            pc.petCannon.getComponent(TransformComponent.class).y = tcPetBody.y - PET_CANNON_SHIFT_Y;
            tcPetBody.x = X_SPAWN_POSITION;
            tcPetHead.x = tcPetBody.x;
        }
        scPetBody.player.speed = 0;
        scPetHead.player.speed = 0;
        cannonsc.player.speed = 0;
    }

    private void outside(PetComponent pc,
                         SpriterComponent cannonsc,
                         SpriterComponent scPetHead,
                         TransformComponent tcPetHead,
                         Entity entity) {

        if (entity.getComponent(TransformComponent.class).x < -100) {
            pc.state = OUTSIDE;
            entity.remove(ActionComponent.class);
            pc.petHead.remove(ActionComponent.class);
            entity.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            tcPetHead.x = entity.getComponent(TransformComponent.class).x;
            pc.setOutsideStateDuration();
        }

        if (pc.state.equals(OUTSIDE)) {
            pc.state = SPAWNING;
            pc.eatenBugsCounter = 0;
            entity.getComponent(TransformComponent.class).x = X_SPAWN_POSITION;
            entity.getComponent(TransformComponent.class).y = PetComponent.getNewPositionY();
            tcPetHead.x = entity.getComponent(TransformComponent.class).x;
            tcPetHead.y = entity.getComponent(TransformComponent.class).y;

            setSpawnAnimation(entity.getComponent(SpriterComponent.class));
            setSpawnAnimation(scPetHead);
            setSpawnAnimation(cannonsc);
        }
    }

    private void dash(float deltaTime,
                      PetComponent pc,
                      SpriterComponent cannonsc,
                      SpriterComponent scPetHead,
                      Entity entity) {

        if (pc.state.equals(DASH)) {
            pc.stageCounter = 0;
            if (entity.getComponent(TransformComponent.class).x < 900 && cannonsc.player.getTime() >= cannonsc.player.getAnimation().length / 2) {
                ActionComponent ac2 = entity.getComponent(ActionComponent.class);
                Actions.checkInit();
                ac2.dataArray.add(Actions.moveTo(-320, entity.getComponent(TransformComponent.class).y, 3.6f, Interpolation.linear));
                // spawning projectiles

            } else if (cannonsc.player.getTime() >= cannonsc.player.getAnimation().length / 2) {
                entity.remove(ActionComponent.class);
                pc.petHead.remove(ActionComponent.class);
                if (entity.getComponent(ActionComponent.class) == null) {
                    ActionComponent ac = gameStage.sceneLoader.engine.createComponent(ActionComponent.class);
                    entity.add(ac);
                }
                Actions.checkInit();
                entity.getComponent(ActionComponent.class).dataArray.add(Actions.moveTo(220, entity.getComponent(TransformComponent.class).y, 4.2f, Interpolation.pow3Out));
            }

            if (pc.isBiteDash) {
                setDashBiteAnimation(scPetHead);
                if (isAnimationFinished(scPetHead)) {
                    setDashAnimation(scPetHead);
                    pc.isBiteDash = false;
                }
            }
            if (isAnimationFinished(cannonsc)) {
                cannonsc.player.speed = 0;
            }

            if(counter > gameStage.gameScript.fpc.currentPet.projectileSpawnIntervalFrames) {
                EffectUtils.spawnPetProjectile(gameStage, entity.getComponent(TransformComponent.class).x, entity.getComponent(TransformComponent.class).y);
                counter = 0;
            }
        }
    }

    private void tap(Entity entity, SpriterComponent cannonsc) {
        PetComponent pc = entity.getComponent(PetComponent.class);
        Vector2 v = getTouchCoordinates();
        if (Gdx.input.justTouched() &&
                pc.boundsRect.contains(v.x, v.y)
                && !pc.state.equals(TAPPED) && !pc.state.equals(DASH)) {
            if (pc.eatenBugsCounter < pc.amountBugsBeforeCharging) {
                pc.state = TAPPED;
                setDashAnimation(entity.getComponent(SpriterComponent.class));
                setDashAnimation(pc.petHead.getComponent(SpriterComponent.class));
                pc.isCollision = false;
                setTappedAnimation(entity.getComponent(SpriterComponent.class));
                setTappedAnimation(pc.petHead.getComponent(SpriterComponent.class));
                setTappedAnimation(cannonsc);

                EffectUtils.playYellowStarsParticleEffect(gameStage, v.x, v.y);

                entity.remove(ActionComponent.class);
                pc.petCannon.remove(ActionComponent.class);

                if (entity.getComponent(ActionComponent.class) == null) {
                    ActionComponent ac = gameStage.sceneLoader.engine.createComponent(ActionComponent.class);
                    entity.add(ac);
                }
                Actions.checkInit();
                entity.getComponent(ActionComponent.class).dataArray.add(Actions.moveTo(TAPPED_X, entity.getComponent(TransformComponent.class).y, DURATION_TAP));
                checkPetThePetGoal();
            }
//            else {
////                canPlayAnimation = true;
//                setDashAnimation(cannonsc);
//                setDashAnimation(entity.getComponent(SpriterComponent.class));
//                setDashAnimation(pc.petHead.getComponent(SpriterComponent.class));
//                pc.state = DASH;
//                checkPetDashGoal();
//            }
        }
    }

    private boolean isAnimationFinished(SpriterComponent sc) {
        return sc.player.getTime() >= sc.player.getAnimation().length - 20;
    }

    private void updateRect(PetComponent pc, TransformComponent tc, DimensionsComponent dc,
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

    public void setDashBiteAnimation(SpriterComponent sc) {
        sc.player.setAnimation(5);
    }

    private void checkPetThePetGoal() {
        if (gameStage.gameScript.fpc.level.getGoalByType(PET_THE_PET) != null) {
            gameStage.gameScript.fpc.level.getGoalByType(PET_THE_PET).update();
        }
    }

    private void checkPetDashGoal() {
        if (gameStage.gameScript.fpc.level.getGoalByType(PET_DASH_N_TIMES) != null) {
            gameStage.gameScript.fpc.level.getGoalByType(PET_DASH_N_TIMES).update();
        }
    }
}
