package com.fd.etf.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Interpolation;
import com.fd.etf.Main;
import com.fd.etf.entity.componets.Level;
import com.fd.etf.entity.componets.listeners.ImageButtonListener;
import com.fd.etf.stages.ui.*;
import com.fd.etf.utils.BackgroundMusicMgr;
import com.fd.etf.utils.SaveMngr;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.fd.etf.entity.componets.FlowerComponent.*;
import static com.fd.etf.entity.componets.LeafsComponent.*;
import static com.fd.etf.stages.ui.AbstractDialog.isDialogOpen;
import static com.fd.etf.stages.ui.PromoWindow.offerPromo;
import static com.fd.etf.utils.GlobalConstants.*;

public class MenuScreenScript implements IScript, GameStage.IhaveFlower {

    private static final String BTN_PLAY = "btn_play";
    private static final String BTN_SHOP = "btn_shop";
    private static final String BTN_SETTINGS = "btn_settings";
    private static final String BTN_RATE = "btn_rate";
    private static final String BTN_GOALS = "btn_goals";
    private static final String LBL_GOALS_NOTIFICATION = "label_goal_notification";
    private static final String IMG_GOAL_NOTIFICATION = "goal_notification_img";
    private static final String CURTAIN = "curtain_mm";
    private static final String BTN_FB = "btn_fb";
    private static final String BTN_LEADERBOARD = "btn_leaderboard";
    private static final String LEADERBOARD_C = "lederboard_composite";
    private static final String BTN_ACHIEVEMENTS = "btn_achievements";
    private static final String ACHIEVEMENTS_C = "achievements_composite";
    private static final String BTN_PLAY_SERVICES = "btn_playServices";
    private static final String MEGA_FLOWER = "mega_flower";
    private static final String MEGA_LEAVES = "mega_leafs";

    private static final int TIMER_X = 1064;
    private static final int TIMER_Y = 411;
    private static final float TINT_STEP = 0.05f;
    private static final String IMG_LOGO = "img_logo";
    private static final String LBL_TAP2START = "lbl_tap2start";
    private static final float RATE_APP_BTN_ALPHA = 0.8352941f;
    private final GameStage gameStage;

    public boolean playServiceFlapIsOut = false;
    public boolean movingFlaps = false;

    ItemWrapper menuItem;
    public static Settings settings;

    //Dima's party time
    static Entity curtain_mm;
    boolean startGameTransition;
    boolean startShopTransition;
    boolean startTransitionIn;
    public static boolean showGoalNotification;
    public int currentFlowerFrame;

    private TrialTimer timer;
    private static PauseDialog pauseDialog;
    private static Entity imgGoalNotification;
    private static Entity lblGoalNotification;
    private Entity btnShop;
    private Entity btnSettings;
    private Entity rateAppBtn;
    private Entity btnChalenges;
    private Entity btnFB;
    private Entity btnLB;
    private Entity btnAchievements;
    private Entity btnPlayServices;
    private Entity leaderboard_C;
    private Entity achievements_C;

    public float wrldW = 800;
    public float wrldH = 524;
    float dx;
    float dy;
    int frames;

    public int camPosX = 130;
    private double transitionCoefficient;
    public Entity megaFlower;
    public Entity megaLeaves;
    public static boolean canClickPlay;

    private BasicDialog exitDialog;

    public MenuScreenScript(GameStage gameStage) {
        this.gameStage = gameStage;
        showGoalNotification = Level.goalStatusChanged;
        gameStage.sceneLoader.addComponentsByTagName(BUTTON_TAG, ButtonComponent.class);
    }

    @Override
    public void init(Entity item) {

        frames = 0;
        canClickPlay = false;
        menuItem = new ItemWrapper(item);
        curtain_mm = menuItem.getChild(CURTAIN).getEntity();
        curtain_mm.getComponent(TintComponent.class).color.a = 1f;
        startGameTransition = false;
        startShopTransition = false;
        startTransitionIn = true;
        isDialogOpen.set(false);

        if (timer == null) {
            timer = new TrialTimer(gameStage, menuItem, TIMER_X, TIMER_Y);
        } else {
            timer.mainItem = menuItem;
        }

        initGoalsNotification();

        initFlower(menuItem.getChild(MEGA_FLOWER).getEntity(), menuItem.getChild(MEGA_LEAVES).getEntity());

        // all the single alphas
        menuItem.getChild(IMG_LOGO).getEntity().getComponent(TransformComponent.class).scaleX = 0.3f;
        menuItem.getChild(IMG_LOGO).getEntity().getComponent(TransformComponent.class).scaleY = 0.3f;
        BackgroundMusicMgr.getBackgroundMusicMgr().playMenu();
    }

