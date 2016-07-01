package com.mygdx.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.mygdx.etf.entity.componets.VanityComponent;
import com.mygdx.etf.stages.GameStage;
import com.mygdx.etf.stages.MenuScreenScript;
import com.mygdx.etf.utils.SaveMngr;
import com.uwsoft.editor.renderer.components.ActionComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.HashMap;

import static com.mygdx.etf.stages.GameStage.sceneLoader;
import static com.mygdx.etf.utils.GlobalConstants.BEE_SPAWN_INTERVAL_REGULAR;
import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.mygdx.etf.utils.GlobalConstants.FAR_FAR_AWAY_Y;

/**
 * Created by ARudyk on 7/1/2016.
 */
public class BasicDialog extends AbstractDialog{

    public static final String RESET_ALL_PROGRESS = "RESET ALL PROGRESS";
    public static final String RESTORE_ALL_PURCHASES = "RESTORE ALL PURCHASES";

    public static String BASIC_DIALOG = "dialog_basic";
    public static final String DIALOG_TEXT = "dialog_text";
    public static final String BTN_OK = "btn_ok";
    public static final String BTN_CANCEL = "btn_cancel";


    public static final String TYPE_RESTORE_PURCH = "restore_purchases";
    public static final String TYPE_RESET = "reset_progress";

    public static final int DIALOG_Y = 130;
    public static final int DIALOG_X = 260;

    private Entity dialogE;
    private Entity text;
    private Entity okBtn;

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
        Entity cancelBtn = gameItem.getChild(BASIC_DIALOG).getChild(BTN_CANCEL).getEntity();

        cancelBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {

            }

            @Override
            public void touchDown() {

            }

            @Override
            public void clicked() {
                close(dialogE);
            }
        });
    }

    private void restoreAllPurchases() {

    }

//    public void close (Entity e){
//        ActionComponent ac = new ActionComponent();
//        Actions.checkInit();
//        ac.dataArray.add(Actions.moveTo(DIALOG_X, 900, 1, Interpolation.exp10));
//        e.add(ac);
//
//        ActionComponent ac2 = new ActionComponent();
//        ac2.dataArray.add(Actions.fadeOut(0.5f, Interpolation.exp5));
//        shadowE.add(ac2);
//        MenuScreenScript.isDialogOpen = false;
//    }

    public void show(String type){
        addShadow();
        AbstractDialog.isSecondDialogOpen = true;
//        dialogE.getComponent(TransformComponent.class).x = DIALOG_X;
//        dialogE.getComponent(TransformComponent.class).y = 460;

        dialogE.getComponent(ZIndexComponent.class).setZIndex(shadowE.getComponent(ZIndexComponent.class).getZIndex()+10);
        LabelComponent lc = text.getComponent(LabelComponent.class);

        okBtn.remove(ButtonComponent.class);
        okBtn.add(new ButtonComponent());
        if (type.equals(TYPE_RESET)){
            lc.text.replace(0, lc.text.capacity(), RESET_ALL_PROGRESS);
            okBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
                @Override
                public void touchUp() {

                }

                @Override
                public void touchDown() {

                }

                @Override
                public void clicked() {
                    resetAllProgress();
                }
            });
        } else {
            lc.text.replace(0, lc.text.capacity(), RESTORE_ALL_PURCHASES);
            okBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
                @Override
                public void touchUp() {

                }

                @Override
                public void touchDown() {

                }

                @Override
                public void clicked() {
                    restoreAllPurchases();
                }
            });
        }
        ActionComponent ac = new ActionComponent();
        Actions.checkInit();
        ac.dataArray.add(Actions.moveTo(DIALOG_X, DIALOG_Y, 2, Interpolation.exp10Out));
        dialogE.add(ac);
    }


    public void resetAllProgress(){
        for (VanityComponent vc : GameStage.gameScript.fpc.vanities){
            if (vc.enabled){
                vc.disable();
            }
            vc.bought = false;
            vc.enabled = false;
            vc.advertised = false;

            GameStage.gameScript.fpc.score = 0;
            GameStage.gameScript.fpc.bestScore = 0;
            GameStage.gameScript.fpc.totalScore = 0;
            GameStage.gameScript.fpc.level.difficultyLevel = 0;
            GameStage.gameScript.fpc.level.resetNewInfo();

            GameStage.gameScript.fpc.currentPet = null;
            GameStage.gameScript.fpc.upgrades = new HashMap<>();
        }
        close(dialogE);
    }
}
