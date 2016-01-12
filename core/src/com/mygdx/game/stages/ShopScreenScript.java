package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entity.componets.BugComponent;
import com.mygdx.game.entity.componets.FlowerPublicComponent;
import com.mygdx.game.entity.componets.VanityComponent;
import com.mygdx.game.utils.SaveMngr;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import javax.xml.transform.TransformerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Teatree on 7/25/2015.
 */
public class ShopScreenScript implements IScript {

    private GameStage stage;
    private ItemWrapper shopItem;
    public Entity scoreLbl;
    public Entity touchZone;
    public LabelComponent lc;

    public Vector2 tempGdx = new Vector2();
    public boolean isGdxWritten;
    public List<Entity> bags = new ArrayList<>();
    public List<Entity> itemIcons = new ArrayList<>();
    public ButtonComponent touchZoneBtn;
    private boolean began;
    private float time;
    private boolean complete;
    private float duration;
    private Interpolation interpolation;
    private boolean reverse;
    private float startX;
    private float endX;

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

        scoreLbl = shopItem.getChild("score_lbl").getEntity();
        lc = scoreLbl.getComponent(LabelComponent.class);

        touchZone = shopItem.getChild("touchZone_scroll").getEntity();

        touchZoneBtn = touchZone.getComponent(ButtonComponent.class);
    }

    private void getAllAllVanities(){
//        List<VanityComponent> vanityComponentList = ;
        int x = 173;
        int y = 289;

        for (final VanityComponent vc : GameScreenScript.fpc.vanities) {
            CompositeItemVO tempC = GameStage.sceneLoader.loadVoFromLibrary("btn_shop_icon_lib").clone();
            final Entity bagEntity = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempC);
            GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), bagEntity, tempC.composite);
            GameStage.sceneLoader.getEngine().addEntity(bagEntity);

            Entity itemIcon;
            if(!vc.bought) {
                CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary("item_unknown_n").clone();
                itemIcon = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
                GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), itemIcon, tempItemC.composite);
                GameStage.sceneLoader.getEngine().addEntity(itemIcon);
            }else{
                CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary(vc.shopIcon).clone();
                itemIcon = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
                GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), itemIcon, tempItemC.composite);
                GameStage.sceneLoader.getEngine().addEntity(itemIcon);
            }

            TransformComponent tc = bagEntity.getComponent(TransformComponent.class);
            tc.x = x;
            tc.y = y;

            itemIcon.add(new ButtonComponent());
            shopItem.getChild("btn_shop_icon_lib").addChild(itemIcon);
            TransformComponent tcb = itemIcon.getComponent(TransformComponent.class);
            tcb.x = tc.x;
            tcb.y = tc.y;

            bags.add(bagEntity);
            itemIcons.add(itemIcon);

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
        System.out.println("asd: " + touchZoneBtn.isTouched);
        if(touchZoneBtn.isTouched){
            if(!isGdxWritten) {
                tempGdx.x = Gdx.input.getX();
                isGdxWritten = true;
            }
            if(tempGdx.x > Gdx.input.getX()){
                int i = 0;
                while(i<bags.size()){
                    bags.get(i).getComponent(TransformComponent.class).x -= (tempGdx.x-Gdx.input.getX())/15;
                    tempGdx.x -= (tempGdx.x-Gdx.input.getX())/10;
                    itemIcons.get(i).getComponent(TransformComponent.class).x = bags.get(i).getComponent(TransformComponent.class).x;
                    i++;
                }
            }
            if(tempGdx.x < Gdx.input.getX()){

                int i = 0;
                while(i<bags.size()){
                    bags.get(i).getComponent(TransformComponent.class).x += (Gdx.input.getX()-tempGdx.x)/15;
                    tempGdx.x += (Gdx.input.getX()-tempGdx.x)/10;
                    itemIcons.get(i).getComponent(TransformComponent.class).x = bags.get(i).getComponent(TransformComponent.class).x;
                    i++;
                }
            }
        }else{
            isGdxWritten = false;
        }
//        stage.sceneLoader.getEngine().update(Gdx.graphics.getDeltaTime());
        lc.text.replace(0, lc.text.length(), String.valueOf(GameScreenScript.fpc.totalScore));
    }


}
