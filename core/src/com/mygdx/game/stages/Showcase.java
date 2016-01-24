package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.mygdx.game.entity.componets.VanityComponent;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.NodeComponent;
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
import static com.mygdx.game.stages.ResultScreenScript.show;

/**
 * Created by AnastasiiaRudyk on 1/24/2016.
 */
public class Showcase {

    public static final String PATH_PREFIX = "orig\\spriter_animations\\showcase_present_ani\\";
    public static final String TYPE_SUFFIX = ".png";
    public static final String ITEM_UNKNOWN_DEFAULT = "item_unknown";

    private ItemWrapper screenItem;
    private ResultScreenScript resultScreen;

    private Entity showcaseE;
//    private Entity bgE;
//    private Entity btn_noE;
//    private Entity btn_buyE;
    private Entity lbl_nameE;
    private Entity aniE;

    public TransformComponent tcShowCase;
    private TransformComponent tcItem;

    public Showcase(ItemWrapper resultScreenItem, ResultScreenScript resultScreen) {
        this.screenItem = resultScreenItem;
        this.resultScreen = resultScreen;

        showcaseE = screenItem.getChild("showcase").getEntity();;
//        bgE = screenItem.getChild("showcase").getChild("img_bg_show_case").getEntity();
//        btn_noE = screenItem.getChild("showcase").getChild("btn_no").getEntity();
//        btn_buyE = screenItem.getChild("showcase").getChild("btn_buy").getEntity();

        lbl_nameE = screenItem.getChild("showcase").getChild("lbl_item_name").getEntity();
        LabelComponent lc = lbl_nameE.getComponent(LabelComponent.class);
        lc.text.replace(0, lc.text.capacity(), "BUY " + showCaseVanity.name);

        aniE = screenItem.getChild("showcase").getChild("showcase_ani").getEntity();
        SpriterComponent sc = ComponentRetriever.get(aniE, SpriterComponent.class);
        sc.animationName = "intro";
        sc.player.speed = 12;
        sc.player.time = 0;

        initShowCaseBackButton();
        initShowCaseBuyButton();

        tcItem = aniE.getComponent(TransformComponent.class);
        tcShowCase = showcaseE.getComponent(TransformComponent.class);
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
                ResultScreenScript.isWasShowcase = true;
                show = false;
                resultScreen.initResultScreen();
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
                ResultScreenScript.isWasShowcase = true;
                show = false;
                resultScreen.initResultScreen();
            }
        });
    }

    private void fadeChildren(NodeComponent nc, int fadeCoefficient) {
        if (nc != null && nc.children != null && nc.children.size != 0) {
            for (Entity e : nc.children) {
                TintComponent tc = e.getComponent(TintComponent.class);
                tc.color.a += fadeCoefficient * 0.1f;
                fadeChildren(e.getComponent(NodeComponent.class), fadeCoefficient);
            }
        }
    }

    public void showFading() {

        NodeComponent nc = showcaseE.getComponent(NodeComponent.class);
        TintComponent tcp = showcaseE.getComponent(TintComponent.class);

        boolean appear = (tcp.color.a < 1 && show) || (tcp.color.a > 0 && !show);

        int fadeCoefficient = show ? 1 : -1;

        if (appear) {
            tcp.color.a += fadeCoefficient * 0.1f;
            fadeChildren(nc, fadeCoefficient);
        }

        hidePreview(tcp);
    }

    private void hidePreview(TintComponent ticParent) {
        if (show && ticParent.color.a <= 0 && showcaseE != null) {
            TransformComponent previewTc = showcaseE.getComponent(TransformComponent.class);
            previewTc.x = -1500;
//            showCaseVanity = null;
            if (aniE != null) {
                tcItem.x = -1500;
            }
            if (showcaseE != null) {
                tcShowCase.x = -1500;
            }
        }
    }

    public void initShowCase() {
        tcShowCase.x = -25;
        tcShowCase.y = -35;

        tcItem.x = 484;
        tcItem.y = 407;
    }
}
