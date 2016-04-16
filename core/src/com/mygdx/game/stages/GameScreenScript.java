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
import com.uwsoft.editor.renderer.components.TintComponent;
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
import static com.mygdx.game.stages.GameStage.gameScript;
import static com.mygdx.game.stages.ShopScreenScript.allShopItems;
import static com.mygdx.game.utils.GlobalConstants.*;

public class GameScreenScript implements IScript {

    public static final CameraShaker cameraShaker = new CameraShaker();
    public static final String TUTORIAL_LINE = "tutorial_line";
    public static boolean isPause;
    public static boolean isGameOver;
    public static boolean isStarted;
    public final String START_MESSAGE = "TAP TO START";
    public final String DOUBLE_BJ_ICON = "double_bj_badge";
    public final String LBL_SCORE = "lbl_score";
    public final String LBL_TAP_2_START = "lbl_tap2start";
    public final String BTN_PAUSE = "btn_pause";
    public final String MEGA_FLOWER = "mega_flower";
    public final String DANDELION_ANI = "dandelionAni";
    public final String COCCOON = "coccoon";
    public final String BACKGROUND_LIB = "backgroundLib";
    public final String BTN_BACK = "btn_back";

    public GameStage stage;
    public Random random = new Random();
    public FlowerPublicComponent fpc;
    public LabelComponent scoreLabelComponent;
    public LabelComponent startLabelComponent;
    public Entity background;
    public GiftScreen giftScreen;
    public GoalFeedbackScreen goalFeedbackScreen;
    public int dandelionSpawnCounter;
    public int cocoonSpawnCounter;
    public ItemWrapper gameItem;
    private GameOverDialog gameOverDialog;
    private PauseDialog pauseDialog;
    public Entity pauseBtn;

    public GameScreenScript(GameStage gamestage) {
        this.stage = gamestage;
    }

    public static void angerBees() {
        BugSpawnSystem.isAngeredBeesMode = true;
        GameScreenScript.cameraShaker.initShaking(7f, 0.9f);
        BugSpawnSystem.queenBeeOnStage = false;
    }

    public static void checkTryPeriod() {
        long now = System.currentTimeMillis();
        if (gameScript.fpc.currentPet != null && gameScript.fpc.currentPet.tryPeriod) {
            if (now - gameScript.fpc.currentPet.tryPeriodStart >= gameScript.fpc.currentPet.tryPeriodDuration * 1000) {
                gameScript.fpc.currentPet.enabled = false;
                gameScript.fpc.currentPet.bought = false;
                gameScript.fpc.currentPet.tryPeriod = false;
                gameScript.fpc.currentPet.disable(gameScript.fpc);

                if (allShopItems.indexOf(gameScript.fpc.currentPet) >= 0) {
                    allShopItems.get(allShopItems.indexOf(gameScript.fpc.currentPet)).bought = false;
                    allShopItems.get(allShopItems.indexOf(gameScript.fpc.currentPet)).enabled = false;
                }
            }
        }
        if (gameScript.fpc.upgrades != null && !gameScript.fpc.upgrades.isEmpty()) {
            for (Upgrade u : gameScript.fpc.upgrades.values()) {
                if (u.tryPeriod && now - u.tryPeriodStart >= u.tryPeriodDuration * 1000) {
                    u.enabled = false;
                    u.bought = false;
                    u.tryPeriod = false;
                    u.disable(gameScript.fpc);

                    if (allShopItems.indexOf(u) >= 0) {
                        allShopItems.get(allShopItems.indexOf(u)).bought = false;
                        allShopItems.get(allShopItems.indexOf(u)).enabled = false;
                    }
                }
            }
        }
    }

    public static void usePhoenix() {
        gameScript.fpc.upgrades.get(Upgrade.UpgradeType.PHOENIX).usePhoenix(gameScript.fpc);
    }

    @Override
    public void init(Entity item) {
//
//        System.err.print("init game ");
//        System.err.println(Gdx.app.getJavaHeap() / 1000000 + " : " +
//                Gdx.app.getNativeHeap());

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
        initBackground();
        initPet();
        initDoubleBJIcon();

        gameOverDialog = new GameOverDialog(gameItem);
        gameOverDialog.initGameOverDialog();

        pauseDialog = new PauseDialog(gameItem);

        gameScript.fpc.level.updateLevel();
        pauseDialog.init();

//        giftScreen = new GiftScreen(gameItem);
//        giftScreen.init();

//        if (goalFeedbackScreen == null) {
//            goalFeedbackScreen = new GoalFeedbackScreen(gameItem);
//        }
//        goalFeedbackScreen.init(false);

        checkTryPeriod();
    }

