package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.utils.SaveMngr;

import java.util.Random;

public class PetComponent implements Component {

    public static final int DEFAULT_EAT_COUNTER = 26;
    public static final int DEFAULT_IDLE_VELOCITY = 0;
    public static final int IDLE_DURATION_MAX = 3000;
    public static final int IDLE_DURATION_MIN = 1000;
    public static final int SPAWN_DURATION = 100;

    public State state;
    public int eatCounter = DEFAULT_EAT_COUNTER;
    public Rectangle boundsRect;
    public float velocity;
    public int counter;

    public String name;
    public boolean bought;
    public boolean activated;
    public long cost;
    public boolean tryPeriod;
    public int tryPeriodDuration;
//    public float duration = 14;
//    public float time;

    public static final String CHARGING_ANI = "Charging";
    public static final String IDLE_ANI = "Idle";
    public static final String SPAWN_ANI = "Idle";
    public static final String PREPARING_ANI = "Preparing";

    public PetComponent() {
        this.state = State.SPAWNING;
        this.boundsRect = new Rectangle();
        this.counter = SPAWN_DURATION;
        this.name = "pet";
    }

    public PetComponent (SaveMngr.Pet pet){
        this.name = pet.name;
        this.activated = pet.activated;
        this.bought = pet.bought;
        this.cost = pet.cost;
        this.tryPeriod = pet.tryPeriod;
        this.tryPeriodDuration = pet.tryPeriodDuration;

        this.state = State.SPAWNING;
        this.boundsRect = new Rectangle();
        this.counter = SPAWN_DURATION;
    }

    public enum State {
        SPAWNING,
        IDLE,
        EATING,
        PREPARING,
        CHARGING,
        OUTSIDE
    }

    public void setIdleStateDuration (){
        this.counter = new Random().nextInt(IDLE_DURATION_MAX - IDLE_DURATION_MIN) + IDLE_DURATION_MIN;
    }
}
