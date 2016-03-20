package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.mygdx.game.entity.componets.*;
import com.mygdx.game.stages.ui.GameOverDialog;
import com.mygdx.game.stages.ui.GiftScreen;
import com.mygdx.game.stages.ui.GoalFeedbackScreen;
import com.mygdx.game.stages.ui.PauseDialog;
import com.mygdx.game.system.*;
import com.mygdx.game.utils.CameraShaker;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.Random;

import static com.mygdx.game.entity.componets.FlowerComponent.*;
import static com.mygdx.game.stages.GameStage.sceneLoader;
import static com.mygdx.game.stages.ShopScreenScript.allShopItems;
import static com.mygdx.game.utils.GlobalConstants.*;

public class GameScreenScript implements IScript {

    public static final String START_MESSAGE = "TAP TO START";
    public static final String DOUBLE_BJ_BADGE = "double_bj_badge";

    public static final CameraShaker cameraShaker = new CameraShaker();
    public static final String LBL_SCORE = "lbl_score";
    public static final String LBL_TAP_2_START = "lbl_tap2start";
    public static final String BTN_PAUSE = "btn_pause";
    public static final String MEGA_FLOWER = "mega_flower";
    public static final String DANDELION_ANI = "dandelionAni";
    public static final String COCCOON = "coccoon";
    public static final String BACKGROUND_LIB = "backgroundLib";
    public static final String BTN_BACK = "btn_back";

    public static GameStage game;
    public static FlowerPublicComponent fpc;

    public static LabelComponent scoreLabelComponent;
    public static LabelComponent startLabelComponent;

    public static boolean isPause;
    public static boolean isGameOver;
    public static boolean isStarted;

    public static Entity background;
    public static GiftScreen giftScreen;
    public static GoalFeedbackScreen goalFeedbackScreen;
    private static GameOverDialog gameOverDialog;
    public Random random = new Random();
    public int dandelionSpawnCounter;
    public int cocoonSpawnCounter;
    private ItemWrapper gameItem;
    private PauseDialog pauseDialog;

    public GameScreenScript(GameStage game) {
        GameScreenScript.game = game;
    }

    public static void onBugOutOfBounds() {
        if (fpc.canUsePhoenix()) {
            usePhoenix();
        } else {
            GameOverDialog.show();
        }
    }

    public static void angerBees() {
        BugSpawnSystem.isAngeredBeesMode = true;
        GameScreenScript.cameraShaker.initShaking(7f, 0.9f);
        BugSpawnSystem.queenBeeOnStage = false;
    }

    public static void reloadScoreLabel(FlowerPublicComponent fcc) {
        scoreLabelComponent.text.replace(0, scoreLabelComponent.text.capacity(), "" + fcc.score + "/" + fcc.totalScore);
    }

    public static void checkTryPeriod() {
        long now = System.currentTimeMillis();
        if (fpc.currentPet != null && fpc.currentPet.tryPeriod) {
            if (now - fpc.currentPet.tryPeriodStart >= fpc.currentPet.tryPeriodDuration * 1000) {
                fpc.currentPet.enabled = false;
                fpc.currentPet.bought = false;
                fpc.currentPet.tryPeriod = false;

                if (allShopItems.indexOf(fpc.currentPet) >= 0) {
                    allShopItems.get(allShopItems.indexOf(fpc.currentPet)).bought = false;
                    allShopItems.get(allShopItems.indexOf(fpc.currentPet)).enabled = false;
                }
            }
        }
        if (fpc.upgrades != null && !fpc.upgrades.isEmpty()) {
            for (Upgrade u: fpc.upgrades.values()) {
                if (u.tryPeriod && now - u.tryPeriodStart >= u.tryPeriodDuration * 1000) {
                    u.enabled = false;
                    u.bought = false;
                    u.tryPeriod = false;

                    if (allShopItems.indexOf(u) >= 0) {
                        allShopItems.get(allShopItems.indexOf(u)).bought = false;
                        allShopItems.get(allShopItems.indexOf(u)).enabled = false;
                    }
                }
            }
        }
    }

    public static void usePhoenix() {
        fpc.upgrades.get(Upgrade.UpgradeType.PHOENIX).usePhoenix(fpc);
//        continueGame(gameOverDialog.getComponent(TransformComponent.class));
    }

