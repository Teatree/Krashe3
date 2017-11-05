package com.fd.etf.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.fd.etf.Main;
import com.fd.etf.entity.componets.*;
import com.fd.etf.entity.componets.listeners.ImageButtonListener;
import com.fd.etf.stages.ui.GameOverDialog;
import com.fd.etf.stages.ui.GoalFeedbackScreen;
import com.fd.etf.stages.ui.PauseDialog;
import com.fd.etf.system.*;
import com.fd.etf.utils.*;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.fd.etf.entity.componets.FlowerComponent.*;
import static com.fd.etf.entity.componets.Goal.GoalType.SURVIVE_N_ANGERED_MODES;
import static com.fd.etf.entity.componets.LeafsComponent.*;
import static com.fd.etf.entity.componets.PetComponent.State.SPAWNING;
import static com.fd.etf.utils.GlobalConstants.*;

public class GameScreenScript implements IScript, GameStage.IhaveFlower {

    public static final CameraShaker cameraShaker = new CameraShaker();
    public static final String TUTORIAL_LINE = "tutorial_line";
    private static final String LOSE_FEEDBACK = "lose_feedback";
    private static final String DOUBLE_BJ_ICON = "double_bj_badge";
    private static final String PHOENIX_ICON = "extra_life_badge";
    private static final String LBL_SCORE = "lbl_score";
    private static final String LBL_SCORE_SH = "lbl_score_sh";
    private static final String SCOREC = "scoreC";
    private static final String LBL_TAP_2_START = "lbl_tap2start";
    private static final String BTN_PAUSE = "btn_pause";
    private static final String MEGA_FLOWER = "mega_flower";
    private static final String MEGA_LEAVES = "mega_leafs";
    private static final String BTN_BACK = "btn_back";
    private static final String BEES_MODE_ANI = "bees_mode_ani";
    private static final String CURTAIN_GAME = "curtain_game";
    private static final String BACKGROUND_SHIT = "background_shit";
    private static final String GAME_OVER_LBL = "lbl_gameover";

    private static final String BUGS_SPAWNED_LBL = "bugs_spawnd";

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
    public Entity backgroundShitE;
    public LabelComponent startLabelComponent;
    public static int currentFlowerFrame;
    public GoalFeedbackScreen goalFeedbackScreen;
    public static List<Rectangle> projectileBounds;

    public ItemWrapper gameItem;
    public GameOverDialog gameOverDialog;
    private PauseDialog pauseDialog;
    public Entity pauseBtn;
    public static int gameOverReviveTimesLimit;
    public boolean wasGameOverReviveShown;

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

