package com.fd.etf.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.fd.etf.entity.componets.FlowerComponent;
import com.fd.etf.entity.componets.FlowerPublicComponent;
import com.fd.etf.stages.GameStage;
import com.fd.etf.utils.EffectUtils;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;

import static com.fd.etf.entity.componets.FlowerComponent.*;
import static com.fd.etf.entity.componets.FlowerComponent.State.*;
import static com.fd.etf.stages.GameScreenScript.isGameOver;
import static com.fd.etf.stages.GameScreenScript.isPause;
import static com.fd.etf.utils.GlobalConstants.FPS;

public class FlowerSystem extends IteratingSystem {

    private static final float FLOWER_MOVE_SPEED = 47.5f;
    private static final int FLOWER_MAX_Y_POS = 640;

    private static final String TUTORIAL_LINE = "tutorial_line";
    private int randomSulkSwtich;

    private GameStage gameStage;

    public FlowerSystem(GameStage gameStage) {
        super(Family.all(FlowerComponent.class).get());
        this.gameStage = gameStage;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        entity.getComponent(SpriterComponent.class).scale = FLOWER_SCALE;

        updateRect(entity.getComponent(TransformComponent.class));
        act(entity.getComponent(TransformComponent.class), entity.getComponent(SpriterComponent.class), deltaTime);
    }

    public void updateRect(TransformComponent tc) {
        gameStage.gameScript.fpc.boundsRect.x = (int) tc.x - 100 * tc.scaleX;
        gameStage.gameScript.fpc.boundsRect.y = (int) tc.y + 130 * tc.scaleY;
        gameStage.gameScript.fpc.boundsRect.width = 190 * tc.scaleX;
        gameStage.gameScript.fpc.boundsRect.height = 150 * tc.scaleY;
        if (state.equals(IDLE) || state.equals(IDLE_BITE)) {
            gameStage.gameScript.fpc.boundsRect.x = (int) tc.x - 60 * tc.scaleX;
            gameStage.gameScript.fpc.boundsRect.y = (int) tc.y - 25 * tc.scaleY;
        }
//        gameStage.sceneLoader.renderer.drawDebugRect(gameStage.gameScript.fpc.boundsRect.x, gameStage.gameScript.fpc.boundsRect.y, gameStage.gameScript.fpc.boundsRect.width, gameStage.gameScript.fpc.boundsRect.height, "");
//        gameStage.sceneLoader.renderer.drawDebugRect(bc.boundsRectScary.x, bc.boundsRectScary.y,
//                bc.boundsRectScary.width, bc.boundsRectScary.height, entity.toString());
    }

    public void act(TransformComponent tc, SpriterComponent sc, float delta) {
        FlowerPublicComponent.state = state;

//        if(FlowerComponent.isLosing){
//            sd
//        }

        if(BugJuiceBubbleSystem.isCalculatingScore) {
            if (FlowerPublicComponent.oldScore <= (float) gameStage.gameScript.fpc.score) {
                FlowerPublicComponent.oldScore += FlowerPublicComponent.scoreDiff;

                gameStage.gameScript.scoreLabelE.getComponent(LabelComponent.class).text.replace(0,
                        gameStage.gameScript.scoreLabelE.getComponent(LabelComponent.class).text.capacity(), "" +
                                "" + (int) FlowerPublicComponent.oldScore);
                gameStage.gameScript.scoreLabelEsh.getComponent(LabelComponent.class).text.replace(0,
                        gameStage.gameScript.scoreLabelEsh.getComponent(LabelComponent.class).text.capacity(), "" +
                                "" + (int) FlowerPublicComponent.oldScore);

            }
            if (FlowerPublicComponent.oldScore >= gameStage.gameScript.fpc.score) {
                BugJuiceBubbleSystem.isCalculatingScore = false;
                FlowerPublicComponent.scoreDiff = 0;
            }
        }

        if (!isPause.get() && !isGameOver.get()) {
            sc.player.speed = FPS;

            idle(sc);

            ifIsTouched(tc, sc);

            transition(sc);

            transitionBack(sc);

            attackAndRetreat(tc, sc, delta);

            bite(sc);

            usePhoenix(tc, sc);

            reviveAds(tc, sc);
        } else if (isPause.get() && isGameOver.get()) {
            sc.player.speed = FPS;

            sulk(sc);
        } else {
            sc.player.speed = 0;
        }

        if (state.equals(State.LOSING)) {
            if (tc.y > FLOWER_Y_POS) {
                tc.y -= FLOWER_MOVE_SPEED * delta * FPS;
                isPause.set(true);
            } else {
                isPause.set(true);
                setLoseAnimation(sc);
                if (isAnimationFinished(sc)) {
                    setSulkAnimation(sc);
                    state = State.SULKING;
                    FlowerComponent.isLosing = false;
                    if (gameStage.gameScript.fpc.canUsePhoenix()) {
                        gameStage.gameScript.usePhoenix();
                    } else {
                        isGameOver.set(true);
                        gameStage.gameScript.endGame();
                    }
                }
            }
        }
    }

    private void sulk(SpriterComponent sc) {
        if (state.equals(SULKING)) {
            randomSulkSwtich++;
            if (isAnimationFinished(sc) && randomSulkSwtich > 150) {
                setSulkAnimation(sc);
            } else if (isAnimationFinished(sc) && randomSulkSwtich < 150) {
                setSulkIdleAnimation(sc);
            }
        }
    }

