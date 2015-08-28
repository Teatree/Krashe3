//package com.mygdx.game.actors.controllers;
//
//import com.badlogic.ashley.core.Entity;
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.math.Rectangle;
//import com.badlogic.gdx.scenes.scene2d.ui.Image;
//import com.mygdx.game.stages.GameStage;
//import com.uwsoft.editor.renderer.components.DimensionsComponent;
//import com.uwsoft.editor.renderer.components.TransformComponent;
//import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationComponent;
//import com.uwsoft.editor.renderer.scene2d.CompositeActor;
//import com.uwsoft.editor.renderer.scripts.IScript;
//import com.uwsoft.editor.renderer.utils.ComponentRetriever;
//import com.uwsoft.editor.renderer.utils.ItemWrapper;
//
//import java.awt.font.TransformAttribute;
//
//import static com.mygdx.game.stages.GameScreenScript.*;
//
///**
// * Created by MainUser on 07/06/2015.
// */
//public class FlowerController implements IScript {
//
//    public GameStage stage;
//
//    private ItemWrapper itemWrapper;
//    private TransformComponent transformComponent;
//    private DimensionsComponent dimensionsComponent;
//    private CompositeActor compositeActor;
//
//    private boolean isMovingUp = false;
//    private boolean isEating = false;
//
//    private int eatCounter;
//
//    public SpriteAnimationComponent saFlower;
//    public SpriteAnimationComponent saHead;
//
//    public Rectangle headBoundsRect = new Rectangle();
//
//    public Image itemPeduncleImg;
//
//    public FlowerController(GameStage stage) {
//        this.stage = stage;
//    }
//
//    @Override
//    public void init(Entity item) {
//        itemWrapper = new ItemWrapper(item);
//
//        transformComponent = ComponentRetriever.get(item, TransformComponent.class);
//        dimensionsComponent = ComponentRetriever.get(item, DimensionsComponent.class);
//
//        transformComponent.x = Gdx.graphics.getWidth() - 200;
//        transformComponent.y = 0;
//        transformComponent.originX = dimensionsComponent.width/ 2;
//        transformComponent.originY = 0;
//
//    }
//
//    public void addMovementActionUp() {
//        transformComponent.y += 20;
////        item.addAction(
////                Actions.sequence(
////                        Actions.moveBy(0, 20)));
//    }
//
//    public void addMovementActionDown() {
//        transformComponent.y -= 20;
////        item.addAction(
////                Actions.sequence(
////                        Actions.moveBy(0, -20)));
//    }
//
//    @Override
//    public void act(float delta) {
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
//    }
//
//    private void updateRect() {
//        headBoundsRect.x = item.getX() + item.getSpriterActorById("flower_head2").getX();
//        headBoundsRect.y = item.getY() + item.getSpriterActorById("flower_head2").getY();
//        headBoundsRect.width = item.getSpriterActorById("flower_head2").getWidth();
//        headBoundsRect.height = item.getSpriterActorById("flower_head2").getHeight();
//    }
//
//    @Override
//    public void dispose() {
//        item.dispose();
//        stage.dispose();
//    }
//
//    public void eat() {
//        isEating = true;
//        saHead.setAnimation(1);
//    }
//
//
//}
