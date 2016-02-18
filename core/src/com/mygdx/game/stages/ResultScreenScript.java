package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.mygdx.game.Main;
import com.mygdx.game.entity.componets.VanityComponent;
import com.mygdx.game.stages.ui.Showcase;
import com.mygdx.game.utils.SaveMngr;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.Random;

import static com.mygdx.game.stages.GameScreenScript.fpc;
import static com.mygdx.game.stages.GameStage.sceneLoader;
import static com.mygdx.game.utils.GlobalConstants.*;

public class ResultScreenScript implements IScript {

    public static final int MAX_PROGRESS_BAR_WIDTH = 690;
    public static final double PERCENTS_TO_OFFER_AD = 0.01;
    public static VanityComponent showCaseVanity;
    public static boolean show;
    public static boolean isWasShowcase;
    public Entity txtNeedE;
    public Entity txtBestE;
    public Entity txtTotalE;
    public Entity progressBarE;
    Entity txtEarnedE;
    LabelComponent earnedLabel;
    boolean showcasePopup;
    long nextVanityCost;
    int i = 0;
    int j = 0;
    private GameStage stage;
    private ItemWrapper resultScreenItem;
    private Entity adsBtn;
    private Showcase showcase;

    public ResultScreenScript(GameStage stage) {
        this.stage = stage;
    }

    @Override
    public void init(Entity item) {
        i = 0;
        j = 0;
        resultScreenItem = new ItemWrapper(item);
        sceneLoader.addComponentsByTagName(BUTTON_TAG, ButtonComponent.class);
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

        long need = getNeedForNextItem();

        LabelComponent needLabel = txtNeedE.getComponent(LabelComponent.class);
        if (need > 0) {
            needLabel.text.replace(0, needLabel.text.capacity(), "YOU NEED " + String.valueOf(need) + " TO UNLOCK NEXT ITEM");
        } else {
            needLabel.text.replace(0, needLabel.text.capacity(), "YOU UNLOCKED NEXT ITEM!");
        }
    }

