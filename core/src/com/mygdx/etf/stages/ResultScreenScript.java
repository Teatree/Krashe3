package com.mygdx.etf.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.mygdx.etf.Main;
import com.mygdx.etf.entity.componets.Upgrade;
import com.mygdx.etf.entity.componets.VanityComponent;
import com.mygdx.etf.stages.ui.Showcase;
import com.mygdx.etf.stages.ui.TrialTimer;
import com.mygdx.etf.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.etf.stages.GameStage.gameScript;
import static com.mygdx.etf.stages.GameStage.sceneLoader;
import static com.mygdx.etf.utils.GlobalConstants.*;

public class ResultScreenScript implements IScript {

    public static final int MAX_PROGRESS_BAR_WIDTH = 690;
    public static final double PERCENTS_TO_OFFER_AD = 0.99;
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
    public static final String LBL_TOTAL = "lbl_TOTAL";
    public static final String LBL_YOU_EARNED = "lbl_YOU_EARNED";
    public static final String LBL_BET_SCORE = "lbl_BET_SCORE";
    public static final String LBL_TO_UNLOCK = "lbl_TO_UNLOCK";
    private static final String TRIAL_TIMER = "timer_lbl";

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
    long need;
    int i = 0;
    int j = 0;
    Entity backPlay;
    private GameStage stage;
    private ItemWrapper resultScreenItem;
    private Entity adsBtn;
    private Showcase showcase;
    private TrialTimer timer;

    public ResultScreenScript(GameStage stage) {
        this.stage = stage;
    }

    @Override
    public void init(Entity item) {

        i = 0;
        j = 0;
        resultScreenItem = new ItemWrapper(item);
        sceneLoader.addComponentsByTagName(BUTTON_TAG, ButtonComponent.class);

        txtTotalE = resultScreenItem.getChild(LBL_TOTAL).getEntity();
        txtEarnedE = resultScreenItem.getChild(LBL_YOU_EARNED).getEntity();
        progressBarE = resultScreenItem.getChild(IMG_PROGRESS_BAR).getEntity();
        txtBestE = resultScreenItem.getChild(LBL_BET_SCORE).getEntity();
        txtNeedE = resultScreenItem.getChild(LBL_TO_UNLOCK).getEntity();

        initResultScreen();
        showcase = new Showcase(resultScreenItem, this);
        if (timer == null) {
            timer = new TrialTimer(resultScreenItem, 120, 650);
        }
    }

    public void initButtons() {
        initBackButton();
        initPlayBtn();
        initShopBtn();
    }

    public void initResultScreen() {
        show = false;

        LabelComponent totalLabel = txtTotalE.getComponent(LabelComponent.class);
        totalLabel.text.replace(0, totalLabel.text.capacity(), TOTAL + String.valueOf(gameScript.fpc.totalScore));

        earnedLabel = txtEarnedE.getComponent(LabelComponent.class);

        LabelComponent bestLabel = txtBestE.getComponent(LabelComponent.class);
        bestLabel.text.replace(0, bestLabel.text.capacity(), YOUR_BEST + String.valueOf(gameScript.fpc.bestScore));

        if (showCaseVanity == null && !isWasShowcase) {
            getNeedForNextItem();
            isWasShowcase = false;
        }
        need = showCaseVanity.cost - gameScript.fpc.totalScore;
        setProgressBar();

        LabelComponent needLabel = txtNeedE.getComponent(LabelComponent.class);
        if (need > 0) {
            needLabel.text.replace(0, needLabel.text.capacity(), YOU_NEED + String.valueOf(need) + " TO UNLOCK NEXT ITEM");
        } else {
            needLabel.text.replace(0, needLabel.text.capacity(), YOU_UNLOCKED_NEXT_ITEM);
        }
    }

