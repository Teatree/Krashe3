package com.mygdx.etf.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.mygdx.etf.entity.componets.*;
import com.mygdx.etf.stages.ui.*;
import com.mygdx.etf.system.*;
import com.mygdx.etf.utils.CameraShaker;
import com.mygdx.etf.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.HashMap;
import java.util.Random;

import static com.mygdx.etf.entity.componets.FlowerComponent.*;
import static com.mygdx.etf.entity.componets.Goal.GoalType.SURVIVE_N_ANGERED_MODES;
import static com.mygdx.etf.stages.GameStage.gameScript;
import static com.mygdx.etf.stages.GameStage.sceneLoader;
import static com.mygdx.etf.stages.ShopScreenScript.allShopItems;
import static com.mygdx.etf.utils.GlobalConstants.*;

public class GameScreenScript implements IScript {

    public static final CameraShaker cameraShaker = new CameraShaker();
    public static final String TUTORIAL_LINE = "tutorial_line";
    public static final String UMBRELLA_ANI = "umbrellaAni";
    public static final int COCOON_X = 980;
    public static final int COCOON_Y = 800;
    public static final String BEES_MODE_ANI = "bees_mode_ani";
    public final String START_MESSAGE = "TAP TO START";
    public final String DOUBLE_BJ_ICON = "double_bj_badge";
    public final String LBL_SCORE = "lbl_score";
    public final String LBL_TAP_2_START = "lbl_tap2start";
    public final String BTN_PAUSE = "btn_pause";
    public final String MEGA_FLOWER = "mega_flower";
    public final String COCCOON = "coccoon";
    public final String BACKGROUND_LIB = "backgroundLib";
    public final String BTN_BACK = "btn_back";

    public static boolean isPause;
    public static boolean isGameOver;
    public static boolean isStarted;

    public GameStage stage;
    public Random random = new Random();
    public FlowerPublicComponent fpc;
    public Entity scoreLabelE;
    public LabelComponent startLabelComponent;
    public Entity background;
    public GiftScreen giftScreen;
    public GoalFeedbackScreen goalFeedbackScreen;

    public static float umbrellaSpawnCounter;
    public float cocoonSpawnCounter;
    public ItemWrapper gameItem;
    private GameOverDialog gameOverDialog;
    private PauseDialog pauseDialog;
    public Entity pauseBtn;

    //bee mode
    public static Entity beesModeAni;
    public static boolean isAngeredBeesMode = false;
    public static int angeredBeesModeTimer = ANGERED_BEES_MODE_DURATION;
    private TrialTimer timer;

    public GameScreenScript(GameStage gamestage) {
        this.stage = gamestage;
    }

    public static void angerBees() {
        isAngeredBeesMode = true;
        GameScreenScript.cameraShaker.initShaking(7f, 0.9f);
        BugSpawnSystem.queenBeeOnStage = false;

        BugSystem.blowUpAllBugs = true;
        beesModeAni.getComponent(SpriterComponent.class).player.setAnimation(0);
        beesModeAni.getComponent(SpriterComponent.class).player.speed = 26;
        beesModeAni.getComponent(SpriterComponent.class).player.setTime(0);

    }

    private void updateAngeredBeesMode() {
        if (isAngeredBeesMode) {
            angeredBeesModeTimer--;
            //stop ani when it's finished
            if (beesModeAni.getComponent(SpriterComponent.class).player.getTime() != 0 &&
                    beesModeAni.getComponent(SpriterComponent.class).player.getTime()%
                            beesModeAni.getComponent(SpriterComponent.class).player.getAnimation().length == 0) {
                beesModeAni.getComponent(SpriterComponent.class).player.speed = 0;
                BugSystem.blowUpAllBugs = false;
            }
            if (angeredBeesModeTimer <= 0) {
                isAngeredBeesMode = false;
                cameraShaker.initBlinking(40, 3);
                angeredBeesModeTimer = ANGERED_BEES_MODE_DURATION;

                checkAngeredBeesGoal();
            }
        }
    }

