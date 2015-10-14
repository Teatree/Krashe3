package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.mygdx.game.entity.componets.BugComponent;
import com.mygdx.game.stages.GameStage;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import static com.mygdx.game.entity.componets.BugComponent.State.DEAD;
import static com.mygdx.game.utils.GlobalConstants.*;
import static com.mygdx.game.utils.SoundMgr.soundMgr;

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
        transformComponent.scaleX = 0.6f;
        transformComponent.scaleY = 0.6f;
        BugComponent bugComponent = mapper.get(entity);

        if (bugComponent.state != DEAD) {
            updateRect(bugComponent, transformComponent, dimensionsComponent);
//        transformComponent.y = bugComponent.startYPosition + 0.5f;
            moveEntity(deltaTime, transformComponent, bugComponent);
            if (isOutOfBounds(bugComponent)) {
                soundMgr.play("eat");
                bugComponent.state = DEAD;
                GameStage.sceneLoader.getEngine().removeEntity(entity);
            }
        }
    }

    private void moveEntity(float deltaTime, TransformComponent transformComponent, BugComponent bugComponent){
        switch (bugComponent.type.toString()){
            case "SIMPLE":
                transformComponent.y = bugComponent.startYPosition + (-(float) Math.cos(transformComponent.x / 20) * 75);
                transformComponent.x += bugComponent.velocity;
                bugComponent.velocity += deltaTime * 0.4;
                break;
            case "DRUNK":
                transformComponent.y = bugComponent.startYPosition + (-(float) Math.cos(transformComponent.x / 20) * 75);
                transformComponent.x += bugComponent.velocity;
                bugComponent.velocity += deltaTime * 0.4;
                break;
            case "CHARGER":
                bugComponent.counter--;

                // Move
                transformComponent.x += bugComponent.velocity;

                // Idle
                if (bugComponent.state == BugComponent.State.IDLE) {
                    bugComponent.velocity = deltaTime * IDLE_MVMNT_SPEED;
                    if (bugComponent.counter == 0) {
//                        spriterActor.setAnimation(1);
//                        animationComponent.animations
                        bugComponent.counter = PREPARATION_TIME;
                        bugComponent.state = BugComponent.State.PREPARING;
                    }
                }
                // Preparing
                else if (bugComponent.state == BugComponent.State.PREPARING) {
                    bugComponent.velocity = deltaTime * PREPARING_MVMNT_SPEED;
                    if (bugComponent.counter == 0) {
//                        spriterActor.setAnimation(2);
                        bugComponent.state = BugComponent.State.CHARGING;
                        bugComponent.velocity = deltaTime * CHARGING_MVMNT_SPEED;
                    }
                }
                // Charging
                else if (bugComponent.state == BugComponent.State.CHARGING) {
                    bugComponent.velocity += deltaTime * 3.4;
                }
                break;
            case "BEE":
                transformComponent.y = bugComponent.startYPosition + (-(float) Math.cos(transformComponent.x / 20) * 75);
                transformComponent.x += bugComponent.velocity;
                bugComponent.velocity += deltaTime * 0.4;
                break;
            case "QUEENBEE":
                transformComponent.y = bugComponent.startYPosition + (-(float) Math.cos(transformComponent.x / 20) * 75);
                transformComponent.x += bugComponent.velocity;
                bugComponent.velocity += deltaTime * 0.4;
                break;
            default:
                break;
        }
    }

    public void updateRect(BugComponent bc, TransformComponent tc, DimensionsComponent dc) {
        bc.boundsRect.x = (int)tc.x;
        bc.boundsRect.y = (int)tc.y;
        bc.boundsRect.width = (int)dc.width;
        bc.boundsRect.height = (int)dc.height;
    }

//    public boolean isOutOfBounds(BugComponent bc, FlowerComponent fc){
////        Flower flower = ((GameStage) stage).game.flower;
//        if (bc.boundsRect.getX() >= fc.boundsRect.getX()+fc.boundsRect.getWidth()+100){
//            return true;
//        }
//        return false;
//    }
    public boolean isOutOfBounds(BugComponent bc){
        if (bc.boundsRect.getX() >= Gdx.graphics.getWidth()){
            return true;
        }
        return false;
    }
}
