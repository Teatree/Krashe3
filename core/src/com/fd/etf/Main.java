package com.fd.etf;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.pay.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fd.etf.entity.componets.PetComponent;
import com.fd.etf.entity.componets.Upgrade;
import com.fd.etf.stages.GameStage;
import com.fd.etf.utils.ETFSceneLoader;
import com.fd.etf.utils.PlatformResolver;
import com.fd.etf.utils.SaveMngr;

import java.util.List;

public class Main extends Game {
    private static final int WORLD_WIDTH = 1200;
    private static final int WORLD_HEIGHT = 786;
    private static int viewportWidth = 1200;
    private static int viewportHeight = 786;

    public GameStage gameStage;
    private Stage loadingStage;
    private SpriteBatch batch;
    private Sprite tex;
    private Viewport oneViewport;

    public static AllController mainController;
    public static PlayServices playServices;
    static PlatformResolver m_platformResolver;
    public PurchaseManagerConfig purchaseManagerConfig;

    public Main(AllController controller) {
        if (controller != null) {
            Main.mainController = controller;
            Main.playServices = controller;

        } else {
            Main.mainController = new DummyAllController();
        }

        // ---- IAP: define products ---------------------
        purchaseManagerConfig = new PurchaseManagerConfig();
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet_discount"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("raven_pet"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("raven_pet_discount"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("raven_pet_discount______2"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("dragon_pet"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("dragon_pet______2"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("dragon_pet_discount"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("phoenix"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("phoenix_____2"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("phoenix_discount"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("bj_upgrade"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("bj_upgrade_discount"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("bj_discount______2"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("no_ads"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("no_ads______2"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet_2"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet_3"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet_4"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet_5"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet_6"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet_7"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet_8"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet_9"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet_10"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet_11"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet_12"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet_13"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet_14"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet_14"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet_15"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet_16"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet_17"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet_18"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet_19"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet_20"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet_21"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet_22"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("cat_pet_23"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("nastya_pet"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("nastya_cake"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("nastya_or_nastya"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("b"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("n"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("raven_pet_discount_3"));
    }

    @Override
    public void create() {
        oneViewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT);
        loadingStage = new Stage(oneViewport);
        Gdx.input.setInputProcessor(loadingStage);

        batch = new SpriteBatch();
        tex = new Sprite(new Texture(Gdx.files.internal("orig/loading.png")));
//        mainController.setupPlayServices();
//        mainController.signIn();

