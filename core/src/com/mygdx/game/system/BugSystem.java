package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.mygdx.game.Main;
import com.mygdx.game.entity.componets.BugComponent;
import com.mygdx.game.entity.componets.BugComponent.BugType;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.entity.componets.Goal;
import com.mygdx.game.entity.componets.Upgrade;
import com.mygdx.game.stages.ui.GameOverDialog;
import com.mygdx.game.utils.BugPool;
import com.mygdx.game.utils.EffectUtils;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import static com.mygdx.game.entity.componets.BugComponent.State.*;
import static com.mygdx.game.entity.componets.Goal.GoalType.*;
import static com.mygdx.game.stages.GameScreenScript.*;
import static com.mygdx.game.stages.GameStage.sceneLoader;
import static com.mygdx.game.utils.BugPool.*;
import static com.mygdx.game.utils.GlobalConstants.*;

public class BugSystem extends IteratingSystem {

    public static final String CHARGING_ANI = "Charging";
    public static final String IDLE_ANI = "Idle";
    public static final String PREPARING_ANI = "Preparing";

    boolean canPlayAnimation = true;
    private ComponentMapper<BugComponent> mapper = ComponentMapper.getFor(BugComponent.class);
    private ComponentMapper<FlowerPublicComponent> fMapper = ComponentMapper.getFor(FlowerPublicComponent.class);

