package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.mygdx.game.entity.componets.FlowerComponent;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.stages.GameScreenScript;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import static com.mygdx.game.entity.componets.FlowerComponent.State.*;
import static com.mygdx.game.stages.GameStage.sceneLoader;
import static com.mygdx.game.utils.SoundMgr.soundMgr;

public class FlowerSystem extends IteratingSystem {

    public static int ANIMATION_SPEED = 24;
    private ComponentMapper<FlowerComponent> mapper = ComponentMapper.getFor(FlowerComponent.class);
    private ComponentMapper<FlowerPublicComponent> collisionMapper = ComponentMapper.getFor(FlowerPublicComponent.class);

    public FlowerSystem() {
        super(Family.all(FlowerComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
        DimensionsComponent dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);
        SpriterComponent spriterComponent = ComponentRetriever.get(entity, SpriterComponent.class);
        spriterComponent.scale = 0.6f;
        FlowerComponent flowerComponent = mapper.get(entity);
        FlowerPublicComponent fcc = collisionMapper.get(entity);
        updateRect(flowerComponent, fcc, transformComponent);
        act(fcc, flowerComponent, transformComponent, dimensionsComponent, spriterComponent, deltaTime);
        sceneLoader.renderer.drawDebugRect(fcc.boundsRect.x, fcc.boundsRect.y, fcc.boundsRect.width, fcc.boundsRect.height, entity.toString());
    }

    public void updateRect(FlowerComponent fc, FlowerPublicComponent fcc, TransformComponent tc) {
        fcc.boundsRect.x = (int) tc.x - 40 * tc.scaleX;
        fcc.boundsRect.y = (int) tc.y + 95 * tc.scaleY;
        fcc.boundsRect.width = 150 * tc.scaleX;
        fcc.boundsRect.height = 150 * tc.scaleY;
        if (FlowerComponent.state.equals(IDLE) || FlowerComponent.state.equals(IDLE_BITE)) {
            fcc.boundsRect.x = (int) tc.x - 40 * tc.scaleX;
            fcc.boundsRect.y = (int) tc.y + 25 * tc.scaleY;
        }

//        GameStage.sceneLoader.drawDebugRect(fcc.boundsRect.x,fcc.boundsRect.y,fcc.boundsRect.width,fcc.boundsRect.height);
    }

    public void act(FlowerPublicComponent fcc, FlowerComponent fc, TransformComponent tc, DimensionsComponent dc, SpriterComponent sc, float delta) {
        if (!GameScreenScript.isPause && !GameScreenScript.isGameOver) {
//            sc.player.speed = ANIMATION_SPEED;

            if (fc.state == IDLE_BITE) {
                setBiteIdleAnimation(sc);
            }

            if (FlowerComponent.state == IDLE) {
                if (fcc.isCollision) {
                    FlowerComponent.state = IDLE_BITE;
                    fcc.isCollision = false;

                    soundMgr.play("eat");
                } else {
                    setIdleAnimation(sc);
                }
            }
            if (Gdx.input.justTouched() && FlowerComponent.state == IDLE) {
                FlowerComponent.state = TRANSITION;
            }

            if (Gdx.input.justTouched()
                    && FlowerComponent.state != TRANSITION
                    && FlowerComponent.state != ATTACK) {
                FlowerComponent.state = ATTACK;
                setAttackAnimation(sc);
            }

            if (FlowerComponent.state == TRANSITION) {
                setTransitionAnimation(sc);
                if (isAnimationFinished(sc)) {
                    setAttackAnimation(sc);
                    FlowerComponent.state = ATTACK;
                }
            }

            if (FlowerComponent.state == TRANSITION_BACK) {
                setTransitionBackAnimation(sc);

                if (Gdx.input.justTouched()) {  // This is added for quick breaking of an animation
                    setAttackAnimation(sc);
                    FlowerComponent.state = ATTACK;
                }

                if (sc.player.getTime() > 20 && sc.player.getTime() < 62) {
                    setIdleAnimation(sc);
                    FlowerComponent.state = IDLE;
                }
            }

            if (FlowerComponent.state == ATTACK || FlowerComponent.state == RETREAT) {
                if (fcc.isCollision) {
                    FlowerComponent.state = ATTACK_BITE;
                    setBiteAttackAnimation(sc);
                    fcc.isCollision = false;

                    soundMgr.play("eat");
                } else {
                    move(tc, fc);
                    if (tc.y >= 660 && FlowerComponent.state == ATTACK) {
                        FlowerComponent.state = RETREAT;
                    }
                    if (tc.y <= 106 && FlowerComponent.state == RETREAT) {
                        sc.player.setTime(sc.player.getAnimation().length);
                        FlowerComponent.state = TRANSITION_BACK;
                    }
                }
            }

            if (FlowerComponent.state == ATTACK_BITE || FlowerComponent.state == IDLE_BITE) {

                if (FlowerComponent.state == ATTACK_BITE) {
                    if (isAnimationFinished(sc)) {
                        FlowerComponent.state = RETREAT;
                        setAttackAnimation(sc);
                    }

                    if (fcc.isCollision && canInterruptAttackBite(sc)) {
                        FlowerComponent.state = ATTACK_BITE;
                        setBiteAttackAnimation(sc);
                    }
                }

                if (FlowerComponent.state == IDLE_BITE && isAnimationFinished(sc)) {
                    FlowerComponent.state = IDLE;
                    setIdleAnimation(sc);
                }
            }

            if (FlowerComponent.state.equals(PHOENIX)) {
                tc.x = 988;
                tc.y = 105;
                setBiteIdleAnimation(sc);
                if (isAnimationFinished(sc)) {
                    FlowerComponent.state = IDLE;
                    setIdleAnimation(sc);
                }
            }
        } else {
            sc.player.speed = 0;
        }
    }

    public boolean isAnimationFinished(SpriterComponent sc) {
        return sc.player.getTime() >= sc.player.getAnimation().length - 20;
    }

    public boolean canInterruptAttackBite(SpriterComponent sc) {
        return sc.player.getTime() >= sc.player.getAnimation().length / 3;
    }

    private void setIdleAnimation(SpriterComponent sc) {
        sc.player.speed = ANIMATION_SPEED;
        sc.player.setAnimation(0);
    }

    private void setBiteIdleAnimation(SpriterComponent sc) {
        sc.player.speed = ANIMATION_SPEED;
        sc.player.setAnimation(1);
    }

    private void setTransitionAnimation(SpriterComponent sc) {
        sc.player.speed = 40; //40
        sc.player.setAnimation(2);
    }

    private void setTransitionBackAnimation(SpriterComponent sc) {
        sc.player.speed = -40;
        sc.player.setAnimation(2);
    }

    private void setAttackAnimation(SpriterComponent sc) {
        sc.player.speed = ANIMATION_SPEED;
        sc.player.setAnimation(3);
    }

    private void setBiteAttackAnimation(SpriterComponent sc) {
        sc.player.speed = ANIMATION_SPEED;
        sc.player.setAnimation(4);
    }

    public void move(TransformComponent tc, FlowerComponent fc) {
        tc.y += FlowerComponent.state == ATTACK ? FlowerComponent.FLOWER_MOVE_SPEED : -FlowerComponent.FLOWER_MOVE_SPEED;
    }
}
