package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.data.CompositeVO;
import com.uwsoft.editor.renderer.resources.IResourceRetriever;
import com.uwsoft.editor.renderer.scene2d.CompositeActor;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

/**
 * Created by Teatree on 7/25/2015.
 */
public class MenuScreenScript implements IScript {

    private GameStage stage;
    private ItemWrapper menuItem;
    public IResourceRetriever ir;

    public MenuScreenScript(GameStage stage) {
        this.stage = stage;
        ir = stage.sceneLoader.getRm();
    }


    @Override
    public void init(Entity item) {
        menuItem = new ItemWrapper(item);
        stage.sceneLoader.addComponentsByTagName("button", ButtonComponent.class);
        Entity playBtn = menuItem.getChild("btn_settings").getEntity();

        final Entity btnShop = menuItem.getChild("btn_shop").getEntity();

        // Adding a Click listener to playButton so we can start game when clicked
        playBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener(){
            @Override
            public void touchUp() {
                System.out.println("Piiiip");
            }

            @Override
            public void touchDown() {
                stage.initGame();
            }

            @Override
            public void clicked() {

            }
        });

        btnShop.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {

            }

            @Override
            public void touchDown() {
                stage.initShopMenu();
            }

            @Override
            public void clicked() {

            }
        });
//        btnSettings.addListener(new ClickListener(){
//            // Need to keep touch down in order for touch up to work normal (libGDX awkwardness)
//            public boolean touchDown (InputEvent event, float x, float y, int
//                    pointer, int button) {
//                touchDownButton(btnSettings);
//                return true;
//            }
//            public void touchUp (InputEvent event, float x, float y, int pointer,
//                                 int button) {
//                touchUpButton(btnSettings);
//            }
//        });
//        btnNoAds.addListener(new ClickListener(){
//            // Need to keep touch down in order for touch up to work normal (libGDX awkwardness)
//            public boolean touchDown (InputEvent event, float x, float y, int
//                    pointer, int button) {
//                touchDownButton(btnNoAds);
//                return true;
//            }
//            public void touchUp (InputEvent event, float x, float y, int pointer,
//                                 int button) {
//                touchUpButton(btnNoAds);
//            }
//        });
//        btnShop.addListener(new ClickListener(){
//            // Need to keep touch down in order for touch up to work normal (libGDX awkwardness)
//            public boolean touchDown (InputEvent event, float x, float y, int
//                    pointer, int button) {
//                touchDownButton(btnShop);
//                stage.initShopMenu();
//
//                return true;
//            }
//            public void touchUp (InputEvent event, float x, float y, int pointer,
//                                 int button) {
//                touchUpButton(btnShop);
//            }
//        });
    }

    @Override
    public void dispose() {
//        SaveManager.saveProperties();
    }

    @Override
    public void act(float delta) {
//        stage.sceneLoader.getEngine().update(Gdx.graphics.getDeltaTime());
    }
}
