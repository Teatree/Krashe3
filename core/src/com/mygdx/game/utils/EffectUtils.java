package com.mygdx.game.utils;

import com.badlogic.ashley.core.Entity;
import com.mygdx.game.entity.componets.ParticleLifespanComponent;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.components.particle.ParticleComponent;
import com.uwsoft.editor.renderer.data.ParticleEffectVO;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import java.text.SimpleDateFormat;

import static com.mygdx.game.stages.GameStage.sceneLoader;

public class EffectUtils {

    public static final String STARS_YELLOW_BURST = "starsyellowburst";
    public static final String GREEN_SPLATTER = "splatter.party";

    public static SimpleDateFormat getDateFormat(){
        return new SimpleDateFormat("MM/dd/yyyy");
    }

    public static void fadeChildren(NodeComponent nc, int fadeCoefficient) {
        if (nc != null && nc.children != null && nc.children.size != 0) {
            for (Entity e : nc.children) {
                TintComponent tc = e.getComponent(TintComponent.class);
                tc.color.a += fadeCoefficient * 0.1f;
                fadeChildren(e.getComponent(NodeComponent.class), fadeCoefficient);
            }
        }
    }

    public static void fade(Entity entity, boolean isPause) {
        NodeComponent nc = entity.getComponent(NodeComponent.class);
        TintComponent tcp = entity.getComponent(TintComponent.class);

        boolean appear = (tcp.color.a < 1 && isPause) ||
                (tcp.color.a > 0 && !isPause);

        int fadeCoefficient = isPause ? 1 : -1;

        if (appear) {
            tcp.color.a += fadeCoefficient * 0.1f;
            fadeChildren(nc, fadeCoefficient);
        }

        if (!isPause && tcp.color.a <= 0) {
            TransformComponent dialogTc = entity.getComponent(TransformComponent.class);
            dialogTc.x = -1000;
        }
    }

    public static void playParticleEffect(float x, float y, String particleName, float duration) {

        ParticleEffectVO vo = new ParticleEffectVO();
        vo.particleName = particleName;
        vo.layerName = "Default";
        vo.x = x;
        vo.y = y;

        Entity starBurstParticleE = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), vo);
        sceneLoader.getEngine().addEntity(starBurstParticleE);

        ParticleComponent pc = ComponentRetriever.get(starBurstParticleE, ParticleComponent.class);
        starBurstParticleE.add(pc);

        starBurstParticleE.getComponent(ZIndexComponent.class).setZIndex(101);

        ParticleLifespanComponent lifespanComponent = new ParticleLifespanComponent();
        lifespanComponent.duration = duration;
        starBurstParticleE.add(lifespanComponent);

        pc.particleEffect.start();
    }

    public static void playYellowStarsParticleEffect(float x, float y){
        playParticleEffect(x, y, STARS_YELLOW_BURST, 0.8f);
    }

    public static void playSplatterParticleEffect(float x, float y){
        playParticleEffect(x, y, GREEN_SPLATTER, 0.5f);
    }
}
