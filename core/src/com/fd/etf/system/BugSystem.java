package com.fd.etf.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;
import com.fd.etf.entity.componets.BugComponent;
import com.fd.etf.entity.componets.FlowerComponent;
import com.fd.etf.stages.GameStage;
import com.fd.etf.utils.BugPool;
import com.fd.etf.utils.EffectUtils;
import com.fd.etf.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;

import static com.fd.etf.entity.componets.BugComponent.*;
import static com.fd.etf.entity.componets.Goal.GoalType.*;
import static com.fd.etf.stages.GameScreenScript.*;
import static com.fd.etf.utils.GlobalConstants.*;

public class BugSystem extends IteratingSystem {

    private static final String CHARGING_ANI = "CHARGING";
    private static final String IDLE_ANI = "IDLE";
    private static final String PREPARING_ANI = "PREPARING";
    public static final String FLY_ANI = "fly";
    public static final String SCARE_ANI = "scare";

    public static boolean blowUpAllBugs;
    public static float blowUpCounter;
    public static float destroyAllBugsCounter;

    boolean canPlayAnimation = true;

    private ComponentMapper<BugComponent> mapper = ComponentMapper.getFor(BugComponent.class);

    private GameStage gameStage;

    public BugSystem(GameStage gameStage) {
        super(Family.all(BugComponent.class).get());
        this.gameStage = gameStage;
        destroyAllBugsCounter = BEES_MODE_DESTROY_LENGTH;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        SpriteAnimationComponent sac = entity.getComponent(SpriteAnimationComponent.class);
        SpriteAnimationStateComponent sasc = entity.getComponent(SpriteAnimationStateComponent.class);

        entity.getComponent(TransformComponent.class).scaleX = BUG_SCALE;
        entity.getComponent(TransformComponent.class).scaleY = BUG_SCALE;
        if(entity.getComponent(BugComponent.class).type.equals(DRUNK)) {
            entity.getComponent(TransformComponent.class).scaleX = 0.5f;
            entity.getComponent(TransformComponent.class).scaleY = 0.5f;
        }

        BugComponent bc = mapper.get(entity);
        if (bc.type == QUEENBEE) {
            entity.getComponent(TransformComponent.class).scaleX = 0.6f;
            entity.getComponent(TransformComponent.class).scaleY = 0.6f;
        }

        if (blowUpAllBugs) {
            updateBlowUpAllBugs(entity, deltaTime);
        } else if (!isPause.get() && !isGameOver.get() && isStarted) {

            sasc.paused = false;

            if (!blowUpAllBugs && !DEAD.equals(bc.state)) {
                updateRect(bc, entity.getComponent(TransformComponent.class), entity.getComponent(DimensionsComponent.class));
                updateRectScary(bc, entity.getComponent(TransformComponent.class), entity.getComponent(DimensionsComponent.class));
                moveEntity(deltaTime, entity.getComponent(TransformComponent.class), bc, sasc, sac);
                if (gameStage.gameScript.fpc.flowerCollisionCheck(bc.boundsRectScary)
                        && FlowerComponent.state.equals(FlowerComponent.State.ATTACK)) {
                    entity.getComponent(TransformComponent.class).scaleX += 0.5f;
                    if (sac.frameRangeMap.containsKey(SCARE_ANI) && !gameStage.gameScript.fpc.isScary && !bc.state.equals(SCARED)) {
                        canPlayAnimation = true;
                        bc.state = SCARED;
                        setAnimation(SCARE_ANI, Animation.PlayMode.LOOP, sasc, sac);
                        bc.scareCounter = 0.5f;
//                        System.out.println("SETTING scareCounter!!! OMG " + bc.scareCounter);
                        gameStage.gameScript.fpc.isScary = true;
                    }
                }

                if (checkCollision(bc) || checkCollisionPetProjectiles(bc)) {
                    bc.state = DEAD;

                    if (bc.type.equals(QUEENBEE)) {
                        gameStage.gameScript.angerBees();
                    }

                    if (sac.frameRangeMap.containsKey(FLY_ANI)) {
                        canPlayAnimation = true;
                        setAnimation(FLY_ANI, Animation.PlayMode.LOOP, sasc, sac);
                    }
                    gameStage.gameScript.fpc.isScary = false;

                    BugPool.getInstance(gameStage).release(entity);

                    if (gameStage.gameScript.fpc.flowerCollisionCheck(bc.boundsRect)) {
                        gameStage.gameScript.fpc.isCollision = true;
                        checkGoals(bc);
                    }

                    checkPetEatBugGoal(bc);

                    spawnBugJuiceBubble(bc);
                }
                if (isOutOfBounds(bc)) {
                    gameStage.gameScript.loseFeedback.getComponent(TransformComponent.class).y =
                            entity.getComponent(BugComponent.class).boundsRect.y;
                    BugPool.getInstance(gameStage).release(entity);
                    gameStage.gameScript.onBugOutOfBounds();
                }
            }
//            sceneLoader.renderer.drawDebugRect(bc.boundsRect.x, bc.boundsRect.y, bc.boundsRect.width, bc.boundsRect.height, entity.toString());
//            sceneLoader.renderer.drawDebugRect(bc.boundsRectScary.x, bc.boundsRectScary.y,
//                    bc.boundsRectScary.width, bc.boundsRectScary.height, entity.toString());
        }

        if (isPause.get() && !blowUpAllBugs) {
            sasc.paused = true;
        }
        if (!isStarted) {
            sasc.paused = true;
            if (!blowUpAllBugs) {
                BugPool.getInstance(gameStage).release(entity);
            }
        }

        if (bc.scareCounter <= 0 && bc.state.equals(SCARED) && sac.frameRangeMap.containsKey(FLY_ANI)) {
            canPlayAnimation = true;
            setAnimation(FLY_ANI, Animation.PlayMode.LOOP, sasc, sac);
//            System.out.println("SETTING FLY ANIMATION!!! OMG " + bc.scareCounter);
        }
    }

