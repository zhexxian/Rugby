package com.forofour.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.forofour.game.MyGdxGame;
import com.forofour.game.TouchPadTest.TouchPadTest;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Rugby Prototype 1";
		config.width = 960;
		config.height = 540;
		new LwjglApplication(new MyGdxGame(), config);
	}
}
