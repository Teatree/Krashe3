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
public class ShopUpgrTabListener implements ButtonComponent.ButtonListener {
    private ShopScreenScript shopScreenScript;

    public ShopUpgrTabListener(ShopScreenScript shopScreenScript) {
        this.shopScreenScript = shopScreenScript;
    }

    @Override
    public void touchUp() {
        if (!shopScreenScript.isPreviewOn) {
            LayerMapComponent lc = shopScreenScript.btnUpg.getComponent(LayerMapComponent.class);
            if (shopScreenScript.isPreviewOn && lc.getLayer(BTN_NORMAL).isVisible) {
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
        if (!shopScreenScript.isPreviewOn) {
            LayerMapComponent lc = shopScreenScript.btnUpg.getComponent(LayerMapComponent.class);
            if (shopScreenScript.isPreviewOn && lc.getLayer(BTN_NORMAL).isVisible) {
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

        if (!shopScreenScript.isPreviewOn) {
            if (shopScreenScript.btnUpg.getComponent(ButtonComponent.class).enable) {
                changeTabBtnsLayers();
                switchScreenToUpgrds();
            } else if (!shopScreenScript.isPreviewOn) {
                LayerMapComponent lc1 = shopScreenScript.btnUpg.getComponent(LayerMapComponent.class);
                lc1.getLayer(BTN_PRESSED).isVisible = true;
                lc1.getLayer(BTN_NORMAL).isVisible = false;
            }
        }
    }

    private void changeTabBtnsLayers() {
        shopScreenScript.btnUpg.getComponent(ButtonComponent.class).enable = false;
        LayerMapComponent lc = shopScreenScript.btnUpg.getComponent(LayerMapComponent.class);
        lc.getLayer(BTN_NORMAL).isVisible = false;
        lc.getLayer(BTN_PRESSED).isVisible = true;

        shopScreenScript.btnShop.getComponent(ButtonComponent.class).enable = true;
        LayerMapComponent lc1 = shopScreenScript.btnShop.getComponent(LayerMapComponent.class);
        lc1.getLayer(BTN_NORMAL).isVisible = true;
        lc1.getLayer(BTN_PRESSED).isVisible = false;
    }

    private void switchScreenToUpgrds() {
        ActionComponent ac = new ActionComponent();
        Actions.checkInit();

        ac.dataArray.add(
                Actions.moveTo(-1300, shopScreenScript.hcSectionE.getComponent(TransformComponent.class).y, 0.7f, Interpolation.exp10));
        shopScreenScript.hcSectionE.add(ac);

        for (Entity bag : shopScreenScript.bags) {
            ActionComponent a = new ActionComponent();
            Actions.checkInit();

            a.dataArray.add(
                    Actions.moveTo(bag.getComponent(TransformComponent.class).x - 1227,
                            bag.getComponent(TransformComponent.class).y, 0.7f, Interpolation.exp10)
            );
            bag.add(a);
        }

        for (Entity icon : shopScreenScript.itemIcons.values()) {
            ActionComponent a = new ActionComponent();
            Actions.checkInit();

            a.dataArray.add(
                    Actions.moveTo(icon.getComponent(TransformComponent.class).x - 1227,
                            icon.getComponent(TransformComponent.class).y, 0.7f, Interpolation.exp10)
            );

            icon.add(a);
        }
        ActionComponent acTouchZone = new ActionComponent();
        Actions.checkInit();

        acTouchZone.dataArray.add(
                Actions.moveTo(0, shopScreenScript.touchZone.getComponent(TransformComponent.class).y, 0.7f, Interpolation.exp10));
        shopScreenScript.touchZone.add(acTouchZone);
    }
}