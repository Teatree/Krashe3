package com.fd.etf.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Teatree on 9/3/2015.
 */
public class ButterflyComponent implements Component {

//    public Rectangle boundsRect = new Rectangle();
//    public float velocityX;
//    public float velocityY;
//    public float gravity;
//    public float speedIncrCoeficient = 1f;
//    public float gravityDecreaseMultiplier = 1.5f;
//
//    //(310, 400, 45, 55);
//    public int randXmin = 310;
//    public int randXmax = 400;
//    public int randYmin = 45;
//    public int randYmax = 55;

    public State state;
    public int points;

    public Rectangle boundsRect = new Rectangle();

    public Bezier<Vector2> myCatmull;
    public float speed = 0.15f;
    public float current = 0;
    public Vector2 out;
    public Vector2[] dataSet;

    public ButterflyComponent() {
        state = State.SPAWN;
        points = 3;
    }

    public enum State {
        SPAWN, FLY, DEAD
    }
}
