package com.fd.etf.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fd.etf.entity.componets.PetProjectileComponent;
import com.fd.etf.stages.GameScreenScript;
import com.fd.etf.stages.GameStage;
import com.fd.etf.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;

public class PetProjectileSystem extends IteratingSystem {
    private ComponentMapper<PetProjectileComponent> mapper = ComponentMapper.getFor(PetProjectileComponent.class);

    private GameStage gameStage;

    public PetProjectileSystem(GameStage gameStage) {
        super(Family.all(PetProjectileComponent.class).get());
        this.gameStage = gameStage;
    }

    protected void processEntity(Entity entity, float deltaTime) {
        PetProjectileComponent ppc = mapper.get(entity);

        if (GameScreenScript.isGameOver.get() || !GameScreenScript.isStarted) {
            entity.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
            ppc.complete = true;
            end(entity);
        } else {
            if (!ppc.began) {
                begin(ppc, entity.getComponent(TransformComponent.class));
                ppc.began = true;
            }
            ppc.time += deltaTime;
            ppc.complete = ppc.time >= ppc.duration;
            float percent;
            if (ppc.complete) {
                percent = 1;
            } else {
                percent = ppc.time / ppc.duration;
                if (ppc.interpolation != null) percent = ppc.interpolation.apply(percent);
            }
            update(ppc, entity.getComponent(TransformComponent.class), ppc.reverse ? 1 - percent : percent);
            if (ppc.complete) end(entity);

            if (ppc.time <= (ppc.duration / 5)) {
                entity.getComponent(TintComponent.class).color.a = 0;
            } else if (ppc.time > (ppc.duration / 5) && ppc.time < (4 * ppc.duration / 5) && entity.getComponent(TintComponent.class).color.a <= 0.7f) {
                entity.getComponent(TintComponent.class).color.a += 0.1f;
            } else if (ppc.time >= (4 * ppc.duration / 5)) {
                entity.getComponent(TintComponent.class).color.a -= 0.05f;
            }
        }
    }

    public void updateRect(PetProjectileComponent ppc, TransformComponent tc) {
        ppc.boundsRect.x = (int) tc.x + 50;
        ppc.boundsRect.y = (int) tc.y + 30;
        ppc.boundsRect.width = 100;
        ppc.boundsRect.height = 100;
    }

    public void update(PetProjectileComponent ppc, TransformComponent tc, float percent) {
        setPosition(tc, ppc.startX + (ppc.endX - ppc.startX) * percent, ppc.startY + (ppc.endY - ppc.startY) * percent);
        updateRect(ppc, tc);
    }

    protected void begin(PetProjectileComponent ppc, TransformComponent tc) {
        ppc.startX = tc.x;
        ppc.startY = tc.y;

        gameStage.gameScript.projectileBounds.add(ppc.boundsRect);
    }

    protected void end(Entity entity) {
//        isCalculatingScore = true;

//        if(gameStage.gameScript.scoreCE.getComponent(ActionComponent.class) == null){
//            gameStage.gameScript.scoreCE.add(new ActionComponent());
//        }
//        gameStage.gameScript.scoreCE.getComponent(ActionComponent.class).reset();
//        float moveMulti = (gameStage.gameScript.fpc.score - gameStage.gameScript.fpc.oldScore)/3;
//        float sizeMulti = (gameStage.gameScript.fpc.score - gameStage.gameScript.fpc.oldScore)/20+1;
//        if (moveMulti > 5f){
//            moveMulti = 5f;
//        }
//        if (sizeMulti > 2f){
//            sizeMulti = 2f;
//        }
//        gameStage.gameScript.scoreCE.getComponent(ActionComponent.class).dataArray.add(
//                Actions.sequence(
//                        Actions.parallel(
//                                Actions.moveBy(-15*moveMulti, 0, 0.3f, Interpolation.exp5),
//                                Actions.scaleTo(1.2f*sizeMulti, 1.2f*sizeMulti, 0.3f, Interpolation.exp5)),
//                        Actions.parallel(
//                                Actions.moveBy(15*moveMulti, 0, 0.3f, Interpolation.exp5),
//                                Actions.scaleTo(1f, 1f, 0.3f, Interpolation.fade))));

        gameStage.gameScript.projectileBounds.remove(entity.getComponent(PetProjectileComponent.class).boundsRect);
        gameStage.sceneLoader.getEngine().removeEntity(entity);
    }

    public void setPosition(TransformComponent tc, float x, float y) {
        tc.x = x;
        tc.y = y;
    }

}
