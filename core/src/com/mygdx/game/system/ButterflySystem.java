package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entity.componets.ButterflyComponent;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.stages.GameScreenScript;
import com.mygdx.game.stages.GameStage;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import java.util.Random;

import static com.mygdx.game.entity.componets.ButterflyComponent.State.*;

/**
 * Created by Teatree on 9/3/2015.
 */
public class ButterflySystem extends IteratingSystem {

    private ComponentMapper<ButterflyComponent> mapper = ComponentMapper.getFor(ButterflyComponent.class);
    private ComponentMapper<FlowerPublicComponent> collisionMapper = ComponentMapper.getFor(FlowerPublicComponent.class);

    public ButterflySystem() {
        super(Family.all(ButterflyComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SpriteAnimationStateComponent sasc = ComponentRetriever.get(entity, SpriteAnimationStateComponent.class);
        if (!GameScreenScript.isPause && !GameScreenScript.isGameOver) {
            sasc.paused = false;

            ButterflyComponent bc = mapper.get(entity);
            TransformComponent tc = ComponentRetriever.get(entity, TransformComponent.class);
            DimensionsComponent dc = ComponentRetriever.get(entity, DimensionsComponent.class);
            FlowerPublicComponent fcc = collisionMapper.get(entity);

            if (bc.state.equals(SPAWN)) {
                bc.dataSet = new Vector2[3];
                bc.dataSet[0] = new Vector2(tc.x, tc.y);
                bc.dataSet[1] = new Vector2(-500, Gdx.graphics.getHeight() / 2);
                bc.dataSet[2] = new Vector2(Gdx.graphics.getWidth() - 30, Gdx.graphics.getHeight() / 2);

                bc.myCatmull = new Bezier<>(bc.dataSet);
                bc.out = new Vector2(340, Gdx.graphics.getHeight() / 4);
                bc.myCatmull.valueAt(bc.out, 5);
                bc.myCatmull.derivativeAt(bc.out, 5);

                bc.state = FLY;
            }

            bc.current += Gdx.graphics.getDeltaTime() * bc.speed;
            bc.myCatmull.valueAt(bc.out, bc.current);
            tc.x = bc.out.x;
            tc.y = bc.out.y;

            if (bc.current >= 1 && bc.state.equals(FLY) || isOutOfBounds(bc)) {
                die(bc, tc);
            }

            updateRectangle(bc, tc, dc);
            if (checkCollision(bc, fcc)) {
                fcc.isCollision = true;
//                GameStage.sceneLoader.getEngine().removeEntity(entity);
                tc.x = -300;
                tc.y = -300;
                entity.remove(ButterflyComponent.class);

                fcc.totalScore -= fcc.score;
                fcc.score += bc.points;
                fcc.totalScore += fcc.score;

                GameScreenScript.reloadScoreLabel(fcc);
            }
        } else {
            sasc.paused = true;
        }
    }

    private void die(ButterflyComponent bc, TransformComponent tc) {
        tc.x = -900;
        tc.y = -900;
        bc.state = DEAD;
    }

    public void updateRectangle(ButterflyComponent bc, TransformComponent tc, DimensionsComponent dc) {
        bc.boundsRect.x = (int) tc.x;
        bc.boundsRect.y = (int) tc.y;
        bc.boundsRect.width = (int) dc.width * tc.scaleX;
        bc.boundsRect.height = (int) dc.height * tc.scaleY;
    }

    public boolean isOutOfBounds(ButterflyComponent bc) {
        return bc.boundsRect.getX() >= Gdx.graphics.getWidth();
    }

    private boolean checkCollision(ButterflyComponent bc, FlowerPublicComponent fcc) {
        return bc.boundsRect.overlaps(fcc.boundsRect);
    }
}
