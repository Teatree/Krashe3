package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.pay.PurchaseManagerConfig;
import com.badlogic.gdx.pay.PurchaseObserver;
import com.badlogic.gdx.pay.Transaction;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.*;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.ETFSceneLoader;
import com.mygdx.game.utils.SaveMngr;

public class Main extends Game {

    public static final int WORLD_WIDTH = 1200;
    public static final int WORLD_HEIGHT = 786;
    public static int viewportWidth = 1200;
    public static int viewportHeight = 786;

    public static GameStage gameStage;
    public Stage loadingStage;
    SpriteBatch batch;
    Sprite tex;
    Viewport oneViewport;

    public static AdsController adsController;

    //iap
    public static final int APPSTORE_UNDEFINED = 0;
    public static final int APPSTORE_GOOGLE = 1;
    private int isAppStore = APPSTORE_UNDEFINED;

    public final static String phoenix_trans_ID = "phoenix";
    public final static String bj_double_trans_ID = "bj_double";
    public final static String no_ads_trans_ID = "no_ads";
    public final static String pet_bird_trans_ID = "pet_bird";

    static PlatformResolver m_platformResolver;
    public PurchaseManagerConfig purchaseManagerConfig;

    public PurchaseObserver purchaseObserver = new PurchaseObserver() {
        @Override
        public void handleRestore(Transaction[] transactions) {
            for (Transaction transaction : transactions) {
                if (checkTransaction(transaction.getIdentifier(), true)) break;
            }
        }

        @Override
        public void handleRestoreError(Throwable e) {
            throw new GdxRuntimeException(e);
        }

        @Override
        public void handleInstall() {
        }

        @Override
        public void handleInstallError(Throwable e) {
            Gdx.app.log("ERROR", "PurchaseObserver: handleInstallError!: " + e.getMessage());
            throw new GdxRuntimeException(e);
        }

        @Override
        public void handlePurchase(Transaction transaction) {
            checkTransaction(transaction.getIdentifier(), false);
        }

        @Override
        public void handlePurchaseError(Throwable e) {    //--- Amazon IAP: this will be called for cancelled
            throw new GdxRuntimeException(e);
        }

        @Override
        public void handlePurchaseCanceled() {    //--- will not be called by amazonIAP
        }
    };

    protected boolean checkTransaction(String id, boolean isRestore) {
        boolean returnbool = false;

        switch (id) {
            case (phoenix_trans_ID): {
                Gdx.app.log("checkTransaction", "phoenix!");
                returnbool = true;
                break;
            }
            case (bj_double_trans_ID): {
                Gdx.app.log("checkTransaction", "bj_double!");
                returnbool = true;
                break;
            }
            case (no_ads_trans_ID): {
                Gdx.app.log("checkTransaction", "no_ads!");
                GameStage.gameScript.fpc.settings.noAds = true;
                returnbool = true;
                break;
            }
            case (pet_bird_trans_ID): {
                Gdx.app.log("checkTransaction", "get pet bird!");
                returnbool = true;
                break;
            }
        }
        return returnbool;
    }

    public Main(AdsController adsController) {
        if (adsController != null) {
            Main.adsController = adsController;
        } else {
            Main.adsController = new DummyAdsController();
        }
    }

    @Override
    public void create() {
        oneViewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT);
        loadingStage = new Stage(oneViewport);
        Gdx.input.setInputProcessor(loadingStage);

        batch = new SpriteBatch();
        tex = new Sprite(new Texture(Gdx.files.internal("orig/loading.png")));
    }

    public void async() {
//        SaveMngr.generateVanityJson();
//        SaveMngr.generatePetsJson();
//        SaveMngr.generateLevelsJson();

        ETFSceneLoader sceneLoader = new ETFSceneLoader(oneViewport);
        gameStage = new GameStage(sceneLoader);
        gameStage.setViewport(oneViewport);
        GameStage.viewport = oneViewport;
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
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        super.dispose();
        SaveMngr.saveStats(GameStage.gameScript.fpc);
    }

    public static PlatformResolver getPlatformResolver() {
        return m_platformResolver;
    }

    public static void setPlatformResolver(PlatformResolver platformResolver) {
        m_platformResolver = platformResolver;
    }

    public int getAppStore() {
        return isAppStore;
    }

    public void setAppStore(int isAppStore) {
        this.isAppStore = isAppStore;
    }
}
