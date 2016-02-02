package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.mygdx.game.entity.componets.BugComponent;
import com.mygdx.game.entity.componets.BugJuiceBubbleComponent;
import com.mygdx.game.entity.componets.BugType;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.utils.BugPool;
import com.mygdx.game.utils.EffectUtils;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import static com.mygdx.game.entity.componets.BugComponent.State.*;
import static com.mygdx.game.utils.GlobalConstants.*;
import static com.mygdx.game.stages.GameScreenScript.*;
import static com.mygdx.game.stages.GameStage.*;
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

        if (!isPause && !isGameOver) {
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
                    fcc.totalScore += bc.points;

                    if (bc.type.equals(BugType.QUEENBEE)) {
                        angerBees();
                    }
                    BugPool.getInstance().release(entity);
                    fcc.isCollision = true;

                    spawnBugJuiceBubble(bc);
                }
                if (isOutOfBounds(bc)) {
                    BugPool.getInstance().release(entity);
                    showGameOver();
                }
            }
        } else {
            sasc.paused = true;
            if (isGameOver){
                BugPool.getInstance().release(entity);
            }
        }
    }

    private void spawnBugJuiceBubble(BugComponent bc) {
        CompositeItemVO bugJuiceBubbleC = sceneLoader.loadVoFromLibrary("bug_juice_bubble_lib");

        Entity bugJuiceBubbleE = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), bugJuiceBubbleC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), bugJuiceBubbleE, bugJuiceBubbleC.composite);
        sceneLoader.getEngine().addEntity(bugJuiceBubbleE);

        TransformComponent tc = bugJuiceBubbleE.getComponent(TransformComponent.class);
        bugJuiceBubbleE.add(new BugJuiceBubbleComponent());
        tc.x = bc.boundsRect.getX();
        tc.y = bc.boundsRect.getY();

        EffectUtils.playSplatterParticleEffect(tc.x, tc.y);
        bugJuiceBubbleE.add(fpc);
    }

    private boolean checkFlowerCollision(FlowerPublicComponent fcc, BugComponent bc){
        return fcc.boundsRect.overlaps(bc.boundsRect);
    }

    private void moveEntity(float deltaTime,
                            TransformComponent transformComponent,
                            BugComponent bugComponent,
                            SpriteAnimationStateComponent sasc,
                            SpriteAnimationComponent sac){

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

    private void moveCharger(float deltaTime,
                             TransformComponent tc,
                             BugComponent bc,
                             SpriteAnimationStateComponent sasc,
                             SpriteAnimationComponent sac) {
        bc.counter--;
        // Move
        tc.x += bc.velocity;

        // Idle
        if (bc.state.equals(IDLE)) {
            setAnimation("Idle", Animation.PlayMode.LOOP, sasc, sac);
            bc.velocity = deltaTime * IDLE_MVMNT_SPEED;
            if (bc.counter == 0) {
                canPlayAnimation = true;
                setAnimation("Preparing", Animation.PlayMode.LOOP, sasc, sac);
                bc.counter = PREPARATION_TIME;
                bc.state = PREPARING;
            }
        }
        // Preparing
        else if (bc.state.equals(PREPARING)) {
            bc.velocity = deltaTime * PREPARING_MVMNT_SPEED;
            if (bc.counter == 0) {
                bc.state = CHARGING;
                canPlayAnimation = true;
                setAnimation("Charging", Animation.PlayMode.LOOP, sasc, sac);
                bc.velocity = deltaTime * CHARGING_MVMNT_SPEED;
            }
        }
        // Charging
        else if (CHARGING.equals(bc.state)) {
            bc.velocity += deltaTime * 3.4;
        }
    }

    private void moveSimple(float deltaTime, TransformComponent transformComponent, BugComponent bugComponent) {

        if (!bugComponent.began) {
            begin(bugComponent, transformComponent);
            bugComponent.began = true;
        }
        bugComponent.time += deltaTime;
        bugComponent.complete = bugComponent.time >= bugComponent.duration;
        float percent;
        if (bugComponent.complete) {
            percent = 1;
        } else {
            percent = bugComponent.time / bugComponent.duration;
            if (bugComponent.interpolation != null) percent = bugComponent.interpolation.apply(percent);
        }
        update(bugComponent, transformComponent, bugComponent.reverse ? 1 - percent : percent);
    }

    public void updateRect(BugComponent bc, TransformComponent tc, DimensionsComponent dc) {
        bc.boundsRect.x = (int)tc.x;
        bc.boundsRect.y = (int)tc.y;
        bc.boundsRect.width = (int)dc.width*tc.scaleX;
        bc.boundsRect.height = (int)dc.height*tc.scaleY;
    }

    public void update(BugComponent uc, TransformComponent tc, float percent) {
        float x = uc.startX + (uc.endX - uc.startX) * percent * percent;

        double y =  (Math.sin(x / 100) * 50) + uc.startY;
        setPosition(tc, x, (float)y);
    }

    public boolean isOutOfBounds(BugComponent bc){
        return bc.boundsRect.getX() >= 1200;
    }

    public void setAnimation(String animationName, Animation.PlayMode mode, SpriteAnimationStateComponent sasComponent, SpriteAnimationComponent saComponent){
        if (canPlayAnimation) {
            sasComponent.set(saComponent.frameRangeMap.get(animationName), FPS, mode);
            canPlayAnimation = false;
        }
    }

    protected void begin(BugComponent uc, TransformComponent tc) {
        uc.startX = tc.x;
        uc.startY = tc.y;
    }

    public void setPosition(TransformComponent tc, float x, float y) {
        tc.x = x;
        tc.y = y;
    }
}
