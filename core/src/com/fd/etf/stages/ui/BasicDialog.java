package com.fd.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.fd.etf.Main;
import com.fd.etf.entity.componets.VanityComponent;
import com.fd.etf.entity.componets.listeners.ImageButtonListener;
import com.fd.etf.stages.GameStage;
import com.fd.etf.stages.MenuScreenScript;
import com.fd.etf.utils.GlobalConstants;
import com.fd.etf.utils.SoundMgr;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import static com.fd.etf.stages.ui.Settings.SETTINGS_SCALE;
import static com.fd.etf.stages.ui.AbstractDialog.isDialogOpen;

public class BasicDialog extends AbstractDialog {

    private static final String RESET_ALL_PROGRESS = "ARE YOU SURE YOU'D LIKE TO \n" + "RESET ALL YOUR PROGRESS?";
    private static final String RESTORE_ALL_PURCHASES = "ARE YOU SURE YOU'D LIKE TO \n" + " RESTORE ALL YOUR PURCHASES?";
    private static final String RESTORE_ALL_PURCHASES_RESULT = "ALL PURCHASES WERE \n" + "RESTORED";
    public static final String RESET_ALL_PROGRESS_RESULT = "YOUR PROGRESS WAS \n" + "RESET";
    public static final String ERROR = "UNEXPECTED ERROR \n" + "OCCURRED!";
    private static final String BYE_BYE_MESSAGE = "ARE YOU SURE YOU'D LIKE TO \n" + " LEAVE THE GAME? :(";

    private static final String BASIC_DIALOG = "popup_basic_lib";
    private static final String DIALOG_TEXT = "dialog_text";
    private static final String BTN_OK = "btn_ok";
    private static final String BTN_CANCEL = "btn_cancel";


    //different dialog types
    private static final String TYPE_RESTORE_PURCH = "restore_purchases";
    public static final String TYPE_RESET = "reset_progress";
    public static final String TYPE_RESET_RESULT = "reset_progress_res";
    public static final String TYPE_RESTORE_PURCH_RESULT = "restore_purchases_res";
    public static final String TYPE_EXIT = "exit";
    public static final String TYPE_ERROR = "error";
    public static final String TYPE_ERROR_SETTINGS = "error_settings";

    private static final int DIALOG_Y = 130;
    private static final int DIALOG_X = 630;
    private static final int OK_BTN_X = 110;
    private static final int BTN_Y = 60;
    private static final int CANCEL_BTN_X = 465;
    private static final int OK_CENTER = 300;

    private Entity dialogE;
    private Entity text;
    private Entity okBtn;
    private Entity cancelBtn;

    public AbstractDialog parent;
    public String type;

    public BasicDialog(GameStage gameStage, ItemWrapper gameItem) {
        super(gameStage);
        this.gameItem = gameItem;
    }