    private void attackAndRetreat(TransformComponent tc, SpriterComponent sc, float delta) {
        if (state.equals(ATTACK) || state.equals(RETREAT)) {

            if (gameStage.gameScript.fpc.isCollision) {
                state = ATTACK_BITE;
                setBiteAttackAnimation(sc);
                gameStage.gameScript.fpc.isCollision = false;

            } else {
                if (tc.y < FLOWER_MAX_Y_POS || !Gdx.input.isTouched()) {
                    move(tc, delta);
                }
                if (tc.y >= FLOWER_MAX_Y_POS && state.equals(ATTACK) && !Gdx.input.isTouched()) {
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
    }

    private void idle(SpriterComponent sc) {
        if (state.equals(IDLE_BITE)) {
            setBiteIdleAnimation(sc);
        }

        if (state.equals(IDLE)) {
            if (gameStage.gameScript.fpc.isCollision) {
                state = IDLE_BITE;
                gameStage.gameScript.fpc.isCollision = false;

//                soundMgr.play(SoundMgr.EAT_SOUND);

            } else {
                setIdleAnimation(sc);
            }
        }
    }

    private void transition(SpriterComponent sc) {
        if (state.equals(TRANSITION)) {
            setTransitionAnimation(sc);
            if (isAnimationFinished(sc)) {
                setAttackAnimation(sc);
                state = ATTACK;
            }
        }
    }

    private void ifIsTouched(TransformComponent tc, SpriterComponent sc) {
        if (!FlowerComponent.isLosing && Gdx.input.isTouched() && state.equals(IDLE) && canAttackCoord()) {
            state = TRANSITION;
        }

        if (!FlowerComponent.isLosing && Gdx.input.isTouched()
                && !state.equals(TRANSITION)
                && !state.equals(ATTACK) && canAttackCoord()) {
            state = ATTACK;

            setAttackAnimation(sc);
        }
    }

    private void usePhoenix(TransformComponent tc, SpriterComponent sc) {
        if (state.equals(PHOENIX)) {
            tc.x = FLOWER_X_POS;
            tc.y = FLOWER_Y_POS;
            setReviveAnimation(sc);
            if (isAnimationFinished(sc)) {
                state = IDLE;
                setIdleAnimation(sc);
//                BugSystem.blowUpAllBugs = false;
//                System.out.println("USEPHOENIX(): " + BugSystem.blowUpAllBugs);
            }
        }
    }

    private void reviveAds(TransformComponent tc, SpriterComponent sc) {
        if (state.equals(REVIVE_ADS)) {
            tc.x = FLOWER_X_POS;
            tc.y = FLOWER_Y_POS;
            setReviveAnimation(sc);
            if (isAnimationFinished(sc)) {
                state = IDLE;
                setIdleAnimation(sc);
                BugSystem.blowUpAllBugs = false;
//                System.out.println("reviveAds(): " + BugSystem.blowUpAllBugs);
            }
        }
    }

    private void bite(SpriterComponent sc) {
        if (state.equals(ATTACK_BITE) || state.equals(IDLE_BITE)) {

            if (state.equals(ATTACK_BITE)) {
                if (isAnimationFinished(sc)) {
                    state = RETREAT;
                    setAttackAnimation(sc);
                }

                if (gameStage.gameScript.fpc.isCollision && canInterruptAttackBite(sc)) {
                    state = ATTACK_BITE;
                    setBiteAttackAnimation(sc);
                }
            }

            if (state.equals(IDLE_BITE) && isAnimationFinished(sc)) {
                state = IDLE;
                setIdleAnimation(sc);
            }
        }
    }

    private void transitionBack(SpriterComponent sc) {
        if (state.equals(TRANSITION_BACK)) {
            setTransitionBackAnimation(sc);

            if (Gdx.input.justTouched() && canAttackCoord()) {  // This is added for quick breaking of an animation
                setAttackAnimation(sc);
                state = ATTACK;
            }

            if (sc.player.getTime() > 20 && sc.player.getTime() < 62) {
                setIdleAnimation(sc);
                state = IDLE;
            }
        }
    }

    private boolean canAttackCoord() {
        return (
                FlowerPublicComponent.currentPet == null || !FlowerPublicComponent.currentPet.enabled ||
                        !FlowerPublicComponent.currentPet.boundsRect.contains(EffectUtils.getTouchCoordinates()))
                && !gameStage.gameScript.pauseBtn.getComponent(DimensionsComponent.class).boundBox.contains(EffectUtils.getTouchCoordinates()
        );
    }

    private void hideTutorialLine() {
        if (gameStage.gameScript.gameItem.getChild(TUTORIAL_LINE).getEntity().getComponent(TintComponent.class).color.a > 0.1f) {
            gameStage.gameScript.gameItem.getChild(TUTORIAL_LINE).getEntity().getComponent(TintComponent.class).color.a -= 0.25f;
        }else{
            gameStage.gameScript.gameItem.getChild(TUTORIAL_LINE).getEntity().getComponent(TintComponent.class).color.a = 0;
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

    private void setLoseAnimation(SpriterComponent sc) {
        sc.player.speed = FPS;
        sc.player.setAnimation(5);
    }

    private void setSulkIdleAnimation(SpriterComponent sc) {
        sc.player.speed = FPS;
        sc.player.setAnimation(6);
    }

    private void setSulkAnimation(SpriterComponent sc) {
        sc.player.speed = FPS;
        sc.player.setAnimation(7);
    }

    private void setReviveAnimation(SpriterComponent sc) {
        sc.player.speed = FPS;
        sc.player.setAnimation(8);
    }

    public void move(TransformComponent tc, float delta) {
        tc.y += state.equals(ATTACK) ? FLOWER_MOVE_SPEED * delta * FPS : -FLOWER_MOVE_SPEED * delta * FPS;
    }
}
