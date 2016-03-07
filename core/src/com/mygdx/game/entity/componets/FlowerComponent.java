package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;

public class FlowerComponent implements Component {
    public static final float FLOWER_MOVE_SPEED = 33.5f;
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
        PHOENIX
    }
}
