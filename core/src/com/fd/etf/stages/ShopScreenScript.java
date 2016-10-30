package com.fd.etf.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.fd.etf.entity.componets.FlowerPublicComponent;
import com.fd.etf.entity.componets.PetComponent;
import com.fd.etf.entity.componets.ShopItem;
import com.fd.etf.entity.componets.Upgrade;
import com.fd.etf.entity.componets.listeners.ImageButtonListener;
import com.fd.etf.entity.componets.listeners.ShopPoverUpTabListener;
import com.fd.etf.entity.componets.listeners.ShopClothingTabListener;
import com.fd.etf.stages.ui.Preview;
import com.fd.etf.system.ParticleLifespanSystem;
import com.fd.etf.utils.GlobalConstants;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.*;

import static com.fd.etf.entity.componets.ShopItem.HARD;
import static com.fd.etf.utils.GlobalConstants.*;


public class ShopScreenScript implements IScript {

    public static final Map<String, Entity> itemIcons = new LinkedHashMap<>();
    public static final int SENSITIVITY = 7;
    public static final String SCORE_LBL = "total_coins";
    public static final String SCORE_LBL_SH = "total_coins_sh";
    public static final String TOUCH_ZON_AND_BUTTONS = "touch_zon_and_buttons";
    public static final String TOUCH_ZONE_SCROLL = "touchZone_scroll";
    public static final String BTN_IMG_SHOP_ICON_LIB = "btn_img_shop_icon_lib";
    public static final String ITEM_UNKNOWN_N = "item_unknown_n";
    public static final String BTN_BACK = "btn_back";
    public static final String HC_SHOP_SEC = "HC_shop_sec";
    public static final String TAB_BTN_SHOP = "tab_btn_shop";
    public static final String TAB_BTN_UPG = "tab_btn_upg";
    public static final String CURTAIN_SHOP = "curtain_shop";
    public static final int INIT_HC_ITEMS_X = 146;

    public static final int STOP_VELOCITY_DIV = 20;

    public static final int FIRST_BAG_X = 1950;
    public static final int FIRST_BAG_Y = 440;
    public static final int X_ICON_ON_BAG = 55;
    public static final int Y_ICON_ON_BAG = 55;
    private static final int SPACE_BETWEEN_BAGS_X = 20;
    private static final int SPACE_BETWEEN_BAGS_Y = 0;

    private static final int PAGE_SIZE = 1050;
    public static final int CAN_MOVE_LEFT_BAG_X = 990;
    public static final int CAN_MOVE_RIGHT_BAG_X = 10;

    private static final String TITLE = "title";
    private static final String BTN_SCROLL_LEFT = "btn_scroll_left";
    private static final int SCCREEN_WIDTH = 1227;
    private static final String BTN_SCROLL_RIGHT = "btn_scroll_right";
    private static final String DOTZ_LIB = "dotz_lib";

    // Dima's fun house
    private static Entity curtain_shop;
    boolean startTransitionIn;
    boolean startTransitionOut;

    public static Entity scoreLbl;
    public static Entity scoreLblsh;
    public static boolean isPreviewOn;
    public static boolean canOpenPreview = true;
    public static int bagsZindex;

    public static List<ShopItem> allShopItems = new ArrayList<>();
    public static List<ShopItem> allHCItems = new ArrayList<>();

    public Entity touchZoneNButton;
    public Entity touchZone;
    public LabelComponent lc;
    public LabelComponent lcsh;
    public Vector2 tempGdx = new Vector2();
    public boolean isGdxWritten;
    public List<Entity> bags = new ArrayList<>();
    public ButtonComponent touchZoneBtn;
    public int bagPosIdX;
    public Preview preview;
    float stopVelocity;

    private ItemWrapper shopItem;
    public Entity hcSectionE;
    public Entity btnClothing;
    public Entity btnPowerUp;
    private int bagPosIdY;
    private int bagPageId;

    private List<Entity> pageDots = new ArrayList<>();
    private int pageIndex = 0;

    public ShopScreenScript() {
//        this.stage = stage;
        getAllAllVanities();
    }

    public static void reloadScoreLabel(FlowerPublicComponent fcc) {
        scoreLbl.getComponent(LabelComponent.class).text.replace(0, scoreLbl.getComponent(LabelComponent.class).text.capacity(),
                String.valueOf(fcc.totalScore));
    }