        Gdx.input.setCatchBackKey(true);
        Gdx.input.setCatchMenuKey(true);
    }

    public PurchaseObserver purchaseObserver = new PurchaseObserver() {
        @Override
        public void handleRestore (Transaction[] transactions) {
            for (int i = 0; i < transactions.length; i++) {
                if (checkTransaction(transactions[i].getIdentifier(), true) == true);
            }


//            if (skus.isEmpty()) {
//                for (String sku : skus) {
//                    if (sku.equals(SKU_BJ) || sku.equals(SKU_DISCOUNT_BJ)) {
//                        Upgrade.getBJDouble(gameStage).buyAndUse(gameStage);
//                    }
//
//                    if (sku.equals(SKU_PHOENIX) || sku.equals(SKU_DISCOUNT_PHOENIX)) {
//                        Upgrade.getPhoenix(gameStage).buyAndUse(gameStage);
//                    }
//
//                    if (gameStage.gameScript != null && gameStage.gameScript.fpc != null && gameStage.gameScript.fpc.pets != null) {
//                        for (PetComponent p : gameStage.gameScript.fpc.pets) {
//                            if(p.sku == sku || p.sku_discount == sku){
//                                p.buyAndUse(gameStage);
//                            }
//                        }
//                    }
//
//                }
//            }
        }
        @Override
        public void handleRestoreError (Throwable e) {
            throw new GdxRuntimeException(e);
        }
        @Override
        public void handleInstall () {	}

        @Override
        public void handleInstallError (Throwable e) {
            Gdx.app.log("ERROR", "PurchaseObserver: handleInstallError!: " + e.getMessage());
            throw new GdxRuntimeException(e);
        }
        @Override
        public void handlePurchase (Transaction transaction) {
            checkTransaction(transaction.getIdentifier(), false);
        }

        @Override
        public void handlePurchaseError (Throwable e) {	//--- Amazon IAP: this will be called for cancelled
//            throw new GdxRuntimeException(e);
            mainController.setReceivedErrorResponse(true);
        }
        @Override
        public void handlePurchaseCanceled () {	//--- will not be called by amazonIAP
        }
    };

    protected boolean checkTransaction (String ID, boolean isRestore) {
        boolean returnbool = false;

        //----- put your logic for full version here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        Gdx.app.log("ID: ", " --- " + ID);

        // get pet
        for (PetComponent p: gameStage.gameScript.fpc.pets) {

            if(ID.equalsIgnoreCase(p.sku) || ID.equalsIgnoreCase(p.sku_discount)) {
                Gdx.app.log("checkTransaction", "we've got a pet!");
                p.buyAndUse(gameStage);
            }
        }

        //get upgrade
        if (ID.equalsIgnoreCase("phoenix") || ID.equalsIgnoreCase("phoenix_discount")){
            Gdx.app.log("checkTransaction", "we've got a extra life!");
            gameStage.gameScript.fpc.upgrades.get(Upgrade.UpgradeType.PHOENIX).buyAndUse(gameStage);
        }
        if (ID.equalsIgnoreCase("bj_upgrade") || ID.equalsIgnoreCase("bj_upgrade_discount")){
            Gdx.app.log("checkTransaction", "we've got a double coins!");
            gameStage.gameScript.fpc.upgrades.get(Upgrade.UpgradeType.BJ_DOUBLE).buyAndUse(gameStage);
        }

        if (ID.equalsIgnoreCase("no_ads")){
            gameStage.gameScript.fpc.settings.noAds = true;
        }

        returnbool = true;


        // there are gogn to be checks for different skus here
        // and we can ever reuse outr pet an buy methods , but first we need to have a MAP with each sku and each purachase
        //

        mainController.setReceivedResponse(true);
        return returnbool;
    }

    public void async() {
//        SaveMngr.generateVanityJson();
//        SaveMngr.generatePetsJson();
//        SaveMngr.generateLevelsJson();

        ETFSceneLoader sceneLoader = new ETFSceneLoader(oneViewport);
        GameStage.viewport = oneViewport;
        gameStage = new GameStage(sceneLoader);
        gameStage.setViewport(oneViewport);
        Gdx.input.setInputProcessor(gameStage);
    }

    @Override
    public void render() {

        Gdx.gl.glClearColor(0, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        loadingStage.getViewport().update(viewportWidth, viewportHeight, true);
        batch.begin();
        tex.setSize(viewportWidth, viewportHeight);
        tex.draw(batch);
        batch.end();

        if (gameStage != null) {
            gameStage.update();
            gameStage.act();
            gameStage.getViewport().update(viewportWidth, viewportHeight, true);
            GameStage.viewport.update(viewportWidth, viewportHeight, true);
            gameStage.draw();
        } else {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    async();
                }
            });
            return;
        }
    }

    public void resize(int width, int height) {
        super.resize(width, height);
        viewportWidth = width;
        viewportHeight = height;
        if (gameStage != null) {
            gameStage.getViewport().update(width, height, true);
            GameStage.viewport.update(width, height, true);
        } else {
            loadingStage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void pause() {
        SaveMngr.saveStats(gameStage.gameScript.fpc);
    }

    @Override
    public void resume() {
        SaveMngr.loadStats();
    }

    @Override
    public void dispose() {
        SaveMngr.saveStats(gameStage.gameScript.fpc);
        super.dispose();
        mainController.signOut();
    }

    public static void setPlatformResolver (PlatformResolver platformResolver) {
        m_platformResolver = platformResolver;
    }
    public static PlatformResolver getPlatformResolver () {
        return m_platformResolver;
    }
}