package com.fd.etf.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Pool;

public class BugJuiceBubbleComponent implements Component, Pool.Poolable  {
    public float startX, startY;
    public float endX = 550;
    public float endY = 80;
    public float duration = 1;
    public float time;
    public float alpha = 0.2f;
    public Interpolation interpolation = Interpolation.fade;
    public boolean reverse, began, complete;
    public int pointsToAdd;

    public BugJuiceBubbleComponent(int points) {
        pointsToAdd = points;
    }

    @Override
    public void reset() {
        startX = 0;
        startY = 0;
        endX = 550;
        endY = 680;
        duration = 1;
        time = 0;
        alpha = 0.2f;
        interpolation = Interpolation.fade;
        reverse = false;
        began = false;
        complete = false;
    }
}
