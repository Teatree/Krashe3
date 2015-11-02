package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.mygdx.game.entity.componets.ButterflyComponent;
import com.mygdx.game.entity.componets.FlowerCollisionComponent;
import com.mygdx.game.stages.GameStage;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import java.util.Random;

import static com.mygdx.game.entity.componets.ButterflyComponent.State.*;

/**
 * Created by Teatree on 9/3/2015.
 */
public class ButterflySystem extends IteratingSystem {

    private ComponentMapper<ButterflyComponent> mapper = ComponentMapper.getFor(ButterflyComponent.class);
    private ComponentMapper<FlowerCollisionComponent> collisionMapper = ComponentMapper.getFor(FlowerCollisionComponent.class);

    public Random random = new Random();

    public ButterflySystem() {
        super(Family.all(ButterflyComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        FlowerCollisionComponent fcc = collisionMapper.get(entity);
        ButterflyComponent bc = mapper.get(entity);

        TransformComponent tc = ComponentRetriever.get(entity, TransformComponent.class);
        DimensionsComponent dc = ComponentRetriever.get(entity, DimensionsComponent.class);
        LayerMapComponent lc = ComponentRetriever.get(entity, LayerMapComponent.class);

        if (bc.state.equals(SPAWN)) {
            push(bc, tc);
            bc.state = FLY;
        } else {
            fly(bc, tc, deltaTime);
        }
        updateRectangle(bc, tc, dc);

        if (checkCollision(bc, fcc)) {
            bc.state = DEAD;
            GameStage.sceneLoader.getEngine().removeEntity(entity);
        }
    }

    public void fly(ButterflyComponent bc, TransformComponent tc, float delta) {
//            if(isGameAlive()) {
        bc.velocityX += bc.gravity * delta;
        tc.x = tc.x + bc.velocityX * delta;
        tc.y = tc.y + +bc.velocityY * delta;
//            }
    }

    public void updateRectangle(ButterflyComponent bc, TransformComponent tc, DimensionsComponent dc) {
        bc.boundsRect.x = (int) tc.x;
        bc.boundsRect.y = (int) tc.y;
        bc.boundsRect.width = (int) dc.width * tc.scaleX;
        bc.boundsRect.height = (int) dc.height * tc.scaleY;
    }

    public boolean isOutOfBounds(ButterflyComponent bc) {
        return bc.boundsRect.getX() >= Gdx.graphics.getWidth();
    }

    public void push(ButterflyComponent bc, TransformComponent tc) {
        bc.velocityX = ((random.nextInt(bc.randXmax - bc.randXmin) + bc.randXmin) * -1) * bc.speedIncrCoeficient;
//        gravity *= speedIncrCoeficient/2;
//        System.out.println("velocityX " + bc.velocityX);
        if (tc.y > Gdx.graphics.getHeight() / 2) {
            bc.velocityY = (random.nextInt((bc.randYmax - bc.randYmin) + bc.randYmin) * -1) * bc.speedIncrCoeficient;
        } else {
            bc.velocityY = (random.nextInt((bc.randYmax - bc.randYmin) + bc.randYmin)) * bc.speedIncrCoeficient;
        }
//        System.out.println("velocityY " + bc.velocityY);
//        speedIncrCoeficient += 0.5f;
        bc.gravity = Math.abs(bc.velocityX / (7 - bc.speedIncrCoeficient * bc.gravityDecreaseMultiplier));
        bc.speedIncrCoeficient += 0.1f;
        bc.gravityDecreaseMultiplier -= 0.05f;
//        System.out.println("gravity " + bc.gravity);
    }

    private boolean checkCollision(ButterflyComponent bc, FlowerCollisionComponent fcc) {
        return bc.boundsRect.overlaps(fcc.boundsRect);
    }
}
