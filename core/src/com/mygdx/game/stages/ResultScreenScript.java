package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.mygdx.game.entity.componets.DailyGoal;
import com.mygdx.game.entity.componets.VanityComponent;
import com.mygdx.game.utils.SaveMngr;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.NinePatchComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.Image9patchVO;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.mygdx.game.stages.GameScreenScript.*;

/**
 * Created by Teatree on 7/25/2015.
 */
public class ResultScreenScript implements IScript {

    private GameStage stage;
    private ItemWrapper resultScreenItem;

    public ResultScreenScript(GameStage stage) {
        this.stage = stage;
    }

    @Override
    public void init(Entity item) {
        resultScreenItem = new ItemWrapper(item);
        stage.sceneLoader.addComponentsByTagName("button", ButtonComponent.class);
        initBackButton();
        initPlayBtn();
        initShopBtn();

        Entity txtTotalE = resultScreenItem.getChild("lbl_TOTAL").getEntity();
        LabelComponent totalLabel = txtTotalE.getComponent(LabelComponent.class);
        totalLabel.text.replace(0, totalLabel.text.capacity(), "TOTAL: " + String.valueOf(fpc.totalScore));

        Entity txtEarnedE = resultScreenItem.getChild("lbl_YOU_EARNED").getEntity();
        LabelComponent earnedLabel = txtEarnedE.getComponent(LabelComponent.class);
        earnedLabel.text.replace(0, earnedLabel.text.capacity(), "YOU EARNED: " + String.valueOf(fpc.score));

        List<VanityComponent> vanities = SaveMngr.getAllVanity();
        Collections.sort(vanities, new Comparator<VanityComponent>() {
            @Override
            public int compare(VanityComponent o1, VanityComponent o2) {
                if (o1.cost > o2.cost) return 1;
                if (o1.cost < o2.cost) return -1;
                return 0;
            }
        });
        int nextVanityCost = 0;
        for(VanityComponent vc: vanities){
            if(vc.cost >= fpc.totalScore){
                nextVanityCost = vc.cost;
                break;
            }
        }
        int need = nextVanityCost - fpc.totalScore;

        Entity txtNeedE = resultScreenItem.getChild("lbl_TO_UNLOCK").getEntity();
        LabelComponent needLabel = txtNeedE.getComponent(LabelComponent.class);
        needLabel.text.replace(0, needLabel.text.capacity(), "YOU NEED " + String.valueOf(need) + " TO UNLOCK NEXT ITEM");


        Entity progressBarE = resultScreenItem.getChild("img_progress_bar").getEntity();
        DimensionsComponent dcProgressBar = progressBarE.getComponent(DimensionsComponent.class);
        dcProgressBar.width = ((float)fpc.totalScore/(float)nextVanityCost) * 100 * 6.9f;
    }

    private void initBackButton() {
        Entity backBtn = resultScreenItem.getChild("btn_back").getEntity();
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
                stage.initMenu();

            }
        });
    }

    private void initPlayBtn() {
        Entity backBtn = resultScreenItem.getChild("btn_play").getEntity();
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
                stage.initGame();
            }
        });
    }

    private void initShopBtn() {
        Entity backBtn = resultScreenItem.getChild("btn_shop").getEntity();
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
                stage.initShopMenu();
            }
        });
    }

    @Override
    public void dispose() {

    }

    @Override
    public void act(float delta) {
    }

}
