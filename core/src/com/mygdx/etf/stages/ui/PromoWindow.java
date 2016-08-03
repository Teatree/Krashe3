package com.mygdx.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.mygdx.etf.entity.componets.ShopItem;
import com.mygdx.etf.entity.componets.listeners.ImageButtonListener;
import com.mygdx.etf.stages.ResultScreenScript;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.etf.stages.GameStage.sceneLoader;
import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_Y;

/**
 * Created by ARudyk on 7/4/2016.
 */
public class PromoWindow extends AbstractDialog {
    
    public static final String PROMO_WINDOW = "promo_window";
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
    
    public void init(){
        initShadow();
        promoWindowE = gameItem.getChild(PROMO_WINDOW).getEntity();
        promoWindowE.getComponent(ZIndexComponent.class).setZIndex(shadowE.getComponent(ZIndexComponent.class).getZIndex() + 10);

        final Entity closeBtn = gameItem.getChild(PROMO_WINDOW).getChild(CLOSE_DISC_BTN).getEntity();
        closeBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(closeBtn) {
                    @Override
                    public void clicked() {
                        close(promoWindowE);
                        ResultScreenScript.active = true;
                    }
                }
                /*new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
            }

            @Override
            public void touchDown() {
            }

            @Override
            public void clicked() {
                close(promoWindowE);
                ResultScreenScript.active = true;
            }
        }*/);

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

        Entity iconE = new ItemWrapper(sceneLoader.getRoot()).getChild(PROMO_WINDOW).getChild(offer.name).getEntity();
        iconE.getComponent(TransformComponent.class).x = 320;
        iconE.getComponent(TransformComponent.class).y = 420;

        ActionComponent ac = new ActionComponent();
        Actions.checkInit();
        ac.dataArray.add(Actions.moveTo(DISCOUNT_X, DISCOUNT_Y, 2, Interpolation.exp10Out));
        promoWindowE.add(ac);

        Entity buyBtn = gameItem.getChild(PROMO_WINDOW).getChild(BUY_DISC_BTN).getEntity();
        buyBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(buyBtn) {
                    @Override
                    public void clicked() {
                        PromoWindow.offer.buyHard();
                        close(promoWindowE);
                        ResultScreenScript.active = true;
                    }
                }
               /* new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {}

            @Override
            public void touchDown() {}

            @Override
            public void clicked() {
                PromoWindow.offer.buyHard();
                close(promoWindowE);
                ResultScreenScript.active = true;
            }
        }*/);

        Entity lbl = gameItem.getChild(PROMO_WINDOW).getChild(DISC_TEXT_LBL).getEntity();
        LabelComponent lc = lbl.getComponent(LabelComponent.class);
        lc.text.replace(0, lc.text.capacity(), "Buy " + offer.name + " with discount!!! ");
    }
}
