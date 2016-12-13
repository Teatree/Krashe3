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
import static com.fd.etf.stages.GameStage.gameScript;
import static com.fd.etf.stages.GameStage.sceneLoader;
import static com.fd.etf.stages.ui.PauseDialog.PROGRESS;
import static com.fd.etf.stages.ui.PauseDialog.SLASH;
import static com.fd.etf.utils.EffectUtils.fade;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.fd.etf.utils.GlobalConstants.FPS;

public class GoalFeedbackScreen {

    public static final String GOALFEEDBACK = "lib_gift_feedbacker";
    private static final String ITEM_MONEY_GIFT = "itemMoneyGift";
    private static final String LBL_AMOUNT = "lbl_amount";
    public final String GIFT_SCREEN = "lib_gift_screen";
    public final String SPINNY_SHINE = "spinny_shine";
    public final String GREEN_SHADE = "green_shade";
    public static final String LBL_DIALOG = "lbl_level";
    public static final String LBL_DIALOG_SH = "lbl_level_sh";
    public static final String GOAL_LIB = "goalTile";
    public static final String GOAL_ANI = "goalAni";
    public static final String GOAL_PROGRESS = "goal_progress";
    public static final String GOAL_LBL = "goal_lbl";
    public final String LBL_GIFT_SCREEN = "lbl_gift_screen";
    public final String LBL_TAP_TO_OPEN = "lbl_tap_to_open";
    public final String BOX_ANI = "box_ani";
    public static final int POS_X = -22;
    public static final int POS_Y = -19;

    public static final int GOAL_STEP_Y = 110;

    public static final int GOAL_INIT_POS_X = 501;
    public static final int GOAL_INIT_POS_Y = 570;
    public static final float GOAL_SCALE = 1f;

    public static final float INITIAL_DELAY = 0.7f;
    public static final float MOVE_TILES_DELAY = 0.3f;
    public static final String DEFAULT = "Default";
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
    private Entity lblMoney;
    private Entity giftE;
    private Entity greenShadeE;
    private Entity spinnyShineE;
    private Entity giftIconE;
    private boolean canTap;
    private boolean isShading;
    private int helpTimer;

