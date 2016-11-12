package com.fd.etf.stages.ui;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.fd.etf.Main;
import com.fd.etf.entity.componets.Goal;
import com.fd.etf.entity.componets.listeners.ImageButtonListener;
import com.fd.etf.stages.GameStage;
import com.fd.etf.system.BugSpawnSystem;
import com.fd.etf.system.BugSystem;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;

import static com.fd.etf.stages.GameScreenScript.*;
import static com.fd.etf.stages.GameStage.gameScript;
import static com.fd.etf.utils.EffectUtils.fade;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_Y;

public class GameOverDialog extends AbstractDialog {

    public static final int GAME_OVER_PROBABILITY = 0;

    public static final int TAP_COOL = 30;
    public final String GAME_OVER_DIALOG = "game_over_lib";
    public final String BTN_WATCH_VIDEO = "btn_watch_video";
    public final String LBL_TURN_ON_WIFI = "lbl_turn_on_wifi";
    public final String LABEL_TIMER_GAMEOVER = "label_timer_gameover";

    public static float gameOverTimer = 0;
    public static final int GAME_OVER_COUNT = 5;
    public static int gameOverCounter = GAME_OVER_COUNT;
    private int tapCoolDown = TAP_COOL;

    public static Entity gameOverDialogE;

    public static boolean releaseAllBugs() {
        return (isGameOver.get() && gameOverCounter <= 0 && !BugSystem.blowUpAllBugs);
    }

    public void show() {
        addShadow();
        isActive = true;
        isGameOver.set(true);
        System.gc();

        final TransformComponent dialogTc = gameOverDialogE.getComponent(TransformComponent.class);
        dialogTc.x = 300;
        dialogTc.y = 100;
        gameOverDialogE.getComponent(ZIndexComponent.class).setZIndex(shadowE.getComponent(ZIndexComponent.class).getZIndex() + 1);

        tapCoolDown = TAP_COOL;
        gameOverTimer = 0;
        gameOverCounter = GAME_OVER_COUNT;

        Entity gameOverTimerLbl = gameOverDialogE.getComponent(NodeComponent.class).getChild(LABEL_TIMER_GAMEOVER);
        LabelComponent gameOverLblC = gameOverTimerLbl.getComponent(LabelComponent.class);
        gameOverLblC.text.replace(0, gameOverLblC.text.capacity(), Integer.toString(GAME_OVER_COUNT));
        gameOverTimerLbl.getComponent(ZIndexComponent.class).setZIndex(gameOverDialogE.getComponent(ZIndexComponent.class).getZIndex() + 1);
    }

