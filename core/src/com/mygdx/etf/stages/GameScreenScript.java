package com.mygdx.etf.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.mygdx.etf.entity.componets.*;
import com.mygdx.etf.entity.componets.listeners.ImageButtonListener;
import com.mygdx.etf.stages.ui.*;
import com.mygdx.etf.system.*;
import com.mygdx.etf.utils.CameraShaker;
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

import java.util.Random;

import static com.mygdx.etf.entity.componets.FlowerComponent.*;
import static com.mygdx.etf.entity.componets.LeafsComponent.*;
import static com.mygdx.etf.entity.componets.Goal.GoalType.SURVIVE_N_ANGERED_MODES;
import static com.mygdx.etf.stages.GameStage.gameScript;
import static com.mygdx.etf.stages.GameStage.sceneLoader;
import static com.mygdx.etf.stages.ShopScreenScript.allShopItems;
import static com.mygdx.etf.utils.GlobalConstants.*;
import static com.mygdx.etf.system.BugSystem.*;

public class GameScreenScript implements IScript {

    public static final CameraShaker cameraShaker = new CameraShaker();
    public static final String TUTORIAL_LINE = "tutorial_line";
    public static final String UMBRELLA_ANI = "umbrellaAni";
    public final String START_MESSAGE = "TAP TO START";
    public final String DOUBLE_BJ_ICON = "double_bj_badge";
    public final String LBL_SCORE = "lbl_score";
    public final String LBL_SCORE_S = "lbl_score_s";
    public final String LBL_TAP_2_START = "lbl_tap2start";
    public final String BTN_PAUSE = "btn_pause";
    public final String MEGA_FLOWER = "mega_flower";
    public final String MEGA_LEAFS = "mega_leafs";
    public final String COCCOON = "coccoon";
    public final String BTN_BACK = "btn_back";
    public static final String BEES_MODE_ANI = "bees_mode_ani";

    public static final int COCOON_X = 980;
    public static final int COCOON_Y = 800;
    public static final int TRIAL_TIMER_X = 120;
    public static final int TRIAL_TIMER_Y = 650;

    public static boolean isPause;
    public static boolean isGameOver;
    public static boolean isStarted;

    public GameStage stage;
    public Random random = new Random();
    public FlowerPublicComponent fpc;
    public Entity scoreLabelE;
    public Entity scoreLabelES;
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
    public int gameOverReviveTimesLimit;

    //bee mode
    public static Entity beesModeAni;
    public static boolean isAngeredBeesMode = false;
    public static int angeredBeesModeTimer = ANGERED_BEES_MODE_DURATION;
    public static boolean shouldShowGameOverDialog;

    public GameScreenScript(GameStage gamestage) {
        this.stage = gamestage;
    }

    public static void angerBees() {
        isAngeredBeesMode = true;
        GameScreenScript.cameraShaker.initShaking(7f, 0.9f);
        BugSpawnSystem.queenBeeOnStage = false;

        BugSystem.blowUpAllBugs = true;
        beesModeAni.getComponent(TransformComponent.class).y = 394;
        beesModeAni.getComponent(SpriterComponent.class).player.setAnimation(0);
        beesModeAni.getComponent(SpriterComponent.class).player.speed = 26;
        beesModeAni.getComponent(SpriterComponent.class).player.setTime(0);

    }

