package com.fd.etf.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Interpolation;
import com.fd.etf.Main;
import com.fd.etf.entity.componets.VanityComponent;
import com.fd.etf.entity.componets.listeners.ImageButtonListener;
import com.fd.etf.stages.ui.PromoWindow;
import com.fd.etf.stages.ui.Showcase;
import com.fd.etf.stages.ui.TrialTimer;
import com.fd.etf.system.ParticleLifespanSystem;
import com.fd.etf.utils.GlobalConstants;
import com.fd.etf.utils.SaveMngr;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.fd.etf.stages.ui.PromoWindow.offerPromo;
import static com.fd.etf.utils.GlobalConstants.*;

public class ResultScreenScript implements IScript {

    private static final int MAX_PROGRESS_BAR_WIDTH = 660;
    public static final double PERCENTS_TO_OFFER_AD = 0.99;
    private static final int PROGRESS_BAR_STEP = 12;
    private static final String IMG_PROGRESS_BAR = "img_progress_bar";
    private static final String BTN_WATCH_FOR_MONEY = "btn_watch_for_money";
    private static final String YOU_EARNED = "+";
    private static final String YOUR_BEST = "BEST: ";
    private static final String YOU_NEED = "YOU NEED ";
    private static final String YOU_UNLOCKED_NEXT_ITEM = "YOU UNLOCKED NEXT ITEM!";
    private static final String BTN_BACK = "btn_back";
    private static final String BTN_PLAY = "btn_play";
    private static final String BTN_SHOP = "btn_shop";
    private static final String LBL_TOTAL = "lbl_TOTAL";
    private static final String LBL_YOU_EARNED = "lbl_YOU_EARNED";
    private static final String LBL_BET_SCORE = "lbl_BET_SCORE";
    private static final String LBL_TO_UNLOCK = "lbl_TO_UNLOCK";
    private static final String NEXT_ITEM_ICON = "next_item";
//    private static final String TRIAL_TIMER = "timer_lbl";

    public static VanityComponent showCaseVanity;
    public static boolean show;
    boolean showcasePopup;
    boolean shopTransitionIsOn;
    public static boolean isWasShowcase;
    public static boolean active = true;

    public Entity txtNeedE;
    public Entity txtBestE;
    public Entity txtTotalE;
    //    public Entity txtTotalsE;
    public Entity progressBarE;
    Entity txtEarnedE;
    Entity txtEarnedsE;

    LabelComponent earnedLabel;
    //    LabelComponent earnedLabels;
    public long need;
    int i = 0;
    int j = 0;
    Entity backPlay;
    //    private GameStage gameStage;
    public ItemWrapper resultScreenItem;
    private Entity adsBtn;
    private Showcase showcase;
    private TrialTimer timer;
    private PromoWindow promoWindow;

    private GameStage gameStage;

    public ResultScreenScript(GameStage gameStage) {
        this.gameStage = gameStage;
    }

    @Override
    public void init(Entity item) {

        gameStage.sceneLoader.getEngine().addSystem(new ParticleLifespanSystem());

        i = 0;
        j = 0;
        resultScreenItem = new ItemWrapper(item);
        gameStage.sceneLoader.addComponentsByTagName(BUTTON_TAG, ButtonComponent.class);

        txtTotalE = resultScreenItem.getChild(LBL_TOTAL).getEntity();
        txtEarnedE = resultScreenItem.getChild(LBL_YOU_EARNED).getEntity();
        progressBarE = resultScreenItem.getChild(IMG_PROGRESS_BAR).getEntity();
        txtBestE = resultScreenItem.getChild(LBL_BET_SCORE).getEntity();
        txtNeedE = resultScreenItem.getChild(LBL_TO_UNLOCK).getEntity();

        initResultScreen();
        if (timer == null) {
            timer = new TrialTimer(gameStage, resultScreenItem, 140, 289);
        }
        resultScreenItem.getComponent(NodeComponent.class).addChild(timer.timerE);

        for (Entity e2 : resultScreenItem.getComponent(NodeComponent.class).children) {
            if (e2.getComponent(ActionComponent.class) == null) {
                e2.add(new ActionComponent());
            }
            e2.getComponent(ActionComponent.class).dataArray.add(Actions.sequence(Actions.moveBy(0, +100, 0), Actions.moveBy(0, -100, 0.5f, Interpolation.exp5)));
        }

        if (resultScreenItem.getChild("curtain_result").getEntity().getComponent(ActionComponent.class) == null) {
            resultScreenItem.getChild("curtain_result").getEntity().add(new ActionComponent());
        }
        resultScreenItem.getChild("curtain_result").getEntity().getComponent(TintComponent.class).color.a = 1;
        resultScreenItem.getChild("curtain_result").getEntity().getComponent(ActionComponent.class).dataArray.add(Actions.fadeOut(0.4f));
    }

