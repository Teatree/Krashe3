package com.fd.etf.stages;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.fd.etf.entity.componets.FlowerPublicComponent;
import com.fd.etf.entity.componets.PetComponent;
import com.fd.etf.entity.componets.ShopItem;
import com.fd.etf.entity.componets.Upgrade;
import com.fd.etf.entity.componets.listeners.ImageButtonListener;
import com.fd.etf.entity.componets.listeners.ShopClothingTabListener;
import com.fd.etf.entity.componets.listeners.ShopPoverUpTabListener;
import com.fd.etf.stages.ui.Preview;
import com.fd.etf.system.ParticleLifespanSystem;
import com.fd.etf.utils.DebugSystem;
import com.uwsoft.editor.renderer.components.*;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.systems.action.Actions;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.fd.etf.entity.componets.ShopItem.HARD;
import static com.fd.etf.utils.GlobalConstants.*;


public class ShopScreenScript implements IScript {

    public static Map<String, Entity> itemIcons = new LinkedHashMap<>();
    public static final String SCORE_LBL = "total_coins";
    public static final String SCORE_LBL_SH = "total_coins_sh";
    public static final String TOUCH_ZON_AND_BUTTONS = "touch_zon_and_buttons";
    public static final String BTN_IMG_SHOP_ICON_LIB = "btn_img_shop_icon_lib";
    public static final String ITEM_UNKNOWN_N = "item_unknown_n";
    public static final String BTN_BACK = "btn_back";
    public static final String HC_SHOP_SEC = "HC_shop_sec";
    public static final String TAB_BTN_SHOP = "tab_btn_shop";
    public static final String TAB_BTN_UPG = "tab_btn_upg";
    public static final String CURTAIN_SHOP = "curtain_shop";

    public static final int INIT_HC_ITEMS_X = 146;

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
    private static final String TITLE_2 = "title_2";
    private static final String NEW_LINE_SIGN = "~";  //this symbol is used in the name of the item to identify the place where it new line will start
    private static final String BTN_SCROLL_LEFT = "btn_scroll_left";
    private static final int SCCREEN_WIDTH = 1227;
    private static final String BTN_SCROLL_RIGHT = "btn_scroll_right";
    private static final String BTN_SCROLL_INACTIVE = "Gray";
    private static final String DOTZ = "dotz_";
    private static final String DOT_TAG = "dot";

    // Dima's fun house
    private static Entity curtain_shop;
    public static boolean shouldReload = false;
    boolean startTransitionIn;
    boolean startTransitionOut;

    public static Entity scoreLbl;
    public static Entity scoreLblsh;
    public static AtomicBoolean isPreviewOn = new AtomicBoolean(false);
    public static int bagsZindex;

    public static List<ShopItem> allSoftItems = new ArrayList<>();
    public static List<ShopItem> allHCItems = new ArrayList<>();

    public Entity touchZoneNButton;
    public LabelComponent lc;
    public LabelComponent lcsh;
    public Vector2 tempGdx = new Vector2();
    public List<Entity> bags = new ArrayList<>();
    public ButtonComponent touchZoneBtn;
    public int bagPosIdX;
    public Preview preview;

    private ItemWrapper shopItem;
    public Entity hcSectionE;
    public Entity btnClothing;
    public Entity btnPowerUp;
    private int bagPosIdY;
    private int bagPageId;

    public boolean isAllowedMoving;

    private List<Entity> pageDots = new ArrayList<>();
    public int currentPageIndex = 0;

    public static float firstBagTargetPos;
    public static boolean canChangeTabs;

