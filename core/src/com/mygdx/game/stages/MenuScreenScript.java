package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.mygdx.game.entity.componets.Upgrade;
import com.mygdx.game.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.game.stages.GameScreenScript.fpc;
import static com.mygdx.game.utils.GlobalConstants.BUTTON_TAG;

public class MenuScreenScript implements IScript {

    public static final String BTN_PLAY = "btn_play";
    public static final String BTN_SHOP = "btn_shop";
    public static final String BTN_NO_ADS = "btn_noAds";
    public static final String TRIAL_TIMER = "trial_timer";
    ItemWrapper menuItem;
    private GameStage stage;

    public MenuScreenScript(GameStage stage) {
        this.stage = stage;
    }


    @Override
    public void init(Entity item) {
        menuItem = new ItemWrapper(item);

        GameStage.sceneLoader.addComponentsByTagName(BUTTON_TAG, ButtonComponent.class);
        final Entity playBtn = menuItem.getChild(BTN_PLAY).getEntity();
        final Entity btnShop = menuItem.getChild(BTN_SHOP).getEntity();
        final Entity btnNoAds = menuItem.getChild(BTN_NO_ADS).getEntity();

        btnNoAds.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
            }

            @Override
            public void touchDown() {
            }

            @Override
            public void clicked() {
                GameScreenScript.fpc.settings.noAds = true;
            }
        });
        playBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {

            @Override
            public void touchUp() {
            }

            @Override
            public void touchDown() {
            }

            @Override
            public void clicked() {
                stage.initGame();
            }
        });
        btnShop.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {

            }

            @Override
            public void touchDown() {
                stage.initShop();
            }

            @Override
            public void clicked() {

            }
        });
    }

    private void timer(ItemWrapper menuItem) {
        final Entity timerE = menuItem.getChild(TRIAL_TIMER).getEntity();
        LabelComponent lc = timerE.getComponent(LabelComponent.class);
        boolean tryPeriod = false;
        if (fpc.currentPet != null && fpc.currentPet.tryPeriod) {
            lc.text.replace(0, lc.text.length, fpc.currentPet.updateTryPeriodTimer());
            tryPeriod = true;
        }
        for (Upgrade u : fpc.upgrades.values()) {
            if (u.tryPeriod) {
                lc.text.replace(0, lc.text.length, u.updateTryPeriodTimer());
                tryPeriod = true;
            }
        }
        if (!tryPeriod) {
            timerE.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    public void act(float delta) {
        if (fpc.currentPet != null && fpc.currentPet.tryPeriod) {
            long now = System.currentTimeMillis();
            if (now - fpc.currentPet.tryPeriodStart >= fpc.currentPet.tryPeriodDuration * 1000) {
                fpc.currentPet = null;
            }
        }
        timer(menuItem);
    }
}