    private void initGoalsNotification() {
        if (showGoalNotification) {
            lblGoalNotification = menuItem.getChild(LBL_GOALS_NOTIFICATION).getEntity();
            LabelComponent lc = lblGoalNotification.getComponent(LabelComponent.class);
            lc.text.replace(0, lc.text.length, gameStage.gameScript.fpc.level.getRemainingGoals());
            imgGoalNotification = menuItem.getChild(IMG_GOAL_NOTIFICATION).getEntity();
            imgGoalNotification.getComponent(TintComponent.class).color.a = 1;
            lblGoalNotification.getComponent(TintComponent.class).color.a = 1;
        } else {
            Entity lblGoalNotification = menuItem.getChild(LBL_GOALS_NOTIFICATION).getEntity();
            lblGoalNotification.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            Entity imgGoalNotification = menuItem.getChild(IMG_GOAL_NOTIFICATION).getEntity();
            imgGoalNotification.getComponent(TintComponent.class).color.a = 0;
        }

        if (achievements_C != null) {
            achievements_C.getComponent(TransformComponent.class).y = 330f;
            leaderboard_C.getComponent(TransformComponent.class).y = 330f;
            leaderboard_C.getComponent(TintComponent.class).color.a = 1f;
            achievements_C.getComponent(TintComponent.class).color.a = 1f;
        }

        exitDialog = new BasicDialog(gameStage, menuItem);
        exitDialog.init();
    }

    public void setupMenuScreenWorld() {
        wrldW = 800;
        wrldH = 524;
        camPosX = 130;

        //less code!
        for (Entity e : menuItem.getComponent(NodeComponent.class).children) {
            if (!e.getComponent(MainItemComponent.class).itemIdentifier.equals("bg") && !e.getComponent(MainItemComponent.class).itemIdentifier.equals("curtain_mm") && !e.getComponent(MainItemComponent.class).libraryLink.equals("lib_shadow")) {
                e.getComponent(TintComponent.class).color.a = 0;
            }
        }
//        if (timer != null) {
//            timer.timerE.getComponent(TintComponent.class).color.a = 0;
//        }
        GameStage.viewport.setWorldSize(wrldW, wrldH);
        GameStage.viewport.getCamera().translate(0, 0, 0);
    }

    public void initButtons() {
        Entity playBtn = menuItem.getChild(BTN_PLAY).getEntity();
        btnShop = menuItem.getChild(BTN_SHOP).getEntity();
        btnSettings = menuItem.getChild(BTN_SETTINGS).getEntity();
        rateAppBtn = menuItem.getChild(BTN_RATE).getEntity();
        btnPlayServices = menuItem.getChild(BTN_PLAY_SERVICES).getEntity();
        btnFB = menuItem.getChild(BTN_FB).getEntity();
        btnChalenges = menuItem.getChild(BTN_GOALS).getEntity();

        leaderboard_C = menuItem.getChild(LEADERBOARD_C).getEntity();
        achievements_C = menuItem.getChild(ACHIEVEMENTS_C).getEntity();

        btnAchievements = achievements_C.getComponent(NodeComponent.class).getChild(BTN_ACHIEVEMENTS);
        btnLB = leaderboard_C.getComponent(NodeComponent.class).getChild(BTN_LEADERBOARD);

        btnFB.add(new ButtonComponent());
        btnFB.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(btnFB, new AtomicBoolean[]{isDialogOpen}) {
                    @Override
                    public void clicked() {

                        if (!isDialogOpen.get()) {
                            movingFlaps = playServiceFlapIsOut;
                            Main.mainController.openFB();
                        }
                    }
                });

