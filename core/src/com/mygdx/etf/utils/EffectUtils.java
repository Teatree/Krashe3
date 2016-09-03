package com.mygdx.etf.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.etf.entity.componets.BugJuiceBubbleComponent;
import com.mygdx.etf.entity.componets.ParticleLifespanComponent;
import com.mygdx.etf.stages.GameStage;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.particle.ParticleComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.data.ParticleEffectVO;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import static com.mygdx.etf.stages.GameStage.sceneLoader;

public class EffectUtils {

    public static final String STARS_YELLOW_BURST = "starsyellowburst";
    public static final String GREEN_SPLATTER = "splatter.party";
    public static final String TRAIL_GREEN = "trailgreen.party";
    public static final String DEFAULT_LAYER = "Default";
    public static final String BUG_JUICE_BUBBLE_LIB = "bug_juice_bubble_lib";

    public static void fadeChildren(NodeComponent nc, int fadeCoefficient) {
        if (nc != null && nc.children != null && nc.children.size != 0) {
            for (Entity e : nc.children) {
                TintComponent tc = e.getComponent(TintComponent.class);
                tc.color.a += fadeCoefficient * GlobalConstants.TENTH;
                fadeChildren(e.getComponent(NodeComponent.class), fadeCoefficient);
            }
        }
    }

    public static void fade(Entity entity, boolean isPause) {
        NodeComponent nc = entity.getComponent(NodeComponent.class);
        TintComponent tcp = entity.getComponent(TintComponent.class);

        boolean appear = ((tcp.color.a < 1 && isPause) ||
                (tcp.color.a > 0 && !isPause));

        int fadeCoefficient = isPause ? 1 : -1;

        if (appear) {
            tcp.color.a += fadeCoefficient * GlobalConstants.TENTH;
            fadeChildren(nc, fadeCoefficient);
        }

        if (!isPause && tcp.color.a <= 0) {
            TransformComponent dialogTc = entity.getComponent(TransformComponent.class);
            dialogTc.x = GlobalConstants.FAR_FAR_AWAY_X;
        }
    }

    public static void playParticleEffect(float x, float y, String particleName, float duration) {

        ParticleEffectVO vo = new ParticleEffectVO();
        vo.particleName = particleName;
        vo.layerName = DEFAULT_LAYER;
        vo.x = x;
        vo.y = y;

        Entity particleE = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), vo);
        sceneLoader.getEngine().addEntity(particleE);

        ParticleComponent pc = ComponentRetriever.get(particleE, ParticleComponent.class);
        particleE.add(pc);

        particleE.getComponent(ZIndexComponent.class).setZIndex(101);
        particleE.getComponent(ParentNodeComponent.class).parentEntity.getComponent(TransformComponent.class).x = x;
        particleE.getComponent(ParentNodeComponent.class).parentEntity.getComponent(TransformComponent.class).y = y;

        ParticleLifespanComponent lifespanComponent = new ParticleLifespanComponent();
        lifespanComponent.duration = duration;
        particleE.add(lifespanComponent);
        pc.particleEffect.setPosition(x, y);
        pc.particleEffect.start();
    }

    public static void playYellowStarsParticleEffect(float x, float y){
        playParticleEffect(x, y, STARS_YELLOW_BURST, 0.8f);
    }

    public static void playSplatterParticleEffect(float x, float y){
        playParticleEffect(x, y, GREEN_SPLATTER, 0.5f);
    }

    public static void playTrailGreenParticleEffect(float x, float y){
        playParticleEffect(x, y, TRAIL_GREEN, 0.5f);
    }

    public static void spawnBugJuiceBubble(float x, float y) {
        CompositeItemVO bugJuiceBubbleC = sceneLoader.loadVoFromLibrary(BUG_JUICE_BUBBLE_LIB);

        Entity bugJuiceBubbleE = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), bugJuiceBubbleC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), bugJuiceBubbleE, bugJuiceBubbleC.composite);
        sceneLoader.getEngine().addEntity(bugJuiceBubbleE);

        bugJuiceBubbleE.getComponent(ZIndexComponent.class).setZIndex(200);

        TransformComponent tc = bugJuiceBubbleE.getComponent(TransformComponent.class);
        bugJuiceBubbleE.add(new BugJuiceBubbleComponent());
        tc.x = x;
        tc.y = y;

//        EffectUtils.playSplatterParticleEffect(tc.x, tc.y);
        bugJuiceBubbleE.add(GameStage.gameScript.fpc);
    }

    public static Vector2 getTouchCoordinates() {
        return GameStage.viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
    }
}
