package com.fd.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.fd.etf.stages.GameStage;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_Y;

/**
 * Created by ARudyk on 7/1/2016.
 */
public class AbstractDialog {

    public static final String LIB_SHADOW = "lib_shadow";
    public static final int HIDE_Y = 900;
    public static final float POPUP_MOVE_DURATION = 1.6f;

    protected ItemWrapper gameItem;
    protected Entity shadowE;
    public static AtomicBoolean isDialogOpen = new AtomicBoolean(false);
    public static AtomicBoolean isSecondDialogOpen = new AtomicBoolean(false);
    public static AtomicBoolean isSecondDialogClosed = new AtomicBoolean(false);
    public boolean isActive;
    protected GameStage gameStage;

    public AbstractDialog(GameStage gameStage) {
        this.gameStage = gameStage;
    }

    protected void initShadow() {
        if (shadowE == null) {
            CompositeItemVO tempC = gameStage.sceneLoader.loadVoFromLibrary(LIB_SHADOW).clone();
            shadowE = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), tempC);
            gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), shadowE, tempC.composite);
            gameStage.sceneLoader.getEngine().addEntity(shadowE);
        }
        hideShadow();
    }

    protected void hideShadow() {
        shadowE.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        shadowE.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
    }

    protected void addShadow() {
        shadowE.getComponent(TransformComponent.class).x = 0;
        shadowE.getComponent(TransformComponent.class).y = 0;
        shadowE.getComponent(ZIndexComponent.class).setZIndex(59);
        shadowE.getComponent(TintComponent.class).color.a = 0;

        ActionComponent ac = gameStage.sceneLoader.engine.createComponent(ActionComponent.class);
        Actions.checkInit();
        ac.dataArray.add(Actions.fadeIn(0.5f, Interpolation.exp5, 0.7f));
        shadowE.add(ac);
    }

    public void close (Entity e){
        if (isActive) {
            isActive = false;
            ActionComponent ac = gameStage.sceneLoader.engine.createComponent(ActionComponent.class);
            Actions.checkInit();
            ac.dataArray.add(Actions.moveTo(e.getComponent(TransformComponent.class).x, HIDE_Y, 1, Interpolation.exp10));
            e.add(ac);

            ActionComponent ac2 = gameStage.sceneLoader.engine.createComponent(ActionComponent.class);
            ac2.dataArray.add(Actions.fadeOut(0.5f, Interpolation.exp5));
            shadowE.add(ac2);

            if (isSecondDialogOpen.get()) {
                isSecondDialogClosed.set(true);
            } else {
                isDialogOpen.set(false);
            }
        }
    }

    public static void checkSecondaryDialog(){
        if (isSecondDialogClosed.get()){
            isSecondDialogOpen.set(false);
//            isSecondDialogClosed.set(false);
        }
    }
}
