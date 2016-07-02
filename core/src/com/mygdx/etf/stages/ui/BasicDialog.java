package com.mygdx.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.mygdx.etf.entity.componets.VanityComponent;
import com.mygdx.etf.stages.GameStage;
import com.mygdx.etf.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.HashMap;

/**
 * Created by ARudyk on 7/1/2016.
 */
public class BasicDialog extends AbstractDialog{

    public static final String RESET_ALL_PROGRESS = "RESET ALL PROGRESS. ARE YOU SURE?";
    public static final String RESTORE_ALL_PURCHASES = "RESTORE ALL PURCHASES. ARE YOU SURE?";
    public static final String RESTORE_ALL_PURCHASES_RESULT = "ALL PURCHASES WERE RESTORED";
    public static final String RESET_ALL_PROGRESS_RESULT = "YOUR PROGRESS WAS ERASED";
    public static final String ERROR = "WE HAD ERROR :(";

    public static String BASIC_DIALOG = "dialog_basic";
    public static final String DIALOG_TEXT = "dialog_text";
    public static final String BTN_OK = "btn_ok";
    public static final String BTN_CANCEL = "btn_cancel";


    //different dialog types
    public static final String TYPE_RESTORE_PURCH = "restore_purchases";
    public static final String TYPE_RESET = "reset_progress";
    public static final String TYPE_RESET_RESULT = "reset_progress_res";
    public static final String TYPE_RESTORE_PURCH_RESULT = "restore_purchases_res";

    public static final int DIALOG_Y = 130;
    public static final int DIALOG_X = 260;

    private Entity dialogE;
    private Entity text;
    private Entity okBtn;
    private Entity cancelBtn;

    public AbstractDialog parent;

    public BasicDialog(ItemWrapper gameItem){
        this.gameItem = gameItem;
    }

    public void init (){
        initShadow();

        dialogE = gameItem.getChild(BASIC_DIALOG).getEntity();
        dialogE.getComponent(TransformComponent.class).x = DIALOG_X;
        dialogE.getComponent(TransformComponent.class).y = HIDE_Y;
        dialogE.getComponent(ZIndexComponent.class).setZIndex(shadowE.getComponent(ZIndexComponent.class).getZIndex());

        text = gameItem.getChild(BASIC_DIALOG).getChild(DIALOG_TEXT).getEntity();
        okBtn = gameItem.getChild(BASIC_DIALOG).getChild(BTN_OK).getEntity();
        cancelBtn = gameItem.getChild(BASIC_DIALOG).getChild(BTN_CANCEL).getEntity();

        cancelBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {}

            @Override
            public void touchDown() {}

            @Override
            public void clicked() {
                close(dialogE);
            }
        });
        okBtn.getComponent(TransformComponent.class).x = 110;
        okBtn.getComponent(TransformComponent.class).y = 60;

        cancelBtn.getComponent(TransformComponent.class).x = 465;
        cancelBtn.getComponent(TransformComponent.class).y = 60;
    }

    private void restoreAllPurchases() {
        close(dialogE);
        show(TYPE_RESTORE_PURCH_RESULT);
    }

    public void show(String type){
        parent.isActive = false;
        this.isActive = true;
        addShadow();
        AbstractDialog.isSecondDialogOpen = true;

        dialogE.getComponent(ZIndexComponent.class).setZIndex(shadowE.getComponent(ZIndexComponent.class).getZIndex()+10);

        okBtn.remove(ButtonComponent.class);
        okBtn.add(new ButtonComponent());
        switch (type){
            case TYPE_RESET : {
               showResetPrgress();
                break;
           }
            case TYPE_RESTORE_PURCH : {
                showRestorePurch();
                break;
            }
            case TYPE_RESET_RESULT : {
                showResetProgressResult();
                break;
            }
            case TYPE_RESTORE_PURCH_RESULT : {
                showRestorePurchResult();
                break;
            }
        }
        ActionComponent ac = new ActionComponent();
        Actions.checkInit();
        ac.dataArray.add(Actions.moveTo(DIALOG_X, DIALOG_Y, 2, Interpolation.exp10Out));
        dialogE.add(ac);
    }

    private void showRestorePurch() {
        LabelComponent lc = text.getComponent(LabelComponent.class);
        lc.text.replace(0, lc.text.capacity(), RESTORE_ALL_PURCHASES);

        okBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {}

            @Override
            public void touchDown() {}

            @Override
            public void clicked() {
                restoreAllPurchases();
            }
        });

        okBtn.getComponent(TransformComponent.class).x = 110;
        okBtn.getComponent(TransformComponent.class).y = 60;

        cancelBtn.getComponent(TransformComponent.class).x = 465;
        cancelBtn.getComponent(TransformComponent.class).y = 60;
    }

    private void showResetProgressResult() {
        LabelComponent lc = text.getComponent(LabelComponent.class);
        lc.text.replace(0, lc.text.capacity(), RESET_ALL_PROGRESS_RESULT);
        okBtn.getComponent(TransformComponent.class).x +=50;

        cancelBtn.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
        cancelBtn.getComponent(TransformComponent.class).y = GlobalConstants.FAR_FAR_AWAY_Y;

        okBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {}

            @Override
            public void touchDown() {}

            @Override
            public void clicked() {
                close(dialogE);
            }
        });
    }

    private void showRestorePurchResult() {
        LabelComponent lc = text.getComponent(LabelComponent.class);
        lc.text.replace(0, lc.text.capacity(), RESTORE_ALL_PURCHASES_RESULT);
        okBtn.getComponent(TransformComponent.class).x +=50;
        okBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {}

            @Override
            public void touchDown() {}

            @Override
            public void clicked() {
                close(dialogE);
            }
        });

        okBtn.getComponent(TransformComponent.class).x +=50;

        cancelBtn.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
        cancelBtn.getComponent(TransformComponent.class).y = GlobalConstants.FAR_FAR_AWAY_Y;
    }

    private void showResetPrgress() {
        LabelComponent lc = text.getComponent(LabelComponent.class);
        lc.text.replace(0, lc.text.capacity(), RESET_ALL_PROGRESS);
        okBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {}

            @Override
            public void touchDown() {}

            @Override
            public void clicked() {
                GameStage.resetAllProgress();
                close(dialogE);
                show(TYPE_RESET_RESULT);
            }
        });

        okBtn.getComponent(TransformComponent.class).x = 110;
        okBtn.getComponent(TransformComponent.class).y = 60;

        cancelBtn.getComponent(TransformComponent.class).x = 465;
        cancelBtn.getComponent(TransformComponent.class).y = 60;
    }

    @Override
    public void close (Entity e){
        parent.isActive = true;
        super.close(e);
    }
}
