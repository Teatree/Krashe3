package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.mygdx.game.system.ParticleLifespanSystem;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.resources.IResourceRetriever;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

/**
 * Created by Teatree on 7/25/2015.
 */
public class MenuScreenScript implements IScript {

    private GameStage stage;
    public IResourceRetriever ir;

    public MenuScreenScript(GameStage stage) {
        this.stage = stage;
        ir = GameStage.sceneLoader.getRm();
    }


    @Override
    public void init(Entity item) {
        ItemWrapper menuItem = new ItemWrapper(item);

        GameStage.sceneLoader.addComponentsByTagName("button", ButtonComponent.class);
        final Entity playBtn = menuItem.getChild("btn_play").getEntity();
        final Entity btnShop = menuItem.getChild("btn_shop").getEntity();
        final Entity btnNoAds = menuItem.getChild("btn_noAds").getEntity();

        btnNoAds.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
            }

            @Override
            public void touchDown() {
            }

            @Override
            public void clicked() {
                GameScreenScript.fpc.noAds = true;
            }
        });
        playBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener(){

            @Override
            public void touchUp() {}

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
