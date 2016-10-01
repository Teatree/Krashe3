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
    public static final String CURTAIN = "curtain_mm";
    public static final String BTN_FB = "btn_fb";
    public static final String BTN_LEADERBOARD = "btn_leaderboard";
    public static final String LEADERBOARD_C = "lederboard_composite";
    public static final String BTN_ACHIEVEMENTS = "btn_achievements";
    public static final String ACHIEVEMENTS_C = "achievements_composite";
    public static final String BTN_PLAY_SERVICES = "btn_playServices";

    public static final int TIMER_X = 680;
    public static final int TIMER_Y = 500;
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
    private static Entity lblGoalNotification;
    private static Entity playBtn;
    private static Entity btnShop;
    private static Entity btnSettings;
    private static Entity rateAppBtn;
    private static Entity btnGoals;
    private static Entity btnFB;
    private static Entity btnLB;
    private static Entity btnAch;
    private static Entity btnPlayServices;
    private static Entity btnSignInOut;
    private static Entity leaderboard_C;
    private static Entity achievements_C;

    public float wrldW = 800;
    public float wrldH = 524;
    float dx;
    float dy;

    public int camPosX = 400;
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
        } else {
            Entity lblGoalNotification = menuItem.getChild(LBL_GOALS_NOTIFICATION).getEntity();
            lblGoalNotification.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        }
    }

    public void setupMenuScreenWorld() {
        wrldW = 800;
        wrldH = 524;
        camPosX = 400;

        menuItem.getChild(TAP_TO_PLAY).getEntity().getComponent(TintComponent.class).color.a = 1;
        menuItem.getChild(IMG_LOGO).getEntity().getComponent(TintComponent.class).color.a = 1;
        btnSettings.getComponent(TintComponent.class).color.a = 1;
        btnShop.getComponent(TintComponent.class).color.a = 1;
        btnGoals.getComponent(TintComponent.class).color.a = 1;
        btnFB.getComponent(TintComponent.class).color.a = 1;
        btnLB.getComponent(TintComponent.class).color.a = 1;
        btnAch.getComponent(TintComponent.class).color.a = 1;
        btnSignInOut.getComponent(TintComponent.class).color.a = 1;

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
        btnSignInOut.add(signInOutTbc);
        btnSignInOut.add(new ButtonComponent());
        btnSignInOut.getComponent(ButtonComponent.class).isDefaultLayersChange = false;

        final LayerMapComponent lc = ComponentRetriever.get(btnSignInOut, LayerMapComponent.class);
        if (Main.mainController.isSignedIn()) {
            signInOutTbc.setOff();
            lc.getLayer(BTN_NORMAL).isVisible = false;
            lc.getLayer(BTN_PRESSED).isVisible = true;
        } else {
            signInOutTbc.setOn();
            lc.getLayer(BTN_NORMAL).isVisible = true;
            lc.getLayer(BTN_DEFAULT).isVisible = true;
            lc.getLayer(BTN_PRESSED).isVisible = false;
        }

        if (0 == btnSignInOut.getComponent(ButtonComponent.class).listeners.size) {
            btnSignInOut.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
                private ComponentMapper<ToggleButtonComponent> mapper = ComponentMapper.getFor(ToggleButtonComponent.class);

                @Override
                public void touchDown() {
                    if(playServiceFlapIsOut && !movingFlaps) {
                        btnSignInOut.getComponent(TransformComponent.class).scaleX -= GlobalConstants.TENTH;
                        btnSignInOut.getComponent(TransformComponent.class).scaleY -= GlobalConstants.TENTH;
                        btnSignInOut.getComponent(TransformComponent.class).x += btnSignInOut.getComponent(DimensionsComponent.class).width / 20;
                        btnSignInOut.getComponent(TransformComponent.class).y += btnSignInOut.getComponent(DimensionsComponent.class).height / 20;
                    }
                }

                @Override
                public void touchUp() {
                    if(playServiceFlapIsOut && !movingFlaps) {
                        btnSignInOut.getComponent(TransformComponent.class).scaleX += GlobalConstants.TENTH;
                        btnSignInOut.getComponent(TransformComponent.class).scaleY += GlobalConstants.TENTH;
                        btnSignInOut.getComponent(TransformComponent.class).x -= btnSignInOut.getComponent(DimensionsComponent.class).width / 20;
                        btnSignInOut.getComponent(TransformComponent.class).y -= btnSignInOut.getComponent(DimensionsComponent.class).height / 20;
                    }
                }

                @Override
                public void clicked() {
                    if(playServiceFlapIsOut && !movingFlaps) {
                        final ToggleButtonComponent tbc = mapper.get(btnSignInOut);
                        if (tbc.isOn()) {
                            lc.getLayer(BTN_NORMAL).isVisible = false;
                            lc.getLayer(BTN_PRESSED).isVisible = true;
//                        lc.getLayer(BTN_DEFAULT).isVisible = false;
                            Main.mainController.signOut();
                            tbc.setOff();
                            System.out.println("Signed out");
                        } else {
                            lc.getLayer(BTN_NORMAL).isVisible = true;
                            lc.getLayer(BTN_PRESSED).isVisible = false;
                            Main.mainController.signIn();
                            System.out.println("Signed in");
                            tbc.setOn();
                        }
                    }
                }
            });
        }

        btnFB.add(new ButtonComponent());
        btnFB.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(btnFB) {
                    @Override
                    public void clicked() {
                        Main.mainController.openFB();
                    }
                });

        btnAch.add(new ButtonComponent());
        btnAch.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(btnAch) {
                    @Override
                    public void clicked() {
                        if(playServiceFlapIsOut) {
                            Main.mainController.getAchievements();
                        }
                    }
                });

        btnLB.add(new ButtonComponent());
        btnLB.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(btnLB) {
                    @Override
                    public void clicked() {

                        if(playServiceFlapIsOut) {
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
                            isDialogOpen = true;
                            showGoalNotification = false;
                            Level.goalStatusChanged = false;
                            Entity lblGoalNotification = menuItem.getChild(LBL_GOALS_NOTIFICATION).getEntity();
                            lblGoalNotification.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;

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
        if(movingFlaps) {
            if (!playServiceFlapIsOut) {
                    if (achievements_C.getComponent(TransformComponent.class).y > 215) {
                        achievements_C.getComponent(TransformComponent.class).y -= 4;
                        if (achievements_C.getComponent(TransformComponent.class).y <= 275.5f && leaderboard_C.getComponent(TransformComponent.class).y > 268.40f) {
                            leaderboard_C.getComponent(TransformComponent.class).y -= 4;
                            if (leaderboard_C.getComponent(TransformComponent.class).y <= 275.40f) {
                                playServiceFlapIsOut = true;
                                movingFlaps = false;
                            }
                        }
                    }
            } else {
                if (achievements_C.getComponent(TransformComponent.class).y < 331) {
                    achievements_C.getComponent(TransformComponent.class).y += 4;
                    if (achievements_C.getComponent(TransformComponent.class).y >= 338) {
                        playServiceFlapIsOut = false;
                        movingFlaps = false;
                    }
                }
                if (leaderboard_C.getComponent(TransformComponent.class).y < 333) {
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

        transitionCoefficient += 1.3;
        wrldH += 6.1f * dy * transitionCoefficient;
        wrldW += 6.1f * dx * transitionCoefficient;
        camPosX = 1200 - (int) GameStage.viewport.getWorldWidth();

        if (menuItem.getChild(TAP_TO_PLAY).getEntity().getComponent(TintComponent.class).color.a >= 0) {
            menuItem.getChild(TAP_TO_PLAY).getEntity().getComponent(TintComponent.class).color.a -= TINT_STEP;
            menuItem.getChild(IMG_LOGO).getEntity().getComponent(TintComponent.class).color.a -= TINT_STEP;
            btnSettings.getComponent(TintComponent.class).color.a -= TINT_STEP;
            btnShop.getComponent(TintComponent.class).color.a -= TINT_STEP;
            btnGoals.getComponent(TintComponent.class).color.a -= TINT_STEP;
            btnFB.getComponent(TintComponent.class).color.a -= TINT_STEP;
            btnLB.getComponent(TintComponent.class).color.a -= TINT_STEP;
            btnAch.getComponent(TintComponent.class).color.a -= TINT_STEP;
            btnSignInOut.getComponent(TintComponent.class).color.a -= TINT_STEP;
            rateAppBtn.getComponent(TintComponent.class).color.a -= TINT_STEP;
            btnPlayServices.getComponent(TintComponent.class).color.a -= TINT_STEP;
        }

        if (GameStage.viewport.getWorldHeight() >= 785) {
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
