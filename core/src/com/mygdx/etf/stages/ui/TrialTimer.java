package com.mygdx.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.mygdx.etf.entity.componets.Upgrade;
import com.mygdx.etf.stages.GameScreenScript;
import com.mygdx.etf.stages.GameStage;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.etf.stages.GameStage.sceneLoader;
import static com.mygdx.etf.stages.GameStage.*;
import static com.mygdx.etf.utils.GlobalConstants.*;

/**
 * Created by ARudyk on 7/3/2016.
 */
public class TrialTimer {

    private static final String TRIAL_TIMER = "timer_lbl";

    private Entity timerLogo;
    private ItemWrapper mainItem;

    public int x;
    public int y;

    public TrialTimer(ItemWrapper gameItem, int x, int y) {
        this.mainItem = gameItem;
        this.x = x;
        this.y = y;
    }

    public void timer() {
        GameScreenScript.checkTryPeriod();
        final Entity timerE = mainItem.getChild(TRIAL_TIMER).getEntity();
        if (!ifShouldShowTimer(timerE) && timerE.getComponent(TransformComponent.class).x !=FAR_FAR_AWAY_X) {
            PromoWindow.offerPromo = true;
            timerE.getComponent(TransformComponent.class).x =FAR_FAR_AWAY_X;
            if (timerLogo != null) {
                timerLogo.getComponent(TransformComponent.class).x =FAR_FAR_AWAY_X;
                GameStage.sceneLoader.getEngine().removeEntity(timerLogo);
            }
        }
    }

    private boolean ifShouldShowTimer(Entity timerE) {
        boolean showTimer = false;
        if (gameScript.fpc.currentPet != null && gameScript.fpc.currentPet.tryPeriod) {
            showTimer = true;
            showTimer(timerE, gameScript.fpc.currentPet.logoName);
            PromoWindow.offer = gameScript.fpc.currentPet;
        } else {
            for (Upgrade u : gameScript.fpc.upgrades.values()) {
                if (u.tryPeriod) {
                    showTimer = true;
                    PromoWindow.offer = u;
                    showTimer(timerE, u.logoName);
                }
            }
        }
        return showTimer;
    }

    private void showTimer(Entity timerE, String logoname) {
        LabelComponent lc = timerE.getComponent(LabelComponent.class);
        lc.text.replace(0, lc.text.length, gameScript.fpc.currentPet.updateTryPeriodTimer());
        addTimerLogo(logoname);
        timerE.getComponent(TransformComponent.class).x = x
                + timerLogo.getComponent(DimensionsComponent.class).width * timerLogo.getComponent(TransformComponent.class).scaleX;
        timerE.getComponent(TransformComponent.class).y = y + 15;
        timerE.getComponent(ZIndexComponent.class).setZIndex(105);
    }

    private void addTimerLogo(String logoLibName) {
        if (timerLogo == null) {
            final CompositeItemVO tempC = sceneLoader.loadVoFromLibrary(logoLibName);
            timerLogo = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempC);
            sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), timerLogo, tempC.composite);
            sceneLoader.getEngine().addEntity(timerLogo);
        }
        timerLogo.getComponent(TransformComponent.class).x = this.x;
        timerLogo.getComponent(TransformComponent.class).y = this.y;
        timerLogo.getComponent(TransformComponent.class).scaleX = 0.4f;
        timerLogo.getComponent(TransformComponent.class).scaleY = 0.4f;
        timerLogo.getComponent(ZIndexComponent.class).setZIndex(105);
    }
}