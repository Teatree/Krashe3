package com.fd.etf.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.fd.etf.entity.componets.ButterflyComponent;
import com.fd.etf.stages.GameStage;
import com.fd.etf.utils.EffectUtils;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationComponent;

import static com.fd.etf.entity.componets.ButterflyComponent.State.*;
import static com.fd.etf.entity.componets.Goal.GoalType.EAT_N_BUTTERFLIES;
import static com.fd.etf.stages.GameScreenScript.*;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_Y;


public class ButterflySystem extends IteratingSystem {

    private GameStage gameStage;

    public ButterflySystem(GameStage gameStage) {
        super(Family.all(ButterflyComponent.class).get());
        this.gameStage = gameStage;
    }

    @Override
    protected void processEntity(Entity e, float deltaTime) {

        if (!isStarted || isGameOver.get()) {
            e.getComponent(TransformComponent.class).x = -900;
            e.getComponent(TransformComponent.class).y = -900;

//            e.getComponent(SpriteAnimationStateComponent.class).set(e.getComponent(SpriteAnimationComponent.class).frameRangeMap.get("Default"), 14, Animation.PlayMode.LOOP);
            e.getComponent(SpriteAnimationComponent.class).fps = 0;

//            e.getComponent(SpriterComponent.class).player.speed = 0;
            e.getComponent(ButterflyComponent.class).state = DEAD;
        }
        if (!isPause.get() && !isGameOver.get() && isStarted &&
                e.getComponent(ButterflyComponent.class).state != DEAD) {
            e.getComponent(SpriteAnimationComponent.class).fps = 14;

            e.getComponent(DimensionsComponent.class).height = 147;
            e.getComponent(DimensionsComponent.class).width = 138;

            if (e.getComponent(ButterflyComponent.class).state.equals(SPAWN)) {
                e.getComponent(ButterflyComponent.class).dataSet = new Vector2[3];
                e.getComponent(ButterflyComponent.class).dataSet[0] = new Vector2(e.getComponent(TransformComponent.class).x, e.getComponent(TransformComponent.class).y);
                e.getComponent(ButterflyComponent.class).dataSet[1] = new Vector2(-500, 400);
                e.getComponent(ButterflyComponent.class).dataSet[2] = new Vector2(1170, 400);

                e.getComponent(ButterflyComponent.class).myCatmull = new Bezier<>(e.getComponent(ButterflyComponent.class).dataSet);
                e.getComponent(ButterflyComponent.class).out = new Vector2(340, 200);
                e.getComponent(ButterflyComponent.class).myCatmull.valueAt(e.getComponent(ButterflyComponent.class).out, 5);
                e.getComponent(ButterflyComponent.class).myCatmull.derivativeAt(e.getComponent(ButterflyComponent.class).out, 5);

                e.getComponent(ButterflyComponent.class).state = FLY;
            }

            e.getComponent(ButterflyComponent.class).current += Gdx.graphics.getDeltaTime() * e.getComponent(ButterflyComponent.class).speed;
            e.getComponent(ButterflyComponent.class).myCatmull.valueAt(e.getComponent(ButterflyComponent.class).out, e.getComponent(ButterflyComponent.class).current);
            e.getComponent(TransformComponent.class).x = e.getComponent(ButterflyComponent.class).out.x;
            e.getComponent(TransformComponent.class).y = e.getComponent(ButterflyComponent.class).out.y;

            //side switching
            if (e.getComponent(ButterflyComponent.class).current > 0.4f) {
                e.getComponent(TransformComponent.class).scaleX = 1;
            } else {
                e.getComponent(TransformComponent.class).x -= e.getComponent(DimensionsComponent.class).width / 2;
                e.getComponent(TransformComponent.class).scaleX = -1;
            }

            if (e.getComponent(ButterflyComponent.class).current >= 1 && e.getComponent(ButterflyComponent.class).state.equals(FLY) || isOutOfBounds(e.getComponent(ButterflyComponent.class))) {
                die(e.getComponent(ButterflyComponent.class), e.getComponent(TransformComponent.class));
            }

            updateRectangle(e.getComponent(ButterflyComponent.class), e.getComponent(TransformComponent.class), e.getComponent(DimensionsComponent.class));
            if (checkCollision(e.getComponent(ButterflyComponent.class))) {
                gameStage.gameScript.fpc.isCollision = true;

//                gameStage.gameScript.fpc.addScore(e.getComponent(ButterflyComponent.class).points);

                spawnBugJuiceBubble(e);
                e.getComponent(ButterflyComponent.class).state = DEAD;
                e.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
                e.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
//                gameStage.gameScript.reloadScoreLabel(gameStage.gameScript.fpc);
            }

            if (e.getComponent(ButterflyComponent.class).state == DEAD) {
                e.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
                e.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
            }
        } else {
            // stopMenu
            e.getComponent(SpriteAnimationComponent.class).fps = 0;
//            e.getComponent(SpriterComponent.class).player.speed = 0;
        }

    }

    private void die(ButterflyComponent bc, TransformComponent tc) {
        tc.x = FAR_FAR_AWAY_X;
        tc.y = FAR_FAR_AWAY_Y;
        bc.state = DEAD;
    }

    public void updateRectangle(ButterflyComponent bc, TransformComponent tc, DimensionsComponent dc) {
        bc.boundsRect.x = (int) tc.x;
        bc.boundsRect.y = (int) tc.y;
        bc.boundsRect.width = (int) dc.width * tc.scaleX;
        bc.boundsRect.height = (int) dc.height * tc.scaleY;
    }

    private void spawnBugJuiceBubble(Entity e) {
        EffectUtils.spawnBugJuiceBubble(e.getComponent(ButterflyComponent.class).points, gameStage, e.getComponent(TransformComponent.class).x,
                e.getComponent(TransformComponent.class).y);
    }

    public boolean isOutOfBounds(ButterflyComponent bc) {
        return bc.boundsRect.getX() >= 1200;
    }

    private boolean checkCollision(ButterflyComponent bc) {
        checkGoal(bc);
//        System.out.print("bounds >> " + entity.getComponent(ButterflyComponent.class).boundsRect);
//        System.out.print(" || flower >> " + gameStage.gameScript.fpc.boundsRect);
//        System.out.println(" || overlap >>> " + gameStage.gameScript.fpc.petAndFlowerCollisionCheck(entity.getComponent(ButterflyComponent.class).boundsRect) );
        return gameStage.gameScript.fpc.petAndFlowerCollisionCheck(bc.boundsRect);
    }

    private void checkGoal(ButterflyComponent bc) {
        if (gameStage.gameScript.fpc.flowerCollisionCheck(bc.boundsRect)) {
            if (gameStage.gameScript.fpc.level.getGoalByType(EAT_N_BUTTERFLIES) != null) {
                gameStage.gameScript.fpc.level.getGoalByType(EAT_N_BUTTERFLIES).update();
            }
        }
    }
}
