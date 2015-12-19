package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.*;
import com.mygdx.game.stages.GameScreenScript;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.SaveMngr;

public class Main extends ApplicationAdapter {

	public static GameStage stage;
	Array<Viewport> viewports;
	Array<String> names;

	@Override
	public void create () {
		names = getViewportNames();

		GameScreenScript.fpc = SaveMngr.loadStats();
		stage = new GameStage();
		viewports = getViewports(stage.getCamera());
		stage.setViewport(viewports.first());

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.update();

		stage.act();
//		stage.setDebugAll(true);
		stage.draw();
	}

	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause () {
//		SaveMngr.saveStats(GameScreenScript.fpc);
	}

	@Override
	public void resume () {
//		SaveManager.loadProperties();
	}

	@Override
	public void dispose () {
		SaveMngr.saveStats(GameScreenScript.fpc);
	}

	static public Array<String> getViewportNames () {
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

	static public Array<Viewport> getViewports (Camera camera) {

		int minWorldWidth = 2400;
		int minWorldHeight = 1440;
		int maxWorldWidth = 2400;
		int maxWorldHeight = 1440;

		Array<Viewport> viewports = new Array();
		viewports.add(new StretchViewport(minWorldWidth, minWorldHeight, camera));
		viewports.add(new FillViewport(minWorldWidth, minWorldHeight, camera));
		viewports.add(new FitViewport(minWorldWidth, minWorldHeight, camera));
		viewports.add(new ExtendViewport(minWorldWidth, minWorldHeight, camera));
		viewports.add(new ExtendViewport(minWorldWidth, minWorldHeight, maxWorldWidth, maxWorldHeight, camera));
		viewports.add(new ScreenViewport(camera));

		ScreenViewport screenViewport = new ScreenViewport(camera);
		screenViewport.setUnitsPerPixel(0.75f);
		viewports.add(screenViewport);

		viewports.add(new ScalingViewport(Scaling.none, minWorldWidth, minWorldHeight, camera));
		return viewports;
	}
}
