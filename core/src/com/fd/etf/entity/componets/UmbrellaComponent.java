package com.fd.etf.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

import java.util.List;

/**
 * Created by Teatree on 10/6/2015.
 */
public class UmbrellaComponent implements Component, Pool.Poolable{

    public static float SPAWN_INTERVAL_BASE = 25;
    public static float SPAWNING_TIME = 0.2f;

    public static float INIT_SPAWN_X = 1165;
    public static float INIT_SPAWN_Y = 600;

    public Rectangle boundsRect = new Rectangle();

    public Bezier<Vector2> myCatmull;
    public float speed = 0.15f;
    public float current = 0;
    public Vector2 out;
    public Vector2[] dataSet;

    public State state;
    public int pointsMult;

    public float getSpawningTimeCounter = 2;
    public int blinkCounter;

    public static List<DandelionMultiplier> multipliers;
    public static DandelionMultiplier currentMultiplier;

    public UmbrellaComponent() {
        this.state = State.PUSH;
        getSpawningTimeCounter = 0;
        pointsMult = 2;
        blinkCounter = 9;
    }

    @Override
    public void reset() {
        this.state = State.PUSH;
        getSpawningTimeCounter = 0;
        pointsMult = 2;
        blinkCounter = 9;
    }

    public enum State {
        PUSH, FLY, DEAD, SPAWNING
    }

    public void setToSpawningState(){
        state = State.SPAWNING;
        getSpawningTimeCounter = 2;
    }

    public static class DandelionMultiplier {
        public float minSpawnCoefficient;
        public float maxSpawnCoefficient;
        public int startOn;
        public int finishOn;
    }
}
