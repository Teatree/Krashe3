package com.fd.etf.stages;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.fd.etf.Main;
import com.fd.etf.entity.componets.*;
import com.fd.etf.entity.componets.listeners.ImageButtonListener;
import com.fd.etf.stages.ui.GameOverDialog;
import com.fd.etf.stages.ui.GoalFeedbackScreen;
import com.fd.etf.stages.ui.PauseDialog;
import com.fd.etf.system.*;
import com.fd.etf.utils.BackgroundMusicMgr;
import com.fd.etf.utils.CameraShaker;
import com.fd.etf.utils.DebugSystem;
import com.fd.etf.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.fd.etf.entity.componets.CocoonComponent.*;
import static com.fd.etf.entity.componets.FlowerComponent.*;
import static com.fd.etf.entity.componets.Goal.GoalType.SURVIVE_N_ANGERED_MODES;
import static com.fd.etf.entity.componets.LeafsComponent.*;
import static com.fd.etf.utils.GlobalConstants.*;

public class GameScreenScript implements IScript, GameStage.IhaveFlower {

    public static final CameraShaker cameraShaker = new CameraShaker();
    private static final String TUTORIAL_LINE = "tutorial_line";
    private static final String LOSE_FEEDBACK = "lose_feedback";
    private static final String UMBRELLA_ANI = "umbrellaAni";
    private static final String START_MESSAGE = "TAP TO START";
    private static final String DOUBLE_BJ_ICON = "double_bj_badge";
    private static final String PHOENIX_ICON = "extra_life_badge";
    private static final String LBL_SCORE = "lbl_score";
    private static final String LBL_SCORE_SH = "lbl_score_sh";
    private static final String SCOREC = "scoreC";
    private static final String LBL_TAP_2_START = "lbl_tap2start";
    private static final String BTN_PAUSE = "btn_pause";
    private static final String MEGA_FLOWER = "mega_flower";
    private static final String MEGA_LEAVES = "mega_leafs";
    private static final String COCOON = "coccoon";
    private static final String BTN_BACK = "btn_back";
    private static final String BEES_MODE_ANI = "bees_mode_ani";
    private static final String CURTAIN_GAME = "curtain_game";

    public static AtomicBoolean isPause = new AtomicBoolean(false);
    public static AtomicBoolean isGameOver = new AtomicBoolean(false);
    public static boolean isStarted;

    public GameStage gameStage;
    public Random random = new Random();
    public FlowerPublicComponent fpc;
    public Entity scoreLabelE;
    public Entity scoreLabelEsh;
    public Entity loseFeedback;
    public Entity curtainGameE;
    public LabelComponent startLabelComponent;
    public static int currentFlowerFrame;
    public GoalFeedbackScreen goalFeedbackScreen;
    public static List<Rectangle> projectileBounds;

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
    private Entity phoenixIcon;
    public boolean changePet;

    public Entity megaFlower;
    public Entity megaLeaves;
    public Entity scoreCE;

    public GameScreenScript(GameStage gamestage) {
        this.gameStage = gamestage;
    }

    public void angerBees() {
        BugSpawnSystem.isFirst = true;
        isAngeredBeesMode = true;
        BugSpawnSystem.angerBeePattern = 2 /*random.nextInt(3)*/;
//        BugSpawnSystem.break_counter = 1;
        GameScreenScript.cameraShaker.initShaking(7f, 0.9f);
        BugSpawnSystem.queenBeeOnStage = false;

//        BugSystem.blowUpAllBugs = true;
//        BugSystem.blowUpCounter = GlobalConstants.BEES_MODE_BLOW_UP_LENGTH;
        BugSystem.blowUpAllBugs();
        beesModeAni.getComponent(TransformComponent.class).y = 394;
        beesModeAni.getComponent(SpriterComponent.class).player.setAnimation(0);
        beesModeAni.getComponent(SpriterComponent.class).player.speed = 26;
        beesModeAni.getComponent(SpriterComponent.class).player.setTime(0);

        BugSpawnSystem.resetBreakCounter();
        BugSpawnSystem.break_counter = BugSpawnSystem.rand.nextInt((int) (BugSpawnSystem.curBreakFreqMax * 100) - (int) (BugSpawnSystem.curBreakFreqMin * 100)) + (BugSpawnSystem.curBreakFreqMin * 100);
        BugSpawnSystem.break_counter /= 100;
        System.out.println("angerBees() + break_counter: " + BugSpawnSystem.break_counter);
    }

