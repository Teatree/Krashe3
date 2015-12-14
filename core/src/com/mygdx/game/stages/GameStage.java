package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.utils.GlobalConstants;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.game.utils.SoundMgr.*;
import static com.mygdx.game.utils.BackgroundMusicMgr.*;

/**
 * Created by Teatree on 5/25/2015.
 */
public class GameStage extends Stage{

//    public GameScreenScript game;
    public static Viewport viewport;
    public static SceneLoader sceneLoader;

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
        GameScreenScript game = new GameScreenScript();
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        root.addScript(game);
//        Flower.init(this, sceneLoader);
        GlobalConstants.CUR_SCREEN = "GAME";

        backgroundMusicMgr.stop();
//        soundMgr.play("tuturu");

//        //init Flower
//        CompositeItemVO tempC = sceneLoader.loadVoFromLibrary("flowerLib");
////        LayerItemVO tempL = tempC.
//        Entity flowerEntity = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempC);
//        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), flowerEntity, tempC.composite);
//        sceneLoader.getEngine().addEntity(flowerEntity);
//
//        TransformComponent tc = new TransformComponent();
//        tc.x = 300;
//        tc.y = -400;
//        tc.scaleX = 0.6f;
//        tc.scaleY = 0.6f;
//        flowerEntity.add(tc);
//
//        LayerMapComponent lc = ComponentRetriever.get(flowerEntity, LayerMapComponent.class);
//        lc.setLayers(tempC.composite.layers);
//        flowerEntity.add(lc);
//
//        LayerItemVO tempL = lc.getLayer("Layer1");
//
////                tempC.composite.layers.get(0).isVisible = false;
//
//        SpriteAnimationComponent spriteAnimationComponent = new SpriteAnimationComponent();
////        SpriteAnimationStateComponent spriteAnimationStateComponent = new SpriteAnimationStateComponent();
//        sceneLoader.getEngine().addSystem(new LayerSystem());
//
//        tempL.isVisible = false;
//        System.out.println(tempL.isVisible + " IS THE NAME");
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