    public BugSystem() {
        super(Family.all(BugComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SpriteAnimationComponent sac = ComponentRetriever.get(entity, SpriteAnimationComponent.class);
        SpriteAnimationStateComponent sasc = ComponentRetriever.get(entity, SpriteAnimationStateComponent.class);

        if(!isStarted){
            BugPool.getInstance().release(entity);
        }

        if (!isPause && !isGameOver && isStarted) {

            sasc.paused = false;

            DimensionsComponent dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);
            TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
            transformComponent.scaleX = BUG_SCALE;
            transformComponent.scaleY = BUG_SCALE;
            FlowerPublicComponent fcc = fMapper.get(entity);
            BugComponent bc = mapper.get(entity);

            if (BugSpawnSystem.isBlewUp() || Upgrade.blowUpAllBugs) {
                destroyBug(entity, transformComponent);
                Upgrade.blowUpAllBugs = false;

            } else if (bc.state != DEAD) {
                updateRect(bc, transformComponent, dimensionsComponent);
                moveEntity(deltaTime, transformComponent, bc, sasc, sac);

                if (checkFlowerCollision(fcc, bc)) {
                    bc.state = DEAD;

                    fcc.addScore(bc.points);
//                    fcc.score += fcc.haveBugJuiceDouble() ? 2 * bc.points : bc.points;
//                    fcc.totalScore += bc.points;

                    if (bc.type.equals(BugType.QUEENBEE)) {
                        angerBees();
                    }
                    BugPool.getInstance().release(entity);

                    if (fcc.flowerCollisionCheck(bc.boundsRect)) {
                        fcc.isCollision = true;
                        checkGoals(bc);
                    }

                    checkPetEatBugGoal(fcc, bc);

                    spawnBugJuiceBubble(bc);
                }
                if (isOutOfBounds(bc)) {
                    BugPool.getInstance().release(entity);
                    showGameOver();
                }
            }
            sceneLoader.renderer.drawDebugRect(bc.boundsRect.x, bc.boundsRect.y, bc.boundsRect.width, bc.boundsRect.height, entity.toString());
        } else {
            sasc.paused = true;
            if (GameOverDialog.releaseAllBugs()) {
                BugPool.getInstance().release(entity);
            }
        }

    }

    private void checkPetEatBugGoal(FlowerPublicComponent fcc, BugComponent bc) {
        if (fcc.petCollisionCheck(bc.boundsRect)) {
            if (fpc.level.getGoalByType(PET_EAT_N_BUGS) != null) {
                fpc.level.getGoalByType(PET_EAT_N_BUGS).update();
            }
        }
    }

    private void spawnBugJuiceBubble(BugComponent bc) {
        EffectUtils.spawnBugJuiceBubble(bc.boundsRect.x + bc.boundsRect.getWidth() / 2,
                bc.boundsRect.y + bc.boundsRect.getHeight() / 2);
    }

    private void destroyBug(Entity bugE, TransformComponent tc) {
//        EffectUtils.playSplatterParticleEffect(tc.x, tc.y);
        BugPool.getInstance().release(bugE);
    }

    private boolean checkFlowerCollision(FlowerPublicComponent fcc, BugComponent bc) {
        return fcc.petAndFlowerCollisionCheck(bc.boundsRect);
    }

    private void moveEntity(float deltaTime,
                            TransformComponent transformComponent,
                            BugComponent bugComponent,
                            SpriteAnimationStateComponent sasc,
                            SpriteAnimationComponent sac) {

        switch (bugComponent.type.toString()) {
            case SIMPLE:
                moveSimple(deltaTime, transformComponent, bugComponent);
                break;
            case DRUNK:
                moveSimple(deltaTime, transformComponent, bugComponent);
                break;
            case CHARGER:
                moveCharger(deltaTime, transformComponent, bugComponent, sasc, sac);
                break;
            case BEE:
                moveSimple(deltaTime, transformComponent, bugComponent);
                break;
            case QUEENBEE:
                moveSimple(deltaTime, transformComponent, bugComponent);
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
            bc.velocity = deltaTime * IDLE_MVMNT_SPEED;
            if (bc.counter == 0) {
                canPlayAnimation = true;
                setAnimation(PREPARING_ANI, Animation.PlayMode.LOOP, sasc, sac);
                bc.counter = PREPARATION_TIME;
                bc.state = PREPARING;
            }
        }
        // Preparing
        else if (bc.state.equals(PREPARING)) {
            bc.velocity = deltaTime * PREPARING_MVMNT_SPEED;
            if (bc.counter == 0) {
                bc.state = CHARGING;
                canPlayAnimation = true;
                setAnimation(CHARGING_ANI, Animation.PlayMode.LOOP, sasc, sac);
                bc.velocity = deltaTime * CHARGING_MVMNT_SPEED;
            }
        }
        // Charging
        else if (CHARGING.equals(bc.state)) {
            bc.velocity += deltaTime * 3.4;
        }

        if (checkFlowerCollision(fpc, bc) || isOutOfBounds(bc)) {
            bc.state = DEAD;
            canPlayAnimation = true;
            setAnimation(IDLE_ANI, Animation.PlayMode.LOOP, sasc, sac);
            if (fpc.level.getGoalByType(EAT_N_BUGS) != null && checkFlowerCollision(fpc, bc)) {
                fpc.level.getGoalByType(EAT_N_BUGS).update();
            }
            if (fpc.level.getGoalByType(EAT_N_CHARGERS) != null && checkFlowerCollision(fpc, bc)) {
                fpc.level.getGoalByType(EAT_N_CHARGERS).update();
            }
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
        bc.boundsRect.x = (int) tc.x + 50; //Nastya can not see this
        bc.boundsRect.y = (int) tc.y + 30;
        bc.boundsRect.width = (int) dc.width * tc.scaleX - 50;
        bc.boundsRect.height = (int) dc.height * tc.scaleY - 30;
    }

    public void update(BugComponent uc, TransformComponent tc, float percent) {
        float step = percent <= 0.3 ? 5 : 0;
        float x = uc.startX + (uc.endX - uc.startX) * percent * (percent + step);

        double y = (Math.sin(x / 100) * 50) + uc.startY;
        setPosition(tc, x, (float) y);
    }

    public boolean isOutOfBounds(BugComponent bc) {
        return bc.boundsRect.getX() >= Main.viewportWidth;
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

        for (Goal g : fpc.level.getGoals()) {
            g.updateInARowGoals(bc);
        }

        if (fpc.level.getGoalByType(EAT_N_BUGS) != null) {
            fpc.level.getGoalByType(EAT_N_BUGS).update();
        }
        if (fpc.level.getGoalByType(EAT_N_BEES) != null && bc.type.equals(BugType.BEE)) {
            fpc.level.getGoalByType(EAT_N_BEES).update();
        }
        if (fpc.level.getGoalByType(EAT_N_DRUNKS) != null && bc.type.equals(BugType.DRUNK)) {
            fpc.level.getGoalByType(EAT_N_DRUNKS).update();
        }
        if (fpc.level.getGoalByType(EAT_N_SIMPLE) != null && bc.type.equals(BugType.SIMPLE)) {
            fpc.level.getGoalByType(EAT_N_SIMPLE).update();
        }
        if (fpc.level.getGoalByType(EAT_N_QUEENS) != null && bc.type.equals(BugType.QUEENBEE)) {
            fpc.level.getGoalByType(EAT_N_QUEENS).update();
        }

        if (fpc.level.getGoalByType(EAT_N_BUG_LOWER) != null && fpc.boundsRect.getY() < 400) {
            fpc.level.getGoalByType(EAT_N_BUG_LOWER).update();
        }
        if (fpc.level.getGoalByType(EAT_N_BUG_UPPER) != null && fpc.boundsRect.getY() > 400) {
            fpc.level.getGoalByType(EAT_N_BUG_UPPER).update();
        }
    }
}
