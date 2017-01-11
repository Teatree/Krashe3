package com.fd.etf.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.fd.etf.Main;
import com.fd.etf.entity.componets.*;
import com.fd.etf.entity.componets.listeners.ImageButtonListener;
import com.fd.etf.stages.ui.GameOverDialog;
import com.fd.etf.stages.ui.GoalFeedbackScreen;
import com.fd.etf.stages.ui.PauseDialog;
import com.fd.etf.system.*;
import com.fd.etf.utils.CameraShaker;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.fd.etf.entity.componets.CocoonComponent.*;
import static com.fd.etf.entity.componets.FlowerComponent.*;
import static com.fd.etf.entity.componets.Goal.GoalType.SURVIVE_N_ANGERED_MODES;
import static com.fd.etf.entity.componets.LeafsComponent.*;
import static com.fd.etf.system.BugSystem.blowUpAllBugs;
import static com.fd.etf.system.BugSystem.blowUpCounter;
import static com.fd.etf.utils.GlobalConstants.*;

public class GameScreenScript implements IScript {

    public static final CameraShaker cameraShaker = new CameraShaker();
    private static final String TUTORIAL_LINE = "tutorial_line";
    private static final String LOSE_FEEDBACK = "lose_feedback";
    private static final String UMBRELLA_ANI = "umbrellaAni";
    private static final String START_MESSAGE = "TAP TO START";
    private static final String DOUBLE_BJ_ICON = "double_bj_badge";
    private static final String PHOENIX_ICON = "extra_life_badge";
    private static final String LBL_SCORE = "lbl_score";
    private static final String LBL_SCORE_S = "lbl_score_s";
    private static final String LBL_TAP_2_START = "lbl_tap2start";
    private static final String BTN_PAUSE = "btn_pause";
    private static final String MEGA_FLOWER = "mega_flower";
    private static final String MEGA_LEAFS = "mega_leafs";
    private static final String COCOON = "coccoon";
    private static final String BTN_BACK = "btn_back";
    private static final String BEES_MODE_ANI = "bees_mode_ani";

    public static AtomicBoolean isPause = new AtomicBoolean(false);
    public static AtomicBoolean isGameOver = new AtomicBoolean(false);
    public static boolean isStarted;

    public GameStage gameStage;
    public Random random = new Random();
    public FlowerPublicComponent fpc;
    public Entity scoreLabelE;
    public Entity loseFeedback;
    public Entity scoreLabelES;
    public LabelComponent startLabelComponent;
    public Entity background;
    public static int currentFlowerFrame;
    public GoalFeedbackScreen goalFeedbackScreen;

    public static float umbrellaSpawnCounter;
    public float cocoonSpawnCounter;
    public ItemWrapper gameItem;
    public GameOverDialog gameOverDialog;
    private PauseDialog pauseDialog;
    public Entity pauseBtn;
    public int gameOverReviveTimesLimit;

    //bee mode
    private Entity beesModeAni;
    private Entity beesModeEndAni;
    public static boolean isAngeredBeesMode = false;
    public static int angeredBeesModeTimer = ANGERED_BEES_MODE_DURATION;
    private static boolean shouldShowGameOverDialog;
    private Entity petE;

    public GameScreenScript(GameStage gamestage) {
        this.gameStage = gamestage;
    }

