package com.fd.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Interpolation;
import com.fd.etf.entity.componets.Gift;
import com.fd.etf.entity.componets.Goal;
import com.fd.etf.stages.GameStage;
import com.fd.etf.utils.EffectUtils;
import com.fd.etf.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.ArrayList;
import java.util.List;

import static com.fd.etf.stages.GameScreenScript.isGameOver;
import static com.fd.etf.stages.GameScreenScript.isPause;
import static com.fd.etf.stages.ui.PauseDialog.PROGRESS;
import static com.fd.etf.stages.ui.PauseDialog.SLASH;
import static com.fd.etf.utils.EffectUtils.fade;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.fd.etf.utils.GlobalConstants.FPS;

public class GoalFeedbackScreen {

    private static final String GOALFEEDBACK = "lib_gift_feedbacker";
    private static final String ITEM_MONEY_GIFT = "itemMoneyGift";
    private static final String LBL_AMOUNT = "lbl_amount";
    public static final String GIFT_SCREEN = "lib_gift_screen";
    public static final String SPINNY_SHINE = "spinny_shine";
    public static final String GREEN_SHADE = "green_shade";
    private static final String LBL_DIALOG = "lbl_level";
    public static final String GOAL_TILE = "goalTile";
    private static final String GOAL_LIB = GOAL_TILE;
    private static final String GOAL_ANI = "goalAni";
    private static final String GOAL_PROGRESS = "goal_progress";
    private static final String TILE2_TEXT_DESC = "tile2_text_desc";
    private static final String GOAL_LBL = "goal_lbl";
    public static final String LBL_GIFT_SCREEN = "lbl_gift_screen";
    public static final String LBL_GIFT_SCREEN2 = "lbl_gift_screen2";
    public static final String LBL_TAP_TO_OPEN = "lbl_tap_to_open";
    public final String BOX_ANI = "box_ani";
    private static final int POS_X = -22;
    private static final int POS_Y = -19;

    private static final int GOAL_STEP_Y = 110;

    private static final int GOAL_INIT_POS_X = 501;
    private static final int GOAL_INIT_POS_Y = 570;
    private static final float GOAL_SCALE = 1f;

    private static final float INITIAL_DELAY = 0.7f;
    private static final float MOVE_TILES_DELAY = 0.3f;
    private static final String DEFAULT = "Default";
    private static final float INITIAL_DELAY_ANI = 0.15f;
    public static boolean shouldShow;

    private static List<Entity> tiles;
    private static List<Entity> prevLvlTiles;
    public boolean isNewLevel;
    public static boolean isGoalFeedbackOpen;
    private static Entity feedbackEntity;
    private int iNastya2;

    // Gift
    private boolean canPlayAnimation = true;
    SpriteAnimationComponent saBox;
    SpriteAnimationStateComponent sasBox;
    private float stateTime;
    private boolean isGiftShown;
    private boolean isGiftShouldShow;
    private boolean isOpeningBox;
    private boolean isAbleToProceedToResult;
    private Gift gift;
    private Entity lblTapToOpen;
    private Entity lbl;
    private Entity lbl2;
//    private Entity lblMoney;
    private Entity giftE;
    private Entity greenShadeE;
    private Entity spinnyShineE;
    private Entity giftIconE;
    private boolean canTap;
    private boolean isShading;
    private int helpTimer;
    private int boxIdleTimer;

    private GameStage gameStage;

    public GoalFeedbackScreen(GameStage gameStage) {
        this.gameStage = gameStage;
    }