    private void checkAngeredBeesGoal() {
        if (fpc.level.getGoalByType(SURVIVE_N_ANGERED_MODES) != null) {
            fpc.level.getGoalByType(SURVIVE_N_ANGERED_MODES).update();
        }
    }

    public static void checkTryPeriod() {
        long now = System.currentTimeMillis();
        if (gameScript.fpc.currentPet != null && gameScript.fpc.currentPet.tryPeriod) {
            if (now - gameScript.fpc.currentPet.tryPeriodStart >= gameScript.fpc.currentPet.tryPeriodDuration * 1000) {
                gameScript.fpc.currentPet.enabled = false;
                gameScript.fpc.currentPet.bought = false;
                gameScript.fpc.currentPet.tryPeriod = false;
                gameScript.fpc.currentPet.disable();

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
                    u.disable();

                    if (allShopItems.indexOf(u) >= 0) {
                        allShopItems.get(allShopItems.indexOf(u)).bought = false;
                        allShopItems.get(allShopItems.indexOf(u)).enabled = false;
                    }
                }
            }
        }
    }

    public static void usePhoenix() {
        gameScript.fpc.upgrades.get(Upgrade.UpgradeType.PHOENIX).usePhoenix();
    }

    @Override
    public void init(Entity item) {

//        System.err.print("init game ");
//        System.err.println(Gdx.app.getJavaHeap() / 1000000 + " : " +
//                Gdx.app.getNativeHeap());

        gameItem = new ItemWrapper(item);

        beesModeAni = gameItem.getChild(BEES_MODE_ANI).getEntity();
        beesModeAni.getComponent(SpriterComponent.class).scale = 0.7f;
        beesModeAni.getComponent(SpriterComponent.class).player.speed = 0;

        CocoonSystem.resetSpawnCoefficients();
        cocoonSpawnCounter = CocoonSystem.getNextSpawnInterval();

        umbrellaSpawnCounter = UmbrellaSystem.getNextSpawnInterval();
        umbrellaSpawnCounter = 5;

        scoreLabelE = gameItem.getChild(LBL_SCORE).getEntity();
        LabelComponent scoreLabel = scoreLabelE.getComponent(LabelComponent.class);
        scoreLabel.text.replace(0, scoreLabel.text.capacity(), "0");

        Entity startLabel = gameItem.getChild(LBL_TAP_2_START).getEntity();
        startLabelComponent = startLabel.getComponent(LabelComponent.class);
        startLabelComponent.text.replace(0, startLabelComponent.text.capacity(), START_MESSAGE);

        addSystems();
        initFlower();
        initBackground();
        initPet();
        initDoubleBJIcon();
        initUmbrella();
        initCocoon();
        gameOverDialog = new GameOverDialog(gameItem);
        gameOverDialog.initGameOverDialog();
        pauseDialog = new PauseDialog(gameItem);
        pauseDialog.init();

        gameScript.fpc.level.updateLevel();

        giftScreen = new GiftScreen(gameItem);
        giftScreen.init();

        if (goalFeedbackScreen == null) {
            goalFeedbackScreen = new GoalFeedbackScreen(gameItem);
        }
        goalFeedbackScreen.init(false);

        if (timer == null) {
            timer = new TrialTimer(gameItem, 120, 650);
        }
        checkTryPeriod();

        gameScript.fpc.settings.totalPlayedGames++;
        gameScript.fpc.settings.playedGames++;
        isAngeredBeesMode = false;
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
        sceneLoader.getEngine().addSystem(new CocoonSystem(this));
        sceneLoader.getEngine().addSystem(new BugSpawnSystem());
    }

    private void initBackButton() {
        Entity backBtn = gameItem.getChild(BTN_BACK).getEntity();

        final LayerMapComponent lc = ComponentRetriever.get(backBtn, LayerMapComponent.class);
        backBtn.getComponent(TransformComponent.class).scaleX = 0.7f;
        backBtn.getComponent(TransformComponent.class).scaleY = 0.7f;
        backBtn.getComponent(TransformComponent.class).x = 10;
        backBtn.getComponent(TransformComponent.class).y = 640;

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
                if (!isGameOver) {
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
        if (fpc.currentPet != null) {
            Entity pet = gameItem.getChild(fpc.currentPet.name).getEntity();
            if (fpc.currentPet.enabled) {
                fpc.currentPet.init();

                TransformComponent tc = pet.getComponent(TransformComponent.class);
                tc.x = PetComponent.X_SPAWN_POSITION;
                tc.y = PetComponent.getNewPositionY();
                tc.scaleX = 1.3f;
                tc.scaleY = 1.3f;

                fpc.currentPet.petCannon.getComponent(TransformComponent.class).x = tc.x;
                fpc.currentPet.petCannon.getComponent(TransformComponent.class).y = tc.y;
                fpc.currentPet.petCannon.getComponent(ZIndexComponent.class).setZIndex(127);

                pet.add(fpc.currentPet);
            } else {
                if (pet != null && !fpc.currentPet.enabled) {
                    if (fpc.currentPet.petCannon != null) {
                        fpc.currentPet.petCannon.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
                        sceneLoader.getEngine().removeEntity(fpc.currentPet.petCannon);
                    }
                    sceneLoader.getEngine().removeEntity(pet);
                }
            }
        }
    }

    @Override
    public void dispose() {
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
                if (canUmbrellaSpawn()) {
                    umbrellaSpawnCounter -= delta;
                }
                if (canCocoonSpawn()) {
                    cocoonSpawnCounter -= delta;
                }
                if (umbrellaSpawnCounter <= 0) {
                    spawnUmbrella(UmbrellaComponent.INIT_SPAWN_X, UmbrellaComponent.INIT_SPAWN_Y);
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
            updateAngeredBeesMode();
            timer.timer();
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
        scoreLabelE.getComponent(LabelComponent.class).text.replace(0,
                scoreLabelE.getComponent(LabelComponent.class).text.capacity(),
                "" + fcc.score + "/" + fcc.totalScore);
    }

    private void initCocoon() {
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        Entity cocoonEntity = root.getChild(COCCOON).getEntity();
        if (cocoonEntity.getComponent(CocoonComponent.class) == null) {
            CocoonComponent dc = new CocoonComponent();
            cocoonEntity.add(dc);
        }
        cocoonEntity.getComponent(CocoonComponent.class).state = CocoonComponent.State.DEAD;
        cocoonEntity.getComponent(CocoonComponent.class).hitCounter = 0;

        Entity butEntity = root.getChild(CocoonSystem.BUTTERFLY_ANI).getEntity();
        if (butEntity.getComponent(ButterflyComponent.class) == null) {
            ButterflyComponent dc = new ButterflyComponent();
            butEntity.add(dc);
        }
        butEntity.getComponent(ButterflyComponent.class).state = ButterflyComponent.State.DEAD;
    }

    private void initUmbrella() {
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        Entity umbrellaEntity = root.getChild(UMBRELLA_ANI).getEntity();
        if (umbrellaEntity.getComponent(UmbrellaComponent.class) == null) {
            UmbrellaComponent dc = new UmbrellaComponent();
            umbrellaEntity.add(dc);
        }
        umbrellaEntity.getComponent(UmbrellaComponent.class).state = UmbrellaComponent.State.DEAD;
    }

    private void spawnUmbrella(float x, float y){

        Entity umbrellaEntity = gameItem.getChild(UMBRELLA_ANI).getEntity();

        TransformComponent transformComponent = new TransformComponent();
        transformComponent.x = x;
        transformComponent.y = y;
        umbrellaEntity.add(transformComponent);

        if (umbrellaEntity.getComponent(UmbrellaComponent.class)==null) {
            UmbrellaComponent umbrellaComponent = new UmbrellaComponent();
            umbrellaComponent.setToSpawningState();
            umbrellaEntity.add(umbrellaComponent);
            umbrellaEntity.add(GameStage.gameScript.fpc);
        } else {
            umbrellaEntity.getComponent(UmbrellaComponent.class).setToSpawningState();
        }
        umbrellaSpawnCounter = UmbrellaSystem.getNextSpawnInterval();
    }

    private void spawnCocoon() {
        if (canCocoonSpawn()) {
            cocoonSpawnCounter = CocoonSystem.getNextSpawnInterval();

            ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
            Entity cocoonEntity = root.getChild(COCCOON).getEntity();

            cocoonEntity.getComponent(SpriterComponent.class).scale = 0.3f;
            cocoonEntity.getComponent(SpriterComponent.class).player.setAnimation(0);

            TransformComponent tc = cocoonEntity.getComponent(TransformComponent.class);

            tc.x = COCOON_X;
            tc.y = COCOON_Y;
            cocoonEntity.add(tc);

            cocoonEntity.getComponent(CocoonComponent.class).state = CocoonComponent.State.SPAWNING;
            cocoonEntity.getComponent(CocoonComponent.class).hitCounter = 0;
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
                                .get(0).getComponent(TransformComponent.class).y == FAR_FAR_AWAY_Y;
    }

    private boolean canUmbrellaSpawn() {

        return (sceneLoader.getEngine().getEntitiesFor(Family.all(UmbrellaComponent.class).get()).size() == 0 ||
                        sceneLoader.getEngine().getEntitiesFor(Family.all(UmbrellaComponent.class).get())
                                .get(0).getComponent(TransformComponent.class).x == FAR_FAR_AWAY_X ||
                        sceneLoader.getEngine().getEntitiesFor(Family.all(UmbrellaComponent.class).get())
                                .get(0).getComponent(TransformComponent.class).x <= 0 ||
                        sceneLoader.getEngine().getEntitiesFor(Family.all(UmbrellaComponent.class).get())
                                .get(0).getComponent(TransformComponent.class).y == FAR_FAR_AWAY_Y
                );
    }
//
//    private void timer(ItemWrapper gameItem) {
//        final Entity timerE = gameItem.getChild(TRIAL_TIMER).getEntity();
//        boolean showTimer = false;
//        if (fpc.currentPet != null && fpc.currentPet.tryPeriod) {
//            showTimer = showTimer(timerE, fpc.currentPet.logoName);
//        } else {
//            for (Upgrade u : fpc.upgrades.values()) {
//                if (u.tryPeriod) {
//                   showTimer(timerE, u.logoName);
//                }
//            }
//        }
//        if (!showTimer) {
//            timerE.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
//            timerLogo.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
//            GameStage.sceneLoader.getEngine().removeEntity(timerLogo);
//            timerLogo = null;
//        }
//    }
//
//    private boolean showTimer(Entity timerE, String logoname) {
//        LabelComponent lc = timerE.getComponent(LabelComponent.class);
//        boolean showTimer;
//        lc.text.replace(0, lc.text.length, fpc.currentPet.updateTryPeriodTimer());
//        showTimer = true;
//        addTimerLogo(logoname);
//        timerE.getComponent(TransformComponent.class).x = 160;
//        timerE.getComponent(TransformComponent.class).y = 660;
//        timerLogo.getComponent(ZIndexComponent.class).setZIndex(background.getComponent(ZIndexComponent.class).getZIndex()+5);
//        return showTimer;
//    }
//
//    private void addTimerLogo(String logoLibName) {
//        if (timerLogo == null){
//            final CompositeItemVO tempC = sceneLoader.loadVoFromLibrary(logoLibName);
//            timerLogo = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempC);
//            sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), timerLogo, tempC.composite);
//            sceneLoader.getEngine().addEntity(timerLogo);
//        }
//        timerLogo.getComponent(TransformComponent.class).x = 120;
//        timerLogo.getComponent(TransformComponent.class).y = 650;
//        timerLogo.getComponent(TransformComponent.class).scaleX = 0.4f;
//        timerLogo.getComponent(TransformComponent.class).scaleY = 0.4f;
//        timerLogo.getComponent(ZIndexComponent.class).setZIndex(background.getComponent(ZIndexComponent.class).getZIndex()+5);
//    }
}