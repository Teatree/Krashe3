package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.game.utils.GlobalConstants.BUTTON_TAG;

public class MenuScreenScript implements IScript {

    public static final String BTN_PLAY = "btn_play";
    public static final String BTN_SHOP = "btn_shop";
    public static final String BTN_NO_ADS = "btn_noAds";
    private GameStage stage;

    public MenuScreenScript(GameStage stage) {
        this.stage = stage;
    }


    @Override
    public void init(Entity item) {
        ItemWrapper menuItem = new ItemWrapper(item);

        GameStage.sceneLoader.addComponentsByTagName(BUTTON_TAG, ButtonComponent.class);
        final Entity playBtn = menuItem.getChild(BTN_PLAY).getEntity();
        final Entity btnShop = menuItem.getChild(BTN_SHOP).getEntity();
        final Entity btnNoAds = menuItem.getChild(BTN_NO_ADS).getEntity();

        btnNoAds.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
            }

            @Override
            public void touchDown() {
            }

            @Override
            public void clicked() {
                GameScreenScript.fpc.settings.noAds = true;
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
                stage.initShop();
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
