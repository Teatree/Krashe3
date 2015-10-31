package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.entity.componets.BugComponent;
import com.mygdx.game.entity.componets.CacconComponent;
import com.mygdx.game.entity.componets.FlowerCollisionComponent;
import com.mygdx.game.entity.componets.UmbrellaComponent;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.GlobalConstants;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import static com.mygdx.game.entity.componets.CacconComponent.State.*;

/**
 * Created by AnastasiiaRudyk on 31/10/2015.
 */
public class CacoonSystem extends IteratingSystem {

    private ComponentMapper<CacconComponent> mapper = ComponentMapper.getFor(CacconComponent.class);
    private ComponentMapper<FlowerCollisionComponent> collisionMapper = ComponentMapper.getFor(FlowerCollisionComponent.class);
    private SceneLoader sl;

    public CacoonSystem(SceneLoader sl) {
        super(Family.all(BugComponent.class).get());
        this.sl = sl;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CacconComponent cc = mapper.get(entity);
        DimensionsComponent dc = ComponentRetriever.get(entity, DimensionsComponent.class);
        TransformComponent tc = ComponentRetriever.get(entity, TransformComponent.class);
        FlowerCollisionComponent fcc = collisionMapper.get(entity);

        if (cc != null) {
            updateRect(cc, tc, dc);
            act(cc, entity, deltaTime);

            if (cc.isCollision) {
                hit(cc);
            }
        }
    }

    public void act(CacconComponent cc, Entity entity, float delta) {

        if (GlobalConstants.CUR_SCREEN == "GAME") {
            cc.counter++;
            if (cc.state == SPAWNING) {
                if (cc.counter >= GlobalConstants.COCOON_SPAWNING_DURATION) {
                    cc.state = IDLE;
                    cc.counter = 0;
                }
            }

            if (cc.state == HIT) {
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
                GameStage.sceneLoader.getEngine().removeEntity(entity);
            }
        }
    }

    public void hit(CacconComponent cc) {
        System.out.println("Hit!");
        cc.isCollision = false;
        if (cc.state != DEAD) {
            cc.health--;
            cc.state = HIT;
            cc.counter = 0;
        }
    }

    public void updateRect(CacconComponent cc, TransformComponent tc, DimensionsComponent dc) {
        if (cc != null) {
            cc.boundsRect.x = (int) tc.x;
            cc.boundsRect.y = (int) tc.y-200;
            cc.boundsRect.width = (int) dc.width * tc.scaleX;
            cc.boundsRect.height = (int) dc.height * tc.scaleY;
        }
    }

    private boolean checkCollision(CacconComponent cc, FlowerCollisionComponent fcc) {
        System.out.println("YES COLLISION");
        cc.isCollision = cc.boundsRect.overlaps(fcc.boundsRect);
        return cc.isCollision;
    }

    private void spawnButterfly() {
//            ((GameStage) stage).game.butterflyPowerUp = new ButterflyPowerUp(sceneLoader, stage);
//            ((GameStage) stage).game.butterflyPowerUp.createUmbrellaController();
//            ((GameStage) stage).game.butterflyPowerUp.getCompositeItem().setX(1900);
//            ((GameStage) stage).game.butterflyPowerUp.getCompositeItem().setY(700);
//            stage.addActor(((GameStage) stage).game.butterflyPowerUp.getCompositeItem());
//        }
    }


}