    public void initGameOverDialog() {
        initShadow();

        CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary(GAME_OVER_DIALOG).clone();
        gameOverDialogE = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
        GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), gameOverDialogE, tempItemC.composite);
        GameStage.sceneLoader.getEngine().addEntity(gameOverDialogE);

        final TransformComponent dialogTc = gameOverDialogE.getComponent(TransformComponent.class);
        dialogTc.x = FAR_FAR_AWAY_X;
        dialogTc.y = FAR_FAR_AWAY_Y;
        gameOverDialogE.getComponent(ZIndexComponent.class).setZIndex(shadowE.getComponent(ZIndexComponent.class).getZIndex() + 1);
        initReviveBtn(dialogTc);
    }

    private void initReviveBtn(final TransformComponent dialogTc) {
        final Entity reviveBtn = gameOverDialogE.getComponent(NodeComponent.class).getChild(BTN_WATCH_VIDEO);
        final Entity turnOnWifi = gameOverDialogE.getComponent(NodeComponent.class).getChild(LBL_TURN_ON_WIFI);
//        if (gameScript.fpc.settings.shouldShowReviveVideoBtnAd()) {
        turnOnWifi.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        reviveBtn.getComponent(TransformComponent.class).x = 240;
        reviveBtn.getComponent(TransformComponent.class).y = 80;
        reviveBtn.add(new ButtonComponent());
        reviveBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(reviveBtn) {
                    @Override
                    public void clicked() {
                        if (Main.mainController.isWifiConnected()) {
                            playVideoAd(dialogTc);
                        } else {
                            turnOnWifi.getComponent(TransformComponent.class).x = 127;
                            turnOnWifi.getComponent(TransformComponent.class).y = 45;
                            continueGame(dialogTc);

                        }
                        close(gameOverDialogE);
                    }
                });
    }

    public void continueGame(TransformComponent dialogTc) {
        isGameOver.set(false);
        dialogTc.x = -1000;
        gameOverTimer = 0;
        gameOverCounter = GAME_OVER_COUNT;
        isAngeredBeesMode = false;
        BugSystem.blowUpAllBugs = true;
        BugSystem.blowUpCounter = 10;
    }

    private void playVideoAd(final TransformComponent dialogTc) {
        if (Main.mainController.isWifiConnected()) {
            Main.mainController.showReviveVideoAd(new Runnable() {
                @Override
                public void run() {

                    continueGame(dialogTc);
                }
            });
        } else {
            System.out.println("Interstitial ad not (yet) loaded");
        }
    }

    public void update(float deltaTime) {

        fade(gameOverDialogE, isGameOver.get());
        if (isGameOver.get()) {
            final Entity gameOverTimerLbl = gameOverDialogE.getComponent(NodeComponent.class).getChild(LABEL_TIMER_GAMEOVER);
            final LabelComponent gameOverLblC = gameOverTimerLbl.getComponent(LabelComponent.class);

            final ActionComponent ac = new ActionComponent();
            Actions.checkInit();
            ac.dataArray.add(Actions.scaleTo(99, 99, 48, Interpolation.elastic));
            gameOverTimerLbl.add(ac);


            if (Gdx.input.justTouched() && tapCoolDown <= 0) {
                gameOverTimer = 1;
                tapCoolDown = TAP_COOL;
            }
            tapCoolDown--;

            gameOverTimer += deltaTime;
            if (gameOverTimer >= 1) {
                gameOverTimer = 0;
                gameOverLblC.text.replace(0, gameOverLblC.text.capacity(), String.valueOf(gameOverCounter--));
            }
            finishGame();
        } else {
            shadowE.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            gameOverDialogE.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        }
    }

    public void finishGame() {
        if (gameOverCounter <= 0) {
            if (!gameScript.fpc.canUsePhoenix()) {
                resetGameData();
                if (GoalFeedbackScreen.shouldShow &&
                        (gameScript.goalFeedbackScreen == null ||
                                !gameScript.goalFeedbackScreen.isGoalFeedbackOpen)) {
                    gameScript.showGoalFeedback();
                    close(gameOverDialogE);
                    isGameOver.set(true);
                } else if ((gameScript.goalFeedbackScreen == null ||
                        !gameScript.goalFeedbackScreen.isGoalFeedbackOpen) &&
                        (gameScript.giftScreen == null || !gameScript.giftScreen.isGiftScreenOpen)) {
                    isGameOver.set(false);
                    gameScript.resetPauseDialog();
                    gameScript.stage.initResultWithAds();
                    close(gameOverDialogE);
                }
            }
        }
    }

    public static void resetGameData() {
        gameOverTimer = 0;
        gameOverCounter = GAME_OVER_COUNT;
        isStarted = false;
        isPause.set(false);
        isAngeredBeesMode = false;
        BugSpawnSystem.queenBeeOnStage = false;
        if (gameScript.fpc.bestScore < gameScript.fpc.score) {
            gameScript.fpc.bestScore = gameScript.fpc.score;
        }
        gameScript.fpc.resetPhoenix();

        //reset goals with type "In one life"
        for (Goal g : gameScript.fpc.level.getGoals()) {
            if (!g.periodType.equals(Goal.PeriodType.TOTAL) && !g.achieved) {
                g.counter = 0;
            }
        }
    }
}
