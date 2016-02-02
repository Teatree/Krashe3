package com.mygdx.game.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.entity.componets.ParticleLifespanComponent;

/**
 * Created by AnastasiiaRudyk on 2/2/2016.
 */
public class ParticleLifespanSystem extends IteratingSystem {

    private ComponentMapper<ParticleLifespanComponent> mapper = ComponentMapper.getFor(ParticleLifespanComponent.class);

    public ParticleLifespanSystem() {
        super(Family.all(ParticleLifespanComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ParticleLifespanComponent component = mapper.get(entity);
        component.duration -= deltaTime;

        if (component.duration <= 0) {
            getEngine().removeEntity(entity);
        }
    }
}