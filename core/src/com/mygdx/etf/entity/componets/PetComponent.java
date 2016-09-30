package com.mygdx.etf.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.etf.Main;
import com.mygdx.etf.stages.GameStage;
import com.mygdx.etf.utils.SaveMngr;
import com.uwsoft.editor.renderer.data.CompositeItemVO;

import java.util.Random;

import static com.mygdx.etf.stages.GameStage.sceneLoader;

public class PetComponent extends ShopItem implements Component {

    public static final int OUTSIDE_DURATION_MAX = 1000;
    public static final int OUTSIDE_DURATION_MIN = 500;

    public static final float X_SPAWN_POSITION = 1150;
    public static final int Y_SPAWN_POSITION_MAX = 568;
    public static final int Y_SPAWN_POSITION_MIN = 370;
    private static final String HEAD_PREFFIX = "_head";
    private static final String PET_CANNON = "pet_cannon";

    public State state;
    public Rectangle boundsRect;
    public float velocity;
    public boolean isCollision;
    public boolean isBiteDash;
    public boolean tappedback;

    public int outsideCounter;
    public int amountBugsBeforeCharging;
    public int totalEatenBugs;
    public int duringGameEatenBugs;

    public int eatenBugsCounter;

    public String petCannonName;
    public Entity petCannon;

    //    public String petName;
    public Entity petHead;

    public int stageCounter;

    public PetComponent() {
//        init();
        currencyType = HARD;
    }

    public PetComponent(SaveMngr.PetJson petJson) {
        currencyType = HARD;
        this.name = petJson.name;
        this.enabled = petJson.activated;
        this.bought = petJson.bought;
        this.cost = petJson.cost;
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
        this.logoName = petJson.logoName;
        this.discountTransactionId = petJson.discountTransactionId;
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
//        petCannon = GameStage.gameScript.gameItem.getChild(petCannonName).getEntity();
//        petHead = GameStage.gameScript.gameItem.getChild(name+"_head").getEntity();
        stageCounter = 0;
    }

    @Override
    public void apply() {
        this.enabled = true;
        GameStage.gameScript.hideCurrentPet();
        GameStage.gameScript.fpc.currentPet = this;
//        GameStage.gameScript.initPet();
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

    @Override
    public void buyHardDiscount() {
        Main.mainController.getBirdPetDiscount(this);
    }

    public void setOutsideStateDuration() {
        this.outsideCounter = new Random().nextInt(OUTSIDE_DURATION_MAX - OUTSIDE_DURATION_MIN) + OUTSIDE_DURATION_MIN;
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

    private void loadFromLib(String petName) {
        sceneLoader.rm.addSpriterToLoad(petName + HEAD_PREFFIX);
        sceneLoader.rm.addSpriterToLoad(PET_CANNON);

        CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary(petName + HEAD_PREFFIX);
        petHead = GameStage.sceneLoader.entityFactory.createSpriterEntity(GameStage.sceneLoader.getRoot(), tempItemC);
        GameStage.sceneLoader.getEngine().addEntity(petHead);

        CompositeItemVO tempItemCannon = GameStage.sceneLoader.loadVoFromLibrary(petCannonName);
        petCannon = GameStage.sceneLoader.entityFactory.createSpriterEntity(GameStage.sceneLoader.getRoot(), tempItemCannon);
        GameStage.sceneLoader.getEngine().addEntity(petCannon);
    }
}
