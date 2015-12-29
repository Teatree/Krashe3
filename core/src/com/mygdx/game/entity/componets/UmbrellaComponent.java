package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Align;

import java.util.Random;

/**
 * Created by Teatree on 10/6/2015.
 */
public class UmbrellaComponent implements Component {
    public float speedIncrCoeficient = 1f;
    public float gravityDecreaseMultiplier = 1.5f;

    public Rectangle boundsRect = new Rectangle();
    public float velocityX;
    public float velocityY;
    public float gravity;

    public State state;
    public int pointsMult;

    public CatmullRomSpline<Vector2> myCatmull;
    public float speed = 0.25f;
    public float current = 0;
    public Vector2 out;

    public float startX, startY;
    public float endX = Gdx.graphics.getWidth()-120;
    public float endY = Gdx.graphics.getHeight()/2;
    public float duration = 17;
    public float time;
    public Interpolation interpolation = Interpolation.bounce;
    public boolean reverse, began, complete;
    public int alignment = Align.bottomLeft;

    public UmbrellaComponent() {
        this.state = UmbrellaComponent.State.PUSH;
        pointsMult = 2;

        Vector2[] dataSet = new Vector2[2];
        dataSet[0] = new Vector2(Gdx.graphics.getWidth()-100, Gdx.graphics.getHeight()/2);
        dataSet[1] = new Vector2(40, Gdx.graphics.getHeight()/2);

        myCatmull = new CatmullRomSpline<Vector2>(dataSet, true);
        out = new Vector2(340, Gdx.graphics.getHeight()/4);
        myCatmull.valueAt(out, 5);
        myCatmull.derivativeAt(out, 5);
    }

    public enum State {
        PUSH, FLY, DEAD
    }
}
