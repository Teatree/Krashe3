package com.mygdx.etf.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.etf.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {

		Main game = new Main(null);

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1200;
		config.height = 800;

		new LwjglApplication(game, config);
	}
}