    @Override
    public void init(Entity item) {
        GameStage.sceneLoader.addComponentsByTagName(BUTTON_TAG, ButtonComponent.class);
        shopItem = new ItemWrapper(item);
        preview = new Preview();
        GameStage.sceneLoader.getEngine().addSystem(new ParticleLifespanSystem());

        // Dima's fun house
        curtain_shop = shopItem.getChild(CURTAIN_SHOP).getEntity();
        curtain_shop.getComponent(TintComponent.class).color.a = 1f;
        startTransitionIn = true;
        startTransitionOut = false;


        addBackButtonPlease();
        scoreLbl = shopItem.getChild(SCORE_LBL).getEntity();
        scoreLblsh = shopItem.getChild(SCORE_LBL_SH).getEntity();
        lc = scoreLbl.getComponent(LabelComponent.class);
        lcsh = scoreLblsh.getComponent(LabelComponent.class);
        touchZoneNButton = shopItem.getChild(TOUCH_ZON_AND_BUTTONS).getEntity();
        touchZone = shopItem.getChild(TOUCH_ZON_AND_BUTTONS).getChild(TOUCH_ZONE_SCROLL).getEntity();
        touchZoneBtn = touchZone.getComponent(ButtonComponent.class);
        createIconsForAllShopItems();
        createIconsForAllHCItems();
        initTabBtns();
        initScrollLeftBtn();
        initScrollRightBtn();
        bagPosIdX = 0;
        bagPosIdY = 0;
        bagPageId = 0;
        pageIndex = 0;

        isPreviewOn = false;
    }

    public void initTabBtns() {

        btnClothing = shopItem.getChild(TAB_BTN_SHOP).getEntity();
        btnClothing.getComponent(ButtonComponent.class).enable = false;
        btnClothing.getComponent(LayerMapComponent.class).getLayer(BTN_NORMAL).isVisible = true;
        btnClothing.getComponent(LayerMapComponent.class).getLayer(BTN_PRESSED).isVisible = false;
        btnClothing.getComponent(ButtonComponent.class).addListener(new ShopPoverUpTabListener(this));

        btnPowerUp = shopItem.getChild(TAB_BTN_UPG).getEntity();
        btnPowerUp.getComponent(ButtonComponent.class).enable = true;
        btnPowerUp.getComponent(LayerMapComponent.class).getLayer(BTN_NORMAL).isVisible = false;
        btnPowerUp.getComponent(LayerMapComponent.class).getLayer(BTN_DEFAULT).isVisible = false;
        btnPowerUp.getComponent(LayerMapComponent.class).getLayer(BTN_PRESSED).isVisible = true;
        btnPowerUp.getComponent(ButtonComponent.class).addListener(new ShopClothingTabListener(this));
    }

    private void getAllAllVanities() {
        if (allShopItems.isEmpty()) {
            for (PetComponent pet : GameStage.gameScript.fpc.pets) {
                if (pet.isHardCurr) {
                    allHCItems.add(pet);
                }
            }
            allHCItems.addAll(getAllUpgrades());
            allShopItems.addAll(GameStage.gameScript.fpc.vanities);
        }

        Collections.sort(allShopItems, new Comparator<ShopItem>() {
            @Override
            public int compare(ShopItem o1, ShopItem o2) {
                if (o1.currencyType.equals(o2.currencyType)) {
                    return compareByCost(o1, o2);
                } else {
                    return o1.currencyType.equals(HARD) ? 1 : -1;
                }
            }

            public int compareByCost(ShopItem o1, ShopItem o2) {
                if (o1.cost == o2.cost) {
                    return 0;
                } else {
                    return o1.cost < o2.cost ? 1 : -1;
                }
            }
        });
        Collections.reverse(allShopItems);
    }

    private void createIconsForAllHCItems() {
        hcSectionE = shopItem.getChild(HC_SHOP_SEC).getEntity();
        NodeComponent nc = hcSectionE.getComponent(NodeComponent.class);

        int i = 0;
        for (Entity e : nc.children) {
            final ShopItem hc = allHCItems.get(i);

            // set item title
            Entity child = e.getComponent(NodeComponent.class).getChild(TITLE);
            child.getComponent(LabelComponent.class).text.replace(
                    0, child.getComponent(LabelComponent.class).text.length,
                    hc.name
            );

            e.getComponent(ButtonComponent.class).addListener(
                    new ImageButtonListener(e) {
                        @Override
                        public void clicked() {
                            if (!isPreviewOn && canOpenPreview) {
                                preview.showPreview(hc, true, false);
                            }
                        }
                    });
            i++;
        }
    }

