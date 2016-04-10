package com.mygdx.game.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
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
    public static final String LBL_PAUSE_TIMER = "lbl_timer_pause";

    public static final int PAUSE_Y = 30;
    public static final int PAUSE_X = 300;
    public static final int GOAL_TILE_START_Y = 510;
    public static final int GOAL_TILE_SPACE_X = 100;
    public static final float GOAL_TILE_SCALE = 0.7f;
    public static final int GOAL_TILE_STEP_Y = 110;
    public static final int TAP_COOLDOWN = 30;
    public static final int PAUSE_COUNT = 3;

    private Map<Goal, Entity> tiles;
    private ItemWrapper gameItem;
    private Entity pauseDialog;
    private Entity shadowE;

    private Entity lblPauseTimer;
    public float pauseTimer = 0;
    public int pauseCounter = 10;
    private int tapCoolDown = 30;

    public PauseDialog(ItemWrapper gameItem) {
        tiles = new HashMap<>();
        this.gameItem = gameItem;
    }

    public void init() {
        pauseDialog = gameItem.getChild(PAUSE_DIALOG).getEntity();
        Entity closePauseBtn = gameItem.getChild(PAUSE_DIALOG).getChild(BTN_CLOSE).getEntity();

        CompositeItemVO tempC = GameStage.sceneLoader.loadVoFromLibrary(LIB_SHADOW).clone();

        if (shadowE == null) {
            shadowE = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempC);
            GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), shadowE, tempC.composite);
            GameStage.sceneLoader.getEngine().addEntity(shadowE);
        }
        shadowE.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        shadowE.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;

        closePauseBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
            }

            @Override
            public void touchDown() {
            }

            @Override
            public void clicked() {
                pauseTimer = 0;
                pauseCounter = PAUSE_COUNT - 1;
                lblPauseTimer.getComponent(LabelComponent.class).text.replace(0,
                        lblPauseTimer.getComponent(LabelComponent.class).text.capacity(),
                        String.valueOf(PAUSE_COUNT));
            }
        });

        final TransformComponent dialogTc = pauseDialog.getComponent(TransformComponent.class);
        dialogTc.x = FAR_FAR_AWAY_X;
        dialogTc.y = FAR_FAR_AWAY_Y;

        lblPauseTimer = gameItem.getChild(LBL_PAUSE_TIMER).getEntity();
        lblPauseTimer.getComponent(LabelComponent.class).text.replace(0,
                lblPauseTimer.getComponent(LabelComponent.class).text.length, "");

        createGoalTiles();
    }

    public void show() {

        pauseCounter = 10;

        final TransformComponent dialogTc = pauseDialog.getComponent(TransformComponent.class);
        dialogTc.x = PAUSE_X;
        dialogTc.y = PAUSE_Y;

        lblPauseTimer.getComponent(LabelComponent.class).text.replace(0,
                lblPauseTimer.getComponent(LabelComponent.class).text.length, "");

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

    public void update(float delta) {
        fade(pauseDialog, pauseCounter > PAUSE_COUNT);
        fade(shadowE, pauseCounter > PAUSE_COUNT);
        for (Entity tile : tiles.values()) {
            fade(tile, pauseCounter > PAUSE_COUNT);
        }

        if (pauseCounter == 0 && isPause && pauseTimer >= 1) {
            isPause = false;
            lblPauseTimer.getComponent(LabelComponent.class).text.replace(0,
                    lblPauseTimer.getComponent(LabelComponent.class).text.capacity(),
                    "");
        }
        pauseTimer += delta;
        if (pauseCounter <= PAUSE_COUNT && pauseCounter > 0) {
            if (Gdx.input.justTouched() && tapCoolDown <= 0) {
                pauseTimer = 1;
                tapCoolDown = TAP_COOLDOWN;
            }
            tapCoolDown--;


            if (pauseTimer >= 1) {
                pauseTimer = 0;
                lblPauseTimer.getComponent(LabelComponent.class).text.replace(0,
                        lblPauseTimer.getComponent(LabelComponent.class).text.capacity(),
                        String.valueOf(pauseCounter--));
            }
        } else if (pauseTimer >= 1){
            lblPauseTimer.getComponent(LabelComponent.class).text.replace(0,
                    lblPauseTimer.getComponent(LabelComponent.class).text.capacity(),
                    "");
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