    public static void blowUpAllBugs() {
        blowUpAllBugs = true;
        blowUpCounter = GlobalConstants.BEES_MODE_BLOW_UP_LENGTH;
//        System.out.println("HOW MANY TIMES AM I CALLED?");
    }

    private void updateBlowUpAllBugs(Entity entity, float deltaTime) {
        SpriteAnimationComponent sac = entity.getComponent(SpriteAnimationComponent.class);
        SpriteAnimationStateComponent sasc = entity.getComponent(SpriteAnimationStateComponent.class);
        BugComponent bc = entity.getComponent(BugComponent.class);

        if (sac.frameRangeMap.containsKey("death") && !bc.isPlayingDeathAnimation) {
            canPlayAnimation = true;
            setAnimation("death", Animation.PlayMode.NORMAL, sasc, sac);
            bc.isPlayingDeathAnimation = true;
            sasc.paused = false;
        }
        if (blowUpCounter <= 0) {
            destroyBug(entity);
            bc.isPlayingDeathAnimation = false;
        } else {
            destroyAllBugsCounter = BEES_MODE_DESTROY_LENGTH;
        }
    }

    private void checkPetEatBugGoal(BugComponent bc) {
        if (gameStage.gameScript.fpc.petCollisionCheck(bc.boundsRect)) {
            if (gameStage.gameScript.fpc.level.getGoalByType(PET_EAT_N_BUGS) != null) {
                gameStage.gameScript.fpc.level.getGoalByType(PET_EAT_N_BUGS).update();
            }
        }
    }

    private void spawnBugJuiceBubble(BugComponent bc) {
        EffectUtils.spawnBugJuiceBubble(bc.points, gameStage.gameScript.gameStage, bc.boundsRect.x + bc.boundsRect.getWidth() / 2,
                bc.boundsRect.y + bc.boundsRect.getHeight() / 2);
    }

    public void destroyBug(Entity bugE) {
        spawnBugJuiceBubble(bugE.getComponent(BugComponent.class));
        BugPool.getInstance(gameStage).release(bugE);

        if (bugE.getComponent(SpriteAnimationComponent.class).frameRangeMap.containsKey(FLY_ANI)) {
            canPlayAnimation = true;
            setAnimation(FLY_ANI, Animation.PlayMode.LOOP, bugE.getComponent(SpriteAnimationStateComponent.class), bugE.getComponent(SpriteAnimationComponent.class));
        }
    }

