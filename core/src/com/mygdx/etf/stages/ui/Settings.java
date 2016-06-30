package com.mygdx.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.mygdx.etf.stages.GameStage;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.etf.stages.GameStage.sceneLoader;
import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_Y;

/**
 * Created by ARudyk on 6/30/2016.
 */
public class Settings {

    public static final String LIB_SHADOW = "lib_shadow";
    public static final String SETTINGS = "settings";
    public static final String BTN_RESET = "btn_reset";
    public static final String BTN_RESTORE = "btn_restore";
    public static final String BTN_CLOSE = "btn_close_settings";
    public static final String BTN_NEXT_INFO = "btn_next_info";

    public static final int SETTINGS_Y = 30;
    public static final int SETTINGS_X = 260;

    private ItemWrapper gameItem;
    private Entity settingsE;
    private Entity shadowE;

    public Settings (ItemWrapper gameItem){
        this.gameItem = gameItem;
    }

    public void init() {
        settingsE = gameItem.getChild(SETTINGS).getEntity();
        Entity closePauseBtn = gameItem.getChild(SETTINGS).getChild(BTN_CLOSE).getEntity();

        initShadow();

        closePauseBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
            }

            @Override
            public void touchDown() {
            }

            @Override
            public void clicked() {
                closeSettings();
            }
        });

        final TransformComponent settingsTc = settingsE.getComponent(TransformComponent.class);
        settingsTc.x = FAR_FAR_AWAY_X;
        settingsTc.y = FAR_FAR_AWAY_Y;
    }

    private void initShadow() {
        CompositeItemVO tempC = GameStage.sceneLoader.loadVoFromLibrary(LIB_SHADOW).clone();
        if (shadowE == null) {
            shadowE = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempC);
            GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), shadowE, tempC.composite);
            GameStage.sceneLoader.getEngine().addEntity(shadowE);
        }
        shadowE.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        shadowE.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
    }

    private void addShadow() {
        CompositeItemVO tempItemC = sceneLoader.loadVoFromLibrary(LIB_SHADOW).clone();
        shadowE = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempItemC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), shadowE, tempItemC.composite);
        shadowE.getComponent(TransformComponent.class).x = 0;
        shadowE.getComponent(TransformComponent.class).y = 0;
        shadowE.getComponent(ZIndexComponent.class).setZIndex(39);
        sceneLoader.getEngine().addEntity(shadowE);
        shadowE.getComponent(TintComponent.class).color.a = 0;
        Actions.checkInit();
        ActionComponent ac = new ActionComponent();
        ac.dataArray.add(Actions.fadeIn(0.5f, Interpolation.exp5));
        shadowE.add(ac);
    }

    public void closeSettings (){
        ActionComponent ac = new ActionComponent();
        Actions.checkInit();
        ac.dataArray.add(Actions.moveTo(SETTINGS_X, 900, 1, Interpolation.exp10));
        settingsE.add(ac);

        ActionComponent ac2 = new ActionComponent();
        ac2.dataArray.add(Actions.fadeOut(0.5f, Interpolation.exp5));
        shadowE.add(ac2);
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
}
