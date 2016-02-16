package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.mygdx.game.Main;
import com.mygdx.game.entity.componets.*;
import com.mygdx.game.system.*;
import com.mygdx.game.utils.CameraShaker;
import com.mygdx.game.utils.DailyGoalSystem;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.Random;

import static com.mygdx.game.stages.GameStage.sceneLoader;
import static com.mygdx.game.utils.EffectUtils.fade;
import static com.mygdx.game.utils.GlobalConstants.*;


public class GameScreenScript implements IScript {

    public static final String START_MESSAGE = "TAP TO START";
    public static final String DOUBLE_BJ_BADGE = "double_bj_badge";
    public static final CameraShaker cameraShaker = new CameraShaker();
    public static GameStage game;
    public static FlowerPublicComponent fpc;
    public static LabelComponent scoreLabelComponent;
    public static LabelComponent startLabelComponent;
    public static boolean isPause;
    public static boolean isGameOver;
    public static boolean isStarted;
    public static int gameOverCounter = 240;
    public static Entity background;
    public static Entity gameOverDialog;
    private static ItemWrapper gameItem;
    public Random random = new Random();
    public int dandelionSpawnCounter;
    public int cocoonSpawnCounter;
    public DailyGoalSystem dailyGoalGenerator;
    private Entity pauseDialog;

    public GameScreenScript(GameStage game) {
        GameScreenScript.game = game;
    }

    public static void showGameOver() {
        isGameOver = true;

        final TransformComponent dialogTc = gameOverDialog.getComponent(TransformComponent.class);
        dialogTc.x = 300;
        dialogTc.y = 100;

        Entity gameOverTimerLbl = gameItem.getChild("game_over_dialog").getChild("label_timer_gameover").getEntity();
        LabelComponent gameOverLblC = gameOverTimerLbl.getComponent(LabelComponent.class);
        gameOverLblC.text.replace(0, gameOverLblC.text.capacity(), "5");

        TintComponent tc = gameOverTimerLbl.getComponent(TintComponent.class);
        tc.color.a = 0;
    }

    public static void angerBees() {
        BugSpawnSystem.isAngeredBeesMode = true;
        GameScreenScript.cameraShaker.initShaking(7f, 0.9f);
        BugSpawnSystem.queenBeeOnStage = false;
    }

    public static void reloadScoreLabel(FlowerPublicComponent fcc) {
        scoreLabelComponent.text.replace(0, scoreLabelComponent.text.capacity(), "" + fcc.score + "/" + fcc.totalScore);
    }

    @Override
    public void init(Entity item) {

        ResultScreenScript.showCaseVanity = null;

        gameItem = new ItemWrapper(item);
        dandelionSpawnCounter = random.nextInt(DANDELION_SPAWN_CHANCE_MAX - DANDELION_SPAWN_CHANCE_MIN) + DANDELION_SPAWN_CHANCE_MIN;
        cocoonSpawnCounter = random.nextInt(COCOON_SPAWN_MAX - COCOON_SPAWN_MIN) + COCOON_SPAWN_MIN;

//        GameStage.sceneLoader.addComponentsByTagName("button", ButtonComponent.class);

        Entity scoreLabel = gameItem.getChild("lbl_score").getEntity();
        scoreLabelComponent = scoreLabel.getComponent(LabelComponent.class);
        scoreLabelComponent.text.replace(0, scoreLabelComponent.text.capacity(), "0");

        Entity startLabel = gameItem.getChild("lbl_tap2start").getEntity();
        startLabelComponent = startLabel.getComponent(LabelComponent.class);
        startLabelComponent.text.replace(0, startLabelComponent.text.capacity(), START_MESSAGE);

        addSystems();
        initFlower();
        initPauseBtn();
        initBackButton();
        initPauseDialog();
        initGameOverDialog();
        initBackground();
        initPet();
        initDoubleBJbadge();
    }

