package com.mygdx.etf;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.etf.stages.GameStage;
import com.mygdx.etf.utils.ETFSceneLoader;
import com.mygdx.etf.utils.SaveMngr;

import java.lang.instrument.Instrumentation;

public class Main extends Game {

    public static Instrumentation inst;

    public static final int WORLD_WIDTH = 1200;
    public static final int WORLD_HEIGHT = 786;
    public static int viewportWidth = 1200;
    public static int viewportHeight = 786;

    public static GameStage gameStage;
    public Stage loadingStage;
    SpriteBatch batch;
    Sprite tex;
    Viewport oneViewport;

    public static AllController mainController;

    public Main(AllController controller) {
        if (controller != null) {
            Main.mainController = controller;
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
    }

    public void async() {
//        SaveMngr.generateVanityJson();
        SaveMngr.generatePetsJson();
//        SaveMngr.generateLevelsJson();

        ETFSceneLoader sceneLoader = new ETFSceneLoader(oneViewport);
//        SceneLoader sceneLoader = new SceneLoader();
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

    public static void printMemoryInfo(){
//        System.err.println(inst.getObjectSize(Main.gameStage.sceneLoader.engineByScene));
//        System.err.println(inst.getObjectSize(Main.gameStage.sceneLoader.rootEntityByScene));
//        System.err.println(inst.getObjectSize(Main.gameStage.sceneLoader.engine));
    }
}
