package com.mygdx.etf.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.etf.entity.componets.ParticleLifespanComponent;
import com.uwsoft.editor.renderer.components.particle.ParticleComponent;

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
            ParticleComponent pc = entity.getComponent(ParticleComponent.class);
            pc.particleEffect.dispose();
        }
    }
}