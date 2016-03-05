package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.*;
import com.mygdx.game.stages.GameScreenScript;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.ETFSceneLoader;
import com.mygdx.game.utils.SaveMngr;

public class Main extends Game {

    public static final int WORLD_WIDTH = 1200;
    public static final int WORLD_HEIGHT = 786;

    public static int viewportWidth = 1200;
    public static int viewportHeight = 786;

    public static GameStage gameStage;
    public static AdsController adsController;
    public Stage loadingStage;
    Array<Viewport> viewports;
    SpriteBatch batch;
    Array<String> names;

    Sprite tex;
    Viewport oneViewport;

    public Main(AdsController adsController) {
        if (adsController != null) {
            Main.adsController = adsController;
        } else {
            Main.adsController = new DummyAdsController();
        }
    }

    static public Array<String> getViewportNames() {
        Array<String> names = new Array();
        names.add("StretchViewport");
        names.add("FillViewport");
        names.add("FitViewport");
        names.add("ExtendViewport: no max");
        names.add("ExtendViewport: max");
        names.add("ScreenViewport: 1:1");
        names.add("ScreenViewport: 0.75:1");
        names.add("ScalingViewport: none");
        return names;
    }

    static public Array<Viewport> getViewports(Camera camera) {

        int minWorldWidth = 2400;
        int minWorldHeight = 1440;
        int maxWorldWidth = 2400;
        int maxWorldHeight = 1440;

        Array<Viewport> viewports = new Array();
        viewports.add(new StretchViewport(minWorldWidth, minWorldHeight, camera));
        viewports.add(new FitViewport(minWorldWidth, minWorldHeight, camera));
        viewports.add(new FillViewport(minWorldWidth, minWorldHeight, camera));
        viewports.add(new ExtendViewport(minWorldWidth, minWorldHeight, maxWorldWidth, maxWorldHeight, camera));
        viewports.add(new ExtendViewport(minWorldWidth, minWorldHeight, camera));
        viewports.add(new ScreenViewport(camera));

        ScreenViewport screenViewport = new ScreenViewport(camera);
        screenViewport.setUnitsPerPixel(0.75f);
        viewports.add(screenViewport);

        viewports.add(new ScalingViewport(Scaling.none, minWorldWidth, minWorldHeight, camera));
        return viewports;
    }

    @Override
    public void create() {
        names = getViewportNames();

        oneViewport = new FillViewport(WORLD_WIDTH, WORLD_HEIGHT);
        loadingStage = new Stage(oneViewport);
        Gdx.input.setInputProcessor(loadingStage);

        batch = new SpriteBatch();
        tex = new Sprite(new Texture(Gdx.files.internal("orig/loading.png")));
    }

    public void async() {
        SaveMngr.generateVanityJson();
        SaveMngr.generatePetsJson();
        SaveMngr.generateLevelsJson();


        GameScreenScript.fpc = SaveMngr.loadStats();

        ETFSceneLoader sceneLoader = new ETFSceneLoader(oneViewport);
        gameStage = new GameStage(sceneLoader);
        gameStage.setViewport(oneViewport);
        GameStage.viewport = oneViewport;
        Gdx.input.setInputProcessor(gameStage);
    }

    @Override
    public void render() {
        try {
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
        }catch (Exception e){
            System.out.println(e);
        }
//        super.render();
    }

    public void resize(int width, int height) {
        viewportWidth = width;
        viewportHeight = height;
        oneViewport.update(width, height, true);
        if (gameStage != null) {
            gameStage.getViewport().update(width, height, true);
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
        SaveMngr.saveStats(GameScreenScript.fpc);
    }
}
