package com.fd.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.fd.etf.entity.componets.Upgrade;
import com.fd.etf.stages.GameStage;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;

/**
 * Created by ARudyk on 7/3/2016.
 */
public class TrialTimer {

    private static final String TRIAL_TIMER = "timer_lbl";
    public static final String TIMER_LBL_TIME_UP = "TIME'NEW_LINE_SIGN UP";
    private final GameStage gameStage;

    public Entity timerLogo;
    public Entity timerE;
    public ItemWrapper mainItem;

    public static String trialTimerLogoName;
    //to be deleted
    public int x;
    public int y;

    public TrialTimer(GameStage gameStage, ItemWrapper gameItem, int x, int y) {
        this.mainItem = gameItem;
        this.gameStage = gameStage;
        this.x = x;
        this.y = y;
    }

    public void update() {
        gameStage.gameScript.checkTryPeriod();
        timerE = mainItem.getChild(TRIAL_TIMER).getEntity();//
        if (!ifShouldShowTimer()) {
            timerE.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            if (timerLogo != null) {
                timerLogo.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            }
        }
        if (ifShouldShowTimer()) {
            showTimer();
            timerE.getComponent(TransformComponent.class).y = y + 15;
        }
    }

    public boolean ifShouldShowTimer() {
        boolean showTimer = false;
        if (gameStage.gameScript.fpc.currentPet != null && gameStage.gameScript.fpc.currentPet.tryPeriod) {
            showTimer = true;
            TrialTimer.trialTimerLogoName = gameStage.gameScript.fpc.currentPet.shopIcon;
            PromoWindow.offer = gameStage.gameScript.fpc.currentPet;
            PromoWindow.offerPromo = true;
        } else {
            for (Upgrade u : gameStage.gameScript.fpc.upgrades.values()) {
                if (u.tryPeriod) {
                    showTimer = true;
                    TrialTimer.trialTimerLogoName = u.shopIcon;
                    PromoWindow.offer = u;
                    PromoWindow.offerPromo = true;
                }
            }
        }
        return showTimer;
    }

    private void showTimer() {
        if (timerE != null) {
            LabelComponent lc = timerE.getComponent(LabelComponent.class);
            if (gameStage.gameScript.fpc.currentPet != null && gameStage.gameScript.fpc.currentPet.tryPeriod) {
                lc.text.replace(0, lc.text.length, gameStage.gameScript.fpc.currentPet.updateTryPeriodTimer());
            }
            for (Upgrade u : gameStage.gameScript.fpc.upgrades.values()) {
                if (u.tryPeriod) {
                    lc.text.replace(0, lc.text.length, u.updateTryPeriodTimer());
                }
            }


            timerE.getComponent(TransformComponent.class).x = this.x;
            timerE.getComponent(TransformComponent.class).y = this.y;
            if (trialTimerLogoName != null && !"".equals(trialTimerLogoName)) {
                addTimerLogo(trialTimerLogoName);
            }

            timerE.getComponent(ZIndexComponent.class).setZIndex(531);
        }
    }

    private void addTimerLogo(String logoLibName) {
        if (timerLogo == null) {
            final CompositeItemVO tempC = gameStage.sceneLoader.loadVoFromLibrary(logoLibName);
            timerLogo = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), tempC);
            gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), timerLogo, tempC.composite);
            gameStage.sceneLoader.getEngine().addEntity(timerLogo);
        }
        timerLogo.getComponent(TransformComponent.class).x = this.x - 55;
        timerLogo.getComponent(TransformComponent.class).y = this.y - 5;
        timerLogo.getComponent(TransformComponent.class).scaleX = 0.7f;
        timerLogo.getComponent(TransformComponent.class).scaleY = 0.7f;
        timerLogo.getComponent(ZIndexComponent.class).setZIndex(531);
    }
}
