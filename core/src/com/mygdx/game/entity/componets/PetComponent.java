package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.utils.SaveMngr;

import java.util.Random;

public class PetComponent implements Component {

    public static final int DEFAULT_EAT_DURATION = 26;
    public static final int OUTSIDE_DURATION_MAX = 1000;
    public static final int OUTSIDE_DURATION_MIN = 500;
    public static final int SPAWN_DURATION = 100;
    public static final int TACK_DURATION = 100;

    public static final float X_SPAWN_POSITION = 1049;
    public static final int Y_SPAWN_POSITION_MAX = 568;
    public static final int Y_SPAWN_POSITION_MIN = 370;

    public static final String CHARGING_ANI = "Charging";
    public static final String IDLE_ANI = "Idle";
    public static final String SPAWN_ANI = "Idle";
    public static final String TACK_ANI = "Preparing";
    public static final String EAt_ANI = "Charging";

    public State state;
    public Rectangle boundsRect;
    public float velocity;
    public int animationCounter;

    public int amountBugsBeforeCharging;
    public int totalEatenBugs;
    public int duringGameEatenBugs;
    public int eatenBugsCounter;

    public String name;
    public boolean bought;
    public boolean activated;
    public long cost;
    public boolean tryPeriod;
    public int tryPeriodDuration;

    public boolean isCollision;

    public PetComponent() {
        init();
        this.name = "pet";
    }

    private void init() {
        this.state = State.SPAWNING;
        this.boundsRect = new Rectangle();
        this.animationCounter = SPAWN_DURATION;
    }

    public PetComponent (SaveMngr.Pet pet){
        this.name = pet.name;
        this.activated = pet.activated;
        this.bought = pet.bought;
        this.cost = pet.cost;
        this.tryPeriod = pet.tryPeriod;
        this.tryPeriodDuration = pet.tryPeriodDuration;
        this.amountBugsBeforeCharging = pet.amountBugsBeforeCharging;
        this.totalEatenBugs = pet.totalEatenBugs;
        init();
    }

    public enum State {
        SPAWNING,
        IDLE,
        EATING,
        MOVE,
        CHARGING,
        OUTSIDE
    }

    public void setOutsideStateDuration(){
        this.animationCounter = new Random().nextInt(OUTSIDE_DURATION_MAX - OUTSIDE_DURATION_MIN) + OUTSIDE_DURATION_MIN;
    }

    public static int getNewPositionY (){
        return new Random().nextInt(Y_SPAWN_POSITION_MAX - Y_SPAWN_POSITION_MIN) + Y_SPAWN_POSITION_MIN;
    }

    public static void eatThatBug(PetComponent pet, Rectangle bugRectangle) {
        if (pet.boundsRect.overlaps(bugRectangle)){
            pet.eatenBugsCounter++;
            pet.totalEatenBugs++;
            pet.duringGameEatenBugs++;

            pet.state = State.EATING;
            pet.isCollision = true;
            pet.animationCounter = DEFAULT_EAT_DURATION;
        }
    }
}