    public void initButtons() {
        initPauseBtn();
        initBackButton();
    }

    public void reset() {
        init(gameItem.getEntity());
    }

    private void initDoubleBJIcon() {
        Entity bjIcon = gameItem.getChild(DOUBLE_BJ_ICON).getEntity();
        if (gameScript.fpc.haveBugJuiceDouble()) {
            TransformComponent tc = bjIcon.getComponent(TransformComponent.class);
            tc.scaleX = 0.6f;
            tc.scaleY = 0.6f;
            tc.x = 953;
            tc.y = 647;
        } else {
            bjIcon.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
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
        sceneLoader.getEngine().addSystem(new UmbrellaSystem());
        sceneLoader.getEngine().addSystem(new FlowerSystem());
        sceneLoader.getEngine().addSystem(new ButterflySystem());
        sceneLoader.getEngine().addSystem(new BugSystem());
        sceneLoader.getEngine().addSystem(new BugJuiceBubbleSystem());
        sceneLoader.getEngine().addSystem(new ParticleLifespanSystem());
        sceneLoader.getEngine().addSystem(new PetSystem());
        sceneLoader.getEngine().addSystem(new DandelionSystem(this));
        sceneLoader.getEngine().addSystem(new CocoonSystem(this));
        sceneLoader.getEngine().addSystem(new BugSpawnSystem(fpc));
    }

    private void initBackButton() {
        Entity backBtn = gameItem.getChild(BTN_BACK).getEntity();

        final LayerMapComponent lc = ComponentRetriever.get(backBtn, LayerMapComponent.class);
        backBtn.getComponent(TransformComponent.class).scaleX = 0.7f;
        backBtn.getComponent(TransformComponent.class).scaleY = 0.7f;
        backBtn.getComponent(TransformComponent.class).x = 90;
        backBtn.getComponent(TransformComponent.class).y = 680;

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
                resetPauseDialog();
                stage.initMenu();
            }
        });
    }

    public void resetPauseDialog() {
        pauseDialog.deleteTiles();
    }

    private void initPauseBtn() {
        pauseBtn = gameItem.getChild(BTN_PAUSE).getEntity();

        pauseBtn.getComponent(TransformComponent.class).scaleX = 0.7f;
        pauseBtn.getComponent(TransformComponent.class).scaleY = 0.7f;

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
                if(!isGameOver) {
                    pauseDialog.show();
                }
            }
        });
    }

    private void initFlower() {
        gameScript.fpc.score = 0;
        Entity flowerEntity = gameItem.getChild(MEGA_FLOWER).getEntity();
        TransformComponent tc = flowerEntity.getComponent(TransformComponent.class);
        tc.x = FLOWER_X_POS;
        tc.y = FLOWER_Y_POS;
        tc.scaleX = FLOWER_SCALE;
        tc.scaleY = FLOWER_SCALE;
        flowerEntity.add(tc);

        FlowerComponent fc = new FlowerComponent();

        gameItem.getChild(TUTORIAL_LINE).getEntity().getComponent(TintComponent.class).color.a = 0.7f;
        gameItem.getChild(TUTORIAL_LINE).getEntity().getComponent(TransformComponent.class).x = 975;

        flowerEntity.add(fc);
        flowerEntity.add(fpc);
    }

    public void initPet() {
        if (fpc.currentPet != null
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
//        System.gc();
    }

    @Override
    public void act(float delta) {

        if (!GameStage.justCreated) {
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
            pauseDialog.update(delta);
            giftScreen.update();
            goalFeedbackScreen.update();
        }
    }

    private void updateTapGoal() {
        if (fpc.level.getGoalByType(Goal.GoalType.TAP) != null) {
            fpc.level.getGoalByType(Goal.GoalType.TAP).update();
        }
    }

    public void onBugOutOfBounds() {
        if (fpc.canUsePhoenix()) {
            usePhoenix();
        } else {
            gameOverDialog.show();
        }
    }

    public void reloadScoreLabel(FlowerPublicComponent fcc) {
        scoreLabelComponent.text.replace(0, scoreLabelComponent.text.capacity(), "" + fcc.score + "/" + fcc.totalScore);
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
