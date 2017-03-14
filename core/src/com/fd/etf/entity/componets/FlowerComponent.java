package com.fd.etf.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class FlowerComponent implements Component, Pool.Poolable{
    public static final int FLOWER_Y_POS = 129;
    public static final int FLOWER_X_POS = 1023;
    public static final float FLOWER_SCALE = 0.7f;

    public static boolean isLosing;
    public static State state = State.IDLE;

    public FlowerComponent() {
        state = State.IDLE;
    }

    @Override
    public void reset() {

    }

    public enum State {
        IDLE,
        ATTACK,
        TRANSITION,
        TRANSITION_BACK,
        RETREAT,
        IDLE_BITE,
        ATTACK_BITE,
        PHOENIX,
        LOSING,
        SULKING,
        REVIVE_ADS,
        NO_REVIVE
    }
}
