package com.fd.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.fd.etf.entity.componets.FlowerPublicComponent;
import com.fd.etf.entity.componets.PetComponent;
import com.fd.etf.entity.componets.ShopItem;
import com.fd.etf.entity.componets.Upgrade;
import com.fd.etf.entity.componets.listeners.ImageButtonListener;
import com.fd.etf.stages.GameStage;
import com.fd.etf.stages.ResultScreenScript;
import com.fd.etf.utils.SoundMgr;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_Y;

/**
 * Created by ARudyk on 7/4/2016.
 */
public class PromoWindow extends AbstractDialog {
    
    private static final String PROMO_WINDOW = "promo_lib";
    private static final String BUY_DISC_BTN = "buy_disc_btn";
    private static final String PRICE_CROSS_LBL = "price_cross_lbl";
    private static final String CLOSE_DISC_BTN = "close_disc_btn";
    private static final String HEADER_LBL = "header_lbl";
    private static final String HEADER_LBL_SH = "header_lbl_sh";
    private static final String DESCRIPTION_LBL = "description_lbl";
    private static final String DESCRIPTION_LBL_SH = "descripton_lbl_sh";
    private static final String DESCRIPTION_LBL_2 = "description_lbl_2";
    private static final String DESCRIPTION_LBL_SH_2 = "descripton_lbl_sh_2";
    private static final String DESCRIPTION_LBL_3 = "description_lbl_3";
    private static final String DESCRIPTION_LBL_SH_3 = "descripton_lbl_sh_3";
    private static final int DISCOUNT_Y = 30;
    private static final int DISCOUNT_X = 260;

    private static final int PREVIEW_Y = 900;

    private static final int ICON_Y = 1171;
    private static final String NEW_LINE_SIGN = "\n";
    private static final String SPACE_SIGN = "~";

    public static boolean offerPromo;
    public static ShopItem offer;

    private Entity promoWindowE;
    private Entity offerIconE;
    private Entity petPromoE;

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
//                        close(promoWindowE);
                        checkAndClose();
                        ResultScreenScript.active = true;
                    }});

        final TransformComponent settingsTc = promoWindowE.getComponent(TransformComponent.class);
        settingsTc.x = FAR_FAR_AWAY_X;
        settingsTc.y = FAR_FAR_AWAY_Y;
    }

    public void show() {
        // temprorary cheeat
//        offer = Upgrade.getBJDouble(gameStage);

        SoundMgr.getSoundMgr().play(SoundMgr.SPECIAL_OFFER);
        isActive = true;
        addShadow();
        promoWindowE.getComponent(TransformComponent.class).x = DISCOUNT_X;
        promoWindowE.getComponent(TransformComponent.class).y = 600;
        promoWindowE.getComponent(ZIndexComponent.class).setZIndex(100);

//        Entity iconE = new ItemWrapper(sceneLoader.getRoot()).getChild(PROMO_WINDOW).getChild(offer.name);
//        iconE.getComponent(TransformComponent.class).x = 320;
//        iconE.getComponent(TransformComponent.class).y = 420;

        ActionComponent ac = new ActionComponent();
        Actions.checkInit();
        ac.dataArray.add(Actions.moveBy(0, -1130, 2, Interpolation.exp10Out)); //KEEP THE MOVEBYY -1130 THAT'S EXACTLY THE MIDDLE OF THE SCREEN
        promoWindowE.add(ac);

        Entity buyBtn = promoWindowE.getComponent(NodeComponent.class).getChild(BUY_DISC_BTN);
//        buyBtn.getComponent(NodeComponent.class).getChild("lbl1");
        if (buyBtn.getComponent(ButtonComponent.class) == null){
            buyBtn.add(new ButtonComponent());
        }
        buyBtn.getComponent(ButtonComponent.class).clearListeners();
        buyBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(buyBtn) {
                    @Override
                    public void clicked() {
                        PromoWindow.offer.buyHardDiscount(gameStage);
                        checkAndClose();
//                        close(promoWindowE);
                        ResultScreenScript.active = true;
                    }
                });

