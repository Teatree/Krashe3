package com.mygdx.game.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.entity.componets.BugJuiceBubbleComponent;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;

/**
 * Created by AnastasiiaRudyk on 12/26/2015.
 */
public class BugJuiceBubbleSystem extends IteratingSystem {

    private float startX, startY;
    private float endX = Gdx.graphics.getWidth();
    private float endY = Gdx.graphics.getHeight();
    private float duration = 10;
    private float time;
    private Interpolation interpolation;
    private boolean reverse, began, complete;
    private int alignment = Align.bottomLeft;

    public BugJuiceBubbleSystem() {
        super(Family.all(BugJuiceBubbleComponent.class).get());
    }

    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent tc = entity.getComponent(TransformComponent.class);
        DimensionsComponent dc = entity.getComponent(DimensionsComponent.class);

//        if (tc.x <= Gdx.graphics.getWidth() - dc.width || tc.y <= Gdx.graphics.getHeight() - dc.height) {
//            tc.x++;
//            tc.y++;
            if (!began) {
                begin(tc);
                began = true;
            }
            time += deltaTime;
            complete = time >= duration;
            float percent;
            if (complete) {
                percent = 1;
            } else {
                percent = time / duration;
                if (interpolation != null) percent = interpolation.apply(percent);
            }
            update(tc, reverse ? 1 - percent : percent);
            if (complete) end();
//        }
    }

    public void update(TransformComponent tc, float percent) {
        setPosition(tc, startX + (endX - startX) * percent, startY + (endY - startY) * percent);
    }

    protected void begin(TransformComponent tc) {
        startX = tc.x;
        startY = tc.y;
    }

    protected void end() {
    }

    public void setPosition(TransformComponent tc, float x, float y) {
        tc.x = x;
        tc.y = y;
    }

}