    public ShopScreenScript() {
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
        GameStage.sceneLoader.getEngine().addSystem(new DebugSystem());

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
        touchZoneNButton.getComponent(TransformComponent.class).x = 1320;
        for (Entity e : touchZoneNButton.getComponent(NodeComponent.class).children) {
            if (e.getComponent(MainItemComponent.class).tags.contains(DOT_TAG)) {
                e.getComponent(TintComponent.class).color.a = 0;
            }
        }

        createIconsForAllSoftItems();
        createIconsForAllHCItems();
        initTabBtns();
        initScrollLeftBtn();
        initScrollRightBtn();
        bagPosIdX = 0;
        bagPosIdY = 0;
        bagPageId = 0;
        currentPageIndex = 0;
        canChangeTabs = true;
        isPreviewOn.set(false);
        isAllowedMoving = true;
    }

    public void initTabBtns() {
        btnClothing = shopItem.getChild(TAB_BTN_SHOP).getEntity();
        btnClothing.getComponent(ButtonComponent.class).enable = false;
        btnClothing.getComponent(LayerMapComponent.class).getLayer(BTN_NORMAL).isVisible = true;
        btnClothing.getComponent(LayerMapComponent.class).getLayer(BTN_PRESSED).isVisible = false;
        btnClothing.getComponent(ButtonComponent.class).clearListeners();
        btnClothing.getComponent(ButtonComponent.class).addListener(new ShopPoverUpTabListener(this));

        btnPowerUp = shopItem.getChild(TAB_BTN_UPG).getEntity();
        btnPowerUp.getComponent(ButtonComponent.class).enable = true;
        btnPowerUp.getComponent(LayerMapComponent.class).getLayer(BTN_NORMAL).isVisible = false;
        btnPowerUp.getComponent(LayerMapComponent.class).getLayer(BTN_DEFAULT).isVisible = false;
        btnPowerUp.getComponent(LayerMapComponent.class).getLayer(BTN_PRESSED).isVisible = true;
        btnPowerUp.getComponent(ButtonComponent.class).clearListeners();
        btnPowerUp.getComponent(ButtonComponent.class).addListener(new ShopClothingTabListener(this));
    }

    private void sortHCitemsAccordingUI() {
        String[] names = new String[]{"raven", "phoenix", "dog", "bj_double", "cat"};
        List<ShopItem> hcsi = new ArrayList<>();
        for (String title : names) {
            hcsi.add(findCorrectHCitemByTitle(title));
        }
        allHCItems = hcsi;
    }

