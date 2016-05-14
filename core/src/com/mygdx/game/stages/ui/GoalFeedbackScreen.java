package com.mygdx.game.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.mygdx.game.entity.componets.Goal;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.EffectUtils;
import com.mygdx.game.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.ArrayList;
import java.util.List;

import static com.mygdx.game.stages.GameScreenScript.isPause;
import static com.mygdx.game.stages.GameStage.gameScript;
import static com.mygdx.game.stages.GameStage.sceneLoader;
import static com.mygdx.game.utils.EffectUtils.fade;

public class GoalFeedbackScreen {

    public static final String GOALFEEDBACK = "lib_gift_feedbacker";
    public static final String LBL_DIALOG = "lbl_level";
    public static final int POS_X = -22;
    public static final int POS_Y = -19;
    public static final String ACHIEVED_GOAL_LIB = "achieved_goal_lib";
    public static final int GOAL_STEP_Y = 110;
    public static final int GOAL_INIT_POS_X = 381;
    public static final float GOAL_SCALE = 0.75f;
    public static final int DELAY_ON_ANIMATION = 5000;
    public static final int GOAL_INIT_POS_Y = 556;
    public static final float INITIAL_DELAY = 1.2f;
    public static final float MOVE_TILES_DELAY = 0.3f;
    public static boolean shouldShow;

    private List<Entity> tiles;
    private List<Entity> prevLvlTiles;
    public boolean isAniPlaying;
    public boolean isNewLevel;
    public int aniPlayingIndex;
    public boolean isGoalFeedbackOpen;
    public List<SpriterComponent> tilesScs = new ArrayList<>();
    public List<SpriterComponent> tilesScs2 = new ArrayList<>();
    private Entity feedbackEntity;


    public GoalFeedbackScreen(ItemWrapper gameItem) {
//        if (tiles != null) {
//            for (Entity tile : tiles) {
//                tile.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
//                sceneLoader.getEngine().removeEntity(tile);
//            }
//        }
//        tiles = new ArrayList<>();
    }

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

//            if (tiles != null) {
//                for (Entity tile : tiles) {
//                    tile.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
//                    sceneLoader.getEngine().removeEntity(tile);
//                }
//            }
        }

        tiles = new ArrayList<>();

