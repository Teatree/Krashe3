package com.fd.etf.stages.ui;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.fd.etf.Main;
import com.fd.etf.entity.componets.FlowerComponent;
import com.fd.etf.entity.componets.Goal;
import com.fd.etf.entity.componets.listeners.ImageButtonListener;
import com.fd.etf.stages.GameScreenScript;
import com.fd.etf.stages.GameStage;
import com.fd.etf.system.BugSpawnSystem;
import com.fd.etf.system.BugSystem;
import com.fd.etf.utils.BackgroundMusicMgr;
import com.fd.etf.utils.SoundMgr;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;

import static com.fd.etf.stages.GameScreenScript.*;
import static com.fd.etf.utils.EffectUtils.fade;
import static com.fd.etf.utils.GlobalConstants.ANGERED_BEES_MODE_DURATION;
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

    public GameOverDialog(GameStage gameStage){
        super(gameStage);
    }

    public void show() {
        hide();
        addShadow();
        isActive = true;
        isGameOver.set(true);

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

        if(BackgroundMusicMgr.getBackgroundMusicMgr().musicMenu.isPlaying()){
            BackgroundMusicMgr.getBackgroundMusicMgr().musicMenu.setVolume(0.05f);
        }
        if(BackgroundMusicMgr.getBackgroundMusicMgr().musicGame.isPlaying()){
            BackgroundMusicMgr.getBackgroundMusicMgr().musicGame.setVolume(0.05f);
        }
    }

    public void hide() {
        gameOverDialogE.getComponent(TransformComponent.class).x = GAME_OVER_X;
        gameOverDialogE.getComponent(TransformComponent.class).y = HIDE_Y;
    }

    public static boolean releaseAllBugs() {
        return (isGameOver.get() && gameOverCounter <= 0 && !BugSystem.blowUpAllBugs);
    }

    public void initGameOverDialog() {
        initShadow();

        CompositeItemVO tempItemC = gameStage.sceneLoader.loadVoFromLibrary(GAME_OVER_DIALOG).clone();
        gameOverDialogE = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), tempItemC);
        gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), gameOverDialogE, tempItemC.composite);
        gameStage.sceneLoader.getEngine().addEntity(gameOverDialogE);
        hide();

        gameOverDialogE.getComponent(ZIndexComponent.class).setZIndex(shadowE.getComponent(ZIndexComponent.class).getZIndex() + 1);
        initReviveBtn();
    }

    private void initReviveBtn() {
        final Entity reviveBtn = gameOverDialogE.getComponent(NodeComponent.class).getChild(BTN_WATCH_VIDEO);
//        if (gameScript.fpc.settings.shouldShowReviveVideoBtnAd()) {
//        reviveBtn.getComponent(TransformComponent.class).x = 240;
//        reviveBtn.getComponent(TransformComponent.class).y = 80;
        reviveBtn.add(new ButtonComponent());
        reviveBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(reviveBtn) {
                    @Override
                    public void clicked() {
                        gameStage.gameScript.isSameSession = true;
                        if (Main.mainController.isWifiConnected()) {
                            playVideoAd();
                            gameOverReviveTimesLimit--;
                        }
                        if (Gdx.app.getType().equals(Application.ApplicationType.Desktop)){
//                            continueGame(dialogTc);
                        }
                        gameStage.gameScript.loseFeedback.getComponent(TintComponent.class).color.a = 0;
                        close(gameOverDialogE);
                    }
                });
    }

    public void continueGame() {
        isGameOver.set(false);
        isPause.set(false);
        gameOverTimer = 0;
        gameOverCounter = 0;
        isAngeredBeesMode = false;

        angeredBeesModeTimer = ANGERED_BEES_MODE_DURATION;

        ActionComponent ac3 = new ActionComponent();
        ac3.dataArray.add(Actions.sequence(
                Actions.delay(0.5f),
                Actions.moveTo(-155, 413, 1f, Interpolation.exp5),
                Actions.scaleTo(1, 1, 3f)));
        GameScreenScript.beesAngryFeedbackE.add(ac3);
        SoundMgr.getSoundMgr().stop(SoundMgr.BEES);

//        BugSystem.blowUpAllBugs = true;
//        BugSystem.blowUpCounter = GlobalConstants.BEES_MODE_BLOW_UP_LENGTH;
        BugSystem.blowUpAllBugs();
        FlowerComponent.state = FlowerComponent.State.REVIVE_ADS;
        SoundMgr.getSoundMgr().play(SoundMgr.REVIVE);

        if(BackgroundMusicMgr.getBackgroundMusicMgr().musicMenu.isPlaying()){
            BackgroundMusicMgr.getBackgroundMusicMgr().musicMenu.setVolume(0.2f);
        }
        if(BackgroundMusicMgr.getBackgroundMusicMgr().musicGame.isPlaying()){
            BackgroundMusicMgr.getBackgroundMusicMgr().musicGame.setVolume(0.2f);
        }
    }

    private void playVideoAd() {
        if (Main.mainController.isWifiConnected()) {
            Main.mainController.showReviveVideoAd(new Runnable() {
                @Override
                public void run() {
                    continueGame();
                }
            });
        } else {
            //System.out.println("Interstitial ad not (yet) loaded");
        }
    }

    public void update(float deltaTime) {

//        System.out.println("gameOverCounter: " + gameOverCounter + " -- gameOverTimer: " + gameOverTimer);

        fade(gameOverDialogE, isGameOver.get());
        if (isGameOver.get()) {
            final Entity gameOverTimerLbl = gameOverDialogE.getComponent(NodeComponent.class).getChild(TIMER).getComponent(NodeComponent.class).getChild(LABEL_TIMER_GAMEOVER);
            final LabelComponent gameOverLblC = gameOverTimerLbl.getComponent(LabelComponent.class);

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
            gameStage.gameScript.fpc.totalScore += gameStage.gameScript.fpc.score;
            //System.out.println("gameStage.gameScript.fpc.totalScore: " + gameStage.gameScript.fpc.totalScore);
            //System.out.println("gameStage.gameScript.fpc.score: " + gameStage.gameScript.fpc.score);
            gameStage.gameScript.submitScoreToGooglePlay();
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
                    gameStage.gameScript.curtainGameE.getComponent(ActionComponent.class).reset();
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
//        isStarted = false;
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
