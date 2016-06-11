package com.mygdx.etf.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.etf.entity.componets.FlowerPublicComponent;
import com.mygdx.etf.entity.componets.UmbrellaComponent;
import com.mygdx.etf.stages.GameStage;
import com.mygdx.etf.utils.EffectUtils;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import java.util.Random;

import static com.mygdx.etf.entity.componets.Goal.GoalType.BOUNCE_UMBRELLA_N_TIMES;
import static com.mygdx.etf.entity.componets.Goal.GoalType.EAT_N_UMBRELLA;
import static com.mygdx.etf.stages.GameScreenScript.*;
import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_Y;

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
        FlowerPublicComponent fcc = fccMapper.get(entity);

        if (!isStarted) {
            entity.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            entity.remove(UmbrellaComponent.class);
//            GameStage.sceneLoader.getEngine().removeEntity(entity);
        }
        if (!isPause && !isGameOver && isStarted) {

            UmbrellaComponent uc = mapper.get(entity);
            DimensionsComponent dc = ComponentRetriever.get(entity, DimensionsComponent.class);
            TransformComponent tc = ComponentRetriever.get(entity, TransformComponent.class);
            tc.scaleX = UMBRELLA_SCALE;
            tc.scaleY = UMBRELLA_SCALE;

            uc.current += Gdx.graphics.getDeltaTime() * uc.speed;

            if (uc.state == UmbrellaComponent.State.PUSH) {
                uc.dataSet = new Vector2[3];
                uc.dataSet[0] = new Vector2(tc.x, tc.y);
                uc.dataSet[1] = new Vector2(-500, 400);
                uc.dataSet[2] = new Vector2(1170, 400);

                uc.myCatmull = new Bezier<>(uc.dataSet);
                uc.out = new Vector2(340, 200);
                uc.myCatmull.valueAt(uc.out, 5);
                uc.myCatmull.derivativeAt(uc.out, 5);

                uc.state = UmbrellaComponent.State.FLY;
            }

            if (uc.current >= 1 && uc.state.equals(UmbrellaComponent.State.FLY)) {
                uc.dataSet[0] = new Vector2(uc.dataSet[2].x, uc.dataSet[2].y);
                uc.dataSet[2] = new Vector2(1170, random.nextInt(700) + 100);
                uc.dataSet[1] = new Vector2(-1100, (uc.dataSet[2].y + uc.dataSet[0].y) / 2);

                uc.myCatmull = new Bezier<Vector2>(uc.dataSet);
                uc.out = new Vector2(340, 200);
                uc.myCatmull.valueAt(uc.out, 5);
                uc.myCatmull.derivativeAt(uc.out, 5);

                uc.current -= 1;
                checkBounceGoal(fcc);
            }

            uc.myCatmull.valueAt(uc.out, uc.current);
            tc.x = uc.out.x;
            tc.y = uc.out.y;

            sasc.paused = false;
//
//
            updateRect(uc, tc, dc);

            if (checkCollision(uc, fcc)) {
                fcc.isCollision = true;
                hide(entity, tc);

                fcc.umbrellaMult(uc.pointsMult);
                GameStage.gameScript.reloadScoreLabel(fcc);

                playParticleEffectFor();

                checkEatGoal(uc, fcc);
            }
        } else {
            sasc.paused = true;
        }
    }

    private void playParticleEffectFor() {
        EffectUtils.playYellowStarsParticleEffect(1088, 674);
    }

    private void hide(Entity entity, TransformComponent tc) {
        dandelionSpawnCounter = DandelionSystem.getNextSpawnInterval();

        tc.x = FAR_FAR_AWAY_X;
        tc.y = FAR_FAR_AWAY_Y;
        entity.remove(UmbrellaComponent.class);
    }

    public void updateRect(UmbrellaComponent uc, TransformComponent tc, DimensionsComponent dc) {
        uc.boundsRect.x = (int) tc.x;
        uc.boundsRect.y = (int) tc.y;
        uc.boundsRect.width = (int) dc.width * tc.scaleX;
        uc.boundsRect.height = (int) dc.height * tc.scaleY;
    }

    private boolean checkCollision(UmbrellaComponent bc, FlowerPublicComponent fcc) {
        return fcc.flowerCollisionCheck(bc.boundsRect);
    }

    private void checkEatGoal(UmbrellaComponent uc, FlowerPublicComponent fcc) {
        if (fcc.flowerCollisionCheck(uc.boundsRect)) {
            if (fcc.level.getGoalByType(EAT_N_UMBRELLA) != null) {
                fcc.level.getGoalByType(EAT_N_UMBRELLA).update();
            }
        }
    }

    private void checkBounceGoal(FlowerPublicComponent fpc) {
        if (fpc.level.getGoalByType(BOUNCE_UMBRELLA_N_TIMES) != null) {
            fpc.level.getGoalByType(BOUNCE_UMBRELLA_N_TIMES).update();
        }
    }
}