    private void updateAngeredBeesMode() {
        if (isAngeredBeesMode) {
            angeredBeesModeTimer--;
            //stop ani when it's finished
            if (beesModeAni.getComponent(SpriterComponent.class).player.getTime() != 0 &&
                    beesModeAni.getComponent(SpriterComponent.class).player.getTime() %
                            beesModeAni.getComponent(SpriterComponent.class).player.getAnimation().length == 0) {
                beesModeAni.getComponent(SpriterComponent.class).player.speed = 0;
                BugSystem.blowUpAllBugs = false;
            }
            if (angeredBeesModeTimer <= 0) {
                isAngeredBeesMode = false;
//                cameraShaker.initBlinking(40, 3);
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
        gameOverReviveTimesLimit = 2;
        gameItem = new ItemWrapper(item);

        beesModeAni = gameItem.getChild(BEES_MODE_ANI).getEntity();
        beesModeAni.getComponent(SpriterComponent.class).scale = 0.7f;
        beesModeAni.getComponent(SpriterComponent.class).player.speed = 0;
        beesModeAni.getComponent(TransformComponent.class).y = 1800;

        CocoonSystem.resetSpawnCoefficients();
        cocoonSpawnCounter = CocoonSystem.getNextSpawnInterval();

        umbrellaSpawnCounter = UmbrellaSystem.getNextSpawnInterval();
//        umbrellaSpawnCounter = 5;

        fpc.score = 0;

        scoreLabelE = gameItem.getChild(LBL_SCORE).getEntity();
        LabelComponent scoreLabel = scoreLabelE.getComponent(LabelComponent.class);
        scoreLabel.text.replace(0, scoreLabel.text.capacity(), "" + fpc.score);

        scoreLabelES = gameItem.getChild(LBL_SCORE_S).getEntity();
        LabelComponent scoreLabelS = scoreLabelES.getComponent(LabelComponent.class);
        scoreLabelS.text.replace(0, scoreLabelS.text.capacity(), "" + fpc.score);

        Entity startLabel = gameItem.getChild(LBL_TAP_2_START).getEntity();
        startLabelComponent = startLabel.getComponent(LabelComponent.class);
        startLabelComponent.text.replace(0, startLabelComponent.text.capacity(), START_MESSAGE);

        addSystems();
        initFlower();
        initLeafs();
//        initBackground();
        initPet();
//        initDoubleBJIcon();
        initUmbrella();
        initCocoon();

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

    private void addSystems() {
        sceneLoader.getEngine().addSystem(new UmbrellaSystem());
        sceneLoader.getEngine().addSystem(new LeafsSystem());
        sceneLoader.getEngine().addSystem(new ButterflySystem());
        sceneLoader.getEngine().addSystem(new FlowerSystem());
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

        backBtn.getComponent(ButtonComponent.class).addListener(new ImageButtonListener(backBtn) {
            @Override
            public void clicked() {
                resetPauseDialog();
                stage.initMenu();
            }
        });
    }

    public void resetPauseDialog() {
        if (pauseDialog != null) {
            pauseDialog.deleteTiles();
        }
    }

    private void initPauseBtn() {
        pauseBtn = gameItem.getChild(BTN_PAUSE).getEntity();

        pauseBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(pauseBtn) {
                    @Override
                    public void clicked() {
                        isPause = true;
                        if (!isGameOver && isStarted) {
                            if (pauseDialog == null) {
                                pauseDialog = new PauseDialog(gameItem);
                                pauseDialog.init();
                            }
                            pauseDialog.show();
                        }
                    }
                });
    }

    private void initLeafs() {
        Entity leafsEntity = gameItem.getChild(MEGA_LEAFS).getEntity();

        TransformComponent tcL = leafsEntity.getComponent(TransformComponent.class);
        tcL.x = LEAFS_X_POS;
        tcL.y = LEAFS_Y_POS;
        tcL.scaleX = LEAFS_SCALE;
        tcL.scaleY = LEAFS_SCALE;
        leafsEntity.add(tcL);

        LeafsComponent lc = new LeafsComponent();

        leafsEntity.add(lc);
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
            Entity petE = gameItem.getChild(fpc.currentPet.name).getEntity();
            if (fpc.currentPet.enabled) {
                fpc.currentPet.init();

                TransformComponent tc = petE.getComponent(TransformComponent.class); //TODO: NPE
                tc.x = PetComponent.X_SPAWN_POSITION;
                tc.y = PetComponent.getNewPositionY();
                tc.scaleX = 1.3f;
                tc.scaleY = 1.3f;

                fpc.currentPet.petCannon.getComponent(TransformComponent.class).x = tc.x+64;
                fpc.currentPet.petCannon.getComponent(TransformComponent.class).y = tc.y-9;
                fpc.currentPet.petCannon.getComponent(ZIndexComponent.class).setZIndex(127);

                petE.add(fpc.currentPet);
            } else {
                if (petE != null && !fpc.currentPet.enabled) {
                    if (fpc.currentPet.petCannon != null) {
                        fpc.currentPet.petCannon.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
                    }
                }
            }
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    public void act(float delta) {

        if (blowUpAllBugs) {
            blowUpCounter--;
        }

        if (!GameStage.justCreated) {
            if (cameraShaker.time > 0) {
                cameraShaker.shake(delta);
//                cameraShaker.blink();
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
                    System.out.println(" SPAWNING UMBRELLA, ALSO KNOWN AS x2 ");
                }
                //spawn Cocoon
                if (cocoonSpawnCounter <= 0) {
                    spawnCocoon();
                }
            }

            if (gameOverDialog != null) {
                gameOverDialog.update(delta);
            }

            if (pauseDialog != null) {
                pauseDialog.update(delta);
            }
            if (giftScreen != null) {
                giftScreen.update();
            }
            if (goalFeedbackScreen != null) {
                goalFeedbackScreen.update();
            }
            updateAngeredBeesMode();
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
            endGame();
        }
    }

    private void endGame() {
        gameOverReviveTimesLimit--;
        if (gameOverReviveTimesLimit <= 0) {
            shouldShowGameOverDialog = false;
        }else{
            shouldShowGameOverDialog = true;
        }

//        if (new Random().nextInt(100) >= GameOverDialog.GAME_OVER_PROBABILITY){
//            shouldShowGameOverDialog = true;
//        } else {
//            shouldShowGameOverDialog = false;
//        }

        if (shouldShowGameOverDialog) {
            showGameOverDialog();
        } else {
            isGameOver = false;
//            resetGameData();

            if (GoalFeedbackScreen.shouldShow &&
                    (gameScript.giftScreen == null || !gameScript.goalFeedbackScreen.isGoalFeedbackOpen)) {
                showGoalFeedback();
//                close(gameOverDialogE);
                isGameOver = true;
            } else if ((gameScript.goalFeedbackScreen == null || !gameScript.goalFeedbackScreen.isGoalFeedbackOpen) &&
                    (gameScript.giftScreen == null || !gameScript.giftScreen.isGiftScreenOpen)) {
                isGameOver = false;
                gameScript.resetPauseDialog();
                gameScript.stage.initResultWithAds();
//                close(gameOverDialogE);
            }
//            gameScript.stage.initResultWithAds();
        }
    }

    public void showGoalFeedback() {
        if (goalFeedbackScreen == null) {
            goalFeedbackScreen = new GoalFeedbackScreen();
        }
        goalFeedbackScreen.init(false);
        gameScript.goalFeedbackScreen.show();
    }

    private void showGameOverDialog() {
        if (gameOverDialog == null) {
            gameOverDialog = new GameOverDialog();
            gameOverDialog.initGameOverDialog();
        }
        gameOverDialog.show();
    }

    public void reloadScoreLabel(FlowerPublicComponent fcc) {
        scoreLabelE.getComponent(LabelComponent.class).text.replace(0,
                scoreLabelE.getComponent(LabelComponent.class).text.capacity(),     // real look alike
                "" + fcc.score);
        scoreLabelES.getComponent(LabelComponent.class).text.replace(0,
                scoreLabelES.getComponent(LabelComponent.class).text.capacity(), "" + fcc.score);
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
        if (umbrellaEntity.getComponent(UmbrellaComponent.class) != null) {
            umbrellaEntity.remove(UmbrellaComponent.class);
        }
        UmbrellaSystem.hide(umbrellaEntity);
    }

    private void spawnUmbrella(float x, float y) {

        Entity umbrellaEntity = gameItem.getChild(UMBRELLA_ANI).getEntity();

        if (umbrellaEntity.getComponent(UmbrellaComponent.class) == null) {
            UmbrellaComponent umbrellaComponent = new UmbrellaComponent();
            umbrellaComponent.setToSpawningState();
            umbrellaEntity.add(umbrellaComponent);
        } else {
            umbrellaEntity.getComponent(UmbrellaComponent.class).setToSpawningState();
        }

        umbrellaEntity.getComponent(TransformComponent.class).x = x;
        umbrellaEntity.getComponent(TransformComponent.class).y = y;

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
}
