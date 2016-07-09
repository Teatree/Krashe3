package com.mygdx.etf.entity.componets;

import com.badlogic.ashley.core.Component;

public class LeafsComponent implements Component {
    public static final int LEAFS_Y_POS = 131;
    public static final int LEAFS_X_POS = 1023;
    public static final float LEAFS_SCALE = 0.6f;

    public static State state = State.IDLE;

    public LeafsComponent() {
        state = State.IDLE;
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
