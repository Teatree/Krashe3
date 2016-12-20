package com.fd.etf.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fd.etf.entity.componets.LeafsComponent;
import com.fd.etf.stages.GameScreenScript;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import static com.fd.etf.entity.componets.LeafsComponent.LEAFS_SCALE;
import static com.fd.etf.utils.GlobalConstants.FPS;

public class LeafsSystem extends IteratingSystem {

    public LeafsSystem() {
        super(Family.all(LeafsComponent.class).get());
    }

    @Override
    protected void processEntity(Entity e, float deltaTime) {

        SpriterComponent spriterComponentLeafs = ComponentRetriever.get(e, SpriterComponent.class);
        spriterComponentLeafs.scale = LEAFS_SCALE;

        if (!GameScreenScript.isPause.get() && !GameScreenScript.isGameOver.get()) {
            e.getComponent(SpriterComponent.class).player.speed = FPS;
        } else {
            e.getComponent(SpriterComponent.class).player.speed = 0;
        }
    }
}
