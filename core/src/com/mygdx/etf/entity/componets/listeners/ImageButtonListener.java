package com.mygdx.etf.entity.componets.listeners;

import com.badlogic.ashley.core.Entity;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;

/**
 * Created by ARudyk on 8/3/2016.
 */
public abstract class ImageButtonListener implements ButtonComponent.ButtonListener{
    private Entity btn;

    public ImageButtonListener (Entity btn){
        this.btn = btn;
    }

    @Override
    public void touchUp() {
        btn.getComponent(TransformComponent.class).scaleX +=0.1f;
        btn.getComponent(TransformComponent.class).scaleY +=0.1f;
    }

    @Override
    public void touchDown() {
        btn.getComponent(TransformComponent.class).scaleX -=0.1f;
        btn.getComponent(TransformComponent.class).scaleY -=0.1f;
    }
}
