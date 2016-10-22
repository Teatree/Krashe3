package com.mygdx.etf.stages;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.mygdx.etf.Main;
import com.mygdx.etf.entity.componets.Level;
import com.mygdx.etf.entity.componets.ToggleButtonComponent;
import com.mygdx.etf.entity.componets.listeners.ImageButtonListener;
import com.mygdx.etf.stages.ui.PauseDialog;
import com.mygdx.etf.stages.ui.Settings;
import com.mygdx.etf.stages.ui.TrialTimer;
import com.mygdx.etf.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import javax.swing.*;

import static com.mygdx.etf.stages.GameStage.gameScript;
import static com.mygdx.etf.stages.ui.AbstractDialog.isDialogOpen;
import static com.mygdx.etf.utils.GlobalConstants.*;

public class MenuScreenScript implements IScript {

    public static final String BTN_PLAY = "btn_play";
    public static final String BTN_SHOP = "btn_shop";
    public static final String BTN_SETTINGS = "btn_settings";
    public static final String BTN_RATE = "btn_rate";
    public static final String BTN_GOALS = "btn_goals";
    public static final String LBL_GOALS_NOTIFICATION = "label_goal_notification";
    public static final String LBL_GOALS_NOTIFICATION_SH = "label_goal_notification_sh";
    public static final String IMG_GOAL_NOTIFICATION = "goal_notification_img";
    public static final String CURTAIN = "curtain_mm";
    public static final String BTN_FB = "btn_fb";
    public static final String BTN_LEADERBOARD = "btn_leaderboard";
    public static final String LEADERBOARD_C = "lederboard_composite";
    public static final String BTN_ACHIEVEMENTS = "btn_achievements";
    public static final String ACHIEVEMENTS_C = "achievements_composite";
    public static final String BTN_PLAY_SERVICES = "btn_playServices";

    public static final int TIMER_X = 945;
    public static final int TIMER_Y = 441;
    public static final String MM_LEAFS = "MM_leafs";
    private static final float TINT_STEP = 0.05f;
    private static final String IMG_LOGO = "img_logo";
    private static final String TAP_TO_PLAY = "tap_to_play";

    public boolean playServiceFlapIsOut = false;
    public boolean movingFlaps = false;

    ItemWrapper menuItem;
    private Settings settings;

    //Dima's party time
    static Entity curtain_mm;
    boolean startGameTransition;
    boolean startShopTransition;
    boolean startTransitionIn;
    public static boolean showGoalNotification;

    private static TrialTimer timer;
    private static PauseDialog pauseDialog;
    private static Entity imgGoalNotification;
    private static Entity lblGoalNotification;
    private static Entity lblGoalNotificationSh;
    private static Entity playBtn;
    private static Entity btnShop;
    private static Entity btnSettings;
    private static Entity rateAppBtn;
    private static Entity btnGoals;
    private static Entity btnFB;
    private static Entity btnLB;
    private static Entity btnAch;
    private static Entity btnPlayServices;
    private static Entity leaderboard_C;
    private static Entity achievements_C;

    public float wrldW = 800;
    public float wrldH = 524;
    float dx;
    float dy;
    int frames;

    public int camPosX = 430;
    private double transitionCoefficient;

    public MenuScreenScript() {
        showGoalNotification = Level.goalStatusChanged;
        GameStage.sceneLoader.addComponentsByTagName(BUTTON_TAG, ButtonComponent.class);
    }

    @Override
    public void init(Entity item) {
//        System.err.print("init menu ");
//        System.err.println(Gdx.app.getJavaHeap() / 1000000 + " : " +
//                Gdx.app.getNativeHeap());
        frames = 0;
        menuItem = new ItemWrapper(item);
        curtain_mm = menuItem.getChild(CURTAIN).getEntity();
        curtain_mm.getComponent(TintComponent.class).color.a = 1f;
        startGameTransition = false;
        startShopTransition = false;
        startTransitionIn = true;

        isDialogOpen = false;
        if (timer == null) {
            timer = new TrialTimer(menuItem, TIMER_X, TIMER_Y);
        }

        if (showGoalNotification) {
            lblGoalNotification = menuItem.getChild(LBL_GOALS_NOTIFICATION).getEntity();
            LabelComponent lc = lblGoalNotification.getComponent(LabelComponent.class);
            lc.text.replace(0, lc.text.length, gameScript.fpc.level.getRemainingGoals());
            lblGoalNotificationSh = menuItem.getChild(LBL_GOALS_NOTIFICATION_SH).getEntity();
            LabelComponent lcSh = lblGoalNotificationSh.getComponent(LabelComponent.class);
            lcSh.text.replace(0, lcSh.text.length, gameScript.fpc.level.getRemainingGoals());
            imgGoalNotification = menuItem.getChild(IMG_GOAL_NOTIFICATION).getEntity();
            imgGoalNotification.getComponent(TintComponent.class).color.a = 1;
            lblGoalNotification.getComponent(TintComponent.class).color.a = 1;
            lblGoalNotificationSh.getComponent(TintComponent.class).color.a = 1;
        } else {
            Entity lblGoalNotification = menuItem.getChild(LBL_GOALS_NOTIFICATION).getEntity();
            lblGoalNotification.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            Entity lblGoalNotificationSh = menuItem.getChild(LBL_GOALS_NOTIFICATION_SH).getEntity();
            lblGoalNotificationSh.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            Entity imgGoalNotification = menuItem.getChild(IMG_GOAL_NOTIFICATION).getEntity();
            imgGoalNotification.getComponent(TintComponent.class).color.a = 0;
        }

        if (achievements_C != null){
            achievements_C.getComponent(TransformComponent.class).y = 330f;
            leaderboard_C.getComponent(TransformComponent.class).y = 330f;
            leaderboard_C.getComponent(TintComponent.class).color.a = 1f;
            achievements_C.getComponent(TintComponent.class).color.a = 1f;
        }
    }

