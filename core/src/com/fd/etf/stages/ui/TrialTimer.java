package com.fd.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.fd.etf.entity.componets.Upgrade;
import com.fd.etf.stages.GameScreenScript;
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
    private static final String TRIAL_TIMER_SH = "timer_lbl_sh";
    public static final String TIMER_LBL_TIME_UP = "TIME'NEW_LINE_SIGN UP";
    private final GameStage gameStage;

    public Entity timerLogo;
    public Entity timerE;
    public Entity timerEsh;
    public ItemWrapper mainItem;

    //to be deleted
    public int x;
    public int y;

    public TrialTimer(GameStage gameStage, ItemWrapper gameItem, int x, int y) {
        this.mainItem = gameItem;
        this.gameStage= gameStage;
        this.x = x;
        this.y = y;
    }

    public void timer() {
        gameStage.gameScript.checkTryPeriod();
        timerE = mainItem.getChild(TRIAL_TIMER).getEntity();
        timerEsh = mainItem.getChild(TRIAL_TIMER_SH).getEntity();
        if (!ifShouldShowTimer()) {

            timerE.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            if (timerLogo != null) {
                timerLogo.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
                gameStage.sceneLoader.getEngine().removeEntity(timerLogo);
            }
        }
        if (ifShouldShowTimer()) {
            timerE.getComponent(TransformComponent.class).x = x + timerLogo.getComponent(DimensionsComponent.class).width * timerLogo.getComponent(TransformComponent.class).scaleX;
            timerE.getComponent(TransformComponent.class).y = y + 15;
            timerEsh.getComponent(TransformComponent.class).x = x + timerLogo.getComponent(DimensionsComponent.class).width * timerLogo.getComponent(TransformComponent.class).scaleX;
            timerEsh.getComponent(TransformComponent.class).y = y + 15;
        } else {
            timerE.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            timerEsh.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        }
    }

    public boolean ifShouldShowTimer() {
        boolean showTimer = false;
        if (gameStage.gameScript.fpc.currentPet != null && gameStage.gameScript.fpc.currentPet.tryPeriod) {
            showTimer = true;
            showTimer(timerE, timerEsh, gameStage.gameScript.fpc.currentPet.shopIcon);
            PromoWindow.offer = gameStage.gameScript.fpc.currentPet;
            PromoWindow.offerPromo = true;
        } else {
            for (Upgrade u : gameStage.gameScript.fpc.upgrades.values()) {
                if (u.tryPeriod) {
                    showTimer = true;
                    PromoWindow.offer = u;
                    PromoWindow.offerPromo = true;
                    showTimer(timerE, timerEsh, u.shopIcon);
                }
            }
        }
        return showTimer;
    }

    private void showTimer(Entity timerE, Entity timerEsh, String logoname) {
        if (timerE != null) {
            LabelComponent lc = timerE.getComponent(LabelComponent.class);
            lc.text.replace(0, lc.text.length, gameStage.gameScript.fpc.currentPet.updateTryPeriodTimer());
            addTimerLogo(logoname);
//            timerE.getComponent(TransformComponent.class).x = x
//                    + timerLogo.getComponent(DimensionsComponent.class).width * timerLogo.getComponent(TransformComponent.class).scaleX;
//            timerE.getComponent(TransformComponent.class).y = y + 15;
            timerE.getComponent(ZIndexComponent.class).setZIndex(31);

            LabelComponent lcSh = timerEsh.getComponent(LabelComponent.class);
            lcSh.text.replace(0, lcSh.text.length, gameStage.gameScript.fpc.currentPet.updateTryPeriodTimer());
            timerEsh.getComponent(ZIndexComponent.class).setZIndex(32);
        }
    }

    private void addTimerLogo(String logoLibName) {
        if (timerLogo == null) {
            final CompositeItemVO tempC = gameStage.sceneLoader.loadVoFromLibrary(logoLibName);
            timerLogo = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), tempC);
            gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), timerLogo, tempC.composite);
            gameStage.sceneLoader.getEngine().addEntity(timerLogo);
        }
        timerLogo.getComponent(TransformComponent.class).x = this.x + 190;
        timerLogo.getComponent(TransformComponent.class).y = this.y;
        timerLogo.getComponent(TransformComponent.class).scaleX = 0.7f;
        timerLogo.getComponent(TransformComponent.class).scaleY = 0.7f;
        timerLogo.getComponent(ZIndexComponent.class).setZIndex(10);
    }
}
