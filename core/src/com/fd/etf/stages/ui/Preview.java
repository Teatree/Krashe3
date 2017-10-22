package com.fd.etf.stages.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import com.fd.etf.entity.componets.ShopItem;
import com.fd.etf.entity.componets.VanityComponent;
import com.fd.etf.entity.componets.listeners.ImageButtonListener;
import com.fd.etf.stages.GameStage;
import com.fd.etf.stages.ShopScreenScript;
import com.fd.etf.utils.SoundMgr;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.systems.action.Actions;
//import com.badlogic.gdx.scenes.scene2d.actions.A

import javax.xml.soap.Node;
import java.util.List;

import static com.fd.etf.entity.componets.ShopItem.HARD;
import static com.fd.etf.entity.componets.ShopItem.SOFT;
import static com.fd.etf.stages.ShopScreenScript.isPreviewOn;
import static com.fd.etf.stages.ShopScreenScript.itemIcons;
import static com.fd.etf.utils.EffectUtils.DEFAULT_LAYER;
import static com.fd.etf.utils.EffectUtils.playYellowStarsParticleEffect;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_X;
import static com.fd.etf.utils.GlobalConstants.FAR_FAR_AWAY_Y;

public class Preview extends AbstractDialog {

    private static final String ITEM_UNKNOWN = "item_unknown_n";
    private static final String BTN_RIGHT = "tag_right_btn";
    private static final String BTN_LEFT = "tag_left_btn";
    private static final String BTN_BUY = "tag_btn_buy";
    private static final String BTN_DISABLE = "tag_btn_disable";
    private static final String BTN_ENABLE = "tag_btn_enable";
    private static final String TAG_INFO_LIB = "tag_info_lib";
    private static final String SPACE_SIGN = "_";

    private static final String BTN_CLOSE = "btn_close_lib";
    private static final String LBL_ITEM_NAME = "tag_lbl_item_name";
    private static final String LBL_ITEM_NAME_2 = "tag_lbl_item_name_2";
    private static final String LBL_PAPER_PIECE = "paper_piece_img";
    private static final String COINZ_ICON = "coinz_icon";
    private static final String LBL_PRICE = "tag_lbl_price";

    private static final int HIDE_INFO_TAG_RIGHT = 2000;
    private static final int HIDE_INFO_TAG_LEFT = -1000;

    private static final String LBL_DESC = "tag_lbl_desc";
    private static final String LBL_DESC_21 = "tag_lbl_desc_21";
    private static final String LBL_DESC_22 = "tag_lbl_desc_22";
    private static final String LBL_DESC_31 = "tag_lbl_desc_31";
    private static final String LBL_DESC_32 = "tag_lbl_desc_32";
    private static final String LBL_DESC_33 = "tag_lbl_desc_33";
    private static final String LBL_DESC_41 = "tag_lbl_desc_41";
    private static final String LBL_DESC_42 = "tag_lbl_desc_42";
    private static final String LBL_DESC_43 = "tag_lbl_desc_43";
    private static final String LBL_DESC_44 = "tag_lbl_desc_44";
    private static final String IMG_SEC_BUBBLE = "img_sec_bubble";
    private static final float HIDE_INFO_TAG_DURATION = 0.3f;
    private static final String BTN_INACTIVE = "Gray";
    private static final String TAG_BUTTONZ_LIB = "tag_buttonz_lib";
    public static final String NEW_LINE_SIGN = "~";
    public Entity lbl_desc;
    public Entity lbl_desc_21;
    public Entity lbl_desc_22;
    public Entity lbl_desc_31;
    public Entity lbl_desc_32;
    public Entity lbl_desc_33;
    public Entity lbl_desc_41;
    public Entity lbl_desc_42;
    public Entity lbl_desc_43;
    public Entity lbl_desc_44;
    public Entity lblPrice;
    public Entity lblPriceSh;
    public Entity lblTitle;
    public Entity lblTitleLine2;

    private static final String TAG_NOT_NUFF = "tag_lbl_not_enough";
    public Entity lblNotEnough;

    private static final int ICON_X = 550;
    private static final float ICON_SCALE_X = 2.5f;
    private static final float ICON_SCALE_Y = 2.5f;
    private static final int ICON_X_RELATIVE = 70;
    private static final int ICON_Y_RELATIVE = 180;

    private static final int INFO_TAG_X = 350;
    private static final int INFO_TAG_Y = 240;

    private static final int BTNZ_X = 308;
    private static final int BTNZ_Y = 33;

    private static final int BTNZ_CLOSE_X = 1050;
    private static final int BTNZ_CLOSE_Y = 625;

    private static final int UNKNOWN_ICON_Y = 350;
    private static final int INFO_TAG_HIDE_Y = 900;
    private static final int UNKNOWN_ICON_Y_ON_JUMP = INFO_TAG_HIDE_Y;

    private static boolean shouldDeleteIconE = true;
    private Entity iconE;
    private Entity btnPrev;
    private Entity btnNext;
    private Entity btnClose;
    private Entity btnBuy;
    private Entity infoTag;
    private Entity buttonz;

    private ShopItem vc;
    private int movedTo;
    public boolean canPlayDescAni;

