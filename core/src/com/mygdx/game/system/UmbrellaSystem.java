package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.entity.componets.UmbrellaComponent;
import com.mygdx.game.stages.GameScreenScript;
import com.mygdx.game.stages.GameStage;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import java.util.Random;

import static com.mygdx.game.stages.GameScreenScript.scoreLabelComponent;

/**
 * Created by Teatree on 10/6/2015.
 */
public class UmbrellaSystem extends IteratingSystem {

    public Random random = new Random();
    private ComponentMapper<UmbrellaComponent> mapper = ComponentMapper.getFor(UmbrellaComponent.class);
    private ComponentMapper<FlowerPublicComponent> fccMapper = ComponentMapper.getFor(FlowerPublicComponent.class);

    int randXmin = 110;
    int randXmax = 150;
    int randYmin = 45;
    int randYmax = 45;

    public UmbrellaSystem() {
        super(Family.all(UmbrellaComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (!GameScreenScript.isPause && !GameScreenScript.isGameOver) {
            DimensionsComponent dc = ComponentRetriever.get(entity, DimensionsComponent.class);

            TransformComponent tc = ComponentRetriever.get(entity, TransformComponent.class);
            tc.scaleX = 0.2f;
            tc.scaleY = 0.2f;

            FlowerPublicComponent fcc = fccMapper.get(entity);
            UmbrellaComponent uc = mapper.get(entity);

            move(deltaTime, tc, uc);
            updateRect(uc, tc, dc);

            if (checkCollision(uc, fcc)) {
                fcc.isCollision = true;
                GameStage.sceneLoader.getEngine().removeEntity(entity);
                fcc.score *= uc.pointsMult;
                scoreLabelComponent.text.replace(0, scoreLabelComponent.text.capacity(), "" + fcc.score);
            }
        }
    }

    private void move(float deltaTime, TransformComponent tc, UmbrellaComponent uc) {
        if (uc.state == UmbrellaComponent.State.PUSH) {
            pushUmbrella(uc, tc);
            uc.state = UmbrellaComponent.State.FLY;
        } else {
            flyUmbrella(uc, tc, deltaTime);
        }
        if (isOutOfBounds(uc)) {
            pushUmbrella(uc, tc);
        }
    }

    public void pushUmbrella(UmbrellaComponent uc, TransformComponent tc) {
        uc.velocityX = ((random.nextInt(randXmax - randXmin) + randXmin) * -1) * uc.speedIncrCoeficient;
//        gravity *= speedIncrCoeficient/2;
        if (tc.y > Gdx.graphics.getHeight() / 2) {
            uc.velocityY = (random.nextInt((randYmax - randYmin) + randYmin) * -1) * uc.speedIncrCoeficient;
        } else {
            uc.velocityY = (random.nextInt((randYmax - randYmin) + randYmin)) * uc.speedIncrCoeficient;
        }
//        speedIncrCoeficient += 0.5f;
        uc.gravity = Math.abs(uc.velocityX / (7 - uc.speedIncrCoeficient * uc.gravityDecreaseMultiplier));
        uc.speedIncrCoeficient += 0.1f;
        uc.gravityDecreaseMultiplier -= 0.05f;
    }

    public void flyUmbrella(UmbrellaComponent uc, TransformComponent tc, float delta) {
        uc.velocityX += uc.gravity * delta;
        tc.x += uc.velocityX * delta;
        tc.y += uc.velocityY * delta;
    }

    public void updateRect(UmbrellaComponent uc, TransformComponent tc, DimensionsComponent dc) {
        uc.boundsRect.x = (int) tc.x;
        uc.boundsRect.y = (int) tc.y;
        uc.boundsRect.width = (int) dc.width * tc.scaleX;
        uc.boundsRect.height = (int) dc.height * tc.scaleY;
    }

    public boolean isOutOfBounds(UmbrellaComponent uc) {
        return uc.boundsRect.getX() >= Gdx.graphics.getWidth();
    }

    private boolean checkCollision(UmbrellaComponent bc, FlowerPublicComponent fcc) {
        return bc.boundsRect.overlaps(fcc.boundsRect);
    }
}
