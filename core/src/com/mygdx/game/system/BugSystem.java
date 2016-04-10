package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.mygdx.game.entity.componets.BugComponent;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.entity.componets.Goal;
import com.mygdx.game.entity.componets.Upgrade;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.stages.ui.GameOverDialog;
import com.mygdx.game.utils.BugPool;
import com.mygdx.game.utils.EffectUtils;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import static com.mygdx.game.entity.componets.BugComponent.*;
import static com.mygdx.game.entity.componets.FlowerComponent.State.ATTACK_BITE;
import static com.mygdx.game.entity.componets.FlowerComponent.state;
import static com.mygdx.game.entity.componets.Goal.GoalType.*;
import static com.mygdx.game.stages.GameScreenScript.*;
import static com.mygdx.game.stages.GameStage.gameScript;
import static com.mygdx.game.stages.GameStage.sceneLoader;
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
        SpriterComponent sac = ComponentRetriever.get(entity, SpriterComponent.class);

        if (!isStarted) {
            BugPool.getInstance().release(entity);
        }

        if (!isPause && !isGameOver && isStarted) {

            sac.player.speed = 24;

            DimensionsComponent dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);
            sac.scale = BUG_SCALE;
            TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
//            transformComponent.scaleX = BUG_SCALE;
//            transformComponent.scaleY = BUG_SCALE;
            FlowerPublicComponent fcc = fMapper.get(entity);
            BugComponent bc = mapper.get(entity);

            if (BugSpawnSystem.isBlewUp() || Upgrade.blowUpAllBugs) {
                destroyBug(entity, transformComponent);

            } else if (bc.state != DEAD) {
                updateRect(bc, transformComponent, dimensionsComponent, sac);
                updateRectScary(bc, transformComponent, dimensionsComponent, sac);

                moveEntity(deltaTime, transformComponent, bc, sac);
                
                if (fcc.flowerCollisionCheck(bc.boundsRectScary)) {
                    sac.scale+= 0.5f;
//                    fcc.state = ATTACK_BITE;
                    fcc.isScary = true;
                }

                if (checkFlowerCollision(fcc, bc)) {
                    bc.state = DEAD;

                    fcc.addScore(bc.points);

                    if (bc.type.equals(QUEENBEE)) {
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
                    gameScript.onBugOutOfBounds();
                }
            }
            sceneLoader.renderer.drawDebugRect(bc.boundsRect.x, bc.boundsRect.y, bc.boundsRect.width, bc.boundsRect.height, entity.toString());
            sceneLoader.renderer.drawDebugRect(bc.boundsRectScary.x, bc.boundsRectScary.y,
                    bc.boundsRectScary.width, bc.boundsRectScary.height, entity.toString());
        } else {
            sac.player.speed = 24;
            if (GameOverDialog.releaseAllBugs()) {
                BugPool.getInstance().release(entity);
            }
        }

    }

    private void checkPetEatBugGoal(FlowerPublicComponent fcc, BugComponent bc) {
        if (fcc.petCollisionCheck(bc.boundsRect)) {
            if (GameStage.gameScript.fpc.level.getGoalByType(PET_EAT_N_BUGS) != null) {
                GameStage.gameScript.fpc.level.getGoalByType(PET_EAT_N_BUGS).update();
            }
        }
    }

    private void spawnBugJuiceBubble(BugComponent bc) {
        EffectUtils.spawnBugJuiceBubble(bc.boundsRect.x + bc.boundsRect.getWidth() / 2,
                bc.boundsRect.y + bc.boundsRect.getHeight() / 2);
    }

    private void destroyBug(Entity bugE, TransformComponent tc) {
        EffectUtils.playSplatterParticleEffect(tc.x, tc.y);
        BugPool.getInstance().release(bugE);
    }

    private boolean checkFlowerCollision(FlowerPublicComponent fcc, BugComponent bc) {
        return fcc.petAndFlowerCollisionCheck(bc.boundsRect);
    }

    private void moveEntity(float deltaTime,
                            TransformComponent transformComponent,
                            BugComponent bugComponent,
                            SpriterComponent sac) {

        switch (bugComponent.type) {
            case SIMPLE:
                moveSimple(deltaTime, transformComponent, bugComponent);
                break;
            case DRUNK:
                moveSimple(deltaTime, transformComponent, bugComponent);
                break;
            case CHARGER:
                moveCharger(deltaTime, transformComponent, bugComponent, sac);
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
                             SpriterComponent sac) {

        bc.counter--;
        // Move
        tc.x += bc.velocity;

        // Idle
        if (bc.state.equals(IDLE)) {
            setAnimation(IDLE_ANI, Animation.PlayMode.LOOP, sac);
            bc.velocity = deltaTime * IDLE_MVMNT_SPEED;
            if (bc.counter == 0) {
                canPlayAnimation = true;
                setAnimation(PREPARING_ANI, Animation.PlayMode.LOOP, sac);
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
                setAnimation(CHARGING_ANI, Animation.PlayMode.LOOP, sac);
                bc.velocity = deltaTime * CHARGING_MVMNT_SPEED;
            }
        }
        // Charging
        else if (CHARGING.equals(bc.state)) {
            bc.velocity += deltaTime * 3.4;
        }

        if (checkFlowerCollision(GameStage.gameScript.fpc, bc) || isOutOfBounds(bc)) {
            bc.state = DEAD;
            canPlayAnimation = true;
            setAnimation(IDLE_ANI, Animation.PlayMode.LOOP, sac);
            updateChargerGoals(bc);
            updateBugGoals(bc);
        }
    }

    private void updateChargerGoals(BugComponent bc) {
        if (GameStage.gameScript.fpc.level.getGoalByType(EAT_N_CHARGERS) != null && checkFlowerCollision(GameStage.gameScript.fpc, bc)) {
            GameStage.gameScript.fpc.level.getGoalByType(EAT_N_CHARGERS).update();
        }
    }

    private void updateBugGoals(BugComponent bc) {
        if (GameStage.gameScript.fpc.level.getGoalByType(EAT_N_BUGS) != null && checkFlowerCollision(GameStage.gameScript.fpc, bc)) {
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

    public void updateRect(BugComponent bc, TransformComponent tc, DimensionsComponent dc, SpriterComponent sac) {
        bc.boundsRect.x = (int) tc.x - ((int) dc.width/2 * sac.scale)/2;
        bc.boundsRect.y = (int) tc.y - ((int) dc.height/2 * sac.scale)/2;
        bc.boundsRect.width = (int) dc.width/2 * sac.scale;
        bc.boundsRect.height = (int) dc.height/2 * sac.scale;
    }

    public void updateRectScary(BugComponent bc, TransformComponent tc, DimensionsComponent dc, SpriterComponent sac) {
        bc.boundsRectScary.x = (int) tc.x - ((int) dc.width * sac.scale);
        bc.boundsRectScary.y = (int) tc.y - ((int) dc.height * sac.scale)*2;
        bc.boundsRectScary.width = (int) dc.width * sac.scale*2;
        bc.boundsRectScary.height = (int) dc.height * sac.scale*4;
    }

    public void update(BugComponent uc, TransformComponent tc, float percent) {
        float step = percent <= 0.3 ? 5 : 0;
        float x = uc.startX + (uc.endX - uc.startX) * percent * (percent + step);

        double y = (Math.sin(x / 100) * 50) + uc.startY;
        setPosition(tc, x, (float) y);
    }

    public boolean isOutOfBounds(BugComponent bc) {
        return bc.boundsRect.getX() >= 1200;
    }

    public void setAnimation(String animationName, Animation.PlayMode mode, SpriterComponent spriterC) {
        if (canPlayAnimation) {
            spriterC.player.setAnimation(animationName);
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

        for (Goal g : GameStage.gameScript.fpc.level.getGoals()) {
            g.updateInARowGoals(bc);
        }

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
        if (GameStage.gameScript.fpc.level.getGoalByType(EAT_N_QUEENS) != null && bc.type.equals(QUEENBEE)) {
            GameStage.gameScript.fpc.level.getGoalByType(EAT_N_QUEENS).update();
        }

        if (GameStage.gameScript.fpc.level.getGoalByType(EAT_N_BUG_LOWER) != null && GameStage.gameScript.fpc.boundsRect.getY() < 400) {
            GameStage.gameScript.fpc.level.getGoalByType(EAT_N_BUG_LOWER).update();
        }
        if (GameStage.gameScript.fpc.level.getGoalByType(EAT_N_BUG_UPPER) != null && GameStage.gameScript.fpc.boundsRect.getY() > 400) {
            GameStage.gameScript.fpc.level.getGoalByType(EAT_N_BUG_UPPER).update();
        }
    }
}
