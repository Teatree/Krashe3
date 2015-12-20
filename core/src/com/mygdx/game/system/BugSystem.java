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
            sasc.paused = false;

            DimensionsComponent dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);
            TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
            transformComponent.scaleX = BUG_SCALE;
            transformComponent.scaleY = BUG_SCALE;
            FlowerPublicComponent fcc = fMapper.get(entity);
            BugComponent bc = mapper.get(entity);

            if (bc.state != DEAD) {

                updateRect(bc, transformComponent, dimensionsComponent);
                moveEntity(deltaTime, transformComponent, bc, sasc, sac);

                if (checkFlowerCollision(fcc, bc)) {

                    bc.state = DEAD;
                    fcc.score += bc.points;

                    //temp
                    fcc.totalScore += bc.points;

                    scoreLabelComponent.text.replace(0, scoreLabelComponent.text.capacity(), "" + fcc.score + "/" + fcc.totalScore);

                    if (bc.type.equals(BugType.QUEENBEE)) {
                        GameScreenScript.angerBees();
                    }
//                    resetCharger(sac, sasc, bc);
                    BugPool.getInstance().release(entity);
                    fcc.isCollision = true;
                }
                if (isOutOfBounds(bc)) {
//                    resetCharger(sac, sasc, bc);
                    BugPool.getInstance().release(entity);
                }
            }
        } else {
            sasc.paused = true;
        }
    }

    private boolean checkFlowerCollision(FlowerPublicComponent fcc, BugComponent bc){
        return fcc.boundsRect.overlaps(bc.boundsRect);
    }

    private void moveEntity(float deltaTime, TransformComponent transformComponent, BugComponent bugComponent,
                            SpriteAnimationStateComponent sasc, SpriteAnimationComponent sac){
        switch (bugComponent.type.toString()){
            case SIMPLE:
                moveSimple(deltaTime, transformComponent, bugComponent);
                break;
            case DRUNK:
                moveSimple(deltaTime, transformComponent, bugComponent);
                break;
            case CHARGER:
                moveCharger(deltaTime, transformComponent, bugComponent, sasc, sac);
                break;
            case BEE:
                moveSimple(deltaTime, transformComponent, bugComponent);
                break;
            case QUEENBEE:
                moveSimple(deltaTime, transformComponent, bugComponent);
                break;
            default:
                break;
        }
    }

    private void moveCharger(float deltaTime, TransformComponent tc, BugComponent bc, SpriteAnimationStateComponent sasc, SpriteAnimationComponent sac) {
        bc.counter--;
        // Move
        tc.x += bc.velocity;

        // Idle
        if (bc.state == BugComponent.State.IDLE) {
            setAnimation("Idle", Animation.PlayMode.LOOP, sasc, sac);
            bc.velocity = deltaTime * IDLE_MVMNT_SPEED;
            if (bc.counter == 0) {
                canPlayAnimation = true;
                setAnimation("Preparing", Animation.PlayMode.LOOP, sasc, sac);
                bc.counter = PREPARATION_TIME;
                bc.state = BugComponent.State.PREPARING;
            }
        }
        // Preparing
        else if (bc.state == BugComponent.State.PREPARING) {
            bc.velocity = deltaTime * PREPARING_MVMNT_SPEED;
            if (bc.counter == 0) {
                bc.state = BugComponent.State.CHARGING;
                canPlayAnimation = true;
                setAnimation("Charging", Animation.PlayMode.LOOP, sasc, sac);
                bc.velocity = deltaTime * CHARGING_MVMNT_SPEED;
            }
        }
        // Charging
        else if (bc.state == BugComponent.State.CHARGING) {
            bc.velocity += deltaTime * 3.4;
        }
    }

    private void moveSimple(float deltaTime, TransformComponent transformComponent, BugComponent bugComponent) {
        transformComponent.y = bugComponent.startYPosition + (-(float) Math.cos(transformComponent.x / 20) * 75);
        transformComponent.x += bugComponent.velocity;
        bugComponent.velocity += deltaTime * 0.4;
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
