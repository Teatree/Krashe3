package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

/**
 * Created by Teatree on 7/25/2015.
 */
public class ShopScreenScript implements IScript {

    private GameStage stage;
    private ItemWrapper shopItem;
    private SpriteAnimationComponent spriteAnimationComponent;

    public ShopScreenScript(GameStage stage) {
        this.stage = stage;
    }

    @Override
    public void init(Entity item) {
        shopItem = new ItemWrapper(item);

        stage.sceneLoader.addComponentsByTagName("button", ButtonComponent.class);
        Entity playBtn = shopItem.getChild("btn_shop").getEntity();
        spriteAnimationComponent = stage.sceneLoader.loadFromLibrary("chargerAni").getComponent(SpriteAnimationComponent.class);
//        final Entity btnSettings = menuItem.getCompositeById("btn_settings");
//        final Entity btnNoAds = menuItem.getCompositeById("btn_noAds");
//        final Entity btnShop = menuItem.getChild("btn_shop").getEntity();

        // Adding a Click listener to playButton so we can start game when clicked
        playBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
//                spriteAnimationComponent.playMode
                System.out.println(spriteAnimationComponent.playMode);
            }

            @Override
            public void touchDown() {
                System.out.println("Poop");
            }

            @Override
            public void clicked() {

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
