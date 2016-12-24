package com.fd.etf.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;

public class CocoonComponent implements Component, Pool.Poolable {

    public static final float COCOON_SCALE = 0.5f;
    public static final int COCOON_X = 980;
    public static final int COCOON_Y = 780;

    public int hitCounter = 0;
    public State state;
    public Rectangle boundsRect;
    public boolean canHit;

    public CocoonComponent() {
        this.boundsRect = new Rectangle();
    }

    @Override
    public void reset() {
        hitCounter = 0;
        state = State.DEAD;
        boundsRect = new Rectangle();
        canHit = false;
    }

    public enum State {
        IDLE,
        SPAWNING,
        HIT,
        DEAD
    }

    public static class CocoonMultiplier {
        public float minSpawnCoefficient;
        public float maxSpawnCoefficient;
        public int startOn;
        public int finishOn;
    }
}
