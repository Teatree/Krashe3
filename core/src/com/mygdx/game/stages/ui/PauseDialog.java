package com.mygdx.game.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.mygdx.game.entity.componets.Goal;
import com.mygdx.game.stages.GameStage;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.HashMap;
import java.util.Map;

import static com.mygdx.game.stages.GameScreenScript.isPause;
import static com.mygdx.game.stages.GameStage.sceneLoader;
import static com.mygdx.game.utils.EffectUtils.fade;
import static com.mygdx.game.utils.GlobalConstants.*;

public class PauseDialog {

    public static final String PAUSE_DIALOG = "dialog";
    public static final String LBL_DIALOG = "lbl_dialog";
    public static final String ACHIEVED_GOAL_LIB = "achieved_goal_lib";
    public static final String BTN_CLOSE = "btn_close";
    public static final String LIB_SHADOW = "lib_shadow";

    public static final int PAUSE_Y = 30;
    public static final int PAUSE_X = 300;
    public static final int GOAL_TILE_START_Y = 510;
    public static final int GOAL_TILE_SPACE_X = 100;
    public static final float GOAL_TILE_SCALE = 0.7f;
    public static final int GOAL_TILE_STEP_Y = 110;

    private Map<Goal, Entity> tiles;
    private ItemWrapper gameItem;
    private Entity pauseDialog;
    private Entity shadowE;

    public PauseDialog(ItemWrapper gameItem) {
//        if (tiles != null) {
//            for (Entitytile : tiles.values()) {
//                tile.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
//                sceneLoader.getEngine().removeEntity(tile);
//            }
//        }

        tiles = new HashMap<>();
        this.gameItem = gameItem;
    }

    public void init() {
        pauseDialog = gameItem.getChild(PAUSE_DIALOG).getEntity();
        Entity closePauseBtn = gameItem.getChild(PAUSE_DIALOG).getChild(BTN_CLOSE).getEntity();

        CompositeItemVO tempC = GameStage.sceneLoader.loadVoFromLibrary(LIB_SHADOW).clone();

        if(shadowE == null) {
            shadowE = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempC);
            GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), shadowE, tempC.composite);
            GameStage.sceneLoader.getEngine().addEntity(shadowE);
        }else{
            GameStage.sceneLoader.getEngine().removeEntity(shadowE);
        }

        closePauseBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
            }

            @Override
            public void touchDown() {
            }

            @Override
            public void clicked() {
                isPause = false;
            }
        });

        final TransformComponent dialogTc = pauseDialog.getComponent(TransformComponent.class);
        dialogTc.x = FAR_FAR_AWAY_X;
        dialogTc.y = FAR_FAR_AWAY_Y;

        createGoalTiles();
    }

    public void show() {
        final TransformComponent dialogTc = pauseDialog.getComponent(TransformComponent.class);
        dialogTc.x = PAUSE_X;
        dialogTc.y = PAUSE_Y;

        shadowE.getComponent(TransformComponent.class).x = 0;
        shadowE.getComponent(TransformComponent.class).y = 0;

        shadowE.getComponent(ZIndexComponent.class).setZIndex(49);

        final Entity goalLabel = gameItem.getChild(PAUSE_DIALOG).getChild(LBL_DIALOG).getEntity();
        LabelComponent goalsLabelComp = goalLabel.getComponent(LabelComponent.class);
        goalsLabelComp.text.replace(0, goalsLabelComp.text.capacity(), " \n     " + GameStage.gameScript.fpc.level.name + " \n ");

        int y = GOAL_TILE_START_Y;
        for (Map.Entry<Goal, Entity> pair : tiles.entrySet()) {
            showGoalTile(y, pair.getValue(), pair.getKey());
            y -= GOAL_TILE_STEP_Y;
        }

        isPause = true;
    }

    private void createGoalTiles() {
        for (Goal goal : GameStage.gameScript.fpc.level.getGoals()) {
            CompositeItemVO tempC;
            tempC = sceneLoader.loadVoFromLibrary(ACHIEVED_GOAL_LIB).clone();

            final Entity tile = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempC);
            sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), tile, tempC.composite);
            sceneLoader.getEngine().addEntity(tile);

            tile.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;

            NodeComponent nc = tile.getComponent(NodeComponent.class);
            for (Entity e : nc.children) {
                if (e.getComponent(LabelComponent.class) != null) {
                    e.getComponent(LabelComponent.class).text.replace(0, e.getComponent(LabelComponent.class).text.capacity(),
                            goal.getDescription());
                    e.getComponent(ZIndexComponent.class).setZIndex(120);
                }
            }
            tiles.put(goal, tile);
        }
    }

    private void showGoalTile(int y, Entity tile, Goal goal) {
        tile.getComponent(TransformComponent.class).x = PAUSE_X + GOAL_TILE_SPACE_X;
        tile.getComponent(TransformComponent.class).y = y;
        tile.getComponent(TransformComponent.class).scaleY = GOAL_TILE_SCALE;

        NodeComponent nc = tile.getComponent(NodeComponent.class);
        for (Entity e : nc.children) {
            SpriterComponent sc = e.getComponent(SpriterComponent.class);
            if (sc != null) {
                sc.scale = 0.8f;
                if (goal.achieved) {
                    sc.player.setTime(sc.player.getAnimation().length - 2);
                    sc.player.speed = 0;
                } else {
                    sc.player.setTime(0);
                    sc.player.speed = 0;
                }
            }
        }
        tile.getComponent(ZIndexComponent.class).setZIndex(200);
    }

    public void update() {
        fade(pauseDialog, isPause);
        fade(shadowE, isPause);
        for (Entity tile : tiles.values()) {
            fade(tile, isPause);
        }
    }

    public void deleteTiles() {
        if (tiles != null && !tiles.isEmpty()) {
            for (Entity e : tiles.values()) {
                sceneLoader.getEngine().removeEntity(e);
            }
        }
    }
}
