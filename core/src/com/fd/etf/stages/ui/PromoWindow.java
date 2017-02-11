package com.fd.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.fd.etf.entity.componets.ShopItem;
import com.fd.etf.entity.componets.listeners.ImageButtonListener;
import com.fd.etf.stages.GameStage;
import com.fd.etf.stages.ResultScreenScript;
import com.fd.etf.stages.ShopScreenScript;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.fd.etf.stages.ShopScreenScript.itemIcons;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_Y;

/**
 * Created by ARudyk on 7/4/2016.
 */
public class PromoWindow extends AbstractDialog {
    
    private static final String PROMO_WINDOW = "promo_lib";
    private static final String BUY_DISC_BTN = "buy_disc_btn";
    private static final String PRICE_LBL = "price_lbl";
    private static final String PRICE_LBL_SH = "price_lbl_sh";
    private static final String PRICE_CROSS_LBL = "price_cross_lbl";
    private static final String CLOSE_DISC_BTN = "close_disc_btn";
    private static final String HEADER_LBL = "header_lbl";
    private static final String HEADER_LBL_SH = "header_lbl_sh";
    private static final String DESCRIPTION_LBL = "description_lbl";
    private static final String DESCRIPTION_LBL_SH = "descripton_lbl_sh";
    private static final int DISCOUNT_Y = 30;
    private static final int DISCOUNT_X = 260;

    public static boolean offerPromo;
    public static ShopItem offer;

    private Entity promoWindowE;
    
    public PromoWindow(GameStage gameStage, ItemWrapper gameItem) {
        super(gameStage);
        this.gameItem = gameItem;
    }

