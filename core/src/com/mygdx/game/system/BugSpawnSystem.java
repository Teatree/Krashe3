package com.mygdx.game.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.mygdx.game.entity.componets.BugComponent;
import com.mygdx.game.entity.componets.BugType;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.stages.GameScreenScript;
import com.mygdx.game.utils.BugPool;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;

import java.util.HashMap;
import java.util.Random;

import static com.mygdx.game.entity.componets.BugComponent.State.IDLE;
import static com.mygdx.game.utils.BugPool.*;

/**
 * Created by Teatree on 9/3/2015.
 */
public class BugSpawnSystem extends EntitySystem {

    public static int ANGERED_BEES_MODE_DURATION = 800;

    private int SPAWN_MAX_X = -400;
    private int SPAWN_MIN_X = 300;
    private int SPAWN_MIN_Y = -200;
    private int SPAWN_MAX_Y = 700;

    public static boolean isAngeredBeesMode = false;
    public static boolean queenBeeOnStage = false;
    public static int angeredBeesModeTimer = ANGERED_BEES_MODE_DURATION;

    public FlowerPublicComponent fcc;
    private HashMap<BugType, String> libBugsNameType = new HashMap<>();

    private Random rand = new Random();

    private int SPAWN_INTERVAL = 200;

    public BugSpawnSystem(FlowerPublicComponent fcc) {
        this.fcc = fcc;
        init();
    }

    private void init() {
        SPAWN_MIN_X = -400;
        SPAWN_MIN_Y = 100;
        SPAWN_MAX_X = -200;
        SPAWN_MAX_Y = 700;

        libBugsNameType.put(BugType.SIMPLE, SIMPLE);
        libBugsNameType.put(BugType.DRUNK, DRUNK);
        libBugsNameType.put(BugType.CHARGER, CHARGER);
        libBugsNameType.put(BugType.BEE, BEE);
        libBugsNameType.put(BugType.QUEENBEE, QUEENBEE);
    }

    private TransformComponent getPos(BugComponent bc){
        TransformComponent transformComponent = new TransformComponent();
        transformComponent.x = rand.nextInt(SPAWN_MAX_X-SPAWN_MIN_X)+SPAWN_MIN_X;
        transformComponent.y = rand.nextInt(SPAWN_MAX_Y-SPAWN_MIN_Y)+SPAWN_MIN_Y;
        bc.endX = transformComponent.x + Gdx.graphics.getWidth()+400;
        bc.endY = transformComponent.y;

        return transformComponent;
    }

    public void spawn() {
        if(SPAWN_INTERVAL == 0){
            if (isAngeredBeesMode){
                createBug(BugType.BEE);
            } else{
                int probabilityValue = rand.nextInt(100);
                if (probabilityValue < 10) {
                    createBug(BugType.DRUNK);
                } else if (probabilityValue >= 10 && probabilityValue < 40) {
                    createBug(BugType.SIMPLE);
                } else if (probabilityValue >= 41 && probabilityValue < 60 ) {
                    createBug(BugType.CHARGER);
                } else if (probabilityValue >= 61 && probabilityValue < 70 ){
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
//        bugEntity.add(tc);
    }

    @Override
    public void update(float deltaTime) {
        if (!GameScreenScript.isPause && !GameScreenScript.isGameOver) {
            if(GameScreenScript.isStarted) {
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
                GameScreenScript.cameraShaker.initBlinking(40, 3);
                angeredBeesModeTimer = ANGERED_BEES_MODE_DURATION;
            }
        }
    }

}
