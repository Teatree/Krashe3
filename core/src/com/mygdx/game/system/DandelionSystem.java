package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.entity.componets.DandelionComponent;
import com.mygdx.game.entity.componets.UmbrellaComponent;
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
    private SceneLoader sl;

    private int counter;
    private CompositeItemVO umbrellaComposite;

    public DandelionSystem(SceneLoader sceneLoader) {
        super(Family.all(DandelionComponent.class).get());
        this.sl = sceneLoader;

        umbrellaComposite = sceneLoader.loadVoFromLibrary("simpleLib");
    }

    private void spawnUmbrella(){
        Entity umbrellaEntity = sl.entityFactory.createEntity(sl.getRoot(), umbrellaComposite);
        sl.entityFactory.initAllChildren(sl.getEngine(), umbrellaEntity, umbrellaComposite.composite);
        sl.getEngine().addEntity(umbrellaEntity);

        TransformComponent transformComponent = new TransformComponent();
        transformComponent.x = 1300;
        transformComponent.y = 210;
        umbrellaEntity.add(transformComponent);

        UmbrellaComponent umbrellaComponent  = new UmbrellaComponent();
        umbrellaComponent.state = UmbrellaComponent.State.PUSH;
        umbrellaEntity.add(umbrellaComponent);

//        stage.addActor(((GameStage)stage).game.umbrellaPowerUp.getCompositeItem());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        DandelionComponent dc = mapper.get(entity);

        if(isGameAlive() && CUR_SCREEN == "GAME") {
            counter++;
            if (dc.state == GROWING) {
                if (counter >= GlobalConstants.DANDELION_GROWING_DURATION) {
//                    spriterActor.setAnimation(1);
                    dc.state = IDLE;
                    counter = 0;
                }
            }
            if (dc.state == IDLE) {
                if (counter >= GlobalConstants.DANDELION_IDLE_DURATION) {
//                    spriterActor.setAnimation(2);
                    dc.state = DYING;
                    counter = 0;
                }
            }
            if (dc.state == DYING) {
                if (counter == GlobalConstants.DANDELION_UMBRELLA_DUYING_POINT) {
                    spawnUmbrella();
                } else if (counter >= GlobalConstants.DANDELION_DUYING_DURATION) {
                    dc.state = DEAD;
                    sl.getEngine().removeEntity(entity);
//                    ((GameStage) stage).removeActor(item);
//                    ((GameStage) stage).game.dandelionPowerup = null;
                }
            }
        }
    }
}