    public void initButtons() {
        initBackButton();
        initPlayBtn();
        initShopBtn();
    }

    public void initResultScreen() {
        show = false;

        LabelComponent totalLabel = txtTotalE.getComponent(LabelComponent.class);
//        LabelComponent totalLabels = txtTotalsE.getComponent(LabelComponent.class);
        totalLabel.text.replace(0, totalLabel.text.capacity(), String.valueOf(gameStage.gameScript.fpc.totalScore));
//        totalLabels.text.replace(0, totalLabels.text.capacity(), String.valueOf(gameStage.gameScript.fpc.totalScore));

        earnedLabel = txtEarnedE.getComponent(LabelComponent.class);
//        earnedLabels = txtEarnedsE.getComponent(LabelComponent.class);

        LabelComponent bestLabel = txtBestE.getComponent(LabelComponent.class);
        bestLabel.text.replace(0, bestLabel.text.capacity(), YOUR_BEST + String.valueOf(gameStage.gameScript.fpc.bestScore));

        if (showCaseVanity == null && !isWasShowcase) {
            getNeedForNextItem();
            isWasShowcase = false;
        }
        need = 0;
        if (showCaseVanity != null) {
            need = showCaseVanity.cost - gameStage.gameScript.fpc.totalScore;
        }
        setProgressBar();

        LabelComponent needLabel = txtNeedE.getComponent(LabelComponent.class);
        if (need > 0) {
            needLabel.text.replace(0, needLabel.text.capacity(), YOU_NEED + String.valueOf(need) + " TO UNLOCK NEXT ITEM");
        } else {
            needLabel.text.replace(0, needLabel.text.capacity(), YOU_UNLOCKED_NEXT_ITEM);
        }
    }

    public long getNeedForNextItem() {
        for (VanityComponent vc : gameStage.gameScript.fpc.vanities) {
            if (!vc.bought) {
                if (vc.cost >= gameStage.gameScript.fpc.totalScore) {
                    showCaseVanity = vc;
                    break;
                } else {
                    showCaseVanity = vc;
                }
            }
        }
        if (showCaseVanity != null) {
            return showCaseVanity.cost - gameStage.gameScript.fpc.totalScore;
        } else {
            return 0;
        }
    }

