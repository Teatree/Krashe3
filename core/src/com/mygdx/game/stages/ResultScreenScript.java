package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.mygdx.game.entity.componets.VanityComponent;
import com.mygdx.game.utils.SaveMngr;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.mygdx.game.stages.GameScreenScript.*;

/**
 * Created by Teatree on 7/25/2015.
 */
public class ResultScreenScript implements IScript {

    public static final int PROGRESS_BAR_WIDTH = 690;
    public static final String SHOWCASE = "showcase";


    private GameStage stage;
    private ItemWrapper resultScreenItem;
    private Entity btn_lblE;
    private Entity aniE;
    private Entity btn_buyE;
    private Entity btn_noE;
    private Entity bgE;

    public static VanityComponent showCaseVanity;
    private static boolean show;

    public ResultScreenScript(GameStage stage) {
        this.stage = stage;
}

    Entity txtEarnedE;
    Entity progressBarE;

    LabelComponent earnedLabel;

    boolean showcasePopup;
    int nextVanityCost;
    int i = 0;
    int j = 0;
    int counter = 100;

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

        progressBarE = resultScreenItem.getChild("img_progress_bar").getEntity();

        DimensionsComponent dcProgressBar = progressBarE.getComponent(DimensionsComponent.class);
        dcProgressBar.width = fpc.totalScore - fpc.score;

//        Entity txtEarnedE = resultScreenItem.getChild("lbl_YOU_EARNED").getEntity();
//        LabelComponent earnedLabel = txtEarnedE.getComponent(LabelComponent.class);
//        earnedLabel.text.replace(0, earnedLabel.text.capacity(), "YOU EARNED: " + String.valueOf(fpc.score));

        txtEarnedE = resultScreenItem.getChild("lbl_YOU_EARNED").getEntity();
        earnedLabel = txtEarnedE.getComponent(LabelComponent.class);

        Entity txtBestE = resultScreenItem.getChild("lbl_BET_SCORE").getEntity();
        LabelComponent bestLabel = txtBestE.getComponent(LabelComponent.class);
        bestLabel.text.replace(0, bestLabel.text.capacity(), "YOUR BEST: " + String.valueOf(fpc.bestScore));

        int need = getNeedForNextItem();

        Entity txtNeedE = resultScreenItem.getChild("lbl_TO_UNLOCK").getEntity();
        LabelComponent needLabel = txtNeedE.getComponent(LabelComponent.class);
        if (need > 0) {
            needLabel.text.replace(0, needLabel.text.capacity(), "YOU NEED " + String.valueOf(need) + " TO UNLOCK NEXT ITEM");
        } else {
            needLabel.text.replace(0, needLabel.text.capacity(), "YOU UNLOCKED NEXT ITEM!");
        }
    }

    private int getNeedForNextItem() {
        Collections.sort(fpc.vanities, new Comparator<VanityComponent>() {
            @Override
            public int compare(VanityComponent o1, VanityComponent o2) {
                if (o1.cost > o2.cost) return 1;
                if (o1.cost < o2.cost) return -1;
                return 0;
            }
        });
        nextVanityCost = 0;
        for (VanityComponent vc : fpc.vanities) {
            if (!vc.advertised) {
                if (vc.cost > fpc.totalScore) {
                    nextVanityCost = vc.cost;
                    break;
                } else {
                    nextVanityCost = vc.cost;
                    showCaseVanity = vc;
                    vc.advertised = true;
                    break;
                }
            }
        }
        return nextVanityCost - fpc.totalScore;
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
                if (!showcasePopup) {
                    stage.initMenu();
                    show = false;
                }
            }
        });
    }

    private void initPlayBtn() {
        Entity backPlay = resultScreenItem.getChild("btn_play").getEntity();
        final LayerMapComponent lc = ComponentRetriever.get(backPlay, LayerMapComponent.class);
        backPlay.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
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
//                if (!showcasePopup) {
                    stage.initGame();
                show = false;
//                }
            }
        });
    }

    private void initShopBtn() {
        Entity shopBtn = resultScreenItem.getChild("btn_shop").getEntity();
        final LayerMapComponent lc = ComponentRetriever.get(shopBtn, LayerMapComponent.class);
        shopBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
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
//                if (!showcasePopup) {
                    stage.initShopMenu();
                show = false;
//                }
            }
        });
    }

    @Override
    public void act(float delta) {
        if (i < fpc.score) {
            updateScore();
        } else {
            updateProgressBar();
        }
//        if (showcasePopup) {
//            showFadeIn();

//        }
    }

    private void updateProgressBar() {
        DimensionsComponent dcProgressBar = progressBarE.getComponent(DimensionsComponent.class);
        float progressBarActualLength = ((float) fpc.totalScore / (float) nextVanityCost) * 100 * 6.9f;
        if (dcProgressBar.width <= progressBarActualLength &&
                dcProgressBar.width <= PROGRESS_BAR_WIDTH) {
            dcProgressBar.width += 2;
        } else if (!showcasePopup && showCaseVanity != null) {
            initShowcase();
        }
//        else {
//            if (!showcasePopup) {
//                initShowcase();
//            }
//        }
    }

    private void updateScore() {
        j++;
        int counterStep = fpc.score / 72 > 1 ? fpc.score / 72 : 1;
        if (j == 2) {
            earnedLabel.text.replace(0, earnedLabel.text.capacity(), "YOU EARNED: " + String.valueOf(i));
            i += counterStep;
            j = 0;
        }
    }

    private void initShowcase() {
        if (!show) {
            stage.initShowcase();
            show = true;
        }
    }
//    private void showFadeIn() {
//        showcaseE = resultScreenItem.getChild("showcase").getEntity();
//        TintComponent tic = bgE.getComponent(TintComponent.class);
//
//        if (tic.color.a <= 1) {
//            bgE = resultScreenItem.getChild("showcase").getChild("img_bg_show_case").getEntity();
//            tic.color.a += 0.05f;
//
//            btn_noE = resultScreenItem.getChild("showcase").getChild("btn_no").getChild("img_n").getEntity();
//            TintComponent ticNo = btn_noE.getComponent(TintComponent.class);
//            ticNo.color.a += 0.05f;
//
//            btn_buyE = resultScreenItem.getChild("showcase").getChild("btn_buy").getChild("img_n").getEntity();
//            TintComponent ticBuy = btn_buyE.getComponent(TintComponent.class);
//            ticBuy.color.a += 0.05f;
//
//            btn_lblE = resultScreenItem.getChild("showcase").getChild("lbl_item_name").getEntity();
//            TintComponent ticLbl = btn_lblE.getComponent(TintComponent.class);
//            ticLbl.color.a += 0.05f;
//
//        } else if (tic.color.a >= 0.9f) {
//            Entity aniE = resultScreenItem.getChild("showcase").getChild("showcase_ani").getEntity();
//            SpriterComponent sc = ComponentRetriever.get(aniE, SpriterComponent.class);
//
//            sc.player.speed = 12;
//
//            if (sc.player.time >= 225) {
//                sc.player.setAnimation(1);
//            }
//        }
//
//        TransformComponent tc = showcaseE.getComponent(TransformComponent.class);
//        tc.x = -25;
//        tc.y = -35;
//    }

    @Override
    public void dispose() {

    }
}
