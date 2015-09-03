package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.uwsoft.editor.renderer.components.TransformComponent;

/**
 * Created by Teatree on 9/3/2015.
 */
public class BugComponent implements Component {

    public BugType type;

    public int points;

    public Rectangle boundsRect = new Rectangle();

    public float velocity = 0;
    public float startYPosition;

}
