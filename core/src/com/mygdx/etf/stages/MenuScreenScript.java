package com.mygdx.etf.stages;

import com.badlogic.ashley.core.Entity;
import com.mygdx.etf.Main;
import com.mygdx.etf.entity.componets.Upgrade;
import com.mygdx.etf.stages.ui.Settings;
import com.mygdx.etf.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.etf.utils.GlobalConstants.BUTTON_TAG;
import static com.mygdx.etf.stages.GameStage.*;

public class MenuScreenScript implements IScript {

    public static final String BTN_PLAY = "btn_play";
    public static final String BTN_SHOP = "btn_shop";
    public static final String BTN_NO_ADS = "btn_noAds";
    public static final String BTN_SETTINGS = "btn_settings";
    public static final String TRIAL_TIMER = "trial_timer";
    public static final String CURTAIN = "curtain_mm";

    ItemWrapper menuItem;
    private GameStage stage;

    private Settings settings;

    //Dima's party time
    Entity curtain_mm;
    boolean startGameTransition;
    boolean startShopTransition;
    boolean startTransitionIn;

    public MenuScreenScript(GameStage stage) {
        GameStage.sceneLoader.addComponentsByTagName(BUTTON_TAG, ButtonComponent.class);
        this.stage = stage;
    }


    @Override
    public void init(Entity item) {
//        System.err.print("init menu ");
//        System.err.println(Gdx.app.getJavaHeap() / 1000000 + " : " +
//                Gdx.app.getNativeHeap());

        menuItem = new ItemWrapper(item);
        curtain_mm = menuItem.getChild(CURTAIN).getEntity();
        curtain_mm.getComponent(TintComponent.class).color.a = 1f;
        startGameTransition = false;
        startShopTransition = false;
        startTransitionIn = true;

        settings = new Settings(menuItem);
        settings.init();
    }

    public void initButtons() {
        Entity playBtn = menuItem.getChild(BTN_PLAY).getEntity();
        Entity btnShop = menuItem.getChild(BTN_SHOP).getEntity();
        Entity btnNoAds = menuItem.getChild(BTN_NO_ADS).getEntity();
        Entity btnSettings = menuItem.getChild(BTN_SETTINGS).getEntity();

        btnNoAds.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
            }

            @Override
            public void touchDown() {
            }

            @Override
            public void clicked() {
                Main.mainController.removeAds();
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
                startGameTransition = true;
            }
        });
        btnShop.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
            }

            @Override
            public void touchDown() {
                startShopTransition = true;
            }

            @Override
            public void clicked() {
                startShopTransition = true;
            }
        });

        btnSettings.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {

            }

            @Override
            public void touchDown() {

            }

            @Override
            public void clicked() {
                settings.show();
            }
        });
    }

    private void timer(ItemWrapper menuItem) {
        final Entity timerE = menuItem.getChild(TRIAL_TIMER).getEntity();
        LabelComponent lc = timerE.getComponent(LabelComponent.class);
        boolean showTimer = false;
        if (gameScript.fpc.currentPet != null && gameScript.fpc.currentPet.tryPeriod) {
            lc.text.replace(0, lc.text.length, gameScript.fpc.currentPet.updateTryPeriodTimer());
            showTimer = true;
        } else {
            for (Upgrade u : gameScript.fpc.upgrades.values()) {
                if (u.tryPeriod) {
                    lc.text.replace(0, lc.text.length, u.updateTryPeriodTimer());
                    showTimer = true;
                }
            }
        }
        if (!showTimer) {
            timerE.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
        }

    }

    @Override
    public void dispose() {
        System.gc();
    }

    @Override
    public void act(float delta) {
        GameScreenScript.checkTryPeriod();
        timer(menuItem);

        if(startGameTransition){
            curtain_mm.getComponent(TintComponent.class).color.a+=0.05f;
            if (curtain_mm.getComponent(TintComponent.class).color.a>=1){
                startGameTransition = false;
                stage.initGame();
    }
}

        if(startShopTransition){
            curtain_mm.getComponent(TintComponent.class).color.a+=0.05f;
            if (curtain_mm.getComponent(TintComponent.class).color.a>=1){
                startShopTransition = false;
                stage.initShopWithAds();
            }
        }

        if(startTransitionIn){
            curtain_mm.getComponent(TintComponent.class).color.a-=0.05f;
            if (curtain_mm.getComponent(TintComponent.class).color.a<=0){
                startTransitionIn = false;
            }
        }
    }


}
