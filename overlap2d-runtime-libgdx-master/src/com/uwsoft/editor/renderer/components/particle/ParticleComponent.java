package com.uwsoft.editor.renderer.components.particle;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.utils.Pool;

public class ParticleComponent implements Component, Pool.Poolable {
    public String particleName = "";
    public ParticleEffect particleEffect;
    public float worldMultiplyer = 1f;

    @Override
    public void reset() {
        particleName = "";
        particleEffect = null;
        worldMultiplyer = 1f;
    }
}
