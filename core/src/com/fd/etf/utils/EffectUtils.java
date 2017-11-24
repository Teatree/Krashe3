package com.fd.etf.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.fd.etf.entity.componets.BugJuiceBubbleComponent;
import com.fd.etf.entity.componets.ParticleLifespanComponent;
import com.fd.etf.entity.componets.PetProjectileComponent;
import com.fd.etf.stages.GameStage;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.particle.ParticleComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.data.ParticleEffectVO;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import java.util.Random;

public class EffectUtils {

    public static final String STARS_YELLOW_BURST = "starsyellowburst";
    public static final String HEARTS_BURST = "starsredburst.party";
    public static final String PROJECTILEHIT = "projectilehit.party";
    public static final String SHINE_BURST = "shine.party";
    public static final String GREEN_SPLATTER = "splatter.party";
    public static final String DEFAULT_LAYER = "Default";
    public static final String BUG_JUICE_BUBBLE_LIB = "bug_juice_bubble_lib";
    public static final String PROJECTILE_DOG = "projctile_DOG";
    public static final String PROJECTILE_RAVEN = "projectile_RAVEN";
    public static final String PROJECTILE_CAT = "projectile_CAT";

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

    public static void playParticleEffect(GameStage gameStage, float x, float y, String particleName, float duration) {

        ParticleEffectVO vo = new ParticleEffectVO();
        vo.particleName = particleName;
        vo.layerName = DEFAULT_LAYER;
        vo.x = x;
        vo.y = y;

        Entity particleE = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), vo);
        gameStage.sceneLoader.getEngine().addEntity(particleE);

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

    public static void playYellowStarsParticleEffect(GameStage gameStage, float x, float y) {
        playParticleEffect(gameStage, x, y, STARS_YELLOW_BURST, 1.2f);
    }

    public static void playHeartsBurstParticleEffect(GameStage gameStage, float x, float y){
        playParticleEffect(gameStage, x, y, HEARTS_BURST, 0.8f);
    }

    public static void playProjectileHitParticleEffect(GameStage gameStage, float x, float y){
        playParticleEffect(gameStage, x, y, PROJECTILEHIT, 0.7f);
    }

    public static void playShineParticleEffect(GameStage gameStage, float x, float y) {
        playParticleEffect(gameStage, x, y, SHINE_BURST, 0.8f);
    }

    public static void playSplatterParticleEffect(GameStage gameStage, float x, float y) {
        playParticleEffect(gameStage, x, y, GREEN_SPLATTER, 0.5f);
    }

    public static void spawnBugJuiceBubble(int points, GameStage gameStage, float x, float y) {
        CompositeItemVO bugJuiceBubbleC = gameStage.sceneLoader.loadVoFromLibrary(BUG_JUICE_BUBBLE_LIB);

        Entity bugJuiceBubbleE = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), bugJuiceBubbleC);
        gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), bugJuiceBubbleE, bugJuiceBubbleC.composite);
        gameStage.sceneLoader.getEngine().addEntity(bugJuiceBubbleE);

        bugJuiceBubbleE.getComponent(ZIndexComponent.class).setZIndex(20);

        TransformComponent tc = bugJuiceBubbleE.getComponent(TransformComponent.class);
        bugJuiceBubbleE.add(new BugJuiceBubbleComponent(points));
        tc.x = x;
        tc.y = y;

//        EffectUtils.playSplatterParticleEffect(tc.x, tc.y);
        bugJuiceBubbleE.add(gameStage.gameScript.fpc);
    }

    public static void spawnPetProjectile(GameStage gameStage, float x, float y, String projectileName) {
        CompositeItemVO petProjectileC = gameStage.sceneLoader.loadVoFromLibrary(projectileName);

        Entity petProjectileE = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), petProjectileC);
        gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), petProjectileE, petProjectileC.composite);
        gameStage.sceneLoader.getEngine().addEntity(petProjectileE);

        petProjectileE.getComponent(ZIndexComponent.class).setZIndex(200);

        TransformComponent tc = petProjectileE.getComponent(TransformComponent.class);
        petProjectileE.add(new PetProjectileComponent());
        tc.x = x;
        tc.y = y;

        Random rand = new Random();
        tc.rotation = rand.nextInt(360);

//        ActionComponent ac = new ActionComponent();
//        ac.dataArray.add(Actions.sequence(Actions.rotateBy(360, 6f, Interpolation.bounceIn)));

//        EffectUtils.playSplatterParticleEffect(tc.x, tc.y);
        petProjectileE.add(gameStage.gameScript.fpc);
    }

    public static Vector2 getTouchCoordinates() {
        return GameStage.viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
    }
}
