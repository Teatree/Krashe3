package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.utils.GlobalConstants;

/**
 * Created by AnastasiiaRudyk on 31/10/2015.
 */
public class CacconComponent implements Component {

    public int health = GlobalConstants.COCOON_HEALTH;
    public int counter;
    public State state;
    public Rectangle boundsRect;
    public boolean isCollision;

    public CacconComponent() {
        this.boundsRect = new Rectangle();
        state = State.SPAWNING;
    }

    public enum State {
        IDLE,
        SPAWNING,
        HIT,
        DEAD;
    }
}