    public void init(boolean isNewLevel) {
        this.isNewLevel = isNewLevel;
        if (!isNewLevel) {
            if (tiles != null) {
                for (Entity tile : tiles) {
                    tile.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
                    sceneLoader.getEngine().removeEntity(tile);
                }
            }
        } else {
            prevLvlTiles = new ArrayList<>();
            for (Entity tile : tiles) {
                prevLvlTiles.add(tile);
            }
        }

        tiles = new ArrayList<>();

        final CompositeItemVO tempC = sceneLoader.loadVoFromLibrary(GOALFEEDBACK);
        feedbackEntity = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), feedbackEntity, tempC.composite);
        sceneLoader.getEngine().addEntity(feedbackEntity);

        feedbackEntity.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
        feedbackEntity.getComponent(TransformComponent.class).y = GlobalConstants.FAR_FAR_AWAY_Y;
    }

    public void show() {
        shouldShow = false;
        iNastya2 = 0;
        feedbackEntity.getComponent(TransformComponent.class).x = POS_X;
        feedbackEntity.getComponent(TransformComponent.class).y = POS_Y;
        feedbackEntity.getComponent(ZIndexComponent.class).setZIndex(190);

        final Entity goalLabel = new ItemWrapper(feedbackEntity).getChild(LBL_DIALOG).getEntity();
        final Entity goalLabelSh = new ItemWrapper(feedbackEntity).getChild(LBL_DIALOG_SH).getEntity();
        LabelComponent goalsLabelComp = goalLabel.getComponent(LabelComponent.class);
        LabelComponent goalsLabelShComp = goalLabelSh.getComponent(LabelComponent.class);

        goalsLabelComp.text.replace(0, goalsLabelComp.text.capacity(), " \n     " + gameScript.fpc.level.name + " \n ");
        goalsLabelShComp.text.replace(0, goalsLabelShComp.text.capacity(), " \n     " + gameScript.fpc.level.name + " \n ");

        if (tiles == null || tiles.isEmpty() || !isPause.get()) {
            int y = GOAL_INIT_POS_Y;
            for (Goal g : gameScript.fpc.level.getGoals()) {
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
        for (Goal g : gameScript.fpc.level.getGoals()) {
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
        CompositeItemVO tempC = sceneLoader.loadVoFromLibrary(GOAL_LIB).clone();

        sceneLoader.rm.addSPRITEtoLoad("goalTile");
        final Entity tile = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), tile, tempC.composite);
        sceneLoader.getEngine().addEntity(tile);

        ActionComponent ac = new ActionComponent();
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
            if (e.getComponent(MainItemComponent.class).itemIdentifier.equals(GOAL_LBL)) {
                e.getComponent(LabelComponent.class).text.replace(0, e.getComponent(LabelComponent.class).text.capacity(),
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
            System.out.println("isGiftShouldShow: " + isGiftShouldShow);

            if (Gdx.input.justTouched() && isGoalFeedbackOpen && !isGiftShown && !isGiftShouldShow) {
                if (!gameScript.fpc.level.checkAllGoals() /*&& !(gameScript.giftScreen != null && gameScript.giftScreen.isGiftScreenOpen)*/) {
                    hideGoalFeedback();
                    gameScript.stage.initResultWithAds();
                    System.out.println("INIT RESULT WITH ADS BECAUSE FUCK YOU!");
                }
            }

            if(isGiftShouldShow && prevLvlTiles != null && prevLvlTiles.get(0).getComponent(TransformComponent.class).y < -100){
                showGift();
                System.out.println("showing Gift");
            }
        }
    }

    private void doWhenAllGoalsAchieved() {
        if (gameScript.fpc.level.checkAllGoals()) {
            // Temp2
            GameStage.gameScript.fpc.level.updateLevel(GameStage.gameScript.fpc);
            isNewLevel = true;
            prevLvlTiles = new ArrayList<>();

            for (Entity tile : tiles) {
                prevLvlTiles.add(tile);
            }

            tiles = new ArrayList<>();
            showNewLevel();
        }
    }

    public static void hideGoalFeedback() {
        isGoalFeedbackOpen = false;

        feedbackEntity.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
        feedbackEntity.getComponent(TransformComponent.class).y = GlobalConstants.FAR_FAR_AWAY_Y;
        sceneLoader.getEngine().removeEntity(feedbackEntity);
        prevLvlTiles = null;
        for (Entity tile : tiles) {
            tile.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
        }
        gameScript.stage.initResultWithAds();
    }

    private void updateLevelLabel() {
        if (prevLvlTiles != null &&
                prevLvlTiles.get(0).getComponent(TransformComponent.class).y <=
                        -289) {
            final Entity goalLabel = new ItemWrapper(feedbackEntity).getChild(LBL_DIALOG).getEntity();
            final Entity goalLabelSh = new ItemWrapper(feedbackEntity).getChild(LBL_DIALOG_SH).getEntity();

            LabelComponent goalsLabelComp = goalLabel.getComponent(LabelComponent.class);
            LabelComponent goalsLabelShComp = goalLabelSh.getComponent(LabelComponent.class);
            if (!goalsLabelComp.text.toString().equals(gameScript.fpc.level.name)) {
                EffectUtils.playYellowStarsParticleEffect(goalLabel.getComponent(TransformComponent.class).x,
                        goalLabel.getComponent(TransformComponent.class).y);
                goalsLabelComp.text.replace(0, goalsLabelComp.text.capacity(), gameScript.fpc.level.name);
            }
            if (!goalsLabelShComp.text.toString().equals(gameScript.fpc.level.name)) {
                EffectUtils.playYellowStarsParticleEffect(goalLabelSh.getComponent(TransformComponent.class).x,
                        goalLabelSh.getComponent(TransformComponent.class).y);
                goalsLabelShComp.text.replace(0, goalsLabelShComp.text.capacity(), gameScript.fpc.level.name);
            }
        }
    }

    private void showGift() {
        //init

        if(!isGiftShown){
            System.out.println("ENTER GIFT SIDE");
            isGiftShown = true;
            canTap = true;
            gift = Gift.getRandomGift();

            helpTimer = 0;
            isAbleToProceedToResult = false;
            final CompositeItemVO tempC = sceneLoader.loadVoFromLibrary(GIFT_SCREEN);
            giftE = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempC);
            sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), giftE, tempC.composite);
            sceneLoader.getEngine().addEntity(giftE);
            spinnyShineE = new ItemWrapper(giftE).getChild(SPINNY_SHINE).getEntity();
            spinnyShineE.getComponent(TintComponent.class).color.a = 0;
            greenShadeE = new ItemWrapper(giftE).getChild(GREEN_SHADE).getEntity();
            greenShadeE.getComponent(TintComponent.class).color.a = 0;
            giftE.getComponent(ZIndexComponent.class).setZIndex(190);
            giftE.getComponent(TransformComponent.class).x = -20;
            giftE.getComponent(TransformComponent.class).y = -20;

            lblTapToOpen = new ItemWrapper(giftE).getChild(LBL_TAP_TO_OPEN).getEntity();
            lblTapToOpen.getComponent(TintComponent.class).color.a = 0;
            lbl = new ItemWrapper(giftE).getChild(LBL_GIFT_SCREEN).getEntity();
            lbl.getComponent(TintComponent.class).color.a = 0;
            ActionComponent ac = new ActionComponent();
            Actions.checkInit();
            ac.dataArray.add(Actions.rotateBy(20000, 400));
            spinnyShineE.add(ac);
            isShading = false;

            Entity boxAniE = new ItemWrapper(giftE).getChild(BOX_ANI).getEntity();
            saBox = boxAniE.getComponent(SpriteAnimationComponent.class);
            sasBox = boxAniE.getComponent(SpriteAnimationStateComponent.class);
        }
        helpTimer++;

        if (saBox.currentAnimation != "open" && spinnyShineE.getComponent(TintComponent.class).color.a < 0.96f){
            spinnyShineE.getComponent(TintComponent.class).color.a += 0.03f;
        }

        if(Gdx.input.justTouched() && canTap){
            System.out.println("touched!");
            setAnimation("open", Animation.PlayMode.NORMAL, sasBox, saBox);
            isOpeningBox = true;
            canTap = false;
        }

        if(saBox.currentAnimation == "open") {
            stateTime += Gdx.graphics.getDeltaTime();
            if (sasBox.get().isAnimationFinished(stateTime) && isOpeningBox) {
                isOpeningBox = false;
                showGiftIcon(gift);
            }
        }

        if(giftIconE != null && giftIconE.getComponent(TransformComponent.class).x > 420){
            isAbleToProceedToResult = true;
        }

        if(isShading && greenShadeE.getComponent(TintComponent.class).color.a < 0.98f){
            greenShadeE.getComponent(TintComponent.class).color.a += 0.02f;
            lbl.getComponent(TintComponent.class).color.a += 0.02f;
        }

        if(helpTimer>250 && saBox.currentAnimation != "open"){
            lblTapToOpen.getComponent(TintComponent.class).color.a += 0.05f;
        }else if(saBox.currentAnimation == "open"){
//            helpTimer = 0;
            lblTapToOpen.getComponent(TintComponent.class).color.a -= 0.05f;
        }

        if(isAbleToProceedToResult && Gdx.input.justTouched()){
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
        } else {
            lbl.getComponent(LabelComponent.class).text.replace(0,
                    lbl.getComponent(LabelComponent.class).text.capacity(),
                    "YOU GOT " + gift.money + " " + gift.type + " !!!");
        }

        if (gift.pet != null) {
            CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary(gift.pet.shopIcon);
            giftIconE = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
            GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), giftIconE, tempItemC.composite);
            GameStage.sceneLoader.getEngine().addEntity(giftIconE);
            giftIconE.getComponent(ZIndexComponent.class).setZIndex(200);
            giftIconE.getComponent(TransformComponent.class).x = 200;
            giftIconE.getComponent(TransformComponent.class).y = 329;
        } else if (gift.upgrade != null) {
            CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary(gift.upgrade.shopIcon);
            giftIconE = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
            GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), giftIconE, tempItemC.composite);
            GameStage.sceneLoader.getEngine().addEntity(giftIconE);
            giftIconE.getComponent(ZIndexComponent.class).setZIndex(100);
            giftIconE.getComponent(TransformComponent.class).x = 100;
            giftIconE.getComponent(TransformComponent.class).y = 329;
        } else {
            CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary(ITEM_MONEY_GIFT);
            giftIconE = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
            GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), giftIconE, tempItemC.composite);
            GameStage.sceneLoader.getEngine().addEntity(giftIconE);
            String money = Integer.toString(gift.money);
            giftIconE.getComponent(NodeComponent.class).getChild(LBL_AMOUNT).getComponent(LabelComponent.class).setText(money);
            giftIconE.getComponent(ZIndexComponent.class).setZIndex(200);
            giftIconE.getComponent(TransformComponent.class).x = 100;
            giftIconE.getComponent(TransformComponent.class).y = 329;
        }

        ActionComponent ac = new ActionComponent();
        Actions.checkInit();
        ac.dataArray.add(Actions.moveTo(435, 439, 2f, Interpolation.exp5));
        giftIconE.add(ac);
        isShading = true;
    }

    public void hideGift() {
        giftE.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
        giftE.getComponent(TransformComponent.class).y = GlobalConstants.FAR_FAR_AWAY_Y;
        isGiftShown = false;
        giftIconE.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        isGameOver.set(false);
        isGoalFeedbackOpen = false;
        hideGoalFeedback();
        gameScript.stage.initResultWithAds();
    }
}
