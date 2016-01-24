package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mygdx.game.entity.componets.VanityComponent;
import com.mygdx.game.utils.SaveMngr;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.Collections;
import java.util.Comparator;

import static com.mygdx.game.stages.GameScreenScript.*;
import static com.mygdx.game.stages.ShowcaseScreenScript.*;

/**
 * Created by Teatree on 7/25/2015.
 */
public class ResultScreenScript implements IScript {

    public static final int PROGRESS_BAR_WIDTH = 690;
    public static final String SHOWCASE = "showcase";


    private GameStage stage;
    private ItemWrapper resultScreenItem;

    public static VanityComponent showCaseVanity;
    public static boolean show;
    public static boolean isWasShowcase;

    public ResultScreenScript(GameStage stage) {
        this.stage = stage;
    }

    Entity txtEarnedE;
    public Entity txtNeedE;
    public Entity txtBestE;
    public Entity txtTotalE;
    public Entity progressBarE;

    LabelComponent earnedLabel;

    boolean showcasePopup;
    int nextVanityCost;
    int i = 0;
    int j = 0;

    private Showcase showcase;

    @Override
    public void init(Entity item) {
        resultScreenItem = new ItemWrapper(item);
        stage.sceneLoader.addComponentsByTagName("button", ButtonComponent.class);
        initBackButton();
        initPlayBtn();
        initShopBtn();

        txtTotalE = resultScreenItem.getChild("lbl_TOTAL").getEntity();
        txtEarnedE = resultScreenItem.getChild("lbl_YOU_EARNED").getEntity();
        progressBarE = resultScreenItem.getChild("img_progress_bar").getEntity();
        txtBestE = resultScreenItem.getChild("lbl_BET_SCORE").getEntity();
        txtNeedE = resultScreenItem.getChild("lbl_TO_UNLOCK").getEntity();

        initResultScreen();
        showcase = new Showcase(resultScreenItem, this);
    }

    public void initResultScreen() {
        LabelComponent totalLabel = txtTotalE.getComponent(LabelComponent.class);
        totalLabel.text.replace(0, totalLabel.text.capacity(), "TOTAL: " + String.valueOf(fpc.totalScore));
        setProgressBar();

        earnedLabel = txtEarnedE.getComponent(LabelComponent.class);

        LabelComponent bestLabel = txtBestE.getComponent(LabelComponent.class);
        bestLabel.text.replace(0, bestLabel.text.capacity(), "YOUR BEST: " + String.valueOf(fpc.bestScore));

        int need = getNeedForNextItem();

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
        VanityComponent tempVc = null;
        for (VanityComponent vc : fpc.vanities) {
            if (!vc.advertised) {
                if (vc.cost > fpc.totalScore) {
                    nextVanityCost = vc.cost;
                    break;
                } else {
                    tempVc = vc;
                }
            }
        }
        if(tempVc!=null) {
            showCaseVanity = tempVc;
            showCaseVanity.advertised = true;
            nextVanityCost = showCaseVanity.cost;
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
                if (!showcasePopup && !show) {
                    stage.initMenu();
                    show = false;
                }
                isWasShowcase = false;
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
                if (!show) {
                    stage.initGame();
                    show = false;
                    isWasShowcase = false;
                }
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
                if(!show) {
                    stage.initShopMenu();
                    show = false;
                    isWasShowcase = false;
                }
            }
        });
    }

    @Override
    public void act(float delta) {
        if(!isWasShowcase) {
            if (i <= fpc.score) {
                updateScore();
            } else {
                earnedLabel.text.replace(0, earnedLabel.text.capacity(), "YOU EARNED: " + String.valueOf(fpc.score));
                updateProgressBar();
            }
        }else {
            earnedLabel.text.replace(0, earnedLabel.text.capacity(), "YOU EARNED: " + String.valueOf(fpc.score));
            float progressBarActualLength = ((float) fpc.totalScore / (float) nextVanityCost) * 100 * 6.9f;
            progressBarE.getComponent(DimensionsComponent.class).width = progressBarActualLength;
        }
        showcase.showFading();
    }

    private void updateProgressBar() {
        DimensionsComponent dcProgressBar = progressBarE.getComponent(DimensionsComponent.class);
        float progressBarActualLength = ((float) fpc.totalScore / (float) nextVanityCost) * 100 * 6.9f;
        if (dcProgressBar.width <= progressBarActualLength &&
                dcProgressBar.width <= PROGRESS_BAR_WIDTH) {
            dcProgressBar.width += 2;
        } else if (!show && showCaseVanity != null) {
            initShowcase();
            progressBarE = resultScreenItem.getChild("img_progress_bar").getEntity();

            setProgressBar();
        }

        if(fpc.totalScore - fpc.score < 0){
            dcProgressBar.width = 0;
        }else if(fpc.totalScore - fpc.score > 690){
            dcProgressBar.width = 690;
        }
    }

    private void setProgressBar() {
        DimensionsComponent dcProgressBar = progressBarE.getComponent(DimensionsComponent.class);
        int scoreDiff = fpc.totalScore - fpc.score;
        if (scoreDiff < 690) {
            dcProgressBar.width = scoreDiff;
        }else if(scoreDiff < 0){
            dcProgressBar.width = 0;
        }else{
            dcProgressBar.width = 690;
        }
    }

    private void updateScore() {
        j++;
        int counterStep = fpc.score / 48 > 1 ? fpc.score / 48 : 1;
        if (j == 2) {
            earnedLabel.text.replace(0, earnedLabel.text.capacity(), "YOU EARNED: " + String.valueOf(i));
            i += counterStep;
            j = 0;
        }
    }

    private void initShowcase() {
        if (!show) {
            show = true;
//            Showcase showcase = new Showcase(resultScreenItem);
            showcase.initShowCase();

//            FileHandle newAsset = Gdx.files.internal(PATH_PREFIX + showCaseVanity.icon + TYPE_SUFFIX);
//            newAsset.copyTo(Gdx.files.local(PATH_PREFIX + ITEM_UNKNOWN_DEFAULT + TYPE_SUFFIX));
//
//            stage.initShowcase();
        }
    }

    @Override
    public void dispose() {
        SaveMngr.saveStats(fpc);
    }
}
