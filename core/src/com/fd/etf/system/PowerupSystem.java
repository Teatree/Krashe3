package com.fd.etf.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.fd.etf.entity.componets.ButterflyComponent;
import com.fd.etf.entity.componets.CocoonComponent;
import com.fd.etf.entity.componets.DebugComponent;
import com.fd.etf.entity.componets.UmbrellaComponent;
import com.fd.etf.stages.GameStage;
import com.fd.etf.utils.SoundMgr;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.fd.etf.entity.componets.CocoonComponent.COCOON_SCALE;
import static com.fd.etf.entity.componets.CocoonComponent.COCOON_X;
import static com.fd.etf.entity.componets.CocoonComponent.COCOON_Y;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_Y;

/**
 * Created by River on 3/20/2017.
 */
public class PowerupSystem {

    private static final String COCOON = "coccoon";
    private static final String UMBRELLA_ANI = "umbrellaAni";

    public static float umbrellaSpawnCounter;
    public static float cocoonSpawnCounter;

    private GameStage gameStage;
    private ItemWrapper gameItem;

    public PowerupSystem(GameStage gs, ItemWrapper gi) {
        gameStage = gs;
        gameItem = gi;

        cocoonSpawnCounter = CocoonSystem.getNextSpawnInterval();
        umbrellaSpawnCounter = UmbrellaSystem.getNextSpawnInterval();
    }

    public void resetCounters(){
        cocoonSpawnCounter = CocoonSystem.getNextSpawnInterval();
        umbrellaSpawnCounter = UmbrellaSystem.getNextSpawnInterval();
    }

    public void initCocoon() {
        Entity cocoonEntity = gameItem.getChild(COCOON).getEntity();

        if (cocoonEntity.getComponent(CocoonComponent.class) == null) {
            CocoonComponent cocoonComponentc = new CocoonComponent();
            cocoonEntity.add(cocoonComponentc);
        }
        cocoonEntity.getComponent(CocoonComponent.class).state = CocoonComponent.State.DEAD;
        cocoonEntity.getComponent(CocoonComponent.class).hitCounter = 0;
        cocoonEntity.add(new DebugComponent(cocoonEntity.getComponent(CocoonComponent.class).boundsRect));

        Entity butEntity = gameItem.getChild(CocoonSystem.BUTTERFLY_ANI).getEntity();
        if (butEntity.getComponent(ButterflyComponent.class) == null) {
            ButterflyComponent dc = new ButterflyComponent();
            butEntity.add(dc);
        }
        butEntity.getComponent(ButterflyComponent.class).state = ButterflyComponent.State.DEAD;
        butEntity.add(new DebugComponent(butEntity.getComponent(ButterflyComponent.class).boundsRect));
    }

    public void initUmbrella() {
        Entity umbrellaEntity = gameItem.getChild(UMBRELLA_ANI).getEntity();
        if (umbrellaEntity.getComponent(UmbrellaComponent.class) != null) {
            umbrellaEntity.remove(UmbrellaComponent.class);
        }
        UmbrellaSystem.hide(umbrellaEntity);
    }

    private void spawnUmbrella(float x, float y) {
        Entity umbrellaEntity = gameItem.getChild(UMBRELLA_ANI).getEntity();

        if (umbrellaEntity.getComponent(UmbrellaComponent.class) == null) {
            UmbrellaComponent umbrellaComponent = new UmbrellaComponent();
            umbrellaComponent.setToSpawningState();
            umbrellaEntity.add(umbrellaComponent);

        } else {
            umbrellaEntity.getComponent(UmbrellaComponent.class).setToSpawningState();
        }

        umbrellaEntity.getComponent(TransformComponent.class).x = x;
        umbrellaEntity.getComponent(TransformComponent.class).y = y;

        umbrellaSpawnCounter = UmbrellaSystem.getNextSpawnInterval();
        umbrellaEntity.add(new DebugComponent(umbrellaEntity.getComponent(UmbrellaComponent.class).boundsRect));
        BugSpawnSystem.umbrellaBugsSpawned = 0;

        umbrellaEntity.getComponent(UmbrellaComponent.class).justSpawned = true;
        //System.out.println("just spawned is true!!");
    }

