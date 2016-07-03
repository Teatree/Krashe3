package com.mygdx.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.mygdx.etf.entity.componets.Upgrade;
import com.mygdx.etf.stages.GameScreenScript;
import com.mygdx.etf.stages.GameStage;
import com.mygdx.etf.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.etf.stages.GameStage.sceneLoader;
import static com.mygdx.etf.stages.GameStage.*;

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
        boolean showTimer = false;
        if (gameScript.fpc.currentPet != null && gameScript.fpc.currentPet.tryPeriod) {
            showTimer = true;
            showTimer(timerE, gameScript.fpc.currentPet.logoName);
        } else {
            for (Upgrade u : gameScript.fpc.upgrades.values()) {
                if (u.tryPeriod) {
                    showTimer = true;
                    showTimer(timerE, u.logoName);
                }
            }
        }
        if (!showTimer) {
            timerE.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
            if (timerLogo != null) {
                timerLogo.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
                GameStage.sceneLoader.getEngine().removeEntity(timerLogo);
            }
        }
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
