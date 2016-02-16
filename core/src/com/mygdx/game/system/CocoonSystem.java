package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.entity.componets.ButterflyComponent;
import com.mygdx.game.entity.componets.CocoonComponent;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.stages.GameScreenScript;
import com.mygdx.game.utils.GlobalConstants;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.game.entity.componets.CocoonComponent.State.*;
import static com.mygdx.game.stages.GameStage.sceneLoader;

public class CocoonSystem extends IteratingSystem {

    FlowerPublicComponent fcc;
    Entity butterflyEntity;
    private ComponentMapper<CocoonComponent> mapper = ComponentMapper.getFor(CocoonComponent.class);
    private ComponentMapper<FlowerPublicComponent> collisionMapper = ComponentMapper.getFor(FlowerPublicComponent.class);
    private SpriterComponent sc = new SpriterComponent();

    public CocoonSystem(SceneLoader sl) {
        super(Family.all(CocoonComponent.class).get());
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        butterflyEntity = root.getChild("butterfly").getEntity();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        sc = entity.getComponent(SpriterComponent.class);
        if (!GameScreenScript.isPause && !GameScreenScript.isGameOver) {
            CocoonComponent cc = mapper.get(entity);
            DimensionsComponent dc = ComponentRetriever.get(entity, DimensionsComponent.class);
            TransformComponent tc = ComponentRetriever.get(entity, TransformComponent.class);
            fcc = collisionMapper.get(entity);

            updateRect(cc, tc, dc);
            act(cc, entity, deltaTime);

            if (checkCollision(cc, fcc) && !cc.canHit) {
                hit(cc);
            }

        }
    }

    public void act(CocoonComponent cc, Entity entity, float delta) {

        if ("GAME".equals(GlobalConstants.CUR_SCREEN)) {

            if (cc.state.equals(SPAWNING)) {

                if (isAnimationFinished()) {
                    cc.state = IDLE;
                    sc.player.setAnimation(1);
                    sc.player.speed = 0;
                }
            }

            if (cc.state == HIT) {
                if (isAnimationFinished()) {
                    if (cc.hitCounter >= GlobalConstants.COCOON_HIT_AMOUNT) {
                        cc.state = DEAD;
                        spawnButterfly();
                    } else {
                        cc.state = IDLE;
                        if(cc.hitCounter+1<4) {
                            sc.player.setAnimation(cc.hitCounter + 1);
                        }
                        sc.player.speed = 0;
                    }
                }
            }

            if (cc.state == DEAD) {
//                GameStage.sceneLoader.getEngine().removeEntity(entity);
                sc.player.setAnimation(3);
                if (isAnimationFinished()) {
                    entity.getComponent(TransformComponent.class).y = -500;
                }
            }
        }
    }

    public boolean isAnimationFinished() {
        return sc.player.getTime() >= sc.player.getAnimation().length - 20;
    }

    public void hit(CocoonComponent cc) {

        cc.canHit = true;
        if (cc.state != DEAD) {
            cc.state = HIT;
//            cc.hitCounter+=1;
            System.out.println("hit counter: " + cc.hitCounter);
            cc.canHit = true;
            sc.player.speed = 24;
            sc.player.setAnimation(cc.hitCounter++);
        }
    }

    public void updateRect(CocoonComponent cc, TransformComponent tc, DimensionsComponent dc) {
        cc.boundsRect.x = (int) tc.x;
        cc.boundsRect.y = 793;
        cc.boundsRect.width = (int) dc.width * tc.scaleX;
        cc.boundsRect.height = (int) dc.height * tc.scaleY;
//        sceneLoader.renderer.drawDebug(cc.boundsRect.x,cc.boundsRect.y,cc.boundsRect.width,cc.boundsRect.height);
    }

    private boolean checkCollision(CocoonComponent cc, FlowerPublicComponent fcc) {
        if (!cc.boundsRect.overlaps(fcc.boundsRect)){
            cc.canHit = false;
        }
        return cc.boundsRect.overlaps(fcc.boundsRect);
    }

    private void spawnButterfly() {

        TransformComponent tc = butterflyEntity.getComponent(TransformComponent.class);
        tc.x = 700;
        tc.y = 750;

        ButterflyComponent bc = new ButterflyComponent();
        butterflyEntity.add(fcc);
        butterflyEntity.add(bc);
    }
}
