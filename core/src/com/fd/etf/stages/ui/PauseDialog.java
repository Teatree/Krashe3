package com.fd.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.fd.etf.Main;
import com.fd.etf.entity.componets.Goal;
import com.fd.etf.entity.componets.listeners.ImageButtonListener;
import com.fd.etf.stages.GameStage;
import com.fd.etf.utils.SoundMgr;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import javax.xml.soap.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fd.etf.stages.GameScreenScript.isPause;
import static com.fd.etf.stages.ui.Settings.SETTINGS_SCALE;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_Y;

public class PauseDialog extends AbstractDialog {

    private static final String LBL_DIALOG = "lbl_dialog";
    private static final String LBL_DIALOG_SH = "lbl_dialog_sh";
    private static final String LBL_LEVEL_INDICATOR = "lbl_level_indicator";
    private static final String LBL_GOAL_PROGRESS = "goal_progress";
    private static final String BTN_CLOSE = "btn_close";
    public static final String LBL_PAUSE_TIMER = "lbl_timer_pause";
    public static final String PAUSETIMER_C = "pauseTimer_C";
    public static final String IMG_PAUSE_TIMER = "img_pause_timer";
    private static final String TILE_TAG = "tile";
    static final String COMPLETED = "Completed";
    static final String PROGRESS = "Progress: ";
    static final String SLASH = "/";
    private static final String TILE2_TEXT_GOAL = "tile2_text_goal";
    private static final String GOAL_PROGRESS_LBL = "goal_progress";
    private static final String TILE2_TEXT_DESC = "tile2_text_desc";
    private static final String GOAL_LBL = "goal_lbl";
    private static final String GOAL_LBL_SPLIT_1 = "goal_lbl_split_1";
    private static final String GOAL_LBL_SPLIT_2 = "goal_lbl_split_2";

    private static final int PAUSE_Y = 50;
    private static final int PAUSE_X = 260;
    private static final int GOALS_X = 610;
    private static final int GOAL_TILE_START_Y = 400;
    private static final int GOAL_TILE_SPACE_X = 170;
    private static final float GOAL_TILE_SCALE = 2f;
    private static final int GOAL_TILE_STEP_Y = 110;
    private static final int TAP_COOLDOWN = 10;
    private static final int PAUSE_COUNT = 3;
    private static final String GOALS_POPUP = "goal_popup_lib";
    private static final String ACHIEVED = "achieved";
    private static final String NOTACHIEVED = "notachieved";
    private static final int PAUSE_Y_UP = 460;
    private static final String PAUSE_TEXT = "Pause";
    private static final String CHALLENGES = "Challenges";
    private static final String LEVEL = "LEVEL: ";

    private static Map<Goal, Entity> tiles;

    public static boolean pauseUpdate;
    public static boolean goalsUpdate;

    private ItemWrapper gameItem;
    private Entity pauseDialogE;

    private Entity lblPauseTimer;
    private float pauseTimer = 0;
    private int pauseCounter = 10;
    private int tapCoolDown = 30;

    private String goalProgressValue;
    private float popupX;

    public PauseDialog(GameStage gameStage, ItemWrapper gameItem) {
        super(gameStage);
        tiles = new HashMap<>();
        this.gameItem = gameItem;
    }

