package com.fd.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.fd.etf.entity.componets.ShopItem;
import com.fd.etf.entity.componets.listeners.ImageButtonListener;
import com.fd.etf.stages.GameStage;
import com.fd.etf.stages.ResultScreenScript;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
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
    
    public static final String PROMO_WINDOW = "promo_lib";
    public static final String BUY_DISC_BTN = "buy_disc_btn";
    public static final String DISC_TEXT_LBL = "disc_text_lbl";
    public static final String CLOSE_DISC_BTN = "close_disc_btn";
    public static final int DISCOUNT_Y = 30;
    public static final int DISCOUNT_X = 260;

    public static boolean offerPromo;
    public static ShopItem offer;

    private Entity promoWindowE;
    
    public PromoWindow(ItemWrapper gameItem) {
        this.gameItem = gameItem;
    }

    private void loadPromoFromLib() {
        CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary(PROMO_WINDOW).clone();
        promoWindowE = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
        GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), promoWindowE, tempItemC.composite);
        GameStage.sceneLoader.getEngine().addEntity(promoWindowE);
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

        ActionComponent ac = new ActionComponent();
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
                        PromoWindow.offer.buyHard();
                        close(promoWindowE);
                        ResultScreenScript.active = true;
                    }
                });

        Entity lbl = promoWindowE.getComponent(NodeComponent.class).getChild(DISC_TEXT_LBL);
        LabelComponent lc = lbl.getComponent(LabelComponent.class);
        if(offer != null) {
            lc.text.replace(0, lc.text.capacity(), "Buy " + offer.name + " with discount!!! ");
        }
    }
}
