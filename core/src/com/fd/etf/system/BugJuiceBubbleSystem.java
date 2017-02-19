package com.fd.etf.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Interpolation;
import com.fd.etf.entity.componets.BugJuiceBubbleComponent;
import com.fd.etf.stages.GameScreenScript;
import com.fd.etf.stages.GameStage;
import com.fd.etf.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.systems.action.Actions;

import javax.swing.*;

public class BugJuiceBubbleSystem extends IteratingSystem {
    private ComponentMapper<BugJuiceBubbleComponent> mapper = ComponentMapper.getFor(BugJuiceBubbleComponent.class);

    private GameStage gameStage;
    public static boolean isCalculatingScore;

    public BugJuiceBubbleSystem(GameStage gameStage) {
        super(Family.all(BugJuiceBubbleComponent.class).get());
        this.gameStage = gameStage;
    }

    protected void processEntity(Entity entity, float deltaTime) {
        BugJuiceBubbleComponent bjc = mapper.get(entity);

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
    }

    public void update(BugJuiceBubbleComponent bjc, TransformComponent tc, float percent) {
        setPosition(tc, bjc.startX + (bjc.endX - bjc.startX) * percent, bjc.startY + (bjc.endY - bjc.startY) * percent);

    }

    protected void begin(BugJuiceBubbleComponent bjc, TransformComponent tc) {
        bjc.startX = tc.x;
        bjc.startY = tc.y;
    }

    protected void end(Entity entity) {
        isCalculatingScore = true;

        if(gameStage.gameScript.scoreCE.getComponent(ActionComponent.class) == null){
            gameStage.gameScript.scoreCE.add(new ActionComponent());
        }
        gameStage.gameScript.scoreCE.getComponent(ActionComponent.class).reset();
        float moveMulti = 5f;
        float sizeMulti = 1.5f;
//        if (moveMulti > 5f){
//            moveMulti = 5f;
//        }
        if (sizeMulti > 2f){
            sizeMulti = 2f;
        }
        if(gameStage.gameScript.fpc.scoreDiff > 0) {
            gameStage.gameScript.scoreCE.getComponent(ActionComponent.class).dataArray.add(
                    Actions.sequence(
                            Actions.parallel(
                                    Actions.moveBy(-15 * moveMulti, 0, 0.2f, Interpolation.exp5),
                                    Actions.scaleTo(1.2f * sizeMulti, 1.2f * sizeMulti, 0.2f, Interpolation.exp5)),
                            Actions.parallel(
                                    Actions.moveBy(15 * moveMulti, 0, 0.2f, Interpolation.exp5),
                                    Actions.scaleTo(1f, 1f, 0.2f, Interpolation.fade))));
            if(gameStage.gameScript.scoreCE.getComponent(TransformComponent.class).x < 500){
                gameStage.gameScript.scoreCE.getComponent(TransformComponent.class).x = 520;
            }
        }
        gameStage.sceneLoader.getEngine().removeEntity(entity);
    }

    public void setPosition(TransformComponent tc, float x, float y) {
        tc.x = x;
        tc.y = y;
    }

}
