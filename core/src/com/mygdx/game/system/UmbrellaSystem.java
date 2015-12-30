package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.mygdx.game.entity.componets.BugJuiceBubbleComponent;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.entity.componets.UmbrellaComponent;
import com.mygdx.game.stages.GameScreenScript;
import com.mygdx.game.stages.GameStage;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import java.util.Random;

import static com.mygdx.game.stages.GameScreenScript.scoreLabelComponent;

/**
 * Created by Teatree on 10/6/2015.
 */
public class UmbrellaSystem extends IteratingSystem {

    public static final float UMBRELLA_SCALE = 0.4f;

    public Random random = new Random();
    private ComponentMapper<UmbrellaComponent> mapper = ComponentMapper.getFor(UmbrellaComponent.class);
    private ComponentMapper<FlowerPublicComponent> fccMapper = ComponentMapper.getFor(FlowerPublicComponent.class);

    public UmbrellaSystem() {
        super(Family.all(UmbrellaComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SpriteAnimationStateComponent sasc = ComponentRetriever.get(entity, SpriteAnimationStateComponent.class);
        if (!GameScreenScript.isPause && !GameScreenScript.isGameOver) {

            UmbrellaComponent uc = mapper.get(entity);
            DimensionsComponent dc = ComponentRetriever.get(entity, DimensionsComponent.class);
            TransformComponent tc = ComponentRetriever.get(entity, TransformComponent.class);
            tc.scaleX = UMBRELLA_SCALE;
            tc.scaleY = UMBRELLA_SCALE;

            uc.current += Gdx.graphics.getDeltaTime() * uc.speed;

            if (uc.state == UmbrellaComponent.State.PUSH) {
                uc.dataSet = new Vector2[3];
                uc.dataSet[0] = new Vector2(tc.x, tc.y);
                uc.dataSet[1] = new Vector2(-500, Gdx.graphics.getHeight() / 2);
                uc.dataSet[2] = new Vector2(Gdx.graphics.getWidth() - 30, Gdx.graphics.getHeight() / 2);

                uc.myCatmull = new Bezier<>(uc.dataSet);
                uc.out = new Vector2(340, Gdx.graphics.getHeight() / 4);
                uc.myCatmull.valueAt(uc.out, 5);
                uc.myCatmull.derivativeAt(uc.out, 5);

                uc.state = UmbrellaComponent.State.FLY;
            }

            if (uc.current >= 1 && uc.state == UmbrellaComponent.State.FLY) {
                uc.dataSet[0] = new Vector2(uc.dataSet[2].x, uc.dataSet[2].y);
                uc.dataSet[2] = new Vector2(Gdx.graphics.getWidth() - 30, random.nextInt(((Gdx.graphics.getHeight() - 100) - 100)) + 100);
                uc.dataSet[1] = new Vector2(-1100, (uc.dataSet[2].y + uc.dataSet[0].y) / 2);
                System.out.println("0: " + uc.dataSet[0].y + " || 1: " + uc.dataSet[1].y + " || 2: " + uc.dataSet[2].y);

                uc.myCatmull = new Bezier<Vector2>(uc.dataSet);
                uc.out = new Vector2(340, Gdx.graphics.getHeight() / 4);
                uc.myCatmull.valueAt(uc.out, 5);
                uc.myCatmull.derivativeAt(uc.out, 5);

                uc.current -= 1;
            }

            uc.myCatmull.valueAt(uc.out, uc.current);
            tc.x = uc.out.x;
            tc.y = uc.out.y;

            sasc.paused = false;
//
            FlowerPublicComponent fcc = fccMapper.get(entity);
//
            updateRect(uc, tc, dc);

            if (checkCollision(uc, fcc)) {
                fcc.isCollision = true;
                GameStage.sceneLoader.getEngine().removeEntity(entity);

                //temp
                fcc.totalScore -= fcc.score;
                fcc.score *= uc.pointsMult;
                fcc.totalScore += fcc.score;

                GameScreenScript.reloadScoreLabel(fcc);
            }
        } else {
            sasc.paused = true;
        }
    }

    public void updateRect(UmbrellaComponent uc, TransformComponent tc, DimensionsComponent dc) {
        uc.boundsRect.x = (int) tc.x;
        uc.boundsRect.y = (int) tc.y;
        uc.boundsRect.width = (int) dc.width * tc.scaleX;
        uc.boundsRect.height = (int) dc.height * tc.scaleY;
    }

    private boolean checkCollision(UmbrellaComponent bc, FlowerPublicComponent fcc) {
        return bc.boundsRect.overlaps(fcc.boundsRect);
    }
}
