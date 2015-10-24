package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.mygdx.game.entity.componets.UmbrellaComponent;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import java.util.Random;

/**
 * Created by Teatree on 10/6/2015.
 */
public class UmbrelaSystem extends IteratingSystem {

    public Random random = new Random();
    private ComponentMapper<UmbrellaComponent> mapper = ComponentMapper.getFor(UmbrellaComponent.class);

    int randXmin = 110;
    int randXmax = 200;
    int randYmin = 45;
    int randYmax = 45;

    public UmbrelaSystem() {
        super(Family.all(UmbrellaComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        DimensionsComponent dc = ComponentRetriever.get(entity, DimensionsComponent.class);

        TransformComponent tc = ComponentRetriever.get(entity, TransformComponent.class);
        tc.scaleX = 0.2f;
        tc.scaleY = 0.2f;

        UmbrellaComponent uc = mapper.get(entity);
        if (uc.state == UmbrellaComponent.State.PUSH){
            pushUmbrella(uc, tc);
            uc.state = UmbrellaComponent.State.FLY;
        } else {
            flyUmbrella(uc, tc, deltaTime);
        }
        if (isOutOfBounds(uc)) {
            pushUmbrella(uc, tc);
        }
        updateRect(uc, tc, dc);
    }

    public void pushUmbrella(UmbrellaComponent uc, TransformComponent tc) {
        uc.velocityX = ((random.nextInt(randXmax-randXmin)+randXmin)*-1)*uc.speedIncrCoeficient;
//        gravity *= speedIncrCoeficient/2;
        System.out.println("velocityX " + uc.velocityX);
        if(tc.y> Gdx.graphics.getHeight()/2){
            uc.velocityY = (random.nextInt((randYmax-randYmin)+randYmin)*-1)*uc.speedIncrCoeficient;
        }else {
            uc.velocityY = (random.nextInt((randYmax - randYmin) + randYmin))*uc.speedIncrCoeficient;
        }
        System.out.println("velocityY " + uc.velocityY);
//        speedIncrCoeficient += 0.5f;
        uc.gravity = Math.abs(uc.velocityX/(7-uc.speedIncrCoeficient*uc.gravityDecreaseMultiplier));
        uc.speedIncrCoeficient += 0.1f;
        uc.gravityDecreaseMultiplier -= 0.05f;
        System.out.println("gravity " + uc.gravity);
    }

    public void flyUmbrella (UmbrellaComponent uc, TransformComponent tc, float delta){
        uc.velocityX += uc.gravity * delta;
        tc.x += uc.velocityX * delta;
        tc.y += uc.velocityY * delta;
    }
    public void updateRect(UmbrellaComponent uc, TransformComponent tc, DimensionsComponent dc) {
        uc.boundsRect.x = (int)tc.x;
        uc.boundsRect.y = (int)tc.y;
        uc.boundsRect.width = (int)dc.width;
        uc.boundsRect.height = (int)dc.height;
    }

    public boolean isOutOfBounds(UmbrellaComponent uc){
        if (uc.boundsRect.getX() >= Gdx.graphics.getWidth()){
            return true;
        }
        return false;
    }
}
