package com.mygdx.etf.entity.componets.listeners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.mygdx.etf.stages.ShopScreenScript;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.systems.action.Actions;

import static com.mygdx.etf.utils.GlobalConstants.BTN_NORMAL;
import static com.mygdx.etf.utils.GlobalConstants.BTN_PRESSED;

/**
 * Created by ARudyk on 8/1/2016.
 */
public class ShopTabListener implements ButtonComponent.ButtonListener {

    private static ShopScreenScript shopScreenScript;

    public ShopTabListener(ShopScreenScript shopScript) {
        shopScreenScript = shopScript;
    }

    @Override
    public void touchUp() {
        if (!ShopScreenScript.isPreviewOn) {
            LayerMapComponent lc = shopScreenScript.btnShop.getComponent(LayerMapComponent.class);
            if (lc.getLayer(BTN_NORMAL).isVisible) {
                lc.getLayer(BTN_NORMAL).isVisible = true;
                lc.getLayer(BTN_PRESSED).isVisible = false;
            } else {
                lc.getLayer(BTN_NORMAL).isVisible = false;
                lc.getLayer(BTN_PRESSED).isVisible = true;
            }
        }
    }

    @Override
    public void touchDown() {
        if (!ShopScreenScript.isPreviewOn) {
            LayerMapComponent lc = shopScreenScript.btnShop.getComponent(LayerMapComponent.class);
            if (lc.getLayer(BTN_NORMAL).isVisible) {
                lc.getLayer(BTN_NORMAL).isVisible = true;
                lc.getLayer(BTN_PRESSED).isVisible = false;
            } else {
                lc.getLayer(BTN_NORMAL).isVisible = false;
                lc.getLayer(BTN_PRESSED).isVisible = true;
            }
        }
    }

    @Override
    public void clicked() {
        if (!ShopScreenScript.isPreviewOn) {
            if (shopScreenScript.btnShop.getComponent(ButtonComponent.class).enable) {
                changeTabBtnsLayers();
                shiftHCsections();
                shiftBags();
                shiftTouchZone();
            } else {
                LayerMapComponent lc1 = shopScreenScript.btnShop.getComponent(LayerMapComponent.class);
                lc1.getLayer(BTN_NORMAL).isVisible = false;
                lc1.getLayer(BTN_PRESSED).isVisible = true;
            }
        }
    }

    private static void shiftHCsections() {
        ActionComponent ac = new ActionComponent();
        Actions.checkInit();
        ac.dataArray.add(
                Actions.moveTo(ShopScreenScript.INIT_HC_ITEMS_X, shopScreenScript.hcSectionE.getComponent(TransformComponent.class).y, 0.7f, Interpolation.exp10));
        shopScreenScript.hcSectionE.add(ac);
    }

    private static void shiftTouchZone() {
        ActionComponent acTouchZone = new ActionComponent();
        Actions.checkInit();

        acTouchZone.dataArray.add(
                Actions.moveTo(1300, shopScreenScript.touchZone.getComponent(TransformComponent.class).y, 0.7f, Interpolation.exp10));
        shopScreenScript.touchZone.add(acTouchZone);
    }

    private static void shiftBags() {
        float bagsShift = 73 - shopScreenScript.bags.get(0).getComponent(TransformComponent.class).x;
        for (Entity bag : shopScreenScript.bags) {
            ActionComponent a = new ActionComponent();
            Actions.checkInit();

            a.dataArray.add(
                    Actions.moveTo(bag.getComponent(TransformComponent.class).x + 1227 + bagsShift,
                            bag.getComponent(TransformComponent.class).y, 0.7f, Interpolation.exp10)
            );
            bag.add(a);
        }
        shiftIcons(bagsShift);
    }

    private static void shiftIcons(float bagsShift) {
        for (Entity icon : ShopScreenScript.itemIcons.values()) {
            ActionComponent a = new ActionComponent();
            Actions.checkInit();

            a.dataArray.add(
                    Actions.moveTo(icon.getComponent(TransformComponent.class).x + 1227 + bagsShift,
                            icon.getComponent(TransformComponent.class).y, 0.7f, Interpolation.exp10)
            );

            icon.add(a);
        }
    }

    private static void changeTabBtnsLayers() {
        shopScreenScript.btnUpg.getComponent(ButtonComponent.class).enable = true;
        LayerMapComponent lc = shopScreenScript.btnUpg.getComponent(LayerMapComponent.class);
        lc.getLayer(BTN_NORMAL).isVisible = true;
        lc.getLayer(BTN_PRESSED).isVisible = false;

        shopScreenScript.btnShop.getComponent(ButtonComponent.class).enable = false;
        LayerMapComponent lc1 = shopScreenScript.btnShop.getComponent(LayerMapComponent.class);
        lc1.getLayer(BTN_NORMAL).isVisible = false;
        lc1.getLayer(BTN_PRESSED).isVisible = true;
    }

    public static void reset() {
        changeTabBtnsLayers();
        shopScreenScript.hcSectionE.getComponent(TransformComponent.class).x = ShopScreenScript.INIT_HC_ITEMS_X;
        float bagsShift = 73 - shopScreenScript.bags.get(0).getComponent(TransformComponent.class).x;
        for (Entity bag : shopScreenScript.bags) {
            bag.getComponent(TransformComponent.class).x += 1227 + bagsShift;
        }
        for (Entity icon : ShopScreenScript.itemIcons.values()) {
            ActionComponent a = new ActionComponent();
         icon.getComponent(TransformComponent.class).x += 1227 + bagsShift;
        }
        shiftTouchZone();
    }
}

