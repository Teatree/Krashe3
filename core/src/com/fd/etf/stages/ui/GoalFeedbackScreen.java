package com.fd.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Interpolation;
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

import static com.fd.etf.stages.GameScreenScript.isPause;
import static com.fd.etf.stages.GameStage.gameScript;
import static com.fd.etf.stages.GameStage.sceneLoader;
import static com.fd.etf.stages.ui.PauseDialog.PROGRESS;
import static com.fd.etf.stages.ui.PauseDialog.SLASH;
import static com.fd.etf.utils.EffectUtils.fade;

public class GoalFeedbackScreen {

    public static final String GOALFEEDBACK = "lib_gift_feedbacker";
    public static final String LBL_DIALOG = "lbl_level";
    public static final String GOAL_LIB = "goalTile";
    public static final String GOAL_PROGRESS = "goal_progress";
    public static final String GOAL_LBL = "goal_lbl";
    public static final int POS_X = -22;
    public static final int POS_Y = -19;

    public static final int GOAL_STEP_Y = 110;

    public static final int GOAL_INIT_POS_X = 591;
    public static final int GOAL_INIT_POS_Y = 500;
    public static final float GOAL_SCALE = 1f;

    public static final int DELAY_ON_ANIMATION = 5000;
    public static final float INITIAL_DELAY = 1.2f;
    public static final float MOVE_TILES_DELAY = 0.3f;
    public static final String DEFAULT = "Default";
    public static boolean shouldShow;

    private List<Entity> tiles;
    private List<Entity> prevLvlTiles;
    public boolean isAniPlaying;
    public boolean isNewLevel;
    public int aniPlayingIndex;
    public boolean isGoalFeedbackOpen;
    public List<SpriteAnimationStateComponent> tilesScs = new ArrayList<>();
    public List<SpriteAnimationStateComponent> tilesScs2 = new ArrayList<>();
    private Entity feedbackEntity;
    private float stateTime;

    public void init(boolean isNewLevel) {
        aniPlayingIndex = -1;
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
        feedbackEntity.getComponent(TransformComponent.class).x = POS_X;
        feedbackEntity.getComponent(TransformComponent.class).y = POS_Y;
        feedbackEntity.getComponent(ZIndexComponent.class).setZIndex(190);

        final Entity goalLabel = new ItemWrapper(feedbackEntity).getChild(LBL_DIALOG).getEntity();
        LabelComponent goalsLabelComp = goalLabel.getComponent(LabelComponent.class);
        goalsLabelComp.text.replace(0, goalsLabelComp.text.capacity(), " \n     " + gameScript.fpc.level.name + " \n ");

        if (tiles == null || tiles.isEmpty() || !isPause) {
            int y = GOAL_INIT_POS_Y;
            tilesScs = new ArrayList<>();
            for (Goal g : gameScript.fpc.level.getGoals()) {
                tiles.add(createGoalTile(g, y));
                y -= GOAL_STEP_Y;
            }
        }
        isGoalFeedbackOpen = true;
    }

    public void showNewLevel() {
        shouldShow = false;
        feedbackEntity.getComponent(TransformComponent.class).x = POS_X;
        feedbackEntity.getComponent(TransformComponent.class).y = POS_Y;
        feedbackEntity.getComponent(ZIndexComponent.class).setZIndex(190);

        if (prevLvlTiles != null || !prevLvlTiles.isEmpty() || !isPause) {
            addMoveInPrevTilesActions();
        }
        if (tiles == null || tiles.isEmpty() || !isPause) {
            addMoveInTilesActions();
        }

        isGoalFeedbackOpen = true;
        isNewLevel = false;
    }

