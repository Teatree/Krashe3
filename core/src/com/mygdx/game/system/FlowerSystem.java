package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.mygdx.game.entity.componets.FlowerComponent;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.entity.componets.Upgrade;
import com.mygdx.game.stages.GameScreenScript;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.EffectUtils;
import com.mygdx.game.utils.SoundMgr;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import static com.mygdx.game.entity.componets.FlowerComponent.*;
import static com.mygdx.game.entity.componets.FlowerComponent.State.*;
import static com.mygdx.game.stages.GameStage.sceneLoader;
import static com.mygdx.game.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.mygdx.game.utils.GlobalConstants.FPS;
import static com.mygdx.game.utils.SoundMgr.soundMgr;

public class FlowerSystem extends IteratingSystem {

    private ComponentMapper<FlowerComponent> mapper = ComponentMapper.getFor(FlowerComponent.class);
    private ComponentMapper<FlowerPublicComponent> collisionMapper = ComponentMapper.getFor(FlowerPublicComponent.class);

    public FlowerSystem() {
        super(Family.all(FlowerComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
        SpriterComponent spriterComponent = ComponentRetriever.get(entity, SpriterComponent.class);
        spriterComponent.scale = 0.6f;
        FlowerPublicComponent fcc = collisionMapper.get(entity);
        updateRect(fcc, transformComponent);
        act(fcc, transformComponent, spriterComponent, deltaTime);
        sceneLoader.renderer.drawDebugRect(fcc.boundsRect.x, fcc.boundsRect.y, fcc.boundsRect.width, fcc.boundsRect.height, entity.toString());
    }

    public void updateRect(FlowerPublicComponent fcc, TransformComponent tc) {
        fcc.boundsRect.x = (int) tc.x - 40 * tc.scaleX;
        fcc.boundsRect.y = (int) tc.y + 95 * tc.scaleY;
        fcc.boundsRect.width = 150 * tc.scaleX;
        fcc.boundsRect.height = 150 * tc.scaleY;
        if (state.equals(IDLE) || state.equals(IDLE_BITE)) {
            fcc.boundsRect.x = (int) tc.x - 40 * tc.scaleX;
            fcc.boundsRect.y = (int) tc.y + 25 * tc.scaleY;
        }
//        GameStage.sceneLoader.drawDebugRect(fcc.boundsRect.x,fcc.boundsRect.y,fcc.boundsRect.width,fcc.boundsRect.height);
    }

    public void act(FlowerPublicComponent fcc, TransformComponent tc, SpriterComponent sc, float delta) {
        if (!GameScreenScript.isPause && !GameScreenScript.isGameOver) {
            sc.player.speed = FPS;

            if (state.equals(IDLE_BITE)) {
                setBiteIdleAnimation(sc);
            }

            if (state.equals(IDLE)) {
                if (fcc.isCollision) {
                    state = IDLE_BITE;
                    fcc.isCollision = false;

                    soundMgr.play(SoundMgr.EAT_SOUND);
                } else {
                    setIdleAnimation(sc);
                }
            }
            if (Gdx.input.isTouched() && state.equals(IDLE) && canAttackCoord(fcc)) {
                state = TRANSITION;
            }

            if (Gdx.input.isTouched()
                    && !state.equals(TRANSITION)
                    && !state.equals(ATTACK) && canAttackCoord(fcc)) {
                state = ATTACK;

                setAttackAnimation(sc);
            }

            if (state.equals(TRANSITION)) {
                setTransitionAnimation(sc);
                if (isAnimationFinished(sc)) {
                    setAttackAnimation(sc);
                    state = ATTACK;
                }
            }

            if (state.equals(TRANSITION_BACK)) {
                setTransitionBackAnimation(sc);

                if (Gdx.input.justTouched() && canAttackCoord(fcc)) {  // This is added for quick breaking of an animation
                    setAttackAnimation(sc);
                    state = ATTACK;
                }

                if (sc.player.getTime() > 20 && sc.player.getTime() < 62) {
                    setIdleAnimation(sc);
                    state = IDLE;
                }
            }

            if (state.equals(ATTACK) || state.equals(RETREAT)) {

                if (fcc.isCollision) {
                    state = ATTACK_BITE;
                    setBiteAttackAnimation(sc);
                    fcc.isCollision = false;

                    soundMgr.play(SoundMgr.EAT_SOUND);
                } else {
                    move(tc, delta);
                    if (tc.y >= FLOWER_MAX_Y_POS && state.equals(ATTACK)) {
                        state = RETREAT;
                        hideTutorialLine();
                    }
                    if (tc.y <= FLOWER_Y_POS + FLOWER_MOVE_SPEED * delta * FPS && state.equals(RETREAT)) {
                        tc.y = FLOWER_Y_POS;
                        sc.player.setTime(sc.player.getAnimation().length);
                        state = TRANSITION_BACK;
                    }
                }
            }

            if (state.equals(ATTACK_BITE) || state.equals(IDLE_BITE)) {

                if (state.equals(ATTACK_BITE)) {
                    if (isAnimationFinished(sc)) {
                        state = RETREAT;
                        setAttackAnimation(sc);
                    }

                    if (fcc.isCollision && canInterruptAttackBite(sc)) {
                        state = ATTACK_BITE;
                        setBiteAttackAnimation(sc);
                    }
                }

                if (state.equals(IDLE_BITE) && isAnimationFinished(sc)) {
                    state = IDLE;
                    setIdleAnimation(sc);
                }
            }

            if (state.equals(PHOENIX)) {
                tc.x = FLOWER_X_POS;
                tc.y = FLOWER_Y_POS;
                setBiteIdleAnimation(sc);
                if (isAnimationFinished(sc)) {
                    state = IDLE;
                    setIdleAnimation(sc);
                    Upgrade.blowUpAllBugs = false;
                }
            }
        } else {
            sc.player.speed = 0;
        }
    }

    private boolean canAttackCoord(FlowerPublicComponent fcc) {
        return (fcc.currentPet == null || !fcc.currentPet.boundsRect.contains(EffectUtils.getTouchCoordinates()))
                && !GameStage.gameScript.pauseBtn.getComponent(DimensionsComponent.class).boundBox.contains(EffectUtils.getTouchCoordinates());
    }

    private void hideTutorialLine() {
        if(GameStage.gameScript.gameItem.getChild("tutorial_line").getEntity().getComponent(TintComponent.class).color.a > 0.3f){
            GameStage.gameScript.gameItem.getChild("tutorial_line").getEntity().getComponent(TintComponent.class).color.a -= 0.3f;
        }else{
            GameStage.gameScript.gameItem.getChild("tutorial_line").getEntity().getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        }
    }

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

    public void move(TransformComponent tc, float delta) {
        tc.y += state.equals(ATTACK) ? FLOWER_MOVE_SPEED * delta * FPS : -FLOWER_MOVE_SPEED * delta * FPS;
    }
}
