package com.mygdx.etf.stages;

import com.badlogic.ashley.core.Entity;
import com.mygdx.etf.Main;
import com.mygdx.etf.entity.componets.Upgrade;
import com.mygdx.etf.stages.ui.Settings;
import com.mygdx.etf.stages.ui.TrialTimer;
import com.mygdx.etf.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.etf.entity.componets.FlowerComponent.FLOWER_SCALE;
import static com.mygdx.etf.entity.componets.FlowerComponent.FLOWER_X_POS;
import static com.mygdx.etf.entity.componets.FlowerComponent.FLOWER_Y_POS;
import static com.mygdx.etf.entity.componets.LeafsComponent.LEAFS_SCALE;
import static com.mygdx.etf.entity.componets.LeafsComponent.LEAFS_X_POS;
import static com.mygdx.etf.entity.componets.LeafsComponent.LEAFS_Y_POS;
import static com.mygdx.etf.stages.GameStage.gameScript;
import static com.mygdx.etf.stages.GameStage.sceneLoader;
import static com.mygdx.etf.stages.ui.AbstractDialog.isDialogOpen;
import static com.mygdx.etf.utils.GlobalConstants.BUTTON_TAG;

public class MenuScreenScript implements IScript {

    public static final String BTN_PLAY = "btn_play";
    public static final String BTN_SHOP = "btn_shop";
//    public static final String BTN_NO_ADS = "btn_noAds";
    public static final String BTN_SETTINGS = "btn_settings";
    public static final String BTN_RATE = "btn_restore";
    public static final String CURTAIN = "curtain_mm";

    ItemWrapper menuItem;
    private GameStage stage;
    private Settings settings;

    //Dima's party time
    Entity curtain_mm;
    boolean startGameTransition;
    boolean startShopTransition;
    boolean startTransitionIn;

    private TrialTimer timer;

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
        isDialogOpen = false;
        if (timer == null) {
            timer = new TrialTimer(menuItem, 680, 500);
        }

        Entity mmFlowerEntity = menuItem.getChild("MM_flower").getEntity();

        TransformComponent tc = mmFlowerEntity.getComponent(TransformComponent.class);
        tc.x = FLOWER_X_POS;
        tc.y = FLOWER_Y_POS;
        tc.scaleX = FLOWER_SCALE;
        tc.scaleY = FLOWER_SCALE;

        Entity mmLeafsEntity = menuItem.getChild("MM_leafs").getEntity();

        TransformComponent tcL = mmLeafsEntity.getComponent(TransformComponent.class);
        tcL.x = LEAFS_X_POS;
        tcL.y = LEAFS_Y_POS;
        tcL.scaleX = LEAFS_SCALE;
        tcL.scaleY = LEAFS_SCALE;
    }

    public void initButtons() {
        Entity playBtn = menuItem.getChild(BTN_PLAY).getEntity();
        Entity btnShop = menuItem.getChild(BTN_SHOP).getEntity();
//        Entity btnNoAds = menuItem.getChild(BTN_NO_ADS).getEntity();
        Entity btnSettings = menuItem.getChild(BTN_SETTINGS).getEntity();
        final Entity rateAppBtn = menuItem.getChild(BTN_RATE).getEntity();

        rateAppBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {}

            @Override
            public void touchDown() {}

            @Override
            public void clicked() {
                rateMyApp();
            }
        });

        playBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {}

            @Override
            public void touchDown() {}

            @Override
            public void clicked() {
                if (!isDialogOpen) {
                    startGameTransition = true;
                }
            }
        });
        btnShop.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
            }

            @Override
            public void touchDown() {
                if(!isDialogOpen) {
                    startShopTransition = true;
                }
            }

            @Override
            public void clicked() {
                if(!isDialogOpen) {
                    startShopTransition = true;
                }
            }
        });

        btnSettings.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {}

            @Override
            public void touchDown() {}

            @Override
            public void clicked() {
                if(!isDialogOpen) {
                    isDialogOpen = true;
                    settings.show();
                }
            }
        });
    }

//    private void timer(ItemWrapper menuItem) {
//        final Entity timerE = menuItem.getChild(TRIAL_TIMER).getEntity();
//        LabelComponent lc = timerE.getComponent(LabelComponent.class);
//        boolean showTimer = false;
//        if (gameScript.fpc.currentPet != null && gameScript.fpc.currentPet.tryPeriod) {
//            lc.text.replace(0, lc.text.length, gameScript.fpc.currentPet.updateTryPeriodTimer());
//            showTimer = true;
//            addTimerLogo(gameScript.fpc.currentPet.logoName);
//        } else {
//            for (Upgrade u : gameScript.fpc.upgrades.values()) {
//                if (u.tryPeriod) {
//                    lc.text.replace(0, lc.text.length, u.updateTryPeriodTimer());
//                    showTimer = true;
//                    addTimerLogo(u.logoName);
//                }
//            }
//        }
//        if (!showTimer) {
//            timerE.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
//            timerLogo.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
//            sceneLoader.getEngine().removeEntity(timerLogo);
//            timerLogo = null;
//        }
//    }
//
//    private void addTimerLogo(String logoLibName) {
//        if (timerLogo == null){
//            final CompositeItemVO tempC = sceneLoader.loadVoFromLibrary(logoLibName);
//            timerLogo = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempC);
//            sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), timerLogo, tempC.composite);
//            sceneLoader.getEngine().addEntity(timerLogo);
//
//            timerLogo.getComponent(TransformComponent.class).x = 680;
//            timerLogo.getComponent(TransformComponent.class).y = 500;
//            timerLogo.getComponent(TransformComponent.class).scaleX = 0.4f;
//            timerLogo.getComponent(TransformComponent.class).scaleY = 0.4f;
//        }
//    }

    @Override
    public void dispose() {
        System.gc();
    }

    @Override
    public void act(float delta) {
        GameScreenScript.checkTryPeriod();
        timer.timer();

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

    private void rateMyApp() {
        Main.mainController.rateMyApp();
    }
}
