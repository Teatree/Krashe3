package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.utils.GlobalConstants;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.resources.IResourceRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

/**
 * Created by Teatree on 5/25/2015.
 */
public class GameStage extends Stage{

//    public GameScreenScript game;
    public Viewport viewport;
    public SceneLoader sceneLoader;

    public GameStage getInstance() {
        return this;
    }

    public GameStage() {
        sceneLoader = new SceneLoader();
        viewport = new FillViewport(1200, 786);

        initMenu();
    }

    public void initGame() {
        sceneLoader = new SceneLoader();
        sceneLoader.loadScene("MainScene", viewport);
//        GameScreenScript game = new GameScreenScript(this);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
//        root.addScript(game);
//        Flower.init(this, sceneLoader);
        GlobalConstants.CUR_SCREEN = "GAME";
    }

    public void initMenu(){
        sceneLoader = new SceneLoader();
        sceneLoader.loadScene("MenuScene", viewport);
        MenuScreenScript menu = new MenuScreenScript(this);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        root.addScript(menu);
        GlobalConstants.CUR_SCREEN = "MENU";
//
//        GameScreenScript.isAngeredBeesMode = false;
    }

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
