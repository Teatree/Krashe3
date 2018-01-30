package com.fd.etf.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fd.etf.Main;
import com.fd.etf.entity.componets.FlowerPublicComponent;
import com.fd.etf.entity.componets.VanityComponent;
import com.fd.etf.entity.componets.listeners.ShopPoverUpTabListener;
import com.fd.etf.utils.*;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.data.SceneVO;
import com.uwsoft.editor.renderer.data.SpriterVO;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.fd.etf.utils.BackgroundMusicMgr.backgroundMusicMgr;
import static com.fd.etf.utils.BackgroundMusicMgr.getBackgroundMusicMgr;
import static com.fd.etf.utils.GlobalConstants.BUTTON_TAG;
import static com.fd.etf.utils.SoundMgr.getSoundMgr;
import static com.fd.etf.utils.SoundMgr.soundMgr;

public class GameStage extends Stage {

    private static final String SHOP_SCENE = "ShopScene";
    private static final String RESULT_SCENE = "ResultScene";
    public static final String MAIN_SCENE = "MainScene";
    private static final String MENU_SCENE = "MenuScene";
    public static final String FLOWER_LEAFS_IDLE = "flower_leafs_idle";
    public static final String FLOWER_IDLE = "flower_idle";

    public static String currentScreen;

    public static Viewport viewport;
    public ETFSceneLoader sceneLoader;
    public static boolean changedFlowerEntity;
    public static boolean changedFlowerEntity2;
    public static boolean changedLeavesAni;
    public static boolean changedFlowerAni;

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

    public void initGame(int currentFlowerFrame) {
        currentScreen = "Game";
        GameScreenScript.currentFlowerFrame = currentFlowerFrame;
        sceneLoader.setScene(MAIN_SCENE, viewport);
        if (justCreated) {
            ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
            sceneLoader.addComponentsByTagName(BUTTON_TAG, ButtonComponent.class);
            root.addScript(gameScript);
            gameScript.initButtons();
            justCreated = false;
            gameScript.fpc.level.updateLevel(gameScript.fpc);
        }
        if (changedFlowerEntity || changedFlowerEntity2) {
            SceneVO sceneVO = sceneLoader.rm.getSceneVO(MAIN_SCENE);
            sceneLoader.rm.reloadFlowerAni(changedFlowerAni, changedLeavesAni);
            sceneVO.composite.updateSpriter(sceneVO.composite);
            changedFlowerEntity = false;
            reloadFlower(sceneVO, gameScript);
        }

        gameScript.fpc.settings.totalPlayedGames++;
        gameScript.reset();

        backgroundMusicMgr.stopMenu();
        backgroundMusicMgr.playGame();

        gameScript.initStartTrans();
        GameScreenScript.isPause.set(false);
        GameScreenScript.isGameOver.set(false);
        BugPool.getInstance(this);

        GameScreenScript.beesAngryFeedbackE.getComponent(TransformComponent.class).y = 413;
        GameScreenScript.beesAngryFeedbackE.getComponent(TransformComponent.class).x = -155;
    }

    public void initMenu() {
        currentScreen = "Menu";
        sceneLoader.setScene(MENU_SCENE, viewport);
        if (changedFlowerEntity || changedFlowerEntity2) {

            SceneVO sceneVO = sceneLoader.rm.getSceneVO(MENU_SCENE);
            sceneLoader.rm.reloadFlowerAni(changedFlowerAni, changedLeavesAni);

            changedFlowerEntity2 = false;
            reloadFlower(sceneVO, menuScript);
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

    private void initResult() {
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

        BackgroundMusicMgr.getBackgroundMusicMgr().playMenu();
        if(BackgroundMusicMgr.getBackgroundMusicMgr().musicMenu.isPlaying()){
            BackgroundMusicMgr.getBackgroundMusicMgr().musicMenu.setVolume(0.05f);
        }
        if(BackgroundMusicMgr.getBackgroundMusicMgr().musicGame.isPlaying()){
            BackgroundMusicMgr.getBackgroundMusicMgr().musicGame.setVolume(0.05f);
        }
    }

    public void initShop() {
        currentScreen = "Shop";
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
        soundMgr.stop(SoundMgr.SCORE_COUNT);

        System.gc();
        System.runFinalization();
        ShopScreenScript.btnPlay.getComponent(TransformComponent.class).y = -300;
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
//        try {
            sceneLoader.getEngine().update(Gdx.graphics.getDeltaTime());
//        }catch (Exception e){
//            System.err.println(e);
//        }
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
        gameScript.fpc.settings.totalPlayedGames = 0;
        gameScript.fpc.bestScore = 0;
        gameScript.fpc.totalScore = 0;
        gameScript.fpc.curDay = 0;
        gameScript.fpc.level.difficultyLevel = 0;
        gameScript.fpc.level.resetNewInfo();

        FlowerPublicComponent.currentPet = null;
//        gameScript.fpc.upgrades = new HashMap<>();

        gameScript.fpc.level.difficultyLevel = 0;
        gameScript.fpc.level.resetNewInfo();
        gameScript.fpc.level.goals = gameScript.fpc.level.goalGenerator.getGoals(gameScript.fpc);
        System.gc();
        System.runFinalization();
    }

    //TODO: RELOAD ONLY ONE ANIMATION
    private void reloadFlower(SceneVO sceneVO, IhaveFlower script) {

        Entity newFlower = null;
        Entity newLeaves = null;

        for (SpriterVO sVO : sceneVO.composite.sSpriterAnimations){
            if (sVO.animationName.equals(FLOWER_IDLE) && changedFlowerAni){
                script.getMegaFlower().getComponent(TransformComponent.class).x = -1000;
                script.getMegaFlower().getComponent(TransformComponent.class).y = -1000;
                sceneLoader.engine.removeEntity(script.getMegaFlower());

                newFlower = new Entity();
                sceneLoader.entityFactory.getSpriterComponentFactory()
                        .createComponents(sceneLoader.getRoot(), newFlower, sVO);
                sceneLoader.entityFactory.postProcessEntity(newFlower);
                newFlower.getComponent(ZIndexComponent.class).setZIndex(13);
                sceneLoader.getEngine().addEntity(newFlower);
                changedFlowerAni = (changedFlowerEntity || changedFlowerEntity2);
            }

            if (sVO.animationName.equals(FLOWER_LEAFS_IDLE) && changedLeavesAni){
                script.getMegaLeaves().getComponent(TransformComponent.class).x = -1000;
                script.getMegaLeaves().getComponent(TransformComponent.class).y = -1000;
                sceneLoader.engine.removeEntity(script.getMegaLeaves());

                newLeaves = new Entity();
                sceneLoader.entityFactory.getSpriterComponentFactory()
                        .createComponents(sceneLoader.getRoot(), newLeaves, sVO);
                sceneLoader.entityFactory.postProcessEntity(newLeaves);
                newLeaves.getComponent(ZIndexComponent.class).setZIndex(20);
                sceneLoader.getEngine().addEntity(newLeaves);
                changedLeavesAni =  (changedFlowerEntity || changedFlowerEntity2);
            }
        }

        script.initFlower(newFlower, newLeaves);
    }

    public interface IhaveFlower {
        void initFlower(Entity flower, Entity leaves);

        Entity getMegaFlower();

        Entity getMegaLeaves();
    }
}