    public void init(boolean isNewLevel) {
        this.isNewLevel = isNewLevel;
        if (!isNewLevel) {
            if (tiles != null) {
                for (Entity tile : tiles) {
                    tile.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
                    gameStage.sceneLoader.getEngine().removeEntity(tile);
                }
            }
        } else {
            prevLvlTiles = new ArrayList<>();
            for (Entity tile : tiles) {
                prevLvlTiles.add(tile);
            }
        }

        tiles = new ArrayList<>();

        final CompositeItemVO tempC = gameStage.sceneLoader.loadVoFromLibrary(GOALFEEDBACK);
        feedbackEntity = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), tempC);
        gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), feedbackEntity, tempC.composite);
        gameStage.sceneLoader.getEngine().addEntity(feedbackEntity);

        feedbackEntity.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
        feedbackEntity.getComponent(TransformComponent.class).y = GlobalConstants.FAR_FAR_AWAY_Y;

        gameStage.gameScript.fpc.totalScore += gameStage.gameScript.fpc.score;
        System.out.println("GoalFeedbackScreen gameStage.gameScript.fpc.score: " + gameStage.gameScript.fpc.score);
        System.out.println("GoalFeedbackScreen gameStage.gameScript.fpc.totalScore: " + gameStage.gameScript.fpc.totalScore);
    }

    public void show() {
        shouldShow = false;
        iNastya2 = 0;
        feedbackEntity.getComponent(TransformComponent.class).x = POS_X;
        feedbackEntity.getComponent(TransformComponent.class).y = POS_Y;
        feedbackEntity.getComponent(ZIndexComponent.class).setZIndex(190);

        final Entity goalLabel = new ItemWrapper(feedbackEntity).getChild(LBL_DIALOG).getEntity();
        LabelComponent goalsLabelComp = goalLabel.getComponent(LabelComponent.class);

        goalsLabelComp.text.replace(0, goalsLabelComp.text.capacity(), " \n     " + gameStage.gameScript.fpc.level.name + " \n ");

        if (tiles == null || tiles.isEmpty() || !isPause.get()) {
            int y = GOAL_INIT_POS_Y;
            for (Goal g : gameStage.gameScript.fpc.level.getGoals()) {
                tiles.add(createGoalTile(g, y));
                y -= GOAL_STEP_Y;
            }
        }
        isGoalFeedbackOpen = true;
        isGiftShouldShow = false;
    }

    public void showNewLevel() {
        shouldShow = false;
        feedbackEntity.getComponent(TransformComponent.class).x = POS_X;
        feedbackEntity.getComponent(TransformComponent.class).y = POS_Y;
        feedbackEntity.getComponent(ZIndexComponent.class).setZIndex(190);

        if (prevLvlTiles != null || !prevLvlTiles.isEmpty() || !isPause.get()) {
            addMoveInPrevTilesActions();
        }
        if (tiles == null || tiles.isEmpty() || !isPause.get()) {
            addMoveInTilesActions();
        }

        isGiftShouldShow = true;
        isGoalFeedbackOpen = true;
        isNewLevel = false;
    }

    private void addMoveInTilesActions() {
        int y = GOAL_INIT_POS_Y;
        int i = 1;
        float delay = INITIAL_DELAY + prevLvlTiles.size() * (MOVE_TILES_DELAY + INITIAL_DELAY_ANI) + 2;
        for (Goal g : gameStage.gameScript.fpc.level.getGoals()) {
            Entity newTile = createGoalTile(g, y);

            ActionComponent ac = newTile.getComponent(ActionComponent.class);
            Actions.checkInit();

            if (i == 1) {
                delay = INITIAL_DELAY + prevLvlTiles.size() * (MOVE_TILES_DELAY + INITIAL_DELAY_ANI) + 2;
            } else {
                delay += GlobalConstants.TENTH;
            }
            i++;

            ac.dataArray.add(Actions.sequence(Actions.delay(delay),
                    Actions.moveTo(GOAL_INIT_POS_X, newTile.getComponent(TransformComponent.class).y, 1.5f, Interpolation.exp10Out)));
            newTile.add(ac);
            tiles.add(newTile);
            y -= GOAL_STEP_Y;
        }
    }

    private void addMoveInPrevTilesActions() {
        int y = GOAL_INIT_POS_Y;
        int i = 1;

        float delay = 0;
        for (Entity e : prevLvlTiles) {
            e.getComponent(TransformComponent.class).x = GOAL_INIT_POS_X;
            e.getComponent(TransformComponent.class).y = y;
            e.getComponent(TintComponent.class).color.a = 1;

            ActionComponent ac = e.getComponent(ActionComponent.class);
            Actions.checkInit();

            if (i == 1) {
                delay = INITIAL_DELAY * prevLvlTiles.size();
            } else {
                delay -= MOVE_TILES_DELAY;
            }
            i++;

            ac.dataArray.add(Actions.sequence(Actions.delay(delay),
                    Actions.moveTo(e.getComponent(TransformComponent.class).x,
                            -300, 1f, Interpolation.exp10In)));
            e.add(ac);

            if (e.getComponent(NodeComponent.class) != null && e.getComponent(NodeComponent.class).children != null
                    && e.getComponent(NodeComponent.class).children.size != 0) {
                for (Entity e2 : e.getComponent(NodeComponent.class).children) {
                    TintComponent tc = e2.getComponent(TintComponent.class);
                    tc.color.a = 1;

                    e2.getComponent(TintComponent.class).color.a = 1;
                }
            }
            e.getComponent(TransformComponent.class).x = GOAL_INIT_POS_X;
            e.getComponent(ZIndexComponent.class).setZIndex(2000);
            y -= GOAL_STEP_Y;
        }
    }

    private Entity createGoalTile(final Goal goal, int y) {
        CompositeItemVO tempC = gameStage.sceneLoader.loadVoFromLibrary(GOAL_LIB).clone();

        gameStage.sceneLoader.rm.addSPRITEtoLoad(GOAL_TILE);
        final Entity tile = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), tempC);
        gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), tile, tempC.composite);
        gameStage.sceneLoader.getEngine().addEntity(tile);

        ActionComponent ac = gameStage.sceneLoader.engine.createComponent(ActionComponent.class);
        Actions.checkInit();
        tile.add(ac);

        if (isNewLevel) {
            tile.getComponent(TransformComponent.class).x = Gdx.graphics.getWidth() + 1000;
        } else {
            tile.getComponent(TransformComponent.class).x = POS_X + GOAL_INIT_POS_X;
        }
        tile.getComponent(TransformComponent.class).y = y;
        tile.getComponent(TransformComponent.class).scaleY = GOAL_SCALE;

        String goalProgressValue;
        NodeComponent nc = tile.getComponent(NodeComponent.class);
        int iNastya = 0;
        while (iNastya < nc.children.size) {
            Entity e = nc.children.get(iNastya);
            //set progress label
            if (e.getComponent(MainItemComponent.class).itemIdentifier.equals(GOAL_PROGRESS)) {
                if (goal.getCounter() >= goal.getN()) {
                    goalProgressValue = PauseDialog.COMPLETED;
                } else {
                    goalProgressValue = PROGRESS + String.valueOf(goal.getCounter() + SLASH + goal.getN());
                }
                e.getComponent(LabelComponent.class).text.replace(0, e.getComponent(LabelComponent.class).text.capacity(),
                        goalProgressValue);
            }

            //set goal desc label
            if (e.getComponent(MainItemComponent.class).itemIdentifier.equals(TILE2_TEXT_DESC)) {
                e.getComponent(NodeComponent.class).children.get(0).getComponent(LabelComponent.class).text.replace(0, e.getComponent(LabelComponent.class).text.capacity(),
                        goal.getDescription());
            }

            final SpriteAnimationStateComponent sc = tile.getComponent(NodeComponent.class).getChild(GOAL_ANI).getComponent(SpriteAnimationStateComponent.class);
            SpriteAnimationComponent s = tile.getComponent(NodeComponent.class).getChild(GOAL_ANI).getComponent(SpriteAnimationComponent.class);
            sc.paused = true;
            if (goal.achieved) {
                if (goal.justAchieved) {
                    tile.getComponent(ActionComponent.class).dataArray.add(
                            Actions.sequence(
                                    Actions.delay(INITIAL_DELAY_ANI * iNastya2),
                                    Actions.run(new Runnable() {
                                        @Override
                                        public void run() {
                                            sc.paused = false;
                                            sc.currentAnimation.setPlayMode(Animation.PlayMode.NORMAL);
                                            goal.justAchieved = false;
                                            // gameScript.fpc.level.getGoals().get(iNastyaChild).justAchieved = false;
                                        }
                                    })));

                    iNastya2++;

                } else {
                    sc.set(s.frameRangeMap.get(DEFAULT), 0, Animation.PlayMode.REVERSED);
                    sc.paused = true;
                }
            } else {
                sc.paused = true;
            }
            iNastya++;
        }
        tile.getComponent(ZIndexComponent.class).setZIndex(200);
        return tile;
    }

    public void update() {
        if (isGoalFeedbackOpen) {
            fade(feedbackEntity, isGoalFeedbackOpen);
            updateLevelLabel();
            doWhenAllGoalsAchieved();

            if (Gdx.input.justTouched() && isGoalFeedbackOpen && !isGiftShown && !isGiftShouldShow) {
                if (!gameStage.gameScript.fpc.level.checkAllGoals()) {
                    hideGoalFeedback();
                    gameStage.gameScript.gameStage.initResultWithAds();
                }
            }

            if (isGiftShouldShow && prevLvlTiles != null && prevLvlTiles.get(0).getComponent(TransformComponent.class).y < -100) {
                showGift();
            }
        }
    }

    private void doWhenAllGoalsAchieved() {
        if (gameStage.gameScript.fpc.level.checkAllGoals()) {
            // Temp2
            gameStage.gameScript.fpc.level.updateLevel(gameStage.gameScript.fpc);
            isNewLevel = true;
            prevLvlTiles = new ArrayList<>();

            for (Entity tile : tiles) {
                prevLvlTiles.add(tile);
            }

            tiles = new ArrayList<>();
            showNewLevel();
            PauseDialog.setToUpdate();
        }
    }

    public void hideGoalFeedback() {
        isGoalFeedbackOpen = false;

        feedbackEntity.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
        feedbackEntity.getComponent(TransformComponent.class).y = GlobalConstants.FAR_FAR_AWAY_Y;
        gameStage.gameScript.gameStage.sceneLoader.getEngine().removeEntity(feedbackEntity);
        prevLvlTiles = null;
        for (Entity tile : tiles) {
            tile.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
        }
        gameStage.gameScript.gameStage.initResultWithAds();
    }

    private void updateLevelLabel() {
        if (prevLvlTiles != null &&
                prevLvlTiles.get(0).getComponent(TransformComponent.class).y <=
                        -289) {
            final Entity goalLabel = new ItemWrapper(feedbackEntity).getChild(LBL_DIALOG).getEntity();

            LabelComponent goalsLabelComp = goalLabel.getComponent(LabelComponent.class);
            if (!goalsLabelComp.text.toString().equals(gameStage.gameScript.fpc.level.name)) {
                EffectUtils.playYellowStarsParticleEffect(gameStage, goalLabel.getComponent(TransformComponent.class).x,
                        goalLabel.getComponent(TransformComponent.class).y);
                goalsLabelComp.text.replace(0, goalsLabelComp.text.capacity(),
                        gameStage.gameScript.fpc.level.name);
            }
        }
    }

    private void showGift() {
        if (!isGiftShown) {
            isGiftShown = true;
            gift = Gift.getRandomGift(gameStage);

            helpTimer = 0;
            boxIdleTimer = 0;
            isAbleToProceedToResult = false;
            final CompositeItemVO tempC = gameStage.sceneLoader.loadVoFromLibrary(GIFT_SCREEN);
            giftE = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), tempC);
            gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), giftE, tempC.composite);
            gameStage.sceneLoader.getEngine().addEntity(giftE);
            spinnyShineE = new ItemWrapper(giftE).getChild(SPINNY_SHINE).getEntity();
            spinnyShineE.getComponent(TintComponent.class).color.a = 0;
            greenShadeE = new ItemWrapper(giftE).getChild(GREEN_SHADE).getEntity();
            greenShadeE.getComponent(TintComponent.class).color.a = 0;
            giftE.getComponent(ZIndexComponent.class).setZIndex(190);
            giftE.getComponent(TransformComponent.class).x = -20;
            giftE.getComponent(TransformComponent.class).y = -20;

            lblTapToOpen = new ItemWrapper(giftE).getChild(LBL_TAP_TO_OPEN).getEntity();
            lblTapToOpen.getComponent(TintComponent.class).color.a = 0;
            lblTapToOpen.getComponent(ZIndexComponent.class).setZIndex(greenShadeE.getComponent(ZIndexComponent.class).getZIndex() - 1);
            lbl = new ItemWrapper(giftE).getChild(LBL_GIFT_SCREEN).getEntity();
            lbl.getComponent(TintComponent.class).color.a = 0;
            lbl2 = new ItemWrapper(giftE).getChild(LBL_GIFT_SCREEN2).getEntity();
            lbl2.getComponent(TintComponent.class).color.a = 0;
            ActionComponent ac = gameStage.sceneLoader.engine.createComponent(ActionComponent.class);
            Actions.checkInit();
            ac.dataArray.add(Actions.rotateBy(20000, 400));
            spinnyShineE.add(ac);
            isShading = false;

            Entity boxAniE = new ItemWrapper(giftE).getChild(BOX_ANI).getEntity();
            saBox = boxAniE.getComponent(SpriteAnimationComponent.class);
            sasBox = boxAniE.getComponent(SpriteAnimationStateComponent.class);
        }
        helpTimer++;
        if (saBox.currentAnimation != "open") {
            boxIdleTimer++;
        }

        if (saBox.currentAnimation != "open" && spinnyShineE.getComponent(TintComponent.class).color.a < 0.96f) {
            spinnyShineE.getComponent(TintComponent.class).color.a += 0.03f;
            if (spinnyShineE.getComponent(TintComponent.class).color.a < 0.76f) {
                canTap = true;
            }
        }
        if (Gdx.input.justTouched() && canTap) {
            canPlayAnimation = true;
            setAnimation("open", Animation.PlayMode.NORMAL, sasBox, saBox);
            isOpeningBox = true;
            canTap = false;
        }

        if (saBox.currentAnimation == "open") {
            stateTime += Gdx.graphics.getDeltaTime();
            if (sasBox.get().isAnimationFinished(stateTime) && isOpeningBox) {
                isOpeningBox = false;
                showGiftIcon(gift);
            }
        }

        if (giftIconE != null && giftIconE.getComponent(TransformComponent.class).x > 420) {
            isAbleToProceedToResult = true;
        }

        if (isShading && greenShadeE.getComponent(TintComponent.class).color.a < 0.98f) {
            greenShadeE.getComponent(TintComponent.class).color.a += 0.02f;
            lbl.getComponent(ZIndexComponent.class).setZIndex(greenShadeE.getComponent(ZIndexComponent.class).getZIndex()+1);
            lbl.getComponent(TintComponent.class).color.a += 0.02f;
            if (gift.pet != null || gift.upgrade != null){
                lbl2.getComponent(ZIndexComponent.class).setZIndex(greenShadeE.getComponent(ZIndexComponent.class).getZIndex()+1);
                lbl2.getComponent(TintComponent.class).color.a += 0.02f;
            }
        }

        if (helpTimer > 250 && saBox.currentAnimation != "open") {
            lblTapToOpen.getComponent(TintComponent.class).color.a += 0.05f;
//            System.out.println("showing tap to open");
        } else if (saBox.currentAnimation == "open") {
//            helpTimer = 0;
//            System.out.println("NOT showing tap to open");
            lblTapToOpen.getComponent(TintComponent.class).color.a -= 0.05f;
        }

        if (boxIdleTimer > 270 && saBox.currentAnimation != "open") {
            canPlayAnimation = true;
            setAnimation("idle", Animation.PlayMode.NORMAL, sasBox, saBox);
            boxIdleTimer = 0;
        }

        if (isAbleToProceedToResult && Gdx.input.justTouched()) {
            hideGift();
        }

