package com.fd.etf.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.fd.etf.stages.GameStage;
import com.fd.etf.system.BugSpawnSystem;

import java.util.Random;

import static com.fd.etf.utils.GlobalConstants.MAX_IDLE_COUNT;
import static com.fd.etf.utils.GlobalConstants.MIN_IDLE_COUNTER;

public class BugComponent implements Component, Pool.Poolable {


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
    public static final int DRUNK_BUG_MOVE_DURATION_BASE = 18;
    public static final int DRUNK_BUG_AMPLITUDE_BASE = 50;
    public static final int BEE_MOVE_DURATION_BASE = 16;
    public static final int BEE_AMPLITUDE_BASE = 0;
    public static final int QUEENBEE_MOVE_DURATION_BASE = 18;
    public static final int QUEENBEE_AMPLITUDE_BASE = 50;
    public static final int CHARGER_BUG_MOVE_BASE = 115;
    public static final int SIMPLE_BUG_MOVE_DURATION_BASE = 16;
    public static final int SIMPLE_BUG_AMPLITUDE_BASE = 0;

    public String type;
    public String state = IDLE;

    public float startX, startY;
    public float endX;
    public float endY;
    public float y;
    public float duration = 14;
    public float amplitude = 50;
    public float time;
    public Interpolation interpolation = Interpolation.exp5;
    public boolean reverse, began, complete;

    public float IDLE_MVMNT_SPEED;
    public float PREPARING_MVMNT_SPEED;
    public float CHARGING_MVMNT_SPEED;

    public boolean isPlayingDeathAnimation;

    public int points;

    public Rectangle boundsRect = new Rectangle();
    public Rectangle boundsRectScary = new Rectangle();

    public float velocity = 0;
    public float startYPosition;

    public int counter = new Random().nextInt(MAX_IDLE_COUNT - MIN_IDLE_COUNTER) + MIN_IDLE_COUNTER;
    private GameStage gameStage;
    public float scareCounter;
    public boolean isAngeredBee;

    public BugComponent(GameStage gameStage, String type, BugSpawnSystem.Multiplier m) {
        this.gameStage = gameStage;
        this.type = type;
        this.state = IDLE;
        switch (type) {
            case DRUNK: {
                duration = DRUNK_BUG_MOVE_DURATION_BASE * m.drunkBugMoveDuration * gameStage.gameScript.fpc.level.drunkBugMoveDuration;
                amplitude = DRUNK_BUG_AMPLITUDE_BASE * m.drunkBugAmplitude * gameStage.gameScript.fpc.level.drunkBugAmplitude;
                points = 4;
                break;
            }
            case BEE: {
                duration = BEE_MOVE_DURATION_BASE * m.beeMoveDuration * gameStage.gameScript.fpc.level.beeMoveDuration;
                amplitude = BEE_AMPLITUDE_BASE * m.beeAmplitude * gameStage.gameScript.fpc.level.beeAmplitude;
                points = 6;
                break;
            }
            case CHARGER: {
                points = 10;
                IDLE_MVMNT_SPEED = CHARGER_BUG_MOVE_BASE * m.chargerBugMove * gameStage.gameScript.fpc.level.chargerBugMove;
                PREPARING_MVMNT_SPEED = 40 * m.chargerBugMove * gameStage.gameScript.fpc.level.chargerBugMove;
                CHARGING_MVMNT_SPEED = 505 * m.chargerBugMove * gameStage.gameScript.fpc.level.chargerBugMove;
                break;
            }
            case QUEENBEE: {
                duration = QUEENBEE_MOVE_DURATION_BASE * m.queenBeeMoveDuration * gameStage.gameScript.fpc.level.queenBeeMoveDuration;
                amplitude = QUEENBEE_AMPLITUDE_BASE * m.queenBeeAmplitude * gameStage.gameScript.fpc.level.queenBeeAmplitude;
                points = 12;
                break;
            }
            default: {
                duration = SIMPLE_BUG_MOVE_DURATION_BASE * m.simpleBugMoveDuration * gameStage.gameScript.fpc.level.simpleBugMoveDuration;
                amplitude = SIMPLE_BUG_AMPLITUDE_BASE * m.simpleBugAmplitude * gameStage.gameScript.fpc.level.simpleBugAmplitude;
                points = 3;
                break;
            }
        }
    }

    @Override
    public void reset() {
        state = IDLE;

        startX = 0;
        startY= 0;
        endX= 0;
        endY= 0;
        duration = 14;
        amplitude = 50;
        time = 0;
        interpolation = Interpolation.exp5;
        reverse = false;
        began = false;
        complete = false;
        points = 0;

        velocity = 0;
        startYPosition = 0;

        counter = new Random().nextInt(MAX_IDLE_COUNT - MIN_IDLE_COUNTER) + MIN_IDLE_COUNTER;
    }
}
