package com.mygdx.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.mygdx.etf.entity.componets.Goal;
import com.mygdx.etf.entity.componets.listeners.ImageButtonListener;
import com.mygdx.etf.stages.GameStage;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mygdx.etf.stages.GameScreenScript.isPause;
import static com.mygdx.etf.stages.GameStage.gameScript;
import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_Y;
import static com.mygdx.etf.stages.ui.Settings.SETTINGS_SCALE;

public class PauseDialog extends AbstractDialog {

    public static final String PAUSE_DIALOG = "dialog";
    public static final String LBL_DIALOG = "lbl_dialog";
    public static final String LBL_DIALOG_S = "lbl_dialog_2";
    public static final String LBL_LEVEL_INDICATOR = "lbl_level_indicator";
    public static final String LBL_LEVEL_INDICATOR_S = "lbl_level_indicator_s";
    public static final String LBL_GOAL_PROGRESS = "goal_progress";
    public static final String BTN_CLOSE = "btn_close";
    public static final String LBL_PAUSE_TIMER = "lbl_timer_pause";
    public static final String TILE_TAG = "tile";
    public static final String COMPLETED = "Completed";
    public static final String PROGRESS = "Progress: ";
    public static final String SLASH = "/";
    public static final String GOAL_PROGRESS_LBL = "goal_progress_lbl";
    public static final String GOAL_LBL = "goal_lbl";

    public static final int PAUSE_Y = 50;
    public static final int PAUSE_X = 260;
    public static final int GOALS_X = 630;
    public static final int GOAL_TILE_START_Y = 400;
    public static final int GOAL_TILE_SPACE_X = 170;
    public static final float GOAL_TILE_SCALE = 2f;
    public static final int GOAL_TILE_STEP_Y = 110;
    public static final int TAP_COOLDOWN = 30;
    public static final int PAUSE_COUNT = 3;
    public static final String GOALS_POPUP = "goal_popup_lib";
    public static final String ACHIEVED = "achieved";
    public static final String NOTACHIEVED = "notachieved";
    public static final String ENTER = " \n ";
    public static final int PAUSE_Y_UP = 460;
    private static final String PAUSE_TEXT = "Pause";
    private static final String CHALLENGES = "Challenges";

    private Map<Goal, Entity> tiles;
    private ItemWrapper gameItem;
    private Entity pauseDialogE;

    private Entity lblPauseTimer;
    public float pauseTimer = 0;
    public int pauseCounter = 10;
    private int tapCoolDown = 30;

    private String goalProgressValue;
    private float popupX;

    public PauseDialog(ItemWrapper gameItem) {
        tiles = new HashMap<>();
        this.gameItem = gameItem;
    }

