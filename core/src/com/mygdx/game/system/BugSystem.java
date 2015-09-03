package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.entity.componets.BugComponent;
import com.mygdx.game.entity.componets.FlowerComponent;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

/**
 * Created by Teatree on 9/3/2015.
 */
public class BugSystem extends IteratingSystem {

    private ComponentMapper<BugComponent> mapper = ComponentMapper.getFor(BugComponent.class);

    public BugSystem(){
         super(Family.all(BugComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
        DimensionsComponent dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);
        BugComponent bugComponent = mapper.get(entity);

        updateRect(bugComponent, transformComponent, dimensionsComponent);
//        transformComponent.y = bugComponent.startYPosition + 0.5f;
        transformComponent.x += bugComponent.velocity;
        bugComponent.velocity += deltaTime * 0.4;
    }

    public void updateRect(BugComponent bc, TransformComponent tc, DimensionsComponent dc) {
        bc.boundsRect.x = (int)tc.x;
        bc.boundsRect.y = (int)tc.y;
        bc.boundsRect.width = (int)dc.width;
        bc.boundsRect.height = (int)dc.height;
    }

    public boolean isOutOfBounds(BugComponent bc, FlowerComponent fc){
//        Flower flower = ((GameStage) stage).game.flower;
        if (bc.boundsRect.getX() >= fc.boundsRect.getX()+fc.boundsRect.getWidth()+100){
            return true;
        }
        return false;
    }
}
