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



        getAllVanities2();

        addBackButton();


    }

    private void addBackButton() {
        stage.sceneLoader.addComponentsByTagName("button", ButtonComponent.class);
        Entity backBtn = shopItem.getChild("btn_back").getEntity();
        final LayerMapComponent lc = ComponentRetriever.get(backBtn, LayerMapComponent.class);
        backBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
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

//                List<VanityComponent> vanityComponentList = SaveMngr.getAllVanity();
//                System.out.println(vanityComponentList.get(1).icon);
//                vanityComponentList.get(1).apply(GameScreenScript.fpc);
            }

            @Override
            public void clicked() {
                stage.initMenu();

            }
        });
    }

    private void getAllVanities2() {
        List<VanityComponent> vanityComponentList = SaveMngr.getAllVanity();



        for (final VanityComponent vc : vanityComponentList) {

            Entity entity = shopItem.getChild(vc.icon).getEntity();

            final LayerMapComponent lc2 = ComponentRetriever.get(entity, LayerMapComponent.class);
            lc2.getLayer("LockedN").isVisible = false;
            lc2.getLayer("LockedP").isVisible = false;
            lc2.getLayer("Default").isVisible = true;
            lc2.getLayer("UnlockedN").isVisible = false;
            lc2.getLayer("UnlockedP").isVisible = false;

            entity.add(new ButtonComponent());
            entity.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
                @Override
                public void touchUp() {
                    lc2.getLayer("LockedN").isVisible = false;
                    lc2.getLayer("LockedP").isVisible = false;
                    lc2.getLayer("Default").isVisible = true;
                    lc2.getLayer("UnlockedN").isVisible = false;
                    lc2.getLayer("UnlockedP").isVisible = false;
                }

                @Override
                public void touchDown() {

                    lc2.getLayer("LockedN").isVisible = false;
                    lc2.getLayer("LockedP").isVisible = false;
                    lc2.getLayer("Default").isVisible = false;
                    lc2.getLayer("UnlockedN").isVisible = false;
                    lc2.getLayer("UnlockedP").isVisible = false;

                }

                @Override
                public void clicked() {
                    System.out.println(vc.icon);
                    vc.apply(GameScreenScript.fpc);

                }
            });
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    public void act(float delta) {
//        stage.sceneLoader.getEngine().update(Gdx.graphics.getDeltaTime());
    }

}
