package com.mygdx.etf.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.mygdx.etf.Main;
import com.mygdx.etf.entity.componets.Level;
import com.mygdx.etf.entity.componets.listeners.ImageButtonListener;
import com.mygdx.etf.stages.ui.PauseDialog;
import com.mygdx.etf.stages.ui.Settings;
import com.mygdx.etf.stages.ui.TrialTimer;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.scene2d.ButtonClickListener;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.etf.entity.componets.FlowerComponent.FLOWER_SCALE;
import static com.mygdx.etf.entity.componets.FlowerComponent.FLOWER_X_POS;
import static com.mygdx.etf.entity.componets.FlowerComponent.FLOWER_Y_POS;
import static com.mygdx.etf.entity.componets.LeafsComponent.LEAFS_SCALE;
import static com.mygdx.etf.entity.componets.LeafsComponent.LEAFS_X_POS;
import static com.mygdx.etf.entity.componets.LeafsComponent.LEAFS_Y_POS;
import static com.mygdx.etf.stages.GameStage.gameScript;
import static com.mygdx.etf.stages.ui.AbstractDialog.isDialogOpen;
import static com.mygdx.etf.utils.GlobalConstants.BTN_DEFAULT;
import static com.mygdx.etf.utils.GlobalConstants.BUTTON_TAG;
import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;

public class MenuScreenScript implements IScript {

    public static final String BTN_PLAY = "btn_play";
    public static final String BTN_SHOP = "btn_shop";
    //    public static final String BTN_NO_ADS = "btn_noAds";
    public static final String BTN_SETTINGS = "btn_settings";
    public static final String BTN_RATE = "btn_rate";
    public static final String BTN_GOALS = "btn_goals";
    public static final String LBL_GOALS_NOTIFICATION = "label_goal_notification";
    public static final String CURTAIN = "curtain_mm";
    public static final String MM_FLOWER = "MM_flower";

    ItemWrapper menuItem;
    private GameStage stage;
    private Settings settings;

    //Dima's party time
    Entity curtain_mm;
    boolean startGameTransition;
    boolean startShopTransition;
    boolean startTransitionIn;
    public static boolean showGoalNotification;

    private TrialTimer timer;
    private PauseDialog pauseDialog;

    public MenuScreenScript(GameStage stage) {
        showGoalNotification = Level.goalStatusChanged;
        GameStage.sceneLoader.addComponentsByTagName(BUTTON_TAG, ButtonComponent.class);
        this.stage = stage;
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

//        settings = new Settings(menuItem);
//        settings.init();
        isDialogOpen = false;
        if (timer == null) {
            timer = new TrialTimer(menuItem, 680, 500);
        }

        if (showGoalNotification) {
            Entity lblGoalNotification = menuItem.getChild(LBL_GOALS_NOTIFICATION).getEntity();
            LabelComponent lc = lblGoalNotification.getComponent(LabelComponent.class);
            lc.text.replace(0, lc.text.length, gameScript.fpc.level.getRemainingGoals());
        } else {
            Entity lblGoalNotification = menuItem.getChild(LBL_GOALS_NOTIFICATION).getEntity();
            lblGoalNotification.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        }
        pauseDialog = new PauseDialog(menuItem);
        pauseDialog.init();
    }

    public void initButtons() {
        final Entity playBtn = menuItem.getChild(BTN_PLAY).getEntity();
        final Entity btnShop = menuItem.getChild(BTN_SHOP).getEntity();
        final Entity btnSettings = menuItem.getChild(BTN_SETTINGS).getEntity();
        final Entity rateAppBtn = menuItem.getChild(BTN_RATE).getEntity();
        final Entity btnGoals = menuItem.getChild(BTN_GOALS).getEntity();
        Entity mmFlowerEntity = menuItem.getChild(MM_FLOWER).getEntity();

        TransformComponent tc = mmFlowerEntity.getComponent(TransformComponent.class);
        tc.x = FLOWER_X_POS;
        tc.y = FLOWER_Y_POS;
        tc.scaleX = FLOWER_SCALE;
        tc.scaleY = FLOWER_SCALE;

        Entity mmLeafsEntity = menuItem.getChild("MM_leafs").getEntity();

        TransformComponent tcL = mmLeafsEntity.getComponent(TransformComponent.class);
        tcL.x = LEAFS_X_POS;
        tcL.y = LEAFS_Y_POS;
        tcL.scaleX = LEAFS_SCALE;
        tcL.scaleY = LEAFS_SCALE;

        rateAppBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(rateAppBtn) {
                    @Override
                    public void clicked() {
                        if (!isDialogOpen) {
                            rateMyApp();
                        }
                    }
                }
                /*new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
            }

            @Override
            public void touchDown() {
            }

            @Override
            public void clicked() {
                if (!isDialogOpen) {
                    rateMyApp();
                }
            }
        }*/);

        playBtn.getComponent(ButtonComponent.class).addListener(
                new ButtonComponent.ButtonListener() {
                    @Override
                    public void touchUp() {
                    }

                    @Override
                    public void touchDown() {
                    }

                    @Override
                    public void clicked() {
                        System.out.println(Gdx.app.getJavaHeap() / 1000000);
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
                }
          /*      new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
            }

            @Override
            public void touchDown() {
                if (!isDialogOpen) {
                    startShopTransition = true;
                }
            }

            @Override
            public void clicked() {
                if (!isDialogOpen) {
                    startShopTransition = true;
                    resetPauseDialog();
                }
            }
        }*/);

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
                }


         /*       new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
                btnSettings.getComponent(TintComponent.class).color.set(1, 1, 1, 1f);
            }

            @Override
            public void touchDown() {
                btnSettings.getComponent(TintComponent.class).color.set(0, 0, 0, 0.5f);
            }

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
        }*/);

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
                }
                /*new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
            }

            @Override
            public void touchDown() {
                if (!isDialogOpen) {
                }
            }

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
        }*/);
    }

    @Override
    public void dispose() {
        System.gc();
    }

    @Override
    public void act(float delta) {
        GameScreenScript.checkTryPeriod();
        timer.timer();

        pauseDialog.update(delta);
        if (startGameTransition) {
            curtain_mm.getComponent(TintComponent.class).color.a += 0.05f;
            if (curtain_mm.getComponent(TintComponent.class).color.a >= 1) {
                startGameTransition = false;
                stage.initGame();
            }
        }

        if (startShopTransition) {
            curtain_mm.getComponent(TintComponent.class).color.a += 0.05f;
            if (curtain_mm.getComponent(TintComponent.class).color.a >= 1) {
                startShopTransition = false;
                stage.initShopWithAds();
            }
        }

        if (startTransitionIn) {
            curtain_mm.getComponent(TintComponent.class).color.a -= 0.05f;
            if (curtain_mm.getComponent(TintComponent.class).color.a <= 0) {
                startTransitionIn = false;
            }
        }
    }

    private void rateMyApp() {
        Main.mainController.rateMyApp();
    }

    public void resetPauseDialog() {
        pauseDialog.deleteTiles();
    }
}