    public void init() {
        CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary(GOALS_POPUP).clone();
        pauseDialogE = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
        GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), pauseDialogE, tempItemC.composite);
        GameStage.sceneLoader.getEngine().addEntity(pauseDialogE);

        final Entity closePauseBtn = pauseDialogE.getComponent(NodeComponent.class).getChild(BTN_CLOSE);

        closePauseBtn.add(new ButtonComponent());
        closePauseBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(closePauseBtn) {
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

        final Entity goalLabels = pauseDialogE.getComponent(NodeComponent.class).getChild(LBL_DIALOG_S);
        LabelComponent goalsLabelComps = goalLabels.getComponent(LabelComponent.class);
        goalsLabelComps.text.replace(0, goalsLabelComps.text.capacity(), PAUSE_TEXT);

        final Entity goalLabel = pauseDialogE.getComponent(NodeComponent.class).getChild(LBL_DIALOG);
        LabelComponent goalsLabelComp = goalLabel.getComponent(LabelComponent.class);
        goalsLabelComp.text.replace(0, goalsLabelComp.text.capacity(), PAUSE_TEXT);

        createGoalTiles();

        popupX = PAUSE_X;
    }

    public void initGoals() {
        init();

        final Entity goalLabels = pauseDialogE.getComponent(NodeComponent.class).getChild(LBL_DIALOG_S);
        LabelComponent goalsLabelComps = goalLabels.getComponent(LabelComponent.class);
        goalsLabelComps.text.replace(0, goalsLabelComps.text.capacity(), CHALLENGES);

        final Entity goalLabel = pauseDialogE.getComponent(NodeComponent.class).getChild(LBL_DIALOG);
        LabelComponent goalsLabelComp = goalLabel.getComponent(LabelComponent.class);
        goalsLabelComp.text.replace(0, goalsLabelComp.text.capacity(), CHALLENGES);


        pauseDialogE.getComponent(TransformComponent.class).scaleX = SETTINGS_SCALE;
        pauseDialogE.getComponent(TransformComponent.class).scaleY = SETTINGS_SCALE;

        popupX = GOALS_X;
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

        final Entity levelLabels = pauseDialogE.getComponent(NodeComponent.class).getChild(LBL_LEVEL_INDICATOR_S);
        LabelComponent levelLabelComps = levelLabels.getComponent(LabelComponent.class);
        levelLabelComps.text.replace(0, levelLabelComps.text.capacity(), "Level: " + gameScript.fpc.level.name + ENTER);

        final Entity levelLabel = pauseDialogE.getComponent(NodeComponent.class).getChild(LBL_LEVEL_INDICATOR);
        LabelComponent levelLabelsComp = levelLabel.getComponent(LabelComponent.class);
        levelLabelsComp.text.replace(0, levelLabelComps.text.capacity(), "Level: " + gameScript.fpc.level.name + ENTER);

        int y = GOAL_TILE_START_Y;
        for (Map.Entry<Goal, Entity> pair : tiles.entrySet()) {
            showGoalTile(y, pair.getValue(), pair.getKey());
            y -= GOAL_TILE_STEP_Y;
        }

        pauseDialogE.getComponent(TransformComponent.class).x = popupX;
        pauseDialogE.getComponent(TransformComponent.class).y = PAUSE_Y_UP;
        pauseDialogE.getComponent(ZIndexComponent.class).setZIndex(100);

        ActionComponent ac = new ActionComponent();
        Actions.checkInit();
        ac.dataArray.add(Actions.moveTo(popupX, PAUSE_Y, POPUP_MOVE_DURATION, Interpolation.exp10Out));
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
            Entity tile = tileEntities.get(i);

            goalProgressValue = String.valueOf(goal.getCounter());
            NodeComponent nc = tile.getComponent(NodeComponent.class);
            for (Entity e : nc.children) {

                if (goal.getCounter() >= goal.getN()) {
                    goalProgressValue = COMPLETED;
                } else {
                    goalProgressValue = PROGRESS + String.valueOf(goal.getCounter() + SLASH + goal.getN());
                }
                if (e.getComponent(MainItemComponent.class).tags.contains(GOAL_PROGRESS_LBL)) {
                    e.getComponent(LabelComponent.class).text.replace(0, e.getComponent(LabelComponent.class).text.capacity(),
                            goalProgressValue);
                } else if (e.getComponent(MainItemComponent.class).tags.contains(GOAL_LBL)) {
                    e.getComponent(LabelComponent.class).text.replace(0, e.getComponent(LabelComponent.class).text.capacity(),
                            goal.getDescription());
                }
            }
            tiles.put(goal, tile);
            i++;
        }
        while (i < tileEntities.size()) {
            tileEntities.get(i++).getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        }
    }

    private void showGoalTile(int y, Entity tile, Goal goal) {
        tile.getComponent(TransformComponent.class).x = GOAL_TILE_SPACE_X;
        tile.getComponent(TransformComponent.class).y = y;

        if (goal.achieved){
            tile.getComponent(LayerMapComponent.class).getLayer(ACHIEVED).isVisible = true;
            tile.getComponent(LayerMapComponent.class).getLayer(NOTACHIEVED).isVisible = false;
        } else {
            tile.getComponent(LayerMapComponent.class).getLayer(ACHIEVED).isVisible = false;
            tile.getComponent(LayerMapComponent.class).getLayer(NOTACHIEVED).isVisible = true;
        }

        NodeComponent nc = tile.getComponent(NodeComponent.class);
        for (Entity e : nc.children) {
            if (goal.getCounter() >= goal.getN()) {
                goalProgressValue = COMPLETED;
            }else{
                goalProgressValue = PROGRESS + String.valueOf(goal.getCounter() + SLASH + goal.getN());
            }

            if (e.getComponent(LabelComponent.class) != null) {
                if (e.getComponent(LabelComponent.class).getText().toString().contains(PROGRESS)) { //checks if the right label is being used
                    e.getComponent(LabelComponent.class).text.replace(0, e.getComponent(LabelComponent.class).text.capacity(),
                            goalProgressValue);
                }
            }
        }
        tile.getComponent(ZIndexComponent.class).setZIndex(200);
    }

    public void update(float delta) {
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
                e.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            }
        }
    }
}
