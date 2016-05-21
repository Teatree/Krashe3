package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.system.BugSpawnSystem;
import com.mygdx.game.utils.GlobalConstants;

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
    public float amplitude = 50;
    public float time;
    public Interpolation interpolation = Interpolation.exp5;
    public boolean reverse, began, complete;

    public float IDLE_MVMNT_SPEED;
    public float PREPARING_MVMNT_SPEED;
    public float CHARGING_MVMNT_SPEED;

    public int points;

    public Rectangle boundsRect = new Rectangle();
    public Rectangle boundsRectScary = new Rectangle();

    public float velocity = 0;
    public float startYPosition;

    public int counter = new Random().nextInt(MAX_IDLE_COUNT - MIN_IDLE_COUNTER) + MIN_IDLE_COUNTER;

    public BugComponent(String type, BugSpawnSystem.Multiplier m) {
        this.type = type;
        this.state = IDLE;
        switch (type) {
            case DRUNK: {
                duration = 12 *m.drunkBugMoveDuration *GameStage.gameScript.fpc.level.drunkBugMoveDuration;
                amplitude = 50 *m.drunkBugAmplitude *GameStage.gameScript.fpc.level.drunkBugAmplitude;
                points = 10;
                break;
            }
            case BEE: {
                duration = 14 *m.beeMoveDuration *GameStage.gameScript.fpc.level.beeMoveDuration;
                amplitude = 0 *m.beeAmplitude *GameStage.gameScript.fpc.level.beeAmplitude;
                points = 15;
                break;
            }
            case CHARGER: {
                points = 25;
                IDLE_MVMNT_SPEED = 115 * m.chargerBugMove * GameStage.gameScript.fpc.level.chargerBugMove;
                PREPARING_MVMNT_SPEED = 40 * m.chargerBugMove * GameStage.gameScript.fpc.level.chargerBugMove;
                CHARGING_MVMNT_SPEED = 505 * m.chargerBugMove * GameStage.gameScript.fpc.level.chargerBugMove;
                break;
            }
            case QUEENBEE: {
                duration = 12 *m.queenBeeMoveDuration *GameStage.gameScript.fpc.level.queenBeeMoveDuration;
                amplitude = 50 *m.queenBeeAmplitude *GameStage.gameScript.fpc.level.queenBeeAmplitude;
                points = 33;
                break;
            }
            default: {
                duration = 14 *m.simpleBugMoveDuration *GameStage.gameScript.fpc.level.simpleBugMoveDuration;
                amplitude = 0 *m.simpleBugAmplitude *GameStage.gameScript.fpc.level.simpleBugAmplitude;
                points = 10;
                break;
            }
        }
    }
}