    private long getNeedForNextItem() {
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
        if (tempVc != null) {
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
                lc.getLayer(BTN_NORMAL).isVisible = true;
                lc.getLayer(BTN_PRESSED).isVisible = false;
            }

            @Override
            public void touchDown() {
                lc.getLayer(BTN_NORMAL).isVisible = false;
                lc.getLayer(BTN_PRESSED).isVisible = true;
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
        final Entity backPlay = resultScreenItem.getChild("btn_play").getEntity();
        final LayerMapComponent lc = ComponentRetriever.get(backPlay, LayerMapComponent.class);
        backPlay.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
                lc.getLayer(BTN_NORMAL).isVisible = true;
                lc.getLayer(BTN_PRESSED).isVisible = false;
            }

            @Override
            public void touchDown() {
                lc.getLayer(BTN_NORMAL).isVisible = false;
                lc.getLayer(BTN_PRESSED).isVisible = true;
            }

            @Override
            public void clicked() {
                if (!show) {
                    if (new Random().nextInt(10) <= 3) {
                        Main.adsController.showInterstitialGeneralAd(new Runnable() {
                            @Override
                            public void run() {
                                backToGame();
                            }
                        });
                    } else {
                        backToGame();
                    }
                }
            }

            private void backToGame() {
                stage.initGame();
                show = false;
                isWasShowcase = false;
            }
        });
    }

    private void initShopBtn() {
        Entity shopBtn = resultScreenItem.getChild("btn_shop").getEntity();
        final LayerMapComponent lc = ComponentRetriever.get(shopBtn, LayerMapComponent.class);
        shopBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
                lc.getLayer(BTN_NORMAL).isVisible = true;
                lc.getLayer(BTN_PRESSED).isVisible = false;
            }

            @Override
            public void touchDown() {
                lc.getLayer(BTN_NORMAL).isVisible = false;
                lc.getLayer(BTN_PRESSED).isVisible = true;
            }

            @Override
            public void clicked() {
                if (!show) {
                    stage.initShopMenu();
                    show = false;
                    isWasShowcase = false;
                }
            }
        });
    }

    @Override
    public void act(float delta) {
        if (!isWasShowcase) {
            if (i <= fpc.score) {
                updateScore();
            } else {
                earnedLabel.text.replace(0, earnedLabel.text.capacity(), "YOU EARNED: " + String.valueOf(fpc.score));
                updateProgressBar();
            }
        } else {
            earnedLabel.text.replace(0, earnedLabel.text.capacity(), "YOU EARNED: " + String.valueOf(fpc.score));
            progressBarE.getComponent(DimensionsComponent.class).width = getProgressBarActualLength();
        }
        showcase.showFading();
    }

    private void updateProgressBar() {
        DimensionsComponent dcProgressBar = progressBarE.getComponent(DimensionsComponent.class);

        if (dcProgressBar.width <= getProgressBarActualLength() &&
                dcProgressBar.width < MAX_PROGRESS_BAR_WIDTH) {
            dcProgressBar.width += 2;
        } else {
            if (!show && showCaseVanity != null) {
                initShowcase();
                progressBarE = resultScreenItem.getChild("img_progress_bar").getEntity();
                setProgressBar();
            }
            if (!show && fpc.totalScore >= PERCENTS_TO_OFFER_AD * nextVanityCost && adsBtn == null) {
                initWatchAdsForMoneyBtn();
            }
        }

        if (fpc.totalScore - fpc.score < 0) {
            dcProgressBar.width = 0;
        } else if (fpc.totalScore - fpc.score > 690) {
            dcProgressBar.width = 690;
        }
    }

    private float getProgressBarActualLength() {
        return fpc.totalScore < nextVanityCost ?
                ((float) fpc.totalScore / (float) nextVanityCost) * 100 * 6.9f :
                MAX_PROGRESS_BAR_WIDTH;
    }

    private void setProgressBar() {
        DimensionsComponent dcProgressBar = progressBarE.getComponent(DimensionsComponent.class);
        long scoreDiff = fpc.totalScore - fpc.score;
        if (scoreDiff < 0) {
            dcProgressBar.width = 0;
        } else if (scoreDiff < 690) {
            dcProgressBar.width = scoreDiff;
        } else {
            dcProgressBar.width = 690;
        }
    }

    private void updateScore() {
        j++;
        long counterStep = fpc.score / 48 > 1 ? fpc.score / 48 : 1;
        if (j == 2) {
            earnedLabel.text.replace(0, earnedLabel.text.capacity(), "YOU EARNED: " + String.valueOf(i));
            i += counterStep;
            j = 0;
        }
    }

    private void initShowcase() {
        if (!show) {
            show = true;
            showcase.initShowCase();
        }
    }

    private void initWatchAdsForMoneyBtn() {

        adsBtn = resultScreenItem.getChild("btn_watch_for_money").getEntity();

        TransformComponent tc = adsBtn.getComponent(TransformComponent.class);
        tc.x = 978;
        tc.y = 250;

        final LayerMapComponent lc = ComponentRetriever.get(adsBtn, LayerMapComponent.class);
        adsBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
                lc.getLayer(BTN_NORMAL).isVisible = true;
                lc.getLayer(BTN_PRESSED).isVisible = false;
            }

            @Override
            public void touchDown() {
                lc.getLayer(BTN_NORMAL).isVisible = false;
                lc.getLayer(BTN_PRESSED).isVisible = true;
            }

            @Override
            public void clicked() {
                if (!show) {
                    if (Main.adsController.isWifiConnected()) {
                        Main.adsController.showInterstitialGeneralAd(new Runnable() {
                            @Override
                            public void run() {
                                //give that money!
                                fpc.totalScore = nextVanityCost;
                                adsBtn.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
                                init(resultScreenItem.getEntity());
                            }
                        });
                    } else {
                        fpc.score = nextVanityCost - fpc.totalScore;
                        fpc.totalScore = nextVanityCost;

                        adsBtn.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
                        init(resultScreenItem.getEntity());

//                        Entity lbl = resultScreenItem.getChild("btn_watch_for_money").getChild("lbl").getEntity();
//                        LabelComponent lc = lbl.getComponent(LabelComponent.class);
//                        lc.text.replace(0, lc.text.capacity(), "You need internet connection");
                    }
                }
            }
        });

        tc.scaleY = 0.1f;
        tc.scaleX = 0.1f;
        adsBtn.getComponent(TintComponent.class).color.a = 0.1f;
        ActionComponent ac = new ActionComponent();
        Actions.checkInit();
        ac.dataArray.add(Actions.parallel(
                Actions.scaleTo(1f, 1f, 0.5f, Interpolation.exp5Out),
                Actions.fadeIn(0.5f, Interpolation.exp10Out)));
        adsBtn.add(ac);
    }

    @Override
    public void dispose() {
        SaveMngr.saveStats(fpc);
    }
}
