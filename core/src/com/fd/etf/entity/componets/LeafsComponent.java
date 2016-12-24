package com.fd.etf.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class LeafsComponent implements Component, Pool.Poolable{
    public static final int LEAFS_Y_POS = 135;
    public static final int LEAFS_X_POS = 1023;
    public static final float LEAFS_SCALE = 0.7f;

    public static State state = State.IDLE;

    public LeafsComponent() {
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
        PHOENIX
    }
}