    public void setupMenuScreenWorld() {
        wrldW = 800;
        wrldH = 524;
        camPosX = 430;

        menuItem.getChild(TAP_TO_PLAY).getEntity().getComponent(TintComponent.class).color.a = 1;
        menuItem.getChild(IMG_LOGO).getEntity().getComponent(TintComponent.class).color.a = 1;
        btnSettings.getComponent(TintComponent.class).color.a = 1;
        btnPlayServices.getComponent(TintComponent.class).color.a = 1;
        rateAppBtn.getComponent(TintComponent.class).color.a = 1;
        btnShop.getComponent(TintComponent.class).color.a = 1;
        btnGoals.getComponent(TintComponent.class).color.a = 1;
        btnFB.getComponent(TintComponent.class).color.a = 1;
        btnLB.getComponent(TintComponent.class).color.a = 1;
        btnAch.getComponent(TintComponent.class).color.a = 1;
        leaderboard_C.getComponent(TintComponent.class).color.a = 1;
        achievements_C.getComponent(TintComponent.class).color.a = 1;
        if(timer!=null) {
            timer.timerE.getComponent(TintComponent.class).color.a = 1;
            timer.timerEsh.getComponent(TintComponent.class).color.a = 1;
            timer.timerLogo.getComponent(TintComponent.class).color.a = 1;
        }
//        imgGoalNotification.getComponent(TintComponent.class).color.a = 1;
//        lblGoalNotificationSh.getComponent(TintComponent.class).color.a = 1;
//        lblGoalNotification.getComponent(TintComponent.class).color.a = 1;

        GameStage.viewport.setWorldSize(wrldW, wrldH);
        GameStage.viewport.getCamera().translate(0, 0, 0);
    }

    public void initButtons() {
        playBtn = menuItem.getChild(BTN_PLAY).getEntity();
        btnShop = menuItem.getChild(BTN_SHOP).getEntity();
        btnSettings = menuItem.getChild(BTN_SETTINGS).getEntity();
        rateAppBtn = menuItem.getChild(BTN_RATE).getEntity();
        btnPlayServices = menuItem.getChild(BTN_PLAY_SERVICES).getEntity();
        btnFB = menuItem.getChild(BTN_FB).getEntity();
        btnGoals = menuItem.getChild(BTN_GOALS).getEntity();

        leaderboard_C = menuItem.getChild(LEADERBOARD_C).getEntity();
        achievements_C = menuItem.getChild(ACHIEVEMENTS_C).getEntity();

        btnAch = achievements_C.getComponent(NodeComponent.class).getChild(BTN_ACHIEVEMENTS);
        btnLB = leaderboard_C.getComponent(NodeComponent.class).getChild(BTN_LEADERBOARD);

        ToggleButtonComponent signInOutTbc = new ToggleButtonComponent();
//        }

        btnFB.add(new ButtonComponent());
        btnFB.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(btnFB) {
                    @Override
                    public void clicked() {
                        if (playServiceFlapIsOut) {
                            movingFlaps = true;
                        }
                        Main.mainController.openFB();
                    }
                });

