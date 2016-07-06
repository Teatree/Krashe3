package com.mygdx.etf.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.etf.Main;
import com.mygdx.etf.entity.componets.VanityComponent;
import com.mygdx.etf.utils.BugPool;
import com.mygdx.etf.utils.ETFSceneLoader;
import com.mygdx.etf.utils.GlobalConstants;
import com.mygdx.etf.utils.SaveMngr;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.resources.ResourceManager;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.HashMap;

import static com.mygdx.etf.Main.mainController;
import static com.mygdx.etf.utils.BackgroundMusicMgr.backgroundMusicMgr;
import static com.mygdx.etf.utils.BackgroundMusicMgr.getBackgroundMusicMgr;
import static com.mygdx.etf.utils.GlobalConstants.BUTTON_TAG;
import static com.mygdx.etf.utils.SoundMgr.getSoundMgr;

public class GameStage extends Stage {

    public static final String SHOP_SCENE = "ShopScene";
    public static final String RESULT_SCENE = "ResultScene";
    public static final String MAIN_SCENE = "MainScene";
    public static final String MENU_SCENE = "MenuScene";

    public static final String MENU = "MENU";
    public static final String SHOP = "SHOP";
    public static final String RESULT = "RESULT";
    public static final String GAME = "GAME";

    public static Viewport viewport;
    public static ETFSceneLoader sceneLoader;
    public static boolean changedFlower;
    public static boolean changedFlower2;

    public static boolean justCreated;

    public static GameScreenScript gameScript;

    private ResultScreenScript resultScript;
    public static ShopScreenScript shopScript;
    private MenuScreenScript menuScript;

    public GameStage(ETFSceneLoader sceneLoader) {
        GameStage.sceneLoader = sceneLoader;
        getSoundMgr();
        getBackgroundMusicMgr();
        gameScript = new GameScreenScript(this);
        gameScript.fpc = SaveMngr.loadStats();
        justCreated = true;

        if (gameScript.fpc.settings.shouldShowLaunchAd()) {
            Main.mainController.showLaunchAd(new Runnable() {
                @Override
                public void run() {
                    initMenu();
                }
            });
        } else {
            initMenu();
        }

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
            changedFlower = false;
            sceneLoader.loadScene(MAIN_SCENE, viewport);
            sceneLoader.setScene(MAIN_SCENE);

            BugPool.resetBugPool();

            ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
            sceneLoader.addComponentsByTagName(BUTTON_TAG, ButtonComponent.class);

            root.addScript(gameScript);
            gameScript.initButtons();
            gameScript.reset();

        } else {
            sceneLoader.setScene(MAIN_SCENE);
            if (justCreated) {
                ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
                sceneLoader.addComponentsByTagName(BUTTON_TAG, ButtonComponent.class);
                root.addScript(gameScript);
                gameScript.initButtons();
                justCreated = false;
            } else {
                gameScript.reset();
            }
        }

        GlobalConstants.CUR_SCREEN = GAME;
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
            menuScript = null;
        }
        sceneLoader.setScene(MENU_SCENE);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        if (menuScript == null) {
            menuScript = new MenuScreenScript(this);
            root.addScript(menuScript);
            menuScript.initButtons();
        } else {
            menuScript.init(menuScript.menuItem.getEntity());
        }
        GlobalConstants.CUR_SCREEN = MENU;
    }

    public void initResult() {
        sceneLoader.setScene(RESULT_SCENE);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        if (resultScript == null) {
            resultScript = new ResultScreenScript(this);
            root.addScript(resultScript);
            resultScript.initButtons();
        } else {
            resultScript.initResultScreen();
        }
        GlobalConstants.CUR_SCREEN = RESULT;
    }

    public void initShop() {
        sceneLoader.setScene(SHOP_SCENE);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        if (shopScript == null) {
            shopScript = new ShopScreenScript(this);
            root.addScript(shopScript);
        }
        ShopScreenScript.isPreviewOn = false;
        GlobalConstants.CUR_SCREEN = SHOP;
    }

    public void initShopWithAds() {
        if (gameScript.fpc.settings.shouldShowShopAd()){
            mainController.showGeneralShopAd(new Runnable() {
                @Override
                public void run() {
                    initShop();
                }
            });
        } else {
            initShop();
        }
    }

    public void initResultWithAds() {
        if (gameScript.fpc.settings.shouldShowResultAd()){
            mainController.showResultScreenAd(new Runnable() {
                @Override
                public void run() {
                    initResult();
                }
            });
        } else {
            initResult();
        }
    }

    public void update() {
        sceneLoader.getEngine().update(Gdx.graphics.getDeltaTime());
    }


    public static void resetAllProgress(){
        for (VanityComponent vc : gameScript.fpc.vanities){
            if (vc.enabled){
                vc.disable();
            }
            vc.bought = false;
            vc.enabled = false;
            vc.advertised = false;

            gameScript.fpc.score = 0;
            gameScript.fpc.bestScore = 0;
            gameScript.fpc.totalScore = 0;
            gameScript.fpc.level.difficultyLevel = 0;
            gameScript.fpc.level.resetNewInfo();

            gameScript.fpc.currentPet = null;
            gameScript.fpc.upgrades = new HashMap<>();
        }
    }
}