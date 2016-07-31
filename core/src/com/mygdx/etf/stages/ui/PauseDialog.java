package com.mygdx.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.mygdx.etf.entity.componets.Goal;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mygdx.etf.stages.GameScreenScript.isPause;
import static com.mygdx.etf.stages.GameStage.gameScript;
import static com.mygdx.etf.stages.GameStage.sceneLoader;
import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_Y;

public class PauseDialog extends AbstractDialog {

    public static final String PAUSE_DIALOG = "dialog";
    public static final String LBL_DIALOG = "lbl_dialog";
    public static final String ACHIEVED_GOAL_LIB = "achieved_goal_lib";
    public static final String BTN_CLOSE = "btn_close";
    public static final String LBL_PAUSE_TIMER = "lbl_timer_pause";

    public static final int PAUSE_Y = 30;
    public static final int PAUSE_X = 300;
    public static final int GOAL_TILE_START_Y = 460;
    public static final int GOAL_TILE_SPACE_X = 100;
    public static final float GOAL_TILE_SCALE = 0.7f;
    public static final int GOAL_TILE_STEP_Y = 110;
    public static final int TAP_COOLDOWN = 30;
    public static final int PAUSE_COUNT = 3;
    public static final String TILE_TAG = "tile";

    private Map<Goal, Entity> tiles;
    private ItemWrapper gameItem;
    private Entity pauseDialogE;

    private Entity lblPauseTimer;
    public float pauseTimer = 0;
    public int pauseCounter = 10;
    private int tapCoolDown = 30;

    public PauseDialog(ItemWrapper gameItem) {
        tiles = new HashMap<>();
        this.gameItem = gameItem;
    }

    public void init() {
        pauseDialogE = gameItem.getChild(PAUSE_DIALOG).getEntity();
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
                closePauseDialog();
            }
        });

        final TransformComponent dialogTc = pauseDialogE.getComponent(TransformComponent.class);
        dialogTc.x = FAR_FAR_AWAY_X;
        dialogTc.y = FAR_FAR_AWAY_Y;
        initShadow();

        lblPauseTimer = gameItem.getChild(LBL_PAUSE_TIMER).getEntity();
        if (lblPauseTimer != null) {
            lblPauseTimer.getComponent(LabelComponent.class).text.replace(0,
                    lblPauseTimer.getComponent(LabelComponent.class).text.length, "");
        }
        createGoalTiles();
    }

    private void closePauseDialog() {
        close(pauseDialogE);
        pauseTimer = 0;
        pauseCounter = PAUSE_COUNT - 1;
        if (lblPauseTimer != null) {
            lblPauseTimer.getComponent(LabelComponent.class).text.replace(0,
                    lblPauseTimer.getComponent(LabelComponent.class).text.capacity(),
                    String.valueOf(PAUSE_COUNT));
        }
    }

    public void show() {
        isPause = true;
        isActive = true;
        pauseCounter = 10;
        addShadow();

        if (lblPauseTimer != null) {
            lblPauseTimer.getComponent(LabelComponent.class).text.replace(0,
                    lblPauseTimer.getComponent(LabelComponent.class).text.length, "");
        }

        final Entity goalLabel = gameItem.getChild(PAUSE_DIALOG).getChild(LBL_DIALOG).getEntity();
        LabelComponent goalsLabelComp = goalLabel.getComponent(LabelComponent.class);
        goalsLabelComp.text.replace(0, goalsLabelComp.text.capacity(), " \n     " + gameScript.fpc.level.name + " \n ");

        int y = GOAL_TILE_START_Y;
        for (Map.Entry<Goal, Entity> pair : tiles.entrySet()) {
            showGoalTile(y, pair.getValue(), pair.getKey());
            y -= GOAL_TILE_STEP_Y;
        }

        pauseDialogE.getComponent(TransformComponent.class).x = PAUSE_X;
        pauseDialogE.getComponent(TransformComponent.class).y = 460;
        pauseDialogE.getComponent(ZIndexComponent.class).setZIndex(100);

        ActionComponent ac = new ActionComponent();
        Actions.checkInit();
        ac.dataArray.add(Actions.moveTo(PAUSE_X, PAUSE_Y, 2, Interpolation.exp10Out));
        pauseDialogE.add(ac);
    }

    private List<Entity> getTileEntities(Entity pauseDialogE) {
        List<Entity> tiles = new ArrayList<>();
        NodeComponent nc = pauseDialogE.getComponent(NodeComponent.class);
        for (Entity e : nc.children) {
            if (e.getComponent(MainItemComponent.class).tags.contains(TILE_TAG)) {
                tiles.add(e);
            }
        }
        return tiles;
    }

    private void createGoalTiles() {
        List<Entity> tileEntities = getTileEntities(pauseDialogE);
        int i = 0;
        for (Goal goal : gameScript.fpc.level.getGoals()) {
            Entity tile = tileEntities.get(i++); //TODO: out of bound exception
//            CompositeItemVO tempC;
//            tempC = sceneLoader.loadVoFromLibrary(ACHIEVED_GOAL_LIB).clone();
//
//            final Entity tile = sceneLoader.entityFactory.createEntity(sceneLoader.getRoot(), tempC);
////            sceneLoader.getEngine().addEntity(tile);
//            tile.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
//            pauseDialogE.getComponent(NodeComponent.class).addChild(tile);
//            sceneLoader.entityFactory.initAllChildren(sceneLoader.getEngine(), tile, tempC.composite);

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
        while (i < tileEntities.size()) {
            tileEntities.get(i++).getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        }
    }

    private void showGoalTile(int y, Entity tile, Goal goal) {
        tile.getComponent(TransformComponent.class).x = GOAL_TILE_SPACE_X;
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
//        fade(pauseDialogE, pauseCounter > PAUSE_COUNT);
//        fade(shadowE, pauseCounter > PAUSE_COUNT);
//        for (Entity tile : tiles.values()) {
//            fade(tile, pauseCounter > PAUSE_COUNT);
//        }

        if (pauseCounter == 0 && isPause && pauseTimer >= 1 && lblPauseTimer != null) {
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


            if (pauseTimer >= 1 && lblPauseTimer != null) {
                pauseTimer = 0;
                lblPauseTimer.getComponent(LabelComponent.class).text.replace(0,
                        lblPauseTimer.getComponent(LabelComponent.class).text.capacity(),
                        String.valueOf(pauseCounter--));
            }
        } else if (pauseTimer >= 1 && lblPauseTimer != null) {
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
