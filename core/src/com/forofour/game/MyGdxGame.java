/*This is where the game starts*/

package com.forofour.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.forofour.game.handlers.AssetLoader;
import com.forofour.game.handlers.GameConstants;
import com.forofour.game.screens.MenuScreen;

public class MyGdxGame extends Game {

	@Override
	public void create() {
		Gdx.app.log("Rugby Game", "created");
        AssetLoader.load(); // load image files
		GameConstants.init(); // Initialize when able to
		setScreen(new MenuScreen()); // set the start screen to be the main menu
	}

	public void dispose() {
		super.dispose();
		AssetLoader.dispose();
	}
}
