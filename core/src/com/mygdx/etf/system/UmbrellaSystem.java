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
import com.mygdx.etf.entity.componets.UmbrellaComponent;
import com.mygdx.etf.stages.GameScreenScript;
import com.mygdx.etf.stages.GameStage;
import com.mygdx.etf.utils.EffectUtils;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import java.util.Random;

import static com.mygdx.etf.entity.componets.Goal.GoalType.BOUNCE_UMBRELLA_N_TIMES;
import static com.mygdx.etf.entity.componets.Goal.GoalType.EAT_N_UMBRELLA;
import static com.mygdx.etf.stages.GameScreenScript.*;
import static com.mygdx.etf.stages.GameStage.*;
import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_Y;

import static com.mygdx.etf.entity.componets.UmbrellaComponent.State.*;
import static com.mygdx.etf.entity.componets.UmbrellaComponent.*;

public class UmbrellaSystem extends IteratingSystem {

    public static final float UMBRELLA_SCALE = 0.4f;

    public Random random = new Random();
    private ComponentMapper<UmbrellaComponent> mapper = ComponentMapper.getFor(UmbrellaComponent.class);

    public UmbrellaSystem() {
        super(Family.all(UmbrellaComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SpriteAnimationStateComponent sasc = ComponentRetriever.get(entity, SpriteAnimationStateComponent.class);

        if (!isStarted || isGameOver) {
            entity.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            entity.getComponent(UmbrellaComponent.class).state = DEAD;
//            GameStage.sceneLoader.getEngine().removeEntity(entity);
        }

        if (entity.getComponent(UmbrellaComponent.class).state.equals(DEAD)) {
            entity.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            entity.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
        }

        if (!isPause && !isGameOver && isStarted &&
                entity.getComponent(UmbrellaComponent.class).state != DEAD) {

            UmbrellaComponent uc = mapper.get(entity);
            DimensionsComponent dc = ComponentRetriever.get(entity, DimensionsComponent.class);
            TransformComponent tc = ComponentRetriever.get(entity, TransformComponent.class);
            tc.scaleX = UMBRELLA_SCALE;
            tc.scaleY = UMBRELLA_SCALE;

            uc.current += Gdx.graphics.getDeltaTime() * uc.speed;

            if (uc.state.equals(SPAWNING)) {
//                dandelionSpawnCounter+=deltaTime;
//                if (dandelionSpawnCounter >= SPAWNING_TIME){
                uc.blinkCounter--;
                if (uc.blinkCounter == 0){
                    if (entity.getComponent(TintComponent.class).color.a > 0.95f) {
                        entity.getComponent(TintComponent.class).color.a -= 0.3f;
                    } else {
                        entity.getComponent(TintComponent.class).color.a += 0.4f;
                    }
                    uc.blinkCounter = 10;
                }

                umbrellaSpawnCounter += deltaTime;
                if (umbrellaSpawnCounter >= SPAWNING_TIME) {
                    uc.state = PUSH;
                    entity.getComponent(TintComponent.class).color.a = 1;
                }
            }

            if (uc.state.equals(PUSH)) {
                uc.dataSet = new Vector2[3];
                uc.dataSet[0] = new Vector2(tc.x, tc.y);
                uc.dataSet[1] = new Vector2(-500, 400);
                uc.dataSet[2] = new Vector2(1170, 400);

                uc.myCatmull = new Bezier<>(uc.dataSet);
//                uc.out = new Vector2(340, 200);
                uc.out = new Vector2(UmbrellaComponent.INIT_SPAWN_X, UmbrellaComponent.INIT_SPAWN_Y);
                uc.myCatmull.valueAt(uc.out, 5);
                uc.myCatmull.derivativeAt(uc.out, 5);
                uc.state = FLY;
            }

            if (uc.current >= 1 && uc.state.equals(FLY)) {
                uc.dataSet[0] = new Vector2(uc.dataSet[2].x, uc.dataSet[2].y);
                uc.dataSet[2] = new Vector2(1170, random.nextInt(700) + 100);
                uc.dataSet[1] = new Vector2(-1100, (uc.dataSet[2].y + uc.dataSet[0].y) / 2);

                uc.myCatmull = new Bezier<Vector2>(uc.dataSet);
                uc.out = new Vector2(340, 200);
                uc.out = new Vector2(UmbrellaComponent.INIT_SPAWN_X, UmbrellaComponent.INIT_SPAWN_Y);
                uc.myCatmull.valueAt(uc.out, 5);
                uc.myCatmull.derivativeAt(uc.out, 5);

                uc.current -= 1;
                checkBounceGoal();
            }
            if (uc.myCatmull != null && !uc.state.equals(SPAWNING)) {
                uc.myCatmull.valueAt(uc.out, uc.current);
                tc.x = uc.out.x;
                tc.y = uc.out.y;

            }
            sasc.paused = false;
            updateRect(uc, tc, dc);

            if (checkCollision(uc)) {
                gameScript.fpc.isCollision = true;
                hide(entity, tc);

                gameScript.fpc.umbrellaMult(uc.pointsMult);
                gameScript.reloadScoreLabel(gameScript.fpc);

                playParticleEffectFor();

                checkEatGoal(uc);
            }
        } else {
            sasc.paused = true;
        }
    }

    private void playParticleEffectFor() {
        EffectUtils.playYellowStarsParticleEffect(gameScript.scoreLabelE.getComponent(TransformComponent.class).x,
                gameScript.scoreLabelE.getComponent(TransformComponent.class).y);
    }

    private void hide(Entity entity, TransformComponent tc) {
        umbrellaSpawnCounter = getNextSpawnInterval();
//        dandelionSpawnCounter = getNextSpawnInterval();
        entity.getComponent(UmbrellaComponent.class).state = DEAD;
        tc.x = -500;
        tc.y = FAR_FAR_AWAY_Y;
    }

    public static float getNextSpawnInterval() {
        Random r = new Random();
        float randCoefficient = currentMultiplier.minSpawnCoefficient +
                r.nextFloat() * (currentMultiplier.maxSpawnCoefficient - currentMultiplier.minSpawnCoefficient);
//        return SPAWN_INTERVAL_BASE*randCoefficient;
        return 3;
    }

    public void updateRect(UmbrellaComponent uc, TransformComponent tc, DimensionsComponent dc) {
        uc.boundsRect.x = (int) tc.x;
        uc.boundsRect.y = (int) tc.y;
        uc.boundsRect.width = (int) dc.width * tc.scaleX;
        uc.boundsRect.height = (int) dc.height * tc.scaleY;
    }

    private boolean checkCollision(UmbrellaComponent bc) {
        return gameScript.fpc.flowerCollisionCheck(bc.boundsRect);
    }

    private void checkEatGoal(UmbrellaComponent uc) {
        if (gameScript.fpc.flowerCollisionCheck(uc.boundsRect)) {
            if (gameScript.fpc.level.getGoalByType(EAT_N_UMBRELLA) != null) {
                gameScript.fpc.level.getGoalByType(EAT_N_UMBRELLA).update();
            }
        }
    }

    private void checkBounceGoal() {
        if (gameScript.fpc.level.getGoalByType(BOUNCE_UMBRELLA_N_TIMES) != null) {
            gameScript.fpc.level.getGoalByType(BOUNCE_UMBRELLA_N_TIMES).update();
        }
    }
}
