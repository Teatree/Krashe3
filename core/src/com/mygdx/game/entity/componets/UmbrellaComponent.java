package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
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



    public float startX, startY;
    public float endX = Gdx.graphics.getWidth();
    public float endY = Gdx.graphics.getHeight();
    public float duration = 17;
    public float time;
    public Interpolation interpolation = Interpolation.bounceOut;
    public boolean reverse, began, complete;
    public int alignment = Align.bottomLeft;

    public UmbrellaComponent() {
        this.state = UmbrellaComponent.State.PUSH;
        pointsMult = 2;
    }

    public enum State {
        PUSH, FLY, DEAD
    }
}
