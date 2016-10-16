package com.mygdx.etf.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
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
    private static final int PET_CANNON_SHIFT_Y = 10;
    private static final int PET_CANNON_SHIFT_X = 62;
    public Random random = new Random();
    boolean canPlayAnimation = true;

    private ComponentMapper<PetComponent> mapper = ComponentMapper.getFor(PetComponent.class);

    public PetSystem() {
        super(Family.all(PetComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        PetComponent pc = mapper.get(entity);
        DimensionsComponent dcPetBody = entity.getComponent(DimensionsComponent.class);
        TransformComponent tcPetBody = entity.getComponent(TransformComponent.class);
        SpriterComponent scPetBody = entity.getComponent(SpriterComponent.class);

        DimensionsComponent cannondc = entity.getComponent(DimensionsComponent.class);
        TransformComponent cannontc = pc.petCannon.getComponent(TransformComponent.class);
        SpriterComponent cannonsc = pc.petCannon.getComponent(SpriterComponent.class);

//        DimensionsComponent dcPetHead = entity.getComponent(DimensionsComponent.class);
        TransformComponent tcPetHead = pc.petHead.getComponent(TransformComponent.class);
        SpriterComponent scPetHead = pc.petHead.getComponent(SpriterComponent.class);

        dcPetBody.width = 56;
        dcPetBody.height = 100;
        updateRect(pc, tcPetBody, dcPetBody, cannontc, cannondc);
        moveCannonWithPet(entity, pc);
        if (!isPause && !isGameOver) {
            scPetBody.player.speed = FPS;
            scPetHead.player.speed = FPS;
            cannonsc.player.speed = FPS;
            tapped(entity, pc, tcPetBody, scPetBody, cannontc, cannonsc, scPetHead, tcPetHead);
            bite(pc, scPetBody, cannonsc, scPetHead);
            spawn(pc, tcPetBody, scPetBody, cannontc, cannonsc, scPetHead, tcPetHead);
            dash(deltaTime, pc, tcPetBody, cannontc, cannonsc, tcPetHead, scPetHead, entity);
            outside(pc, tcPetBody, scPetBody, cannontc, cannonsc, scPetHead, tcPetHead, entity);
            tap(entity, cannonsc);
        } else {
            pausedState(pc, tcPetBody, scPetBody, cannonsc, scPetHead, tcPetHead);
        }
        if (!pc.enabled) {
            tcPetBody.x = FAR_FAR_AWAY_X;
            tcPetHead.x = tcPetBody.x;
            pc.petCannon.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        }

//        GameStage.sceneLoader.renderer.drawDebugRect(pc.boundsRect.x,pc.boundsRect.y,pc.boundsRect.width,pc.boundsRect.height,entity.toString());
    }

    private void tapped(Entity entity, PetComponent pc, TransformComponent tcPetBody,
                        SpriterComponent scPetBody, TransformComponent cannontc, SpriterComponent cannonsc, SpriterComponent scPetHead, TransformComponent tcPetHead) {

        if (tcPetBody.x <= X_SPAWN_POSITION + 2 && pc.state.equals(TAPPED_BACK)) {
            pc.state = IDLE;
        }

        if (pc.state.equals(TAPPED)) {
            if (tcPetBody.x == pc.previousX) {
                pc.state = TAPPED_BACK;
                System.out.println("add ac back");
                entity.remove(ActionComponent.class);
                pc.petCannon.remove(ActionComponent.class);

                ActionComponent ac = new ActionComponent();
//                ActionComponent acc = new ActionComponent();
                Actions.checkInit();
                tcPetBody.y = PetComponent.getNewPositionY();
                cannontc.y = tcPetBody.y;
                tcPetHead.y = tcPetBody.y;
                ac.dataArray.add(Actions.moveTo(X_SPAWN_POSITION, tcPetBody.y, DURATION_TAP));
//                acc.dataArray.add(Actions.moveTo(X_SPAWN_POSITION + 62, tcPetBody.y + 11, DURATION_TAP));

                entity.add(ac);
                pc.petHead.add(ac);
//                pc.petCannon.add(acc);

                setIdleAnimationStage1(scPetBody);
                setIdleAnimationStage1(scPetHead);

                if (pc.stageCounter == 0) {
                    setIdleAnimationStage1(cannonsc);
                } else if (pc.stageCounter == 1) {
                    setIdleAnimationStage2(cannonsc);
                } else if (pc.stageCounter == 2) {
                    setIdleAnimationStage3(cannonsc);
                }
            } else {
                pc.previousX = tcPetBody.x;
            }
        }
    }

    private void moveCannonWithPet(Entity entity, PetComponent pc) {
        pc.petCannon.getComponent(TransformComponent.class).x = entity.getComponent(TransformComponent.class).x + PET_CANNON_SHIFT_X;
        pc.petCannon.getComponent(TransformComponent.class).y = entity.getComponent(TransformComponent.class).y - PET_CANNON_SHIFT_Y;
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

//                if (pc.eatenBugsCounter < pc.amountBugsBeforeCharging) {
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
//                } else {
//                    canPlayAnimation = true;
//                    setDashAnimation(cannonsc);
//                    setDashAnimation(sc);
//                    pc.state = DASH;
//                    checkPetDashGoal();
//                }
            }
        }
    }

    private void spawn(PetComponent pc, TransformComponent tcPetBody, SpriterComponent scPetBody, TransformComponent cannontc, SpriterComponent cannonsc, SpriterComponent scPetHead, TransformComponent tcPetHead) {
        if (pc.state.equals(SPAWNING)) {
            tcPetBody.x = PetComponent.X_SPAWN_POSITION;
            tcPetHead.x = tcPetBody.x;
            tcPetHead.y = tcPetBody.y;
            cannontc.x = PetComponent.X_SPAWN_POSITION + PET_CANNON_SHIFT_X;
            cannontc.y = tcPetBody.y - PET_CANNON_SHIFT_Y;
            setSpawnAnimation(scPetBody);
            setSpawnAnimation(scPetHead);
            setSpawnAnimation(cannonsc);
            pc.velocity = 0;
            if (isAnimationFinished(scPetBody) && isAnimationFinished(scPetHead)) {
                pc.state = IDLE;
                canPlayAnimation = true;
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

    private void pausedState(PetComponent pc, TransformComponent tcPetBody, SpriterComponent scPetBody, SpriterComponent cannonsc, SpriterComponent scPetHead, TransformComponent tcPetHead) {
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

    private void outside(PetComponent pc, TransformComponent tcPetBody, SpriterComponent scPetBody, TransformComponent cannontc, SpriterComponent cannonsc, SpriterComponent scPetHead, TransformComponent tcPetHead, Entity entity) {
        if (tcPetBody.x < -100) {
            pc.state = OUTSIDE;
            entity.remove(ActionComponent.class);
            pc.petHead.remove(ActionComponent.class);
            tcPetBody.x = FAR_FAR_AWAY_X;
            tcPetHead.x = tcPetBody.x;
            pc.setOutsideStateDuration();
        }

        if (pc.state.equals(OUTSIDE)) {
            pc.state = SPAWNING;
            pc.eatenBugsCounter = 0;
            tcPetBody.x = X_SPAWN_POSITION;
            tcPetBody.y = PetComponent.getNewPositionY();
            tcPetHead.x = tcPetBody.x;
            tcPetHead.y = tcPetBody.y;

            cannontc.x = tcPetBody.x;
            cannontc.y = tcPetBody.y;
            setSpawnAnimation(scPetBody);
            setSpawnAnimation(scPetHead);
            setSpawnAnimation(cannonsc);
        }
    }

    private void dash(float deltaTime, PetComponent pc, TransformComponent tcPetBody, TransformComponent cannontc, SpriterComponent cannonsc, TransformComponent tcPetHead, SpriterComponent scPetHead, Entity entity) {
        if (pc.state.equals(DASH)) {
            pc.stageCounter = 0;

            SpriterComponent sc = entity.getComponent(SpriterComponent.class);

//            sc.scale -= 0.01f;
//            tcPetBody.x -= 0.04f;
//            scPetHead.scale -= 0.01f;
//            tcPetHead.x -= 0.04f;

            ActionComponent ac = new ActionComponent();
            ActionComponent acc = new ActionComponent();

//            ActionComponent ac2 = new ActionComponent();
//            ActionComponent acc2 = new ActionComponent();

//            pc.velocity += deltaTime * 3.4;
            if (tcPetBody.x < 900 && cannonsc.player.getTime() >= cannonsc.player.getAnimation().length / 2) {
                entity.remove(ac.getClass());
                pc.petHead.remove(acc.getClass());
                ActionComponent ac2 = new ActionComponent();
                ActionComponent acc2 = new ActionComponent();
                Actions.checkInit();
                ac2.dataArray.add(Actions.moveTo(-320, tcPetBody.y, 1.6f, Interpolation.linear));
                acc2.dataArray.add(Actions.moveTo(-320, tcPetBody.y, 1.6f, Interpolation.linear));
                entity.add(ac2);
                pc.petHead.add(acc2);
//                System.out.println("switching to linear");
            } else if (cannonsc.player.getTime() >= cannonsc.player.getAnimation().length / 2) {
                entity.remove(ActionComponent.class);
                pc.petHead.remove(ActionComponent.class);
                ac = new ActionComponent();
                acc = new ActionComponent();
                Actions.checkInit();
                ac.dataArray.add(Actions.moveTo(220, tcPetBody.y, 3.2f, Interpolation.pow3Out));
                acc.dataArray.add(Actions.moveTo(220, tcPetBody.y, 3.2f, Interpolation.pow3Out));
                entity.add(ac);
                pc.petHead.add(acc);
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
            if (pc.eatenBugsCounter < pc.amountBugsBeforeCharging) {
                pc.state = TAPPED;
                setDashAnimation(entity.getComponent(SpriterComponent.class));
                setDashAnimation(pc.petHead.getComponent(SpriterComponent.class));
                pc.isCollision = false;
                setTappedAnimation(entity.getComponent(SpriterComponent.class));
                setTappedAnimation(pc.petHead.getComponent(SpriterComponent.class));
                setTappedAnimation(cannonsc);
//                pc.tappedback = false;

                EffectUtils.playYellowStarsParticleEffect(v.x, v.y);

//            entity.getComponent(TransformComponent.class).x++;
                entity.remove(ActionComponent.class);
                pc.petCannon.remove(ActionComponent.class);

                ActionComponent ac = new ActionComponent();
//                ActionComponent acc = new ActionComponent();
                Actions.checkInit();
                ac.dataArray.add(Actions.moveTo(TAPPED_X, entity.getComponent(TransformComponent.class).y, DURATION_TAP));
//                acc.dataArray.add(Actions.moveTo(TAPPED_X + 62, entity.getComponent(TransformComponent.class).y + 11, DURATION_TAP));
                entity.add(ac);
//                pc.petCannon.add(acc);
                pc.petHead.add(ac);

                checkPetThePetGoal();
            } else {
                canPlayAnimation = true;
                setDashAnimation(cannonsc);
                setDashAnimation(entity.getComponent(SpriterComponent.class));
                setDashAnimation(pc.petHead.getComponent(SpriterComponent.class));
                pc.state = DASH;
                checkPetDashGoal();
            }
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
