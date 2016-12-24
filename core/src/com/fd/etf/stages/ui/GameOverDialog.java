package com.fd.etf.stages.ui;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.fd.etf.Main;
import com.fd.etf.entity.componets.FlowerComponent;
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

public class GameOverDialog extends AbstractDialog {

    public static final int TAP_COOL = 30;
    private static final int GAME_OVER_X = 300;
    private static final int GAME_OVER_Y = 150;
    private static final String GAME_OVER_DIALOG = "game_over_lib";
    private static final String BTN_WATCH_VIDEO = "btn_watch_video";
    private static final String LABEL_TIMER_GAMEOVER = "label_timer_gameover";
    private static final String LABEL_TIMER_GAMEOVER_SH = "label_timer_gameover_sh";
    private static final String TIMER = "timer_composite";

    public static float gameOverTimer = 0;
    public static final int GAME_OVER_COUNT = 5;
    public static int gameOverCounter;
    private int tapCoolDown = TAP_COOL;

    public static Entity gameOverDialogE;

    public static boolean releaseAllBugs() {
        return (isGameOver.get() && gameOverCounter <= 0 && !BugSystem.blowUpAllBugs);
    }

    public void show() {
        hide();
        addShadow();
        isActive = true;
        isGameOver.set(true);
        System.gc();

        ActionComponent ac = new ActionComponent();
        Actions.checkInit();
        ac.dataArray.add(Actions.moveTo(GAME_OVER_X, GAME_OVER_Y, POPUP_MOVE_DURATION, Interpolation.exp10Out));
        gameOverDialogE.add(ac);

        gameOverDialogE.getComponent(ZIndexComponent.class).setZIndex(shadowE.getComponent(ZIndexComponent.class).getZIndex() + 1);

        tapCoolDown = TAP_COOL;
        gameOverTimer = 0;
        gameOverCounter = GAME_OVER_COUNT;

        Entity gameOverTimerLbl = gameOverDialogE.getComponent(NodeComponent.class).getChild(TIMER).getComponent(NodeComponent.class).getChild(LABEL_TIMER_GAMEOVER);
        LabelComponent gameOverLblC = gameOverTimerLbl.getComponent(LabelComponent.class);
        gameOverLblC.text.replace(0, gameOverLblC.text.capacity(), Integer.toString(GAME_OVER_COUNT));
        gameOverTimerLbl.getComponent(ZIndexComponent.class).setZIndex(gameOverDialogE.getComponent(ZIndexComponent.class).getZIndex() + 1);

        Entity gameOverTimerLblsh = gameOverDialogE.getComponent(NodeComponent.class).getChild(TIMER).getComponent(NodeComponent.class).getChild(LABEL_TIMER_GAMEOVER_SH);
        LabelComponent gameOverLblCsh = gameOverTimerLblsh.getComponent(LabelComponent.class);
        gameOverLblCsh.text.replace(0, gameOverLblCsh.text.capacity(), Integer.toString(GAME_OVER_COUNT));
        gameOverTimerLblsh.getComponent(ZIndexComponent.class).setZIndex(gameOverDialogE.getComponent(ZIndexComponent.class).getZIndex() + 2);
    }

    public void hide() {
        gameOverDialogE.getComponent(TransformComponent.class).x = GAME_OVER_X;
        gameOverDialogE.getComponent(TransformComponent.class).y = HIDE_Y;
    }

    public void initGameOverDialog() {
        initShadow();

        CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary(GAME_OVER_DIALOG).clone();
        gameOverDialogE = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
        GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), gameOverDialogE, tempItemC.composite);
        GameStage.sceneLoader.getEngine().addEntity(gameOverDialogE);
        hide();

        gameOverDialogE.getComponent(ZIndexComponent.class).setZIndex(shadowE.getComponent(ZIndexComponent.class).getZIndex() + 1);
        initReviveBtn(gameOverDialogE.getComponent(TransformComponent.class));
    }

    private void initReviveBtn(final TransformComponent dialogTc) {
        final Entity reviveBtn = gameOverDialogE.getComponent(NodeComponent.class).getChild(BTN_WATCH_VIDEO);
//        if (gameScript.fpc.settings.shouldShowReviveVideoBtnAd()) {
//        reviveBtn.getComponent(TransformComponent.class).x = 240;
//        reviveBtn.getComponent(TransformComponent.class).y = 80;
        reviveBtn.add(new ButtonComponent());
        reviveBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(reviveBtn) {
                    @Override
                    public void clicked() {
                        if (Main.mainController.isWifiConnected()) {
                            playVideoAd(dialogTc);
                        }
                        if (Gdx.app.getType().equals(Application.ApplicationType.Desktop)){
                            continueGame(dialogTc);
                        }
                        close(gameOverDialogE);
                    }
                });
    }

    public void continueGame(TransformComponent dialogTc) {
        isGameOver.set(false);
        isPause.set(false);
        gameOverTimer = 0;
        gameOverCounter = GAME_OVER_COUNT;
        isAngeredBeesMode = false;
        BugSystem.blowUpAllBugs = true;
        BugSystem.blowUpCounter = 10;
        FlowerComponent.state = FlowerComponent.State.REVIVE_ADS;
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
            final Entity gameOverTimerLbl = gameOverDialogE.getComponent(NodeComponent.class).getChild(TIMER).getComponent(NodeComponent.class).getChild(LABEL_TIMER_GAMEOVER);
            final Entity gameOverTimerLblsh = gameOverDialogE.getComponent(NodeComponent.class).getChild(TIMER).getComponent(NodeComponent.class).getChild(LABEL_TIMER_GAMEOVER_SH);
            final LabelComponent gameOverLblC = gameOverTimerLbl.getComponent(LabelComponent.class);
            final LabelComponent gameOverLblCsh = gameOverTimerLblsh.getComponent(LabelComponent.class);

            final ActionComponent ac = new ActionComponent();
            Actions.checkInit();
            ac.dataArray.add(Actions.scaleTo(99, 99, 48, Interpolation.elastic));
            gameOverTimerLbl.add(ac);
            gameOverTimerLblsh.add(ac);


            if (Gdx.input.justTouched() && tapCoolDown <= 0) {
                gameOverTimer = 1;
                tapCoolDown = TAP_COOL;
            }
            tapCoolDown--;

            gameOverTimer += deltaTime;
            if (gameOverTimer >= 1) {
                gameOverTimer = 0;
                gameOverLblC.text.replace(0, gameOverLblC.text.capacity(), String.valueOf(gameOverCounter--));
                gameOverLblCsh.text.replace(0, gameOverLblCsh.text.capacity(), gameOverLblC.getText().toString());
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
                                !GoalFeedbackScreen.isGoalFeedbackOpen)) {
                    gameScript.showGoalFeedback();
                    close(gameOverDialogE);
                    isGameOver.set(true);
                } else if ((gameScript.goalFeedbackScreen == null ||
                        !GoalFeedbackScreen.isGoalFeedbackOpen)) {
                    isGameOver.set(false);
                    gameScript.resetPauseDialog();
                    hide();
                    gameScript.stage.initResultWithAds();
//                    close(gameOverDialogE);
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
