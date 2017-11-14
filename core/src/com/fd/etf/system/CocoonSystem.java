package com.fd.etf.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fd.etf.entity.componets.ButterflyComponent;
import com.fd.etf.entity.componets.CocoonComponent;
import com.fd.etf.stages.GameScreenScript;
import com.fd.etf.utils.SoundMgr;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.List;
import java.util.Random;

import static com.fd.etf.entity.componets.CocoonComponent.CocoonMultiplier;
import static com.fd.etf.entity.componets.CocoonComponent.State.*;
import static com.fd.etf.entity.componets.Goal.GoalType.DESTROY_N_COCOON;
import static com.fd.etf.stages.GameScreenScript.*;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_Y;

public class CocoonSystem extends IteratingSystem {

    private static float SPAWN_INTERVAL_BASE = 60;
    private static final int COCOON_HIT_AMOUNT = 3;

    public static List<CocoonMultiplier> cocoonMultipliers;
    public static CocoonMultiplier currentCocoonMultiplier;

    public static final String BUTTERFLY_ANI = "butterfly";

    ItemWrapper gameItem;
    private GameScreenScript gameScript;

    public CocoonSystem(GameScreenScript gameScript) {
        super(Family.all(CocoonComponent.class).get());
        this.gameScript = gameScript;
        this.gameItem = gameScript.gameItem;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (!isStarted) {
            entity.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
        }

        if (!isPause.get() && !isGameOver.get() && isStarted) {
            TransformComponent tc = ComponentRetriever.get(entity, TransformComponent.class);

            updateRect(entity.getComponent(CocoonComponent.class), tc, entity.getComponent(DimensionsComponent.class));
            act(entity.getComponent(CocoonComponent.class), entity, deltaTime);

            if (checkCollision(entity.getComponent(CocoonComponent.class)) && !entity.getComponent(CocoonComponent.class).canHit) {
                hit(entity.getComponent(CocoonComponent.class), entity);
            }
        } else {
            entity.getComponent(SpriterComponent.class).player.speed = 0;
        }
    }

    public void act(CocoonComponent cc, Entity entity, float delta) {
        if (cc.state.equals(SPAWNING)) {
            if (isAnimationFinished(entity)) {
                cc.state = IDLE;
                entity.getComponent(SpriterComponent.class).player.setAnimation(1);
                entity.getComponent(SpriterComponent.class).player.speed = 0;
            }
        }

        if (cc.state.equals(HIT)) {
            if (isAnimationFinished(entity)) {
                SoundMgr.getSoundMgr().play(SoundMgr.COCOON_HIT);
                if (cc.hitCounter >= COCOON_HIT_AMOUNT) {
                    cc.state = DEAD;
                    spawnButterfly();
                    checkCocoonGoal();
                    SoundMgr.getSoundMgr().play(SoundMgr.COCOON_RELEASE_B);
                } else {
                    cc.state = IDLE;
                    if (cc.hitCounter + 1 < 4) {
                        entity.getComponent(SpriterComponent.class).player.setAnimation(cc.hitCounter + 1);
                    }
                    entity.getComponent(SpriterComponent.class).player.speed = 0;
                }
            }
        }

        if (cc.state.equals(DEAD)) {
            entity.getComponent(SpriterComponent.class).player.setAnimation(3);
            if (isAnimationFinished(entity)) {
                entity.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
            }
        }
    }

    public boolean isAnimationFinished(Entity entity) {
        return entity.getComponent(SpriterComponent.class).player.getTime() >= entity.getComponent(SpriterComponent.class).player.getAnimation().length - 20;
    }

    public void hit(CocoonComponent cc, Entity entity) {

        cc.canHit = true;
        if (!cc.state.equals(DEAD)) {
            cc.state = HIT;
//            cc.hitCounter+=1;
            cc.canHit = true;
            if (!isPause.get() && !isGameOver.get()) {
                entity.getComponent(SpriterComponent.class).player.speed = 24;
            }
            if (cc.hitCounter <= 3) {
                entity.getComponent(SpriterComponent.class).player.setAnimation(cc.hitCounter++);
            }
        }
    }

    public void updateRect(CocoonComponent cc, TransformComponent tc, DimensionsComponent dc) {
        cc.boundsRect.x = (int) tc.x;
        cc.boundsRect.y = 693;
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
        if (gameScript.fpc.level.getGoalByType(DESTROY_N_COCOON) != null) {
            gameScript.fpc.level.getGoalByType(DESTROY_N_COCOON).update();
        }
    }

    public static int getNextSpawnInterval() {
        Random r = new Random();
        float randCoefficient = currentCocoonMultiplier.minSpawnCoefficient +
                r.nextFloat() * (currentCocoonMultiplier.maxSpawnCoefficient - currentCocoonMultiplier.minSpawnCoefficient);
        return (int)(SPAWN_INTERVAL_BASE * randCoefficient);
    }

    public static void resetSpawnCoefficients() {
        currentCocoonMultiplier = cocoonMultipliers.get(0);
    }
}
