package com.fd.etf.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.brashmonkey.spriter.Rectangle;
import com.fd.etf.entity.componets.DebugComponent;
import com.fd.etf.entity.componets.Goal;
import com.fd.etf.entity.componets.PetProjectileComponent;
import com.fd.etf.stages.GameScreenScript;
import com.fd.etf.stages.GameStage;
import com.fd.etf.utils.EffectUtils;
import com.fd.etf.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;

import java.util.Iterator;
import java.util.Map;

public class PetProjectileSystem extends IteratingSystem {
    private ComponentMapper<PetProjectileComponent> mapper = ComponentMapper.getFor(PetProjectileComponent.class);

    private GameStage gameStage;

    public PetProjectileSystem(GameStage gameStage) {
        super(Family.all(PetProjectileComponent.class).get());
        this.gameStage = gameStage;
    }

    protected void processEntity(Entity entity, float deltaTime) {
        PetProjectileComponent ppc = mapper.get(entity);

        if (GameScreenScript.isGameOver.get() || !GameScreenScript.isStarted) {
            entity.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;

            ppc.complete = true;
            end(entity);
        } else {
            if (!ppc.began) {
                begin(entity, ppc, entity.getComponent(TransformComponent.class));
                ppc.began = true;
            }
            ppc.time += deltaTime;
            ppc.complete = ppc.time >= ppc.duration;
            float percent;
            if (ppc.complete) {
                percent = 1;
            } else {
                percent = ppc.time / ppc.duration;
                if (ppc.interpolation != null) percent = ppc.interpolation.apply(percent);
            }
            update(ppc, entity.getComponent(TransformComponent.class), ppc.reverse ? 1 - percent : percent);
            if (ppc.complete || entity.getComponent(PetProjectileComponent.class).isDead) end(entity);

            if (ppc.time <= (ppc.duration / 5)) {
                entity.getComponent(TintComponent.class).color.a = 0;
            } else if (ppc.time > (ppc.duration / 5) && ppc.time < (4 * ppc.duration / 5) && entity.getComponent(TintComponent.class).color.a <= 0.7f) {
                entity.getComponent(TintComponent.class).color.a += 0.1f;
            } else if (ppc.time >= (4 * ppc.duration / 5)) {
                entity.getComponent(TintComponent.class).color.a -= 0.05f;
            }
        }
    }

    public void updateRect(PetProjectileComponent ppc, TransformComponent tc) {
        ppc.boundsRect.x = (int) tc.x - 40;
        ppc.boundsRect.y = (int) tc.y - 40;
        ppc.boundsRect.width = 60;
        ppc.boundsRect.height = 60;
    }

    public void update(PetProjectileComponent ppc, TransformComponent tc, float percent) {
        setPosition(tc, ppc.startX + (ppc.endX - ppc.startX) * percent, ppc.startY + (ppc.endY - ppc.startY) * percent);
        updateRect(ppc, tc);
//        System.out.println("tc.x = " + tc.x);
//        System.out.println("ppc.boundsRect.x = " + ppc.boundsRect.x);
    }

    protected void begin(Entity entity, PetProjectileComponent ppc, TransformComponent tc) {
        ppc.startX = tc.x;
        ppc.startY = tc.y;

        entity.add(new DebugComponent(ppc.boundsRect));

        gameStage.gameScript.projectileBounds.put(entity, ppc.boundsRect);
    }

    protected void end(Entity entity) {
        Iterator it = gameStage.gameScript.projectileBounds.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<Entity, Rectangle> pair = (Map.Entry<Entity, Rectangle>) it.next();

            if (pair.getKey().getComponent(PetProjectileComponent.class).isDead) {
                gameStage.gameScript.projectileBounds.remove(pair.getKey());
            } else if (entity.getComponent(PetProjectileComponent.class).complete) {
                it.remove();
            }
        }

        gameStage.sceneLoader.getEngine().removeEntity(entity);
    }

    public void setPosition(TransformComponent tc, float x, float y) {
        tc.x = x;
        tc.y = y;
    }

}