        btnAchievements.add(new ButtonComponent());
        btnAchievements.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(btnAchievements, new AtomicBoolean[]{isDialogOpen}) {
                    @Override
                    public void clicked() {
                        if (playServiceFlapIsOut) {
                            movingFlaps = true;
                            Main.mainController.getAchievements();
                        }
                    }
                });

        btnLB.add(new ButtonComponent());
        btnLB.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(btnLB) {
                    @Override
                    public void clicked() {
                        if (playServiceFlapIsOut) {
                            movingFlaps = true;
                            Main.mainController.getLeaderboard();
                        }
                    }
                });

        rateAppBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(rateAppBtn) {
                    @Override

                    public void clicked() {
                        if (!isDialogOpen.get()) {
                            if (playServiceFlapIsOut) {
                                movingFlaps = true;
                            }
                            Main.mainController.rateMyApp();
                        }

                    }
                });

        playBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(playBtn, new AtomicBoolean[]{isDialogOpen}) {
                    @Override
                    public void clicked() {
                        if (!isDialogOpen.get() && canClickPlay) {
                            startGameTransition = true;
                        }
                    }
                });

        btnShop.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(btnShop, new AtomicBoolean[]{isDialogOpen}) {
                    @Override
                    public void clicked() {
                        if (!isDialogOpen.get()) {
                            startShopTransition = true;
                            resetPauseDialog();
                        }
                    }
                });

        btnSettings.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(btnSettings, new AtomicBoolean[]{isDialogOpen}) {
                    @Override
                    public void clicked() {
                        if (!startShopTransition) {
                            if (!isDialogOpen.get()) {
                                if (playServiceFlapIsOut) {
                                    movingFlaps = true;
                                }
                                isDialogOpen.set(true);
                                if (settings == null) {
                                    settings = new Settings(gameStage, menuItem);
                                    settings.init();
                                }
                                settings.show();
                            }
                        }
                    }
                });

        btnPlayServices.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(btnPlayServices, new AtomicBoolean[]{isDialogOpen}) {
                    @Override
                    public void clicked() {
                        if (!isDialogOpen.get()) {
                            movingFlaps = true;
                        }
                    }
                });

        btnChalenges.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(btnChalenges, new AtomicBoolean[]{isDialogOpen}) {
                    @Override
                    public void clicked() {
                        if (!isDialogOpen.get()) {
                            if (!startShopTransition) {
                                if (playServiceFlapIsOut) {
                                    movingFlaps = true;
                                }
                                isDialogOpen.set(true);
                                showGoalNotification = false;
                                Level.goalStatusChanged = false;
                                Entity lblGoalNotification = menuItem.getChild(LBL_GOALS_NOTIFICATION).getEntity();
                                lblGoalNotification.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
                                Entity imgGoalNotification = menuItem.getChild(IMG_GOAL_NOTIFICATION).getEntity();
                                if (imgGoalNotification.getComponent(ActionComponent.class) != null) {
                                    imgGoalNotification.getComponent(ActionComponent.class).reset();
                                }
                                imgGoalNotification.getComponent(TintComponent.class).color.a = 0;

                                if (pauseDialog == null) {
                                    pauseDialog = new PauseDialog(gameStage, menuItem);
                                    pauseDialog.initGoals();
                                }
                                pauseDialog.show();
                                PauseDialog.goalsUpdate = false;
                            }
                        }
                    }
                });

        for (Entity e : menuItem.getComponent(NodeComponent.class).children) {
            if (!e.getComponent(MainItemComponent.class).itemIdentifier.equals("bg") && !e.getComponent(MainItemComponent.class).itemIdentifier.equals("curtain_mm") && !e.getComponent(MainItemComponent.class).libraryLink.equals("lib_shadow")) {
                e.getComponent(TintComponent.class).color.a = 0;
            }
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    public void act(float delta) {

        if (Gdx.input.isKeyPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.K)) {
            SaveMngr.saveStats(gameStage.gameScript.fpc);
            canClickPlay = false;
            isDialogOpen.set(true);
            exitDialog.show(BasicDialog.TYPE_EXIT);
//            Gdx.app.exit();
        }

        if (menuItem.getChild(LBL_TAP2START).getEntity().getComponent(TintComponent.class) != null &&
                menuItem.getChild(LBL_TAP2START).getEntity().getComponent(TintComponent.class).color.a > 0.7f) {
            canClickPlay = true;
        }

        //move da other buttons
        moveDaFlaps();

        GameStage.viewport.setWorldSize(wrldW, wrldH);
        GameStage.viewport.getCamera().translate(camPosX, 0, 0);

        gameStage.gameScript.checkTryPeriod();
        timer.update();

        if (pauseDialog != null)
            pauseDialog.update(delta);
        if (startGameTransition) {

            //world size
            transitionCoefficient = 1;
            gameTransition();
        }

        if (startShopTransition) {
            curtain_mm.getComponent(TintComponent.class).color.a += ALPHA_TRANSITION_STEP;
            if (curtain_mm.getComponent(TintComponent.class).color.a >= 1) {
                startShopTransition = false;
                gameStage.initShopWithAds();
                curtain_mm.getComponent(TintComponent.class).color.a = 1;
            }
        }

        if (startTransitionIn) {
            curtain_mm.getComponent(TintComponent.class).color.a -= ALPHA_TRANSITION_STEP;
            if (camPosX < 430) {
                camPosX += (430 - camPosX) * (1 * ALPHA_TRANSITION_STEP * 4);
            }

            if (curtain_mm.getComponent(TintComponent.class).color.a <= 0) {

                startTransitionIn = false;

                curtain_mm.getComponent(TintComponent.class).color.a = 0;

                // initial animations
                for (Entity e2 : menuItem.getComponent(NodeComponent.class).children) {
                    if (e2.getComponent(ActionComponent.class) == null) {
                        e2.add(new ActionComponent());
                    }
                }

                Actions.checkInit();

                menuItem.getChild(IMG_LOGO).getEntity().getComponent(ActionComponent.class).dataArray.add(Actions.sequence(
                        Actions.delay(0.6f),
                        Actions.parallel(Actions.fadeIn(0.4f, Interpolation.exp5Out), Actions.scaleTo(0.6f, 0.6f, 0.4f, Interpolation.exp5Out))));

                menuItem.getChild(LBL_TAP2START).getEntity().getComponent(ActionComponent.class).reset();
                ActionComponent ac = new ActionComponent();
                ac.dataArray.add(Actions.sequence(
                        Actions.delay(1.1f), Actions.fadeIn(2f, Interpolation.exp5Out), Actions.delay(2f),
                        Actions.parallel(Actions.scaleBy(0.1f, 0.1f, 0.4f), Actions.rotateBy(1, 0.3f), Actions.moveBy(0, 1, 0.3f, Interpolation.fade)),
                        Actions.parallel(Actions.scaleBy(-0.1f, -0.f, 0.4f), Actions.rotateBy(-1, 0.4f), Actions.moveBy(0, -1, 0.4f, Interpolation.fade))));
                menuItem.getChild(LBL_TAP2START).getEntity().add(ac);

//                System.out.println("Flower x: " + menuItem.getChild("mega_flower").getComponent(TransformComponent.class).x);
                for (Entity e : menuItem.getComponent(NodeComponent.class).children) {
                    if (!e.getComponent(MainItemComponent.class).itemIdentifier.equals(IMG_LOGO)
                            && !e.getComponent(MainItemComponent.class).itemIdentifier.equals(LBL_TAP2START)
                            && !e.getComponent(MainItemComponent.class).itemIdentifier.equals("bg")
                            && !e.getComponent(MainItemComponent.class).itemIdentifier.equals("mega_leafs")
                            && !e.getComponent(MainItemComponent.class).itemIdentifier.equals("mega_flower")
                            && !e.getComponent(MainItemComponent.class).itemIdentifier.equals("curtain_mm")
                            && !e.getComponent(MainItemComponent.class).libraryLink.equals("lib_shadow")
                            && !e.getComponent(MainItemComponent.class).libraryLink.equals("popup_basic_lib")) {
                        e.getComponent(TintComponent.class).color.a = 0;

                        if (!e.getComponent(MainItemComponent.class).itemIdentifier.equals("btn_rate")) {
                            if (e.getComponent(TransformComponent.class).x < wrldW) {
                                e.getComponent(TransformComponent.class).x -= 100;
                            } else {
                                e.getComponent(TransformComponent.class).x += 100;
                            }
                        } else {
                            e.getComponent(TransformComponent.class).y -= 100;
                        }

                        if (gameStage.gameScript.fpc.settings.totalPlayedGames >= 1) {
                            Actions.checkInit();
                            if (e.getComponent(MainItemComponent.class).itemIdentifier.equals(IMG_GOAL_NOTIFICATION) && !showGoalNotification) {
                                continue;
                            }
                            if (!e.getComponent(MainItemComponent.class).itemIdentifier.equals(BTN_RATE)) {
                                if (e.getComponent(TransformComponent.class).x < wrldW) {
                                    e.getComponent(ActionComponent.class).dataArray.add(Actions.sequence(
                                            Actions.delay(1.3f), Actions.parallel(Actions.moveTo(e.getComponent(TransformComponent.class).x + 100, e.getComponent(TransformComponent.class).y, 1f, Interpolation.exp5), Actions.fadeIn(1.5f, Interpolation.exp5Out))));
                                } else {
                                    e.getComponent(ActionComponent.class).dataArray.add(Actions.sequence(
                                            Actions.delay(1.3f), Actions.parallel(Actions.moveTo(e.getComponent(TransformComponent.class).x - 100, e.getComponent(TransformComponent.class).y, 1f, Interpolation.exp5), Actions.fadeIn(1.5f, Interpolation.exp5Out))));
                                }
                            } else {
                                e.getComponent(ActionComponent.class).dataArray.add(Actions.sequence(
                                        Actions.delay(1.3f), Actions.parallel(Actions.moveTo(e.getComponent(TransformComponent.class).x, e.getComponent(TransformComponent.class).y + 100, 1f, Interpolation.exp5), Actions.fadeIn(1.5f, Interpolation.exp5Out))));
                            }

                        }
                    }
                }
            }
        }
    }

    private void moveDaFlaps() {
        if (movingFlaps) {
            if (!playServiceFlapIsOut) {
                if (achievements_C.getComponent(TransformComponent.class).y > 210) {
                    achievements_C.getComponent(TransformComponent.class).y -= 4;
                    if (achievements_C.getComponent(TransformComponent.class).y <= 275.5f
                            && leaderboard_C.getComponent(TransformComponent.class).y > 263.40f) {
                        leaderboard_C.getComponent(TransformComponent.class).y -= 4;
                        if (leaderboard_C.getComponent(TransformComponent.class).y <= 270.40f) {
                            playServiceFlapIsOut = true;
                            movingFlaps = false;
                        }
                    }
                }
            } else {
                if (achievements_C.getComponent(TransformComponent.class).y < 331) {
                    achievements_C.getComponent(TransformComponent.class).y += 4;
                    if (achievements_C.getComponent(TransformComponent.class).y >= 331) {
                        playServiceFlapIsOut = false;
                        movingFlaps = false;
                    }
                }
                if (leaderboard_C.getComponent(TransformComponent.class).y < 331) {
                    leaderboard_C.getComponent(TransformComponent.class).y += 4;
                }
            }
        }
    }

    private void gameTransition() {
        dx = 1200 - 800;
        dy = 786 - 524;

        float length1 = (float) Math.sqrt(dx * dx + dy * dy);
        dx /= length1;
        dy /= length1;

        transitionCoefficient += 6.3;

        wrldH += dy * transitionCoefficient;
        wrldW += dx * transitionCoefficient;

        float fg = 30f / 67;
        if (fg < 30) {
            camPosX = (int) (1230 - wrldW - fg * frames);

            frames++;
        }
        for (Entity e : menuItem.getComponent(NodeComponent.class).children) {
            if (!e.getComponent(MainItemComponent.class).itemIdentifier.equals("bg")
                    && !e.getComponent(MainItemComponent.class).itemIdentifier.equals("curtain_mm")
                    && !e.getComponent(MainItemComponent.class).libraryLink.equals("lib_shadow")
                    && e.getComponent(TintComponent.class).color.a > 0) {

                e.getComponent(TintComponent.class).color.a -= TINT_STEP;
            }
        }

        if (GameStage.viewport.getWorldWidth() >= 1195) {
            startGameTransition = false;
            GameStage.viewport.getCamera().translate(0, 0, 0);
            currentFlowerFrame = megaFlower.getComponent(SpriterComponent.class).player.getTime();
            GameScreenScript.isStarted = true;
            gameStage.initGame(currentFlowerFrame);
        }
    }

    public void resetPauseDialog() {
        if (pauseDialog != null)
            pauseDialog.deleteTiles();
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

        megaFlower.getComponent(SpriterComponent.class).scale = FLOWER_SCALE;
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

    @Override
    public Entity getMegaFlower() {
        return megaFlower;
    }
}
