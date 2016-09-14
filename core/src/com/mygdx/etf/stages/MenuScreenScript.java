package com.mygdx.etf.stages;

import com.badlogic.ashley.core.Entity;
import com.mygdx.etf.Main;
import com.mygdx.etf.entity.componets.Level;
import com.mygdx.etf.entity.componets.listeners.ImageButtonListener;
import com.mygdx.etf.stages.ui.PauseDialog;
import com.mygdx.etf.stages.ui.Settings;
import com.mygdx.etf.stages.ui.TrialTimer;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ItemWrapper;
import static com.mygdx.etf.stages.GameStage.gameScript;
import static com.mygdx.etf.stages.ui.AbstractDialog.isDialogOpen;
import static com.mygdx.etf.utils.GlobalConstants.BUTTON_TAG;
import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.mygdx.etf.utils.GlobalConstants.ALPHA_TRANSITION_STEP;

public class MenuScreenScript implements IScript {

    public static final String BTN_PLAY = "btn_play";
    public static final String BTN_SHOP = "btn_shop";
    public static final String BTN_SETTINGS = "btn_settings";
    public static final String BTN_RATE = "btn_rate";
    public static final String BTN_GOALS = "btn_goals";
    public static final String LBL_GOALS_NOTIFICATION = "label_goal_notification";
    public static final String CURTAIN = "curtain_mm";
    public static final String MM_FLOWER = "MM_flower";
    public static final String BTN_FB_INFO = "btn_fb_info";

    public static final int TIMER_X = 680;
    public static final int TIMER_Y = 500;
    public static final String MM_LEAFS = "MM_leafs";

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

    public void initButtons() {
        playBtn = menuItem.getChild(BTN_PLAY).getEntity();
        btnShop = menuItem.getChild(BTN_SHOP).getEntity();
        btnSettings = menuItem.getChild(BTN_SETTINGS).getEntity();
        rateAppBtn = menuItem.getChild(BTN_RATE).getEntity();
        btnFB = menuItem.getChild(BTN_FB_INFO).getEntity();
        btnGoals = menuItem.getChild(BTN_GOALS).getEntity();

        if (btnFB != null) {
            btnFB.add(new ButtonComponent());
            btnFB.getComponent(ButtonComponent.class).addListener(
                    new ImageButtonListener(btnFB) {
                        @Override
                        public void clicked() {
                            Main.mainController.showScore();
//                            Gdx.net.openURI("https://facebook.com/Teatree1992"); // opens site if app not installed
                        }
                    });
        }

        rateAppBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(rateAppBtn) {
                    @Override
                    public void clicked() {
                        if (!isDialogOpen) {
                            Main.mainController.rateGame();
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
                                pauseDialog.init();
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
        GameScreenScript.checkTryPeriod();
        timer.timer();

        if (pauseDialog != null)
            pauseDialog.update(delta);
        if (startGameTransition) {
            curtain_mm.getComponent(TintComponent.class).color.a += ALPHA_TRANSITION_STEP;
            if (curtain_mm.getComponent(TintComponent.class).color.a >= 1) {
                startGameTransition = false;
                GameStage.initGame();
            }
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

    public void resetPauseDialog() {
        if (pauseDialog != null)
            pauseDialog.deleteTiles();
    }
}
