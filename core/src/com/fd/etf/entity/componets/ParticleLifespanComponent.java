package com.fd.etf.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class ParticleLifespanComponent implements Component, Pool.Poolable{
    public float duration;


    @Override
    public void reset() {
        duration = 0;
    }
}

