package com.fd.etf.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.fd.etf.Main;
import com.fd.etf.stages.GameStage;
import com.fd.etf.utils.SaveMngr;
import com.uwsoft.editor.renderer.data.CompositeItemVO;

import java.util.Random;

public class PetComponent extends ShopItem implements Component, Pool.Poolable{

    public static final int OUTSIDE_DURATION_MAX = 10000;
    public static final int OUTSIDE_DURATION_MIN = 5000;

    public static final float X_SPAWN_POSITION = 1150;
    public static final int Y_SPAWN_POSITION_MAX = 568;
    public static final int Y_SPAWN_POSITION_MIN = 370;
    private static final String HEAD_PREFFIX = "_head";
    private static final String PET_CANNON = "pet_cannon";

    public State state;
    public Rectangle boundsRect;
    public float velocity;
    public int projectileSpawnIntervalFrames;
    public boolean isCollision;
    public boolean isBiteDash;
    public float previousX;
//    public boolean tappedback;

    public int outsideCounter;
    public int amountBugsBeforeCharging;
    public int totalEatenBugs;
    public int duringGameEatenBugs;

    public int eatenBugsCounter;

    public String petCannonName;
    public Entity petCannon;
    public Entity petHead;

    public int stageCounter;
    public boolean isHardCurr;

    public GameStage gameStage;

    public PetComponent(GameStage gameStage) {
        currencyType = HARD;
        this.gameStage = gameStage;
    }

    public PetComponent(SaveMngr.PetJson petJson) {
        currencyType = HARD;
        this.name = petJson.name;
        this.enabled = petJson.activated;
        this.bought = petJson.bought;
        this.cost = petJson.cost;
        this.costDisc = petJson.costDisc;
        this.disc = petJson.disc;
        this.projectileSpawnIntervalFrames = petJson.projectileSpawnIntervalFrames;
        this.tryPeriod = petJson.tryPeriod;
        this.tryPeriodDuration = petJson.tryPeriodDuration - (System.currentTimeMillis() - petJson.tryPeriodStart) / 1000;
        this.amountBugsBeforeCharging = petJson.amountBugsBeforeCharging;
        this.totalEatenBugs = petJson.totalEatenBugs;
        this.shopIcon = petJson.shopIcon;
        this.tryPeriodTimer = petJson.tryPeriodTimer;
        this.tryPeriodStart = petJson.tryPeriodStart;
        this.transactionId = petJson.transactionId;
        this.petCannonName = petJson.petCannonName;
//        this.petName = petJson.petName;
//        this.logoName = petJson.logoName;
        this.discountTransactionId = petJson.discountTransactionId;
        this.isHardCurr = petJson.isHardCurr;
//        init();
    }

    public static int getNewPositionY() {
        return new Random().nextInt(Y_SPAWN_POSITION_MAX - Y_SPAWN_POSITION_MIN) + Y_SPAWN_POSITION_MIN;
    }

    public static void eatThatBug(PetComponent pet, Rectangle bugRectangle) {
        if (pet.boundsRect.overlaps(bugRectangle)) {

            if (!pet.state.equals(State.DASH) && !pet.state.equals(State.TAPPED)) {
                pet.state = State.BITE;
                pet.isCollision = true;
            } else if (pet.state.equals(State.DASH)) {
                pet.isBiteDash = true;
            }
        }
    }

    public void init() {
        this.state = State.SPAWNING;
        this.boundsRect = new Rectangle();
        eatenBugsCounter = 0;

        loadFromLib(name);
        stageCounter = 0;
    }

    @Override
    public void apply(GameStage gameStage) {
        this.enabled = true;
        gameStage.gameScript.hideCurrentPet();
        gameStage.gameScript.fpc.currentPet = this;
        gameStage.gameScript.changePet = true;
        for (PetComponent petComponent : gameStage.gameScript.fpc.pets){
            if (!petComponent.equals(this)){
                petComponent.enabled = false;
            }
        }
//        gameStage.gameScript.initPet();
    }

    @Override
    public void disable(GameStage gameStage) {
        this.enabled = false;
        gameStage.gameScript.changePet = true;
//        fpc.currentPet = null;
    }

    @Override
    public void buyAndUse(GameStage gameStage) {
        this.bought = true;
        if (gameStage.gameScript.fpc.currentPet != null) {
            gameStage.gameScript.fpc.currentPet.tryPeriod = false;
        }
//        gameStage.gameScript.fpc.currentPet = this;
        apply(gameStage);
    }

    @Override
    public void buyHard(GameStage gameStage) {
        this.buyAndUse(gameStage);
//        Main.mainController.getBirdPet(gameStage, this);
    }

    @Override
    public void buyHardDiscount(GameStage gameStage) {
        Main.mainController.getBirdPetDiscount(gameStage, this);
    }

    public void setOutsideStateDuration() {
        this.outsideCounter = new Random().nextInt(OUTSIDE_DURATION_MAX - OUTSIDE_DURATION_MIN) + OUTSIDE_DURATION_MIN;
    }

    @Override
    public void reset() {

    }
//
//    public String updateTryPeriodTimer() {
//        float deltaTime = Gdx.graphics.getDeltaTime();
//
//        tryPeriodTimer = (tryPeriodStart / 1000 + tryPeriodDuration) - System.currentTimeMillis() / 1000;
//
//        int minutes = ((int) tryPeriodTimer) / 60;
//        int seconds = ((int) tryPeriodTimer) % 60;
//        String result =  "" + minutes + " : " + seconds;
//        if (tryPeriodTimer < 0 ){
//            result = TrialTimer.TIMER_LBL_TIME_UP;
//        }
//        return result;
//    }

    public enum State {
        SPAWNING,
        IDLE,
        BITE,
        TAPPED,
        TAPPED_BACK,
        DASH,
        OUTSIDE
    }

    private void loadFromLib(String petNamecaps) {
        String petName = petNamecaps.toLowerCase();
        gameStage.sceneLoader.rm.addSpriterToLoad(petName + HEAD_PREFFIX);
        gameStage.sceneLoader.rm.addSpriterToLoad(PET_CANNON);

        CompositeItemVO tempItemC = gameStage.sceneLoader.loadVoFromLibrary(petName + HEAD_PREFFIX);
        petHead = gameStage.sceneLoader.entityFactory.createSPRITERentity(gameStage.sceneLoader.getRoot(), tempItemC);
        gameStage.sceneLoader.getEngine().addEntity(petHead);

        CompositeItemVO tempItemCannon = gameStage.sceneLoader.loadVoFromLibrary(petCannonName);
        petCannon = gameStage.sceneLoader.entityFactory.createSPRITERentity(gameStage.sceneLoader.getRoot(), tempItemCannon);
        gameStage.sceneLoader.getEngine().addEntity(petCannon);
    }
}