    private void initDoubleBJbadge (){
        if (fpc.doubleJuice){
            Entity badge = gameItem.getChild(DOUBLE_BJ_BADGE).getEntity();
            TransformComponent tc = badge.getComponent(TransformComponent.class);
            tc.scaleX = 0.6f;
            tc.scaleY = 0.6f;
            tc.x = 953;
            tc.y = 647;
        }
    }

    private void initBackground() {
        final CompositeItemVO tempC = sceneLoader.loadVoFromLibrary("backgroundLib");
        background = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), background, tempC.composite);
        sceneLoader.getEngine().addEntity(background);

        LayerMapComponent lc = ComponentRetriever.get(background, LayerMapComponent.class);
        lc.setLayers(tempC.composite.layers);
        background.add(lc);
        lc.getLayer(CameraShaker.BLINK).isVisible = false;
        TransformComponent tc = new TransformComponent();
        tc.x = 0;
        tc.y = 0;
        background.add(tc);
    }

    private void addSystems() {
        sceneLoader.getEngine().addSystem(new DandelionSystem(fpc));
        sceneLoader.getEngine().addSystem(new UmbrellaSystem());
        sceneLoader.getEngine().addSystem(new FlowerSystem());
        sceneLoader.getEngine().addSystem(new CocoonSystem(sceneLoader));
        sceneLoader.getEngine().addSystem(new BugSpawnSystem(fpc));
        sceneLoader.getEngine().addSystem(new ButterflySystem());
        sceneLoader.getEngine().addSystem(new BugSystem());
        sceneLoader.getEngine().addSystem(new BugJuiceBubbleSystem());
        sceneLoader.getEngine().addSystem(new ParticleLifespanSystem());
        sceneLoader.getEngine().addSystem(new PetSystem());

    }

    private void initBackButton() {

        Entity backBtn = gameItem.getChild("btn_back").getEntity();
        final LayerMapComponent lc = ComponentRetriever.get(backBtn, LayerMapComponent.class);

        TransformComponent backBtnTc = backBtn.getComponent(TransformComponent.class);
        backBtnTc.scaleX = 0.7f;
        backBtnTc.scaleY = 0.7f;
        backBtnTc.x = 90;
        backBtnTc.y = 680;


        backBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
                lc.getLayer("normal").isVisible = true;
                lc.getLayer("pressed").isVisible = false;
            }

            @Override
            public void touchDown() {
                lc.getLayer("normal").isVisible = false;
                lc.getLayer("pressed").isVisible = true;
            }

            @Override
            public void clicked() {
                game.initMenu();
            }
        });
    }

    private void initPauseBtn() {
        final Entity pauseBtn = gameItem.getChild("btn_pause").getEntity();

        TransformComponent pauseBtnTc = pauseBtn.getComponent(TransformComponent.class);
        pauseBtnTc.scaleX = 0.7f;
        pauseBtnTc.scaleY = 0.7f;

        pauseBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            LayerMapComponent lc = ComponentRetriever.get(pauseBtn, LayerMapComponent.class);

            @Override
            public void touchUp() {
                lc.getLayer("normal").isVisible = true;
                lc.getLayer("pressed").isVisible = false;
            }

            @Override
            public void touchDown() {
                lc.getLayer("normal").isVisible = false;
                lc.getLayer("pressed").isVisible = true;
            }

            @Override
            public void clicked() {
                pause();
            }
        });
    }

    private void initPauseDialog() {
        pauseDialog = gameItem.getChild("dialog").getEntity();
        Entity closePauseBtn = gameItem.getChild("dialog").getChild("btn_close").getEntity();
        closePauseBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
            }

            @Override
            public void touchDown() {
            }

            @Override
            public void clicked() {
                isPause = false;
            }
        });

        final TransformComponent dialogTc = pauseDialog.getComponent(TransformComponent.class);
        dialogTc.x = -3000;
        dialogTc.y = -1000;
    }

    private void initGameOverDialog() {
        gameOverDialog = gameItem.getChild("game_over_dialog").getEntity();
        final TransformComponent dialogTc = gameOverDialog.getComponent(TransformComponent.class);
        dialogTc.x = -3000;
        dialogTc.y = -1000;

        final Entity watchAdBtn = gameItem.getChild("game_over_dialog").getChild("btn_watch_video").getEntity();
        final Entity turnOnWifi = gameItem.getChild("game_over_dialog").getChild("lbl_turn_on_wifi").getEntity();
        turnOnWifi.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;

        watchAdBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
            }

            @Override
            public void touchDown() {
            }

            @Override
            public void clicked() {
                if (Main.adsController.isWifiConnected()) {
                    playVideoAd(dialogTc);
                } else {
                    turnOnWifi.getComponent(TransformComponent.class).x = 127;
                    turnOnWifi.getComponent(TransformComponent.class).y = 45;
                    isGameOver = false;
                    dialogTc.x = -1000;
                    gameOverCounter = 240;
                    BugSpawnSystem.isAngeredBeesMode = false;
                    fpc.currentPet.init();
                }
            }
        });
    }

    private void playVideoAd(final TransformComponent dialogTc) {
        if (Main.adsController.isWifiConnected()) {
            Main.adsController.showInterstitialVideoAd(new Runnable() {
                @Override
                public void run() {
                    isGameOver = false;
                    dialogTc.x = -1000;
                    gameOverCounter = 240;
                    fpc.currentPet.init();
                    BugSpawnSystem.isAngeredBeesMode = false;
                }
            });
        } else {
            System.out.println("Interstitial ad not (yet) loaded");
        }
    }

    private void pause() {
        final TransformComponent dialogTc = pauseDialog.getComponent(TransformComponent.class);
        dialogTc.x = 300;
        dialogTc.y = 100;

        final Entity goalLabel = gameItem.getChild("dialog").getChild("lbl_dialog").getEntity();
        LabelComponent goalsLabelComp = goalLabel.getComponent(LabelComponent.class);

        StringBuilder goalsList = new StringBuilder();
        for (DailyGoal g : fpc.goals) {
            String achieved = g.achieved ? " achieved " : " not achieved ";
            goalsList.append(" \n  - ").append(g.description).append(" - ").append(achieved);
        }
        goalsLabelComp.text.replace(0, goalsLabelComp.text.capacity(), "GOALS FOR TODAY!" + goalsList);
        isPause = true;
    }

    public void updateGameOver() {

        fade(gameOverDialog, isGameOver);
        if (isGameOver) {
            final Entity gameOverTimerLbl = gameItem.getChild("game_over_dialog").getChild("label_timer_gameover").getEntity();
            final LabelComponent gameOverLblC = gameOverTimerLbl.getComponent(LabelComponent.class);

            final ActionComponent ac = new ActionComponent();
            Actions.checkInit();
            ac.dataArray.add(Actions.scaleTo(99, 99, 48, Interpolation.elastic));
            gameOverTimerLbl.add(ac);

            gameOverCounter--;
            if (gameOverCounter % 48 == 0) {
                gameOverLblC.text.replace(0, gameOverLblC.text.capacity(), String.valueOf(gameOverCounter / 48));
            }

            if (gameOverCounter <= 0) {
                finishGame();
            }
        }
    }

    private void finishGame() {
        gameOverCounter = 240;
        isGameOver = false;
        isStarted = false;
        isPause = false;
        BugSpawnSystem.isAngeredBeesMode = false;
        BugSpawnSystem.queenBeeOnStage = false;
        if (fpc.bestScore < fpc.score) {
            fpc.bestScore = fpc.score;
        }
        game.initResult();
    }

    private void initFlower() {
        fpc.score = 0;
        Entity flowerEntity = gameItem.getChild("mega_flower").getEntity();
        TransformComponent tc = flowerEntity.getComponent(TransformComponent.class);
        tc.x = 988;
        tc.y = 105;
        tc.scaleX = BUG_SCALE;
        tc.scaleY = BUG_SCALE;
        flowerEntity.add(tc);

        FlowerComponent fc = new FlowerComponent();
        dailyGoalGenerator = new DailyGoalSystem();
        if (fpc.goals == null || fpc.goals.isEmpty()) {
            fpc.goals = dailyGoalGenerator.getGoalsForToday();
        }
        flowerEntity.add(fc);
        flowerEntity.add(fpc);
    }

    public void initPet (){
        if (fpc.currentPet != null
                && fpc. currentPet.bought
                && fpc.currentPet.enabled) {
            Entity pet = gameItem.getChild(fpc.currentPet.name).getEntity();
            TransformComponent tc = pet.getComponent(TransformComponent.class);
            tc.x = 1200;
            tc.y = 455;
            tc.scaleX = 1.3f;
            tc.scaleY = 1.3f;

            fpc.currentPet.init();

            pet.add(fpc.currentPet);
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    public void act(float delta) {

        if (cameraShaker.time > 0) {
            cameraShaker.shake(delta);
            cameraShaker.blink();
        }

        if (!isStarted && Gdx.input.justTouched()) {
            startLabelComponent.text.replace(0, startLabelComponent.text.capacity(), "");
            isStarted = true;
        }

        if (!isPause && !isGameOver && isStarted) {
            if (canDandelionSpawn()) {
                dandelionSpawnCounter--;
            }
            if (canCocoonSpawn()) {
                cocoonSpawnCounter--;
            }
            //Spawn dandelion
            if (dandelionSpawnCounter <= 0) {
                spawnDandelion();
            }
            //spawn Cocoon
            if (cocoonSpawnCounter <= 0) {
                spawnCocoon();
            }
        }


        updateGameOver();
        fade(pauseDialog, isPause);
    }

    private void spawnDandelion() {
        if (canDandelionSpawn()) {
            dandelionSpawnCounter =
                    random.nextInt(DANDELION_SPAWN_CHANCE_MAX - DANDELION_SPAWN_CHANCE_MIN) + DANDELION_SPAWN_CHANCE_MIN;

            ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
            Entity dandelionEntity = root.getChild("dandelionAni").getEntity();

            TransformComponent tc = dandelionEntity.getComponent(TransformComponent.class);
            tc.x = 300;
            tc.y = 40;
            tc.scaleX = 0.7f;
            tc.scaleY = 0.7f;

            dandelionEntity.add(tc);
            DandelionComponent dc = new DandelionComponent();
            dc.state = DandelionComponent.State.GROWING;
            dandelionEntity.add(dc);
//            sceneLoader.getEngine().addEntity(dandelionEntity);
        }
    }

    private void spawnCocoon() {
        if (canCocoonSpawn()) {
            cocoonSpawnCounter = random.nextInt(COCOON_SPAWN_MAX - COCOON_SPAWN_MIN) + COCOON_SPAWN_MIN;

            ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
            Entity cocoonEntity = root.getChild("coccoon").getEntity();

            cocoonEntity.getComponent(SpriterComponent.class).scale = 0.3f;
            cocoonEntity.getComponent(SpriterComponent.class).player.setAnimation(0);

            TransformComponent tc = cocoonEntity.getComponent(TransformComponent.class);

            tc.x = 980;
            tc.y = 800;
            cocoonEntity.add(tc);

            cocoonEntity.add(fpc);
            CocoonComponent cc = new CocoonComponent();
            cocoonEntity.add(cc);
        }
    }

    private boolean canCocoonSpawn() {
        return sceneLoader.getEngine().getEntitiesFor(Family.all(CocoonComponent.class).get()) == null ||
                sceneLoader.getEngine().getEntitiesFor(Family.all(CocoonComponent.class).get()).size() == 0;
    }

    private boolean canDandelionSpawn() {
        return (sceneLoader.getEngine().getEntitiesFor(Family.all(DandelionComponent.class).get()) == null ||
                sceneLoader.getEngine().getEntitiesFor(Family.all(DandelionComponent.class).get()).size() == 0) &&
                (sceneLoader.getEngine().getEntitiesFor(Family.all(UmbrellaComponent.class).get()) == null ||
                        sceneLoader.getEngine().getEntitiesFor(Family.all(UmbrellaComponent.class).get()).size() == 0);
    }
}
