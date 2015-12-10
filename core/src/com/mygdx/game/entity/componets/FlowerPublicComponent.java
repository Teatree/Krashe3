package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by AnastasiiaRudyk on 27/10/2015.
 */
public class FlowerPublicComponent implements Component {
    public Rectangle boundsRect = new Rectangle();
    public boolean isCollision;

    public int score;
}
          