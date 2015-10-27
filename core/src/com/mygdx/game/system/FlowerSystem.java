package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.mygdx.game.entity.componets.FlowerCollisionComponent;
import com.mygdx.game.entity.componets.FlowerComponent;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import static com.mygdx.game.entity.componets.FlowerComponent.State.*;

/**
 * Created by Teatree on 9/3/2015.
 */
public class FlowerSystem extends IteratingSystem {

    private ComponentMapper<FlowerComponent> mapper = ComponentMapper.getFor(FlowerComponent.class);
    private ComponentMapper<FlowerCollisionComponent> collisionMapper = ComponentMapper.getFor(FlowerCollisionComponent.class);

    public FlowerSystem() {
        super(Family.all(FlowerComponent.class).get());

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
        DimensionsComponent dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);
        LayerMapComponent layerComponent = ComponentRetriever.get(entity, LayerMapComponent.class);
        transformComponent.scaleX = 0.6f;
        transformComponent.scaleY = 0.6f;
        FlowerComponent flowerComponent = mapper.get(entity);
        FlowerCollisionComponent fcc = collisionMapper.get(entity);
        moveFlower();
        updateRect(fcc, transformComponent, dimensionsComponent);
        act(flowerComponent, transformComponent, dimensionsComponent, layerComponent, deltaTime);
    }

    public void moveFlower() {

    }

    public void updateRect(FlowerCollisionComponent fcc, TransformComponent tc, DimensionsComponent dc) {
        fcc.boundsRect.x = (int) tc.x - 29;
        fcc.boundsRect.y = (int) tc.y + 1181;
        fcc.boundsRect.width = 250;
        fcc.boundsRect.height = 350;
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

    public void act(FlowerComponent fc, TransformComponent tc, DimensionsComponent dc, LayerMapComponent lc, float delta) {

        if (fc.state == FlowerComponent.State.IDLE) {
            setIdleAnimation(lc);
        }
        if (Gdx.input.justTouched() && fc.state != FlowerComponent.State.ATTACK) {
            fc.state = FlowerComponent.State.ATTACK;

            setAttackAnimation(lc);

//                fc.eatCounter = 0;
        }

        if (fc.state == ATTACK || fc.state == FlowerComponent.State.RETREAT) {
            move(tc, fc);

            if (tc.y >= -200 && fc.state == FlowerComponent.State.ATTACK) {
                fc.state = FlowerComponent.State.RETREAT;
            }
            if (tc.y == -774 && fc.state == FlowerComponent.State.RETREAT) {
                fc.state = FlowerComponent.State.IDLE;
                setIdleAnimation(lc);
            }
        }


//            if (Gdx.input.justTouched() && fc.state != FlowerComponent.State.ATTACK && fc.boundsRect.getY() < 1000) {
//                if(!isEating) {
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
    }

    private void setAttackAnimation(LayerMapComponent lc) {
        lc.getLayer("headIdle").isVisible = false;
        lc.getLayer("peduncleIdle").isVisible = false;
        lc.getLayer("leavesDynamic").isVisible = false;

        lc.getLayer("leavesStatic").isVisible = true;
        lc.getLayer("peduncleAttack").isVisible = true;
        lc.getLayer("attackHeadIdleIdle").isVisible = true;
    }

    private void setIdleAnimation(LayerMapComponent lc) {
        lc.getLayer("headIdle").isVisible = true;
        lc.getLayer("peduncleIdle").isVisible = true;
        lc.getLayer("leavesDynamic").isVisible = true;

        lc.getLayer("leavesStatic").isVisible = false;
        lc.getLayer("peduncleAttack").isVisible = false;
        lc.getLayer("attackHeadIdleIdle").isVisible = false;
        lc.getLayer("attackHeadIdle").isVisible = false;
        lc.getLayer("headBite").isVisible = false;
    }

    public void move(TransformComponent tc, FlowerComponent fc) {
        tc.y += fc.state == FlowerComponent.State.ATTACK ? 7.5f : -7.5f;
//        tc.y += fc.state == FlowerComponent.State.RETREAT ? -2.5f : 0f ;

//        tc.y += 2.5f;
//        System.out.println("BOOM!");
    }

//    public void moveDown(TransformComponent tc){
//        tc.y -= 2.5f;
//    }
}
