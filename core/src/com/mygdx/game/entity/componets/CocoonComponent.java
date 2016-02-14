package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import static com.mygdx.game.utils.GlobalConstants.*;

public class CocoonComponent implements Component {

    public int hitCounter = 0;
    public State state;
    public Rectangle boundsRect;
    public boolean canHit;

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
