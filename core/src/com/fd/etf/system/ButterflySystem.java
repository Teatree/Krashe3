package com.fd.etf.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.fd.etf.entity.componets.ButterflyComponent;
import com.fd.etf.stages.GameStage;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import static com.fd.etf.entity.componets.ButterflyComponent.State.*;
import static com.fd.etf.entity.componets.Goal.GoalType.EAT_N_BUTTERFLIES;
import static com.fd.etf.stages.GameScreenScript.*;
import static com.fd.etf.stages.GameStage.gameScript;
import static com.fd.etf.utils.GlobalConstants.*;


public class ButterflySystem extends IteratingSystem {

    private ComponentMapper<ButterflyComponent> mapper = ComponentMapper.getFor(ButterflyComponent.class);

    public ButterflySystem() {
        super(Family.all(ButterflyComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SpriterComponent sasc = ComponentRetriever.get(entity, SpriterComponent.class);

        if (!isStarted || isGameOver.get()) {
            entity.getComponent(TransformComponent.class).x = -900;
            entity.getComponent(TransformComponent.class).y = -900;
            sasc.player.speed = 0;
            entity.getComponent(ButterflyComponent.class).state = DEAD;
        }
        if (!isPause.get() && !isGameOver.get() && isStarted &&
                entity.getComponent(ButterflyComponent.class).state != DEAD) {
            sasc.player.speed = FPS;

            ButterflyComponent bc = mapper.get(entity);
            TransformComponent tc = ComponentRetriever.get(entity, TransformComponent.class);
            DimensionsComponent dc = ComponentRetriever.get(entity, DimensionsComponent.class);
            sasc.scale = 1f;
            dc.height = 147;
            dc.width = 138;

            if (bc.state.equals(SPAWN)) {
                bc.dataSet = new Vector2[3];
                bc.dataSet[0] = new Vector2(tc.x, tc.y);
                bc.dataSet[1] = new Vector2(-500, 400);
                bc.dataSet[2] = new Vector2(1170, 400);

                bc.myCatmull = new Bezier<>(bc.dataSet);
                bc.out = new Vector2(340, 200);
                bc.myCatmull.valueAt(bc.out, 5);
                bc.myCatmull.derivativeAt(bc.out, 5);

                bc.state = FLY;
            }

            bc.current += Gdx.graphics.getDeltaTime() * bc.speed;
            bc.myCatmull.valueAt(bc.out, bc.current);
            tc.x = bc.out.x;
            tc.y = bc.out.y;

            if (bc.current > 0.4f) {
                sasc.player.setAnimation(0);
            } else {
                sasc.player.setAnimation(1);
            }

            if (bc.current >= 1 && bc.state.equals(FLY) || isOutOfBounds(bc)) {
                die(bc, tc);
            }

            updateRectangle(bc, tc, dc);
            if (checkCollision(bc)) {
                gameScript.fpc.isCollision = true;
                tc.x = FAR_FAR_AWAY_X;
                tc.y = FAR_FAR_AWAY_Y;
                entity.getComponent(ButterflyComponent.class).state = DEAD;
//                entity.remove(ButterflyComponent.class);

                gameScript.fpc.addScore(bc.points);
                GameStage.gameScript.reloadScoreLabel(GameStage.gameScript.fpc);
            }

            if (entity.getComponent(ButterflyComponent.class).state == DEAD) {
                entity.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
                entity.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
            }
        } else {
            sasc.player.speed = 0;
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

    public boolean isOutOfBounds(ButterflyComponent bc) {
        return bc.boundsRect.getX() >= 1200;
    }

    private boolean checkCollision(ButterflyComponent bc) {
        checkGoal(bc);
//        System.out.print("bounds >> " + bc.boundsRect);
//        System.out.print(" || flower >> " + gameScript.fpc.boundsRect);
//        System.out.println(" || overlap >>> " + gameScript.fpc.petAndFlowerCollisionCheck(bc.boundsRect) );
        return gameScript.fpc.petAndFlowerCollisionCheck(bc.boundsRect);
    }

    private void checkGoal(ButterflyComponent bc) {
        if (gameScript.fpc.flowerCollisionCheck(bc.boundsRect)) {
            if (gameScript.fpc.level.getGoalByType(EAT_N_BUTTERFLIES) != null) {
                gameScript.fpc.level.getGoalByType(EAT_N_BUTTERFLIES).update();
            }
        }
    }
}