    private void updateAngeredBeesMode() {
        if (isAngeredBeesMode) {
            angeredBeesModeTimer--;
            //stopMenu ani when it's finished
            if (beesModeAni.getComponent(SpriterComponent.class).player.getTime() != 0 &&
                    beesModeAni.getComponent(SpriterComponent.class).player.getTime() %
                            beesModeAni.getComponent(SpriterComponent.class).player.getAnimation().length == 0) {
                beesModeAni.getComponent(SpriterComponent.class).player.speed = 0;
//                BugSystem.blowUpAllBugs = false;
//                System.out.println("updateAngeredBeesMode(): " + BugSystem.blowUpAllBugs);
            }
            if (angeredBeesModeTimer <= 0) {
                isAngeredBeesMode = false;

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

//                    if (gameStage.shopScript.allSoftItems.indexOf(u) >= 0) {
//                        gameStage.shopScript.allSoftItems.get(gameStage.shopScript.allSoftItems.indexOf(u)).bought = false;
//                        gameStage.shopScript.allSoftItems.get(gameStage.shopScript.allSoftItems.indexOf(u)).enabled = false;
//                    }
                }
            }
        }
    }

    public void usePhoenix() {
        ActionComponent ac2 = new ActionComponent();
        ac2.dataArray.add(Actions.sequence(
                Actions.delay(1f),
                Actions.moveTo(1100, 58, 1f, Interpolation.exp5)));
        phoenixIcon.add(ac2);
    }

    @Override
    public void init(Entity item) {

        gameOverReviveTimesLimit = 2;
        gameItem = new ItemWrapper(item);
        projectileBounds = new LinkedList<>();

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
        curtainGameE = gameItem.getChild(CURTAIN_GAME).getEntity();
        curtainGameE.getComponent(TintComponent.class).color.a = 0;

        scoreCE = gameItem.getChild(SCOREC).getEntity();
        scoreLabelE = scoreCE.getComponent(NodeComponent.class).getChild(LBL_SCORE);
        LabelComponent scoreLabel = scoreLabelE.getComponent(LabelComponent.class);
        scoreLabel.text.replace(0, scoreLabel.text.capacity(), "" + fpc.score);

        scoreLabelEsh = scoreCE.getComponent(NodeComponent.class).getChild(LBL_SCORE_SH);
        LabelComponent scoreLabelsh = scoreLabelEsh.getComponent(LabelComponent.class);
        scoreLabelsh.text.replace(0, scoreLabelsh.text.capacity(), "" + fpc.score);

        Entity startLabel = gameItem.getChild(LBL_TAP_2_START).getEntity();
        startLabel.getComponent(TintComponent.class).color.a = 0;
        startLabel.getComponent(TransformComponent.class).scaleX = 0.1f;
        startLabel.getComponent(TransformComponent.class).scaleY = 0.1f;
        startLabelComponent = startLabel.getComponent(LabelComponent.class);
        startLabelComponent.text.replace(0, startLabelComponent.text.capacity(), START_MESSAGE);

        addSystems();
        initFlower(gameItem.getChild(MEGA_FLOWER).getEntity(), gameItem.getChild(MEGA_LEAVES).getEntity());
//        initPet();
        initDoubleBJIcon();
        initPhoenixIcon();
        initUmbrella();
        initCocoon();

        checkTryPeriod();

        gameStage.gameScript.fpc.settings.totalPlayedGames++;
        gameStage.gameScript.fpc.settings.playedGames++;
        isAngeredBeesMode = false;

        changePet = true;
        BackgroundMusicMgr.getBackgroundMusicMgr().playGame();
    }

    public void initStartTrans() {
        FlowerComponent.state = FlowerComponent.State.IDLE;

        gameItem.getChild(BTN_PAUSE).getEntity().getComponent(TransformComponent.class).x -= 200;
        gameItem.getChild(BTN_PAUSE).getEntity().getComponent(TintComponent.class).color.a = 0;
        ActionComponent ac = new ActionComponent();
        ac.dataArray.add(Actions.sequence(
                Actions.delay(1f),
                Actions.parallel(Actions.fadeIn(2f, Interpolation.exp10, 0.5f),
                        Actions.moveTo(gameItem.getChild(BTN_PAUSE).getEntity().getComponent(TransformComponent.class).x + 200,
                                gameItem.getChild(BTN_PAUSE).getEntity().getComponent(TransformComponent.class).y, 2f, Interpolation.exp10))));

        if (gameItem.getChild(BTN_BACK) != null) {
            gameItem.getChild(BTN_BACK).getEntity().getComponent(TransformComponent.class).x -= 200;
        }

        ActionComponent ac2 = new ActionComponent();
        ac2.dataArray.add(Actions.sequence(
                Actions.delay(1f),
                Actions.parallel(Actions.fadeIn(2f, Interpolation.exp10, 0.5f),
                        Actions.moveTo(gameItem.getChild(BTN_BACK).getEntity().getComponent(TransformComponent.class).x + 200,
                                gameItem.getChild(BTN_BACK).getEntity().getComponent(TransformComponent.class).y, 2f, Interpolation.exp10))));

        gameItem.getChild(BTN_PAUSE).getEntity().add(ac);
        if (gameItem.getChild(BTN_BACK) != null) {
            gameItem.getChild(BTN_BACK).getEntity().add(ac2);
        }

        gameItem.getChild("tutorial_line").getEntity().getComponent(TintComponent.class).color.a = 0;
        ActionComponent ac3 = new ActionComponent();
        ac3.dataArray.add(Actions.sequence(
                Actions.delay(1f),
                Actions.fadeIn(2f, Interpolation.exp5, 0.5f)));
        gameItem.getChild("tutorial_line").getEntity().add(ac3);

        gameItem.getChild(LBL_TAP_2_START).getEntity().getComponent(TintComponent.class).color.a = 0;
        ActionComponent ac4 = new ActionComponent();
        ac4.dataArray.add(Actions.sequence(
                Actions.delay(5f),
                Actions.parallel(Actions.fadeIn(1f, Interpolation.exp5Out), Actions.scaleTo(1f, 1f, 1f, Interpolation.exp5Out))));
        gameItem.getChild(LBL_TAP_2_START).getEntity().add(ac4);

        ActionComponent ac5 = new ActionComponent();
        ac5.dataArray.add(Actions.sequence(
                Actions.delay(1f),
                Actions.fadeIn(2f, Interpolation.exp5)));
        scoreLabelE.add(ac5);
        scoreLabelEsh.add(ac5);
    }

    public void initButtons() {
        initPauseBtn();
        pauseDialog = null;
        gameOverDialog = null;
        initBackButton();
        curtainGameE.getComponent(TintComponent.class).color.a = 0;
    }

    public void reset() {
        fpc.score = 0;
        isAngeredBeesMode = false;
        scoreLabelE.getComponent(LabelComponent.class).text.replace(0,
                scoreLabelE.getComponent(LabelComponent.class).text.capacity(), "" + fpc.score);
        scoreLabelE.getComponent(TintComponent.class).color.a = 0;
        scoreLabelEsh.getComponent(LabelComponent.class).text.replace(0,
                scoreLabelEsh.getComponent(LabelComponent.class).text.capacity(), "" + fpc.score);
        scoreLabelEsh.getComponent(TintComponent.class).color.a = 0;
        startLabelComponent.text.replace(0, startLabelComponent.text.capacity(), START_MESSAGE);

        loseFeedback.getComponent(TintComponent.class).color.a = 0;
        curtainGameE.getComponent(TintComponent.class).color.a = 0.99f;
        if (curtainGameE.getComponent(ActionComponent.class) == null) {
            curtainGameE.add(new ActionComponent());
        }
        curtainGameE.getComponent(ActionComponent.class).reset();
        curtainGameE.getComponent(ActionComponent.class).dataArray.add(Actions.fadeOut(0.4f));

        initDoubleBJIcon();
        initPhoenixIcon();

        CocoonSystem.resetSpawnCoefficients();
        cocoonSpawnCounter = CocoonSystem.getNextSpawnInterval();
        umbrellaSpawnCounter = UmbrellaSystem.getNextSpawnInterval();

        if (changePet) {
            hideCurrentPet();
            initPet();
            changePet = false;
        }

        megaFlower.getComponent(TransformComponent.class).y = FLOWER_Y_POS;
        megaFlower.getComponent(TransformComponent.class).x = FLOWER_X_POS;
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
        phoenixIcon = gameItem.getChild(PHOENIX_ICON).getEntity();
        if (gameStage.gameScript.fpc.havePhoenix()) {
            TransformComponent tc = phoenixIcon.getComponent(TransformComponent.class);
            tc.x = -24;
            tc.y = 637;
        } else {
            phoenixIcon.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        }
    }

    private void addSystems() {
        gameStage.sceneLoader.getEngine().addSystem(new UmbrellaSystem(gameStage));
        gameStage.sceneLoader.getEngine().addSystem(new LeafsSystem());
        gameStage.sceneLoader.getEngine().addSystem(new ButterflySystem(gameStage));
        gameStage.sceneLoader.getEngine().addSystem(new FlowerSystem(gameStage));
        gameStage.sceneLoader.getEngine().addSystem(new BugSystem(gameStage));
        gameStage.sceneLoader.getEngine().addSystem(new BugJuiceBubbleSystem(gameStage));
        gameStage.sceneLoader.getEngine().addSystem(new PetProjectileSystem(gameStage));
        gameStage.sceneLoader.getEngine().addSystem(new ParticleLifespanSystem());
        gameStage.sceneLoader.getEngine().addSystem(new PetSystem(gameStage));
        gameStage.sceneLoader.getEngine().addSystem(new CocoonSystem(this));
        gameStage.sceneLoader.getEngine().addSystem(new BugSpawnSystem(gameStage));
//        gameStage.sceneLoader.getEngine().addSystem(new DebugSystem(gameStage));
    }

    private void initBackButton() {
        Entity backMenuBtn = gameItem.getChild(BTN_BACK).getEntity();

        backMenuBtn.getComponent(ButtonComponent.class)
                .addListener(new ImageButtonListener(backMenuBtn, new AtomicBoolean[]{isGameOver, isPause}) {
                    @Override
                    public void clicked() {
                        if (!isPause.get() && !isGameOver.get()) {
                            resetPauseDialog();
                            megaFlower.getComponent(SpriterComponent.class).player.setTime(0);
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
                            PauseDialog.pauseUpdate = false;
                        }
                    }
                });
    }

    private void initLeafs(Entity leaves) {
        this.megaLeaves = leaves;

        TransformComponent tcL = megaLeaves.getComponent(TransformComponent.class);
        tcL.x = LEAFS_X_POS;
        tcL.y = LEAFS_Y_POS;
        tcL.scaleX = LEAFS_SCALE;
        tcL.scaleY = LEAFS_SCALE;

        megaLeaves.getComponent(SpriterComponent.class).scale = LEAFS_SCALE;

    }

    public Entity getMegaLeaves() {
        return megaLeaves;
    }

    public void initFlower(Entity flower, Entity leaves) {
        if (flower != null) {
            initFlower(flower);
        }

        if (leaves != null) {
            initLeafs(leaves);
        }
    }

    private void initFlower(Entity flower) {
        this.megaFlower = flower;
        gameStage.gameScript.fpc.score = 0;

        TransformComponent tc = megaFlower.getComponent(TransformComponent.class);
        tc.x = FLOWER_X_POS;
        tc.y = FLOWER_Y_POS;
        tc.scaleX = FLOWER_SCALE;
        tc.scaleY = FLOWER_SCALE;
        megaFlower.add(tc);

        FlowerComponent fc = new FlowerComponent();

//        System.out.println("currentFlowerFrame: " + currentFlowerFrame);
        megaFlower.getComponent(SpriterComponent.class).player.setTime(currentFlowerFrame);
//        System.out.println("megaFlower.getComponent(SpriterComponent.class).player: " + megaFlower.getComponent(SpriterComponent.class).player.getTime());

        gameItem.getChild(TUTORIAL_LINE).getEntity().getComponent(TintComponent.class).color.a = 0.6f;
        gameItem.getChild(TUTORIAL_LINE).getEntity().getComponent(TransformComponent.class).x = 975;

        megaFlower.add(fc);
        megaFlower.add(fpc);
    }

    public void hideCurrentPet() {
        if (gameStage.gameScript.fpc.currentPet != null) {
//            gameStage.gameScript.fpc.currentPet.disable();
            if (petE != null && petE.getComponent(TransformComponent.class) != null) {
                petE.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
                petE.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
                gameStage.sceneLoader.getEngine().removeEntity(petE);
            }
            if (gameStage.gameScript.fpc.currentPet.petHead != null &&
                    gameStage.gameScript.fpc.currentPet.petHead.getComponent(TransformComponent.class)!= null) {
                gameStage.gameScript.fpc.currentPet.petHead.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
                gameStage.gameScript.fpc.currentPet.petCannon.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
                gameStage.sceneLoader.getEngine().removeEntity(gameStage.gameScript.fpc.currentPet.petCannon);
                gameStage.sceneLoader.getEngine().removeEntity(gameStage.gameScript.fpc.currentPet.petHead);
            }
        }
    }

    public void initPet() {
//        hideCurrentPet();
        if (fpc.currentPet != null && fpc.currentPet.enabled) {
            fpc.currentPet.gameStage = gameStage;
            loadPetFromLib();
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
        gameStage.sceneLoader.rm.addSpriterToLoad(fpc.currentPet.name.toLowerCase());
        CompositeItemVO tempItemC = gameStage.sceneLoader.loadVoFromLibrary(fpc.currentPet.name.toLowerCase());
        petE = gameStage.sceneLoader.entityFactory.createSPRITERentity(gameStage.sceneLoader.getRoot(), tempItemC);
        gameStage.sceneLoader.getEngine().addEntity(petE);
    }

    @Override
    public void dispose() {
    }

    @Override
    public void act(float delta) {
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
            if(phoenixIcon.getComponent(TransformComponent.class).x >= 1100) {
                phoenixIcon.getComponent(TransformComponent.class).x = -200;
                loseFeedback.getComponent(TransformComponent.class).x = -600;
                gameStage.gameScript.fpc.upgrades.get(Upgrade.UpgradeType.PHOENIX).usePhoenix();
                System.out.println("IT'S VERY UNLIKELY THAT I AM CALLED MORE THAN ONCE");
//                System.out.println("Phoenix!");
                isPause.set(false);
            }
        }
        if(BugSystem.blowUpAllBugs){
            BugSystem.blowUpCounter -= delta;
            BugSystem.destroyAllBugsCounter -= delta;

            if( BugSystem.blowUpCounter >= 0) {
                BugSystem.destroyAllBugsCounter = BEES_MODE_DESTROY_LENGTH;
            }

            if (BugSystem.blowUpCounter <= 0 && BugSystem.destroyAllBugsCounter <= 0) {
                BugSystem.blowUpAllBugs = false;
                BugSystem.destroyAllBugsCounter = BEES_MODE_DESTROY_LENGTH;
                System.out.println("let's put it there!");
            }

            System.out.println("destroyAllBugsCounter: " + BugSystem.destroyAllBugsCounter + " blowUpCounter: " + BugSystem.blowUpCounter + " delta: " + delta);
        }
    }

    private void updateTapGoal() {
        if (fpc.level.getGoalByType(Goal.GoalType.TAP) != null) {
            fpc.level.getGoalByType(Goal.GoalType.TAP).update();
        }
    }

    public void onBugOutOfBounds() {
        FlowerComponent.state = FlowerComponent.State.LOSING;

//        if (fpc.canUsePhoenix()) {
//            usePhoenix();
//        } else {

            loseFeedback.getComponent(TransformComponent.class).scaleX = 0.5f;
            loseFeedback.getComponent(TransformComponent.class).x = 950;
            loseFeedback.getComponent(ZIndexComponent.class).setZIndex(1200);

            ActionComponent ac = loseFeedback.getComponent(ActionComponent.class);
            if (ac == null) {
                ac = gameStage.sceneLoader.engine.createComponent(ActionComponent.class);
                Actions.checkInit();
                loseFeedback.add(ac);
            }

            ac.dataArray.add(Actions.fadeIn(0.2f));
            ac.dataArray.add(Actions.scaleTo(1.5f, 1.5f, 0.2f));
            ac.dataArray.add(Actions.moveTo(742, loseFeedback.getComponent(TransformComponent.class).y, 0.2f));

//            FlowerComponent.state = FlowerComponent.State.LOSING;
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
            if (GoalFeedbackScreen.shouldShow && !GoalFeedbackScreen.isGoalFeedbackOpen) {
                showGoalFeedback();
                isGameOver.set(true);
            } else if ((gameStage.gameScript.goalFeedbackScreen == null || !GoalFeedbackScreen.isGoalFeedbackOpen)) {
                isGameOver.set(false);
                gameStage.gameScript.resetPauseDialog();
                submitScoreToGooglePlay();
                gameStage.gameScript.gameOverDialog.hide();
                megaFlower.getComponent(SpriterComponent.class).player.setTime(0);

                gameStage.initResultWithAds();
            }
        }
    }

    private void submitScoreToGooglePlay() {
        if (Main.mainController.isWifiConnected() && Main.mainController.isSignedIn()) {
            Main.mainController.submitScore(fpc.score);
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
        scoreLabelEsh.getComponent(LabelComponent.class).text.replace(0,
                scoreLabelEsh.getComponent(LabelComponent.class).text.capacity(),     // real look alike
                "" + fcc.score);
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

    @Override
    public Entity getMegaFlower() {
        return megaFlower;
    }
}
