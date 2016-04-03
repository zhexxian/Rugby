/*This defines the main game screen*/

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
    //main action of game screen -- initialize and update game world (logic) and game renderer (style)
    private GameWorld world;
    private GameRenderer renderer;
    private Vector2 screenSize;
    private float runTime;

    public GameScreen(boolean isServer) {
        super();
        if(isServer) {

        }
    }

    public GameScreen(){
        Gdx.app.log("GameScreen", "Attached"); //print out for debugging

        // Initialize some constants upon being able to
        world = new GameWorld();
        renderer = new GameRenderer(world);

        Gdx.input.setInputProcessor(new InputHandler(world)); // Stage itself is an inputAdapter

    }

    @Override
    public void show() {
        Gdx.app.log("GameScreen", "show called");
    }

    @Override
    public void render(float delta) {
        runTime += delta;

        renderer.render(delta); // Why render than update?
        world.update(delta);

        if(world.reinitRequired()){ // Required when new objects are added to the game
            renderer.reinitialize();
            ((InputHandler)Gdx.input.getInputProcessor()).reinitialize();
        }
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("GameScreen", String.format("resizing (%d, %d)",
                width, height));
//        world.getGameStage().getViewport().update(width, height, true);
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
        world.getGameStage().dispose();
    }
}
