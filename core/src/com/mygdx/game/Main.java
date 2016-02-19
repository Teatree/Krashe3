package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.*;
import com.mygdx.game.stages.GameScreenScript;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.SaveMngr;

public class Main extends Game{

	public static GameStage stage;
    public static int viewportWidth;
    public static int viewportHeight;
	public static AdsController adsController;
	Array<Viewport> viewports;
	Array<String> names;

	public Main(AdsController adsController){
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
	public void create () {

		SaveMngr.generateVanityJson();
		SaveMngr.generatePetsJson();


		GameScreenScript.fpc = SaveMngr.loadStats();

		names = getViewportNames();

//		this.setScreen(new LoadingScreen(this));

//		Gdx.app.postRunnable(new Runnable() {
//			@Override
//			public void run() {
				stage = new GameStage();
				viewports = getViewports(stage.getCamera());
				stage.setViewport(viewports.first());

				Gdx.input.setInputProcessor(stage);
//			}
//		});
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		if (stage.sceneLoader.rm.resourcesLoaded){
			stage.update();

			stage.act();
			stage.getViewport().update(viewportWidth, viewportHeight, true);
			stage.setDebugAll(true);
			stage.draw();
//		}
//		super.render();
	}

	public void resize(int width, int height) {
        viewportWidth = width;
        viewportHeight = height;
//		if (stage!= null && stage.sceneLoader.rm.resourcesLoaded) {
			stage.getViewport().update(width, height, true);
//		}
	}

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
		super.dispose();
		SaveMngr.saveStats(GameScreenScript.fpc);
	}

//	public class LoadingScreen implements Screen{
//
//		ShapeRenderer sr = new ShapeRenderer();
//		public Main main;
//
//		public LoadingScreen(Main main) {
//			sr = new ShapeRenderer();
//			this.main = main;
//			System.out.println("loading");
//		}
//
//		@Override
//		public void show() {
//
//		}
//
//		@Override
//		public void render(float delta) {
//			Gdx.gl.glClearColor(0, 0, 1, 1);
//			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//			System.out.println("loading");
//			sr.end();
//			sr.setAutoShapeType(true);
//			sr.setColor(Color.DARK_GRAY);
//			sr.begin();
//			sr.circle(100, 100, 100);
//			sr.end();
//			update(delta);
//		}
//
//		private void update(float delta) {
//			if (stage.sceneLoader.rm.resourcesLoaded){
//				stage.initMenu();
//			}
//		}
//
//		@Override
//		public void resize(int width, int height) {
//
//		}
//
//		@Override
//		public void pause() {
//
//		}
//
//		@Override
//		public void resume() {
//
//		}
//
//		@Override
//		public void hide() {
//
//		}
//
//		@Override
//		public void dispose() {
//
//		}
//	}
}
