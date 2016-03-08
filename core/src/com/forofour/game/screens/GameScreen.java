package com.forofour.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.forofour.game.gameworlds.GameRenderer;
import com.forofour.game.gameworlds.GameWorld;
import com.forofour.game.handlers.GameConstants;
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

        // Assume 960x540 (qHD - 16:9 aspect ratio)
        GameConstants.GAME_WIDTH = 160;
        GameConstants.GAME_HEIGHT = screenHeight / (screenWidth / GameConstants.GAME_WIDTH);

        world = new GameWorld();
        renderer = new GameRenderer(world);

        Gdx.input.setInputProcessor(new InputHandler(world)); // Stage has is an inputAdapter
//        Gdx.input.setInputProcessor(new InputHandler(world));

    }

    @Override
    public void show() {
        Gdx.app.log("GameScreen", "show called");
    }

    @Override
    public void render(float delta) {
        //Gdx.app.log("GameScreen(" + gameWidth + "x" + gameHeight + ") FPS", (1 / delta) + " DELTA" + delta);
        runTime += delta;

        renderer.render(delta);

        world.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("GameScreen", String.format("resizing (%d, %d)",
                width, height));
        world.getViewport().update(width, height, true);
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
        world.dispose();
    }
}
