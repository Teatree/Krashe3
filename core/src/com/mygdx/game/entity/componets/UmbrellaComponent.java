package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.sun.org.apache.bcel.internal.generic.PUSH;

import java.util.Random;

/**
 * Created by Teatree on 10/6/2015.
 */
public class UmbrellaComponent implements Component {
    public float speedIncrCoeficient = 1f;
    public float gravityDecreaseMultiplier = 1.5f;

    public Random random = new Random();
    public Rectangle boundsRect = new Rectangle();
    public float velocityX;
    public float velocityY;
    public float gravity;

    public State state;

    public enum State {
        PUSH, FLY, DEAD
    }
}
