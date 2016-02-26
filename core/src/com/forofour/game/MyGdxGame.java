package com.forofour.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.forofour.game.screens.GameScreen;

public class MyGdxGame extends Game {

	@Override
	public void create() {
		Gdx.app.log("Rugby Game", "created");
		setScreen(new GameScreen());
	}
}
