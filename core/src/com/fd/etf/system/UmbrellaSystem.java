package com.fd.etf.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.fd.etf.entity.componets.UmbrellaComponent;
import com.fd.etf.utils.EffectUtils;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;

import java.util.Random;

import static com.fd.etf.entity.componets.Goal.GoalType.BOUNCE_UMBRELLA_N_TIMES;
import static com.fd.etf.entity.componets.Goal.GoalType.EAT_N_UMBRELLA;
import static com.fd.etf.entity.componets.UmbrellaComponent.SPAWNING_TIME;
import static com.fd.etf.entity.componets.UmbrellaComponent.State.*;
import static com.fd.etf.entity.componets.UmbrellaComponent.currentMultiplier;
import static com.fd.etf.stages.GameScreenScript.*;
import static com.fd.etf.stages.GameStage.gameScript;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_Y;

public class UmbrellaSystem extends IteratingSystem {

    private static final float UMBRELLA_SCALE = 2f;
    public static float umbrellaSpawnStateCounter;

    private static Random random = new Random();
    private ComponentMapper<UmbrellaComponent> mapper = ComponentMapper.getFor(UmbrellaComponent.class);

    public UmbrellaSystem() {
        super(Family.all(UmbrellaComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (!isStarted) {
            hide(entity);
        }

        if (!isPause.get() && !isGameOver.get() && isStarted &&
                entity.getComponent(UmbrellaComponent.class).state != DEAD) {

            UmbrellaComponent uc = mapper.get(entity);
            entity.getComponent(TransformComponent.class).scaleX = UMBRELLA_SCALE;
            entity.getComponent(TransformComponent.class).scaleY = UMBRELLA_SCALE;

            uc.current += Gdx.graphics.getDeltaTime() * uc.speed;

            spawn(entity, deltaTime);

            push(uc, entity.getComponent(TransformComponent.class));

            fly(entity.getComponent(SpriteAnimationStateComponent.class),
                    uc,
                    entity.getComponent(DimensionsComponent.class),
                    entity.getComponent(TransformComponent.class));

            if (checkCollision(uc)) {
                gameScript.fpc.isCollision = true;
                hide(entity);

                gameScript.fpc.umbrellaMult(uc.pointsMult);
                gameScript.reloadScoreLabel(gameScript.fpc);

                playParticleEffectFor();

                checkEatGoal(uc);
            }
        } else {
            entity.getComponent(SpriteAnimationStateComponent.class).paused = true;
        }
    }

    private void spawn(Entity e, float deltaTime) {
        if (e.getComponent(UmbrellaComponent.class).state.equals(SPAWNING)) {
            e.getComponent(SpriteAnimationStateComponent.class).paused = true;
            e.getComponent(TransformComponent.class).x = UmbrellaComponent.INIT_SPAWN_X;
            e.getComponent(TransformComponent.class).y = 400;
            e.getComponent(UmbrellaComponent.class).blinkCounter--;
            if (e.getComponent(UmbrellaComponent.class).blinkCounter == 0){
                if (e.getComponent(TintComponent.class).color.a > 0.95f) {
                    e.getComponent(TintComponent.class).color.a -= 0.1f;
                } else {
                    e.getComponent(TintComponent.class).color.a += 0.2f;
                }
                e.getComponent(UmbrellaComponent.class).blinkCounter = 10;
            }

            umbrellaSpawnStateCounter += deltaTime;
            if (umbrellaSpawnStateCounter >= SPAWNING_TIME) {
                e.getComponent(UmbrellaComponent.class).state = PUSH;
                e.getComponent(TintComponent.class).color.a = 1;
                umbrellaSpawnStateCounter = 0;
            }
        }
    }

    private void fly(SpriteAnimationStateComponent sasc,
                     UmbrellaComponent uc,
                     DimensionsComponent dc,
                     TransformComponent tc) {

        if (uc.current >= 1 && uc.state.equals(FLY)) {
            uc.dataSet[0] = new Vector2(uc.dataSet[2].x, uc.dataSet[2].y);
            uc.dataSet[2] = new Vector2(1170, random.nextInt(700) + 100);
            uc.dataSet[1] = new Vector2(-1100, (uc.dataSet[2].y + uc.dataSet[0].y) / 2);

            uc.myCatmull = new Bezier<Vector2>(uc.dataSet);
            uc.out = new Vector2(UmbrellaComponent.INIT_SPAWN_X, UmbrellaComponent.INIT_SPAWN_Y);
            uc.myCatmull.valueAt(uc.out, 5);
            uc.myCatmull.derivativeAt(uc.out, 5);

            uc.current -= 1;
            checkBounceGoal();
            sasc.paused = false;
        }
        if (uc.myCatmull != null && !uc.state.equals(SPAWNING)) {
            uc.myCatmull.valueAt(uc.out, uc.current);
            tc.x = uc.out.x;
            tc.y = uc.out.y;
            sasc.paused = false;
        }
        updateRect(uc, tc, dc);
    }

    private void push(UmbrellaComponent uc, TransformComponent tc) {
        if (uc.state.equals(PUSH)) {
            uc.dataSet = new Vector2[3];
            uc.dataSet[0] = new Vector2(tc.x, tc.y);
            uc.dataSet[1] = new Vector2(1170, 400);
            uc.dataSet[2] = new Vector2(1170, 400);

            uc.myCatmull = new Bezier<>(uc.dataSet);
//                uc.out = new Vector2(340, 200);
            uc.out = new Vector2(UmbrellaComponent.INIT_SPAWN_X, UmbrellaComponent.INIT_SPAWN_Y);
            uc.myCatmull.valueAt(uc.out, 5);
            uc.myCatmull.derivativeAt(uc.out, 5);
            uc.state = FLY;
        }
    }

    private void playParticleEffectFor() {
        EffectUtils.playYellowStarsParticleEffect(gameScript.scoreLabelE.getComponent(TransformComponent.class).x,
                gameScript.scoreLabelE.getComponent(TransformComponent.class).y);
    }

    public static void hide(Entity entity) {
        umbrellaSpawnCounter = getNextSpawnInterval();
        if (entity.getComponent(UmbrellaComponent.class) == null){
            entity.add(new UmbrellaComponent());
        }
        entity.getComponent(UmbrellaComponent.class).state = DEAD;
        entity.getComponent(TransformComponent.class).x = -500;
        entity.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
    }

    public static float getNextSpawnInterval() {
        float randCoefficient = currentMultiplier.minSpawnCoefficient +
                random.nextFloat() * (currentMultiplier.maxSpawnCoefficient - currentMultiplier.minSpawnCoefficient);
        return UmbrellaComponent.SPAWN_INTERVAL_BASE*randCoefficient;
    }

    public void updateRect(UmbrellaComponent uc, TransformComponent tc, DimensionsComponent dc) {
        uc.boundsRect.x = (int) tc.x - 30;
        uc.boundsRect.y = (int) tc.y - 30;
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
