package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.entity.componets.DandelionComponent;
import com.mygdx.game.entity.componets.FlowerCollisionComponent;
import com.mygdx.game.entity.componets.UmbrellaComponent;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.GlobalConstants;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;

import static com.mygdx.game.entity.componets.DandelionComponent.State.*;
import static com.mygdx.game.utils.GlobalConstants.*;
import static com.mygdx.game.stages.GameScreenScript.*;


/**
 * Created by Teatree on 9/3/2015.
 */
public class DandelionSystem extends IteratingSystem {

    private ComponentMapper<DandelionComponent> mapper = ComponentMapper.getFor(DandelionComponent.class);
    private FlowerCollisionComponent fcc;

    private int counter;
    private CompositeItemVO umbrellaComposite;

    public DandelionSystem(FlowerCollisionComponent fcc) {
        super(Family.all(DandelionComponent.class).get());
        this.fcc = fcc;
        umbrellaComposite = GameStage.sceneLoader.loadVoFromLibrary("simpleLib");
    }

    private void spawnUmbrella(){
        Entity umbrellaEntity = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), umbrellaComposite);
        GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), umbrellaEntity, umbrellaComposite.composite);
        GameStage.sceneLoader.getEngine().addEntity(umbrellaEntity);

        TransformComponent transformComponent = new TransformComponent();
        transformComponent.x = 1300;
        transformComponent.y = 210;
        umbrellaEntity.add(transformComponent);

        UmbrellaComponent umbrellaComponent  = new UmbrellaComponent();
        umbrellaEntity.add(umbrellaComponent);

        umbrellaEntity.add(fcc);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        DandelionComponent dc = mapper.get(entity);

        if(isGameAlive() && "GAME".equals(CUR_SCREEN)) {
            counter++;
            if (dc.state == GROWING) {
                if (counter >= GlobalConstants.DANDELION_GROWING_DURATION) {
                    dc.state = IDLE;
                    counter = 0;
                }
            }
            if (dc.state == IDLE) {
                if (counter >= GlobalConstants.DANDELION_IDLE_DURATION) {
                    dc.state = DYING;
                    counter = 0;
                }
            }
            if (dc.state == DYING) {
                if (counter == GlobalConstants.DANDELION_UMBRELLA_DUYING_POINT) {
                    spawnUmbrella();
                } else if (counter >= GlobalConstants.DANDELION_DUYING_DURATION) {
                    dc.state = DEAD;
                    GameStage.sceneLoader.getEngine().removeEntity(entity);
//                    ((GameStage) stage).removeActor(item);
//                    ((GameStage) stage).game.dandelionPowerup = null;
                }
            }
        }
    }
}

