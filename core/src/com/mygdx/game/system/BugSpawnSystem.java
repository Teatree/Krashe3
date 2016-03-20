package com.mygdx.game.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.mygdx.game.entity.componets.BugComponent;
import com.mygdx.game.entity.componets.BugComponent.BugType;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.utils.BugPool;
import com.uwsoft.editor.renderer.components.TransformComponent;

import java.util.Random;

import static com.mygdx.game.entity.componets.Goal.GoalType.SURVIVE_N_ANGERED_MODES;
import static com.mygdx.game.stages.GameScreenScript.*;

public class BugSpawnSystem extends EntitySystem {

    public static int ANGERED_BEES_MODE_DURATION = 800;
    public static boolean isAngeredBeesMode = false;
    public static boolean queenBeeOnStage = false;
    public static int angeredBeesModeTimer = ANGERED_BEES_MODE_DURATION;
    public FlowerPublicComponent fcc;

    private int SPAWN_MAX_X = -400;
    private int SPAWN_MIN_X = 300;
    private int SPAWN_MIN_Y = -200;
    private int SPAWN_MAX_Y = 600;
    private int SPAWN_INTERVAL = 100;

    private Random rand = new Random();

    public BugSpawnSystem(FlowerPublicComponent fcc) {
        this.fcc = fcc;
        init();
    }

    public static boolean isBlewUp() {
        return isAngeredBeesMode & angeredBeesModeTimer == ANGERED_BEES_MODE_DURATION - 10;
    }

    private void init() {
        SPAWN_MIN_X = -300;
        SPAWN_MIN_Y = 100;
        SPAWN_MAX_X = -200;
        SPAWN_MAX_Y = 700;
    }

    private TransformComponent getPos(BugComponent bc) {
        TransformComponent transformComponent = new TransformComponent();
        transformComponent.x = rand.nextInt(SPAWN_MAX_X - SPAWN_MIN_X) + SPAWN_MIN_X;
        transformComponent.y = rand.nextInt(SPAWN_MAX_Y - SPAWN_MIN_Y) + SPAWN_MIN_Y;
        bc.endX = 1450;
        bc.endY = transformComponent.y;

        return transformComponent;
    }

    public void spawn() {
        if (SPAWN_INTERVAL == 0) {
            if (isAngeredBeesMode) {
                createBug(BugComponent.BugType.BEE);
            } else {
                int probabilityValue = rand.nextInt(100);
                if (probabilityValue < 10) {
                    createBug(BugType.DRUNK);
                } else if (probabilityValue >= 10 && probabilityValue < 60) {
                    createBug(BugType.SIMPLE);
                } else if (probabilityValue >= 61 && probabilityValue < 67) {
                    createBug(BugType.CHARGER);
                } else if (probabilityValue >= 68 && probabilityValue < 70) {
                    if (!queenBeeOnStage) {
                        createBug(BugType.QUEENBEE);
                        queenBeeOnStage = true;
                    }
                } else {
                    createBug(BugType.BEE);
                }
            }

            SPAWN_INTERVAL = 100;
        } else {
            SPAWN_INTERVAL--;
        }
    }

    private void createBug(BugType tempType) {
        Entity bugEntity = BugPool.getInstance().get(tempType);
        BugComponent bc = new BugComponent(tempType);
        bugEntity.add(bc);
        bugEntity.add(fcc);

        TransformComponent tc = getPos(bc);
        bc.startYPosition = tc.y;
        bugEntity.getComponent(TransformComponent.class).x = tc.x;
        bugEntity.getComponent(TransformComponent.class).y = tc.y;
    }

    @Override
    public void update(float deltaTime) {
        if (!isPause && !isGameOver) {
            if (isStarted) {
                super.update(deltaTime);
                spawn();
                updateAngeredBeesMode();
            }
        }
    }

    private void updateAngeredBeesMode() {
        if (isAngeredBeesMode) {
            angeredBeesModeTimer--;
            if (angeredBeesModeTimer <= 0) {
                isAngeredBeesMode = false;
                cameraShaker.initBlinking(40, 3);
                angeredBeesModeTimer = ANGERED_BEES_MODE_DURATION;

                checkAngeredBeesGoal();
            }
        }
    }

    private void checkAngeredBeesGoal() {
        if (fpc.level.getGoalByType(SURVIVE_N_ANGERED_MODES) != null) {
            fpc.level.getGoalByType(SURVIVE_N_ANGERED_MODES).update();
        }
    }
}