    private void createIconsForAllShopItems() {
        TransformComponent previousTc = null;
        for (final ShopItem vc : allShopItems) {
            CompositeItemVO tempC = GameStage.sceneLoader.loadVoFromLibrary(BTN_IMG_SHOP_ICON_LIB).clone();
            final Entity bagEntity = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempC);
            GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), bagEntity, tempC.composite);
            GameStage.sceneLoader.getEngine().addEntity(bagEntity);

            Entity itemIcon = initSoftCurrencyShopItem(vc);
            itemIcon.getComponent(ZIndexComponent.class).setZIndex(bagsZindex + 1);

            final TransformComponent tc = getNextBagPos(previousTc, bagEntity.getComponent(DimensionsComponent.class));
            bagEntity.add(tc);
            bagsZindex = bagEntity.getComponent(ZIndexComponent.class).getZIndex();
            previousTc = tc;

            shopItem.getChild(BTN_IMG_SHOP_ICON_LIB).addChild(itemIcon);
            TransformComponent tcb = itemIcon.getComponent(TransformComponent.class);
            tcb.x = tc.x + X_ICON_ON_BAG;
            tcb.y = tc.y + Y_ICON_ON_BAG;
            bagEntity.getComponent(NodeComponent.class).getChild(TITLE).getComponent(LabelComponent.class).text.replace(
                    0, bagEntity.getComponent(NodeComponent.class).getChild(TITLE).getComponent(LabelComponent.class).text.length,
                    vc.name
            );

            bags.add(bagEntity);
            itemIcons.put(vc.shopIcon, itemIcon);

            bagEntity.add(new ButtonComponent());
            bagEntity.getComponent(ButtonComponent.class).addListener(
                    new ImageButtonListener(bagEntity) {
                        @Override
                        public void clicked() {
                            if (!isPreviewOn && canOpenPreview) {
                                preview.showPreview(vc, true, false);
                            }
                        }
                    });
        }
    }

    public List<Upgrade> getAllUpgrades() {
        List<Upgrade> upgrades = Upgrade.getAllUpgrades();
        if (!GameStage.gameScript.fpc.upgrades.isEmpty()) {
            for (Upgrade u : GameStage.gameScript.fpc.upgrades.values()) {
                for (Upgrade u2 : upgrades) {
                    if (u.upgradeType.equals(u2.upgradeType)) {
                        u2.tryPeriod = u.tryPeriod;
                        u2.tryPeriodDuration = u.tryPeriodDuration;
                        u2.tryPeriodStart = u.tryPeriodStart;
                        u2.tryPeriodTimer = u.tryPeriodTimer;
                        u2.enabled = u.enabled;
                        u2.bought = u.bought;
                    }
                }
            }
        }
        return upgrades;
    }

    private Entity initSoftCurrencyShopItem(ShopItem vc) {
        //TODO: Should not be deleted!
//        if (!vc.bought) {
        return getIconFromLib(ITEM_UNKNOWN_N);
//        } else {
//            return getIconFromLib(vc.shopIcon);
//        }
    }

    private Entity getIconFromLib(String name) {
        CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary(name).clone();
        Entity itemIcon = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
        GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), itemIcon, tempItemC.composite);
        GameStage.sceneLoader.getEngine().addEntity(itemIcon);
        return itemIcon;
    }

    private void addBackButtonPlease() {
        Entity btnBack = shopItem.getChild(BTN_BACK).getEntity();
        btnBack.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(btnBack) {
                    @Override
                    public void clicked() {
                        if (!isPreviewOn) {
                            startTransitionOut = true;
                        }
                    }
                });
    }

    @Override
    public void act(float delta) {
        if (!isPreviewOn) {
            transitionIn();
            transitionOut();

            List<Entity> itemIcons2 = new ArrayList<>(itemIcons.values());
            if (touchZoneBtn.isTouched) {
                startScrolling();
                canOpenPreview = tempGdx.x == Gdx.input.getX();
                scrollLeft(delta, itemIcons2);
                scrollRight(delta, itemIcons2);
            } else {
                stopScrolling(delta, itemIcons2);
            }
        }
        preview.checkAndClose();
        lc.text.replace(0, lc.text.length(), String.valueOf(GameStage.gameScript.fpc.totalScore));
        lcsh.text.replace(0, lcsh.text.length(), String.valueOf(GameStage.gameScript.fpc.totalScore));
    }

    private void transitionOut() {
        if (startTransitionOut) {
            curtain_shop.getComponent(TintComponent.class).color.a += ALPHA_TRANSITION_STEP;
            if (curtain_shop.getComponent(TintComponent.class).color.a >= 1) {
                startTransitionOut = false;
                startTransitionIn = true;
                GameStage.initMenu();
            }
        }
    }

    private void transitionIn() {
        if (startTransitionIn) {
            curtain_shop.getComponent(TintComponent.class).color.a -= ALPHA_TRANSITION_STEP;
            if (curtain_shop.getComponent(TintComponent.class).color.a <= 0) {
                startTransitionIn = false;
            }
        }
    }

    private void stopScrolling(float delta, List<Entity> itemIcons2) {
        isGdxWritten = false;
        if (stopVelocity != 0 && (canMoveBagsLeft() && canMoveBagsRight())) {
            int i = 0;
            while (i < bags.size()) {
                bags.get(i).getComponent(TransformComponent.class).x += stopVelocity * delta * GlobalConstants.FPS;
                itemIcons2.get(i).getComponent(TransformComponent.class).x = bags.get(i)
                        .getComponent(TransformComponent.class).x + X_ICON_ON_BAG;
                i++;
            }
            stopVelocity -= stopVelocity / STOP_VELOCITY_DIV;
        }
    }

    private void scrollRight(float delta, List<Entity> itemIcons2) {
        if (tempGdx.x < Gdx.input.getX() && canMoveBagsRight()) {
            int i = 0;
            while (i < bags.size()) {
                bags.get(i).getComponent(TransformComponent.class).x += ((Gdx.input.getX() - tempGdx.x) / SENSITIVITY) * delta * GlobalConstants.FPS;
                itemIcons2.get(i).getComponent(TransformComponent.class).x = bags.get(i).getComponent(TransformComponent.class).x + X_ICON_ON_BAG;
                i++;
            }
            stopVelocity = (Gdx.input.getX() - tempGdx.x) / SENSITIVITY;
            tempGdx.x += ((Gdx.input.getX() - tempGdx.x) / SENSITIVITY) * delta * GlobalConstants.FPS;
            ButtonComponent.skipDefaultLayersChange = true;
        }
    }

    private void scrollLeft(float delta, List<Entity> itemIcons2) {
        if (tempGdx.x > Gdx.input.getX() && canMoveBagsLeft()) {
            int i = 0;
            while (i < bags.size()) {
                bags.get(i).getComponent(TransformComponent.class).x -= ((tempGdx.x - Gdx.input.getX()) / SENSITIVITY) * delta * GlobalConstants.FPS;
                itemIcons2.get(i).getComponent(TransformComponent.class).x = bags.get(i).getComponent(TransformComponent.class).x + X_ICON_ON_BAG;
                i++;
            }
            stopVelocity = (Gdx.input.getX() - tempGdx.x) / SENSITIVITY;
            tempGdx.x -= ((tempGdx.x - Gdx.input.getX()) / SENSITIVITY) * delta * GlobalConstants.FPS;

            ButtonComponent.skipDefaultLayersChange = true;
        }
    }

    private void startScrolling() {
        if (!isGdxWritten) {
            tempGdx.x = Gdx.input.getX();
            isGdxWritten = true;

            ButtonComponent.skipDefaultLayersChange = false;
        }
    }

    public boolean canMoveBagsLeft() {
        return bags.get(bags.size() - 1).getComponent(TransformComponent.class).x >= CAN_MOVE_LEFT_BAG_X;
    }

    public boolean canMoveBagsRight() {
        return bags.get(0).getComponent(TransformComponent.class).x <= CAN_MOVE_RIGHT_BAG_X;
    }

    public TransformComponent getNextBagPos(TransformComponent previous, DimensionsComponent previousDc) {
        TransformComponent tc = new TransformComponent();

        if (previous == null) {
            tc.x = FIRST_BAG_X;
            tc.y = FIRST_BAG_Y;
            createTheDot(bagPageId);
        } else {

            if (bagPosIdX % 4 == 0) {
                tc.x = FIRST_BAG_X + bagPageId * (PAGE_SIZE + 180);
            } else {
                tc.x = previous.x + previousDc.width + SPACE_BETWEEN_BAGS_X;
            }

            if (bagPosIdY < 4) {
                tc.y = FIRST_BAG_Y;
            } else if (bagPosIdY >= 4 && bagPosIdY < 8) {
                tc.y = FIRST_BAG_Y - previousDc.height + SPACE_BETWEEN_BAGS_Y;
            }
        }
        if (bagPosIdY == 7) {
            bagPosIdY = 0;
            bagPosIdX++;
            bagPageId++;
            createTheDot(bagPageId);
        } else {
            bagPosIdY++;
            bagPosIdX++;
        }
        return tc;
    }

    private void createTheDot(int pageId){
        CompositeItemVO tempC = GameStage.sceneLoader.loadVoFromLibrary(DOTZ_LIB);
        final Entity dotEntity = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempC);
        GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), dotEntity, tempC.composite);
        GameStage.sceneLoader.getEngine().addEntity(dotEntity);

        touchZoneNButton.getComponent(NodeComponent.class).addChild(dotEntity);
        dotEntity.getComponent(MainItemComponent.class).uniqueId += pageId;
        pageDots.add(dotEntity);
        dotEntity.getComponent(TransformComponent.class).x = 550 + pageId*30;
        dotEntity.getComponent(TransformComponent.class).y = 10;
    }

    public void setDotActive(int pagIndex){

        for (int i = 0; i < pageDots.size(); i++) {
            if (i != pagIndex) {
                pageDots.get(i).getComponent(LayerMapComponent.class).getLayer("Default").isVisible = true;
                pageDots.get(i).getComponent(LayerMapComponent.class).getLayer("Active").isVisible = false;
            } else {
                pageDots.get(i).getComponent(LayerMapComponent.class).getLayer("Default").isVisible = false;
                pageDots.get(i).getComponent(LayerMapComponent.class).getLayer("Active").isVisible = true;
            }
        }
    }

    private void initScrollLeftBtn() {
        Entity scrollLeftBtn = touchZoneNButton.getComponent(NodeComponent.class).getChild(BTN_SCROLL_LEFT);
        scrollLeftBtn.add(new ButtonComponent());
        scrollLeftBtn.getComponent(ButtonComponent.class).addListener(new ImageButtonListener(scrollLeftBtn) {
            @Override
            public void clicked() {
                if (bags.get(0).getComponent(TransformComponent.class).x < 100) {
                    for (Entity bag : bags) {
                        ActionComponent a = new ActionComponent();
                        Actions.checkInit();

                        a.dataArray.add(
                                Actions.moveTo(bag.getComponent(TransformComponent.class).x + SCCREEN_WIDTH,
                                        bag.getComponent(TransformComponent.class).y, 0.7f, Interpolation.exp10)
                        );
                        bag.add(a);
                    }

                    for (Entity icon : ShopScreenScript.itemIcons.values()) {
                        ActionComponent a = new ActionComponent();
                        Actions.checkInit();

                        a.dataArray.add(
                                Actions.moveTo(icon.getComponent(TransformComponent.class).x + SCCREEN_WIDTH,
                                        icon.getComponent(TransformComponent.class).y, 0.7f, Interpolation.exp10)
                        );

                        icon.add(a);
                    }
                    pageIndex--;
                    setDotActive(pageIndex);
                }
            }
        });
    }

    private void initScrollRightBtn() {
        Entity scrollLeftBtn = touchZoneNButton.getComponent(NodeComponent.class).getChild(BTN_SCROLL_RIGHT);
        scrollLeftBtn.add(new ButtonComponent());
        scrollLeftBtn.getComponent(ButtonComponent.class).addListener(new ImageButtonListener(scrollLeftBtn) {
            @Override
            public void clicked() {
                if (bags.get(bags.size() - 1).getComponent(TransformComponent.class).x > SCCREEN_WIDTH) {
                    for (Entity bag : bags) {
                        ActionComponent a = new ActionComponent();
                        Actions.checkInit();

                        a.dataArray.add(
                                Actions.moveTo(bag.getComponent(TransformComponent.class).x - SCCREEN_WIDTH,
                                        bag.getComponent(TransformComponent.class).y, 0.7f, Interpolation.exp10)
                        );
                        bag.add(a);
                    }

                    for (Entity icon : ShopScreenScript.itemIcons.values()) {
                        ActionComponent a = new ActionComponent();
                        Actions.checkInit();

                        a.dataArray.add(
                                Actions.moveTo(icon.getComponent(TransformComponent.class).x - SCCREEN_WIDTH,
                                        icon.getComponent(TransformComponent.class).y, 0.7f, Interpolation.exp10)
                        );

                        icon.add(a);
                    }
                    pageIndex++;
                    setDotActive(pageIndex);
                }
            }
        });
    }

    @Override
    public void dispose() {
        scoreLbl = null;
        touchZone = null;
        lc = null;
        tempGdx = new Vector2();
        bags = null;
        touchZoneBtn = null;
        preview = null;
        shopItem = null;
        System.gc();
    }
}