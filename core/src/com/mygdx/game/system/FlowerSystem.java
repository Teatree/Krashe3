package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.mygdx.game.entity.componets.BugComponent;
import com.mygdx.game.entity.componets.FlowerComponent;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

/**
 * Created by Teatree on 9/3/2015.
 */
public class FlowerSystem extends IteratingSystem {

    private ComponentMapper<FlowerComponent> mapper = ComponentMapper.getFor(FlowerComponent.class);

    public FlowerSystem() {
        super(Family.all(FlowerComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
        DimensionsComponent dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);
        transformComponent.scaleX = 0.6f;
        transformComponent.scaleY = 0.6f;
        FlowerComponent flowerComponent = mapper.get(entity);
        moveFlower();
        updateRect(flowerComponent, transformComponent, dimensionsComponent);


    }

    public void moveFlower(){

    }

    public void updateRect(FlowerComponent bc, TransformComponent tc, DimensionsComponent dc) {
        bc.boundsRect.x = (int)tc.x;
        bc.boundsRect.y = (int)tc.y;
        bc.boundsRect.width = (int)dc.width;
        bc.boundsRect.height = (int)dc.height;
    }

//    public void addMovementActionUp() {
//        transformComponent.addAction(
//                Actions.sequence(
//                        Actions.moveBy(0, 20)));
//    }
//
//    public void addMovementActionDown() {
//        item.addAction(
//                Actions.sequence(
//                        Actions.moveBy(0, -20)));
//    }

    public void act(float delta) {
//        if(isGameAlive()) {
//            updateRect();
////            checkForCollisions();
//
//            if (Gdx.input.justTouched() && !isMovingUp && headBoundsRect.getY() < 1000){
//                isEating = false;
//                saHead.setAnimation(0);
//                System.out.println("saHead get animations: " + saHead.getAnimations());
//
//                eatCounter = 0;
//            }
//
//            if (Gdx.input.justTouched() && !isMovingUp && headBoundsRect.getY() < 1000 && !isEating) {
//                if(!isEating) {
//                    System.out.print("Gdx.input.isTouched() " + Gdx.input.isTouched());
//                    System.out.println(" isMovingUp " + isMovingUp);
//                    isMovingUp = true;
//                }
//            }
//
//            if (!isMovingUp && headBoundsRect.getY() >= POINT_TRAVEL - 20 && !isEating) {
//                addMovementActionDown();
//
//                if (headBoundsRect.getY() <= POINT_TRAVEL) {
//                    saFlower.setVisible(true);
//
//                    saHead.setVisible(false);
//                    itemPeduncleImg.setVisible(false);
//                }
//            }
//
//            if (isMovingUp && !isEating) {
//                addMovementActionUp();
//                saFlower.setVisible(false);
//
//                saHead.setVisible(true);
//                itemPeduncleImg.setVisible(true);
//
//                if (headBoundsRect.getY() > 1000) {
//                    if (((GameStage) stage).game.cocoonPowerUp != null){
//                        ((GameStage) stage).game.cocoonPowerUp.getCocoonController().hit();
//                    }
//                    isMovingUp = false;
//                }
//            }
//
//            if (isEating){
//                eatCounter++;
//                if(eatCounter>=30){
//                    isMovingUp = false;
//                    eatCounter = 0;
//                    saHead.setAnimation(0);
//                    isEating = false;
//                }
//            }
//        }
    }
}
