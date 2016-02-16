package com.mygdx.game.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.utils.BugPool;
import com.mygdx.game.utils.GlobalConstants;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.game.utils.BackgroundMusicMgr.backgroundMusicMgr;
import static com.mygdx.game.utils.BackgroundMusicMgr.getBackgroundMusicMgr;
import static com.mygdx.game.utils.SoundMgr.getSoundMgr;

/**
 * Created by Teatree on 5/25/2015.
 */
public class GameStage extends Stage{

    public static Viewport viewport;
    public static SceneLoader sceneLoader;
    private GameScreenScript gameScript;

    public GameStage() {
        sceneLoader = new SceneLoader();
        viewport = new FillViewport(1200, 786);
        getSoundMgr();
        getBackgroundMusicMgr();
        initMenu();
    }

    public GameStage getInstance() {
        return this;
    }

    public void initGame() {
        sceneLoader.loadScene("MainScene", viewport);
        sceneLoader.addComponentsByTagName("button", ButtonComponent.class);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        if (gameScript == null){
            gameScript = new GameScreenScript(this);
        }
        root.addScript(gameScript);
        GlobalConstants.CUR_SCREEN = "GAME";
        backgroundMusicMgr.stop();

        GameScreenScript.isStarted = false;
        BugPool.resetBugPool();
    }

    public void initMenu(){
        sceneLoader.loadScene("MenuScene", viewport);
        MenuScreenScript menu = new MenuScreenScript(this);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        root.addScript(menu);
        GlobalConstants.CUR_SCREEN = "MENU";
    }
    public void initResult(){
        sceneLoader.loadScene("ResultScene", viewport);
        ResultScreenScript result = new ResultScreenScript(this);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        root.addScript(result);
        GlobalConstants.CUR_SCREEN = "RESULT";
    }

    public void initShopMenu(){
        sceneLoader.loadScene("ShopScene", viewport);
        ShopScreenScript shop = new ShopScreenScript(this);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        root.addScript(shop);
        GlobalConstants.CUR_SCREEN = "SHOP";
    }

    public void update() {
        sceneLoader.getEngine().update(Gdx.graphics.getDeltaTime());
    }

}
