package com.fd.etf.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.fd.etf.entity.componets.DebugComponent;
import com.fd.etf.stages.GameScreenScript;
import com.fd.etf.stages.GameStage;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;

/**
 * Created by ARudyk on 11/9/2016. Yeah I know
 */
public class DebugSystem extends IteratingSystem {

    private boolean traceEveryone;
    private GameStage gameStage;

    public DebugSystem(GameStage gameStage) {
        super(Family.all(DebugComponent.class).get());
        this.priority = 0;
        this.gameStage = gameStage;
    }

    @Override
    protected void processEntity(Entity e, float deltaTime) {
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            traceEveryone = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            traceEveryone = false;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            GameScreenScript.isPause.set(true);
            traceEveryone = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.O)) {
            GameScreenScript.isPause.set(false);
            traceEveryone = false;
        }

//        if (traceEveryone) {
            if (e.getComponent(DebugComponent.class).boundingBox != null){
                gameStage.sceneLoader.renderer.drawDebugRect(e.getComponent(DebugComponent.class).boundingBox.x,
                        e.getComponent(DebugComponent.class).boundingBox.y,
                        e.getComponent(DebugComponent.class).boundingBox.width,
                        e.getComponent(DebugComponent.class).boundingBox.height,
                        e.toString());
//                System.out.println("E: " + e + " bounding Bx!");n
            } else {
                gameStage.sceneLoader.renderer.drawDebugRect(e.getComponent(TransformComponent.class).x,
                        e.getComponent(TransformComponent.class).y,
                        e.getComponent(DimensionsComponent.class).width,
                        e.getComponent(DimensionsComponent.class).height,
                        e.toString());
//                System.out.println("E: " + e + " TRANSFORM + DIMENSION!");
            }
//        }
    }
}
