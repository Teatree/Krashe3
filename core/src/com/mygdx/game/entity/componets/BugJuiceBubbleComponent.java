package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.system.BugJuiceBubbleSystem;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;

/**
 * Created by AnastasiiaRudyk on 12/26/2015.
 */
public class BugJuiceBubbleComponent implements Component {
    public float startX, startY;
//    public float endX = Gdx.graphics.getWidth() - 120;
//    public float endY = Gdx.graphics.getHeight() - 120;
    public float endX = 1080;
    public float endY = 680;
    public float duration = 1;
    public float time;
    public float alpha = 0.2f;
    public Interpolation interpolation = Interpolation.fade;
    public boolean reverse, began, complete;
    public int alignment = Align.bottomLeft;
    public int counter;

    public Entity splatterEffectE;
    public TransformComponent tc2;

    public BugJuiceBubbleComponent() {
        CompositeItemVO splatterEffectC = GameStage.sceneLoader.loadVoFromLibrary("splatter_particle_lib");

        splatterEffectE = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), splatterEffectC);
        GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), splatterEffectE, splatterEffectC.composite);
        GameStage.sceneLoader.getEngine().addEntity(splatterEffectE);

        tc2 = splatterEffectE.getComponent(TransformComponent.class);
        tc2.x = 333;
        tc2.y = 333;
    }

}
