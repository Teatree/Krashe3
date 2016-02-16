package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.utils.SaveMngr;

import java.util.Random;

public class PetComponent extends ShopItem implements Component {

    public static final int DEFAULT_BITE_DURATION = 26;
    public static final int OUTSIDE_DURATION_MAX = 1000;
    public static final int OUTSIDE_DURATION_MIN = 500;
    public static final int SPAWN_DURATION = 40;
    public static final int TAP_DURATION = 100;

    public static final float X_SPAWN_POSITION = 1200;
    public static final int Y_SPAWN_POSITION_MAX = 568;
    public static final int Y_SPAWN_POSITION_MIN = 370;

    public static final String CHARGING_ANI = "dash";
    public static final String IDLE_ANI = "idle";
    public static final String SPAWN_ANI = "spawn";
    public static final String TACK_ANI = "tapped";
    public static final String EAt_ANI = "bite";

    public State state;
    public Rectangle boundsRect;
    public float velocity;
    public int outsideCounter;

    public int amountBugsBeforeCharging;
    public int totalEatenBugs;
    public int duringGameEatenBugs;
    public int eatenBugsCounter;

    public boolean tryPeriod;
    public int tryPeriodDuration;

    public boolean isCollision;

    public PetComponent() {
        init();
        currencyType = CurrencyType.HARD;
    }

    public PetComponent(SaveMngr.PetJson pet) {
        currencyType = CurrencyType.HARD;
        this.name = pet.name;
        this.enabled = pet.activated;
        this.bought = pet.bought;
        this.cost = pet.cost;
        this.tryPeriod = pet.tryPeriod;
        this.tryPeriodDuration = pet.tryPeriodDuration;
        this.amountBugsBeforeCharging = pet.amountBugsBeforeCharging;
        this.totalEatenBugs = pet.totalEatenBugs;
        this.shopIcon = pet.shopIcon;
        init();
    }

    public static int getNewPositionY() {
        return new Random().nextInt(Y_SPAWN_POSITION_MAX - Y_SPAWN_POSITION_MIN) + Y_SPAWN_POSITION_MIN;
    }

    public static void eatThatBug(PetComponent pet, Rectangle bugRectangle) {
        if (pet.boundsRect.overlaps(bugRectangle)) {
            pet.eatenBugsCounter++;
            pet.totalEatenBugs++;
            pet.duringGameEatenBugs++;

            if (!pet.state.equals(State.DASH)) {
                pet.state = State.BITE;
                pet.isCollision = true;
            }
        }
    }

    public void init() {
        this.state = State.SPAWNING;
        this.boundsRect = new Rectangle();
//        this.animationCounter = SPAWN_DURATION;
        eatenBugsCounter = 0;
    }

    @Override
    public void apply(FlowerPublicComponent fpc) {
        this.enabled = true;
        fpc.currentPet = this;
    }

    @Override
    public void disable(FlowerPublicComponent fpc) {
        this.enabled = false;
        fpc.currentPet = null;
    }

    @Override
    public void buyAndUse(FlowerPublicComponent fpc) {
        this.bought = true;
        apply(fpc);
    }

    public void setOutsideStateDuration() {
        this.outsideCounter = new Random().nextInt(OUTSIDE_DURATION_MAX - OUTSIDE_DURATION_MIN) + OUTSIDE_DURATION_MIN;
    }

    public enum State {
        SPAWNING,
        IDLE,
        BITE,
        TAPPED,
        DASH,
        OUTSIDE
    }

}
