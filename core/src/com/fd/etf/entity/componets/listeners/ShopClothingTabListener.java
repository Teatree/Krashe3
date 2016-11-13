package com.fd.etf.entity.componets.listeners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.fd.etf.stages.ShopScreenScript;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.systems.action.Actions;

import static com.fd.etf.utils.GlobalConstants.*;

/**
 * Created by ARudyk on 8/1/2016.
 */
public class ShopClothingTabListener implements ButtonComponent.ButtonListener {

    private static final int SCREEN_WIDTH = 1227;
    private static final int PADDING = 50;
    private ShopScreenScript shopScreenScript;

    public ShopClothingTabListener(ShopScreenScript shopScreenScript) {
        this.shopScreenScript = shopScreenScript;
    }

    @Override
    public void touchUp() {
        if (!ShopScreenScript.isPreviewOn.get() /*&& ShopScreenScript.canScroll*/) {
            LayerMapComponent lc = shopScreenScript.btnPowerUp.getComponent(LayerMapComponent.class);
            if (ShopScreenScript.isPreviewOn.get() && lc.getLayer(BTN_NORMAL).isVisible) {
                lc.getLayer(BTN_NORMAL).isVisible = true;
                lc.getLayer(BTN_PRESSED).isVisible = false;
            } else {
                lc.getLayer(BTN_NORMAL).isVisible = false;
                lc.getLayer(BTN_PRESSED).isVisible = true;
                lc.getLayer(BTN_DEFAULT).isVisible = true;
            }
        }
    }

    @Override
    public void touchDown() {
        if (!ShopScreenScript.isPreviewOn.get() /*&& ShopScreenScript.canScroll*/) {
            LayerMapComponent lc = shopScreenScript.btnPowerUp.getComponent(LayerMapComponent.class);
            if (ShopScreenScript.isPreviewOn.get() && lc.getLayer(BTN_NORMAL).isVisible) {
                lc.getLayer(BTN_NORMAL).isVisible = true;
                lc.getLayer(BTN_PRESSED).isVisible = false;
            } else {
                lc.getLayer(BTN_NORMAL).isVisible = false;
                lc.getLayer(BTN_PRESSED).isVisible = true;
                lc.getLayer(BTN_DEFAULT).isVisible = true;
            }
        }
    }

    @Override
    public void clicked() {

        if (!ShopScreenScript.isPreviewOn.get()/* && ShopScreenScript.canScroll*/) {
            if (shopScreenScript.btnPowerUp.getComponent(ButtonComponent.class).enable) {
                changeTabBtnsLayers();
                switchScreenToUpgrds();
            } else if (!ShopScreenScript.isPreviewOn.get()) {
                LayerMapComponent lc1 = shopScreenScript.btnPowerUp.getComponent(LayerMapComponent.class);
                lc1.getLayer(BTN_PRESSED).isVisible = true;
                lc1.getLayer(BTN_DEFAULT).isVisible = true;
                lc1.getLayer(BTN_NORMAL).isVisible = false;
            }
        }
    }

    private void changeTabBtnsLayers() {
        shopScreenScript.btnPowerUp.getComponent(ButtonComponent.class).enable = false;
        LayerMapComponent lc = shopScreenScript.btnPowerUp.getComponent(LayerMapComponent.class);
        lc.getLayer(BTN_NORMAL).isVisible = false;
        lc.getLayer(BTN_PRESSED).isVisible = true;
        lc.getLayer(BTN_DEFAULT).isVisible = true;

        shopScreenScript.btnClothing.getComponent(ButtonComponent.class).enable = true;
        LayerMapComponent lc1 = shopScreenScript.btnClothing.getComponent(LayerMapComponent.class);
        lc1.getLayer(BTN_NORMAL).isVisible = true;
        lc1.getLayer(BTN_PRESSED).isVisible = false;
    }

    private void switchScreenToUpgrds() {
//        ShopScreenScript.canScroll = false;
        ActionComponent ac = new ActionComponent();
        Actions.checkInit();

        ac.dataArray.add(
                Actions.moveTo(-1300, shopScreenScript.hcSectionE.getComponent(TransformComponent.class).y, 0.7f, Interpolation.exp10));
        shopScreenScript.hcSectionE.add(ac);

        for (Entity bag : shopScreenScript.bags) {
            ActionComponent a = new ActionComponent();
            Actions.checkInit();

            a.dataArray.add(
                    Actions.moveTo(bag.getComponent(TransformComponent.class).x - SCREEN_WIDTH + PADDING,
                            bag.getComponent(TransformComponent.class).y, 0.7f, Interpolation.exp10)
            );
            bag.add(a);
        }
        ShopScreenScript.firstBagTargetPos = shopScreenScript.bags.get(0).getComponent(TransformComponent.class).x - SCREEN_WIDTH + PADDING;

        for (Entity icon : ShopScreenScript.itemIcons.values()) {
            ActionComponent a = new ActionComponent();
            Actions.checkInit();

            a.dataArray.add(
                    Actions.moveTo(icon.getComponent(TransformComponent.class).x - SCREEN_WIDTH + PADDING,
                            icon.getComponent(TransformComponent.class).y, 0.7f, Interpolation.exp10)
            );

            icon.add(a);
        }
        ActionComponent acTouchZone = new ActionComponent();
        Actions.checkInit();

        acTouchZone.dataArray.add(
                Actions.moveTo(0, shopScreenScript.touchZoneNButton.getComponent(TransformComponent.class).y, 0.7f, Interpolation.exp10));
        shopScreenScript.touchZoneNButton.add(acTouchZone);
        shopScreenScript.setDotActive(0);
    }
}