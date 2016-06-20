package com.mygdx.etf.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.etf.entity.componets.ButterflyComponent;
import com.mygdx.etf.entity.componets.CocoonComponent;
import com.mygdx.etf.stages.GameScreenScript;
import com.mygdx.etf.stages.GameStage;
import com.mygdx.etf.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.Random;

import static com.mygdx.etf.entity.componets.CocoonComponent.*;
import static com.mygdx.etf.entity.componets.CocoonComponent.State.*;
import static com.mygdx.etf.entity.componets.Goal.GoalType.DESTROY_N_COCOON;
import static com.mygdx.etf.stages.GameScreenScript.*;
import static com.mygdx.etf.stages.GameStage.*;
import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_Y;

public class CocoonSystem extends IteratingSystem {

    public static final String BUTTERFLY_ANI = "butterfly";

    ItemWrapper gameItem;

    private ComponentMapper<CocoonComponent> mapper = ComponentMapper.getFor(CocoonComponent.class);
    private SpriterComponent sc = new SpriterComponent();

    public CocoonSystem(GameScreenScript gameScript) {
        super(Family.all(CocoonComponent.class).get());
        this.gameItem = gameScript.gameItem;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        sc = entity.getComponent(SpriterComponent.class);
        if (!isStarted) {
            entity.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
        }

        if (!isPause && !isGameOver && isStarted) {
            CocoonComponent cc = mapper.get(entity);
            DimensionsComponent dc = ComponentRetriever.get(entity, DimensionsComponent.class);
            TransformComponent tc = ComponentRetriever.get(entity, TransformComponent.class);

            updateRect(cc, tc, dc);
            act(cc, entity, deltaTime);

            if (checkCollision(cc) && !cc.canHit) {
                hit(cc);
            }

        } else {
            sc.player.speed = 0;
        }
    }

    public void act(CocoonComponent cc, Entity entity, float delta) {

        if ("GAME".equals(GlobalConstants.CUR_SCREEN)) {

            if (cc.state.equals(SPAWNING)) {

                if (isAnimationFinished()) {
                    cc.state = IDLE;
                    sc.player.setAnimation(1);
                    sc.player.speed = 0;
                }
            }

            if (cc.state.equals(HIT)) {
                if (isAnimationFinished()) {
                    if (cc.hitCounter >= CocoonComponent.COCOON_HIT_AMOUNT) {
                        cc.state = DEAD;
                        spawnButterfly();
                        checkCocoonGoal();
                    } else {
                        cc.state = IDLE;
                        if (cc.hitCounter + 1 < 4) {
                            sc.player.setAnimation(cc.hitCounter + 1);
                        }
                        sc.player.speed = 0;
                    }
                }
            }

            if (cc.state.equals(DEAD)) {
                sc.player.setAnimation(3);
                if (isAnimationFinished()) {
                    entity.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
                }
            }
        }
    }

    public boolean isAnimationFinished() {
        return sc.player.getTime() >= sc.player.getAnimation().length - 20;
    }

    public void hit(CocoonComponent cc) {

        cc.canHit = true;
        if (!cc.state.equals(DEAD)) {
            cc.state = HIT;
//            cc.hitCounter+=1;
            cc.canHit = true;
            if (!isPause && !isGameOver) {
                sc.player.speed = 24;
            }
            if (cc.hitCounter <= 3) {
                sc.player.setAnimation(cc.hitCounter++);
            }
        }
    }

    public void updateRect(CocoonComponent cc, TransformComponent tc, DimensionsComponent dc) {
        cc.boundsRect.x = (int) tc.x;
        cc.boundsRect.y = 793;
        cc.boundsRect.width = (int) dc.width * tc.scaleX;
        cc.boundsRect.height = (int) dc.height * tc.scaleY;
//        sceneLoader.renderer.drawDebug(cc.boundsRect.x,cc.boundsRect.y,cc.boundsRect.width,cc.boundsRect.height);
    }

    private boolean checkCollision(CocoonComponent cc) {
        if (!cc.boundsRect.overlaps(gameScript.fpc.boundsRect)) {
            cc.canHit = false;
        }
        return cc.boundsRect.overlaps(gameScript.fpc.boundsRect);
    }

    private void spawnButterfly() {
        Entity butterflyEntity = gameItem.getChild(BUTTERFLY_ANI).getEntity();
        TransformComponent tc = butterflyEntity.getComponent(TransformComponent.class);
        tc.x = 700;
        tc.y = 750;

        ButterflyComponent bc = new ButterflyComponent();
        butterflyEntity.add(bc);
    }

    public void checkCocoonGoal() {
        if (GameStage.gameScript.fpc.level.getGoalByType(DESTROY_N_COCOON) != null) {
            GameStage.gameScript.fpc.level.getGoalByType(DESTROY_N_COCOON).update();
        }
    }

    public static float getNextSpawnInterval() {
        Random r = new Random();
        float randCoefficient = currentCocoonMultiplier.minSpawnCoefficient +
                r.nextFloat() * (currentCocoonMultiplier.maxSpawnCoefficient - currentCocoonMultiplier.minSpawnCoefficient);
        return SPAWN_INTERVAL_BASE * randCoefficient;
    }

    public static void resetSpawnCoefficients() {
        currentCocoonMultiplier = cocoonMultipliers.get(0);
    }
}
