package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.uwsoft.editor.renderer.components.TransformComponent;

import java.util.Random;
import static com.mygdx.game.utils.GlobalConstants.*;

/**
 * Created by Teatree on 9/3/2015.
 */
public class BugComponent implements Component {

    public BugType type;
    public State state = State.IDLE;

    public int pointsMult;

    public CatmullRomSpline<Vector2> myCatmull;
    public float speed = 0.05f;
    public float current = 0;
    public Vector2 out;

    public float startX, startY;
    public float endX;
    public float endY;
    public float duration = 14;
    public float time;
    public Interpolation interpolation = Interpolation.exp5In;
    public boolean reverse, began, complete;
    public int alignment = Align.bottomLeft;

    public int points;

    public Rectangle boundsRect = new Rectangle();

    public float velocity = 0;
    public float startYPosition;

    public int counter = new Random().nextInt(MAX_IDLE_COUNT - MIN_IDLE_COUNTER) + MIN_IDLE_COUNTER;

    public BugComponent() {
        state = State.IDLE;
    }

    public BugComponent(BugType type) {
        this.type = type;
        this.state = State.IDLE;
        switch (type){
            case DRUNK: {
                points = 10;
                break;
            }
            case BEE: {
                points = 15;
                break;
            }
            case CHARGER: {
                points = 25;
                break;
            }
            case QUEENBEE: {
                points = 33;
                break;
            }
            default:{
                points = 10;
                break;
            }
        }
    }

    public enum State {
        IDLE,
        PREPARING,
        CHARGING,
        DEAD
    }

}