        btnAch.add(new ButtonComponent());
        btnAch.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(btnAch) {
                    @Override
                    public void clicked() {
                        if (playServiceFlapIsOut) {
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
                            Main.mainController.getLeaderboard();
                        }
                    }
                });

        rateAppBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(rateAppBtn) {
                    @Override

                    public void clicked() {
                        if (!isDialogOpen) {
                            Main.mainController.rateMyApp();
                        }

                    }
                });

        playBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(playBtn) {
                    @Override
                    public void clicked() {
                        if (!isDialogOpen) {
                            startGameTransition = true;
                        }
                    }
                });

        btnShop.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(btnShop) {
                    @Override
                    public void clicked() {
                        if (!isDialogOpen) {
                            startShopTransition = true;
                            resetPauseDialog();
                        }
                    }
                });

        btnSettings.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(btnSettings) {
                    @Override
                    public void clicked() {
                        if (!isDialogOpen) {
                            if (playServiceFlapIsOut) {
                                movingFlaps = true;
                            }
                            isDialogOpen = true;
                            if (settings == null) {
                                settings = new Settings(menuItem);
                                settings.init();
                            }
                            settings.show();
                        }
                    }
                });

        btnPlayServices.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(btnPlayServices) {
                    @Override
                    public void clicked() {
                        movingFlaps = true;
                    }
                });

        btnGoals.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(btnGoals) {
                    @Override
                    public void clicked() {
                        if (!isDialogOpen) {
                            if (playServiceFlapIsOut) {
                                movingFlaps = true;
                            }
                            isDialogOpen = true;
                            showGoalNotification = false;
                            Level.goalStatusChanged = false;
                            Entity lblGoalNotification = menuItem.getChild(LBL_GOALS_NOTIFICATION).getEntity();
                            lblGoalNotification.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
                            Entity lblGoalNotificationSh = menuItem.getChild(LBL_GOALS_NOTIFICATION_SH).getEntity();
                            lblGoalNotificationSh.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
                            Entity imgGoalNotification = menuItem.getChild(IMG_GOAL_NOTIFICATION).getEntity();
                            imgGoalNotification.getComponent(TintComponent.class).color.a = 0;

                            if (pauseDialog == null) {
                                pauseDialog = new PauseDialog(menuItem);
                                pauseDialog.initGoals();
                            }
                            pauseDialog.show();
                        }
                    }
                });
    }

    @Override
    public void dispose() {
        System.gc();
    }

    @Override
    public void act(float delta) {
//        GameStage.viewport.getCamera()
//        System.out.println("world width:" + GameStage.viewport.getWorldWidth());
//        System.out.println("world height:" + GameStage.viewport.getWorldHeight());

        //move da other buttons
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

        GameStage.viewport.setWorldSize(wrldW, wrldH);
        GameStage.viewport.getCamera().translate(camPosX, 0, 0);

        GameScreenScript.checkTryPeriod();
        timer.timer();

        if (pauseDialog != null)
            pauseDialog.update(delta);
        if (startGameTransition) {
//            curtain_mm.getComponent(TintComponent.class).color.a += ALPHA_TRANSITION_STEP;
//            if (curtain_mm.getComponent(TintComponent.class).color.a >= 1) {
//                startGameTransition = false;
//                GameStage.initGame();
//            }

            //world size
            transitionCoefficient = 1;
            gameTransition();
        }

        if (startShopTransition) {
            curtain_mm.getComponent(TintComponent.class).color.a += ALPHA_TRANSITION_STEP;
            if (curtain_mm.getComponent(TintComponent.class).color.a >= 1) {
                startShopTransition = false;
                GameStage.initShopWithAds();
            }
        }

        if (startTransitionIn) {
            curtain_mm.getComponent(TintComponent.class).color.a -= ALPHA_TRANSITION_STEP;
            if (curtain_mm.getComponent(TintComponent.class).color.a <= 0) {
                startTransitionIn = false;
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

        float fg = 30f/67;
        if(fg<30) {
            camPosX = (int) (1230 - wrldW - fg * frames);

            frames++;
        }

        if (menuItem.getChild(TAP_TO_PLAY).getEntity().getComponent(TintComponent.class).color.a >= 0) {
            menuItem.getChild(TAP_TO_PLAY).getEntity().getComponent(TintComponent.class).color.a -= TINT_STEP;
            menuItem.getChild(IMG_LOGO).getEntity().getComponent(TintComponent.class).color.a -= TINT_STEP;
            btnSettings.getComponent(TintComponent.class).color.a -= TINT_STEP;
            btnShop.getComponent(TintComponent.class).color.a -= TINT_STEP;
            btnGoals.getComponent(TintComponent.class).color.a -= TINT_STEP;
            btnFB.getComponent(TintComponent.class).color.a -= TINT_STEP;
            btnLB.getComponent(TintComponent.class).color.a -= TINT_STEP;
            btnAch.getComponent(TintComponent.class).color.a -= TINT_STEP;
            if(timer!=null) {
                timer.timerE.getComponent(TintComponent.class).color.a -= TINT_STEP;
                timer.timerEsh.getComponent(TintComponent.class).color.a -= TINT_STEP;
                timer.timerLogo.getComponent(TintComponent.class).color.a -= TINT_STEP;
            }
            rateAppBtn.getComponent(TintComponent.class).color.a = 0;
            btnPlayServices.getComponent(TintComponent.class).color.a -= TINT_STEP;
            leaderboard_C.getComponent(TintComponent.class).color.a = 0;
            achievements_C.getComponent(TintComponent.class).color.a = 0;
            if (imgGoalNotification != null && imgGoalNotification.getComponent(TintComponent.class).color.a != 0) {
                imgGoalNotification.getComponent(TintComponent.class).color.a -= TINT_STEP;
                lblGoalNotificationSh.getComponent(TintComponent.class).color.a -= TINT_STEP;
                lblGoalNotification.getComponent(TintComponent.class).color.a -= TINT_STEP;
            }

        }

        if (GameStage.viewport.getWorldWidth() >= 1195) {
            startGameTransition = false;
            GameStage.viewport.getCamera().translate(0, 0, 0);
            GameStage.initGame();
        }
    }

    public void resetPauseDialog() {
        if (pauseDialog != null)
            pauseDialog.deleteTiles();
    }

}
