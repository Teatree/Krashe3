package com.fd.etf.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;

import java.util.Random;

public class PetProjectileComponent implements Component, Pool.Poolable  {
    public float startX, startY;
    public float endX = new Random().nextInt(700 - 100) + 100;
    public float endY = new Random().nextInt(700 - 100) + 100;
    public float duration = 1;
    public float time;
    public float alpha = 0.2f;
    public Interpolation interpolation = Interpolation.fade;
    public boolean reverse, began, complete;
    public Rectangle boundsRect = new Rectangle();
    public boolean isDead = false;

    public PetProjectileComponent() {
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
