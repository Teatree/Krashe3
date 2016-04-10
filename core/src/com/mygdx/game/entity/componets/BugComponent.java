package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

import static com.mygdx.game.utils.GlobalConstants.MAX_IDLE_COUNT;
import static com.mygdx.game.utils.GlobalConstants.MIN_IDLE_COUNTER;

public class BugComponent implements Component {


    public static final String IDLE = "IDLE";
    public static final String PREPARING = "PREPARING";
    public static final String CHARGING = "CHARGING";
    public static final String SCARED = "SCARED";
    public static final String DEAD = "DEAD";

    public static final String SIMPLE = "SIMPLE";
    public static final String DRUNK = "DRUNK";
    public static final String CHARGER = "CHARGER";
    public static final String BEE = "BEE";
    public static final String QUEENBEE = "QUEENBEE";

    public String type;
    public String state = IDLE;

    public float startX, startY;
    public float endX;
    public float endY;
    public float duration = 14;
    public float time;
    public Interpolation interpolation = Interpolation.exp5;
    public boolean reverse, began, complete;

    public int points;

    public Rectangle boundsRect = new Rectangle();
    public Rectangle boundsRectScary = new Rectangle();

    public float velocity = 0;
    public float startYPosition;

    public int counter = new Random().nextInt(MAX_IDLE_COUNT - MIN_IDLE_COUNTER) + MIN_IDLE_COUNTER;

    public BugComponent() {
        state = IDLE;
    }

    public BugComponent(String type) {
        this.type = type;
        this.state = IDLE;
        switch (type) {
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
            default: {
                points = 10;
                break;
            }
        }
    }
//
//    public enum State {
//        IDLE,
//        PREPARING,
//        CHARGING,
//        SCARED,
//        DEAD
//    }

//    public enum BugType {
//        SIMPLE,
//        DRUNK,
//        CHARGER,
//        BEE,
//        QUEENBEE
//    }
}
