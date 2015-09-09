package com.mygdx.game.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.uwsoft.editor.renderer.SceneLoader;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

/**
 * Created by Teatree on 9/3/2015.
 */
public class BugSpawnerSystem extends EntitySystem {

    private int SPAWN_MAX_X = -400;
    private int SPAWN_MIN_X = 300;
    private int SPAWN_MIN_Y = -200;
    private int SPAWN_MAX_Y = 1200;

    private Random rand;
    private SceneLoader sl;

    private int timer;
    private int spawnInterval = 200;

    public BugSpawnerSystem(SceneLoader sl) {
        this.sl = sl;
        init();
    }

    public void processEntity(float deltaTime) {
        timer++;

//        if (timer >= spawnInterval) {
//            bugs.add(spawner.spawn(bugGenerator.getBugSafe(stage), stage.getInstance()));
//            timer = 0;
//        }
    }

    private void init() {
        rand = new Random();

        SPAWN_MIN_X = -400;
        SPAWN_MIN_Y = 300;
        SPAWN_MAX_X = -200;
        SPAWN_MAX_Y = 1200;
    }

    private Vector2 getPos(){
        float x = rand.nextInt(SPAWN_MAX_X-SPAWN_MIN_X)+SPAWN_MIN_X;
        float y = rand.nextInt(SPAWN_MAX_Y-SPAWN_MIN_Y)+SPAWN_MIN_Y;

        return new Vector2(x,y);
    }

//    public Bug spawnUnsafe(Bug bug, Overlap2DStage stage) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
//        bug.setPosition(getPos());
//        stage.addActor(bug.getCompositeItem());
//        return bug;
//    }
//
//    public Bug spawn(Bug bug, Overlap2DStage stage){
//        try {
//            return spawnUnsafe(bug, stage);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
