package com.mygdx.etf.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Align;

public class BugJuiceBubbleComponent implements Component {
    public float startX, startY;
    public float endX = 601;
    public float endY = 680;
    public float duration = 1;
    public float time;
    public float alpha = 0.2f;
    public Interpolation interpolation = Interpolation.fade;
    public boolean reverse, began, complete;
    public int alignment = Align.bottomLeft;

    public BugJuiceBubbleComponent() {
    }

}