    private void spawnCocoon() {
        if (canCocoonSpawn(gameStage)) {
            cocoonSpawnCounter = CocoonSystem.getNextSpawnInterval();
            BugSpawnSystem.cocconBugsSpawned = 0;

            Entity cocoonEntity = gameItem.getChild(COCOON).getEntity();

            cocoonEntity.getComponent(SpriterComponent.class).scale = COCOON_SCALE;
            cocoonEntity.getComponent(SpriterComponent.class).player.setAnimation(0);

            TransformComponent tc = cocoonEntity.getComponent(TransformComponent.class);

            tc.x = COCOON_X;
            tc.y = COCOON_Y;
            cocoonEntity.add(tc);

            cocoonEntity.getComponent(CocoonComponent.class).state = CocoonComponent.State.SPAWNING;
            cocoonEntity.getComponent(CocoonComponent.class).hitCounter = 0;
            SoundMgr.getSoundMgr().play(SoundMgr.COCOON_APPEAR);
        }
    }

    public static boolean canCocoonSpawn(GameStage gameStage) {
        return
                gameStage.sceneLoader.getEngine().getEntitiesFor(Family.all(CocoonComponent.class).get()).size() == 0 ||
                        gameStage.sceneLoader.getEngine().getEntitiesFor(Family.all(CocoonComponent.class).get())
                                .get(0).getComponent(TransformComponent.class).x == FAR_FAR_AWAY_X ||
                        gameStage.sceneLoader.getEngine().getEntitiesFor(Family.all(CocoonComponent.class).get())
                                .get(0).getComponent(TransformComponent.class).x <= 0 ||
                        gameStage.sceneLoader.getEngine().getEntitiesFor(Family.all(CocoonComponent.class).get())
                                .get(0).getComponent(TransformComponent.class).y == FAR_FAR_AWAY_Y;
    }

    public static boolean canUmbrellaSpawn(GameStage gameStage) {

        return (gameStage.sceneLoader.getEngine().getEntitiesFor(Family.all(UmbrellaComponent.class).get()).size() == 0 ||
                gameStage.sceneLoader.getEngine().getEntitiesFor(Family.all(UmbrellaComponent.class).get())
                        .get(0).getComponent(TransformComponent.class).x == FAR_FAR_AWAY_X ||
                gameStage.sceneLoader.getEngine().getEntitiesFor(Family.all(UmbrellaComponent.class).get())
                        .get(0).getComponent(TransformComponent.class).x <= 0 ||
                gameStage.sceneLoader.getEngine().getEntitiesFor(Family.all(UmbrellaComponent.class).get())
                        .get(0).getComponent(TransformComponent.class).y == FAR_FAR_AWAY_Y
        );
    }

    public void update(float delta) {
//        if (canUmbrellaSpawn()) {
//            umbrellaSpawnCounter -= delta;
//        }
//        if (canCocoonSpawn()) {
//            cocoonSpawnCounter -= delta;
//        }
//        if (umbrellaSpawnCounter <= 0) {
//            spawnUmbrella(UmbrellaComponent.INIT_SPAWN_X, UmbrellaComponent.INIT_SPAWN_Y);
//        }
//        if (cocoonSpawnCounter <= 0) {
//            spawnCocoon();
//        }

        if(BugSpawnSystem.cocconBugsSpawned == cocoonSpawnCounter && canCocoonSpawn(gameStage)){
            spawnCocoon();
        }
        if(BugSpawnSystem.umbrellaBugsSpawned == umbrellaSpawnCounter && canUmbrellaSpawn(gameStage)){
            spawnUmbrella(UmbrellaComponent.INIT_SPAWN_X, UmbrellaComponent.INIT_SPAWN_Y);
        }
    }

    public void removePowerupsFromStage(){
        gameItem.getChild(COCOON).getEntity().getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        gameItem.getChild(COCOON).getEntity().getComponent(CocoonComponent.class).state = CocoonComponent.State.DEAD;

        gameItem.getChild(UMBRELLA_ANI).getEntity().getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        gameItem.getChild(UMBRELLA_ANI).getEntity().getComponent(UmbrellaComponent.class).state = UmbrellaComponent.State.DEAD;

        gameItem.getChild(CocoonSystem.BUTTERFLY_ANI).getEntity().getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        gameItem.getChild(CocoonSystem.BUTTERFLY_ANI).getEntity().getComponent(ButterflyComponent.class).state = ButterflyComponent.State.DEAD;


    }
}
