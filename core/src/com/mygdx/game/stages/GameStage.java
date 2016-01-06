package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.entity.componets.VanityComponent;
import com.mygdx.game.utils.BugPool;
import com.mygdx.game.utils.GlobalConstants;
import com.mygdx.game.utils.SaveMngr;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.List;

import static com.mygdx.game.utils.SoundMgr.*;
import static com.mygdx.game.utils.BackgroundMusicMgr.*;

/**
 * Created by Teatree on 5/25/2015.
 */
public class GameStage extends Stage{

    public static Viewport viewport;
    public static SceneLoader sceneLoader;
    private GameScreenScript gameScript;

    public GameStage getInstance() {
        return this;
    }

    public GameStage() {
        sceneLoader = new SceneLoader();
        viewport = new FillViewport(1200, 786);
        getSoundMgr();
        getBackgroundMusicMgr();
        initMenu();
    }

    public void initGame() {
        sceneLoader = new SceneLoader();
        sceneLoader.loadScene("MainScene", viewport);
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
        sceneLoader = new SceneLoader();
        sceneLoader.loadScene("MenuScene", viewport);
        MenuScreenScript menu = new MenuScreenScript(this);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        root.addScript(menu);
        GlobalConstants.CUR_SCREEN = "MENU";


//        backgroundMusicMgrInst.play();
//
//        GameScreenScript.isAngeredBeesMode = false;
    }
    public void initResult(){
        sceneLoader = new SceneLoader();
        sceneLoader.loadScene("ResultScene", viewport);
        ResultScreenScript result = new ResultScreenScript(this);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        root.addScript(result);
        GlobalConstants.CUR_SCREEN = "RESULT";
    }

//    public void initShowcase(){
//        sceneLoader = new SceneLoader();
//        sceneLoader.loadScene("ShowcaseScene", viewport);
//        ShowcaseScreenScript result = new ShowcaseScreenScript(this);
//        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
//        root.addScript(result);
//        GlobalConstants.CUR_SCREEN = "SHOW";
//    }

    public void initShopMenu(){
        sceneLoader = new SceneLoader();
        sceneLoader.loadScene("ShopScene", viewport);
        ShopScreenScript shop = new ShopScreenScript(this);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        root.addScript(shop);
        GlobalConstants.CUR_SCREEN = "SHOP";
    }

    public void update() {
        sceneLoader.getEngine().update(Gdx.graphics.getDeltaTime());
//        System.out.println(sceneLoader.getEngine());
    }

    public void removeActor(Entity item) {
        for (Actor actor : this.getActors()) {
            if (actor.equals(item)) {
                actor.remove();
            }
        }
    }

}
