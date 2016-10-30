package com.fd.etf.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fd.etf.Main;

public class DesktopLauncher {

	public static void main (String[] arg) {
		Main game = new Main(null);
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 500;

		new LwjglApplication(game, config);
	}
}
