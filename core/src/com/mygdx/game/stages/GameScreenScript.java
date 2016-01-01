package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.mygdx.game.entity.componets.*;
import com.mygdx.game.system.*;
import com.mygdx.game.utils.CameraShaker;
import com.mygdx.game.utils.DailyGoalSystem;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.Random;

import static com.mygdx.game.utils.GlobalConstants.*;
import static com.mygdx.game.stages.GameStage.*;

/**
 * Created by Teatree on 7/25/2015.
 */
public class GameScreenScript implements IScript {

    public static final String START_MESSAGE = "TAP TO START";

    private static ItemWrapper gameItem;
    GameStage game;
    public Random random = new Random();

    public int dandelionSpawnCounter;
    public int cocoonSpawnCounter;

    //One flower collision component will be used in all systems
    public static FlowerPublicComponent fpc;
    public static LabelComponent scoreLabelComponent;
    public static LabelComponent startLabelComponent;
    public static boolean isPause;
    public static boolean isGameOver;
    public static boolean isStarted;

    public static int gameOverCounter = 240;

    public static CameraShaker cameraShaker = new CameraShaker();
    public static Entity background;

    public DailyGoalSystem dailyGoalGenerator;

    public GameScreenScript(GameStage game) {
        this.game = game;
    }

    @Override
    public void init(Entity item) {

        gameItem = new ItemWrapper(item);
        dandelionSpawnCounter = random.nextInt(DANDELION_SPAWN_CHANCE_MAX - DANDELION_SPAWN_CHANCE_MIN) + DANDELION_SPAWN_CHANCE_MIN;
        cocoonSpawnCounter = random.nextInt(COCOON_SPAWN_MAX - COCOON_SPAWN_MIN) + COCOON_SPAWN_MIN;

        GameStage.sceneLoader.addComponentsByTagName("button", ButtonComponent.class);

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

        final CompositeItemVO tempC = GameStage.sceneLoader.loadVoFromLibrary("backgroundLib");
        background = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempC);
        GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), background, tempC.composite);
        GameStage.sceneLoader.getEngine().addEntity(background);

        LayerMapComponent lc = ComponentRetriever.get(background, LayerMapComponent.class);
        lc.setLayers(tempC.composite.layers);
        background.add(lc);

        lc.getLayer("blink").isVisible = false;
        TransformComponent tc = new TransformComponent();
        tc.x = 0;
        tc.y = 0;
        background.add(tc);
    }

    private void addSystems() {
        GameStage.sceneLoader.getEngine().addSystem(new DandelionSystem(fpc));
        GameStage.sceneLoader.getEngine().addSystem(new UmbrellaSystem());
        GameStage.sceneLoader.getEngine().addSystem(new FlowerSystem());
        GameStage.sceneLoader.getEngine().addSystem(new CocoonSystem(sceneLoader));
        GameStage.sceneLoader.getEngine().addSystem(new BugSpawnSystem(fpc));
        GameStage.sceneLoader.getEngine().addSystem(new ButterflySystem());
        GameStage.sceneLoader.getEngine().addSystem(new BugSystem());
        GameStage.sceneLoader.getEngine().addSystem(new BugJuiceBubbleSystem());
    }

    private void initBackButton() {
        game.sceneLoader.addComponentsByTagName("button", ButtonComponent.class);
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

//                List<VanityComponent> vanityComponentList = SaveMngr.getAllVanity();
//                System.out.println(vanityComponentList.get(1).icon);
//                vanityComponentList.get(1).apply(GameScreenScript.fpc);
            }

            @Override
            public void clicked() {
//                game.initMenu();
                pause();
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

    private void pause() {
        final Entity dialog = gameItem.getChild("dialog").getEntity();
        final TransformComponent dialogTc = dialog.getComponent(TransformComponent.class);
        dialogTc.x = 300;
        dialogTc.y = 100;

        Entity goalLabel = gameItem.getChild("dialog").getChild("lbl_dialog").getEntity();
        LabelComponent dialogLabelComp = goalLabel.getComponent(LabelComponent.class);

        StringBuilder goalsList = new StringBuilder();
        for (DailyGoal g : fpc.goals){
            String achieved = g.achieved ? " achieved " : " not achieved ";
            goalsList.append(" \n  - " + g.description + " - " + achieved);
        }
        dialogLabelComp.text.replace(0, dialogLabelComp.text.capacity(), "GOALS FOR TODAY!" + goalsList );

        Entity closeDialogBtn = gameItem.getChild("dialog").getChild("btn_close").getEntity();
        closeDialogBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
            }

            @Override
            public void touchDown() {
            }

            @Override
            public void clicked() {
                isPause = false;
                dialogTc.x = -1000;
            }
        });

        isPause = true;
    }

    public void updateGameOver() {
        Entity gameOverTimerLbl = gameItem.getChild("game_over_dialog").getChild("label_timer_gameover").getEntity();
        LabelComponent gameOverLblC = gameOverTimerLbl.getComponent(LabelComponent.class);

        gameOverCounter--;
        if (gameOverCounter % 48 == 0) {
            gameOverLblC.text.replace(0, gameOverLblC.text.capacity(), String.valueOf(gameOverCounter / 48));
        }

        if (gameOverCounter <= 0) {
            gameOverCounter = 240;
            isGameOver = false;
            isStarted = false;
            isPause = false;
            BugSpawnSystem.isAngeredBeesMode = false;
            BugSpawnSystem.queenBeeOnStage = false;
            game.initMenu();
        }
    }

    public static void showGameOver() {
        isGameOver = true;

        final Entity gameOverDialog = gameItem.getChild("game_over_dialog").getEntity();
        final TransformComponent dialogTc = gameOverDialog.getComponent(TransformComponent.class);
        dialogTc.x = 300;
        dialogTc.y = 100;

        Entity gameOverTimerLbl = gameItem.getChild("game_over_dialog").getChild("label_timer_gameover").getEntity();
        LabelComponent gameOverLblC = gameOverTimerLbl.getComponent(LabelComponent.class);
        gameOverLblC.text.replace(0, gameOverLblC.text.capacity(), "5");

        Entity watchAdBtn = gameItem.getChild("game_over_dialog").getChild("btn_watch_video").getEntity();
        watchAdBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
            }

            @Override
            public void touchDown() {
            }

            @Override
            public void clicked() {
                isGameOver = false;
                dialogTc.x = -1000;
                gameOverCounter = 240;
            }
        });
    }

    private void initFlower() {
        Entity flowerEntity = gameItem.getChild("mega_flower").getEntity();
        TransformComponent tc = flowerEntity.getComponent(TransformComponent.class);
        tc.x = 988;
        tc.y = 105;
        tc.scaleX = BUG_SCALE;
        tc.scaleY = BUG_SCALE;
        flowerEntity.add(tc);

        FlowerComponent fc = new FlowerComponent();
        dailyGoalGenerator = new DailyGoalSystem();
        fpc.goals = dailyGoalGenerator.getGoalsForToday();
        flowerEntity.add(fc);
        flowerEntity.add(fpc);
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

        if (isGameOver) {
            updateGameOver();
        }
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

            CompositeItemVO cocoonComposite = sceneLoader.loadVoFromLibrary("drunkbugLib");
            Entity cocoonEntity = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), cocoonComposite);
            sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), cocoonEntity, cocoonComposite.composite);

            TransformComponent tc = cocoonEntity.getComponent(TransformComponent.class);
            tc.x = 850;
            tc.y = 710;
            cocoonEntity.add(tc);

            cocoonEntity.add(fpc);
            CocoonComponent cc = new CocoonComponent();
            cocoonEntity.add(cc);
            sceneLoader.getEngine().addEntity(cocoonEntity);
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

    public static void angerBees() {
        BugSpawnSystem.isAngeredBeesMode = true;
        GameScreenScript.cameraShaker.initShaking(7f, 0.9f);
        BugSpawnSystem.queenBeeOnStage = false;
    }

    public static void reloadScoreLabel(FlowerPublicComponent fcc) {
        scoreLabelComponent.text.replace(0, scoreLabelComponent.text.capacity(), "" + fcc.score + "/" + fcc.totalScore);
    }
}
