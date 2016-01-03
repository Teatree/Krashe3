package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.mygdx.game.entity.componets.VanityComponent;
import com.mygdx.game.utils.SaveMngr;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import javax.xml.transform.TransformerFactory;
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

        stage.sceneLoader.addComponentsByTagName("button", ButtonComponent.class);

        getAllAllVanities();

        addBackButtonPlease();

    }

    private void getAllAllVanities(){
        List<VanityComponent> vanityComponentList = SaveMngr.getAllVanity();
        int x = 173;
        int y = 289;

        for (final VanityComponent vc : vanityComponentList) {


            CompositeItemVO tempC = GameStage.sceneLoader.loadVoFromLibrary("btn_shop_icon_lib").clone();
            final Entity bagEntity = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempC);
            GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), bagEntity, tempC.composite);
            GameStage.sceneLoader.getEngine().addEntity(bagEntity);

            CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary("item_white_beard_n").clone();
            final Entity beardE = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
            GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), beardE, tempItemC.composite);
            GameStage.sceneLoader.getEngine().addEntity(beardE);

            TransformComponent tc = bagEntity.getComponent(TransformComponent.class);
            tc.x = x;
            tc.y = y;

            beardE.add(new ButtonComponent());
            shopItem.getChild("btn_shop_icon_lib").addChild(beardE);
            TransformComponent tcb = beardE.getComponent(TransformComponent.class);
            tcb.x = tc.x;
            tcb.y = tc.y;

            bagEntity.add(new ButtonComponent());
            bagEntity.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
                @Override
                public void touchUp() {

                }

                @Override
                public void touchDown() {

                }

                @Override
                public void clicked() {
//                    System.out.println(vc.icon);
                    vc.apply(GameScreenScript.fpc);
                }
            });
            x += 250;
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
