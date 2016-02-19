package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.utils.BugPool;
import com.mygdx.game.utils.ETFSceneLoader;
import com.mygdx.game.utils.GlobalConstants;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.resources.ResourceManager;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.game.utils.BackgroundMusicMgr.backgroundMusicMgr;
import static com.mygdx.game.utils.BackgroundMusicMgr.getBackgroundMusicMgr;
import static com.mygdx.game.utils.GlobalConstants.BUTTON_TAG;
import static com.mygdx.game.utils.SoundMgr.getSoundMgr;

public class GameStage extends Stage {

    public static final String SHOP_SCENE = "ShopScene";
    public static final String RESULT_SCENE = "ResultScene";
    public static final String MAIN_SCENE = "MainScene";
    public static final String MENU_SCENE = "MenuScene";
    public static final String MEGA_FLOWER = "mega_flower";

    public static Viewport viewport;
    public static SceneLoader sceneLoader;
    public static boolean changedFlower;
    private GameScreenScript gameScript;
    public static boolean changedFlower2;

    public GameStage() {
        viewport = new FillViewport(1200, 786);
//        sceneLoader = new ETFSceneLoader(viewport);
        sceneLoader = new SceneLoader();
        getSoundMgr();
        getBackgroundMusicMgr();
        initMenu();
    }

//    public static void updateFlowerAni() {
//        ((ResourceManager) sceneLoader.getRm()).loadSpriterAnimations();
////        Entity[] e = sceneLoader.rootEntity.getComponent(NodeComponent.class).children.toArray();
//        if (sceneLoader.rootEntity != null) {
//            sceneLoader.entityFactory.updateSpriterAnimation(sceneLoader.engine, sceneLoader.rootEntity,
//                    sceneLoader.sceneVO.composite.sComposites.get(0).composite.sSpriterAnimations);
////            Entity bp = new ItemWrapper(sceneLoader.rootEntity).getChild(BTN_PLAY).getEntity();
////            if (bp!= null) {
////                sceneLoader.getEngine().removeEntity(bp);
////                sceneLoader.getEngine().addEntity(bp);
////            }
////
////            Entity mf = new ItemWrapper(sceneLoader.rootEntity).getChild(MEGA_FLOWER).getEntity();
////            if (mf!= null) {
////                sceneLoader.getEngine().removeEntity(mf);
////                sceneLoader.getEngine().addEntity(mf);
////            }
//        }
//    }

    public GameStage getInstance() {
        return this;
    }

    public void initGame() {

//        if (changedFlower || changedFlower2) {
            sceneLoader.loadScene(MAIN_SCENE, viewport);
//            changedFlower = false;
//            sceneLoader.setScene(MAIN_SCENE);
//            BugPool.resetBugPool();
//        } else {
//            sceneLoader.setScene(MAIN_SCENE);
//        }

        sceneLoader.addComponentsByTagName(BUTTON_TAG, ButtonComponent.class);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        if (gameScript == null) {
            gameScript = new GameScreenScript(this);
        }
        root.addScript(gameScript);
        GlobalConstants.CUR_SCREEN = "GAME";
        backgroundMusicMgr.stop();

        GameScreenScript.isStarted = false;
        BugPool.getInstance();
    }

    public void initMenu() {
//        if (changedFlower || changedFlower2) {
            sceneLoader.loadScene(MENU_SCENE, viewport);
//            changedFlower2 = false;
//        }
//        sceneLoader.setScene(MENU_SCENE);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        root.addScript(new MenuScreenScript(this));
        GlobalConstants.CUR_SCREEN = "MENU";
    }

    public void initResult() {
//        sceneLoader.setScene(RESULT_SCENE);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        root.addScript(new ResultScreenScript(this));
        GlobalConstants.CUR_SCREEN = "RESULT";
    }

    public void initShopMenu() {
//        sceneLoader.engineByScene.remove(SHOP_SCENE);
//        sceneLoader.rootEntityByScene.remove(SHOP_SCENE);
        sceneLoader.loadScene(SHOP_SCENE, viewport);
//        sceneLoader.setScene(SHOP_SCENE);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        root.addScript(new ShopScreenScript(this));
        GlobalConstants.CUR_SCREEN = "SHOP";
    }

    public void initLoading() {
        sceneLoader.loadScene("LoadingScene", viewport);
        new ItemWrapper(sceneLoader.getRoot()).addScript(new IScript() {
            boolean t = true;
            int l = 0;
            @Override
            public void init(Entity entity) {

            }

            @Override
            public void act(float delta) {

                if (t) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            ((ResourceManager) sceneLoader.getRm()).initAllResources();
                        }
                    });
                    t = false;
                }
                while (l <= 1000) {
                    initMenu();
                    l++;
                }
            }

            @Override
            public void dispose() {

            }
        });
        GlobalConstants.CUR_SCREEN = "LOADING";
    }

    public void update() {
        sceneLoader.getEngine().update(Gdx.graphics.getDeltaTime());
    }
}
