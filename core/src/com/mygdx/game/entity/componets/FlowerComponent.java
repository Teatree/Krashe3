package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.mygdx.game.utils.GlobalConstants;

/**
 * Created by Teatree on 9/3/2015.
 */
public class FlowerComponent implements Component {

    private int maxHp = GlobalConstants.DEFAULT_MAX_HP;
    private int curHp = maxHp;

//    public Rectangle boundsRect = new Rectangle();

    public State state = State.IDLE;

    public int eatCounter = 30;

    public enum State{
        IDLE,
        ATTACK,
        RETREAT,
        IDLE_BITE,
        ATTACK_BITE;
    }

    public boolean isEating(){
        return state == State.IDLE_BITE || state == State.ATTACK_BITE;
    }


}
