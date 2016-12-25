package com.fd.etf.stages.ui;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.fd.etf.Main;
import com.fd.etf.entity.componets.ToggleButtonComponent;
import com.fd.etf.entity.componets.listeners.ImageButtonListener;
import com.fd.etf.stages.GameStage;
import com.fd.etf.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.fd.etf.utils.GlobalConstants.*;

/**
 * Created by ARudyk on 6/30/2016.
 */
public class Settings extends AbstractDialog {

    private static final String SETTINGS = "settings_lib";
    private static final String INFO = "info_lib";

    private static final String BTN_RESET = "btn_reset";
    private static final String BTN_RESTORE = "btn_restore";
    private static final String BTN_CLOSE_SETTINGS = "btn_close_settings";
    private static final String BTN_CLOSE_INFO = "btn_close_info";
    private static final String BTN_NEXT_INFO = "btn_next_info";
    private static final String BTN_BACK_SETTINGS = "btn_back_settings";
    private static final String BTN_MUSIC = "btn_music";
    private static final String BTN_SOUND = "btn_sound";
    private static final String BTN_NO_ADS = "btn_noAds";

    private static final int SETTINGS_Y = 50;
    private static final int SETTINGS_X = 560;
    private static final int INFO_HIDDEN_X = 1600;
    private static final int SETTINGS_HIDDEN_X = -1000;
    public static final float SETTINGS_SCALE = 0.65f;

    public Entity settingsE;
    private Entity infoE;

    public Settings(ItemWrapper gameItem) {
        this.gameItem = gameItem;
    }

    public void init() {
        loadSettingsFromLib();
        loadInfoFromLib();

        Entity closeSettingsBtn = settingsE.getComponent(NodeComponent.class).getChild(BTN_CLOSE_SETTINGS);
        Entity btnNoAds = settingsE.getComponent(NodeComponent.class).getChild(BTN_NO_ADS);
        Entity nextInfoBtn = settingsE.getComponent(NodeComponent.class).getChild(BTN_NEXT_INFO);
        Entity restorePurchasesBtn = settingsE.getComponent(NodeComponent.class).getChild(BTN_RESTORE);
        Entity resetProgressBtn = settingsE.getComponent(NodeComponent.class).getChild(BTN_RESET);

        initSoundBtn();
        initMusicBtn();

        initInfo();

        final BasicDialog dialog = new BasicDialog(gameItem);
        dialog.init();
        dialog.parent = this;

        initShadow();

        closeSettingsBtn.add(new ButtonComponent());
        closeSettingsBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(closeSettingsBtn) {
                    @Override
                    public void clicked() {
                        close(settingsE);
                    }
                });

