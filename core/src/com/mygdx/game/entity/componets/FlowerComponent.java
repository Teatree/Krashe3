package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.mygdx.game.utils.GlobalConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Teatree on 9/3/2015.
 */
public class FlowerComponent implements Component {
    public static final int DEFAULT_EAT_COUNTER = 26;
    public static final int DEFAULT_TRANS_COUNTER = 8;

    private int maxHp = GlobalConstants.DEFAULT_MAX_HP;
    private int curHp = maxHp;
    public State state = State.IDLE;
    public int eatCounter = DEFAULT_EAT_COUNTER;
    public int transCounter = DEFAULT_TRANS_COUNTER;

    public enum State {
        IDLE,
        ATTACK,
        TRANSITION,
        RETREAT,
        IDLE_BITE,
        ATTACK_BITE;
    }

    public boolean isEating(){
        return state == State.IDLE_BITE || state == State.ATTACK_BITE;
    }
}
