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
import com.fd.etf.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;

import static com.fd.etf.stages.GameScreenScript.*;
import static com.fd.etf.utils.EffectUtils.fade;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;

public class GameOverDialog extends AbstractDialog {

    public static final int TAP_COOL = 30;
    private static final int GAME_OVER_X = 300;
    private static final int GAME_OVER_Y = 150;
    private static final String GAME_OVER_DIALOG = "game_over_lib";
    private static final String BTN_WATCH_VIDEO = "btn_watch_video";
    private static final String LABEL_TIMER_GAMEOVER = "label_timer_gameover";
    private static final String TIMER = "timer_composite";

    public static float gameOverTimer = 0;
    public static final int GAME_OVER_COUNT = 5;
    public static int gameOverCounter;
    private int tapCoolDown = TAP_COOL;

    public static Entity gameOverDialogE;

    public static boolean releaseAllBugs() {
        return (isGameOver.get() && gameOverCounter <= 0 && !BugSystem.blowUpAllBugs);
    }

    public GameOverDialog(GameStage gameStage){
        super(gameStage);
    }

    public void show() {
        hide();
        addShadow();
        isActive = true;
        isGameOver.set(true);

        ActionComponent ac = gameStage.sceneLoader.engine.createComponent(ActionComponent.class);
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
    }

    public void hide() {
        gameOverDialogE.getComponent(TransformComponent.class).x = GAME_OVER_X;
        gameOverDialogE.getComponent(TransformComponent.class).y = HIDE_Y;
    }

    public void initGameOverDialog() {
        initShadow();

        CompositeItemVO tempItemC = gameStage.sceneLoader.loadVoFromLibrary(GAME_OVER_DIALOG).clone();
        gameOverDialogE = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), tempItemC);
        gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), gameOverDialogE, tempItemC.composite);
        gameStage.sceneLoader.getEngine().addEntity(gameOverDialogE);
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
                        gameStage.gameScript.loseFeedback.getComponent(TintComponent.class).color.a = 0;
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
//        BugSystem.blowUpAllBugs = true;
//        BugSystem.blowUpCounter = GlobalConstants.BEES_MODE_BLOW_UP_LENGTH;
        BugSystem.blowUpAllBugs();
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
            final LabelComponent gameOverLblC = gameOverTimerLbl.getComponent(LabelComponent.class);

//            final ActionComponent ac = gameStage.sceneLoader.engine.createComponent(ActionComponent.class);
//            Actions.checkInit();
//            ac.dataArray.add(Actions.scaleTo(99, 99, 48, Interpolation.elastic));
//            gameOverTimerLbl.add(ac);
//            gameOverTimerLblsh.add(ac);


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

        if(gameStage.gameScript.curtainGameE.getComponent(TintComponent.class).color.a >= 1) {
            isGameOver.set(false);
            gameStage.initResultWithAds();
        }
    }

    public void finishGame() {
        if (gameOverCounter+1 <= 0) {
            if (!gameStage.gameScript.fpc.canUsePhoenix()) {
                resetGameData();
                if (GoalFeedbackScreen.shouldShow &&
                        (gameStage.gameScript.goalFeedbackScreen == null ||
                                !GoalFeedbackScreen.isGoalFeedbackOpen)) {
                    gameStage.gameScript.showGoalFeedback();
                    close(gameOverDialogE);
                    isGameOver.set(true);
                } else if ((gameStage.gameScript.goalFeedbackScreen == null ||
                        !GoalFeedbackScreen.isGoalFeedbackOpen)) {
                    gameStage.gameScript.resetPauseDialog();
                    if (gameStage.gameScript.curtainGameE.getComponent(ActionComponent.class) == null) {
                        gameStage.gameScript.curtainGameE.add(new ActionComponent());
                    }
                    gameStage.gameScript.curtainGameE.getComponent(ActionComponent.class).dataArray.add(Actions.fadeIn(0.3f));
                    gameStage.gameScript.curtainGameE.getComponent(ZIndexComponent.class).setZIndex(200);
//                    hide();
//                    close(gameOverDialogE);
                }
            }
        }
    }

    public void resetGameData() {
        gameOverTimer = 0;
        gameOverCounter = GAME_OVER_COUNT;
        isStarted = false;
        isPause.set(false);
        isAngeredBeesMode = false;
        BugSpawnSystem.queenBeeOnStage = false;
        if (gameStage.gameScript.fpc.bestScore < gameStage.gameScript.fpc.score) {
            gameStage.gameScript.fpc.bestScore = gameStage.gameScript.fpc.score;
        }
        gameStage.gameScript.fpc.resetPhoenix();

        //reset goals with type "In one life"
        for (Goal g : gameStage.gameScript.fpc.level.getGoals()) {
            if (!g.periodType.equals(Goal.PeriodType.TOTAL) && !g.achieved) {
                g.counter = 0;
            }
        }
    }
}