//        LabelComponent lc = lbl.getComponent(LabelComponent.class);
//        LabelComponent lc_sh = lbl_sh.getComponent(LabelComponent.class);
        if(offer != null) {
//            lc.text.replace(0, lc.text.capacity(),  "$ " + String.valueOf(offer.cost));
//            lc_sh.text.replace(0, lc_sh.text.capacity(),  "$ " + String.valueOf(offer.cost));

            buyBtn.getComponent(NodeComponent.class).getChild("lbl1").getComponent(LabelComponent.class).text.replace(0, buyBtn.getComponent(NodeComponent.class).getChild("lbl1").getComponent(LabelComponent.class).text.capacity(),  "$ " + String.valueOf((float)offer.costDisc/100));
            buyBtn.getComponent(NodeComponent.class).getChild("lbl1_s").getComponent(LabelComponent.class).text.replace(0, buyBtn.getComponent(NodeComponent.class).getChild("lbl1_s").getComponent(LabelComponent.class).text.capacity(),  "$ " + String.valueOf((float)offer.costDisc/100));

//            promoWindowE.getComponent(NodeComponent.class).getChild(DESCRIPTION_LBL).getComponent(LabelComponent.class).text.replace(0, promoWindowE.getComponent(NodeComponent.class).getChild(DESCRIPTION_LBL).getComponent(LabelComponent.class).text.capacity(), offer.description);
//            promoWindowE.getComponent(NodeComponent.class).getChild(DESCRIPTION_LBL_SH).getComponent(LabelComponent.class).text.replace(0, promoWindowE.getComponent(NodeComponent.class).getChild(DESCRIPTION_LBL_SH).getComponent(LabelComponent.class).text.capacity(), offer.description);
            if (offer.description.contains(NEW_LINE_SIGN)) {
                String[] lines = offer.description.split(NEW_LINE_SIGN);
                promoWindowE.getComponent(NodeComponent.class).getChild(DESCRIPTION_LBL_2).getComponent(TintComponent.class).color.a = 1;
                promoWindowE.getComponent(NodeComponent.class).getChild(DESCRIPTION_LBL_SH_2).getComponent(TintComponent.class).color.a = 1;
                promoWindowE.getComponent(NodeComponent.class).getChild(DESCRIPTION_LBL).getComponent(LabelComponent.class).text.replace(
                        0, promoWindowE.getComponent(NodeComponent.class).getChild(DESCRIPTION_LBL).getComponent(LabelComponent.class).text.length,
                        lines[0]);
                promoWindowE.getComponent(NodeComponent.class).getChild(DESCRIPTION_LBL_SH).getComponent(LabelComponent.class).text.replace(
                        0, promoWindowE.getComponent(NodeComponent.class).getChild(DESCRIPTION_LBL_SH).getComponent(LabelComponent.class).text.length,
                        lines[0]);
                promoWindowE.getComponent(NodeComponent.class).getChild(DESCRIPTION_LBL_2).getComponent(LabelComponent.class).text.replace(
                        0, promoWindowE.getComponent(NodeComponent.class).getChild(DESCRIPTION_LBL_2).getComponent(LabelComponent.class).text.length,
                        lines[1]);
                promoWindowE.getComponent(NodeComponent.class).getChild(DESCRIPTION_LBL_SH_2).getComponent(LabelComponent.class).text.replace(
                        0, promoWindowE.getComponent(NodeComponent.class).getChild(DESCRIPTION_LBL_SH_2).getComponent(LabelComponent.class).text.length,
                        lines[1]);
            } else {
                promoWindowE.getComponent(NodeComponent.class).getChild(DESCRIPTION_LBL).getComponent(TintComponent.class).color.a = 0;
                promoWindowE.getComponent(NodeComponent.class).getChild(DESCRIPTION_LBL_SH).getComponent(TintComponent.class).color.a = 0;
                promoWindowE.getComponent(NodeComponent.class).getChild(DESCRIPTION_LBL_2).getComponent(LabelComponent.class).text.replace(
                        0, promoWindowE.getComponent(NodeComponent.class).getChild(DESCRIPTION_LBL_2).getComponent(LabelComponent.class).text.length,
                        offer.description);
                promoWindowE.getComponent(NodeComponent.class).getChild(DESCRIPTION_LBL_SH_2).getComponent(LabelComponent.class).text.replace(
                        0, promoWindowE.getComponent(NodeComponent.class).getChild(DESCRIPTION_LBL_SH_2).getComponent(LabelComponent.class).text.length,
                        offer.description);
            }

            if (offer.name.contains(SPACE_SIGN)) {
                promoWindowE.getComponent(NodeComponent.class).getChild(HEADER_LBL).getComponent(LabelComponent.class).text.replace(0, promoWindowE.getComponent(NodeComponent.class).getChild(HEADER_LBL).getComponent(LabelComponent.class).text.capacity(), offer.name.replace(SPACE_SIGN, " "));
                promoWindowE.getComponent(NodeComponent.class).getChild(HEADER_LBL_SH).getComponent(LabelComponent.class).text.replace(0, promoWindowE.getComponent(NodeComponent.class).getChild(HEADER_LBL_SH).getComponent(LabelComponent.class).text.capacity(), offer.name.replace(SPACE_SIGN, " "));
            }else{
                promoWindowE.getComponent(NodeComponent.class).getChild(HEADER_LBL).getComponent(LabelComponent.class).text.replace(0, promoWindowE.getComponent(NodeComponent.class).getChild(HEADER_LBL).getComponent(LabelComponent.class).text.capacity(), offer.name);
                promoWindowE.getComponent(NodeComponent.class).getChild(HEADER_LBL_SH).getComponent(LabelComponent.class).text.replace(0, promoWindowE.getComponent(NodeComponent.class).getChild(HEADER_LBL_SH).getComponent(LabelComponent.class).text.capacity(), offer.name);
            }
            promoWindowE.getComponent(NodeComponent.class).getChild(PRICE_CROSS_LBL).getComponent(LabelComponent.class).text.replace(0, promoWindowE.getComponent(NodeComponent.class).getChild(PRICE_CROSS_LBL).getComponent(LabelComponent.class).text.capacity(), "$ " + String.valueOf((float)offer.cost/100));

            if (FlowerPublicComponent.currentPet != null && offer == FlowerPublicComponent.currentPet) {
                gameStage.sceneLoader.rm.addSpriterToLoad(FlowerPublicComponent.currentPet.name);
                CompositeItemVO tempItemC = gameStage.sceneLoader.loadVoFromLibrary(FlowerPublicComponent.currentPet.name);
                petPromoE = gameStage.sceneLoader.entityFactory.createSPRITERentity(gameStage.sceneLoader.getRoot(), tempItemC);
                gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), petPromoE, tempItemC.composite);
                gameStage.sceneLoader.getEngine().addEntity(petPromoE);
                petPromoE.getComponent(TransformComponent.class).x = 520;
                petPromoE.getComponent(TransformComponent.class).y = 875;
                petPromoE.getComponent(ZIndexComponent.class).setZIndex(230);
                petPromoE.add(ac);
            }else{
                CompositeItemVO tempItemC = gameStage.sceneLoader.loadVoFromLibrary(offer.shopIcon);
                offerIconE = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), tempItemC);
                gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), offerIconE, tempItemC.composite);
                gameStage.sceneLoader.getEngine().addEntity(offerIconE);
                offerIconE.getComponent(TransformComponent.class).x = 470;
                offerIconE.getComponent(TransformComponent.class).y = 840;
                offerIconE.getComponent(TransformComponent.class).scaleX = 2f;
                offerIconE.getComponent(TransformComponent.class).scaleY = 2f;
                offerIconE.getComponent(ZIndexComponent.class).setZIndex(230);
                offerIconE.add(ac);
            }
        }
    }

    public void checkAndClose(){
        ActionComponent ac = new ActionComponent();
        Actions.checkInit();
        ac.dataArray.add(Actions.moveTo(promoWindowE.getComponent(TransformComponent.class).x, PREVIEW_Y, 0.8f, Interpolation.exp10));
        promoWindowE.add(ac);

        if(petPromoE != null) {
            ActionComponent acIconz = new ActionComponent();
            Actions.checkInit();
            acIconz.dataArray.add(Actions.moveTo(petPromoE.getComponent(TransformComponent.class).x, ICON_Y, 0.8f, Interpolation.exp10));
            petPromoE.add(acIconz);
        }

        if(offerIconE != null) {
            ActionComponent acIconz2 = new ActionComponent();
            Actions.checkInit();
            acIconz2.dataArray.add(Actions.moveTo(offerIconE.getComponent(TransformComponent.class).x, ICON_Y, 0.8f, Interpolation.exp10));
            offerIconE.add(acIconz2);
        }

        if(offer instanceof PetComponent) {
            // go thorough all pet and remove by nae
            for (PetComponent p : gameStage.gameScript.fpc.pets) {
                if(offer.name.equalsIgnoreCase(p.name)){
                    p.disable(gameStage);
                }
            }
        }

        if(gameStage.gameScript.fpc.upgrades.get(Upgrade.UpgradeType.PHOENIX) != null && offer instanceof Upgrade && ((Upgrade) offer).upgradeType == Upgrade.UpgradeType.PHOENIX){
            gameStage.gameScript.fpc.upgrades.get(Upgrade.UpgradeType.PHOENIX).disable(gameStage);
        }

        if(gameStage.gameScript.fpc.upgrades.get(Upgrade.UpgradeType.BJ_DOUBLE) != null && offer instanceof Upgrade && ((Upgrade) offer).upgradeType == Upgrade.UpgradeType.BJ_DOUBLE){
            gameStage.gameScript.fpc.upgrades.get(Upgrade.UpgradeType.BJ_DOUBLE).disable(gameStage);
        }

        ActionComponent ac2 = new ActionComponent();
        ac2.dataArray.add(Actions.fadeOut(0.8f, Interpolation.exp5));
        shadowE.add(ac2);
    }
}
