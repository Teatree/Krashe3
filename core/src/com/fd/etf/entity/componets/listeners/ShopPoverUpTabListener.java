package com.fd.etf.entity.componets.listeners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.fd.etf.stages.ShopScreenScript;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.systems.action.Actions;

import static com.fd.etf.stages.ShopScreenScript.isPreviewOn;
import static com.fd.etf.utils.GlobalConstants.*;

/**
 * Created by ARudyk on 8/1/2016.
 */
public class ShopPoverUpTabListener implements ButtonComponent.ButtonListener {

    private static ShopScreenScript shopScreen;

    public ShopPoverUpTabListener(ShopScreenScript shopScript) {
        shopScreen = shopScript;
    }

    @Override
    public void touchUp() {
        if (!isPreviewOn.get()) {
            if (!isPreviewOn.get() && ShopScreenScript.canChangeTabs) {
                LayerMapComponent lc = shopScreen.btnClothing.getComponent(LayerMapComponent.class);
                if (isPreviewOn.get() && lc.getLayer(BTN_NORMAL).isVisible) {
                    lc.getLayer(BTN_NORMAL).isVisible = true;
                    lc.getLayer(BTN_PRESSED).isVisible = false;
                } else {
                    lc.getLayer(BTN_NORMAL).isVisible = false;
                    lc.getLayer(BTN_PRESSED).isVisible = true;
                    lc.getLayer(BTN_DEFAULT).isVisible = true;
                }
            }
        } else {
            ButtonComponent.skipDefaultLayersChange = false;
        }
    }

    @Override
    public void touchDown() {
        if (!isPreviewOn.get()) {
            if (!isPreviewOn.get() && ShopScreenScript.canChangeTabs) {
                LayerMapComponent lc = shopScreen.btnClothing.getComponent(LayerMapComponent.class);
                if (lc.getLayer(BTN_NORMAL).isVisible) {
                    lc.getLayer(BTN_NORMAL).isVisible = true;
                    lc.getLayer(BTN_PRESSED).isVisible = false;
                } else {
                    lc.getLayer(BTN_NORMAL).isVisible = false;
                    lc.getLayer(BTN_PRESSED).isVisible = true;
                }
            }
        } else {
            ButtonComponent.skipDefaultLayersChange = true;
        }
    }

    @Override
    public void clicked() {
        if (!isPreviewOn.get())
            if (!isPreviewOn.get() && ShopScreenScript.canChangeTabs) {
                if (shopScreen.btnClothing.getComponent(ButtonComponent.class).enable) {
                    changeTabBtnsLayers();
                    shiftHCsections();
                    shiftBags();
                    shiftTouchZone();
                } else {
                    LayerMapComponent lc1 = shopScreen.btnClothing.getComponent(LayerMapComponent.class);
                    lc1.getLayer(BTN_NORMAL).isVisible = false;
                    lc1.getLayer(BTN_PRESSED).isVisible = true;
                    lc1.getLayer(BTN_DEFAULT).isVisible = true;
                }
            }
    }

    private static void shiftHCsections() {
        ActionComponent ac = shopScreen.gameStage.sceneLoader.engine.createComponent(ActionComponent.class);
        Actions.checkInit();
        ac.dataArray.add(
                Actions.moveTo(ShopScreenScript.INIT_HC_ITEMS_X, shopScreen.hcSectionE.getComponent(TransformComponent.class).y, 0.7f, Interpolation.exp10));
        shopScreen.hcSectionE.add(ac);
    }

    private static void shiftTouchZone() {
        ActionComponent acTouchZone = shopScreen.gameStage.sceneLoader.engine.createComponent(ActionComponent.class);
        Actions.checkInit();

        acTouchZone.dataArray.add(
                Actions.moveTo(1300, shopScreen.touchZoneNButton.getComponent(TransformComponent.class).y, 0.7f, Interpolation.exp10));
        shopScreen.touchZoneNButton.add(acTouchZone);
    }

    private static void shiftBags() {
        ShopScreenScript.canChangeTabs = false;
        float bagsShift = 73 - shopScreen.bags.get(0).getComponent(TransformComponent.class).x;
        for (Entity bag : shopScreen.bags) {
            ActionComponent a = shopScreen.gameStage.sceneLoader.engine.createComponent(ActionComponent.class);
            Actions.checkInit();

            a.dataArray.add(
                    Actions.moveTo(bag.getComponent(TransformComponent.class).x + 1227 + bagsShift,
                            bag.getComponent(TransformComponent.class).y, 0.7f, Interpolation.exp10)
            );
            bag.add(a);
        }
        ShopScreenScript.firstBagTargetPos = shopScreen.bags.get(0).getComponent(TransformComponent.class).x + 1227 + bagsShift;
        shiftIcons(bagsShift);
        shopScreen.resetPages();
    }

    private static void shiftIcons(float bagsShift) {
        for (Entity icon : ShopScreenScript.itemIcons.values()) {
            ActionComponent a = shopScreen.gameStage.sceneLoader.engine.createComponent(ActionComponent.class);
            Actions.checkInit();

            a.dataArray.add(
                    Actions.moveTo(icon.getComponent(TransformComponent.class).x + 1227 + bagsShift,
                            icon.getComponent(TransformComponent.class).y, 0.7f, Interpolation.exp10)
            );

            icon.add(a);
        }
    }

    private static void changeTabBtnsLayers() {
        shopScreen.btnPowerUp.getComponent(ButtonComponent.class).enable = true;
        LayerMapComponent lc = shopScreen.btnPowerUp.getComponent(LayerMapComponent.class);
        lc.getLayer(BTN_NORMAL).isVisible = true;
        lc.getLayer(BTN_PRESSED).isVisible = false;

        shopScreen.btnClothing.getComponent(ButtonComponent.class).enable = false;
        LayerMapComponent lc1 = shopScreen.btnClothing.getComponent(LayerMapComponent.class);
        lc1.getLayer(BTN_NORMAL).isVisible = false;
        lc1.getLayer(BTN_PRESSED).isVisible = true;
        lc1.getLayer(BTN_DEFAULT).isVisible = true;

    }

    public static void reset() {
        changeTabBtnsLayers();
        shopScreen.hcSectionE.getComponent(TransformComponent.class).x = ShopScreenScript.INIT_HC_ITEMS_X;
        float bagsShift = 73 - shopScreen.bags.get(0).getComponent(TransformComponent.class).x;
        for (Entity bag : shopScreen.bags) {
            bag.getComponent(TransformComponent.class).x += 1227 + bagsShift;
        }
        for (Entity icon : ShopScreenScript.itemIcons.values()) {
            icon.getComponent(TransformComponent.class).x += 1227 + bagsShift;
        }
        shopScreen.touchZoneNButton.getComponent(TransformComponent.class).x = 1300;
//        shiftTouchZone();
    }
}