    public void angerBees() {
        isAngeredBeesMode = true;
        BugSpawnSystem.break_counter = 1;
        GameScreenScript.cameraShaker.initShaking(7f, 0.9f);
        BugSpawnSystem.queenBeeOnStage = false;

        BugSystem.blowUpAllBugs = true;
        BugSystem.blowUpCounter = 10;
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

                GameScreenScript.cameraShaker.initShaking(7f, 0.9f);
                beesModeEndAni.getComponent(TransformComponent.class).y = 394;
                beesModeEndAni.getComponent(SpriterComponent.class).player.setAnimation(0);
                beesModeEndAni.getComponent(SpriterComponent.class).player.speed = 26;
                beesModeEndAni.getComponent(SpriterComponent.class).player.setTime(0);
                angeredBeesModeTimer = ANGERED_BEES_MODE_DURATION;
                checkAngeredBeesGoal();
            }
        }
        if (beesModeEndAni.getComponent(SpriterComponent.class).player.getTime() != 0 &&
                beesModeEndAni.getComponent(SpriterComponent.class).player.getTime() %
                        beesModeEndAni.getComponent(SpriterComponent.class).player.getAnimation().length == 0) {
            beesModeEndAni.getComponent(SpriterComponent.class).player.speed = 0;
        }

    }

    private void checkAngeredBeesGoal() {
        if (fpc.level.getGoalByType(SURVIVE_N_ANGERED_MODES) != null) {
            fpc.level.getGoalByType(SURVIVE_N_ANGERED_MODES).update();
        }
    }

    public void checkTryPeriod() {
        long now = System.currentTimeMillis();
        if (gameStage.gameScript.fpc.currentPet != null && gameStage.gameScript.fpc.currentPet.tryPeriod) {
            if (now - gameStage.gameScript.fpc.currentPet.tryPeriodStart >= gameStage.gameScript.fpc.currentPet.tryPeriodDuration * 1000) {
                gameStage.gameScript.fpc.currentPet.enabled = false;
                gameStage.gameScript.fpc.currentPet.bought = false;
                gameStage.gameScript.fpc.currentPet.tryPeriod = false;
                gameStage.gameScript.fpc.currentPet.disable(gameStage);

                if (gameStage.shopScript.allSoftItems.indexOf(gameStage.gameScript.fpc.currentPet) >= 0) {
                    gameStage.shopScript.allSoftItems.get(gameStage.shopScript.allSoftItems.indexOf(gameStage.gameScript.fpc.currentPet)).bought = false;
                    gameStage.shopScript.allSoftItems.get(gameStage.shopScript.allSoftItems.indexOf(gameStage.gameScript.fpc.currentPet)).enabled = false;
                }
            }
        }
        if (gameStage.gameScript.fpc.upgrades != null && !gameStage.gameScript.fpc.upgrades.isEmpty()) {
            for (Upgrade u : gameStage.gameScript.fpc.upgrades.values()) {
                u.gameStage = gameStage;
                if (u.tryPeriod && now - u.tryPeriodStart >= u.tryPeriodDuration * 1000) {
                    u.enabled = false;
                    u.bought = false;
                    u.tryPeriod = false;
                    u.disable(gameStage);

                    if (gameStage.shopScript.allSoftItems.indexOf(u) >= 0) {
                        gameStage.shopScript.allSoftItems.get(gameStage.shopScript.allSoftItems.indexOf(u)).bought = false;
                        gameStage.shopScript.allSoftItems.get(gameStage.shopScript.allSoftItems.indexOf(u)).enabled = false;
                    }
                }
            }
        }
    }

    public void usePhoenix() {
        gameStage.gameScript.fpc.upgrades.get(Upgrade.UpgradeType.PHOENIX).usePhoenix();
    }

    @Override
    public void init(Entity item) {

        gameOverReviveTimesLimit = 2;
        gameItem = new ItemWrapper(item);

        beesModeAni = gameItem.getChild(BEES_MODE_ANI).getEntity();
        beesModeAni.getComponent(SpriterComponent.class).scale = 0.7f;
        beesModeAni.getComponent(SpriterComponent.class).player.speed = 0;
        beesModeAni.getComponent(TransformComponent.class).y = 1800;

        beesModeEndAni = gameItem.getChild(BEES_MODE_ANI).getEntity();
        beesModeEndAni.getComponent(SpriterComponent.class).scale = 0.7f;
        beesModeEndAni.getComponent(SpriterComponent.class).player.speed = 0;
        beesModeEndAni.getComponent(TransformComponent.class).y = 1800;

        loseFeedback = gameItem.getChild(LOSE_FEEDBACK).getEntity();
        loseFeedback.getComponent(TintComponent.class).color.a = 0;

        CocoonSystem.resetSpawnCoefficients();
        cocoonSpawnCounter = CocoonSystem.getNextSpawnInterval();

        umbrellaSpawnCounter = UmbrellaSystem.getNextSpawnInterval();

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
        initPet();
        initDoubleBJIcon();
        initPhoenixIcon();
        initUmbrella();
        initCocoon();

        checkTryPeriod();

        gameStage.gameScript.fpc.settings.totalPlayedGames++;
        gameStage.gameScript.fpc.settings.playedGames++;
        isAngeredBeesMode = false;
    }

    public void initButtons() {
        initPauseBtn();
        pauseDialog = null;
        gameOverDialog = null;
        initBackButton();
    }

    public void reset() {
        fpc.score = 0;
        scoreLabelE.getComponent(LabelComponent.class).text.replace(0,
                scoreLabelE.getComponent(LabelComponent.class).text.capacity(), "" + fpc.score);
        scoreLabelES.getComponent(LabelComponent.class).text.replace(0,
                scoreLabelES.getComponent(LabelComponent.class).text.capacity(), "" + fpc.score);
        startLabelComponent.text.replace(0, startLabelComponent.text.capacity(), START_MESSAGE);
    }

    private void initDoubleBJIcon() {
        Entity bjIcon = gameItem.getChild(DOUBLE_BJ_ICON).getEntity();
        if (gameStage.gameScript.fpc.haveBugJuiceDouble()) {
            TransformComponent tc = bjIcon.getComponent(TransformComponent.class);
            if (gameStage.gameScript.fpc.havePhoenix()) {
                tc.x = 117;
                tc.y = 675;
            } else {
                tc.x = 15;
                tc.y = 675;
            }
        } else {
            bjIcon.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        }
    }

    private void initPhoenixIcon() {
        Entity bjIcon = gameItem.getChild(PHOENIX_ICON).getEntity();
        if (gameStage.gameScript.fpc.havePhoenix()) {
            TransformComponent tc = bjIcon.getComponent(TransformComponent.class);
            tc.x = -24;
            tc.y = 637;
        } else {
            bjIcon.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        }
    }

    private void addSystems() {
//        gameStage.sceneLoader.getEngine().addSystem(new DebugSystem());
        gameStage.sceneLoader.getEngine().addSystem(new UmbrellaSystem(gameStage));
        gameStage.sceneLoader.getEngine().addSystem(new LeafsSystem());
        gameStage.sceneLoader.getEngine().addSystem(new ButterflySystem(gameStage));
        gameStage.sceneLoader.getEngine().addSystem(new FlowerSystem(gameStage));
        gameStage.sceneLoader.getEngine().addSystem(new BugSystem(gameStage));
        gameStage.sceneLoader.getEngine().addSystem(new BugJuiceBubbleSystem(gameStage));
        gameStage.sceneLoader.getEngine().addSystem(new ParticleLifespanSystem());
        gameStage.sceneLoader.getEngine().addSystem(new PetSystem(gameStage));
        gameStage.sceneLoader.getEngine().addSystem(new CocoonSystem(this));
        gameStage.sceneLoader.getEngine().addSystem(new BugSpawnSystem(gameStage));
    }

    private void initBackButton() {
        Entity backMenuBtn = gameItem.getChild(BTN_BACK).getEntity();

        backMenuBtn.getComponent(ButtonComponent.class)
                .addListener(new ImageButtonListener(backMenuBtn, new AtomicBoolean[]{isGameOver, isPause}) {
                    @Override
                    public void clicked() {
                        if (!isPause.get() && !isGameOver.get()) {
                            resetPauseDialog();
                            gameItem.getChild(MEGA_FLOWER).getEntity().getComponent(SpriterComponent.class).player.setTime(0);
                            gameStage.initMenu();
                        }
                    }
                });
    }

    public void resetPauseDialog() {
        if (pauseDialog != null) {
            gameItem.getChild(PauseDialog.LBL_PAUSE_TIMER).getEntity()
                    .getComponent(LabelComponent.class).text.replace(0, 1, "");
            pauseDialog.deleteTiles();
        }
    }

    private void initPauseBtn() {
        pauseBtn = gameItem.getChild(BTN_PAUSE).getEntity();

        pauseBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(pauseBtn, new AtomicBoolean[]{isGameOver, isPause}) {
                    @Override
                    public void clicked() {
                        if (!isGameOver.get() && isStarted && !isPause.get()) {
                            isPause.set(true);
                            if (pauseDialog == null) {
                                pauseDialog = new PauseDialog(gameStage, gameItem);
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
        leafsEntity.add(new DebugComponent());
    }

    private void initFlower() {
        gameStage.gameScript.fpc.score = 0;
        Entity flowerEntity = gameItem.getChild(MEGA_FLOWER).getEntity();

        TransformComponent tc = flowerEntity.getComponent(TransformComponent.class);
        tc.x = FLOWER_X_POS;
        tc.y = FLOWER_Y_POS;
        tc.scaleX = FLOWER_SCALE;
        tc.scaleY = FLOWER_SCALE;
        flowerEntity.add(tc);

        FlowerComponent fc = new FlowerComponent();

//        System.out.println("currentFlowerFrame: " + currentFlowerFrame);
        flowerEntity.getComponent(SpriterComponent.class).player.setTime(currentFlowerFrame);
//        System.out.println("flowerEntity.getComponent(SpriterComponent.class).player: " + flowerEntity.getComponent(SpriterComponent.class).player.getTime());

        gameItem.getChild(TUTORIAL_LINE).getEntity().getComponent(TintComponent.class).color.a = 0.7f;
        gameItem.getChild(TUTORIAL_LINE).getEntity().getComponent(TransformComponent.class).x = 975;

        flowerEntity.add(fc);
        flowerEntity.add(fpc);
    }

    public void hideCurrentPet() {
        if (gameStage.gameScript.fpc.currentPet != null) {
//            gameStage.gameScript.fpc.currentPet.disable();
            if (petE != null) {
                petE.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
                petE.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
                gameStage.sceneLoader.getEngine().removeEntity(petE);
            }
            if (gameStage.gameScript.fpc.currentPet.petHead != null) {
                gameStage.gameScript.fpc.currentPet.petHead.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
                gameStage.gameScript.fpc.currentPet.petCannon.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
                gameStage.sceneLoader.getEngine().removeEntity(gameStage.gameScript.fpc.currentPet.petCannon);
                gameStage.sceneLoader.getEngine().removeEntity(gameStage.gameScript.fpc.currentPet.petHead);
            }
        }
    }

    public void initPet() {
        hideCurrentPet();
        if (fpc.currentPet != null && fpc.currentPet.enabled) {
            loadPetFromLib();
            fpc.currentPet.gameStage = gameStage;
            if (fpc.currentPet.enabled) {
                fpc.currentPet.init();

                TransformComponent tc = petE.getComponent(TransformComponent.class); //TODO: NPE
                tc.x = PetComponent.X_SPAWN_POSITION;
                tc.y = PetComponent.getNewPositionY();
                tc.scaleX = 1.3f;
                tc.scaleY = 1.3f;

                fpc.currentPet.petCannon.getComponent(TransformComponent.class).x = tc.x + 64;
                fpc.currentPet.petCannon.getComponent(TransformComponent.class).y = tc.y - 9;
                fpc.currentPet.petCannon.getComponent(ZIndexComponent.class).setZIndex(127);

                petE.add(fpc.currentPet);
                petE.add(new DebugComponent());
            } else {
                if (petE != null && !fpc.currentPet.enabled) {
                    if (fpc.currentPet.petCannon != null) {
                        fpc.currentPet.petCannon.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
                    }
                }
            }
        }
    }

    private void loadPetFromLib() {
        CompositeItemVO tempItemC = gameStage.sceneLoader.loadVoFromLibrary(fpc.currentPet.name);
        gameStage.sceneLoader.rm.addSpriterToLoad(fpc.currentPet.name);
        petE = gameStage.sceneLoader.entityFactory.createSPRITERentity(gameStage.sceneLoader.getRoot(), tempItemC);
        gameStage.sceneLoader.getEngine().addEntity(petE);
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
            }

            if (!isStarted && Gdx.input.justTouched()) {
                startLabelComponent.text.replace(0, startLabelComponent.text.capacity(), "");
                isStarted = true;

                updateTapGoal();
            }

            if (!isPause.get() && !isGameOver.get() && isStarted) {
                if (canUmbrellaSpawn()) {
                    umbrellaSpawnCounter -= delta;
                }
                if (canCocoonSpawn()) {
                    cocoonSpawnCounter -= delta;
                }
                if (umbrellaSpawnCounter <= 0) {
                    spawnUmbrella(UmbrellaComponent.INIT_SPAWN_X, UmbrellaComponent.INIT_SPAWN_Y);
                }
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
//        if (fpc.canUsePhoenix()) {
//            usePhoenix();
//        }
//        else {

        loseFeedback.getComponent(TransformComponent.class).scaleX = 0.5f;
        loseFeedback.getComponent(TransformComponent.class).x = 950;
        loseFeedback.getComponent(ZIndexComponent.class).setZIndex(1200);
        ActionComponent ac = gameStage.sceneLoader.engine.createComponent(ActionComponent.class);
        Actions.checkInit();
        ac.dataArray.add(Actions.fadeIn(0.2f));
        ac.dataArray.add(Actions.scaleTo(1.5f,1.5f,0.2f));
        ac.dataArray.add(Actions.moveTo(742, loseFeedback.getComponent(TransformComponent.class).y, 0.2f));
        loseFeedback.add(ac);

            FlowerComponent.state = FlowerComponent.State.LOSING;
//            endGame();
//        }
    }

    public void endGame() {
        gameOverReviveTimesLimit--;
        shouldShowGameOverDialog = gameOverReviveTimesLimit > 0;

        if (shouldShowGameOverDialog) {
            showGameOverDialog();
        } else {
            isGameOver.set(false);
            if (GoalFeedbackScreen.shouldShow &&
                    !GoalFeedbackScreen.isGoalFeedbackOpen) {
                showGoalFeedback();
                isGameOver.set(true);
            } else if ((gameStage.gameScript.goalFeedbackScreen == null ||
                    !GoalFeedbackScreen.isGoalFeedbackOpen)) {
                isGameOver.set(false);
                gameStage.gameScript.resetPauseDialog();
                if (Main.mainController.isWifiConnected() && Main.mainController.isSignedIn()) {
                    Main.mainController.submitScore(fpc.score);
                }
                gameStage.gameScript.gameOverDialog.hide();
                gameItem.getChild(MEGA_FLOWER).getEntity().getComponent(SpriterComponent.class).player.setTime(0);
                gameStage.gameScript.gameStage.initResultWithAds();
            }
        }
    }

    public void showGoalFeedback() {
        if (goalFeedbackScreen == null) {
            goalFeedbackScreen = new GoalFeedbackScreen(gameStage);
        }
        goalFeedbackScreen.init(false);
        gameStage.gameScript.goalFeedbackScreen.show();
    }

    private void showGameOverDialog() {
        if (gameOverDialog == null) {
            gameOverDialog = new GameOverDialog(gameStage);
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
        Entity cocoonEntity = gameItem.getChild(COCOON).getEntity();

        if (cocoonEntity.getComponent(CocoonComponent.class) == null) {
            CocoonComponent cocoonComponentc = new CocoonComponent();
            cocoonEntity.add(cocoonComponentc);
        }
        cocoonEntity.getComponent(CocoonComponent.class).state = CocoonComponent.State.DEAD;
        cocoonEntity.getComponent(CocoonComponent.class).hitCounter = 0;
        cocoonEntity.add(new DebugComponent(cocoonEntity.getComponent(CocoonComponent.class).boundsRect));

        Entity butEntity = gameItem.getChild(CocoonSystem.BUTTERFLY_ANI).getEntity();
        if (butEntity.getComponent(ButterflyComponent.class) == null) {
            ButterflyComponent dc = new ButterflyComponent();
            butEntity.add(dc);
        }
        butEntity.getComponent(ButterflyComponent.class).state = ButterflyComponent.State.DEAD;
        butEntity.add(new DebugComponent(butEntity.getComponent(ButterflyComponent.class).boundsRect));
    }

    private void initUmbrella() {
        Entity umbrellaEntity = gameItem.getChild(UMBRELLA_ANI).getEntity();
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
        umbrellaEntity.add(new DebugComponent(umbrellaEntity.getComponent(UmbrellaComponent.class).boundsRect));
    }

    private void spawnCocoon() {
        if (canCocoonSpawn()) {
            cocoonSpawnCounter = CocoonSystem.getNextSpawnInterval();

            Entity cocoonEntity = gameItem.getChild(COCOON).getEntity();

            cocoonEntity.getComponent(SpriterComponent.class).scale = COCOON_SCALE;
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
                gameStage.sceneLoader.getEngine().getEntitiesFor(Family.all(CocoonComponent.class).get()).size() == 0 ||
                        gameStage.sceneLoader.getEngine().getEntitiesFor(Family.all(CocoonComponent.class).get())
                                .get(0).getComponent(TransformComponent.class).x == FAR_FAR_AWAY_X ||
                        gameStage.sceneLoader.getEngine().getEntitiesFor(Family.all(CocoonComponent.class).get())
                                .get(0).getComponent(TransformComponent.class).x <= 0 ||
                        gameStage.sceneLoader.getEngine().getEntitiesFor(Family.all(CocoonComponent.class).get())
                                .get(0).getComponent(TransformComponent.class).y == FAR_FAR_AWAY_Y;
    }

    private boolean canUmbrellaSpawn() {

        return (gameStage.sceneLoader.getEngine().getEntitiesFor(Family.all(UmbrellaComponent.class).get()).size() == 0 ||
                gameStage.sceneLoader.getEngine().getEntitiesFor(Family.all(UmbrellaComponent.class).get())
                        .get(0).getComponent(TransformComponent.class).x == FAR_FAR_AWAY_X ||
                gameStage.sceneLoader.getEngine().getEntitiesFor(Family.all(UmbrellaComponent.class).get())
                        .get(0).getComponent(TransformComponent.class).x <= 0 ||
                gameStage.sceneLoader.getEngine().getEntitiesFor(Family.all(UmbrellaComponent.class).get())
                        .get(0).getComponent(TransformComponent.class).y == FAR_FAR_AWAY_Y
        );
    }

    public void reloadFlower(){
        Entity root = gameStage.sceneLoader.rootEntityByScene.get(GameStage.MAIN_SCENE);

//        root.getComponent(NodeComponent.class).addChild();
    }
}