    private void loadPromoFromLib() {
        CompositeItemVO tempItemC = gameStage.sceneLoader.loadVoFromLibrary(PROMO_WINDOW).clone();
        promoWindowE = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), tempItemC);
        gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), promoWindowE, tempItemC.composite);
        gameStage.sceneLoader.getEngine().addEntity(promoWindowE);
    }

    public void init(){
        initShadow();
        loadPromoFromLib();

        promoWindowE.getComponent(ZIndexComponent.class).setZIndex(shadowE.getComponent(ZIndexComponent.class).getZIndex() + 10);

        final Entity closeBtn = promoWindowE.getComponent(NodeComponent.class).getChild(CLOSE_DISC_BTN);
        if (closeBtn.getComponent(ButtonComponent.class) == null){
            closeBtn.add(new ButtonComponent());
        }
        closeBtn.getComponent(ButtonComponent.class).clearListeners();
        closeBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(closeBtn) {
                    @Override
                    public void clicked() {
                        close(promoWindowE);
                        ResultScreenScript.active = true;
                    }});

        final TransformComponent settingsTc = promoWindowE.getComponent(TransformComponent.class);
        settingsTc.x = FAR_FAR_AWAY_X;
        settingsTc.y = FAR_FAR_AWAY_Y;
    }

    public void show() {
        isActive = true;
        addShadow();
        promoWindowE.getComponent(TransformComponent.class).x = DISCOUNT_X;
        promoWindowE.getComponent(TransformComponent.class).y = 460;
        promoWindowE.getComponent(ZIndexComponent.class).setZIndex(100);

//        Entity iconE = new ItemWrapper(sceneLoader.getRoot()).getChild(PROMO_WINDOW).getChild(offer.name);
//        iconE.getComponent(TransformComponent.class).x = 320;
//        iconE.getComponent(TransformComponent.class).y = 420;

        ActionComponent ac = gameStage.sceneLoader.engine.createComponent(ActionComponent.class);
        Actions.checkInit();
        ac.dataArray.add(Actions.moveTo(DISCOUNT_X, DISCOUNT_Y, 2, Interpolation.exp10Out));
        promoWindowE.add(ac);

        Entity buyBtn = promoWindowE.getComponent(NodeComponent.class).getChild(BUY_DISC_BTN);
        if (buyBtn.getComponent(ButtonComponent.class) == null){
            buyBtn.add(new ButtonComponent());
        }
        buyBtn.getComponent(ButtonComponent.class).clearListeners();
        buyBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(buyBtn) {
                    @Override
                    public void clicked() {
                        PromoWindow.offer.buyHard(gameStage);
                        close(promoWindowE);
                        ResultScreenScript.active = true;
                    }
                });

        Entity lbl = promoWindowE.getComponent(NodeComponent.class).getChild(PRICE_LBL);
        Entity lbl_sh = promoWindowE.getComponent(NodeComponent.class).getChild(PRICE_LBL_SH);
        LabelComponent lc = lbl.getComponent(LabelComponent.class);
        LabelComponent lc_sh = lbl_sh.getComponent(LabelComponent.class);
        LabelComponent dL = lbl.getComponent(LabelComponent.class);
        LabelComponent dL_sh = lbl_sh.getComponent(LabelComponent.class);
        if(offer != null) {
            lc.text.replace(0, lc.text.capacity(),  "$ " + String.valueOf(offer.cost));
            lc_sh.text.replace(0, lc_sh.text.capacity(),  "$ " + String.valueOf(offer.cost));
            dL.text.replace(0, dL.text.capacity(),  "$ " + String.valueOf(offer.costDisc));
            dL_sh.text.replace(0, dL_sh.text.capacity(),  "$ " + String.valueOf(offer.costDisc));

            promoWindowE.getComponent(NodeComponent.class).getChild(DESCRIPTION_LBL).getComponent(LabelComponent.class).text.replace(0, promoWindowE.getComponent(NodeComponent.class).getChild(DESCRIPTION_LBL).getComponent(LabelComponent.class).text.capacity(), offer.description);
            promoWindowE.getComponent(NodeComponent.class).getChild(DESCRIPTION_LBL_SH).getComponent(LabelComponent.class).text.replace(0, promoWindowE.getComponent(NodeComponent.class).getChild(DESCRIPTION_LBL_SH).getComponent(LabelComponent.class).text.capacity(), offer.description);
            promoWindowE.getComponent(NodeComponent.class).getChild(HEADER_LBL).getComponent(LabelComponent.class).text.replace(0, promoWindowE.getComponent(NodeComponent.class).getChild(HEADER_LBL).getComponent(LabelComponent.class).text.capacity(), offer.name);
            promoWindowE.getComponent(NodeComponent.class).getChild(HEADER_LBL_SH).getComponent(LabelComponent.class).text.replace(0, promoWindowE.getComponent(NodeComponent.class).getChild(HEADER_LBL_SH).getComponent(LabelComponent.class).text.capacity(), offer.name);
            promoWindowE.getComponent(NodeComponent.class).getChild(PRICE_CROSS_LBL).getComponent(LabelComponent.class).text.replace(0, promoWindowE.getComponent(NodeComponent.class).getChild(PRICE_CROSS_LBL).getComponent(LabelComponent.class).text.capacity(), "$ " + String.valueOf(offer.cost));

            if(gameStage.gameScript.fpc.currentPet != null) {
                gameStage.sceneLoader.rm.addSpriterToLoad(gameStage.gameScript.fpc.currentPet.name);
                CompositeItemVO tempItemC = gameStage.sceneLoader.loadVoFromLibrary(gameStage.gameScript.fpc.currentPet.name);
                Entity petPromoE = gameStage.sceneLoader.entityFactory.createSPRITERentity(gameStage.sceneLoader.getRoot(), tempItemC);
                gameStage.sceneLoader.getEngine().addEntity(petPromoE);
                petPromoE.getComponent(TransformComponent.class).x = 300;
                petPromoE.getComponent(TransformComponent.class).y = 300;
                petPromoE.getComponent(ZIndexComponent.class).setZIndex(230);
            }else{
                CompositeItemVO tempItemC = gameStage.sceneLoader.loadVoFromLibrary(offer.shopIcon);
                Entity offerIconE = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), tempItemC);
//                gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), iconBagClone, tempItemC.composite);
                gameStage.sceneLoader.getEngine().addEntity(offerIconE);
                offerIconE.getComponent(TransformComponent.class).x = 300;
                offerIconE.getComponent(TransformComponent.class).y = 300;
                offerIconE.getComponent(ZIndexComponent.class).setZIndex(230);
            }
        }
    }
}
