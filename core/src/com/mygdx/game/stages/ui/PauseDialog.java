package com.mygdx.game.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.mygdx.game.entity.componets.Goal;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.mygdx.game.stages.GameScreenScript.isPause;
import static com.mygdx.game.stages.GameStage.sceneLoader;
import static com.mygdx.game.utils.EffectUtils.fade;

public class PauseDialog {

    public static final String PAUSE_DIALOG = "dialog";
    public static final String LBL_DIALOG = "lbl_dialog";
    public static final int PAUSE_X = 300;
    public static final int PAUSE_Y = 30;
    public static final String ACHIEVED_GOAL_LIB = "achieved_goal_lib";
    public static final String BTN_CLOSE = "btn_close";
    private static List<Entity> tiles;
    private ItemWrapper gameItem;
    private Entity pauseDialog;


    public PauseDialog(ItemWrapper gameItem) {
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
        pauseDialog = gameItem.getChild(PAUSE_DIALOG).getEntity();
        Entity closePauseBtn = gameItem.getChild(PAUSE_DIALOG).getChild(BTN_CLOSE).getEntity();
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
        dialogTc.x = GlobalConstants.FAR_FAR_AWAY_X;
        dialogTc.y = GlobalConstants.FAR_FAR_AWAY_Y;
    }

    public void pause() {
        final TransformComponent dialogTc = pauseDialog.getComponent(TransformComponent.class);
        dialogTc.x = PAUSE_X;
        dialogTc.y = PAUSE_Y;

        final Entity goalLabel = gameItem.getChild(PAUSE_DIALOG).getChild(LBL_DIALOG).getEntity();
        LabelComponent goalsLabelComp = goalLabel.getComponent(LabelComponent.class);
        goalsLabelComp.text.replace(0, goalsLabelComp.text.capacity(), " \n     " + GameStage.gameScript.fpc.level.name + " \n ");

        if (tiles == null || tiles.isEmpty() || !isPause) {
            int y = 416;
            for (Goal g : GameStage.gameScript.fpc.level.getGoals()) {
                tiles.add(createGoalTile(g, y));
                y -= 130;
            }
        }
        isPause = true;
    }

    private Entity createGoalTile(Goal goal, int y) {
        CompositeItemVO tempC;
        tempC = sceneLoader.loadVoFromLibrary(ACHIEVED_GOAL_LIB).clone();

        final WeakReference<Entity> tile = new WeakReference<Entity>(sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempC));
        sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), tile.get(), tempC.composite);
        sceneLoader.getEngine().addEntity(tile.get());

        TransformComponent tc = new TransformComponent();
        tc.x = PAUSE_X + 61;
        tc.y = y;
        tc.scaleY = 0.7f;

        tile.get().add(tc);

        NodeComponent nc = tile.get().getComponent(NodeComponent.class);
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
                    sc.player.setTime(sc.player.getAnimation().length-2);
                    sc.player.speed = 0;
                } else {
                    sc.player.setTime(0);
                    sc.player.speed = 0;
                }
            }
        }
        tile.get().getComponent(ZIndexComponent.class).setZIndex(200);
        return tile.get();
    }

    public void update() {
        fade(pauseDialog, isPause);
        for (Entity tile : tiles) {
            fade(tile, isPause);
        }
    }
}
