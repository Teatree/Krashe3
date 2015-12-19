package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.entity.componets.FlowerComponent;
import com.mygdx.game.stages.GameScreenScript;
import com.mygdx.game.stages.GameStage;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import static com.mygdx.game.entity.componets.FlowerComponent.State.*;
import static com.mygdx.game.utils.SoundMgr.soundMgr;

/**
 * Created by Teatree on 9/3/2015.
 */
public class FlowerSystem extends IteratingSystem {

    public static final int BITE_ANIMATION_TIME = 50;
    private ComponentMapper<FlowerComponent> mapper = ComponentMapper.getFor(FlowerComponent.class);
    private ComponentMapper<FlowerPublicComponent> collisionMapper = ComponentMapper.getFor(FlowerPublicComponent.class);
    private ShapeRenderer sr;

    public FlowerSystem() {
        super(Family.all(FlowerComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
        DimensionsComponent dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);
//        LayerMapComponent layerComponent = ComponentRetriever.get(entity, LayerMapComponent.class);
        SpriterComponent spriterComponent = ComponentRetriever.get(entity, SpriterComponent.class);
        spriterComponent.scale = 0.6f;
        FlowerComponent flowerComponent = mapper.get(entity);
        FlowerPublicComponent fcc = collisionMapper.get(entity);
        moveFlower();
        updateRect(fcc, transformComponent, dimensionsComponent);
        act(fcc, flowerComponent, transformComponent, dimensionsComponent, spriterComponent, deltaTime);

    }

    public void moveFlower() {

    }

    public void updateRect(FlowerPublicComponent fcc, TransformComponent tc, DimensionsComponent dc) {
        fcc.boundsRect.x = (int) tc.x;
        fcc.boundsRect.y = (int) tc.y + 105 * tc.scaleY;
        fcc.boundsRect.width = 150 * tc.scaleX;
        fcc.boundsRect.height = 150 * tc.scaleY;
//        System.out.println(fpc.boundsRect.x + " " + fpc.boundsRect.y + " " + fpc.boundsRect.width + " " + fpc.boundsRect.height);
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

    public void act(FlowerPublicComponent fcc, FlowerComponent fc, TransformComponent tc, DimensionsComponent dc, SpriterComponent sc, float delta) {
        if (!GameScreenScript.isPause && !GameScreenScript.isGameOver) {
            if (fc.state == FlowerComponent.State.IDLE_BITE) {
                setBiteIdleAnimation(sc);
            }
//        if (fc.state == FlowerComponent.State.ATTACK_BITE) {
//            setBiteAttackAnimation(lc);
//        }

            if (fc.state == FlowerComponent.State.IDLE) {
                if (fcc.isCollision) {
                    fc.state = FlowerComponent.State.IDLE_BITE;
                    fc.eatCounter = BITE_ANIMATION_TIME;
                    fcc.isCollision = false;

                    soundMgr.play("eat");
                } else {
                    setIdleAnimation(sc);
                }
            }
            if (Gdx.input.justTouched() && fc.state != FlowerComponent.State.ATTACK) {
                fc.state = FlowerComponent.State.ATTACK;

                setAttackAnimation(sc);
//                fc.eatCounter = 0;
            }

            if (fc.state == ATTACK || fc.state == FlowerComponent.State.RETREAT) {
                if (fcc.isCollision) {

                    fc.state = FlowerComponent.State.ATTACK_BITE;
                    fc.eatCounter = BITE_ANIMATION_TIME;
                    setBiteAttackAnimation(sc);
                    fcc.isCollision = false;

                    soundMgr.play("eat");
                } else {
                    move(tc, fc);
                    if (tc.y >= 660 && fc.state == FlowerComponent.State.ATTACK) {
                        fc.state = FlowerComponent.State.RETREAT;
                    }
                    if (tc.y <= 106 && fc.state == FlowerComponent.State.RETREAT) {
                        fc.state = FlowerComponent.State.IDLE;
                        setIdleAnimation(sc);
                    }
                }
            }

            if (fc.state == ATTACK_BITE || fc.state == IDLE_BITE) {
                fc.eatCounter--;

                if (fc.state == ATTACK_BITE && fc.eatCounter == 0) {
                    fc.state = FlowerComponent.State.RETREAT;
                    setAttackAnimation(sc);
                }
                if (fc.state == IDLE_BITE && fc.eatCounter == 0) {
                    fc.state = FlowerComponent.State.IDLE;
                    setIdleAnimation(sc);
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
    }

    private void setAttackAnimation(SpriterComponent sc) {
        sc.player.setAnimation(3);
    }

    private void setIdleAnimation(SpriterComponent sc) {
        sc.player.setAnimation(0);
    }

    private void setBiteAttackAnimation(SpriterComponent sc) {
        sc.player.setAnimation(4);
    }

    private void setBiteIdleAnimation(SpriterComponent sc) {
        sc.player.setAnimation(1);
    }

    public void move(TransformComponent tc, FlowerComponent fc) {
        tc.y += fc.state == FlowerComponent.State.ATTACK ? 11.5f : -11.5f;
//        tc.y += fc.state == FlowerComponent.State.RETREAT ? -2.5f : 0f ;

//        tc.y += 2.5f;
//        System.out.println("BOOM!");
    }

//    public void moveDown(TransformComponent tc){
//        tc.y -= 2.5f;
//    }
}
