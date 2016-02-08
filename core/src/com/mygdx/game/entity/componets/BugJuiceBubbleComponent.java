package com.mygdx.game.entity.componets;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.system.BugJuiceBubbleSystem;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;

public class BugJuiceBubbleComponent implements Component {
    public float startX, startY;
    public float endX = 1080;
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