    private void loadFromLib() {
        CompositeItemVO tempItemC = gameStage.sceneLoader.loadVoFromLibrary(BASIC_DIALOG).clone();
        dialogE = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), tempItemC);
        gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), dialogE, tempItemC.composite);
        gameStage.sceneLoader.getEngine().addEntity(dialogE);
    }

    public void init() {
        initShadow();
        loadFromLib();
        dialogE.getComponent(TransformComponent.class).x = DIALOG_X;
        dialogE.getComponent(TransformComponent.class).y = HIDE_Y;
        dialogE.getComponent(TransformComponent.class).scaleX = SETTINGS_SCALE;
        dialogE.getComponent(TransformComponent.class).scaleY = SETTINGS_SCALE;
        dialogE.getComponent(ZIndexComponent.class).setZIndex(shadowE.getComponent(ZIndexComponent.class).getZIndex());

        text = dialogE.getComponent(NodeComponent.class).getChild(DIALOG_TEXT);
        okBtn = dialogE.getComponent(NodeComponent.class).getChild(BTN_OK);
        if (okBtn.getComponent(ButtonComponent.class) == null) {
            okBtn.add(new ButtonComponent());
        }

        cancelBtn = dialogE.getComponent(NodeComponent.class).getChild(BTN_CANCEL);
        if (cancelBtn.getComponent(ButtonComponent.class) == null) {
            cancelBtn.add(new ButtonComponent());
        }
        cancelBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(cancelBtn) {
                    @Override
                    public void clicked() {
                        close(dialogE);
                    }
                });

        okBtn.getComponent(TransformComponent.class).x = OK_BTN_X;
        okBtn.getComponent(TransformComponent.class).y = BTN_Y;

        cancelBtn.getComponent(TransformComponent.class).x = CANCEL_BTN_X;
        cancelBtn.getComponent(TransformComponent.class).y = BTN_Y;
    }

    private void restoreAllPurchases() {
        close(dialogE);
        show(TYPE_RESTORE_PURCH_RESULT);
    }

    public void show(String type) {

        SoundMgr.getSoundMgr().play(SoundMgr.WIND_POP_UP_OPEN);

        this.type = type;
        if (parent != null) {
            parent.isActive = false;
        }
        this.isActive = true;
        addShadow();
        AbstractDialog.isSecondDialogOpen.set(true);
        dialogE.getComponent(ZIndexComponent.class).setZIndex(shadowE.getComponent(ZIndexComponent.class).getZIndex() + 10);
        dialogE.getComponent(TintComponent.class).color.a = 1;
        for (Entity e : dialogE.getComponent(NodeComponent.class).children) {
            e.getComponent(TintComponent.class).color.a = 1;
        }

        okBtn.remove(ButtonComponent.class);
        okBtn.add(new ButtonComponent());
        switch (type) {
            case TYPE_RESET: {
                showResetPrgress();
                break;
            }
            case TYPE_RESTORE_PURCH: {
                showRestorePurchase();
                break;
            }
            case TYPE_RESET_RESULT: {
                showResetProgressResult();
                isSecondDialogClosed.set(false);
                break;
            }
            case TYPE_RESTORE_PURCH_RESULT: {
                showRestorePurchResult();
                isSecondDialogClosed.set(false);
                break;
            }
            case TYPE_EXIT: {
                showExitMessage();
                break;
            }
            case TYPE_ERROR : {
                addShadow(0.7f, 259);
                showErrorResult();
                isSecondDialogClosed.set(false);
                break;
            }
            case TYPE_ERROR_SETTINGS : {
                addShadow(0.7f, 259);
                showErrorSettings();
                isSecondDialogClosed.set(false);
                break;
            }
        }
        ActionComponent ac = new ActionComponent();
        Actions.checkInit();
        ac.dataArray.add(Actions.moveTo(dialogE.getComponent(TransformComponent.class).x, DIALOG_Y, POPUP_MOVE_DURATION, Interpolation.exp10Out));
        dialogE.add(ac);
    }

    private void showExitMessage() {
        dialogE.getComponent(TintComponent.class).color.a = 1;
        LabelComponent lc = text.getComponent(LabelComponent.class);
        lc.text.replace(0, lc.text.capacity(), BYE_BYE_MESSAGE);
        text.getComponent(TransformComponent.class).x = -25;

        okBtn.getComponent(ButtonComponent.class).addListener(new ButtonComponent.ButtonListener() {
            @Override
            public void touchUp() {
            }

            @Override
            public void touchDown() {
            }

            @Override
            public void clicked() {
                Gdx.app.exit();
            }
        });

        okBtn.getComponent(TransformComponent.class).x = OK_BTN_X;
        okBtn.getComponent(TransformComponent.class).y = BTN_Y;

        cancelBtn.getComponent(TransformComponent.class).x = CANCEL_BTN_X;
        cancelBtn.getComponent(TransformComponent.class).y = BTN_Y;
    }

    private void showRestorePurchase() {
        LabelComponent lc = text.getComponent(LabelComponent.class);
        lc.text.replace(0, lc.text.capacity(), RESTORE_ALL_PURCHASES);
        text.getComponent(TransformComponent.class).x = -25;

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

        okBtn.getComponent(TransformComponent.class).x = OK_BTN_X;
        okBtn.getComponent(TransformComponent.class).y = BTN_Y;

        cancelBtn.getComponent(TransformComponent.class).x = CANCEL_BTN_X;
        cancelBtn.getComponent(TransformComponent.class).y = BTN_Y;
    }

    private void showResetProgressResult() {
        LabelComponent lc = text.getComponent(LabelComponent.class);
        lc.text.replace(0, lc.text.capacity(), RESET_ALL_PROGRESS_RESULT);
        text.getComponent(TransformComponent.class).x = -99;
        okBtn.getComponent(TransformComponent.class).x = OK_CENTER;

        cancelBtn.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
        cancelBtn.getComponent(TransformComponent.class).y = GlobalConstants.FAR_FAR_AWAY_Y;

        okBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(okBtn) {
                    @Override
                    public void clicked() {
                        close(dialogE);
                        dialogE.getComponent(TransformComponent.class).y = GlobalConstants.FAR_FAR_AWAY_Y;
                        MenuScreenScript.settings.close(MenuScreenScript.settings.settingsE);
                        MenuScreenScript.settings.settingsE.getComponent(TransformComponent.class).y = GlobalConstants.FAR_FAR_AWAY_Y;
                        VanityComponent.disableAllVanitiesAssets();
                        GameStage.changedFlowerEntity = true;
                        GameStage.changedFlowerEntity2 = true;
                        GameStage.changedFlowerAni = true;
                        GameStage.changedLeavesAni = true;
//                        AbstractDialog.isDialogOpen.set(false);
                        isSecondDialogOpen.set(true);
                        gameStage.initMenu();
                    }
                });
    }

     private void showRestorePurchResult() {
        LabelComponent lc = text.getComponent(LabelComponent.class);
        lc.text.replace(0, lc.text.capacity(), RESTORE_ALL_PURCHASES_RESULT);
        text.getComponent(TransformComponent.class).x = -99;
        okBtn.getComponent(TransformComponent.class).x = OK_CENTER;
        okBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(okBtn) {
                    @Override
                    public void clicked() {
                        close(dialogE);
                    }
                });

        cancelBtn.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
        cancelBtn.getComponent(TransformComponent.class).y = GlobalConstants.FAR_FAR_AWAY_Y;
    }

    private void showErrorSettings() {
        dialogE.getComponent(ZIndexComponent.class).setZIndex(shadowE.getComponent(ZIndexComponent.class).getZIndex() + 10);
        LabelComponent lc = text.getComponent(LabelComponent.class);
        if(Main.mainController.isWifiConnected() == false) {
            lc.text.replace(0, lc.text.capacity(), "INTERNET CONNECTION \n" + "NOT FOUND");
        }else{
            lc.text.replace(0, lc.text.capacity(), ERROR);
        }
        text.getComponent(TransformComponent.class).x = -99;
        okBtn.getComponent(TransformComponent.class).x = OK_CENTER;
        okBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(okBtn) {
                    @Override
                    public void clicked() {
                        close(dialogE);
                    }
                });
        cancelBtn.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
        cancelBtn.getComponent(TransformComponent.class).y = GlobalConstants.FAR_FAR_AWAY_Y;
    }

    private void showErrorResult() {
        dialogE.getComponent(ZIndexComponent.class).setZIndex(170);
        dialogE.getComponent(TransformComponent.class).x = 300;
        dialogE.getComponent(TransformComponent.class).scaleX = 1f;
        dialogE.getComponent(TransformComponent.class).scaleY = 1f;
        dialogE.getComponent(ZIndexComponent.class).setZIndex(shadowE.getComponent(ZIndexComponent.class).getZIndex() + 10);
        LabelComponent lc = text.getComponent(LabelComponent.class);
        if(Main.mainController.isWifiConnected() == false) {
            lc.text.replace(0, lc.text.capacity(), "INTERNET CONNECTION \n" + "NOT FOUND");
        }else if(Main.mainController.getReceivedErrorResponse() == true){
            lc.text.replace(0, lc.text.capacity(), "CAN'T PURCHASE THIS ITEM. \n" + "YOU ALREADY HAVE IT!");
        }else{
            lc.text.replace(0, lc.text.capacity(), ERROR);
        }
        text.getComponent(TransformComponent.class).x = -99;
        okBtn.getComponent(TransformComponent.class).x = OK_CENTER;
        okBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(okBtn) {
                    @Override
                    public void clicked() {
                        close(dialogE);

                        checkSecondaryDialog();
                    }
                });

        cancelBtn.getComponent(TransformComponent.class).x = GlobalConstants.FAR_FAR_AWAY_X;
        cancelBtn.getComponent(TransformComponent.class).y = GlobalConstants.FAR_FAR_AWAY_Y;
    }

    private void showResetPrgress() {
        LabelComponent lc = text.getComponent(LabelComponent.class);
        lc.text.replace(0, lc.text.capacity(), RESET_ALL_PROGRESS);
        text.getComponent(TransformComponent.class).x = -25;
        okBtn.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(okBtn) {
                    @Override
                    public void clicked() {
                        gameStage.resetAllProgress();
                        close(dialogE);
                        show(TYPE_RESET_RESULT);
                    }
                });

        okBtn.getComponent(TransformComponent.class).x = 110;
        okBtn.getComponent(TransformComponent.class).y = BTN_Y;

        cancelBtn.getComponent(TransformComponent.class).x = 465;
        cancelBtn.getComponent(TransformComponent.class).y = BTN_Y;
    }

    @Override
    public void close(Entity e) {
        if (parent != null) {
            parent.isActive = true;
        }
        super.close(e);

        if (type.equals(TYPE_EXIT)){
            MenuScreenScript.canClickPlay = true;
            isDialogOpen.set(false);
        }
    }
}