//        if (gameScript.giftScreen == null) {
//            gameScript.giftScreen = new GiftScreen();
//        }
//        gameScript.giftScreen.init();
//        gameScript.giftScreen.show();
    }

    public void setAnimation(String animationName, Animation.PlayMode mode, SpriteAnimationStateComponent sasComponent, SpriteAnimationComponent saComponent) {
        if (canPlayAnimation) {
            sasComponent.set(saComponent.frameRangeMap.get(animationName), FPS, mode);
            saComponent.currentAnimation = animationName;
            canPlayAnimation = false;
        }
    }

    private void showGiftIcon(Gift gift) {
        if (gift.pet != null || gift.upgrade != null) {

            lbl.getComponent(LabelComponent.class).text.replace(0,
                    lbl.getComponent(LabelComponent.class).text.capacity(),
                    "YOU GOT A " + gift.type + " !!!");
            if (gift.pet != null) {
                lbl2.getComponent(LabelComponent.class).text.replace(0,
                        lbl2.getComponent(LabelComponent.class).text.capacity(),
                        "IT EXPIRES IN " + gift.pet.getTimerTimeTime() + " !!!");
            }
            if (gift.upgrade != null) {
                lbl2.getComponent(LabelComponent.class).text.replace(0,
                        lbl2.getComponent(LabelComponent.class).text.capacity(),
                        "IT EXPIRES IN " + gift.upgrade.getTimerTimeTime() + " !!!");
            }

        } else {
            lbl.getComponent(LabelComponent.class).text.replace(0,
                    lbl.getComponent(LabelComponent.class).text.capacity(),
                    "YOU GOT " + gift.money + " " + gift.type + " !!!");
        }

        if (gift.pet != null) {
            CompositeItemVO tempItemC = gameStage.sceneLoader.loadVoFromLibrary(gift.pet.shopIcon);
            giftIconE = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), tempItemC);
            gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), giftIconE, tempItemC.composite);
            gameStage.sceneLoader.getEngine().addEntity(giftIconE);
            giftIconE.getComponent(ZIndexComponent.class).setZIndex(200);
            giftIconE.getComponent(TransformComponent.class).x = 200;
            giftIconE.getComponent(TransformComponent.class).y = 329;
        } else if (gift.upgrade != null) {
            CompositeItemVO tempItemC = gameStage.sceneLoader.loadVoFromLibrary(gift.upgrade.shopIcon);
            giftIconE = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), tempItemC);
            gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), giftIconE, tempItemC.composite);
            gameStage.sceneLoader.getEngine().addEntity(giftIconE);
            giftIconE.getComponent(ZIndexComponent.class).setZIndex(100);
            giftIconE.getComponent(TransformComponent.class).x = 100;
            giftIconE.getComponent(TransformComponent.class).y = 329;
        } else {
            CompositeItemVO tempItemC = gameStage.sceneLoader.loadVoFromLibrary(ITEM_MONEY_GIFT);
            giftIconE = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), tempItemC);
            gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), giftIconE, tempItemC.composite);
            gameStage.sceneLoader.getEngine().addEntity(giftIconE);
            String money = Integer.toString(gift.money);
            giftIconE.getComponent(NodeComponent.class).getChild(LBL_AMOUNT).getComponent(LabelComponent.class).setText(money);
            giftIconE.getComponent(ZIndexComponent.class).setZIndex(200);
            giftIconE.getComponent(TransformComponent.class).x = 100;
            giftIconE.getComponent(TransformComponent.class).y = 329;
        }

        ActionComponent ac = gameStage.sceneLoader.engine.createComponent(ActionComponent.class);
        Actions.checkInit();
        ac.dataArray.add(Actions.moveTo(435, 439, 2f, Interpolation.exp5));
        giftIconE.add(ac);
        isShading = true;
    }

    public void hideGift() {
        giftE.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
        giftE.getComponent(TransformComponent.class).y = GlobalConstants.FAR_FAR_AWAY_Y;
        isGiftShown = false;
        giftIconE.getComponent(TransformComponent.class).x = -FAR_FAR_AWAY_X;
        isGameOver.set(false);
        stateTime = 0;
        isGoalFeedbackOpen = false;
        hideGoalFeedback();
        gift.takeGift(gameStage, gameStage.gameScript.fpc);
    }
}
