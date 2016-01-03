package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.mygdx.game.entity.componets.VanityComponent;
import com.mygdx.game.utils.SaveMngr;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.List;

/**
 * Created by Teatree on 7/25/2015.
 */
public class ShopScreenScript implements IScript {

    private GameStage stage;
    private ItemWrapper shopItem;
//    private int spawnCounter = 0;


    public ShopScreenScript(GameStage stage) {
        this.stage = stage;
    }

    @Override
    public void init(Entity item) {
        shopItem = new ItemWrapper(item);

//        getAllVanities2();
        stage.sceneLoader.addComponentsByTagName("button", ButtonComponent.class);
        getAllVanities();

        addBackButtonPlease();
    }

    private void getAllVanities() {
//        Entity backBtn = shopItem.getChild("btn_shop_item_1").getEntity();
        List<VanityComponent> vanityComponentList = SaveMngr.getAllVanity();

        for (final VanityComponent vc : vanityComponentList) {

            Entity btn = shopItem.getChild(vc.icon).getEntity();

            final LayerMapComponent lc = ComponentRetriever.get(btn, LayerMapComponent.class);
            btn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
                @Override
                public void touchUp() {
                    lc.getLayer("normal").isVisible = true;
                    lc.getLayer("Default").isVisible = false;
                    lc.getLayer("pressed").isVisible = false;
                }

                @Override
                public void touchDown() {
                    lc.getLayer("normal").isVisible = false;
                    lc.getLayer("Default").isVisible = false;
                    lc.getLayer("pressed").isVisible = true;
                }

                @Override
                public void clicked() {
                    System.out.println(vc.icon);
                    vc.apply(GameScreenScript.fpc);

                }
            });
        }
    }

    private void addBackButtonPlease(){
        Entity btnBack = shopItem.getChild("btn_back").getEntity();
        final LayerMapComponent lc = ComponentRetriever.get(btnBack, LayerMapComponent.class);
        btnBack.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
                lc.getLayer("normal").isVisible = true;
                lc.getLayer("Default").isVisible = false;
                lc.getLayer("pressed").isVisible = false;
            }

            @Override
            public void touchDown() {
                lc.getLayer("normal").isVisible = false;
                lc.getLayer("Default").isVisible = false;
                lc.getLayer("pressed").isVisible = true;
            }

            @Override
            public void clicked() {
                stage.initMenu();
            }
        });
    }

    @Override
    public void dispose() {

    }

    @Override
    public void act(float delta) {
//        stage.sceneLoader.getEngine().update(Gdx.graphics.getDeltaTime());
    }

}