    private void loadPreviewFromLib() {
        CompositeItemVO infoTempItemC = gameStage.sceneLoader.loadVoFromLibrary(TAG_INFO_LIB).clone();
        infoTag = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), infoTempItemC);
        gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), infoTag, infoTempItemC.composite);
        gameStage.sceneLoader.getEngine().addEntity(infoTag);

        CompositeItemVO buttonzTempItemC = gameStage.sceneLoader.loadVoFromLibrary(TAG_BUTTONZ_LIB).clone();
        buttonz = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), buttonzTempItemC);
        gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), buttonz, buttonzTempItemC.composite);
        gameStage.sceneLoader.getEngine().addEntity(buttonz);
    }

    public Preview(GameStage gameStage) {
        super(gameStage);
    }

    public void init() {
        loadPreviewFromLib();
        Entity bubble = infoTag.getComponent(NodeComponent.class).getChild("bubble");

        lbl_desc = bubble.getComponent(NodeComponent.class).getChild(LBL_DESC);
        lbl_desc_21 = bubble.getComponent(NodeComponent.class).getChild(LBL_DESC_21);
        lbl_desc_22 = bubble.getComponent(NodeComponent.class).getChild(LBL_DESC_22);
        lbl_desc_31 = bubble.getComponent(NodeComponent.class).getChild(LBL_DESC_31);
        lbl_desc_32 = bubble.getComponent(NodeComponent.class).getChild(LBL_DESC_32);
        lbl_desc_33 = bubble.getComponent(NodeComponent.class).getChild(LBL_DESC_33);
        lbl_desc_41 = bubble.getComponent(NodeComponent.class).getChild(LBL_DESC_41);
        lbl_desc_42 = bubble.getComponent(NodeComponent.class).getChild(LBL_DESC_42);
        lbl_desc_43 = bubble.getComponent(NodeComponent.class).getChild(LBL_DESC_43);
        lbl_desc_44 = bubble.getComponent(NodeComponent.class).getChild(LBL_DESC_44);
        lbl_desc.getComponent(TintComponent.class).color.a = 0;
        lbl_desc_21.getComponent(TintComponent.class).color.a = 0;
        lbl_desc_22.getComponent(TintComponent.class).color.a = 0;
        lbl_desc_31.getComponent(TintComponent.class).color.a = 0;
        lbl_desc_32.getComponent(TintComponent.class).color.a = 0;
        lbl_desc_33.getComponent(TintComponent.class).color.a = 0;
        lbl_desc_41.getComponent(TintComponent.class).color.a = 0;
        lbl_desc_42.getComponent(TintComponent.class).color.a = 0;
        lbl_desc_43.getComponent(TintComponent.class).color.a = 0;
        lbl_desc_44.getComponent(TintComponent.class).color.a = 0;

        lblTitle = infoTag.getComponent(NodeComponent.class).getChild(LBL_ITEM_NAME);
        lblTitleLine2 = infoTag.getComponent(NodeComponent.class).getChild(LBL_ITEM_NAME_2);
        lblPrice = buttonz.getComponent(NodeComponent.class).getChild(LBL_PRICE);
        lblPriceSh = buttonz.getComponent(NodeComponent.class).getChild(LBL_PRICE + "_sh");
        lblNotEnough = buttonz.getComponent(NodeComponent.class).getChild(TAG_NOT_NUFF);
        btnPrev = buttonz.getComponent(NodeComponent.class).getChild(BTN_LEFT);
        btnNext = buttonz.getComponent(NodeComponent.class).getChild(BTN_RIGHT);

        CompositeItemVO closeItemC = gameStage.sceneLoader.loadVoFromLibrary(BTN_CLOSE);
        btnClose = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), closeItemC);
        gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), btnClose, closeItemC.composite);
        gameStage.sceneLoader.getEngine().addEntity(btnClose);

        initCloseBtn();
        infoTag.getComponent(ZIndexComponent.class).setZIndex(51);
        buttonz.getComponent(ZIndexComponent.class).setZIndex(51);
        buttonz.getComponent(TransformComponent.class).x = BTNZ_X;
        buttonz.getComponent(TransformComponent.class).y = -500;
        infoTag.getComponent(TransformComponent.class).x = INFO_TAG_X;
        infoTag.getComponent(TransformComponent.class).y = INFO_TAG_HIDE_Y;

        infoTag.getComponent(NodeComponent.class).
                getChild(LBL_PAPER_PIECE).getComponent(ZIndexComponent.class).setZIndex(20);
        ZIndexComponent z1 = infoTag.getComponent(NodeComponent.class).
                getChild(LBL_PAPER_PIECE).getComponent(ZIndexComponent.class);
        btnClose.getComponent(ZIndexComponent.class).setZIndex(z1.getZIndex()+100);
        infoTag.getComponent(NodeComponent.class).
                getChild(LBL_ITEM_NAME).getComponent(ZIndexComponent.class).setZIndex(z1.getZIndex()+1);

        initShadow();
    }

    private void initCloseBtn() {
//        btnClose = infoTag.getComponent(NodeComponent.class).getChild(BTN_CLOSE);

        if (btnClose.getComponent(ButtonComponent.class) == null) {
            btnClose.add(new ButtonComponent());
        }
        btnClose.getComponent(TransformComponent.class).x = 760;
        btnClose.getComponent(TransformComponent.class).y = 470;
        btnClose.getComponent(ButtonComponent.class).clearListeners();
        btnClose.getComponent(ButtonComponent.class).
                addListener(new ImageButtonListener(btnClose) {
                    @Override
                    public void clicked() {
                        checkAndClose();
                    }
                });
    }

    public void initBoughtPreviewIcon(boolean playAni) {

        removeIconE();
        CompositeItemVO tempItemC = gameStage.sceneLoader.loadVoFromLibrary(vc.shopIcon);
        iconE = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), tempItemC);
        gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), iconE, tempItemC.composite);
        gameStage.sceneLoader.getEngine().addEntity(iconE);

        if (playAni) {
            iconE.getComponent(TransformComponent.class).x = infoTag.getComponent(TransformComponent.class).x + 20;
            iconE.getComponent(TransformComponent.class).y = UNKNOWN_ICON_Y;
            iconE.getComponent(ZIndexComponent.class).setZIndex(infoTag.getComponent(ZIndexComponent.class).getZIndex() + 10);
            playYellowStarsParticleEffect(gameStage, 544, 467);
        } else {
            iconE.getComponent(TransformComponent.class).x = INFO_TAG_X + ICON_X_RELATIVE;
            iconE.getComponent(TransformComponent.class).y = INFO_TAG_Y + ICON_Y_RELATIVE;
            iconE.getComponent(TransformComponent.class).scaleX = ICON_SCALE_X;
            iconE.getComponent(TransformComponent.class).scaleY = ICON_SCALE_Y;
            iconE.getComponent(ZIndexComponent.class).setZIndex(infoTag.getComponent(ZIndexComponent.class).getZIndex() + 200);
        }
    }

    public void setLabelsValues() {
//        lblTitle.getComponent(LabelComponent.class).text.replace(0, lblTitle.getComponent(LabelComponent.class).text.length, vc.name);
        if (vc.name.contains(NEW_LINE_SIGN)){
            String[] lines = vc.name.split(NEW_LINE_SIGN);
            lblTitle.getComponent(TintComponent.class).color.a = 1;
            lblTitleLine2.getComponent(ZIndexComponent.class).setZIndex(lblTitle.getComponent(ZIndexComponent.class).getZIndex() + 1);
            lblTitleLine2.getComponent(TransformComponent.class).y = 58;
            lblTitle.getComponent(LabelComponent.class).text.replace(
                    0, lblTitle.getComponent(LabelComponent.class).text.length, lines[0]);
            lblTitleLine2.getComponent(LabelComponent.class).text.replace(
                    0, lblTitleLine2.getComponent(LabelComponent.class).text.length, lines[1]);
        }else {
            lblTitle.getComponent(TintComponent.class).color.a = 0;
            lblTitle.getComponent(ZIndexComponent.class).setZIndex(lblTitle.getComponent(ZIndexComponent.class).getZIndex()+1);
            lblTitleLine2.getComponent(LabelComponent.class).text.replace(
                    0, lblTitleLine2.getComponent(LabelComponent.class).text.length, vc.name);
            lblTitleLine2.getComponent(TransformComponent.class).y = 85;
        }

        if (vc.name.contains(SPACE_SIGN)) {
            lblTitle.getComponent(LabelComponent.class).text.replace(
                    SPACE_SIGN, " ");
            lblTitleLine2.getComponent(LabelComponent.class).text.replace(
                    SPACE_SIGN, " ");
        }

        lblPrice.getComponent(LabelComponent.class).text.replace(0, lblPrice.getComponent(LabelComponent.class).text.length,
                String.valueOf(vc.cost));
        lblPriceSh.getComponent(LabelComponent.class).text.replace(0, lblPriceSh.getComponent(LabelComponent.class).text.length,
                String.valueOf(vc.cost));
        lblPrice.getComponent(LabelComponent.class).fontScaleX = 0.7f;
        lblPrice.getComponent(LabelComponent.class).fontScaleY = 0.7f;
        lblPriceSh.getComponent(LabelComponent.class).fontScaleX = 0.7f;
        lblPriceSh.getComponent(LabelComponent.class).fontScaleY = 0.7f;

        if(btnBuy != null && lblPriceSh.getComponent(LabelComponent.class).text.length <= 3) {
            btnBuy.getComponent(TransformComponent.class).scaleX = 0.9f;
            btnBuy.getComponent(TransformComponent.class).x = 140;
        }else if(btnBuy != null && lblPriceSh.getComponent(LabelComponent.class).text.length == 4){
            btnBuy.getComponent(TransformComponent.class).scaleX = 1f;
            btnBuy.getComponent(TransformComponent.class).x = 130;
        }else if(btnBuy != null && lblPriceSh.getComponent(LabelComponent.class).text.length == 5){
            btnBuy.getComponent(TransformComponent.class).scaleX = 1.1f;
            btnBuy.getComponent(TransformComponent.class).x = 115;
        }else if(btnBuy != null && lblPriceSh.getComponent(LabelComponent.class).text.length == 6){
            btnBuy.getComponent(TransformComponent.class).scaleX = 1.2f;
            btnBuy.getComponent(TransformComponent.class).x = 110;
        }
    }

    public boolean canBuyCheck(VanityComponent vc, Entity btn_buy) {
        if (vc.canBuy(gameStage) ) {
            btn_buy.getComponent(ZIndexComponent.class).setZIndex(100);
            lblNotEnough.remove(ActionComponent.class);
            lblNotEnough.getComponent(TintComponent.class).color.a = 0;
            return true;
        } else {
            btn_buy.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
            lblNotEnough.getComponent(TransformComponent.class).y = 36;
            lblNotEnough.getComponent(TintComponent.class).color.a = 1;
            if (lblNotEnough.getComponent(ActionComponent.class) == null) {
                lblNotEnough.add(new ActionComponent());
            }
            lblNotEnough.getComponent(ActionComponent.class).reset();

            lblNotEnough.getComponent(ActionComponent.class).dataArray.add(Actions.sequence(Actions.moveTo(lblNotEnough.getComponent(TransformComponent.class).x, lblNotEnough.getComponent(TransformComponent.class).y + 20, 1f, Interpolation.fade),
                    Actions.moveTo(lblNotEnough.getComponent(TransformComponent.class).x, lblNotEnough.getComponent(TransformComponent.class).y, 2f, Interpolation.fade),
                    Actions.moveTo(lblNotEnough.getComponent(TransformComponent.class).x, lblNotEnough.getComponent(TransformComponent.class).y + 20, 2f, Interpolation.fade),
                    Actions.moveTo(lblNotEnough.getComponent(TransformComponent.class).x, lblNotEnough.getComponent(TransformComponent.class).y, 2f, Interpolation.fade),
                    Actions.moveTo(lblNotEnough.getComponent(TransformComponent.class).x, lblNotEnough.getComponent(TransformComponent.class).y + 20, 2f, Interpolation.fade),
                    Actions.moveTo(lblNotEnough.getComponent(TransformComponent.class).x, lblNotEnough.getComponent(TransformComponent.class).y, 2f, Interpolation.fade),
                    Actions.moveTo(lblNotEnough.getComponent(TransformComponent.class).x, lblNotEnough.getComponent(TransformComponent.class).y + 20, 2f, Interpolation.fade),
                    Actions.moveTo(lblNotEnough.getComponent(TransformComponent.class).x, lblNotEnough.getComponent(TransformComponent.class).y, 2f, Interpolation.fade),
                    Actions.moveTo(lblNotEnough.getComponent(TransformComponent.class).x, lblNotEnough.getComponent(TransformComponent.class).y + 20, 2f, Interpolation.fade),
                    Actions.moveTo(lblNotEnough.getComponent(TransformComponent.class).x, lblNotEnough.getComponent(TransformComponent.class).y, 2f, Interpolation.fade),
                    Actions.moveTo(lblNotEnough.getComponent(TransformComponent.class).x, lblNotEnough.getComponent(TransformComponent.class).y + 20, 2f, Interpolation.fade),
                    Actions.moveTo(lblNotEnough.getComponent(TransformComponent.class).x, lblNotEnough.getComponent(TransformComponent.class).y, 2f, Interpolation.fade),
                    Actions.moveTo(lblNotEnough.getComponent(TransformComponent.class).x, lblNotEnough.getComponent(TransformComponent.class).y + 20, 2f, Interpolation.fade),
                    Actions.moveTo(lblNotEnough.getComponent(TransformComponent.class).x, lblNotEnough.getComponent(TransformComponent.class).y, 2f, Interpolation.fade)));
            Actions.checkInit();
        }
        lblNotEnough.getComponent(ZIndexComponent.class).setZIndex(99);
        lblPrice.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        lblPriceSh.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        buttonz.getComponent(NodeComponent.class).getChild(COINZ_ICON).getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
        return false;
    }

    public Entity initUnknownPreviewIcon(boolean jump) {
        removeIconE();

        CompositeItemVO tempItemC = gameStage.sceneLoader.loadVoFromLibrary(ITEM_UNKNOWN);
        iconE = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), tempItemC);
        gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), iconE, tempItemC.composite);
        gameStage.sceneLoader.getEngine().addEntity(iconE);

        lblNotEnough.getComponent(ZIndexComponent.class).setZIndex(52);

        if (!jump) {
            iconE.getComponent(TransformComponent.class).x =
                    infoTag.getComponent(TransformComponent.class).x + 20;
            iconE.getComponent(TransformComponent.class).y = UNKNOWN_ICON_Y;
        } else {
            iconE.getComponent(TransformComponent.class).x = ICON_X;
            iconE.getComponent(TransformComponent.class).scaleX = ICON_SCALE_X;
            iconE.getComponent(TransformComponent.class).scaleY = ICON_SCALE_Y;
            iconE.getComponent(TransformComponent.class).y = UNKNOWN_ICON_Y_ON_JUMP;
        }
        iconE.getComponent(ZIndexComponent.class).setZIndex(infoTag.getComponent(ZIndexComponent.class).getZIndex() + 100);
        return iconE;
    }

    public void showPreview(ShopItem vc, boolean jump, boolean justBoughtAni) {
        if (infoTag == null) {
            init();
        }
        this.vc = vc;
        isPreviewOn.set(true);
        setLabelsValues();

        if (!vc.bought) {
            initBuyButton(vc);
        } else {
            initEnableButton(vc);
            initDisableButton(vc);
        }
        initPrevButton(vc);
        initNextButton(vc);

        initIcon(vc, jump, justBoughtAni);
        iconE.getComponent(ZIndexComponent.class).setZIndex(101);

        if (jump) {
            addShadow(0.8f);
            infoTag.getComponent(TransformComponent.class).y = INFO_TAG_HIDE_Y - 10;
            ActionComponent ac = new ActionComponent();
            Actions.checkInit();
            ac.dataArray.add(Actions.moveTo(INFO_TAG_X, INFO_TAG_Y, 1f, Interpolation.exp10Out));
            infoTag.add(ac);

            btnClose.getComponent(TransformComponent.class).x = BTNZ_CLOSE_X;
            btnClose.getComponent(TransformComponent.class).y = 1200;
            ActionComponent acClose = new ActionComponent();
            Actions.checkInit();
            acClose.dataArray.add(Actions.moveTo(BTNZ_CLOSE_X, BTNZ_CLOSE_Y, 1f, Interpolation.exp10Out));
            btnClose.add(acClose);

            ActionComponent acButtonz = new ActionComponent();
            Actions.checkInit();
            acButtonz.dataArray.add(Actions.moveTo(BTNZ_X, BTNZ_Y, 1f, Interpolation.exp10Out));
            buttonz.add(acButtonz);

        } else {
            ActionComponent ac = new ActionComponent();
            Actions.checkInit();
            ac.dataArray.add(Actions.moveTo(INFO_TAG_X, infoTag.getComponent(TransformComponent.class).y, HIDE_INFO_TAG_DURATION));
            infoTag.add(ac);
        }

        setDescription(vc, jump, justBoughtAni);

        for (Entity e: ShopScreenScript.itemIcons.values()){
            e.getComponent(ZIndexComponent.class).
                    setZIndex(shadowE.getComponent(ZIndexComponent.class).getZIndex()-2);
        }
        infoTag.getComponent(ZIndexComponent.class).setZIndex(shadowE.getComponent(ZIndexComponent.class).getZIndex() + 10);
        buttonz.getComponent(ZIndexComponent.class).setZIndex(shadowE.getComponent(ZIndexComponent.class).getZIndex() + 10);
    }

    private void setDescription(ShopItem vc, boolean jump, boolean justBoughtAni) {
        setDesciptionLabels();
        infoTag.getComponent(NodeComponent.class).
                getChild(LBL_PAPER_PIECE).getComponent(ZIndexComponent.class).setZIndex(20);
        infoTag.getComponent(NodeComponent.class).
                getChild(LBL_ITEM_NAME).getComponent(ZIndexComponent.class).setZIndex(21);
        infoTag.getComponent(NodeComponent.class).
                getChild(LBL_ITEM_NAME_2).getComponent(ZIndexComponent.class).setZIndex(21);
        Entity bubble = infoTag.getComponent(NodeComponent.class).getChild("bubble");

        if (vc.description == null) {
            bubble.getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
        } else if (vc.description != null && canPlayDescAni){

            bubble.getComponent(TransformComponent.class).x = 100;
            bubble.getComponent(TransformComponent.class).y = 51;
            bubble.getComponent(TransformComponent.class).scaleX = 0.5f;
            bubble.getComponent(TransformComponent.class).scaleY = 0.5f;
            bubble.getComponent(TintComponent.class).color.a = 0;
            bubble.getComponent(ZIndexComponent.class).setZIndex(18);

            if(bubble.getComponent(ActionComponent.class) != null) {
                bubble.remove(ActionComponent.class);
            }

            ActionComponent ac = new ActionComponent();
            ActionComponent ac2 = new ActionComponent();
            Actions.checkInit();
            ac.dataArray.add(Actions.sequence(Actions.delay(0.5f),
                    Actions.parallel(Actions.fadeIn(1.5f, Interpolation.exp5), Actions.moveTo(453, 51, 1, Interpolation.exp5),
                            Actions.scaleTo(1, 1, 1f, Interpolation.exp5))));
            ac2.dataArray.add(Actions.sequence(Actions.delay(0.5f),
                    Actions.fadeIn(1.5f, Interpolation.exp5)));
            bubble.add(ac);
            for(Entity e : bubble.getComponent(NodeComponent.class).children){
                if(e.getComponent(MainItemComponent.class).itemIdentifier != IMG_SEC_BUBBLE &&
                        e.getComponent(LabelComponent.class) != null &&
                        !e.getComponent(LabelComponent.class).text.toString().contains("Error")){
                    e.add(ac2);
                }
                if(e.getComponent(LabelComponent.class) != null &&
                        e.getComponent(LabelComponent.class).text.toString().contains("Error")){
                    e.getComponent(TintComponent.class).color.a = 0;
                }
//                e.getComponent(ZIndexComponent.class).setZIndex(100);
            }
            canPlayDescAni = false;
        }
    }

    private void initIcon(ShopItem vc, boolean jump, boolean justBoughtAni) {
        if (vc.bought || vc.currencyType.equals(HARD)) {
            initBoughtPreviewIcon(justBoughtAni);
        } else {
            initUnknownPreviewIcon(jump);
        }
    }

    public void initBuyButton(final ShopItem vc) {
        btnBuy = buttonz.getComponent(NodeComponent.class).getChild(BTN_BUY);
        final Entity coinzE = buttonz.getComponent(NodeComponent.class).getChild(COINZ_ICON);
        buttonz.getComponent(NodeComponent.class).getChild(BTN_DISABLE).getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
        buttonz.getComponent(NodeComponent.class).getChild(BTN_ENABLE).getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
        buttonz.getComponent(NodeComponent.class).getChild(TAG_NOT_NUFF).getComponent(TintComponent.class).color.a = 0;

        if (!vc.bought && (vc.currencyType.equals(HARD) || canBuyCheck((VanityComponent) vc, btnBuy))) {
            btnBuy.getComponent(ZIndexComponent.class).setZIndex(61);

            if(btnBuy != null && lblPriceSh.getComponent(LabelComponent.class).text.length <= 3) {
                btnBuy.getComponent(TransformComponent.class).scaleX = 0.9f;
                btnBuy.getComponent(TransformComponent.class).x = 140;
            }else if(btnBuy != null && lblPriceSh.getComponent(LabelComponent.class).text.length == 4){
                btnBuy.getComponent(TransformComponent.class).scaleX = 1f;
                btnBuy.getComponent(TransformComponent.class).x = 130;
            }else if(btnBuy != null && lblPriceSh.getComponent(LabelComponent.class).text.length == 5){
                btnBuy.getComponent(TransformComponent.class).scaleX = 1.1f;
                btnBuy.getComponent(TransformComponent.class).x = 115;
            }else if(btnBuy != null && lblPriceSh.getComponent(LabelComponent.class).text.length == 6){
                btnBuy.getComponent(TransformComponent.class).scaleX = 1.2f;
                btnBuy.getComponent(TransformComponent.class).x = 108;
            }

            btnBuy.getComponent(TransformComponent.class).y = 5;

            if (vc.currencyType.equals(HARD)) {
                coinzE.getComponent(TintComponent.class).color.a = 0;
                lblPrice.getComponent(LabelComponent.class).text.append("$");
                lblPriceSh.getComponent(LabelComponent.class).text.append("$");
            } else {
                coinzE.getComponent(TintComponent.class).color.a = 1;
                coinzE.getComponent(TransformComponent.class).y = 27;
                coinzE.getComponent(ZIndexComponent.class).setZIndex(btnBuy.getComponent(ZIndexComponent.class).getZIndex() + 10);
            }
            // Finding the middle of the button to display price and coin icon
            // only works if width of text label is 1
            int wordCount = lblPrice.getComponent(LabelComponent.class).getText().length;
            int base = 282; // x pos
            if (vc.currencyType.equals(SOFT)) {
                lblPrice.getComponent(TransformComponent.class).x = base - 10 * wordCount;
                lblPriceSh.getComponent(TransformComponent.class).x = lblPrice.getComponent(TransformComponent.class).x +3;
                coinzE.getComponent(TransformComponent.class).x = lblPrice.getComponent(TransformComponent.class).x - coinzE.getComponent(DimensionsComponent.class).width - 8;
            } else {
                lblPrice.getComponent(TransformComponent.class).x = base - 15 * wordCount;
                lblPriceSh.getComponent(TransformComponent.class).x = lblPrice.getComponent(TransformComponent.class).x + 3;
            }
            lblPrice.getComponent(ZIndexComponent.class).setZIndex(btnBuy.getComponent(ZIndexComponent.class).getZIndex() + 10);
            lblPriceSh.getComponent(ZIndexComponent.class).setZIndex(btnBuy.getComponent(ZIndexComponent.class).getZIndex() + 9);

            if (btnBuy.getComponent(ButtonComponent.class) == null) {
                btnBuy.add(new ButtonComponent());
            }
            btnBuy.getComponent(ButtonComponent.class).clearListeners();
            btnBuy.getComponent(ButtonComponent.class).addListener(new ImageButtonListener(btnBuy) {
                @Override
                public void clicked() {
                    if (btnBuy.getComponent(ZIndexComponent.class).getZIndex() > 2 && animFinished()) {
                        SoundMgr.getSoundMgr().play(SoundMgr.BUTTON_TAP_SHOP_BUY);

                        if (vc.currencyType.equals(HARD)) {
                            vc.buyHard(gameStage);
                        } else {
                            vc.buyAndUse(gameStage);
                            putInPlaceNewIconPosition();
                        }
                        showPreview(vc, false, true);
                        ShopScreenScript.btnPlay.getComponent(TransformComponent.class).y = 22;
                        ShopScreenScript.reloadScoreLabel(gameStage.gameScript.fpc);
                    }
                }
            });
        }
    }

    private void putInPlaceNewIconPosition() {
        TransformComponent tc = gameStage.shopScript.changeBagIcon(vc);
        gameStage.sceneLoader.getEngine().addEntity(itemIcons.get(vc.shopIcon));
        itemIcons.get(vc.shopIcon).getComponent(ZIndexComponent.class).setZIndex(
                shadowE.getComponent(ZIndexComponent.class).getZIndex() - 1);
        itemIcons.get(vc.shopIcon).getComponent(TransformComponent.class).x = tc.x;
        itemIcons.get(vc.shopIcon).getComponent(TransformComponent.class).y = tc.y;
        itemIcons.get(vc.shopIcon).getComponent(ZIndexComponent.class).setZIndex(
                ShopScreenScript.bagsZindex + 10);
    }

