package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mygdx.game.entity.componets.VanityComponent;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.game.stages.GameScreenScript.fpc;
import static com.mygdx.game.stages.ResultScreenScript.showCaseVanity;
import static com.mygdx.game.stages.ResultScreenScript.show;
import static com.mygdx.game.stages.GameStage.sceneLoader;

/**
 * Created by AnastasiiaRudyk on 1/24/2016.
 */
public class Showcase {

    public static final String PATH_PREFIX = "orig\\spriter_animations\\showcase_present_ani\\";
    public static final String TYPE_SUFFIX = ".png";
    public static final String ITEM_UNKNOWN_DEFAULT = "item_unknown";
    public static final String INTRO = "intro";

    private ItemWrapper screenItem;
    private ResultScreenScript resultScreen;

    private Entity showcaseE;

    public TransformComponent tcShowCase;
    private TransformComponent tcItem;
    private Entity itemIcon;

    public Showcase(ItemWrapper resultScreenItem, ResultScreenScript resultScreen) {
        this.screenItem = resultScreenItem;
        this.resultScreen = resultScreen;

        showcaseE = screenItem.getChild("showcase").getEntity();

        Entity lbl_nameE = screenItem.getChild("showcase").getChild("lbl_item_name").getEntity();
        LabelComponent lc = lbl_nameE.getComponent(LabelComponent.class);
        lc.text.replace(0, lc.text.capacity(), "BUY " + showCaseVanity.name);

        initShowCaseBackButton();
        initShowCaseBuyButton();

        tcShowCase = showcaseE.getComponent(TransformComponent.class);
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
        if (!show && ticParent.color.a <= 0 && showcaseE != null) {
            if (itemIcon != null) {
                sceneLoader.getEngine().removeEntity(itemIcon);
                itemIcon = null;
                tcItem = null;
            }
            tcShowCase.x = -1500;
        }
    }

    public void initShowCase() {

//        FileHandle newAsset = Gdx.files.internal(PATH_PREFIX + showCaseVanity.icon + TYPE_SUFFIX);
//        newAsset.copyTo(Gdx.files.local(PATH_PREFIX + ITEM_UNKNOWN_DEFAULT + TYPE_SUFFIX));
        System.out.println("showcase init : " + showCaseVanity.name + " asset " + showCaseVanity.icon);
        Entity aniE = screenItem.getChild("showcase").getChild("showcase_ani").getEntity();

        SpriterComponent sc = ComponentRetriever.get(aniE, SpriterComponent.class);
        sc.animationName = INTRO;
        sc.player.speed = 6;
        sc.player.time = 0;

//        tcItem = aniE.getComponent(TransformComponent.class);
//        tcItem.x = 484;
//        tcItem.y = 407;

        CompositeItemVO tempItemC = sceneLoader.loadVoFromLibrary(showCaseVanity.shopIcon).clone();
        itemIcon = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempItemC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), itemIcon, tempItemC.composite);
        sceneLoader.getEngine().addEntity(itemIcon);
        itemIcon.getComponent(ZIndexComponent.class).setZIndex(100);
//        screenItem.getChild("showcase").addChild(itemIcon);

        tcItem = itemIcon.getComponent(TransformComponent.class);
        tcItem.x = 480;
        tcItem.y = 395;

        tcShowCase.x = -25;
        tcShowCase.y = -35;
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
}
