package com.mygdx.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.mygdx.etf.Main;
import com.mygdx.etf.stages.GameStage;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_Y;

/**
 * Created by ARudyk on 6/30/2016.
 */
public class Settings extends AbstractDialog {

    public static final String SETTINGS = "settings";
    public static final String INFO = "info";

//    public static final String LIB_SHADOW = "lib_shadow";
    public static final String BTN_RATE = "btn_restore";
    public static final String BTN_RESET = "btn_reset";
    public static final String BTN_RESTORE = "btn_restore";
    public static final String BTN_CLOSE_SETTINGS = "btn_close_settings";
    public static final String BTN_CLOSE_INFO = "btn_close_info";
    public static final String BTN_NEXT_INFO = "btn_next_info";
    public static final String BTN_BACK_SETTINGS = "btn_back_settings";

    public static final int SETTINGS_Y = 30;
    public static final int SETTINGS_X = 260;
    public static final int INFO_HIDDEN_X = 1600;
    public static final int SETTINGS_HIDDEN_X = -1000;

//    private ItemWrapper gameItem;
    private Entity settingsE;
    private Entity infoE;
//    private Entity shadowE;

    public Settings (ItemWrapper gameItem){
        this.gameItem = gameItem;
    }

    public void init() {
        settingsE = gameItem.getChild(SETTINGS).getEntity();
        Entity closeSettingsBtn = gameItem.getChild(SETTINGS).getChild(BTN_CLOSE_SETTINGS).getEntity();
        Entity nextInfoBtn = gameItem.getChild(SETTINGS).getChild(BTN_NEXT_INFO).getEntity();
        Entity restorePurchasesBtn = gameItem.getChild(SETTINGS).getChild(BTN_RESTORE).getEntity();
        Entity resetProgressBtn = gameItem.getChild(SETTINGS).getChild(BTN_RESET).getEntity();

        infoE = gameItem.getChild(INFO).getEntity();
        infoE.getComponent(TransformComponent.class).x = INFO_HIDDEN_X;
        infoE.getComponent(TransformComponent.class).y = SETTINGS_Y;

        Entity closeInfoBtn = gameItem.getChild(INFO).getChild(BTN_CLOSE_INFO).getEntity();
        Entity backBtn = gameItem.getChild(INFO).getChild(BTN_BACK_SETTINGS).getEntity();
        final Entity rateAppBtn = gameItem.getChild(INFO).getChild(BTN_RATE).getEntity();

        final BasicDialog dialog = new BasicDialog(gameItem);
        dialog.init();
//        dialogRestore.init(BasicDialog.TYPE_RESTORE_PURCH);

        initShadow();

        closeSettingsBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {}

            @Override
            public void touchDown() {}

            @Override
            public void clicked() {
                close(settingsE);
            }
        });

        closeInfoBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {}

            @Override
            public void touchDown() {}

            @Override
            public void clicked() {
                close(infoE);
            }
        });

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

        nextInfoBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {}

            @Override
            public void touchDown() {}

            @Override
            public void clicked() {
                infoE.getComponent(TransformComponent.class).x = INFO_HIDDEN_X;
                infoE.getComponent(TransformComponent.class).y = SETTINGS_Y;
                ActionComponent acSettings = new ActionComponent();
                Actions.checkInit();
                acSettings.dataArray.add(Actions.moveTo(SETTINGS_HIDDEN_X, SETTINGS_Y, 1, Interpolation.exp10));
                settingsE.add(acSettings);

                ActionComponent acInfo = new ActionComponent();
                Actions.checkInit();
                acInfo.dataArray.add(Actions.moveTo(SETTINGS_X, SETTINGS_Y, 1, Interpolation.exp10));
                infoE.getComponent(ZIndexComponent.class).setZIndex(shadowE.getComponent(ZIndexComponent.class).getZIndex()+10);
                infoE.add(acInfo);
            }
        });

        backBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {}

            @Override
            public void touchDown() {}

            @Override
            public void clicked() {
                ActionComponent acSettings = new ActionComponent();
                Actions.checkInit();
                acSettings.dataArray.add(Actions.moveTo(SETTINGS_X, SETTINGS_Y, 1, Interpolation.exp10));
                settingsE.add(acSettings);

                ActionComponent acInfo = new ActionComponent();
                Actions.checkInit();
                acInfo.dataArray.add(Actions.moveTo(INFO_HIDDEN_X, SETTINGS_Y, 1, Interpolation.exp10));
                infoE.add(acInfo);
            }
        });

        resetProgressBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {}

            @Override
            public void touchDown() {}

            @Override
            public void clicked() {
                if (!AbstractDialog.isSecondDialogOpen) {
                    dialog.show(BasicDialog.TYPE_RESET);
                }
            }
        });

        restorePurchasesBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {}

            @Override
            public void touchDown() {}

            @Override
            public void clicked() {
                if (!AbstractDialog.isSecondDialogOpen) {
                    dialog.show(BasicDialog.TYPE_RESTORE_PURCH);
                }
            }
        });

        final TransformComponent settingsTc = settingsE.getComponent(TransformComponent.class);
        settingsTc.x = FAR_FAR_AWAY_X;
        settingsTc.y = FAR_FAR_AWAY_Y;
    }


    private void rateMyApp() {
        Main.mainController.rateMyApp();
    }

    public void show(){
        addShadow();
        settingsE.getComponent(TransformComponent.class).x = SETTINGS_X;
        settingsE.getComponent(TransformComponent.class).y = 460;
        settingsE.getComponent(ZIndexComponent.class).setZIndex(100);

        ActionComponent ac = new ActionComponent();
        Actions.checkInit();
        ac.dataArray.add(Actions.moveTo(SETTINGS_X, SETTINGS_Y, 2, Interpolation.exp10Out));
        settingsE.add(ac);
    }


    public void musicOn(){
        GameStage.gameScript.fpc.settings.noMusic = false;
    }

    public void musicOff(){
        GameStage.gameScript.fpc.settings.noMusic = true;
    }

    public void soundOn(){
        GameStage.gameScript.fpc.settings.noSound = false;
    }

    public void soundOff(){
        GameStage.gameScript.fpc.settings.noSound = true;
    }

}
