package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.utils.BugPool;
import com.mygdx.game.utils.ETFSceneLoader;
import com.mygdx.game.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.resources.ResourceManager;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import javax.xml.soap.Node;

import java.util.List;

import static com.mygdx.game.utils.BackgroundMusicMgr.backgroundMusicMgr;
import static com.mygdx.game.utils.BackgroundMusicMgr.getBackgroundMusicMgr;
import static com.mygdx.game.utils.SoundMgr.getSoundMgr;

public class GameStage extends Stage {

    public static Viewport viewport;
    public static ETFSceneLoader sceneLoader;
    private GameScreenScript gameScript;

    public GameStage() {
        viewport = new FillViewport(1200, 786);
        sceneLoader = new ETFSceneLoader(viewport);
//        rm.initScene("LoadingScene");
//        sceneLoader = new SceneLoader(rm);
        getSoundMgr();
        getBackgroundMusicMgr();
        initMenu();
//        initLoading();
    }

    public GameStage getInstance() {
        return this;
    }

    public void initGame() {
//        sceneLoader.loadScene("MainScene", viewport);
//        updateFlowerAni();

        sceneLoader.setScene("MainScene");
//        updateFlowerAni();

        sceneLoader.addComponentsByTagName("button", ButtonComponent.class);
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

    public void updateFlowerAni() {
        ((ResourceManager)sceneLoader.getRm()).loadSpriterAnimations();
        Entity[] e = sceneLoader.rootEntity.getComponent(NodeComponent.class).children.toArray();
//        ((ResourceManager)sceneLoader.getRm()).loadSpriterAnimation("flower_idle");
        if (sceneLoader.rootEntity != null) {
            sceneLoader.entityFactory.updateSpriterAnimation(sceneLoader.engine, sceneLoader.rootEntity,
                    sceneLoader.sceneVO.composite.sComposites.get(0).composite.sSpriterAnimations);
            sceneLoader.entityFactory.updateMap(new ItemWrapper(sceneLoader.rootEntity).getChild("btn_play").getEntity());
            sceneLoader.entityFactory.updateMap(new ItemWrapper(sceneLoader.rootEntity).getChild("mega_flower").getEntity());
        }
//        sceneLoader.sceneVO.composite.sComposites.get(0);

    }

    public void initMenu() {
        sceneLoader.loadScene("MenuScene", viewport);
        sceneLoader.setScene("MenuScene");
        updateFlowerAni();
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        root.addScript(new MenuScreenScript(this));
        GlobalConstants.CUR_SCREEN = "MENU";
    }

    public void initResult() {
//        sceneLoader.loadScene("ResultScene", viewport);
        sceneLoader.setScene("ResultScene");
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        root.addScript(new ResultScreenScript(this));
        GlobalConstants.CUR_SCREEN = "RESULT";
    }

    public void initShopMenu() {
//        sceneLoader.engineByScene.remove("ShopScene");
//        sceneLoader.rootEntityByScene.remove("ShopScene");
        sceneLoader.loadScene("ShopScene", viewport);
        sceneLoader.setScene("ShopScene");
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
