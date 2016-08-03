package com.mygdx.etf.entity.componets.listeners;

import com.badlogic.ashley.core.Entity;
import com.uwsoft.editor.renderer.components.TintComponent;
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
        btn.getComponent(TintComponent.class).color.set(1, 1, 1, 1f);
    }

    @Override
    public void touchDown() {
        btn.getComponent(TintComponent.class).color.set(0, 0, 0, 0.5f);
    }
}
