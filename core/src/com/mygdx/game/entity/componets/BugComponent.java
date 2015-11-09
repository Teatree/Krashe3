package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.uwsoft.editor.renderer.components.TransformComponent;

import java.util.Random;
import static com.mygdx.game.utils.GlobalConstants.*;

/**
 * Created by Teatree on 9/3/2015.
 */
public class BugComponent implements Component {

    public BugType type;
    public State state = State.IDLE;

    public int points;

    public Rectangle boundsRect = new Rectangle();

    public float velocity = 0;
    public float startYPosition;

    public int counter = new Random().nextInt(MAX_IDLE_COUNT - MIN_IDLE_COUNTER) + MIN_IDLE_COUNTER;

    public BugComponent() {
        points = 1;
    }

    public enum State {
        IDLE,
        PREPARING,
        CHARGING,
        DEAD
    }

}