//        feedbackEntity = gameItem.getChild(GOALFEEDBACK).getEntity();
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
        goalsLabelComp.text.replace(0, goalsLabelComp.text.capacity(), " \n     " + GameStage.gameScript.fpc.level.name + " \n ");

        if (tiles == null || tiles.isEmpty() || !isPause) {
            int y = GOAL_INIT_POS_Y;
            tilesScs = new ArrayList<>();
            for (Goal g : GameStage.gameScript.fpc.level.getGoals()) {
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

//        final Entity goalLabel = gameItem.getChild(GOALFEEDBACK).getChild(LBL_DIALOG).getEntity();
//        LabelComponent goalsLabelComp = goalLabel.getComponent(LabelComponent.class);
//        goalsLabelComp.text.replace(0, goalsLabelComp.text.capacity(), " \n     " + GameStage.gameScript.fpc.level.name + " \n ");

        if (prevLvlTiles != null || !prevLvlTiles.isEmpty() || !isPause) {
            for (SpriterComponent s : tilesScs) {
                tilesScs2.add(s);

//                s.player.speed = 25;
//                s.player.setAnimation(0);
            }
//            tilesScs = new ArrayList<>();
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
//                        e2.getComponent(ZIndexComponent.class).setZIndex(2000);
                    }
                }
                e.getComponent(TransformComponent.class).x = GOAL_INIT_POS_X;
                e.getComponent(ZIndexComponent.class).setZIndex(2000);
                y -= GOAL_STEP_Y;
            }
        }
        if (tiles == null || tiles.isEmpty() || !isPause) {
            int y = GOAL_INIT_POS_Y;
            tilesScs = new ArrayList<>();

            int i = 1;
            float delay = INITIAL_DELAY + prevLvlTiles.size() * MOVE_TILES_DELAY + 2;
            for (Goal g : GameStage.gameScript.fpc.level.getGoals()) {
                Entity newTile = createGoalTile(g, y);

                ActionComponent ac = new ActionComponent();
                Actions.checkInit();

                if (i == 1) {
                    delay = INITIAL_DELAY + prevLvlTiles.size() * MOVE_TILES_DELAY + 2;
                } else {
                    delay += 0.1f;
                }
                i++;

                ac.dataArray.add(Actions.sequence(Actions.delay(delay),
                        Actions.moveTo(GOAL_INIT_POS_X, newTile.getComponent(TransformComponent.class).y, 1.5f, Interpolation.exp10Out)));
                newTile.add(ac);
                tiles.add(newTile);
                y -= GOAL_STEP_Y;
            }
        }


        isGoalFeedbackOpen = true;
        isNewLevel = false;
    }

    private Entity createGoalTile(Goal goal, int y) {
        CompositeItemVO tempC;
        tempC = sceneLoader.loadVoFromLibrary(ACHIEVED_GOAL_LIB).clone();

        final Entity tile = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), tile, tempC.composite);
        sceneLoader.getEngine().addEntity(tile);

        if (isNewLevel) {
            tile.getComponent(TransformComponent.class).x = Gdx.graphics.getWidth();
        } else {
            tile.getComponent(TransformComponent.class).x = POS_X + GOAL_INIT_POS_X;
        }
        tile.getComponent(TransformComponent.class).y = y;
        tile.getComponent(TransformComponent.class).scaleY = GOAL_SCALE;

        NodeComponent nc = tile.getComponent(NodeComponent.class);
        for (Entity e : nc.children) {
            if (e.getComponent(LabelComponent.class) != null) {
                e.getComponent(LabelComponent.class).text.replace(0, e.getComponent(LabelComponent.class).text.capacity(),
                        goal.getDescription());
                e.getComponent(ZIndexComponent.class).setZIndex(120);
            }
            SpriterComponent sc = e.getComponent(SpriterComponent.class);
            if (sc != null) {
                sc.scale = GOAL_SCALE;
                if (goal.achieved) {
                    if (goal.justAchieved) {
                        ActionComponent ac = new ActionComponent();
                        Actions.checkInit();
                        ac.dataArray.add(Actions.delay(DELAY_ON_ANIMATION));
                        tile.add(ac);

                        sc.player.speed = 0;
                    } else {
                        sc.player.speed = 0;
                        sc.player.setTime(sc.player.getAnimation().length - 2);
                    }
                } else {
                    sc.player.setTime(0);
                    sc.player.speed = 0;
                }
                tilesScs.add(sc);
            }
        }
        tile.getComponent(ZIndexComponent.class).setZIndex(200);
        return tile;
    }

    public void update() {
        fade(feedbackEntity, isGoalFeedbackOpen);
        int i = 0;
        while (i < tiles.size()) {
            fade(tiles.get(i), isGoalFeedbackOpen);
            if (GameStage.gameScript.fpc.level.getGoals().get(i).justAchieved && !isAniPlaying) {
                tilesScs.get(i).player.speed = 4;
                isAniPlaying = true;
                aniPlayingIndex = i;
                GameStage.gameScript.fpc.level.getGoals().get(i).justAchieved = false;
            } else if (tilesScs.get(i).player.getTime() >=
                    tilesScs.get(i).player.getAnimation().length - 20) {
                tilesScs.get(i).player.speed = 0;
                tilesScs.get(i).player.setTime(tilesScs.get(i).player.getAnimation().length - 20);
            }
            i++;
        }

        if (!tilesScs.isEmpty() && aniPlayingIndex >= 0 && tilesScs.get(aniPlayingIndex).player.getTime() >=
                tilesScs.get(aniPlayingIndex).player.getAnimation().length - 20) {
            isAniPlaying = false;
        }

        if (prevLvlTiles != null && prevLvlTiles.get(prevLvlTiles.size()-1).getComponent(TransformComponent.class).x <=
                -prevLvlTiles.get(prevLvlTiles.size()-1).getComponent(DimensionsComponent.class).width ){
            final Entity goalLabel = new ItemWrapper(feedbackEntity).getChild(LBL_DIALOG).getEntity();

            LabelComponent goalsLabelComp = goalLabel.getComponent(LabelComponent.class);
            if(!goalsLabelComp.text.toString().equals(GameStage.gameScript.fpc.level.name)) {
                EffectUtils.playYellowStarsParticleEffect(goalLabel.getComponent(TransformComponent.class).x,
                        goalLabel.getComponent(TransformComponent.class).y);
                goalsLabelComp.text.replace(0, goalsLabelComp.text.capacity(), GameStage.gameScript.fpc.level.name);
            }
        }

        if (Gdx.input.justTouched() && isGoalFeedbackOpen) {
            if (GameStage.gameScript.fpc.level.checkAllGoals()) {
                gameScript.giftScreen.show();

                isGoalFeedbackOpen = false;
            } else {
                if (GameStage.gameScript.giftScreen.isGiftScreenOpen) {
                    GameStage.gameScript.giftScreen.isGiftScreenOpen = false;
                    GameStage.gameScript.giftScreen.hide();

                } else if (tiles.get(tiles.size()-1).getComponent(TransformComponent.class).x <= GOAL_INIT_POS_X+20){
                    isGoalFeedbackOpen = false;
                    feedbackEntity.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
                    feedbackEntity.getComponent(TransformComponent.class).y = GlobalConstants.FAR_FAR_AWAY_Y;
                    sceneLoader.getEngine().removeEntity(feedbackEntity);
                    prevLvlTiles = null;
                    GameStage.gameScript.stage.initResult();
                }
            }
        }
    }
}