    private boolean checkCollision(BugComponent bc) {
        return gameStage.gameScript.fpc.petAndFlowerCollisionCheck(bc.boundsRect);
    }

    private boolean checkCollisionPetProjectiles(BugComponent bc) {
        boolean collides = false;
        if (projectileBounds != null) {
            for (Rectangle r : projectileBounds) {
                collides = r.overlaps(bc.boundsRect);
            }
        }
        return collides;
    }

    private void moveEntity(float deltaTime,
                            TransformComponent transformComponent,
                            BugComponent bugComponent,
                            SpriteAnimationStateComponent sasc,
                            SpriteAnimationComponent sac) {

        switch (bugComponent.type) {
            case SIMPLE:
                bugComponent.boundsRect.setHeight(70);
                bugComponent.boundsRect.setY(transformComponent.y + 90);
                moveSimple(deltaTime, transformComponent, bugComponent, sasc, sac);
                break;
            case DRUNK:
                moveSimple(deltaTime, transformComponent, bugComponent, sasc, sac);
                break;
            case CHARGER:
                moveCharger(deltaTime, transformComponent, bugComponent, sasc, sac);
                break;
            case BEE:
                bugComponent.boundsRect.setHeight(70);
                moveSimple(deltaTime, transformComponent, bugComponent, sasc, sac);
                break;
            case QUEENBEE:
                moveSimple(deltaTime, transformComponent, bugComponent, sasc, sac);
                break;
            default:
                break;
        }
    }

    private void moveCharger(float deltaTime,
                             TransformComponent tc,
                             BugComponent bc,
                             SpriteAnimationStateComponent sasc,
                             SpriteAnimationComponent sac) {

        bc.counter--;
        // Move
        tc.x += bc.velocity;

        // Idle
        if (bc.state.equals(IDLE)) {
            setAnimation(IDLE_ANI, Animation.PlayMode.LOOP, sasc, sac);
            bc.velocity = deltaTime * bc.IDLE_MVMNT_SPEED;
            if (bc.counter == 0) {
                canPlayAnimation = true;
                setAnimation(PREPARING_ANI, Animation.PlayMode.LOOP, sasc, sac);
                bc.counter = PREPARATION_TIME;
                bc.state = PREPARING;
            }
        }
        // Preparing
        else if (bc.state.equals(PREPARING)) {
            bc.velocity = deltaTime * bc.PREPARING_MVMNT_SPEED;
            if (bc.counter == 0) {
                bc.state = CHARGING;
                canPlayAnimation = true;
                setAnimation(CHARGING_ANI, Animation.PlayMode.LOOP, sasc, sac);
                bc.velocity = deltaTime * bc.CHARGING_MVMNT_SPEED;
            }
        }
        // Charging
        else if (CHARGING.equals(bc.state)) {
            bc.velocity += deltaTime * 3.4;
        }

        // Checking collision
        if (checkCollision(bc) || isOutOfBounds(bc) || checkCollisionPetProjectiles(bc)) {
            bc.state = DEAD;
            canPlayAnimation = true;
            setAnimation(IDLE_ANI, Animation.PlayMode.LOOP, sasc, sac);
            updateChargerGoals(bc);
            updateBugGoals(bc);
        }
    }

    private void updateChargerGoals(BugComponent bc) {
        if (gameStage.gameScript.fpc.level.getGoalByType(EAT_N_CHARGERS) != null && (checkCollision(bc) || checkCollisionPetProjectiles(bc))) {
            gameStage.gameScript.fpc.level.getGoalByType(EAT_N_CHARGERS).update();
        }
    }

    private void updateBugGoals(BugComponent bc) {
        if (gameStage.gameScript.fpc.level.getGoalByType(EAT_N_BUGS) != null && (checkCollision(bc) || checkCollisionPetProjectiles(bc))) {
            gameStage.gameScript.fpc.level.getGoalByType(EAT_N_BUGS).update();
        }
    }

