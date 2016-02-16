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
        if (fc.state.equals(IDLE) || fc.state.equals(IDLE_BITE)) {
            fcc.boundsRect.x = (int) tc.x - 40 * tc.scaleX;
            fcc.boundsRect.y = (int) tc.y + 25 * tc.scaleY;
        }

//        GameStage.sceneLoader.drawDebugRect(fcc.boundsRect.x,fcc.boundsRect.y,fcc.boundsRect.width,fcc.boundsRect.height);
    }

    public void act(FlowerPublicComponent fcc, FlowerComponent fc, TransformComponent tc, DimensionsComponent dc, SpriterComponent sc, float delta) {
        if (!GameScreenScript.isPause && !GameScreenScript.isGameOver) {
//            sc.player.speed = ANIMATION_SPEED;

//            if (fc.state == IDLE_BITE) {
//                setBiteIdleAnimation(sc);
//            }

            if (fc.state == IDLE) {
                if (fcc.isCollision) {
                    fc.state = IDLE_BITE;
                    fcc.isCollision = false;

                    soundMgr.play("eat");
                } else {
                    setIdleAnimation(sc);
                }
            }
            if (Gdx.input.justTouched() && fc.state == IDLE) {
                fc.state = TRANSITION;
            }

            if (Gdx.input.justTouched()
                    && fc.state != TRANSITION
                    && fc.state != ATTACK) {
                fc.state = ATTACK;
                setAttackAnimation(sc);
            }

            if (fc.state == TRANSITION) {
                setTransitionAnimation(sc);
                if (isAnimationFinished(sc)) {
                    setAttackAnimation(sc);
                    fc.state = ATTACK;
                }
            }

            if (fc.state == TRANSITION_BACK) {
                setTransitionBackAnimation(sc);

                if (Gdx.input.justTouched()) {  // This is added for quick breaking of an animation
                    setAttackAnimation(sc);
                    fc.state = ATTACK;
                }

                if (sc.player.getTime() > 20 && sc.player.getTime() < 62) {
                    setIdleAnimation(sc);
                    fc.state = IDLE;
                }
            }

            if (fc.state == ATTACK || fc.state == RETREAT) {
                if (fcc.isCollision) {
                    fc.state = ATTACK_BITE;
                    setBiteAttackAnimation(sc);
                    fcc.isCollision = false;

                    soundMgr.play("eat");
                } else {
                    move(tc, fc);
                    if (tc.y >= 660 && fc.state == ATTACK) {
                        fc.state = RETREAT;
                    }
                    if (tc.y <= 106 && fc.state == RETREAT) {
                        sc.player.setTime(sc.player.getAnimation().length);
                        fc.state = TRANSITION_BACK;
                    }
                }
            }

            if (fc.state == ATTACK_BITE || fc.state == IDLE_BITE) {

                if (fc.state == ATTACK_BITE) {
                    if (isAnimationFinished(sc)) {
                        fc.state = RETREAT;
                        setAttackAnimation(sc);
                    }

                    if (fcc.isCollision && canInterruptAttackBite(sc)) {
                        fc.state = ATTACK_BITE;
                        setBiteAttackAnimation(sc);
                    }
                }
                if (fc.state == IDLE_BITE && isAnimationFinished(sc)) {
                    fc.state = IDLE;
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
        tc.y += fc.state == ATTACK ? FlowerComponent.FLOWER_MOVE_SPEED : -FlowerComponent.FLOWER_MOVE_SPEED;
    }
}
