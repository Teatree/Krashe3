package com.fd.etf.entity.componets.listeners;

import com.badlogic.ashley.core.Entity;
import com.fd.etf.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;

/**
 * Created by ARudyk on 8/3/2016.
 */
public abstract class ImageButtonListener implements ButtonComponent.ButtonListener {
    public Entity btn;

    public ImageButtonListener(Entity btn) {
        this.btn = btn;
    }

    @Override
    public void touchUp() {
        btn.getComponent(TransformComponent.class).scaleX += GlobalConstants.TENTH;
        btn.getComponent(TransformComponent.class).scaleY += GlobalConstants.TENTH;
        btn.getComponent(TransformComponent.class).x -= btn.getComponent(DimensionsComponent.class).width / 20;
        btn.getComponent(TransformComponent.class).y -= btn.getComponent(DimensionsComponent.class).height / 20;
    }

    @Override
    public void touchDown() {
        btn.getComponent(TransformComponent.class).scaleX -= GlobalConstants.TENTH;
        btn.getComponent(TransformComponent.class).scaleY -= GlobalConstants.TENTH;
        btn.getComponent(TransformComponent.class).x += btn.getComponent(DimensionsComponent.class).width / 20;
        btn.getComponent(TransformComponent.class).y += btn.getComponent(DimensionsComponent.class).height / 20;
    }
}
