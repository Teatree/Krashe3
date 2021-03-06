package com.fd.etf.stages.ui;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.fd.etf.Main;
import com.fd.etf.entity.componets.ShopItem;
import com.fd.etf.entity.componets.ToggleButtonComponent;
import com.fd.etf.entity.componets.listeners.ImageButtonListener;
import com.fd.etf.stages.GameStage;
import com.fd.etf.stages.ShopScreenScript;
import com.fd.etf.utils.BackgroundMusicMgr;
import com.fd.etf.utils.GlobalConstants;
import com.fd.etf.utils.SoundMgr;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.fd.etf.utils.EffectUtils.playYellowStarsParticleEffect;
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
    private static final int SETTINGS_X = 600;
    private static final int INFO_HIDDEN_X = 1600;
    private static final int SETTINGS_HIDDEN_X = -1000;
    public static final float SETTINGS_SCALE = 0.65f;

    public Entity settingsE;
    private Entity infoE;

    Entity btnNoAds;
    BasicDialog dialog;

    public Settings(GameStage gameStage, ItemWrapper gameItem) {
        super(gameStage);
        this.gameItem = gameItem;
    }

    public void init() {
        loadSettingsFromLib();
        loadInfoFromLib();

        Entity closeSettingsBtn = settingsE.getComponent(NodeComponent.class).getChild(BTN_CLOSE_SETTINGS);
        btnNoAds = settingsE.getComponent(NodeComponent.class).getChild(BTN_NO_ADS);
        Entity nextInfoBtn = settingsE.getComponent(NodeComponent.class).getChild(BTN_NEXT_INFO);
        Entity restorePurchasesBtn = settingsE.getComponent(NodeComponent.class).getChild(BTN_RESTORE);
        Entity resetProgressBtn = settingsE.getComponent(NodeComponent.class).getChild(BTN_RESET);

        settingsE.getComponent(NodeComponent.class).getChild("settings_title").getComponent(LabelComponent.class).fontScaleX = 0.7f;
        settingsE.getComponent(NodeComponent.class).getChild("settings_title").getComponent(LabelComponent.class).fontScaleY = 0.7f;
        settingsE.getComponent(NodeComponent.class).getChild("settings_title_sh").getComponent(LabelComponent.class).fontScaleX = 0.7f;
        settingsE.getComponent(NodeComponent.class).getChild("settings_title_sh").getComponent(LabelComponent.class).fontScaleY = 0.7f;
        settingsE.getComponent(NodeComponent.class).getChild("btn_reset").getComponent(NodeComponent.class).getChild("reset_lbl").getComponent(LabelComponent.class).fontScaleX = 0.5f;
        settingsE.getComponent(NodeComponent.class).getChild("btn_reset").getComponent(NodeComponent.class).getChild("reset_lbl").getComponent(LabelComponent.class).fontScaleY = 0.5f;
        settingsE.getComponent(NodeComponent.class).getChild("btn_reset").getComponent(NodeComponent.class).getChild("reset_lbl_sh").getComponent(LabelComponent.class).fontScaleX = 0.5f;
        settingsE.getComponent(NodeComponent.class).getChild("btn_reset").getComponent(NodeComponent.class).getChild("reset_lbl_sh").getComponent(LabelComponent.class).fontScaleY = 0.5f;
        settingsE.getComponent(NodeComponent.class).getChild("btn_restore").getComponent(NodeComponent.class).getChild("restore_lbl").getComponent(LabelComponent.class).fontScaleX = 0.5f;
        settingsE.getComponent(NodeComponent.class).getChild("btn_restore").getComponent(NodeComponent.class).getChild("restore_lbl").getComponent(LabelComponent.class).fontScaleY = 0.5f;
        settingsE.getComponent(NodeComponent.class).getChild("btn_restore").getComponent(NodeComponent.class).getChild("restore_lbl_sh").getComponent(LabelComponent.class).fontScaleX = 0.5f;
        settingsE.getComponent(NodeComponent.class).getChild("btn_restore").getComponent(NodeComponent.class).getChild("restore_lbl_sh").getComponent(LabelComponent.class).fontScaleY = 0.5f;

        initSoundBtn();
        initMusicBtn();

        initInfo();

        dialog = new BasicDialog(gameStage, gameItem);
        dialog.init();
        dialog.parent = this;

        initShadow();

        closeSettingsBtn.add(new ButtonComponent());
        closeSettingsBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(closeSettingsBtn) {
                    @Override
                    public void clicked() {
                        checkSecondaryDialog();
                        close(settingsE);
                        if (BackgroundMusicMgr.getBackgroundMusicMgr().musicMenu.isPlaying()) {
                            BackgroundMusicMgr.getBackgroundMusicMgr().musicMenu.setVolume(0.2f);
                        }
                        if (BackgroundMusicMgr.getBackgroundMusicMgr().musicGame.isPlaying()) {
                            BackgroundMusicMgr.getBackgroundMusicMgr().musicGame.setVolume(0.2f);
                        }
                    }
                });
        if(gameStage.gameScript.fpc.settings.noAds) {
            btnNoAds.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
        }else {
            btnNoAds.add(new ButtonComponent());
            btnNoAds.getComponent(ButtonComponent.class).addListener(
                    new ImageButtonListener(btnNoAds) {
                        @Override
                        public void clicked() {
                            if (!isSecondDialogOpen.get()) {
                                Main.mainController.removeAds();

                                btnNoAds.add(getActionForHardCurrency());
                            }
                        }
                    });
        }

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

                            ActionComponent acSettings = new ActionComponent();
                            Actions.checkInit();
                            acSettings.dataArray.add(
                                    Actions.moveTo(SETTINGS_HIDDEN_X, SETTINGS_Y, POPUP_MOVE_DURATION, Interpolation.exp10));
                            settingsE.add(acSettings);

                            ActionComponent acInfo = new ActionComponent();
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
                            PauseDialog.goalsUpdate = true;
                        }
                    }
                });

        restorePurchasesBtn.add(new ButtonComponent());
        restorePurchasesBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(restorePurchasesBtn) {
                    @Override
                    public void clicked() {
                        if (!isSecondDialogOpen.get() && isActive) {
                            if (Main.mainController.isWifiConnected()) {
                                try {
                                    Main.mainController.restorePurchases(gameStage);
                                    dialog.show(BasicDialog.TYPE_RESTORE_PURCH_RESULT);
                                    if(gameStage.gameScript.fpc.settings.noAds)
                                        btnNoAds.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
                                } catch (Exception e) {
                                    dialog.show(BasicDialog.TYPE_ERROR_SETTINGS);
                                }
                            } else {
                                dialog.show(BasicDialog.TYPE_ERROR_SETTINGS);
                            }
                        }
                        checkSecondaryDialog();
                    }
                });

        final TransformComponent settingsTc = settingsE.getComponent(TransformComponent.class);
        settingsTc.x = FAR_FAR_AWAY_X;
        settingsTc.y = FAR_FAR_AWAY_Y;
    }

    private ActionComponent getActionForHardCurrency2() {
        ActionComponent ac = new ActionComponent();
        Actions.checkInit();
        ac.dataArray.add(Actions.sequence(
                Actions.delay(0.5f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        checkIfNastya();
                    }
                }),
                Actions.delay(0.5f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        checkIfNastya();
                    }
                }),
                Actions.delay(0.5f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        checkIfNastya();
                    }
                }),
                Actions.delay(0.5f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        checkIfNastya();
                    }
                }),
                Actions.delay(0.5f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        checkIfNastya();
                    }
                }),
                Actions.delay(0.5f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        checkIfNastya(true);
                    }
                })));
        return ac;
    }

    private ActionComponent getActionForHardCurrency() {
        ActionComponent ac = new ActionComponent();
        Actions.checkInit();
        ac.dataArray.add(Actions.sequence(
                Actions.delay(0.5f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        if(gameStage.gameScript.fpc.settings.noAds) {
                            btnNoAds.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
                            btnNoAds.remove(ActionComponent.class);
                        }
                    }
                }),
                Actions.delay(0.5f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        if(gameStage.gameScript.fpc.settings.noAds) {
                            btnNoAds.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
                            btnNoAds.remove(ActionComponent.class);
                        }
                    }
                }),
                Actions.delay(0.5f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        if(gameStage.gameScript.fpc.settings.noAds) {
                            btnNoAds.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
                            btnNoAds.remove(ActionComponent.class);
                        }
                    }
                }),
                Actions.delay(0.5f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        if(gameStage.gameScript.fpc.settings.noAds) {
                            btnNoAds.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
                            btnNoAds.remove(ActionComponent.class);
                        }
                    }
                }),
                Actions.delay(0.5f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        if(gameStage.gameScript.fpc.settings.noAds) {
                            btnNoAds.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
                            btnNoAds.remove(ActionComponent.class);
                        }else{
                            btnNoAds.remove(ActionComponent.class);
                        }
                    }
                }),
                Actions.delay(0.5f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        checkIfNastya(true);
                    }
                })));
        return ac;
    }

    private void checkIfNastya() {
        if (Main.mainController.getReceivedResponse() == true) {
            //add component that checks every 10 seconds for 2 minutes
            btnNoAds.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;

            btnNoAds.remove(ActionComponent.class);
        }else if(Main.mainController.getReceivedErrorResponse() == true){
            dialog.show(BasicDialog.TYPE_ERROR_SETTINGS);

            btnNoAds.remove(ActionComponent.class);
        }
    }

    private void checkIfNastya(boolean i) {
        if(i) {
            if (Main.mainController.getReceivedResponse() == true) {
                //add component that checks every 10 seconds for 2 minutes
                btnNoAds.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;

                btnNoAds.remove(ActionComponent.class);
            } else if (Main.mainController.getReceivedErrorResponse() == true) {
                dialog.show(BasicDialog.TYPE_ERROR_SETTINGS);

                btnNoAds.remove(ActionComponent.class);
            }else{
                dialog.show(BasicDialog.TYPE_ERROR_SETTINGS);

                btnNoAds.remove(ActionComponent.class);
            }
        }
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
                        ActionComponent acSettings = new ActionComponent();
                        Actions.checkInit();
                        acSettings.dataArray.add(Actions.moveTo(SETTINGS_X, SETTINGS_Y, POPUP_MOVE_DURATION, Interpolation.exp10));
                        settingsE.add(acSettings);

                        ActionComponent acInfo = new ActionComponent();
                        Actions.checkInit();
                        acInfo.dataArray.add(Actions.moveTo(INFO_HIDDEN_X, SETTINGS_Y, POPUP_MOVE_DURATION, Interpolation.exp10));
                        infoE.add(acInfo);
                    }
                });
    }

    private void loadSettingsFromLib() {
        CompositeItemVO tempItemC = gameStage.sceneLoader.loadVoFromLibrary(SETTINGS).clone();
        settingsE = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), tempItemC);
        gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), settingsE, tempItemC.composite);
        gameStage.sceneLoader.getEngine().addEntity(settingsE);
        settingsE.getComponent(TransformComponent.class).scaleX = SETTINGS_SCALE;
        settingsE.getComponent(TransformComponent.class).scaleY = SETTINGS_SCALE;
    }

    private void loadInfoFromLib() {
        CompositeItemVO tempItemC = gameStage.sceneLoader.loadVoFromLibrary(INFO).clone();
        infoE = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), tempItemC);
        gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), infoE, tempItemC.composite);
        gameStage.sceneLoader.getEngine().addEntity(infoE);

        infoE.getComponent(NodeComponent.class).getChild("settings_title").getComponent(LabelComponent.class).fontScaleX = 0.7f;
        infoE.getComponent(NodeComponent.class).getChild("settings_title").getComponent(LabelComponent.class).fontScaleY = 0.7f;
        infoE.getComponent(NodeComponent.class).getChild("settings_title_sh").getComponent(LabelComponent.class).fontScaleX = 0.7f;
        infoE.getComponent(NodeComponent.class).getChild("settings_title_sh").getComponent(LabelComponent.class).fontScaleY = 0.7f;
    }

    public void show() {

        SoundMgr.getSoundMgr().play(SoundMgr.WIND_POP_UP_OPEN);

        isActive = true;
        addShadow();
        settingsE.getComponent(TransformComponent.class).x = SETTINGS_X;
        settingsE.getComponent(TransformComponent.class).y = 460;
        settingsE.getComponent(ZIndexComponent.class).setZIndex(100);

        if(gameStage.gameScript.fpc.settings.noAds)
            btnNoAds.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;

        ActionComponent ac = new ActionComponent();
        Actions.checkInit();
        ac.dataArray.add(Actions.moveTo(SETTINGS_X, SETTINGS_Y, POPUP_MOVE_DURATION, Interpolation.exp10Out));
        settingsE.add(ac);

        if (BackgroundMusicMgr.getBackgroundMusicMgr().musicMenu.isPlaying()) {
            BackgroundMusicMgr.getBackgroundMusicMgr().musicMenu.setVolume(0.05f);
        }
        if (BackgroundMusicMgr.getBackgroundMusicMgr().musicGame.isPlaying()) {
            BackgroundMusicMgr.getBackgroundMusicMgr().musicGame.setVolume(0.05f);
        }

        settingsE.getComponent(TintComponent.class).color.a = 1;

    }


    public void musicOn() {
        if (isActive) {
            gameStage.gameScript.fpc.settings.noMusic = false;
            BackgroundMusicMgr.musicOn = true;
            BackgroundMusicMgr.getBackgroundMusicMgr().playMenu();
        }
    }

    public void musicOff() {
        if (isActive) {
            gameStage.gameScript.fpc.settings.noMusic = true;
            BackgroundMusicMgr.musicOn = false;
            BackgroundMusicMgr.getBackgroundMusicMgr().stopMenu();
            BackgroundMusicMgr.getBackgroundMusicMgr().stopGame();
        }
    }

    public void soundOn() {
        if (isActive) {
            gameStage.gameScript.fpc.settings.noSound = false;
            SoundMgr.soundOn = true;
        }
    }

    public void soundOff() {
        if (isActive) {
            gameStage.gameScript.fpc.settings.noSound = true;
            SoundMgr.soundOn = false;
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
                        musicOff();
                        tbc.setOff();
                    } else {
                        lc.getLayer(BTN_NORMAL).isVisible = true;
                        lc.getLayer(BTN_PRESSED).isVisible = false;
                        musicOn();
                        tbc.setOn();
                    }
                }
            });
        }
    }

    private void setMusicBtnState(Entity e) {
        ToggleButtonComponent musictbc = e.getComponent(ToggleButtonComponent.class);
        LayerMapComponent lc = e.getComponent(LayerMapComponent.class);
        if (gameStage.gameScript.fpc.settings.noMusic) {
            musictbc.setOff();
            lc.getLayer(BTN_NORMAL).isVisible = false;
            lc.getLayer(BTN_DEFAULT).isVisible = false;
            lc.getLayer(BTN_PRESSED).isVisible = true;
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
                        soundOff();
                        tbc.setOff();
                    } else {
                        lc.getLayer(BTN_NORMAL).isVisible = true;
                        lc.getLayer(BTN_PRESSED).isVisible = false;
                        soundOn();
                        tbc.setOn();
                    }
                }
            });
        }
    }

    private void setSoundBtnState(Entity e) {
        ToggleButtonComponent soundtbc = e.getComponent(ToggleButtonComponent.class);
        LayerMapComponent lc8 = e.getComponent(LayerMapComponent.class);
        if (gameStage.gameScript.fpc.settings.noSound) {
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
