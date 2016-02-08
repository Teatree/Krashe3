package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.entity.componets.ButterflyComponent;
import com.mygdx.game.entity.componets.CocoonComponent;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.stages.GameScreenScript;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.GlobalConstants;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.game.entity.componets.CocoonComponent.State.*;
import static com.mygdx.game.stages.GameStage.sceneLoader;

public class CocoonSystem extends IteratingSystem {

    private ComponentMapper<CocoonComponent> mapper = ComponentMapper.getFor(CocoonComponent.class);
    private ComponentMapper<FlowerPublicComponent> collisionMapper = ComponentMapper.getFor(FlowerPublicComponent.class);
    FlowerPublicComponent fcc;
    Entity butterflyEntity;

    public CocoonSystem(SceneLoader sl) {
        super(Family.all(CocoonComponent.class).get());
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        butterflyEntity = root.getChild("butterflyAni8").getEntity();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (!GameScreenScript.isPause && !GameScreenScript.isGameOver) {
            CocoonComponent cc = mapper.get(entity);
            DimensionsComponent dc = ComponentRetriever.get(entity, DimensionsComponent.class);
            TransformComponent tc = ComponentRetriever.get(entity, TransformComponent.class);
            fcc = collisionMapper.get(entity);

            updateRect(cc, tc, dc);
            act(cc, entity, deltaTime);

            if (checkCollision(cc, fcc)) {
                hit(cc);
            }
        }
    }

    public void act(CocoonComponent cc, Entity entity, float delta) {

        if ("GAME".equals(GlobalConstants.CUR_SCREEN)) {

            if (cc.state == SPAWNING) {
                if (cc.counter >= GlobalConstants.COCOON_SPAWNING_DURATION) {
                    cc.state = IDLE;
                    cc.counter = 0;
                }
            }

            if (cc.state == HIT) {
                cc.counter++;
                if (cc.counter >= GlobalConstants.COCOON_HIT_DURATION) {
                    if (cc.health <= 0) {
                        cc.state = DEAD;
                        spawnButterfly();
                        cc.counter = 0;
                    } else {
                        cc.state = IDLE;
                        cc.counter = 0;
                    }
                }
            }

            if (cc.state == DEAD) {
//                GameStage.sceneLoader.getEngine().removeEntity(entity);
                entity.getComponent(TransformComponent.class).y = -500;
            }
        }
    }

    public void hit(CocoonComponent cc) {
        cc.isCollision = false;
        if (cc.state != DEAD) {
            cc.health--;
            cc.state = HIT;
            cc.counter = 0;
        }
    }

    public void updateRect(CocoonComponent cc, TransformComponent tc, DimensionsComponent dc) {
        cc.boundsRect.x = (int) tc.x;
        cc.boundsRect.y = 793;
        cc.boundsRect.width = (int) dc.width * tc.scaleX;
        cc.boundsRect.height = (int) dc.height * tc.scaleY;
    }

    private boolean checkCollision(CocoonComponent cc, FlowerPublicComponent fcc) {
        return cc.boundsRect.overlaps(fcc.boundsRect);
    }

    private void spawnButterfly() {

        TransformComponent tc = butterflyEntity.getComponent(TransformComponent.class);
        tc.x = 750;
        tc.y = 700;

        ButterflyComponent bc = new ButterflyComponent();
        butterflyEntity.add(fcc);
        butterflyEntity.add(bc);
    }
}
