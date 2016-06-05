package com.mygdx.game.stages.ui;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.mygdx.game.Main;
import com.mygdx.game.entity.componets.Goal;
import com.mygdx.game.entity.componets.Upgrade;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.system.BugSpawnSystem;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.game.stages.GameScreenScript.*;
import static com.mygdx.game.stages.GameStage.gameScript;
import static com.mygdx.game.utils.EffectUtils.fade;
import static com.mygdx.game.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.mygdx.game.utils.GlobalConstants.FAR_FAR_AWAY_Y;
import static com.mygdx.game.Main.*;

public class GameOverDialog {

    public static final int TAP_COOL = 30;
    public final String GAME_OVER_DIALOG = "game_over_dialog";
    public final String BTN_WATCH_VIDEO = "btn_watch_video";
    public final String LBL_TURN_ON_WIFI = "lbl_turn_on_wifi";
    public final String LABEL_TIMER_GAMEOVER = "label_timer_gameover";

    public static float gameOverTimer = 0;
    public static final int GAME_OVER_COUNT = 5;
    public static int gameOverCounter = GAME_OVER_COUNT;
    private int tapCoolDown = TAP_COOL;

    private static ItemWrapper gameItem;
    public static Entity gameOverDialog;


    public GameOverDialog(ItemWrapper gameItem) {
        GameOverDialog.gameItem = gameItem;
    }

    public static boolean releaseAllBugs() {
        return isGameOver && gameOverCounter <= 0 && !Upgrade.blowUpAllBugs;
    }

    public void show() {
        isGameOver = true;
        System.gc();
        final TransformComponent dialogTc = gameOverDialog.getComponent(TransformComponent.class);
        dialogTc.x = 300;
        dialogTc.y = 100;

        tapCoolDown = TAP_COOL;
        gameOverTimer = 0;
        gameOverCounter = GAME_OVER_COUNT;

        Entity gameOverTimerLbl = gameItem.getChild(GAME_OVER_DIALOG).getChild(LABEL_TIMER_GAMEOVER).getEntity();
        LabelComponent gameOverLblC = gameOverTimerLbl.getComponent(LabelComponent.class);
        gameOverLblC.text.replace(0, gameOverLblC.text.capacity(), Integer.toString(GAME_OVER_COUNT));

        TintComponent tc = gameOverTimerLbl.getComponent(TintComponent.class);
        tc.color.a = 0;
    }

    public void initGameOverDialog() {
        gameOverDialog = gameItem.getChild(GAME_OVER_DIALOG).getEntity();
        final TransformComponent dialogTc = gameOverDialog.getComponent(TransformComponent.class);
        dialogTc.x = FAR_FAR_AWAY_X;
        dialogTc.y = FAR_FAR_AWAY_Y;

        initReviveBtn(dialogTc);
    }

    private void initReviveBtn(final TransformComponent dialogTc) {
        final Entity reviveBtn = gameItem.getChild(GAME_OVER_DIALOG).getChild(BTN_WATCH_VIDEO).getEntity();
        final Entity turnOnWifi = gameItem.getChild(GAME_OVER_DIALOG).getChild(LBL_TURN_ON_WIFI).getEntity();
        if (gameScript.fpc.settings.shouldShowReviveVideoBtnAd()) {
            turnOnWifi.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            reviveBtn.getComponent(TransformComponent.class).x = 210;
            reviveBtn.getComponent(TransformComponent.class).y = 80;
            reviveBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
                @Override
                public void touchUp() {
                }

                @Override
                public void touchDown() {
                }

                @Override
                public void clicked() {
                    if (Main.adsController.isWifiConnected()) {
                        playVideoAd(dialogTc);
                    } else {
                        turnOnWifi.getComponent(TransformComponent.class).x = 127;
                        turnOnWifi.getComponent(TransformComponent.class).y = 45;
                        continueGame(dialogTc);
                    }
                }
            });
        } else {
            turnOnWifi.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            reviveBtn.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        }
    }

    public void continueGame(TransformComponent dialogTc) {
        isGameOver = false;
        dialogTc.x = -1000;
        gameOverTimer = 0;
        gameOverCounter = GAME_OVER_COUNT;
        BugSpawnSystem.isAngeredBeesMode = false;
    }

    private void playVideoAd(final TransformComponent dialogTc) {
        if (Main.adsController.isWifiConnected()) {
            Main.adsController.showReviveVideoAd(new Runnable() {
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

        fade(gameOverDialog, isGameOver);
        if (isGameOver) {
            final Entity gameOverTimerLbl = gameItem.getChild(GAME_OVER_DIALOG).getChild(LABEL_TIMER_GAMEOVER).getEntity();
            final LabelComponent gameOverLblC = gameOverTimerLbl.getComponent(LabelComponent.class);

            final ActionComponent ac = new ActionComponent();
            Actions.checkInit();
            ac.dataArray.add(Actions.scaleTo(99, 99, 48, Interpolation.elastic));
            gameOverTimerLbl.add(ac);


            if(Gdx.input.justTouched() && tapCoolDown <= 0){
                gameOverTimer = 1;
                tapCoolDown = TAP_COOL;
            }
            tapCoolDown--;

            gameOverTimer += deltaTime ;
            if (gameOverTimer >= 1){
                gameOverTimer =0;
                gameOverLblC.text.replace(0, gameOverLblC.text.capacity(), String.valueOf(gameOverCounter--));
            }
            finishGame();
        }
    }

    private void finishGame() {
        if (gameOverCounter <= 0) {
            if (!GameStage.gameScript.fpc.canUsePhoenix()) {
                resetGameData();
                if (GoalFeedbackScreen.shouldShow && !gameScript.goalFeedbackScreen.isGoalFeedbackOpen) {
                    gameScript.goalFeedbackScreen.show();
                    isGameOver = true;
                } else if (!gameScript.goalFeedbackScreen.isGoalFeedbackOpen && !gameScript.giftScreen.isGiftScreenOpen) {
                    isGameOver = false;
                    gameScript.resetPauseDialog();
                    gameScript.stage.initResultWithAds();
                }
            }
        }
    }

    private void resetGameData() {
        gameOverTimer = 0;
        gameOverCounter = GAME_OVER_COUNT;
        isStarted = false;
        isPause = false;
        BugSpawnSystem.isAngeredBeesMode = false;
        BugSpawnSystem.queenBeeOnStage = false;
        if (GameStage.gameScript.fpc.bestScore < GameStage.gameScript.fpc.score) {
            GameStage.gameScript.fpc.bestScore = GameStage.gameScript.fpc.score;
        }
        GameStage.gameScript.fpc.resetPhoenix();

        //reset goals with type "In one life"
        for (Goal g : GameStage.gameScript.fpc.level.getGoals()) {
            if (!g.periodType.equals(Goal.PeriodType.TOTAL) && !g.achieved) {
                g.counter = 0;
            }
        }
    }
}