    public void getAllAllVanities() {
        if (allSoftItems.isEmpty()) {
            for (PetComponent pet : GameStage.gameScript.fpc.pets) {
                if (pet.isHardCurr) {
                    allHCItems.add(pet);
                }
            }
            allHCItems.addAll(getAllUpgrades());
            sortHCitemsAccordingUI();
            allSoftItems.addAll(GameStage.gameScript.fpc.vanities);
        }

        Collections.sort(allSoftItems, new Comparator<ShopItem>() {
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
        Collections.reverse(allSoftItems);
    }

    private ShopItem findCorrectHCitemByTitle(String title) {
        for (ShopItem si : allHCItems) {
            if (si.name.equals(title))
                return si;
        }
        return null;
    }

    private void createIconsForAllHCItems() {
        hcSectionE = shopItem.getChild(HC_SHOP_SEC).getEntity();
        NodeComponent nc = hcSectionE.getComponent(NodeComponent.class);

        for (Entity e : nc.children) {
            final ShopItem hc = findCorrectHCitemByTitle(e.getComponent(MainItemComponent.class).itemIdentifier);
//            Entity child = e.getComponent(NodeComponent.class).getChild(TITLE);
//            child.getComponent(LabelComponent.class).text.replace(
//                    0, child.getComponent(LabelComponent.class).text.length,
//                    hc.name
//            );

            if (hc.name.contains(NEW_LINE_SIGN)){
                String[] lines = hc.name.split(NEW_LINE_SIGN);
                e.getComponent(NodeComponent.class).getChild(TITLE_2).getComponent(TintComponent.class).color.a = 1;
                e.getComponent(NodeComponent.class).getChild(TITLE).getComponent(LabelComponent.class).text.replace(
                        0, e.getComponent(NodeComponent.class).getChild(TITLE).getComponent(LabelComponent.class).text.length,
                        lines[0]);
                e.getComponent(NodeComponent.class).getChild(TITLE_2).getComponent(LabelComponent.class).text.replace(
                        0, e.getComponent(NodeComponent.class).getChild(TITLE_2).getComponent(LabelComponent.class).text.length,
                        lines[1]);
            }else {
                e.getComponent(NodeComponent.class).getChild(TITLE_2).getComponent(TintComponent.class).color.a = 0;
                e.getComponent(NodeComponent.class).getChild(TITLE).getComponent(LabelComponent.class).text.replace(
                        0, e.getComponent(NodeComponent.class).getChild(TITLE).getComponent(LabelComponent.class).text.length,
                        hc.name);
            }

            e.getComponent(ButtonComponent.class).addListener(
                    new ImageButtonListener(e, new AtomicBoolean[]{isPreviewOn}) {
                        @Override
                        public void clicked() {
                            if (!isPreviewOn.get() && isAllowedMoving) {
                                preview.canPlayDescAni = true;
                                preview.showPreview(hc, true, false);
                            }
                        }
                    });
        }
    }

    public void createIconsForAllSoftItems() {
        TransformComponent previousTc = null;
        for (final ShopItem vc : allSoftItems) {
            CompositeItemVO tempC = GameStage.sceneLoader.loadVoFromLibrary(BTN_IMG_SHOP_ICON_LIB).clone();
            final Entity bagEntity = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempC);
            GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), bagEntity, tempC.composite);
            GameStage.sceneLoader.getEngine().addEntity(bagEntity);

            Entity itemIcon = initSoftCurrencyShopItem(vc);
            itemIcon.getComponent(ZIndexComponent.class).setZIndex(200);

            final TransformComponent tc = getNextBagPos(previousTc, bagEntity.getComponent(DimensionsComponent.class));
            bagEntity.add(tc);
            bagsZindex = bagEntity.getComponent(ZIndexComponent.class).getZIndex() > bagsZindex ?
                    bagEntity.getComponent(ZIndexComponent.class).getZIndex() : bagsZindex;
            previousTc = tc;

            shopItem.getChild(BTN_IMG_SHOP_ICON_LIB).addChild(itemIcon);
            TransformComponent tcb = itemIcon.getComponent(TransformComponent.class);
            tcb.x = tc.x + X_ICON_ON_BAG;
            tcb.y = tc.y + Y_ICON_ON_BAG;

            if (vc.name.contains(NEW_LINE_SIGN)){
                String[] lines = vc.name.split(NEW_LINE_SIGN);
                bagEntity.getComponent(NodeComponent.class).getChild(TITLE_2).getComponent(TintComponent.class).color.a = 1;
                bagEntity.getComponent(NodeComponent.class).getChild(TITLE).getComponent(LabelComponent.class).text.replace(
                        0, bagEntity.getComponent(NodeComponent.class).getChild(TITLE).getComponent(LabelComponent.class).text.length,
                        lines[0]);
                bagEntity.getComponent(NodeComponent.class).getChild(TITLE_2).getComponent(LabelComponent.class).text.replace(
                        0, bagEntity.getComponent(NodeComponent.class).getChild(TITLE_2).getComponent(LabelComponent.class).text.length,
                        lines[1]);
            }else {
                bagEntity.getComponent(NodeComponent.class).getChild(TITLE_2).getComponent(TintComponent.class).color.a = 0;
                bagEntity.getComponent(NodeComponent.class).getChild(TITLE).getComponent(LabelComponent.class).text.replace(
                        0, bagEntity.getComponent(NodeComponent.class).getChild(TITLE).getComponent(LabelComponent.class).text.length,
                        vc.name);
            }

            bags.add(bagEntity);
            itemIcons.put(vc.shopIcon, itemIcon);

            bagEntity.add(new ButtonComponent());
            bagEntity.getComponent(ButtonComponent.class).addListener(
                    new ImageButtonListener(bagEntity, new AtomicBoolean[]{isPreviewOn}) {
                        @Override
                        public void clicked() {
                            if (!isPreviewOn.get() && isAllowedMoving) {
                                preview.canPlayDescAni = true;
                                preview.showPreview(vc, true, false);
                            }
                        }
                    });
        }
    }

    public List<Upgrade> getAllUpgrades() {
        List<Upgrade> upgrades = new ArrayList<Upgrade>(GameStage.gameScript.fpc.upgrades.values());
        for (Upgrade u : Upgrade.getAllUpgrades()) {
            if (!upgrades.contains(u)) {
                upgrades.add(u);
            }
        }
        return upgrades;
    }

    private Entity initSoftCurrencyShopItem(ShopItem vc) {
        if (!vc.bought) {
            return getIconFromLib(ITEM_UNKNOWN_N);
        } else {
            return getIconFromLib(vc.shopIcon);
        }
    }

    public Entity getIconFromLib(String name) {
        CompositeItemVO tempItemC = GameStage.sceneLoader.loadVoFromLibrary(name).clone();
        Entity itemIcon = GameStage.sceneLoader.entityFactory.createEntity(GameStage.sceneLoader.getRoot(), tempItemC);
        GameStage.sceneLoader.entityFactory.initAllChildren(GameStage.sceneLoader.getEngine(), itemIcon, tempItemC.composite);
        GameStage.sceneLoader.getEngine().addEntity(itemIcon);
        return itemIcon;
    }

    private void addBackButtonPlease() {
        Entity btnBack = shopItem.getChild(BTN_BACK).getEntity();
        btnBack.getComponent(ButtonComponent.class).addListener(
                new ImageButtonListener(btnBack, new AtomicBoolean[]{isPreviewOn}) {
                    @Override
                    public void clicked() {
                        if (!isPreviewOn.get()) {
                            startTransitionOut = true;
                        }
                    }
                });
    }

    @Override
    public void act(float delta) {
        if (!canChangeTabs && bags.get(0).getComponent(TransformComponent.class).x == firstBagTargetPos) {
            canChangeTabs = true;
        }
        if (!isPreviewOn.get()) {
            transitionIn();
            transitionOut();
        }
        updateScrollButtonsState();
        preview.updatePreview();
        lc.text.replace(0, lc.text.length(), String.valueOf(GameStage.gameScript.fpc.totalScore));
        lcsh.text.replace(0, lcsh.text.length(), String.valueOf(GameStage.gameScript.fpc.totalScore));

        if (firstBagTargetPos != 0) {
            float bPos = bags.get(0).getComponent(TransformComponent.class).x;
            if (firstBagTargetPos == 0 || bPos == firstBagTargetPos) {
                isAllowedMoving = true;
                firstBagTargetPos = 0;
            } else {
                isAllowedMoving = false;
            }
        }
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

    public TransformComponent getNextBagPos(TransformComponent previous, DimensionsComponent previousDc) {
        TransformComponent tc = new TransformComponent();

        if (previous == null) {
            tc.x = FIRST_BAG_X;
            tc.y = FIRST_BAG_Y;
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
            placeTheDot(bagPageId);
        } else {
            bagPosIdY++;
            bagPosIdX++;
        }
        return tc;
    }

    private void placeTheDot(int pageId) {
        int vV = pageId + 1;
        Entity dotEntity = touchZoneNButton.getComponent(NodeComponent.class).getChild(DOTZ + vV);
        pageDots.add(dotEntity);
        dotEntity.getComponent(TintComponent.class).color.a = 1;
        dotEntity.getComponent(TransformComponent.class).x = 550 + pageId * 30;
        dotEntity.getComponent(TransformComponent.class).y = 10;
    }

    public void setDotActive(int pagIndex) {

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
        scrollLeftBtn.getComponent(ButtonComponent.class)
                .addListener(new ImageButtonListener(scrollLeftBtn, new AtomicBoolean[]{isPreviewOn}) {
                    @Override
                    public void touchUp() {
                        if (canMoveBagsLeft())
                            super.touchUp();
                    }

                    @Override
                    public void touchDown() {
                        if (canMoveBagsLeft())
                            super.touchDown();
                    }

                    @Override
                    public void clicked() {
                        if (!isPreviewOn.get() && isAllowedMoving) {
                            scrollBagsOnePageLeft();
                        }
                        updateScrollButtonsState();
                    }
                });
    }

    public void scrollBagsOnePageLeft() {
        if (canMoveBagsLeft()) {
            for (Entity bag : bags) {
                ActionComponent a = new ActionComponent();
                Actions.checkInit();
                a.dataArray.add(
                        Actions.moveTo(bag.getComponent(TransformComponent.class).x + SCCREEN_WIDTH,
                                bag.getComponent(TransformComponent.class).y, 0.7f, Interpolation.exp10)
                );
                bag.add(a);
            }
            for (Entity icon : itemIcons.values()) {
                ActionComponent a = new ActionComponent();
                Actions.checkInit();

                a.dataArray.add(
                        Actions.moveTo(icon.getComponent(TransformComponent.class).x + SCCREEN_WIDTH,
                                icon.getComponent(TransformComponent.class).y, 0.7f, Interpolation.exp10)
                );
                icon.add(a);
            }
            currentPageIndex--;
            setDotActive(currentPageIndex);
            firstBagTargetPos = bags.get(0).getComponent(TransformComponent.class).x + SCCREEN_WIDTH;
        }
    }

    private void initScrollRightBtn() {
        Entity scrollLeftBtn = touchZoneNButton.getComponent(NodeComponent.class).getChild(BTN_SCROLL_RIGHT);
        scrollLeftBtn.add(new ButtonComponent());
        scrollLeftBtn.getComponent(ButtonComponent.class).addListener(new ImageButtonListener(scrollLeftBtn, new AtomicBoolean[]{isPreviewOn}) {
            @Override
            public void touchUp() {
                if (canMoveBagsRight())
                    super.touchUp();
            }

            @Override
            public void touchDown() {
                if (canMoveBagsRight())
                    super.touchDown();
            }

            @Override
            public void clicked() {
                if (!isPreviewOn.get() && isAllowedMoving)
                    scrollBagsOnePageRight();
                updateScrollButtonsState();
            }
        });
    }

    public void scrollBagsOnePageRight() {
        if (canMoveBagsRight()) {
            for (Entity bag : bags) {
                ActionComponent a = new ActionComponent();
                Actions.checkInit();
                a.dataArray.add(
                        Actions.moveTo(bag.getComponent(TransformComponent.class).x - SCCREEN_WIDTH,
                                bag.getComponent(TransformComponent.class).y, 0.7f, Interpolation.exp10)
                );
                bag.add(a);
            }
            for (Entity icon : itemIcons.values()) {
                ActionComponent a = new ActionComponent();
                Actions.checkInit();
                a.dataArray.add(
                        Actions.moveTo(icon.getComponent(TransformComponent.class).x - SCCREEN_WIDTH,
                                icon.getComponent(TransformComponent.class).y, 0.7f, Interpolation.exp10)
                );
                icon.add(a);
            }
            currentPageIndex++;
            setDotActive(currentPageIndex);
            firstBagTargetPos = bags.get(0).getComponent(TransformComponent.class).x - SCCREEN_WIDTH;
        }
    }

    private void updateScrollButtonsState() {
        if (canMoveBagsLeft()) {
            touchZoneNButton.getComponent(NodeComponent.class).getChild(BTN_SCROLL_LEFT)
                    .getComponent(LayerMapComponent.class).getLayer(BTN_DEFAULT).isVisible = true;
            touchZoneNButton.getComponent(NodeComponent.class).getChild(BTN_SCROLL_LEFT)
                    .getComponent(LayerMapComponent.class).getLayer(BTN_SCROLL_INACTIVE).isVisible = false;
        } else {
            touchZoneNButton.getComponent(NodeComponent.class).getChild(BTN_SCROLL_LEFT)
                    .getComponent(LayerMapComponent.class).getLayer(BTN_DEFAULT).isVisible = false;
            touchZoneNButton.getComponent(NodeComponent.class).getChild(BTN_SCROLL_LEFT)
                    .getComponent(LayerMapComponent.class).getLayer(BTN_SCROLL_INACTIVE).isVisible = true;
        }
        if (canMoveBagsRight()) {
            touchZoneNButton.getComponent(NodeComponent.class).getChild(BTN_SCROLL_RIGHT)
                    .getComponent(LayerMapComponent.class).getLayer(BTN_DEFAULT).isVisible = true;
            touchZoneNButton.getComponent(NodeComponent.class).getChild(BTN_SCROLL_RIGHT)
                    .getComponent(LayerMapComponent.class).getLayer(BTN_SCROLL_INACTIVE).isVisible = false;
        } else {
            touchZoneNButton.getComponent(NodeComponent.class).getChild(BTN_SCROLL_RIGHT)
                    .getComponent(LayerMapComponent.class).getLayer(BTN_DEFAULT).isVisible = false;
            touchZoneNButton.getComponent(NodeComponent.class).getChild(BTN_SCROLL_RIGHT)
                    .getComponent(LayerMapComponent.class).getLayer(BTN_SCROLL_INACTIVE).isVisible = true;
        }
    }

    public boolean canMoveBagsLeft() {
        return bags.get(0).getComponent(TransformComponent.class).x <= 0;
    }

    public boolean canMoveBagsRight() {
        return !(bags.get(bags.size() - 1).getComponent(TransformComponent.class).x <= SCCREEN_WIDTH &&
                bags.get(bags.size() - 1).getComponent(TransformComponent.class).x >= 0);
    }

    @Override
    public void dispose() {
        scoreLbl = null;
        lc = null;
        tempGdx = new Vector2();
        bags = null;
        touchZoneBtn = null;
        preview = null;
        shopItem = null;
        System.gc();
    }

    public void checkIfChanged() {
        if (shouldReload) {
            for (Map.Entry<String, Entity> entry : itemIcons.entrySet()) {

                TransformComponent oldIconTC = new TransformComponent();
                oldIconTC.x = entry.getValue().getComponent(TransformComponent.class).x;
                oldIconTC.y = entry.getValue().getComponent(TransformComponent.class).y;

                entry.getValue().getComponent(TransformComponent.class).x = FAR_FAR_AWAY_X;
                GameStage.sceneLoader.engine.removeEntity(entry.getValue());

                Entity newUnknownIcon = getIconFromLib(ITEM_UNKNOWN_N);
                newUnknownIcon.getComponent(TransformComponent.class).x = oldIconTC.x;
                newUnknownIcon.getComponent(TransformComponent.class).y = oldIconTC.y;
                newUnknownIcon.getComponent(ZIndexComponent.class).setZIndex(200);

                entry.setValue(newUnknownIcon);
            }
            shouldReload = false;
        }
    }

    public void resetPages() {
        setDotActive(0);
        currentPageIndex = 0;
        touchZoneNButton.getComponent(NodeComponent.class).getChild(BTN_SCROLL_LEFT)
                .getComponent(LayerMapComponent.class).getLayer(BTN_DEFAULT).isVisible = false;
        touchZoneNButton.getComponent(NodeComponent.class).getChild(BTN_SCROLL_LEFT)
                .getComponent(LayerMapComponent.class).getLayer(BTN_SCROLL_INACTIVE).isVisible = true;
    }
}