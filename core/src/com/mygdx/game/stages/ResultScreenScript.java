package com.mygdx.game.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.mygdx.game.Main;
import com.mygdx.game.entity.componets.VanityComponent;
import com.mygdx.game.stages.ui.Showcase;
import com.mygdx.game.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.Random;

import static com.mygdx.game.stages.GameStage.sceneLoader;
import static com.mygdx.game.utils.GlobalConstants.*;

public class ResultScreenScript implements IScript {

    public static final int MAX_PROGRESS_BAR_WIDTH = 690;
    public static final double PERCENTS_TO_OFFER_AD = 0.01;
    public static final int PROGRESS_BAR_STEP = 12;
    public static final String IMG_PROGRESS_BAR = "img_progress_bar";
    public static final String BTN_WATCH_FOR_MONEY = "btn_watch_for_money";
    public static final String YOU_EARNED = "YOU EARNED: ";
    public static final String TOTAL = "TOTAL: ";
    public static final String YOUR_BEST = "YOUR BEST: ";
    public static final String YOU_NEED = "YOU NEED ";
    public static final String YOU_UNLOCKED_NEXT_ITEM = "YOU UNLOCKED NEXT ITEM!";
    public static final String BTN_BACK = "btn_back";
    public static final String BTN_PLAY = "btn_play";
    public static final String BTN_SHOP = "btn_shop";

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
//    static long nextVanityCost;
    long need;
    int i = 0;
    int j = 0;
    Entity backPlay;
    private GameStage stage;
    private ItemWrapper resultScreenItem;
    private Entity adsBtn;
    private Showcase showcase;

    public ResultScreenScript(GameStage stage) {
        this.stage = stage;
    }

    @Override
    public void init(Entity item) {

//        System.err.print("init res ");
//        System.err.println(Gdx.app.getJavaHeap() / 1000000 + " : " +
//                Gdx.app.getNativeHeap());

        i = 0;
        j = 0;
        resultScreenItem = new ItemWrapper(item);
        sceneLoader.addComponentsByTagName(BUTTON_TAG, ButtonComponent.class);

        txtTotalE = resultScreenItem.getChild("lbl_TOTAL").getEntity();
        txtEarnedE = resultScreenItem.getChild("lbl_YOU_EARNED").getEntity();
        progressBarE = resultScreenItem.getChild("img_progress_bar").getEntity();
        txtBestE = resultScreenItem.getChild("lbl_BET_SCORE").getEntity();
        txtNeedE = resultScreenItem.getChild("lbl_TO_UNLOCK").getEntity();

        initResultScreen();
        showcase = new Showcase(resultScreenItem, this);
    }

    public void initButtons() {
        initBackButton();
        initPlayBtn();
        initShopBtn();
    }
    public void initResultScreen() {
        show = false;

        LabelComponent totalLabel = txtTotalE.getComponent(LabelComponent.class);
        totalLabel.text.replace(0, totalLabel.text.capacity(), TOTAL + String.valueOf(GameStage.gameScript.fpc.totalScore));

        earnedLabel = txtEarnedE.getComponent(LabelComponent.class);

        LabelComponent bestLabel = txtBestE.getComponent(LabelComponent.class);
        bestLabel.text.replace(0, bestLabel.text.capacity(), YOUR_BEST + String.valueOf(GameStage.gameScript.fpc.bestScore));

//        long need = nextVanityCost == 0 ? getNeedForNextItem() : nextVanityCost;
        if (showCaseVanity == null && !isWasShowcase) {
            getNeedForNextItem();
            isWasShowcase = false;
        }
        need = showCaseVanity.cost - GameStage.gameScript.fpc.totalScore;
        setProgressBar();

        LabelComponent needLabel = txtNeedE.getComponent(LabelComponent.class);
        if (need > 0) {
            needLabel.text.replace(0, needLabel.text.capacity(), YOU_NEED + String.valueOf(need) + " TO UNLOCK NEXT ITEM");
        } else {
            needLabel.text.replace(0, needLabel.text.capacity(), YOU_UNLOCKED_NEXT_ITEM);
        }
    }

    private long getNeedForNextItem() {
//        nextVanityCost = 0;
        VanityComponent tempVc = null;
        for (VanityComponent vc : GameStage.gameScript.fpc.vanities) {
            if (!vc.bought) {
                if (vc.cost >= GameStage.gameScript.fpc.totalScore) {
                    showCaseVanity = vc;
//                    nextVanityCost = vc.cost;
                    break;
                } else {
                    showCaseVanity = vc;
//                    nextVanityCost = 0;
                    tempVc = vc;
                }
            }
        }
//        if (tempVc != null && showCaseVanity == null) {
//            showCaseVanity = tempVc;
//            showCaseVanity.advertised = true;
//            nextVanityCost = showCaseVanity.cost;
//        }
        return showCaseVanity.cost - GameStage.gameScript.fpc.totalScore;
    }

