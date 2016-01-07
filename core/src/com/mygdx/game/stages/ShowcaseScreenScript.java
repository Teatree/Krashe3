package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.game.stages.GameScreenScript.fpc;
import static com.mygdx.game.stages.ResultScreenScript.showCaseVanity;

/**
 * Created by Teatree on 01/05/2016.
 */
public class ShowcaseScreenScript implements IScript {

    public static final String PATH_PREFIX = "orig\\spriter_animations\\showcase_present_ani\\";
    public static final String TYPE_SUFFIX = ".png";
    public static final String ITEM_UNKNOWN_DEFAULT = "item_unknown";


    private GameStage stage;
    private ItemWrapper screenItem;

    public ShowcaseScreenScript(GameStage stage) {
        this.stage = stage;
    }

    @Override
    public void init(Entity entity) {
        screenItem = new ItemWrapper(entity);
        stage.sceneLoader.addComponentsByTagName("button", ButtonComponent.class);

        Entity showcaseE = screenItem.getChild("showcase").getEntity();

        Entity bgE = screenItem.getChild("showcase").getChild("img_bg_show_case").getEntity();
        TintComponent tic = bgE.getComponent(TintComponent.class);
//        tic.color.a = 0f;

        Entity btn_noE = screenItem.getChild("showcase").getChild("btn_no").getChild("img_n").getEntity();
        TintComponent ticNo = btn_noE.getComponent(TintComponent.class);
//        ticNo.color.a = 0f;

        Entity btn_buyE = screenItem.getChild("showcase").getChild("btn_buy").getChild("img_n").getEntity();
        TintComponent ticBuy = btn_buyE.getComponent(TintComponent.class);
//        ticBuy.color.a = 0f;

        Entity btn_lblE = screenItem.getChild("showcase").getChild("lbl_item_name").getEntity();
        TintComponent ticLbl = btn_lblE.getComponent(TintComponent.class);
//        ticLbl.color.a = 0f;
        LabelComponent lc = btn_lblE.getComponent(LabelComponent.class);
        lc.text.replace(0, lc.text.capacity(), "BUY " + showCaseVanity.name);

        Entity aniE = screenItem.getChild("showcase").getChild("showcase_ani").getEntity();
        SpriterComponent sc = ComponentRetriever.get(aniE, SpriterComponent.class);
        sc.animationName = "intro";
        sc.player.speed = 12;
//        sc.player.time = 0;

        initShowCaseBackButton();
        initShowCaseBuyButton();

        TransformComponent tc = showcaseE.getComponent(TransformComponent.class);
        tc.x = -25;
        tc.y = -35;
    }

    private void initShowCaseBackButton() {
        Entity backBtn = screenItem.getChild("showcase").getChild("btn_no").getEntity();
        final LayerMapComponent lc = ComponentRetriever.get(backBtn, LayerMapComponent.class);
        backBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
                lc.getLayer("normal").isVisible = true;
                lc.getLayer("pressed").isVisible = false;
            }

            @Override
            public void touchDown() {
                lc.getLayer("normal").isVisible = false;
                lc.getLayer("pressed").isVisible = true;
            }

            @Override
            public void clicked() {
                stage.initResult();
            }
        });
    }

    private void initShowCaseBuyButton() {
        Entity backBtn = screenItem.getChild("showcase").getChild("btn_buy").getEntity();
        final LayerMapComponent lc = ComponentRetriever.get(backBtn, LayerMapComponent.class);
        backBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
                lc.getLayer("normal").isVisible = true;
                lc.getLayer("pressed").isVisible = false;
            }

            @Override
            public void touchDown() {
                lc.getLayer("normal").isVisible = false;
                lc.getLayer("pressed").isVisible = true;
            }

            @Override
            public void clicked() {
                showCaseVanity.buyAndUse(fpc);
                stage.initResult();
            }
        });
    }

    @Override
    public void act(float delta) {

    }

    @Override
    public void dispose() {

    }
}