    private void moveSimple(float deltaTime, TransformComponent transformComponent, BugComponent bugComponent, SpriteAnimationStateComponent sasc,
                            SpriteAnimationComponent sac) {
        if (!bugComponent.began) {
            begin(bugComponent, transformComponent);
            bugComponent.began = true;
        }
        bugComponent.time += deltaTime;
        bugComponent.complete = bugComponent.time >= bugComponent.duration;
        float percent;
        if (bugComponent.complete) {
            percent = 1;
        } else {
            percent = bugComponent.time / bugComponent.duration;
            if (bugComponent.interpolation != null) percent = bugComponent.interpolation.apply(percent);
        }

        if (bugComponent.state.equals(SCARED)) {
            setAnimation(SCARE_ANI, Animation.PlayMode.LOOP, sasc, sac);
            bugComponent.scareCounter -= deltaTime;
        }

        update(bugComponent, transformComponent, bugComponent.reverse ? 1 - percent : percent);
    }

    public void updateRect(BugComponent bc, TransformComponent tc, DimensionsComponent dc) {
        bc.boundsRect.x = (int) tc.x + 50; //Nastya can not see this. I can.
        bc.boundsRect.y = (int) tc.y + 30;
        bc.boundsRect.width = (int) dc.width * tc.scaleX - 50;
        bc.boundsRect.height = (int) dc.height * tc.scaleY - 30;
        if (bc.type.equals(BEE)) {
            bc.boundsRect.x = (int) tc.x + 250; //Nastya can not see this. I can.
            bc.boundsRect.y = (int) tc.y + 130;
            bc.boundsRect.width = (int) dc.width * tc.scaleX - 330;
            bc.boundsRect.height = (int) dc.height * tc.scaleY - 300;
        }
    }

    public void updateRectScary(BugComponent bc, TransformComponent tc, DimensionsComponent dc) {
        bc.boundsRectScary.x = (int) tc.x + dc.width / 4;
        bc.boundsRectScary.y = (int) tc.y;
        bc.boundsRectScary.width = (int) dc.width / 2;
        bc.boundsRectScary.height = (int) dc.height;
    }

    public void update(BugComponent uc, TransformComponent tc, float percent) {
        float step = percent <= 0.3 ? 5 : 0;
        float x = uc.startX + (uc.endX - uc.startX) * percent * (percent + step);

        double y = (Math.sin(x / 100) * uc.amplitude) + uc.startY;
        setPosition(tc, x, (float) y);
    }

    public boolean isOutOfBounds(BugComponent bc) {
        return bc.boundsRect.getX() >= 1200;
    }

    public void setAnimation(String animationName, Animation.PlayMode mode, SpriteAnimationStateComponent sasComponent, SpriteAnimationComponent saComponent) {
        if (canPlayAnimation) {
            sasComponent.set(saComponent.frameRangeMap.get(animationName), FPS, mode);
            canPlayAnimation = false;
        }
    }

    protected void begin(BugComponent uc, TransformComponent tc) {
        uc.startX = tc.x;
        uc.startY = tc.y;
    }

    public void setPosition(TransformComponent tc, float x, float y) {
        tc.x = x;
        tc.y = y;
    }

    private void checkGoals(BugComponent bc) {

        updateBugGoals(bc);

        if (gameStage.gameScript.fpc.level.getGoalByType(EAT_N_BEES) != null && bc.type.equals(BEE)) {
            gameStage.gameScript.fpc.level.getGoalByType(EAT_N_BEES).update();
        }
        if (gameStage.gameScript.fpc.level.getGoalByType(EAT_N_DRUNKS) != null && bc.type.equals(DRUNK)) {
            gameStage.gameScript.fpc.level.getGoalByType(EAT_N_DRUNKS).update();
        }
        if (gameStage.gameScript.fpc.level.getGoalByType(EAT_N_SIMPLE) != null && bc.type.equals(SIMPLE)) {
            gameStage.gameScript.fpc.level.getGoalByType(EAT_N_SIMPLE).update();
        }
        if (bc.type.equals(QUEENBEE)) {
            if (gameStage.gameScript.fpc.level.getGoalByType(EAT_N_QUEENS) != null) {
                gameStage.gameScript.fpc.level.getGoalByType(EAT_N_QUEENS).update();
            }
            AchievementSystem.checkQueenSlayerAchievement();
        }
    }
}
