package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.mygdx.game.utils.GlobalConstants;

/**
 * Created by Teatree on 9/3/2015.
 */
public class FlowerComponent implements Component {
    public static final float FLOWER_MOVE_SPEED = 13.5f;
    public static State state = State.IDLE;
    private int maxHp = GlobalConstants.DEFAULT_MAX_HP;
    private int curHp = maxHp;

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
