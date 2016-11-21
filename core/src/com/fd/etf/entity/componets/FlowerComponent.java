package com.fd.etf.entity.componets;

import com.badlogic.ashley.core.Component;

public class FlowerComponent implements Component {
    public static final float FLOWER_MOVE_SPEED = 47.5f;
    public static final int FLOWER_Y_POS = 129;
    public static final int FLOWER_X_POS = 1023;
    public static final int FLOWER_MAX_Y_POS = 640;
    public static final float FLOWER_SCALE = 0.7f;

    public static State state = State.IDLE;

    public FlowerComponent() {
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
        PHOENIX,
        LOSING,
        DEAD,
        REVIVE_ADS,
        NO_REVIVE
    }
}
