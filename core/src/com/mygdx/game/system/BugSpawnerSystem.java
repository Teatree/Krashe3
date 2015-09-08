package com.mygdx.game.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

/**
 * Created by Teatree on 9/3/2015.
 */
public class BugSpawnerSystem extends EntitySystem {
    @Override
    public void update(float deltaTime) {

    }

    private void init() {
        MIN_X = -400;
        MIN_Y = 300;
        MAX_X = -200;
        MAX_Y = 1200;
    }
}