    public static PowerupSystem powerupSystem;
    private Entity bugsSpwndE;
    private LabelComponent bugsSpwndELabel;

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
        //System.out.println("angerBees() + break_counter: " + BugSpawnSystem.break_counter);
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
                Actions.delay(0.5f),
                Actions.parallel(Actions.moveTo(1000, 99, 1f, Interpolation.exp5), Actions.fadeOut(1.2f))));
        phoenixIcon.add(ac2);
    }

    @Override
    public void init(Entity item) {

        if (!fpc.isSameDay()) {
            gameOverReviveTimesLimit = fpc.settings.reviveAd_max;
        }

        gameItem = new ItemWrapper(item);
        projectileBounds = new LinkedList<>();

        powerupSystem = new PowerupSystem(gameStage, gameItem);

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
        powerupSystem.resetCounters();

        fpc.score = 0;
        curtainGameE = gameItem.getChild(CURTAIN_GAME).getEntity();
        curtainGameE.getComponent(TintComponent.class).color.a = 0;

        scoreCE = gameItem.getChild(SCOREC).getEntity();
        scoreLabelE = scoreCE.getComponent(NodeComponent.class).getChild(LBL_SCORE);
        LabelComponent scoreLabel = scoreLabelE.getComponent(LabelComponent.class);
        scoreLabel.text.replace(0, scoreLabel.text.capacity(), " ");

        scoreLabelEsh = scoreCE.getComponent(NodeComponent.class).getChild(LBL_SCORE_SH);
        LabelComponent scoreLabelsh = scoreLabelEsh.getComponent(LabelComponent.class);
        scoreLabelsh.text.replace(0, scoreLabelsh.text.capacity(), " ");

        bugsSpwndE = gameItem.getChild(BUGS_SPAWNED_LBL).getEntity();
        bugsSpwndELabel = bugsSpwndE.getComponent(LabelComponent.class);
        bugsSpwndELabel.text.replace(0, bugsSpwndELabel.text.capacity(), " ");

        Entity startLabel = gameItem.getChild(LBL_TAP_2_START).getEntity();
        startLabel.getComponent(TintComponent.class).color.a = 0;
        startLabel.getComponent(TransformComponent.class).scaleX = 0.1f;
        startLabel.getComponent(TransformComponent.class).scaleY = 0.1f;
        startLabelComponent = startLabel.getComponent(LabelComponent.class);

        addSystems();
        initFlower(gameItem.getChild(MEGA_FLOWER).getEntity(), gameItem.getChild(MEGA_LEAVES).getEntity());
//        initPet();
        initDoubleBJIcon();
        initPhoenixIcon();
        powerupSystem.initUmbrella();
        powerupSystem.initCocoon();

        checkTryPeriod();

        backgroundShitE = gameItem.getChild(BACKGROUND_SHIT).getEntity();

        gameStage.gameScript.fpc.settings.playedGames++;
        isAngeredBeesMode = false;

        gameItem.getChild(PauseDialog.PAUSETIMER_C).getEntity().getComponent(TintComponent.class).color.a = 0;

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
            // BACK BUTTON WILL NOT RETURN UNLESS THIS
//            gameItem.getChild(BTN_BACK).getEntity().getComponent(TransformComponent.class).x -= 200;
        }

        ActionComponent ac2 = new ActionComponent();
        ac2.dataArray.add(Actions.sequence(
                Actions.delay(1f),
                Actions.parallel(Actions.fadeIn(2f, Interpolation.exp10, 0.5f),
                        Actions.moveTo(gameItem.getChild(BTN_BACK).getEntity().getComponent(TransformComponent.class).x + 200,
                                gameItem.getChild(BTN_BACK).getEntity().getComponent(TransformComponent.class).y, 2f, Interpolation.exp10))));

        gameItem.getChild(BTN_PAUSE).getEntity().add(ac);
        if (gameItem.getChild(BTN_BACK) != null) {
            // AND THIS
//            gameItem.getChild(BTN_BACK).getEntity().add(ac2);
        }

        gameItem.getChild(TUTORIAL_LINE).getEntity().getComponent(TintComponent.class).color.a = 0;
        ActionComponent ac3 = new ActionComponent();
        ac3.dataArray.add(Actions.sequence(
                Actions.delay(1f),
                Actions.fadeIn(2f, Interpolation.exp5, 0.5f)));
        gameItem.getChild(TUTORIAL_LINE).getEntity().add(ac3);

        if (!isStarted) {
            setTapToPlayAnimations();
        }

        ActionComponent ac5 = new ActionComponent();
        ac5.dataArray.add(Actions.sequence(
                Actions.delay(1f),
                Actions.fadeIn(2f, Interpolation.exp5)));
        scoreLabelE.add(ac5);
        scoreLabelEsh.add(ac5);
    }

    private void setTapToPlayAnimations() {
        gameItem.getChild(LBL_TAP_2_START).getEntity().getComponent(TintComponent.class).color.a = 0;
        ActionComponent ac4 = new ActionComponent();
        ac4.dataArray.add(Actions.sequence(
                Actions.delay(2f),
                Actions.parallel(Actions.fadeIn(1f, Interpolation.exp5Out), Actions.scaleTo(1f, 1f, 1f, Interpolation.exp5Out))));
        gameItem.getChild(LBL_TAP_2_START).getEntity().add(ac4);
    }

    public void initButtons() {
        initPauseBtn();
        pauseDialog = null;
        gameOverDialog = null;
        //initBackButton();
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

        loseFeedback.getComponent(TintComponent.class).color.a = 0;
        curtainGameE.getComponent(TintComponent.class).color.a = 0.99f;
        cleanupTheScene();
        if (curtainGameE.getComponent(ActionComponent.class) == null) {
            curtainGameE.add(new ActionComponent());
        }
        curtainGameE.getComponent(ActionComponent.class).reset();
        curtainGameE.getComponent(ActionComponent.class).dataArray.add(Actions.fadeOut(0.4f));
        BugSpawnSystem.bugsSpawned = 0;
        BugSpawnSystem.umbrellaBugsSpawned = 0;
        BugSpawnSystem.cocconBugsSpawned = 0;
        BugSpawnSystem.resetMultipliers();

        wasGameOverReviveShown = false;

        initDoubleBJIcon();
        initPhoenixIcon();

        CocoonSystem.resetSpawnCoefficients();
        powerupSystem.resetCounters();

        if (changePet) {
            hideCurrentPet();
            initPet();
            changePet = false;
        }

        if (fpc.upgrades.get(Upgrade.UpgradeType.PHOENIX) != null) {
            fpc.upgrades.get(Upgrade.UpgradeType.PHOENIX).counter = 0;
        }

        megaFlower.getComponent(TransformComponent.class).y = FLOWER_Y_POS;
        megaFlower.getComponent(TransformComponent.class).x = FLOWER_X_POS;

        if (!isStarted) {
            setTapToPlayAnimations();
        }

        if(pauseDialog != null){
            pauseDialog.reset();
        }
    }

    private void initDoubleBJIcon() {
        Entity bjIcon = gameItem.getChild(DOUBLE_BJ_ICON).getEntity();
        if (gameStage.gameScript.fpc.haveBugJuiceDouble()) {
            TransformComponent tc = bjIcon.getComponent(TransformComponent.class);
                tc.x = 0;
                tc.y = 640;
            bjIcon.getComponent(ZIndexComponent.class).setZIndex(150);
        } else {
            bjIcon.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        }
    }

    private void initPhoenixIcon() {
        phoenixIcon = gameItem.getChild(PHOENIX_ICON).getEntity();
        if (gameStage.gameScript.fpc.havePhoenix()) {
            TransformComponent tc = phoenixIcon.getComponent(TransformComponent.class);
            if (gameStage.gameScript.fpc.haveBugJuiceDouble()) {
                tc.x = 105;
                tc.y = 663;
            } else {
                tc.x = 0;
                tc.y = 663;
            }
            phoenixIcon.getComponent(ZIndexComponent.class).setZIndex(150);
            phoenixIcon.getComponent(TintComponent.class).color.a = 1;
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
    }

    private void initBackButton() {
        Entity backMenuBtn = gameItem.getChild(BTN_BACK).getEntity();

        backMenuBtn.getComponent(ButtonComponent.class)
                .addListener(new ImageButtonListener(backMenuBtn, new AtomicBoolean[]{isGameOver, isPause}) {
                    @Override
                    public void clicked() {
                        if (!isPause.get() && !isGameOver.get()) {
                            backToMenu();
                        }
                    }
                });
    }

    public void backToMenu() {
        resetPauseDialog();
        loseFeedback.getComponent(TransformComponent.class).x = -600;
        megaFlower.getComponent(SpriterComponent.class).player.setTime(0);
        cleanupTheScene();
        gameStage.initMenu();
    }

    public void resetPauseDialog() {
        if (pauseDialog != null) {
            gameItem.getChild(PauseDialog.PAUSETIMER_C).getEntity().getComponent(NodeComponent.class).getChild(PauseDialog.LBL_PAUSE_TIMER).getComponent(LabelComponent.class).text.replace(0, 1, "");
            gameItem.getChild(PauseDialog.PAUSETIMER_C).getEntity().getComponent(TintComponent.class).color.a = 1;
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
                            pauseGame();
                        }
                    }
                });
    }

    public void pauseGame() {
        isPause.set(true);
        if (pauseDialog == null) {
            pauseDialog = new PauseDialog(gameStage, gameItem);
            pauseDialog.init();
        }
        pauseDialog.show();
        PauseDialog.pauseUpdate = false;
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
                    gameStage.gameScript.fpc.currentPet.petHead.getComponent(TransformComponent.class) != null) {
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
                setInitialPetState();
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

    private void setInitialPetState() {
        fpc.currentPet.init();

        TransformComponent tc = petE.getComponent(TransformComponent.class); //TODO: NPE
        tc.x = PetComponent.X_SPAWN_POSITION;
        tc.y = PetComponent.getNewPositionY();
        tc.scaleX = 1.3f;
        tc.scaleY = 1.3f;

        fpc.currentPet.petCannon.getComponent(TransformComponent.class).x = tc.x + 64;
        fpc.currentPet.petCannon.getComponent(TransformComponent.class).y = tc.y - 9;
        fpc.currentPet.petCannon.getComponent(ZIndexComponent.class).setZIndex(127);
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
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
//            SaveMngr.saveStats(gameStage.gameScript.fpc);
            backToMenu();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.MENU)) {
//            SaveMngr.saveStats(gameStage.gameScript.fpc);
            pauseGame();
        }

        if (!GameStage.justCreated) {
            if (cameraShaker.time > 0) {
                cameraShaker.shake(delta);
            }

            if (!isStarted && Gdx.input.justTouched()) {
                gameItem.getChild(LBL_TAP_2_START).getEntity().getComponent(LabelComponent.class).text.replace(0, startLabelComponent.text.capacity(), "");
                isStarted = true;

                updateTapGoal();
            }

            if (!isPause.get() && !isGameOver.get() && isStarted) {
                powerupSystem.update(delta);
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
            if (phoenixIcon.getComponent(TransformComponent.class).x >= 900 && gameStage.gameScript.fpc.upgrades.get(Upgrade.UpgradeType.PHOENIX) != null && gameStage.gameScript.fpc.upgrades.get(Upgrade.UpgradeType.PHOENIX).enabled) {
                phoenixIcon.getComponent(TransformComponent.class).x = -200;
                loseFeedback.getComponent(TransformComponent.class).x = -600;
                gameStage.gameScript.fpc.upgrades.get(Upgrade.UpgradeType.PHOENIX).usePhoenix();
                //System.out.println("IT'S VERY UNLIKELY THAT I AM CALLED MORE THAN ONCE");
//                System.out.println("Phoenix!");
                isPause.set(false);
            }

            bugsSpwndELabel.text.replace(0, bugsSpwndELabel.text.capacity(), "bugs spawned:" + BugSpawnSystem.bugsSpawned +
                            "\n" + "spawnInterval " + BugSpawnSystem.curSpawnInterval +
                            "\n" + "curBreakFreqMin " + BugSpawnSystem.curBreakFreqMin +
                            "\n" + "curBreakFreqMax " + BugSpawnSystem.curBreakFreqMax +
                            "\n" + "curBreakLengthMin " + BugSpawnSystem.curBreakLengthMin +
                            "\n" + "curBreakLengthMax " + BugSpawnSystem.curBreakLengthMax +
                            "\n" + "curSimpleProb " + BugSpawnSystem.curSimpleProb +
                            "\n" + "curDrunkProb " + BugSpawnSystem.curDrunkProb +
                            "\n" + "curChargerProb " + BugSpawnSystem.curChargerProb +
                            "\n" + "curQueenBeeProb " + BugSpawnSystem.curQueenBeeProb +
                            "\n" + "curBeeProb " + BugSpawnSystem.curBeeProb +
                            "\n" + "cocoonSpawnCounter " + PowerupSystem.cocoonSpawnCounter +
                            "\n" + "cocconBugsSpawned " + BugSpawnSystem.cocconBugsSpawned +
                            "\n" + "umbrellaSpawnCounter " + PowerupSystem.umbrellaSpawnCounter +
                            "\n" + "umbrellaBugsSpawned " + BugSpawnSystem.umbrellaBugsSpawned
            );

        }
        if (BugSystem.blowUpAllBugs) {
            BugSystem.blowUpCounter -= delta;
            BugSystem.destroyAllBugsCounter -= delta;

            if (BugSystem.blowUpCounter >= 0) {
                BugSystem.destroyAllBugsCounter = BEES_MODE_DESTROY_LENGTH;
            }

            if (BugSystem.blowUpCounter <= 0 && BugSystem.destroyAllBugsCounter <= 0) {
                BugSystem.blowUpAllBugs = false;
                BugSystem.destroyAllBugsCounter = BEES_MODE_DESTROY_LENGTH;
//                System.out.println("let's put it there!");
            }

        }

        if (fpc.score == 0) {
            scoreLabelE.getComponent(LabelComponent.class).text.replace(0,
                    scoreLabelE.getComponent(LabelComponent.class).text.capacity(),     // real look alike
                    "");
            scoreLabelEsh.getComponent(LabelComponent.class).text.replace(0,
                    scoreLabelEsh.getComponent(LabelComponent.class).text.capacity(),     // real look alike
                    "");
        }

        backgroundShitE.getComponent(ZIndexComponent.class).setZIndex(megaFlower.getComponent(ZIndexComponent.class).getZIndex() + 1);
    }

    private void updateTapGoal() {
        if (fpc.level.getGoalByType(Goal.GoalType.TAP) != null) {
            fpc.level.getGoalByType(Goal.GoalType.TAP).update();
        }
    }

    public void onBugOutOfBounds() {
        FlowerComponent.state = FlowerComponent.State.LOSING;
        FlowerComponent.isLosing = true;

//        if (fpc.canUsePhoenix()) {
//            usePhoenix();
//        } else {

        loseFeedback.getComponent(TransformComponent.class).scaleX = 0.5f;
        loseFeedback.getComponent(TransformComponent.class).x = 950;
        loseFeedback.getComponent(ZIndexComponent.class).setZIndex(1200);

        ActionComponent ac = loseFeedback.getComponent(ActionComponent.class);
        if (ac == null) {
            ac = new ActionComponent();
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
        shouldShowGameOverDialog = gameOverReviveTimesLimit > 0 && !wasGameOverReviveShown;

        if (shouldShowGameOverDialog) {
            showGameOverDialog();
        } else {
//            isGameOver.set(false);

//            if (curtainGameE.getComponent(ActionComponent.class) != null) {
//                curtainGameE.getComponent(ActionComponent.class).reset();
//            }
            gameStage.gameScript.fpc.totalScore += gameStage.gameScript.fpc.score;
            //System.out.println("gameScreenScript gameStage.gameScript.fpc.totalScore: " + gameStage.gameScript.fpc.totalScore);
            //System.out.println("gameScreenScript gameStage.gameScript.fpc.score: " + gameStage.gameScript.fpc.score);
            ActionComponent ac = new ActionComponent();
            ac.dataArray.add(Actions.sequence(
                    Actions.fadeIn(1f, Interpolation.exp5, 0.5f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            gameStage.initResultWithAds();
                        }
                    })));

//            ac.dataArray.add(Actions.fadeIn(1f, Interpolation.exp5, 0.5f));
//            curtainGameE.add(ac);
//            curtainGameE.getComponent(ZIndexComponent.class).setZIndex(150);
//            ActionComponent ac2 = new ActionComponent();
//            ac2.dataArray.add(Actions.sequence(
//                    Actions.parallel(Actions.moveTo(305, 352, 0.7f, Interpolation.exp5), Actions.fadeIn(0.7f)),
//                    Actions.delay(1.5f),
//                    Actions.run(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (curtainGameE.getComponent(ActionComponent.class) != null) {
//                                curtainGameE.getComponent(ActionComponent.class).reset();
//                            }
//                            gameItem.getChild(GAME_OVER_LBL).getEntity().getComponent(ZIndexComponent.class).setZIndex(curtainGameE.getComponent(ZIndexComponent.class).getZIndex() - 1);
//                            ActionComponent ac3 = new ActionComponent();
//                            ac3.dataArray.add(Actions.fadeIn(0.3f));
//                            curtainGameE.add(ac3);
//                        }
//                    }),
//                    Actions.delay(0.3f),
//                    Actions.run(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (GoalFeedbackScreen.shouldShow && !GoalFeedbackScreen.isGoalFeedbackOpen) {
//                                showGoalFeedback();
//                                isGameOver.set(true);
//                            } else if ((gameStage.gameScript.goalFeedbackScreen == null || !GoalFeedbackScreen.isGoalFeedbackOpen)) {
//                                isGameOver.set(false);
//                                gameStage.gameScript.resetPauseDialog();
//                                submitScoreToGooglePlay();
//                                gameStage.gameScript.gameOverDialog.hide();
//                                megaFlower.getComponent(SpriterComponent.class).player.setTime(0);
//
//                                cleanupTheScene();
//                                gameItem.getChild(GAME_OVER_LBL).getEntity().getComponent(TransformComponent.class).y = -108;
//                            }
//                        }
//                    })));
//            if (gameItem.getChild(GAME_OVER_LBL).getEntity().getComponent(ActionComponent.class) != null) {
//                gameItem.getChild(GAME_OVER_LBL).getEntity().getComponent(ActionComponent.class).reset();
//            }
//            gameItem.getChild(GAME_OVER_LBL).getEntity().add(ac2);
//            gameItem.getChild(GAME_OVER_LBL).getEntity().getComponent(ZIndexComponent.class).setZIndex(curtainGameE.getComponent(ZIndexComponent.class).getZIndex() + 1);


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
        wasGameOverReviveShown = true;
        gameOverDialog.show();
    }

    public void reloadScoreLabel(FlowerPublicComponent fcc) {
        if (fpc.score > 0) {
            scoreLabelE.getComponent(LabelComponent.class).text.replace(0,
                    scoreLabelE.getComponent(LabelComponent.class).text.capacity(),     // real look alike
                    "" + fcc.score);
            scoreLabelEsh.getComponent(LabelComponent.class).text.replace(0,
                    scoreLabelEsh.getComponent(LabelComponent.class).text.capacity(),     // real look alike
                    "" + fcc.score);
        } else {
            scoreLabelE.getComponent(LabelComponent.class).text.replace(0,
                    scoreLabelE.getComponent(LabelComponent.class).text.capacity(),     // real look alike
                    "");
            scoreLabelEsh.getComponent(LabelComponent.class).text.replace(0,
                    scoreLabelEsh.getComponent(LabelComponent.class).text.capacity(),     // real look alike
                    "");
        }
    }

    private void cleanupTheScene() {
        powerupSystem.removePowerupsFromStage();
        BugPool.getInstance(gameStage).removeBugsFromStage();
        if (petE != null) {
            if (petE.getComponent(ActionComponent.class) != null) {
                petE.getComponent(ActionComponent.class).reset();
                petE.getComponent(PetComponent.class).state = SPAWNING;
            }
            TransformComponent tc = petE.getComponent(TransformComponent.class); //TODO: NPE
            tc.x = PetComponent.X_SPAWN_POSITION;
            tc.y = PetComponent.getNewPositionY();

            fpc.currentPet.petCannon.getComponent(TransformComponent.class).x = tc.x + 64;
            fpc.currentPet.petCannon.getComponent(TransformComponent.class).y = tc.y - 9;
            fpc.currentPet.petCannon.getComponent(ZIndexComponent.class).setZIndex(127);

            fpc.currentPet.state = SPAWNING;
        }
    }

    @Override
    public Entity getMegaFlower() {
        return megaFlower;
    }
}
