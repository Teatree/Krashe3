package com.mygdx.etf.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.*;

/**
 * Created by Teatree on 10/6/2015.
 */
public class UmbrellaComponent implements Component {

    public Rectangle boundsRect = new Rectangle();

    public Bezier<Vector2> myCatmull;
    public float speed = 0.15f;
    public float current = 0;
    public Vector2 out;
    public Vector2[] dataSet;

    public State state;
    public int pointsMult;

    public UmbrellaComponent() {
        this.state = UmbrellaComponent.State.PUSH;
        pointsMult = 2;
    }

    public enum State {
        PUSH, FLY, DEAD
    }
}
