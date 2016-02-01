//package com.mygdx.game.stages;
//
//import com.badlogic.ashley.core.Entity;
//import com.uwsoft.editor.renderer.components.LayerMapComponent;
//import com.uwsoft.editor.renderer.components.TintComponent;
//import com.uwsoft.editor.renderer.components.TransformComponent;
//import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
//import com.uwsoft.editor.renderer.components.label.LabelComponent;
//import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
//import com.uwsoft.editor.renderer.scripts.IScript;
//import com.uwsoft.editor.renderer.utils.ComponentRetriever;
//import com.uwsoft.editor.renderer.utils.ItemWrapper;
//
//import static com.mygdx.game.stages.GameScreenScript.fpc;
//import static com.mygdx.game.stages.ResultScreenScript.showCaseVanity;
//
//
//public class ShowcaseScreenScript implements IScript {
//
//    public static final String PATH_PREFIX = "orig\\spriter_animations\\showcase_present_ani\\";
//    public static final String TYPE_SUFFIX = ".png";
//    public static final String ITEM_UNKNOWN_DEFAULT = "item_unknown";
//
//    private Entity showcaseE;
//    private Entity bgE;
//    private GameStage stage;
//    private ItemWrapper screenItem;
//    private Entity btn_noE;
//    private Entity btn_buyE;
//    private Entity btn_lblE;
//    private Entity aniE;
//
//    //spawn == 0 -> stable
//    //spawn > 0 -> appearing
//    //spawn < 0 -> disappearing
//    private int spawn;
//
//    public ShowcaseScreenScript(GameStage stage) {
//        this.stage = stage;
//    }
//
//    @Override
//    public void init(Entity entity) {
//        spawn = 1;
//        screenItem = new ItemWrapper(entity);
//        stage.sceneLoader.addComponentsByTagName("button", ButtonComponent.class);
//
//        showcaseE = screenItem.getChild("showcase").getEntity();
//
//        bgE = screenItem.getChild("showcase").getChild("img_bg_show_case").getEntity();
//        TintComponent tic = bgE.getComponent(TintComponent.class);
//        tic.color.a = 0f;
//
//        btn_noE = screenItem.getChild("showcase").getChild("btn_no").getChild("img_n").getEntity();
//        TintComponent ticNo = btn_noE.getComponent(TintComponent.class);
//        ticNo.color.a = 0f;
//
//        btn_buyE = screenItem.getChild("showcase").getChild("btn_buy").getChild("img_n").getEntity();
//        TintComponent ticBuy = btn_buyE.getComponent(TintComponent.class);
//        ticBuy.color.a = 0f;
//
//        btn_lblE = screenItem.getChild("showcase").getChild("lbl_item_name").getEntity();
//        TintComponent ticLbl = btn_lblE.getComponent(TintComponent.class);
//        ticLbl.color.a = 0f;
//        LabelComponent lc = btn_lblE.getComponent(LabelComponent.class);
//        lc.text.replace(0, lc.text.capacity(), "BUY " + showCaseVanity.name);
//
//        aniE = screenItem.getChild("showcase").getChild("showcase_ani").getEntity();
//        SpriterComponent sc = ComponentRetriever.get(aniE, SpriterComponent.class);
//        sc.animationName = "intro";
//        sc.player.speed = 12;
//        sc.player.time = 0;
//
//        initShowCaseBackButton();
//        initShowCaseBuyButton();
//
//        TransformComponent tc = showcaseE.getComponent(TransformComponent.class);
//        tc.x = -25;
//        tc.y = -35;
//
//    }
//
//    private void initShowCaseBackButton() {
//        Entity backBtn = screenItem.getChild("showcase").getChild("btn_no").getEntity();
//        final LayerMapComponent lc = ComponentRetriever.get(backBtn, LayerMapComponent.class);
//        backBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
//            @Override
//            public void touchUp() {
//                lc.getLayer("normal").isVisible = true;
//                lc.getLayer("pressed").isVisible = false;
//            }
//
//            @Override
//            public void touchDown() {
//                lc.getLayer("normal").isVisible = false;
//                lc.getLayer("pressed").isVisible = true;
//            }
//
//            @Override
//            public void clicked() {
//                spawn = -1;
//                ResultScreenScript.isWasShowcase = true;
//                stage.initResult();
//            }
//        });
//    }
//
//    private void initShowCaseBuyButton() {
//        Entity backBtn = screenItem.getChild("showcase").getChild("btn_buy").getEntity();
//        final LayerMapComponent lc = ComponentRetriever.get(backBtn, LayerMapComponent.class);
//        backBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
//            @Override
//            public void touchUp() {
//                lc.getLayer("normal").isVisible = true;
//                lc.getLayer("pressed").isVisible = false;
//
//            }
//
//            @Override
//            public void touchDown() {
//                lc.getLayer("normal").isVisible = false;
//                lc.getLayer("pressed").isVisible = true;
//            }
//
//            @Override
//            public void clicked() {
//                showCaseVanity.buyAndUse(fpc);
//                ResultScreenScript.isWasShowcase = true;
//                stage.initResult();
//                spawn = -1;
//            }
//        });
//    }
//
//    private void showFadeIn() {
//        TintComponent tic = bgE.getComponent(TintComponent.class);
//        TintComponent ticNo = btn_noE.getComponent(TintComponent.class);
//        TintComponent ticBuy = btn_buyE.getComponent(TintComponent.class);
//        TintComponent ticLbl = btn_lblE.getComponent(TintComponent.class);
//
//        boolean shouldChange = (ticLbl.color.a < 1 && spawn > 0) ||
//                (ticLbl.color.a > 0 && spawn < 0);
//
//        int fadeCoefficient = spawn;
//
//        if (spawn < 0 && ticLbl.color.a <= 0.5) {
//            spawn = 0;
//            stage.initResult();
//        }
//
//        if (shouldChange) {
//            tic.color.a += fadeCoefficient * 0.1f;
//            ticNo.color.a += fadeCoefficient * 0.1f;
//            ticBuy.color.a += fadeCoefficient * 0.1f;
//            ticLbl.color.a += fadeCoefficient * 0.1f;
//        }
//        if (tic.color.a >= 0.9f) {
//            aniE = screenItem.getChild("showcase").getChild("showcase_ani").getEntity();
//            SpriterComponent sc = ComponentRetriever.get(aniE, SpriterComponent.class);
//            sc.player.speed = 12;
//            if (sc.player.time >= 225) {
//                sc.player.setAnimation(1);
//            }
//        }
//        if (spawn > 0 && ticLbl.color.a >= 1) {
//            spawn = 0;
//        }
//
//        TransformComponent tc = showcaseE.getComponent(TransformComponent.class);
//        tc.x = -25;
//        tc.y = -35;
//    }
//
//    @Override
//    public void act(float delta) {
//        showFadeIn();
//    }
//
//    @Override
//    public void dispose() {
//        showCaseVanity = null;
//    }
//}