//    public TransformComponent changeBagIcon(ShopItem vc) {
//        if (vc.currencyType.equals(SOFT)) {
//            CompositeItemVO tempItemC = gameStage.sceneLoader.loadVoFromLibrary(vc.shopIcon);
//            Entity iconBagClone = gameStage.sceneLoader.entityFactory.createEntity(gameStage.sceneLoader.getRoot(), tempItemC);
//            gameStage.sceneLoader.entityFactory.initAllChildren(gameStage.sceneLoader.getEngine(), iconBagClone, tempItemC.composite);
//            TransformComponent oldTc = itemIcons.get(vc.shopIcon).getComponent(TransformComponent.class);
//            ZIndexComponent newZ = itemIcons.get(vc.shopIcon).getComponent(ZIndexComponent.class);
//            iconBagClone.add(newZ);
//            gameStage.sceneLoader.getEngine().removeEntity(itemIcons.get(vc.shopIcon));
//            itemIcons.put(vc.shopIcon, iconBagClone);
//            return oldTc;
//        }
//        return null;
//    }

    public void initEnableButton(final ShopItem vc) {
        if (vc.bought && !vc.enabled) {
            buttonz.getComponent(NodeComponent.class).getChild(BTN_ENABLE).getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
            buttonz.getComponent(NodeComponent.class).getChild(BTN_BUY).getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
            buttonz.getComponent(NodeComponent.class).getChild(TAG_NOT_NUFF).getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
            buttonz.getComponent(NodeComponent.class).getChild(COINZ_ICON).getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
            lblPrice.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            lblPriceSh.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;

            Entity enableBtn = buttonz.getComponent(NodeComponent.class).getChild(BTN_DISABLE);
            enableBtn.getComponent(ZIndexComponent.class).setZIndex(101);
//            enableBtn.getComponent(TransformComponent.class).x = 282;
            enableBtn.getComponent(TransformComponent.class).y = 0;

            if (enableBtn.getComponent(ButtonComponent.class) == null) {
                enableBtn.add(new ButtonComponent());
            }
            enableBtn.getComponent(ButtonComponent.class).clearListeners();
            enableBtn.getComponent(ButtonComponent.class)
                    .addListener(new ImageButtonListener(enableBtn) {
                        @Override
                        public void clicked() {
                            if (animFinished()) {
                                vc.apply(gameStage);
                                showPreview(vc, false, false);
                                ShopScreenScript.btnPlay.getComponent(TransformComponent.class).y = 22;
                            }
                        }
                    });
        }
    }

    public void initDisableButton(final ShopItem vc) {
        if (vc.bought && vc.enabled) {
            buttonz.getComponent(NodeComponent.class).getChild(BTN_DISABLE).getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
            buttonz.getComponent(NodeComponent.class).getChild(BTN_BUY).getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
            buttonz.getComponent(NodeComponent.class).getChild(TAG_NOT_NUFF).getComponent(TransformComponent.class).y = FAR_FAR_AWAY_X;
            buttonz.getComponent(NodeComponent.class).getChild(COINZ_ICON).getComponent(TransformComponent.class).y = FAR_FAR_AWAY_Y;
            lblPrice.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            lblPriceSh.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;

            Entity disableBtn = buttonz.getComponent(NodeComponent.class).getChild(BTN_ENABLE);
            disableBtn.getComponent(ZIndexComponent.class).setZIndex(101);
//            disableBtn.getComponent(TransformComponent.class).x = 282;
            disableBtn.getComponent(TransformComponent.class).y = 0;

            disableBtn.add(new ButtonComponent());
            if (disableBtn.getComponent(ButtonComponent.class) == null) {
                disableBtn.getComponent(ButtonComponent.class).clearListeners();
            }
            disableBtn.getComponent(ButtonComponent.class)
                    .addListener(new ImageButtonListener(disableBtn) {
                        @Override
                        public void clicked() {
                            if (animFinished()) {
                                vc.disable(gameStage);
                                showPreview(vc, false, false);
                                ShopScreenScript.btnPlay.getComponent(TransformComponent.class).y = 22;
                            }
                        }
                    });
        }
    }

    private void initPrevButton(final ShopItem vc) {
        if (btnPrev.getComponent(ButtonComponent.class) == null) {
            btnPrev.add(new ButtonComponent());
        }
        btnPrev.getComponent(ButtonComponent.class).clearListeners();

        LayerMapComponent lc = btnPrev.getComponent(LayerMapComponent.class);
        if (isPrevBtnActive(vc)) {
            lc.getLayer(BTN_INACTIVE).isVisible = false;
            lc.getLayer(DEFAULT_LAYER).isVisible = true;
        } else {
            lc.getLayer(DEFAULT_LAYER).isVisible = false;
            lc.getLayer(BTN_INACTIVE).isVisible = false;
        }

        btnPrev.getComponent(ButtonComponent.class)
                .addListener(new ImageButtonListener(btnPrev) {
                    @Override
                    public void touchUp() {
                        if (isPrevBtnActive(vc))
                            super.touchUp();
                    }

                    @Override
                    public void touchDown() {
                        if (isPrevBtnActive(vc))
                            super.touchDown();
                    }

                    @Override
                    public void clicked() {
                        SoundMgr.getSoundMgr().play(SoundMgr.PAPER_FLIP_SHOP);
                        if (animFinished() && isPrevBtnActive(vc)) {
                            ActionComponent ac = new ActionComponent();
                            Actions.checkInit();
                            ac.dataArray.add(Actions.moveTo(HIDE_INFO_TAG_RIGHT, infoTag.getComponent(TransformComponent.class).y, HIDE_INFO_TAG_DURATION));
                            infoTag.add(ac);
                            movedTo = HIDE_INFO_TAG_RIGHT;

                            if(lblNotEnough.getComponent(ActionComponent.class) != null) {
                                lblNotEnough.getComponent(ActionComponent.class).reset();
                                lblNotEnough.getComponent(TintComponent.class).color.a = 0;
                            }

                            if (vc.currencyType.equals(SOFT) && (gameStage.shopScript.allSoftItems.indexOf(vc)) % 8 == 0) {
                                gameStage.shopScript.scrollBagsOnePageLeft();
                            }
                        }
                    }
                });
    }

    private boolean isPrevBtnActive(ShopItem vc) {
        return (vc.currencyType.equals(HARD) && gameStage.shopScript.allHCItems.indexOf(vc) > 0) ||
                (vc.currencyType.equals(SOFT) && gameStage.shopScript.allSoftItems.indexOf(vc) > 0);
    }

    private void showPreviewAfterAniPrev() {
        if (vc.currencyType.equals(SOFT)) {
            prevItem(gameStage.shopScript.allSoftItems);
        } else {
            prevItem(gameStage.shopScript.allHCItems);
        }
    }

    private void prevItem(List<ShopItem> list) {
        int previousIndex = list.indexOf(vc) - 1;
        if (previousIndex >= 0) {
            setShouldDeleteIconE();
            canPlayDescAni = true;
            showPreview(list.get(previousIndex), false, false);
        }
    }

    private void initNextButton(final ShopItem vc) {
        if (btnNext.getComponent(ButtonComponent.class) == null) {
            btnNext.add(new ButtonComponent());
        }

        LayerMapComponent lc = btnNext.getComponent(LayerMapComponent.class);
        if (isNextBtnActive(vc)) {
            lc.getLayer(BTN_INACTIVE).isVisible = false;
            lc.getLayer(DEFAULT_LAYER).isVisible = true;
        } else {
            lc.getLayer(DEFAULT_LAYER).isVisible = false;
            lc.getLayer(BTN_INACTIVE).isVisible = false;
        }

        btnNext.getComponent(ButtonComponent.class).clearListeners();
        btnNext.getComponent(ButtonComponent.class)
                .addListener(new ImageButtonListener(btnNext) {
                    @Override
                    public void touchUp() {
                        if (isNextBtnActive(vc))
                            super.touchUp();
                    }

                    @Override
                    public void touchDown() {
                        if (isNextBtnActive(vc))
                            super.touchDown();
                    }

                    @Override
                    public void clicked() {
                        if (isNextBtnActive(vc) && animFinished()) {
                            ActionComponent ac = new ActionComponent();
                            Actions.checkInit();
                            ac.dataArray.add(Actions.moveTo(HIDE_INFO_TAG_LEFT, infoTag.getComponent(TransformComponent.class).y, HIDE_INFO_TAG_DURATION));
                            infoTag.add(ac);
                            movedTo = HIDE_INFO_TAG_LEFT;
                            if(lblNotEnough.getComponent(ActionComponent.class) != null) {
                                lblNotEnough.getComponent(ActionComponent.class).reset();
                                lblNotEnough.getComponent(TintComponent.class).color.a = 0;
                            }

                            if (vc.currencyType.equals(SOFT) && (gameStage.shopScript.allSoftItems.indexOf(vc) + 1) % 8 == 0) {
                                gameStage.shopScript.scrollBagsOnePageRight();
                            }
                            SoundMgr.getSoundMgr().play(SoundMgr.PAPER_FLIP_SHOP);
                        }
                    }
                });
    }

    private boolean isNextBtnActive(ShopItem vc) {
        return (gameStage.shopScript.allHCItems.indexOf(vc) < gameStage.shopScript.allHCItems.size() - 1 && vc.currencyType.equals(HARD))
                || (vc.currencyType.equals(SOFT) && gameStage.shopScript.allSoftItems.indexOf(vc) < gameStage.shopScript.allSoftItems.size() - 1);
    }

    private void showPreviewAfterAniNext() {
        if (vc.currencyType.equals(SOFT)) {
            nextItem(gameStage.shopScript.allSoftItems);
        } else {
            nextItem(gameStage.shopScript.allHCItems);
        }
    }

    private void nextItem(List<ShopItem> list) {
        int nextIndex = list.indexOf(vc) + 1;
        if (nextIndex < list.size()) {
            setShouldDeleteIconE();
            canPlayDescAni = true;
            showPreview(list.get(nextIndex), false, false);
        }
    }

    public void checkAndClose() {
        SoundMgr.getSoundMgr().play(SoundMgr.WIND_POP_UP_CLOSE);

        if (infoTag != null) {
            float currentYpos = infoTag.getComponent(TransformComponent.class).y;
            if (currentYpos <= INFO_TAG_Y + 30 || currentYpos >= 800) {
                if (isPreviewOn.get()) {
                    ActionComponent ac = new ActionComponent();
                    Actions.checkInit();
                    ac.dataArray.add(Actions.moveBy(0, 1800, 1f, Interpolation.exp10));
                    infoTag.add(ac);
                    btnClose.add(ac);

                    ActionComponent acButtonz = new ActionComponent();
                    Actions.checkInit();
                    acButtonz.dataArray.add(Actions.moveTo(BTNZ_X, -200, 0.4f, Interpolation.exp10));
                    buttonz.add(acButtonz);

                    ActionComponent ac2 = new ActionComponent();
                    ac2.dataArray.add(Actions.fadeOut(0.8f, Interpolation.exp5));
                    shadowE.add(ac2);
                }
            }
        }
    }

    public void removeIconE() {
        if (iconE != null) {
            if (shouldDeleteIconE) {
                gameStage.sceneLoader.getEngine().removeEntity(iconE);
                iconE = null;
            } else {
                iconE.getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
            }
        }
    }

    public void setShouldDeleteIconE() {
        shouldDeleteIconE = vc.currencyType.equals(SOFT);
    }

    public void updatePreview() {
//        if(lbl_desc != null) {
//            System.out.println("lbl_desc_21 x: " + lbl_desc_21.getComponent(TransformComponent.class).x);
//            System.out.println("lbl_desc_21 y: " + lbl_desc_21.getComponent(TransformComponent.class).y);
//            System.out.println("lbl_desc_21 color a: " + lbl_desc_21.getComponent(TintComponent.class).color.a);
//            System.out.println("lbl_desc_21 z: " + lbl_desc_21.getComponent(ZIndexComponent.class).getZIndex());
//        }
        if (movedTo != 0) {
            if (infoTag.getComponent(TransformComponent.class).x == movedTo) {
                if (movedTo == HIDE_INFO_TAG_RIGHT) {
                    infoTag.getComponent(TransformComponent.class).x = -500;
                    showPreviewAfterAniPrev();
                } else {
                    infoTag.getComponent(TransformComponent.class).x = 1300;
                    showPreviewAfterAniNext();
                }
                movedTo = 0;
            }
        }

        if (iconE != null) {
            iconE.getComponent(TransformComponent.class).x = infoTag.getComponent(TransformComponent.class).x + ICON_X_RELATIVE;
            iconE.getComponent(TransformComponent.class).y = infoTag.getComponent(TransformComponent.class).y + ICON_Y_RELATIVE;
            iconE.getComponent(TransformComponent.class).scaleX = ICON_SCALE_X;
            iconE.getComponent(TransformComponent.class).scaleY = ICON_SCALE_Y;
        }
        if (isPreviewOn.get() && infoTag != null && infoTag.getComponent(TransformComponent.class).y >= INFO_TAG_HIDE_Y) {
            isPreviewOn.set(false);
            setShouldDeleteIconE();
            removeIconE();
        }
    }

    private boolean animFinished() {
        return infoTag.getComponent(TransformComponent.class).y <= INFO_TAG_Y + 30
                && infoTag.getComponent(TransformComponent.class).x == INFO_TAG_X;
    }
    String getTextWithoutSpaces(String s){
        if (s.contains(SPACE_SIGN)) {
            return s.replace(SPACE_SIGN, " ");
        }
        return s;
    }

    private void setDesciptionLabels(){
        if (vc.description != null) {
//            lbl_desc.getComponent(LabelComponent.class).text.replace(0, lbl_desc.getComponent(LabelComponent.class).text.length, vc.description);
            if (vc.description.contains(SPACE_SIGN)) {
                vc.description.replace(SPACE_SIGN, " ");
            }

            if (vc.description.contains(NEW_LINE_SIGN)) {
                String[] lines = vc.description.split(NEW_LINE_SIGN);
                for(String s : lines){
                    if (s.contains(SPACE_SIGN)) {
                        s = s.replace(SPACE_SIGN, " ");
                    }
                }
                if (lines.length == 2) {
                    lbl_desc.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_21.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, lines[0]);
                    lbl_desc_22.getComponent(LabelComponent.class).text.replace(0, lbl_desc_22.getComponent(LabelComponent.class).text.length, lines[1]);
                    lbl_desc_31.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_32.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_33.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_41.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_42.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_43.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_44.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                } else if (lines.length == 3) {
                    lbl_desc.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_21.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_22.getComponent(LabelComponent.class).text.replace(0, lbl_desc_22.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_31.getComponent(LabelComponent.class).text.replace(0, lbl_desc_31.getComponent(LabelComponent.class).text.length, lines[0]);
                    lbl_desc_32.getComponent(LabelComponent.class).text.replace(0, lbl_desc_32.getComponent(LabelComponent.class).text.length, lines[1]);
                    lbl_desc_33.getComponent(LabelComponent.class).text.replace(0, lbl_desc_33.getComponent(LabelComponent.class).text.length, lines[2]);
                    lbl_desc_41.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_42.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_43.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_44.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                } else if (lines.length == 4) {
                    lbl_desc.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_21.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_22.getComponent(LabelComponent.class).text.replace(0, lbl_desc_22.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_31.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_32.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_33.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_41.getComponent(LabelComponent.class).text.replace(0, lbl_desc_41.getComponent(LabelComponent.class).text.length, lines[0]);
                    lbl_desc_42.getComponent(LabelComponent.class).text.replace(0, lbl_desc_42.getComponent(LabelComponent.class).text.length, lines[1]);
                    lbl_desc_43.getComponent(LabelComponent.class).text.replace(0, lbl_desc_43.getComponent(LabelComponent.class).text.length, lines[2]);
                    lbl_desc_44.getComponent(LabelComponent.class).text.replace(0, lbl_desc_44.getComponent(LabelComponent.class).text.length, lines[3]);
                }
            }
            else {
                lbl_desc.getComponent(LabelComponent.class).text.replace(0, lbl_desc.getComponent(LabelComponent.class).text.length, getTextWithoutSpaces(vc.description));
                lbl_desc_21.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                lbl_desc_22.getComponent(LabelComponent.class).text.replace(0, lbl_desc_22.getComponent(LabelComponent.class).text.length, "Error");
                lbl_desc_31.getComponent(LabelComponent.class).text.replace(0, lbl_desc_31.getComponent(LabelComponent.class).text.length, "Error");
                lbl_desc_32.getComponent(LabelComponent.class).text.replace(0, lbl_desc_32.getComponent(LabelComponent.class).text.length, "Error");
                lbl_desc_33.getComponent(LabelComponent.class).text.replace(0, lbl_desc_33.getComponent(LabelComponent.class).text.length, "Error");
                lbl_desc_41.getComponent(LabelComponent.class).text.replace(0, lbl_desc_41.getComponent(LabelComponent.class).text.length, "Error");
                lbl_desc_42.getComponent(LabelComponent.class).text.replace(0, lbl_desc_42.getComponent(LabelComponent.class).text.length, "Error");
                lbl_desc_43.getComponent(LabelComponent.class).text.replace(0, lbl_desc_43.getComponent(LabelComponent.class).text.length, "Error");
                lbl_desc_44.getComponent(LabelComponent.class).text.replace(0, lbl_desc_44.getComponent(LabelComponent.class).text.length, "Error");
            }
        }
        if (vc.collection != null) {
//            if (vc.collection.contains(SPACE_SIGN)) {
//                lbl_desc.getComponent(LabelComponent.class).text.replace(SPACE_SIGN, " ");
//            }
            if (vc.collection.contains(SPACE_SIGN)) {
                vc.collection.replace(SPACE_SIGN, " ");
            }

            if (vc.collection.contains(NEW_LINE_SIGN)) {
                String[] lines = vc.collection.split(NEW_LINE_SIGN);
                for(String s : lines){
                    if (s.contains(SPACE_SIGN)) {
                        s = s.replace(SPACE_SIGN, " ");
                    }
                }
                if (lines.length == 2) {
                    lbl_desc.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_21.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, lines[0]);
                    lbl_desc_22.getComponent(LabelComponent.class).text.replace(0, lbl_desc_22.getComponent(LabelComponent.class).text.length, lines[1]);
                    lbl_desc_31.getComponent(LabelComponent.class).text.replace(0, lbl_desc_31.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_32.getComponent(LabelComponent.class).text.replace(0, lbl_desc_32.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_33.getComponent(LabelComponent.class).text.replace(0, lbl_desc_33.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_41.getComponent(LabelComponent.class).text.replace(0, lbl_desc_41.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_42.getComponent(LabelComponent.class).text.replace(0, lbl_desc_42.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_43.getComponent(LabelComponent.class).text.replace(0, lbl_desc_43.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_44.getComponent(LabelComponent.class).text.replace(0, lbl_desc_44.getComponent(LabelComponent.class).text.length, "Error");
                } else if (lines.length == 3) {
                    lbl_desc.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_21.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_22.getComponent(LabelComponent.class).text.replace(0, lbl_desc_22.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_31.getComponent(LabelComponent.class).text.replace(0, lbl_desc_31.getComponent(LabelComponent.class).text.length, lines[0]);
                    lbl_desc_32.getComponent(LabelComponent.class).text.replace(0, lbl_desc_32.getComponent(LabelComponent.class).text.length, lines[1]);
                    lbl_desc_33.getComponent(LabelComponent.class).text.replace(0, lbl_desc_33.getComponent(LabelComponent.class).text.length, lines[2]);
                    lbl_desc_41.getComponent(LabelComponent.class).text.replace(0, lbl_desc_41.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_42.getComponent(LabelComponent.class).text.replace(0, lbl_desc_42.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_43.getComponent(LabelComponent.class).text.replace(0, lbl_desc_43.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_44.getComponent(LabelComponent.class).text.replace(0, lbl_desc_44.getComponent(LabelComponent.class).text.length, "Error");
                } else if (lines.length == 4) {
                    lbl_desc.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_21.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_22.getComponent(LabelComponent.class).text.replace(0, lbl_desc_22.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_31.getComponent(LabelComponent.class).text.replace(0, lbl_desc_31.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_32.getComponent(LabelComponent.class).text.replace(0, lbl_desc_32.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_33.getComponent(LabelComponent.class).text.replace(0, lbl_desc_33.getComponent(LabelComponent.class).text.length, "Error");
                    lbl_desc_41.getComponent(LabelComponent.class).text.replace(0, lbl_desc_41.getComponent(LabelComponent.class).text.length, lines[0]);
                    lbl_desc_42.getComponent(LabelComponent.class).text.replace(0, lbl_desc_42.getComponent(LabelComponent.class).text.length, lines[1]);
                    lbl_desc_43.getComponent(LabelComponent.class).text.replace(0, lbl_desc_43.getComponent(LabelComponent.class).text.length, lines[2]);
                    lbl_desc_44.getComponent(LabelComponent.class).text.replace(0, lbl_desc_44.getComponent(LabelComponent.class).text.length, lines[3]);
                }
            }
            else if(!vc.collection.contains(NEW_LINE_SIGN)) {
                lbl_desc.getComponent(LabelComponent.class).text.replace(0, lbl_desc.getComponent(LabelComponent.class).text.length, getTextWithoutSpaces(vc.collection));
                lbl_desc_21.getComponent(LabelComponent.class).text.replace(0, lbl_desc_21.getComponent(LabelComponent.class).text.length, "Error");
                lbl_desc_22.getComponent(LabelComponent.class).text.replace(0, lbl_desc_22.getComponent(LabelComponent.class).text.length, "Error");
                lbl_desc_31.getComponent(LabelComponent.class).text.replace(0, lbl_desc_31.getComponent(LabelComponent.class).text.length, "Error");
                lbl_desc_32.getComponent(LabelComponent.class).text.replace(0, lbl_desc_32.getComponent(LabelComponent.class).text.length, "Error");
                lbl_desc_33.getComponent(LabelComponent.class).text.replace(0, lbl_desc_33.getComponent(LabelComponent.class).text.length, "Error");
                lbl_desc_41.getComponent(LabelComponent.class).text.replace(0, lbl_desc_41.getComponent(LabelComponent.class).text.length, "Error");
                lbl_desc_42.getComponent(LabelComponent.class).text.replace(0, lbl_desc_42.getComponent(LabelComponent.class).text.length, "Error");
                lbl_desc_43.getComponent(LabelComponent.class).text.replace(0, lbl_desc_43.getComponent(LabelComponent.class).text.length, "Error");
                lbl_desc_44.getComponent(LabelComponent.class).text.replace(0, lbl_desc_44.getComponent(LabelComponent.class).text.length, "Error");
            }
        }
    }
}