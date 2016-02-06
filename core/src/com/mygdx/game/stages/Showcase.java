package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.game.stages.GameScreenScript.fpc;
import static com.mygdx.game.stages.ResultScreenScript.showCaseVanity;
import static com.mygdx.game.stages.ResultScreenScript.show;
import static com.mygdx.game.stages.GameStage.sceneLoader;
import static com.mygdx.game.utils.EffectUtils.*;
import static com.mygdx.game.utils.GlobalConstants.*;


public class Showcase {

    public static final String PATH_PREFIX = "orig\\spriter_animations\\showcase_present_ani\\";
    public static final String TYPE_SUFFIX = ".png";
    public static final String ITEM_UNKNOWN_DEFAULT = "item_unknown";
    public static final String INTRO = "intro";
    public static final String SHOWCASE = "showcase";

    private ItemWrapper screenItem;
    private ResultScreenScript resultScreen;

    private Entity showcaseE;

    public TransformComponent tcShowCase;
    private TransformComponent tcItem;
    private Entity itemIcon;

    public Showcase(ItemWrapper resultScreenItem, ResultScreenScript resultScreen) {
        this.screenItem = resultScreenItem;
        this.resultScreen = resultScreen;

        showcaseE = screenItem.getChild(SHOWCASE).getEntity();

        initShowCaseBackButton();
        initShowCaseBuyButton();

        tcShowCase = showcaseE.getComponent(TransformComponent.class);
    }

    public void showFading() {

        NodeComponent nc = showcaseE.getComponent(NodeComponent.class);
        TintComponent tcp = showcaseE.getComponent(TintComponent.class);

        boolean appear = (tcp.color.a < 1 && show) || (tcp.color.a > 0 && !show);

        int fadeCoefficient = show ? 1 : -1;

//        if(fadeCoefficient == -1 && !nc.children.contains(itemIcon, false) && itemIcon!=null) {
//            screenItem.getChild(SHOWCASE).addChild(itemIcon);
//        }

        if (appear) {
            tcp.color.a += fadeCoefficient * 0.1f;
            fadeChildren(nc, fadeCoefficient);
            if (itemIcon != null && fadeCoefficient <0 )
            fadeChildren(itemIcon.getComponent(NodeComponent.class), fadeCoefficient);
        }
        hideWindow(tcp);

    }

    private void hideWindow(TintComponent ticParent) {
        if (!show && ticParent.color.a <= 0 && showcaseE != null) {
            if (itemIcon != null) {
                tcItem.x = FAR_FAR_AWAY_X;
                sceneLoader.getEngine().removeEntity(itemIcon);
                itemIcon = null;
                tcItem = null;
            }
            tcShowCase.x = FAR_FAR_AWAY_X;
        }
    }

    public void initShowCase() {

//        FileHandle newAsset = Gdx.files.internal(PATH_PREFIX + showCaseVanity.icon + TYPE_SUFFIX);
//        newAsset.copyTo(Gdx.files.local(PATH_PREFIX + ITEM_UNKNOWN_DEFAULT + TYPE_SUFFIX));

        Entity lbl_nameE = screenItem.getChild(SHOWCASE).getChild("lbl_item_name").getEntity();
        LabelComponent lc = lbl_nameE.getComponent(LabelComponent.class);
        lc.text.replace(0, lc.text.capacity(), "BUY " + showCaseVanity.name);

        Entity aniE = screenItem.getChild(SHOWCASE).getChild("showcase_ani").getEntity();

        SpriterComponent sc = ComponentRetriever.get(aniE, SpriterComponent.class);
        sc.animationName = INTRO;
        sc.player.speed = 6;
//        sc.player.time = 0;

        initShowCaseItem();

        tcShowCase.x = -25;
        tcShowCase.y = -35;
    }

    private void initShowCaseItem() {
        CompositeItemVO tempItemC = sceneLoader.loadVoFromLibrary(showCaseVanity.shopIcon).clone();
        itemIcon = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempItemC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), itemIcon, tempItemC.composite);
        sceneLoader.getEngine().addEntity(itemIcon);
        itemIcon.getComponent(ZIndexComponent.class).setZIndex(100);

        tcItem = itemIcon.getComponent(TransformComponent.class);
        tcItem.x = 445;
        tcItem.y = 350;
        tcItem.scaleX = 0.05f;
        tcItem.scaleY = 0.05f;
        itemIcon.getComponent(TintComponent.class).color.a = 0.0f;

        ActionComponent ac = new ActionComponent();
        Actions.checkInit();
        ac.dataArray.add(Actions.parallel(
                Actions.scaleTo(1.5f, 1.5f, 5, Interpolation.exp5Out),
                Actions.fadeIn(5, Interpolation.exp10Out)));
        System.out.println("alpha: " + itemIcon.getComponent(TintComponent.class).color.a);
        itemIcon.add(ac);
    }

    private void initShowCaseBackButton() {
        Entity backBtn = screenItem.getChild(SHOWCASE).getChild("btn_no").getEntity();
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
        Entity backBtn = screenItem.getChild(SHOWCASE).getChild("btn_buy").getEntity();
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