    private void addMoveInTilesActions() {
        int y = GOAL_INIT_POS_Y;
        tilesScs = new ArrayList<>();

        int i = 1;
        float delay = INITIAL_DELAY + prevLvlTiles.size() * MOVE_TILES_DELAY + 2;
        for (Goal g : gameScript.fpc.level.getGoals()) {
            Entity newTile = createGoalTile(g, y);

            ActionComponent ac = new ActionComponent();
            Actions.checkInit();

            if (i == 1) {
                delay = INITIAL_DELAY + prevLvlTiles.size() * MOVE_TILES_DELAY + 2;
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
        for (SpriteAnimationStateComponent s : tilesScs) {
            tilesScs2.add(s);
        }
        int y = GOAL_INIT_POS_Y;
        int i = 1;

        float delay = 0;
        for (Entity e : prevLvlTiles) {
            e.getComponent(TransformComponent.class).x = GOAL_INIT_POS_X;
            e.getComponent(TransformComponent.class).y = y;
            e.getComponent(TintComponent.class).color.a = 1;

            ActionComponent ac = new ActionComponent();
            Actions.checkInit();

            if (i == 1) {
                delay = INITIAL_DELAY;
            } else {
                delay += MOVE_TILES_DELAY;
            }
            i++;

            ac.dataArray.add(Actions.sequence(Actions.delay(delay),
                    Actions.moveTo(-e.getComponent(DimensionsComponent.class).width,
                            e.getComponent(TransformComponent.class).y, 1.5f, Interpolation.exp10In)));
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

    private Entity createGoalTile(Goal goal, int y) {
        CompositeItemVO tempC = sceneLoader.loadVoFromLibrary(GOAL_LIB).clone();

        sceneLoader.rm.addSPRITEtoLoad("goalTile");
        final Entity tile = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), tile, tempC.composite);
        sceneLoader.getEngine().addEntity(tile);

        if (isNewLevel) {
            tile.getComponent(TransformComponent.class).x = Gdx.graphics.getWidth() + 1000;
        } else {
            tile.getComponent(TransformComponent.class).x = POS_X + GOAL_INIT_POS_X;
        }
        tile.getComponent(TransformComponent.class).y = y;
        tile.getComponent(TransformComponent.class).scaleY = GOAL_SCALE;

        String goalProgressValue = String.valueOf(goal.getCounter());
        NodeComponent nc = tile.getComponent(NodeComponent.class);
        for (Entity e : nc.children) {

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

            SpriteAnimationStateComponent sc = e.getComponent(SpriteAnimationStateComponent.class);
            SpriteAnimationComponent s = e.getComponent(SpriteAnimationComponent.class);
            if (sc != null) {
                if (goal.achieved) {
                    if (goal.justAchieved) {
                        ActionComponent ac = new ActionComponent();
                        Actions.checkInit();
                        ac.dataArray.add(Actions.delay(DELAY_ON_ANIMATION));
                        tile.add(ac);
                        sc.paused = true;
                    } else {
                        sc.set(s.frameRangeMap.get(DEFAULT), 0, Animation.PlayMode.REVERSED);
                        sc.paused = true;
                    }
                } else {
                    sc.paused = true;
                }
                tilesScs.add(sc);
            }
        }
        tile.getComponent(ZIndexComponent.class).setZIndex(200);
        return tile;
    }

    private Entity loadGoalFromLib(String goalLib) {
        CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary(goalLib);
        sceneLoader.rm.addSPRITEtoLoad(goalLib);
        Entity bugE = GameStage.sceneLoader.entityFactory.createSPRITEentity(GameStage.sceneLoader.getRoot(), tempItemC);
        GameStage.sceneLoader.getEngine().addEntity(bugE);
        return bugE;
    }

    public void update() {
        stateTime += Gdx.graphics.getDeltaTime();
        fade(feedbackEntity, isGoalFeedbackOpen);
        int i = 0;
        while (i < tiles.size()) {
            fade(tiles.get(i), isGoalFeedbackOpen);
            if (gameScript.fpc.level.getGoals().get(i).justAchieved) {
                if (!isAniPlaying) {
                    tilesScs.get(i).paused = false;
                    tilesScs.get(i).currentAnimation.setPlayMode(Animation.PlayMode.NORMAL);
                    isAniPlaying = true;
                    aniPlayingIndex = i;
                    gameScript.fpc.level.getGoals().get(i).justAchieved = false;

                } else {
                    isAniPlaying = !tilesScs.get(i).currentAnimation.isAnimationFinished(stateTime);
                }
            }
            i++;
        }

        if (prevLvlTiles != null && prevLvlTiles.get(prevLvlTiles.size() - 1).getComponent(TransformComponent.class).x <=
                -prevLvlTiles.get(prevLvlTiles.size() - 1).getComponent(DimensionsComponent.class).width) {
            final Entity goalLabel = new ItemWrapper(feedbackEntity).getChild(LBL_DIALOG).getEntity();

            LabelComponent goalsLabelComp = goalLabel.getComponent(LabelComponent.class);
            if (!goalsLabelComp.text.toString().equals(gameScript.fpc.level.name)) {
                EffectUtils.playYellowStarsParticleEffect(goalLabel.getComponent(TransformComponent.class).x,
                        goalLabel.getComponent(TransformComponent.class).y);
                goalsLabelComp.text.replace(0, goalsLabelComp.text.capacity(), gameScript.fpc.level.name);
            }
        }

        if (Gdx.input.justTouched() && isGoalFeedbackOpen) {
            if (gameScript.fpc.level.checkAllGoals()) {
                showGiftScreen();
            } else {
                if (gameScript.giftScreen != null && gameScript.giftScreen.isGiftScreenOpen) {
                    gameScript.giftScreen.isGiftScreenOpen = false;
                    gameScript.giftScreen.hide();
                } else if (tiles.get(tiles.size() - 1).getComponent(TransformComponent.class).x <= GOAL_INIT_POS_X + 20) {
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
            }
        }
    }

    private void showGiftScreen() {
        if (gameScript.giftScreen == null) {
            gameScript.giftScreen = new GiftScreen();
        }
        gameScript.giftScreen.init();
        gameScript.giftScreen.show();

        isGoalFeedbackOpen = false;
    }
}