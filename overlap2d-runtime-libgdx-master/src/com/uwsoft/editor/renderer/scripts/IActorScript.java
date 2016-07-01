package com.uwsoft.editor.renderer.scripts;

import com.uwsoft.editor.renderer.scene2d.CompositeActor;


public interface IActorScript {
    void init(CompositeActor entity);

    void act(float delta);

    void dispose();
}
