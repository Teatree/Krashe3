package com.mygdx.etf.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.etf.entity.componets.BugJuiceBubbleComponent;
import com.mygdx.etf.entity.componets.FlowerPublicComponent;
import com.mygdx.etf.stages.GameStage;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;

public class BugJuiceBubbleSystem extends IteratingSystem {
    private ComponentMapper<BugJuiceBubbleComponent> mapper = ComponentMapper.getFor(BugJuiceBubbleComponent.class);
    private ComponentMapper<FlowerPublicComponent> flowerMapper = ComponentMapper.getFor(FlowerPublicComponent.class);

    public BugJuiceBubbleSystem() {
        super(Family.all(BugJuiceBubbleComponent.class).get());
    }

    protected void processEntity(Entity entity, float deltaTime) {
        TintComponent tic = entity.getComponent(TintComponent.class);
        TransformComponent tc = entity.getComponent(TransformComponent.class);
        FlowerPublicComponent fcc = flowerMapper.get(entity);
        BugJuiceBubbleComponent bjc = mapper.get(entity);

        if (!bjc.began) {
            begin(bjc, tc);
            bjc.began = true;
        }
        bjc.time += deltaTime;
        bjc.complete = bjc.time >= bjc.duration;
        float percent;
        if (bjc.complete) {
            percent = 1;
        } else {
            percent = bjc.time / bjc.duration;
            if (bjc.interpolation != null) percent = bjc.interpolation.apply(percent);
        }
        update(bjc, tc, bjc.reverse ? 1 - percent : percent);
        if (bjc.complete) end(fcc, entity);

//        tic.color.a = bjc.alpha;
        if (bjc.time <= (bjc.duration/5)){
            tic.color.a = 0;
        } else if (bjc.time > (bjc.duration/5) && bjc.time < (4*bjc.duration/5) && tic.color.a <= 0.7f){
            tic.color.a += 0.1f;
        } else if (bjc.time >= (4*bjc.duration/5) ){
            tic.color.a -= 0.05f;
        }
//        }
    }

    public void update(BugJuiceBubbleComponent bjc, TransformComponent tc, float percent) {
        setPosition(tc, bjc.startX + (bjc.endX - bjc.startX) * percent, bjc.startY + (bjc.endY - bjc.startY) * percent );
    }

    protected void begin(BugJuiceBubbleComponent bjc, TransformComponent tc) {
        bjc.startX = tc.x;
        bjc.startY = tc.y;
    }

    protected void end(FlowerPublicComponent fcc, Entity entity) {
        GameStage.gameScript.scoreLabelComponent.text.replace(0, GameStage.gameScript.scoreLabelComponent.text.capacity(), "" + fcc.score + "/" + fcc.totalScore);
        GameStage.sceneLoader.getEngine().removeEntity(entity);
    }

    public void setPosition(TransformComponent tc, float x, float y) {
        tc.x = x;
        tc.y = y;
    }

}
