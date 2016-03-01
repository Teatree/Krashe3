package com.mygdx.game.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.mygdx.game.entity.componets.Goal;
import com.mygdx.game.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.mygdx.game.stages.GameScreenScript.fpc;
import static com.mygdx.game.stages.GameScreenScript.isPause;
import static com.mygdx.game.utils.EffectUtils.fade;

public class PauseDialog {

    public static final String PAUSE_DIALOG = "dialog";

    private ItemWrapper gameItem;
    private Entity pauseDialog;


    public PauseDialog(ItemWrapper gameItem) {
        this.gameItem = gameItem;
    }

    public void init() {
        pauseDialog = gameItem.getChild(PAUSE_DIALOG).getEntity();
        Entity closePauseBtn = gameItem.getChild(PAUSE_DIALOG).getChild("btn_close").getEntity();
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
        dialogTc.x = 300;
        dialogTc.y = 100;

        final Entity goalLabel = gameItem.getChild(PAUSE_DIALOG).getChild("lbl_dialog").getEntity();
        LabelComponent goalsLabelComp = goalLabel.getComponent(LabelComponent.class);

        StringBuilder goalsList = new StringBuilder();
        goalsList.append(" \n     " + fpc.level.name + " \n ");
        for (Goal g : fpc.level.getGoals()) {
            String achieved = g.achieved ? " achieved " : " not achieved ";
            goalsList.append(" \n  - ").append(g.getDescription()).append(" - ").append(achieved);
        }
        goalsLabelComp.text.replace(0, goalsLabelComp.text.capacity(), "GOALS FOR TODAY!" + goalsList);
        isPause = true;
    }

    public void update() {
        fade(pauseDialog, isPause);
    }

}
