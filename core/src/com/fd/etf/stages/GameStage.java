package com.fd.etf.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fd.etf.entity.componets.VanityComponent;
import com.fd.etf.entity.componets.listeners.ShopPoverUpTabListener;
import com.fd.etf.utils.BugPool;
import com.fd.etf.utils.ETFSceneLoader;
import com.fd.etf.utils.SaveMngr;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.data.SceneVO;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.fd.etf.utils.BackgroundMusicMgr.backgroundMusicMgr;
import static com.fd.etf.utils.BackgroundMusicMgr.getBackgroundMusicMgr;
import static com.fd.etf.utils.GlobalConstants.BUTTON_TAG;
import static com.fd.etf.utils.SoundMgr.getSoundMgr;

public class GameStage extends Stage {

    private static final String SHOP_SCENE = "ShopScene";
    private static final String RESULT_SCENE = "ResultScene";
    public static final String MAIN_SCENE = "MainScene";
    private static final String MENU_SCENE = "MenuScene";

    public static Viewport viewport;
    public ETFSceneLoader sceneLoader;
    public static boolean changedFlower;
    public static boolean changedFlower2;

    public static boolean justCreated;

    public GameScreenScript gameScript;

    private ResultScreenScript resultScript;
    public ShopScreenScript shopScript;
    private MenuScreenScript menuScript;

    public GameStage(ETFSceneLoader sceneLoader) {
        this.sceneLoader = sceneLoader;
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

    public void initGame(int currentFlowerFrame) {
        GameScreenScript.currentFlowerFrame = currentFlowerFrame;

        sceneLoader.setScene(MAIN_SCENE, viewport);
        if (justCreated) {
            ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
            sceneLoader.addComponentsByTagName(BUTTON_TAG, ButtonComponent.class);
            root.addScript(gameScript);
            gameScript.initButtons();
            justCreated = false;
        }
        if (changedFlower || changedFlower2) {
            sceneLoader.setScene(MAIN_SCENE, viewport);
            SceneVO sceneVO = sceneLoader.rm.getSceneVO(MAIN_SCENE);
            sceneLoader.rm.reloadFlowerAni();
            sceneVO.composite.update(sceneVO.composite);

//            newFlower.getComponent(TransformComponent.class).x = 100;
//            newFlower.getComponent(TransformComponent.class).y = 100;

            changedFlower = false;
//            sceneLoader.loadScene(MAIN_SCENE, viewport);
//            sceneLoader.setScene(MAIN_SCENE, viewport);
//
//            BugPool.resetBugPool(this);
//
//            ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
//            sceneLoader.addComponentsByTagName(BUTTON_TAG, ButtonComponent.class);
//
//            root.addScript(gameScript);
//            gameScript.initButtons();
        }

        gameScript.reset();

        backgroundMusicMgr.stop();

        GameScreenScript.isStarted = false;
        GameScreenScript.isPause.set(false);
        GameScreenScript.isGameOver.set(false);
        BugPool.getInstance(this);
    }

    public void initMenu() {
        sceneLoader.setScene(MENU_SCENE, viewport);
        if (changedFlower || changedFlower2) {
//            sceneLoader.loadScene(MENU_SCENE, viewport);
//            changedFlower2 = false;
//            menuScript = null;

            sceneLoader.setScene(MENU_SCENE, viewport);
            SceneVO sceneVO = sceneLoader.rm.getSceneVO(MENU_SCENE);
            sceneLoader.rm.reloadFlowerAni();
            sceneVO.composite.update(sceneVO.composite);

            changedFlower2 = false;
        }

        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        if (menuScript == null) {
            menuScript = new MenuScreenScript(this);
            root.addScript(menuScript);
            menuScript.initButtons();
        } else {
            menuScript.init(menuScript.menuItem.getEntity());
            menuScript.setupMenuScreenWorld();
        }
        System.gc();
        System.runFinalization();
    }

    public void initResult() {
        sceneLoader.setScene(RESULT_SCENE, viewport);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        if (resultScript == null) {
            resultScript = new ResultScreenScript(this);
            root.addScript(resultScript);
            resultScript.initButtons();
        } else {
            resultScript.initResultScreen();
        }
        System.gc();
        System.runFinalization();
    }

    public void initShop() {
        GameStage.viewport.setWorldSize(1200, 786);
        GameStage.viewport.getCamera().translate(0, 0, 0);

        sceneLoader.setScene(SHOP_SCENE, viewport);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        if (shopScript == null) {
            shopScript = new ShopScreenScript(this);
            root.addScript(shopScript);
        }
        ShopScreenScript.isPreviewOn.set(false);
        ShopPoverUpTabListener.reset();
        shopScript.checkIfChanged();
        System.gc();
        System.runFinalization();
    }

    public void initShopWithAds() {
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

    public void resetAllProgress() {
        for (VanityComponent vc : gameScript.fpc.vanities) {
            Set<String> changedFiles = new HashSet<>();
            if (vc.enabled || vc.bought) {
                for (Map.Entry<String, String> entry : vc.assetsToChange.entrySet()) {
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

        gameScript.fpc.level.difficultyLevel = 0;
        gameScript.fpc.level.resetNewInfo();
        gameScript.fpc.level.goals = gameScript.fpc.level.goalGenerator.getGoals(gameScript.fpc);
        System.gc();
        System.runFinalization();
    }
}