    @Override
    public void init(Entity item) {

        ResultScreenScript.showCaseVanity = null;

        gameItem = new ItemWrapper(item);
        dandelionSpawnCounter = random.nextInt(DANDELION_SPAWN_CHANCE_MAX - DANDELION_SPAWN_CHANCE_MIN) + DANDELION_SPAWN_CHANCE_MIN;
        cocoonSpawnCounter = random.nextInt(COCOON_SPAWN_MAX - COCOON_SPAWN_MIN) + COCOON_SPAWN_MIN;

        Entity scoreLabel = gameItem.getChild(LBL_SCORE).getEntity();
        scoreLabelComponent = scoreLabel.getComponent(LabelComponent.class);
        scoreLabelComponent.text.replace(0, scoreLabelComponent.text.capacity(), "0");

        Entity startLabel = gameItem.getChild(LBL_TAP_2_START).getEntity();
        startLabelComponent = startLabel.getComponent(LabelComponent.class);
        startLabelComponent.text.replace(0, startLabelComponent.text.capacity(), START_MESSAGE);

        addSystems();
        initFlower();
        initPauseBtn();
        initBackButton();
        initBackground();
        initPet();
        initDoubleBJbadge();

            gameOverDialog = new GameOverDialog(gameItem);
        gameOverDialog.initGameOverDialog();

        pauseDialog = new PauseDialog(gameItem);

        fpc.level.updateLevel();
        pauseDialog.init();

        giftScreen = new GiftScreen(gameItem);
        giftScreen.init();

        goalFeedbackScreen = new GoalFeedbackScreen(gameItem);
        goalFeedbackScreen.init();

        checkTryPeriod();
    }

    public void reset() {
        init(gameItem.getEntity());
    }

    private void initDoubleBJbadge() {
        if (fpc.haveBugJuiceDouble()) {
            Entity badge = gameItem.getChild(DOUBLE_BJ_BADGE).getEntity();
            TransformComponent tc = badge.getComponent(TransformComponent.class);
            tc.scaleX = 0.6f;
            tc.scaleY = 0.6f;
            tc.x = 953;
            tc.y = 647;
        }
    }

    private void initBackground() {
        final CompositeItemVO tempC = sceneLoader.loadVoFromLibrary(BACKGROUND_LIB);
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
        sceneLoader.getEngine().addSystem(new DandelionSystem(gameItem));
        sceneLoader.getEngine().addSystem(new UmbrellaSystem());
        sceneLoader.getEngine().addSystem(new FlowerSystem());
        sceneLoader.getEngine().addSystem(new CocoonSystem(gameItem));
        sceneLoader.getEngine().addSystem(new BugSpawnSystem(fpc));
        sceneLoader.getEngine().addSystem(new ButterflySystem());
        sceneLoader.getEngine().addSystem(new BugSystem());
        sceneLoader.getEngine().addSystem(new BugJuiceBubbleSystem());
        sceneLoader.getEngine().addSystem(new ParticleLifespanSystem());
        sceneLoader.getEngine().addSystem(new PetSystem());

    }

    private void initBackButton() {

        Entity backBtn = gameItem.getChild(BTN_BACK).getEntity();
        final LayerMapComponent lc = ComponentRetriever.get(backBtn, LayerMapComponent.class);

        TransformComponent backBtnTc = backBtn.getComponent(TransformComponent.class);
        backBtnTc.scaleX = 0.7f;
        backBtnTc.scaleY = 0.7f;
        backBtnTc.x = 90;
        backBtnTc.y = 680;


        backBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
                lc.getLayer(BTN_NORMAL).isVisible = true;
                lc.getLayer(BTN_PRESSED).isVisible = false;
            }

            @Override
            public void touchDown() {
                lc.getLayer(BTN_NORMAL).isVisible = false;
                lc.getLayer(BTN_PRESSED).isVisible = true;
            }

