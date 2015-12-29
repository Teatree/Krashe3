package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Align;

/**
 * Created by AnastasiiaRudyk on 12/26/2015.
 */
public class BugJuiceBubbleComponent implements Component {
    public float startX, startY;
    public float endX = Gdx.graphics.getWidth() - 120;
    public float endY = Gdx.graphics.getHeight() - 120;
    public float duration = 1;
    public float time;
    public float alpha = 0.2f;
    public Interpolation interpolation = Interpolation.fade;
    public boolean reverse, began, complete;
    public int alignment = Align.bottomLeft;
}
