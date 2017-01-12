package com.fd.etf;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fd.etf.stages.GameStage;
import com.fd.etf.utils.ETFSceneLoader;
import com.fd.etf.utils.SaveMngr;

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

    public Main(AllController controller) {
        if (controller != null) {
            Main.mainController = controller;
            Main.playServices = controller;
        } else {
            Main.mainController = new DummyAllController();
        }
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
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        super.dispose();
        SaveMngr.saveStats(gameStage.gameScript.fpc);
        mainController.signOut();
    }
}