        btnNoAds.add(new ButtonComponent());
        btnNoAds.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(btnNoAds) {
                    @Override
                    public void clicked() {
                        if (!isSecondDialogOpen.get()) {
                            Main.mainController.removeAds();
                        }
                    }
                });

        nextInfoBtn.add(new ButtonComponent());
        nextInfoBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(nextInfoBtn) {
                    @Override
                    public void clicked() {
                        checkSecondaryDialog();
                        if (!isSecondDialogOpen.get() && isActive) {
                            infoE.getComponent(TransformComponent.class).x = INFO_HIDDEN_X;
                            infoE.getComponent(TransformComponent.class).y = SETTINGS_Y;
                            infoE.getComponent(TransformComponent.class).scaleX = SETTINGS_SCALE;
                            infoE.getComponent(TransformComponent.class).scaleY = SETTINGS_SCALE;

                            ActionComponent acSettings = GameStage.sceneLoader.engine.createComponent(ActionComponent.class);
                            Actions.checkInit();
                            acSettings.dataArray.add(
                                    Actions.moveTo(SETTINGS_HIDDEN_X, SETTINGS_Y, POPUP_MOVE_DURATION, Interpolation.exp10));
                            settingsE.add(acSettings);

                            ActionComponent acInfo = GameStage.sceneLoader.engine.createComponent(ActionComponent.class);
                            Actions.checkInit();
                            acInfo.dataArray.add(Actions.moveTo(SETTINGS_X, SETTINGS_Y, POPUP_MOVE_DURATION, Interpolation.exp10));
                            infoE.getComponent(ZIndexComponent.class).setZIndex(
                                    shadowE.getComponent(ZIndexComponent.class).getZIndex() + 10);
                            infoE.add(acInfo);
                        }

                    }
                });

        resetProgressBtn.add(new ButtonComponent());
        resetProgressBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(resetProgressBtn) {
                    @Override
                    public void clicked() {
                        checkSecondaryDialog();
                        if (!isSecondDialogOpen.get() && isActive) {
                            dialog.show(BasicDialog.TYPE_RESET);
                        }
                    }
                });

        restorePurchasesBtn.add(new ButtonComponent());
        restorePurchasesBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(restorePurchasesBtn) {
                    @Override
                    public void clicked() {
                        if (!isSecondDialogOpen.get() && isActive) {
                            try {
                                Main.mainController.restorePurchases();
                                dialog.show(BasicDialog.TYPE_RESTORE_PURCH_RESULT);
                            } catch (Exception e) {
                                dialog.show(BasicDialog.ERROR);
                            }
                        }
                        checkSecondaryDialog();
                    }
                });

        final TransformComponent settingsTc = settingsE.getComponent(TransformComponent.class);
        settingsTc.x = FAR_FAR_AWAY_X;
        settingsTc.y = FAR_FAR_AWAY_Y;
    }

    private void initInfo() {
        infoE.getComponent(TransformComponent.class).x = INFO_HIDDEN_X;
        infoE.getComponent(TransformComponent.class).y = SETTINGS_Y;

        Entity closeInfoBtn = infoE.getComponent(NodeComponent.class).getChild(BTN_CLOSE_INFO);
        closeInfoBtn.add(new ButtonComponent());
        closeInfoBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(closeInfoBtn) {
                    @Override
                    public void clicked() {
                        close(infoE);
                    }
                });

        Entity backBtn = infoE.getComponent(NodeComponent.class).getChild(BTN_BACK_SETTINGS);
        backBtn.add(new ButtonComponent());
        backBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(backBtn) {
                    @Override
                    public void clicked() {
                        ActionComponent acSettings = GameStage.sceneLoader.engine.createComponent(ActionComponent.class);
                        Actions.checkInit();
                        acSettings.dataArray.add(Actions.moveTo(SETTINGS_X, SETTINGS_Y, POPUP_MOVE_DURATION, Interpolation.exp10));
                        settingsE.add(acSettings);

                        ActionComponent acInfo = GameStage.sceneLoader.engine.createComponent(ActionComponent.class);
                        Actions.checkInit();
                        acInfo.dataArray.add(Actions.moveTo(INFO_HIDDEN_X, SETTINGS_Y, POPUP_MOVE_DURATION, Interpolation.exp10));
                        infoE.add(acInfo);
                    }
                });


    }

    private void loadSettingsFromLib() {
        CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary(SETTINGS).clone();
        settingsE = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
        GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), settingsE, tempItemC.composite);
        GameStage.sceneLoader.getEngine().addEntity(settingsE);
        settingsE.getComponent(TransformComponent.class).scaleX = SETTINGS_SCALE;
        settingsE.getComponent(TransformComponent.class).scaleY = SETTINGS_SCALE;
    }

    private void loadInfoFromLib() {
        CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary(INFO).clone();
        infoE = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
        GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), infoE, tempItemC.composite);
        GameStage.sceneLoader.getEngine().addEntity(infoE);
    }

    public void show() {
        isActive = true;
        addShadow();
        settingsE.getComponent(TransformComponent.class).x = SETTINGS_X;
        settingsE.getComponent(TransformComponent.class).y = 460;
        settingsE.getComponent(ZIndexComponent.class).setZIndex(100);

        ActionComponent ac = GameStage.sceneLoader.engine.createComponent(ActionComponent.class);
        Actions.checkInit();
        ac.dataArray.add(Actions.moveTo(SETTINGS_X, SETTINGS_Y, POPUP_MOVE_DURATION, Interpolation.exp10Out));
        settingsE.add(ac);
    }


    public void musicOn() {
        if (isActive) {
            GameStage.gameScript.fpc.settings.noMusic = false;
        }
    }

    public void musicOff() {
        if (isActive) {
            GameStage.gameScript.fpc.settings.noMusic = true;
        }
    }

    public void soundOn() {
        if (isActive) {
            GameStage.gameScript.fpc.settings.noSound = false;
        }
    }

    public void soundOff() {
        if (isActive) {
            GameStage.gameScript.fpc.settings.noSound = true;
        }
    }

    private void initMusicBtn() {
        final Entity musicBtn = settingsE.getComponent(NodeComponent.class).getChild(BTN_MUSIC);
        ToggleButtonComponent musictbc = new ToggleButtonComponent();
        musicBtn.add(musictbc);
        setMusicBtnState(musicBtn);

        musicBtn.add(new ButtonComponent());
        musicBtn.getComponent(ButtonComponent.class).isDefaultLayersChange = false;

        if (0 == musicBtn.getComponent(ButtonComponent.class).listeners.size) {
            musicBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {

                final LayerMapComponent lc = ComponentRetriever.get(musicBtn, LayerMapComponent.class);
                private ComponentMapper<ToggleButtonComponent> mapper = ComponentMapper.getFor(ToggleButtonComponent.class);

                @Override
                public void touchDown() {
                    musicBtn.getComponent(TransformComponent.class).scaleX -= GlobalConstants.TENTH;
                    musicBtn.getComponent(TransformComponent.class).scaleY -= GlobalConstants.TENTH;
                    musicBtn.getComponent(TransformComponent.class).x += musicBtn.getComponent(DimensionsComponent.class).width / 20;
                    musicBtn.getComponent(TransformComponent.class).y += musicBtn.getComponent(DimensionsComponent.class).height / 20;
                }

                @Override
                public void touchUp() {
                    musicBtn.getComponent(TransformComponent.class).scaleX += GlobalConstants.TENTH;
                    musicBtn.getComponent(TransformComponent.class).scaleY += GlobalConstants.TENTH;
                    musicBtn.getComponent(TransformComponent.class).x -= musicBtn.getComponent(DimensionsComponent.class).width / 20;
                    musicBtn.getComponent(TransformComponent.class).y -= musicBtn.getComponent(DimensionsComponent.class).height / 20;
                }

                @Override
                public void clicked() {

                    final ToggleButtonComponent tbc = mapper.get(musicBtn);
                    if (tbc.isOn()) {
                        lc.getLayer(BTN_NORMAL).isVisible = false;
                        lc.getLayer(BTN_PRESSED).isVisible = true;
                        musicOn();
                        tbc.setOff();
                    } else {
                        lc.getLayer(BTN_NORMAL).isVisible = true;
                        lc.getLayer(BTN_PRESSED).isVisible = false;
                        musicOff();
                        tbc.setOn();
                    }
                }
            });
        }
    }

    private void setMusicBtnState(Entity e) {
        ToggleButtonComponent musictbc = e.getComponent(ToggleButtonComponent.class);
        LayerMapComponent lc = e.getComponent(LayerMapComponent.class);
        if (GameStage.gameScript.fpc.settings.noMusic) {
            lc.getLayer(BTN_NORMAL).isVisible = false;
            lc.getLayer(BTN_DEFAULT).isVisible = false;
            lc.getLayer(BTN_PRESSED).isVisible = true;
            musictbc.setOff();
        } else {
            musictbc.setOn();
            lc.getLayer(BTN_NORMAL).isVisible = true;
            lc.getLayer(BTN_PRESSED).isVisible = false;
            lc.getLayer(BTN_DEFAULT).isVisible = false;
        }
    }

    private void initSoundBtn() {
        final Entity soundBtn = settingsE.getComponent(NodeComponent.class).getChild(BTN_SOUND);
        ToggleButtonComponent soundtbc = new ToggleButtonComponent();
//        final LayerMapComponent lc8 = ComponentRetriever.get(soundBtn, LayerMapComponent.class);
        soundBtn.add(soundtbc);
        setSoundBtnState(soundBtn);
        soundBtn.add(new ButtonComponent());
        soundBtn.getComponent(ButtonComponent.class).isDefaultLayersChange = false;
        if (0 == soundBtn.getComponent(ButtonComponent.class).listeners.size) {
            soundBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
                final LayerMapComponent lc = ComponentRetriever.get(soundBtn, LayerMapComponent.class);
                private ComponentMapper<ToggleButtonComponent> mapper = ComponentMapper.getFor(ToggleButtonComponent.class);

                @Override
                public void touchDown() {
                    soundBtn.getComponent(TransformComponent.class).scaleX -= GlobalConstants.TENTH;
                    soundBtn.getComponent(TransformComponent.class).scaleY -= GlobalConstants.TENTH;
                    soundBtn.getComponent(TransformComponent.class).x += soundBtn.getComponent(DimensionsComponent.class).width / 20;
                    soundBtn.getComponent(TransformComponent.class).y += soundBtn.getComponent(DimensionsComponent.class).height / 20;
                }

                @Override
                public void touchUp() {
                    soundBtn.getComponent(TransformComponent.class).scaleX += GlobalConstants.TENTH;
                    soundBtn.getComponent(TransformComponent.class).scaleY += GlobalConstants.TENTH;
                    soundBtn.getComponent(TransformComponent.class).x -= soundBtn.getComponent(DimensionsComponent.class).width / 20;
                    soundBtn.getComponent(TransformComponent.class).y -= soundBtn.getComponent(DimensionsComponent.class).height / 20;
                }

                @Override
                public void clicked() {
                    final ToggleButtonComponent tbc = mapper.get(soundBtn);
                    if (tbc.isOn()) {
                        lc.getLayer(BTN_NORMAL).isVisible = false;
                        lc.getLayer(BTN_PRESSED).isVisible = true;
                        soundOn();
                        tbc.setOff();
                    } else {
                        lc.getLayer(BTN_NORMAL).isVisible = true;
                        lc.getLayer(BTN_PRESSED).isVisible = false;
                        soundOff();
                        tbc.setOn();
                    }
                }
            });
        }
    }

    private void setSoundBtnState(Entity e) {
        ToggleButtonComponent soundtbc = e.getComponent(ToggleButtonComponent.class);
        LayerMapComponent lc8 = e.getComponent(LayerMapComponent.class);
        if (GameStage.gameScript.fpc.settings.noSound) {
            soundtbc.setOff();
            lc8.getLayer(BTN_NORMAL).isVisible = false;
            lc8.getLayer(BTN_PRESSED).isVisible = true;
        } else {
            soundtbc.setOn();
            lc8.getLayer(BTN_NORMAL).isVisible = true;
            lc8.getLayer(BTN_PRESSED).isVisible = false;
        }
    }
}
