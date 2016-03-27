package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.AdsController;
import com.mygdx.game.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {

		Main game = new Main(null);

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1200;
		config.height = 800;

		// Configure platform dependent code
		DesktopResolver res = new DesktopResolver(game);
		Main.setPlatformResolver(res);

		new LwjglApplication(game, config);
	}
}
