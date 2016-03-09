package com.mygdx.game.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.utils.BugPool;
import com.mygdx.game.utils.ETFSceneLoader;
import com.mygdx.game.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.resources.ResourceManager;
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

    public static Viewport viewport;
    public static ETFSceneLoader sceneLoader;
    public static boolean changedFlower;
    public static boolean changedFlower2;

    private GameScreenScript gameScript;
    private ResultScreenScript resultScript;

    public GameStage(ETFSceneLoader sceneLoader) {
        GameStage.sceneLoader = sceneLoader;
        getSoundMgr();
        getBackgroundMusicMgr();
        initMenu();
    }

    public static void updateFlowerAni() {
        ((ResourceManager) sceneLoader.getRm()).loadSpriterAnimations();
        if (sceneLoader.rootEntity != null) {
            sceneLoader.entityFactory.updateSpriterAnimation(sceneLoader.engine, sceneLoader.rootEntity,
                    sceneLoader.sceneVO.composite.sComposites.get(0).composite.sSpriterAnimations);
        }
    }

    public void initGame() {

        if (changedFlower || changedFlower2) {
            try {
                sceneLoader.loadScene(MAIN_SCENE, viewport);
                changedFlower = false;
                sceneLoader.setScene(MAIN_SCENE);
                BugPool.resetBugPool();
                sceneLoader.addComponentsByTagName(BUTTON_TAG, ButtonComponent.class);
                ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
                root.addScript(gameScript);
            } catch (Exception e) {
                System.err.println(
                        e
                );

                throw e;
            }
        } else {
            sceneLoader.setScene(MAIN_SCENE);
            if (gameScript == null) {
                gameScript = new GameScreenScript(this);
                ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
                sceneLoader.addComponentsByTagName(BUTTON_TAG, ButtonComponent.class);
                root.addScript(gameScript);
            }
            gameScript.reset();
        }

        GlobalConstants.CUR_SCREEN = "GAME";
        backgroundMusicMgr.stop();

        GameScreenScript.isStarted = false;
        GameScreenScript.isPause = false;
        GameScreenScript.isGameOver = false;
        BugPool.getInstance();
    }

    public void initMenu() {
        if (changedFlower || changedFlower2) {
            sceneLoader.loadScene(MENU_SCENE, viewport);
            changedFlower2 = false;
        }
        sceneLoader.setScene(MENU_SCENE);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        root.addScript(new MenuScreenScript(this));
        GlobalConstants.CUR_SCREEN = "MENU";
    }

    public void initResult() {
        sceneLoader.setScene(RESULT_SCENE);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        if (resultScript == null) {
            resultScript = new ResultScreenScript(this);
            root.addScript(resultScript);
        }
        else {
            resultScript.initResultScreen();
        }
        GlobalConstants.CUR_SCREEN = "RESULT";
    }

    public void initShop() {
        sceneLoader.engineByScene.remove(SHOP_SCENE);
        sceneLoader.rootEntityByScene.remove(SHOP_SCENE);
        sceneLoader.loadScene(SHOP_SCENE, viewport);
        sceneLoader.setScene(SHOP_SCENE);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        root.addScript(new ShopScreenScript(this));
        GlobalConstants.CUR_SCREEN = "SHOP";
    }

    public void update() {
        sceneLoader.getEngine().update(Gdx.graphics.getDeltaTime());
    }
}
