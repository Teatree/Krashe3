package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;

/**
 *
 * Created by Teatree on 9/3/2015.
 */
public class DandelionComponent implements Component {

    public State state;

    public enum State {
        IDLE,
        GROWING,
        DYING,
        DEAD;
    }
}
