/*This defines the menu screen*/

package com.forofour.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.forofour.game.actors.MenuActorMaker;
import com.forofour.game.handlers.AssetLoader;
import com.forofour.game.handlers.GameConstants;

/**
 * Created by seanlim on 8/3/2016.
 */
public class MenuScreen implements Screen {
    private Stage stage;
    private MenuActorMaker actorMaker;
    private SpriteBatch batch;


    public MenuScreen(){
        stage = new Stage(new ExtendViewport(
                GameConstants.GAME_WIDTH,
                GameConstants.GAME_HEIGHT));
        actorMaker = new MenuActorMaker(stage); //menu button actions are initialized
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();

        // music initialized
        AssetLoader.mainMusic.setLooping(true);
        AssetLoader.mainMusic.setVolume(GameConstants.MUSIC_VOLUME);
        AssetLoader.mainMusic.play();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        //set menu screen background
        batch.draw(AssetLoader.bgMenu, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("GameScreen", String.format("resizing (%d, %d)",
                width, height));
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        AssetLoader.mainMusic.play();
    }

    @Override
    public void hide() {
        AssetLoader.mainMusic.stop();
    }

    @Override
    public void dispose() {
        AssetLoader.mainMusic.dispose();
    }
}
