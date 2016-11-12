package com.fd.etf.entity.componets.listeners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.fd.etf.stages.GameScreenScript;
import com.fd.etf.stages.MenuScreenScript;
import com.fd.etf.stages.ShopScreenScript;
import com.fd.etf.stages.ui.BasicDialog;
import com.fd.etf.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by ARudyk on 8/3/2016.
 */
public abstract class ImageButtonListener implements ButtonComponent.ButtonListener {
    private AtomicBoolean[] atomicpropertiesToCheck = new AtomicBoolean[]{};
    public Entity btn;
    private Boolean [] propertiesToCheck;
    public Vector2 initialPos;

//    public ImageButtonListener(Entity btn, Boolean [] propertiesToCheck) {
//        this.propertiesToCheck = propertiesToCheck;
//        this.btn = btn;
//        initialPos = new Vector2(btn.getComponent(TransformComponent.class).x, btn.getComponent(TransformComponent.class).y);
//    }

    public ImageButtonListener(Entity btn, AtomicBoolean[] propertiesToCheck) {
        this.atomicpropertiesToCheck = propertiesToCheck;
        this.btn = btn;
        initialPos = new Vector2(btn.getComponent(TransformComponent.class).x, btn.getComponent(TransformComponent.class).y);
    }

    public ImageButtonListener(Entity btn) {
        this.propertiesToCheck = new Boolean[]{};
        this.btn = btn;
        initialPos = new Vector2(btn.getComponent(TransformComponent.class).x, btn.getComponent(TransformComponent.class).y);
    }

    public Vector2 getInitialPos(){
        return initialPos;
    }

    @Override
    public void touchUp() {
        Boolean shouldSkip = false;
//        if (propertiesToCheck.length != 0) {
//            for (Boolean b : propertiesToCheck) {
//                shouldSkip = shouldSkip || b;
//            }
//        }
        shouldSkip = false;
        if (atomicpropertiesToCheck.length != 0) {
            for (AtomicBoolean b : atomicpropertiesToCheck) {
                shouldSkip = shouldSkip || b.get();
            }
        }
        if (!shouldSkip) {
            btn.getComponent(TransformComponent.class).scaleX += GlobalConstants.TENTH;
            btn.getComponent(TransformComponent.class).scaleY += GlobalConstants.TENTH;
            btn.getComponent(TransformComponent.class).x -= btn.getComponent(DimensionsComponent.class).width / 20;
            btn.getComponent(TransformComponent.class).y -= btn.getComponent(DimensionsComponent.class).height / 20;
        }
    }

    @Override
    public void touchDown() {
        Boolean shouldSkip = false;
//        if (propertiesToCheck.length != 0) {
//            for (Boolean b : propertiesToCheck) {
//                shouldSkip = shouldSkip || b;
//            }
//        }

        shouldSkip = false;
        if (atomicpropertiesToCheck.length != 0) {
            for (AtomicBoolean b : atomicpropertiesToCheck) {
                shouldSkip = shouldSkip || b.get();
            }
        }
        if (!shouldSkip) {
            btn.getComponent(TransformComponent.class).scaleX -= GlobalConstants.TENTH;
            btn.getComponent(TransformComponent.class).scaleY -= GlobalConstants.TENTH;
            btn.getComponent(TransformComponent.class).x += btn.getComponent(DimensionsComponent.class).width / 20;
            btn.getComponent(TransformComponent.class).y += btn.getComponent(DimensionsComponent.class).height / 20;
        }
    }
}