    private void initBackButton() {
        Entity backBtn = resultScreenItem.getChild(BTN_BACK).getEntity();
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
                }
                isWasShowcase = false;
            }
        });
    }

    private void initPlayBtn() {
        backPlay = resultScreenItem.getChild(BTN_PLAY).getEntity();
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
                    if (Main.adsController.isWifiConnected()) {
                        ShowAdsWithChance();
                    } else {
                        backToGame();
                    }
                }
            }

            private void ShowAdsWithChance() {
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

            private void backToGame() {
                stage.initGame();
                isWasShowcase = false;
            }
        });
    }

    private void initShopBtn() {
        Entity shopBtn = resultScreenItem.getChild(BTN_SHOP).getEntity();
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
                    stage.initShop();
                    isWasShowcase = false;
                }
            }
        });
    }

    @Override
    public void act(float delta) {
        if (!isWasShowcase) {
            if (i <= GameStage.gameScript.fpc.score) {
                updateScore();
            } else {
                earnedLabel.text.replace(0, earnedLabel.text.capacity(), YOU_EARNED + String.valueOf(GameStage.gameScript.fpc.score));
                updateProgressBar(delta);
            }
        } else {
            earnedLabel.text.replace(0, earnedLabel.text.capacity(), YOU_EARNED + String.valueOf(GameStage.gameScript.fpc.score));
            progressBarE.getComponent(DimensionsComponent.class).width = MAX_PROGRESS_BAR_WIDTH;
            txtNeedE.getComponent(LabelComponent.class).text.replace(0, txtNeedE.getComponent(LabelComponent.class).text.length, "");
        }
        showcase.showFading();
    }

    private void updateProgressBar(float deltaTime) {
        DimensionsComponent dcProgressBar = progressBarE.getComponent(DimensionsComponent.class);

        if (dcProgressBar.width <= getProgressBarActualLength() &&
                dcProgressBar.width < MAX_PROGRESS_BAR_WIDTH) {
            dcProgressBar.width += PROGRESS_BAR_STEP * deltaTime * GlobalConstants.FPS;
        } else {
            if (!show && showCaseVanity!= null && showCaseVanity.cost <= GameStage.gameScript.fpc.totalScore) {
                initShowcase();
                progressBarE = resultScreenItem.getChild(IMG_PROGRESS_BAR).getEntity();
//                setProgressBar();
            }
            if (!show && GameStage.gameScript.fpc.totalScore < PERCENTS_TO_OFFER_AD
                    && GameStage.gameScript.fpc.totalScore >= PERCENTS_TO_OFFER_AD * need
                    && adsBtn == null) {
                initWatchAdsForMoneyBtn();
            }
        }

        if (GameStage.gameScript.fpc.totalScore - GameStage.gameScript.fpc.score < 0) {
            dcProgressBar.width = 0;
        } else if ((GameStage.gameScript.fpc.totalScore - GameStage.gameScript.fpc.score)*MAX_PROGRESS_BAR_WIDTH/showCaseVanity.cost > MAX_PROGRESS_BAR_WIDTH) {
            dcProgressBar.width = MAX_PROGRESS_BAR_WIDTH;
        }
    }

    private float getProgressBarActualLength() {
        return GameStage.gameScript.fpc.totalScore < showCaseVanity.cost ?
                ((float) GameStage.gameScript.fpc.totalScore / (float) showCaseVanity.cost) * 100 * 6.9f :
                MAX_PROGRESS_BAR_WIDTH;
    }

    private void setProgressBar() {
        DimensionsComponent dcProgressBar = progressBarE.getComponent(DimensionsComponent.class);
        long scoreDiff = (GameStage.gameScript.fpc.totalScore - GameStage.gameScript.fpc.score)*MAX_PROGRESS_BAR_WIDTH/showCaseVanity.cost;
        if (scoreDiff < 0) {
            dcProgressBar.width = 0;
        } else if (scoreDiff < MAX_PROGRESS_BAR_WIDTH) {
            dcProgressBar.width = scoreDiff;
        } else {
            dcProgressBar.width = MAX_PROGRESS_BAR_WIDTH;
        }
    }

    private void updateScore() {
        j++;
        long counterStep = GameStage.gameScript.fpc.score / 48 > 1 ? GameStage.gameScript.fpc.score / 48 : 1;
        if (j == 2) {
            earnedLabel.text.replace(0, earnedLabel.text.capacity(), YOU_EARNED + String.valueOf(i));
            i += counterStep;
            j = 0;
        }
    }

    private void initShowcase() {
        progressBarE.getComponent(DimensionsComponent.class).width = MAX_PROGRESS_BAR_WIDTH;
        if (!show && GameStage.gameScript.fpc.score > 0) {
            showcase.initShowCase();
            txtNeedE.getComponent(LabelComponent.class).text.replace(0,txtNeedE.getComponent(LabelComponent.class).text.length,"");
        }
    }

    private void initWatchAdsForMoneyBtn() {

        adsBtn = resultScreenItem.getChild(BTN_WATCH_FOR_MONEY).getEntity();

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
                                GameStage.gameScript.fpc.totalScore = showCaseVanity.cost;
                                adsBtn.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
                                init(resultScreenItem.getEntity());
                            }
                        });
                    } else {
                        GameStage.gameScript.fpc.score = showCaseVanity.cost - GameStage.gameScript.fpc.totalScore;
                        GameStage.gameScript.fpc.totalScore = showCaseVanity.cost;

                        adsBtn.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
                        init(resultScreenItem.getEntity());
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
        System.gc();
    }

    public void reset() {
        init(resultScreenItem.getEntity());
    }
}
