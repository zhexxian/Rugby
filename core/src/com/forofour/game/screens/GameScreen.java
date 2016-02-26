package com.forofour.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.forofour.game.gameworlds.GameRenderer;
import com.forofour.game.gameworlds.GameWorld;
import com.forofour.game.handlers.InputHandler;

/**
 * Created by seanlim on 19/2/2016.
 */
public class GameScreen implements Screen {

    private GameWorld world;
    private GameRenderer renderer;
    private Vector2 screenSize;
    private float runTime;

    public GameScreen(){
        Gdx.app.log("GameScreen", "Attached");

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        //Vector2 screenSize = new Vector2(136, screenHeight / (screenWidth / 136));
        float gameWidth = 960;
        float gameHeight = screenHeight / (screenWidth / gameWidth);

        world = new GameWorld();
        Gdx.input.setInputProcessor(new InputHandler(world));
        renderer = new GameRenderer(world, gameWidth, gameHeight);

    }

    @Override
    public void show() {
        Gdx.app.log("GameScreen", "show called");
    }

    @Override
    public void render(float delta) {
        Gdx.app.log("GameScreen FPS", (1 / delta) + "");
        runTime += delta;

        world.update(delta);
        renderer.render(delta);

    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("GameScreen", String.format("resizing (%d, %d)",
                width, height));
    }

    @Override
    public void pause() {
        Gdx.app.log("GameScreen", "pause called");
    }

    @Override
    public void resume() {
        Gdx.app.log("GameScreen", "resume called");
    }

    @Override
    public void hide() {
        Gdx.app.log("GameScreen", "hide called");
    }

    @Override
    public void dispose() {

    }
}
