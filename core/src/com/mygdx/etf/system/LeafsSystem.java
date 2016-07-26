package com.mygdx.etf.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.mygdx.etf.entity.componets.FlowerComponent;
import com.mygdx.etf.entity.componets.LeafsComponent;
import com.mygdx.etf.stages.GameScreenScript;
import com.mygdx.etf.utils.EffectUtils;
import com.mygdx.etf.utils.SoundMgr;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import static com.mygdx.etf.entity.componets.LeafsComponent.*;
import static com.mygdx.etf.entity.componets.LeafsComponent.State.*;
import static com.mygdx.etf.stages.GameStage.gameScript;
import static com.mygdx.etf.stages.GameStage.sceneLoader;
import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.mygdx.etf.utils.GlobalConstants.FPS;
import static com.mygdx.etf.utils.SoundMgr.soundMgr;

public class LeafsSystem extends IteratingSystem {

    public LeafsSystem() {
        super(Family.all(LeafsComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);

        SpriterComponent spriterComponentLeafs = ComponentRetriever.get(entity, SpriterComponent.class);
        spriterComponentLeafs.scale = LEAFS_SCALE;

        updateRect(transformComponent);
        act(transformComponent, spriterComponentLeafs, deltaTime);
//        sceneLoader.renderer.drawDebugRect(gameScript.fpc.boundsRect.x, gameScript.fpc.boundsRect.y, gameScript.fpc.boundsRect.width, gameScript.fpc.boundsRect.height, entity.toString());
    }

    public void updateRect(TransformComponent tc) {
        gameScript.fpc.boundsRect.x = (int) tc.x - 60 * tc.scaleX;
        gameScript.fpc.boundsRect.y = (int) tc.y + 140 * tc.scaleY;
        gameScript.fpc.boundsRect.width = 200 * tc.scaleX;
        gameScript.fpc.boundsRect.height = 200 * tc.scaleY;
        if (state.equals(IDLE) || state.equals(IDLE_BITE)) {
            gameScript.fpc.boundsRect.x = (int) tc.x - 40 * tc.scaleX;
            gameScript.fpc.boundsRect.y = (int) tc.y + 25 * tc.scaleY;
        }
//        GameStage.sceneLoader.drawDebugRect(gameScript.fpc.boundsRect.x,gameScript.fpc.boundsRect.y,gameScript.fpc.boundsRect.width,gameScript.fpc.boundsRect.height);
    }

    public void act(TransformComponent tc, SpriterComponent sc, float delta) {
        if (!GameScreenScript.isPause && !GameScreenScript.isGameOver) {
            sc.player.speed = FPS;

//            if (state.equals(IDLE_BITE)) {
//                setBiteIdleAnimation(sc);
//            }
//
//            if (state.equals(IDLE)) {
//                if (gameScript.fpc.isCollision) {
//                    state = IDLE_BITE;
//                    gameScript.fpc.isCollision = false;
//
//                    soundMgr.play(SoundMgr.EAT_SOUND);
//                } else {
//                    setIdleAnimation(sc);
//                }
//            }
//            if (Gdx.input.isTouched() && state.equals(IDLE) &&
//                    canAttackCoord()) {
//                state = TRANSITION;
//            }
//
//            if (Gdx.input.isTouched()
//                    && !state.equals(TRANSITION)
//                    && !state.equals(ATTACK) && canAttackCoord()) {
//                state = ATTACK;
//
//                setAttackAnimation(sc);
//            }
//
//            if (state.equals(TRANSITION)) {
//                setTransitionAnimation(sc);
//                if (isAnimationFinished(sc)) {
//                    setAttackAnimation(sc);
//                    state = ATTACK;
//                }
//            }
//
//            if (state.equals(TRANSITION_BACK)) {
//                setTransitionBackAnimation(sc);
//
//                if (Gdx.input.justTouched() && canAttackCoord()) {  // This is added for quick breaking of an animation
//                    setAttackAnimation(sc);
//                    state = ATTACK;
//                }
//
//                if (sc.player.getTime() > 20 && sc.player.getTime() < 62) {
//                    setIdleAnimation(sc);
//                    state = IDLE;
//                }
//            }
//
//            if (state.equals(ATTACK) || state.equals(RETREAT)) {
//
//                if (gameScript.fpc.isCollision) {
//                    state = ATTACK_BITE;
//                    setBiteAttackAnimation(sc);
//                    gameScript.fpc.isCollision = false;
//
//                    soundMgr.play(SoundMgr.EAT_SOUND);
//                } else {
//                    move(tc, delta);
//                    if (tc.y >= FLOWER_MAX_Y_POS && state.equals(ATTACK)) {
//                        state = RETREAT;
//                        hideTutorialLine();
//                    }
//                    if (tc.y <= FLOWER_Y_POS + FLOWER_MOVE_SPEED * delta * FPS && state.equals(RETREAT)) {
//                        tc.y = FLOWER_Y_POS;
//                        sc.player.setTime(sc.player.getAnimation().length);
//                        state = TRANSITION_BACK;
//                    }
//                }
//            }
//
//            if (state.equals(ATTACK_BITE) || state.equals(IDLE_BITE)) {
//
//                if (state.equals(ATTACK_BITE)) {
//                    if (isAnimationFinished(sc)) {
//                        state = RETREAT;
//                        setAttackAnimation(sc);
//                    }
//
//                    if (gameScript.fpc.isCollision && canInterruptAttackBite(sc)) {
//                        state = ATTACK_BITE;
//                        setBiteAttackAnimation(sc);
//                    }
//                }
//
//                if (state.equals(IDLE_BITE) && isAnimationFinished(sc)) {
//                    state = IDLE;
//                    setIdleAnimation(sc);
//                }
//            }
//
//            if (state.equals(PHOENIX)) {
//                tc.x = FLOWER_X_POS;
//                tc.y = FLOWER_Y_POS;
//                setBiteIdleAnimation(sc);
//                if (isAnimationFinished(sc)) {
//                    state = IDLE;
//                    setIdleAnimation(sc);
//                    BugSystem.blowUpAllBugs = false;
//                }
//            }
        } else {
            sc.player.speed = 0;
        }
    }

    //:TODO NPE
    private boolean canAttackCoord() {
        return (gameScript.fpc.currentPet == null || !gameScript.fpc.currentPet.enabled ||
                !gameScript.fpc.currentPet.boundsRect.contains(EffectUtils.getTouchCoordinates()))
                && !gameScript.pauseBtn.getComponent(DimensionsComponent.class).boundBox.contains(EffectUtils.getTouchCoordinates());
    }

//    private void hideTutorialLine() {
//        if (gameScript.gameItem.getChild(TUTORIAL_LINE).getEntity().getComponent(TintComponent.class).color.a > 0.3f) {
//            gameScript.gameItem.getChild(TUTORIAL_LINE).getEntity().getComponent(TintComponent.class).color.a -= 0.3f;
//        } else {
//            gameScript.gameItem.getChild(TUTORIAL_LINE).getEntity().getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
//        }
//    }

    public boolean isAnimationFinished(SpriterComponent sc) {
        return sc.player.getTime() >= sc.player.getAnimation().length - 20;
    }

    public boolean canInterruptAttackBite(SpriterComponent sc) {
        return sc.player.getTime() >= sc.player.getAnimation().length / 3;
    }

    private void setIdleAnimation(SpriterComponent sc) {
        sc.player.speed = FPS;
        sc.player.setAnimation(0);
    }

    private void setBiteIdleAnimation(SpriterComponent sc) {
        sc.player.speed = FPS;
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
        sc.player.speed = FPS;
        sc.player.setAnimation(3);
    }

    private void setBiteAttackAnimation(SpriterComponent sc) {
        sc.player.speed = FPS;
        sc.player.setAnimation(4);
    }

//    public void move(TransformComponent tc, float delta) {
//        tc.y += state.equals(ATTACK) ? FLOWER_MOVE_SPEED * delta * FPS : -FLOWER_MOVE_SPEED * delta * FPS;
//    }
}