    private long getNeedForNextItem() {
        for (VanityComponent vc : gameScript.fpc.vanities) {
            if (!vc.bought) {
                if (vc.cost >= gameScript.fpc.totalScore) {
                    showCaseVanity = vc;
                    break;
                } else {
                    showCaseVanity = vc;
                }
            }
        }
        return showCaseVanity.cost - gameScript.fpc.totalScore;
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
                    stage.initShopWithAds();
                    isWasShowcase = false;
                }
            }
        });
    }

    @Override
    public void act(float delta) {
        timer.timer();
        if (!isWasShowcase) {
            if (i <= gameScript.fpc.score) {
                updateScore();
            } else {
                earnedLabel.text.replace(0, earnedLabel.text.capacity(), YOU_EARNED + String.valueOf(gameScript.fpc.score));
                updateProgressBar(delta);
            }
        } else {
            earnedLabel.text.replace(0, earnedLabel.text.capacity(), YOU_EARNED + String.valueOf(gameScript.fpc.score));
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
            if (!show && showCaseVanity != null && showCaseVanity.cost <= gameScript.fpc.totalScore) {
                initShowcase();
                progressBarE = resultScreenItem.getChild(IMG_PROGRESS_BAR).getEntity();
//                setProgressBar();
            }
            if (!show && gameScript.fpc.settings.shouldShowGetMoneyVideoBtnAd(need) && adsBtn == null) {
                initWatchAdsForMoneyBtn();
            }
        }

        if (gameScript.fpc.totalScore - gameScript.fpc.score < 0) {
            dcProgressBar.width = 0;
        } else if ((gameScript.fpc.totalScore - gameScript.fpc.score) * MAX_PROGRESS_BAR_WIDTH / showCaseVanity.cost > MAX_PROGRESS_BAR_WIDTH) {
            dcProgressBar.width = MAX_PROGRESS_BAR_WIDTH;
        }
    }

    private float getProgressBarActualLength() {
        return gameScript.fpc.totalScore < showCaseVanity.cost ?
                ((float) gameScript.fpc.totalScore / (float) showCaseVanity.cost) * 100 * 6.9f :
                MAX_PROGRESS_BAR_WIDTH;
    }

    private void setProgressBar() {
        DimensionsComponent dcProgressBar = progressBarE.getComponent(DimensionsComponent.class);
        long scoreDiff = (gameScript.fpc.totalScore - gameScript.fpc.score) * MAX_PROGRESS_BAR_WIDTH / showCaseVanity.cost;
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
        long counterStep = gameScript.fpc.score / 48 > 1 ? gameScript.fpc.score / 48 : 1;
        if (j == 2) {
            earnedLabel.text.replace(0, earnedLabel.text.capacity(), YOU_EARNED + String.valueOf(i));
            i += counterStep;
            j = 0;
        }
    }

    private void initShowcase() {
        progressBarE.getComponent(DimensionsComponent.class).width = MAX_PROGRESS_BAR_WIDTH;
        if (!show && gameScript.fpc.score > 0) {
            showcase.initShowCase();
            txtNeedE.getComponent(LabelComponent.class).text.replace(0, txtNeedE.getComponent(LabelComponent.class).text.length, "");
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
                    if (Main.mainController.isWifiConnected()) {
                        Main.mainController.showGetMoneyVideoAd(new Runnable() {
                            @Override
                            public void run() {
                                //give that money!
                                gameScript.fpc.totalScore = showCaseVanity.cost;
                                adsBtn.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
                                init(resultScreenItem.getEntity());
                            }
                        });
                    } else {
                        gameScript.fpc.score = showCaseVanity.cost - gameScript.fpc.totalScore;
                        gameScript.fpc.totalScore = showCaseVanity.cost;

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
//
//
//    private void timer(ItemWrapper gameItem) {
//        final Entity timerE = gameItem.getChild(TRIAL_TIMER).getEntity();
//        boolean showTimer = false;
//        if (fpc.currentPet != null && fpc.currentPet.tryPeriod) {
//            showTimer = showTimer(timerE, fpc.currentPet.logoName);
//        } else {
//            for (Upgrade u : fpc.upgrades.values()) {
//                if (u.tryPeriod) {
//                    showTimer(timerE, u.logoName);
//                }
//            }
//        }
//        if (!showTimer) {
//            timerE.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
//            timerLogo.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
//            GameStage.sceneLoader.getEngine().removeEntity(timerLogo);
//            timerLogo = null;
//        }
//    }
//
//    private boolean showTimer(Entity timerE, String logoname) {
//        LabelComponent lc = timerE.getComponent(LabelComponent.class);
//        boolean showTimer;
//        lc.text.replace(0, lc.text.length, fpc.currentPet.updateTryPeriodTimer());
//        showTimer = true;
//        addTimerLogo(logoname);
//        timerE.getComponent(TransformComponent.class).x = 160;
//        timerE.getComponent(TransformComponent.class).y = 660;
//        timerLogo.getComponent(ZIndexComponent.class).setZIndex(background.getComponent(ZIndexComponent.class).getZIndex()+5);
//        return showTimer;
//    }
//
//    private void addTimerLogo(String logoLibName) {
//        if (timerLogo == null){
//            final CompositeItemVO tempC = sceneLoader.loadVoFromLibrary(logoLibName);
//            timerLogo = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempC);
//            sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), timerLogo, tempC.composite);
//            sceneLoader.getEngine().addEntity(timerLogo);
//        }
//        timerLogo.getComponent(TransformComponent.class).x = 120;
//        timerLogo.getComponent(TransformComponent.class).y = 650;
//        timerLogo.getComponent(TransformComponent.class).scaleX = 0.4f;
//        timerLogo.getComponent(TransformComponent.class).scaleY = 0.4f;
//        timerLogo.getComponent(ZIndexComponent.class).setZIndex(background.getComponent(ZIndexComponent.class).getZIndex()+5);
//    }
}