    public void init() {
        CompositeItemVO tempItemC = gameStage.sceneLoader.loadVoFromLibrary(GOALS_POPUP).clone();
        pauseDialogE = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), tempItemC);
        gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), pauseDialogE, tempItemC.composite);
        gameStage.sceneLoader.getEngine().addEntity(pauseDialogE);

        final Entity closePauseBtn = pauseDialogE.getComponent(NodeComponent.class).getChild(BTN_CLOSE);

        closePauseBtn.add(new ButtonComponent());
        closePauseBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(closePauseBtn) {
                    @Override
                    public void clicked() {
                        closePauseDialog();
                    }
                });

        final Entity finishCheatBtn = pauseDialogE.getComponent(NodeComponent.class).getChild("btn_finish_cheat");
        finishCheatBtn.add(new ButtonComponent());
        finishCheatBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(finishCheatBtn) {
                    @Override
                    public void clicked() {
                        for (Goal g :gameStage.gameScript.fpc.level.goals.values()) {
                            g.counter = g.n;
                        }
                    }
                });

        final TransformComponent dialogTc = pauseDialogE.getComponent(TransformComponent.class);
        dialogTc.x = FAR_FAR_AWAY_X;
        dialogTc.y = FAR_FAR_AWAY_Y;
        initShadow();

        if(gameItem.getChild(PAUSETIMER_C).getEntity() != null) {
            lblPauseTimer = gameItem.getChild(PAUSETIMER_C).getEntity().getComponent(NodeComponent.class).getChild(LBL_PAUSE_TIMER);
        }
        if (lblPauseTimer != null) {
            lblPauseTimer.getComponent(LabelComponent.class).text.replace(0,
                    lblPauseTimer.getComponent(LabelComponent.class).text.length, "");
            gameItem.getChild(PAUSETIMER_C).getEntity().getComponent(TintComponent.class).color.a = 0;
        }

        final Entity goalLabel = pauseDialogE.getComponent(NodeComponent.class).getChild(LBL_DIALOG);
        LabelComponent goalsLabelComp = goalLabel.getComponent(LabelComponent.class);
        goalsLabelComp.fontScaleX = 0.7f;
        goalsLabelComp.fontScaleY = 0.7f;
        goalsLabelComp.text.replace(0, goalsLabelComp.text.capacity(), PAUSE_TEXT);

        final Entity goalLabelsh = pauseDialogE.getComponent(NodeComponent.class).getChild(LBL_DIALOG_SH);
        LabelComponent goalsLabelCompsh = goalLabelsh.getComponent(LabelComponent.class);
        goalsLabelCompsh.fontScaleX = 0.7f;
        goalsLabelCompsh.fontScaleY = 0.7f;
        goalsLabelCompsh.text.replace(0, goalsLabelCompsh.text.capacity(), PAUSE_TEXT);

        createGoalTiles();

        popupX = PAUSE_X;
    }

    public void initGoals() {
        init();

        final Entity goalLabel = pauseDialogE.getComponent(NodeComponent.class).getChild(LBL_DIALOG);
        LabelComponent goalsLabelComp = goalLabel.getComponent(LabelComponent.class);
        goalsLabelComp.text.replace(0, goalsLabelComp.text.capacity(), CHALLENGES);
        goalsLabelComp.fontScaleX = 0.7f;
        goalsLabelComp.fontScaleY = 0.7f;

        final Entity goalLabelsh = pauseDialogE.getComponent(NodeComponent.class).getChild(LBL_DIALOG_SH);
        LabelComponent goalsLabelCompsh = goalLabelsh.getComponent(LabelComponent.class);
        goalsLabelCompsh.text.replace(0, goalsLabelCompsh.text.capacity(), CHALLENGES);
        goalsLabelCompsh.fontScaleX = 0.7f;
        goalsLabelCompsh.fontScaleY = 0.7f;

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
        SoundMgr.getSoundMgr().play(SoundMgr.WIND_POP_UP_OPEN);

        isPause.set(true);
        isActive = true;
        pauseCounter = 10;
        addShadow();

        if (lblPauseTimer != null) {
            lblPauseTimer.getComponent(LabelComponent.class).text.replace(0,
                    lblPauseTimer.getComponent(LabelComponent.class).text.length, "");
        }

        int y = GOAL_TILE_START_Y;
        LabelComponent levelLabelsComp = pauseDialogE.getComponent(NodeComponent.class).getChild(LBL_LEVEL_INDICATOR).getComponent(LabelComponent.class);
        levelLabelsComp.text.replace(0, levelLabelsComp.text.capacity(), String.valueOf(LEVEL + gameStage.gameScript.fpc.level.difficultyLevel));

        if (goalsUpdate || pauseUpdate){
            createGoalTiles();
        }

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
        tiles = new HashMap<>();
        List<Entity> tileEntities = getTileEntities(pauseDialogE);
        int i = 0;
        for (Goal goal : gameStage.gameScript.fpc.level.getGoals()) {
            Entity tile = tileEntities.get(i);

            goalProgressValue = String.valueOf(goal.getCounter());
            NodeComponent nc = tile.getComponent(NodeComponent.class);
            for (Entity e : nc.children) {

                if (goal.getCounter() >= goal.getN()) {
                    goalProgressValue = COMPLETED;
                } else {
                    goalProgressValue = PROGRESS + String.valueOf(goal.getCounter() + SLASH + goal.getN());
                }
                if (e.getComponent(MainItemComponent.class).tags.contains(TILE2_TEXT_GOAL)) {
                    e.getComponent(NodeComponent.class).getChild(GOAL_PROGRESS_LBL).getComponent(LabelComponent.class).text.replace(0, e.getComponent(NodeComponent.class).getChild(GOAL_PROGRESS_LBL).getComponent(LabelComponent.class).text.capacity(),
                            goalProgressValue);
                } else if (e.getComponent(MainItemComponent.class).tags.contains(TILE2_TEXT_DESC)) {
                    if(goal.getDescription().length() > 24) {
                        String[] words = goal.getDescription().split(" ");
                        String split1 = "";
                        String split2 = "";
                        for(String s: words){
                            if(split1.length() + s.length() < 24 && split2 == ""){
                                split1 += s + " ";
                            }else{
                                split2 += s + " ";
                            }
                        }
                        e.getComponent(NodeComponent.class).getChild(GOAL_LBL).getComponent(LabelComponent.class).text.replace(0, e.getComponent(NodeComponent.class).getChild(GOAL_LBL).getComponent(LabelComponent.class).text.capacity(),
                                goal.getDescription());

                        e.getComponent(NodeComponent.class).getChild(GOAL_LBL).getComponent(TintComponent.class).color.a = 0;
                        e.getComponent(NodeComponent.class).getChild(GOAL_LBL_SPLIT_1).getComponent(LabelComponent.class).text.replace(0, e.getComponent(NodeComponent.class).getChild(GOAL_LBL_SPLIT_1).getComponent(LabelComponent.class).text.capacity(),
                                split1);
                        e.getComponent(NodeComponent.class).getChild(GOAL_LBL_SPLIT_2).getComponent(LabelComponent.class).text.replace(0, e.getComponent(NodeComponent.class).getChild(GOAL_LBL_SPLIT_2).getComponent(LabelComponent.class).text.capacity(),
                                split2);
                        e.getComponent(NodeComponent.class).getChild(GOAL_LBL_SPLIT_1).getComponent(TintComponent.class).color.a = 1;
                        e.getComponent(NodeComponent.class).getChild(GOAL_LBL_SPLIT_2).getComponent(TintComponent.class).color.a = 1;
                    }else{
                        e.getComponent(NodeComponent.class).getChild(GOAL_LBL).getComponent(LabelComponent.class).text.replace(0, e.getComponent(NodeComponent.class).getChild(GOAL_LBL).getComponent(LabelComponent.class).text.capacity(),
                                goal.getDescription());
                        e.getComponent(NodeComponent.class).getChild(GOAL_LBL).getComponent(TintComponent.class).color.a = 1;
                        e.getComponent(NodeComponent.class).getChild(GOAL_LBL_SPLIT_1).getComponent(TintComponent.class).color.a = 0;
                        e.getComponent(NodeComponent.class).getChild(GOAL_LBL_SPLIT_2).getComponent(TintComponent.class).color.a = 0;
                    }
                }
            }
            tile.getComponent(ZIndexComponent.class).setZIndex(160);
            tiles.put(goal, tile);
            i++;
        }
        while (i < tileEntities.size()) {
            tileEntities.get(i++).getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        }
    }

    public void reset() {
        shadowE.getComponent(TintComponent.class).color.a = 0;
        pauseCounter = 0;
        if (isSecondDialogOpen.get()) {
            isSecondDialogClosed.set(true);
        } else {
            isDialogOpen.set(false);
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

        for (Entity e : tile.getComponent(NodeComponent.class).children) {
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
        tile.getComponent(ZIndexComponent.class).setZIndex(pauseDialogE.getComponent(ZIndexComponent.class).getZIndex()+20);
    }

    public void update(float delta) {
        if (pauseCounter == 0 && isPause.get() && pauseTimer >= 1 && lblPauseTimer != null) {
            gameItem.getChild(PAUSETIMER_C).getEntity().getComponent(TintComponent.class).color.a = 0;
            isPause.set(false);
//            System.out.println("setting isPause to false! All the way from pause dialog.");

            lblPauseTimer.getComponent(LabelComponent.class).text.replace(0,
                    lblPauseTimer.getComponent(LabelComponent.class).text.capacity(),
                    "");

            ActionComponent ac2 = new ActionComponent();
            ac2.dataArray.add(Actions.fadeOut(0.5f, Interpolation.exp5));
            shadowE.add(ac2);
            pauseCounter = 10;
        }
        pauseTimer += delta;
        if (pauseCounter <= PAUSE_COUNT && pauseCounter > 0 && pauseCounter < 8) {

//            System.out.println("pauseTimer * pauseCounter/3: " + pauseTimer * pauseCounter/3);
//            System.out.println("scaleX: " + gameItem.getChild(PAUSETIMER_C).getEntity().getComponent(NodeComponent.class).getChild(IMG_PAUSE_TIMER).getComponent(TransformComponent.class).scaleX);
//            if(gameItem.getChild(PAUSETIMER_C).getEntity().getComponent(NodeComponent.class).getChild(IMG_PAUSE_TIMER) != null) {
//                gameItem.getChild(PAUSETIMER_C).getEntity().getComponent(NodeComponent.class).getChild(IMG_PAUSE_TIMER).getComponent(TransformComponent.class).scaleX -= (float) pauseCounter / 6;
//                gameItem.getChild(PAUSETIMER_C).getEntity().getComponent(NodeComponent.class).getChild(IMG_PAUSE_TIMER).getComponent(TransformComponent.class).scaleY -= (float) pauseCounter / 6;
//            }

            if(gameItem.getChild(PAUSETIMER_C).getEntity() != null) {
                gameItem.getChild(PAUSETIMER_C).getEntity().getComponent(TintComponent.class).color.a = 1;
            }
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

    public static void setToUpdate (){
        pauseUpdate = true;
        goalsUpdate = true;
    }

    @Override
    public void close (Entity e){
        SoundMgr.getSoundMgr().play(SoundMgr.WIND_POP_UP_CLOSE);

        if (isActive) {
            isActive = false;
            ActionComponent ac = new ActionComponent();
            Actions.checkInit();
            ac.dataArray.add(Actions.moveTo(e.getComponent(TransformComponent.class).x, HIDE_Y, 1, Interpolation.exp10));
            e.add(ac);
            pauseCounter = 10;

            if(!gameStage.currentScreen.equals("Game")) {
                ActionComponent ac2 = new ActionComponent();
                ac2.dataArray.add(Actions.fadeOut(0.5f, Interpolation.exp5));
                shadowE.add(ac2);
            }else{
                shadowE.getComponent(ZIndexComponent.class).setZIndex(gameItem.getChild(PauseDialog.PAUSETIMER_C).getEntity().getComponent(ZIndexComponent.class).getZIndex() -1);
            }

            if (isSecondDialogOpen.get()) {
                isSecondDialogClosed.set(true);
            } else {
                isDialogOpen.set(false);
            }
        }
    }
}