            @Override
            public void clicked() {
                game.initMenu();
            }
        });
    }

    private void initPauseBtn() {
        final Entity pauseBtn = gameItem.getChild(BTN_PAUSE).getEntity();

        TransformComponent pauseBtnTc = pauseBtn.getComponent(TransformComponent.class);
        pauseBtnTc.scaleX = 0.7f;
        pauseBtnTc.scaleY = 0.7f;

        pauseBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            LayerMapComponent lc = ComponentRetriever.get(pauseBtn, LayerMapComponent.class);

            @Override
            public void touchUp() {
                lc.getLayer(BTN_NORMAL).isVisible = true;
                lc.getLayer(BTN_PRESSED).isVisible = false;
            }

            @Override
            public void touchDown() {
                lc.getLayer(BTN_NORMAL).isVisible = false;
                lc.getLayer(BTN_PRESSED).isVisible = true;
            }

            @Override
            public void clicked() {
                pauseDialog.pause();
            }
        });
    }

    private void initFlower() {
        fpc.score = 0;
        Entity flowerEntity = gameItem.getChild(MEGA_FLOWER).getEntity();
        TransformComponent tc = flowerEntity.getComponent(TransformComponent.class);
        tc.x = FLOWER_X_POS;
        tc.y = FLOWER_Y_POS;
        tc.scaleX = FLOWER_SCALE;
        tc.scaleY = FLOWER_SCALE;
        flowerEntity.add(tc);

        FlowerComponent fc = new FlowerComponent();

//        if (fpc.level.getGoals() == null || fpc.level.getGoals().isEmpty()) {
////            fpc.level.updateLevel();
//        }
        flowerEntity.add(fc);
        flowerEntity.add(fpc);
    }

    public void initPet() {
        if (fpc.currentPet != null
                && fpc.currentPet.bought
                && fpc.currentPet.enabled) {
            Entity pet = gameItem.getChild(fpc.currentPet.name).getEntity();
            TransformComponent tc = pet.getComponent(TransformComponent.class);
            tc.x = PetComponent.X_SPAWN_POSITION;
            tc.y = PetComponent.getNewPositionY();
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

            updateTapGoal();
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

        gameOverDialog.update(delta);
        pauseDialog.update();
        giftScreen.update();
        goalFeedbackScreen.update();
    }

    private void updateTapGoal() {
        if (fpc.level.getGoalByType(Goal.GoalType.TAP) != null) {
            fpc.level.getGoalByType(Goal.GoalType.TAP).update();
        }
    }

    private void spawnDandelion() {
        if (canDandelionSpawn()) {
            dandelionSpawnCounter =
                    random.nextInt(DANDELION_SPAWN_CHANCE_MAX - DANDELION_SPAWN_CHANCE_MIN) + DANDELION_SPAWN_CHANCE_MIN;

            ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
            Entity dandelionEntity = root.getChild(DANDELION_ANI).getEntity();

            TransformComponent tc = dandelionEntity.getComponent(TransformComponent.class);
            tc.x = 300;
            tc.y = 40;
            tc.scaleX = 0.7f;
            tc.scaleY = 0.7f;

            dandelionEntity.add(tc);
            DandelionComponent dc = new DandelionComponent();
            dc.state = DandelionComponent.State.GROWING;
            dandelionEntity.add(dc);
        }
    }

    private void spawnCocoon() {
        if (canCocoonSpawn()) {
            cocoonSpawnCounter = random.nextInt(COCOON_SPAWN_MAX - COCOON_SPAWN_MIN) + COCOON_SPAWN_MIN;

            ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
            Entity cocoonEntity = root.getChild(COCCOON).getEntity();

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
        return
                sceneLoader.getEngine().getEntitiesFor(Family.all(CocoonComponent.class).get()).size() == 0 ||
                        sceneLoader.getEngine().getEntitiesFor(Family.all(CocoonComponent.class).get())
                                .get(0).getComponent(TransformComponent.class).x == FAR_FAR_AWAY_X ||
                        sceneLoader.getEngine().getEntitiesFor(Family.all(CocoonComponent.class).get())
                                .get(0).getComponent(TransformComponent.class).x <= 0 ||
                        sceneLoader.getEngine().getEntitiesFor(Family.all(CocoonComponent.class).get())
                                .get(0).getComponent(TransformComponent.class).y <= 0;
    }

    private boolean canDandelionSpawn() {

        return (sceneLoader.getEngine().getEntitiesFor(Family.all(DandelionComponent.class).get()).size() == 0 ||
                sceneLoader.getEngine().getEntitiesFor(Family.all(DandelionComponent.class).get())
                        .get(0).getComponent(TransformComponent.class).x == FAR_FAR_AWAY_X ||
                sceneLoader.getEngine().getEntitiesFor(Family.all(DandelionComponent.class).get())
                        .get(0).getComponent(TransformComponent.class).x <= 0 ||
                sceneLoader.getEngine().getEntitiesFor(Family.all(DandelionComponent.class).get())
                        .get(0).getComponent(TransformComponent.class).y <= 0) &&
                (sceneLoader.getEngine().getEntitiesFor(Family.all(UmbrellaComponent.class).get()).size() == 0 ||
                        sceneLoader.getEngine().getEntitiesFor(Family.all(UmbrellaComponent.class).get())
                                .get(0).getComponent(TransformComponent.class).x == FAR_FAR_AWAY_X ||
                        sceneLoader.getEngine().getEntitiesFor(Family.all(UmbrellaComponent.class).get())
                                .get(0).getComponent(TransformComponent.class).x <= 0 ||
                        sceneLoader.getEngine().getEntitiesFor(Family.all(UmbrellaComponent.class).get())
                                .get(0).getComponent(TransformComponent.class).y == 0
                );
    }
}
