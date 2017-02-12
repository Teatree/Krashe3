package com.fd.etf.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Interpolation;
import com.fd.etf.entity.componets.PetProjectileComponent;
import com.fd.etf.stages.GameScreenScript;
import com.fd.etf.stages.GameStage;
import com.fd.etf.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;

import java.util.Random;

public class PetProjectileSystem extends IteratingSystem {
    private ComponentMapper<PetProjectileComponent> mapper = ComponentMapper.getFor(PetProjectileComponent.class);

    private GameStage gameStage;
    public static boolean isCalculatingScore;

    public PetProjectileSystem(GameStage gameStage) {
        super(Family.all(PetProjectileComponent.class).get());
        this.gameStage = gameStage;
    }

    protected void processEntity(Entity entity, float deltaTime) {
        PetProjectileComponent bjc = mapper.get(entity);

        if (GameScreenScript.isGameOver.get() || !GameScreenScript.isStarted) {
            entity.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
            bjc.complete = true;
            end(entity);
        } else {
            if (!bjc.began) {
                begin(bjc, entity.getComponent(TransformComponent.class));
                bjc.began = true;
            }
            bjc.time += deltaTime;
            bjc.complete = bjc.time >= bjc.duration;
            float percent;
            if (bjc.complete) {
                percent = 1;
            } else {
                percent = bjc.time / bjc.duration;
                if (bjc.interpolation != null) percent = bjc.interpolation.apply(percent);
            }
            update(bjc, entity.getComponent(TransformComponent.class), bjc.reverse ? 1 - percent : percent);
            if (bjc.complete) end(entity);

            if (bjc.time <= (bjc.duration / 5)) {
                entity.getComponent(TintComponent.class).color.a = 0;
            } else if (bjc.time > (bjc.duration / 5) && bjc.time < (4 * bjc.duration / 5) && entity.getComponent(TintComponent.class).color.a <= 0.7f) {
                entity.getComponent(TintComponent.class).color.a += 0.1f;
            } else if (bjc.time >= (4 * bjc.duration / 5)) {
                entity.getComponent(TintComponent.class).color.a -= 0.05f;
            }
        }




//        // Soemthing
//
//        ActionComponent ac3 = new ActionComponent();
//        Actions.checkInit();
//        ac3.dataArray.add(Actions.moveBy(0, 150, 3.6f));
//
//        for(int i = 0; i < gameStage.gameScript.fpc.currentPet.projectilesNum ; i++){
//            CompositeItemVO tempItemC = gameStage.sceneLoader.loadVoFromLibrary("projectile_DOG");
//            Entity petPromoE = gameStage.sceneLoader.entityFactory.createSPRITERentity(gameStage.sceneLoader.getRoot(), tempItemC);
//            projectileEntities.add(petPromoE);
//            gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), projectileEntities.get(i), tempItemC.composite);
//            gameStage.sceneLoader.getEngine().addEntity(projectileEntities.get(i));
//            projectileEntities.get(i).add(ac3);
//        }
    }

    public void update(PetProjectileComponent bjc, TransformComponent tc, float percent) {
        setPosition(tc, bjc.startX + (bjc.endX - bjc.startX) * percent, bjc.startY + (bjc.endY - bjc.startY) * percent);

    }

    protected void begin(PetProjectileComponent bjc, TransformComponent tc) {
        bjc.startX = tc.x;
        bjc.startY = tc.y;
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

        gameStage.sceneLoader.getEngine().removeEntity(entity);
    }

    public void setPosition(TransformComponent tc, float x, float y) {
        tc.x = x;
        tc.y = y;
    }

}
