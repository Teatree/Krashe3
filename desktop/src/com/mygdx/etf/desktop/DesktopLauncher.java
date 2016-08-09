package com.mygdx.etf.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.etf.Main;

import java.lang.instrument.Instrumentation;

public class DesktopLauncher {

	public static void main (String[] arg) {
		Main game = new Main(null);
		Main.inst = ObjectSizeFetcher.instrumentation;
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 500;

		new LwjglApplication(game, config);
	}
}
