package com.mygdx.etf.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.brashmonkey.spriter.Entity;
import com.mygdx.etf.Main;
import com.mygdx.etf.stages.GameStage;
import com.mygdx.etf.utils.SaveMngr;

import java.util.Random;

public class PetComponent extends ShopItem implements Component {

    public static final int OUTSIDE_DURATION_MAX = 1000;
    public static final int OUTSIDE_DURATION_MIN = 500;

    public static final float X_SPAWN_POSITION = 1150;
    public static final int Y_SPAWN_POSITION_MAX = 568;
    public static final int Y_SPAWN_POSITION_MIN = 370;

    public State state;
    public Rectangle boundsRect;
    public float velocity;
    public int outsideCounter;

    public int amountBugsBeforeCharging;
    public int totalEatenBugs;
    public int duringGameEatenBugs;
    public int eatenBugsCounter;

    public boolean tryPeriod;
    public long tryPeriodDuration;
    public long tryPeriodStart;
    public long tryPeriodTimer;

    public boolean isCollision;

    public String petCannonName;
    public com.badlogic.ashley.core.Entity petCannon;

    public PetComponent() {
//        init();
        currencyType = HARD;
    }

    public PetComponent(SaveMngr.PetJson pet) {
        currencyType = HARD;
        this.name = pet.name;
        this.enabled = pet.activated;
        this.bought = pet.bought;
        this.cost = pet.cost;
        this.tryPeriod = pet.tryPeriod;
        this.tryPeriodDuration = pet.tryPeriodDuration - (System.currentTimeMillis() - pet.tryPeriodStart) / 1000;
        this.amountBugsBeforeCharging = pet.amountBugsBeforeCharging;
        this.totalEatenBugs = pet.totalEatenBugs;
        this.shopIcon = pet.shopIcon;
        this.tryPeriodTimer = pet.tryPeriodTimer;
        this.tryPeriodStart = pet.tryPeriodStart;
        this.transactionId = pet.transactionId;
        this.petCannonName = pet.petCannonName;
//        init();
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
        eatenBugsCounter = 0;

        petCannon = GameStage.gameScript.gameItem.getChild(petCannonName).getEntity();
    }

    @Override
    public void apply() {
        this.enabled = true;
        GameStage.gameScript.fpc.currentPet = this;
    }

    @Override
    public void disable() {
        this.enabled = false;
//        fpc.currentPet = null;
    }

    @Override
    public void buyAndUse() {
        this.bought = true;
        if (GameStage.gameScript.fpc.currentPet != null) {
            GameStage.gameScript.fpc.currentPet.tryPeriod = false;
        }
        apply();
    }

    @Override
    public void buyHard() {
        Main.mainController.getBirdPet(this);
    }

    public void setOutsideStateDuration() {
        this.outsideCounter = new Random().nextInt(OUTSIDE_DURATION_MAX - OUTSIDE_DURATION_MIN) + OUTSIDE_DURATION_MIN;
    }

    public String updateTryPeriodTimer() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        tryPeriodTimer = (tryPeriodStart / 1000 + tryPeriodDuration) - System.currentTimeMillis() / 1000;

        int minutes = ((int) tryPeriodTimer) / 60;
        int seconds = ((int) tryPeriodTimer) % 60;
        return "" + minutes + " : " + seconds;
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
