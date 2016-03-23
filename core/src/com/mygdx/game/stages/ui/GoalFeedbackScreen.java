package com.mygdx.game.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.mygdx.game.entity.componets.Goal;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
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

    public static final String GOALFEEDBACK = "gift_feedbacker";
    public static final String LBL_DIALOG = "lbl_level";
    public static final int POS_X = -22;
    public static final int POS_Y = -19;
    public static final String ACHIEVED_GOAL_LIB = "achieved_goal_lib";
    public static boolean shouldShow;

    private static List<Entity> tiles;
    public boolean isAniPlaying;
    public int aniPlayingIndex;
    public boolean isGoalFeedbackOpen;
    public List<SpriterComponent> tilesScs = new ArrayList<>();
    private ItemWrapper gameItem;
    private Entity pauseDialog;


    public GoalFeedbackScreen(ItemWrapper gameItem) {
        if (tiles != null) {
            for (Entity tile : tiles) {
                tile.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
                sceneLoader.getEngine().removeEntity(tile);
            }
        }
        tiles = new ArrayList<>();
        this.gameItem = gameItem;
    }

    public void init() {
        pauseDialog = gameItem.getChild(GOALFEEDBACK).getEntity();

        final TransformComponent dialogTc = pauseDialog.getComponent(TransformComponent.class);
        dialogTc.x = GlobalConstants.FAR_FAR_AWAY_X;
        dialogTc.y = GlobalConstants.FAR_FAR_AWAY_Y;
    }

    public void show() {
        shouldShow = false;
        final TransformComponent dialogTc = pauseDialog.getComponent(TransformComponent.class);
        dialogTc.x = POS_X;
        dialogTc.y = POS_Y;

        final Entity goalLabel = gameItem.getChild(GOALFEEDBACK).getChild(LBL_DIALOG).getEntity();
        LabelComponent goalsLabelComp = goalLabel.getComponent(LabelComponent.class);
        goalsLabelComp.text.replace(0, goalsLabelComp.text.capacity(), " \n     " + GameStage.gameScript.fpc.level.name + " \n ");

        if (tiles == null || tiles.isEmpty() || !isPause) {
            int y = 416;
            for (Goal g : GameStage.gameScript.fpc.level.getGoals()) {
                tiles.add(createGoalTile(g, y));
                y -= 130;
            }
        }
//        isPause = true;
        isGoalFeedbackOpen = true;
    }

    private Entity createGoalTile(Goal goal, int y) {
        CompositeItemVO tempC;
//        if (goal.achieved){
        tempC = sceneLoader.loadVoFromLibrary(ACHIEVED_GOAL_LIB).clone();
//        }

        final Entity tile = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempC);
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), tile, tempC.composite);
        sceneLoader.getEngine().addEntity(tile);

        TransformComponent tc = new TransformComponent();
        tc.x = POS_X + 61;
        tc.y = y;

        tile.add(tc);

        NodeComponent nc = tile.getComponent(NodeComponent.class);
        for (Entity e : nc.children) {
            if (e.getComponent(LabelComponent.class) != null) {
                e.getComponent(LabelComponent.class).text.replace(0, e.getComponent(LabelComponent.class).text.capacity(),
                        goal.getDescription());
                e.getComponent(ZIndexComponent.class).setZIndex(120);
            }
            SpriterComponent sc = e.getComponent(SpriterComponent.class);
            if (sc != null) {
                sc.scale = 0.8f;
                if (goal.achieved) {
                    if (goal.justAchieved) {
                        ActionComponent ac = new ActionComponent();
                        Actions.checkInit();
                        ac.dataArray.add(Actions.delay(5000));
                        tile.add(ac);

                        sc.player.speed = 0;
//                        goal.justAchieved = false;
                    } else {
                        sc.player.setTime(sc.player.getAnimation().length);
                        sc.player.speed = 0;
                    }
                } else {
                    sc.player.setTime(0);
                    sc.player.speed = 0;
                }
                tilesScs.add(sc);
            }
//            } else if (e.getComponent(MainItemComponent.class).tags.contains(STAR_TAG)){
//                e.getComponent(TransformComponent.class).scaleX = 0.2f;
//                e.getComponent(TransformComponent.class).scaleY = 0.2f;
//                e.getComponent(TransformComponent.class).x = -70;
//                e.getComponent(TransformComponent.class).y = -60;
//                e.getComponent(ZIndexComponent.class).setZIndex(140);
//            }
        }
        tile.getComponent(ZIndexComponent.class).setZIndex(200);
        return tile;
    }

    public void update() {
        fade(pauseDialog, isGoalFeedbackOpen);
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
//                isAniPlaying = false;
            }
            i++;
        }
        if (!tilesScs.isEmpty() && tilesScs.get(aniPlayingIndex).player.getTime() >=
                tilesScs.get(aniPlayingIndex).player.getAnimation().length - 20) {
            isAniPlaying = false;
        }

        if (Gdx.input.justTouched() && isGoalFeedbackOpen) {
            if (GameStage.gameScript.fpc.level.checkAllGoals()) {
                gameScript.giftScreen.show();
            } else {
                GameStage.gameScript.stage.initResult();
            }
            isGoalFeedbackOpen = false;
        }
    }
}
