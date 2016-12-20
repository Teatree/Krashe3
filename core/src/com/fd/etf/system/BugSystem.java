package com.fd.etf.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.fd.etf.entity.componets.BugComponent;
import com.fd.etf.stages.GameScreenScript;
import com.fd.etf.stages.GameStage;
import com.fd.etf.utils.BugPool;
import com.fd.etf.utils.EffectUtils;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;

import static com.fd.etf.entity.componets.BugComponent.*;
import static com.fd.etf.entity.componets.Goal.GoalType.*;
import static com.fd.etf.stages.GameScreenScript.*;
import static com.fd.etf.stages.GameStage.gameScript;
import static com.fd.etf.utils.GlobalConstants.*;

public class BugSystem extends IteratingSystem {

    private static final String CHARGING_ANI = "CHARGING";
    private static final String IDLE_ANI = "IDLE";
    private static final String PREPARING_ANI = "PREPARING";

    public static boolean blowUpAllBugs;
    public static int blowUpCounter;

    boolean canPlayAnimation = true;
    private ComponentMapper<BugComponent> mapper = ComponentMapper.getFor(BugComponent.class);

    public BugSystem() {
        super(Family.all(BugComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SpriteAnimationComponent sac = entity.getComponent(SpriteAnimationComponent.class);
        SpriteAnimationStateComponent sasc = entity.getComponent(SpriteAnimationStateComponent.class);

        entity.getComponent(TransformComponent.class).scaleX = BUG_SCALE;
        entity.getComponent(TransformComponent.class).scaleY = BUG_SCALE;

         if (blowUpAllBugs) {
            destroyBug(entity, entity.getComponent(TransformComponent.class));
            if(blowUpCounter<=0 && blowUpAllBugs){
                blowUpAllBugs = false;
            }
        } else if (!isPause.get() && !isGameOver.get() && isStarted ) {

            sasc.paused = false;

            BugComponent bc = mapper.get(entity);

             if (!blowUpAllBugs && !DEAD.equals(bc.state)) {
                updateRect(bc, entity.getComponent(TransformComponent.class), entity.getComponent(DimensionsComponent.class));
                updateRectScary(bc, entity.getComponent(TransformComponent.class), entity.getComponent(DimensionsComponent.class));
                moveEntity(deltaTime, entity.getComponent(TransformComponent.class), bc, sasc, sac);
                if (gameScript.fpc.flowerCollisionCheck(bc.boundsRectScary)) {
                    entity.getComponent(TransformComponent.class).scaleX += 0.5f;
//                    gameScript.fpc.state = ATTACK_BITE;
                    gameScript.fpc.isScary = true;
                }

                if (checkCollision(bc)) {
                    bc.state = DEAD;

                    gameScript.fpc.addScore(bc.points);

                    if (bc.type.equals(QUEENBEE)) {
                        angerBees();
                    }

                    BugPool.getInstance().release(entity);

                    if (gameScript.fpc.flowerCollisionCheck(bc.boundsRect)) {
                        gameScript.fpc.isCollision = true;
                        checkGoals(bc);
                    }

                    checkPetEatBugGoal(bc);

                    spawnBugJuiceBubble(bc);
                }
                if (isOutOfBounds(bc)) {
                    BugPool.getInstance().release(entity);
                    gameScript.onBugOutOfBounds();
                }
            }
//            sceneLoader.renderer.drawDebugRect(bc.boundsRect.x, bc.boundsRect.y, bc.boundsRect.width, bc.boundsRect.height, entity.toString());
//            GameStage.sceneLoader.renderer.drawDebugRect(bc.boundsRectScary.x, bc.boundsRectScary.y,
//                    bc.boundsRectScary.width, bc.boundsRectScary.height, entity.toString());
        }
        if (isPause.get()){
            sasc.paused = true;
        }
        if(isGameOver.get() || !isStarted ){
            sasc.paused = true;
            if (!blowUpAllBugs) {
                BugPool.getInstance().release(entity);
            }
        }
    }

    private void checkPetEatBugGoal(BugComponent bc) {
        if (gameScript.fpc.petCollisionCheck(bc.boundsRect)) {
            if (GameStage.gameScript.fpc.level.getGoalByType(PET_EAT_N_BUGS) != null) {
                GameStage.gameScript.fpc.level.getGoalByType(PET_EAT_N_BUGS).update();
            }
        }
    }

    private void spawnBugJuiceBubble(BugComponent bc) {
        EffectUtils.spawnBugJuiceBubble(bc.boundsRect.x + bc.boundsRect.getWidth() / 2,
                bc.boundsRect.y + bc.boundsRect.getHeight() / 2);
    }

    public static void destroyBug(Entity bugE, TransformComponent tc) {
        EffectUtils.playSplatterParticleEffect(tc.x, tc.y);
        BugPool.getInstance().release(bugE);
    }

    private boolean checkCollision(BugComponent bc) {
        return gameScript.fpc.petAndFlowerCollisionCheck(bc.boundsRect);
    }

    private void moveEntity(float deltaTime,
                            TransformComponent transformComponent,
                            BugComponent bugComponent,
                            SpriteAnimationStateComponent sasc,
                            SpriteAnimationComponent sac) {

        if (GameScreenScript.isAngeredBeesMode){
            moveAngryBee(transformComponent);
        } else {
            switch (bugComponent.type) {
                case SIMPLE:
                    bugComponent.boundsRect.setHeight(70);
                    bugComponent.boundsRect.setY(transformComponent.y + 90);
                    moveSimple(deltaTime, transformComponent, bugComponent);
                    break;
                case DRUNK:
                    moveSimple(deltaTime, transformComponent, bugComponent);
                    break;
                case CHARGER:
                    moveCharger(deltaTime, transformComponent, bugComponent, sasc, sac);
                    break;
                case BEE:
                    bugComponent.boundsRect.setHeight(70);
                    moveSimple(deltaTime, transformComponent, bugComponent);
                    break;
                case QUEENBEE:
                    moveSimple(deltaTime, transformComponent, bugComponent);
                    break;
                default:
                    break;
            }
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

        if (checkCollision(bc) || isOutOfBounds(bc)) {
            bc.state = DEAD;
            canPlayAnimation = true;
            setAnimation(IDLE_ANI, Animation.PlayMode.LOOP, sasc, sac);
            updateChargerGoals(bc);
            updateBugGoals(bc);
        }
    }

    private void updateChargerGoals(BugComponent bc) {
        if (GameStage.gameScript.fpc.level.getGoalByType(EAT_N_CHARGERS) != null && checkCollision(bc)) {
            GameStage.gameScript.fpc.level.getGoalByType(EAT_N_CHARGERS).update();
        }
    }

    private void updateBugGoals(BugComponent bc) {
        if (GameStage.gameScript.fpc.level.getGoalByType(EAT_N_BUGS) != null && checkCollision(bc)) {
            GameStage.gameScript.fpc.level.getGoalByType(EAT_N_BUGS).update();
        }
    }

    private void moveSimple(float deltaTime, TransformComponent transformComponent, BugComponent bugComponent) {
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

        update(bugComponent, transformComponent, bugComponent.reverse ? 1 - percent : percent);
    }

    public void updateRect(BugComponent bc, TransformComponent tc, DimensionsComponent dc) {
        bc.boundsRect.x = (int) tc.x + 50; //Nastya can not see this. I can.
        bc.boundsRect.y = (int) tc.y + 30;
        bc.boundsRect.width = (int) dc.width * tc.scaleX - 50;
        bc.boundsRect.height = (int) dc.height * tc.scaleY - 30;
    }

    public void updateRectScary(BugComponent bc, TransformComponent tc, DimensionsComponent dc) {
        bc.boundsRectScary.x = (int) tc.x;
        bc.boundsRectScary.y = (int) tc.y - dc.height;
        bc.boundsRectScary.width = (int) dc.width;
        bc.boundsRectScary.height = (int) dc.height * 2;
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

    public void moveAngryBee(TransformComponent tc){
        tc.x += 2.3f;
    }

    private void checkGoals(BugComponent bc) {

        updateBugGoals(bc);

        if (GameStage.gameScript.fpc.level.getGoalByType(EAT_N_BEES) != null && bc.type.equals(BEE)) {
            GameStage.gameScript.fpc.level.getGoalByType(EAT_N_BEES).update();
        }
        if (GameStage.gameScript.fpc.level.getGoalByType(EAT_N_DRUNKS) != null && bc.type.equals(DRUNK)) {
            GameStage.gameScript.fpc.level.getGoalByType(EAT_N_DRUNKS).update();
        }
        if (GameStage.gameScript.fpc.level.getGoalByType(EAT_N_SIMPLE) != null && bc.type.equals(SIMPLE)) {
            GameStage.gameScript.fpc.level.getGoalByType(EAT_N_SIMPLE).update();
        }
        if (bc.type.equals(QUEENBEE)) {
            if (GameStage.gameScript.fpc.level.getGoalByType(EAT_N_QUEENS) != null) {
                GameStage.gameScript.fpc.level.getGoalByType(EAT_N_QUEENS).update();
            }
            AchievementSystem.checkQueenSlayerAchievement();
        }
    }
}
