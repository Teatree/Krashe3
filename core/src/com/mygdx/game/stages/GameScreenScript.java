//package com.mygdx.game.stages;
//
//import com.badlogic.ashley.core.Entity;
//import com.badlogic.gdx.Gdx;
//import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
//import com.uwsoft.editor.renderer.resources.IResourceRetriever;
//import com.uwsoft.editor.renderer.scripts.IScript;
//import com.uwsoft.editor.renderer.utils.ItemWrapper;
//
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.Random;
//
//import static game.utils.Animator.*;
//
///**
// * Created by MainUser on 26/07/2015.
// */
//public class GameScreenScript implements IScript {
//
//    public static boolean GAME_OVER = false;
//    public static boolean GAME_PAUSED = false;
//
//    private GameStage stage;
//    private Entity gameItem;
//    public IResourceRetriever ir;
//
//    public Flower flower;
//    public static List<Bug> bugs = new LinkedList<>();
//    private int spawnInterval = 200;
//    public Random random = new Random();
//
//    private int timer;
//    final MrSpawner spawner = new MrSpawner();
//    public BugGenerator bugGenerator;
//    public static boolean isAngeredBeesMode = false;
//    public static int angeredBeesTimer = 0;
//    public DandelionPowerUp dandelionPowerup;
//    public UmbrellaPowerUp umbrellaPowerUp;
//    public CocoonPowerUp cocoonPowerUp;
//    public ButterflyPowerUp butterflyPowerUp;
//    public int dandelionSpawnCounter;
//    public int cacoonSpawnCounter;
//    public ItemWrapper itemWrapper;
//
//    private Animator uiController = new Animator();
//
//    public GameScreenScript(GameStage stage) {
//        this.stage = stage;
//    }
//
//    @Override
//    public void init(Entity item) {
//        gameItem = item;
//        ir = stage.sceneLoader.getRm();
//
//        itemWrapper = new ItemWrapper(item);
//
//        dandelionSpawnCounter = random.nextInt(GlobalConstants.DANDELION_SPAWN_CHANCE_MAX - GlobalConstants.DANDELION_SPAWN_CHANCE_MIN) + GlobalConstants.DANDELION_SPAWN_CHANCE_MIN;
//        cacoonSpawnCounter = random.nextInt(GlobalConstants.COCOON_SPAWN_MAX - GlobalConstants.COCOON_SPAWN_MIN) + GlobalConstants.COCOON_SPAWN_MIN;
//
//        initPauseButton();
//
//        uiController.init(this);
//        bugGenerator = new BugGenerator(stage.sceneLoader);
//    }
//
//    public void initPauseButton() {
//        final Entity btnPause = itemWrapper.getChild("btn_pause").getEntity();
//
//        btnPause.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
//            @Override
//            public void touchUp() {
//                uiController.pouseGame();
//            }
//
//            @Override
//            public void touchDown() {
//                uiController.showPausePopup();
//            }
//
//            @Override
//            public void clicked() {
//
//            }
//            // Need to keep touch down in order for touch up to work normal (libGDX awkwardness)
////            public boolean touchDown() {
////                uiController.showPausePopup();
////
////                return true;
////            }
////
////            public void touchUp(InputEvent event, float x, float y, int pointer,
////                                int button) {
////                uiController.pouseGame();
////            }
//        });
//    }
//
//    @Override
//    public void dispose() {
////        SaveManager.saveProperties();
//    }
//
//    @Override
//    public void act(float delta) {
//        uiController.update();
//
//        for (Bug bug : bugs) {
//            if (bug.getController().isOutOfBounds() && flower.getCurHp() <= 0) {
//                GAME_PAUSED = true;
//            }
//        }
//
//        if (flower.getCurHp() <= 0) {
//            uiController.showGameOverPopup();
//        }
//
//        if (flower.getCurHp() <= 0) {
//            uiController.playGameOverTimer();
//        }
//
//
//        if (isGameAlive() && GlobalConstants.CUR_SCREEN == "GAME") {
//            timer++;
//            dandelionSpawnCounter--;
//            cacoonSpawnCounter--;
//
//            if (timer >= spawnInterval) {
//                bugs.add(spawner.spawn(bugGenerator.getBugSafe(stage), stage.getInstance()));
//                timer = 0;
//            }
//            if (Gdx.input.isTouched() && isGameOver()) {
//                stage.getActors().removeRange(2, stage.getActors().size - 1);
//                reloadBugs();
//                isAngeredBeesMode = false;
//                Flower.pointsAmount += Flower.sessionPointsAmount;
//            }
//            if (isAngeredBeesMode) {
//                isAngeredBeesMode = angeredBeesTimer-- >= 0;
//                spawnInterval = isAngeredBeesMode ? GlobalConstants.BEE_SPAWN_INTERVAL_ANGERED : GlobalConstants.BEE_SPAWN_INTERVAL_REGULAR;
//            }
//
//            if (dandelionSpawnCounter <= 0 && umbrellaPowerUp == null) {
//                dandelionSpawnCounter = random.nextInt(GlobalConstants.DANDELION_SPAWN_CHANCE_MAX - GlobalConstants.DANDELION_SPAWN_CHANCE_MIN) + GlobalConstants.DANDELION_SPAWN_CHANCE_MIN;
//                dandelionPowerup = new DandelionPowerUp(stage.sceneLoader, stage);
//            }
//
//            CollisionChecker.checkCollisions(stage);
//        }
//    }
//
//    private void reloadBugs() {
//        bugs = new ArrayList<>();
//    }
//
//    public boolean isGameOver() {
//        for (Bug bug : bugs) {
//            if (bug.getController().isOutOfBounds() && flower.getCurHp() <= 0) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public static boolean isGameAlive() {
//        return !GAME_PAUSED && !GAME_OVER;
//    }
//
//    public List<Bug> getBugs() {
//        return bugs;
//    }
//
//    public void setBugs(List<Bug> bugs) {
//        this.bugs = bugs;
//    }
//
//    public int getTimer() {
//        return timer;
//    }
//
//    public void setTimer(int timer) {
//        this.timer = timer;
//    }
//
//    public MrSpawner getSpawner() {
//        return spawner;
//    }
//
//
//    public Entity getGameItem() {
//        return gameItem;
//    }
//
//    public void setGameItem(Entity gameItem) {
//        this.gameItem = gameItem;
//    }
//
//    public GameStage getStage() {
//        return stage;
//    }
//
//    public void setStage(GameStage stage) {
//        this.stage = stage;
//    }
//
//    public Flower getFlower() {
//        return flower;
//    }
//
//    public void setFlower(Flower flower) {
//        this.flower = flower;
//    }
//
//
//}
