package com.mygdx.etf.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.etf.entity.componets.ButterflyComponent;
import com.mygdx.etf.entity.componets.FlowerPublicComponent;
import com.mygdx.etf.stages.GameStage;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import static com.mygdx.etf.entity.componets.ButterflyComponent.State.*;
import static com.mygdx.etf.entity.componets.Goal.GoalType.EAT_N_BUTTERFLIES;
import static com.mygdx.etf.stages.GameScreenScript.*;
import static com.mygdx.etf.utils.GlobalConstants.*;

public class ButterflySystem extends IteratingSystem {

    private ComponentMapper<ButterflyComponent> mapper = ComponentMapper.getFor(ButterflyComponent.class);
    private ComponentMapper<FlowerPublicComponent> collisionMapper = ComponentMapper.getFor(FlowerPublicComponent.class);

    public ButterflySystem() {
        super(Family.all(ButterflyComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SpriterComponent sasc = ComponentRetriever.get(entity, SpriterComponent.class);

        if (!isStarted || isGameOver) {
            entity.getComponent(TransformComponent.class).x = -900;
            entity.getComponent(TransformComponent.class).y = -900;
            sasc.player.speed = 0;
            entity.getComponent(ButterflyComponent.class).state = DEAD;
        }

        if (!isPause && !isGameOver && isStarted) {
            sasc.player.speed = FPS;

            ButterflyComponent bc = mapper.get(entity);
            TransformComponent tc = ComponentRetriever.get(entity, TransformComponent.class);
            DimensionsComponent dc = ComponentRetriever.get(entity, DimensionsComponent.class);
            sasc.scale = 0.3f;
            dc.height = 147;
            dc.width = 138;
            FlowerPublicComponent fcc = collisionMapper.get(entity);

            if (bc.state.equals(SPAWN)) {
                bc.dataSet = new Vector2[3];
                bc.dataSet[0] = new Vector2(tc.x, tc.y);
                bc.dataSet[1] = new Vector2(-500, 400);
                bc.dataSet[2] = new Vector2(1170,400);

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

            if(bc.current>0.4f){
                sasc.player.setAnimation(1);
            }else{
                sasc.player.setAnimation(0);
            }

            if (bc.current >= 1 && bc.state.equals(FLY) || isOutOfBounds(bc)) {
                die(bc, tc);
            }

            updateRectangle(bc, tc, dc);
            if (checkCollision(bc, fcc)) {
                fcc.isCollision = true;
                tc.x = FAR_FAR_AWAY_X;
                tc.y = FAR_FAR_AWAY_Y;
                entity.getComponent(ButterflyComponent.class).state = DEAD;
//                entity.remove(ButterflyComponent.class);

                fcc.addScore(bc.points);
                GameStage.gameScript.reloadScoreLabel(fcc);
            }

            if (entity.getComponent(ButterflyComponent.class).state == DEAD){
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

    private boolean checkCollision(ButterflyComponent bc, FlowerPublicComponent fcc) {
        checkGoal(bc, fcc);
        return fcc.petAndFlowerCollisionCheck(bc.boundsRect);
    }

    private void checkGoal(ButterflyComponent bc, FlowerPublicComponent fcc) {
        if (fcc.flowerCollisionCheck(bc.boundsRect)) {
            if (fcc.level.getGoalByType(EAT_N_BUTTERFLIES) != null) {
                fcc.level.getGoalByType(EAT_N_BUTTERFLIES).update();
            }
        }
    }
}
