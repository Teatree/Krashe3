package com.mygdx.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.mygdx.etf.stages.GameStage;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.etf.stages.GameStage.sceneLoader;
import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_Y;

/**
 * Created by ARudyk on 7/1/2016.
 */
public class AbstractDialog {

    public static final String LIB_SHADOW = "lib_shadow";
    public static final int HIDE_Y = 900;
    public static final float POPUP_MOVE_DURATION = 1.6f;

    protected ItemWrapper gameItem;
    protected Entity shadowE;
    public static boolean isDialogOpen;
    public static boolean isSecondDialogOpen;
    public static boolean isSecondDialogClosed;
    public boolean isActive;

    protected void initShadow() {
        if (shadowE == null) {
            CompositeItemVO tempC = GameStage.sceneLoader.loadVoFromLibrary(LIB_SHADOW).clone();
            shadowE = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempC);
            GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), shadowE, tempC.composite);
            GameStage.sceneLoader.getEngine().addEntity(shadowE);
        }
        shadowE.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        shadowE.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
    }

    protected void addShadow() {
        shadowE.getComponent(TransformComponent.class).x = 0;
        shadowE.getComponent(TransformComponent.class).y = 0;
        shadowE.getComponent(ZIndexComponent.class).setZIndex(39);
//        sceneLoader.getEngine().addEntity(shadowE);
        shadowE.getComponent(TintComponent.class).color.a = 0;

        ActionComponent ac = new ActionComponent();
        Actions.checkInit();
        ac.dataArray.add(Actions.fadeIn(0.5f, Interpolation.exp5));
        shadowE.add(ac);
    }

    public void close (Entity e){
        if (isActive) {
            isActive = false;
            ActionComponent ac = new ActionComponent();
            Actions.checkInit();
            ac.dataArray.add(Actions.moveTo(e.getComponent(TransformComponent.class).x, HIDE_Y, 1, Interpolation.exp10));
            e.add(ac);

            ActionComponent ac2 = new ActionComponent();
            ac2.dataArray.add(Actions.fadeOut(0.5f, Interpolation.exp5));
            shadowE.add(ac2);
            if (isSecondDialogOpen) {
                isSecondDialogClosed = true;
            } else {
                isDialogOpen = false;
            }
        }
    }

    public static void checkSecondaryDialog(){
        if (isSecondDialogOpen && isSecondDialogClosed){
            isSecondDialogOpen = false;
            isSecondDialogClosed = false;
        }
    }
}
