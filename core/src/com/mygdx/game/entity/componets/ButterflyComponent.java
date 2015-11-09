package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

/**
 * Created by Teatree on 9/3/2015.
 */
public class ButterflyComponent implements Component {

    public Rectangle boundsRect = new Rectangle();
    public float velocityX;
    public float velocityY;
    public float gravity;
    public float speedIncrCoeficient = 1f;
    public float gravityDecreaseMultiplier = 1.5f;

    //(310, 400, 45, 55);
    public int randXmin = 310;
    public int randXmax = 400;
    public int randYmin = 45;
    public int randYmax = 55;

    public State state;
    public int points;

    public ButterflyComponent() {
        state = State.SPAWN;
        points = 3;
    }

    public enum State {
        SPAWN, FLY, DEAD
    }
}
