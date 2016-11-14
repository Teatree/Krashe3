package com.fd.etf.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fd.etf.entity.componets.VanityComponent;
import com.fd.etf.entity.componets.listeners.ShopPoverUpTabListener;
import com.fd.etf.utils.BugPool;
import com.fd.etf.utils.ETFSceneLoader;
import com.fd.etf.utils.GlobalConstants;
import com.fd.etf.utils.SaveMngr;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.fd.etf.utils.BackgroundMusicMgr.backgroundMusicMgr;
import static com.fd.etf.utils.BackgroundMusicMgr.getBackgroundMusicMgr;
import static com.fd.etf.utils.GlobalConstants.BUTTON_TAG;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.fd.etf.utils.SoundMgr.getSoundMgr;

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
    private static MenuScreenScript menuScript;

    public GameStage(ETFSceneLoader sceneLoader) {
        GameStage.sceneLoader = sceneLoader;
        getSoundMgr();
        getBackgroundMusicMgr();
        gameScript = new GameScreenScript(this);
        gameScript.fpc = SaveMngr.loadStats();
        justCreated = true;

        if (gameScript.fpc.settings.shouldShowLaunchAd()) {
//            Main.mainController.showLaunchAd(new Runnable() {
//                @Override
//                public void run() {
                    initMenu();
//                }
//            });
        } else {
            initMenu();
        }

    }

    public static void initGame() {
        if (changedFlower || changedFlower2) {
            changedFlower = false;
            sceneLoader.loadScene(MAIN_SCENE, viewport);
            sceneLoader.setScene(MAIN_SCENE, viewport);

            BugPool.resetBugPool();

            ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
            sceneLoader.addComponentsByTagName(BUTTON_TAG, ButtonComponent.class);

            root.addScript(gameScript);
            gameScript.initButtons();
            gameScript.reset();
        } else {
            sceneLoader.setScene(MAIN_SCENE, viewport);
            if (justCreated) {
                ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());

                //TODO: only when debug needed!
//                sceneLoader.engine.addSystem(sceneLoader.renderer);

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
        GameScreenScript.isPause.set(false);
        GameScreenScript.isGameOver.set(false);
        BugPool.getInstance();
    }

    public static void initMenu() {
        if (changedFlower || changedFlower2) {
            sceneLoader.loadScene(MENU_SCENE, viewport);
            changedFlower2 = false;
            menuScript = null;
        }
        sceneLoader.setScene(MENU_SCENE, viewport);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        if (menuScript == null) {
            menuScript = new MenuScreenScript();
            root.addScript(menuScript);
            menuScript.initButtons();
        } else {
            menuScript.init(menuScript.menuItem.getEntity());
            menuScript.setupMenuScreenWorld();
        }
        GlobalConstants.CUR_SCREEN = MENU;
    }

    public void initResult() {
        sceneLoader.setScene(RESULT_SCENE, viewport);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        if (resultScript == null) {
            resultScript = new ResultScreenScript();
            root.addScript(resultScript);
            resultScript.initButtons();
        } else {
            resultScript.initResultScreen();
        }
        GlobalConstants.CUR_SCREEN = RESULT;
    }

    public static void initShop() {
        GameStage.viewport.setWorldSize(1200, 786);
        GameStage.viewport.getCamera().translate(0, 0, 0);

        sceneLoader.setScene(SHOP_SCENE, viewport);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        if (shopScript == null) {
            shopScript = new ShopScreenScript();
            root.addScript(shopScript);
        }
        ShopScreenScript.isPreviewOn.set(false);
        ShopPoverUpTabListener.reset();
        shopScript.checkIfChanged();
        GlobalConstants.CUR_SCREEN = SHOP;
    }

    public static void initShopWithAds() {
//        if (gameScript.fpc.settings.shouldShowShopAd()) {
//            mainController.showGeneralShopAd(new Runnable() {
//                @Override
//                public void run() {
//                    initShop();
//                }
//            });
//        } else {
            initShop();
//        }
    }

    public void initResultWithAds() {
//        if (gameScript.fpc.settings.shouldShowResultAd()) {
//            mainController.showResultScreenAd(new Runnable() {
//                @Override
//                public void run() {
//                    initResult();
//                }
//            });
//        } else {
            initResult();
//        }
    }

    public void update() {
        sceneLoader.getEngine().update(Gdx.graphics.getDeltaTime());
    }


    public static void resetAllProgress() {
        for (VanityComponent vc : gameScript.fpc.vanities) {
            Set<String> changedFiles = new HashSet<>();
            if (vc.enabled){
                for (Map.Entry<String,String> entry : vc.assetsToChange.entrySet()) {
                    if (changedFiles.add(entry.getKey())) {
                        vc.resetOneFileTodefault(entry);
                    }
                }
            }
            if (shopScript != null) shopScript.currentPageIndex = 0;
            vc.bought = false;
            vc.enabled = false;
            vc.advertised = false;
        }

        ShopScreenScript.shouldReload = true;

        ResultScreenScript.showCaseVanity = null;
        gameScript.fpc.score = 0;
        gameScript.fpc.bestScore = 0;
        gameScript.fpc.totalScore = 0;
        gameScript.fpc.level.difficultyLevel = 0;
        gameScript.fpc.level.resetNewInfo();

        gameScript.fpc.currentPet = null;
        gameScript.fpc.upgrades = new HashMap<>();
    }
}