    private void initBackButton() {
        Entity backBtn = resultScreenItem.getChild(BTN_BACK).getEntity();
        backBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(backBtn) {
                    @Override
                    public void clicked() {
                        resultScreenItem.getChild(NEXT_ITEM_ICON).getEntity().getComponent(TintComponent.class).color.a = 1;
                        if (!showcasePopup && !show) {
                            gameStage.initMenu();
                        }
                        isWasShowcase = false;
                    }
                });
    }

    private void initPlayBtn() {
        backPlay = resultScreenItem.getChild(BTN_PLAY).getEntity();
        backPlay.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(backPlay) {
                    @Override
                    public void clicked() {
                        if (active) {
                            if (!show) {
                                resultScreenItem.getChild(NEXT_ITEM_ICON).getEntity().getComponent(TintComponent.class).color.a = 1;
                                backToGame();
                            }
                        }
                    }

                    private void backToGame() {
                        GameScreenScript.isStarted = false;
                        gameStage.initGame(0);
                        isWasShowcase = false;
                    }
                });
    }

    private void initShopBtn() {
        Entity shopBtn = resultScreenItem.getChild(BTN_SHOP).getEntity();
        shopBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(shopBtn) {
                    @Override
                    public void clicked() {
                        resultScreenItem.getChild(NEXT_ITEM_ICON).getEntity().getComponent(TintComponent.class).color.a = 1;
                        if (active)
                            if (!show) {
                                shopTransitionIsOn = true;
                                isWasShowcase = false;
                            }
                    }
                });
    }

    @Override
    public void act(float delta) {

        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
//            SaveMngr.saveStats(gameStage.gameScript.fpc);
            SaveMngr.saveStats(gameStage.gameScript.fpc);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            SaveMngr.saveStats(gameStage.gameScript.fpc);
            resultScreenItem.getChild(NEXT_ITEM_ICON).getEntity().getComponent(TintComponent.class).color.a = 1;
            gameStage.initMenu();
            isWasShowcase = false;
        }

        if (timer != null) {
            timer.update();
            if (timer.timerE != null) {
                timer.timerE.getComponent(ZIndexComponent.class).setZIndex(progressBarE.getComponent(ZIndexComponent.class).getZIndex() + 1);
            }

            if (offerPromo && active && !timer.ifShouldShowTimer()) {
                if (promoWindow == null) {
                    promoWindow = new PromoWindow(gameStage, resultScreenItem);
                }
                promoWindow.init();
                offerPromo = false;
                promoWindow.show();
                active = false;
            }
        }

        if (active && resultScreenItem.getChild("curtain_result").getEntity().getComponent(TintComponent.class).color.a <= 0) {
            if (!isWasShowcase) {
                if (i <= gameStage.gameScript.fpc.score) {
                    updateScore();
                } else {
                    earnedLabel.text.replace(0, earnedLabel.text.capacity(), YOU_EARNED + String.valueOf(gameStage.gameScript.fpc.score));
//                    earnedLabels.text.replace(0, earnedLabels.text.capacity(), YOU_EARNED + String.valueOf(gameStage.gameScript.fpc.score));
                    updateProgressBar(delta);
                }
            } else {
                earnedLabel.text.replace(0, earnedLabel.text.capacity(), YOU_EARNED + String.valueOf(gameStage.gameScript.fpc.score));
//                earnedLabels.text.replace(0, earnedLabels.text.capacity(), YOU_EARNED + String.valueOf(gameStage.gameScript.fpc.score));
                progressBarE.getComponent(DimensionsComponent.class).width = MAX_PROGRESS_BAR_WIDTH;
                txtNeedE.getComponent(LabelComponent.class).text.replace(0, txtNeedE.getComponent(LabelComponent.class).text.length, "");
            }
            if (showcase != null) {
                showcase.showFading();
            }
        }

        if (showcase != null) {
            showcase.act(delta);
        }
        if (shopTransitionIsOn && resultScreenItem.getChild("curtain_result").getEntity().getComponent(TintComponent.class).color.a >= 0) {
            shopTransitionIsOn = false;
            gameStage.initShopWithAds();
        }

    }

    private void updateProgressBar(float deltaTime) {
        DimensionsComponent dcProgressBar = progressBarE.getComponent(DimensionsComponent.class);

        if (dcProgressBar.width <= getProgressBarActualLength() &&
                dcProgressBar.width < MAX_PROGRESS_BAR_WIDTH) {
            dcProgressBar.width += PROGRESS_BAR_STEP * deltaTime * GlobalConstants.FPS;
        } else {
            if (!show && showCaseVanity != null && showCaseVanity.cost <= gameStage.gameScript.fpc.totalScore) {
//                System.out.println("INITING SHOWCASE!!");
                initShowcase();
                progressBarE = resultScreenItem.getChild(IMG_PROGRESS_BAR).getEntity();
            }
            if (!show && gameStage.gameScript.fpc.settings.shouldShowGetMoneyVideoBtnAd(gameStage, need) && adsBtn == null) {
                initWatchAdsForMoneyBtn();
            }
        }

        if (gameStage.gameScript.fpc.totalScore - gameStage.gameScript.fpc.score < 0) {
            dcProgressBar.width = 0;
        } else if (showCaseVanity == null ||
                (gameStage.gameScript.fpc.totalScore - gameStage.gameScript.fpc.score) * MAX_PROGRESS_BAR_WIDTH / showCaseVanity.cost > MAX_PROGRESS_BAR_WIDTH
                ) {
            dcProgressBar.width = MAX_PROGRESS_BAR_WIDTH;
        }
    }

    private float getProgressBarActualLength() {
        if (showCaseVanity == null) {
            return MAX_PROGRESS_BAR_WIDTH;
        }
        return gameStage.gameScript.fpc.totalScore < showCaseVanity.cost ?
                ((float) gameStage.gameScript.fpc.totalScore / (float) showCaseVanity.cost) * 100 * 6.9f :
                MAX_PROGRESS_BAR_WIDTH;
    }

    private void setProgressBar() {
        if (showCaseVanity != null) {
            progressBarE.getComponent(TintComponent.class).color.a = 1;
            DimensionsComponent dcProgressBar = progressBarE.getComponent(DimensionsComponent.class);
            long scoreDiff = (gameStage.gameScript.fpc.totalScore - gameStage.gameScript.fpc.score) * MAX_PROGRESS_BAR_WIDTH / showCaseVanity.cost;
            if (scoreDiff < 40) {
                dcProgressBar.width = 40;
            } else if (scoreDiff < MAX_PROGRESS_BAR_WIDTH) {
                dcProgressBar.width = scoreDiff;
            } else {
                dcProgressBar.width = MAX_PROGRESS_BAR_WIDTH;
            }
        }
        if (gameStage.gameScript.fpc.totalScore == 0)
            progressBarE.getComponent(TintComponent.class).color.a = 0;
    }

    private void updateScore() {
        j++;
        long counterStep = gameStage.gameScript.fpc.score / 48 > 1 ? gameStage.gameScript.fpc.score / 48 : 1;
        if (j == 2) {
            earnedLabel.text.replace(0, earnedLabel.text.capacity(), YOU_EARNED + String.valueOf(i));
//            earnedLabels.text.replace(0, earnedLabels.text.capacity(), YOU_EARNED + String.valueOf(i));
            i += counterStep;
            j = 0;
        }
    }

    private void initShowcase() {
        progressBarE.getComponent(DimensionsComponent.class).width = MAX_PROGRESS_BAR_WIDTH;
        if (!show && gameStage.gameScript.fpc.score > 0) {
            if (showcase == null) {
                showcase = new Showcase(gameStage, this);
            }
            showcase.initShowCase();
            txtNeedE.getComponent(LabelComponent.class).text.replace(0, txtNeedE.getComponent(LabelComponent.class).text.length, "");
        }
    }

    private void initWatchAdsForMoneyBtn() {

        adsBtn = resultScreenItem.getChild(BTN_WATCH_FOR_MONEY).getEntity();

        TransformComponent tc = adsBtn.getComponent(TransformComponent.class);
        tc.x = 978;
        tc.y = 250;

        adsBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(adsBtn) {
                    @Override
                    public void clicked() {
                        if (active)
                            if (!show) {
                                if (Main.mainController.isWifiConnected()) {
                                    Main.mainController.showGetMoneyVideoAd(new Runnable() {
                                        @Override
                                        public void run() {
                                            //give that money!
                                            gameStage.gameScript.fpc.totalScore = showCaseVanity.cost;
                                            System.out.println("ResultScreenScript gameStage.gameScript.fpc.totalScore: " + gameStage.gameScript.fpc.totalScore);
                                            adsBtn.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
                                            init(resultScreenItem.getEntity());
                                        }
                                    });
                                } else {
                                    gameStage.gameScript.fpc.score = (int) (showCaseVanity.cost - gameStage.gameScript.fpc.totalScore);
                                    gameStage.gameScript.fpc.totalScore = showCaseVanity.cost;

                                    adsBtn.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
                                    init(resultScreenItem.getEntity());
                                }
                            }
                    }
                });

        tc.scaleY = TENTH;
        tc.scaleX = TENTH;
        adsBtn.getComponent(TintComponent.class).color.a = TENTH;
        ActionComponent ac = gameStage.sceneLoader.engine.createComponent(ActionComponent.class);
        Actions.checkInit();
        ac.dataArray.add(Actions.parallel(
                Actions.scaleTo(1f, 1f, 0.5f, Interpolation.exp5Out),
                Actions.fadeIn(0.5f, Interpolation.exp10Out)));
        adsBtn.add(ac);
    }

    @Override
    public void dispose() {
    }

    public void reset() {
        init(resultScreenItem.getEntity());
    }

}
