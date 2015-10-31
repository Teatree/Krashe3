package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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

    public static final int BITE_ANIMATION_TIME = 50;
    private ComponentMapper<FlowerComponent> mapper = ComponentMapper.getFor(FlowerComponent.class);
    private ComponentMapper<FlowerCollisionComponent> collisionMapper = ComponentMapper.getFor(FlowerCollisionComponent.class);
    private ShapeRenderer sr;

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
        act(fcc, flowerComponent, transformComponent, dimensionsComponent, layerComponent, deltaTime);

    }

    public void moveFlower() {

    }

    public void updateRect(FlowerCollisionComponent fcc, TransformComponent tc, DimensionsComponent dc) {
        fcc.boundsRect.x = (int) tc.x;
        fcc.boundsRect.y = (int) tc.y + 1500 * tc.scaleY;
        fcc.boundsRect.width = 150 * tc.scaleX;
        fcc.boundsRect.height = 150 * tc.scaleY;
//        System.out.println(fcc.boundsRect.x + " " + fcc.boundsRect.y + " " + fcc.boundsRect.width + " " + fcc.boundsRect.height);
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

    public void act(FlowerCollisionComponent fcc, FlowerComponent fc, TransformComponent tc, DimensionsComponent dc, LayerMapComponent lc, float delta) {
        if (fc.state == FlowerComponent.State.IDLE_BITE) {
            setBiteIdleAnimation(lc);
        }
//        if (fc.state == FlowerComponent.State.ATTACK_BITE) {
//            setBiteAttackAnimation(lc);
//        }

        if (fc.state == FlowerComponent.State.IDLE) {
            if (fcc.isCollision) {
                fc.state = FlowerComponent.State.IDLE_BITE;
                fc.eatCounter = BITE_ANIMATION_TIME;
                fcc.isCollision = false;
            } else {
                setIdleAnimation(lc);
            }
        }
        if (Gdx.input.justTouched() && fc.state != FlowerComponent.State.ATTACK) {
            fc.state = FlowerComponent.State.ATTACK;

            setAttackAnimation(lc);
//                fc.eatCounter = 0;
        }

        if (fc.state == ATTACK || fc.state == FlowerComponent.State.RETREAT) {
            if (fcc.isCollision) {
                fc.state = FlowerComponent.State.ATTACK_BITE;
                fc.eatCounter = BITE_ANIMATION_TIME;
                setBiteAttackAnimation(lc);
                fcc.isCollision = false;
            } else {
                move(tc, fc);
                if (tc.y >= -200 && fc.state == FlowerComponent.State.ATTACK) {
                    fc.state = FlowerComponent.State.RETREAT;
                }
                if (tc.y == -774 && fc.state == FlowerComponent.State.RETREAT) {
                    fc.state = FlowerComponent.State.IDLE;
                    setIdleAnimation(lc);
                }
            }
        }

        if (fc.state == ATTACK_BITE || fc.state == IDLE_BITE) {
            fc.eatCounter--;

            if (fc.state == ATTACK_BITE && fc.eatCounter == 0) {
                fc.state = FlowerComponent.State.RETREAT;
                setAttackAnimation(lc);
            }
            if (fc.state == IDLE_BITE && fc.eatCounter == 0) {
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
        lc.getLayer("attackHeadIdle").isVisible = false;
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

    private void setBiteAttackAnimation(LayerMapComponent lc) {
        lc.getLayer("headIdle").isVisible = false;
        lc.getLayer("peduncleIdle").isVisible = false;
        lc.getLayer("leavesDynamic").isVisible = false;

        lc.getLayer("leavesStatic").isVisible = true;
        lc.getLayer("peduncleAttack").isVisible = true;
        lc.getLayer("attackHeadIdleIdle").isVisible = false;
        lc.getLayer("attackHeadIdle").isVisible = true;
        lc.getLayer("headBite").isVisible = false;
    }

    private void setBiteIdleAnimation(LayerMapComponent lc) {
        lc.getLayer("headIdle").isVisible = false;
        lc.getLayer("peduncleIdle").isVisible = true;
        lc.getLayer("leavesDynamic").isVisible = true;

        lc.getLayer("leavesStatic").isVisible = false;
        lc.getLayer("peduncleAttack").isVisible = false;
        lc.getLayer("attackHeadIdleIdle").isVisible = false;
        lc.getLayer("attackHeadIdle").isVisible = false;
        lc.getLayer("headBite").isVisible = true;
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
