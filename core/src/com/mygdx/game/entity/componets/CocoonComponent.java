package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import static com.mygdx.game.utils.GlobalConstants.*;

/**
 * Created by AnastasiiaRudyk on 31/10/2015.
 */
public class CocoonComponent implements Component {

    public int health = COCOON_HEALTH;
    public int counter;
    public State state;
    public Rectangle boundsRect;
    public boolean isCollision;

    public CocoonComponent() {
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
