package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.mygdx.game.entity.componets.BugComponent;
import com.mygdx.game.entity.componets.BugType;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.stages.GameScreenScript;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.BugPool;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import static com.mygdx.game.entity.componets.BugComponent.State.DEAD;
import static com.mygdx.game.utils.GlobalConstants.*;
import static com.mygdx.game.stages.GameScreenScript.*;

import static com.mygdx.game.utils.BugPool.*;


/**
 * Created by Teatree on 9/3/2015.
 */
public class BugSystem extends IteratingSystem {

    private ComponentMapper<BugComponent> mapper = ComponentMapper.getFor(BugComponent.class);
    private ComponentMapper<FlowerPublicComponent> fMapper = ComponentMapper.getFor(FlowerPublicComponent.class);

    boolean canPlayAnimation = true;

    public BugSystem(){
         super(Family.all(BugComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SpriteAnimationComponent sac = ComponentRetriever.get(entity, SpriteAnimationComponent.class);
        SpriteAnimationStateComponent sasc = ComponentRetriever.get(entity, SpriteAnimationStateComponent.class);

        if (!GameScreenScript.isPause && !GameScreenScript.isGameOver) {
            TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
            DimensionsComponent dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);
            transformComponent.scaleX = BUG_SCALE;
            transformComponent.scaleY = BUG_SCALE;
            FlowerPublicComponent fcc = fMapper.get(entity);
            BugComponent bugComponent = mapper.get(entity);

            if (bugComponent.state != DEAD) {
                updateRect(bugComponent, transformComponent, dimensionsComponent);
//          transformComponent.y = bugComponent.startYPosition + 0.5f;
                moveEntity(deltaTime, transformComponent, bugComponent, sasc, sac);
                if (checkFlowerCollision(fcc, bugComponent)) {

                    bugComponent.state = DEAD;
                    fcc.score += bugComponent.points;
                    scoreLabelComponent.text.replace(0, scoreLabelComponent.text.capacity(), "" + fcc.score);

                    if (bugComponent.type.equals(BugType.QUEENBEE)) {
                        BugSpawnSystem.isAngeredBeesMode = true;
                        BugSpawnSystem.queenBeeOnStage = false;
                    }
//                    GameStage.sceneLoader.getEngine().removeEntity(entity);
                    BugPool.getInstance().release(entity);
                    fcc.isCollision = true;
                }
                if (isOutOfBounds(bugComponent)) {
                    BugPool.getInstance().release(entity);
//                    GameStage.sceneLoader.getEngine().removeEntity(entity);
//                    GameScreenScript.isGameOver = true;
                }
            }
        } else {
            sasc.paused = true;
        }


    }

    private boolean checkFlowerCollision(FlowerPublicComponent fcc, BugComponent bc){

//        fcc.isCollision = fcc.boundsRect.overlaps(bc.boundsRect);
        return fcc.boundsRect.overlaps(bc.boundsRect);
    }

    private void moveEntity(float deltaTime, TransformComponent transformComponent, BugComponent bugComponent,
                            SpriteAnimationStateComponent sasc, SpriteAnimationComponent sac){
        switch (bugComponent.type.toString()){
            case SIMPLE:
                transformComponent.y = bugComponent.startYPosition + (-(float) Math.cos(transformComponent.x / 20) * 75);
                transformComponent.x += bugComponent.velocity;
                bugComponent.velocity += deltaTime * 0.4;
                break;
            case DRUNK:
                transformComponent.y = bugComponent.startYPosition + (-(float) Math.cos(transformComponent.x / 20) * 75);
                transformComponent.x += bugComponent.velocity;
                bugComponent.velocity += deltaTime * 0.4;
                break;
            case CHARGER:
                bugComponent.counter--;

                // Move
                transformComponent.x += bugComponent.velocity;

                // Idle
                if (bugComponent.state == BugComponent.State.IDLE) {
                    bugComponent.velocity = deltaTime * IDLE_MVMNT_SPEED;
                    setAnimation("Idle", Animation.PlayMode.LOOP, sasc, sac);
                    if (bugComponent.counter == 0) {
//                        spriterActor.setAnimation(1);
//                        animationComponent.animations
                        canPlayAnimation = true;
                        setAnimation("Preparing", Animation.PlayMode.LOOP, sasc, sac);
                        bugComponent.counter = PREPARATION_TIME;
                        bugComponent.state = BugComponent.State.PREPARING;
                    }
                }
                // Preparing
                else if (bugComponent.state == BugComponent.State.PREPARING) {
                    bugComponent.velocity = deltaTime * PREPARING_MVMNT_SPEED;
                    if (bugComponent.counter == 0) {
//                        spriterActor.setAnimation(2);
                        bugComponent.state = BugComponent.State.CHARGING;
                        canPlayAnimation = true;
                        setAnimation("Charging", Animation.PlayMode.LOOP, sasc, sac);
                        bugComponent.velocity = deltaTime * CHARGING_MVMNT_SPEED;
                    }
                }
                // Charging
                else if (bugComponent.state == BugComponent.State.CHARGING) {
                    bugComponent.velocity += deltaTime * 3.4;
                }
                break;
            case BEE:
                transformComponent.y = bugComponent.startYPosition + (-(float) Math.cos(transformComponent.x / 20) * 75);
                transformComponent.x += bugComponent.velocity;
                bugComponent.velocity += deltaTime * 0.4;
                break;
            case QUEENBEE:
                transformComponent.y = bugComponent.startYPosition + (-(float) Math.cos(transformComponent.x / 20) * 75);
                transformComponent.x += bugComponent.velocity;
                bugComponent.velocity += deltaTime * 0.4;
                break;
            default:
                break;
        }
    }

    public void updateRect(BugComponent bc, TransformComponent tc, DimensionsComponent dc) {
        bc.boundsRect.x = (int)tc.x;
        bc.boundsRect.y = (int)tc.y;
        bc.boundsRect.width = (int)dc.width*tc.scaleX;
        bc.boundsRect.height = (int)dc.height*tc.scaleY;
    }

    public boolean isOutOfBounds(BugComponent bc){
        if (bc.boundsRect.getX() >= Gdx.graphics.getWidth()){
            return true;
        }
        return false;
    }

    public void setAnimation(String animationName, Animation.PlayMode mode, SpriteAnimationStateComponent sasComponent, SpriteAnimationComponent saComponent){
        if (canPlayAnimation) {
            sasComponent.set(saComponent.frameRangeMap.get(animationName), FPS, mode);
            canPlayAnimation = false;
        }
    }
}
